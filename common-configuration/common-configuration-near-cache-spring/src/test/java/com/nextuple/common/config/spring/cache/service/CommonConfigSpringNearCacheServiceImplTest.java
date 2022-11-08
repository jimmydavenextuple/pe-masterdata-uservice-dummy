package com.nextuple.common.config.spring.cache.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nextuple.common.config.spring.cache.util.TestUtil;
import com.nextuple.common.configuration.cache.domain.CommonConfigCacheKey;
import com.nextuple.common.configuration.cache.domain.CommonConfigCacheValue;
import com.nextuple.core.cache.domain.CacheValue;
import com.nextuple.core.cache.service.GenericFeignCacheService;
import com.nextuple.core.constants.NearCacheConstants;
import com.nextuple.core.registry.NearCacheRegistry;
import com.nextuple.core.spring.service.AbstractGenericFeignClientServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CommonConfigSpringNearCacheServiceImplTest {
  @InjectMocks private CommonConfigSpringNearCacheServiceImpl commonConfigSpringNearCacheService;

  @InjectMocks private TestUtil testUtil;

  @Mock private AbstractGenericFeignClientServiceImpl abstractGenericFeignClientService;

  @Mock
  private GenericFeignCacheService<CommonConfigCacheKey, CommonConfigCacheValue> feignCacheService;

  @Mock private NearCacheRegistry nearCacheRegistry;

  @Test
  void getValidTest() {
    CommonConfigCacheKey cacheKey = testUtil.getCommonConfigCacheKey();
    CommonConfigCacheValue cacheValue = testUtil.getCommonConfigCacheValue();

    when(feignCacheService.get(any())).thenReturn(cacheValue);
    when(abstractGenericFeignClientService.get(any())).thenReturn(cacheValue);

    // First Invocation
    CacheValue cacheValue1 = commonConfigSpringNearCacheService.get(cacheKey);
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
    CommonConfigCacheKey cacheKey = testUtil.getCommonConfigCacheKey();

    when(feignCacheService.get(any())).thenReturn(null);
    assertNull(commonConfigSpringNearCacheService.get(cacheKey));
    verify(feignCacheService, times(1)).get(cacheKey);
  }

  @Test
  void deleteTest() {
    CommonConfigCacheKey cacheKey = testUtil.getCommonConfigCacheKey();

    commonConfigSpringNearCacheService.delete(cacheKey);
    CacheValue cacheValue = commonConfigSpringNearCacheService.get(cacheKey);
    assertNull(cacheValue);
  }

  @Test
  void deleteAllTest() {
    CommonConfigCacheKey cacheKey = testUtil.getCommonConfigCacheKey();

    commonConfigSpringNearCacheService.deleteAll();
    CacheValue cacheValue = commonConfigSpringNearCacheService.get(cacheKey);
    assertNull(cacheValue);
  }

  @Test
  void selfRegister() {
    doNothing().when(nearCacheRegistry).registerNearCacheEntity(any(), any(), any());
    commonConfigSpringNearCacheService.selfRegister();

    verify(nearCacheRegistry, times(1)).registerNearCacheEntity(any(), any(), any());
  }

  @Test
  void getEntityName() {
    assertEquals(
        NearCacheConstants.COMMON_CONFIG_ENTITY_NAME,
        commonConfigSpringNearCacheService.getEntityName());
  }
}
