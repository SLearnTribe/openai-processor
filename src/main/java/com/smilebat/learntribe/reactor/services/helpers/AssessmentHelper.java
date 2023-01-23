package com.smilebat.learntribe.reactor.services.helpers;

import com.smilebat.learntribe.dataaccess.jpa.entity.Assessment;
import com.smilebat.learntribe.dataaccess.jpa.entity.UserAstReltn;
import com.smilebat.learntribe.enums.AssessmentDifficulty;
import com.smilebat.learntribe.enums.AssessmentStatus;
import com.smilebat.learntribe.enums.UserAstReltnType;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * Assessment Service Helper methods.
 *
 * <p>Copyright &copy; 2022 Smile .Bat
 *
 * @author Pai,Sai Nandan
 */
@Component
public class AssessmentHelper {

  private Assessment createSystemAssessment(String skill, AssessmentDifficulty difficulty) {
    Assessment assessment = new Assessment();
    assessment.setTitle(skill.toUpperCase().trim());
    assessment.setDifficulty(difficulty);
    assessment.setDescription("Recommended");
    assessment.setCreatedBy("SYSTEM");
    return assessment;
  }

  /**
   * Helper methods to create default Assessment entities.
   *
   * @param skill the User Skill.
   * @return the List of {@link Assessment} entities.
   */
  public List<Assessment> createDefaultAssessments(String skill) {
    return List.of(createSystemAssessment(skill, AssessmentDifficulty.BEGINNER));
    // createSystemAssessment(skill, AssessmentDifficulty.INTERMEDIATE));
  }

  /**
   * Creates a User Assessment relation object for HR.
   *
   * @param userId the keyCloak user Id
   * @param assessment the Assessment to be assigned
   * @return the {@link UserAstReltn}
   */
  public UserAstReltn createUserAstReltnForCandidate(String userId, Assessment assessment) {
    UserAstReltn userAstReltn = new UserAstReltn();
    userAstReltn.setUserId(userId);
    userAstReltn.setAssessmentId(assessment.getId());
    userAstReltn.setAssessmentTitle(assessment.getTitle());
    userAstReltn.setStatus(AssessmentStatus.PENDING);
    userAstReltn.setUserAstReltnType(UserAstReltnType.ASSIGNED);
    return userAstReltn;
  }

  /**
   * Creates a User Assessment relation object for HR.
   *
   * @param userId the keyCloak user Id
   * @param assessment the Assessment to be assigned
   * @return the {@link UserAstReltn}
   */
  public UserAstReltn createUserAstReltnForHr(String userId, Assessment assessment) {
    UserAstReltn userAstReltn = new UserAstReltn();
    userAstReltn.setUserId(userId);
    userAstReltn.setAssessmentId(assessment.getId());
    userAstReltn.setAssessmentTitle(assessment.getTitle());
    userAstReltn.setStatus(AssessmentStatus.DEFAULT);
    userAstReltn.setUserAstReltnType(UserAstReltnType.CREATED);
    return userAstReltn;
  }
}
