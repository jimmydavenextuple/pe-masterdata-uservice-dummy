/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.sourcing.cost.config.controller;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.sourcing.cost.config.controller.docs.CreateCostAttributeMappingDoc;
import com.nextuple.sourcing.cost.config.controller.docs.GetCostAttributeMappingByOrgIdAndCanonicalName;
import com.nextuple.sourcing.cost.config.controller.docs.GetCostAttributeMappingByOrgIdAndCostAttributeMappingId;
import com.nextuple.sourcing.cost.config.controller.docs.GetCostAttributeMappingCacheKeysDoc;
import com.nextuple.sourcing.cost.config.controller.docs.UpdateCostAttributeMapping;
import com.nextuple.sourcing.cost.config.dto.CostAttributeMappingCacheKeyDto;
import com.nextuple.sourcing.cost.config.inbound.CostAttributeMappingRequest;
import com.nextuple.sourcing.cost.config.outbound.CostAttributeMappingResponse;
import com.nextuple.sourcing.cost.config.service.CostAttributeMappingService;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for managing cost attribute mappings in the user interface.
 *
 * <p>This controller provides APIs for creating, retrieving, updating, and deleting cost attribute
 * mappings. Cost attribute mappings are used to link cost attributes to specific organizations,
 * providing the flexibility to manage cost configurations effectively.
 *
 * <p>The controller is tagged with "Cost Attribute Mapping APIs" for easy categorization in the API
 * documentation.
 */
@Validated
@RestController
@RequestMapping("/cost-attribute-mapping")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Cost Attribute Mapping APIs")
public class CostAttributeMappingController {

  private static final Logger logger =
      LoggerFactory.getLogger(CostAttributeMappingController.class);

  private final CostAttributeMappingService costAttributeMappingService;

  /**
   * Creates a new cost attribute mapping.
   *
   * <p>This method processes a POST request to create a cost attribute mapping for a specific
   * organization.
   *
   * @param orgId The unique identifier of the organization. Must not be blank.
   * @param costAttributeMappingRequest The request body containing the details of the cost
   *     attribute mapping to be created.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with the created cost
   *     attribute mapping details.
   * @throws CommonServiceException If an error occurs during cost attribute mapping creation.
   */
  @CreateCostAttributeMappingDoc
  @PostMapping(value = "/{orgId}")
  public ResponseEntity<BaseResponse<CostAttributeMappingResponse>> createCostAttributeMapping(
      @NotBlank(message = "Unique identifier for organisation can't be empty")
          @PathVariable
          @Parameter(
              description = "Unique identifier of the organization.",
              example = "NEXTUPLE_GR")
          String orgId,
      @Valid @RequestBody CostAttributeMappingRequest costAttributeMappingRequest)
      throws CommonServiceException {
    log.debug("Processing create Cost Attribute Mapping request");
    var costAttributeMappingResponse =
        costAttributeMappingService.createCostAttributeMapping(orgId, costAttributeMappingRequest);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(
            BaseResponse.builder()
                .message("Cost Attribute Mapping created successfully!")
                .payload(costAttributeMappingResponse)
                .build());
  }

  /**
   * Retrieves cost attribute mapping details by organization ID and mapping ID.
   *
   * <p>This method processes a GET request to fetch the details of a cost attribute mapping using
   * its unique organization ID and mapping ID.
   *
   * @param orgId The unique identifier of the organization. Must not be blank.
   * @param costAttributeMappingId The unique identifier of the cost attribute mapping. Must not be
   *     null.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with the cost attribute
   *     mapping details.
   * @throws CommonServiceException If an error occurs while fetching the cost attribute mapping
   *     details.
   */
  @GetCostAttributeMappingByOrgIdAndCostAttributeMappingId
  @GetMapping(value = "/{orgId}/{costAttributeMappingId}")
  public ResponseEntity<BaseResponse<CostAttributeMappingResponse>>
      getCostAttributeMappingByOrgIdAndId(
          @NotBlank(message = "Unique identifier for organisation can't be empty")
              @PathVariable
              @Parameter(
                  description = "Unique identifier of the organization.",
                  example = "NEXTUPLE_GR")
              String orgId,
          @NotNull(message = "Unique identifier for cost attribute mapping can't be null")
              @PathVariable
              @Parameter(
                  description = "Unique identifier for cost attribute mapping",
                  example = "1")
              Long costAttributeMappingId)
          throws CommonServiceException {
    log.debug("Processing get Cost Attribute Mapping by org id and id request");
    var costAttributeMappingResponse =
        costAttributeMappingService.findCostAttributeMappingByOrgIdAndId(
            orgId, costAttributeMappingId);
    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Cost Attribute Mapping details fetched successfully!")
            .payload(costAttributeMappingResponse)
            .build());
  }

  /**
   * Updates an existing cost attribute mapping.
   *
   * <p>This method processes a PUT request to update a cost attribute mapping with the specified ID
   * and update details for a specific organization.
   *
   * @param orgId The unique identifier of the organization. Must not be blank.
   * @param costAttributeMappingId The unique identifier of the cost attribute mapping to be
   *     updated. Must not be null.
   * @param costAttributeMappingRequest The request body containing the updated details for the cost
   *     attribute mapping.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with the updated cost
   *     attribute mapping details.
   * @throws CommonServiceException If an error occurs during the update process.
   */
  @UpdateCostAttributeMapping
  @PutMapping(value = "/{orgId}/{costAttributeMappingId}")
  public ResponseEntity<BaseResponse<CostAttributeMappingResponse>> updateCostAttributeMapping(
      @NotBlank(message = "Unique identifier for organisation can't be empty")
          @PathVariable
          @Parameter(
              description = "Unique identifier of the organization.",
              example = "NEXTUPLE_GR")
          String orgId,
      @NotNull(message = "Unique identifier for cost attribute mapping can't be null")
          @PathVariable
          @Parameter(description = "Unique identifier for cost attribute mapping", example = "1")
          Long costAttributeMappingId,
      @Valid @RequestBody CostAttributeMappingRequest costAttributeMappingRequest)
      throws CommonServiceException {
    log.debug("Processing update Cost Attribute Mapping request");
    var costAttributeMappingResponse =
        costAttributeMappingService.updateCostAttributeMapping(
            costAttributeMappingId, orgId, costAttributeMappingRequest);
    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Cost Attribute Mapping updated successfully!")
            .payload(costAttributeMappingResponse)
            .build());
  }

  /**
   * Retrieves cost attribute mapping details by organization ID and canonical name.
   *
   * <p>This method processes a GET request to fetch the details of a cost attribute mapping using
   * its unique organization ID and canonical name.
   *
   * @param orgId The unique identifier of the organization. Must not be blank.
   * @param canonicalName The canonical name of the cost attribute defined by the tenant. Must not
   *     be blank.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with the cost attribute
   *     mapping details.
   * @throws CommonServiceException If an error occurs while fetching the cost attribute mapping
   *     details.
   */
  @GetCostAttributeMappingByOrgIdAndCanonicalName
  @GetMapping(value = "/{orgId}/canonical-name/{canonicalName}")
  public ResponseEntity<BaseResponse<CostAttributeMappingResponse>>
      getCostAttributeMappingByOrgIdAndCanonicalName(
          @NotBlank(message = "Unique identifier for organisation can't be empty")
              @PathVariable
              @Parameter(
                  description = "Unique identifier for the organization.",
                  example = "NEXTUPLE_GR")
              String orgId,
          @NotBlank(
                  message = "Canonical name of the cost attribute defined by tenant can't be blank")
              @PathVariable
              @Parameter(
                  description = "Canonical name of the cost attribute defined by tenant",
                  example = "itemLength")
              String canonicalName)
          throws CommonServiceException {
    log.debug("Processing get Cost Attribute Mapping by org id and canonical request");
    var costAttributeMappingResponse =
        costAttributeMappingService.findCostAttributeMappingByOrgIdAndCanonicalName(
            orgId, canonicalName);
    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Cost Attribute Mapping details fetched successfully!")
            .payload(costAttributeMappingResponse)
            .build());
  }

  /**
   * Retrieves all cache keys for cost attribute mappings.
   *
   * <p>This method processes a GET request to fetch a list of cache keys associated with cost
   * attribute mappings.
   *
   * @param limit The maximum number of cache keys to retrieve. Defaults to 100.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with the list of cache keys.
   */
  @GetCostAttributeMappingCacheKeysDoc
  @GetMapping("/get-all-cache-keys")
  public ResponseEntity<BaseResponse<List<CostAttributeMappingCacheKeyDto>>>
      getCostAttributeMappingCacheKeys(@RequestParam(defaultValue = "100") Integer limit) {
    logger.debug("Processing cost attribute mapping Cache Keys");
    var response = costAttributeMappingService.getAllCostAttributeMappingCacheKeys(limit);
    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Cost attribute mapping Keys fetched successfully")
            .payload(response)
            .build());
  }
}
