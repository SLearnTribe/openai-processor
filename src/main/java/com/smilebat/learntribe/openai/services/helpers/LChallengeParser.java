package com.smilebat.learntribe.openai.services.helpers;

import com.smilebat.learntribe.dataaccess.jpa.entity.Challenge;
import java.util.HashSet;
import java.util.Set;
import org.springframework.stereotype.Component;

/**
 * Parser to parse open AI text and convert to Challenges.
 *
 * <p>Copyright &copy; 2023 Smile .Bat
 *
 * @author Pai,Sai Nandan
 */
@Component
public class LChallengeParser {

  /**
   * Parses the text obtained from open ai text completion.
   *
   * @param text the {@link String} text to be parsed
   * @return the list of {@link Challenge}.
   */
  public Set<Challenge> parseText(String text) {
    String[] arr = text.split("\n");
    AbstractChallenge absractChallenge = new AbstractChallenge();
    Set<Challenge> challenges = new HashSet<>(3);
    int index = 0;

    while (index < arr.length) {
      while (arr[index].isBlank()) {
        index += 1;
      }
      String inputText = arr[index].trim();
      absractChallenge.selectAbstraction(ChallengePattern.evaluate(inputText), inputText);
      if (absractChallenge.isChallengeParsed()) {
        challenges.add(absractChallenge.getChallenge());
        absractChallenge = new AbstractChallenge();
      }
      index++;
    }
    return challenges;
  }
}
