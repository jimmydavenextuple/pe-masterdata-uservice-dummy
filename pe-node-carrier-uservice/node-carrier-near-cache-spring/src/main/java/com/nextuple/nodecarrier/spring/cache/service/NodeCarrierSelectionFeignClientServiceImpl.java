/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */
package com.nextuple.nodecarrier.spring.cache.service;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.core.spring.service.AbstractGenericFeignClientServiceImpl;
import com.nextuple.node.carrier.domain.outbound.NodeCarrierSelectionResponse;
import com.nextuple.nodecarrier.cache.domain.NodeCarrierSelectionCacheKey;
import com.nextuple.nodecarrier.cache.domain.NodeCarrierSelectionCacheValue;
import com.nextuple.nodecarrier.spring.cache.feign.NodeCarrierSelectionFeignImpl;
import com.nextuple.nodecarrier.spring.cache.mapper.NodeCarrierSelectionMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NodeCarrierSelectionFeignClientServiceImpl
    extends AbstractGenericFeignClientServiceImpl<
        NodeCarrierSelectionCacheKey,
        NodeCarrierSelectionCacheValue,
        String,
        BaseResponse<List<NodeCarrierSelectionResponse>>> {

  private final NodeCarrierSelectionFeignImpl nodeCarrierSelectionFeign;

  private final NodeCarrierSelectionMapper nodeCarrierSelectionMapper;

  @Override
  public NodeCarrierSelectionCacheValue get(NodeCarrierSelectionCacheKey key) {
    try {
      return nodeCarrierSelectionMapper.responseToCacheValue(
          nodeCarrierSelectionFeign.getNodeCarrierSelectionDetails(
              key.getOrgId(), key.getServiceOption(), key.getDestinationGeozone()));
    } catch (RuntimeException e) {
      return NodeCarrierSelectionCacheValue.builder().build();
    }
  }
}
