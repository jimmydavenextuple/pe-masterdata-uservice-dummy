package com.nextuple.node.carrier.calendar.cache.spring.service;

import com.nextuple.calendar.domain.CalendarDaysStatusInfo;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.core.cache.mapper.GenericMapper;
import com.nextuple.core.spring.service.AbstractGenericFeignClientServiceImpl;
import com.nextuple.node.carrier.calendar.cache.domain.NodeCarrierCalendarCacheKey;
import com.nextuple.node.carrier.calendar.cache.domain.NodeCarrierCalendarCacheValue;
import com.nextuple.node.carrier.calendar.cache.spring.feign.NodeCarrierCalendarFeignImpl;
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
