/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.sourcing.cost.config.controller;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.sourcing.cost.config.controller.docs.CreateCostFactorBucketTypeDoc;
import com.nextuple.sourcing.cost.config.controller.docs.DeleteCostFactorBucketDoc;
import com.nextuple.sourcing.cost.config.controller.docs.GetCostFactorBucketDoc;
import com.nextuple.sourcing.cost.config.controller.docs.GetCostFactorBucketTypeCacheKeyDoc;
import com.nextuple.sourcing.cost.config.controller.docs.UpdateCostFactorBucketDoc;
import com.nextuple.sourcing.cost.config.dto.CostFactorBucketTypeCacheKeyDto;
import com.nextuple.sourcing.cost.config.dto.CostFactorBucketTypeDto;
import com.nextuple.sourcing.cost.config.inbound.CostFactorBucketTypeRequest;
import com.nextuple.sourcing.cost.config.inbound.UpdateCostFactorBucketTypeRequest;
import com.nextuple.sourcing.cost.config.service.CostFactorBucketTypeServiceImpl;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for managing cost factor bucket types.
 *
 * <p>This controller provides APIs to create, retrieve, update, and delete cost factor bucket types
 * for a specific organization. It also includes methods to fetch cache keys associated with cost
 * factor bucket types. The controller ensures that all operations related to cost factor bucketing
 * are efficiently handled through interaction with the service layer.
 *
 * <p>The controller is tagged with "Cost factor bucketing APIs" for easy categorization in API
 * documentation.
 */
@Validated
@RestController
@RequestMapping("/cost-config/cost-factor-buckets")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Cost factor bucketing APIs")
public class CostFactorBucketingController {
  private static final Logger logger = LoggerFactory.getLogger(CostFactorBucketingController.class);
  private final CostFactorBucketTypeServiceImpl costFactorBucketTypeServiceImpl;

  /**
   * Creates a new cost factor bucket type.
   *
   * <p>This method processes a POST request to create a cost factor bucket type for a specific
   * organization.
   *
   * @param orgId The unique identifier of the organization. Must not be empty.
   * @param costFactorBucketTypeRequest The request body containing details of the cost factor
   *     bucket type to be created.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with the created cost factor
   *     bucket type details.
   * @throws CommonServiceException If an error occurs during the creation of the cost factor bucket
   *     type.
   */
  @CreateCostFactorBucketTypeDoc
  @PostMapping(value = "/{orgId}")
  public ResponseEntity<BaseResponse<CostFactorBucketTypeDto>> createCostFactorBucketType(
      @NotBlank(message = "orgId cannot be empty")
          @PathVariable
          @Parameter(description = "Unique identifier for organization")
          String orgId,
      @Valid @RequestBody CostFactorBucketTypeRequest costFactorBucketTypeRequest)
      throws CommonServiceException {
    log.debug(
        "Processing create cost factor bucket request for request: {}",
        costFactorBucketTypeRequest);
    var costFactorBucketResponse =
        costFactorBucketTypeServiceImpl.createCostFactorBucketType(
            orgId, costFactorBucketTypeRequest);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(
            BaseResponse.builder()
                .message("Cost Factor Bucket type created successfully!")
                .payload(costFactorBucketResponse)
                .build());
  }

  /**
   * Retrieves cost factor bucket type details.
   *
   * <p>This method processes a GET request to fetch the details of a cost factor bucket type for a
   * given organization and cost factor.
   *
   * @param orgId The unique identifier of the organization. Must not be empty.
   * @param costFactor The cost factor for the bucket type. Must not be empty.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with the fetched cost factor
   *     bucket type details.
   * @throws CommonServiceException If an error occurs while retrieving the cost factor bucket type
   *     details.
   */
  @GetCostFactorBucketDoc
  @GetMapping(value = "/{orgId}/{costFactor}")
  public ResponseEntity<BaseResponse<CostFactorBucketTypeDto>> getCostFactorBucket(
      @NotBlank(message = "orgId cannot be empty")
          @PathVariable
          @Parameter(description = "Unique identifier for organization")
          String orgId,
      @NotBlank(message = "costFactor cannot be empty")
          @PathVariable
          @Parameter(description = "Cost factor for the bucket type")
          String costFactor)
      throws CommonServiceException {
    log.debug(
        "Processing get cost factor bucket request for orgId: {} and cost factor: {}",
        orgId,
        costFactor);
    var costFactorBucketResponse =
        costFactorBucketTypeServiceImpl.getCostFactorBucketType(orgId, costFactor);
    return ResponseEntity.status(HttpStatus.OK)
        .body(
            BaseResponse.builder()
                .message("Cost Factor Bucket type fetched successfully!")
                .payload(costFactorBucketResponse)
                .build());
  }

  /**
   * Updates an existing cost factor bucket type.
   *
   * <p>This method processes a PUT request to update a cost factor bucket type for a given
   * organization and cost factor.
   *
   * @param orgId The unique identifier of the organization. Must not be empty.
   * @param costFactor The cost factor for the bucket type. Must not be empty.
   * @param updateCostFactorBucketTypeRequest The request body containing updated details of the
   *     cost factor bucket type.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with the updated cost factor
   *     bucket type details.
   * @throws CommonServiceException If an error occurs during the update of the cost factor bucket
   *     type.
   */
  @UpdateCostFactorBucketDoc
  @PutMapping(value = "{orgId}/{costFactor}")
  public ResponseEntity<BaseResponse<CostFactorBucketTypeDto>> updateCostFactorBucket(
      @NotBlank(message = "orgId cannot be empty")
          @PathVariable
          @Parameter(description = "Unique identifier for organization")
          String orgId,
      @NotBlank(message = "costFactor cannot be empty")
          @PathVariable
          @Parameter(description = "Cost factor for the bucket type")
          String costFactor,
      @Valid @RequestBody UpdateCostFactorBucketTypeRequest updateCostFactorBucketTypeRequest)
      throws CommonServiceException {
    log.debug(
        "Processing update cost factor bucket request for orgId: {} and costFactor: {}",
        orgId,
        costFactor);
    var costFactorBucketResponse =
        costFactorBucketTypeServiceImpl.updateCostFactorBucketType(
            orgId, costFactor, updateCostFactorBucketTypeRequest);
    return ResponseEntity.status(HttpStatus.OK)
        .body(
            BaseResponse.builder()
                .message("Cost Factor Bucket type updated successfully!")
                .payload(costFactorBucketResponse)
                .build());
  }

  /**
   * Deletes an existing cost factor bucket type.
   *
   * <p>This method processes a DELETE request to remove a cost factor bucket type for a given
   * organization and cost factor.
   *
   * @param orgId The unique identifier of the organization. Must not be empty.
   * @param costFactor The cost factor for the bucket type. Must not be empty.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with the details of the
   *     deleted cost factor bucket type.
   * @throws CommonServiceException If an error occurs during the deletion of the cost factor bucket
   *     type.
   */
  @DeleteCostFactorBucketDoc
  @DeleteMapping(value = "{orgId}/{costFactor}")
  public ResponseEntity<BaseResponse<CostFactorBucketTypeDto>> deleteCostFactorBucket(
      @NotBlank(message = "orgId cannot be empty")
          @PathVariable
          @Parameter(description = "Unique identifier for organization")
          String orgId,
      @NotBlank(message = "costFactor cannot be empty")
          @PathVariable
          @Parameter(description = "Cost factor for the bucket type")
          String costFactor)
      throws CommonServiceException {
    log.debug(
        "Processing delete cost factor bucket request for orgId: {} and costFactor: {}",
        orgId,
        costFactor);
    var costFactorBucketResponse =
        costFactorBucketTypeServiceImpl.deleteCostFactorBucketType(orgId, costFactor);
    return ResponseEntity.status(HttpStatus.OK)
        .body(
            BaseResponse.builder()
                .message("Cost Factor Bucket type deleted successfully!")
                .payload(costFactorBucketResponse)
                .build());
  }

  /**
   * Retrieves all cache keys for cost factor bucket types.
   *
   * <p>This method processes a GET request to fetch a list of cache keys associated with cost
   * factor bucket types.
   *
   * @param limit The maximum number of cache keys to retrieve. Defaults to 100.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with the list of cache keys.
   */
  @GetCostFactorBucketTypeCacheKeyDoc
  @GetMapping("/get-all-cache-keys")
  public ResponseEntity<BaseResponse<List<CostFactorBucketTypeCacheKeyDto>>>
      getCostFactorBucketTypeCacheKeys(@RequestParam(defaultValue = "100") Integer limit) {
    logger.debug("Processing get Cost Factor Bucket Type Cache Keys");

    var response = costFactorBucketTypeServiceImpl.getCostFactorBucketTypeCacheKeys(limit);

    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Cost Factor Bucket Type Cache Keys fetched successfully")
            .payload(response)
            .build());
  }
}
