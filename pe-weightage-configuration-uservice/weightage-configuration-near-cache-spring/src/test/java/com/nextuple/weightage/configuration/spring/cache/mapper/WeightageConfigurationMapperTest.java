/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.weightage.configuration.spring.cache.mapper;

import static org.junit.jupiter.api.Assertions.*;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.core.cache.mapper.GenericMapper;
import com.nextuple.weightage.configuration.api.domain.inbound.FetchWeightageRequest;
import com.nextuple.weightage.configuration.cache.domain.WeightageConfigurationCacheKey;
import com.nextuple.weightage.configuration.cache.domain.WeightageConfigurationCacheValue;
import com.nextuple.weightage.configuration.spring.cache.util.TestUtil;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class WeightageConfigurationMapperTest {

  @InjectMocks
  private GenericMapper<
          WeightageConfigurationCacheKey,
          WeightageConfigurationCacheValue,
          FetchWeightageRequest,
          BaseResponse<Map<String, Float>>>
      genericMapper = new WeightageConfigurationMapper();

  @InjectMocks private TestUtil testUtil;

  @Test
  void requestToCacheKey() {
    assertNull(genericMapper.requestToCacheKey(new FetchWeightageRequest()));
  }

  @Test
  void cacheKeyToRequest() {
    WeightageConfigurationCacheKey cacheKey = testUtil.getWeightageConfigurationCacheKey();
    assertEquals(cacheKey.getFetchWeightageRequest(), genericMapper.cacheKeyToRequest(cacheKey));
  }

  @Test
  void responseToCacheValue() {
    WeightageConfigurationCacheValue cacheValue = testUtil.getWeightageConfigurationCacheValue();
    BaseResponse<Map<String, Float>> response = testUtil.getBaseResponseForFetchWeightageRequest();

    assertEquals(cacheValue, genericMapper.responseToCacheValue(response));
  }

  @Test
  void cacheValueToResponse() {
    WeightageConfigurationCacheValue cacheValue = testUtil.getWeightageConfigurationCacheValue();
    assertNull(genericMapper.cacheValueToResponse(cacheValue));
  }
}
