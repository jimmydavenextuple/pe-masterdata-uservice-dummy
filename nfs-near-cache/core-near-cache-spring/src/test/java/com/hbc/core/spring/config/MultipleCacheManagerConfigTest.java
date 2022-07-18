package com.hbc.core.spring.config;

import static com.hbc.core.spring.config.CacheProperties.CACHE_PROPERTY_VALUE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.CacheManager;

@ExtendWith(MockitoExtension.class)
class MultipleCacheManagerConfigTest {

  @InjectMocks MultipleCacheManagerConfig multipleCacheManagerConfig;

  @Mock private CacheProperties cacheProperties;

  @Test
  void caffeineCacheManagerTest() {
    when(cacheProperties.getCacheMap()).thenReturn(getCacheMap());
    CacheManager cacheManager = multipleCacheManagerConfig.caffeineCacheManager();
    assertNotNull(cacheManager);
    verify(cacheProperties, times(0)).setCacheDefaults();
  }

  @Test
  void caffeineCacheManagerDefaultCachePropertiesTest() {
    when(cacheProperties.getCacheMap()).thenReturn(null);
    CacheManager cacheManager = multipleCacheManagerConfig.caffeineCacheManager();
    assertNotNull(cacheManager);
    verify(cacheProperties, times(1)).setCacheDefaults();
  }

  private Map<String, String> getCacheMap() {
    Map<String, String> cacheMap = new HashMap<>();
    cacheMap.put("node", CACHE_PROPERTY_VALUE);
    cacheMap.put("carrier", CACHE_PROPERTY_VALUE);
    cacheMap.put("node_carrier", CACHE_PROPERTY_VALUE);
    cacheMap.put("node_calendar", CACHE_PROPERTY_VALUE);
    cacheMap.put("carrier_calendar", CACHE_PROPERTY_VALUE);
    cacheMap.put("node_carrier_calendar", CACHE_PROPERTY_VALUE);
    cacheMap.put("postal code timezone", CACHE_PROPERTY_VALUE);
    cacheMap.put("sourcingRule", CACHE_PROPERTY_VALUE);
    cacheMap.put("weightage configuration", CACHE_PROPERTY_VALUE);
    return cacheMap;
  }
}
