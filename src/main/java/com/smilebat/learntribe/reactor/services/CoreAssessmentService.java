package com.smilebat.learntribe.reactor.services;

import com.google.common.base.Verify;
import com.smilebat.learntribe.dataaccess.AssessmentRepository;
import com.smilebat.learntribe.dataaccess.ChallengeRepository;
import com.smilebat.learntribe.dataaccess.UserAstReltnRepository;
import com.smilebat.learntribe.dataaccess.jpa.entity.Assessment;
import com.smilebat.learntribe.dataaccess.jpa.entity.Challenge;
import com.smilebat.learntribe.dataaccess.jpa.entity.UserAstReltn;
import com.smilebat.learntribe.enums.AssessmentDifficulty;
import com.smilebat.learntribe.inquisitve.UserProfileRequest;
import com.smilebat.learntribe.learntribeclients.openai.OpenAiService;
import com.smilebat.learntribe.openai.OpenAiRequest;
import com.smilebat.learntribe.openai.response.Choice;
import com.smilebat.learntribe.openai.response.OpenAiResponse;
import com.smilebat.learntribe.reactor.services.helpers.AssessmentHelper;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Core Assessment Service to hold the Open Assessment creation business logic.
 *
 * <p>Copyright &copy; 2022 Smile .Bat
 *
 * @author Pai,Sai Nandan
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class CoreAssessmentService {

  private final AssessmentRepository assessmentRepository;

  private final ChallengeRepository challengeRepository;

  private final UserAstReltnRepository userAstReltnRepository;

  private final OpenAiService openAiService;

  private final AssessmentHelper helper;

  private final ChallengeParser challengeParser;

  @Value("${feature.openai}")
  private boolean isOpenAiEnabled;

  private static final int MAX_QUESTIONS = 15;

  /**
   * Evaluates and Creates new User Assessments.
   *
   * @param profile the User Profile.
   */
  @Transactional
  public void evaluateUserAssessments(UserProfileRequest profile) {
    Verify.verifyNotNull(profile, "User Profile cannot be null");
    String candidateId = profile.getKeyCloakId();
    Verify.verifyNotNull(candidateId, "Candidate Id cannot be null");
    log.info("Evaluating Assessments for User {}", candidateId);
    final List<UserAstReltn> userAstReltns = userAstReltnRepository.findByUserId(candidateId);
    Set<String> userSkills = evaluateUserSkills(profile, userAstReltns);
    if (!userSkills.isEmpty()) {
      createFreshUserAssessments(candidateId, userSkills);
    }
  }

  private Set<String> getUpdatedUserSkills(
      Set<String> userSkills, List<UserAstReltn> userAstReltns) {
    log.info("Fetching Updated User skills");
    return userSkills
        .stream()
        .filter(skill -> !isAssessmentPresent(userAstReltns, skill))
        .collect(Collectors.toSet());
  }

  private boolean isAssessmentPresent(Collection<UserAstReltn> userAstReltns, String skill) {
    return userAstReltns
        .stream()
        .anyMatch(reltn -> skill.trim().equalsIgnoreCase(reltn.getAssessmentTitle().trim()));
  }

  private void createFreshUserAssessments(String candidateId, Set<String> userSkills) {
    log.info("Creating Fresh User Assessments");
    final List<Assessment> defaultAssessments = getDefaultAssessments(userSkills);
    evaluateChallenges(candidateId, defaultAssessments);
  }

  private List<Assessment> getDefaultAssessments(Set<String> userSkills) {
    log.info("Fetching System Default Assessments for the New Skill");
    return userSkills
        .stream()
        .map(helper::createDefaultAssessments)
        .flatMap(List::stream)
        .collect(Collectors.toList());
  }

  private void evaluateChallenges(String candidateId, List<Assessment> defaultAssessments) {
    for (Assessment assessment : defaultAssessments) {
      log.info("Creating fresh assessment for User {}", candidateId);
      Set<Challenge> freshChallenges = createFreshChallenges(assessment);
      if (!freshChallenges.isEmpty()) {
        assessment.setChallenges(freshChallenges);
        assessment.setQuestions(freshChallenges.size());
        assessmentRepository.save(assessment);
        challengeRepository.saveAll(freshChallenges);
        Long assessmentId = assessment.getId();
        createUserAssessmentRelation("SYSTEM", List.of(candidateId), assessmentId);
        log.info("Successfuly create assessment {} for User {}", assessmentId, candidateId);
      }
    }
  }

  private Set<String> evaluateUserSkills(
      UserProfileRequest userProfile, List<UserAstReltn> userAstReltns) {
    log.info("Evaluating User Skills");
    String skills = userProfile.getSkills();
    if (skills == null || skills.isEmpty()) {
      return Collections.emptySet();
    }
    boolean hasUserAssessments = userAstReltns != null && !userAstReltns.isEmpty();
    Set<String> userSkills = Arrays.stream(skills.split(",")).collect(Collectors.toSet());
    final Set<String> updatedUserSkills = getUpdatedUserSkills(userSkills, userAstReltns);
    if (hasUserAssessments && updatedUserSkills.isEmpty()) {
      log.info("All System Assessments already present");
      return Collections.emptySet();
    }
    return hasUserAssessments ? updatedUserSkills : userSkills;
  }

  /**
   * Creates Fresh MCQ's for the new assessments.
   *
   * @param assessment the {@link Assessment}.
   * @return Set of {@link Challenge}.
   */
  @Transactional
  public Set<Challenge> createFreshChallenges(Assessment assessment) {
    final String skill = assessment.getTitle();
    final AssessmentDifficulty difficulty = assessment.getDifficulty();

    Set<Challenge> challenges =
        isOpenAiEnabled ? getOpenAiCompletions(skill, difficulty) : Collections.emptySet();

    int totalQAGenerated = challenges.size();

    int remainingQARequired =
        totalQAGenerated > MAX_QUESTIONS
            ? totalQAGenerated - MAX_QUESTIONS
            : MAX_QUESTIONS - totalQAGenerated;

    Set<Challenge> preExisitngDbChallenges =
        challengeRepository.findBySkill(skill, remainingQARequired, 0);
    if (!preExisitngDbChallenges.isEmpty()) {
      challenges.addAll(preExisitngDbChallenges);
    }

    if (!challenges.isEmpty()) {
      for (Challenge challenge : challenges) {
        challenge.setSkill(skill);
        challenge.setAssessmentInfo(assessment);
      }
    }
    return challenges;
  }

  private Set<Challenge> getOpenAiCompletions(String skill, AssessmentDifficulty difficulty) {
    String prompt =
        "Create 5 "
            + difficulty.getString()
            + " "
            + skill
            + " questions with options and correct answers";
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
    //     String completedText = "\n\n1. What is the name of the software development environment
    // in" +
    //     "which Java is primarily used?\n\nA. Eclipse\nB. NetBeans\nC. IntelliJ IDEA\nD. Android"
    // +
    //     "Studio\n\nAnswer: C. IntelliJ IDEA\n\n2. What is the file extension for Java source" +
    //     "files?\n\nA. .java\nB. .class\nC. .jar\nD. .exe\n\nAnswer: A. .java\n\n3. Which of the"+
    //     "following is not a Java primitive type?\n\nA. int\nB. float\nC. String\nD.
    // char\n\nAnswer: C."+
    //     "String\n\n4. Which of the following is not a keyword in Java?\n\nA. public\nB.
    // static\nC."+
    //     "void\nD. native\n\nAnswer: D. native\n\n5. What is the output of the following"+
    //     "code?\n\npublic class Test {\n   public static void main(String[] args) {\n"+
    //     "System.out.println(\"Hello, world!\");\n   }\n}\n\nA. Hello, world!\nB. 0\nC. Hello,"+
    //             "world\nD. compilation error\n\nAnswer: A. Hello, world!";
    return challengeParser.parseText(completedText);
  }

  /**
   * Parses the text completion for query extractions.
   *
   * @param str the completed open ai text.
   * @param assessment the {@link Assessment} entity.
   * @return the Set of {@link Challenge}.
   */
  @Deprecated
  private Set<Challenge> parseCompletedText(String str, Assessment assessment) {
    String[] arr = str.split("\n\n");
    Set<Challenge> challenges = new HashSet<>(15);
    int index = 1;
    int arrLen = arr.length;
    try {

      while (index < arrLen) {
        Challenge challenge = new Challenge();
        challenge.setQuestion(arr[index++].substring(3));
        challenge.setOptions(arr[index++].split("\n"));
        challenge.setAnswer(arr[index++]);
        challenge.setAssessmentInfo(assessment);
        challenges.add(challenge);
      }
    } catch (IndexOutOfBoundsException exception) {
      log.info("Error Parsing Open AI response");
    }
    return challenges;
  }

  /**
   * Creates user and assessment relation for fresh assessment
   *
   * @param hrId the HR user id
   * @param candidateIds the candidate id list
   * @param assessmentId the new assessment id
   */
  public void createUserAssessmentRelation(
      String hrId, Collection<String> candidateIds, Long assessmentId) {
    final Optional<Assessment> pAssessment = assessmentRepository.findById(assessmentId);
    if (!pAssessment.isPresent()) {
      throw new IllegalArgumentException();
    }
    Assessment assessment = pAssessment.get();
    final List<UserAstReltn> userAstReltns =
        candidateIds
            .stream()
            .map(candidateId -> helper.createUserAstReltnForCandidate(candidateId, assessment))
            .collect(Collectors.toList());
    UserAstReltn userAstReltnForHr = helper.createUserAstReltnForHr(hrId, assessment);
    userAstReltns.add(userAstReltnForHr);
    userAstReltnRepository.saveAll(userAstReltns);
  }
}
