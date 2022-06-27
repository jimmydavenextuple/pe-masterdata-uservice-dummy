package com.hbc.node.calendar.cache.spring.mapper;

import com.hbc.calendar.domain.CalendarDaysStatusInfo;
import com.hbc.common.response.BaseResponse;
import com.hbc.core.cache.mapper.GenericMapper;
import com.hbc.node.calendar.cache.domain.NodeCalendarCacheKey;
import com.hbc.node.calendar.cache.domain.NodeCalendarCacheValue;
import java.util.List;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;

@Component
public class NodeCalendarMapper
    implements GenericMapper<
        NodeCalendarCacheKey,
        NodeCalendarCacheValue,
        String,
        BaseResponse<List<CalendarDaysStatusInfo>>> {
  public static final DataMapper DATA_MAPPER = Mappers.getMapper(DataMapper.class);

  @Override
  public NodeCalendarCacheKey requestToCacheKey(String request) {
    return null;
  }

  @Override
  public String cacheKeyToRequest(NodeCalendarCacheKey cacheKey) {
    return null;
  }

  @Override
  public NodeCalendarCacheValue responseToCacheValue(
      BaseResponse<List<CalendarDaysStatusInfo>> resp) {
    return NodeCalendarCacheValue.builder().calendarDaysStatusInfo(resp.getPayload()).build();
  }

  @Override
  public BaseResponse<List<CalendarDaysStatusInfo>> cacheValueToResponse(
      NodeCalendarCacheValue cacheValue) {
    return null;
  }
}
