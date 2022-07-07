package com.hbc.carriercalendar.spring.cache.service;

import com.hbc.calendar.common.CalendarCommonFeignImpl;
import com.hbc.calendar.domain.CalendarDaysStatusInfo;
import com.hbc.carrier.calendar.cache.domain.CarrierServiceCalendarCacheKey;
import com.hbc.carrier.calendar.cache.domain.CarrierServiceCalendarCacheValue;
import com.hbc.carrier.calendar.cache.spring.service.CarrierServiceCalendarFeignClientServiceImpl;
import com.hbc.carriercalendar.spring.cache.util.TestUtil;
import com.hbc.common.response.BaseResponse;
import com.hbc.core.cache.mapper.GenericMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CarrierCalendarFeignClientServiceImplTest {

    @InjectMocks
    private CarrierServiceCalendarFeignClientServiceImpl carrierServiceCalendarFeignClientService;

    @InjectMocks private TestUtil testUtil;

    @Mock
    private GenericMapper<
            CarrierServiceCalendarCacheKey, CarrierServiceCalendarCacheValue, String, BaseResponse<List<CalendarDaysStatusInfo>>>
            mapper;

    @Mock private CalendarCommonFeignImpl calendarCommonFeign;

    @Test
    void getExceptionTest() {
        CarrierServiceCalendarCacheKey invalidCacheKey = testUtil.getCarrierServiceCalendarCacheKey();

        when(mapper.responseToCacheValue(any())).thenThrow(new RuntimeException("Error message"));
        assertNull(carrierServiceCalendarFeignClientService.get(invalidCacheKey));
        verify(mapper, times(1)).responseToCacheValue(any());
    }
}
