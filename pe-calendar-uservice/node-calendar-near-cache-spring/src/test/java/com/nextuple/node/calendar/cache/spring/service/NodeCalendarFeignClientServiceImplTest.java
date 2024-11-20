/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.node.calendar.cache.spring.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.nextuple.calendar.common.CalendarCommonFeignImpl;
import com.nextuple.calendar.domain.CalendarDaysStatusInfo;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.core.cache.mapper.GenericMapper;
import com.nextuple.node.calendar.cache.domain.NodeCalendarCacheKey;
import com.nextuple.node.calendar.cache.domain.NodeCalendarCacheValue;
import com.nextuple.node.calendar.cache.spring.util.TestUtil;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class NodeCalendarFeignClientServiceImplTest {

  @InjectMocks private NodeCalendarFeignClientServiceImpl nodeCalendarFeignClientService;

  @InjectMocks private TestUtil testUtil;

  @Mock
  private GenericMapper<
          NodeCalendarCacheKey,
          NodeCalendarCacheValue,
          String,
          BaseResponse<List<CalendarDaysStatusInfo>>>
      mapper;

  @Mock private CalendarCommonFeignImpl calendarCommonFeign;

  @Test
  void getTest() {
    NodeCalendarCacheKey cacheKey = testUtil.getNodeCalendarCacheKey();
    NodeCalendarCacheValue cacheValue = testUtil.getNodeCalendarCacheValue();

    when(mapper.responseToCacheValue(any())).thenReturn(cacheValue);
    when(calendarCommonFeign.getNodeCalendar(any(), any(), any()))
        .thenReturn(testUtil.getBaseResponseOfListOfCalendarDaysStatusInfo());

    assertEquals(cacheValue, nodeCalendarFeignClientService.get(cacheKey));
    assertFalse(nodeCalendarFeignClientService.get(cacheKey).isUndefined());
    verify(mapper, times(2)).responseToCacheValue(any());
  }

  @Test
  void getExceptionTest() {
    NodeCalendarCacheKey invalidCacheKey = testUtil.getNodeCalendarCacheKey();
    assertNotNull(nodeCalendarFeignClientService.get(invalidCacheKey));
    verify(mapper, times(0)).responseToCacheValue(any());
  }
}
