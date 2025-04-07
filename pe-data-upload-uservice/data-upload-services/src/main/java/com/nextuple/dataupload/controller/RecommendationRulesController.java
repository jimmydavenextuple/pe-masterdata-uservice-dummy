/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.dataupload.controller;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.dataupload.common.inbound.ConfigureShipChargeCappingRequest;
import com.nextuple.dataupload.common.inbound.DeleteTargetProfitMarginRequest;
import com.nextuple.dataupload.common.inbound.TargetProfitMarginRequest;
import com.nextuple.dataupload.common.outbound.AttributeAndValuesTGMResponse;
import com.nextuple.dataupload.common.outbound.ConfigureShipChargeCappingResponse;
import com.nextuple.dataupload.common.outbound.ShipChargeDetailsTGMResponse;
import com.nextuple.dataupload.common.outbound.TargetProfitMarginResponse;
import com.nextuple.dataupload.controller.docs.ConfigureShipChargeCappingDoc;
import com.nextuple.dataupload.controller.docs.ConfigureTargetProfitMarginDoc;
import com.nextuple.dataupload.controller.docs.DeleteTargetProfitMarginDoc;
import com.nextuple.dataupload.controller.docs.FetchAttributeDetailsDoc;
import com.nextuple.dataupload.controller.docs.FetchShipChargeDetailsDoc;
import com.nextuple.dataupload.controller.docs.FetchTargetProfitMarginDoc;
import com.nextuple.dataupload.controller.docs.UpdateShipChargeCappingStatusDoc;
import com.nextuple.dataupload.controller.docs.UpdateTargetProfitMarginDoc;
import com.nextuple.dataupload.service.RecommendationRulesService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for managing recommendation rules and target profit margins.
 *
 * <p>This controller provides APIs to configure, update, and manage recommendation rules including
 * target profit margins and ship charge capping configurations. It supports operations for:
 *
 * <ul>
 *   <li>Creating and updating target profit margins
 *   <li>Managing ship charge capping constants
 *   <li>Fetching attribute details and configurations
 *   <li>Deleting target profit margin configurations
 * </ul>
 *
 * <p>The controller exposes endpoints for managing recommendation rules at the organization level,
 * with support for attribute-based configurations and ship charge capping logic.
 *
 * @see RecommendationRulesService
 * @see TargetProfitMarginRequest
 * @see ConfigureShipChargeCappingRequest
 */
@RestController
@RequestMapping("/ui/recommendation-rules")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Recommendation rule APIs")
public class RecommendationRulesController {

  private final RecommendationRulesService recommendationRulesService;

  /**
   * Creates a new target profit margin configuration for an organization.
   *
   * <p>This endpoint processes a POST request to create a target profit margin configuration based
   * on the provided organization ID and target profit margin details.
   *
   * @param orgId The unique identifier for the organization.
   * @param targetProfitMarginRequest The request payload containing target profit margin details.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with the created target
   *     profit margin configuration.
   * @throws CommonServiceException If there is an error in processing the request.
   */
  @ConfigureTargetProfitMarginDoc
  @PostMapping(value = "/{orgId}")
  public ResponseEntity<BaseResponse<TargetProfitMarginResponse>> createTargetProfitMargin(
      @PathVariable @Parameter(description = "Unique identifier for organization ID.") String orgId,
      @Valid @RequestBody TargetProfitMarginRequest targetProfitMarginRequest)
      throws CommonServiceException {
    log.debug(
        "Processing create gross profit target margin {} for orgId {},  attributeName {},  attribute value {} ",
        targetProfitMarginRequest.getTargetGrossProfitMargin(),
        orgId,
        targetProfitMarginRequest.getAttributeName(),
        targetProfitMarginRequest.getAttributeValue());
    return ResponseEntity.status(HttpStatus.OK)
        .body(
            BaseResponse.builder()
                .message("Target profit margin configured successfully")
                .payload(
                    recommendationRulesService.createTargetProfitMargin(
                        orgId, targetProfitMarginRequest))
                .build());
  }

  /**
   * Updates an existing target profit margin configuration for an organization.
   *
   * <p>This method processes a PUT request to update the target profit margin configuration for a
   * given organization. The update is performed based on the provided {@link
   * TargetProfitMarginRequest} which contains the new target profit margin details.
   *
   * @param orgId The unique identifier for the organization. Must not be blank. Example value:
   *     "NEXTUPLE"
   * @param targetProfitMarginRequest The request payload containing updated target profit margin
   *     details including attribute name, value and target gross profit margin.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with the updated target
   *     profit margin configuration.
   * @throws CommonServiceException If there is an error in processing the request or if the target
   *     profit margin configuration does not exist.
   */
  @PutMapping("/{orgId}")
  @UpdateTargetProfitMarginDoc
  public ResponseEntity<BaseResponse<TargetProfitMarginResponse>> updateTargetProfitMarginConfig(
      @PathVariable @Parameter(description = "Unique identifier for organization ID.") String orgId,
      @Valid @RequestBody TargetProfitMarginRequest targetProfitMarginRequest)
      throws CommonServiceException {
    log.debug(
        "Processing update gross profit target margin {} for orgId {},  attributeName {},  attribute value {} ",
        targetProfitMarginRequest.getTargetGrossProfitMargin(),
        orgId,
        targetProfitMarginRequest.getAttributeName(),
        targetProfitMarginRequest.getAttributeValue());
    return ResponseEntity.status(HttpStatus.OK)
        .body(
            BaseResponse.builder()
                .message("Target profit margin updated successfully")
                .payload(
                    recommendationRulesService.updateTargetProfitGrossMargin(
                        orgId, targetProfitMarginRequest))
                .build());
  }

  /**
   * Deletes target profit margin configurations for specified attributes in an organization.
   *
   * <p>This method processes a PUT request to delete target profit margin configurations for a
   * given organization based on the specified attribute name and values. The deletion is performed
   * using the provided {@link DeleteTargetProfitMarginRequest} which contains the attribute details
   * to be removed.
   *
   * @param orgId The unique identifier for the organization. Must not be blank. Example value:
   *     "NEXTUPLE"
   * @param request The {@link DeleteTargetProfitMarginRequest} containing the attribute name and
   *     values for which the target profit margins should be deleted.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with a success message
   *     indicating the target profit margins were deleted.
   * @throws CommonServiceException If there is an error in processing the request or if the target
   *     profit margin configurations do not exist.
   */
  @PutMapping(value = "/{orgId}/delete")
  @DeleteTargetProfitMarginDoc
  public ResponseEntity<BaseResponse<String>> deleteTargetProfitMargin(
      @PathVariable @Parameter(description = "Unique identifier for organization ID.") String orgId,
      @RequestBody DeleteTargetProfitMarginRequest request)
      throws CommonServiceException {
    log.debug(
        "Processing delete gross profit target margin for orgId: {},  attributeName: {},  attribute values: {} ",
        orgId,
        request.getAttributeName(),
        String.join(",", request.getAttributeValues()));
    recommendationRulesService.deleteTargetProfitMargin(orgId, request);
    return ResponseEntity.status(HttpStatus.OK)
        .body(
            BaseResponse.builder().message("Target profit margin(s) deleted successfully").build());
  }

  /**
   * Retrieves target profit margin configurations for a specific organization and attribute.
   *
   * <p>This method processes a GET request to fetch target profit margin configurations for a given
   * organization and attribute name. It returns all configured profit margins associated with the
   * specified attribute within the organization.
   *
   * @param orgId The unique identifier for the organization. Must not be blank. Example value:
   *     "NEXTUPLE"
   * @param attributeName The name of the attribute for which to fetch profit margins. This
   *     identifies the category of profit margins to retrieve.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with a list of {@link
   *     TargetProfitMarginResponse} objects representing the configured profit margins.
   * @throws CommonServiceException If there is an error in processing the request or if the
   *     configurations cannot be retrieved.
   */
  @GetMapping(value = "/{orgId}/{attributeName}")
  @FetchTargetProfitMarginDoc
  public ResponseEntity<BaseResponse<List<TargetProfitMarginResponse>>> fetchTargetProfitMargins(
      @PathVariable @Parameter(description = "Unique identifier for organization ID.") String orgId,
      @PathVariable @Parameter(description = "Attribute name of target profit margin")
          String attributeName)
      throws CommonServiceException {
    log.debug(
        "Processing fetch gross profit target margin for orgId: {},  attributeName: {}",
        orgId,
        attributeName);
    return ResponseEntity.status(HttpStatus.OK)
        .body(
            BaseResponse.builder()
                .message("Target profit margin fetched successfully")
                .payload(recommendationRulesService.fetchTargetProfitMargin(orgId, attributeName))
                .build());
  }

  /**
   * Retrieves attribute details for the specified organization.
   *
   * <p>This method processes a GET request to fetch all available attribute details and their
   * corresponding values for a given organization. These attributes are used in target profit
   * margin configurations.
   *
   * @param orgId The unique identifier for the organization. Must not be blank. Example value:
   *     "NEXTUPLE"
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with an {@link
   *     AttributeAndValuesTGMResponse} object representing the available attributes and their
   *     values.
   * @throws CommonServiceException If there is an error in processing the request or if the
   *     attribute details cannot be retrieved.
   */
  @GetMapping(value = "/fetch-attribute-details/{orgId}")
  @FetchAttributeDetailsDoc
  public ResponseEntity<BaseResponse<AttributeAndValuesTGMResponse>> fetchAttributeDetails(
      @PathVariable @Parameter(description = "Unique identifier for organization ID.") String orgId)
      throws CommonServiceException {
    log.debug("Processing fetch attribute details for orgId: {}", orgId);
    return ResponseEntity.status(HttpStatus.OK)
        .body(
            BaseResponse.builder()
                .message("Attribute fetched successfully")
                .payload(recommendationRulesService.fetchAttributeDetails(orgId))
                .build());
  }

  /**
   * Retrieves ship charge capping constants for the specified organization.
   *
   * <p>This method processes a GET request to fetch all ship charge capping constants configured
   * for a given organization. These constants are used to determine shipping charge limits and
   * thresholds.
   *
   * @param orgId The unique identifier for the organization. Must not be blank. Example value:
   *     "NEXTUPLE"
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with a {@link
   *     ShipChargeDetailsTGMResponse} object representing the configured ship charge capping
   *     constants.
   */
  @GetMapping(value = "/ship-charge-capping-constants/{orgId}")
  @FetchShipChargeDetailsDoc
  public ResponseEntity<BaseResponse<ShipChargeDetailsTGMResponse>> fetchShipChargeDetailsDetails(
      @PathVariable @Parameter(description = "Unique identifier for organization ID.")
          String orgId) {
    log.debug("Processing fetch ship charge details for orgId: {}", orgId);
    return ResponseEntity.status(HttpStatus.OK)
        .body(
            BaseResponse.builder()
                .message("Ship charge capping constants fetched successfully")
                .payload(recommendationRulesService.fetchShipChargeDetails(orgId))
                .build());
  }

  /**
   * Configures ship charge capping constants for the specified organization.
   *
   * <p>This method processes a POST request to configure ship charge capping constants for a given
   * organization. The configuration is performed using the provided {@link
   * ConfigureShipChargeCappingRequest} which contains the capping thresholds and limits.
   *
   * @param orgId The unique identifier for the organization. Must not be blank. Example value:
   *     "NEXTUPLE"
   * @param request The {@link ConfigureShipChargeCappingRequest} containing the ship charge capping
   *     configuration details.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with a {@link
   *     ConfigureShipChargeCappingResponse} object representing the configured ship charge capping
   *     constants.
   * @throws CommonServiceException If there is an error in processing the request or if the
   *     configuration cannot be applied.
   */
  @PostMapping(value = "/ship-charge-capping-constants/{orgId}")
  @ConfigureShipChargeCappingDoc
  public ResponseEntity<BaseResponse<ConfigureShipChargeCappingResponse>>
      configureShipChargeCapping(
          @PathVariable @Parameter(description = "Unique identifier for organization ID.")
              String orgId,
          @RequestBody ConfigureShipChargeCappingRequest request)
          throws CommonServiceException {
    log.debug("Processing configure ship charge details for orgId: {}", orgId);
    return ResponseEntity.status(HttpStatus.OK)
        .body(
            BaseResponse.builder()
                .message("Ship charge capping constants configured successfully")
                .payload(recommendationRulesService.configureShipChargeCapping(orgId, request))
                .build());
  }

  /**
   * Updates the ship charge capping status for the specified organization.
   *
   * <p>This method processes a PUT request to update the ship charge capping logic status for a
   * given organization. The status determines whether shipping charge capping logic should be
   * applied during calculations.
   *
   * @param orgId The unique identifier for the organization. Must not be blank. Example value:
   *     "NEXTUPLE"
   * @param isShipChargeCappingLogicEnabled Boolean flag indicating whether the shipping charge
   *     capping logic should be enabled or disabled. True enables the logic, false disables it.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with a success message
   *     indicating the status was updated.
   * @throws CommonServiceException If there is an error in processing the request or if the status
   *     cannot be updated.
   */
  @PutMapping(value = "/update-ship-charge-capping-status/{orgId}")
  @UpdateShipChargeCappingStatusDoc
  public ResponseEntity<BaseResponse<String>> updateShipChargeCappingStatus(
      @PathVariable @Parameter(description = "Unique identifier for organization ID.") String orgId,
      @RequestParam
          @Parameter(description = "Flag to describe about the shipping logic is applied or not.")
          Boolean isShipChargeCappingLogicEnabled)
      throws CommonServiceException {
    log.debug(
        "Processing updating ship charge capping status for orgId: {} & isShipChargeCappingLogicEnabled: {}",
        orgId,
        isShipChargeCappingLogicEnabled);
    recommendationRulesService.updateShipChargeCappingStatus(
        orgId, isShipChargeCappingLogicEnabled);
    return ResponseEntity.status(HttpStatus.OK)
        .body(
            BaseResponse.builder()
                .message("Ship charge capping logic status updated successfully")
                .build());
  }
}
