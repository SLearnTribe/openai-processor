package com.smilebat.learntribe.openai.services;

import com.google.common.base.Function;
import com.google.common.base.Verify;
import com.smilebat.learntribe.dataaccess.ChallengeRepository;
import com.smilebat.learntribe.dataaccess.jpa.entity.Challenge;
import com.smilebat.learntribe.enums.AssessmentDifficulty;
import com.smilebat.learntribe.learntribeclients.openai.OpenAiService;
import com.smilebat.learntribe.openai.OpenAiRequest;
import com.smilebat.learntribe.openai.response.Choice;
import com.smilebat.learntribe.openai.response.OpenAiResponse;
import com.smilebat.learntribe.openai.services.helpers.LChallengeParser;
import java.util.List;
import java.util.Set;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Factory to compute challenges from open ai.
 *
 * <p>Copyright &copy; 2022 Smile .Bat
 *
 * @author Pai,Sai Nandan
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ChallengeFactory {

  private final OpenAiService openAiService;
  private final LChallengeParser challengeParser;

  private final ChallengeRepository challengeRepository;

  private static final Function<ChallengeFactoryRequest, String> MCQ_PROMPT =
      (request) ->
          "Create 3 "
              + request.getDifficulty()
              + " "
              + request.getSkill()
              + " questions with options and correct answers";

  /** Request for challenge factory. */
  @Builder
  @Getter
  public static class ChallengeFactoryRequest {
    private String difficulty;
    private String skill;
    private String type;
    private int quantity;
  }

  /**
   * Creates challenges from open ai raw text.
   *
   * @param factoryRequest the {@link ChallengeFactoryRequest}.
   */
  public void createChallenges(ChallengeFactoryRequest factoryRequest) {
    Verify.verifyNotNull(factoryRequest, "Factory request cannot be null");
    final int quantity = factoryRequest.getQuantity();
    int challenges = 0;
    while (challenges < quantity) {
      Set<Challenge> generatedChallenges = getOpenAiCompletions(MCQ_PROMPT.apply(factoryRequest));

      if (!generatedChallenges.isEmpty()) {
        for (Challenge challenge : generatedChallenges) {
          challenge.setSkill(factoryRequest.getSkill().trim().toUpperCase());
          challenge.setDifficulty(
              AssessmentDifficulty.getFromValue(factoryRequest.getDifficulty()));
        }
        challengeRepository.saveAll(generatedChallenges);
        challenges++;
      }
    }
  }

  private Set<Challenge> getOpenAiCompletions(String prompt) {
    log.info("Prompt: {}", prompt);
    OpenAiRequest request = new OpenAiRequest();
    request.setPrompt(prompt);
    final OpenAiResponse completions = openAiService.getCompletions(request);
    final List<Choice> choices = completions.getChoices();
    if (choices == null || choices.isEmpty()) {
      log.info("Unable to create open ai completion text");
      throw new IllegalArgumentException();
    }
    Choice choice = choices.get(0);
    String completedText = choice.getText();
    return challengeParser.parseText(completedText);
  }
}
