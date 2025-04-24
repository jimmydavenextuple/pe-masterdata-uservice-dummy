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
import com.nextuple.configuration.controller.docs.AddTenantConfigdataDoc;
import com.nextuple.configuration.controller.docs.DeleteTenantConfigdataDoc;
import com.nextuple.configuration.controller.docs.GetTenantConfigdataDoc;
import com.nextuple.configuration.controller.docs.UpdateTenantConfigdataDoc;
import com.nextuple.configuration.controller.docs.UpsertTenantConfigdataDoc;
import com.nextuple.configuration.inbound.TenantConfigdataBaseRequest;
import com.nextuple.configuration.inbound.TenantConfigdataRequest;
import com.nextuple.configuration.outbound.TenantConfigdataResponse;
import com.nextuple.configuration.service.TenantConfigdataService;
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
 * Controller for managing tenant configuration data.
 *
 * <p>This controller provides APIs for adding, updating, retrieving, and deleting tenant-specific
 * configuration data based on unique configuration keys and organization IDs. These operations
 * allow clients to manage configuration settings for various tenants.
 *
 * <p>The controller is tagged with "Tenant Configuration APIs" for easy categorization in the API
 * documentation.
 */
@Validated
@RestController
@RequestMapping("/tenant-configuration")
@RequiredArgsConstructor
@Tag(name = "Tenant Configuration APIs")
public class TenantConfigdataController {
  private static final Logger logger = LoggerFactory.getLogger(TenantConfigdataController.class);

  private final TenantConfigdataService tenantConfigdataService;

  @UpsertTenantConfigdataDoc
  @PostMapping(
      value = "/upsert",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<BaseResponse<TenantConfigdataResponse>> upsertTenantConfigdata(
      @Valid @RequestBody TenantConfigdataRequest tenantConfigdataRequest)
      throws PromiseEngineException, CommonServiceException {
    logger.debug("-- Processing upsert tenant configuration data request --");
    try {
      var tenantConfigdataResponse =
          tenantConfigdataService.upsertTenantConfigdata(tenantConfigdataRequest);
      return ResponseEntity.ok(
          BaseResponse.<TenantConfigdataResponse>builder()
              .message("Tenant configuration data saved successfully")
              .payload(tenantConfigdataResponse)
              .build());
    } catch (Exception e) {
      logger.error("Failed to process upsert tenant configuration data request", e);
      throw e;
    }
  }

  /**
   * Adds tenant configuration data.
   *
   * <p>This method processes a POST request to add new tenant-specific configuration data based on
   * the details provided in the request body.
   *
   * @param tenantConfigdataRequest The request body containing the details of the tenant
   *     configuration data to be added.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with the added tenant
   *     configuration data details.
   * @throws PromiseEngineException If an error occurs related to the promise engine.
   * @throws CommonServiceException If a general service-related error occurs while processing the
   *     request.
   */
  @AddTenantConfigdataDoc
  @PostMapping(
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<BaseResponse<TenantConfigdataResponse>> addTenantConfigdata(
      @Valid @RequestBody TenantConfigdataRequest tenantConfigdataRequest)
      throws PromiseEngineException, CommonServiceException {
    logger.debug("-- Processing add tenant configuration data request --");
    try {
      var tenantConfigdataResponse =
          tenantConfigdataService.processAddTenantConfigdata(tenantConfigdataRequest);
      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Configuration data added successfully")
              .payload(tenantConfigdataResponse)
              .build());
    } catch (Exception e) {
      logger.error("Failed to process add tenant configuration data request");
      throw e;
    }
  }

  /**
   * Retrieves tenant configuration data by organization ID and configuration key.
   *
   * <p>This method processes a GET request to fetch the configuration data associated with the
   * specified organization ID and configuration key.
   *
   * @param orgId The unique identifier of the organization.
   * @param configKey The unique configuration key of the tenant-based configuration.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with the fetched tenant
   *     configuration data.
   * @throws PromiseEngineException If an error occurs related to the promise engine.
   * @throws CommonServiceException If a general service-related error occurs while processing the
   *     request.
   */
  @GetTenantConfigdataDoc
  @GetMapping(
      value = "/orgId/{orgId}/configKey/{configKey}",
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<BaseResponse<TenantConfigdataResponse>>
      getTenantConfigdataByOrgIdAndConfigKey(
          @NotBlank(message = "orgId can't be empty")
              @PathVariable
              @Parameter(description = "Unique identifier for organisation", example = "NEXTUPLE")
              String orgId,
          @NotBlank(message = "configKey can't be empty")
              @PathVariable
              @Parameter(description = "Configuration key of the tenant-based configuration")
              String configKey)
          throws PromiseEngineException, CommonServiceException {
    logger.debug("-- Processing get tenant configuration data by orgId and configKey request --");
    try {
      var tenantConfigdataResponse =
          tenantConfigdataService.processGetTenantConfigdataByOrgIdAndConfigKey(orgId, configKey);
      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Config data fetched successfully")
              .payload(tenantConfigdataResponse)
              .build());

    } catch (Exception e) {
      logger.error(
          "Failed to process get tenant configuration data by orgId and configKey request");
      throw e;
    }
  }

  /**
   * Updates tenant configuration data.
   *
   * <p>This method processes a PUT request to update existing tenant configuration data based on
   * the specified organization ID, configuration key, and the details provided in the request body.
   *
   * @param orgId The unique identifier of the organization.
   * @param configKey The unique configuration key of the tenant-based configuration to be updated.
   * @param tenantConfigdataBaseRequest The request body containing the updated details of the
   *     tenant configuration data.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with the updated tenant
   *     configuration data.
   * @throws PromiseEngineException If an error occurs related to the promise engine.
   * @throws CommonServiceException If a general service-related error occurs while processing the
   *     request.
   */
  @UpdateTenantConfigdataDoc
  @PutMapping(
      value = "/orgId/{orgId}/configKey/{configKey}",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<BaseResponse<TenantConfigdataResponse>> updateTenantConfigdata(
      @NotBlank(message = "orgId can't be empty")
          @PathVariable
          @Parameter(description = "Unique identifier for organisation", example = "NEXTUPLE")
          String orgId,
      @NotBlank(message = "configKey can't be empty")
          @PathVariable
          @Parameter(description = "Configuration key of the tenant-based configuration")
          String configKey,
      @Valid @RequestBody TenantConfigdataBaseRequest tenantConfigdataBaseRequest)
      throws PromiseEngineException, CommonServiceException {
    logger.debug("-- Processing update tenant configuration data request --");
    try {
      var tenantCofigdataResponse =
          tenantConfigdataService.processUpdateTenantConfigdata(
              orgId, configKey, tenantConfigdataBaseRequest);
      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Tenant config data successfully updated")
              .payload(tenantCofigdataResponse)
              .build());
    } catch (Exception e) {
      logger.error("Failed to process update tenant configuration data request");
      throw e;
    }
  }

  /**
   * Deletes tenant configuration data.
   *
   * <p>This method processes a DELETE request to remove tenant-specific configuration data based on
   * the specified organization ID and configuration key.
   *
   * @param orgId The unique identifier of the organization.
   * @param configKey The unique configuration key of the tenant-based configuration to be deleted.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with the details of the
   *     deleted tenant configuration data.
   * @throws PromiseEngineException If an error occurs related to the promise engine.
   * @throws CommonServiceException If a general service-related error occurs while processing the
   *     request.
   */
  @DeleteTenantConfigdataDoc
  @DeleteMapping(
      value = "/orgId/{orgId}/configKey/{configKey}",
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<BaseResponse<TenantConfigdataResponse>> deleteTenantConfigdata(
      @NotBlank(message = "orgId can't be empty")
          @PathVariable
          @Parameter(description = "Unique identifier for organisation", example = "NEXTUPLE")
          String orgId,
      @NotBlank(message = "configKey can't be empty")
          @PathVariable
          @Parameter(description = "Configuration key of the tenant-based configuration")
          String configKey)
      throws PromiseEngineException, CommonServiceException {
    logger.debug("-- Processing delete tenant configuration data request --");
    try {
      var tenantCofigdataResponse =
          tenantConfigdataService.processDeleteTenantConfigdata(orgId, configKey);
      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Config data deleted successfully")
              .payload(tenantCofigdataResponse)
              .build());
    } catch (Exception e) {
      logger.error("Failed to process delete tenant configuration data request");
      throw e;
    }
  }
}
