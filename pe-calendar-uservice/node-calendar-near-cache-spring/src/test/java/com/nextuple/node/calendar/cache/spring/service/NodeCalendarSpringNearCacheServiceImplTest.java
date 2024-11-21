/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.node.calendar.cache.spring.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.github.benmanes.caffeine.cache.Cache;
import com.nextuple.core.cache.domain.CacheValue;
import com.nextuple.core.cache.service.GenericFeignCacheService;
import com.nextuple.core.constants.NearCacheConstants;
import com.nextuple.core.registry.NearCacheRegistry;
import com.nextuple.core.spring.service.AbstractGenericFeignClientServiceImpl;
import com.nextuple.node.calendar.cache.domain.NodeCalendarCacheKey;
import com.nextuple.node.calendar.cache.domain.NodeCalendarCacheValue;
import com.nextuple.node.calendar.cache.spring.util.TestUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.test.util.ReflectionTestUtils;

class NodeCalendarSpringNearCacheServiceImplTest {
  @InjectMocks private NodeCalendarSpringNearCacheService nodeCalendarSpringNearCacheService;

  @InjectMocks private TestUtil testUtil;

  @Mock private AbstractGenericFeignClientServiceImpl abstractGenericFeignClientService;

  @Mock
  private GenericFeignCacheService<NodeCalendarCacheKey, NodeCalendarCacheValue> feignCacheService;

  @Mock private NearCacheRegistry nearCacheRegistry;

  @Mock private CaffeineCacheManager caffeineCacheManager;
  @Mock private Cache<Object, Object> objectCache;
  @Mock private CaffeineCache caffeineCache;

  @BeforeEach
  void setup() {
    MockitoAnnotations.openMocks(this);
    ReflectionTestUtils.setField(
        nodeCalendarSpringNearCacheService, "cacheManager", caffeineCacheManager);
    ReflectionTestUtils.setField(
        nodeCalendarSpringNearCacheService, "feignCacheService", feignCacheService);
  }

  @Test
  void getValidTest() {
    NodeCalendarCacheKey cacheKey = testUtil.getNodeCalendarCacheKey();
    NodeCalendarCacheValue cacheValue = testUtil.getNodeCalendarCacheValue();

    when(feignCacheService.get(any())).thenReturn(cacheValue);
    when(abstractGenericFeignClientService.get(any())).thenReturn(cacheValue);

    // First Invocation
    CacheValue cacheValue1 = nodeCalendarSpringNearCacheService.get(cacheKey);
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
    NodeCalendarCacheKey cacheKey = testUtil.getNodeCalendarCacheKey();

    when(feignCacheService.get(any())).thenReturn(null);
    assertNull(nodeCalendarSpringNearCacheService.get(cacheKey));
    verify(feignCacheService, times(1)).get(cacheKey);
  }

  @Test
  void deleteTest() {
    NodeCalendarCacheKey cacheKey = testUtil.getNodeCalendarCacheKey();

    nodeCalendarSpringNearCacheService.delete(cacheKey);
    CacheValue cacheValue = nodeCalendarSpringNearCacheService.get(cacheKey);
    assertNull(cacheValue);
  }

  @Test
  @DisplayName("Delete with partial eviction of matching keys")
  void deleteWithPartialEvictionOfMatchingKeysTest() {
    var concurrentHashMap = testUtil.getNodeCalendarCacheMap();
    when(caffeineCacheManager.getCache(any())).thenReturn(caffeineCache);
    when(caffeineCache.getNativeCache()).thenReturn(objectCache);
    when(objectCache.asMap()).thenReturn(concurrentHashMap);
    nodeCalendarSpringNearCacheService.delete(testUtil.getNodeCalendarCacheKey());
    CacheValue cacheValue =
        nodeCalendarSpringNearCacheService.get(testUtil.getNodeCalendarCacheKey());
    assertNull(cacheValue);
    verify(caffeineCacheManager, times(3)).getCache(any());
    verify(objectCache, times(1)).asMap();
    verify(caffeineCache, times(5)).evict(any());
  }

  @Test
  void deleteAllTest() {
    NodeCalendarCacheKey cacheKey = testUtil.getNodeCalendarCacheKey();

    nodeCalendarSpringNearCacheService.deleteAll();
    CacheValue cacheValue = nodeCalendarSpringNearCacheService.get(cacheKey);
    assertNull(cacheValue);
  }

  @Test
  void selfRegisterTest() {
    doNothing().when(nearCacheRegistry).registerNearCacheEntity(any(), any(), any());
    nodeCalendarSpringNearCacheService.selfRegister();

    verify(nearCacheRegistry, times(1)).registerNearCacheEntity(any(), any(), any());
  }

  @Test
  void getEntityName() {
    assertEquals(
        NearCacheConstants.NODE_CALENDAR_ENTITY_NAME,
        nodeCalendarSpringNearCacheService.getEntityName());
  }
}
