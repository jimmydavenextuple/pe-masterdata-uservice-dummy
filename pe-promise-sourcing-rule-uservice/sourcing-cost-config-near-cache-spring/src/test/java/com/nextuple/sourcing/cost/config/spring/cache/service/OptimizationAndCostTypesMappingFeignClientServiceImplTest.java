/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.sourcing.cost.config.cache.domain.OptimizationAndCostTypesMappingKey;
import com.nextuple.sourcing.cost.config.cache.domain.OptimizationAndCostTypesMappingValue;
import com.nextuple.sourcing.cost.config.outbound.OptimizationAndCostTypesMappingResponse;
import com.nextuple.sourcing.cost.config.spring.cache.feign.OptimizationAndCostTypesMappingFeign;
import com.nextuple.sourcing.cost.config.spring.cache.mapper.OptimizationAndCostTypesMapper;
import com.nextuple.sourcing.cost.config.spring.cache.utils.TestUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OptimizationAndCostTypesMappingFeignClientServiceImplTest {
  @InjectMocks
  private OptimizationAndCostTypesMappingFeignClientServiceImpl
      optimizationAndCostTypesMappingFeignClientService;

  @InjectMocks private TestUtil testUtil;

  @Mock private OptimizationAndCostTypesMapper optimizationAndCostTypesMapper;

  @Mock private OptimizationAndCostTypesMappingFeign optimizationAndCostTypesMappingFeign;

  @Test
  @DisplayName("Get Feign Response: Happy Path")
  void get() {
    OptimizationAndCostTypesMappingKey cacheKey = testUtil.getOptimizationAndCostTypesMappingKey();
    OptimizationAndCostTypesMappingValue cacheValue =
        testUtil.getOptimizationAndCostTypesMappingValue();
    BaseResponse<OptimizationAndCostTypesMappingResponse> response =
        testUtil.getOptimizationAndCostTypesMappingResponse();

    Mockito.when(
            optimizationAndCostTypesMappingFeign
                .fetchOptimizationAndCostTypesMappingByOrgIdAndStrategy(
                    cacheKey.getOrgId(), cacheKey.getOptimizationStrategy()))
        .thenReturn(response);
    Mockito.when(optimizationAndCostTypesMapper.responseToCacheValue(response))
        .thenReturn(cacheValue);
    assertEquals(cacheValue, optimizationAndCostTypesMappingFeignClientService.get(cacheKey));
    assertFalse(optimizationAndCostTypesMappingFeignClientService.get(cacheKey).isUndefined());
    verify(optimizationAndCostTypesMapper, times(2)).responseToCacheValue(any());
  }

  @Test
  @DisplayName("Get Feign Response: Exception Path")
  void getForExceptionTest() {
    OptimizationAndCostTypesMappingKey optimizationAndCostTypesMappingKey =
        new OptimizationAndCostTypesMappingKey();

    Mockito.when(optimizationAndCostTypesMapper.responseToCacheValue(any()))
        .thenThrow(new RuntimeException("Error message"));
    var response =
        optimizationAndCostTypesMappingFeignClientService.get(optimizationAndCostTypesMappingKey);

    assertNotNull(response);
    assertNull(response.getOptimizationAndCostTypesMappingResponse());
    verify(optimizationAndCostTypesMapper, times(1)).responseToCacheValue(any());
  }
}
