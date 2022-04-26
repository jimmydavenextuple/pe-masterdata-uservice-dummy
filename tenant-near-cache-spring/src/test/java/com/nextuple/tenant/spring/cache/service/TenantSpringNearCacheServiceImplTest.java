package com.nextuple.tenant.spring.cache.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.nextuple.core.cache.domain.CacheValue;
import com.nextuple.core.cache.service.GenericFeignCacheService;
import com.nextuple.core.spring.service.AbstractGenericFeignClientServiceImpl;
import com.nextuple.tenant.cache.domain.TenantCacheKey;
import com.nextuple.tenant.cache.domain.TenantCacheValue;
import com.nextuple.tenant.spring.cache.util.TestUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TenantSpringNearCacheServiceImplTest {

  @BeforeEach
  public void init() {
    MockitoAnnotations.openMocks(this);
  }

  @InjectMocks private TenantSpringNearCacheServiceImpl tenantSpringNearCacheService;

  @InjectMocks private TestUtil testUtil;

  @Mock private TenantFeignClientServiceImpl tenantFeignClientService;

  @Mock private GenericFeignCacheService<TenantCacheKey, TenantCacheValue> feignCacheService;

  @Test
  void getTestForValidTenantObjectId() {

    TenantCacheKey cacheKey = testUtil.getTenantCacheKey("5dc9ae77718224000c992f92");
    TenantCacheValue cacheValue = testUtil.getTenantCacheValue("NEXTUPLE_GR", "Nextuple");

    Mockito.when(feignCacheService.get(any())).thenReturn(cacheValue);
    AbstractGenericFeignClientServiceImpl abstractGenericSpringLocalCacheService =
        Mockito.mock(AbstractGenericFeignClientServiceImpl.class, Mockito.RETURNS_MOCKS);
    Mockito.when(abstractGenericSpringLocalCacheService.get(any())).thenReturn(cacheValue);
    // First Invocation
    CacheValue cacheValue1 = tenantSpringNearCacheService.get(cacheKey);
    assertEquals(cacheValue, cacheValue1);
    // Second Invocation
    CacheValue cacheValue2 = abstractGenericSpringLocalCacheService.get(cacheKey);
    assertEquals(cacheValue, cacheValue2);
    // Third Invocation
    CacheValue cacheValue3 = abstractGenericSpringLocalCacheService.get(cacheKey);
    assertEquals(cacheValue, cacheValue3);
    verify(feignCacheService, times(1)).get(cacheKey);
  }

  @Test
  void getTestForInValidTenantObjectId() {
    TenantCacheKey cacheKey = testUtil.getTenantCacheKey("invalid");

    Mockito.when(feignCacheService.get(any())).thenReturn(null);
    assertNull(tenantSpringNearCacheService.get(cacheKey));
    verify(feignCacheService, times(1)).get(cacheKey);
  }
}
