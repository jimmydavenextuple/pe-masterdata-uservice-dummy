/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.carrier.spring.cache.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nextuple.carrier.cache.domain.CarrierCacheKey;
import com.nextuple.carrier.cache.domain.CarrierCacheValue;
import com.nextuple.carrier.domain.outbound.CarrierServiceResponse;
import com.nextuple.carrier.spring.cache.feign.CarrierFeignImpl;
import com.nextuple.carrier.spring.cache.util.TestUtil;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.core.cache.mapper.GenericMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CarrierFeignClientServiceImplTest {

  @InjectMocks private CarrierFeignClientServiceImpl carrierFeignClientService;

  @Mock
  private GenericMapper<
          CarrierCacheKey, CarrierCacheValue, String, BaseResponse<CarrierServiceResponse>>
      mapper;

  @Mock private CarrierFeignImpl carrierFeign;

  @InjectMocks private TestUtil testUtil;

  @Test
  void getTest() {
    CarrierCacheKey cacheKey = testUtil.getCarrierCacheKey();
    CarrierCacheValue cacheValue = testUtil.getCarrierCacheValue();

    when(mapper.responseToCacheValue(any())).thenReturn(cacheValue);
    when(carrierFeign.getCarrier(any(), any(), any()))
        .thenReturn(testUtil.getBaseResponseOfCarrierResponse());

    assertEquals(cacheValue, carrierFeignClientService.get(cacheKey));
    assertFalse(carrierFeignClientService.get(cacheKey).isUndefined());
    verify(mapper, times(2)).responseToCacheValue(any());
  }

  @Test
  void getExceptionTest() {
    CarrierCacheKey invalidCacheKey = testUtil.getCarrierCacheKey();

    when(mapper.responseToCacheValue(any())).thenThrow(new RuntimeException("Error message"));
    assertNull(carrierFeignClientService.get(invalidCacheKey));
    verify(mapper, times(1)).responseToCacheValue(any());
  }
}
