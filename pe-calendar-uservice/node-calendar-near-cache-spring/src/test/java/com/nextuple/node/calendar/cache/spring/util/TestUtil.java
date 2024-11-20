/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.node.calendar.cache.spring.util;

import com.nextuple.calendar.domain.CalendarDaysStatusInfo;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.node.calendar.cache.domain.NodeCalendarCacheKey;
import com.nextuple.node.calendar.cache.domain.NodeCalendarCacheValue;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

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

  public ConcurrentHashMap<Object, Object> getNodeCalendarCacheMap() {
    var k1 = getNodeCalendarCacheKey();
    var k2 =
        NodeCalendarCacheKey.builder().nodeId(NODE_ID).orgId(ORG_ID).fromDate("2024-01-01").build();
    var k3 =
        NodeCalendarCacheKey.builder().nodeId(NODE_ID).orgId(ORG_ID).fromDate("2025-01-01").build();
    var k4 =
        NodeCalendarCacheKey.builder().nodeId(NODE_ID).orgId(ORG_ID).fromDate("2026-01-01").build();
    var k5 = NodeCalendarCacheKey.builder().nodeId(NODE_ID + "01").orgId(ORG_ID).build();
    var k6 =
        NodeCalendarCacheKey.builder()
            .nodeId(NODE_ID + "01")
            .orgId(ORG_ID)
            .fromDate("2024-01-01")
            .build();
    ConcurrentHashMap map = new ConcurrentHashMap();
    map.put(k1, k1);
    map.put(k2, k2);
    map.put(k3, k3);
    map.put(k4, k4);
    map.put(k5, k5);
    map.put(k6, k6);
    return map;
  }
}
