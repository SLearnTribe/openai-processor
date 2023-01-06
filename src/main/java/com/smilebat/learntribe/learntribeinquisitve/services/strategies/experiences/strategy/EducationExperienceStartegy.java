package com.smilebat.learntribe.learntribeinquisitve.services.strategies.experiences.strategy;

import com.smilebat.learntribe.dataaccess.jpa.entity.EducationExperience;
import com.smilebat.learntribe.learntribeinquisitve.services.strategies.experiences.DefaultExperienceStrategy;
import com.smilebat.learntribe.learntribeinquisitve.services.strategies.experiences.context.EducationExperienceContext;
import java.util.TreeSet;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Business Logic implementation for computing education experiences
 *
 * <p>Copyright &copy; 2022 Smile .Bat
 *
 * @author Pai,Sai Nandan
 */
@Slf4j
@Service
public final class EducationExperienceStartegy
    extends DefaultExperienceStrategy<EducationExperienceContext, EducationExperience> {
  @Override
  public void updateExperiences(EducationExperienceContext context) {
    log.info("Updating Education Experiences for User");
    super.updateExperiences(context);
    context.getProfile().setEducationExperiences(new TreeSet<>(context.getUpdatedExperiences()));
  }
}
