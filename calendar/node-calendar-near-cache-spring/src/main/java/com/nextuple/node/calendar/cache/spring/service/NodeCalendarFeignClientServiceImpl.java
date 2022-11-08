package com.nextuple.node.calendar.cache.spring.service;

import com.nextuple.calendar.common.CalendarCommonFeignImpl;
import com.nextuple.calendar.domain.CalendarDaysStatusInfo;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.core.cache.mapper.GenericMapper;
import com.nextuple.core.spring.service.AbstractGenericFeignClientServiceImpl;
import com.nextuple.node.calendar.cache.domain.NodeCalendarCacheKey;
import com.nextuple.node.calendar.cache.domain.NodeCalendarCacheValue;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NodeCalendarFeignClientServiceImpl
    extends AbstractGenericFeignClientServiceImpl<
        NodeCalendarCacheKey,
        NodeCalendarCacheValue,
        String,
        BaseResponse<List<CalendarDaysStatusInfo>>> {

  @Autowired CalendarCommonFeignImpl calendarCommonFeign;

  @Autowired
  GenericMapper<
          NodeCalendarCacheKey,
          NodeCalendarCacheValue,
          String,
          BaseResponse<List<CalendarDaysStatusInfo>>>
      nodeCalendarMapper;

  @Override
  public NodeCalendarCacheValue get(NodeCalendarCacheKey key) {
    try {
      return nodeCalendarMapper.responseToCacheValue(
          calendarCommonFeign.getNodeCalendar(key.getOrgId(), key.getNodeId()));
    } catch (RuntimeException e) {
      return null;
    }
  }
}
