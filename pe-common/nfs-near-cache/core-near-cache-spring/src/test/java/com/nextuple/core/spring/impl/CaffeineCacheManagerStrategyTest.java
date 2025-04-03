/*
 * Copyright (c) 2025., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.core.spring.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.Cache;
import org.springframework.cache.caffeine.CaffeineCacheManager;

@ExtendWith(MockitoExtension.class)
class CaffeineCacheManagerStrategyTest {

  @Mock private CaffeineCacheManager caffeineCacheManager;

  @Mock private Cache mockCache;

  @InjectMocks private CaffeineCacheManagerStrategy cacheManagerStrategy;

  @BeforeEach
  void setUp() {
    lenient().when(caffeineCacheManager.getCache("testCache")).thenReturn(mockCache);
  }

  @Test
  void testGetCache_ReturnsCache() {
    Cache cache = cacheManagerStrategy.getCache("testCache");
    assertNotNull(cache);
    assertEquals(mockCache, cache);
  }

  @Test
  void testGetCache_ReturnsNull_WhenCacheNotPresent() {
    when(caffeineCacheManager.getCache("nonExistingCache")).thenReturn(null);
    Cache cache = cacheManagerStrategy.getCache("nonExistingCache");
    assertNull(cache);
  }
}
