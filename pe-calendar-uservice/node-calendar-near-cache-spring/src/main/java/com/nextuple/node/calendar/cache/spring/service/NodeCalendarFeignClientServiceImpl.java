/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.node.calendar.cache.spring.service;

import com.nextuple.calendar.common.CalendarCommonFeignImpl;
import com.nextuple.calendar.domain.CalendarDaysStatusInfo;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.core.cache.mapper.GenericMapper;
import com.nextuple.core.spring.service.AbstractGenericFeignClientServiceImpl;
import com.nextuple.node.calendar.cache.domain.NodeCalendarCacheKey;
import com.nextuple.node.calendar.cache.domain.NodeCalendarCacheValue;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NodeCalendarFeignClientServiceImpl
    extends AbstractGenericFeignClientServiceImpl<
        NodeCalendarCacheKey,
        NodeCalendarCacheValue,
        String,
        BaseResponse<List<CalendarDaysStatusInfo>>> {

  private final CalendarCommonFeignImpl calendarCommonFeign;

  private final GenericMapper<
          NodeCalendarCacheKey,
          NodeCalendarCacheValue,
          String,
          BaseResponse<List<CalendarDaysStatusInfo>>>
      nodeCalendarMapper;

  @Override
  public NodeCalendarCacheValue get(NodeCalendarCacheKey key) {
    try {
      BaseResponse<List<CalendarDaysStatusInfo>> response =
          calendarCommonFeign.getNodeCalendar(key.getOrgId(), key.getNodeId(), key.getFromDate());
      if (Objects.isNull(response.getPayload())) {
        return NodeCalendarCacheValue.builder().build();
      }
      return nodeCalendarMapper.responseToCacheValue(response);
    } catch (RuntimeException e) {
      return NodeCalendarCacheValue.builder().build();
    }
  }
}
