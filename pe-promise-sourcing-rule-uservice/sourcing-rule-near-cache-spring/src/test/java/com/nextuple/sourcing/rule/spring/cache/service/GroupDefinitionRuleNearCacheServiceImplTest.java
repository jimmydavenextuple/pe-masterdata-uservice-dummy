/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.sourcing.rule.spring.cache.service;

import static org.junit.jupiter.api.Assertions.*;
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
import com.nextuple.sourcing.rule.cache.domain.GroupDefinitionRuleCacheKey;
import com.nextuple.sourcing.rule.cache.domain.GroupDefinitionRuleCacheValue;
import com.nextuple.sourcing.rule.spring.cache.util.TestUtil;
import org.junit.jupiter.api.Assertions;
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
class GroupDefinitionRuleNearCacheServiceImplTest {
  @InjectMocks private GroupDefinitionRuleNearCacheServiceImpl groupDefinitionRuleNearCacheService;
  @InjectMocks private TestUtil testUtil;

  @Mock
  private GenericFeignCacheService<GroupDefinitionRuleCacheKey, GroupDefinitionRuleCacheValue>
      feignCacheService;

  @Mock private NearCacheRegistry nearCacheRegistry;
  @Mock private CaffeineCacheManager caffeineCacheManager;

  @BeforeEach
  void setup() {
    MockitoAnnotations.openMocks(this);
    ReflectionTestUtils.setField(
        groupDefinitionRuleNearCacheService, "cacheManager", caffeineCacheManager);
    ReflectionTestUtils.setField(
        groupDefinitionRuleNearCacheService, "nearCacheRegistry", nearCacheRegistry);
    ReflectionTestUtils.setField(
        groupDefinitionRuleNearCacheService, "feignCacheService", feignCacheService);
  }

  @Test
  @DisplayName("get test for valid data")
  void getTestForValidData() {
    GroupDefinitionRuleCacheKey cacheKey = testUtil.getGroupDefinitionRuleCacheKey();
    GroupDefinitionRuleCacheValue cacheValue = testUtil.getGroupDefinitionRuleCacheValue();

    when(feignCacheService.get(any())).thenReturn(cacheValue);
    AbstractGenericFeignClientServiceImpl abstractGenericFeignClientService =
        Mockito.mock(AbstractGenericFeignClientServiceImpl.class, Mockito.RETURNS_MOCKS);
    when(abstractGenericFeignClientService.get(any())).thenReturn(cacheValue);

    // First Invocation
    CacheValue cacheValue1 = groupDefinitionRuleNearCacheService.get(cacheKey);
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
  @DisplayName("Get Test for invalid data")
  void getTestForInvalidData() {
    GroupDefinitionRuleCacheKey cacheKey = testUtil.getGroupDefinitionRuleCacheKey();
    when(feignCacheService.get(any())).thenReturn(null);
    Assertions.assertNull(groupDefinitionRuleNearCacheService.get(cacheKey));
    verify(feignCacheService, times(1)).get(cacheKey);
  }

  @Test
  @DisplayName("Delete cache entry test")
  void deleteTest() {
    GroupDefinitionRuleCacheKey cacheKey = testUtil.getGroupDefinitionRuleCacheKey();
    groupDefinitionRuleNearCacheService.delete(cacheKey);
    CacheValue value = groupDefinitionRuleNearCacheService.get(cacheKey);
    Assertions.assertNull(value);
  }

  @Test
  @DisplayName("Delete all test")
  void deleteAllTest() {
    GroupDefinitionRuleCacheKey cacheKey = testUtil.getGroupDefinitionRuleCacheKey();
    groupDefinitionRuleNearCacheService.deleteAll();
    CacheValue cacheValue = groupDefinitionRuleNearCacheService.get(cacheKey);
    assertNull(cacheValue);
  }

  @Test
  @DisplayName("Self register test")
  void selfRegister() {
    doNothing().when(nearCacheRegistry).registerNearCacheEntity(any(), any(), any());
    groupDefinitionRuleNearCacheService.selfRegister();

    verify(nearCacheRegistry, times(1)).registerNearCacheEntity(any(), any(), any());
  }

  @Test
  @DisplayName("Get Entity name test")
  void getEntityName() {
    assertEquals(
        NearCacheConstants.GROUP_DEFINITION_RULE_ENTITY_NAME,
        groupDefinitionRuleNearCacheService.getEntityName());
  }
}
