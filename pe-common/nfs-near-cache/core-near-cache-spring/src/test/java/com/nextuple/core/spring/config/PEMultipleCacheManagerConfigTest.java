package com.nextuple.core.spring.config;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nextuple.core.spring.impl.CustomCompositeCacheManager;
import com.nextuple.core.spring.util.TestUtil;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.redisson.api.RedissonClient;
import org.redisson.spring.cache.RedissonSpringCacheManager;
import org.springframework.cache.CacheManager;

@ExtendWith(MockitoExtension.class)
class PEMultipleCacheManagerConfigTest {

  @InjectMocks private PEMultipleCacheManagerConfig PEMultipleCacheManagerConfig;
  @Mock private CacheProperties cacheProperties;
  @InjectMocks private TestUtil testUtil;
  @Mock private RedissonClient redissonClient;

  @Test
  void caffeineCacheManagerTest() {
    when(cacheProperties.getCachemap()).thenReturn(testUtil.getCacheMap());
    CacheManager cacheManager = PEMultipleCacheManagerConfig.cacheManager();
    assertNotNull(cacheManager);
    verify(cacheProperties, times(0)).setCacheDefaults();
  }

  @Test
  void caffeineCacheManagerDefaultCachePropertiesTest() {
    when(cacheProperties.getCachemap()).thenReturn(null);
    when(cacheProperties.setCacheDefaults()).thenReturn(testUtil.getCacheMap());
    CacheManager cacheManager = PEMultipleCacheManagerConfig.cacheManager();
    assertNotNull(cacheManager);
    verify(cacheProperties, times(1)).setCacheDefaults();
  }

  @Test
  void redisCacheManagerTest_whenRedisEnabled() {
    when(cacheProperties.getCachemap()).thenReturn(testUtil.getCacheMap());

    CacheManager cacheManager = PEMultipleCacheManagerConfig.redisCacheManager();

    assertNotNull(cacheManager);
    assertInstanceOf(RedissonSpringCacheManager.class, cacheManager);
  }

  @Test
  void redisCacheManagerTest_whenCacheMapIsNull() {
    Map<String, String> cacheMap = new HashMap<>();
    cacheMap.put("testCache", "redis,1");
    when(cacheProperties.getCachemap()).thenReturn(null);
    when(cacheProperties.setCacheDefaults()).thenReturn(cacheMap);

    CacheManager cacheManager = PEMultipleCacheManagerConfig.redisCacheManager();

    assertNotNull(cacheManager);
    verify(cacheProperties, times(1)).setCacheDefaults();
  }

  @Test
  void compositeCacheManagerTest() {
    CustomCompositeCacheManager customCompositeCacheManager =
        mock(CustomCompositeCacheManager.class);
    CacheManager cacheManager =
        PEMultipleCacheManagerConfig.compositeCacheManager(customCompositeCacheManager);

    assertNotNull(cacheManager);
    assertEquals(customCompositeCacheManager, cacheManager);
  }
}
