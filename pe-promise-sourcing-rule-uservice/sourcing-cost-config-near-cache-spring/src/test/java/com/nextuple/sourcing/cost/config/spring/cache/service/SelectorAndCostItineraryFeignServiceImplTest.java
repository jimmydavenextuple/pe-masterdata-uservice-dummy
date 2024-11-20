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
import com.nextuple.sourcing.cost.config.cache.domain.SelectorAndCostItineraryCacheKey;
import com.nextuple.sourcing.cost.config.cache.domain.SelectorAndCostItineraryCacheValue;
import com.nextuple.sourcing.cost.config.outbound.SelectorAndCostItineraryMappingResponse;
import com.nextuple.sourcing.cost.config.spring.cache.feign.SelectorAndCostItineraryFeignImpl;
import com.nextuple.sourcing.cost.config.spring.cache.mapper.SelectorAndCostItineraryMapper;
import com.nextuple.sourcing.cost.config.spring.cache.utils.TestUtil;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SelectorAndCostItineraryFeignServiceImplTest {

  @InjectMocks
  private SelectorAndCostItineraryFeignServiceImpl selectorAndCostItineraryFeignServiceImpl;

  @InjectMocks private TestUtil testUtil;

  @Mock private SelectorAndCostItineraryMapper mapper;

  @Mock private SelectorAndCostItineraryFeignImpl selectorAndCostItineraryFeign;

  @Test
  void get() {

    SelectorAndCostItineraryCacheKey cacheKey = testUtil.getSelectorAndCostItineraryCacheKey();
    SelectorAndCostItineraryCacheValue cacheValue =
        testUtil.getSelectorAndCostItineraryCacheValue();
    BaseResponse<List<SelectorAndCostItineraryMappingResponse>> response =
        testUtil.getSelectorAndCostItineraryMappingResponse();

    Mockito.when(
            selectorAndCostItineraryFeign
                .getSelectorAndCostItineraryMappingByOrgIdSelectorCfAndCostType(
                    anyString(), anyString(), anyString()))
        .thenReturn(response);
    Mockito.when(mapper.responseToCacheValue(any())).thenReturn(cacheValue);

    assertEquals(cacheValue, selectorAndCostItineraryFeignServiceImpl.get(cacheKey));
    assertFalse(selectorAndCostItineraryFeignServiceImpl.get(cacheKey).isUndefined());
    verify(mapper, times(2)).responseToCacheValue(any());
  }

  @Test
  void getForExceptionTest() {

    Mockito.when(mapper.responseToCacheValue(any()))
        .thenThrow(new RuntimeException("Error message"));
    var response =
        selectorAndCostItineraryFeignServiceImpl.get(
            testUtil.getSelectorAndCostItineraryCacheKey());

    assertNotNull(response);
    assertNull(response.getSelectorAndCostItineraryMappingResponses());
    verify(mapper, times(1)).responseToCacheValue(any());
  }
}
