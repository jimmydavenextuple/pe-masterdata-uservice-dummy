package com.nextuple.node.calendar.cache.spring.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.nextuple.calendar.common.CalendarCommonFeignImpl;
import com.nextuple.calendar.domain.CalendarDaysStatusInfo;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.core.cache.mapper.GenericMapper;
import com.nextuple.node.calendar.cache.domain.NodeCalendarCacheKey;
import com.nextuple.node.calendar.cache.domain.NodeCalendarCacheValue;
import com.nextuple.node.calendar.cache.spring.util.TestUtil;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class NodeCalendarFeignClientServiceImplTest {

  @InjectMocks private NodeCalendarFeignClientServiceImpl nodeCalendarFeignClientService;

  @InjectMocks private TestUtil testUtil;

  @Mock
  private GenericMapper<
          NodeCalendarCacheKey,
          NodeCalendarCacheValue,
          String,
          BaseResponse<List<CalendarDaysStatusInfo>>>
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
    assertFalse(nodeCalendarFeignClientService.get(cacheKey).isUndefined());
    verify(mapper, times(2)).responseToCacheValue(any());
  }

  @Test
  void getExceptionTest() {
    NodeCalendarCacheKey invalidCacheKey = testUtil.getNodeCalendarCacheKey();

    when(mapper.responseToCacheValue(any())).thenThrow(new RuntimeException("Error message"));
    assertNull(nodeCalendarFeignClientService.get(invalidCacheKey));
    verify(mapper, times(1)).responseToCacheValue(any());
  }
}
