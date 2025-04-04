/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.promise.sourcing.rule.controller;

import com.nextuple.common.base.PagePayload;
import com.nextuple.common.context.Logger;
import com.nextuple.common.context.LoggerFactory;
import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.exception.PromiseEngineException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.SourcingRuleIdRequest;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.AllSourcingRulesResponse;
import com.nextuple.promise.sourcing.rule.api.domain.pojo.SourcingRuleDetails;
import com.nextuple.promise.sourcing.rule.service.SourcingRulesConfigurationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for managing sourcing rules configuration in the user interface.
 *
 * <p>This controller provides APIs for creating, retrieving, deleting, and fetching sourcing rules
 * associated with a specific organization. Sourcing rules are crucial for managing how resources
 * are selected based on predefined conditions within the sourcing environment.
 *
 * <p>The controller is tagged with "Sourcing Rules Configuration UI APIs" for easy categorization
 * in the API documentation.
 */
@Validated
@RestController
@RequestMapping("/ui/sourcing-rules-configuration")
@RequiredArgsConstructor
@Tag(name = "Sourcing Rules Configuration UI APIs")
public class SourcingRulesConfigurationUIController {
  private static final Logger logger =
      LoggerFactory.getLogger(SourcingRulesConfigurationUIController.class);

  private final SourcingRulesConfigurationService sourcingRulesConfigurationService;

  /**
   * Creates a new sourcing rule for the specified organization.
   *
   * <p>This method processes a POST request to create a sourcing rule for the given organization
   * ID.
   *
   * @param orgId The unique identifier of the organization. Must not be null.
   * @param createRequest The request body containing details for the sourcing rule to be created.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with the details of the
   *     created sourcing rule.
   * @throws PromiseEngineException If an error occurs related to the promise engine.
   * @throws CommonServiceException If a common service error occurs while processing the request.
   */
  @Operation(
      summary = "Create Sourcing Rule for a given orgId.",
      description =
          "Creates a sourcing rule. "
              + "This API creates the sourcing rule for the given orgId passed in the path parameter.")
  @ApiResponse(
      responseCode = "200",
      description =
          "A 200 success code indicates that the sourcing rule is created successfully for given orgId.")
  @PostMapping("/{orgId}")
  public ResponseEntity<BaseResponse<AllSourcingRulesResponse>> createSourcingRule(
      @NotNull(message = "orgId cannot be empty")
          @PathVariable
          @Parameter(description = "Unique identifier of the organization.", example = "NEXTUPLE")
          String orgId,
      @Valid @RequestBody AllSourcingRulesResponse createRequest)
      throws PromiseEngineException, CommonServiceException {
    try {
      var response = sourcingRulesConfigurationService.createSourcingRule(orgId, createRequest);

      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Sourcing rule created successfully")
              .payload(response)
              .build());
    } catch (Exception e) {
      logger.error("Failed to create sourcing rule", e);
      throw e;
    }
  }

  /**
   * Retrieves all sourcing rules for the specified organization.
   *
   * <p>This method processes a GET request to fetch the details of all active sourcing rules for a
   * given organization ID, with optional pagination parameters.
   *
   * @param orgId The unique identifier of the organization. Must not be null.
   * @param pageNo The page number for pagination. Defaults to 1 if not specified.
   * @param pageSize The page size for pagination. If null, all records are retrieved.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with the paginated list of
   *     sourcing rules.
   * @throws PromiseEngineException If an error occurs related to the promise engine.
   * @throws CommonServiceException If a common service error occurs while processing the request.
   */
  @Operation(
      summary = "Get All Sourcing Rules",
      description =
          "Retrieves the details of all the active sourcing rules for the given organisation ID. "
              + "This API retrieves the information by passing organisation ID in the path parameter of the request.")
  @ApiResponse(
      responseCode = "200",
      description =
          "A 200 success code indicates that all sourcing rules are fetched successfully for given orgId.")
  @GetMapping("/{orgId}")
  public ResponseEntity<BaseResponse<PagePayload<AllSourcingRulesResponse>>>
      getAllSourcingRuleByOrgId(
          @NotNull(message = "OrgId can't be empty") @PathVariable String orgId,
          @RequestParam(required = false, defaultValue = "1") Integer pageNo,
          @RequestParam(required = false) Integer pageSize)
          throws PromiseEngineException, CommonServiceException {
    logger.debug("-- Processing get all sourcing rule by orgId request");
    try {
      var allSourcingRulesResponses =
          sourcingRulesConfigurationService.processGetAllSourcingRuleDetailsByOrgId(orgId);

      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("All sourcing rule details fetched successfully")
              .payload(
                  paginateAllSourcingRulesResponses(allSourcingRulesResponses, pageNo, pageSize))
              .build());
    } catch (Exception e) {
      logger.error("Failed to process get all sourcing rule details by orgId request", e);
      throw e;
    }
  }

  /**
   * Deletes multiple sourcing rules for the specified organization.
   *
   * <p>This method processes a DELETE request to remove multiple sourcing rules for the given
   * organization ID.
   *
   * @param orgId The unique identifier of the organization. Must not be null.
   * @param sourcingRuleIdRequest The request body containing the list of sourcing rule IDs to be
   *     deleted.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with the details of the
   *     deleted sourcing rules.
   * @throws PromiseEngineException If an error occurs related to the promise engine.
   * @throws CommonServiceException If a common service error occurs while processing the request.
   */
  @Operation(
      summary = "Delete Multiple Sourcing Rule",
      description =
          "Deletes multiple sourcing rule for the given organisation ID and list of sourcing rules.")
  @ApiResponse(
      responseCode = "200",
      description =
          "A 200 success code indicates that multiple sourcing rules are deleted successfully.")
  @DeleteMapping("/{orgId}")
  public ResponseEntity<BaseResponse<List<SourcingRuleDetails>>> deleteMultipleSourcingRules(
      @NotNull(message = "OrgId can't be empty") @PathVariable String orgId,
      @Valid @RequestBody SourcingRuleIdRequest sourcingRuleIdRequest)
      throws PromiseEngineException, CommonServiceException {
    logger.debug("-- Processing delete multiple sourcing rules request");
    try {
      var sourcingRuleDetails =
          sourcingRulesConfigurationService.processDeleteMultipleSourcingRuleDetails(
              orgId, sourcingRuleIdRequest);

      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Sourcing rule deleted successfully")
              .payload(sourcingRuleDetails)
              .build());
    } catch (Exception e) {
      logger.error("Failed to delete multiple sourcing rule", e);
      throw e;
    }
  }

  /**
   * Retrieves the details of a specific sourcing rule for the given organization and rule ID.
   *
   * <p>This method processes a GET request to fetch the details of an active sourcing rule for the
   * specified organization ID and sourcing rule ID.
   *
   * @param orgId The unique identifier of the organization. Must not be null.
   * @param sourcingRuleId The unique identifier of the sourcing rule. Must not be null.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with the details of the
   *     sourcing rule.
   * @throws PromiseEngineException If an error occurs related to the promise engine.
   * @throws CommonServiceException If a common service error occurs while processing the request.
   */
  @Operation(
      summary = "Get Sourcing Rule Details",
      description =
          "Retrieves the details of the active sourcing rules for the given organisation ID and sourcing rule. "
              + "This API retrieves the information by passing organisation ID and sourcing rule in the path parameter of the request.")
  @ApiResponse(
      responseCode = "200",
      description =
          "A 200 success code indicates that active sourcing rules are fetched successfully for given orgId.")
  @GetMapping("/{orgId}/{sourcingRuleId}")
  public ResponseEntity<BaseResponse<AllSourcingRulesResponse>>
      getSourcingRuleByOrgIdAndSourcingRule(
          @NotNull(message = "OrgId can't be empty") @PathVariable String orgId,
          @NotNull(message = "Sourcing rule id can't be empty") @PathVariable Long sourcingRuleId)
          throws PromiseEngineException, CommonServiceException {
    logger.debug("-- Processing get sourcing rule by orgId and sourcing rule request");
    try {
      var allSourcingRulesResponse =
          sourcingRulesConfigurationService.processGetSourcingRuleDetailsByOrgIdAndSourcingRule(
              orgId, sourcingRuleId);

      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Sourcing rule fetched successfully")
              .payload(allSourcingRulesResponse)
              .build());
    } catch (Exception e) {
      logger.error(
          "Failed to process get sourcing rule details by orgId and sourcing rule request", e);
      throw e;
    }
  }

  /**
   * Paginates the list of sourcing rules.
   *
   * <p>This helper method creates a paginated response for the given list of sourcing rules.
   *
   * @param allSourcingRulesResponseList The list of sourcing rules to be paginated.
   * @param pageNo The page number for pagination.
   * @param pageSize The page size for pagination. If null, no pagination is applied.
   * @return A {@link PagePayload} containing the paginated list of sourcing rules.
   */
  private PagePayload<AllSourcingRulesResponse> paginateAllSourcingRulesResponses(
      List<AllSourcingRulesResponse> allSourcingRulesResponseList,
      Integer pageNo,
      Integer pageSize) {
    Pageable element =
        Objects.isNull(pageSize) ? Pageable.unpaged() : PageRequest.of(pageNo - 1, pageSize);

    Page<AllSourcingRulesResponse> pageResp =
        new PageImpl<>(allSourcingRulesResponseList, element, allSourcingRulesResponseList.size());

    PagePayload<AllSourcingRulesResponse> pagePayload = new PagePayload<>();
    PagePayload.Pagination pagination = new PagePayload.Pagination();
    pagination.setTotalRecords((int) pageResp.getTotalElements());
    pagination.setTotalPages(pageResp.getTotalPages());
    pagination.setCurrentPage(pageNo);
    pagePayload.setData(pageResp.getContent());
    pagePayload.setPagination(pagination);

    pagination.setNext("");
    pagination.setPrevious("");
    pagination.setSortBy("");
    pagination.setSortOrder("");

    return pagePayload;
  }
}
