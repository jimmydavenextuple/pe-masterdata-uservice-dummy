/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.node.carrier.controller;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.node.carrier.controller.docs.CreateNodeCarriersDoc;
import com.nextuple.node.carrier.controller.docs.DeleteNodeCarriersDoc;
import com.nextuple.node.carrier.controller.docs.GetNodeCarriersDoc;
import com.nextuple.node.carrier.controller.docs.GetNodeCarriersListByOrgIdAndNodeIdAndServiceOptionDoc;
import com.nextuple.node.carrier.controller.docs.GetNodeCarriersListDoc;
import com.nextuple.node.carrier.controller.docs.GetUniqueNodeCarriersServiceListDoc;
import com.nextuple.node.carrier.controller.docs.UpdateNodeCarriersDoc;
import com.nextuple.node.carrier.domain.constants.NodeCarrierConstants;
import com.nextuple.node.carrier.domain.dto.NodeCarrierListCacheKeyDto;
import com.nextuple.node.carrier.domain.inbound.NodeCarriersRequest;
import com.nextuple.node.carrier.domain.inbound.NodeCarriersUpdateRequest;
import com.nextuple.node.carrier.domain.outbound.NodeCarriersResponse;
import com.nextuple.node.carrier.exception.InvalidDataException;
import com.nextuple.node.carrier.service.NodeCarriersService;
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
@Tag(name = "Node Carriers APIs")
@RequestMapping("/v2/node/carrier")
@RequiredArgsConstructor
public class NodeCarriersController {

  private final NodeCarriersService nodeCarriersService;
  private static final Logger logger = LoggerFactory.getLogger(NodeCarriersController.class);

  @CreateNodeCarriersDoc
  @PostMapping
  public ResponseEntity<BaseResponse<NodeCarriersResponse>> createNodeCarrier(
      @Valid @RequestBody NodeCarriersRequest nodeCarriersRequest)
      throws CommonServiceException, InvalidDataException {
    logger.debug("Processing node carrier creation request {}", nodeCarriersRequest);
    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Node Carrier successfully created")
            .payload(nodeCarriersService.createNodeCarrier(nodeCarriersRequest))
            .build());
  }

  @GetNodeCarriersDoc
  @GetMapping("/{orgId}/{nodeId}/{carrierServiceId}/{serviceOption}")
  public ResponseEntity<BaseResponse<NodeCarriersResponse>> getNodeCarrier(
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
      throws CommonServiceException {
    logger.debug(
        "Fetching node carrier by orgId {} and nodeId {} and carrierServiceId {} and serviceOption {} request",
        orgId,
        nodeId,
        carrierServiceId,
        serviceOption);
    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Node Carrier details fetched successfully")
            .payload(
                nodeCarriersService.getNodeCarrierDetails(
                    orgId, nodeId, carrierServiceId, serviceOption))
            .build());
  }

  @UpdateNodeCarriersDoc
  @PutMapping("/{orgId}/{nodeId}/{carrierServiceId}/{serviceOption}")
  public ResponseEntity<BaseResponse<NodeCarriersResponse>> updateNodeCarrier(
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
      @Valid @RequestBody NodeCarriersUpdateRequest nodeCarriersUpdateRequest)
      throws CommonServiceException, InvalidDataException {
    logger.debug(
        "Updating node carrier by orgId {} and nodeId {} and carrierServiceId {} and serviceOption {} request",
        orgId,
        nodeId,
        carrierServiceId,
        serviceOption);
    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Node Carrier updated successfully")
            .payload(
                nodeCarriersService.updateNodeCarrier(
                    orgId, nodeId, carrierServiceId, serviceOption, nodeCarriersUpdateRequest))
            .build());
  }

  @DeleteNodeCarriersDoc
  @DeleteMapping("/{orgId}/{nodeId}/{carrierServiceId}/{serviceOption}")
  public ResponseEntity<BaseResponse<NodeCarriersResponse>> deleteNodeCarrier(
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
      throws CommonServiceException {
    logger.debug(
        "Deleting node carrier by orgId {} and nodeId {} and carrierServiceId {} and serviceOption {} request",
        orgId,
        nodeId,
        carrierServiceId,
        serviceOption);
    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Node Carrier deleted successfully")
            .payload(
                nodeCarriersService.deleteNodeCarrier(
                    orgId, nodeId, carrierServiceId, serviceOption))
            .build());
  }

  @GetNodeCarriersListDoc
  @GetMapping("/{orgId}/{nodeId}")
  public ResponseEntity<BaseResponse<List<NodeCarriersResponse>>> getNodeCarriersList(
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
    logger.debug("Processing get node carriers for nodeId and orgId");
    List<NodeCarriersResponse> nodeCarriersResponseList =
        nodeCarriersService.getNodeCarriersListByOrgIdAndNodeId(orgId, nodeId);

    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Node Carriers list fetched successfully")
            .payload(nodeCarriersResponseList)
            .build());
  }

  @GetUniqueNodeCarriersServiceListDoc
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
      throws CommonServiceException {
    logger.debug("Processing get list of carrier service names");
    return ResponseEntity.ok(
        BaseResponse.builder()
            .payload(nodeCarriersService.getListOfCarrierServiceNameByOrgIdAndNodeId(orgId, nodeId))
            .build());
  }

  @GetNodeCarriersListByOrgIdAndNodeIdAndServiceOptionDoc
  @GetMapping("/{orgId}/{nodeId}/{serviceOption}")
  public ResponseEntity<BaseResponse<List<NodeCarriersResponse>>>
      getNodeCarriersListByOrgIdAndNodeIdAndServiceOption(
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
    logger.debug("Processing get node carriers for nodeId and orgId and serviceOption");
    List<NodeCarriersResponse> nodeCarriersResponseList =
        nodeCarriersService.getNodeCarriersList(orgId, nodeId, serviceOption);
    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Node Carriers list fetched successfully")
            .payload(nodeCarriersResponseList)
            .build());
  }

  @GetMapping("/get-all-cache-keys")
  public ResponseEntity<BaseResponse<List<NodeCarrierListCacheKeyDto>>> getNodeCarriersCacheKeys(
      @RequestParam Integer limit) throws CommonServiceException {
    logger.debug("Processing get Node Carrier List Cache Keys");
    var response = nodeCarriersService.getAllNodeCarriersCacheKeys(limit);
    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Node Carriers Cache Keys fetched successfully")
            .payload(response)
            .build());
  }

  @GetMapping("/org/{orgId}/carrier-service/{carrierServiceId}")
  public ResponseEntity<BaseResponse<List<NodeCarriersResponse>>>
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
          throws CommonServiceException {
    logger.debug("Processing get all node carriers for a given orgId and carrierServiceId");
    var nodeCarriersResponseList =
        nodeCarriersService.getAllNodeCarriersByOrgIdCarrierServiceId(orgId, carrierServiceId);

    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Node Carriers list fetched successfully")
            .payload(nodeCarriersResponseList)
            .build());
  }

  @GetMapping("/org/{orgId}/nodeId/{nodeId}/carrier-service/{carrierServiceId}")
  public ResponseEntity<BaseResponse<List<NodeCarriersResponse>>>
      getAllNodeCarriersByOrgIdNodeIdAndCarrierServiceId(
          @NotBlank(message = "orgId can't be empty")
              @Parameter(
                  description = NodeCarrierConstants.ORG_ID,
                  example = NodeCarrierConstants.ORG_ID_EXAMPLE)
              @PathVariable
              String orgId,
          @NotBlank(message = "orgId can't be empty")
              @Parameter(
                  description = NodeCarrierConstants.NODE_ID,
                  example = NodeCarrierConstants.NODE_ID_EXAMPLE)
              @PathVariable
              String nodeId,
          @NotBlank(message = "carrierServiceId can't be empty")
              @Parameter(
                  description = NodeCarrierConstants.CARRIER_SERVICE_ID,
                  example = NodeCarrierConstants.CARRIER_SERVICE_ID_EXAMPLE)
              @PathVariable
              String carrierServiceId)
          throws CommonServiceException {
    logger.debug("Processing get all node carriers for a given orgId and carrierServiceId");
    var nodeCarriersResponseList =
        nodeCarriersService.getAllNodeCarriersByOrgIdNodeIdAndCarrierServiceId(
            orgId, nodeId, carrierServiceId);
    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Node Carriers list fetched successfully")
            .payload(nodeCarriersResponseList)
            .build());
  }
}
