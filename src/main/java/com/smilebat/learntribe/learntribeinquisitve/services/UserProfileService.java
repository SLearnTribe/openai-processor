package com.smilebat.learntribe.learntribeinquisitve.services;

import com.google.common.base.Verify;
import com.smilebat.learntribe.assessment.response.AssessmentStatusResponse;
import com.smilebat.learntribe.dataaccess.UserAstReltnRepository;
import com.smilebat.learntribe.dataaccess.UserProfileRepository;
import com.smilebat.learntribe.dataaccess.UserProfileSearchRepository;
import com.smilebat.learntribe.dataaccess.jpa.entity.UserAstReltn;
import com.smilebat.learntribe.dataaccess.jpa.entity.UserProfile;
import com.smilebat.learntribe.enums.AssessmentStatus;
import com.smilebat.learntribe.inquisitve.UserProfileRequest;
import com.smilebat.learntribe.inquisitve.response.CoreUserProfileResponse;
import com.smilebat.learntribe.inquisitve.response.UserProfileResponse;
import com.smilebat.learntribe.learntribeinquisitve.converters.UserProfileConverter;
import com.smilebat.learntribe.learntribeinquisitve.services.strategies.experiences.ExperienceService;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class related user business logic.
 *
 * <p>Copyright &copy; 2022 Smile .Bat
 *
 * @author Sanjay
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserProfileService {

  /* Declation for Repositories */
  private final UserProfileRepository userProfileRepository;
  private final UserProfileSearchRepository userProfileSearchRepository;

  /* Declation for Converters*/
  private final UserProfileConverter profileConverter;

  private final UserAstReltnRepository userAstReltnRepository;

  private final ExperienceService experienceService;

  /**
   * Retrieves all the user profile details based on id.
   *
   * @param userId the {@link String} user id
   * @return the {@link UserProfileResponse}
   */
  @Transactional(readOnly = true)
  public UserProfileResponse getUserInfo(String userId) {
    Verify.verifyNotNull(userId, "User Id cannot be null");
    UserProfile profile = userProfileRepository.findByKeyCloakId(userId);
    return Optional.ofNullable(profile)
        .map(profileConverter::toResponse)
        .orElseGet(CoreUserProfileResponse::new);
  }

  /**
   * Retrieves all the user profile based on skill.
   *
   * @param skill skill necessary in the candidate.
   * @param pageNo page number for pageination
   * @param pageSize for pageination
   * @return the {@link UserProfileResponse}
   */
  @Transactional(readOnly = true)
  public List<? extends UserProfileResponse> getUserInfoBySkill(
      String skill, int pageNo, int pageSize) {
    Verify.verifyNotNull(skill, "Skill cannot be empty");
    Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
    List<UserProfile> userProfile = userProfileRepository.findBySkills(skill, pageable);
    if (userProfile == null) {
      return Collections.emptyList();
    }

    return profileConverter.toResponse(userProfile);
  }

  /**
   * Retrieves all the user profile details.
   *
   * @param page page number for pageination.
   * @param limit for pageination per page.
   * @param keyword to match with participant
   * @return the List of {@link UserProfileResponse}
   */
  @Transactional(readOnly = true)
  public List<CoreUserProfileResponse> getAllUserInfo(int page, int limit, String keyword) {
    Pageable pageable = PageRequest.of(page - 1, limit);
    List<UserProfile> userProfiles = null;

    try {
      userProfiles = retrieveUserProfiles(keyword, pageable);
    } catch (InterruptedException ex) {
      log.info("Failed searching database for keyword {}", keyword);
    }

    if (userProfiles == null || userProfiles.isEmpty()) {
      log.info("No User Profiles found");
      return Collections.emptyList();
    }

    return userProfiles.stream().map(this::getCoreUserProfileDetails).collect(Collectors.toList());
  }

  private CoreUserProfileResponse getCoreUserProfileDetails(UserProfile profile) {
    CoreUserProfileResponse userProfileResponse = profileConverter.toResponse(profile);
    List<UserAstReltn> reltns =
        userAstReltnRepository.findByUserIdAndFilter(
            profile.getKeyCloakId(), new String[] {AssessmentStatus.COMPLETED.name()});
    List<AssessmentStatusResponse> assessmentStatusResponses =
        reltns.stream().map(this::getAssessmentStatuses).collect(Collectors.toList());
    userProfileResponse.setCompletedAssessments(assessmentStatusResponses);
    return userProfileResponse;
  }

  private AssessmentStatusResponse getAssessmentStatuses(UserAstReltn userAstReltn) {
    AssessmentStatusResponse statusResponse = new AssessmentStatusResponse();
    statusResponse.setStatus(userAstReltn.getStatus());
    statusResponse.setSkill(userAstReltn.getAssessmentTitle());
    return statusResponse;
  }

  /**
   * Retrieves Users Profiles based on keyword in a pageable manner
   *
   * @param keyword the search key
   * @param pageable the {@link Pageable}
   * @return the List of {@link UserProfile}.
   * @throws InterruptedException on db failure.
   */
  private List<UserProfile> retrieveUserProfiles(String keyword, Pageable pageable)
      throws InterruptedException {
    if (keyword == null || keyword.isEmpty()) {
      Page<UserProfile> userProfiles = userProfileRepository.findAll(pageable);
      return userProfiles.stream().collect(Collectors.toList());
    }
    return userProfileSearchRepository.search(keyword, pageable);
  }

  /**
   * Saves/Updates all the user profile details.
   *
   * @param profileRequest the {@link UserProfileRequest}
   */
  @Transactional
  public void saveUserProfile(UserProfileRequest profileRequest) {
    String keycloakId = profileRequest.getKeyCloakId();
    Verify.verifyNotNull(keycloakId, "User Id cannot be null");
    Verify.verifyNotNull(profileRequest, "User Profile Request cannot be null");
    UserProfile existingUserProfile = userProfileRepository.findByKeyCloakId(keycloakId);
    UserProfile userProfile = Optional.ofNullable(existingUserProfile).orElseGet(UserProfile::new);
    profileConverter.updateEntity(profileRequest, userProfile);
    experienceService.saveAllExperiences(profileRequest, userProfile);
    userProfileRepository.save(userProfile);
  }
}
