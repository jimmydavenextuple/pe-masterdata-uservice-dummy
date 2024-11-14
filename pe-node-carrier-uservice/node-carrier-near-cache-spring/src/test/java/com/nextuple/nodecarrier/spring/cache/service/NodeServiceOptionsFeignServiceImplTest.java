/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.nodecarrier.spring.cache.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nextuple.nodecarrier.cache.domain.NodeServiceOptionCacheKey;
import com.nextuple.nodecarrier.cache.domain.NodeServiceOptionCacheValue;
import com.nextuple.nodecarrier.spring.cache.feign.NodeServiceOptionsFeignImpl;
import com.nextuple.nodecarrier.spring.cache.mapper.NodeServiceOptionMapper;
import com.nextuple.nodecarrier.spring.cache.util.TestUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class NodeServiceOptionsFeignServiceImplTest {

  @InjectMocks private NodeServiceOptionsFeignServiceImpl nodeServiceOptionsFeignService;

  @InjectMocks private TestUtil testUtil;

  @Mock private NodeServiceOptionMapper mapper;

  @Mock private NodeServiceOptionsFeignImpl nodeCarrierSelectionFeign;

  @Test
  void getTest() {
    NodeServiceOptionCacheKey cacheKey = testUtil.getNodeServiceOptionCacheKey();
    NodeServiceOptionCacheValue cacheValue = testUtil.getNodeServiceOptionCacheValue();

    when(mapper.responseToCacheValue(any())).thenReturn(cacheValue);
    when(nodeCarrierSelectionFeign.getNodeServiceOption(any(), any(), any()))
        .thenReturn(testUtil.getBaseResponseOfNodeServiceOptionResponse());

    assertEquals(cacheValue, nodeServiceOptionsFeignService.get(cacheKey));
    assertFalse(nodeServiceOptionsFeignService.get(cacheKey).isUndefined());
    verify(mapper, times(2)).responseToCacheValue(any());
  }

  @Test
  void getExceptionTest() {
    NodeServiceOptionCacheKey invalidCacheKey = testUtil.getNodeServiceOptionCacheKey();

    when(mapper.responseToCacheValue(any())).thenThrow(new RuntimeException("Error message"));
    assertNotNull(nodeServiceOptionsFeignService.get(invalidCacheKey));
    verify(mapper, times(1)).responseToCacheValue(any());
  }
}
