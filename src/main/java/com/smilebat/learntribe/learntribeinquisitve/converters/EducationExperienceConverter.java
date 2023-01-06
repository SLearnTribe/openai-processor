package com.smilebat.learntribe.learntribeinquisitve.converters;

import com.smilebat.learntribe.dataaccess.jpa.entity.EducationExperience;
import com.smilebat.learntribe.inquisitve.EducationalExpRequest;
import com.smilebat.learntribe.inquisitve.WorkExperienceRequest;
import com.smilebat.learntribe.inquisitve.response.EducationalExpResponse;
import com.smilebat.learntribe.learntribeinquisitve.util.Commons;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Education Experience Converter to map between Entities , Request and Response
 *
 * <p>Copyright &copy; 2022 Smile .Bat
 *
 * @author Pai,Sai Nandan
 */
@Component
@RequiredArgsConstructor
public class EducationExperienceConverter {

  private final Commons commons;

  /**
   * Converts {@link EducationalExpRequest} to {@link EducationExperience}.
   *
   * @param request the {@link WorkExperienceRequest}
   * @return the {@link EducationExperience}
   */
  public EducationExperience toEntity(EducationalExpRequest request) {
    EducationExperience edExperience = new EducationExperience();
    edExperience.setId(request.getId());
    edExperience.setDegree(request.getDegree());
    edExperience.setCollegeName(request.getCollegeName());
    edExperience.setFieldOfStudy(request.getFieldOfStudy());
    String endDate = request.getDateOfCompletion();
    if (endDate != null) {
      edExperience.setDateOfCompletion(commons.toInstant(endDate));
    }
    return edExperience;
  }

  /**
   * Converts List of {@link EducationalExpRequest} to List of {@link EducationExperience}.
   *
   * @param list the list of {@link WorkExperienceRequest}
   * @return the set of {@link EducationExperience}
   */
  public Set<EducationExperience> toEntities(Collection<EducationalExpRequest> list) {
    return list.stream().map(this::toEntity).collect(Collectors.toSet());
  }

  /**
   * Converts {@link EducationExperience} to {@link EducationalExpResponse}.
   *
   * @param edExperience the {@link EducationExperience}
   * @return the {@link EducationalExpResponse}
   */
  public EducationalExpResponse toResponse(EducationExperience edExperience) {
    EducationalExpResponse response = new EducationalExpResponse();
    response.setId(edExperience.getId());
    response.setDegree(edExperience.getDegree());
    response.setCollegeName(edExperience.getCollegeName());
    response.setFieldOfStudy(edExperience.getFieldOfStudy());
    Instant dateOfCompletion = edExperience.getDateOfCompletion();
    if (dateOfCompletion != null) {
      response.setDateOfCompletion(commons.formatInstant.apply(dateOfCompletion));
    }
    return response;
  }

  /**
   * Converts List of {@link EducationExperience} to List of {@link EducationalExpResponse}.
   *
   * @param educationExperiences the list of {@link EducationExperience}
   * @return the list of {@link EducationalExpResponse}
   */
  public List<EducationalExpResponse> toResponse(List<EducationExperience> educationExperiences) {
    return educationExperiences.stream().map(this::toResponse).collect(Collectors.toList());
  }
}
