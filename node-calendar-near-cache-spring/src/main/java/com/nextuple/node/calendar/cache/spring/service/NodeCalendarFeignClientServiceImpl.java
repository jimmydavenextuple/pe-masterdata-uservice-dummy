package com.nextuple.node.calendar.cache.spring.service;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.core.cache.mapper.GenericMapper;
import com.nextuple.core.spring.service.AbstractGenericFeignClientServiceImpl;
import com.nextuple.node.calendar.cache.domain.NodeCalendarCacheKey;
import com.nextuple.node.calendar.cache.domain.NodeCalendarCacheValue;
import com.nextuple.node.calendar.cache.domain.NodeCalendarDaysStatusInfo;
import com.nextuple.node.calendar.cache.spring.feign.NodeCalendarFeignImpl;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NodeCalendarFeignClientServiceImpl
    extends AbstractGenericFeignClientServiceImpl<
        NodeCalendarCacheKey,
        NodeCalendarCacheValue,
        String,
        BaseResponse<List<NodeCalendarDaysStatusInfo>>> {

  @Autowired NodeCalendarFeignImpl nodeCalendarFeign;

  @Autowired
  GenericMapper<
          NodeCalendarCacheKey,
          NodeCalendarCacheValue,
          String,
          BaseResponse<List<NodeCalendarDaysStatusInfo>>>
      nodeCalendarMapper;

  @Override
  public NodeCalendarCacheValue get(NodeCalendarCacheKey key) {
    try {
      return nodeCalendarMapper.responseToCacheValue(
          nodeCalendarFeign.getNodeCalendar(key.getOrgId(), key.getNodeId()));
    } catch (RuntimeException e) {
      return null;
    }
  }
}
