/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.postal.code.timezone.cache.spring.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.postal.code.timezone.api.domain.outbound.CustomRegionResponse;
import com.nextuple.postal.code.timezone.cache.domain.CustomRegionCacheKey;
import com.nextuple.postal.code.timezone.cache.domain.CustomRegionCacheValue;
import com.nextuple.postal.code.timezone.cache.spring.feign.CustomRegionFeignImpl;
import com.nextuple.postal.code.timezone.cache.spring.mapper.CustomRegionMapper;
import com.nextuple.postal.code.timezone.cache.spring.util.TestUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CustomRegionFeignClientServiceImplTest {

  @InjectMocks private CustomRegionFeignClientServiceImpl customRegionFeignClientService;

  @InjectMocks private TestUtil testUtil;

  @Mock private CustomRegionMapper mapper;

  @Mock private CustomRegionFeignImpl customRegionFeign;

  @Test
  void getTest() {
    CustomRegionCacheKey cacheKey = testUtil.getCustomRegionCacheKey();
    CustomRegionCacheValue cacheValue = testUtil.getCustomRegionCacheValue();
    BaseResponse<CustomRegionResponse> response = testUtil.getCustomRegionBaseResponse();

    when(customRegionFeign.fetchCustomRegionIdByPostalCode(any(), any())).thenReturn(response);
    when(mapper.responseToCacheValue(response)).thenReturn(cacheValue);

    assertEquals(cacheValue, customRegionFeignClientService.get(cacheKey));
    assertFalse(customRegionFeignClientService.get(cacheKey).isUndefined());
    verify(mapper, times(2)).responseToCacheValue(any());
  }

  @Test
  void getExceptionTest() {
    CustomRegionCacheKey invalidCacheKey = testUtil.getCustomRegionCacheKey();

    when(mapper.responseToCacheValue(any())).thenThrow(new RuntimeException("Error message"));
    assertNull(customRegionFeignClientService.get(invalidCacheKey));
    verify(mapper, times(1)).responseToCacheValue(any());
  }
}
