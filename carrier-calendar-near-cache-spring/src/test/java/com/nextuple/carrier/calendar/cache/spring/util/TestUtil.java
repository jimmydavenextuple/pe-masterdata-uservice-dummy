/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.carrier.calendar.cache.spring.util;

import com.nextuple.calendar.domain.CalendarDaysStatusInfo;
import com.nextuple.carrier.calendar.cache.domain.CarrierServiceCalendarCacheKey;
import com.nextuple.carrier.calendar.cache.domain.CarrierServiceCalendarCacheValue;
import com.nextuple.common.response.BaseResponse;
import java.util.ArrayList;
import java.util.List;

public class TestUtil {
  public static final String ORG_ID = "Org_Id_01";
  public static final String CARRIER_SERVICE_ID = "Carrier_Service_Id_01";
  public static final String SERVICE_OPTION = "Service_Option_01";

  public CarrierServiceCalendarCacheValue getCarrierServiceCalendarCacheValue() {
    List<CalendarDaysStatusInfo> calendarDaysStatusInfoList = getCalendarDaysStatusInfo();
    return CarrierServiceCalendarCacheValue.builder()
        .calendarDaysStatusInfo(calendarDaysStatusInfoList)
        .build();
  }

  public List<CalendarDaysStatusInfo> getCalendarDaysStatusInfo() {
    List<CalendarDaysStatusInfo> calendarDaysStatusInfoList = new ArrayList<>();
    CalendarDaysStatusInfo calendarDaysStatusInfo = new CalendarDaysStatusInfo();
    calendarDaysStatusInfo.setDate("2022-07-07");
    calendarDaysStatusInfo.setIsActive(true);
    calendarDaysStatusInfoList.add(calendarDaysStatusInfo);
    return calendarDaysStatusInfoList;
  }

  public BaseResponse<List<CalendarDaysStatusInfo>>
      getBaseResponseOfListOfCalendarDaysStatusInfo() {
    return BaseResponse.builder()
        .message("Carrier calendar details fetched successfully")
        .payload(getCalendarDaysStatusInfo())
        .build();
  }

  public CarrierServiceCalendarCacheKey getCarrierServiceCalendarCacheKey() {
    return CarrierServiceCalendarCacheKey.builder()
        .orgId(ORG_ID)
        .carrierServiceId(CARRIER_SERVICE_ID)
        .serviceOption(SERVICE_OPTION)
        .build();
  }
}
