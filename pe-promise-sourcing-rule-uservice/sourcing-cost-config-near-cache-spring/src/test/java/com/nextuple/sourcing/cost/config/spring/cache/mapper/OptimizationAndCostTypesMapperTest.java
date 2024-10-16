/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.sourcing.cost.config.spring.cache.mapper;

import static org.junit.jupiter.api.Assertions.assertNull;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.sourcing.cost.config.cache.domain.OptimizationAndCostTypesMappingKey;
import com.nextuple.sourcing.cost.config.cache.domain.OptimizationAndCostTypesMappingValue;
import com.nextuple.sourcing.cost.config.outbound.OptimizationAndCostTypesMappingResponse;
import com.nextuple.sourcing.cost.config.spring.cache.utils.TestUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OptimizationAndCostTypesMapperTest {
  @InjectMocks private OptimizationAndCostTypesMapper optimizationAndCostTypesMapper;

  @InjectMocks private TestUtil testUtil;

  @Test
  @DisplayName("Optimization And Cost Types Mapper: Request to cache key")
  void requestToCacheKey() {
    assertNull(optimizationAndCostTypesMapper.requestToCacheKey("request"));
  }

  @Test
  @DisplayName("Optimization And Cost Types Mapper: Cache key to request")
  void cacheKeyToRequest() {
    assertNull(
        optimizationAndCostTypesMapper.cacheKeyToRequest(new OptimizationAndCostTypesMappingKey()));
  }

  @Test
  @DisplayName("Optimization And Cost Types Mapper: Response to cache value")
  void responseToCacheValue() {
    OptimizationAndCostTypesMappingValue optimizationAndCostTypesMappingValue =
        testUtil.getOptimizationAndCostTypesMappingValue();
    BaseResponse<OptimizationAndCostTypesMappingResponse> response =
        testUtil.getOptimizationAndCostTypesMappingResponse();

    Assertions.assertEquals(
        optimizationAndCostTypesMappingValue,
        optimizationAndCostTypesMapper.responseToCacheValue(response));
  }

  @Test
  @DisplayName("Optimization And Cost Types Mapper: Cache value to response")
  void cacheValueToResponse() {
    OptimizationAndCostTypesMappingValue optimizationAndCostTypesMappingValue =
        testUtil.getOptimizationAndCostTypesMappingValue();
    assertNull(
        optimizationAndCostTypesMapper.cacheValueToResponse(optimizationAndCostTypesMappingValue));
  }
}
