/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.node.carrier.controller;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.node.carrier.controller.docs.CreateNodeServiceOptionDoc;
import com.nextuple.node.carrier.controller.docs.DeleteNodeServiceOptionDoc;
import com.nextuple.node.carrier.controller.docs.GetNodeServiceOptionDoc;
import com.nextuple.node.carrier.controller.docs.GetNodeServiceOptionsListDoc;
import com.nextuple.node.carrier.controller.docs.UpdateNodeServiceOptionDoc;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@Tag(name = "Node Service Option APIs")
@RequestMapping("/v2/node/service-option")
@RequiredArgsConstructor
public class NodeServiceOptionsController {

  private final NodeServiceOptionsService nodeServiceOptionsService;
  private static final Logger logger = LoggerFactory.getLogger(NodeServiceOptionsController.class);

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
