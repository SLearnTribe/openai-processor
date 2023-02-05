package com.smilebat.learntribe.openai.services.helpers;

import com.smilebat.learntribe.dataaccess.jpa.entity.Challenge;
import java.util.HashSet;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Parser to parse open AI text and convert to Challenges.
 *
 * <p>Copyright &copy; 2022 Smile .Bat
 *
 * @author Pai,Sai Nandan
 */
@Slf4j
@Component
public class ChallengeParser {

  /**
   * Parses the text obtained from open ai text completion.
   *
   * @param text the {@link String} text to be parsed
   * @return the list of {@link Challenge}.
   */
  public Set<Challenge> parseText(String text) {
    String[] arr = text.split("\n");
    ChallengePattern prevType = ChallengePattern.RAW;
    AbstractChallenge absractChallenge = new AbstractChallenge();

    Set<Challenge> challenges = new HashSet<>(15);
    int index = 0;
    String ostr = null;

    while (index < arr.length) {
      while (arr[index].isBlank()) {
        index += 1;
      }
      String inputText = arr[index];
      ChallengePattern currType = ChallengePattern.evaluate(inputText);

      if (prevType != currType && currType != ChallengePattern.TEXT) {
        absractChallenge.selectAbstraction(prevType, ostr);
      }

      if (currType == ChallengePattern.TEXT) {
        ostr = ostr + "\n" + inputText;
      } else {
        prevType = currType;
        ostr = "";
        ostr = ostr + "\n" + inputText;
      }

      if (absractChallenge.isChallengeParsed()) {
        String[] optionsArr = absractChallenge.getOptions().split("\n");
        if (optionsArr.length > 2) {
          challenges.add(absractChallenge.getChallenge());
          absractChallenge = new AbstractChallenge();
        }
      }
      index++;
    }
    return challenges;
  }
}
