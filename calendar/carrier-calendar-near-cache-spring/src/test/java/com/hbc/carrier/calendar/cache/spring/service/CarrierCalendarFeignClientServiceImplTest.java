package com.hbc.carrier.calendar.cache.spring.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.hbc.calendar.common.CalendarCommonFeignImpl;
import com.hbc.calendar.domain.CalendarDaysStatusInfo;
import com.hbc.carrier.calendar.cache.domain.CarrierServiceCalendarCacheKey;
import com.hbc.carrier.calendar.cache.domain.CarrierServiceCalendarCacheValue;
import com.hbc.carrier.calendar.cache.spring.util.TestUtil;
import com.hbc.common.response.BaseResponse;
import com.hbc.core.cache.mapper.GenericMapper;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CarrierCalendarFeignClientServiceImplTest {

  @InjectMocks
  private CarrierServiceCalendarFeignClientServiceImpl carrierServiceCalendarFeignClientService;

  @InjectMocks private TestUtil testUtil;

  @Mock
  private GenericMapper<
          CarrierServiceCalendarCacheKey,
          CarrierServiceCalendarCacheValue,
          String,
          BaseResponse<List<CalendarDaysStatusInfo>>>
      mapper;

  @Mock private CalendarCommonFeignImpl calendarCommonFeign;

  @Test
  void getTest() {
    CarrierServiceCalendarCacheKey cacheKey = testUtil.getCarrierServiceCalendarCacheKey();
    CarrierServiceCalendarCacheValue cacheValue = testUtil.getCarrierServiceCalendarCacheValue();

    when(mapper.responseToCacheValue(any())).thenReturn(cacheValue);
    when(calendarCommonFeign.getCarrierServiceCalendar(any(), any(), any()))
        .thenReturn(testUtil.getBaseResponseOfListOfCalendarDaysStatusInfo());

    assertEquals(cacheValue, carrierServiceCalendarFeignClientService.get(cacheKey));
    verify(mapper, times(1)).responseToCacheValue(any());
  }

  @Test
  void getExceptionTest() {
    CarrierServiceCalendarCacheKey invalidCacheKey = testUtil.getCarrierServiceCalendarCacheKey();

    when(mapper.responseToCacheValue(any())).thenThrow(new RuntimeException("Error message"));
    assertNull(carrierServiceCalendarFeignClientService.get(invalidCacheKey));
    verify(mapper, times(1)).responseToCacheValue(any());
  }
}
