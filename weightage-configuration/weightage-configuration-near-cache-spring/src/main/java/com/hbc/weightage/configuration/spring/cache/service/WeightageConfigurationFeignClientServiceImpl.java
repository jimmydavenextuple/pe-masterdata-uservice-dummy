package com.hbc.weightage.configuration.spring.cache.service;

import com.hbc.common.response.BaseResponse;
import com.hbc.core.cache.mapper.GenericMapper;
import com.hbc.core.spring.service.AbstractGenericFeignClientServiceImpl;
import com.hbc.weightage.configuration.api.domain.inbound.FetchWeightageRequest;
import com.hbc.weightage.configuration.cache.domain.WeightageConfigurationCacheKey;
import com.hbc.weightage.configuration.cache.domain.WeightageConfigurationCacheValue;
import com.hbc.weightage.configuration.spring.cache.feign.WeightageConfigurationFeignImpl;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WeightageConfigurationFeignClientServiceImpl
    extends AbstractGenericFeignClientServiceImpl<
        WeightageConfigurationCacheKey,
        WeightageConfigurationCacheValue,
        FetchWeightageRequest,
        BaseResponse<Map<String, Float>>> {
  @Autowired WeightageConfigurationFeignImpl weightageConfigurationFeign;

  @Autowired
  GenericMapper<
          WeightageConfigurationCacheKey,
          WeightageConfigurationCacheValue,
          FetchWeightageRequest,
          BaseResponse<Map<String, Float>>>
      weightageConfigurationMapper;

  @Override
  public WeightageConfigurationCacheValue get(WeightageConfigurationCacheKey key) {
    try {
      return weightageConfigurationMapper.responseToCacheValue(
          weightageConfigurationFeign.get(weightageConfigurationMapper.cacheKeyToRequest(key)));
    } catch (RuntimeException ex) {
      return null;
    }
  }
}
