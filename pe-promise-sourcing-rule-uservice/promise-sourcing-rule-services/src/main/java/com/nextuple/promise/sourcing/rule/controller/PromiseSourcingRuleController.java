/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.promise.sourcing.rule.controller;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.exception.PromiseEngineException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.promise.sourcing.rule.api.domain.dto.PromiseSourcingRuleDto;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.CreatePromiseSourcingRuleRequest;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.FetchPromiseSourcingRuleRequest;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.UpdatePromiseSourcingRuleRequest;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.FetchPromiseSourcingRuleResponse;
import com.nextuple.promise.sourcing.rule.controller.docs.*;
import com.nextuple.promise.sourcing.rule.service.PromiseSourcingRuleService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
 * Controller for managing promise sourcing rules within an organization.
 *
 * <p>This controller provides APIs for creating, editing, retrieving, updating, and deleting
 * promise sourcing rules, which are used to define sourcing strategies for optimizing node
 * performance within an organization. The rules are associated with specific service options,
 * geozones, allocation rules, and priorities.
 *
 * <p>The controller is tagged with "Promise Sourcing Rule APIs" for easy categorization in the API
 * documentation.
 */
@Validated
@RestController
@Tag(name = "Promise Sourcing Rule APIs")
@RequestMapping("/promise-sourcing-rule")
@RequiredArgsConstructor
public class PromiseSourcingRuleController {

  private static final Logger logger = LoggerFactory.getLogger(PromiseSourcingRuleController.class);
  private final PromiseSourcingRuleService promiseSourcingRuleService;

  /**
   * Fetches Promise Sourcing Rules based on the specified filters.
   *
   * <p>This method processes a POST request to retrieve promise sourcing rules by applying various
   * filters provided in the request body, such as organization ID, service option, geozone, and
   * other attributes.
   *
   * @param baseRequest The request body containing the filters for fetching the promise sourcing
   *     rules.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with the list of fetched
   *     sourcing rules.
   * @throws PromiseEngineException If an error occurs related to the promise engine during the
   *     operation.
   */
  @FetchSourcingRuleDoc
  @PostMapping("/fetch-rules")
  public ResponseEntity<BaseResponse<FetchPromiseSourcingRuleResponse>> fetchSourcingRule(
      @Valid @RequestBody FetchPromiseSourcingRuleRequest baseRequest)
      throws PromiseEngineException {
    logger.debug("Processing fetch Promise Sourcing Rule request");
    try {
      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Promise Sourcing Rule successfully fetched!")
              .payload(promiseSourcingRuleService.fetchSourcingRule(baseRequest))
              .build());
    } catch (Exception e) {
      logger.error("Failed to process fetch Promise Sourcing Rule request!");
      throw e;
    }
  }

  /**
   * Creates a new Promise Sourcing Rule.
   *
   * <p>This method processes a POST request to create a new promise sourcing rule based on the
   * details provided in the request body.
   *
   * @param baseRequest The request body containing the details of the promise sourcing rule to be
   *     created.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with the details of the
   *     newly created rule.
   * @throws PromiseEngineException If an error occurs related to the promise engine.
   * @throws CommonServiceException If a common service exception occurs while processing the
   *     request.
   */
  @CreatePromiseSourcingRuleDoc
  @PostMapping
  public ResponseEntity<BaseResponse<PromiseSourcingRuleDto>> createPromiseSourcingRule(
      @Valid @RequestBody CreatePromiseSourcingRuleRequest baseRequest)
      throws PromiseEngineException, CommonServiceException {
    logger.debug("Processing create Promise Sourcing Rule request");
    try {
      var promiseSourcingRuleDto =
          promiseSourcingRuleService.createPromiseSourcingRule(baseRequest);
      logger.info("Response after creation of promise sourcing rule :{}", promiseSourcingRuleDto);
      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Promise Sourcing Rule successfully created!")
              .payload(promiseSourcingRuleDto)
              .build());
    } catch (Exception e) {
      logger.error("Failed to process create Promise Sourcing Rule request!");
      throw e;
    }
  }

  /**
   * Retrieves a specific Promise Sourcing Rule.
   *
   * <p>This method processes a GET request to fetch a specific promise sourcing rule based on the
   * provided organization ID, service option, destination geozone, allocation rule ID, and
   * priority.
   *
   * @param orgId The unique identifier of the organization.
   * @param serviceOption The service option associated with the promise sourcing rule.
   * @param destinationGeoZone The destination geozone for the rule.
   * @param allocationRuleId The allocation rule ID of the sourcing rule.
   * @param priority The priority level of the rule.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with the details of the
   *     fetched rule.
   * @throws PromiseEngineException If an error occurs related to the promise engine.
   */
  @GetPromiseSourcingRuleDoc
  @GetMapping
  public ResponseEntity<BaseResponse<PromiseSourcingRuleDto>> getPromiseSourcingRule(
      @NotBlank(message = "orgId can't be empty")
          @RequestParam
          @Parameter(description = "Unique identifier of the organization.", example = "NEXTUPLE")
          String orgId,
      @NotBlank(message = "serviceOption can't be empty")
          @RequestParam
          @Parameter(description = "Service option for promise sourcing rule.", example = "SDND")
          String serviceOption,
      @NotBlank(message = "destinationGeoZone can't be empty")
          @RequestParam
          @Parameter(description = "Destination geozone for a given region.", example = "VOA")
          String destinationGeoZone,
      @NotBlank(message = "allocationRuleId can't be empty")
          @RequestParam
          @Parameter(
              description = "Allocation rule ID of a given sourcing rule.",
              example = "DEFAULT")
          String allocationRuleId,
      @NotNull(message = "priority can't be empty") @Min(value = 0) @RequestParam Integer priority)
      throws PromiseEngineException {
    logger.debug("Processing get Promise Sourcing Rule request");
    try {
      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Promise Sourcing Rule successfully fetched!")
              .payload(
                  promiseSourcingRuleService.getPromiseSourcingRule(
                      orgId, serviceOption, destinationGeoZone, allocationRuleId, priority))
              .build());
    } catch (Exception e) {
      logger.error("Failed to process get Promise Sourcing Rule request!");
      throw e;
    }
  }

  /**
   * Retrieves all Promise Sourcing Rules for a given organization ID.
   *
   * <p>This method processes a GET request to fetch all sourcing rules associated with the
   * specified organization.
   *
   * @param orgId The unique identifier of the organization.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with the list of all promise
   *     sourcing rules.
   * @throws PromiseEngineException If an error occurs related to the promise engine during the
   *     operation.
   */
  @GetPromiseSourcingRulesByOrgIdDoc
  @GetMapping("/{orgId}@oid")
  public ResponseEntity<BaseResponse<List<PromiseSourcingRuleDto>>> getPromiseSourcingRulesByOrgId(
      @NotBlank(message = "orgId can't be empty")
          @PathVariable
          @Parameter(description = "Unique identifier of the organization.", example = "NEXTUPLE")
          String orgId)
      throws PromiseEngineException {
    logger.debug("Processing get Promise Sourcing Rules by orgId request");
    try {
      List<PromiseSourcingRuleDto> promiseSourcingRuleDtoList =
          promiseSourcingRuleService.getPromiseSourcingRulesByOrgId(orgId);
      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Promise Sourcing Rules successfully fetched!")
              .payload(promiseSourcingRuleDtoList)
              .build());
    } catch (Exception e) {
      logger.error("Failed to process get Promise Sourcing Rules request!");
      throw e;
    }
  }

  /**
   * Retrieves Promise Sourcing Rules based on the priority.
   *
   * <p>This method processes a GET request to fetch sourcing rules that match the specified
   * priority level.
   *
   * @param priority The priority level of the sourcing rules to fetch.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with the list of matching
   *     sourcing rules.
   * @throws PromiseEngineException If an error occurs related to the promise engine during the
   *     operation.
   */
  @GetPromiseSourcingRulesByPriority
  @GetMapping("/{priority}@pty")
  public ResponseEntity<BaseResponse<List<PromiseSourcingRuleDto>>>
      getPromiseSourcingRulesByPriority(
          @NotNull(message = "priority can't be empty")
              @Min(value = 0)
              @PathVariable
              @Parameter(description = "Priority of the sourcing rule.", example = "1")
              Integer priority)
          throws PromiseEngineException {
    logger.debug("Processing get Promise Sourcing Rules by priority request");
    try {
      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Promise Sourcing Rules successfully fetched!")
              .payload(promiseSourcingRuleService.getPromiseSourcingRulesByPriority(priority))
              .build());
    } catch (Exception e) {
      logger.error("Failed to process get Promise Sourcing Rules request!");
      throw e;
    }
  }

  /**
   * Updates an existing Promise Sourcing Rule.
   *
   * <p>This method processes a PUT request to update an existing promise sourcing rule using the
   * provided details.
   *
   * @param orgId The unique identifier of the organization.
   * @param serviceOption The service option associated with the promise sourcing rule.
   * @param destinationGeoZone The destination geozone for the rule.
   * @param allocationRuleId The allocation rule ID of the sourcing rule.
   * @param priority The priority level of the rule.
   * @param baseRequest The request body containing the updated details for the sourcing rule.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with the updated sourcing
   *     rule details.
   * @throws PromiseEngineException If an error occurs related to the promise engine.
   * @throws CommonServiceException If a common service exception occurs while processing the
   *     request.
   */
  @UpdatePromiseSourcingRuleDoc
  @PutMapping
  public ResponseEntity<BaseResponse<PromiseSourcingRuleDto>> updatePromiseSourcingRule(
      @NotBlank(message = "orgId can't be empty")
          @RequestParam
          @Parameter(description = "Unique identifier of the organization.", example = "NEXTUPLE")
          String orgId,
      @NotBlank(message = "serviceOption can't be empty")
          @RequestParam
          @Parameter(description = "Service option for promise sourcing rule.", example = "SDND")
          String serviceOption,
      @NotBlank(message = "destinationGeoZone can't be empty")
          @RequestParam
          @Parameter(description = "Destination geozone for a given region.", example = "VOA")
          String destinationGeoZone,
      @NotBlank(message = "allocationRuleId can't be empty")
          @RequestParam
          @Parameter(
              description = "Allocation rule ID of a given sourcing rule.",
              example = "DEFAULT")
          String allocationRuleId,
      @NotNull(message = "priority can't be empty") @Min(value = 0) @RequestParam Integer priority,
      @Valid @RequestBody UpdatePromiseSourcingRuleRequest baseRequest)
      throws PromiseEngineException, CommonServiceException {
    logger.debug("Processing update Promise Sourcing Rule request");
    try {
      var promiseSourcingRuleDto =
          promiseSourcingRuleService.updatePromiseSourcingRule(
              orgId, serviceOption, destinationGeoZone, allocationRuleId, priority, baseRequest);
      logger.info("Response after updation of promise sourcing rule :{}", promiseSourcingRuleDto);
      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Promise Sourcing Rule successfully updated!")
              .payload(promiseSourcingRuleDto)
              .build());
    } catch (Exception e) {
      logger.error("Failed to process update Promise Sourcing Rule request!");
      throw e;
    }
  }

  /**
   * Deletes an existing Promise Sourcing Rule.
   *
   * <p>This method processes a DELETE request to remove a promise sourcing rule based on the
   * provided identifiers.
   *
   * @param orgId The unique identifier of the organization.
   * @param serviceOption The service option associated with the promise sourcing rule.
   * @param destinationGeoZone The destination geozone for the rule.
   * @param allocationRuleId The allocation rule ID of the sourcing rule.
   * @param priority The priority level of the rule.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} confirming the deletion.
   * @throws PromiseEngineException If an error occurs related to the promise engine.
   */
  @DeletePromiseSourcingRuleDoc
  @Transactional
  @DeleteMapping
  public ResponseEntity<BaseResponse<PromiseSourcingRuleDto>> deletePromiseSourcingRule(
      @NotBlank(message = "orgId can't be empty")
          @RequestParam
          @Parameter(description = "Unique identifier of the organization.", example = "NEXTUPLE")
          String orgId,
      @NotBlank(message = "serviceOption can't be empty")
          @RequestParam
          @Parameter(description = "Service option for promise sourcing rule.", example = "SDND")
          String serviceOption,
      @NotBlank(message = "destinationGeoZone can't be empty")
          @RequestParam
          @Parameter(description = "Destination geozone for a given region.", example = "VOA")
          String destinationGeoZone,
      @NotBlank(message = "allocationRuleId can't be empty")
          @RequestParam
          @Parameter(
              description = "Allocation rule ID of a given sourcing rule.",
              example = "DEFAULT")
          String allocationRuleId,
      @NotNull(message = "priority can't be empty") @Min(value = 0) @RequestParam Integer priority)
      throws PromiseEngineException {
    logger.debug("Processing delete Promise Sourcing Rule request by sourcingRuleId");
    try {
      var promiseSourcingRuleDto =
          promiseSourcingRuleService.deletePromiseSourcingRule(
              orgId, serviceOption, destinationGeoZone, allocationRuleId, priority);
      logger.info("Response after deletion of promise sourcing rule :{}", promiseSourcingRuleDto);
      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Promise Sourcing Rule successfully deleted!")
              .payload(promiseSourcingRuleDto)
              .build());
    } catch (Exception e) {
      logger.error("Failed to process delete Promise Sourcing Rule request!");
      throw e;
    }
  }
}
