package com.nextuple.node.carrier.calendar.cache.spring.service;

import com.nextuple.controltower.common.base.BaseResponse;
import com.nextuple.core.cache.mapper.GenericMapper;
import com.nextuple.core.spring.service.AbstractGenericFeignClientServiceImpl;
import com.nextuple.node.calendar.domain.CalendarDaysStatusInfo;
import com.nextuple.node.calendar.domain.CarrierServiceCalendarCacheKey;
import com.nextuple.node.calendar.domain.CarrierServiceCalendarCacheValue;
import com.nextuple.node.calendar.feign.CarrierServiceCalendarFeignImpl;
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
