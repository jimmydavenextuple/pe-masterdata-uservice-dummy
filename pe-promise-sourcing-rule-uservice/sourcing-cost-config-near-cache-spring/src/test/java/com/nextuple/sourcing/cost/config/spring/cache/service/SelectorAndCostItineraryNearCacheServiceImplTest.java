/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
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
import com.nextuple.sourcing.cost.config.cache.domain.SelectorAndCostItineraryCacheKey;
import com.nextuple.sourcing.cost.config.cache.domain.SelectorAndCostItineraryCacheValue;
import com.nextuple.sourcing.cost.config.spring.cache.utils.TestUtil;
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
class SelectorAndCostItineraryNearCacheServiceImplTest {

  @InjectMocks
  private SelectorAndCostItineraryNearCacheServiceImpl selectorAndCostItineraryNearCacheService;

  @InjectMocks private TestUtil testUtil;

  @Mock
  private GenericFeignCacheService<
          SelectorAndCostItineraryCacheKey, SelectorAndCostItineraryCacheValue>
      feignCacheService;

  @Mock private NearCacheRegistry nearCacheRegistry;
  @Mock private CaffeineCacheManager caffeineCacheManager;

  @BeforeEach
  void setup() {
    MockitoAnnotations.openMocks(this);
    ReflectionTestUtils.setField(
        selectorAndCostItineraryNearCacheService, "cacheManager", caffeineCacheManager);
    // Added this
    ReflectionTestUtils.setField(
        selectorAndCostItineraryNearCacheService, "feignCacheService", feignCacheService);
  }

  @Test
  void getTestForValidData() {

    SelectorAndCostItineraryCacheKey cacheKey = testUtil.getSelectorAndCostItineraryCacheKey();
    SelectorAndCostItineraryCacheValue cacheValue =
        testUtil.getSelectorAndCostItineraryCacheValue();

    Mockito.when(feignCacheService.get(any())).thenReturn(cacheValue);
    AbstractGenericFeignClientServiceImpl abstractGenericSpringLocalCacheService =
        Mockito.mock(AbstractGenericFeignClientServiceImpl.class, Mockito.RETURNS_MOCKS);
    Mockito.when(abstractGenericSpringLocalCacheService.get(any())).thenReturn(cacheValue);
    // First Invocation
    CacheValue cacheValue1 = selectorAndCostItineraryNearCacheService.get(cacheKey);
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
    SelectorAndCostItineraryCacheKey cacheKey = testUtil.getSelectorAndCostItineraryCacheKey();

    Mockito.when(feignCacheService.get(any())).thenReturn(null);
    assertNull(selectorAndCostItineraryNearCacheService.get(cacheKey));
    verify(feignCacheService, times(1)).get(cacheKey);
  }

  @Test
  void deleteTest() {
    SelectorAndCostItineraryCacheKey cacheKey = testUtil.getSelectorAndCostItineraryCacheKey();
    selectorAndCostItineraryNearCacheService.delete(cacheKey);
    CacheValue cacheValue = selectorAndCostItineraryNearCacheService.get(cacheKey);
    assertNull(cacheValue);
  }

  @Test
  void deleteAllTest() {
    SelectorAndCostItineraryCacheKey cacheKey = testUtil.getSelectorAndCostItineraryCacheKey();
    selectorAndCostItineraryNearCacheService.deleteAll();
    CacheValue cacheValue = selectorAndCostItineraryNearCacheService.get(cacheKey);
    assertNull(cacheValue);
  }

  @Test
  void selfRegister() {
    doNothing().when(nearCacheRegistry).registerNearCacheEntity(any(), any(), any());
    selectorAndCostItineraryNearCacheService.selfRegister();

    verify(nearCacheRegistry, times(1)).registerNearCacheEntity(any(), any(), any());
  }

  @Test
  void getEntityName() {
    assertEquals(
        NearCacheConstants.SELECTOR_AND_COST_ITINERARY_ENTITY_NAME,
        selectorAndCostItineraryNearCacheService.getEntityName());
  }
}
