/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.csvdownload.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nextuple.calendar.domain.feign.CalendarFeign;
import com.nextuple.calendar.domain.outbound.CarrierServiceCalendarResponse;
import com.nextuple.calendar.domain.outbound.NodeCalendarResponse;
import com.nextuple.calendar.domain.outbound.NodeCarrierServiceCalendarResponse;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.csvdownload.common.TestUtil;
import com.nextuple.csvdownload.exception.CarrierServiceException;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Description;

@ExtendWith(MockitoExtension.class)
class CalenderResponseServiceTest {

  @Mock private CalendarFeign calendarFeign;

  @InjectMocks private CalenderResponseService calenderResponseService;

  @InjectMocks private TestUtil testUtil;

  @Test
  void getCarrierServiceCalenderTest() {
    CarrierServiceCalendarResponse response = testUtil.getCarrierServiceCalendarResponse();
    when(calendarFeign.getCarrierServiceCalendar(anyString(), anyString()))
        .thenReturn(BaseResponse.builder().payload(List.of(response)).build());
    Assertions.assertDoesNotThrow(
        () ->
            calenderResponseService.getCarrierServiceCalender(
                TestUtil.ORG_ID, TestUtil.CARRIER_SERVICE_ID));
    verify(calendarFeign, times(1)).getCarrierServiceCalendar(anyString(), anyString());
  }

  @Test
  void getCarrierServiceCalenderTestNullResponse() {
    when(calendarFeign.getCarrierServiceCalendar(anyString(), anyString())).thenReturn(null);
    Assertions.assertThrows(
        CarrierServiceException.class,
        () ->
            calenderResponseService.getCarrierServiceCalender(
                TestUtil.ORG_ID, TestUtil.CARRIER_SERVICE_ID));
    verify(calendarFeign, times(1)).getCarrierServiceCalendar(anyString(), anyString());
  }

  @Test
  void getCarrierServiceCalenderTestEmptyResponse() {
    when(calendarFeign.getCarrierServiceCalendar(anyString(), anyString()))
        .thenReturn(BaseResponse.builder().payload(new ArrayList<>()).build());
    Assertions.assertThrows(
        CarrierServiceException.class,
        () ->
            calenderResponseService.getCarrierServiceCalender(
                TestUtil.ORG_ID, TestUtil.CARRIER_SERVICE_ID));
    verify(calendarFeign, times(1)).getCarrierServiceCalendar(anyString(), anyString());
  }

  @Test
  void getNodeCalendarTest() {
    when(calendarFeign.handleGetNodeCalendar(any(), any()))
        .thenReturn(testUtil.getNodeCalendarBaseResponse());

    List<NodeCalendarResponse> responses =
        calenderResponseService.getNodeCalendar(TestUtil.ORG_ID, TestUtil.NODE_ID);

    assertEquals(1, responses.size());
    assertEquals(TestUtil.ORG_ID, responses.get(0).getOrgId());
    assertEquals(TestUtil.NODE_ID, responses.get(0).getNodeId());
    verify(calendarFeign, times(1)).handleGetNodeCalendar(any(), any());
  }

  @Test
  @Description("Get node carrier Service calendar for orgId - Happy Path")
  void getNodeCarrierServiceCalender() throws CarrierServiceException {
    when(calendarFeign.getAllNodeCarrierServiceCalendar(any()))
        .thenReturn(
            BaseResponse.builder()
                .payload(testUtil.getNodeCarrierServiceCalendarResponse())
                .build());
    List<NodeCarrierServiceCalendarResponse> responses =
        calenderResponseService.getNodeCarrierServiceCalender(TestUtil.ORG_ID);
    assertEquals(1, responses.size());
    assertEquals(TestUtil.ORG_ID, responses.get(0).getOrgId());
    assertEquals(TestUtil.NODE_ID, responses.get(0).getNodeId());
    verify(calendarFeign, times(1)).getAllNodeCarrierServiceCalendar(any());
  }

  @Test
  @Description("Get node carrier Service calendar for orgId - Null Scenario")
  void getNodeCarrierServiceCalenderException() throws CarrierServiceException {
    when(calendarFeign.getAllNodeCarrierServiceCalendar(any())).thenReturn(null);
    List<NodeCarrierServiceCalendarResponse> responses =
        calenderResponseService.getNodeCarrierServiceCalender(TestUtil.ORG_ID);
    assertEquals(0, responses.size());
    verify(calendarFeign, times(1)).getAllNodeCarrierServiceCalendar(any());
  }
}
