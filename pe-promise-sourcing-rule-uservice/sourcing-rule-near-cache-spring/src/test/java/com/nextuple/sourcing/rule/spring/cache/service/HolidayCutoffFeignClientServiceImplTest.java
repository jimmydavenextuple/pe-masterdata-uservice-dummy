/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */
package com.nextuple.sourcing.rule.spring.cache.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.HolidayCutoffRulesResponse;
import com.nextuple.sourcing.rule.cache.domain.HolidayCutoffKey;
import com.nextuple.sourcing.rule.cache.domain.HolidayCutoffValue;
import com.nextuple.sourcing.rule.spring.cache.feign.HolidayCutoffFeignImpl;
import com.nextuple.sourcing.rule.spring.cache.mapper.HolidayCutoffMapper;
import com.nextuple.sourcing.rule.spring.cache.util.TestUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class HolidayCutoffFeignClientServiceImplTest {
  @InjectMocks private HolidayCutoffFeignClientServiceImpl holidayCutoffFeignClientService;

  @InjectMocks private TestUtil testUtil;

  @Mock private HolidayCutoffMapper holidayCutoffMapper;

  @Mock private HolidayCutoffFeignImpl holidayCutoffFeign;

  @Test
  void getCacheValueTest() {

    HolidayCutoffKey cacheKey = testUtil.getHolidayCutoffKey();
    HolidayCutoffValue cacheValue = testUtil.getHolidayCutoffValue();
    BaseResponse<HolidayCutoffRulesResponse> response = testUtil.getHolidayCutoffRulesResponse();

    Mockito.when(
            holidayCutoffFeign.fetchHolidayCutoffRules(cacheKey.getHolidayCutoffRulesRequest()))
        .thenReturn(response);
    Mockito.when(holidayCutoffMapper.responseToCacheValue(response)).thenReturn(cacheValue);

    assertEquals(cacheValue, holidayCutoffFeignClientService.get(cacheKey));
    assertFalse(holidayCutoffFeignClientService.get(cacheKey).isUndefined());
    verify(holidayCutoffMapper, times(2)).responseToCacheValue(any());
  }

  @Test
  void getCacheValueExceptionTest() {
    HolidayCutoffKey cacheKey = testUtil.getHolidayCutoffKey();

    Mockito.when(holidayCutoffMapper.responseToCacheValue(any()))
        .thenThrow(new RuntimeException("Error message"));
    var response = holidayCutoffFeignClientService.get(cacheKey);

    assertNotNull(response);
    assertNull(response.getHolidayCutoffRulesResponse());
    verify(holidayCutoffMapper, times(1)).responseToCacheValue(any());
  }
}
