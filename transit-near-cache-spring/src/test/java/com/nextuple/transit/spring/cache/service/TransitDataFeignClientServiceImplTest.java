/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.transit.spring.cache.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.core.cache.mapper.GenericMapper;
import com.nextuple.transit.cache.domain.TransitCacheKey;
import com.nextuple.transit.cache.domain.TransitCacheValue;
import com.nextuple.transit.domain.outbound.TransitResponse;
import com.nextuple.transit.spring.cache.feign.TransitDataFeignImpl;
import com.nextuple.transit.spring.cache.util.TestUtil;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TransitDataFeignClientServiceImplTest {

  @InjectMocks private TransitDataFeignClientServiceImpl transitDataFeignClientServiceImpl;

  @InjectMocks private TestUtil testUtil;

  @Mock
  private GenericMapper<
          TransitCacheKey, TransitCacheValue, String, BaseResponse<List<TransitResponse>>>
      mapper;

  @Mock private TransitDataFeignImpl transitDataFeign;

  @Test
  void getTest() {
    TransitCacheKey cacheKey = testUtil.getTransitCacheKey();
    TransitCacheValue cacheValue = testUtil.getTransitCacheValue();

    when(mapper.responseToCacheValue(any())).thenReturn(cacheValue);
    when(transitDataFeign.getListOfTransitDetailsForDestinationGeoZone(any(), any()))
        .thenReturn(testUtil.getBaseResponseOfTransit());

    assertEquals(cacheValue, transitDataFeignClientServiceImpl.get(cacheKey));
    assertFalse(transitDataFeignClientServiceImpl.get(cacheKey).isUndefined());
    verify(mapper, times(2)).responseToCacheValue(any());
  }

  @Test
  void getExceptionTest() {
    TransitCacheKey invalidCacheKey = testUtil.getTransitCacheKey();

    when(mapper.responseToCacheValue(any())).thenThrow(new RuntimeException("Error message"));
    assertNull(transitDataFeignClientServiceImpl.get(invalidCacheKey));
    verify(mapper, times(1)).responseToCacheValue(any());
  }
}
