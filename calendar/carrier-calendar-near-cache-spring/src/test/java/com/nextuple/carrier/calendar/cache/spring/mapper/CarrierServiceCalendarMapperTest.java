package com.nextuple.carrier.calendar.cache.spring.mapper;

import static org.junit.jupiter.api.Assertions.*;

import com.nextuple.calendar.domain.CalendarDaysStatusInfo;
import com.nextuple.carrier.calendar.cache.domain.CarrierServiceCalendarCacheValue;
import com.nextuple.carrier.calendar.cache.spring.util.TestUtil;
import com.nextuple.common.response.BaseResponse;
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
