package com.smilebat.learntribe.openai.services.helpers;

import com.smilebat.learntribe.dataaccess.jpa.entity.Challenge;
import io.micrometer.core.instrument.util.StringUtils;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import lombok.ToString;

/**
 * An intermediate representation of challenges.
 *
 * <p>Copyright &copy; 2023 Smile .Bat.
 *
 * @author Pai,Sai Nandan.
 */
@ToString
public class AbstractChallenge {

  private Set<String> loptions = new HashSet<>(4);
  private String question;
  private String answer;
  private boolean parsedQuestion = false;
  private boolean parsedAnswer = false;
  private boolean parsedOptions = false;

  /**
   * Sets the answer.
   *
   * @param answer the {@link String}.
   */
  private void setAnswer(String answer) {
    this.answer = answer;
    this.parsedAnswer = true;
  }

  /**
   * Sets the question.
   *
   * @param question the {@link String}
   */
  private void setQuestion(String question) {
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
   * @param type the {@link ChallengePattern} of the input text
   * @param input the {@link String} input text.
   */
  public void selectAbstraction(ChallengePattern type, String input) {
    switch (type) {
      case QUESTION:
        setQuestion(input);
        break;
      case ANSWER:
        setAnswer(input);
        break;
      case OPTIONS:
        evaluateOptions(input);
        break;
      default:
        break;
    }
  }

  /**
   * Sets the options.
   *
   * @param input the {@link String} input text.
   */
  private void evaluateOptions(String input) {
    Arrays.stream(input.split("[a-zA-Z][.)]"))
        .filter(StringUtils::isNotBlank)
        .map(String::trim)
        .forEach(loptions::add);
    if (loptions.size() > 2) {
      this.parsedOptions = true;
    }
  }

  /**
   * Creates a fresh challenge from abstract
   *
   * @return the new {@link Challenge}.
   */
  public Challenge getChallenge() {
    Challenge challenge = new Challenge();
    challenge.setOptions(loptions.stream().toArray(s -> new String[s]));
    challenge.setQuestion(question);
    challenge.setAnswer(answer);
    return challenge;
  }
}
