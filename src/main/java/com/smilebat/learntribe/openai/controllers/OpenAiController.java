package com.smilebat.learntribe.openai.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smilebat.learntribe.assessment.AssessmentRequest;
import com.smilebat.learntribe.assessment.response.AssessmentResponse;
import com.smilebat.learntribe.kafka.KafkaSkillsRequest;
import com.smilebat.learntribe.openai.kafka.KafkaProducer;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * OpenAI Controller
 *
 * <p>Copyright &copy; 2022 Smile .Bat
 *
 * @author Pai,Sai Nandan
 */
@Slf4j
@RestController
@RequestMapping("/api/openai")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class OpenAiController {

  private final KafkaProducer producer;

  private final ObjectMapper mapper;

  private static final String SUBJECT = "subject";
  private static final String BAD_REQUEST = "subject";
  private static final String FORBIDDEN = "Forbidden";
  private static final String UN_AUTHORIZED = "Un-Authorized";
  private static final String URL_NOT_FOUND = "Url Not found";
  private static final String INVALID_DATA = "Invalid Data";

  /**
   * Creates bulk set of challenges.
   *
   * @param keyCloakId the IAM user id.
   * @param request the {@link AssessmentRequest}.
   * @return the {@link AssessmentResponse}.
   */
  @PostMapping
  @ResponseBody
  @ApiOperation(
      value = "Assessment Creation",
      notes = "Creates assessments based on HR Requirements")
  @ApiResponses(
      value = {
        @ApiResponse(code = 201, message = "Successfully created"),
        @ApiResponse(code = 400, message = BAD_REQUEST),
        @ApiResponse(code = 401, message = UN_AUTHORIZED),
        @ApiResponse(code = 403, message = FORBIDDEN),
        @ApiResponse(code = 404, message = URL_NOT_FOUND),
        @ApiResponse(code = 422, message = INVALID_DATA),
      })
  @ApiImplicitParam(
      name = "Authorization",
      value = "Access Token",
      required = true,
      allowEmptyValue = false,
      paramType = "header",
      dataTypeClass = String.class,
      example = "Bearer access_token")
  public ResponseEntity<Boolean> postEvent(
      @AuthenticationPrincipal(expression = SUBJECT) String keyCloakId,
      @RequestBody KafkaSkillsRequest request)
      throws JsonProcessingException {
    producer.sendMessage(mapper.writeValueAsString(request));
    return ResponseEntity.ok().build();
  }
}
