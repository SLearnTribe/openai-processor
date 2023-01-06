package com.smilebat.learntribe.learntribeinquisitve.controllers;

import com.smilebat.learntribe.inquisitve.UserProfileRequest;
import com.smilebat.learntribe.inquisitve.response.CoreUserProfileResponse;
import com.smilebat.learntribe.inquisitve.response.UserProfileResponse;
import com.smilebat.learntribe.learntribeinquisitve.services.UserProfileService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.util.Collections;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * User Controller
 *
 * <p>Copyright &copy; 2022 Smile .Bat
 *
 * @author Sanjay
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/profile")
// @CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class UserProfileController {

  private final UserProfileService userProfileService;

  /**
   * Saves all user details.
   *
   * @param request the {@link UserProfileRequest}
   * @param id the user id.
   * @return the {@link UserProfileRequest} as response.
   */
  @PostMapping(value = "/user")
  @ResponseBody
  @ApiOperation(value = "Save or Update User Details", notes = "Saves the user details")
  @ApiResponses(
      value = {
        @ApiResponse(code = 200, message = ""),
        @ApiResponse(code = 400, message = "Bad Request"),
        @ApiResponse(code = 401, message = "Unauthorized"),
        @ApiResponse(code = 403, message = "Forbidden"),
        @ApiResponse(code = 404, message = "Url Not found"),
      })
  public ResponseEntity<String> saveUserDetails(
      @AuthenticationPrincipal(expression = "subject") String id,
      @Valid @RequestBody UserProfileRequest request) {

    request.setKeyCloakId(id);
    userProfileService.saveUserProfile(request);

    return ResponseEntity.status(HttpStatus.OK).build();
  }

  /**
   * Retrieves the user from IAM based on user id.
   *
   * @param id the user id
   * @return the Respresentation of User from IAM.
   */
  @GetMapping(value = "/user")
  @ResponseBody
  @ApiOperation(
      value = "Fetches user based on auth token",
      notes = "Retrieves current user details")
  @ApiResponses(
      value = {
        @ApiResponse(
            code = 200,
            message = "Successfully retrieved",
            response = UserProfileResponse.class),
        @ApiResponse(code = 400, message = "Bad Request"),
        @ApiResponse(code = 401, message = "Unauthorized"),
        @ApiResponse(code = 403, message = "Forbidden"),
        @ApiResponse(code = 404, message = "Url Not found"),
      })
  public ResponseEntity<?> getUserDetails(
      @AuthenticationPrincipal(expression = "subject") String id) {
    log.info("Fetching User Details");

    final UserProfileResponse userInfo = userProfileService.getUserInfo(id);
    return ResponseEntity.ok(userInfo);
  }

  /**
   * Retrieves all User information based on the input skillName.
   *
   * @param skillName the {@link String}
   * @param page page number for pageination.
   * @param limit for pageination.
   * @return the List of {@link UserProfileResponse}
   */
  @GetMapping("/skill")
  @ResponseBody
  @Deprecated
  public ResponseEntity<List<? extends UserProfileResponse>> getUserDetailsFromSkill(
      @RequestParam String skillName, @RequestParam int page, @RequestParam int limit) {
    if (skillName == null) {
      return ResponseEntity.ok(Collections.emptyList());
    }
    List<? extends UserProfileResponse> userProfileResponses =
        userProfileService.getUserInfoBySkill(skillName.toLowerCase(), page, limit);
    return ResponseEntity.ok(userProfileResponses);
  }

  /**
   * Retrieves the all the user information.
   *
   * @param page page number for pageination.
   * @param limit for pageination.
   * @param keyword to search for users
   * @return the {@link ResponseEntity} of generic type.
   */
  @GetMapping
  @ResponseBody
  @ApiOperation(
      value = "Fetches all applicants for HR",
      notes = "Retrieves all users based on filters")
  @ApiResponses(
      value = {
        @ApiResponse(
            code = 200,
            message = "Successfully retrieved",
            response = UserProfileResponse.class,
            responseContainer = "List"),
        @ApiResponse(code = 400, message = "Bad Request"),
        @ApiResponse(code = 401, message = "Unauthorized"),
        @ApiResponse(code = 403, message = "Forbidden"),
        @ApiResponse(code = 404, message = "Url Not found"),
      })
  public ResponseEntity<List<CoreUserProfileResponse>> getAllUserDetails(
      @RequestParam int page,
      @RequestParam int limit,
      @RequestParam(defaultValue = "", required = false) String keyword) {
    log.info("Fetching User Details");

    final List<CoreUserProfileResponse> users =
        userProfileService.getAllUserInfo(page, limit, keyword);

    return ResponseEntity.ok(users);
  }
}
