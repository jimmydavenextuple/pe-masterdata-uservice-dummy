package com.nextuple.postal.code.timezone.controller;


import com.nextuple.common.response.BaseResponse;
import com.nextuple.postal.code.timezone.api.domain.dto.PostalCodeTimezoneDto;
import com.nextuple.postal.code.timezone.api.domain.inbound.CreatePostalCodeTimezoneRequest;
import com.nextuple.postal.code.timezone.api.domain.inbound.UpdatePostalCodeTimezoneRequest;
import com.nextuple.postal.code.timezone.exception.common.PromiseEngineException;
import com.nextuple.postal.code.timezone.service.PostalCodeTimezoneService;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
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
    logger.info("Processing create Postal Code Timezone request");
    try {
      return ResponseEntity.ok(
              BaseResponse.builder()
                      .message("Postal Code Timezone successfully created!")
                      .payload(postalCodeTimezoneService.createPostalCodeTimezone(baseRequest))
                      .build());
    } catch (Exception e) {
      logger.error(String.valueOf(e), "Failed to process create Postal Code Timezone request!");
      throw e;
    }
  }

  @GetMapping
  public ResponseEntity<BaseResponse<PostalCodeTimezoneDto>> getPostalCodeTimezone(
          @NotBlank @RequestParam String orgId, @NotBlank @RequestParam String postalCodePrefix)
          throws PromiseEngineException {
    logger.info("Processing get Postal Code Timezone request");
    try {
      return ResponseEntity.ok(
              BaseResponse.builder()
                      .message("Postal Code Timezone successfully fetched!")
                      .payload(postalCodeTimezoneService.getPostalCodeTimezone(orgId, postalCodePrefix))
                      .build());
    } catch (Exception e) {
      logger.error(String.valueOf(e), "Failed to process get Postal Code Timezone request!");
      throw e;
    }
  }

  @PutMapping
  public ResponseEntity<BaseResponse<PostalCodeTimezoneDto>> updatePostalCodeTimezone(
          @NotBlank @RequestParam String orgId,
          @NotBlank @RequestParam String postalCodePrefix,
          @Valid @RequestBody UpdatePostalCodeTimezoneRequest baseRequest)
          throws PromiseEngineException {
    logger.info("Processing update Postal Code Timezone request");
    try {
      return ResponseEntity.ok(
              BaseResponse.builder()
                      .message("Postal Code Timezone successfully updated!")
                      .payload(
                              postalCodeTimezoneService.updatePostalCodeTimezone(
                                      orgId, postalCodePrefix, baseRequest))
                      .build());
    } catch (Exception e) {
      logger.error(String.valueOf(e), "Failed to process update Postal Code Timezone request!");
      throw e;
    }
  }

  @Transactional
  @DeleteMapping
  public ResponseEntity<BaseResponse<PostalCodeTimezoneDto>> deletePostalCodeTimezone(
          @NotBlank @RequestParam String orgId, @NotBlank @RequestParam String postalCodePrefix)
          throws PromiseEngineException {
    logger.info("Processing delete Postal Code Timezone request");
    try {
      return ResponseEntity.ok(
              BaseResponse.builder()
                      .message("Postal Code Timezone successfully deleted!")
                      .payload(postalCodeTimezoneService.deletePostalCodeTimezone(orgId, postalCodePrefix))
                      .build());
    } catch (Exception e) {
      logger.error(String.valueOf(e), "Failed to process delete Postal Code Timezone request!");
      throw e;
    }
  }
}
