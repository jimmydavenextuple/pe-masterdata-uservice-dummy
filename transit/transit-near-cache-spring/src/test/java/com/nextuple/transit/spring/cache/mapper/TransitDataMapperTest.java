package com.nextuple.transit.spring.cache.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.transit.cache.domain.TransitCacheValue;
import com.nextuple.transit.domain.outbound.TransitResponse;
import com.nextuple.transit.spring.cache.util.TestUtil;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TransitDataMapperTest {

  @InjectMocks private TransitDataMapper mapper;

  @InjectMocks private TestUtil testUtil;

  @Test
  void requestToCacheKey() {
    assertNull(mapper.requestToCacheKey("request"));
  }

  @Test
  void cacheKeyToRequest() {
    assertNull(mapper.cacheKeyToRequest(testUtil.getTransitCacheKey()));
  }

  @Test
  void responseToCacheValue() {
    TransitCacheValue transitCacheValue = testUtil.getTransitCacheValue();

    BaseResponse<List<TransitResponse>> response = testUtil.getBaseResponseOfTransit();

    assertEquals(transitCacheValue, mapper.responseToCacheValue(response));
  }

  @Test
  void cacheValueToResponse() {
    assertNull(mapper.cacheValueToResponse(testUtil.getTransitCacheValue()));
  }
}
