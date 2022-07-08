package com.hbc.nodecalendar.spring.cache.util;

import com.hbc.calendar.domain.CalendarDaysStatusInfo;
import com.hbc.common.response.BaseResponse;
import com.hbc.node.calendar.cache.domain.NodeCalendarCacheKey;
import com.hbc.node.calendar.cache.domain.NodeCalendarCacheValue;
import java.util.ArrayList;
import java.util.List;

public class TestUtil {

  public static final String NODE_ID = "Node_Id_01";
  public static final String ORG_ID = "Org_Id_01";

  public NodeCalendarCacheValue getNodeCalendarCacheValue() {
    List<CalendarDaysStatusInfo> calendarDaysStatusInfoList = getCalendarDaysStatusInfo();
    return NodeCalendarCacheValue.builder()
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

  public NodeCalendarCacheKey getNodeCalendarCacheKey() {
    return NodeCalendarCacheKey.builder().nodeId(NODE_ID).orgId(ORG_ID).build();
  }
}
