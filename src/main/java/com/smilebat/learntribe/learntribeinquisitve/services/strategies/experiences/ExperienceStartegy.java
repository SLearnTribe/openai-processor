package com.smilebat.learntribe.learntribeinquisitve.services.strategies.experiences;

/**
 * A Common Strategy interface for all experiences.
 *
 * <p>Copyright &copy; 2022 Smile .Bat
 *
 * @author Pai,Sai Nandan
 */
public interface ExperienceStartegy<P extends ExperienceContext> {
  /**
   * Updates any experience context with latest values.
   *
   * @param p the {@link ExperienceContext}.
   */
  void updateExperiences(P p);
}
