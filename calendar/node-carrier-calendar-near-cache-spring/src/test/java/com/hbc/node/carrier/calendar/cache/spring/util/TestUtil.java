package com.hbc.node.carrier.calendar.cache.spring.util;

import com.hbc.calendar.domain.CalendarDaysStatusInfo;
import com.hbc.common.response.BaseResponse;
import com.hbc.node.carrier.calendar.cache.domain.NodeCarrierCalendarCacheKey;
import com.hbc.node.carrier.calendar.cache.domain.NodeCarrierCalendarCacheValue;
import java.util.ArrayList;
import java.util.List;

public class TestUtil {

  public static final String NODE_ID = "Node_id_01";
  public static final String ORG_ID = "Org_Id_01";
  public static final String CARRIER_SERVICE_ID = "Carrier_service_Id_01";
  public static final String SERVICE_OPTION = "Service_Option_01";

  public NodeCarrierCalendarCacheValue getNodeCarrierCalendarCacheValue() {
    List<CalendarDaysStatusInfo> calendarDaysStatusInfoList = getCalendarDaysStatusInfo();
    return NodeCarrierCalendarCacheValue.builder()
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

  public NodeCarrierCalendarCacheKey getNodeCarrierCalendarCacheKey() {
    return NodeCarrierCalendarCacheKey.builder()
        .nodeId(NODE_ID)
        .orgId(ORG_ID)
        .carrierServiceId(CARRIER_SERVICE_ID)
        .serviceOption(SERVICE_OPTION)
        .build();
  }
}
