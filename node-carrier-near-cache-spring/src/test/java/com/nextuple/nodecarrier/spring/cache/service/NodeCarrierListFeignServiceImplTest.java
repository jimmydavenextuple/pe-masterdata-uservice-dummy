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

import com.nextuple.nodecarrier.cache.domain.NodeCarrierListCacheKey;
import com.nextuple.nodecarrier.cache.domain.NodeCarrierListCacheValue;
import com.nextuple.nodecarrier.spring.cache.feign.NodeCarrierListFeignImpl;
import com.nextuple.nodecarrier.spring.cache.mapper.NodeCarrierListMapper;
import com.nextuple.nodecarrier.spring.cache.util.TestUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class NodeCarrierListFeignServiceImplTest {

  @InjectMocks private NodeCarrierListFeignServiceImpl nodeCarrierListFeignService;

  @InjectMocks private TestUtil testUtil;

  @Mock private NodeCarrierListMapper mapper;

  @Mock private NodeCarrierListFeignImpl nodeCarrierFeign;

  @Test
  void getTest() {
    NodeCarrierListCacheKey cacheKey = testUtil.getNodeCarrierListCacheKey();
    NodeCarrierListCacheValue cacheValue = testUtil.getNodeCarrierListCacheValue();

    when(mapper.responseToCacheValue(any())).thenReturn(cacheValue);
    when(nodeCarrierFeign.getNodeCarrierList(any(), any(), any()))
        .thenReturn(testUtil.getListOfBaseResponseOfNodeCarrierResponse());

    assertEquals(cacheValue, nodeCarrierListFeignService.get(cacheKey));
    assertFalse(nodeCarrierListFeignService.get(cacheKey).isUndefined());
    verify(mapper, times(2)).responseToCacheValue(any());
  }

  @Test
  void getExceptionTest() {
    NodeCarrierListCacheKey invalidCacheKey = testUtil.getNodeCarrierListCacheKey();

    when(mapper.responseToCacheValue(any())).thenThrow(new RuntimeException("Error message"));
    assertNotNull(nodeCarrierListFeignService.get(invalidCacheKey));
    verify(mapper, times(1)).responseToCacheValue(any());
  }
}
