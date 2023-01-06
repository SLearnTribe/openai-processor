package com.smilebat.learntribe.learntribeinquisitve.converters;

import com.smilebat.learntribe.analytics.response.HrHiringsResponse;
import com.smilebat.learntribe.dataaccess.jpa.entity.OthersBusiness;
import com.smilebat.learntribe.enums.EmploymentType;
import com.smilebat.learntribe.enums.JobStatus;
import com.smilebat.learntribe.learntribeinquisitve.util.Commons;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Analytics Converter to map between Entities, Request and Response
 *
 * <p>Copyright &copy; 2022 Smile .Bat
 *
 * @author Pai,Sai Nandan
 */
@Component
@RequiredArgsConstructor
public class AnalyticsConverter {
  private final Commons commons;

  /**
   * Converts {@link OthersBusiness} to {@link HrHiringsResponse}.
   *
   * @param othersBusiness the {@link OthersBusiness}.
   * @param jobCount the count of the josb fetched.
   * @return the {@link HrHiringsResponse}.
   */
  public HrHiringsResponse toResponse(OthersBusiness othersBusiness, Long jobCount) {
    HrHiringsResponse response = new HrHiringsResponse();
    response.setJobTitle(othersBusiness.getTitle());
    response.setSkills(othersBusiness.getRequiredSkills());
    Instant createdDate = othersBusiness.getCreatedDate();
    if (createdDate != null) {
      response.setJobPostedOn(commons.formatInstant.apply(createdDate));
    }

    JobStatus status = othersBusiness.getStatus();
    if (status != null) {
      response.setJobStatus(status.name());
    }
    response.setJobCount(jobCount);
    response.setBusinessName(othersBusiness.getBusinessName());
    EmploymentType type = othersBusiness.getEmploymentType();
    if (type != null) {
      response.setEmploymentType(type.name());
    }
    return response;
  }
}
