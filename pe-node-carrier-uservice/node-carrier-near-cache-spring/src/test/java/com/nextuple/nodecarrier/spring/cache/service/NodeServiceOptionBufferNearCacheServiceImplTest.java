/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.nodecarrier.spring.cache.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nextuple.core.cache.domain.CacheValue;
import com.nextuple.core.cache.service.GenericFeignCacheService;
import com.nextuple.core.constants.NearCacheConstants;
import com.nextuple.core.registry.NearCacheRegistry;
import com.nextuple.core.spring.service.AbstractGenericFeignClientServiceImpl;
import com.nextuple.nodecarrier.cache.domain.NodeServiceOptionBufferCacheKey;
import com.nextuple.nodecarrier.cache.domain.NodeServiceOptionBufferCacheValue;
import com.nextuple.nodecarrier.spring.cache.util.TestUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class NodeServiceOptionBufferNearCacheServiceImplTest {

  @InjectMocks
  private NodeServiceOptionBufferNearCacheServiceImpl nodeServiceOptionBufferNearCacheServiceImpl;

  @InjectMocks private TestUtil testUtil;

  @Mock private AbstractGenericFeignClientServiceImpl abstractGenericFeignClientService;

  @Mock
  private GenericFeignCacheService<
          NodeServiceOptionBufferCacheKey, NodeServiceOptionBufferCacheValue>
      feignCacheService;

  @Mock private NearCacheRegistry nearCacheRegistry;
  @Mock private CaffeineCacheManager caffeineCacheManager;

  @BeforeEach
  void setup() {
    ReflectionTestUtils.setField(
        nodeServiceOptionBufferNearCacheServiceImpl, "cacheManager", caffeineCacheManager);
    ReflectionTestUtils.setField(
        nodeServiceOptionBufferNearCacheServiceImpl, "feignCacheService", feignCacheService);
  }

  @Test
  void getValidTest() {
    NodeServiceOptionBufferCacheKey cacheKey = testUtil.getNodeServiceOptionBufferCacheKey();
    NodeServiceOptionBufferCacheValue cacheValue = testUtil.getNodeServiceOptionBufferCacheValue();

    when(feignCacheService.get(any())).thenReturn(cacheValue);
    when(abstractGenericFeignClientService.get(any())).thenReturn(cacheValue);

    // First Invocation
    CacheValue cacheValue1 = nodeServiceOptionBufferNearCacheServiceImpl.get(cacheKey);
    assertEquals(cacheValue, cacheValue1);

    // Second Invocation
    CacheValue cacheValue2 = abstractGenericFeignClientService.get(cacheKey);
    assertEquals(cacheValue, cacheValue2);

    // Third Invocation
    CacheValue cacheValue3 = abstractGenericFeignClientService.get(cacheKey);
    assertEquals(cacheValue, cacheValue3);
    verify(feignCacheService, times(1)).get(cacheKey);
  }

  @Test
  void getInValidTest() {
    NodeServiceOptionBufferCacheKey cacheKey = testUtil.getNodeServiceOptionBufferCacheKey();

    when(feignCacheService.get(any())).thenReturn(null);
    assertNull(nodeServiceOptionBufferNearCacheServiceImpl.get(cacheKey));
    verify(feignCacheService, times(1)).get(cacheKey);
  }

  @Test
  void deleteTest() {
    NodeServiceOptionBufferCacheKey cacheKey = testUtil.getNodeServiceOptionBufferCacheKey();

    nodeServiceOptionBufferNearCacheServiceImpl.delete(cacheKey);
    CacheValue cacheValue = nodeServiceOptionBufferNearCacheServiceImpl.get(cacheKey);
    assertNull(cacheValue);
  }

  @Test
  void deleteAllTest() {
    NodeServiceOptionBufferCacheKey cacheKey = testUtil.getNodeServiceOptionBufferCacheKey();

    nodeServiceOptionBufferNearCacheServiceImpl.deleteAll();
    CacheValue cacheValue = nodeServiceOptionBufferNearCacheServiceImpl.get(cacheKey);
    assertNull(cacheValue);
  }

  @Test
  void selfRegister() {
    doNothing().when(nearCacheRegistry).registerNearCacheEntity(any(), any(), any());
    nodeServiceOptionBufferNearCacheServiceImpl.selfRegister();

    verify(nearCacheRegistry, times(1)).registerNearCacheEntity(any(), any(), any());
  }

  @Test
  void getEntityName() {
    assertEquals(
        NearCacheConstants.NODE_SERVICE_OPTION_BUFFER_ENTITY_NAME,
        nodeServiceOptionBufferNearCacheServiceImpl.getEntityName());
  }
}
