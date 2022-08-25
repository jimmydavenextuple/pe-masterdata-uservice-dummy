package com.hbc.common.config.spring.cache.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hbc.common.config.spring.cache.feign.CommonConfigFeignImpl;
import com.hbc.common.config.spring.cache.util.TestUtil;
import com.hbc.common.configuration.api.domain.dto.CommonConfigurationDto;
import com.hbc.common.configuration.cache.domain.CommonConfigCacheKey;
import com.hbc.common.configuration.cache.domain.CommonConfigCacheValue;
import com.hbc.common.response.BaseResponse;
import com.hbc.core.cache.mapper.GenericMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CommonConfigFeignClientServiceImplTest {

  @InjectMocks private CommonConfigFeignClientServiceImpl commonConfigFeignClientService;

  @InjectMocks private TestUtil testUtil;

  @Mock
  private GenericMapper<
          CommonConfigCacheKey,
          CommonConfigCacheValue,
          String,
          BaseResponse<CommonConfigurationDto>>
      mapper;

  @Mock private CommonConfigFeignImpl commonConfigFeign;

  @Test
  void getTest() {
    CommonConfigCacheKey cacheKey = testUtil.getCommonConfigCacheKey();
    CommonConfigCacheValue cacheValue = testUtil.getCommonConfigCacheValue();

    when(mapper.responseToCacheValue(any())).thenReturn(cacheValue);
    when(commonConfigFeign.fetchValue(any(), any(), any()))
        .thenReturn(testUtil.getBaseResponseOfCommonConfiguration());

    assertEquals(cacheValue, commonConfigFeignClientService.get(cacheKey));
    assertFalse(commonConfigFeignClientService.get(cacheKey).isDummy());

    verify(mapper, times(2)).responseToCacheValue(any());
  }

  @Test
  void getTest2() {
    CommonConfigCacheKey cacheKey = testUtil.getCommonConfigCacheKey();
    BaseResponse<CommonConfigurationDto> response = testUtil.getBaseResponseOfCommonConfiguration();
    response.setPayload(null);
    when(commonConfigFeign.fetchValue(any(), any(), any())).thenReturn(response);

    assertNull(commonConfigFeignClientService.get(cacheKey).getCommonConfigDetails());
    assertTrue(commonConfigFeignClientService.get(cacheKey).isDummy());

    verify(mapper, times(0)).responseToCacheValue(any());
  }

  @Test
  void getExceptionTest() {
    CommonConfigCacheKey invalidCacheKey = testUtil.getCommonConfigCacheKey();

    assertNull(commonConfigFeignClientService.get(invalidCacheKey));

    verify(mapper, times(0)).responseToCacheValue(any());
  }
}
