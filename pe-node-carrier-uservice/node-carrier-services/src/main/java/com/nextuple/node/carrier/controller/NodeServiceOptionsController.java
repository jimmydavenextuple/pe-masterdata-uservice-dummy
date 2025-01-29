/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.node.carrier.controller;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.node.carrier.controller.docs.*;
import com.nextuple.node.carrier.domain.constants.NodeCarrierConstants;
import com.nextuple.node.carrier.domain.inbound.NodeServiceOptionRequest;
import com.nextuple.node.carrier.domain.inbound.NodeServiceOptionUpdateRequest;
import com.nextuple.node.carrier.domain.outbound.NodeServiceOptionResponse;
import com.nextuple.node.carrier.exception.InvalidDataException;
import com.nextuple.node.carrier.service.NodeServiceOptionsService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * Controller that manages the Node Service Option APIs.
 *
 * <p>This controller provides endpoints for creating, updating, retrieving, and deleting Node
 * Service Options. The Node Service Option can be managed via the following actions: - Create a new
 * Node Service Option - Update an existing Node Service Option - Retrieve an existing Node Service
 * Option - Delete a Node Service Option
 *
 * <p>All API endpoints are tagged with "Node Service Option APIs" for easy categorization in API
 * documentation.
 */
@Validated
@RestController
@Tag(name = "Node Service Option APIs")
@RequestMapping("/v2/node/service-option")
@RequiredArgsConstructor
public class NodeServiceOptionsController {

  private final NodeServiceOptionsService nodeServiceOptionsService;
  private static final Logger logger = LoggerFactory.getLogger(NodeServiceOptionsController.class);

  /**
   * Creates a new Node Service Option based on the provided request.
   *
   * <p>This endpoint handles the creation of a Node Service Option. It accepts a request body
   * containing the details of the Node Service Option to be created, processes the request, and
   * returns the newly created Node Service Option in the response.
   *
   * @param nodeServiceOptionRequest the request body containing the details of the Node Service
   *     Option to be created. It must be a valid object, as annotated by {@link Valid}.
   * @return a {@link ResponseEntity} containing a {@link BaseResponse} with the newly created
   *     {@link NodeServiceOptionResponse}.
   * @throws CommonServiceException if an error occurs while processing the request.
   * @throws InvalidDataException if the provided data is invalid or doesn't meet the required
   *     criteria.
   */
  @CreateNodeServiceOptionDoc
  @PostMapping
  public ResponseEntity<BaseResponse<NodeServiceOptionResponse>> createNodeServiceOption(
      @Valid @RequestBody NodeServiceOptionRequest nodeServiceOptionRequest)
      throws CommonServiceException, InvalidDataException {
    logger.debug("Processing node service option creation request {}", nodeServiceOptionRequest);
    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Node Service Option successfully created")
            .payload(nodeServiceOptionsService.createNodeServiceOption(nodeServiceOptionRequest))
            .build());
  }

  /**
   * Updates an existing Node Service Option based on the provided request.
   *
   * <p>This endpoint handles the update of an existing Node Service Option. It accepts a request
   * body containing the updated details of the Node Service Option, identifies the Node Service
   * Option by the provided orgId, nodeId, and serviceOption, and returns the updated Node Service
   * Option in the response.
   *
   * @param orgId the ID of the organization to which the Node Service Option belongs.
   * @param nodeId the ID of the node to which the Node Service Option is associated.
   * @param serviceOption the service option to be updated.
   * @param nodeServiceOptionUpdateRequest the request body containing the updated details of the
   *     Node Service Option. It must be a valid object, as annotated by {@link Valid}.
   * @return a {@link ResponseEntity} containing a {@link BaseResponse} with the updated {@link
   *     NodeServiceOptionResponse}.
   * @throws CommonServiceException if an error occurs while processing the request.
   * @throws InvalidDataException if the provided data is invalid or doesn't meet the required
   *     criteria.
   */
  @UpdateNodeServiceOptionDoc
  @PutMapping("/{orgId}/{nodeId}/{serviceOption}")
  public ResponseEntity<BaseResponse<NodeServiceOptionResponse>> updateNodeServiceOption(
      @NotBlank(message = "orgId can't be empty")
          @Parameter(
              description = NodeCarrierConstants.ORG_ID,
              example = NodeCarrierConstants.ORG_ID_EXAMPLE)
          @PathVariable
          String orgId,
      @NotBlank(message = "nodeId can't be empty")
          @Parameter(
              description = NodeCarrierConstants.NODE_ID,
              example = NodeCarrierConstants.NODE_ID_EXAMPLE)
          @PathVariable
          String nodeId,
      @NotBlank(message = "serviceOption can't be empty")
          @Parameter(
              description = NodeCarrierConstants.SERVICE_OPTION,
              example = NodeCarrierConstants.SERVICE_OPTION_EXAMPLE)
          @PathVariable
          String serviceOption,
      @Valid @RequestBody NodeServiceOptionUpdateRequest nodeServiceOptionUpdateRequest)
      throws CommonServiceException, InvalidDataException {
    logger.debug(
        "Updating node service option by orgId {} and nodeId {} and serviceOption {} request",
        orgId,
        nodeId,
        serviceOption);
    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Node Service Option updated successfully")
            .payload(
                nodeServiceOptionsService.updateNodeServiceOption(
                    orgId, nodeId, serviceOption, nodeServiceOptionUpdateRequest))
            .build());
  }

  /**
   * Retrieves the Node Service Option based on the provided orgId, nodeId, and serviceOption.
   *
   * <p>This endpoint fetches the details of a Node Service Option by the specified orgId, nodeId,
   * and serviceOption.
   *
   * @param orgId the ID of the organization to which the Node Service Option belongs.
   * @param nodeId the ID of the node to which the Node Service Option is associated.
   * @param serviceOption the service option to be fetched.
   * @return a {@link ResponseEntity} containing a {@link BaseResponse} with the fetched {@link
   *     NodeServiceOptionResponse}.
   * @throws CommonServiceException if an error occurs while processing the request.
   */
  @GetNodeServiceOptionDoc
  @GetMapping("/{orgId}/{nodeId}/{serviceOption}")
  public ResponseEntity<BaseResponse<NodeServiceOptionResponse>> getNodeServiceOption(
      @NotBlank(message = "orgId can't be empty")
          @Parameter(
              description = NodeCarrierConstants.ORG_ID,
              example = NodeCarrierConstants.ORG_ID_EXAMPLE)
          @PathVariable
          String orgId,
      @NotBlank(message = "nodeId can't be empty")
          @Parameter(
              description = NodeCarrierConstants.NODE_ID,
              example = NodeCarrierConstants.NODE_ID_EXAMPLE)
          @PathVariable
          String nodeId,
      @NotBlank(message = "serviceOption can't be empty")
          @Parameter(
              description = NodeCarrierConstants.SERVICE_OPTION,
              example = NodeCarrierConstants.SERVICE_OPTION_EXAMPLE)
          @PathVariable
          String serviceOption)
      throws CommonServiceException {
    logger.debug(
        "Fetching node service option by orgId {} and nodeId {} and serviceOption {} request",
        orgId,
        nodeId,
        serviceOption);
    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Node Service Option fetched successfully")
            .payload(nodeServiceOptionsService.getNodeServiceOption(orgId, nodeId, serviceOption))
            .build());
  }

  /**
   * Deletes the Node Service Option based on the provided orgId, nodeId, and serviceOption.
   *
   * <p>This endpoint deletes a Node Service Option associated with the specified orgId, nodeId, and
   * serviceOption.
   *
   * @param orgId the ID of the organization from which the Node Service Option will be deleted.
   * @param nodeId the ID of the node from which the Node Service Option will be deleted.
   * @param serviceOption the service option to be deleted.
   * @return a {@link ResponseEntity} containing a {@link BaseResponse} with the deleted {@link
   *     NodeServiceOptionResponse}.
   * @throws CommonServiceException if an error occurs while processing the request.
   * @throws InvalidDataException if the provided data is invalid for deletion.
   */
  @DeleteNodeServiceOptionDoc
  @DeleteMapping("/{orgId}/{nodeId}/{serviceOption}")
  public ResponseEntity<BaseResponse<NodeServiceOptionResponse>> deleteNodeServiceOption(
      @NotBlank(message = "orgId can't be empty")
          @Parameter(
              description = NodeCarrierConstants.ORG_ID,
              example = NodeCarrierConstants.ORG_ID_EXAMPLE)
          @PathVariable
          String orgId,
      @NotBlank(message = "nodeId can't be empty")
          @Parameter(
              description = NodeCarrierConstants.NODE_ID,
              example = NodeCarrierConstants.NODE_ID_EXAMPLE)
          @PathVariable
          String nodeId,
      @NotBlank(message = "serviceOption can't be empty")
          @Parameter(
              description = NodeCarrierConstants.SERVICE_OPTION,
              example = NodeCarrierConstants.SERVICE_OPTION_EXAMPLE)
          @PathVariable
          String serviceOption)
      throws CommonServiceException, InvalidDataException {
    logger.debug(
        "Deleting node service option by orgId {} and nodeId {} and serviceOption {} request",
        orgId,
        nodeId,
        serviceOption);
    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Node Service Option deleted successfully")
            .payload(
                nodeServiceOptionsService.deleteNodeServiceOption(orgId, nodeId, serviceOption))
            .build());
  }

  /**
   * Fetches the list of Node Service Options based on the provided orgId and nodeId.
   *
   * <p>This endpoint retrieves a list of Node Service Options associated with the specified orgId
   * and nodeId.
   *
   * @param orgId the ID of the organization to fetch Node Service Options for.
   * @param nodeId the ID of the node to fetch Node Service Options for.
   * @return a {@link ResponseEntity} containing a {@link BaseResponse} with a list of {@link
   *     NodeServiceOptionResponse} objects.
   * @throws CommonServiceException if an error occurs while processing the request.
   */
  @GetNodeServiceOptionsListDoc
  @GetMapping("/{orgId}/{nodeId}")
  public ResponseEntity<BaseResponse<List<NodeServiceOptionResponse>>> getNodeServiceOptionsList(
      @NotBlank(message = "orgId can't be empty")
          @Parameter(
              description = NodeCarrierConstants.ORG_ID,
              example = NodeCarrierConstants.ORG_ID_EXAMPLE)
          @PathVariable
          String orgId,
      @NotBlank(message = "nodeId can't be empty")
          @Parameter(
              description = NodeCarrierConstants.NODE_ID,
              example = NodeCarrierConstants.NODE_ID_EXAMPLE)
          @PathVariable
          String nodeId)
      throws CommonServiceException {
    logger.debug("Processing get node carrier for nodeId and orgId");
    List<NodeServiceOptionResponse> nodeServiceOptionResponseList =
        nodeServiceOptionsService.getNodeServiceOptionList(orgId, nodeId);

    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Node Service Option list fetched successfully")
            .payload(nodeServiceOptionResponseList)
            .build());
  }
}
