package com.nextuple.weightage.configuration.spring.cache.service;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.core.cache.mapper.GenericMapper;
import com.nextuple.core.spring.service.AbstractGenericFeignClientServiceImpl;
import com.nextuple.weightage.configuration.cache.domain.FetchWeightageRequest;
import com.nextuple.weightage.configuration.cache.domain.WeightageConfigurationCacheKey;
import com.nextuple.weightage.configuration.cache.domain.WeightageConfigurationCacheValue;
import com.nextuple.weightage.configuration.spring.cache.feign.WeightageConfigurationFeignImpl;
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
