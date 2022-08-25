package com.hbc.common.configuration.controller;

import com.hbc.common.configuration.api.domain.dto.CommonConfigurationDto;
import com.hbc.common.configuration.api.domain.inbound.CreateCommonConfigurationRequest;
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

    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Common Configuration successfully fetched!")
            .payload(commonConfigurationService.fetchValue(orgId, type, key))
            .build());
  }

  @PostMapping
  public ResponseEntity<BaseResponse<CommonConfigurationDto>> createCommonConfiguration(
      @Valid @RequestBody CreateCommonConfigurationRequest baseRequest)
      throws PromiseEngineException {
    logger.debug("Processing create Common Configuration request");

    return ResponseEntity.status(HttpStatus.CREATED)
        .body(
            BaseResponse.builder()
                .message("Common Configuration successfully created!")
                .payload(commonConfigurationService.createCommonConfig(baseRequest))
                .build());
  }

  @PutMapping
  public ResponseEntity<BaseResponse<CommonConfigurationDto>> updateCommonConfiguration(
      @Valid @RequestBody CreateCommonConfigurationRequest baseRequest)
      throws PromiseEngineException {
    logger.debug("Processing update Common Configuration request");

    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Common Configuration successfully updated!")
            .payload(commonConfigurationService.updateCommonConfiguration(baseRequest))
            .build());
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

    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Common Configuration successfully deleted!")
            .payload(commonConfigurationService.deleteCommonConfiguration(orgId, type, key))
            .build());
  }
}
