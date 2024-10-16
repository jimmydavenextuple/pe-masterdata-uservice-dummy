/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.sourcing.cost.config.controller;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.sourcing.cost.config.controller.docs.CreateOptimizationAndCostTypesMappingDoc;
import com.nextuple.sourcing.cost.config.controller.docs.DeleteOptimizationAndCostTypesMappingByOrgIdAndIdDoc;
import com.nextuple.sourcing.cost.config.controller.docs.FetchOptimizationAndCostTypesMappingByOrgIdAndIdDoc;
import com.nextuple.sourcing.cost.config.controller.docs.FetchOptimizationAndCostTypesMappingByOrgIdAndOptStrategyDoc;
import com.nextuple.sourcing.cost.config.controller.docs.UpdateOptimizationAndCostTypesMappingByIdAndOrgIdDoc;
import com.nextuple.sourcing.cost.config.inbound.CreateOptimizationAndCostTypesMappingRequest;
import com.nextuple.sourcing.cost.config.inbound.UpdateOptimizationAndCostTypesMappingRequest;
import com.nextuple.sourcing.cost.config.outbound.OptimizationAndCostTypesMappingResponse;
import com.nextuple.sourcing.cost.config.service.OptimizationAndCostTypesMappingService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/cost-config/optimization-cost-types-mapping")
@RequiredArgsConstructor
@Tag(name = "Optimization And Cost Types Mapping APIs")
public class OptimizationAndCostTypesMappingController {
  private final OptimizationAndCostTypesMappingService optimizationAndCostTypesMappingService;

  @CreateOptimizationAndCostTypesMappingDoc
  @PostMapping(
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<BaseResponse<OptimizationAndCostTypesMappingResponse>>
      createOptimizationAndCostTypesMapping(
          @Valid @RequestBody
              CreateOptimizationAndCostTypesMappingRequest
                  createOptimizationAndCostTypesMappingRequest)
          throws CommonServiceException {
    log.debug(
        "Processing create Optimization And Cost Types Mapping request: {}",
        createOptimizationAndCostTypesMappingRequest);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(
            BaseResponse.builder()
                .message("Optimization And Cost Types Mapping created successfully!")
                .payload(
                    optimizationAndCostTypesMappingService.createOptimizationAndCostTypesMapping(
                        createOptimizationAndCostTypesMappingRequest))
                .build());
  }

  @UpdateOptimizationAndCostTypesMappingByIdAndOrgIdDoc
  @PutMapping(
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE,
      value = "/{orgId}/{id}")
  public ResponseEntity<BaseResponse<OptimizationAndCostTypesMappingResponse>>
      updateOptimizationAndCostTypesMappingByIdAndOrgId(
          @NotBlank(message = "Unique identifier for organisation cannot be empty.")
              @PathVariable
              @Parameter(
                  description = "Unique identifier for organisation.",
                  example = "NEXTUPLE_GR")
              String orgId,
          @NotNull(
                  message =
                      "Unique identifier for optimization and cost types mapping can't be null.")
              @PathVariable
              @Parameter(
                  description = "Unique identifier for optimization and cost types mapping.",
                  example = "1")
              Long id,
          @Valid @RequestBody
              UpdateOptimizationAndCostTypesMappingRequest
                  updateOptimizationAndCostTypesMappingRequest)
          throws CommonServiceException {
    log.debug(
        "Processing update Optimization And Cost Types Mapping request for orgId & id: {}, {}",
        orgId,
        id);
    var optimizationAndCostTypesMappingResponse =
        optimizationAndCostTypesMappingService.updateOptimizationAndCostTypesMappingByIdAndOrgId(
            id, orgId, updateOptimizationAndCostTypesMappingRequest);
    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Optimization And Cost Types Mapping updated successfully!")
            .payload(optimizationAndCostTypesMappingResponse)
            .build());
  }

  @FetchOptimizationAndCostTypesMappingByOrgIdAndOptStrategyDoc
  @GetMapping(
      produces = MediaType.APPLICATION_JSON_VALUE,
      value = "/{orgId}/optimizationStrategy/{optimizationStrategy}")
  public ResponseEntity<BaseResponse<OptimizationAndCostTypesMappingResponse>>
      fetchOptimizationAndCostTypesMappingByOrgIdAndStrategy(
          @NotBlank(message = "Unique identifier for organisation cannot be empty.")
              @PathVariable
              @Parameter(
                  description = "Unique identifier for organisation.",
                  example = "NEXTUPLE_GR")
              String orgId,
          @NotBlank(message = "Name of optimization strategy can't be empty.")
              @PathVariable
              @Parameter(description = "Name of optimization strategy.", example = "COST")
              String optimizationStrategy)
          throws CommonServiceException {
    log.debug(
        "Processing Fetch Optimization And Cost Types Mapping request for orgId & optimizationStrategy: {}, {}",
        orgId,
        optimizationStrategy);
    var optimizationAndCostTypesMappingResponse =
        optimizationAndCostTypesMappingService
            .fetchOptimizationAndCostTypesMappingByOrgIdAndStrategy(orgId, optimizationStrategy);
    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Optimization And Cost Types Mapping fetched successfully!")
            .payload(optimizationAndCostTypesMappingResponse)
            .build());
  }

  @FetchOptimizationAndCostTypesMappingByOrgIdAndIdDoc
  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE, value = "/{orgId}/{id}")
  public ResponseEntity<BaseResponse<OptimizationAndCostTypesMappingResponse>>
      fetchOptimizationAndCostTypesMappingByOrgIdAndId(
          @NotBlank(message = "Unique identifier for organisation cannot be empty.")
              @PathVariable
              @Parameter(
                  description = "Unique identifier for organisation.",
                  example = "NEXTUPLE_GR")
              String orgId,
          @NotNull(
                  message =
                      "Unique identifier for optimization and cost types mapping can't be null.")
              @PathVariable
              @Parameter(
                  description = "Unique identifier for optimization and cost types mapping.",
                  example = "1")
              Long id)
          throws CommonServiceException {
    log.debug(
        "Processing Fetch Optimization And Cost Types Mapping request for orgId & id: {}, {}",
        orgId,
        id);
    var optimizationAndCostTypesMappingResponse =
        optimizationAndCostTypesMappingService.fetchOptimizationAndCostTypesMappingByOrgIdAndId(
            orgId, id);
    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Optimization And Cost Types Mapping fetched successfully!")
            .payload(optimizationAndCostTypesMappingResponse)
            .build());
  }

  @DeleteOptimizationAndCostTypesMappingByOrgIdAndIdDoc
  @DeleteMapping(produces = MediaType.APPLICATION_JSON_VALUE, value = "/{orgId}/{id}")
  public ResponseEntity<BaseResponse<OptimizationAndCostTypesMappingResponse>>
      deleteOptimizationAndCostTypesMappingByOrgIdAndId(
          @NotBlank(message = "Unique identifier for organisation cannot be empty.")
              @PathVariable
              @Parameter(
                  description = "Unique identifier for organisation.",
                  example = "NEXTUPLE_GR")
              String orgId,
          @NotNull(
                  message =
                      "Unique identifier for optimization and cost types mapping can't be null.")
              @PathVariable
              @Parameter(
                  description = "Unique identifier for optimization and cost types mapping.",
                  example = "1")
              Long id)
          throws CommonServiceException {
    log.debug(
        "Processing Delete Optimization And Cost Types Mapping request for orgId & id: {}, {}",
        orgId,
        id);
    var optimizationAndCostTypesMappingResponse =
        optimizationAndCostTypesMappingService.deleteOptimizationAndCostTypesMappingByOrgIdAndId(
            orgId, id);
    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Optimization And Cost Types Mapping deleted successfully!")
            .payload(optimizationAndCostTypesMappingResponse)
            .build());
  }
}
