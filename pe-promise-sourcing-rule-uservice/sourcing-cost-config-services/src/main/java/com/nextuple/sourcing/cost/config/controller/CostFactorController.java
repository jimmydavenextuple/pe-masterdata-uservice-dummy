/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.sourcing.cost.config.controller;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.sourcing.cost.config.controller.docs.CreateCostFactor;
import com.nextuple.sourcing.cost.config.controller.docs.DeleteCostFactor;
import com.nextuple.sourcing.cost.config.controller.docs.GetCostFactorByOrgIdAndCostFactorDoc;
import com.nextuple.sourcing.cost.config.controller.docs.GetCostFactorByOrgIdAndCostFactorId;
import com.nextuple.sourcing.cost.config.controller.docs.GetCostFactorCacheKeyDoc;
import com.nextuple.sourcing.cost.config.controller.docs.UpdateCostFactor;
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
@RequestMapping("/cost-config/cost-factor")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Tenant Cost Factor APIs")
public class CostFactorController {
  private static final Logger logger = LoggerFactory.getLogger(CostFactorBucketingController.class);
  private final CostFactorService costFactorService;

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
