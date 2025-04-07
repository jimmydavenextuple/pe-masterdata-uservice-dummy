/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */
package com.nextuple.sourcing.cost.config.controller;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.sourcing.cost.config.controller.docs.CreateSelectorAndCostItineraryMappingDoc;
import com.nextuple.sourcing.cost.config.controller.docs.DeleteSelectorAndCostItineraryMappingDoc;
import com.nextuple.sourcing.cost.config.controller.docs.GetSelectorAndCostItineraryByOrgIdSelectorCfAndCostTypeDoc;
import com.nextuple.sourcing.cost.config.controller.docs.GetSelectorAndCostItineraryMappingCacheKeysDoc;
import com.nextuple.sourcing.cost.config.controller.docs.GetSelectorAndCostItineraryMappingDoc;
import com.nextuple.sourcing.cost.config.controller.docs.UpdateSelectorAndCostItineraryMappingDoc;
import com.nextuple.sourcing.cost.config.dto.SelectorAndCostItineraryMappingCacheKeyDto;
import com.nextuple.sourcing.cost.config.inbound.SelectorAndCostItineraryMappingRequest;
import com.nextuple.sourcing.cost.config.inbound.UpdateSelectorAndCostItineraryMappingRequest;
import com.nextuple.sourcing.cost.config.outbound.SelectorAndCostItineraryMappingResponse;
import com.nextuple.sourcing.cost.config.service.SelectorAndCostItineraryMappingService;
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

/**
 * Controller for managing selector and cost itinerary mappings for a specific organization.
 *
 * <p>This controller provides various endpoints for creating, retrieving, updating, and deleting
 * selector and cost itinerary mappings. It also includes functionality for retrieving cache keys
 * associated with these mappings.
 *
 * <p>The controller is tagged with "Selector and Cost Itinerary Mapping APIs" for easy
 * categorization in API documentation.
 */
@Slf4j
@RestController
@RequestMapping("/cost-config/selector-itinerary-mapping")
@RequiredArgsConstructor
@Tag(name = "Selector and Cost Itinerary Mapping  APIs")
public class SelectorAndCostItineraryMappingController {

  private static final Logger logger =
      LoggerFactory.getLogger(SelectorAndCostItineraryMappingController.class);

  private final SelectorAndCostItineraryMappingService selectorAndCostItineraryMappingService;

  /**
   * Creates a new selector and cost itinerary mapping for the specified organization.
   *
   * <p>This method processes a POST request to create a new mapping based on the provided
   * organization identifier and the selector and cost itinerary mapping request.
   *
   * @param orgId The unique identifier of the organization (e.g., "NEXTUPLE_GR").
   * @param selectorAndCostItineraryMappingRequest The request payload containing the details for
   *     creating the mapping.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with the created mapping
   *     data.
   * @throws CommonServiceException If there is an error during the creation process.
   */
  @CreateSelectorAndCostItineraryMappingDoc
  @PostMapping(
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE,
      value = "/{orgId}")
  public ResponseEntity<BaseResponse<SelectorAndCostItineraryMappingResponse>>
      createSelectorAndCostItineraryMapping(
          @NotNull(message = "OrgId cannot be null")
              @PathVariable
              @Parameter(
                  description = "Unique identifier of the organization.",
                  example = "NEXTUPLE_GR")
              String orgId,
          @Valid @RequestBody
              SelectorAndCostItineraryMappingRequest selectorAndCostItineraryMappingRequest)
          throws CommonServiceException {

    return ResponseEntity.status(HttpStatus.CREATED)
        .body(
            BaseResponse.builder()
                .message("Selector and Cost Itinerary Mapping created successfully.")
                .payload(
                    selectorAndCostItineraryMappingService.createSelectorAndCostItineraryMapping(
                        orgId, selectorAndCostItineraryMappingRequest))
                .build());
  }

  /**
   * Retrieves a selector and cost itinerary mapping by organization ID and mapping ID.
   *
   * <p>This method processes a GET request to fetch a specific selector and cost itinerary mapping
   * based on the provided organization ID and mapping ID.
   *
   * @param orgId The unique identifier of the organization (e.g., "NEXTUPLE_GR").
   * @param id The unique identifier of the selector and cost itinerary mapping.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with the fetched mapping
   *     data.
   * @throws CommonServiceException If there is an error during the fetch process.
   */
  @GetSelectorAndCostItineraryMappingDoc
  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE, value = "/{orgId}/{id}")
  public ResponseEntity<BaseResponse<SelectorAndCostItineraryMappingResponse>>
      getSelectorAndCostItineraryMapping(
          @NotNull(message = "OrgId cannot be null")
              @PathVariable
              @Parameter(
                  description = "Unique identifier of the organization.",
                  example = "NEXTUPLE_GR")
              String orgId,
          @NotNull(message = "Id cannot be null")
              @PathVariable
              @Parameter(
                  description = "Unique identifier for selector and cost itinerary mapping.",
                  example = "1")
              Long id)
          throws CommonServiceException {

    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Selector and Cost Itinerary Mapping fetched successfully.")
            .payload(
                selectorAndCostItineraryMappingService.getSelectorAndCostItineraryMapping(
                    orgId, id))
            .build());
  }

  /**
   * Retrieves a list of selector and cost itinerary mappings by organization ID, cost type, and
   * optional cost factor.
   *
   * <p>This method processes a GET request to fetch mappings based on the provided organization ID,
   * cost type, and optionally, a cost factor selector.
   *
   * @param orgId The unique identifier of the organization (e.g., "NEXTUPLE_GR").
   * @param costType The cost type (e.g., "SHIPPING_COST").
   * @param selectorCf The value of the selected cost factor (optional).
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with a list of mappings.
   * @throws CommonServiceException If there is an error during the fetch process.
   */
  @GetSelectorAndCostItineraryByOrgIdSelectorCfAndCostTypeDoc
  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE, value = "/{orgId}/costType/{costType}")
  public ResponseEntity<BaseResponse<List<SelectorAndCostItineraryMappingResponse>>>
      getSelectorAndCostItineraryMappingByOrgIdSelectorCfAndCostType(
          @NotNull(message = "OrgId cannot be null")
              @PathVariable
              @Parameter(
                  description = "Unique identifier of the organization.",
                  example = "NEXTUPLE_GR")
              String orgId,
          @NotBlank(message = "Cost type can't be empty")
              @PathVariable
              @Parameter(description = "Specifies the cost type.", example = "SHIPPING_COST")
              String costType,
          @RequestParam(required = false)
              @Parameter(description = "Value of selected cost factor.", example = "UPS_GROUND")
              String selectorCf)
          throws CommonServiceException {

    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Selector and Cost Itinerary Mapping fetched successfully.")
            .payload(
                selectorAndCostItineraryMappingService
                    .getSelectorAndCostItineraryMappingByOrgIdSelectorCfAndCostType(
                        orgId, selectorCf, costType))
            .build());
  }

  /**
   * Deletes a selector and cost itinerary mapping for the specified organization and mapping ID.
   *
   * <p>This method processes a DELETE request to remove a specific mapping based on the provided
   * organization ID and mapping ID.
   *
   * @param orgId The unique identifier of the organization (e.g., "NEXTUPLE_GR").
   * @param id The unique identifier of the selector and cost itinerary mapping.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} confirming the deletion.
   * @throws CommonServiceException If there is an error during the deletion process.
   */
  @DeleteSelectorAndCostItineraryMappingDoc
  @DeleteMapping(produces = MediaType.APPLICATION_JSON_VALUE, value = "{orgId}/{id}")
  public ResponseEntity<BaseResponse<SelectorAndCostItineraryMappingResponse>>
      deleteSelectorAndCostItineraryMapping(
          @NotBlank(message = "Unique identifier for organisation can't be empty")
              @PathVariable
              @Parameter(
                  description = "Unique identifier of the organization.",
                  example = "NEXTUPLE_GR")
              String orgId,
          @NotNull(
                  message =
                      "Unique identifier for selector and cost itinerary mapping can't be null")
              @PathVariable
              @Parameter(
                  description = "Unique identifier for selector and cost itinerary",
                  example = "1")
              Long id)
          throws CommonServiceException {
    log.info("Processing delete selector and cost itinerary mapping request");
    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Selector and Cost Itinerary Mapping deleted successfully.")
            .payload(
                selectorAndCostItineraryMappingService.deleteSelectorAndCostItineraryMapping(
                    orgId, id))
            .build());
  }

  /**
   * Retrieves all selector and cost itinerary mapping cache keys with an optional limit.
   *
   * <p>This method processes a GET request to fetch all cache keys related to selector and cost
   * itinerary mappings, with an optional parameter for limiting the number of rows returned.
   *
   * @param limit The maximum number of rows to be returned from the database (default is 100).
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with the list of cache keys.
   */
  @GetSelectorAndCostItineraryMappingCacheKeysDoc
  @GetMapping("/get-all-cache-keys")
  public ResponseEntity<BaseResponse<List<SelectorAndCostItineraryMappingCacheKeyDto>>>
      getSelectorAndCostItineraryCacheKeys(@RequestParam(defaultValue = "100") Integer limit) {
    logger.debug("Processing get Selector and Cost Itinerary Cache Keys");
    var response =
        selectorAndCostItineraryMappingService.getAllSelectorAndCostItineraryCacheKeys(limit);
    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Selector and Cost Itinerary Keys fetched successfully")
            .payload(response)
            .build());
  }

  /**
   * Updates an existing selector and cost itinerary mapping for the specified organization and
   * mapping ID.
   *
   * <p>This method processes a PUT request to update an existing mapping based on the provided
   * organization ID, mapping ID, and the request payload containing the updated details.
   *
   * @param orgId The unique identifier of the organization (e.g., "NEXTUPLE_GR").
   * @param id The unique identifier of the selector and cost itinerary mapping.
   * @param updateSelectorAndCostItineraryMappingRequest The request payload containing the updated
   *     mapping details.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with the updated mapping
   *     data.
   * @throws CommonServiceException If there is an error during the update process.
   */
  @UpdateSelectorAndCostItineraryMappingDoc
  @PutMapping("/{orgId}/{id}")
  public ResponseEntity<BaseResponse<SelectorAndCostItineraryMappingResponse>>
      updateSelectorAndCostItineraryMappingByIdAndOrgId(
          @NotNull(message = "OrgId cannot be null")
              @PathVariable
              @Parameter(
                  description = "Unique identifier of the organization.",
                  example = "NEXTUPLE_GR")
              String orgId,
          @NotNull(message = "Unique identifier for selector and cost itinerary can't be null")
              @PathVariable
              @Parameter(
                  description = "Unique identifier for selector and cost itinerary",
                  example = "1")
              Long id,
          @Valid @RequestBody
              UpdateSelectorAndCostItineraryMappingRequest
                  updateSelectorAndCostItineraryMappingRequest)
          throws CommonServiceException {

    log.debug("Processing update selector and cost itinerary request for orgId: {}", orgId);
    var selectorAndCostItineraryMappingResponse =
        selectorAndCostItineraryMappingService.updateSelectorAndCostItineraryMappingByIdAndOrgId(
            id, orgId, updateSelectorAndCostItineraryMappingRequest);
    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Selector and cost itinerary updated successfully!")
            .payload(selectorAndCostItineraryMappingResponse)
            .build());
  }
}
