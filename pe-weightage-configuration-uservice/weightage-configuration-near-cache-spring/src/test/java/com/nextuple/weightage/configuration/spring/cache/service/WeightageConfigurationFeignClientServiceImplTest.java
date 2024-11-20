/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.weightage.configuration.spring.cache.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.core.cache.mapper.GenericMapper;
import com.nextuple.weightage.configuration.api.domain.inbound.FetchWeightageRequest;
import com.nextuple.weightage.configuration.cache.domain.WeightageConfigurationCacheKey;
import com.nextuple.weightage.configuration.cache.domain.WeightageConfigurationCacheValue;
import com.nextuple.weightage.configuration.spring.cache.feign.WeightageConfigurationFeignImpl;
import com.nextuple.weightage.configuration.spring.cache.util.TestUtil;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class WeightageConfigurationFeignClientServiceImplTest {

  @InjectMocks
  private WeightageConfigurationFeignClientServiceImpl weightageConfigurationFeignClientService;

  @InjectMocks private TestUtil testUtil;

  @Mock
  private GenericMapper<
          WeightageConfigurationCacheKey,
          WeightageConfigurationCacheValue,
          FetchWeightageRequest,
          BaseResponse<Map<String, Float>>>
      mapper;

  @Mock private WeightageConfigurationFeignImpl weightageConfigurationFeign;

  @Test
  void get() {
    WeightageConfigurationCacheKey cacheKey = testUtil.getWeightageConfigurationCacheKey();
    WeightageConfigurationCacheValue cacheValue = testUtil.getWeightageConfigurationCacheValue();
    BaseResponse<Map<String, Float>> response = testUtil.getBaseResponseForFetchWeightageRequest();

    Mockito.when(mapper.cacheKeyToRequest(cacheKey))
        .thenReturn(cacheKey.getFetchWeightageRequest());
    Mockito.when(weightageConfigurationFeign.get(cacheKey.getFetchWeightageRequest()))
        .thenReturn(response);
    Mockito.when(mapper.responseToCacheValue(response)).thenReturn(cacheValue);

    assertEquals(cacheValue, weightageConfigurationFeignClientService.get(cacheKey));
    assertFalse(weightageConfigurationFeignClientService.get(cacheKey).isUndefined());
    verify(mapper, times(2)).responseToCacheValue(any());
  }

  @Test
  void getForExceptionTest() {
    WeightageConfigurationCacheKey invalidCacheKey = testUtil.getWeightageConfigurationCacheKey();

    Mockito.when(mapper.responseToCacheValue(any()))
        .thenThrow(new RuntimeException("Error message"));
    assertNull(weightageConfigurationFeignClientService.get(invalidCacheKey));
    verify(mapper, times(1)).responseToCacheValue(any());
  }
}
