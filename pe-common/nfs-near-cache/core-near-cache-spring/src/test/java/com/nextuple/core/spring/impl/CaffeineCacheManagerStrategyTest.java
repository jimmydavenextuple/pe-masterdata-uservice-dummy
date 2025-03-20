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
