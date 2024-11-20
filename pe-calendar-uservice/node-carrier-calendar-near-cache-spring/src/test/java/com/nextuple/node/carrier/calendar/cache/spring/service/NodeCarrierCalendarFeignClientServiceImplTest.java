/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.node.carrier.calendar.cache.spring.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.nextuple.calendar.common.CalendarCommonFeignImpl;
import com.nextuple.calendar.domain.CalendarDaysStatusInfo;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.core.cache.mapper.GenericMapper;
import com.nextuple.node.carrier.calendar.cache.domain.NodeCarrierCalendarCacheKey;
import com.nextuple.node.carrier.calendar.cache.domain.NodeCarrierCalendarCacheValue;
import com.nextuple.node.carrier.calendar.cache.spring.util.TestUtil;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class NodeCarrierCalendarFeignClientServiceImplTest {
  @InjectMocks
  private NodeCarrierCalendarFeignClientServiceImpl nodeCarrierCalendarFeignClientService;

  @InjectMocks private TestUtil testUtil;

  @Mock
  private GenericMapper<
          NodeCarrierCalendarCacheKey,
          NodeCarrierCalendarCacheValue,
          String,
          BaseResponse<List<CalendarDaysStatusInfo>>>
      mapper;

  @Mock private CalendarCommonFeignImpl calendarCommonFeign;

  @Test
  void getTest() {
    NodeCarrierCalendarCacheKey cacheKey = testUtil.getNodeCarrierCalendarCacheKey();
    NodeCarrierCalendarCacheValue cacheValue = testUtil.getNodeCarrierCalendarCacheValue();

    when(mapper.responseToCacheValue(any())).thenReturn(cacheValue);
    when(calendarCommonFeign.getNodeCarrierCalendar(any(), any(), any(), any(), any()))
        .thenReturn(testUtil.getBaseResponseOfListOfCalendarDaysStatusInfo());

    assertEquals(cacheValue, nodeCarrierCalendarFeignClientService.get(cacheKey));
    assertFalse(nodeCarrierCalendarFeignClientService.get(cacheKey).isUndefined());
    verify(mapper, times(2)).responseToCacheValue(any());
  }

  @Test
  void getTestNullCheck() {
    NodeCarrierCalendarCacheKey cacheKey = testUtil.getNodeCarrierCalendarCacheKey();
    NodeCarrierCalendarCacheValue cacheValue = testUtil.getNodeCarrierCalendarCacheValue();

    BaseResponse<List<CalendarDaysStatusInfo>> baseResponse =
        testUtil.getBaseResponseOfListOfCalendarDaysStatusInfo();
    baseResponse.setPayload(null);
    when(calendarCommonFeign.getNodeCarrierCalendar(any(), any(), any(), any(), any()))
        .thenReturn(baseResponse);
    var response = nodeCarrierCalendarFeignClientService.get(cacheKey);

    assertNotNull(response);
    assertNull(response.getCalendarDaysStatusInfo());
    verify(mapper, times(0)).responseToCacheValue(any());
  }

  @Test
  void getExceptionTest() {
    NodeCarrierCalendarCacheKey invalidCacheKey = testUtil.getNodeCarrierCalendarCacheKey();

    var response = nodeCarrierCalendarFeignClientService.get(invalidCacheKey);
    assertNotNull(response);
    assertNull(response.getCalendarDaysStatusInfo());
    verify(mapper, times(0)).responseToCacheValue(any());
  }
}
