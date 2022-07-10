package com.hbc.carrier.calendar.cache.spring.mapper;

import static org.junit.jupiter.api.Assertions.*;

import com.hbc.calendar.domain.CalendarDaysStatusInfo;
import com.hbc.carrier.calendar.cache.domain.CarrierServiceCalendarCacheValue;
import com.hbc.carrier.calendar.cache.spring.util.TestUtil;
import com.hbc.common.response.BaseResponse;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CarrierServiceCalendarMapperTest {

  @InjectMocks private CarrierServiceCalendarMapper mapper;

  @InjectMocks private TestUtil testUtil;

  @Test
  void requestToCacheKey() {
    assertNull(mapper.requestToCacheKey("request"));
  }

  @Test
  void cacheKeyToRequest() {
    assertNull(mapper.cacheKeyToRequest(testUtil.getCarrierServiceCalendarCacheKey()));
  }

  @Test
  void responseToCacheValue() {
    CarrierServiceCalendarCacheValue cacheValue = testUtil.getCarrierServiceCalendarCacheValue();

    BaseResponse<List<CalendarDaysStatusInfo>> response =
        testUtil.getBaseResponseOfListOfCalendarDaysStatusInfo();

    assertEquals(cacheValue, mapper.responseToCacheValue(response));
  }

  @Test
  void cacheValueToResponse() {
    assertNull(mapper.cacheValueToResponse(testUtil.getCarrierServiceCalendarCacheValue()));
  }
}
