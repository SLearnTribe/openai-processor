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
import com.smilebat.learntribe.openai.services.helpers.ChallengeParser;
import java.util.HashSet;
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
  private final ChallengeParser challengeParser;

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
   * @return the set of {@link Challenge}.
   */
  public Set<Challenge> createChallenges(ChallengeFactoryRequest factoryRequest) {
    Verify.verifyNotNull(factoryRequest, "Factory request cannot be null");
    final int quantity = factoryRequest.getQuantity();
    // Preconditions.checkArgument(quantity > 0, "Quantity cannot be 0");
    Set<Challenge> challenges = new HashSet<>(quantity);
    while (challenges.size() < quantity) {
      Set<Challenge> generatedChallenges = getOpenAiCompletions(MCQ_PROMPT.apply(factoryRequest));

      if (!generatedChallenges.isEmpty()) {
        for (Challenge challenge : challenges) {
          challenge.setSkill(factoryRequest.getSkill().trim().toUpperCase());
          challenge.setDifficulty(
              AssessmentDifficulty.getFromValue(factoryRequest.getDifficulty()));
        }
        challengeRepository.saveAll(generatedChallenges);
        challenges.addAll(generatedChallenges);
      }
    }
    return challenges;
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

    //    String completedText =
    //        "\n\n1. What is the name of the software development environment"
    //            + "in"
    //            + "which Java is primarily used?\n\nA. Eclipse\nB. NetBeans\nC. IntelliJ IDEA\nD.
    // Android"
    //            + "Studio\n\nAnswer: C. IntelliJ IDEA\n\n2. What is the file extension for Java
    // source"
    //            + "files?\n\nA. .java\nB. .class\nC. .jar\nD. .exe\n\nAnswer: A. .java\n\n3. Which
    // of the"
    //            + "following is not a Java primitive type?\n\nA. int\nB. float\nC. String\nD."
    //            + "char\n\nAnswer: C."
    //            + "String\n\n4. Which of the following is not a keyword in Java?\n\nA. public\nB."
    //            + "static\nC."
    //            + "void\nD. native\n\nAnswer: D. native\n\n5. What is the output of the following"
    //            + "code?\n\npublic class Test {\n   public static void main(String[] args) {\n"
    //            + "System.out.println(\"Hello, world!\");\n   }\n}\n\nA. Hello, world!\nB. 0\nC.
    // Hello,"
    //            + "world\nD. compilation error\n\nAnswer: A. Hello, world!"
    //            + "\n\n6. Which of the following is not in Java?\n\nA. let\nB."
    //            + "static\nC."
    //            + "void\nD. mongo\n\nAnswer: D. mongo\n\n7. What is the output of the following"
    //            + "code?\n\npublic class Test {\n   public static void main(String[] args) {\n"
    //            + "System.out.println(\"Hello, world!\");\n   }\n}\n\nA. Hello, world!\nB. 0\nC.
    // Helloooo,"
    //            + "world\nD. compilation err\n\nAnswer: A. Hell0o, world!";
    return challengeParser.parseText(completedText);
  }
}
