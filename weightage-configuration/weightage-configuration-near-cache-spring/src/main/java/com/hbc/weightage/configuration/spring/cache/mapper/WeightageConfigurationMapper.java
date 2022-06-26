package com.hbc.weightage.configuration.spring.cache.mapper;

import com.hbc.common.response.BaseResponse;
import com.hbc.core.cache.mapper.GenericMapper;
import com.hbc.weightage.configuration.api.domain.inbound.FetchWeightageRequest;
import com.hbc.weightage.configuration.cache.domain.WeightageConfigurationCacheKey;
import com.hbc.weightage.configuration.cache.domain.WeightageConfigurationCacheValue;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class WeightageConfigurationMapper
    implements GenericMapper<
        WeightageConfigurationCacheKey,
        WeightageConfigurationCacheValue,
        FetchWeightageRequest,
        BaseResponse<Map<String, Float>>> {
  @Override
  public WeightageConfigurationCacheKey requestToCacheKey(FetchWeightageRequest request) {
    return null;
  }

  @Override
  public FetchWeightageRequest cacheKeyToRequest(WeightageConfigurationCacheKey cacheKey) {
    return cacheKey.getFetchWeightageRequest();
  }

  @Override
  public WeightageConfigurationCacheValue responseToCacheValue(
      BaseResponse<Map<String, Float>> response) {
    return WeightageConfigurationCacheValue.builder()
        .weightageConfigurationResponse(response.getPayload())
        .build();
  }

  @Override
  public BaseResponse<Map<String, Float>> cacheValueToResponse(
      WeightageConfigurationCacheValue cacheValue) {
    return null;
  }
}
