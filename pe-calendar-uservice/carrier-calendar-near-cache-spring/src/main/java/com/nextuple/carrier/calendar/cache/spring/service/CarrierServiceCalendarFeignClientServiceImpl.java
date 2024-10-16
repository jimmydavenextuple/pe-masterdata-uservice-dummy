/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.carrier.calendar.cache.spring.service;

import com.nextuple.calendar.common.CalendarCommonFeignImpl;
import com.nextuple.calendar.domain.CalendarDaysStatusInfo;
import com.nextuple.carrier.calendar.cache.domain.CarrierServiceCalendarCacheKey;
import com.nextuple.carrier.calendar.cache.domain.CarrierServiceCalendarCacheValue;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.core.cache.mapper.GenericMapper;
import com.nextuple.core.spring.service.AbstractGenericFeignClientServiceImpl;
import java.util.List;
import java.util.Objects;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
// Added this
@AllArgsConstructor
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
      BaseResponse<List<CalendarDaysStatusInfo>> response =
          calendarCommonFeign.getCarrierServiceCalendar(
              key.getOrgId(), key.getCarrierServiceId(), key.getServiceOption(), key.getFromDate());
      if (Objects.isNull(response.getPayload())) {
        return CarrierServiceCalendarCacheValue.builder().build();
      }
      return nodeCalendarMapper.responseToCacheValue(response);
    } catch (RuntimeException e) {
      return CarrierServiceCalendarCacheValue.builder().build();
    }
  }
}
