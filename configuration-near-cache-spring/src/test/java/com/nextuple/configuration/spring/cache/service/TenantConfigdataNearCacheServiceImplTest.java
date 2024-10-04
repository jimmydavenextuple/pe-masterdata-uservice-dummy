/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.configuration.spring.cache.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nextuple.configuration.cache.domain.TenantConfigdataCacheKey;
import com.nextuple.configuration.cache.domain.TenantConfigdataCacheValue;
import com.nextuple.configuration.spring.cache.util.TestUtil;
import com.nextuple.core.cache.domain.CacheValue;
import com.nextuple.core.cache.service.GenericFeignCacheService;
import com.nextuple.core.constants.NearCacheConstants;
import com.nextuple.core.registry.NearCacheRegistry;
import com.nextuple.core.spring.service.AbstractGenericFeignClientServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class TenantConfigdataNearCacheServiceImplTest {
  @InjectMocks private TenantConfigdataNearCacheServiceImpl tenantConfigdataNearCacheService;

  @Mock
  private GenericFeignCacheService<TenantConfigdataCacheKey, TenantConfigdataCacheValue>
      feignCacheService;

  @InjectMocks private TestUtil testUtil;
  @Mock private NearCacheRegistry nearCacheRegistry;
  @Mock private CaffeineCacheManager caffeineCacheManager;

  @BeforeEach
  void setup() {
    ReflectionTestUtils.setField(
        tenantConfigdataNearCacheService, "cacheManager", caffeineCacheManager);
    // Added this
    ReflectionTestUtils.setField(
        tenantConfigdataNearCacheService, "feignCacheService", feignCacheService);
  }

  @Test
  void getTestForValidData() {
    TenantConfigdataCacheKey cacheKey = testUtil.getTenantConfigdataCacheKey();
    TenantConfigdataCacheValue cacheValue = testUtil.getTenantConfigCacheValue();

    when(feignCacheService.get(any())).thenReturn(cacheValue);
    AbstractGenericFeignClientServiceImpl abstractGenericSpringLocalCacheService =
        Mockito.mock(AbstractGenericFeignClientServiceImpl.class, Mockito.RETURNS_MOCKS);
    Mockito.when(abstractGenericSpringLocalCacheService.get(any())).thenReturn(cacheValue);

    CacheValue cacheValue1 = tenantConfigdataNearCacheService.get(cacheKey);
    assertEquals(cacheValue, cacheValue1);

    CacheValue cacheValue2 = abstractGenericSpringLocalCacheService.get(cacheKey);
    assertEquals(cacheValue, cacheValue2);

    CacheValue cacheValue3 = abstractGenericSpringLocalCacheService.get(cacheKey);
    assertEquals(cacheValue, cacheValue3);

    verify(feignCacheService, times(1)).get(cacheKey);
  }

  @Test
  void getTestForInValidData() {
    TenantConfigdataCacheKey cacheKey = testUtil.getTenantConfigdataCacheKey();
    when(feignCacheService.get(any())).thenReturn(null);
    assertNull(tenantConfigdataNearCacheService.get(cacheKey));
    verify(feignCacheService, times(1)).get(cacheKey);
  }

  @Test
  void deleteTest() {
    TenantConfigdataCacheKey cacheKey = testUtil.getTenantConfigdataCacheKey();
    tenantConfigdataNearCacheService.delete(cacheKey);
    CacheValue cacheValue = tenantConfigdataNearCacheService.get(cacheKey);
    assertNull(cacheValue);
  }

  @Test
  void deleteAllTest() {
    TenantConfigdataCacheKey cacheKey = testUtil.getTenantConfigdataCacheKey();
    tenantConfigdataNearCacheService.deleteAll();
    CacheValue cacheValue = tenantConfigdataNearCacheService.get(cacheKey);
    assertNull(cacheValue);
  }

  @Test
  void selfRegister() {
    doNothing().when(nearCacheRegistry).registerNearCacheEntity(any(), any(), any());
    tenantConfigdataNearCacheService.selfRegister();
    verify(nearCacheRegistry, times(1)).registerNearCacheEntity(any(), any(), any());
  }

  @Test
  void getEntityName() {
    assertEquals(
        NearCacheConstants.TENANT_CONFIG_ENTITY_NAME,
        tenantConfigdataNearCacheService.getEntityName());
  }
}
