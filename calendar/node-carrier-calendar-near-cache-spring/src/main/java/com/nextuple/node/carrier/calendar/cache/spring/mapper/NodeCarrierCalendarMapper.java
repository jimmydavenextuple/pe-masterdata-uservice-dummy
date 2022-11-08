package com.nextuple.node.carrier.calendar.cache.spring.mapper;

import com.nextuple.calendar.domain.CalendarDaysStatusInfo;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.core.cache.mapper.GenericMapper;
import com.nextuple.node.carrier.calendar.cache.domain.NodeCarrierCalendarCacheKey;
import com.nextuple.node.carrier.calendar.cache.domain.NodeCarrierCalendarCacheValue;
import java.util.List;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;

@Component
public class NodeCarrierCalendarMapper
    implements GenericMapper<
        NodeCarrierCalendarCacheKey,
        NodeCarrierCalendarCacheValue,
        String,
        BaseResponse<List<CalendarDaysStatusInfo>>> {
  public static final DataMapper DATA_MAPPER = Mappers.getMapper(DataMapper.class);

  @Override
  public NodeCarrierCalendarCacheKey requestToCacheKey(String request) {
    return null;
  }

  @Override
  public String cacheKeyToRequest(NodeCarrierCalendarCacheKey cacheKey) {
    return null;
  }

  @Override
  public NodeCarrierCalendarCacheValue responseToCacheValue(
      BaseResponse<List<CalendarDaysStatusInfo>> resp) {
    return NodeCarrierCalendarCacheValue.builder()
        .calendarDaysStatusInfo(resp.getPayload())
        .build();
  }

  @Override
  public BaseResponse<List<CalendarDaysStatusInfo>> cacheValueToResponse(
      NodeCarrierCalendarCacheValue cacheValue) {
    return null;
  }
}
