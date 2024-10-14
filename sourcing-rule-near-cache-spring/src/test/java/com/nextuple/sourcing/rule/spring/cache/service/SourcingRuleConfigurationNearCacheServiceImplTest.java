/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */
package com.nextuple.sourcing.rule.spring.cache.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.nextuple.core.cache.domain.CacheValue;
import com.nextuple.core.cache.service.GenericFeignCacheService;
import com.nextuple.core.constants.NearCacheConstants;
import com.nextuple.core.registry.NearCacheRegistry;
import com.nextuple.core.spring.service.AbstractGenericFeignClientServiceImpl;
import com.nextuple.sourcing.rule.cache.domain.SourcingRuleConfigurationFetchRulesKey;
import com.nextuple.sourcing.rule.cache.domain.SourcingRuleConfigurationFetchRulesValue;
import com.nextuple.sourcing.rule.spring.cache.util.TestUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class SourcingRuleConfigurationNearCacheServiceImplTest {
  @BeforeEach
  public void init() {
    MockitoAnnotations.openMocks(this);
  }

  @InjectMocks
  private SourcingRuleConfigurationNearCacheServiceImpl sourcingRuleConfigurationNearCacheService;

  @InjectMocks private TestUtil testUtil;

  @Mock
  private GenericFeignCacheService<
          SourcingRuleConfigurationFetchRulesKey, SourcingRuleConfigurationFetchRulesValue>
      feignCacheService;

  @Mock private NearCacheRegistry nearCacheRegistry;
  @Mock private CaffeineCacheManager caffeineCacheManager;

  @BeforeEach
  void setup() {
    MockitoAnnotations.openMocks(this);
    ReflectionTestUtils.setField(
        sourcingRuleConfigurationNearCacheService, "cacheManager", caffeineCacheManager);
    // Added this
    ReflectionTestUtils.setField(
        sourcingRuleConfigurationNearCacheService, "feignCacheService", feignCacheService);
  }

  @Test
  void getTestForValidData() {

    SourcingRuleConfigurationFetchRulesKey cacheKey =
        testUtil.getSourcingRuleConfigurationFetchRulesCacheKey();
    SourcingRuleConfigurationFetchRulesValue cacheValue =
        testUtil.getSourcingRuleConfigurationFetchRuleCacheValue();

    Mockito.when(feignCacheService.get(any())).thenReturn(cacheValue);
    AbstractGenericFeignClientServiceImpl abstractGenericSpringLocalCacheService =
        Mockito.mock(AbstractGenericFeignClientServiceImpl.class, Mockito.RETURNS_MOCKS);
    Mockito.when(abstractGenericSpringLocalCacheService.get(any())).thenReturn(cacheValue);
    // First Invocation
    CacheValue cacheValue1 = sourcingRuleConfigurationNearCacheService.get(cacheKey);
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
  void getTestForInValidData() {
    SourcingRuleConfigurationFetchRulesKey cacheKey =
        testUtil.getSourcingRuleConfigurationFetchRulesCacheKey();

    Mockito.when(feignCacheService.get(any())).thenReturn(null);
    assertNull(sourcingRuleConfigurationNearCacheService.get(cacheKey));
    verify(feignCacheService, times(1)).get(cacheKey);
  }

  @Test
  void deleteTest() {
    SourcingRuleConfigurationFetchRulesKey cacheKey =
        testUtil.getSourcingRuleConfigurationFetchRulesCacheKey();
    sourcingRuleConfigurationNearCacheService.delete(cacheKey);
    CacheValue cacheValue = sourcingRuleConfigurationNearCacheService.get(cacheKey);
    assertNull(cacheValue);
  }

  @Test
  void deleteAllTest() {
    SourcingRuleConfigurationFetchRulesKey cacheKey =
        testUtil.getSourcingRuleConfigurationFetchRulesCacheKey();
    sourcingRuleConfigurationNearCacheService.deleteAll();
    CacheValue cacheValue = sourcingRuleConfigurationNearCacheService.get(cacheKey);
    assertNull(cacheValue);
  }

  @Test
  void selfRegister() {
    doNothing().when(nearCacheRegistry).registerNearCacheEntity(any(), any(), any());
    sourcingRuleConfigurationNearCacheService.selfRegister();

    verify(nearCacheRegistry, times(1)).registerNearCacheEntity(any(), any(), any());
  }

  @Test
  void getEntityName() {
    assertEquals(
        NearCacheConstants.SOURCING_RULE_DETAILS_ENTITY_NAME,
        sourcingRuleConfigurationNearCacheService.getEntityName());
  }
}
