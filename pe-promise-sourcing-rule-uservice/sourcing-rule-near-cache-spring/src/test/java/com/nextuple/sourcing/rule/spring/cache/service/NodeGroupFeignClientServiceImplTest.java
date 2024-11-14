/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */
package com.nextuple.sourcing.rule.spring.cache.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.NodePriorityResponse;
import com.nextuple.sourcing.rule.cache.domain.NodeGroupCacheKey;
import com.nextuple.sourcing.rule.cache.domain.NodeGroupCacheValue;
import com.nextuple.sourcing.rule.spring.cache.feign.NodeGroupFeignImpl;
import com.nextuple.sourcing.rule.spring.cache.mapper.NodeGroupMapper;
import com.nextuple.sourcing.rule.spring.cache.util.TestUtil;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class NodeGroupFeignClientServiceImplTest {
  @InjectMocks private NodeGroupFeignClientServiceImpl nodeGroupFeignClientService;

  @InjectMocks private TestUtil testUtil;

  @Mock private NodeGroupMapper nodeGroupMapper;

  @Mock private NodeGroupFeignImpl nodeGroupFeign;

  @Test
  void getCacheValueTest() {

    NodeGroupCacheKey cacheKey = testUtil.getNodeGroupCacheKey();
    NodeGroupCacheValue cacheValue = testUtil.getNodeGroupCacheValue();
    BaseResponse<List<NodePriorityResponse>> response =
        testUtil.getBaseResponseListOfNodePriorityResponse();

    Mockito.when(
            nodeGroupFeign.getNodePriorityDetailsByOrgIdAndNodeId(
                cacheKey.getOrgId(), cacheKey.getNodeId()))
        .thenReturn(response);
    Mockito.when(nodeGroupMapper.responseToCacheValue(response)).thenReturn(cacheValue);

    assertEquals(cacheValue, nodeGroupFeignClientService.get(cacheKey));
    assertFalse(nodeGroupFeignClientService.get(cacheKey).isUndefined());
    verify(nodeGroupMapper, times(2)).responseToCacheValue(any());
  }

  @Test
  void getCacheValueExceptionTest() {
    NodeGroupCacheKey cacheKey = testUtil.getNodeGroupCacheKey();

    Mockito.when(nodeGroupMapper.responseToCacheValue(any()))
        .thenThrow(new RuntimeException("Error message"));
    var response = nodeGroupFeignClientService.get(cacheKey);

    assertNotNull(response);
    assertNull(response.getNodePriorityResponseList());
    verify(nodeGroupMapper, times(1)).responseToCacheValue(any());
  }
}
