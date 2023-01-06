package com.smilebat.learntribe.learntribeinquisitve.services.strategies.experiences;

import com.smilebat.learntribe.dataaccess.jpa.entity.UserProfile;
import com.smilebat.learntribe.inquisitve.UserProfileRequest;
import com.smilebat.learntribe.learntribeinquisitve.services.strategies.experiences.context.EducationExperienceContext;
import com.smilebat.learntribe.learntribeinquisitve.services.strategies.experiences.context.WorkExperienceContext;
import com.smilebat.learntribe.learntribeinquisitve.services.strategies.experiences.strategy.EducationExperienceStartegy;
import com.smilebat.learntribe.learntribeinquisitve.services.strategies.experiences.strategy.WorkExperienceStartegy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Helper Service class for executing the contexts.
 *
 * <p>Copyright &copy; 2022 Smile .Bat
 *
 * @author Pai,Sai Nandan
 */
@Service
@RequiredArgsConstructor
public class ExperienceService {
  private final WorkExperienceStartegy workExperienceStrategy;

  private final WorkExperienceContext workExperienceContext;

  private final EducationExperienceStartegy educationExperienceStartegy;
  private final EducationExperienceContext educationExperienceContext;

  /**
   * Executes all experience contexts.
   *
   * @param request the {@link UserProfileRequest}
   * @param profile the {@link UserProfile}
   */
  public void saveAllExperiences(UserProfileRequest request, UserProfile profile) {
    saveWorkExperiences(request, profile);
    saveEducationExperiences(request, profile);
  }

  /**
   * Executes work experience context.
   *
   * @param request the {@link UserProfileRequest}
   * @param profile the {@link UserProfile}
   */
  private void saveWorkExperiences(UserProfileRequest request, UserProfile profile) {
    workExperienceContext.setProfile(profile);
    workExperienceContext.setRequest(request.getWorkExperiences());
    workExperienceStrategy.updateExperiences(workExperienceContext);
  }

  /**
   * Executes Education experience contexts.
   *
   * @param request the {@link UserProfileRequest}
   * @param profile the {@link UserProfile}
   */
  private void saveEducationExperiences(UserProfileRequest request, UserProfile profile) {
    educationExperienceContext.setProfile(profile);
    educationExperienceContext.setRequest(request.getEducationExperiences());
    educationExperienceStartegy.updateExperiences(educationExperienceContext);
  }
}
