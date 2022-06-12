package com.nextuple.carrier.calendar.cache.spring.mapper;

import com.nextuple.calendar.domain.CalendarDaysStatusInfo;
import com.nextuple.carrier.calendar.cache.domain.CarrierServiceCalendarCacheKey;
import com.nextuple.carrier.calendar.cache.domain.CarrierServiceCalendarCacheValue;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.core.cache.mapper.GenericMapper;
import java.util.List;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;

@Component
public class CarrierServiceCalendarMapper
    implements GenericMapper<
        CarrierServiceCalendarCacheKey,
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
