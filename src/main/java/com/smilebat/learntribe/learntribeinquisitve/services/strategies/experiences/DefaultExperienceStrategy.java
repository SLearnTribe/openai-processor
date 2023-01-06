package com.smilebat.learntribe.learntribeinquisitve.services.strategies.experiences;

import com.smilebat.learntribe.dataaccess.jpa.entity.Experience;
import java.util.Collection;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

/**
 * The default business logic for all experience computations.
 *
 * <p>Copyright &copy; 2022 Smile .Bat
 *
 * @author Pai,Sai Nandan
 */
@Slf4j
public class DefaultExperienceStrategy<P extends ExperienceContext, R extends Experience>
    implements ExperienceStartegy<P> {

  @Override
  public void updateExperiences(P context) {
    log.info("Updating User Experiences");
    Collection<R> existingExperiences = context.getExistingExperiences();
    Collection<R> updatedExperiences = context.getRequestExperiences();
    context.setUpdatedExperiences(updatedExperiences);
    Set<Long> deletedExperienceIds =
        evaluateDeletedExperienceIds(existingExperiences, updatedExperiences);
    context.getRepository().deleteAllById(deletedExperienceIds);
  }

  protected Set<Long> evaluateDeletedExperienceIds(
      Collection<R> existingExp, Collection<R> updatedExp) {
    return existingExp
        .stream()
        .map(Experience::getId)
        .filter(isExperienceDeleted(updatedExp))
        .collect(Collectors.toSet());
  }

  private Predicate<Long> isExperienceDeleted(Collection<R> updatedExp) {
    return id ->
        updatedExp
            .stream()
            .map(Experience::getId)
            .filter(eid -> eid != null)
            .anyMatch(requestId -> !requestId.equals(id));
  }
}
