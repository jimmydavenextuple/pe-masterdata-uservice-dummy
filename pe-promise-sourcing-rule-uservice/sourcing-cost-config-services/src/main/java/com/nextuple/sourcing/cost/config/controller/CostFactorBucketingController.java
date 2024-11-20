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

@Validated
@RestController
@RequestMapping("/cost-config/cost-factor-buckets")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Cost factor bucketing APIs")
public class CostFactorBucketingController {
  private static final Logger logger = LoggerFactory.getLogger(CostFactorBucketingController.class);
  private final CostFactorBucketTypeServiceImpl costFactorBucketTypeServiceImpl;

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
