/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.promise.sourcing.rule.controller;

import com.nextuple.common.context.Logger;
import com.nextuple.common.context.LoggerFactory;
import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.exception.PromiseEngineException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.CreateSourcingAttributeRequest;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.SourcingAttributeResponse;
import com.nextuple.promise.sourcing.rule.controller.docs.CreateSourcingAttributeDoc;
import com.nextuple.promise.sourcing.rule.service.SourcingAttributeService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for managing sourcing attributes within an organization.
 *
 * <p>This controller provides APIs for creating and retrieving sourcing attributes associated with
 * a specific organization. A sourcing attribute represents a characteristic used in sourcing rules
 * and can be managed and optimized for sourcing operations.
 *
 * <p>The controller is tagged with "Sourcing Attributes APIs" for easy categorization in the API
 * documentation.
 */
@Validated
@RestController
@Tag(name = "Sourcing Attributes APIs")
@RequestMapping("/sourcing-attribute")
@RequiredArgsConstructor
public class SourcingAttributeController {

  private static final Logger logger = LoggerFactory.getLogger(SourcingAttributeController.class);

  private final SourcingAttributeService sourcingAttributeService;

  /**
   * Creates a new sourcing attribute.
   *
   * <p>Processes a POST request to create a sourcing attribute using the provided request details.
   *
   * @param sourcingAttributeRequest The {@link CreateSourcingAttributeRequest} containing details
   *     for the sourcing attribute.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with the created sourcing
   *     attribute details.
   * @throws PromiseEngineException If an error occurs related to the promise engine.
   * @throws CommonServiceException If a common service exception occurs during the process.
   */
  @PostMapping
  @CreateSourcingAttributeDoc
  public ResponseEntity<BaseResponse<SourcingAttributeResponse>> createSourcingAttribute(
      @Valid @RequestBody CreateSourcingAttributeRequest sourcingAttributeRequest)
      throws PromiseEngineException, CommonServiceException {
    logger.debug("Processing create Sourcing Attribute request");
    try {
      var sourcingAtrributeResponse =
          sourcingAttributeService.createSourcingAttribute(sourcingAttributeRequest);
      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Sourcing attribute successfully created!")
              .payload(sourcingAtrributeResponse)
              .build());
    } catch (Exception e) {
      logger.error("Failed to process create Sourcing attribute request", e);
      throw e;
    }
  }

  /**
   * Retrieves a sourcing attribute by its ID and organization ID.
   *
   * <p>Processes a GET request to fetch a sourcing attribute based on the specified organization ID
   * and attribute ID.
   *
   * @param orgId The unique identifier of the organization.
   * @param id The unique identifier for the sourcing attribute.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with the fetched sourcing
   *     attribute details.
   * @throws PromiseEngineException If an error occurs related to the promise engine.
   * @throws CommonServiceException If a common service exception occurs during the process.
   */
  @GetMapping("/orgId/{orgId}/id/{id}")
  public ResponseEntity<BaseResponse<SourcingAttributeResponse>> getSourcingAttributeByOrgIdAndId(
      @NotBlank(message = "orgId can't be empty")
          @PathVariable
          @Parameter(description = "Unique identifier of the organization.", example = "NEXTUPLE")
          String orgId,
      @NotNull(message = "id can't be empty")
          @Min(value = 0)
          @PathVariable
          @Parameter(description = "Unique identifier for sourcing attribute", example = "1")
          Long id)
      throws PromiseEngineException, CommonServiceException {
    logger.debug("-- Processing get sourcing attribute by id request");
    try {
      var sourcingAttributeResponse =
          sourcingAttributeService.getSourcingAttributeByIdAndOrgId(id, orgId);
      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Sourcing attribute details fetched successfully")
              .payload(sourcingAttributeResponse)
              .build());
    } catch (Exception e) {
      logger.error("Failed to process get sourcing attribute details by id and orgId request", e);
      throw e;
    }
  }
}
