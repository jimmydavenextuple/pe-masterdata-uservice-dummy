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

/**
 * Controller for managing Optimization and Cost Types Mappings in the system.
 *
 * <p>This controller provides APIs for creating, updating, fetching, and deleting optimization and
 * cost types mappings. It allows users to manage these mappings and their configurations within the
 * system.
 *
 * <p>The controller is tagged with "Optimization And Cost Types Mapping APIs" for easy
 * categorization in API documentation.
 */
@Slf4j
@RestController
@RequestMapping("/cost-config/optimization-cost-types-mapping")
@RequiredArgsConstructor
@Tag(name = "Optimization And Cost Types Mapping APIs")
public class OptimizationAndCostTypesMappingController {
  private final OptimizationAndCostTypesMappingService optimizationAndCostTypesMappingService;

  /**
   * Creates a new optimization and cost types mapping in the system.
   *
   * <p>This method processes a POST request to create a new optimization and cost types mapping
   * based on the provided request details. It validates the input request and delegates the
   * creation process to the `OptimizationAndCostTypesMappingService`.
   *
   * @param createOptimizationAndCostTypesMappingRequest The request object containing the details
   *     of the optimization and cost types mapping to be created.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with a success message and
   *     the created optimization and cost types mapping details.
   * @throws CommonServiceException If a common service exception occurs while processing the
   *     request.
   */
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

  /**
   * Updates an existing optimization and cost types mapping for the specified organization and
   * mapping ID.
   *
   * <p>This method processes a PUT request to update an existing optimization and cost types
   * mapping.
   *
   * @param orgId The unique identifier of the organization (e.g., "NEXTUPLE_GR").
   * @param id The unique identifier of the optimization and cost types mapping.
   * @param updateOptimizationAndCostTypesMappingRequest The request payload containing the updated
   *     optimization and cost types mapping details.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with the updated
   *     optimization and cost types mapping details.
   * @throws CommonServiceException If there is an error during the update process.
   */
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

  /**
   * Retrieves an optimization and cost types mapping for a specific organization and optimization
   * strategy.
   *
   * <p>This method processes a GET request to fetch an optimization and cost types mapping based on
   * the provided organization ID and optimization strategy. It delegates the retrieval process to
   * the `OptimizationAndCostTypesMappingService`.
   *
   * @param orgId The unique identifier of the organization.
   * @param optimizationStrategy The name of the optimization strategy.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with a success message and
   *     the optimization and cost types mapping details.
   * @throws CommonServiceException If there is an error during the fetch process.
   */
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

  /**
   * Retrieves an optimization and cost types mapping for a specific organization and mapping ID.
   *
   * <p>This method processes a GET request to fetch an optimization and cost types mapping based on
   * the provided organization ID and mapping ID. It delegates the retrieval process to the
   * `OptimizationAndCostTypesMappingService`.
   *
   * @param orgId The unique identifier of the organization.
   * @param id The unique identifier of the optimization and cost types mapping.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with a success message and
   *     the optimization and cost types mapping details.
   * @throws CommonServiceException If there is an error during the fetch process.
   */
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

  /**
   * Deletes an optimization and cost types mapping for a specific organization and mapping ID.
   *
   * <p>This method processes a DELETE request to remove an optimization and cost types mapping
   * based on the provided organization ID and mapping ID. It delegates the deletion process to the
   * `OptimizationAndCostTypesMappingService`.
   *
   * @param orgId The unique identifier of the organization.
   * @param id The unique identifier of the optimization and cost types mapping.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with a success message
   *     confirming the deletion.
   * @throws CommonServiceException If there is an error during the deletion process.
   */
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
