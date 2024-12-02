/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.node.carrier.domain.feign.impl;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.node.carrier.domain.feign.INodeCarrierFeign;
import com.nextuple.node.carrier.domain.feign.NodeCarriersFeign;
import com.nextuple.node.carrier.domain.feign.NodeServiceOptionBufferFeign;
import com.nextuple.node.carrier.domain.feign.NodeServiceOptionsFeign;
import com.nextuple.node.carrier.domain.inbound.NodeCarrierBufferRequest;
import com.nextuple.node.carrier.domain.inbound.NodeCarrierRequest;
import com.nextuple.node.carrier.domain.inbound.NodeCarrierUpdateRequest;
import com.nextuple.node.carrier.domain.inbound.NodeServiceOptionBufferDeleteRequest;
import com.nextuple.node.carrier.domain.mapper.NodeCarrierV2Mapper;
import com.nextuple.node.carrier.domain.outbound.NodeCarrierResponse;
import com.nextuple.node.carrier.domain.outbound.NodeCarriersResponse;
import com.nextuple.node.carrier.domain.outbound.NodeServiceOptionBufferResponse;
import com.nextuple.node.carrier.domain.outbound.NodeServiceOptionResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(
    prefix = "node-carrier",
    name = "version",
    havingValue = "v2",
    matchIfMissing = true)
public class NodeCarrierV2Feign implements INodeCarrierFeign {
  private final NodeCarriersFeign nodeCarriersFeign;
  private final NodeServiceOptionsFeign nodeServiceOptionsFeign;
  private final NodeServiceOptionBufferFeign nodeServiceOptionBufferFeign;
  public static final NodeCarrierV2Mapper MAPPER = Mappers.getMapper(NodeCarrierV2Mapper.class);

  @Override
  public BaseResponse<NodeCarrierResponse> createNodeCarrier(
      NodeCarrierRequest nodeCarrierRequest) {
    BaseResponse<NodeCarriersResponse> baseResponse =
        nodeCarriersFeign.createNodeCarrier(MAPPER.toNodeCarriersRequest(nodeCarrierRequest));
    return BaseResponse.builder()
        .message(baseResponse.getMessage())
        .payload(MAPPER.toNodeCarrierResponse(baseResponse.getPayload()))
        .build();
  }

  @Override
  public BaseResponse<NodeCarrierResponse> updateNodeCarrier(
      String nodeId,
      String orgId,
      String carrierServiceId,
      String serviceOption,
      NodeCarrierUpdateRequest nodeCarrierUpdateRequest) {
    BaseResponse<NodeCarriersResponse> baseResponse =
        nodeCarriersFeign.updateNodeCarrier(
            orgId,
            nodeId,
            carrierServiceId,
            serviceOption,
            MAPPER.toNodeCarriersUpdateRequest(nodeCarrierUpdateRequest));
    return BaseResponse.builder()
        .message(baseResponse.getMessage())
        .payload(MAPPER.toNodeCarrierResponse(baseResponse.getPayload()))
        .build();
  }

  @Override
  public BaseResponse<NodeCarrierResponse> getNodeCarrier(
      String nodeId, String orgId, String carrierServiceId, String serviceOption) {
    BaseResponse<NodeCarriersResponse> baseResponse =
        nodeCarriersFeign.getNodeCarrier(orgId, nodeId, carrierServiceId, serviceOption);
    return BaseResponse.builder()
        .message(baseResponse.getMessage())
        .payload(MAPPER.toNodeCarrierResponse(baseResponse.getPayload()))
        .build();
  }

  @Override
  public BaseResponse<NodeCarrierResponse> deleteNodeCarrier(
      String nodeId, String orgId, String carrierServiceId, String serviceOption) {
    BaseResponse<NodeCarriersResponse> baseResponse =
        nodeCarriersFeign.deleteNodeCarrier(orgId, nodeId, carrierServiceId, serviceOption);
    return BaseResponse.builder()
        .message(baseResponse.getMessage())
        .payload(MAPPER.toNodeCarrierResponse(baseResponse.getPayload()))
        .build();
  }

  @Override
  public BaseResponse<List<NodeCarrierResponse>> getNodeCarrierList(String nodeId, String orgId) {
    BaseResponse<List<NodeServiceOptionResponse>> baseResponse =
        nodeServiceOptionsFeign.getNodeServiceOptionsList(orgId, nodeId);
    return BaseResponse.builder()
        .message(baseResponse.getMessage())
        .payload(MAPPER.toNodeCarrierResponseListFromServiceOptions(baseResponse.getPayload()))
        .build();
  }

  @Override
  public BaseResponse<NodeCarrierResponse> createBuffer(
      NodeCarrierBufferRequest nodeCarrierBufferRequest) {
    BaseResponse<NodeServiceOptionBufferResponse> baseResponse =
        nodeServiceOptionBufferFeign.createNodeServiceOptionBuffer(
            MAPPER.toNodeServiceOptionBufferRequest(nodeCarrierBufferRequest));
    return BaseResponse.builder()
        .message(baseResponse.getMessage())
        .payload(MAPPER.toNodeCarrierResponse(baseResponse.getPayload()))
        .build();
  }

  @Override
  public BaseResponse<NodeServiceOptionBufferResponse> deleteBuffer(
      NodeServiceOptionBufferDeleteRequest nodeServiceOptionBufferDeleteRequest) {
    return nodeServiceOptionBufferFeign.deleteNodeServiceOptionBuffer(
        nodeServiceOptionBufferDeleteRequest);
  }

  @Override
  public BaseResponse<NodeCarrierResponse> createProcessingLeadTime(
      NodeCarrierRequest nodeCarrierRequest) {
    BaseResponse<NodeServiceOptionResponse> baseResponse =
        nodeServiceOptionsFeign.createNodeServiceOption(
            MAPPER.toNodeServiceOptionRequest(nodeCarrierRequest));
    return BaseResponse.builder()
        .message(baseResponse.getMessage())
        .payload(MAPPER.toNodeCarrierResponse(baseResponse.getPayload()))
        .build();
  }

  @Override
  public BaseResponse<NodeCarrierResponse> deleteNodeCarrierByOrgIdNodeIdAndServiceOption(
      String nodeId, String orgId, String carrierServiceId, String serviceOption) {
    BaseResponse<NodeServiceOptionResponse> baseResponse =
        nodeServiceOptionsFeign.deleteNodeServiceOption(orgId, nodeId, serviceOption);
    return BaseResponse.builder()
        .message(baseResponse.getMessage())
        .payload(MAPPER.toNodeCarrierResponse(baseResponse.getPayload()))
        .build();
  }

  @Override
  public BaseResponse<List<String>> getUniqueNodeCarrierServiceList(String orgId, String nodeId) {
    return nodeCarriersFeign.getUniqueNodeCarrierServiceList(orgId, nodeId);
  }

  @Override
  public BaseResponse<List<NodeCarrierResponse>> getNodeCarrierListWithLastPickUpTimeDetails(
      String nodeId, String orgId) {
    BaseResponse<List<NodeCarriersResponse>> baseResponse =
        nodeCarriersFeign.getNodeCarriersList(orgId, nodeId);
    return BaseResponse.builder()
        .message(baseResponse.getMessage())
        .payload(MAPPER.toNodeCarrierResponseListFromCarriers(baseResponse.getPayload()))
        .build();
  }

  @Override
  public BaseResponse<List<NodeCarrierResponse>> getBuffersByOrgIdAndNodeIdAndServiceOption(
      String orgId, String nodeId, String serviceOption) {
    BaseResponse<List<NodeServiceOptionBufferResponse>> baseResponse =
        nodeServiceOptionBufferFeign.getBuffersByOrgIdAndNodeIdAndServiceOption(
            orgId, nodeId, serviceOption);
    return BaseResponse.builder()
        .message(baseResponse.getMessage())
        .payload(MAPPER.toNodeCarrierResponseListFromBuffers(baseResponse.getPayload()))
        .build();
  }

  @Override
  public BaseResponse<List<NodeCarrierResponse>> getAllNodeCarriersByOrgIdCarrierServiceId(
      String orgId, String carrierServiceId) {
    BaseResponse<List<NodeCarriersResponse>> baseResponse =
        nodeCarriersFeign.getAllNodeCarriersByOrgIdCarrierServiceId(orgId, carrierServiceId);
    return BaseResponse.builder()
        .message(baseResponse.getMessage())
        .payload(MAPPER.toNodeCarrierResponseListFromCarriers(baseResponse.getPayload()))
        .build();
  }

  @Override
  public BaseResponse<List<NodeCarrierResponse>> getAllNodeCarriersByOrgIdNodeIdAndCarrierServiceId(
      String orgId, String nodeId, String carrierServiceId) {
    BaseResponse<List<NodeCarriersResponse>> baseResponse =
        nodeCarriersFeign.getAllNodeCarriersByOrgIdNodeIdAndCarrierServiceId(
            orgId, nodeId, carrierServiceId);
    return BaseResponse.builder()
        .message(baseResponse.getMessage())
        .payload(MAPPER.toNodeCarrierResponseListFromCarriers(baseResponse.getPayload()))
        .build();
  }
}
