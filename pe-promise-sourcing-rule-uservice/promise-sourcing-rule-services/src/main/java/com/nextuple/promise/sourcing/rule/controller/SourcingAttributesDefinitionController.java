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
import com.nextuple.promise.sourcing.rule.api.domain.enums.SourcingAttributesDefinitionScopeEnum;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.SourcingAttributesDefinitionRequest;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.SourcingAttributesDefinitionUpdationRequest;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.SourcingAttributesDefinitionResponse;
import com.nextuple.promise.sourcing.rule.controller.docs.CreateSourcingAttributesDefinitionDoc;
import com.nextuple.promise.sourcing.rule.controller.docs.GetActiveSourcingAttributesDefinitionDoc;
import com.nextuple.promise.sourcing.rule.controller.docs.GetSourcingAttributesDefinitionDoc;
import com.nextuple.promise.sourcing.rule.controller.docs.UpdateSourcingAttributesDefinitionDoc;
import com.nextuple.promise.sourcing.rule.service.SourcingAttributesDefinitionService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
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
 * Controller for managing sourcing attributes definitions within an organization.
 *
 * <p>This controller provides APIs for creating, retrieving, updating, and fetching active sourcing
 * attributes definitions. Sourcing attributes definitions are used to manage various attributes
 * involved in sourcing rules, and can be configured for specific organizational needs.
 *
 * <p>The controller is tagged with "Sourcing Attributes Definition APIs" for easy categorization in
 * the API documentation.
 */
@Validated
@RestController
@RequestMapping("/sourcing-attributes-definition")
@RequiredArgsConstructor
@Tag(name = "Sourcing Attributes Definition APIs")
public class SourcingAttributesDefinitionController {

  private static final Logger logger =
      LoggerFactory.getLogger(SourcingAttributesDefinitionController.class);

  private final SourcingAttributesDefinitionService sourcingAttributesDefinitionService;

  /**
   * Creates a new sourcing attributes definition.
   *
   * <p>Processes a POST request to create a sourcing attributes definition using the provided
   * request details.
   *
   * @param sourcingRuleAttributesDefinitionRequest The {@link SourcingAttributesDefinitionRequest}
   *     containing the details for the sourcing attributes definition.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with the created sourcing
   *     attributes definition details.
   * @throws PromiseEngineException If an error occurs related to the promise engine.
   * @throws CommonServiceException If a common service exception occurs during the process.
   */
  @CreateSourcingAttributesDefinitionDoc
  @PostMapping(
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<BaseResponse<SourcingAttributesDefinitionResponse>>
      createSourcingAttributesDefinition(
          @Valid @RequestBody
              SourcingAttributesDefinitionRequest sourcingRuleAttributesDefinitionRequest)
          throws PromiseEngineException, CommonServiceException {
    logger.debug("Processing create sourcing attribute request");
    try {
      var sourcingRuleAttributesDefinitionResponse =
          sourcingAttributesDefinitionService.processCreateSourcingAttributesDefinition(
              sourcingRuleAttributesDefinitionRequest);
      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Sourcing attributes definition successfully added")
              .payload(sourcingRuleAttributesDefinitionResponse)
              .build());
    } catch (Exception e) {
      logger.error("Failed to process create sourcing attribute request", e);
      throw e;
    }
  }

  /**
   * Retrieves a sourcing attributes definition by its ID and organization ID.
   *
   * <p>Processes a GET request to fetch a sourcing attributes definition based on the specified
   * organization ID and definition ID.
   *
   * @param id The unique identifier for the sourcing attributes definition.
   * @param orgId The unique identifier of the organization.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with the fetched sourcing
   *     attributes definition details.
   * @throws PromiseEngineException If an error occurs related to the promise engine.
   * @throws CommonServiceException If a common service exception occurs during the process.
   */
  @GetSourcingAttributesDefinitionDoc
  @GetMapping(value = "/orgId/{orgId}/id/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<BaseResponse<SourcingAttributesDefinitionResponse>>
      getSourcingAttributesDefinitionByIdandOrgId(
          @NotNull(message = "id can't be empty")
              @Min(value = 0)
              @PathVariable
              @Parameter(
                  description = "Unique identifier for sourcing attribute definition",
                  example = "1")
              Long id,
          @NotBlank(message = "orgId can't be empty")
              @PathVariable
              @Parameter(
                  description = "Unique identifier of the organization.",
                  example = "NEXTUPLE")
              String orgId)
          throws PromiseEngineException, CommonServiceException {
    logger.debug("-- Processing get sourcing attributes definition by id request");
    try {
      var sourcingRuleAttributesDefinitionResponse =
          sourcingAttributesDefinitionService.processGetSourcingAttributesDefinitionByIdandOrgId(
              id, orgId);
      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Sourcing attributes definition by id fetched successfully")
              .payload(sourcingRuleAttributesDefinitionResponse)
              .build());
    } catch (Exception e) {
      logger.error("Failed to process get sourcing attributes definition by id request", e);
      throw e;
    }
  }

  /**
   * Retrieves sourcing attributes definitions in active status.
   *
   * <p>Processes a GET request to fetch active sourcing attributes definitions for the specified
   * organization and scope.
   *
   * @param orgId The unique identifier of the organization.
   * @param scope The {@link SourcingAttributesDefinitionScopeEnum} specifying the scope for the
   *     attributes definitions.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with the fetched active
   *     sourcing attributes definitions.
   * @throws PromiseEngineException If an error occurs related to the promise engine.
   * @throws CommonServiceException If a common service exception occurs during the process.
   */
  @GetActiveSourcingAttributesDefinitionDoc
  @GetMapping(value = "/orgId/{orgId}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<BaseResponse<SourcingAttributesDefinitionResponse>>
      getSourcingAttributesDefinitionInActiveStatus(
          @NotEmpty(message = "orgId can't be empty")
              @PathVariable
              @Parameter(
                  description = "Unique identifier of the organization.",
                  example = "NEXTUPLE")
              String orgId,
          @NotNull(message = "scope can't be null")
              @RequestParam
              @Parameter(
                  description = "Scope with respect to which the attributes definition is defined.")
              SourcingAttributesDefinitionScopeEnum scope)
          throws PromiseEngineException, CommonServiceException {
    logger.debug("-- Processing get sourcing attributes definition in active status request");
    try {
      var sourcingRuleAttributesDefinitionResponse =
          sourcingAttributesDefinitionService.processGetSourcingAttributesDefinitionInActiveStatus(
              orgId, scope);
      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Sourcing attributes definition in active status fetched successfully")
              .payload(sourcingRuleAttributesDefinitionResponse)
              .build());
    } catch (Exception e) {
      logger.error(
          "Failed to process get sourcing attributes definition in active status request", e);
      throw e;
    }
  }

  /**
   * Updates an existing sourcing attributes definition.
   *
   * <p>Processes a PUT request to update a sourcing attributes definition using the provided
   * organization ID, definition ID, and update details.
   *
   * @param id The unique identifier for the sourcing attributes definition.
   * @param orgId The unique identifier of the organization.
   * @param sourcingRuleAttributesDefinitionUpdationRequest The {@link
   *     SourcingAttributesDefinitionUpdationRequest} containing the updated details for the
   *     sourcing attributes definition.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with the updated sourcing
   *     attributes definition details.
   * @throws PromiseEngineException If an error occurs related to the promise engine.
   * @throws CommonServiceException If a common service exception occurs during the process.
   */
  @UpdateSourcingAttributesDefinitionDoc
  @PutMapping(
      value = "/orgId/{orgId}/id/{id}",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<BaseResponse<SourcingAttributesDefinitionResponse>>
      updateSourcingAttributesDefinition(
          @NotNull(message = "id can't be empty")
              @Min(value = 0)
              @PathVariable
              @Parameter(
                  description = "Unique identifier for sourcing attribute definition.",
                  example = "1")
              Long id,
          @NotBlank(message = "orgId can't be empty")
              @PathVariable
              @Parameter(
                  description = "Unique identifier of the organization.",
                  example = "NEXTUPLE")
              String orgId,
          @Valid @RequestBody
              SourcingAttributesDefinitionUpdationRequest
                  sourcingRuleAttributesDefinitionUpdationRequest)
          throws PromiseEngineException, CommonServiceException {
    logger.debug("-- Processing update sourcing attributes definition request");
    try {
      var sourcingRuleAttributesDefinitionResponse =
          sourcingAttributesDefinitionService.updateSourcingAttributesDefinition(
              id, orgId, sourcingRuleAttributesDefinitionUpdationRequest);
      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Sourcing attributes definition updated successfully")
              .payload(sourcingRuleAttributesDefinitionResponse)
              .build());
    } catch (Exception e) {
      logger.error("Failed to process update sourcing attributes definition request", e);
      throw e;
    }
  }
}
