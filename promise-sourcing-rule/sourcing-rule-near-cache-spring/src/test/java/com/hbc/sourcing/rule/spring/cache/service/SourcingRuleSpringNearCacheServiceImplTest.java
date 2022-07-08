package com.hbc.sourcing.rule.spring.cache.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hbc.core.cache.domain.CacheValue;
import com.hbc.core.cache.service.GenericFeignCacheService;
import com.hbc.core.constants.NearCacheConstants;
import com.hbc.core.registry.NearCacheRegistry;
import com.hbc.core.spring.service.AbstractGenericFeignClientServiceImpl;
import com.hbc.sourcing.rule.cache.domain.SourcingRuleCacheKey;
import com.hbc.sourcing.rule.cache.domain.SourcingRuleCacheValue;
import com.hbc.sourcing.rule.spring.cache.util.TestUtil;
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

  @Mock private NearCacheRegistry nearCacheRegistry;

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

  @Test
  void selfRegister() {
    doNothing().when(nearCacheRegistry).registerNearCacheEntity(any(), any(), any());
    sourcingRuleSpringNearCacheService.selfRegister();

    verify(nearCacheRegistry, times(1)).registerNearCacheEntity(any(), any(), any());
  }

  @Test
  void getEntityName() {
    assertEquals(
        NearCacheConstants.PROMISE_SOURCING_ENTITY_NAME,
        sourcingRuleSpringNearCacheService.getEntityName());
  }
}
