package com.hbc.nodecalendar.spring.cache.util;

import com.hbc.calendar.domain.CalendarDaysStatusInfo;
import com.hbc.common.response.BaseResponse;
import com.hbc.node.calendar.cache.domain.NodeCalendarCacheKey;

import java.util.List;

public class TestUtil {

    public static final String NODE_ID = "Node_Id_01";
    public static final String ORG_ID = "Org_Id_01";

    public CalendarDaysStatusInfo getCalendarDaysStatusInfo(){
        return CalendarDaysStatusInfo.builder()
                .date("2022-07-07")
                .isActive(true)
                .build();
    }

    public BaseResponse<List<CalendarDaysStatusInfo>> getBaseResponseOfListOfCalendarDaysStatusInfo() {
        return BaseResponse.builder()
                .message("Carrier calendar details fetched successfully")
                .payload(getCalendarDaysStatusInfo())
                .build();
    }

    public NodeCalendarCacheKey getNodeCalendarCacheKey() {
        return NodeCalendarCacheKey.builder()
                .nodeId(NODE_ID)
                .orgId(ORG_ID)
                .build();
    }
}
