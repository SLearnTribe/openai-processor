package com.smilebat.learntribe.learntribeinquisitve.controllers;

import com.smilebat.learntribe.analytics.response.CandidateActivitiesResponse;
import com.smilebat.learntribe.analytics.response.HrHiringsResponse;
import com.smilebat.learntribe.assessment.response.AssessmentResponse;
import com.smilebat.learntribe.inquisitve.response.OthersBusinessResponse;
import com.smilebat.learntribe.learntribeinquisitve.services.AnalyticsService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Analytics Controller
 *
 * <p>Copyright &copy; 2022 Smile .Bat
 *
 * @author Pai,Sai Nandan
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/analytics")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class AnalyticsController {

  private final AnalyticsService analyticsService;

  /**
   * Retrieves all the related activities of User.
   *
   * @param keyCloakId the user keycloak id
   * @return the {@link List} of {@link AssessmentResponse}
   */
  @GetMapping(value = "/candidate/activities")
  @ResponseBody
  @ApiOperation(
      value = "Retrieves candidates status activities",
      notes = "Fetches all candidate activities")
  @ApiResponses(
      value = {
        @ApiResponse(
            code = 200,
            message = "Successfully retrieved",
            response = CandidateActivitiesResponse.class),
        @ApiResponse(code = 400, message = "Bad Request"),
        @ApiResponse(code = 401, message = "Unauthorized"),
        @ApiResponse(code = 403, message = "Forbidden"),
        @ApiResponse(code = 404, message = "Url Not found"),
        @ApiResponse(code = 422, message = "Invalid Data"),
      })
  public ResponseEntity<CandidateActivitiesResponse> evaluateCandidateActivities(
      @AuthenticationPrincipal(expression = "subject") String keyCloakId) {

    CandidateActivitiesResponse response = analyticsService.retrieveCandidateActivities(keyCloakId);

    return ResponseEntity.ok(response);
  }

  /**
   * Retrieves all the considered jobs of candidate.
   *
   * @param keyCloakId the user keycloak id
   * @return the {@link List} of {@link OthersBusinessResponse}
   */
  @GetMapping(value = "/candidate/jobs")
  @ResponseBody
  @ApiOperation(
      value = "Retrieves Considered jobs for a candidate",
      notes = "Fetches all the considered job for a candidate")
  @ApiResponses(
      value = {
        @ApiResponse(
            code = 200,
            message = "Successfully retrieved",
            response = OthersBusinessResponse.class,
            responseContainer = "List"),
        @ApiResponse(code = 400, message = "Bad Request"),
        @ApiResponse(code = 401, message = "Unauthorized"),
        @ApiResponse(code = 403, message = "Forbidden"),
        @ApiResponse(code = 404, message = "Url Not found"),
        @ApiResponse(code = 422, message = "Invalid Data"),
      })
  public ResponseEntity<List<OthersBusinessResponse>> evaluateConsideredJobs(
      @AuthenticationPrincipal(expression = "subject") String keyCloakId) {

    List<OthersBusinessResponse> response = analyticsService.retrieveConsideredJobs(keyCloakId);

    return ResponseEntity.ok(response);
  }

  /**
   * Retrieves all data related to hiring by User Id
   *
   * @param keyCloakId the user keycloak id
   * @param page the page number of pagination
   * @param limit the limit for pagination
   * @param category the hr activity logic name (IN_PROGRESS)
   * @return the {@link List} of {@link HrHiringsResponse}
   */
  @GetMapping(value = "/hr/activities")
  @ResponseBody
  @ApiOperation(
      value = "Retrieves HR dashboard details",
      notes = "Fetches HR hirings based on category data")
  @ApiResponses(
      value = {
        @ApiResponse(
            code = 200,
            message = "Successfully retrieved",
            response = HrHiringsResponse.class,
            responseContainer = "List"),
        @ApiResponse(code = 400, message = "Bad Request"),
        @ApiResponse(code = 401, message = "Unauthorized"),
        @ApiResponse(code = 403, message = "Forbidden"),
        @ApiResponse(code = 404, message = "Url Not found"),
        @ApiResponse(code = 422, message = "Invalid Data"),
      })
  public ResponseEntity<List<HrHiringsResponse>> evaluateHrHirings(
      @AuthenticationPrincipal(expression = "subject") String keyCloakId,
      @RequestParam(value = "page") int page,
      @RequestParam(value = "limit") int limit,
      @RequestParam(value = "category", required = false) String category) {

    Pageable paging = PageRequest.of(page - 1, limit);

    if ("IN_PROGRESS".equals(category)) {
      return ResponseEntity.ok(analyticsService.evaluateHiringsInProgress(keyCloakId, paging));
    }

    return ResponseEntity.ok(analyticsService.evaluateHiringsInLastMonth(keyCloakId, paging));
  }
}
