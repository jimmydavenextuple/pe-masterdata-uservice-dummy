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
import com.nextuple.sourcing.cost.config.dto.PreferenceSelectorCacheKeyDto;
import com.nextuple.sourcing.cost.config.dto.PreferenceSelectorDto;
import com.nextuple.sourcing.cost.config.inbound.CreatePreferenceSelectorRequest;
import com.nextuple.sourcing.cost.config.inbound.UpdatePreferenceSelectorRequest;
import com.nextuple.sourcing.cost.config.service.PreferenceSelectorService;
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
 * Controller for managing tenant preference selectors within an organization.
 *
 * <p>This controller handles operations such as creating, updating, fetching, and deleting
 * preference selectors for cost types and selectors within a given organization. It also includes
 * functionality to retrieve cache keys associated with preference selectors.
 *
 * <p>The controller is tagged with "Tenant Preference for Selector APIs" for easy categorization in
 * API documentation.
 */
@Validated
@RestController
@RequestMapping("/cost-config/selector")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Tenant Preference for Selector APIs")
public class PreferenceSelectorController {

  private static final Logger logger = LoggerFactory.getLogger(PreferenceSelectorController.class);

  private final PreferenceSelectorService preferenceSelectorService;

  /**
   * Creates a new preference selector for the specified organization.
   *
   * <p>This method processes a POST request to create a new preference selector based on the
   * provided details and organization identifier.
   *
   * @param orgId The unique identifier of the organization (e.g., "NEXTUPLE_GR").
   * @param createPreferenceSelectorRequest The request payload containing the details for creating
   *     the preference selector.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with the created preference
   *     selector's data.
   * @throws CommonServiceException If there is an error during the creation process.
   */
  @CreatePreferenceSelector
  @PostMapping(value = "/{orgId}")
  public ResponseEntity<BaseResponse<PreferenceSelectorDto>> createPreferenceSelector(
      @NotBlank(message = "Unique identifier for organisation can't be empty")
          @PathVariable
          @Parameter(
              description = "Unique identifier of the organization.",
              example = "NEXTUPLE_GR")
          String orgId,
      @Valid @RequestBody CreatePreferenceSelectorRequest createPreferenceSelectorRequest)
      throws CommonServiceException {
    log.debug("Processing create Preference Selector request");
    var preferenceSelectorResponse =
        preferenceSelectorService.createPreferenceSelector(orgId, createPreferenceSelectorRequest);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(
            BaseResponse.builder()
                .message("Preference Selector created successfully!")
                .payload(preferenceSelectorResponse)
                .build());
  }

  /**
   * Retrieves a preference selector by organization ID and selector ID.
   *
   * <p>This method processes a GET request to fetch a specific preference selector based on the
   * provided organization ID and selector ID.
   *
   * @param orgId The unique identifier of the organization (e.g., "NEXTUPLE_GR").
   * @param selectorId The unique identifier of the tenant preference for the selector.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with the retrieved
   *     preference selector's data.
   * @throws CommonServiceException If there is an error during the fetch process.
   */
  @GetPreferenceSelectorByOrgIdAndSelectorId
  @GetMapping(value = "/{orgId}/{selectorId}")
  public ResponseEntity<BaseResponse<PreferenceSelectorDto>>
      getPreferenceSelectorByOrgIdAndSelectorId(
          @NotBlank(message = "Unique identifier for organisation can't be empty")
              @PathVariable
              @Parameter(
                  description = "Unique identifier of the organization.",
                  example = "NEXTUPLE_GR")
              String orgId,
          @NotNull(message = "Unique identifier for tenant preference for selector can't be null")
              @PathVariable
              @Parameter(
                  description = "Unique identifier for tenant preference for selector.",
                  example = "1")
              Long selectorId)
          throws CommonServiceException {
    log.debug("Processing get tenant preference for selector by org id and id request");
    var preferenceSelectorResponse =
        preferenceSelectorService.findByOrgIdAndPreferenceSelectorId(orgId, selectorId);
    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Preference Selector fetched successfully!")
            .payload(preferenceSelectorResponse)
            .build());
  }

  /**
   * Retrieves a preference selector by organization ID and cost type.
   *
   * <p>This method processes a GET request to fetch a specific preference selector based on the
   * provided organization ID and cost type.
   *
   * @param orgId The unique identifier of the organization (e.g., "NEXTUPLE_GR").
   * @param costType The type of cost (e.g., "SHIPPING_COST").
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with the retrieved
   *     preference selector's data.
   * @throws CommonServiceException If there is an error during the fetch process.
   */
  @GetPreferenceSelectorByOrgIdAndCostTypeDoc
  @GetMapping(value = "/{orgId}/costType/{costType}")
  public ResponseEntity<BaseResponse<PreferenceSelectorDto>>
      getPreferenceSelectorByOrgIdAndCostType(
          @NotBlank(message = "Unique identifier for organisation can't be empty")
              @PathVariable
              @Parameter(
                  description = "Unique identifier of the organization.",
                  example = "NEXTUPLE_GR")
              String orgId,
          @NotBlank(message = "Cost type can't be empty")
              @PathVariable
              @Parameter(description = "Specifies the cost type.", example = "SHIPPING_COST")
              String costType)
          throws CommonServiceException {
    log.debug("Processing get tenant preference for selector by org id and cost type  request");
    var preferenceSelectorResponse =
        preferenceSelectorService.findByOrgIdAndPreferenceCostType(orgId, costType);
    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Preference Selector fetched successfully!")
            .payload(preferenceSelectorResponse)
            .build());
  }

  /**
   * Updates an existing preference selector for the specified organization and selector ID.
   *
   * <p>This method processes a PUT request to update an existing preference selector.
   *
   * @param orgId The unique identifier of the organization (e.g., "NEXTUPLE_GR").
   * @param selectorId The unique identifier of the tenant preference for the selector.
   * @param updateCreatePreferenceSelectorRequest The request payload containing the updated
   *     preference selector details.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with the updated preference
   *     selector's data.
   * @throws CommonServiceException If there is an error during the update process.
   */
  @UpdatePreferenceSelector
  @PutMapping(value = "/{orgId}/{selectorId}")
  public ResponseEntity<BaseResponse<PreferenceSelectorDto>> updatePreferenceSelector(
      @NotBlank(message = "Unique identifier for organisation can't be empty")
          @PathVariable
          @Parameter(
              description = "Unique identifier of the organization.",
              example = "NEXTUPLE_GR")
          String orgId,
      @NotNull(message = "Unique identifier for tenant preference for selector can't be null")
          @PathVariable
          @Parameter(
              description = "Unique identifier for tenant preference for selector.",
              example = "1")
          Long selectorId,
      @Valid @RequestBody UpdatePreferenceSelectorRequest updateCreatePreferenceSelectorRequest)
      throws CommonServiceException {
    log.debug("Processing update tenant preference for selector request");
    var preferenceSelectorResponse =
        preferenceSelectorService.updatePreferenceSelector(
            selectorId, orgId, updateCreatePreferenceSelectorRequest);
    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Preference Selector updated successfully!")
            .payload(preferenceSelectorResponse)
            .build());
  }

  /**
   * Deletes a preference selector for the specified organization and selector ID.
   *
   * <p>This method processes a DELETE request to remove a specific preference selector based on the
   * provided organization ID and selector ID.
   *
   * @param orgId The unique identifier of the organization (e.g., "NEXTUPLE_GR").
   * @param selectorId The unique identifier of the tenant preference for the selector.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} confirming the deletion.
   * @throws CommonServiceException If there is an error during the deletion process.
   */
  @DeletePreferenceSelector
  @DeleteMapping(value = "/{orgId}/{selectorId}")
  public ResponseEntity<BaseResponse<PreferenceSelectorDto>> deletePreferenceSelector(
      @NotBlank(message = "Unique identifier for organisation can't be empty")
          @PathVariable
          @Parameter(
              description = "Unique identifier of the organization.",
              example = "NEXTUPLE_GR")
          String orgId,
      @NotNull(message = "Unique identifier for tenant preference for selector can't be null")
          @PathVariable
          @Parameter(
              description = "Unique identifier for tenant preference for selector.",
              example = "1")
          Long selectorId)
      throws CommonServiceException {
    log.debug("Processing delete tenant preference for selector request");
    var preferenceSelectorResponse =
        preferenceSelectorService.deletePreferenceSelector(selectorId, orgId);
    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Preference Selector deleted successfully!")
            .payload(preferenceSelectorResponse)
            .build());
  }

  /**
   * Retrieves all preference selector cache keys with an optional limit.
   *
   * <p>This method processes a GET request to fetch all cache keys related to preference selectors,
   * with an optional parameter for limiting the number of rows returned.
   *
   * @param limit The maximum number of rows to be returned from the database (default is 100).
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with the list of preference
   *     selector cache keys.
   */
  @GetPreferenceSelectorCacheKeysDoc
  @GetMapping("/get-all-cache-keys")
  public ResponseEntity<BaseResponse<List<PreferenceSelectorCacheKeyDto>>>
      getPreferenceSelectorCacheKeys(
          @RequestParam(defaultValue = "100")
              @Parameter(
                  description = "Specifies the number of rows to be returned from the DB.",
                  example = "1")
              Integer limit) {
    logger.debug("Processing get Preference Selector Cache Keys");
    var response = preferenceSelectorService.getAllPreferenceSelectorCacheKeys(limit);
    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Preference Selector Keys fetched successfully")
            .payload(response)
            .build());
  }
}
