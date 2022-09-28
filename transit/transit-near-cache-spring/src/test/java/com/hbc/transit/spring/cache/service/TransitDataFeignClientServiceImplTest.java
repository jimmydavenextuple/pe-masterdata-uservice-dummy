package com.hbc.transit.spring.cache.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hbc.common.response.BaseResponse;
import com.hbc.core.cache.mapper.GenericMapper;
import com.hbc.transit.cache.domain.TransitCacheKey;
import com.hbc.transit.cache.domain.TransitCacheValue;
import com.hbc.transit.domain.outbound.TransitResponse;
import com.hbc.transit.spring.cache.feign.TransitDataFeignImpl;
import com.hbc.transit.spring.cache.util.TestUtil;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TransitDataFeignClientServiceImplTest {

  @InjectMocks private TransitDataFeignClientServiceImpl transitDataFeignClientServiceImpl;

  @InjectMocks private TestUtil testUtil;

  @Mock
  private GenericMapper<
          TransitCacheKey, TransitCacheValue, String, BaseResponse<List<TransitResponse>>>
      mapper;

  @Mock private TransitDataFeignImpl transitDataFeign;

  @Test
  void getTest() {
    TransitCacheKey cacheKey = testUtil.getTransitCacheKey();
    TransitCacheValue cacheValue = testUtil.getTransitCacheValue();

    when(mapper.responseToCacheValue(any())).thenReturn(cacheValue);
    when(transitDataFeign.getTransitDetailsListForDestinationGeoZone(any(), any()))
        .thenReturn(testUtil.getBaseResponseOfTransit());

    assertEquals(cacheValue, transitDataFeignClientServiceImpl.get(cacheKey));
    assertFalse(transitDataFeignClientServiceImpl.get(cacheKey).isUndefined());
    verify(mapper, times(2)).responseToCacheValue(any());
  }

  @Test
  void getExceptionTest() {
    TransitCacheKey invalidCacheKey = testUtil.getTransitCacheKey();

    when(mapper.responseToCacheValue(any())).thenThrow(new RuntimeException("Error message"));
    assertNull(transitDataFeignClientServiceImpl.get(invalidCacheKey));
    verify(mapper, times(1)).responseToCacheValue(any());
  }
}
