package com.smilebat.learntribe.learntribeinquisitve.services.strategies.experiences;

import com.smilebat.learntribe.dataaccess.jpa.entity.Experience;
import com.smilebat.learntribe.dataaccess.jpa.entity.UserProfile;
import java.util.Collection;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * A Common context for all experience computations.
 *
 * <p>Copyright &copy; 2022 Smile .Bat
 *
 * @author Pai,Sai Nandan
 */
public abstract class ExperienceContext<P extends Experience, Q extends JpaRepository> {

  @Setter @Getter private Collection<P> updatedExperiences;

  /**
   * Abstract function for getting repository.
   *
   * @return the {@link JpaRepository}
   */
  public abstract Q getRepository();

  /**
   * Abstraction for request.
   *
   * @return the {@link java.util.Collections}
   */
  public abstract Collection<P> getRequestExperiences();

  /**
   * Abstraction for Existing Experiences Computation.
   *
   * @return the {@link java.util.Collections}
   */
  public abstract Collection<P> getExistingExperiences();

  /**
   * Abstract function for original User Context.
   *
   * @return the {@link UserProfile}
   */
  public abstract UserProfile getProfile();
}
