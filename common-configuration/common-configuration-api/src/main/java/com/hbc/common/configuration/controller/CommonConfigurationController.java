package com.hbc.common.configuration.controller;

import com.hbc.common.configuration.api.domain.dto.CommonConfigurationDto;
import com.hbc.common.configuration.api.domain.inbound.CreateCommonConfigurationRequest;
import com.hbc.common.configuration.api.domain.inbound.UpdateCommonConfigurationRequest;
import com.hbc.common.configuration.service.CommonConfigurationService;
import com.hbc.common.exception.CommonServiceException;
import com.hbc.common.exception.PromiseEngineException;
import com.hbc.common.response.BaseResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
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
@RequestMapping("/common-config")
@RequiredArgsConstructor
public class CommonConfigurationController {
  private static final Logger logger = LoggerFactory.getLogger(CommonConfigurationController.class);
  private final CommonConfigurationService commonConfigurationService;

  @GetMapping("/orgId/{orgId}")
  public ResponseEntity<BaseResponse<CommonConfigurationDto>> fetchValue(
      @PathVariable String orgId, @RequestParam String type, @RequestParam String key)
      throws PromiseEngineException {
    logger.debug("Processing Common Configuration fetch request");
    try {
      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Common Configuration successfully fetched!")
              .payload(commonConfigurationService.fetchValue(orgId, type, key))
              .build());
    } catch (Exception e) {
      logger.error("Failed to process fetch Common Configuration request!");
      throw e;
    }
  }

  @PostMapping
  public ResponseEntity<BaseResponse<CommonConfigurationDto>> createCommonConfiguration(
      @Valid @RequestBody CreateCommonConfigurationRequest baseRequest)
      throws PromiseEngineException, CommonServiceException {
    logger.debug("Processing create Common Configuration request");
    try {
      var globalConfigurationDto = commonConfigurationService.createCommonConfig(baseRequest);

      return ResponseEntity.status(HttpStatus.CREATED)
          .body(
              BaseResponse.builder()
                  .message("Common Configuration successfully created!")
                  .payload(globalConfigurationDto)
                  .build());
    } catch (Exception e) {
      logger.error("Failed to process create Common Configuration request!");
      throw e;
    }
  }

  @PutMapping("/orgId/{orgId}")
  public ResponseEntity<BaseResponse<CommonConfigurationDto>> updateCommonConfiguration(
      @PathVariable String orgId,
      @RequestParam("type") String type,
      @RequestParam("key") String key,
      @Valid @RequestBody UpdateCommonConfigurationRequest baseRequest)
      throws PromiseEngineException, CommonServiceException {
    logger.debug("Processing update Common Configuration request");
    try {
      var commonConfigurationDto =
          commonConfigurationService.updateCommonConfiguration(orgId, type, key, baseRequest);
      logger.info("Response after updation of common configuration :{}", commonConfigurationDto);

      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Common Configuration successfully updated!")
              .payload(commonConfigurationDto)
              .build());
    } catch (Exception e) {
      logger.error("Failed to process update Common Configuration request!");
      throw e;
    }
  }

  @DeleteMapping("/orgId/{orgId}")
  public ResponseEntity<BaseResponse<CommonConfigurationDto>> deleteCommonConfiguration(
      @NotBlank @PathVariable String orgId,
      @NotBlank @RequestParam String type,
      @NotBlank @RequestParam String key)
      throws PromiseEngineException, CommonServiceException {
    logger.debug(
        "Processing delete Common Configuration request by orgId: {},type: {},key: {}",
        orgId,
        type,
        key);
    try {
      var commonConfigurationDto =
          commonConfigurationService.deleteCommonConfiguration(orgId, type, key);
      logger.info("Response after deletion of common configuration :{}", commonConfigurationDto);
      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Common Configuration successfully deleted!")
              .payload(commonConfigurationDto)
              .build());
    } catch (Exception e) {
      logger.error("Failed to process delete Common Configuration request!");
      throw e;
    }
  }
}
