package com.hbc.carriercalendar.spring.cache.util;

import com.hbc.calendar.domain.CalendarDaysStatusInfo;
import com.hbc.carrier.calendar.cache.domain.CarrierServiceCalendarCacheKey;
import com.hbc.carrier.calendar.cache.domain.CarrierServiceCalendarCacheValue;
import com.hbc.common.response.BaseResponse;

import java.util.ArrayList;
import java.util.List;

public class TestUtil {
    public static final String ORG_ID = "Org_Id_01";
    public static final String CARRIER_SERVICE_ID = "Carrier_Service_Id_01";
    public static final String SERVICE_OPTION = "Service_Option_01";

    public CarrierServiceCalendarCacheValue getCarrierServiceCalendarCacheValue() {
        List<CalendarDaysStatusInfo> calendarDaysStatusInfoList = getCalendarDaysStatusInfo();
        return CarrierServiceCalendarCacheValue.builder().calendarDaysStatusInfo(calendarDaysStatusInfoList).build();
    }
    public List<CalendarDaysStatusInfo> getCalendarDaysStatusInfo(){
        List<CalendarDaysStatusInfo> calendarDaysStatusInfoList = new ArrayList<>();
        CalendarDaysStatusInfo calendarDaysStatusInfo = new CalendarDaysStatusInfo();
        calendarDaysStatusInfo.setDate("2022-07-07");
        calendarDaysStatusInfo.setIsActive(true);
        calendarDaysStatusInfoList.add(calendarDaysStatusInfo);
        return calendarDaysStatusInfoList;
    }

    public BaseResponse<List<CalendarDaysStatusInfo>> getBaseResponseOfListOfCalendarDaysStatusInfo() {
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
