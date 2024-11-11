/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.nodecarrier.spring.cache.service;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.core.spring.service.AbstractGenericFeignClientServiceImpl;
import com.nextuple.node.carrier.domain.outbound.NodeServiceOptionBufferResponse;
import com.nextuple.nodecarrier.cache.domain.NodeServiceOptionBufferCacheKey;
import com.nextuple.nodecarrier.cache.domain.NodeServiceOptionBufferCacheValue;
import com.nextuple.nodecarrier.spring.cache.feign.NodeServiceOptionBufferFeignImpl;
import com.nextuple.nodecarrier.spring.cache.mapper.NodeServiceOptionBufferMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
// Added this
@RequiredArgsConstructor
public class NodeServiceOptionBufferFeignServiceImpl
    extends AbstractGenericFeignClientServiceImpl<
        NodeServiceOptionBufferCacheKey,
        NodeServiceOptionBufferCacheValue,
        String,
        BaseResponse<List<NodeServiceOptionBufferResponse>>> {
  private final NodeServiceOptionBufferFeignImpl nodeServiceOptionBufferFeign;
  private final NodeServiceOptionBufferMapper nodeServiceOptionBufferMapper;

  @Override
  public NodeServiceOptionBufferCacheValue get(NodeServiceOptionBufferCacheKey key) {
    try {
      return nodeServiceOptionBufferMapper.responseToCacheValue(
          nodeServiceOptionBufferFeign.fetchApplicableNodeServiceOptionBuffers(
              key.getOrgId(),
              key.getNodeId(),
              key.getServiceOption(),
              key.getRequestDate(),
              key.getHorizonDays()));
    } catch (RuntimeException | CommonServiceException e) {
      return NodeServiceOptionBufferCacheValue.builder().build();
    }
  }
}
