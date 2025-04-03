/*
 * Copyright (c) 2025., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.core.spring.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.nextuple.core.spring.config.CacheProperties;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.cache.Cache;

class CustomCompositeCacheManagerTest {

  @Mock private CacheProperties cacheProperties;

  @Mock private CaffeineCacheManagerStrategy caffeineCacheManagerStrategy;

  @Mock private RedisCacheManagerStrategy redisCacheManagerStrategy;

  @Mock private Cache caffeineCache;

  @Mock private Cache redisCache;

  private CustomCompositeCacheManager customCompositeCacheManager;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    when(caffeineCacheManagerStrategy.getCache(anyString())).thenReturn(caffeineCache);
    when(redisCacheManagerStrategy.getCache(anyString())).thenReturn(redisCache);
    customCompositeCacheManager =
        new CustomCompositeCacheManager(
            Optional.of(redisCacheManagerStrategy),
            caffeineCacheManagerStrategy,
            cacheProperties,
            false);
  }

  @Test
  void getCache_returnsCaffeineCache_whenRedisDisabled() {
    when(cacheProperties.getCacheManagerMap()).thenReturn(new HashMap<>());
    Cache cache = customCompositeCacheManager.getCache("testCache");
    assertEquals(caffeineCache, cache);
  }

  @Test
  void getCache_returnsRedisCache_whenRedisEnabled() {
    Map<String, String> cacheManagerMap = new HashMap<>();
    cacheManagerMap.put("testCache", "redis");
    when(cacheProperties.getCacheManagerMap()).thenReturn(cacheManagerMap);
    customCompositeCacheManager =
        new CustomCompositeCacheManager(
            Optional.of(redisCacheManagerStrategy),
            caffeineCacheManagerStrategy,
            cacheProperties,
            true);
    Cache cache = customCompositeCacheManager.getCache("testCache");
    assertEquals(redisCache, cache);
  }

  @Test
  void getCache_returnsCaffeineCache_whenRedisEnabledButCacheTypeIsCaffeine() {
    Map<String, String> cacheManagerMap = new HashMap<>();
    cacheManagerMap.put("testCache", "caffeine");
    when(cacheProperties.getCacheManagerMap()).thenReturn(cacheManagerMap);
    customCompositeCacheManager =
        new CustomCompositeCacheManager(
            Optional.of(redisCacheManagerStrategy),
            caffeineCacheManagerStrategy,
            cacheProperties,
            true);
    Cache cache = customCompositeCacheManager.getCache("testCache");
    assertEquals(caffeineCache, cache);
  }
}
