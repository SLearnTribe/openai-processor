package com.smilebat.learntribe.learntribeinquisitve.controllers;

import com.smilebat.learntribe.inquisitve.JobRequest;
import com.smilebat.learntribe.inquisitve.JobUpdateRequest;
import com.smilebat.learntribe.inquisitve.response.OthersBusinessResponse;
import com.smilebat.learntribe.learntribeinquisitve.services.JobService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Assessment Controller
 *
 * <p>Copyright &copy; 2022 Smile .Bat
 *
 * @author Pai,Sai Nandan
 * @author Likith
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/jobs")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class JobController {

  private final JobService jobService;

  /**
   * Fetchs all the Jobs related to the User id.
   *
   * @param keyCloakId the acual IAM user Id
   * @param page page number for pageination.
   * @param limit for pageination.
   * @return the List of {@link OthersBusinessResponse}
   */
  @GetMapping(value = "/user")
  @ResponseBody
  @ApiOperation(
      value = "Fetches all the jobs for user",
      notes = "Retrieves all the jobs based on user id")
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
      })
  public ResponseEntity<List<OthersBusinessResponse>> retrieveJob(
      @AuthenticationPrincipal(expression = "subject") String keyCloakId,
      @RequestParam(value = "page") int page,
      @RequestParam(value = "limit") int limit) {

    Pageable paging = PageRequest.of(page - 1, limit);
    JobService.PageableJobRequest pageRequest =
        JobService.PageableJobRequest.builder().paging(paging).keyCloakId(keyCloakId).build();
    List<OthersBusinessResponse> responses = jobService.retrieveJobs(pageRequest);
    return ResponseEntity.ok(responses);
  }

  /**
   * Creates a new Job Post.
   *
   * @param keyCloakId the user IAM id
   * @param request the {@link JobRequest}
   * @return the {@link OthersBusinessResponse}
   */
  @PostMapping(value = "/user")
  @ResponseBody
  @ApiOperation(
      value = "Creates a new job for HR",
      notes = "Creates a new job based on HR requirements")
  @ApiResponses(
      value = {
        @ApiResponse(
            code = 200,
            message = "Successfully retrieved",
            response = OthersBusinessResponse.class),
        @ApiResponse(code = 400, message = "Bad Request"),
        @ApiResponse(code = 401, message = "Unauthorized"),
        @ApiResponse(code = 403, message = "Forbidden"),
        @ApiResponse(code = 404, message = "Url Not found"),
      })
  public ResponseEntity<OthersBusinessResponse> createJob(
      @AuthenticationPrincipal(expression = "subject") String keyCloakId,
      @Valid @RequestBody JobRequest request) {

    request.setCreatedBy(keyCloakId);
    jobService.createJob(request);

    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  /**
   * Creates a new Job Post.
   *
   * @param keyCloakId the user IAM id
   * @param request the {@link JobRequest}
   * @return the {@link OthersBusinessResponse}
   */
  @PutMapping(value = "/user")
  @ResponseBody
  @ApiOperation(
      value = "Update exisiting job for HR",
      notes = "Updates job based on HR requirements")
  @ApiResponses(
      value = {
        @ApiResponse(
            code = 200,
            message = "Successfully retrieved",
            response = OthersBusinessResponse.class),
        @ApiResponse(code = 400, message = "Bad Request"),
        @ApiResponse(code = 401, message = "Unauthorized"),
        @ApiResponse(code = 403, message = "Forbidden"),
        @ApiResponse(code = 404, message = "Url Not found"),
      })
  public ResponseEntity<OthersBusinessResponse> updateJob(
      @AuthenticationPrincipal(expression = "subject") String keyCloakId,
      @Valid @RequestBody JobUpdateRequest request) {

    request.setCreatedBy(keyCloakId);
    final OthersBusinessResponse response = jobService.updateJob(request);

    return ResponseEntity.ok(response);
  }
}
