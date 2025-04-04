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
import com.nextuple.promise.sourcing.rule.api.domain.inbound.CreateNodeGroupRequest;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.UpdateNodeGroupRequest;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.NodeGroupResponse;
import com.nextuple.promise.sourcing.rule.controller.docs.CreateNodeGroupDoc;
import com.nextuple.promise.sourcing.rule.controller.docs.GetAllNodeGroupsByOrgIdDoc;
import com.nextuple.promise.sourcing.rule.controller.docs.GetNodeGroupDetailsDoc;
import com.nextuple.promise.sourcing.rule.controller.docs.UpdateNodeGroupDoc;
import com.nextuple.promise.sourcing.rule.persistence.domain.NodeGroupDomainDto;
import com.nextuple.promise.sourcing.rule.service.NodeGroupService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
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
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for managing node groups in an organization.
 *
 * <p>This controller provides APIs for adding, retrieving, updating, and deleting node groups. A
 * node group is a collection of nodes associated with an organization, which can be optimized and
 * managed for specific operations.
 *
 * <p>The controller is tagged with "Node Group APIs" for easy categorization in the API
 * documentation.
 */
@Validated
@RestController
@RequestMapping("/node-group")
@RequiredArgsConstructor
@Tag(name = "Node Group APIs")
public class NodeGroupController {

  private static final Logger logger = LoggerFactory.getLogger(NodeGroupController.class);

  private final NodeGroupService nodeGroupService;

  /**
   * Creates a new Node Group.
   *
   * <p>This method processes a POST request to create a Node Group with the details provided in the
   * request body.
   *
   * @param nodeGroupRequest The request body containing the details of the Node Group to be
   *     created.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with the details of the
   *     created Node Group.
   * @throws PromiseEngineException If an error occurs related to the promise engine.
   * @throws CommonServiceException If a general service-related error occurs.
   */
  @CreateNodeGroupDoc
  @PostMapping(
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<BaseResponse<NodeGroupResponse>> createNodeGroup(
      @Valid @RequestBody CreateNodeGroupRequest nodeGroupRequest)
      throws PromiseEngineException, CommonServiceException {
    logger.debug("Processing create Node Group request");
    try {
      var nodeGroupResponse = nodeGroupService.createNodeGroup(nodeGroupRequest);
      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Node Group successfully created!")
              .payload(nodeGroupResponse)
              .build());
    } catch (Exception e) {
      logger.error("Failed to process create node group request", e);
      throw e;
    }
  }

  /**
   * Retrieves a Node Group by its ID and organization ID.
   *
   * <p>This method processes a GET request to fetch the details of a Node Group using its unique ID
   * and organization ID.
   *
   * @param id The unique identifier of the Node Group.
   * @param orgId The unique identifier of the organization.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with the details of the Node
   *     Group.
   * @throws PromiseEngineException If an error occurs related to the promise engine.
   * @throws CommonServiceException If a general service-related error occurs.
   */
  @GetNodeGroupDetailsDoc
  @GetMapping(value = "/orgId/{orgId}/id/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<BaseResponse<NodeGroupResponse>> getNodeGroupByIdandOrgId(
      @NotNull(message = "id can't be null")
          @Min(value = 0)
          @PathVariable
          @Parameter(description = "Unique identifier of the node group.", example = "1")
          Long id,
      @NotBlank(message = "orgId can't be empty")
          @PathVariable
          @Parameter(description = "Unique identifier of the organization.", example = "NEXTUPLE")
          String orgId)
      throws PromiseEngineException, CommonServiceException {
    logger.debug("Processing get node group by id request");
    try {
      var nodeGroupResponse = nodeGroupService.fetchNodeGroupByIdandOrgId(id, orgId);
      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Node group successfully fetched")
              .payload(nodeGroupResponse)
              .build());
    } catch (Exception e) {
      logger.error("Failed to process get node group by id request", e);
      throw e;
    }
  }

  /**
   * Updates an existing Node Group.
   *
   * <p>This method processes a PUT request to update the details of an existing Node Group.
   *
   * @param id The unique identifier of the Node Group to be updated.
   * @param orgId The unique identifier of the organization.
   * @param updateNodeGroupRequest The request body containing the updated details of the Node
   *     Group.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with the updated Node Group
   *     details.
   * @throws PromiseEngineException If an error occurs related to the promise engine.
   * @throws CommonServiceException If a general service-related error occurs.
   */
  @UpdateNodeGroupDoc
  @PutMapping(
      value = "/orgId/{orgId}/id/{id}",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<BaseResponse<NodeGroupResponse>> updateNodeGroup(
      @NotNull(message = "id can't be null")
          @Min(value = 0)
          @PathVariable
          @Parameter(description = "Unique identifier for the node group", example = "1")
          Long id,
      @NotBlank(message = "orgId can't be empty")
          @PathVariable
          @Parameter(description = "Unique identifier of the organization.", example = "NEXTUPLE")
          String orgId,
      @Valid @RequestBody UpdateNodeGroupRequest updateNodeGroupRequest)
      throws PromiseEngineException, CommonServiceException {
    logger.debug("Processing update node group request");
    try {
      var nodeGroupResponse = nodeGroupService.updateNodeGroup(id, orgId, updateNodeGroupRequest);
      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Node Group Details successfully updated")
              .payload(nodeGroupResponse)
              .build());
    } catch (Exception e) {
      logger.error("Failed to process update node group request", e);
      throw e;
    }
  }

  /**
   * Retrieves a list of Node Groups for a specific organization.
   *
   * <p>This method processes a GET request to fetch all Node Groups associated with a given
   * organization ID.
   *
   * @param orgId The unique identifier of the organization.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with the list of Node
   *     Groups.
   * @throws PromiseEngineException If an error occurs related to the promise engine.
   */
  @GetMapping(value = "/orgId/{orgId}", produces = MediaType.APPLICATION_JSON_VALUE)
  @GetAllNodeGroupsByOrgIdDoc
  public ResponseEntity<BaseResponse<List<NodeGroupDomainDto>>> getNodeGroupListByOrgId(
      @NotBlank(message = "orgId can't be empty")
          @PathVariable
          @Parameter(description = "Unique identifier for organization.", example = "NEXTUPLE")
          String orgId)
      throws PromiseEngineException {
    try {
      var nodeGroupList = nodeGroupService.getNodeGroupListForOrgId(orgId);
      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Node Group List fetched successfully")
              .payload(nodeGroupList)
              .build());
    } catch (Exception e) {
      logger.error("Failed to process get node group list request", e);
      throw e;
    }
  }
}
