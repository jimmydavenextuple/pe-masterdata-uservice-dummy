package com.nextuple.carrier.spring.cache.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nextuple.carrier.cache.domain.CarrierCacheKey;
import com.nextuple.carrier.cache.domain.CarrierCacheValue;
import com.nextuple.carrier.spring.cache.util.TestUtil;
import com.nextuple.core.cache.domain.CacheValue;
import com.nextuple.core.cache.service.GenericFeignCacheService;
import com.nextuple.core.spring.service.AbstractGenericFeignClientServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CarrierSpringNearCacheServiceImplTest {

  @InjectMocks private CarrierSpringNearCacheServiceImpl carrierSpringNearCacheService;

  @InjectMocks private TestUtil testUtil;

  @Mock private AbstractGenericFeignClientServiceImpl abstractGenericFeignClientService;

  @Mock private GenericFeignCacheService<CarrierCacheKey, CarrierCacheValue> feignCacheService;

  @Test
  void getValidTest() {
    CarrierCacheKey cacheKey = testUtil.getCarrierCacheKey();
    CarrierCacheValue cacheValue = testUtil.getCarrierCacheValue();

    when(feignCacheService.get(any())).thenReturn(cacheValue);
    when(abstractGenericFeignClientService.get(any())).thenReturn(cacheValue);

    // First Invocation
    CacheValue cacheValue1 = carrierSpringNearCacheService.get(cacheKey);
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
    CarrierCacheKey cacheKey = testUtil.getCarrierCacheKey();

    when(feignCacheService.get(any())).thenReturn(null);
    assertNull(carrierSpringNearCacheService.get(cacheKey));
    verify(feignCacheService, times(1)).get(cacheKey);
  }

  @Test
  void deleteTest() {
    CarrierCacheKey cacheKey = testUtil.getCarrierCacheKey();

    carrierSpringNearCacheService.delete(cacheKey);
    CacheValue cacheValue = carrierSpringNearCacheService.get(cacheKey);
    assertNull(cacheValue);
  }
}
