/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.sourcing.cost.config.spring.cache.service;

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
import com.nextuple.sourcing.cost.config.cache.domain.OptimizationAndCostTypesMappingKey;
import com.nextuple.sourcing.cost.config.cache.domain.OptimizationAndCostTypesMappingValue;
import com.nextuple.sourcing.cost.config.spring.cache.utils.TestUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
class OptimizationAndCostTypesMappingNearCacheServiceImplTest {

  @InjectMocks
  private OptimizationAndCostTypesMappingNearCacheServiceImpl rulesConfigurationNearCacheService;

  @InjectMocks private TestUtil testUtil;

  @Mock
  private GenericFeignCacheService<
          OptimizationAndCostTypesMappingKey, OptimizationAndCostTypesMappingValue>
      feignCacheService;

  @Mock private NearCacheRegistry nearCacheRegistry;
  @Mock private CaffeineCacheManager caffeineCacheManager;

  @BeforeEach
  public void init() {
    MockitoAnnotations.openMocks(this);
    ReflectionTestUtils.setField(
        rulesConfigurationNearCacheService, "cacheManager", caffeineCacheManager);
    ReflectionTestUtils.setField(
        rulesConfigurationNearCacheService, "feignCacheService", feignCacheService);
  }

  @Test
  @DisplayName("Self register near cache")
  void selfRegisterTest() {
    doNothing().when(nearCacheRegistry).registerNearCacheEntity(any(), any(), any());
    rulesConfigurationNearCacheService.selfRegister();

    verify(nearCacheRegistry, times(1)).registerNearCacheEntity(any(), any(), any());
  }

  @Test
  @DisplayName("Get entity name")
  void getEntityNameTest() {
    assertEquals(
        NearCacheConstants.OPT_COST_TYPES_MAPPING_ENTITY_NAME,
        rulesConfigurationNearCacheService.getEntityName());
  }

  @Test
  @DisplayName("Delete near cache data")
  void deleteTest() {
    OptimizationAndCostTypesMappingKey cacheKey = testUtil.getOptimizationAndCostTypesMappingKey();
    rulesConfigurationNearCacheService.delete(cacheKey);
    CacheValue cacheValue = rulesConfigurationNearCacheService.get(cacheKey);
    assertNull(cacheValue);
  }

  @Test
  @DisplayName("Delete all near cache data")
  void deleteAllTest() {
    OptimizationAndCostTypesMappingKey cacheKey = testUtil.getOptimizationAndCostTypesMappingKey();

    rulesConfigurationNearCacheService.deleteAll();
    CacheValue cacheValue = rulesConfigurationNearCacheService.get(cacheKey);
    assertNull(cacheValue);
  }

  @Test
  @DisplayName("Get Cache Value for valid data")
  void getCacheValueTestForValidData() {

    OptimizationAndCostTypesMappingKey cacheKey = testUtil.getOptimizationAndCostTypesMappingKey();
    OptimizationAndCostTypesMappingValue cacheValue =
        testUtil.getOptimizationAndCostTypesMappingValue();

    Mockito.when(feignCacheService.get(any())).thenReturn(cacheValue);
    AbstractGenericFeignClientServiceImpl abstractGenericSpringLocalCacheService =
        Mockito.mock(AbstractGenericFeignClientServiceImpl.class, Mockito.RETURNS_MOCKS);
    Mockito.when(abstractGenericSpringLocalCacheService.get(any())).thenReturn(cacheValue);
    // First Invocation
    CacheValue cacheValue1 = rulesConfigurationNearCacheService.get(cacheKey);
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
  @DisplayName("Get Cache Value for invalid data")
  void getCacheValueTestForInValidData() {
    OptimizationAndCostTypesMappingKey cacheKey = testUtil.getOptimizationAndCostTypesMappingKey();

    Mockito.when(feignCacheService.get(any())).thenReturn(null);
    assertNull(rulesConfigurationNearCacheService.get(cacheKey));
    verify(feignCacheService, times(1)).get(cacheKey);
  }
}
