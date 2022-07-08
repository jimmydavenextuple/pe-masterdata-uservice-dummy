package com.hbc.nodecalendar.spring.cache.service;

import com.hbc.calendar.common.CalendarCommonFeignImpl;
import com.hbc.calendar.domain.CalendarDaysStatusInfo;
import com.hbc.common.response.BaseResponse;
import com.hbc.core.cache.mapper.GenericMapper;
import com.hbc.node.calendar.cache.domain.NodeCalendarCacheKey;
import com.hbc.node.calendar.cache.domain.NodeCalendarCacheValue;
import com.hbc.node.calendar.cache.spring.service.NodeCalendarFeignClientServiceImpl;
import com.hbc.nodecalendar.spring.cache.util.TestUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NodeCalendarFeignClientServiceImplTest {

    @InjectMocks
    private NodeCalendarFeignClientServiceImpl nodeCalendarFeignClientService;

    @InjectMocks private TestUtil testUtil;

    @Mock
    private GenericMapper<
            NodeCalendarCacheKey, NodeCalendarCacheValue, String, BaseResponse<List<CalendarDaysStatusInfo>>>
            mapper;

    @Mock private CalendarCommonFeignImpl calendarCommonFeign;

    @Test
    void getTest() {
        NodeCalendarCacheKey cacheKey = testUtil.getNodeCalendarCacheKey();
        NodeCalendarCacheValue cacheValue = testUtil.getNodeCalendarCacheValue();

        when(mapper.responseToCacheValue(any())).thenReturn(cacheValue);
        when(calendarCommonFeign.getNodeCalendar(any(), any()))
                .thenReturn(testUtil.getBaseResponseOfListOfCalendarDaysStatusInfo());

        assertEquals(cacheValue, nodeCalendarFeignClientService.get(cacheKey));
        verify(mapper, times(1)).responseToCacheValue(any());
    }
    @Test
    void getExceptionTest() {
        NodeCalendarCacheKey invalidCacheKey = testUtil.getNodeCalendarCacheKey();

        when(mapper.responseToCacheValue(any())).thenThrow(new RuntimeException("Error message"));
        assertNull(nodeCalendarFeignClientService.get(invalidCacheKey));
        verify(mapper, times(1)).responseToCacheValue(any());
    }
}
