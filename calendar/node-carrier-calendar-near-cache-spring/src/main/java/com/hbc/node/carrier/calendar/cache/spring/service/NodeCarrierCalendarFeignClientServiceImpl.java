package com.hbc.node.carrier.calendar.cache.spring.service;

import com.hbc.calendar.common.CalendarCommonFeignImpl;
import com.hbc.calendar.domain.CalendarDaysStatusInfo;
import com.hbc.common.context.Logger;
import com.hbc.common.context.LoggerFactory;
import com.hbc.common.response.BaseResponse;
import com.hbc.core.cache.mapper.GenericMapper;
import com.hbc.core.spring.service.AbstractGenericFeignClientServiceImpl;
import com.hbc.node.carrier.calendar.cache.domain.NodeCarrierCalendarCacheKey;
import com.hbc.node.carrier.calendar.cache.domain.NodeCarrierCalendarCacheValue;
import java.util.List;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NodeCarrierCalendarFeignClientServiceImpl
    extends AbstractGenericFeignClientServiceImpl<
        NodeCarrierCalendarCacheKey,
        NodeCarrierCalendarCacheValue,
        String,
        BaseResponse<List<CalendarDaysStatusInfo>>> {

  @Autowired CalendarCommonFeignImpl calendarCommonFeign;

  @Autowired
  GenericMapper<
          NodeCarrierCalendarCacheKey,
          NodeCarrierCalendarCacheValue,
          String,
          BaseResponse<List<CalendarDaysStatusInfo>>>
      nodeCalendarMapper;

  @Override
  public NodeCarrierCalendarCacheValue get(NodeCarrierCalendarCacheKey key) {
    try {
      BaseResponse<List<CalendarDaysStatusInfo>> response =
          calendarCommonFeign.getNodeCarrierCalendar(
              key.getOrgId(), key.getNodeId(), key.getCarrierServiceId(), key.getServiceOption());

      if (Objects.isNull(response.getPayload())) {
        return NodeCarrierCalendarCacheValue.builder().calendarDaysStatusInfo(null).build();
      }
      return nodeCalendarMapper.responseToCacheValue(response);
    } catch (RuntimeException e) {
      return NodeCarrierCalendarCacheValue.builder().calendarDaysStatusInfo(null).build();
    }
  }
}
