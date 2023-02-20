package com.smilebat.learntribe.openai.services;

import com.smilebat.learntribe.dataaccess.ProfileSummaryRepository;
import com.smilebat.learntribe.dataaccess.jpa.entity.ProfileSummary;
import com.smilebat.learntribe.kafka.KafkaProfileRequest;
import com.smilebat.learntribe.learntribeclients.openai.OpenAiService;
import com.smilebat.learntribe.openai.OpenAiRequest;
import com.smilebat.learntribe.openai.response.Choice;
import com.smilebat.learntribe.openai.response.OpenAiResponse;
import io.micrometer.core.instrument.util.StringUtils;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Factory for loading summaries.
 *
 * <p>Copyright &copy; 2023 Smile .Bat
 *
 * @author Likith
 * @author Pai,Sai Nandan
 */
@RequiredArgsConstructor
@Slf4j
@Service
public class SummaryFactory {

  private final OpenAiService openAiService;

  private final ProfileSummaryRepository repository;

  @Value("${feature.maxsummary}")
  private Integer maxSummaryCap;

  @Value("${feature.openai}")
  private boolean openAiFeature;

  /**
   * Loads summaries.
   *
   * @param request the {@link KafkaProfileRequest}
   */
  public void createSummaries(KafkaProfileRequest request) {
    List<String> skills = request.getSkills();
    String role = request.getRole();

    if (skills != null && !skills.isEmpty()) {
      for (String skill : skills) {
        final int count = repository.countByRoleAndSkill(role, skill);
        extracted(role, skill, count);
      }
      skills.stream().map(sk -> suggestSummaries(role, sk)).forEach(repository::saveAll);
    } else {
      final int count = repository.countByRole(role);
      extracted(role, null, count);
    }
  }

  private void extracted(String role, String skill, int count) {
    int requiredSummaries = 0;
    if (count < maxSummaryCap) {
      requiredSummaries = maxSummaryCap - count;
    }
    int totalSummaries = 0;
    while (totalSummaries < requiredSummaries) {
      Set<ProfileSummary> profileSummaries = suggestSummaries(role, skill);
      repository.saveAll(profileSummaries);
      totalSummaries++;
    }
  }

  private Set<ProfileSummary> suggestSummaries(String role, String skill) {
    final String text = getOpenAiSuggestions(role, skill);
    Set<String> summaries = parseCompletedText(text);
    return getProcessedSummaries(role, skill, summaries);
  }

  private Set<ProfileSummary> getProcessedSummaries(
      String userCurrentRole, String skill, Set<String> summaries) {
    return summaries
        .stream()
        .filter(StringUtils::isNotEmpty)
        .map(summary -> createProfileSummary(userCurrentRole, skill, summary))
        .collect(Collectors.toSet());
  }

  /**
   * Helper to create profile summary entity.
   *
   * @param userCurrentRole the Current role of user
   * @param skill the skill of user.
   * @param summary the summary of the profile.
   * @return the {@link Function}.
   */
  private ProfileSummary createProfileSummary(
      String userCurrentRole, String skill, String summary) {
    ProfileSummary profileSummary = new ProfileSummary();
    profileSummary.setRole(userCurrentRole);
    profileSummary.setSkill(skill);
    profileSummary.setSummary(summary);
    return profileSummary;
  }

  /**
   * Retrieves suggestions from Open AI.
   *
   * @param userCurrentRole the User current role.
   * @param skill the user skill.
   * @return the {@link String}.
   */
  private String getOpenAiSuggestions(String userCurrentRole, String skill) {
    OpenAiRequest request = createOpenAiRequest(userCurrentRole, skill);
    OpenAiResponse completions = openAiService.getCompletions(request);
    final Choice choice = completions.getChoices().get(0);
    return choice.getText();
  }

  /**
   * Creates a Open AI request.
   *
   * @param role the role of user {@link String}.
   * @param skill the skill of user {@link String}.
   * @return the {@link OpenAiRequest}.
   */
  private OpenAiRequest createOpenAiRequest(String role, String skill) {
    String prompt = "Create 2 Good Profile Summaries for " + role + " who is skilled in " + skill;
    if (skill == null) {
      prompt = "Create 2 Good Profile Summaries for " + role;
    }
    OpenAiRequest request = new OpenAiRequest();
    request.setPrompt(prompt);
    request.setTemperature(0.9f);
    return request;
  }

  /**
   * Parses the text completion for query extractions.
   *
   * @param str the completed open ai text.
   * @return Set of String.
   */
  private Set<String> parseCompletedText(String str) {
    String[] arr = str.split("\n");
    Set<String> summaries = new HashSet<>(3);
    int index = 1;
    int arrLen = arr.length;
    while (index < arrLen) {
      while (arr[index].isBlank()) {
        index += 1;
      }
      String inputText = arr[index].trim();
      inputText = inputText.replaceAll("[0-9]. ", "");
      summaries.add(inputText);
      index++;
    }
    return summaries;
  }
}
