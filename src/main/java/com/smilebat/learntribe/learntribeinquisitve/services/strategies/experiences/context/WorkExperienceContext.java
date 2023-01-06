package com.smilebat.learntribe.learntribeinquisitve.services.strategies.experiences.context;

import com.smilebat.learntribe.dataaccess.WorkExperienceRepository;
import com.smilebat.learntribe.dataaccess.jpa.entity.UserProfile;
import com.smilebat.learntribe.dataaccess.jpa.entity.WorkExperience;
import com.smilebat.learntribe.inquisitve.WorkExperienceRequest;
import com.smilebat.learntribe.learntribeinquisitve.converters.WorkExperienceConverter;
import com.smilebat.learntribe.learntribeinquisitve.services.strategies.experiences.ExperienceContext;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

/**
 * Context for work experience
 *
 * <p>Copyright &copy; 2022 Smile .Bat
 *
 * @author Pai,Sai Nandan
 */
@Component
@RequiredArgsConstructor
public final class WorkExperienceContext
    extends ExperienceContext<WorkExperience, WorkExperienceRepository> {
  private final WorkExperienceConverter converter;

  private final WorkExperienceRepository repository;

  @Setter @Getter private Collection<WorkExperienceRequest> request;

  @Getter @Setter private UserProfile profile;

  @Override
  public WorkExperienceRepository getRepository() {
    return this.repository;
  }

  @Override
  public Set<WorkExperience> getRequestExperiences() {
    return request == null ? Collections.emptySet() : converter.toEntities(request);
  }

  @Override
  public Set<WorkExperience> getExistingExperiences() {
    return profile.getWorkExperiences();
  }
}
