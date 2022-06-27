package com.hbc.weightage.configuration.spring.cache.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.hbc.common.response.BaseResponse;
import com.hbc.core.cache.mapper.GenericMapper;
import com.hbc.weightage.configuration.api.domain.inbound.FetchWeightageRequest;
import com.hbc.weightage.configuration.cache.domain.WeightageConfigurationCacheKey;
import com.hbc.weightage.configuration.cache.domain.WeightageConfigurationCacheValue;
import com.hbc.weightage.configuration.spring.cache.feign.WeightageConfigurationFeignImpl;
import com.hbc.weightage.configuration.spring.cache.util.TestUtil;
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
    verify(mapper, times(1)).responseToCacheValue(any());
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
