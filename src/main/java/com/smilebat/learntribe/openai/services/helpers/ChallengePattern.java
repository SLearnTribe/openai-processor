package com.smilebat.learntribe.openai.services.helpers;

import java.util.regex.Pattern;
import lombok.Getter;

/**
 * Holds the Challenge abstract type.
 *
 * <p>Copyright &copy; 2022 Smile .Bat
 *
 * @author Pai,Sai Nandan
 */
public enum ChallengePattern {
  QUESTION("QUESTION"),
  OPTIONS("OPTIONS"),
  ANSWER("ANSWER"),

  TEXT("TEXT"),
  RAW("RAW");

  @Getter private final String type;

  ChallengePattern(String type) {
    this.type = type;
  }

  public static final Pattern questionPattern = Pattern.compile("[0-9].*[?]"); // find the questions
  public static final Pattern answerPattern =
      Pattern.compile("[aA][n][s][w][e][r][:] [a-zA-Z]"); // find the answers

  public static final Pattern optionsPattern = Pattern.compile("[a-zA-Z][.)]");

  /**
   * Evaluates the {@link ChallengePattern} of input text.
   *
   * @param subText the {@link String}.
   * @return the {@link ChallengePattern}.
   */
  public static ChallengePattern evaluate(String subText) {
    if (questionPattern.matcher(subText).find()) {
      return ChallengePattern.QUESTION;
    }
    if (answerPattern.matcher(subText).find()) {
      return ChallengePattern.ANSWER;
    }
    if (optionsPattern.matcher(subText).find()) {
      return ChallengePattern.OPTIONS;
    }
    if (subText.contains("*end*")) {
      return ChallengePattern.RAW;
    }
    return ChallengePattern.TEXT;
  }
}
