package com.nextuple.carrier.calendar.cache.spring.service;

import com.nextuple.calendar.domain.CalendarDaysStatusInfo;
import com.nextuple.carrier.calendar.cache.domain.CarrierServiceCalendarCacheKey;
import com.nextuple.carrier.calendar.cache.domain.CarrierServiceCalendarCacheValue;
import com.nextuple.carrier.calendar.cache.spring.feign.CarrierServiceCalendarFeignImpl;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.core.cache.mapper.GenericMapper;
import com.nextuple.core.spring.service.AbstractGenericFeignClientServiceImpl;
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

  @Autowired CarrierServiceCalendarFeignImpl nodeCalendarFeign;

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
          nodeCalendarFeign.getCarrierServiceCalendar(
              key.getOrgId(), key.getCarrierServiceId(), key.getServiceOption()));
    } catch (RuntimeException e) {
      return null;
    }
  }
}
