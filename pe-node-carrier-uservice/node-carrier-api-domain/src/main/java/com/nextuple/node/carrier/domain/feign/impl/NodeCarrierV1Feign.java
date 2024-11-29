/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.node.carrier.domain.feign.impl;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.node.carrier.domain.feign.INodeCarrierFeign;
import com.nextuple.node.carrier.domain.feign.NodeCarrierFeign;
import com.nextuple.node.carrier.domain.inbound.NodeCarrierBufferRequest;
import com.nextuple.node.carrier.domain.inbound.NodeCarrierRequest;
import com.nextuple.node.carrier.domain.inbound.NodeCarrierUpdateRequest;
import com.nextuple.node.carrier.domain.inbound.NodeServiceOptionBufferDeleteRequest;
import com.nextuple.node.carrier.domain.outbound.NodeCarrierResponse;
import com.nextuple.node.carrier.domain.outbound.NodeServiceOptionBufferResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "node-carrier", name = "version", havingValue = "v1")
public class NodeCarrierV1Feign implements INodeCarrierFeign {
  NodeCarrierFeign nodeCarrierFeign;

  @Override
  public BaseResponse<NodeCarrierResponse> createNodeCarrier(
      NodeCarrierRequest nodeCarrierRequest) {
    return nodeCarrierFeign.createNodeCarrier(nodeCarrierRequest);
  }

  @Override
  public BaseResponse<NodeCarrierResponse> updateNodeCarrier(
      String nodeId,
      String orgId,
      String carrierServiceId,
      String serviceOption,
      NodeCarrierUpdateRequest nodeCarrierUpdateRequest) {
    return nodeCarrierFeign.updateNodeCarrier(
        nodeId, orgId, carrierServiceId, serviceOption, nodeCarrierUpdateRequest);
  }

  @Override
  public BaseResponse<NodeCarrierResponse> getNodeCarrier(
      String nodeId, String orgId, String carrierServiceId, String serviceOption) {
    return nodeCarrierFeign.getNodeCarrier(nodeId, orgId, carrierServiceId, serviceOption);
  }

  @Override
  public BaseResponse<NodeCarrierResponse> deleteNodeCarrier(
      String nodeId, String orgId, String carrierServiceId, String serviceOption) {
    return nodeCarrierFeign.deleteNodeCarrier(nodeId, orgId, carrierServiceId, serviceOption);
  }

  @Override
  public BaseResponse<List<NodeCarrierResponse>> getNodeCarrierList(String nodeId, String orgId) {
    return nodeCarrierFeign.getNodeCarrierList(nodeId, orgId);
  }

  @Override
  public BaseResponse<NodeCarrierResponse> createBuffer(
      NodeCarrierBufferRequest nodeCarrierBufferRequest) {
    return nodeCarrierFeign.updateBuffer(nodeCarrierBufferRequest);
  }

  @Override
  public BaseResponse<NodeServiceOptionBufferResponse> deleteBuffer(
      NodeServiceOptionBufferDeleteRequest nodeServiceOptionBufferDeleteRequest)
      throws CommonServiceException {
    throw new CommonServiceException(
        "Delete buffer is not supported in node carrier version 1",
        HttpStatus.METHOD_NOT_ALLOWED,
        0x1779,
        null);
  }

  @Override
  public BaseResponse<NodeCarrierResponse> createProcessingLeadTime(
      NodeCarrierRequest nodeCarrierRequest) {
    return nodeCarrierFeign.updateProcessingLeadTime(nodeCarrierRequest);
  }

  @Override
  public BaseResponse<NodeCarrierResponse> deleteNodeCarrierByOrgIdNodeIdAndServiceOption(
      String nodeId, String orgId, String carrierServiceId, String serviceOption) {
    return nodeCarrierFeign.deleteNodeCarrierByOrgIdNodeIdAndServiceOption(
        nodeId, orgId, carrierServiceId, serviceOption);
  }

  @Override
  public BaseResponse<List<String>> getUniqueNodeCarrierServiceList(String orgId, String nodeId) {
    return nodeCarrierFeign.getUniqueNodeCarrierServiceList(orgId, nodeId);
  }

  @Override
  public BaseResponse<List<NodeCarrierResponse>> getNodeCarrierListWithLastPickUpTimeDetails(
      String nodeId, String orgId) {
    return nodeCarrierFeign.getNodeCarrierListWithLastPickUpTimeDetails(nodeId, orgId);
  }

  @Override
  public BaseResponse<List<NodeCarrierResponse>> getBuffersByOrgIdAndNodeIdAndServiceOption(
      String orgId, String nodeId, String serviceOption) {
    BaseResponse<List<NodeCarrierResponse>> baseResponse =
        nodeCarrierFeign.getNodeCarrierList(nodeId, orgId);
    List<NodeCarrierResponse> nodeCarrierResponses =
        baseResponse.getPayload().stream()
            .filter(nodeCarrier -> nodeCarrier.getServiceOption().equals(serviceOption))
            .toList();
    return BaseResponse.builder()
        .message(baseResponse.getMessage())
        .payload(nodeCarrierResponses)
        .build();
  }

  @Override
  public BaseResponse<List<NodeCarrierResponse>> getAllNodeCarriersByOrgIdCarrierServiceId(
      String orgId, String carrierServiceId) {
    return nodeCarrierFeign.getAllNodeCarriersByOrgIdCarrierServiceId(orgId, carrierServiceId);
  }

  @Override
  public BaseResponse<List<NodeCarrierResponse>> getAllNodeCarriersByOrgIdNodeIdAndCarrierServiceId(
      String orgId, String nodeId, String carrierServiceId) {
    return nodeCarrierFeign.getAllNodeCarriersByOrgIdNodeIdAndCarrierServiceId(
        orgId, nodeId, carrierServiceId);
  }
}
