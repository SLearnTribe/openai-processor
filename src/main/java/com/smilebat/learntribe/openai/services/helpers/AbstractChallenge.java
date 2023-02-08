package com.smilebat.learntribe.openai.services.helpers;

import com.smilebat.learntribe.dataaccess.jpa.entity.Challenge;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;

/**
 * An intermediate representation of challenges.
 *
 * <p>Copyright &copy; 2023 Smile .Bat.
 *
 * @author Pai,Sai Nandan.
 */
@Getter
public class AbstractChallenge {
  private String options = "";
  private String question = "";
  private String answer = "";
  private boolean parsedQuestion = false;
  private boolean parsedAnswer = false;
  private boolean parsedOptions = false;

  /**
   * Sets the answer.
   *
   * @param answer the {@link String}.
   */
  public void setAnswer(String answer) {
    this.answer = answer;
    this.parsedAnswer = true;
  }

  /**
   * Sets the options.
   *
   * @param options the {@link String}.
   */
  public void setOptions(String options) {
    this.options = options;
    this.parsedOptions = true;
  }

  /**
   * Sets the question.
   *
   * @param question the {@link String}
   */
  public void setQuestion(String question) {
    this.question = question;
    this.parsedQuestion = true;
  }

  /**
   * Returns true if challenge parsing is complete.
   *
   * @return the boolean true/false.
   */
  public boolean isChallengeParsed() {
    return parsedQuestion && parsedAnswer && parsedOptions;
  }

  /**
   * Sets the input string based on the pattern
   *
   * @param prevType the {@link ChallengePattern} of the previous text
   * @param input the {@link String} input text.
   */
  public void selectAbstraction(ChallengePattern prevType, String input) {
    if (prevType == ChallengePattern.QUESTION) {
      this.setQuestion(input);
      // question = ostr;
      // parsedQuestion = true;
    }
    if (prevType == ChallengePattern.OPTIONS) {
      // options = ostr;
      // parsedOptions = true;
      this.setOptions(input);
    }
    if (prevType == ChallengePattern.ANSWER) {
      //          answer = ostr;
      //          parsedAnswer = true;
      this.setAnswer(input);
    }
  }

  /**
   * Creates a fresh challenge from abstract
   *
   * @return the new {@link Challenge}.
   */
  public Challenge getChallenge() {
    Challenge challenge = new Challenge();
    String[] parsedTextOptions = this.getOptions().split("\n");
    Set<String> sanitizedOptions = new HashSet<>();
    for (int i = 0; i < parsedTextOptions.length; i++) {
      if (!parsedTextOptions[i].isEmpty()) {
        sanitizedOptions.add(parsedTextOptions[i]);
      }
    }
    challenge.setOptions(sanitizedOptions.stream().toArray(s -> new String[s]));
    challenge.setQuestion(this.getQuestion());
    challenge.setAnswer(this.getAnswer().split("\n")[1]);
    return challenge;
  }
}
