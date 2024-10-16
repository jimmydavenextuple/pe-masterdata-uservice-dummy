/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.node.carrier.domain.mapper;

import com.nextuple.node.carrier.domain.inbound.NodeCarrierBufferRequest;
import com.nextuple.node.carrier.domain.inbound.NodeCarrierRequest;
import com.nextuple.node.carrier.domain.inbound.NodeCarrierUpdateRequest;
import com.nextuple.node.carrier.domain.inbound.NodeCarriersRequest;
import com.nextuple.node.carrier.domain.inbound.NodeCarriersUpdateRequest;
import com.nextuple.node.carrier.domain.inbound.NodeServiceOptionBufferRequest;
import com.nextuple.node.carrier.domain.inbound.NodeServiceOptionRequest;
import com.nextuple.node.carrier.domain.inbound.NodeServiceOptionUpdateRequest;
import com.nextuple.node.carrier.domain.outbound.NodeCarrierResponse;
import com.nextuple.node.carrier.domain.outbound.NodeCarriersResponse;
import com.nextuple.node.carrier.domain.outbound.NodeServiceOptionBufferResponse;
import com.nextuple.node.carrier.domain.outbound.NodeServiceOptionResponse;
import java.util.Date;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface NodeCarrierV2Mapper {
  NodeCarriersRequest toNodeCarriersRequest(NodeCarrierRequest nodeCarrierRequest);

  NodeCarrierResponse toNodeCarrierResponse(NodeCarriersResponse nodeCarriersResponse);

  List<NodeCarrierResponse> toNodeCarrierResponseListFromCarriers(
      List<NodeCarriersResponse> nodeCarriersResponseList);

  NodeCarriersUpdateRequest toNodeCarriersUpdateRequest(
      NodeCarrierUpdateRequest nodeCarrierUpdateRequest);

  NodeServiceOptionRequest toNodeServiceOptionRequest(NodeCarrierRequest nodeCarrierRequest);

  NodeCarrierResponse toNodeCarrierResponse(NodeServiceOptionResponse nodeServiceOptionResponse);

  List<NodeCarrierResponse> toNodeCarrierResponseListFromServiceOptions(
      List<NodeServiceOptionResponse> nodeServiceOptionResponseList);

  NodeServiceOptionUpdateRequest toNodeServiceOptionUpdateRequest(
      NodeCarrierUpdateRequest nodeCarrierUpdateRequest);

  NodeServiceOptionBufferRequest toNodeServiceOptionBufferRequest(
      NodeCarrierBufferRequest nodeCarrierBufferRequest);

  NodeCarrierResponse toNodeCarrierResponse(
      NodeServiceOptionBufferResponse nodeServiceOptionBufferResponse);

  List<NodeCarrierResponse> toNodeCarrierResponseListFromBuffers(
      List<NodeServiceOptionBufferResponse> nodeServiceOptionBufferResponses);

  default Date toDateString(Long timestamp) {
    return timestamp != null ? new Date(timestamp) : null;
  }
}
