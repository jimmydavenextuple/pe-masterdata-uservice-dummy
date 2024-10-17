/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.dataupload.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nextuple.calendar.domain.feign.CalendarFeign;
import com.nextuple.common.base.PagePayload;
import com.nextuple.dataupload.domain.dto.CalendarDto;
import com.nextuple.dataupload.util.TestUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CalendarDataServiceTest {

  @InjectMocks private CalendarDataService calendarDataService;

  @Mock private CalendarFeign calendarFeign;

  @InjectMocks private TestUtil testUtil;

  @Test
  void getCalendarListSuccessTest() {
    when(calendarFeign.getCalendarListWithPagination(any(), any(), any(), any(), any()))
        .thenReturn(testUtil.getCalendarWithPaginationBaseResponse());
    when(calendarFeign.getNodeCalendars(any(), any()))
        .thenReturn(testUtil.getNodeCalendarBaseResponse());
    when(calendarFeign.getCarrierCalendars(any(), any()))
        .thenReturn(testUtil.getCarrierCalendarBaseResponse());

    PagePayload<CalendarDto> response =
        calendarDataService.getCalendarList(TestUtil.ORG_ID, 1, 1, "calendarId", "DESC");

    assertEquals(2, response.getPagination().getTotalPages());
    assertEquals(1, response.getPagination().getCurrentPage());
    assertEquals(2, response.getPagination().getTotalRecords());
    assertNotNull(response.getPagination().getNext());
    assertNull(response.getPagination().getPrevious());
    assertTrue(response.getData().get((0)).getIsActive());

    verify(calendarFeign, times(1))
        .getCalendarListWithPagination(any(), any(), any(), any(), any());
    verify(calendarFeign, times(2)).getNodeCalendars(any(), any());
    verify(calendarFeign, times(2)).getCarrierCalendars(any(), any());
  }

  @Test
  void getCalendarListIsActiveTrueTest1() {
    when(calendarFeign.getCalendarListWithPagination(any(), any(), any(), any(), any()))
        .thenReturn(testUtil.getCalendarWithPaginationBaseResponse());
    when(calendarFeign.getNodeCalendars(any(), any()))
        .thenReturn(testUtil.getNodeCalendarBaseResponse());
    when(calendarFeign.getCarrierCalendars(any(), any()))
        .thenReturn(testUtil.getEmptyCarrierCalendarBaseResponse());

    PagePayload<CalendarDto> response =
        calendarDataService.getCalendarList(TestUtil.ORG_ID, 1, 1, "calendarId", "DESC");

    assertEquals(2, response.getPagination().getTotalPages());
    assertEquals(1, response.getPagination().getCurrentPage());
    assertEquals(2, response.getPagination().getTotalRecords());
    assertNotNull(response.getPagination().getNext());
    assertNull(response.getPagination().getPrevious());
    assertTrue(response.getData().get((0)).getIsActive());

    verify(calendarFeign, times(1))
        .getCalendarListWithPagination(any(), any(), any(), any(), any());
    verify(calendarFeign, times(2)).getNodeCalendars(any(), any());
    verify(calendarFeign, times(2)).getCarrierCalendars(any(), any());
  }

  @Test
  void getCalendarListIsActiveTrueTest2() {
    when(calendarFeign.getCalendarListWithPagination(any(), any(), any(), any(), any()))
        .thenReturn(testUtil.getCalendarWithPaginationBaseResponse());
    when(calendarFeign.getNodeCalendars(any(), any()))
        .thenReturn(testUtil.getEmptyNodeCalendarBaseResponse());
    when(calendarFeign.getCarrierCalendars(any(), any()))
        .thenReturn(testUtil.getCarrierCalendarBaseResponse());

    PagePayload<CalendarDto> response =
        calendarDataService.getCalendarList(TestUtil.ORG_ID, 1, 1, "calendarId", "DESC");

    assertEquals(2, response.getPagination().getTotalPages());
    assertEquals(1, response.getPagination().getCurrentPage());
    assertEquals(2, response.getPagination().getTotalRecords());
    assertNotNull(response.getPagination().getNext());
    assertNull(response.getPagination().getPrevious());
    assertTrue(response.getData().get((0)).getIsActive());

    verify(calendarFeign, times(1))
        .getCalendarListWithPagination(any(), any(), any(), any(), any());
    verify(calendarFeign, times(2)).getNodeCalendars(any(), any());
    verify(calendarFeign, times(2)).getCarrierCalendars(any(), any());
  }

  @Test
  void getCalendarListIsActiveFalseTest() {
    when(calendarFeign.getCalendarListWithPagination(any(), any(), any(), any(), any()))
        .thenReturn(testUtil.getCalendarWithPaginationBaseResponse());
    when(calendarFeign.getNodeCalendars(any(), any()))
        .thenReturn(testUtil.getEmptyNodeCalendarBaseResponse());
    when(calendarFeign.getCarrierCalendars(any(), any()))
        .thenReturn(testUtil.getEmptyCarrierCalendarBaseResponse());

    PagePayload<CalendarDto> response =
        calendarDataService.getCalendarList(TestUtil.ORG_ID, 1, 1, "calendarId", "ASC");

    assertEquals(2, response.getPagination().getTotalPages());
    assertEquals(1, response.getPagination().getCurrentPage());
    assertEquals(2, response.getPagination().getTotalRecords());
    assertNotNull(response.getPagination().getNext());
    assertNull(response.getPagination().getPrevious());
    assertFalse(response.getData().get((0)).getIsActive());

    verify(calendarFeign, times(1))
        .getCalendarListWithPagination(any(), any(), any(), any(), any());
    verify(calendarFeign, times(2)).getNodeCalendars(any(), any());
    verify(calendarFeign, times(2)).getCarrierCalendars(any(), any());
  }
}
