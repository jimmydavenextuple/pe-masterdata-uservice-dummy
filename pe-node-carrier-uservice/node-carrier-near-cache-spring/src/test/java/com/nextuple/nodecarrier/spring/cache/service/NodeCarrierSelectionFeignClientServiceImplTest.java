/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
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

import com.nextuple.nodecarrier.cache.domain.NodeCarrierSelectionCacheKey;
import com.nextuple.nodecarrier.cache.domain.NodeCarrierSelectionCacheValue;
import com.nextuple.nodecarrier.spring.cache.feign.NodeCarrierSelectionFeignImpl;
import com.nextuple.nodecarrier.spring.cache.mapper.NodeCarrierSelectionMapper;
import com.nextuple.nodecarrier.spring.cache.util.TestUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class NodeCarrierSelectionFeignClientServiceImplTest {
  @InjectMocks
  private NodeCarrierSelectionFeignClientServiceImpl nodeCarrierSelectionFeignClientService;

  @InjectMocks private TestUtil testUtil;

  @Mock private NodeCarrierSelectionMapper mapper;

  @Mock private NodeCarrierSelectionFeignImpl nodeCarrierSelectionFeign;

  @Test
  void getTest() {
    NodeCarrierSelectionCacheKey cacheKey = testUtil.getNodeCarrierSelectionCacheKey();
    NodeCarrierSelectionCacheValue cacheValue = testUtil.getNodeCarrierSelectionCacheValue();

    when(mapper.responseToCacheValue(any())).thenReturn(cacheValue);
    when(nodeCarrierSelectionFeign.getNodeCarrierSelectionDetails(any(), any(), any()))
        .thenReturn(testUtil.getNodeCarrierSelectionResponse());

    assertEquals(cacheValue, nodeCarrierSelectionFeignClientService.get(cacheKey));
    assertFalse(nodeCarrierSelectionFeignClientService.get(cacheKey).isUndefined());
    verify(mapper, times(2)).responseToCacheValue(any());
  }

  @Test
  void getExceptionTest() {
    NodeCarrierSelectionCacheKey invalidCacheKey = testUtil.getNodeCarrierSelectionCacheKey();

    when(mapper.responseToCacheValue(any())).thenThrow(new RuntimeException("Error message"));
    assertNotNull(nodeCarrierSelectionFeignClientService.get(invalidCacheKey));
    verify(mapper, times(1)).responseToCacheValue(any());
  }
}
