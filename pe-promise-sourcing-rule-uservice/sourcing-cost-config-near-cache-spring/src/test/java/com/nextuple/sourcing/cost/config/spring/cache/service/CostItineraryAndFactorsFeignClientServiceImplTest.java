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
import com.nextuple.sourcing.cost.config.cache.domain.CostItineraryAndFactorsCacheKey;
import com.nextuple.sourcing.cost.config.cache.domain.CostItineraryAndFactorsCacheValue;
import com.nextuple.sourcing.cost.config.dto.CostItineraryAndFactorsDto;
import com.nextuple.sourcing.cost.config.spring.cache.feign.CostItineraryAndFactorsDtoFeignImpl;
import com.nextuple.sourcing.cost.config.spring.cache.mapper.CostItineraryAndFactorsMapper;
import com.nextuple.sourcing.cost.config.spring.cache.utils.TestUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CostItineraryAndFactorsFeignClientServiceImplTest {

  @InjectMocks
  private CostItineraryAndFactorsFeignClientServiceImpl
      tostItineraryAndFactorsFeignClientServiceImpl;

  @InjectMocks private TestUtil testUtil;

  @Mock private CostItineraryAndFactorsMapper mapper;

  @Mock private CostItineraryAndFactorsDtoFeignImpl costItineraryAndFactorsDtoFeign;

  @Test
  void get() {

    CostItineraryAndFactorsCacheKey cacheKey = testUtil.getCostItineraryAndFactorsCacheKey();
    CostItineraryAndFactorsCacheValue cacheValue = testUtil.getCostItineraryAndFactorsCacheValue();
    BaseResponse<CostItineraryAndFactorsDto> response =
        testUtil.getCostItineraryAndFactorsResponse();

    Mockito.when(
            costItineraryAndFactorsDtoFeign
                .getCostItineraryAndFactorsByOrgIdCostItineraryItineraryStatusAndIsActive(
                    anyString(), anyString(), any(), any()))
        .thenReturn(response);
    Mockito.when(mapper.responseToCacheValue(any())).thenReturn(cacheValue);

    assertEquals(cacheValue, tostItineraryAndFactorsFeignClientServiceImpl.get(cacheKey));
    assertFalse(tostItineraryAndFactorsFeignClientServiceImpl.get(cacheKey).isUndefined());
    verify(mapper, times(2)).responseToCacheValue(any());
  }

  @Test
  void getForExceptionTest() {

    Mockito.when(mapper.responseToCacheValue(any()))
        .thenThrow(new RuntimeException("Error message"));
    var response =
        tostItineraryAndFactorsFeignClientServiceImpl.get(
            testUtil.getCostItineraryAndFactorsCacheKey());

    assertNotNull(response);
    assertNull(response.getCostFactors());
    verify(mapper, times(1)).responseToCacheValue(any());
  }
}
