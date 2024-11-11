/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */
package com.nextuple.sourcing.rule.spring.cache.service;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.core.spring.service.AbstractGenericFeignClientServiceImpl;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.NodePriorityResponse;
import com.nextuple.sourcing.rule.cache.domain.NodeGroupCacheKey;
import com.nextuple.sourcing.rule.cache.domain.NodeGroupCacheValue;
import com.nextuple.sourcing.rule.spring.cache.feign.NodeGroupFeignImpl;
import com.nextuple.sourcing.rule.spring.cache.mapper.NodeGroupMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
// Added this
@RequiredArgsConstructor
public class NodeGroupFeignClientServiceImpl
    extends AbstractGenericFeignClientServiceImpl<
        NodeGroupCacheKey,
        NodeGroupCacheValue,
        String,
        BaseResponse<java.util.List<NodePriorityResponse>>> {
  private final NodeGroupFeignImpl nodeGroupFeign;

  private final NodeGroupMapper nodeGroupMapper;

  @Override
  public NodeGroupCacheValue get(NodeGroupCacheKey key) {
    try {
      return nodeGroupMapper.responseToCacheValue(
          nodeGroupFeign.getNodePriorityDetailsByOrgIdAndNodeId(key.getOrgId(), key.getNodeId()));
    } catch (RuntimeException ex) {
      return NodeGroupCacheValue.builder().build();
    }
  }
}
