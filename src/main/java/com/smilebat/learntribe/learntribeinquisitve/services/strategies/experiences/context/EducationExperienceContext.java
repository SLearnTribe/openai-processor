package com.smilebat.learntribe.learntribeinquisitve.services.strategies.experiences.context;

import com.smilebat.learntribe.dataaccess.EducationExperienceRepository;
import com.smilebat.learntribe.dataaccess.jpa.entity.EducationExperience;
import com.smilebat.learntribe.dataaccess.jpa.entity.UserProfile;
import com.smilebat.learntribe.inquisitve.EducationalExpRequest;
import com.smilebat.learntribe.learntribeinquisitve.converters.EducationExperienceConverter;
import com.smilebat.learntribe.learntribeinquisitve.services.strategies.experiences.ExperienceContext;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

/**
 * Context for education experience
 *
 * <p>Copyright &copy; 2022 Smile .Bat
 *
 * @author Pai,Sai Nandan
 */
@Component
@RequiredArgsConstructor
public final class EducationExperienceContext
    extends ExperienceContext<EducationExperience, EducationExperienceRepository> {

  private final EducationExperienceConverter converter;
  private final EducationExperienceRepository repository;

  @Getter @Setter private Collection<EducationalExpRequest> request;

  @Setter @Getter private UserProfile profile;

  @Override
  public EducationExperienceRepository getRepository() {
    return this.repository;
  }

  @Override
  public Set<EducationExperience> getRequestExperiences() {
    return request == null ? Collections.emptySet() : converter.toEntities(request);
  }

  @Override
  public Set<EducationExperience> getExistingExperiences() {
    return profile.getEducationExperiences();
  }
}
