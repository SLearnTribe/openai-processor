package com.smilebat.learntribe.learntribeinquisitve.services;

import com.google.common.base.Verify;
import com.smilebat.learntribe.analytics.response.CandidateActivitiesResponse;
import com.smilebat.learntribe.analytics.response.HrHiringsResponse;
import com.smilebat.learntribe.dataaccess.OthersBusinessRepository;
import com.smilebat.learntribe.dataaccess.UserAstReltnRepository;
import com.smilebat.learntribe.dataaccess.UserObReltnRepository;
import com.smilebat.learntribe.dataaccess.jpa.entity.OthersBusiness;
import com.smilebat.learntribe.dataaccess.jpa.entity.UserObReltn;
import com.smilebat.learntribe.inquisitve.response.OthersBusinessResponse;
import com.smilebat.learntribe.learntribeinquisitve.converters.AnalyticsConverter;
import com.smilebat.learntribe.learntribeinquisitve.converters.JobConverter;
import java.util.List;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Analytics Service to hold the business logic.
 *
 * <p>Copyright &copy; 2022 Smile .Bat
 *
 * @author Pai,Sai Nandan
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AnalyticsService {

  private final UserAstReltnRepository userAstReltnRepository;

  private final UserObReltnRepository userObReltnRepository;

  private final OthersBusinessRepository othersBusinessRepository;

  private final JobConverter jobConverter;

  private final AnalyticsConverter analyticsConverter;

  /**
   * Evaluates the Candidate related activites.
   *
   * @param keyCloakId the IAM id.
   * @return the {@link CandidateActivitiesResponse}.
   */
  @Transactional
  public CandidateActivitiesResponse retrieveCandidateActivities(String keyCloakId) {
    Verify.verifyNotNull(keyCloakId, "User IAM Id cannot be null");
    final long completedAssessments =
        userAstReltnRepository.countByUserIdAndFilter(keyCloakId, new String[] {"COMPLETED"});
    final long interviewCalls =
        userObReltnRepository.countByUserIdAndStatus(keyCloakId, new String[] {"IN_PROGRESS"});
    return CandidateActivitiesResponse.builder()
        .completed(completedAssessments)
        .interviewCalls(interviewCalls)
        .jobsApplied(0L)
        .build();
  }

  /**
   * Fetches all the Jobs that a candidate is being considered for.
   *
   * @param keyCloakId the IAM id.
   * @return the List of {@link OthersBusinessResponse}
   */
  @Transactional
  public List<OthersBusinessResponse> retrieveConsideredJobs(String keyCloakId) {
    Verify.verifyNotNull(keyCloakId, "User id cannot be null");
    final List<UserObReltn> userObReltns =
        userObReltnRepository.findByUserIdAndStatus(keyCloakId, new String[] {"IN_PROGRESS"});
    if (userObReltns.isEmpty()) {
      log.info("No result found");
    }

    final List<Long> jobIds =
        userObReltns.stream().map(UserObReltn::getJobId).collect(Collectors.toList());
    final List<OthersBusiness> allJobs =
        (List<OthersBusiness>) othersBusinessRepository.findAllById(jobIds);
    return jobConverter.toResponse(allJobs);
  }

  /**
   * Evaluates In Progress Hirings for a Hr User.
   *
   * @param keyCloakId the IAM id.
   * @param paging the {@link Pageable}.
   * @return the List of {@link HrHiringsResponse}.
   */
  @Transactional
  public List<HrHiringsResponse> evaluateHiringsInProgress(String keyCloakId, Pageable paging) {
    Verify.verifyNotNull(keyCloakId, "User Id cannot be null");

    List<OthersBusiness> createdJobs = othersBusinessRepository.findByUserId(keyCloakId, paging);

    return createdJobs
        .stream()
        .map(job -> createHrHiringsResponse(job, "IN_PROGRESS"))
        .filter(job -> job.getJobCount() > 0)
        .collect(Collectors.toList());
  }

  /**
   * Evaluates the last month Hirings of a HR.
   *
   * @param keyCloakId the IAM id.
   * @param paging the {@link Pageable}
   * @return the List of {@link HrHiringsResponse}
   */
  @Transactional
  public List<HrHiringsResponse> evaluateHiringsInLastMonth(String keyCloakId, Pageable paging) {
    Verify.verifyNotNull(keyCloakId, "User Id cannot be null");

    List<OthersBusiness> createdJobs =
        othersBusinessRepository.findByUserIdAndCurrentDate(keyCloakId, paging);

    return createdJobs
        .stream()
        .map(job -> createHrHiringsResponse(job, "HIRED"))
        .filter(job -> job.getJobCount() > 0)
        .collect(Collectors.toList());
  }

  /**
   * Evaluates and creates a Hiring response object.
   *
   * @param othersBusiness the {@link OthersBusiness} JPA entity
   * @param hiringStatus the {@link String} status
   * @return the {@link HrHiringsResponse}
   */
  private HrHiringsResponse createHrHiringsResponse(
      OthersBusiness othersBusiness, String hiringStatus) {
    final Long jobCount =
        userObReltnRepository.countByJobHiringStatus(
            othersBusiness.getId(), new String[] {hiringStatus});

    if (jobCount > 0) {
      return analyticsConverter.toResponse(othersBusiness, jobCount);
    }

    return new HrHiringsResponse();
  }
}
