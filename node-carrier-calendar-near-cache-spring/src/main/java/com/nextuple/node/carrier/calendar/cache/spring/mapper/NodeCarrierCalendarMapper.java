package com.nextuple.node.carrier.calendar.cache.spring.mapper;

import com.nextuple.controltower.common.base.BaseResponse;
import com.nextuple.core.cache.mapper.GenericMapper;
import com.nextuple.node.calendar.domain.CalendarDaysStatusInfo;
import com.nextuple.node.calendar.domain.CarrierServiceCalendarCacheKey;
import com.nextuple.node.calendar.domain.CarrierServiceCalendarCacheValue;
import java.util.List;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;

@Component
public class NodeCarrierCalendarMapper
    implements GenericMapper<
        NodeCarrierCalendarCacheKey,
        CarrierServiceCalendarCacheValue,
        String,
        BaseResponse<List<CalendarDaysStatusInfo>>> {
  public static final DataMapper DATA_MAPPER = Mappers.getMapper(DataMapper.class);

  @Override
  public CarrierServiceCalendarCacheKey requestToCacheKey(String request) {
    return null;
  }

  @Override
  public String cacheKeyToRequest(CarrierServiceCalendarCacheKey cacheKey) {
    return null;
  }

  @Override
  public CarrierServiceCalendarCacheValue responseToCacheValue(
      BaseResponse<List<CalendarDaysStatusInfo>> resp) {
    return CarrierServiceCalendarCacheValue.builder()
        .calendarDaysStatusInfo(resp.getPayload())
        .build();
  }

  @Override
  public BaseResponse<List<CalendarDaysStatusInfo>> cacheValueToResponse(
      CarrierServiceCalendarCacheValue cacheValue) {
    return null;
  }
}
