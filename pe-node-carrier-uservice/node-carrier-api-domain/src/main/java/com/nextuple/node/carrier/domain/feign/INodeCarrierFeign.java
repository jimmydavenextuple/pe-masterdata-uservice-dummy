/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.node.carrier.domain.feign;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.node.carrier.domain.inbound.NodeCarrierBufferRequest;
import com.nextuple.node.carrier.domain.inbound.NodeCarrierRequest;
import com.nextuple.node.carrier.domain.inbound.NodeCarrierUpdateRequest;
import com.nextuple.node.carrier.domain.inbound.NodeServiceOptionBufferDeleteRequest;
import com.nextuple.node.carrier.domain.outbound.NodeCarrierResponse;
import com.nextuple.node.carrier.domain.outbound.NodeServiceOptionBufferResponse;
import java.util.List;

public interface INodeCarrierFeign {
  BaseResponse<NodeCarrierResponse> createNodeCarrier(NodeCarrierRequest nodeCarrierRequest);

  BaseResponse<NodeCarrierResponse> updateNodeCarrier(
      String nodeId,
      String orgId,
      String carrierServiceId,
      String serviceOption,
      NodeCarrierUpdateRequest nodeCarrierUpdateRequest);

  BaseResponse<NodeCarrierResponse> getNodeCarrier(
      String nodeId, String orgId, String carrierServiceId, String serviceOption);

  BaseResponse<NodeCarrierResponse> deleteNodeCarrier(
      String nodeId, String orgId, String carrierServiceId, String serviceOption);

  BaseResponse<List<NodeCarrierResponse>> getNodeCarrierList(String nodeId, String orgId);

  BaseResponse<NodeCarrierResponse> createBuffer(NodeCarrierBufferRequest nodeCarrierBufferRequest);

  BaseResponse<NodeServiceOptionBufferResponse> deleteBuffer(
      NodeServiceOptionBufferDeleteRequest nodeServiceOptionBufferDeleteRequest)
      throws CommonServiceException;

  BaseResponse<NodeCarrierResponse> createProcessingLeadTime(NodeCarrierRequest nodeCarrierRequest);

  BaseResponse<NodeCarrierResponse> deleteNodeCarrierByOrgIdNodeIdAndServiceOption(
      String nodeId, String orgId, String carrierServiceId, String serviceOption);

  BaseResponse<List<String>> getUniqueNodeCarrierServiceList(String orgId, String nodeId);

  BaseResponse<List<NodeCarrierResponse>> getNodeCarrierListWithLastPickUpTimeDetails(
      String nodeId, String orgId);

  BaseResponse<List<NodeCarrierResponse>> getBuffersByOrgIdAndNodeIdAndServiceOption(
      String orgId, String nodeId, String serviceOption);

  BaseResponse<List<NodeCarrierResponse>> getAllNodeCarriersByOrgIdCarrierServiceId(
      String orgId, String carrierServiceId);

  BaseResponse<List<NodeCarrierResponse>> getAllNodeCarriersByOrgIdNodeIdAndCarrierServiceId(
      String orgId, String nodeId, String carrierServiceId);
}
