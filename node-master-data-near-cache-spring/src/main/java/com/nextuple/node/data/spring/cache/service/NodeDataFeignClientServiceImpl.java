/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.node.data.spring.cache.service;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.core.cache.mapper.GenericMapper;
import com.nextuple.core.spring.service.AbstractGenericFeignClientServiceImpl;
import com.nextuple.node.data.cache.domain.NodeDataCacheKey;
import com.nextuple.node.data.cache.domain.NodeDataCacheValue;
import com.nextuple.node.data.spring.cache.feign.NodeDataFeignImpl;
import com.nextuple.node.domain.outbound.NodeResponse;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
// Added this
@AllArgsConstructor
public class NodeDataFeignClientServiceImpl
    extends AbstractGenericFeignClientServiceImpl<
        NodeDataCacheKey, NodeDataCacheValue, String, BaseResponse<NodeResponse>> {

  @Autowired NodeDataFeignImpl nodeFeign;

  @Autowired
  GenericMapper<NodeDataCacheKey, NodeDataCacheValue, String, BaseResponse<NodeResponse>>
      nodeMapper;

  @Override
  public NodeDataCacheValue get(NodeDataCacheKey key) {
    try {
      return nodeMapper.responseToCacheValue(
          nodeFeign.getNodeDetails(key.getNodeId(), key.getOrgId()));
    } catch (RuntimeException ex) {
      return null;
    }
  }
}
