package com.hbc.common.config.spring.cache.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.hbc.common.config.spring.cache.util.TestUtil;
import com.hbc.common.configuration.api.domain.dto.CommonConfigurationDto;
import com.hbc.common.configuration.cache.domain.CommonConfigCacheValue;
import com.hbc.common.response.BaseResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CommonConfigMapperTest {
  @InjectMocks private CommonConfigMapper mapper;

  @InjectMocks private TestUtil testUtil;

  @Test
  void requestToCacheKey() {
    assertNull(mapper.requestToCacheKey("request"));
  }

  @Test
  void cacheKeyToRequest() {
    assertNull(mapper.cacheKeyToRequest(testUtil.getCommonConfigCacheKey()));
  }

  @Test
  void responseToCacheValue() {
    CommonConfigCacheValue commonConfigCacheValue = testUtil.getCommonConfigCacheValue();

    BaseResponse<CommonConfigurationDto> response = testUtil.getBaseResponseOfCommonConfiguration();

    assertEquals(commonConfigCacheValue, mapper.responseToCacheValue(response));
  }

  @Test
  void cacheValueToResponse() {
    assertNull(mapper.cacheValueToResponse(testUtil.getCommonConfigCacheValue()));
  }
}
