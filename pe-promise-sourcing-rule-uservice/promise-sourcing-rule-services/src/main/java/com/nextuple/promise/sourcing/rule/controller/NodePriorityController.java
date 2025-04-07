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
import com.nextuple.promise.sourcing.rule.api.domain.inbound.NodePriorityRequest;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.NodePriorityUpdationRequest;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.NodeGroupDetailsResponse;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.NodeGroupWithNodesResponse;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.NodePriorityResponse;
import com.nextuple.promise.sourcing.rule.controller.docs.AddNodeToNodeGroupDoc;
import com.nextuple.promise.sourcing.rule.controller.docs.DeleteAllNodesFromNodeGroupDoc;
import com.nextuple.promise.sourcing.rule.controller.docs.GetNodePriorityByNodeId;
import com.nextuple.promise.sourcing.rule.controller.docs.GetNodePriorityDetailsDoc;
import com.nextuple.promise.sourcing.rule.controller.docs.GetNodesAssociatedToNodeGroupDoc;
import com.nextuple.promise.sourcing.rule.controller.docs.RemoveNodeFromNodeGroupDoc;
import com.nextuple.promise.sourcing.rule.controller.docs.UpdateNodePriorityDoc;
import com.nextuple.promise.sourcing.rule.controller.docs.UpdateNodesWithinNodeGroupDoc;
import com.nextuple.promise.sourcing.rule.persistence.domain.NodePriorityDomainDto;
import com.nextuple.promise.sourcing.rule.service.NodePriorityService;
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
 * Controller for managing node priorities within Node Groups in an organization.
 *
 * <p>This controller provides APIs for adding, updating, retrieving, and removing nodes and their
 * priorities within a node group in an organization. A node group is a collection of nodes, and the
 * node priorities dictate the order or importance of nodes within that group.
 *
 * <p>The controller is tagged with "Node Priority APIs" for easy categorization in the API
 * documentation.
 */
@Validated
@RestController
@RequestMapping("/node-priority")
@RequiredArgsConstructor
@Tag(name = "Node Priority APIs")
public class NodePriorityController {

  private static final Logger logger = LoggerFactory.getLogger(NodePriorityController.class);

  private final NodePriorityService nodePriorityService;

  /**
   * Adds a Node Priority to a Node Group.
   *
   * <p>This method processes a POST request to associate a Node with a Node Group and define its
   * priority.
   *
   * @param nodePriorityRequest The details of the Node Priority to be added.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with the details of the
   *     added Node Priority.
   * @throws PromiseEngineException If an error occurs related to the promise engine.
   * @throws CommonServiceException If a general service-related error occurs.
   */
  @AddNodeToNodeGroupDoc
  @PostMapping(
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<BaseResponse<NodePriorityResponse>> addNodePriorityToNodeGroup(
      @Valid @RequestBody NodePriorityRequest nodePriorityRequest)
      throws PromiseEngineException, CommonServiceException {
    logger.debug("Processing add node priority to node group request");
    try {
      var nodePriorityResponse =
          nodePriorityService.processAddNodePriorityToNodeGroup(nodePriorityRequest);
      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Node successfully added to node group")
              .payload(nodePriorityResponse)
              .build());
    } catch (Exception e) {
      logger.error("Failed to process add node priority to node group request", e);
      throw e;
    }
  }

  /**
   * Fetches the Nodes associated with a specific Node Group.
   *
   * <p>This method processes a GET request to retrieve all Nodes belonging to a given Node Group.
   *
   * @param orgId The unique identifier of the organization.
   * @param nodeGroupId The unique identifier of the Node Group.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with the list of Nodes in
   *     the Node Group.
   * @throws PromiseEngineException If an error occurs related to the promise engine.
   * @throws CommonServiceException If a general service-related error occurs.
   */
  @GetNodesAssociatedToNodeGroupDoc
  @GetMapping(
      value = "/list/orgId/{orgId}/nodeGroupId/{nodeGroupId}",
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<BaseResponse<NodeGroupDetailsResponse>> getNodesAssociatedToANodeGroup(
      @NotBlank(message = "orgId can't be blank")
          @PathVariable
          @Parameter(description = "Unique identifier of organization.", example = "NEXTUPLE")
          String orgId,
      @NotNull(message = "nodeGroupId can't be null")
          @Min(value = 0)
          @PathVariable
          @Parameter(description = "Unique identifier for the node group.", example = "NG1")
          Long nodeGroupId)
      throws PromiseEngineException, CommonServiceException {
    logger.debug("Processing get nodes associated to a node group request");
    try {
      var nodeGroupDetailsList =
          nodePriorityService.processGetNodesAssociatedToANodeGroup(orgId, nodeGroupId);
      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Nodes associated to a node group successfully fetched")
              .payload(nodeGroupDetailsList)
              .build());
    } catch (Exception e) {
      logger.error("Failed to process get nodes associated to a node group request", e);
      throw e;
    }
  }

  /**
   * Retrieves the priority details of a Node within an organization.
   *
   * <p>This method processes a GET request to fetch the priority details of a specific Node.
   *
   * @param orgId The unique identifier of the organization.
   * @param id The unique identifier of the Node.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with the Node Priority
   *     details.
   * @throws PromiseEngineException If an error occurs related to the promise engine.
   * @throws CommonServiceException If a general service-related error occurs.
   */
  @GetNodePriorityDetailsDoc
  @GetMapping(value = "/orgId/{orgId}/id/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<BaseResponse<NodePriorityResponse>> getNodePriorityDetailsByOrgIdAndId(
      @NotBlank(message = "orgId can't be empty")
          @PathVariable
          @Parameter(description = "Unique identifier of the organization.", example = "NEXTUPLE")
          String orgId,
      @NotNull(message = "id can't be null")
          @Min(value = 0)
          @PathVariable
          @Parameter(description = "Unique identifier for the node.", example = "23")
          Long id)
      throws PromiseEngineException, CommonServiceException {
    logger.debug("Processing get node priority details by id");
    try {
      var nodePriorityResponse =
          nodePriorityService.processGetNodePriorityDetailsByIdAndOrgId(id, orgId);
      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Node priority details successfully fetched")
              .payload(nodePriorityResponse)
              .build());
    } catch (Exception e) {
      logger.error("Failed to process get node priority details by id", e);
      throw e;
    }
  }

  /**
   * Retrieves the priority details of a Node within an organization.
   *
   * <p>This method processes a GET request to fetch the priority details of a specific Node.
   *
   * @param orgId The unique identifier of the organization.
   * @param nodeId The unique identifier of the Node.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with the Node Priority
   *     details.
   * @throws PromiseEngineException If an error occurs related to the promise engine.
   * @throws CommonServiceException If a general service-related error occurs.
   */
  @GetNodePriorityByNodeId
  @GetMapping(value = "/orgId/{orgId}/nodeId/{nodeId}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<BaseResponse<List<NodePriorityResponse>>>
      getNodePriorityDetailsByOrgIdAndNodeId(
          @NotBlank(message = "orgId can't be blank")
              @PathVariable
              @Parameter(
                  description = "Unique identifier of the organization.",
                  example = "NEXTUPLE")
              String orgId,
          @NotBlank(message = "nodeId can't be blank")
              @PathVariable
              @Parameter(description = "Unique identifier for the node.", example = "Node-1")
              String nodeId)
          throws PromiseEngineException, CommonServiceException {
    try {
      var nodePriorityResponse =
          nodePriorityService.getNodePriorityListByNodeIdAndOrgId(nodeId, orgId);
      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Node priority details successfully fetched")
              .payload(nodePriorityResponse)
              .build());
    } catch (Exception e) {
      logger.error("Failed to process get node priority details by nodeId and orgId", e);
      throw e;
    }
  }

  /**
   * Updates the priority of a Node within a Node Group.
   *
   * <p>This method processes a PUT request to modify the priority of a Node in a specific Node
   * Group.
   *
   * @param orgId The unique identifier of the organization.
   * @param nodeGroupId The unique identifier of the Node Group.
   * @param nodeId The unique identifier of the Node.
   * @param nodePriorityUpdationRequest The details for updating the Node Priority.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with the updated Node
   *     Priority details.
   * @throws PromiseEngineException If an error occurs related to the promise engine.
   * @throws CommonServiceException If a general service-related error occurs.
   */
  @UpdateNodePriorityDoc
  @PutMapping(
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<BaseResponse<NodePriorityResponse>> updatePriorityWithinNodeGroup(
      @NotBlank(message = "orgId can't be empty")
          @RequestParam
          @Parameter(description = "Unique identifier for organization.", example = "NEXTUPLE")
          String orgId,
      @NotNull(message = "nodeGroupId can't be null")
          @Parameter(description = "Unique identifier of the node group.", example = "NG1")
          @RequestParam
          Long nodeGroupId,
      @NotBlank(message = "nodeId can't be empty")
          @Parameter(description = "Unique identifier of the node.", example = "NODE-01")
          @RequestParam
          String nodeId,
      @Valid @RequestBody NodePriorityUpdationRequest nodePriorityUpdationRequest)
      throws PromiseEngineException, CommonServiceException {
    logger.debug("Processing updating node priority within node group request");
    try {
      var nodePriorityResponse =
          nodePriorityService.processUpdateNodePriorityWithinNodeGroup(
              orgId, nodeGroupId, nodeId, nodePriorityUpdationRequest);
      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Node priority successfully updated")
              .payload(nodePriorityResponse)
              .build());
    } catch (Exception e) {
      logger.error("Failed to process update node priority within node group request", e);
      throw e;
    }
  }

  /**
   * Removes a Node from a Node Group.
   *
   * <p>This method processes a DELETE request to dissociate a Node from a Node Group.
   *
   * @param orgId The unique identifier of the organization.
   * @param nodeGroupId The unique identifier of the Node Group.
   * @param nodeId The unique identifier of the Node to be removed.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with the details of the
   *     removed Node.
   * @throws PromiseEngineException If an error occurs related to the promise engine.
   * @throws CommonServiceException If a general service-related error occurs.
   */
  @RemoveNodeFromNodeGroupDoc
  @DeleteMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<BaseResponse<NodePriorityResponse>> removeNodeFromNodeGroup(
      @NotBlank(message = "orgId can't be empty")
          @RequestParam
          @Parameter(description = "Unique identifier of the organization.", example = "NEXTUPLE")
          String orgId,
      @NotNull(message = "nodeGroupId can't be null")
          @Parameter(description = "Unique identifier of the node group.", example = "NG1")
          @RequestParam
          Long nodeGroupId,
      @NotBlank(message = "nodeId can't be empty")
          @Parameter(description = "Unique identifier of the node.", example = "NODE-01")
          @RequestParam
          String nodeId)
      throws PromiseEngineException, CommonServiceException {
    logger.debug("Processing removing node from node group request");
    try {
      var nodePriorityResponse =
          nodePriorityService.processRemoveNodeFromNodeGroup(orgId, nodeGroupId, nodeId);
      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Node successfully removed from the node group")
              .payload(nodePriorityResponse)
              .build());
    } catch (Exception e) {
      logger.error("Failed to process remove node from node group request", e);
      throw e;
    }
  }

  /**
   * Updates multiple Nodes within a Node Group.
   *
   * <p>This method processes a PUT request to modify the association and details of Nodes in a Node
   * Group.
   *
   * @param orgId The unique identifier of the organization.
   * @param nodeGroupName The name of the Node Group to update.
   * @param nodeGroupWithNodes The updated details of the Node Group and its associated Nodes.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with the updated Node Group
   *     details.
   * @throws PromiseEngineException If an error occurs related to the promise engine.
   * @throws CommonServiceException If a general service-related error occurs.
   */
  @UpdateNodesWithinNodeGroupDoc
  @PutMapping(
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE,
      value = "/orgId/{orgId}/nodeGroupName/{nodeGroupName}")
  public ResponseEntity<BaseResponse<NodeGroupWithNodesResponse>> updateNodesWithinNodeGroup(
      @NotBlank(message = "orgId cannot be blank")
          @PathVariable
          @Parameter(description = "Unique identifier of the organization.", example = "NEXTUPLE")
          String orgId,
      @NotBlank(message = "nodeGroupName cannot be blank")
          @PathVariable
          @Parameter(description = "Specifies the name of the node group.", example = "NEXTUPLE")
          String nodeGroupName,
      @RequestBody NodeGroupWithNodesResponse nodeGroupWithNodes)
      throws PromiseEngineException, CommonServiceException {
    try {
      NodeGroupWithNodesResponse response =
          nodePriorityService.editNodesWithinNodeGroup(orgId, nodeGroupName, nodeGroupWithNodes);
      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Node group updated successfully.")
              .payload(response)
              .build());
    } catch (Exception e) {
      logger.error("Failed to update node group", e);
      throw e;
    }
  }

  /**
   * Removes all Nodes from a specific Node Group.
   *
   * <p>This method processes a DELETE request to remove all Nodes associated with a given Node
   * Group.
   *
   * @param orgId The unique identifier of the organization.
   * @param nodeGroupId The unique identifier of the Node Group.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with the details of the
   *     removed Nodes.
   * @throws PromiseEngineException If an error occurs related to the promise engine.
   * @throws CommonServiceException If a general service-related error occurs.
   */
  @DeleteMapping(
      produces = MediaType.APPLICATION_JSON_VALUE,
      value = "/orgId/{orgId}/nodeGroupId/{nodeGroupId}")
  @DeleteAllNodesFromNodeGroupDoc
  public ResponseEntity<BaseResponse<List<NodePriorityDomainDto>>> removeAllNodesFromTheNodeGroup(
      @NotBlank(message = "orgId can't be empty")
          @PathVariable
          @Parameter(description = "Unique identifier for organization.", example = "NEXTUPLE")
          String orgId,
      @NotNull(message = "nodeGroupId can't be null")
          @Parameter(description = "Unique identifier for node group.", example = "NG1")
          @PathVariable
          Long nodeGroupId)
      throws PromiseEngineException, CommonServiceException {
    logger.debug("Processing removing node from node group request");
    try {
      var nodePriorityResponse =
          nodePriorityService.deleteNodesFromTheNodeGroup(orgId, nodeGroupId);
      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Node successfully removed from the node group")
              .payload(nodePriorityResponse)
              .build());
    } catch (Exception e) {
      logger.error("Failed to process remove node from node group request", e);
      throw e;
    }
  }
}
