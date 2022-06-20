package com.nextuple.weightage.configuration.spring.cache.util;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.weightage.configuration.cache.domain.FetchWeightageRequest;
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
