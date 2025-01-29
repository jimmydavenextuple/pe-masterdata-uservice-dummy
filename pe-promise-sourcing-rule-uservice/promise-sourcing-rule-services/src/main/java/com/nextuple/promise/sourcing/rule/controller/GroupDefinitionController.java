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
import com.nextuple.promise.sourcing.rule.api.domain.inbound.GroupDefinitionRequest;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.GroupDefinitionListResponse;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.GroupDefinitionResponse;
import com.nextuple.promise.sourcing.rule.controller.docs.AddGroupDefinitionDoc;
import com.nextuple.promise.sourcing.rule.controller.docs.DeleteGroupDefinitionDoc;
import com.nextuple.promise.sourcing.rule.controller.docs.GetGroupDefinitionDoc;
import com.nextuple.promise.sourcing.rule.controller.docs.GetGroupDefinitionListDoc;
import com.nextuple.promise.sourcing.rule.controller.docs.UpdateGroupDefinitionDoc;
import com.nextuple.promise.sourcing.rule.service.GroupDefinitionService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
 * Controller for managing group definitions.
 *
 * <p>This controller provides APIs for adding, retrieving, updating, and deleting group definitions
 * associated with specific organizations. These operations allow users to manage group definitions
 * in their organization, which are linked to sourcing attributes.
 *
 * <p>The controller is tagged with "Group Definition APIs" for easy categorization in the API
 * documentation.
 */
@Validated
@RestController
@RequestMapping("/group-definition")
@RequiredArgsConstructor
@Tag(name = "Group Definition APIs")
public class GroupDefinitionController {

  private static final Logger logger = LoggerFactory.getLogger(GroupDefinitionController.class);

  private final GroupDefinitionService groupDefinitionService;

  /**
   * Adds a new group definition.
   *
   * <p>This method processes a POST request to create a new group definition based on the details
   * provided in the request body.
   *
   * @param groupDefinitionRequest The request body containing details for the new group definition.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with the created group
   *     definition details.
   * @throws PromiseEngineException If an error occurs related to the promise engine.
   * @throws CommonServiceException If a general service-related error occurs while processing the
   *     request.
   */
  @AddGroupDefinitionDoc
  @PostMapping(
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<BaseResponse<GroupDefinitionResponse>> addGroupDefinition(
      @Valid @RequestBody GroupDefinitionRequest groupDefinitionRequest)
      throws PromiseEngineException, CommonServiceException {
    logger.debug("Processing add group definition request");
    try {
      var groupDefinitionResponse =
          groupDefinitionService.processAddGroupDefinition(groupDefinitionRequest);
      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Group definition successfully added")
              .payload(groupDefinitionResponse)
              .build());
    } catch (Exception e) {
      logger.error("Failed to process add group definition request", e);
      throw e;
    }
  }

  /**
   * Retrieves a group definition by its unique identifier and organization ID.
   *
   * <p>This method processes a GET request to fetch the details of a group definition identified by
   * its ID and associated organization ID.
   *
   * @param orgId The unique identifier of the organization.
   * @param id The unique identifier of the group definition.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with the fetched group
   *     definition details.
   * @throws PromiseEngineException If an error occurs related to the promise engine.
   * @throws CommonServiceException If a general service-related error occurs while processing the
   *     request.
   */
  @GetGroupDefinitionDoc
  @GetMapping(value = "/orgId/{orgId}/id/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<BaseResponse<GroupDefinitionResponse>> getGroupDefinitionByOrgIdAndId(
      @NotBlank(message = "orgId can't be empty")
          @PathVariable
          @Parameter(description = "Unique identifier of the organization.", example = "NEXTUPLE")
          String orgId,
      @NotNull(message = "id can't be null")
          @Min(value = 0)
          @PathVariable
          @Parameter(description = "Unique identifier for group definition.", example = "1")
          Long id)
      throws PromiseEngineException, CommonServiceException {
    logger.debug("Processing get group definition by id request");
    try {
      var groupDefinitionResponse =
          groupDefinitionService.processGetGroupDefinitionByIdAndOrgId(id, orgId);
      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Node group successfully fetched")
              .payload(groupDefinitionResponse)
              .build());
    } catch (Exception e) {
      logger.error("Failed to process get group definition by id request", e);
      throw e;
    }
  }

  /**
   * Retrieves a list of group definitions for a specific organization and sourcing attributes
   * definition ID.
   *
   * <p>This method processes a GET request to fetch all group definitions associated with the given
   * organization ID and sourcing attributes definition ID.
   *
   * @param orgId The unique identifier of the organization.
   * @param sourcingAttributesDefinitionId The reference to the sourcing attributes definition.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with the list of group
   *     definitions.
   * @throws PromiseEngineException If an error occurs related to the promise engine.
   * @throws CommonServiceException If a general service-related error occurs while processing the
   *     request.
   */
  @GetGroupDefinitionListDoc
  @GetMapping(
      value = "list/orgId/{orgId}/sourcingAttributesDefinitionId/{sourcingAttributesDefinitionId}",
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<BaseResponse<GroupDefinitionListResponse>>
      getGroupDefinitionListByOrgIdAndSourcingAttributesDefinitionId(
          @NotBlank(message = "orgId can't be empty")
              @PathVariable
              @Parameter(
                  description = "Unique identifier of the organization.",
                  example = "NEXTUPLE")
              String orgId,
          @NotNull(message = "sourcingAttributesDefinitionId can't be null")
              @Min(value = 0)
              @PathVariable
              @Parameter(description = "Reference to the sourcing attributes definition.")
              Long sourcingAttributesDefinitionId)
          throws PromiseEngineException, CommonServiceException {
    logger.debug("Processing get group definition list request");
    try {
      var groupDefinitionResponse =
          groupDefinitionService
              .processGetGroupDefinitionListByOrgIdAndSourcingAttributesDefinitionId(
                  orgId, sourcingAttributesDefinitionId);
      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Node group successfully fetched")
              .payload(groupDefinitionResponse)
              .build());
    } catch (Exception e) {
      logger.error("Failed to process get group definition list request", e);
      throw e;
    }
  }

  /**
   * Updates an existing group definition.
   *
   * <p>This method processes a PUT request to update the details of an existing group definition
   * based on the provided request body.
   *
   * @param groupDefinitionRequest The request body containing updated details for the group
   *     definition.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with the updated group
   *     definition details.
   * @throws PromiseEngineException If an error occurs related to the promise engine.
   * @throws CommonServiceException If a general service-related error occurs while processing the
   *     request.
   */
  @UpdateGroupDefinitionDoc
  @PutMapping(
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<BaseResponse<GroupDefinitionResponse>> updateGroupDefinition(
      @Valid @RequestBody GroupDefinitionRequest groupDefinitionRequest)
      throws PromiseEngineException, CommonServiceException {
    logger.debug("Processing update group definition request");
    try {
      var groupDefinitionResponse =
          groupDefinitionService.processUpdateGroupDefinition(groupDefinitionRequest);
      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Group definition successfully added")
              .payload(groupDefinitionResponse)
              .build());
    } catch (Exception e) {
      logger.error("Failed to process update group definition request", e);
      throw e;
    }
  }

  /**
   * Deletes a group definition by its unique identifier and organization ID.
   *
   * <p>This method processes a DELETE request to remove a group definition identified by its ID and
   * associated organization ID.
   *
   * @param orgId The unique identifier of the organization.
   * @param id The unique identifier of the group definition to be deleted.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with the deleted group
   *     definition details.
   * @throws PromiseEngineException If an error occurs related to the promise engine.
   * @throws CommonServiceException If a general service-related error occurs while processing the
   *     request.
   */
  @DeleteGroupDefinitionDoc
  @DeleteMapping(value = "/orgId/{orgId}/id/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<BaseResponse<GroupDefinitionResponse>> deleteGroupDefinition(
      @NotBlank(message = "orgId can't be empty")
          @PathVariable
          @Parameter(description = "Unique identifier of the organization.", example = "NEXTUPLE")
          String orgId,
      @NotNull(message = "id can't be null")
          @Min(value = 0)
          @PathVariable
          @Parameter(description = "Unique identifier for group definition.", example = "1")
          Long id)
      throws PromiseEngineException, CommonServiceException {
    logger.debug("Processing delete group definition request");
    try {
      var groupDefinitionResponse = groupDefinitionService.processDeleteGroupDefinition(id, orgId);
      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Node group successfully deleted")
              .payload(groupDefinitionResponse)
              .build());
    } catch (Exception e) {
      logger.error("Failed to process delete group definition request", e);
      throw e;
    }
  }
}
