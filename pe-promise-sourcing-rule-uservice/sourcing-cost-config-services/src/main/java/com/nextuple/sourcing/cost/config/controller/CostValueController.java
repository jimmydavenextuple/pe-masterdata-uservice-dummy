/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.sourcing.cost.config.controller;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.sourcing.cost.config.controller.docs.CreateCostValueDoc;
import com.nextuple.sourcing.cost.config.controller.docs.DeleteCostValueDoc;
import com.nextuple.sourcing.cost.config.controller.docs.DeleteCostValueForCostFactorCombinationKeyDoc;
import com.nextuple.sourcing.cost.config.controller.docs.GetCostValueCacheKeyDoc;
import com.nextuple.sourcing.cost.config.controller.docs.GetCostValueDoc;
import com.nextuple.sourcing.cost.config.controller.docs.GetCostValueForCostFactorCombinationKeyDoc;
import com.nextuple.sourcing.cost.config.controller.docs.UpdateCostValueDoc;
import com.nextuple.sourcing.cost.config.dto.CostValueCacheKeyDto;
import com.nextuple.sourcing.cost.config.inbound.CreateCostValueRequest;
import com.nextuple.sourcing.cost.config.inbound.UpdateCostValueRequest;
import com.nextuple.sourcing.cost.config.outbound.CostValueResponse;
import com.nextuple.sourcing.cost.config.service.CostValueService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
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
@RequestMapping("/cost-config/cost-value")
@RequiredArgsConstructor
@Tag(name = "Cost Value APIs")
public class CostValueController {
  private static final Logger logger = LoggerFactory.getLogger(CostValueController.class);
  private final CostValueService costValueService;

  @CreateCostValueDoc
  @PostMapping(
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE,
      value = "/{orgId}")
  public ResponseEntity<BaseResponse<CostValueResponse>> createCostValue(
      @NotNull(message = "OrgId cannot be null")
          @PathVariable
          @Parameter(
              description = "Unique identifier of the organization.",
              example = "NEXTUPLE_GR")
          String orgId,
      @Valid @RequestBody CreateCostValueRequest costValueRequest)
      throws CommonServiceException {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(
            BaseResponse.builder()
                .message("Cost value created successfully.")
                .payload(costValueService.createCostValue(orgId, costValueRequest))
                .build());
  }

  @GetCostValueDoc
  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE, value = "/{orgId}/{id}")
  public ResponseEntity<BaseResponse<CostValueResponse>> getCostValue(
      @NotNull(message = "OrgId cannot be null")
          @PathVariable
          @Parameter(
              description = "Unique identifier of the organization.",
              example = "NEXTUPLE_GR")
          String orgId,
      @NotNull(message = "Id cannot be null")
          @PathVariable
          @Parameter(description = "Unique identifier for cost value", example = "1")
          Long id)
      throws CommonServiceException {
    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Cost value fetched successfully.")
            .payload(costValueService.getCostValue(orgId, id))
            .build());
  }

  @GetCostValueForCostFactorCombinationKeyDoc
  @GetMapping(
      produces = MediaType.APPLICATION_JSON_VALUE,
      value = "/get-cost/{orgId}/{costItinerary}")
  public ResponseEntity<BaseResponse<CostValueResponse>> getCostValueForCostFactorCombinationKey(
      @NotBlank(message = "OrgId cannot can't be empty")
          @PathVariable
          @Parameter(
              description = "Unique identifier of the organization.",
              example = "NEXTUPLE_GR")
          String orgId,
      @NotBlank(message = "CostItinerary can't be empty")
          @PathVariable
          @Parameter(
              description = "Cost itinerary for cost value",
              example = "SHIPPING_COST_FEDEX_LIKE")
          String costItinerary,
      @RequestParam(required = false, defaultValue = "")
          @Parameter(
              description = "Cost factor combination key for cost value",
              example = "FEDEX_GROUND|NON_HOLIDAYS|Z3|XL")
          String costFactorCombinationKey)
      throws CommonServiceException {
    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Cost value fetched successfully.")
            .payload(
                costValueService.getCostValueForCostFactorCombinationKey(
                    orgId, costItinerary, costFactorCombinationKey))
            .build());
  }

  @UpdateCostValueDoc
  @PutMapping(
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE,
      value = "/{orgId}/{id}")
  public ResponseEntity<BaseResponse<CostValueResponse>> updateCostValue(
      @NotNull(message = "OrgId cannot be null")
          @PathVariable
          @Parameter(
              description = "Unique identifier of the organization.",
              example = "NEXTUPLE_GR")
          String orgId,
      @NotNull(message = "Id cannot be null")
          @PathVariable
          @Parameter(description = "Unique identifier for cost value", example = "1")
          Long id,
      @Valid @RequestBody UpdateCostValueRequest costValueRequest)
      throws CommonServiceException {
    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Cost value updated successfully.")
            .payload(costValueService.updateCostValue(id, orgId, costValueRequest))
            .build());
  }

  @DeleteCostValueDoc
  @DeleteMapping(produces = MediaType.APPLICATION_JSON_VALUE, value = "/{orgId}/{id}")
  public ResponseEntity<BaseResponse<CostValueResponse>> deleteCostValue(
      @NotNull(message = "OrgId cannot be null")
          @PathVariable
          @Parameter(
              description = "Unique identifier of the organization.",
              example = "NEXTUPLE_GR")
          String orgId,
      @NotNull(message = "Id cannot be null")
          @PathVariable
          @Parameter(description = "Unique identifier for cost value", example = "1")
          Long id)
      throws CommonServiceException {
    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Cost value deleted successfully.")
            .payload(costValueService.deleteCostValue(orgId, id))
            .build());
  }

  @DeleteCostValueForCostFactorCombinationKeyDoc
  @DeleteMapping(
      produces = MediaType.APPLICATION_JSON_VALUE,
      value = "/{orgId}/{costItinerary}/{costFactorCombinationKey}")
  public ResponseEntity<BaseResponse<CostValueResponse>> deleteCostValueCostFactorCombinationKey(
      @NotNull(message = "OrgId cannot be null")
          @PathVariable
          @Parameter(
              description = "Unique identifier of the organization.",
              example = "NEXTUPLE_GR")
          String orgId,
      @NotBlank(message = "CostItinerary can't be empty")
          @PathVariable
          @Parameter(
              description = "Cost itinerary for cost value",
              example = "SHIPPING_COST_FEDEX_LIKE")
          String costItinerary,
      @NotBlank(message = "CostFactorCombinationKey can't be empty")
          @PathVariable
          @Parameter(
              description = "Cost factor combination key for cost value",
              example = "FEDEX_GROUND|NON_HOLIDAYS|Z3|XL")
          String costFactorCombinationKey)
      throws CommonServiceException {
    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Cost value deleted successfully.")
            .payload(
                costValueService.deleteCostValueForCostFactorCombinationKey(
                    orgId, costItinerary, costFactorCombinationKey))
            .build());
  }

  @GetCostValueCacheKeyDoc
  @GetMapping("/get-all-cache-keys")
  public ResponseEntity<BaseResponse<List<CostValueCacheKeyDto>>> getCostValueCacheKeys(
      @RequestParam(defaultValue = "100") Integer limit) {
    logger.debug("Processing get Cost Values Cache Keys");
    var response = costValueService.getCostValueCacheKeys(limit);
    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Cost Value Keys fetched successfully")
            .payload(response)
            .build());
  }
}
