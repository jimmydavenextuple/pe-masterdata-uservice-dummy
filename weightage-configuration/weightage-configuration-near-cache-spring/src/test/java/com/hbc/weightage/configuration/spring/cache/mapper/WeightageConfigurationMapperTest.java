package com.hbc.weightage.configuration.spring.cache.mapper;

import static org.junit.jupiter.api.Assertions.*;

import com.hbc.common.response.BaseResponse;
import com.hbc.core.cache.mapper.GenericMapper;
import com.hbc.weightage.configuration.api.domain.inbound.FetchWeightageRequest;
import com.hbc.weightage.configuration.cache.domain.WeightageConfigurationCacheKey;
import com.hbc.weightage.configuration.cache.domain.WeightageConfigurationCacheValue;
import com.hbc.weightage.configuration.spring.cache.util.TestUtil;
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
