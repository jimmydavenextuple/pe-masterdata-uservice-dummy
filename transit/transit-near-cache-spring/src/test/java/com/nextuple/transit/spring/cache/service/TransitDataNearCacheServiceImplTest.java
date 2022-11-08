package com.nextuple.transit.spring.cache.service;

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
import com.nextuple.transit.cache.domain.TransitCacheKey;
import com.nextuple.transit.cache.domain.TransitCacheValue;
import com.nextuple.transit.spring.cache.util.TestUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TransitDataNearCacheServiceImplTest {

  @InjectMocks private TransitDataNearCacheServiceImpl transitDataNearCacheService;

  @InjectMocks private TestUtil testUtil;

  @Mock private AbstractGenericFeignClientServiceImpl abstractGenericFeignClientService;

  @Mock private GenericFeignCacheService<TransitCacheKey, TransitCacheValue> feignCacheService;

  @Mock private NearCacheRegistry nearCacheRegistry;

  @Test
  void getValidTest() {
    TransitCacheKey cacheKey = testUtil.getTransitCacheKey();
    TransitCacheValue cacheValue = testUtil.getTransitCacheValue();

    when(feignCacheService.get(any())).thenReturn(cacheValue);
    when(abstractGenericFeignClientService.get(any())).thenReturn(cacheValue);

    // First Invocation
    CacheValue cacheValue1 = transitDataNearCacheService.get(cacheKey);
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
    TransitCacheKey cacheKey = testUtil.getTransitCacheKey();

    when(feignCacheService.get(any())).thenReturn(null);
    assertNull(transitDataNearCacheService.get(cacheKey));
    verify(feignCacheService, times(1)).get(cacheKey);
  }

  @Test
  void deleteTest() {
    TransitCacheKey cacheKey = testUtil.getTransitCacheKey();

    transitDataNearCacheService.delete(cacheKey);
    CacheValue cacheValue = transitDataNearCacheService.get(cacheKey);
    assertNull(cacheValue);
  }

  @Test
  void deleteAllTest() {
    TransitCacheKey cacheKey = testUtil.getTransitCacheKey();

    transitDataNearCacheService.deleteAll();
    CacheValue cacheValue = transitDataNearCacheService.get(cacheKey);
    assertNull(cacheValue);
  }

  @Test
  void selfRegister() {
    doNothing().when(nearCacheRegistry).registerNearCacheEntity(any(), any(), any());
    transitDataNearCacheService.selfRegister();

    verify(nearCacheRegistry, times(1)).registerNearCacheEntity(any(), any(), any());
  }

  @Test
  void getEntityName() {
    assertEquals(
        NearCacheConstants.TRANSIT_ENTITY_NAME, transitDataNearCacheService.getEntityName());
  }
}
