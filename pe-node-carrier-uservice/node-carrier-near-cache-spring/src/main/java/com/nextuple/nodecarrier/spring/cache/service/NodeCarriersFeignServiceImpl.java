/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.nodecarrier.spring.cache.service;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.core.spring.service.AbstractGenericFeignClientServiceImpl;
import com.nextuple.node.carrier.domain.outbound.NodeCarriersResponse;
import com.nextuple.nodecarrier.cache.domain.NodeCarriersCacheKey;
import com.nextuple.nodecarrier.cache.domain.NodeCarriersCacheValue;
import com.nextuple.nodecarrier.spring.cache.feign.NodeCarriersFeignImpl;
import com.nextuple.nodecarrier.spring.cache.mapper.NodeCarriersMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
// Added this
@RequiredArgsConstructor
public class NodeCarriersFeignServiceImpl
    extends AbstractGenericFeignClientServiceImpl<
        NodeCarriersCacheKey,
        NodeCarriersCacheValue,
        String,
        BaseResponse<List<NodeCarriersResponse>>> {

  private final NodeCarriersFeignImpl nodeCarriersFeign;

  private final NodeCarriersMapper nodeCarriersMapper;

  @Override
  public NodeCarriersCacheValue get(NodeCarriersCacheKey key) {
    try {
      return nodeCarriersMapper.responseToCacheValue(
          nodeCarriersFeign.getNodeCarriersList(
              key.getOrgId(), key.getNodeId(), key.getServiceOption()));
    } catch (RuntimeException e) {
      return NodeCarriersCacheValue.builder().build();
    }
  }
}
