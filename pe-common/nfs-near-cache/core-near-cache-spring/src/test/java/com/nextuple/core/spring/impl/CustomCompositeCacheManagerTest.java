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
