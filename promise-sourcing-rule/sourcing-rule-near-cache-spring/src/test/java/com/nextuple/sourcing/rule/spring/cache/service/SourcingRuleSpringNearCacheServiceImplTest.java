package com.nextuple.sourcing.rule.spring.cache.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nextuple.core.cache.domain.CacheValue;
import com.nextuple.core.cache.service.GenericFeignCacheService;
import com.nextuple.core.spring.service.AbstractGenericFeignClientServiceImpl;
import com.nextuple.sourcing.rule.cache.domain.SourcingRuleCacheKey;
import com.nextuple.sourcing.rule.cache.domain.SourcingRuleCacheValue;
import com.nextuple.sourcing.rule.spring.cache.util.TestUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SourcingRuleSpringNearCacheServiceImplTest {

  @BeforeEach
  public void init() {
    MockitoAnnotations.openMocks(this);
  }

  @InjectMocks private SourcingRuleSpringNearCacheServiceImpl sourcingRuleSpringNearCacheService;

  @InjectMocks private TestUtil testUtil;

  @Mock
  private GenericFeignCacheService<SourcingRuleCacheKey, SourcingRuleCacheValue> feignCacheService;

  @Test
  void getForValidFetchPromiseSourcingRuleRequestTest() {
    SourcingRuleCacheKey cacheKey = testUtil.getSourcingRuleCacheKey();
    SourcingRuleCacheValue cacheValue = testUtil.getSourcingRuleCacheValue();

    when(feignCacheService.get(any())).thenReturn(cacheValue);
    AbstractGenericFeignClientServiceImpl abstractGenericSpringLocalCacheService =
        Mockito.mock(AbstractGenericFeignClientServiceImpl.class, Mockito.RETURNS_MOCKS);
    Mockito.when(abstractGenericSpringLocalCacheService.get(any())).thenReturn(cacheValue);
    // First Invocation
    CacheValue cacheValue1 = sourcingRuleSpringNearCacheService.get(cacheKey);
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
    SourcingRuleCacheKey cacheKey = testUtil.getSourcingRuleCacheKey();

    Mockito.when(feignCacheService.get(any())).thenReturn(null);
    assertNull(sourcingRuleSpringNearCacheService.get(cacheKey));
    verify(feignCacheService, times(1)).get(cacheKey);
  }

  @Test
  void deleteTest() {
    SourcingRuleCacheKey cacheKey = testUtil.getSourcingRuleCacheKey();
    sourcingRuleSpringNearCacheService.delete(cacheKey);
    CacheValue cacheValue = sourcingRuleSpringNearCacheService.get(cacheKey);
    assertNull(cacheValue);
  }

  @Test
  void deleteAllTest() {
    SourcingRuleCacheKey cacheKey = testUtil.getSourcingRuleCacheKey();
    sourcingRuleSpringNearCacheService.deleteAll();
    CacheValue cacheValue = sourcingRuleSpringNearCacheService.get(cacheKey);
    assertNull(cacheValue);
  }
}
