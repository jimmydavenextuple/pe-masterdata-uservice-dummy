package com.nextuple.node.calendar.cache.spring.mapper;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.core.cache.mapper.GenericMapper;
import com.nextuple.node.calendar.cache.domain.NodeCalendarCacheKey;
import com.nextuple.node.calendar.cache.domain.NodeCalendarCacheValue;
import com.nextuple.node.calendar.cache.domain.NodeCalendarDaysStatusInfo;
import java.util.List;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;

@Component
public class NodeCalendarMapper
    implements GenericMapper<
        NodeCalendarCacheKey,
        NodeCalendarCacheValue,
        String,
        BaseResponse<List<NodeCalendarDaysStatusInfo>>> {
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
      BaseResponse<List<NodeCalendarDaysStatusInfo>> resp) {
    return NodeCalendarCacheValue.builder().calendarDaysStatusInfo(resp.getPayload()).build();
  }

  @Override
  public BaseResponse<List<NodeCalendarDaysStatusInfo>> cacheValueToResponse(
      NodeCalendarCacheValue cacheValue) {
    return null;
  }
}
