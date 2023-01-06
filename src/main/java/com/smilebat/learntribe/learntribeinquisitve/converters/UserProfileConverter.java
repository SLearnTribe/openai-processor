package com.smilebat.learntribe.learntribeinquisitve.converters;

import com.smilebat.learntribe.dataaccess.jpa.entity.EducationExperience;
import com.smilebat.learntribe.dataaccess.jpa.entity.UserProfile;
import com.smilebat.learntribe.dataaccess.jpa.entity.WorkExperience;
import com.smilebat.learntribe.enums.Gender;
import com.smilebat.learntribe.inquisitve.UserProfileRequest;
import com.smilebat.learntribe.inquisitve.response.CoreUserProfileResponse;
import com.smilebat.learntribe.inquisitve.response.EducationalExpResponse;
import com.smilebat.learntribe.inquisitve.response.UserProfileResponse;
import com.smilebat.learntribe.inquisitve.response.WorkExperienceResponse;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Profile Converter to map between Entities , Request and Response
 *
 * <p>Copyright &copy; 2022 Smile .Bat
 *
 * @author Pai,Sai Nandan
 */
@Component
@RequiredArgsConstructor
public class UserProfileConverter {
  private final WorkExperienceConverter workExperienceConverter;

  private final EducationExperienceConverter edExperienceConverter;

  /**
   * Updates the entity
   *
   * @param request the {@link UserProfileRequest}
   * @param userProfile the {@link UserProfile}
   */
  public void updateEntity(UserProfileRequest request, UserProfile userProfile) {
    userProfile.setKeyCloakId(request.getKeyCloakId());
    userProfile.setName(request.getName());
    userProfile.setEmail(request.getEmail());
    userProfile.setCountry(request.getCountry());
    userProfile.setLinkedIn(request.getLinkedIn());
    userProfile.setGitHub(request.getGitHub());
    userProfile.setAbout(request.getAbout());
    userProfile.setPhone(request.getPhone());
    String skills = request.getSkills();
    if (skills != null && !skills.isEmpty()) {
      userProfile.setSkills(skills.toUpperCase());
    }
    if (request.getGender() != null) {
      userProfile.setGender(request.getGender());
    }
  }

  /**
   * Converts {@link UserProfileRequest} to {@link UserProfile}.
   *
   * @param request the {@link UserProfileRequest}
   * @return the {@link UserProfile}
   */
  public UserProfile toEntity(UserProfileRequest request) {
    UserProfile profile = new UserProfile();
    updateEntity(request, profile);
    return profile;
  }

  /**
   * Converts List of {@link UserProfileRequest} to List of {@link UserProfile}.
   *
   * @param requests the {@link UserProfileRequest}
   * @return the {@link UserProfile}
   */
  public List<UserProfile> toEntities(final Collection<UserProfileRequest> requests) {
    return requests.stream().map(this::toEntity).collect(Collectors.toList());
  }

  /**
   * Converts the {@link UserProfile} to {@link UserProfileResponse}.
   *
   * @param profile the {@link UserProfile}
   * @return the {@link UserProfileResponse}
   */
  public CoreUserProfileResponse toResponse(UserProfile profile) {
    CoreUserProfileResponse response = new CoreUserProfileResponse();
    response.setAbout(profile.getAbout());
    response.setCountry(profile.getCountry());
    response.setEmail(profile.getEmail());
    response.setGitHub(profile.getGitHub());
    response.setLinkedIn(profile.getLinkedIn());
    response.setName(profile.getName());
    response.setPhone(profile.getPhone());
    response.setSkills(profile.getSkills());
    response.setCurrentRole(profile.getCurrentDesignation());
    Gender gender = profile.getGender();
    if (gender != null) {
      response.setGender(gender.name());
    }

    Set<WorkExperience> experienceSet = profile.getWorkExperiences();
    List<WorkExperienceResponse> workExperienceResponses = Collections.emptyList();
    if (experienceSet != null && !experienceSet.isEmpty()) {
      workExperienceResponses =
          workExperienceConverter.toResponse(experienceSet.stream().collect(Collectors.toList()));
    }

    Set<EducationExperience> edExperienceSet = profile.getEducationExperiences();
    List<EducationalExpResponse> educationalExpResponses = Collections.emptyList();
    if (edExperienceSet != null && !edExperienceSet.isEmpty()) {
      educationalExpResponses =
          edExperienceConverter.toResponse(edExperienceSet.stream().collect(Collectors.toList()));
    }

    response.setWorkExperiences(workExperienceResponses);
    response.setEducationExperiences(educationalExpResponses);
    return response;
  }

  /**
   * Converts List of {@link UserProfile} to List of {@link UserProfileResponse}.
   *
   * @param profiles the List of {@link UserProfile}
   * @return the List of {@link UserProfileResponse}
   */
  public List<? extends UserProfileResponse> toResponse(Collection<UserProfile> profiles) {
    return profiles.stream().map(this::toResponse).collect(Collectors.toList());
  }
}
