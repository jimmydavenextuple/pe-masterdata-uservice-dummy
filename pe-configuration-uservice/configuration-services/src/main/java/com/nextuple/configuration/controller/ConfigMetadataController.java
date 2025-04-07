/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */
package com.nextuple.configuration.controller;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.exception.PromiseEngineException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.configuration.controller.docs.AddConfigMetadataDoc;
import com.nextuple.configuration.controller.docs.DeleteConfigMetadataDoc;
import com.nextuple.configuration.controller.docs.GetConfigMetadataDoc;
import com.nextuple.configuration.controller.docs.UpdateConfigMetadataDoc;
import com.nextuple.configuration.inbound.ConfigMetadataRequest;
import com.nextuple.configuration.inbound.ConfigMetadataUpdateRequest;
import com.nextuple.configuration.outbound.ConfigMetadataResponse;
import com.nextuple.configuration.service.ConfigMetadataService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for managing configuration metadata.
 *
 * <p>This controller provides APIs to add, update, retrieve, and delete configuration metadata
 * based on configuration keys. It allows interaction with the configuration metadata, enabling
 * clients to manage configuration details related to various settings.
 *
 * <p>The controller is tagged with "Configuration Metadata APIs" for easy categorization in API
 * documentation.
 */
@Validated
@RestController
@RequestMapping("/configuration-metadata")
@RequiredArgsConstructor
@Tag(name = "Configuration Metadata APIs")
public class ConfigMetadataController {
  private static final Logger logger = LoggerFactory.getLogger(ConfigMetadataController.class);

  private final ConfigMetadataService configMetadataService;

  /**
   * Adds configuration metadata.
   *
   * <p>This method processes a POST request to add new configuration metadata based on the details
   * provided in the request body.
   *
   * @param configMetadataRequest The request body containing the details of the configuration
   *     metadata to be added.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with the added configuration
   *     metadata details.
   * @throws PromiseEngineException If an error occurs related to the promise engine.
   * @throws CommonServiceException If a general service-related error occurs while processing the
   *     request.
   */
  @AddConfigMetadataDoc
  @PostMapping(
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<BaseResponse<ConfigMetadataResponse>> addConfigMetadata(
      @Valid @RequestBody ConfigMetadataRequest configMetadataRequest)
      throws PromiseEngineException, CommonServiceException {
    logger.debug("-- Processing add configuration metadata request --");
    try {
      var configMetadataResponse =
          configMetadataService.processAddConfigMetadata(configMetadataRequest);
      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Configuration metadata added successfully")
              .payload(configMetadataResponse)
              .build());

    } catch (Exception e) {
      logger.error("Failed to process add configuration metadata request");
      throw e;
    }
  }

  /**
   * Retrieves configuration metadata by configuration key.
   *
   * <p>This method processes a GET request to fetch the configuration metadata associated with the
   * specified configuration key.
   *
   * @param configKey The unique configuration key used to identify the metadata.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with the fetched
   *     configuration metadata details.
   * @throws PromiseEngineException If an error occurs related to the promise engine.
   * @throws CommonServiceException If a general service-related error occurs while processing the
   *     request.
   */
  @GetConfigMetadataDoc
  @GetMapping(value = "/configKey/{configKey}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<BaseResponse<ConfigMetadataResponse>> getConfigMetadataByConfigKey(
      @NotBlank(message = "configKey can't be empty")
          @PathVariable
          @Parameter(description = "Configuration key of the tenant-based configuration")
          String configKey)
      throws PromiseEngineException, CommonServiceException {
    logger.debug("-- Processing get configuration metadata by configKey request --");
    try {
      var configMetadataResponse = configMetadataService.getConfigMetadataByConfigKey(configKey);
      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Config metadata fetched successfully")
              .payload(configMetadataResponse)
              .build());
    } catch (Exception e) {
      logger.error("Failed to process get configuration metadata by configKey request");
      throw e;
    }
  }

  /**
   * Updates configuration metadata.
   *
   * <p>This method processes a PUT request to update existing configuration metadata based on the
   * specified configuration key and the details provided in the request body.
   *
   * @param configKey The unique configuration key identifying the metadata to be updated.
   * @param configMetadataUpdateRequest The request body containing the updated details of the
   *     configuration metadata.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with the updated
   *     configuration metadata details.
   * @throws PromiseEngineException If an error occurs related to the promise engine.
   * @throws CommonServiceException If a general service-related error occurs while processing the
   *     request.
   */
  @UpdateConfigMetadataDoc
  @PutMapping(
      value = "/configKey/{configKey}",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<BaseResponse<ConfigMetadataResponse>> updateConfigMetadata(
      @NotBlank(message = "configKey can't be empty")
          @PathVariable
          @Parameter(description = "Configuration key of the tenant-based configuration")
          String configKey,
      @Valid @RequestBody ConfigMetadataUpdateRequest configMetadataUpdateRequest)
      throws PromiseEngineException, CommonServiceException {
    logger.debug("-- Processing update configuration metadata request--");
    try {
      var configMetadataResponse =
          configMetadataService.processUpdateConfigMetadata(configKey, configMetadataUpdateRequest);
      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Configuration metadata successfully updated")
              .payload(configMetadataResponse)
              .build());

    } catch (Exception e) {
      logger.error("Failed to process update configuration metadata request");
      throw e;
    }
  }

  /**
   * Deletes configuration metadata.
   *
   * <p>This method processes a DELETE request to remove configuration metadata associated with the
   * specified configuration key.
   *
   * @param configKey The unique configuration key identifying the metadata to be deleted.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with the details of the
   *     deleted configuration metadata.
   * @throws PromiseEngineException If an error occurs related to the promise engine.
   * @throws CommonServiceException If a general service-related error occurs while processing the
   *     request.
   */
  @DeleteConfigMetadataDoc
  @DeleteMapping(value = "/configKey/{configKey}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<BaseResponse<ConfigMetadataResponse>> deleteConfigMetadata(
      @NotBlank(message = "configKey can't be empty")
          @PathVariable
          @Parameter(description = "Configuration key of the tenant-based configuration")
          String configKey)
      throws PromiseEngineException, CommonServiceException {
    logger.debug("-- Processing delete configuration metadata request--");
    try {
      var configMetadataResponse = configMetadataService.processDeleteConfigMetadata(configKey);
      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Metadata deleted successfully")
              .payload(configMetadataResponse)
              .build());

    } catch (Exception e) {
      logger.error("Failed to process delete configuration metadata request");
      throw e;
    }
  }
}
