/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.sourcing.cost.config.controller;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.sourcing.cost.config.controller.docs.*;
import com.nextuple.sourcing.cost.config.dto.CostFactorCacheKeyDto;
import com.nextuple.sourcing.cost.config.dto.CostFactorDto;
import com.nextuple.sourcing.cost.config.inbound.CostFactorRequest;
import com.nextuple.sourcing.cost.config.inbound.CostFactorUpdateRequest;
import com.nextuple.sourcing.cost.config.service.CostFactorService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for managing cost factors within an organization.
 *
 * <p>This controller provides APIs for creating, updating, retrieving, and deleting cost factors
 * based on the organization and cost factor identifiers. Additionally, it allows fetching cache
 * keys associated with cost factors.
 *
 * <p>The controller is tagged with "Tenant Cost Factor APIs" for easy categorization in API
 * documentation.
 */
@Validated
@RestController
@RequestMapping("/cost-config/cost-factor")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Tenant Cost Factor APIs")
public class CostFactorController {
  private static final Logger logger = LoggerFactory.getLogger(CostFactorBucketingController.class);
  private final CostFactorService costFactorService;

  /**
   * Creates a new cost factor for the specified organization.
   *
   * <p>This method processes a POST request to create a cost factor based on the details provided.
   *
   * @param orgId The unique identifier of the organization.
   * @param costFactorRequest The request body containing the details for the cost factor to be
   *     created.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with the created cost factor
   *     details.
   * @throws CommonServiceException If an error occurs during the creation process.
   */
  @CreateCostFactor
  @PostMapping(value = "/{orgId}")
  public ResponseEntity<BaseResponse<CostFactorDto>> createCostFactor(
      @NotBlank(message = "Unique identifier for organisation can't be empty")
          @PathVariable
          @Parameter(
              description = "Unique identifier of the organization.",
              example = "NEXTUPLE_GR")
          String orgId,
      @Valid @RequestBody CostFactorRequest costFactorRequest)
      throws CommonServiceException {
    log.debug("Processing create Cost Factor request");
    var costFactorResponse = costFactorService.createCostFactor(orgId, costFactorRequest);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(
            BaseResponse.builder()
                .message("Cost Factor created successfully!")
                .payload(costFactorResponse)
                .build());
  }

  /**
   * Retrieves a cost factor by its unique identifier for a specified organization.
   *
   * <p>This method processes a GET request to fetch the cost factor details based on the
   * organization ID and cost factor ID.
   *
   * @param orgId The unique identifier of the organization.
   * @param costFactorId The unique identifier of the cost factor.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with the requested cost
   *     factor details.
   * @throws CommonServiceException If the cost factor cannot be retrieved.
   */
  @GetCostFactorByOrgIdAndCostFactorId
  @GetMapping(value = "/{orgId}/{costFactorId}")
  public ResponseEntity<BaseResponse<CostFactorDto>> getCostFactorByOrgIdAndCostFactorId(
      @NotBlank(message = "Unique identifier for organisation can't be empty")
          @PathVariable
          @Parameter(
              description = "Unique identifier of the organization.",
              example = "NEXTUPLE_GR")
          String orgId,
      @NotNull(message = "Unique identifier for cost factor can't be null")
          @PathVariable
          @Parameter(description = "Unique identifier for cost factor", example = "1")
          Long costFactorId)
      throws CommonServiceException {
    log.debug("Processing get Cost Factor by org id and id request");
    var costFactorResponse =
        costFactorService.findCostFactorByOrgIdAndCostFactorId(orgId, costFactorId);
    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Cost Factor fetched successfully!")
            .payload(costFactorResponse)
            .build());
  }

  /**
   * Fetches a cost factor by its name for the specified organization.
   *
   * <p>This method processes a GET request to fetch the cost factor details based on the
   * organization ID and cost factor name.
   *
   * @param orgId The unique identifier of the organization.
   * @param costFactor The name of the cost factor.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with the requested cost
   *     factor details.
   * @throws CommonServiceException If the cost factor cannot be retrieved.
   */
  @GetCostFactorByOrgIdAndCostFactorDoc
  @GetMapping(value = "/{orgId}/costFactor/{costFactor}")
  public ResponseEntity<BaseResponse<CostFactorDto>> getCostFactorByOrgIdAndCostFactor(
      @NotBlank(message = "Unique identifier for organisation can't be empty")
          @PathVariable
          @Parameter(
              description = "Unique identifier of the organization.",
              example = "NEXTUPLE_GR")
          String orgId,
      @NotBlank(message = "Cost Factor can't be empty")
          @PathVariable
          @Parameter(description = "Cost Factor", example = "BillWeightUps")
          String costFactor)
      throws CommonServiceException {
    log.debug("Processing get Cost Factor by org id and cost factor request");
    var costFactorResponse =
        costFactorService.findCostFactorByOrgIdAndCostFactor(orgId, costFactor);
    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Cost Factor fetched successfully!")
            .payload(costFactorResponse)
            .build());
  }

  /**
   * Updates an existing cost factor for the specified organization.
   *
   * <p>This method processes a PUT request to update the cost factor based on the details provided
   * in the request body.
   *
   * @param orgId The unique identifier of the organization.
   * @param costFactorId The unique identifier of the cost factor.
   * @param updateCostFactorRequest The request body containing the updated cost factor details.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with the updated cost factor
   *     details.
   * @throws CommonServiceException If an error occurs during the update process.
   */
  @UpdateCostFactor
  @PutMapping(value = "/{orgId}/{costFactorId}")
  public ResponseEntity<BaseResponse<CostFactorDto>> updateCostFactor(
      @NotBlank(message = "Unique identifier for organisation can't be empty")
          @PathVariable
          @Parameter(
              description = "Unique identifier of the organization.",
              example = "NEXTUPLE_GR")
          String orgId,
      @NotNull(message = "Unique identifier for cost factor can't be null")
          @PathVariable
          @Parameter(description = "Unique identifier for cost factor", example = "1")
          Long costFactorId,
      @Valid @RequestBody CostFactorUpdateRequest updateCostFactorRequest)
      throws CommonServiceException {
    log.debug("Processing update Cost Factor request");
    var costFactorResponse =
        costFactorService.updateCostFactor(costFactorId, orgId, updateCostFactorRequest);
    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Cost Factor Details updated successfully!")
            .payload(costFactorResponse)
            .build());
  }

  /**
   * Deletes a cost factor by its unique identifier for the specified organization.
   *
   * <p>This method processes a DELETE request to remove the cost factor based on the provided
   * organization ID and cost factor ID.
   *
   * @param orgId The unique identifier of the organization.
   * @param costFactorId The unique identifier of the cost factor.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with the details of the
   *     deleted cost factor.
   * @throws CommonServiceException If the cost factor cannot be deleted.
   */
  @DeleteCostFactor
  @DeleteMapping(value = "/{orgId}/{costFactorId}")
  public ResponseEntity<BaseResponse<CostFactorDto>> deleteCostFactor(
      @NotBlank(message = "Unique identifier for organisation can't be empty")
          @PathVariable
          @Parameter(
              description = "Unique identifier of the organization.",
              example = "NEXTUPLE_GR")
          String orgId,
      @NotNull(message = "Unique identifier for cost factor can't be null")
          @PathVariable
          @Parameter(description = "Unique identifier for cost factor", example = "1")
          Long costFactorId)
      throws CommonServiceException {
    log.debug("Processing delete Cost Factor request");
    var costFactorResponse = costFactorService.deleteCostFactor(costFactorId, orgId);
    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Cost Factor Details deleted successfully!")
            .payload(costFactorResponse)
            .build());
  }

  /**
   * Retrieves cache keys for cost factors.
   *
   * <p>This method processes a GET request to fetch a list of cache keys associated with cost
   * factors.
   *
   * @param limit The maximum number of cache keys to retrieve. Defaults to 100.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with the list of cache keys.
   */
  @GetCostFactorCacheKeyDoc
  @GetMapping("/get-all-cache-keys")
  public ResponseEntity<BaseResponse<List<CostFactorCacheKeyDto>>> getCostFactorCacheKeys(
      @RequestParam(defaultValue = "100") Integer limit) {
    logger.debug("Processing get Cost Factor Cache Keys");

    var response = costFactorService.getCostFactorCacheKeys(limit);

    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Cost Factor Bucket Type Cache Keys fetched successfully")
            .payload(response)
            .build());
  }
}
