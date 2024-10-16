/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.sourcing.cost.config.controller;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.sourcing.cost.config.controller.docs.CreateCostFactorDiscreteBucketDoc;
import com.nextuple.sourcing.cost.config.controller.docs.DeleteCostFactorDiscreteBucketDoc;
import com.nextuple.sourcing.cost.config.controller.docs.GetCostFactorDiscreteBucketCacheKeyDoc;
import com.nextuple.sourcing.cost.config.controller.docs.GetCostFactorDiscreteBucketDoc;
import com.nextuple.sourcing.cost.config.controller.docs.UpdateCostFactorDiscreteBucketDoc;
import com.nextuple.sourcing.cost.config.dto.CostFactorDiscreteBucketCacheKeyDto;
import com.nextuple.sourcing.cost.config.dto.CostFactorDiscreteBucketDto;
import com.nextuple.sourcing.cost.config.inbound.CostFactorDiscreteBucketRequest;
import com.nextuple.sourcing.cost.config.service.CostFactorDiscreteBucketService;
import com.nextuple.sourcing.cost.config.service.CostFactorDiscreteBucketServiceImpl;
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
@RequestMapping("/cost-config/cost-factor-discrete-buckets")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Cost factor discrete buckets APIs")
public class CostFactorDiscreteBucketController {
  private static final Logger logger =
      LoggerFactory.getLogger(CostFactorDiscreteBucketController.class);

  private final CostFactorDiscreteBucketService costFactorDiscreteBucketService;
  private final CostFactorDiscreteBucketServiceImpl costFactorDiscreteBucketServiceImpl;

  @CreateCostFactorDiscreteBucketDoc
  @PostMapping("/{orgId}")
  public ResponseEntity<BaseResponse<CostFactorDiscreteBucketDto>> createCostFactorDiscreteBucket(
      @NotBlank(message = "orgId cannot be empty")
          @PathVariable
          @Parameter(description = "Unique identifier for organization")
          String orgId,
      @Valid @RequestBody CostFactorDiscreteBucketRequest costFactorDiscreteBucketRequest)
      throws CommonServiceException {
    log.debug(
        "Processing create cost factor bucket request for request: {}",
        costFactorDiscreteBucketRequest);
    var costFactorBucketResponse =
        costFactorDiscreteBucketService.createCostFactorDiscreteBucket(
            orgId, costFactorDiscreteBucketRequest);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(
            BaseResponse.builder()
                .message("Cost Factor Discrete Bucket created successfully!")
                .payload(costFactorBucketResponse)
                .build());
  }

  @GetCostFactorDiscreteBucketDoc
  @GetMapping("/{orgId}/{costFactor}")
  public ResponseEntity<BaseResponse<List<CostFactorDiscreteBucketDto>>>
      getCostFactorDiscreteBucket(
          @NotBlank(message = "orgId cannot be blank")
              @PathVariable
              @Parameter(description = "Unique identifier for organization")
              String orgId,
          @NotBlank(message = "costFactor cannot be empty")
              @PathVariable
              @Parameter(description = "Cost factor for the bucket type")
              String costFactor)
          throws CommonServiceException {
    var costFactorDiscreteBucket =
        costFactorDiscreteBucketService.getCostFactorDiscreteBucket(orgId, costFactor);
    return ResponseEntity.status(HttpStatus.OK)
        .body(
            BaseResponse.builder()
                .message("Cost Factor Discrete Bucket fetched successfully!")
                .payload(costFactorDiscreteBucket)
                .build());
  }

  @UpdateCostFactorDiscreteBucketDoc
  @PutMapping(value = "{orgId}/{id}")
  public ResponseEntity<BaseResponse<CostFactorDiscreteBucketDto>> updateCostFactorBucket(
      @NotBlank(message = "orgId cannot be empty")
          @PathVariable
          @Parameter(description = "Unique identifier for organization")
          String orgId,
      @NotNull(message = "Id cannot be empty")
          @PathVariable
          @Parameter(description = "Id of the bucket record")
          Long id,
      @Valid @RequestBody CostFactorDiscreteBucketRequest request)
      throws CommonServiceException {
    log.debug(
        "Processing update cost factor discrete bucket request for orgId: {} and id: {}",
        orgId,
        id);
    var costFactorDiscreteBucketResponse =
        costFactorDiscreteBucketService.updateCostFactorDiscreteBucket(id, orgId, request);
    return ResponseEntity.status(HttpStatus.OK)
        .body(
            BaseResponse.builder()
                .message("Cost Factor Discrete Bucket updated successfully!")
                .payload(costFactorDiscreteBucketResponse)
                .build());
  }

  @DeleteCostFactorDiscreteBucketDoc
  @DeleteMapping(value = "{orgId}/{id}")
  public ResponseEntity<BaseResponse<CostFactorDiscreteBucketDto>> deleteCostFactorBucket(
      @NotBlank(message = "orgId cannot be empty")
          @PathVariable
          @Parameter(description = "Unique identifier for organization")
          String orgId,
      @NotNull(message = "Id cannot be empty")
          @PathVariable
          @Parameter(description = "Id of the bucket record")
          Long id)
      throws CommonServiceException {
    log.debug("Processing delete cost factor bucket for orgId: {} and id: {}", orgId, id);
    var costFactorBucketResponse =
        costFactorDiscreteBucketService.deleteCostFactorDiscreteBucket(id, orgId);
    return ResponseEntity.status(HttpStatus.OK)
        .body(
            BaseResponse.builder()
                .message("Cost Factor Bucket deleted successfully!")
                .payload(costFactorBucketResponse)
                .build());
  }

  @GetCostFactorDiscreteBucketCacheKeyDoc
  @GetMapping("/get-all-cache-keys")
  public ResponseEntity<BaseResponse<List<CostFactorDiscreteBucketCacheKeyDto>>>
      getCostFactorDiscreteBucketCacheKeys(@RequestParam(defaultValue = "100") Integer limit) {
    logger.debug("Processing get Cost Factor Discrete Bucket Cache Keys");
    var response = costFactorDiscreteBucketServiceImpl.getCostFactorDiscreteBucketCacheKeys(limit);
    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Cost Factor Discrete Bucket Keys fetched successfully")
            .payload(response)
            .build());
  }
}
