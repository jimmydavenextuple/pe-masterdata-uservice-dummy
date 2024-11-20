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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.sourcing.cost.config.cache.domain.CostValueCacheKey;
import com.nextuple.sourcing.cost.config.cache.domain.CostValueCacheValue;
import com.nextuple.sourcing.cost.config.outbound.CostValueResponse;
import com.nextuple.sourcing.cost.config.spring.cache.feign.CostValueFeignImpl;
import com.nextuple.sourcing.cost.config.spring.cache.mapper.CostValueMapper;
import com.nextuple.sourcing.cost.config.spring.cache.utils.TestUtil;
import feign.Request;
import feign.Request.HttpMethod;
import feign.RetryableException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CostValueFeignServiceImplTest {

  @InjectMocks private CostValueFeignServiceImpl costValueFeignService;

  @InjectMocks private TestUtil testUtil;

  @Mock private CostValueMapper mapper;

  @Mock private CostValueFeignImpl costValueFeign;

  @Test
  void get() {

    CostValueCacheKey cacheKey = testUtil.getCostValueCacheKey();
    CostValueCacheValue cacheValue = testUtil.getCostValueCacheValue();
    BaseResponse<CostValueResponse> response = testUtil.getCostValueResponse();

    Mockito.when(
            costValueFeign.getCostValueForCostFactorCombinationKey(
                anyString(), anyString(), anyString()))
        .thenReturn(response);
    Mockito.when(mapper.responseToCacheValue(any())).thenReturn(cacheValue);

    assertEquals(cacheValue, costValueFeignService.get(cacheKey));
    assertFalse(costValueFeignService.get(cacheKey).isUndefined());
    verify(mapper, times(2)).responseToCacheValue(any());
  }

  @Test
  void getForExceptionTest() {
    CostValueCacheKey cacheKey = testUtil.getCostValueCacheKey();
    Mockito.when(
            costValueFeign.getCostValueForCostFactorCombinationKey(
                anyString(), anyString(), anyString()))
        .thenThrow(
            new RetryableException(400, "Timeout", HttpMethod.GET, null, mock(Request.class)));
    var response = costValueFeignService.get(cacheKey);

    assertNotNull(response);
    assertNotNull(response.getMasterDataException());
    assertNull(response.getCostValue());
    verify(mapper, times(0)).responseToCacheValue(any());
  }
}
