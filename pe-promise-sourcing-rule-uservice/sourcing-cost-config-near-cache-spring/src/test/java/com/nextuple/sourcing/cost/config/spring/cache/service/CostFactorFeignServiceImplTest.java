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

import com.nextuple.common.response.BaseResponse;
import com.nextuple.sourcing.cost.config.cache.domain.CostFactorCacheKey;
import com.nextuple.sourcing.cost.config.cache.domain.CostFactorCacheValue;
import com.nextuple.sourcing.cost.config.dto.CostFactorDto;
import com.nextuple.sourcing.cost.config.spring.cache.feign.CostFactorFeignImpl;
import com.nextuple.sourcing.cost.config.spring.cache.mapper.CostFactorMapper;
import com.nextuple.sourcing.cost.config.spring.cache.utils.TestUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CostFactorFeignServiceImplTest {

  @InjectMocks private CostFactorFeignServiceImpl costFactorFeignService;

  @InjectMocks private TestUtil testUtil;

  @Mock private CostFactorMapper mapper;

  @Mock private CostFactorFeignImpl costFactorFeign;

  @Test
  void get() {

    CostFactorCacheKey cacheKey = testUtil.getCostFactorCacheKey();
    CostFactorCacheValue cacheValue = testUtil.getCostFactorCacheValue();
    BaseResponse<CostFactorDto> response = testUtil.getCostFactorDto();

    Mockito.when(costFactorFeign.getCostFactorByOrgIdAndCostFactor(anyString(), anyString()))
        .thenReturn(response);
    Mockito.when(mapper.responseToCacheValue(any())).thenReturn(cacheValue);

    assertEquals(cacheValue, costFactorFeignService.get(cacheKey));
    assertFalse(costFactorFeignService.get(cacheKey).isUndefined());
    verify(mapper, times(2)).responseToCacheValue(any());
  }

  @Test
  void getForExceptionTest() {

    Mockito.when(mapper.responseToCacheValue(any()))
        .thenThrow(new RuntimeException("Error message"));
    var response = costFactorFeignService.get(testUtil.getCostFactorCacheKey());

    assertNotNull(response);
    assertNull(response.getCostFactor());
    verify(mapper, times(1)).responseToCacheValue(any());
  }
}
