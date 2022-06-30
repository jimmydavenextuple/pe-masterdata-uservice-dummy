package com.hbc.node.calendar.cache.spring.service;

import com.hbc.calendar.common.CalendarCommonFeignImpl;
import com.hbc.calendar.domain.CalendarDaysStatusInfo;
import com.hbc.common.response.BaseResponse;
import com.hbc.core.cache.mapper.GenericMapper;
import com.hbc.core.spring.service.AbstractGenericFeignClientServiceImpl;
import com.hbc.node.calendar.cache.domain.NodeCalendarCacheKey;
import com.hbc.node.calendar.cache.domain.NodeCalendarCacheValue;
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
