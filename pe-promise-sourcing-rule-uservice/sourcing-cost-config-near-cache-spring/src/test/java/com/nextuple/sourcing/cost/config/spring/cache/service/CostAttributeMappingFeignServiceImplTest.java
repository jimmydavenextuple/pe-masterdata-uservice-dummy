/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.sourcing.cost.config.spring.cache.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.nextuple.sourcing.cost.config.cache.domain.CostAttributeMappingCacheKey;
import com.nextuple.sourcing.cost.config.cache.domain.CostAttributeMappingCacheValue;
import com.nextuple.sourcing.cost.config.spring.cache.feign.CostAttributeMappingFeignImpl;
import com.nextuple.sourcing.cost.config.spring.cache.mapper.CostAttributeMappingMapper;
import com.nextuple.sourcing.cost.config.spring.cache.utils.TestUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CostAttributeMappingFeignServiceImplTest {

  @InjectMocks private CostAttributeMappingFeignServiceImpl costAttributeMappingFeignServiceImpl;

  @InjectMocks private TestUtil testUtil;

  @Mock private CostAttributeMappingMapper mapper;

  @Mock private CostAttributeMappingFeignImpl costAttributeMappingFeign;

  @Test
  void getTest() {

    CostAttributeMappingCacheKey cacheKey = testUtil.getCostAttributeMappingCacheKey();
    CostAttributeMappingCacheValue cacheValue = testUtil.getCostAttributeMappingCacheValue();

    Mockito.when(
            costAttributeMappingFeign.getCostAttributeMappingByOrgIdAndCanonicalName(
                anyString(), anyString()))
        .thenReturn(testUtil.getBaseResponseOfCostAttributeMapping());
    Mockito.when(mapper.responseToCacheValue(any())).thenReturn(cacheValue);

    assertEquals(cacheValue, costAttributeMappingFeignServiceImpl.get(cacheKey));
    assertFalse(costAttributeMappingFeignServiceImpl.get(cacheKey).isUndefined());
    verify(mapper, times(2)).responseToCacheValue(any());
  }

  @Test
  void getForExceptionTest() {

    Mockito.when(mapper.responseToCacheValue(any()))
        .thenThrow(new RuntimeException("Error message"));
    var response =
        costAttributeMappingFeignServiceImpl.get(testUtil.getCostAttributeMappingCacheKey());

    assertNotNull(response);
    assertNull(response.getCostAttributeMappingResponse());
    verify(mapper, times(1)).responseToCacheValue(any());
  }
}
