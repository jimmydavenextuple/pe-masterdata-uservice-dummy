package com.hbc.nodecarriercalendar.spring.cache.service;

import com.hbc.calendar.common.CalendarCommonFeignImpl;
import com.hbc.calendar.domain.CalendarDaysStatusInfo;
import com.hbc.common.response.BaseResponse;
import com.hbc.core.cache.mapper.GenericMapper;
import com.hbc.node.carrier.calendar.cache.domain.NodeCarrierCalendarCacheKey;
import com.hbc.node.carrier.calendar.cache.domain.NodeCarrierCalendarCacheValue;
import com.hbc.node.carrier.calendar.cache.spring.service.NodeCarrierCalendarFeignClientServiceImpl;
import com.hbc.nodecarriercalendar.spring.cache.util.TestUtil;
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
public class NodeCarrierCalendarFeignClientServiceImplTest {
    @InjectMocks
    private NodeCarrierCalendarFeignClientServiceImpl nodeCarrierCalendarFeignClientService;

    @InjectMocks private TestUtil testUtil;

    @Mock
    private GenericMapper<
            NodeCarrierCalendarCacheKey, NodeCarrierCalendarCacheValue, String, BaseResponse<List<CalendarDaysStatusInfo>>>
            mapper;

    @Mock private CalendarCommonFeignImpl calendarCommonFeign;

    @Test
    void getExceptionTest() {
        NodeCarrierCalendarCacheKey invalidCacheKey = testUtil.getNodeCarrierCalendarCacheKey();

        when(mapper.responseToCacheValue(any())).thenThrow(new RuntimeException("Error message"));
        assertNull(nodeCarrierCalendarFeignClientService.get(invalidCacheKey));
        verify(mapper, times(1)).responseToCacheValue(any());
    }
}
