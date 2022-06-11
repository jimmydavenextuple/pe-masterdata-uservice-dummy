package com.nextuple.carrier.spring.cache.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.nextuple.carrier.cache.domain.CarrierCacheValue;
import com.nextuple.carrier.cache.domain.CarrierServiceResponse;
import com.nextuple.carrier.spring.cache.util.TestUtil;
import com.nextuple.controltower.common.base.BaseResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CarrierMapperTest {

  @InjectMocks private CarrierMapper mapper;

  @InjectMocks private TestUtil testUtil;

  @Test
  void requestToCacheKey() {
    assertNull(mapper.requestToCacheKey("request"));
  }

  @Test
  void cacheKeyToRequest() {
    assertNull(mapper.cacheKeyToRequest(testUtil.getCarrierCacheKey()));
  }

  @Test
  void responseToCacheValue() {
    CarrierCacheValue cacheValue = testUtil.getCarrierCacheValue();

    BaseResponse<CarrierServiceResponse> response = testUtil.getBaseResponseOfCarrierResponse();

    assertEquals(cacheValue, mapper.responseToCacheValue(response));
  }

  @Test
  void cacheValueToResponse() {
    assertNull(mapper.cacheValueToResponse(testUtil.getCarrierCacheValue()));
  }
}
