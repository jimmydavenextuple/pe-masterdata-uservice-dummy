package com.hbc.postal.code.timezone.controller;

import com.hbc.common.exception.PromiseEngineException;
import com.hbc.common.response.BaseResponse;
import com.hbc.postal.code.timezone.api.domain.dto.PostalCodePrefixDto;
import com.hbc.postal.code.timezone.api.domain.dto.PostalCodeTimezoneDto;
import com.hbc.postal.code.timezone.api.domain.inbound.CreatePostalCodeTimezoneRequest;
import com.hbc.postal.code.timezone.api.domain.inbound.UpdatePostalCodeTimezoneRequest;
import com.hbc.postal.code.timezone.service.PostalCodeTimezoneService;
import java.util.List;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/postalCodeTimezone")
@RequiredArgsConstructor
public class PostalCodeTimezoneController {

  private static final Logger logger = LoggerFactory.getLogger(PostalCodeTimezoneController.class);
  private final PostalCodeTimezoneService postalCodeTimezoneService;

  @PostMapping
  public ResponseEntity<BaseResponse<PostalCodeTimezoneDto>> createPostalCodeTimezone(
      @Valid @RequestBody CreatePostalCodeTimezoneRequest baseRequest)
      throws PromiseEngineException {
    logger.debug("Processing create Postal Code Timezone request");
    try {
      var postalCodeTimezoneDto = postalCodeTimezoneService.createPostalCodeTimezone(baseRequest);
      logger.info("Response after creation of postal-code timezone :{}", postalCodeTimezoneDto);
      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Postal Code Timezone successfully created!")
              .payload(postalCodeTimezoneDto)
              .build());
    } catch (Exception e) {
      logger.error("Failed to process create Postal Code Timezone request!");
      throw e;
    }
  }

  @GetMapping
  public ResponseEntity<BaseResponse<PostalCodeTimezoneDto>> getPostalCodeTimezone(
      @NotBlank @RequestParam String orgId, @NotBlank @RequestParam String postalCodePrefix)
      throws PromiseEngineException {
    logger.debug("Processing get Postal Code Timezone request");
    try {
      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Postal Code Timezone successfully fetched!")
              .payload(postalCodeTimezoneService.getPostalCodeTimezone(orgId, postalCodePrefix))
              .build());
    } catch (Exception e) {
      logger.error("Failed to process get Postal Code Timezone request!");
      throw e;
    }
  }

  @PutMapping
  public ResponseEntity<BaseResponse<PostalCodeTimezoneDto>> updatePostalCodeTimezone(
      @NotBlank @RequestParam String orgId,
      @NotBlank @RequestParam String postalCodePrefix,
      @Valid @RequestBody UpdatePostalCodeTimezoneRequest baseRequest)
      throws PromiseEngineException {
    logger.debug("Processing update Postal Code Timezone request");
    try {
      var postalCodeTimezoneDto =
          postalCodeTimezoneService.updatePostalCodeTimezone(orgId, postalCodePrefix, baseRequest);
      logger.info("Response after updation of postal-code timezone :{}", postalCodeTimezoneDto);
      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Postal Code Timezone successfully updated!")
              .payload(postalCodeTimezoneDto)
              .build());
    } catch (Exception e) {
      logger.error("Failed to process update Postal Code Timezone request!");
      throw e;
    }
  }

  @Transactional
  @DeleteMapping
  public ResponseEntity<BaseResponse<PostalCodeTimezoneDto>> deletePostalCodeTimezone(
      @NotBlank @RequestParam String orgId, @NotBlank @RequestParam String postalCodePrefix)
      throws PromiseEngineException {
    logger.debug("Processing delete Postal Code Timezone request");
    try {
      var postalCodeTimezoneDto =
          postalCodeTimezoneService.deletePostalCodeTimezone(orgId, postalCodePrefix);
      logger.info("Response after deletion of postal-code timezone :{}", postalCodeTimezoneDto);
      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Postal Code Timezone successfully deleted!")
              .payload(postalCodeTimezoneDto)
              .build());
    } catch (Exception e) {
      logger.error("Failed to process delete Postal Code Timezone request!");
      throw e;
    }
  }

  @GetMapping("/ui/state-postal-code-prefix/orgId/{orgId}")
  public ResponseEntity<BaseResponse<List<PostalCodePrefixDto>>> getPostalCodePrefixList(
      @PathVariable String orgId) throws PromiseEngineException {
    logger.debug("Processing get state and postal code prefixes list");

    List<PostalCodePrefixDto> responseList =
        postalCodeTimezoneService.fetchPostalCodePrefixList(orgId);

    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("State and Postal Code Prefix list fetched successfully")
            .payload(responseList)
            .build());
  }

  @GetMapping("/org/{orgId}")
  public ResponseEntity<BaseResponse<List<String>>> getPostalCodePrefixForOrgIdAndState(
      @PathVariable String orgId, @RequestParam String state) throws PromiseEngineException {
    logger.debug("Processing get postal code prefix list for orgId and state");
    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("")
            .payload(postalCodeTimezoneService.fetchPostalCodePrefixForOrgIdAndState(orgId, state))
            .build());
  }
}
