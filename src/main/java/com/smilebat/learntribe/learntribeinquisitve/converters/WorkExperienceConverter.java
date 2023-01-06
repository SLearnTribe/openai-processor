package com.smilebat.learntribe.learntribeinquisitve.converters;

import com.smilebat.learntribe.dataaccess.jpa.entity.WorkExperience;
import com.smilebat.learntribe.inquisitve.WorkExperienceRequest;
import com.smilebat.learntribe.inquisitve.response.WorkExperienceResponse;
import com.smilebat.learntribe.learntribeinquisitve.util.Commons;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Work Experience Converter to map between Entities , Request and Response
 *
 * <p>Copyright &copy; 2022 Smile .Bat
 *
 * @author Pai,Sai Nandan
 */
@Component
@RequiredArgsConstructor
public final class WorkExperienceConverter {

  private final Commons commons;

  /**
   * Converts {@link WorkExperienceRequest} to {@link WorkExperience}.
   *
   * @param request the {@link WorkExperienceRequest}
   * @return the {@link WorkExperience}
   */
  public WorkExperience toEntity(WorkExperienceRequest request) {
    WorkExperience workExperience = new WorkExperience();
    workExperience.setId(request.getId());
    workExperience.setDesignation(request.getDesignation());
    workExperience.setOrgName(request.getOrgName());
    String startDate = request.getStartDate();
    String endDate = request.getEndDate();
    if (startDate != null) {
      workExperience.setStartDate(commons.toInstant(startDate));
    }
    if (endDate != null) {
      workExperience.setEndDate(commons.toInstant(endDate));
    }
    workExperience.setYears(request.getYears());
    workExperience.setLocation(request.getLocation());
    return workExperience;
  }

  /**
   * Converts List of {@link WorkExperienceRequest} to List of {@link WorkExperience}.
   *
   * @param list the list of {@link WorkExperienceRequest}
   * @return the {@link WorkExperience}
   */
  public Set<WorkExperience> toEntities(Collection<WorkExperienceRequest> list) {
    return list.stream().map(this::toEntity).collect(Collectors.toSet());
  }

  /**
   * Converts {@link WorkExperience} to {@link WorkExperienceResponse}.
   *
   * @param workExperience the {@link WorkExperience}
   * @return the {@link WorkExperienceResponse}
   */
  public WorkExperienceResponse toResponse(WorkExperience workExperience) {
    WorkExperienceResponse response = new WorkExperienceResponse();
    response.setId(workExperience.getId());
    response.setDesignation(workExperience.getDesignation());
    response.setLocation(workExperience.getLocation());
    Instant startDate = workExperience.getStartDate();
    Instant endDate = workExperience.getEndDate();
    if (startDate != null) {
      response.setStartDate(commons.formatInstant.apply(startDate));
    }
    if (endDate != null) {
      response.setEndDate(commons.formatInstant.apply(endDate));
    }
    response.setYears(workExperience.getYears());
    response.setOrgName(workExperience.getOrgName());
    return response;
  }

  /**
   * Converts List of {@link WorkExperience} to List of {@link WorkExperienceResponse}.
   *
   * @param workExperiences the list of {@link WorkExperience}
   * @return the list of {@link WorkExperienceResponse}
   */
  public List<WorkExperienceResponse> toResponse(List<WorkExperience> workExperiences) {
    return workExperiences.stream().map(this::toResponse).collect(Collectors.toList());
  }
}
