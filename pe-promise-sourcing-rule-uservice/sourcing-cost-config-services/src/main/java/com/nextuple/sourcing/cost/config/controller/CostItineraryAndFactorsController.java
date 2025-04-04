/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.sourcing.cost.config.controller;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.sourcing.cost.config.controller.docs.CreateCostItineraryAndFactors;
import com.nextuple.sourcing.cost.config.controller.docs.DeleteCostItineraryAndFactors;
import com.nextuple.sourcing.cost.config.controller.docs.GetCostItineraryAndFactorsByOrgIdAndId;
import com.nextuple.sourcing.cost.config.controller.docs.GetCostItineraryAndFactorsByOrgIdCostItineraryItineraryStatusAndIsActiveDoc;
import com.nextuple.sourcing.cost.config.controller.docs.GetCostItineraryAndFactorsCacheKeyDoc;
import com.nextuple.sourcing.cost.config.controller.docs.UpdateCostItineraryAndFactors;
import com.nextuple.sourcing.cost.config.controller.docs.UpdateCostItineraryAndFactorsActiveStatusByCostItinerary;
import com.nextuple.sourcing.cost.config.controller.docs.UpdateCostItineraryAndFactorsStatus;
import com.nextuple.sourcing.cost.config.controller.docs.UpdateCostItineraryAndFactorsStatusByCostItinerary;
import com.nextuple.sourcing.cost.config.dto.CostItineraryAndFactorsCacheKeyDto;
import com.nextuple.sourcing.cost.config.dto.CostItineraryAndFactorsDto;
import com.nextuple.sourcing.cost.config.enums.ItineraryStatusEnum;
import com.nextuple.sourcing.cost.config.inbound.CostItineraryAndFactorsRequest;
import com.nextuple.sourcing.cost.config.service.CostItineraryAndFactorsService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
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

/**
 * Controller for managing Cost Itinerary and Cost Factors Mappings within an organization.
 *
 * <p>This controller provides APIs to create, retrieve, update, and delete mappings of cost
 * itineraries and factors for a specific organization. It also supports updating the status and
 * active status of these mappings, and allows fetching cache keys related to them.
 *
 * <p>The controller is tagged with "Cost Itinerary &amp; Cost Factors Mapping APIs" for easy
 * categorization in API documentation.
 */
@Validated
@RestController
@RequestMapping("/cost-config/cost-itinerary-factors-mapping")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Cost Itinerary & Cost Factors Mapping APIs")
public class CostItineraryAndFactorsController {
  private static final Logger logger =
      LoggerFactory.getLogger(CostItineraryAndFactorsController.class);
  private final CostItineraryAndFactorsService costItineraryAndFactorsService;

  /**
   * Creates a new Cost Itinerary and Cost Factors Mapping.
   *
   * <p>This method processes a POST request to create a mapping of cost itineraries and factors for
   * a given organization.
   *
   * @param orgId The unique identifier of the organization for which the mapping is to be created.
   * @param costItineraryAndFactorsRequest The request payload containing details of the cost
   *     itinerary and factors to be created.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with the created mapping
   *     details.
   * @throws CommonServiceException If an error occurs during the creation process.
   */
  @CreateCostItineraryAndFactors
  @PostMapping(value = "/{orgId}")
  public ResponseEntity<BaseResponse<CostItineraryAndFactorsDto>> createCostItineraryAndFactors(
      @NotBlank(message = "Unique identifier for organisation can't be empty")
          @PathVariable
          @Parameter(description = "Unique identifier of the organization.", example = "NEXTUPLE")
          String orgId,
      @Valid @RequestBody CostItineraryAndFactorsRequest costItineraryAndFactorsRequest)
      throws CommonServiceException {
    log.debug("Processing create Cost Itinerary & Cost Factors Mapping request");
    var costItineraryAndFactorsResponse =
        costItineraryAndFactorsService.createCostItineraryAndFactors(
            orgId, costItineraryAndFactorsRequest);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(
            BaseResponse.builder()
                .message("Cost Itinerary & Cost Factors Mapping created successfully!")
                .payload(costItineraryAndFactorsResponse)
                .build());
  }

  /**
   * Retrieves the Cost Itinerary and Cost Factors Mapping by Organization ID and Mapping ID.
   *
   * <p>This method processes a GET request to fetch the details of a cost itinerary and cost
   * factors mapping for a specific organization and mapping identifier.
   *
   * @param orgId The unique identifier of the organization.
   * @param id The unique identifier of the cost itinerary and cost factors mapping.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with the mapping details.
   * @throws CommonServiceException If the mapping cannot be found or retrieved.
   */
  @GetCostItineraryAndFactorsByOrgIdAndId
  @GetMapping(value = "/{orgId}/{id}")
  public ResponseEntity<BaseResponse<CostItineraryAndFactorsDto>>
      getCostItineraryAndFactorsByOrgIdAndId(
          @NotBlank(message = "Unique identifier for the organisation. can't be empty")
              @PathVariable
              @Parameter(
                  description = "Unique identifier of the organization.",
                  example = "NEXTUPLE")
              String orgId,
          @NotNull(
                  message =
                      "Unique identifier for Cost Itinerary & Cost Factors Mapping can't be null")
              @PathVariable
              @Parameter(
                  description = "Unique identifier for Cost Itinerary & Cost Factors Mapping",
                  example = "1")
              Long id)
          throws CommonServiceException {
    log.debug("Processing get Cost Itinerary & Cost Factors Mapping by org id and id request");
    var costItineraryAndFactorsResponse =
        costItineraryAndFactorsService.findCostItineraryAndFactorsByOrgIdAndId(orgId, id);
    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Cost Itinerary & Cost Factors fetched successfully!")
            .payload(costItineraryAndFactorsResponse)
            .build());
  }

  /**
   * Retrieves Cost Itinerary and Factors Mapping by Organization ID, Cost Itinerary, Itinerary
   * Status, and Active Status.
   *
   * <p>This method processes a GET request to fetch cost itinerary and cost factors mappings based
   * on the organization ID, cost itinerary, itinerary status, and active status.
   *
   * @param orgId The unique identifier of the organization.
   * @param costItinerary The specific cost itinerary to determine the mapping.
   * @param itineraryStatus The status of the itinerary (e.g., DRAFT, CREATED).
   * @param isActive The active status of the itinerary (true or false).
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with the retrieved mapping
   *     details.
   * @throws CommonServiceException If no mapping matches the specified parameters.
   */
  @GetCostItineraryAndFactorsByOrgIdCostItineraryItineraryStatusAndIsActiveDoc
  @GetMapping(value = "{orgId}/{costItinerary}/{itineraryStatus}/{isActive}")
  public ResponseEntity<BaseResponse<CostItineraryAndFactorsDto>>
      getCostItineraryAndFactorsByOrgIdCostItineraryItineraryStatusAndIsActive(
          @NotBlank(message = "Unique identifier for organisation can't be empty")
              @PathVariable
              @Parameter(
                  description = "Unique identifier of the organization.",
                  example = "NEXTUPLE")
              String orgId,
          @NotBlank(message = "Cost itinerary to determine the cost can't be empty")
              @PathVariable
              @Parameter(
                  description = "Cost itinerary to determine the cost",
                  example = "SHIPPING_COST_UPSLIKE")
              String costItinerary,
          @NotNull(message = "Itinerary status can't be empty")
              @PathVariable
              @Parameter(description = "Itinerary status", example = "DRAFT/CREATED")
              ItineraryStatusEnum itineraryStatus,
          @NotNull(message = "IsActive can't be empty")
              @PathVariable
              @Parameter(description = "Active status of the itinerary", example = "true/false")
              Boolean isActive)
          throws CommonServiceException {
    log.debug("Processing get Cost Itinerary & Cost Factors Mapping by org id and id request");
    var costItineraryAndFactorsResponse =
        costItineraryAndFactorsService
            .findCostItineraryAndFactorsByOrgIdCostItineraryItineraryStatusAndIsActive(
                orgId, costItinerary, itineraryStatus, isActive);
    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Cost Itinerary & Cost Factors fetched successfully!")
            .payload(costItineraryAndFactorsResponse)
            .build());
  }

  /**
   * Updates an existing Cost Itinerary and Cost Factors Mapping.
   *
   * <p>This method processes a PUT request to update the details of an existing cost itinerary and
   * factors mapping for a given organization and mapping ID.
   *
   * @param orgId The unique identifier of the organization.
   * @param id The unique identifier of the cost itinerary and cost factors mapping.
   * @param updateCostItineraryAndFactorsRequest The request payload containing updated details of
   *     the mapping.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with the updated mapping
   *     details.
   * @throws CommonServiceException If an error occurs during the update process.
   */
  @UpdateCostItineraryAndFactors
  @PutMapping(value = "/{orgId}/{id}")
  public ResponseEntity<BaseResponse<CostItineraryAndFactorsDto>> updateCostItineraryAndFactors(
      @NotBlank(message = "Unique identifier for organisation can't be empty")
          @PathVariable
          @Parameter(description = "Unique identifier of the organization.", example = "NEXTUPLE")
          String orgId,
      @NotNull(
              message = "Unique identifier for Cost Itinerary & Cost Factors Mapping can't be null")
          @Min(value = 0)
          @PathVariable
          @Parameter(
              description = "Unique identifier for Cost Itinerary & Cost Factors Mapping",
              example = "1")
          Long id,
      @Valid @RequestBody CostItineraryAndFactorsRequest updateCostItineraryAndFactorsRequest)
      throws CommonServiceException {
    log.debug("Processing update Cost Itinerary & Cost Factors request");
    var costItineraryAndFactorsResponse =
        costItineraryAndFactorsService.updateCostItineraryAndFactors(
            id, orgId, updateCostItineraryAndFactorsRequest);
    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Cost Itinerary & Cost Factors details updated successfully!")
            .payload(costItineraryAndFactorsResponse)
            .build());
  }

  /**
   * Updates the status of an existing Cost Itinerary and Cost Factors Mapping.
   *
   * <p>This method processes a PUT request to update the status of a cost itinerary and cost
   * factors mapping for a given organization and mapping ID.
   *
   * @param orgId The unique identifier of the organization.
   * @param id The unique identifier of the cost itinerary and cost factors mapping.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with the updated status.
   * @throws CommonServiceException If an error occurs during the update process.
   */
  @UpdateCostItineraryAndFactorsStatus
  @PutMapping(value = "itinerary-status/{orgId}/{id}")
  public ResponseEntity<BaseResponse<CostItineraryAndFactorsDto>>
      updateCostItineraryAndFactorsStatus(
          @NotBlank(
                  message =
                      "Unique identifier for Cost Itinerary & Cost Factors Mapping can't be null")
              @PathVariable
              @Parameter(
                  description = "Unique identifier of the organization.",
                  example = "NEXTUPLE")
              String orgId,
          @NotNull(
                  message =
                      "Unique identifier for Cost Itinerary & Cost Factors Mapping can't be null")
              @PathVariable
              @Parameter(
                  description = "Unique identifier for Cost Itinerary & Cost Factors",
                  example = "1")
              Long id)
          throws CommonServiceException {
    log.debug("Processing update Cost Itinerary & Cost Factors status request");
    var costItineraryAndFactorsResponse =
        costItineraryAndFactorsService.updateCostItineraryAndFactorsStatus(id, orgId);
    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Cost Itinerary & Cost Factors itinerary status updated successfully!")
            .payload(costItineraryAndFactorsResponse)
            .build());
  }

  /**
   * Updates the status of a Cost Itinerary and Factors Mapping by Cost Itinerary.
   *
   * <p>This method processes a PUT request to update the status of a cost itinerary and factors
   * mapping for a specific cost itinerary within an organization.
   *
   * @param orgId The unique identifier of the organization.
   * @param costItinerary The specific cost itinerary to update.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with the updated status
   *     details.
   * @throws CommonServiceException If an error occurs during the update process.
   */
  @Hidden
  @UpdateCostItineraryAndFactorsStatusByCostItinerary
  @PutMapping(value = "/itinerary-status/orgId/{orgId}/costItinerary/{costItinerary}")
  public ResponseEntity<BaseResponse<CostItineraryAndFactorsDto>>
      updateCostItineraryAndFactorsStatusByCostItinerary(
          @NotBlank(
                  message =
                      "Unique identifier for Cost Itinerary & Cost Factors Mapping can't be null")
              @PathVariable
              @Parameter(
                  description = "Unique identifier for the organization.",
                  example = "NEXTUPLE")
              String orgId,
          @NotBlank(
                  message =
                      "Cost Itinerary for Cost Itinerary & Cost Factors Mapping can't be null")
              @PathVariable
              @Parameter(
                  description = "Cost Itinerary for Cost Itinerary & Cost Factors",
                  example = "COST_TYPE_ITINERARY")
              String costItinerary)
          throws CommonServiceException {
    log.debug("Processing update Cost Itinerary & Cost Factors status request by cost itinerary");
    var costItineraryAndFactorsResponse =
        costItineraryAndFactorsService.updateCostItineraryAndFactorsStatusByCostItinerary(
            costItinerary, orgId);
    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Cost Itinerary & Cost Factors itinerary status updated successfully!")
            .payload(costItineraryAndFactorsResponse)
            .build());
  }

  /**
   * Updates the active status of a Cost Itinerary and Factors Mapping by Cost Itinerary.
   *
   * <p>This method processes a PUT request to update the active status of a cost itinerary and
   * factors mapping for a specific cost itinerary within an organization.
   *
   * @param orgId The unique identifier of the organization.
   * @param costItinerary The specific cost itinerary to update.
   * @param isActive The new active status to be set (true or false).
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with the updated active
   *     status details.
   * @throws CommonServiceException If an error occurs during the update process.
   */
  @UpdateCostItineraryAndFactorsActiveStatusByCostItinerary
  @PutMapping(
      value = "/itinerary-active/orgId/{orgId}/costItinerary/{costItinerary}/isActive/{isActive}")
  public ResponseEntity<BaseResponse<CostItineraryAndFactorsDto>>
      updateCostItineraryAndFactorsActiveStatusByCostItinerary(
          @NotBlank(
                  message =
                      "Unique identifier for Cost Itinerary & Cost Factors Mapping can't be null")
              @PathVariable
              @Parameter(
                  description = "Unique identifier for the organization.",
                  example = "NEXTUPLE")
              String orgId,
          @NotBlank(
                  message =
                      "Cost Itinerary for Cost Itinerary & Cost Factors Mapping can't be null")
              @PathVariable
              @Parameter(
                  description = "Cost Itinerary for Cost Itinerary & Cost Factors",
                  example = "COST_TYPE_ITINERARY")
              String costItinerary,
          @NotNull(
                  message = "Active status for Cost Itinerary & Cost Factors Mapping can't be null")
              @PathVariable
              @Parameter(description = "Cost Itinerary active status", example = "true")
              boolean isActive)
          throws CommonServiceException {
    log.debug("Processing update Cost Itinerary & Cost Factors status request by cost itinerary");
    var costItineraryAndFactorsResponse =
        costItineraryAndFactorsService.updateCostItineraryAndFactorsActiveStatusByCostItinerary(
            costItinerary, orgId, isActive);
    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Cost Itinerary & Cost Factors active status updated successfully!")
            .payload(costItineraryAndFactorsResponse)
            .build());
  }

  /**
   * Deletes a Cost Itinerary and Cost Factors Mapping.
   *
   * <p>This method processes a DELETE request to remove a specific cost itinerary and cost factors
   * mapping based on the organization ID and mapping ID.
   *
   * @param orgId The unique identifier of the organization.
   * @param id The unique identifier of the cost itinerary and cost factors mapping.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with details of the deleted
   *     mapping.
   * @throws CommonServiceException If the mapping cannot be deleted or found.
   */
  @DeleteCostItineraryAndFactors
  @DeleteMapping(value = "/{orgId}/{id}")
  public ResponseEntity<BaseResponse<CostItineraryAndFactorsDto>> deleteCostItineraryAndFactors(
      @NotBlank(
              message = "Unique identifier for Cost Itinerary & Cost Factors Mapping can't be null")
          @PathVariable
          @Parameter(description = "Unique identifier for the organization.", example = "NEXTUPLE")
          String orgId,
      @NotNull(
              message = "Unique identifier for Cost Itinerary & Cost Factors Mapping can't be null")
          @PathVariable
          @Parameter(
              description = "Unique identifier for Cost Itinerary & Cost Factors",
              example = "1")
          Long id)
      throws CommonServiceException {
    log.debug("Processing delete Cost Itinerary & Cost Factors Mapping request");
    var costItineraryAndFactorsResponse =
        costItineraryAndFactorsService.deleteCostItineraryAndFactors(orgId, id);
    return ResponseEntity.status(HttpStatus.OK)
        .body(
            BaseResponse.builder()
                .message("Cost Itinerary & Cost Factors details deleted successfully!")
                .payload(costItineraryAndFactorsResponse)
                .build());
  }

  /**
   * Retrieves Cache Keys for Cost Itinerary and Factors Mappings.
   *
   * <p>This method processes a GET request to fetch a list of cache keys associated with cost
   * itinerary and factors mappings.
   *
   * @param limit The maximum number of cache keys to retrieve. Defaults to 100.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with the list of cache keys.
   */
  @GetCostItineraryAndFactorsCacheKeyDoc
  @GetMapping("/get-all-cache-keys")
  public ResponseEntity<BaseResponse<List<CostItineraryAndFactorsCacheKeyDto>>>
      getCostItineraryAndFactorsCacheKeys(@RequestParam(defaultValue = "100") Integer limit) {
    logger.debug("Processing get Cost Itinerary And Factors Cache Keys");
    var response = costItineraryAndFactorsService.getCostItineraryAndFactorsCacheKeys(limit);
    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Cost Itinerary And Factors Keys fetched successfully")
            .payload(response)
            .build());
  }
}
