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
import com.nextuple.configuration.inbound.TenantConfigdataRequest;
import com.nextuple.configuration.inbound.TenantConfigdataUpdateRequest;
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

@Validated
@RestController
@RequestMapping("/tenant-configuration")
@RequiredArgsConstructor
@Tag(name = "Tenant Configuration APIs")
public class TenantConfigdataController {
  private static final Logger logger = LoggerFactory.getLogger(TenantConfigdataController.class);

  private final TenantConfigdataService tenantConfigdataService;

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
      @Valid @RequestBody TenantConfigdataUpdateRequest tenantConfigdataUpdateRequest)
      throws PromiseEngineException, CommonServiceException {
    logger.debug("-- Processing update tenant configuration data request --");
    try {
      var tenantCofigdataResponse =
          tenantConfigdataService.processUpdateTenantConfigdata(
              orgId, configKey, tenantConfigdataUpdateRequest);
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
