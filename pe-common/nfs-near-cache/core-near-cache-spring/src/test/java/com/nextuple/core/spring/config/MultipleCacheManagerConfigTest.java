package com.nextuple.core.spring.config;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nextuple.core.spring.util.TestUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.CacheManager;

@ExtendWith(MockitoExtension.class)
class MultipleCacheManagerConfigTest {

  @InjectMocks private MultipleCacheManagerConfig multipleCacheManagerConfig;
  @Mock private CacheProperties cacheProperties;
  @InjectMocks private TestUtil testUtil;

  @Test
  void caffeineCacheManagerTest() {
    when(cacheProperties.getCachemap()).thenReturn(testUtil.getCacheMap());
    CacheManager cacheManager = multipleCacheManagerConfig.cacheManager();
    assertNotNull(cacheManager);
    verify(cacheProperties, times(0)).setCacheDefaults();
  }

  @Test
  void caffeineCacheManagerDefaultCachePropertiesTest() {
    when(cacheProperties.getCachemap()).thenReturn(null);
    when(cacheProperties.setCacheDefaults()).thenReturn(testUtil.getCacheMap());
    CacheManager cacheManager = multipleCacheManagerConfig.cacheManager();
    assertNotNull(cacheManager);
    verify(cacheProperties, times(1)).setCacheDefaults();
  }
}
