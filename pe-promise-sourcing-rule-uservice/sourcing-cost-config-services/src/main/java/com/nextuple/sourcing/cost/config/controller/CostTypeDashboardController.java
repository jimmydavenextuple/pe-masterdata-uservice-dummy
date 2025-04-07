/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.sourcing.cost.config.controller;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.sourcing.cost.config.controller.docs.CostTypeDashboardDoc;
import com.nextuple.sourcing.cost.config.controller.docs.CostTypeValidationDoc;
import com.nextuple.sourcing.cost.config.controller.docs.UpdateRateCardStatusDoc;
import com.nextuple.sourcing.cost.config.inbound.UpdateRateCardStatusRequest;
import com.nextuple.sourcing.cost.config.outbound.CostTypeResponse;
import com.nextuple.sourcing.cost.config.outbound.CostTypeValidationResponse;
import com.nextuple.sourcing.cost.config.outbound.UpdateRateCardStatusResponse;
import com.nextuple.sourcing.cost.config.service.CostTypeDashboardService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for managing Cost Type Dashboard within an organization.
 *
 * <p>This controller provides APIs to retrieve cost types, update rate card status, and validate
 * cost types for a specific organization. It allows fetching and managing cost type information for
 * organizational configurations.
 *
 * <p>The controller is tagged with "Cost Type Dashboard APIs" for easy categorization in API
 * documentation.
 */
@RestController
@RequestMapping("/cost-config/ui")
@RequiredArgsConstructor
@Tag(name = "Cost Type Dashboard APIs")
@Slf4j
public class CostTypeDashboardController {
  private final CostTypeDashboardService costTypeDashboardService;

  /**
   * Retrieves a list of Cost Types for a specific organization.
   *
   * <p>This method processes a GET request to fetch details of cost types associated with a given
   * organization ID.
   *
   * @param orgId The unique identifier of the organization.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with the list of cost types.
   * @throws CommonServiceException If there is an error during the retrieval of cost types.
   */
  @CostTypeDashboardDoc
  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE, value = "/cost-types/{orgId}")
  public ResponseEntity<BaseResponse<CostTypeResponse>> getCostTypes(
      @PathVariable
          @NotBlank(message = "Unique identifier for organisation can't be empty")
          @Parameter(
              description = "Unique identifier of the organization.",
              example = "NEXTUPLE_GR")
          String orgId)
      throws CommonServiceException {
    log.debug("Processing get cost type for {} request", orgId);
    var costTypeResponse = costTypeDashboardService.getCostTypes(orgId);
    return ResponseEntity.status(HttpStatus.OK)
        .body(
            BaseResponse.builder()
                .message("Cost Types and it’s information fetched successfully!")
                .payload(costTypeResponse)
                .build());
  }

  /**
   * Updates the rate card status for a specific organization.
   *
   * <p>This method processes a PUT request to update the rate card status for the given
   * organization.
   *
   * @param orgId The unique identifier of the organization.
   * @param updateRateCardStatusRequest The request payload containing the rate card status details
   *     to be updated.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with the updated rate card
   *     status.
   * @throws CommonServiceException If there is an error during the update process.
   */
  @UpdateRateCardStatusDoc
  @PutMapping(value = "/cost-definition/rate-card-status/{orgId}")
  public ResponseEntity<BaseResponse<UpdateRateCardStatusResponse>> updateRateCardStatus(
      @NotBlank(message = "Unique identifier for organisation can't be empty")
          @PathVariable
          @Parameter(
              description = "Unique identifier of the organization.",
              example = "NEXTUPLE_GR")
          String orgId,
      @Valid @RequestBody UpdateRateCardStatusRequest updateRateCardStatusRequest)
      throws CommonServiceException {
    log.debug("Processing rate card status request for a given orgId: {}", orgId);
    var updateRateCardStatusResponse =
        costTypeDashboardService.updateRateCardStatus(orgId, updateRateCardStatusRequest);
    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Rate card status updated successfully!")
            .payload(updateRateCardStatusResponse)
            .build());
  }

  /**
   * Retrieves details for Cost Type validation for a specific organization and cost type.
   *
   * <p>This method processes a GET request to fetch details of a specific cost type for validation.
   *
   * @param orgId The unique identifier of the organization.
   * @param costType The cost type to fetch details for validation.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with the cost type
   *     validation details.
   * @throws CommonServiceException If there is an error during the retrieval of validation details.
   */
  @CostTypeValidationDoc
  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE, value = "/cost-types/{orgId}/{costType}")
  public ResponseEntity<BaseResponse<CostTypeValidationResponse>> getCostTypesForValidation(
      @PathVariable
          @NotBlank(message = "Unique identifier for organisation can't be empty")
          @Parameter(
              description = "Unique identifier of the organization.",
              example = "NEXTUPLE_GR")
          String orgId,
      @PathVariable
          @NotBlank(message = "Cost type can't be empty")
          @Parameter(description = "Specifies the cost type.", example = "SHIPPING_COST")
          String costType)
      throws CommonServiceException {
    log.debug("Processing get cost type validation request for {} and {}", orgId, costType);
    var costTypeValidationResponse =
        costTypeDashboardService.getCostTypeDetailsForValidation(orgId, costType);
    return ResponseEntity.status(HttpStatus.OK)
        .body(
            BaseResponse.builder()
                .message("Cost type details fetched successfully!")
                .payload(costTypeValidationResponse)
                .build());
  }
}
