/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */
package com.nextuple.sourcing.cost.config.controller;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.sourcing.cost.config.controller.docs.CreateTenantCostTypeDoc;
import com.nextuple.sourcing.cost.config.controller.docs.DeleteTenantCostTypeDoc;
import com.nextuple.sourcing.cost.config.controller.docs.GetTenantCostTypeByOrgIdDoc;
import com.nextuple.sourcing.cost.config.controller.docs.GetTenantCostTypeCacheKeysDoc;
import com.nextuple.sourcing.cost.config.controller.docs.GetTenantCostTypeDoc;
import com.nextuple.sourcing.cost.config.controller.docs.UpdateTenantCostTypeDoc;
import com.nextuple.sourcing.cost.config.dto.TenantCostTypeCacheKeyDto;
import com.nextuple.sourcing.cost.config.inbound.TenantCostTypeRequest;
import com.nextuple.sourcing.cost.config.inbound.TenantCostTypeUpdateRequest;
import com.nextuple.sourcing.cost.config.outbound.TenantCostTypeResponse;
import com.nextuple.sourcing.cost.config.service.TenantCostTypeService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
@RequestMapping("/cost-config/type")
@RequiredArgsConstructor
@Tag(name = "Tenant Cost Type APIs")
public class TenantCostTypeController {

  private static final Logger logger = LoggerFactory.getLogger(TenantCostTypeController.class);

  private final TenantCostTypeService tenantCostTypeService;

  @CreateTenantCostTypeDoc
  @PostMapping(
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE,
      value = "/{orgId}")
  public ResponseEntity<BaseResponse<TenantCostTypeResponse>> createTenantCostType(
      @NotNull(message = "OrgId cannot be null")
          @PathVariable
          @Parameter(
              description = "Unique identifier of the organization.",
              example = "NEXTUPLE_GR")
          String orgId,
      @Valid @RequestBody TenantCostTypeRequest tenantCostTypeRequest)
      throws CommonServiceException {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(
            BaseResponse.builder()
                .message("Tenant cost type created successfully.")
                .payload(tenantCostTypeService.createTenantCostType(orgId, tenantCostTypeRequest))
                .build());
  }

  @GetTenantCostTypeDoc
  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE, value = "/{orgId}/{id}")
  public ResponseEntity<BaseResponse<TenantCostTypeResponse>> getTenantCostType(
      @NotNull(message = "OrgId cannot be null")
          @PathVariable
          @Parameter(
              description = "Unique identifier of the organization.",
              example = "NEXTUPLE_GR")
          String orgId,
      @NotNull(message = "Id cannot be null")
          @PathVariable
          @Parameter(description = "Unique identifier for tenant cost type.", example = "1")
          Long id)
      throws CommonServiceException {
    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Tenant cost type fetched successfully.")
            .payload(tenantCostTypeService.getTenantCostType(orgId, id))
            .build());
  }

  @GetTenantCostTypeByOrgIdDoc
  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE, value = "/{orgId}")
  public ResponseEntity<BaseResponse<List<TenantCostTypeResponse>>> getTenantCostTypeByOrgId(
      @NotNull(message = "OrgId cannot be null")
          @PathVariable
          @Parameter(
              description = "Unique identifier of the organization.",
              example = "NEXTUPLE_GR")
          String orgId)
      throws CommonServiceException {
    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Tenant cost types fetched successfully.")
            .payload(tenantCostTypeService.getTenantCostTypeByOrgId(orgId))
            .build());
  }

  @UpdateTenantCostTypeDoc
  @PutMapping(
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE,
      value = "/{orgId}/{id}")
  public ResponseEntity<BaseResponse<TenantCostTypeResponse>> updateTenantCostType(
      @NotNull(message = "OrgId cannot be null")
          @PathVariable
          @Parameter(
              description = "Unique identifier of the organization.",
              example = "NEXTUPLE_GR")
          String orgId,
      @NotNull(message = "Id cannot be null")
          @PathVariable
          @Parameter(description = "Unique identifier for tenant cost type.", example = "1")
          Long id,
      @Valid @RequestBody TenantCostTypeUpdateRequest tenantCostTypeUpdateRequest)
      throws CommonServiceException {
    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Tenant cost type updated successfully.")
            .payload(
                tenantCostTypeService.updateTenantCostType(id, orgId, tenantCostTypeUpdateRequest))
            .build());
  }

  @DeleteTenantCostTypeDoc
  @DeleteMapping(value = "/{orgId}/{id}")
  public ResponseEntity<BaseResponse<TenantCostTypeResponse>> deleteTenantCostType(
      @NotNull(message = "OrgId cannot be null")
          @PathVariable
          @Parameter(
              description = "Unique identifier of the organization.",
              example = "NEXTUPLE_GR")
          String orgId,
      @NotNull(message = "Id cannot be null")
          @PathVariable
          @Parameter(description = "Unique identifier for tenant cost type.", example = "1")
          long id)
      throws CommonServiceException {
    var tenantCostTypeResponse = tenantCostTypeService.deleteTenantCostType(id, orgId);
    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Tenant Cost Type Details deleted successfully!")
            .payload(tenantCostTypeResponse)
            .build());
  }

  @GetTenantCostTypeCacheKeysDoc
  @GetMapping("/get-all-cache-keys")
  public ResponseEntity<BaseResponse<List<TenantCostTypeCacheKeyDto>>> getTenantCostTypeCacheKeys(
      @RequestParam(defaultValue = "100")
          @Parameter(
              description = "Specifies the number of rows to be returned from the DB.",
              example = "1")
          Integer limit) {
    logger.debug("Processing tenant cost type Cache Keys");
    var response = tenantCostTypeService.getAllTenantCostTypeCacheKeys(limit);
    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Tenant cost type Keys fetched successfully")
            .payload(response)
            .build());
  }
}
