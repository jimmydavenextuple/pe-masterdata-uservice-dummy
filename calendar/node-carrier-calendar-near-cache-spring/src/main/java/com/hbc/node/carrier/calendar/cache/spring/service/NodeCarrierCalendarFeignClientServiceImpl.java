package com.hbc.node.carrier.calendar.cache.spring.service;

import com.hbc.calendar.domain.CalendarDaysStatusInfo;
import com.hbc.common.response.BaseResponse;
import com.hbc.core.cache.mapper.GenericMapper;
import com.hbc.core.spring.service.AbstractGenericFeignClientServiceImpl;
import com.hbc.node.carrier.calendar.cache.domain.NodeCarrierCalendarCacheKey;
import com.hbc.node.carrier.calendar.cache.domain.NodeCarrierCalendarCacheValue;
import com.hbc.node.carrier.calendar.cache.spring.feign.NodeCarrierCalendarFeignImpl;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NodeCarrierCalendarFeignClientServiceImpl
    extends AbstractGenericFeignClientServiceImpl<
        NodeCarrierCalendarCacheKey,
        NodeCarrierCalendarCacheValue,
        String,
        BaseResponse<List<CalendarDaysStatusInfo>>> {

  @Autowired NodeCarrierCalendarFeignImpl nodeCarrierCalendarFeign;

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
      return nodeCalendarMapper.responseToCacheValue(
          nodeCarrierCalendarFeign.getNodeCarrierCalendar(
              key.getOrgId(), key.getNodeId(), key.getCarrierServiceId(), key.getServiceOption()));
    } catch (RuntimeException e) {
      return null;
    }
  }
}
