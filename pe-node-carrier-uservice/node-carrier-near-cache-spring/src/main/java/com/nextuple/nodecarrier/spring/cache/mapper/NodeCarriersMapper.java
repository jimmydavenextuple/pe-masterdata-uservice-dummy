/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.nodecarrier.spring.cache.mapper;

import static com.nextuple.nodecarrier.spring.cache.mapper.NodeCarrierMapper.DATA_MAPPER;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.core.cache.mapper.GenericMapper;
import com.nextuple.node.carrier.domain.outbound.NodeCarriersResponse;
import com.nextuple.nodecarrier.cache.domain.NodeCarriersCacheKey;
import com.nextuple.nodecarrier.cache.domain.NodeCarriersCacheValue;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class NodeCarriersMapper
    implements GenericMapper<
        NodeCarriersCacheKey,
        NodeCarriersCacheValue,
        String,
        BaseResponse<List<NodeCarriersResponse>>> {

  @Override
  public NodeCarriersCacheKey requestToCacheKey(String request) {
    return null;
  }

  @Override
  public String cacheKeyToRequest(NodeCarriersCacheKey cacheKey) {
    return null;
  }

  @Override
  public NodeCarriersCacheValue responseToCacheValue(
      BaseResponse<List<NodeCarriersResponse>> resp) {
    return NodeCarriersCacheValue.builder()
        .nodeCarriersResponses(DATA_MAPPER.convertToNodeCarriersCacheValue(resp.getPayload()))
        .build();
  }

  @Override
  public BaseResponse<List<NodeCarriersResponse>> cacheValueToResponse(
      NodeCarriersCacheValue cacheValue) {
    return null;
  }
}
