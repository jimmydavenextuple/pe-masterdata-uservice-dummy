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
import com.nextuple.common.pojo.PageParams;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.promise.sourcing.rule.api.domain.dto.AllConstraintUIDto;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.DeleteOptimizationRulesRequest;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.OptimizationRuleUpdationUIRequest;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.OptimizationStrategyUIRequest;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.OptimizationRuleUIResponse;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.PageResponse;
import com.nextuple.promise.sourcing.rule.controller.docs.CreateOptimizationRuleDoc;
import com.nextuple.promise.sourcing.rule.controller.docs.DeleteMultipleOptimizationRulesDoc;
import com.nextuple.promise.sourcing.rule.controller.docs.EditOptimizationRuleDoc;
import com.nextuple.promise.sourcing.rule.controller.docs.GetAllOptimizationRulesByOrgIdDoc;
import com.nextuple.promise.sourcing.rule.controller.docs.GetAllUIConstraintsDoc;
import com.nextuple.promise.sourcing.rule.controller.docs.GetOptimizationRuleByOrgIdAndNamedOptimazationStrategyIdDoc;
import com.nextuple.promise.sourcing.rule.service.NamedOptimizationStrategyService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for managing optimization rules within an organization.
 *
 * <p>This controller provides APIs for adding, editing, deleting, and retrieving optimization
 * rules. Optimization rules define strategies for selecting and prioritizing nodes within a system.
 * These rules are associated with an organization and are used to control how nodes are optimized
 * for specific operations within the organization.
 *
 * <p>The controller is tagged with "Optimization Rule dashboard APIs" for easy categorization in
 * the API documentation.
 */
@Validated
@RestController
@RequestMapping("/ui/optimization-rules")
@RequiredArgsConstructor
@Tag(name = "Optimization Rule dashboard APIs")
public class OptimizationRulesDashboardController {

  private static final Logger logger =
      LoggerFactory.getLogger(OptimizationRulesDashboardController.class);

  private final NamedOptimizationStrategyService namedOptimizationStrategyService;

  /**
   * Creates a new Optimization Rule for the specified organization.
   *
   * <p>This method processes a POST request to create an Optimization Rule within a given
   * organization.
   *
   * @param orgId The unique identifier of the organization.
   * @param optimizationStrategyUIRequest The details of the Optimization Rule to be created.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with the details of the
   *     created Optimization Rule.
   * @throws PromiseEngineException If an error occurs related to the promise engine.
   * @throws CommonServiceException If a general service-related error occurs.
   */
  @CreateOptimizationRuleDoc
  @PostMapping(
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE,
      value = "/{orgId}")
  public ResponseEntity<BaseResponse<OptimizationRuleUIResponse>> createOptimizationRule(
      @NotBlank(message = "orgId can't be empty")
          @PathVariable
          @Parameter(description = "Unique identifier of the organization.", example = "NEXTUPLE")
          String orgId,
      @Valid @RequestBody OptimizationStrategyUIRequest optimizationStrategyUIRequest)
      throws PromiseEngineException, CommonServiceException {
    try {
      var response =
          namedOptimizationStrategyService.createOptimizationRuleUI(
              orgId, optimizationStrategyUIRequest);
      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Optimization Rule created successfully.")
              .payload(response)
              .build());
    } catch (Exception e) {
      logger.error("Failed to create Optimization Rule", e);
      throw e;
    }
  }

  /**
   * Edits an existing Optimization Rule within the specified organization.
   *
   * <p>This method processes a PUT request to update the details of an Optimization Rule.
   *
   * @param orgId The unique identifier of the organization.
   * @param optimizationRuleId The unique identifier of the Optimization Rule to be updated.
   * @param optimizationRuleUpdationUIRequest The updated details of the Optimization Rule.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with the updated
   *     Optimization Rule details.
   * @throws PromiseEngineException If an error occurs related to the promise engine.
   * @throws CommonServiceException If a general service-related error occurs.
   */
  @EditOptimizationRuleDoc
  @PutMapping(
      value = "/{orgId}/{optimizationRuleId}",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<BaseResponse<OptimizationRuleUIResponse>> editOptimizationRule(
      @NotBlank(message = "orgId can't be empty")
          @PathVariable
          @Parameter(description = "Unique identifier of the organization.", example = "NEXTUPLE")
          String orgId,
      @NotNull(message = "optimizationRuleId can't be null")
          @PathVariable
          @Parameter(description = "Unique identifier of the optimization rule.")
          Long optimizationRuleId,
      @Valid @RequestBody OptimizationRuleUpdationUIRequest optimizationRuleUpdationUIRequest)
      throws PromiseEngineException, CommonServiceException {
    logger.debug("Processing edit optimization rule request");
    try {
      var optimizationStrategyUpdatedResponse =
          namedOptimizationStrategyService.processEditOptimizationRuleUI(
              orgId, optimizationRuleId, optimizationRuleUpdationUIRequest);
      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Optimization rule edited successfully")
              .payload(optimizationStrategyUpdatedResponse)
              .build());
    } catch (Exception e) {
      logger.error("Failed to process edit optimization rule request", e);
      throw e;
    }
  }

  /**
   * Deletes multiple Optimization Rules for a specific organization.
   *
   * <p>This method processes a DELETE request to remove multiple Optimization Rules based on the
   * provided identifiers.
   *
   * @param orgId The unique identifier of the organization.
   * @param optimizationRuleIds The request containing the IDs of the Optimization Rules to be
   *     deleted.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with the list of deleted
   *     Optimization Rules.
   * @throws PromiseEngineException If an error occurs related to the promise engine.
   * @throws CommonServiceException If a general service-related error occurs.
   */
  @DeleteMultipleOptimizationRulesDoc
  @Transactional
  @DeleteMapping(value = "/{orgId}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<BaseResponse<List<OptimizationRuleUIResponse>>>
      deleteMultipleOptimizationStrategy(
          @NotBlank(message = "orgId can't be empty")
              @PathVariable
              @Parameter(
                  description = "Unique identifier of the organization.",
                  example = "NEXTUPLE")
              String orgId,
          @Valid @RequestBody DeleteOptimizationRulesRequest optimizationRuleIds)
          throws PromiseEngineException, CommonServiceException {
    logger.debug("Processing delete optimization strategy request");
    try {
      var namedOptimizationStrategyResponseList =
          namedOptimizationStrategyService.processDeleteMultipleOptimizationStrategy(
              orgId, optimizationRuleIds);
      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Optimization rules deleted successfully")
              .payload(namedOptimizationStrategyResponseList)
              .build());
    } catch (Exception e) {
      logger.error("Failed to delete multiple optimization strategy request", e);
      throw e;
    }
  }

  /**
   * Retrieves all UI constraints for Optimization Rules.
   *
   * <p>This method processes a GET request to fetch all constraints used for validation or display
   * purposes in the UI.
   *
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with the list of UI
   *     constraints.
   */
  @GetAllUIConstraintsDoc
  @GetMapping(value = "/constraints", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<BaseResponse<List<AllConstraintUIDto>>> getAllUIConstraints() {
    logger.debug("Processing get all constraints request");
    try {
      var response = namedOptimizationStrategyService.getAllUIConstraints();
      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Constraints fetched successfully.")
              .payload(response)
              .build());
    } catch (Exception e) {
      logger.error("Failed to fetch constraints", e);
      throw e;
    }
  }

  /**
   * Fetches an Optimization Rule by organization ID and Optimization Rule ID.
   *
   * <p>This method processes a GET request to retrieve the details of a specific Optimization Rule.
   *
   * @param orgId The unique identifier of the organization.
   * @param optimizationRuleId The unique identifier of the Optimization Rule.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with the details of the
   *     Optimization Rule.
   * @throws PromiseEngineException If an error occurs related to the promise engine.
   * @throws CommonServiceException If a general service-related error occurs.
   */
  @GetOptimizationRuleByOrgIdAndNamedOptimazationStrategyIdDoc
  @GetMapping(value = "/{orgId}/{optimizationRuleId}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<BaseResponse<OptimizationRuleUIResponse>>
      getOptimizationRuleByOrgIdAndNamedOptimizationStrategyId(
          @NotEmpty(message = "orgId can't be empty")
              @PathVariable
              @Parameter(
                  description = "Unique identifier of the organization.",
                  example = "NEXTUPLE")
              String orgId,
          @NotNull(message = "optimizationRuleId can't be empty")
              @Min(value = 0)
              @PathVariable
              @Parameter(
                  description = "Unique identifier for the optimization rule.",
                  example = "1")
              Long optimizationRuleId)
          throws PromiseEngineException, CommonServiceException {
    logger.debug("Processing get optimization rule by orgId and id request");
    try {

      var response =
          namedOptimizationStrategyService.getOptimizationRuleByOrgIdAndNamedOptimizationStrategyId(
              orgId, optimizationRuleId);

      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Optimization rule fetched successfully.")
              .payload(response)
              .build());
    } catch (Exception e) {
      logger.error("Failed to fetch optimization rule", e);
      throw e;
    }
  }

  /**
   * Fetches all Optimization Rules for a specific organization with pagination.
   *
   * <p>This method processes a GET request to retrieve a paginated list of Optimization Rules for
   * the given organization.
   *
   * @param orgId The unique identifier of the organization.
   * @param pageParams The pagination parameters for fetching Optimization Rules.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with the paginated list of
   *     Optimization Rules.
   * @throws PromiseEngineException If an error occurs related to the promise engine.
   * @throws CommonServiceException If a general service-related error occurs.
   */
  @GetAllOptimizationRulesByOrgIdDoc
  @GetMapping(value = "/{orgId}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<BaseResponse<PageResponse<OptimizationRuleUIResponse>>>
      getAllOptimizationRulesByOrgId(
          @Parameter(description = "Unique identifier of the organization.", example = "NEXTUPLE")
              @NotBlank(message = "orgId can't be empty")
              @PathVariable
              String orgId,
          PageParams pageParams)
          throws CommonServiceException, PromiseEngineException {
    try {
      logger.debug("Processing get optimization rules by orgId with pagination");
      var optimizationRulesUIResponsePagePayload =
          namedOptimizationStrategyService.getAllOptimizationRulesByOrgId(orgId, pageParams);

      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Optimization rules fetched successfully")
              .payload(optimizationRulesUIResponsePagePayload)
              .build());

    } catch (Exception e) {
      logger.error("Failed to fetch optimization rules", e);
      throw e;
    }
  }
}
