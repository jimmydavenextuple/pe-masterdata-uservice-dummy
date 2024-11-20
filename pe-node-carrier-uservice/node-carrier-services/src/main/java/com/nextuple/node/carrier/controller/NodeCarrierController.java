/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.node.carrier.controller;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.node.carrier.controller.docs.AddNodeCarrierSelectionPriorityDoc;
import com.nextuple.node.carrier.controller.docs.CreateNodeCarrierDoc;
import com.nextuple.node.carrier.controller.docs.DeleteNodeCarrierByOrgIdAndServiceOption;
import com.nextuple.node.carrier.controller.docs.DeleteNodeCarrierDoc;
import com.nextuple.node.carrier.controller.docs.DeleteNodeCarrierDocSelectionDetailsDoc;
import com.nextuple.node.carrier.controller.docs.GetAllNodeCarriersByOrgIdCarrierServiceIdDoc;
import com.nextuple.node.carrier.controller.docs.GetAllNodeCarriersByOrgIdDoc;
import com.nextuple.node.carrier.controller.docs.GetNodeCarrierAssociationDoc;
import com.nextuple.node.carrier.controller.docs.GetNodeCarrierDoc;
import com.nextuple.node.carrier.controller.docs.GetNodeCarrierListCacheKeysDoc;
import com.nextuple.node.carrier.controller.docs.GetNodeCarrierListDoc;
import com.nextuple.node.carrier.controller.docs.GetNodeCarrierListWithLastPickUpTimeDetailsDoc;
import com.nextuple.node.carrier.controller.docs.GetNodeCarrierSelectionDetailsDoc;
import com.nextuple.node.carrier.controller.docs.GetUniqueNodeCarrierServiceListDoc;
import com.nextuple.node.carrier.controller.docs.UpdateBufferDoc;
import com.nextuple.node.carrier.controller.docs.UpdateNodeCarrierDoc;
import com.nextuple.node.carrier.controller.docs.UpdateProcessingLeadTimeDoc;
import com.nextuple.node.carrier.domain.constants.NodeCarrierConstants;
import com.nextuple.node.carrier.domain.dto.NodeCarrierListCacheKeyDto;
import com.nextuple.node.carrier.domain.inbound.NodeCarrierBufferRequest;
import com.nextuple.node.carrier.domain.inbound.NodeCarrierRequest;
import com.nextuple.node.carrier.domain.inbound.NodeCarrierSelectionRequest;
import com.nextuple.node.carrier.domain.inbound.NodeCarrierUpdateRequest;
import com.nextuple.node.carrier.domain.outbound.NodeCarrierResponse;
import com.nextuple.node.carrier.domain.outbound.NodeCarrierSelectionResponse;
import com.nextuple.node.carrier.exception.InvalidDataException;
import com.nextuple.node.carrier.exception.NodeCarrierDomainException;
import com.nextuple.node.carrier.exception.NodeCarrierSelectionDomainException;
import com.nextuple.node.carrier.service.NodeCarrierService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@Tag(name = "Node Carrier APIs")
@RequestMapping("/node/carrier")
@RequiredArgsConstructor
public class NodeCarrierController {
  private static final Logger logger = LoggerFactory.getLogger(NodeCarrierController.class);
  private static final String NODE_CARRIER_SUCCESS_MESSAGE =
      "Node Carrier list fetched successfully";

  private final NodeCarrierService nodeCarrierService;

  @CreateNodeCarrierDoc
  @PostMapping
  public ResponseEntity<BaseResponse<NodeCarrierResponse>> createNodeCarrier(
      @Valid @RequestBody NodeCarrierRequest nodeCarrierRequest)
      throws NodeCarrierDomainException, InvalidDataException, CommonServiceException {
    logger.debug("Processing node carrier creation request");
    try {
      var nodeCarrierResponse = nodeCarrierService.createNodeCarrier(nodeCarrierRequest);
      logger.info("Response after creation of node-carrier :{}", nodeCarrierResponse);
      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Node Carrier successfully created")
              .payload(nodeCarrierResponse)
              .build());
    } catch (Exception e) {
      logger.error("Failed to create node carrier details");
      throw e;
    }
  }

  @UpdateBufferDoc
  @PutMapping("/buffer")
  public ResponseEntity<BaseResponse<NodeCarrierResponse>> updateBuffer(
      @Valid @RequestBody NodeCarrierBufferRequest nodeCarrierBufferRequest)
      throws NodeCarrierDomainException, CommonServiceException {
    logger.debug("Processing buffer data creation or updation");
    try {
      var nodeCarrierResponse = nodeCarrierService.updateBufferData(nodeCarrierBufferRequest);
      logger.info("Response after creation or updation of buffer data : {}", nodeCarrierResponse);
      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Buffer data successfully added.")
              .payload(nodeCarrierResponse)
              .build());
    } catch (Exception e) {
      logger.error("Failed to update node carrier buffer details");
      throw e;
    }
  }

  @GetNodeCarrierDoc
  @GetMapping("/{nodeId}/{orgId}/{carrierServiceId}/{serviceOption}")
  public ResponseEntity<BaseResponse<NodeCarrierResponse>> getNodeCarrier(
      @NotBlank(message = "nodeId can't be empty")
          @Parameter(
              description = NodeCarrierConstants.NODE_ID,
              example = NodeCarrierConstants.NODE_ID_EXAMPLE)
          @PathVariable
          String nodeId,
      @NotBlank(message = "orgId can't be empty")
          @Parameter(
              description = NodeCarrierConstants.ORG_ID,
              example = NodeCarrierConstants.ORG_ID_EXAMPLE)
          @PathVariable
          String orgId,
      @NotBlank(message = "carrierServiceId can't be empty")
          @Parameter(
              description = NodeCarrierConstants.CARRIER_SERVICE_ID,
              example = NodeCarrierConstants.CARRIER_SERVICE_ID_EXAMPLE)
          @PathVariable
          String carrierServiceId,
      @NotBlank(message = "serviceOption can't be empty")
          @Parameter(
              description = NodeCarrierConstants.SERVICE_OPTION,
              example = NodeCarrierConstants.SERVICE_OPTION_EXAMPLE)
          @PathVariable
          String serviceOption)
      throws NodeCarrierDomainException, CommonServiceException {
    logger.debug("Processing get node carrier details");
    try {
      var nodeCarrierResponse =
          nodeCarrierService.getNodeCarrierDetails(nodeId, orgId, carrierServiceId, serviceOption);

      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Node Carrier details fetched successfully")
              .payload(nodeCarrierResponse)
              .build());
    } catch (Exception e) {
      logger.error("Failed to get node carrier details");
      throw e;
    }
  }

  @UpdateNodeCarrierDoc
  @PutMapping("/{nodeId}/{orgId}/{carrierServiceId}/{serviceOption}")
  public ResponseEntity<BaseResponse<NodeCarrierResponse>> updateNodeCarrier(
      @NotBlank(message = "nodeId can't be empty")
          @Parameter(
              description = NodeCarrierConstants.NODE_ID,
              example = NodeCarrierConstants.NODE_ID_EXAMPLE)
          @PathVariable
          String nodeId,
      @NotBlank(message = "orgId can't be empty")
          @Parameter(
              description = NodeCarrierConstants.ORG_ID,
              example = NodeCarrierConstants.ORG_ID_EXAMPLE)
          @PathVariable
          String orgId,
      @NotBlank(message = "carrierServiceId can't be empty")
          @Parameter(
              description = NodeCarrierConstants.CARRIER_SERVICE_ID,
              example = NodeCarrierConstants.CARRIER_SERVICE_ID_EXAMPLE)
          @PathVariable
          String carrierServiceId,
      @NotBlank(message = "serviceOption can't be empty")
          @Parameter(
              description = NodeCarrierConstants.SERVICE_OPTION,
              example = NodeCarrierConstants.SERVICE_OPTION_EXAMPLE)
          @PathVariable
          String serviceOption,
      @Valid @RequestBody NodeCarrierUpdateRequest nodeCarrierUpdateRequest)
      throws NodeCarrierDomainException, CommonServiceException, InvalidDataException {
    logger.debug("Processing update node carrier details");

    try {
      var nodeCarrierResponse =
          nodeCarrierService.updateNodeCarrier(
              nodeId, orgId, carrierServiceId, serviceOption, nodeCarrierUpdateRequest);
      logger.info("Response after updation of node-carrier :{}", nodeCarrierResponse);
      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Node Carrier updated successfully")
              .payload(nodeCarrierResponse)
              .build());
    } catch (Exception e) {
      logger.error("Failed to update node carrier details");
      throw e;
    }
  }

  @DeleteNodeCarrierDoc
  @DeleteMapping("/{nodeId}/{orgId}/{carrierServiceId}/{serviceOption}")
  public ResponseEntity<BaseResponse<NodeCarrierResponse>> deleteNodeCarrier(
      @NotBlank(message = "nodeId can't be empty")
          @Parameter(
              description = NodeCarrierConstants.NODE_ID,
              example = NodeCarrierConstants.NODE_ID_EXAMPLE)
          @PathVariable
          String nodeId,
      @NotBlank(message = "orgId can't be empty")
          @Parameter(
              description = NodeCarrierConstants.ORG_ID,
              example = NodeCarrierConstants.ORG_ID_EXAMPLE)
          @PathVariable
          String orgId,
      @NotBlank(message = "carrierServiceId can't be empty")
          @Parameter(
              description = NodeCarrierConstants.CARRIER_SERVICE_ID,
              example = NodeCarrierConstants.CARRIER_SERVICE_ID_EXAMPLE)
          @PathVariable
          String carrierServiceId,
      @NotBlank(message = "serviceOption can't be empty")
          @Parameter(
              description = NodeCarrierConstants.SERVICE_OPTION,
              example = NodeCarrierConstants.SERVICE_OPTION_EXAMPLE)
          @PathVariable
          String serviceOption)
      throws NodeCarrierDomainException, CommonServiceException {
    logger.debug("Processing delete node carrier");
    try {
      var nodeCarrierResponse =
          nodeCarrierService.deleteNodeCarrier(nodeId, orgId, carrierServiceId, serviceOption);
      logger.info("Response after deletion of node-carrier :{}", nodeCarrierResponse);
      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Node Carrier deleted successfully")
              .payload(nodeCarrierResponse)
              .build());
    } catch (Exception e) {
      logger.error("Failed to delete node carrier details");
      throw e;
    }
  }

  @GetNodeCarrierAssociationDoc
  @GetMapping("/{nodeId}/{orgId}/{serviceOption}")
  public ResponseEntity<BaseResponse<List<NodeCarrierResponse>>> getNodeCarrier(
      @NotBlank(message = "nodeId can't be empty")
          @Parameter(
              description = NodeCarrierConstants.NODE_ID,
              example = NodeCarrierConstants.NODE_ID_EXAMPLE)
          @PathVariable
          String nodeId,
      @NotBlank(message = "orgId can't be empty")
          @Parameter(
              description = NodeCarrierConstants.ORG_ID,
              example = NodeCarrierConstants.ORG_ID_EXAMPLE)
          @PathVariable
          String orgId,
      @NotBlank(message = "serviceOption can't be empty")
          @Parameter(
              description = NodeCarrierConstants.SERVICE_OPTION,
              example = NodeCarrierConstants.SERVICE_OPTION_EXAMPLE)
          @PathVariable
          String serviceOption)
      throws NodeCarrierDomainException, CommonServiceException {
    logger.debug("Processing get node carrier for nodeId, orgId and serviceOption");
    try {
      List<NodeCarrierResponse> nodeCarrierResponseList =
          nodeCarrierService.getNodeCarrierForNodeIdAOrgIdAndServiceOption(
              nodeId, orgId, serviceOption);

      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Node Carrier details list fetched successfully")
              .payload(nodeCarrierResponseList)
              .build());
    } catch (Exception e) {
      logger.error("Failed to fetch node carrier details list");
      throw e;
    }
  }

  @GetNodeCarrierListDoc
  @GetMapping("/{nodeId}/{orgId}")
  public ResponseEntity<BaseResponse<List<NodeCarrierResponse>>> getNodeCarrierList(
      @NotBlank(message = "nodeId can't be empty")
          @Parameter(
              description = NodeCarrierConstants.NODE_ID,
              example = NodeCarrierConstants.NODE_ID_EXAMPLE)
          @PathVariable
          String nodeId,
      @NotBlank(message = "orgId can't be empty")
          @Parameter(
              description = NodeCarrierConstants.ORG_ID,
              example = NodeCarrierConstants.ORG_ID_EXAMPLE)
          @PathVariable
          String orgId)
      throws NodeCarrierDomainException {
    logger.debug("Processing get node carrier for nodeId and orgId");
    List<NodeCarrierResponse> nodeCarrierResponseList =
        nodeCarrierService.getNodeCarrierForNodeIdAndOrgId(nodeId, orgId);

    return ResponseEntity.ok(
        BaseResponse.builder()
            .message(NODE_CARRIER_SUCCESS_MESSAGE)
            .payload(nodeCarrierResponseList)
            .build());
  }

  @UpdateProcessingLeadTimeDoc
  @PostMapping("/processing-lead-time")
  public ResponseEntity<BaseResponse<NodeCarrierResponse>> updateProcessingLeadTime(
      @Valid @RequestBody NodeCarrierRequest nodeCarrierRequest) throws NodeCarrierDomainException {
    logger.debug("Processing update processing lead time request");

    var nodeCarrierResponse = nodeCarrierService.updateProcessingLeadTime(nodeCarrierRequest);
    logger.info("Response after updating processing lead time :{}", nodeCarrierResponse);
    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Processing lead time updated successfully for a node carrier")
            .payload(nodeCarrierResponse)
            .build());
  }

  @AddNodeCarrierSelectionPriorityDoc
  @PostMapping("/node-carrier-selection")
  public ResponseEntity<BaseResponse<NodeCarrierSelectionResponse>> addNodeCarrierSelectionPriority(
      @Valid @RequestBody NodeCarrierSelectionRequest nodeCarrierSelectionRequest) {
    var nodeCarrierSelectionPriorityResponse =
        nodeCarrierService.addNodeCarrierSelectionPriority(nodeCarrierSelectionRequest);
    logger.info(
        "Response after addition of node-carrier selection priority :{}",
        nodeCarrierSelectionPriorityResponse);
    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Node Carrier selection priority successfully added")
            .payload(nodeCarrierSelectionPriorityResponse)
            .build());
  }

  @GetNodeCarrierSelectionDetailsDoc
  @GetMapping("/node-carrier-selection/{orgId}/{serviceOption}/{destinationGeozone}")
  public ResponseEntity<BaseResponse<List<NodeCarrierSelectionResponse>>>
      getNodeCarrierSelectionDetails(
          @NotBlank(message = "orgId cannot be empty")
              @Parameter(
                  description = NodeCarrierConstants.ORG_ID,
                  example = NodeCarrierConstants.ORG_ID_EXAMPLE)
              @PathVariable
              String orgId,
          @NotBlank(message = "serviceOption cannot be empty")
              @Parameter(
                  description = NodeCarrierConstants.SERVICE_OPTION,
                  example = NodeCarrierConstants.SERVICE_OPTION_EXAMPLE)
              @PathVariable
              String serviceOption,
          @NotBlank(message = "destinationGeozone cannot be empty")
              @Parameter(
                  description = NodeCarrierConstants.DEST_GEOZONE,
                  example = NodeCarrierConstants.GEOZONE_EXAMPLE)
              @PathVariable
              String destinationGeozone) {
    var nodeCarrierSelectionList =
        nodeCarrierService.getNodeCarrierSelectionDetails(orgId, serviceOption, destinationGeozone);

    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Node Carrier selection details fetched successfully")
            .payload(nodeCarrierSelectionList)
            .build());
  }

  @DeleteNodeCarrierByOrgIdAndServiceOption
  @DeleteMapping("/{nodeId}/{orgId}/{serviceOption}")
  public ResponseEntity<BaseResponse<NodeCarrierResponse>>
      deleteNodeCarrierByOrgIdNodeIdAndServiceOption(
          @NotBlank(message = "nodeId cannot be empty")
              @Parameter(
                  description = NodeCarrierConstants.NODE_ID,
                  example = NodeCarrierConstants.NODE_ID_EXAMPLE)
              @PathVariable
              String nodeId,
          @NotBlank(message = "orgId cannot be empty")
              @Parameter(
                  description = NodeCarrierConstants.ORG_ID,
                  example = NodeCarrierConstants.ORG_ID_EXAMPLE)
              @PathVariable
              String orgId,
          @Parameter(
                  description = "Specifies the unique identifier of the carrier service.",
                  example = "123")
              @RequestParam(required = false)
              String carrierServiceId,
          @NotBlank(message = "serviceOption cannot be empty")
              @Parameter(
                  description = NodeCarrierConstants.SERVICE_OPTION,
                  example = NodeCarrierConstants.SERVICE_OPTION_EXAMPLE)
              @PathVariable
              String serviceOption)
          throws NodeCarrierDomainException, CommonServiceException {
    logger.debug("Processing delete node carrier");

    var nodeCarrierResponse =
        nodeCarrierService.deleteNodeCarrier(nodeId, orgId, carrierServiceId, serviceOption);
    logger.info("Response after deletion of node-carrier :{}", nodeCarrierResponse);
    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Node Carrier deleted successfully")
            .payload(nodeCarrierResponse)
            .build());
  }

  @GetNodeCarrierListCacheKeysDoc
  @GetMapping("/get-all-cache-keys")
  public ResponseEntity<BaseResponse<List<NodeCarrierListCacheKeyDto>>> getNodeCarrierListCacheKeys(
      @RequestParam
          @Parameter(
              description = "Specifies the number of rows to be returned from DB.",
              example = "1")
          Integer limit)
      throws NodeCarrierDomainException {
    logger.debug("Processing get Node Carrier List Cache Keys");

    var response = nodeCarrierService.getAllNodeCarrierCacheKeys(limit);

    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Node Carrier Cache Keys fetched successfully")
            .payload(response)
            .build());
  }

  @DeleteNodeCarrierDocSelectionDetailsDoc
  @DeleteMapping("/node-carrier-selection")
  public ResponseEntity<BaseResponse<NodeCarrierSelectionResponse>>
      deleteNodeCarrierSelectionDetails(
          @Valid @RequestBody NodeCarrierSelectionRequest nodeCarrierSelectionRequest)
          throws CommonServiceException, NodeCarrierSelectionDomainException {
    var nodeCarrierSelectionResponse =
        nodeCarrierService.deleteNodeCarrierSelection(nodeCarrierSelectionRequest);

    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Node Carrier selection deleted successfully")
            .payload(nodeCarrierSelectionResponse)
            .build());
  }

  @GetUniqueNodeCarrierServiceListDoc
  @GetMapping("/{orgId}/{nodeId}/carrier-service")
  public ResponseEntity<BaseResponse<List<String>>> getUniqueNodeCarrierServiceList(
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
      throws NodeCarrierDomainException {
    logger.debug("Processing get list of unique node-carrier-service");
    return ResponseEntity.ok(
        BaseResponse.builder()
            .payload(nodeCarrierService.getUniqueNodeCarrierServiceList(nodeId, orgId))
            .build());
  }

  @GetNodeCarrierListWithLastPickUpTimeDetailsDoc
  @GetMapping("/v1/{nodeId}/{orgId}")
  public ResponseEntity<BaseResponse<List<NodeCarrierResponse>>>
      getNodeCarrierListWithLastPickUpTimeDetails(
          @NotBlank(message = "nodeId can't be empty")
              @Parameter(
                  description = NodeCarrierConstants.NODE_ID,
                  example = NodeCarrierConstants.NODE_ID_EXAMPLE)
              @PathVariable
              String nodeId,
          @NotBlank(message = "orgId can't be empty")
              @Parameter(
                  description = NodeCarrierConstants.ORG_ID,
                  example = NodeCarrierConstants.ORG_ID_EXAMPLE)
              @PathVariable
              String orgId)
          throws NodeCarrierDomainException {
    logger.debug("Processing get node carrier for nodeId and orgId");
    List<NodeCarrierResponse> nodeCarrierResponseList =
        nodeCarrierService.getNodeCarrierListForNodeIdAndOrgId(nodeId, orgId);

    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Node Carrier list with last pickup time details fetched successfully")
            .payload(nodeCarrierResponseList)
            .build());
  }

  @GetAllNodeCarriersByOrgIdDoc
  @GetMapping("/{orgId}/node-carriers")
  public ResponseEntity<BaseResponse<List<NodeCarrierResponse>>> getAllNodeCarriersByOrgId(
      @NotBlank(message = "orgId can't be empty")
          @Parameter(
              description = NodeCarrierConstants.ORG_ID,
              example = NodeCarrierConstants.ORG_ID_EXAMPLE)
          @PathVariable
          String orgId)
      throws NodeCarrierDomainException {
    logger.debug("Processing get all node carriers for a given orgId");
    var nodeCarrierResponseList = nodeCarrierService.getAllNodeCarrierByOrgId(orgId);

    return ResponseEntity.ok(
        BaseResponse.builder()
            .message(NODE_CARRIER_SUCCESS_MESSAGE)
            .payload(nodeCarrierResponseList)
            .build());
  }

  @GetAllNodeCarriersByOrgIdCarrierServiceIdDoc
  @GetMapping("/{orgId}/{carrierServiceId}/node-carriers")
  public ResponseEntity<BaseResponse<List<NodeCarrierResponse>>>
      getAllNodeCarriersByOrgIdCarrierServiceId(
          @NotBlank(message = "orgId can't be empty")
              @Parameter(
                  description = NodeCarrierConstants.ORG_ID,
                  example = NodeCarrierConstants.ORG_ID_EXAMPLE)
              @PathVariable
              String orgId,
          @NotBlank(message = "carrierServiceId can't be empty")
              @Parameter(
                  description = NodeCarrierConstants.CARRIER_SERVICE_ID,
                  example = NodeCarrierConstants.CARRIER_SERVICE_ID_EXAMPLE)
              @PathVariable
              String carrierServiceId)
          throws NodeCarrierDomainException {
    logger.debug("Processing get all node carriers for a given orgId and carrierServiceId");
    var nodeCarrierResponseList =
        nodeCarrierService.getAllNodeCarriersByOrgIdCarrierServiceId(orgId, carrierServiceId);

    return ResponseEntity.ok(
        BaseResponse.builder()
            .message(NODE_CARRIER_SUCCESS_MESSAGE)
            .payload(nodeCarrierResponseList)
            .build());
  }
}
