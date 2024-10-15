/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.weightage.configuration.spring.cache.util;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.weightage.configuration.api.domain.inbound.FetchWeightageRequest;
import com.nextuple.weightage.configuration.cache.domain.WeightageConfigurationCacheKey;
import com.nextuple.weightage.configuration.cache.domain.WeightageConfigurationCacheValue;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class TestUtil {
  public Map<String, Float> getWeightageConfigurationResponse(String key, Float value) {
    Map<String, Float> weightageConfigurationResponse = new HashMap<>();
    weightageConfigurationResponse.put(key, value);
    return weightageConfigurationResponse;
  }

  public FetchWeightageRequest getFetchWeightageRequest() {
    return FetchWeightageRequest.builder()
        .orgId("ABC")
        .type("PRIORITY")
        .keys(Collections.singletonList("P1"))
        .build();
  }

  public WeightageConfigurationCacheKey getWeightageConfigurationCacheKey() {
    return WeightageConfigurationCacheKey.builder()
        .fetchWeightageRequest(getFetchWeightageRequest())
        .build();
  }

  public WeightageConfigurationCacheValue getWeightageConfigurationCacheValue() {
    return WeightageConfigurationCacheValue.builder()
        .weightageConfigurationResponse(getWeightageConfigurationResponse("P1", 100F))
        .build();
  }

  public BaseResponse<Map<String, Float>> getBaseResponseForFetchWeightageRequest() {
    BaseResponse<Map<String, Float>> response = new BaseResponse<>();
    response.setPayload(getWeightageConfigurationResponse("P1", 100F));
    return response;
  }
}
