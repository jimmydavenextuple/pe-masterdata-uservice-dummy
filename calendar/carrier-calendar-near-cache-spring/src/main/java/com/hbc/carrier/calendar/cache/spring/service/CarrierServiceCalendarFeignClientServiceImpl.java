package com.hbc.carrier.calendar.cache.spring.service;

import com.hbc.calendar.common.CalendarCommonFeignImpl;
import com.hbc.calendar.domain.CalendarDaysStatusInfo;
import com.hbc.carrier.calendar.cache.domain.CarrierServiceCalendarCacheKey;
import com.hbc.carrier.calendar.cache.domain.CarrierServiceCalendarCacheValue;
import com.hbc.common.response.BaseResponse;
import com.hbc.core.cache.mapper.GenericMapper;
import com.hbc.core.spring.service.AbstractGenericFeignClientServiceImpl;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CarrierServiceCalendarFeignClientServiceImpl
    extends AbstractGenericFeignClientServiceImpl<
        CarrierServiceCalendarCacheKey,
        CarrierServiceCalendarCacheValue,
        String,
        BaseResponse<List<CalendarDaysStatusInfo>>> {

  @Autowired CalendarCommonFeignImpl calendarCommonFeign;

  @Autowired
  GenericMapper<
          CarrierServiceCalendarCacheKey,
          CarrierServiceCalendarCacheValue,
          String,
          BaseResponse<List<CalendarDaysStatusInfo>>>
      nodeCalendarMapper;

  @Override
  public CarrierServiceCalendarCacheValue get(CarrierServiceCalendarCacheKey key) {
    try {
      return nodeCalendarMapper.responseToCacheValue(
          calendarCommonFeign.getCarrierServiceCalendar(
              key.getOrgId(), key.getCarrierServiceId(), key.getServiceOption()));
    } catch (RuntimeException e) {
      return null;
    }
  }
}
