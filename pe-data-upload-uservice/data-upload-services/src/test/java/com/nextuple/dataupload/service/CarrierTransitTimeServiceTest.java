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
import com.nextuple.carrier.domain.feign.CarrierFeign;
import com.nextuple.common.base.PagePayload;
import com.nextuple.dataupload.domain.dto.CarrierTransitDto;
import com.nextuple.dataupload.util.TestUtil;
import com.nextuple.transit.domain.feign.TransitFeign;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CarrierTransitTimeServiceTest {

  @InjectMocks private CarrierTransitTimeService carrierTransitTimeService;
  @InjectMocks private TestUtil testUtil;

  @Mock private CarrierFeign carrierFeign;
  @Mock private CalendarFeign calendarFeign;
  @Mock private TransitFeign transitFeign;

  @Test
  void getCarrierTransitTimeListSuccessTest1() {

    when(carrierFeign.getCarrierServiceListWithPagination(any(), any(), any(), any(), any()))
        .thenReturn(testUtil.getCarrierServiceListWithPaginationBaseResponse());
    when(calendarFeign.getCarrierServiceCalendar(TestUtil.ORG_ID, TestUtil.CARRIER_SERVICE_ID))
        .thenReturn(testUtil.getCarrierServiceCalendarBaseResponse());
    when(transitFeign.getTransitTimeEntries(any(), any()))
        .thenReturn(testUtil.getTransitTimeEntriesDtoBaseResponse(5));
    when(calendarFeign.getCalendar(TestUtil.ORG_ID, TestUtil.CALENDAR_ID))
        .thenReturn(testUtil.getBaseResponseOfCalendar());

    PagePayload<CarrierTransitDto> response =
        carrierTransitTimeService.getCarrierTransitTimeList(
            TestUtil.ORG_ID, 1, 1, "carrierId", "DESC");

    assertEquals(2, response.getPagination().getTotalPages());
    assertEquals(1, response.getPagination().getCurrentPage());
    assertEquals(2, response.getPagination().getTotalRecords());
    assertNotNull(response.getPagination().getNext());
    assertNull(response.getPagination().getPrevious());
    assertTrue(response.getData().get((0)).getIsCarrierActive());
    assertTrue(response.getData().get((0)).getIsCalendarAssigned());
    assertEquals(
        TestUtil.EFFECTIVE_DATE,
        response.getData().get((0)).getCarrierServiceCalendar().getEffectiveDate());

    verify(carrierFeign, times(1))
        .getCarrierServiceListWithPagination(any(), any(), any(), any(), any());
    verify(calendarFeign, times(2)).getCarrierServiceCalendar(any(), any());
    verify(calendarFeign, times(2)).getCalendar(any(), any());
    verify(transitFeign, times(2)).getTransitTimeEntries(any(), any());
  }

  @Test
  void getCarrierTransitTimeListSuccessTest2() {

    when(carrierFeign.getCarrierServiceListWithPagination(any(), any(), any(), any(), any()))
        .thenReturn(testUtil.getCarrierServiceListWithPaginationBaseResponse());
    when(calendarFeign.getCarrierServiceCalendar(TestUtil.ORG_ID, TestUtil.CARRIER_SERVICE_ID))
        .thenThrow(new RuntimeException("Carrier Calendar not found"));
    when(transitFeign.getTransitTimeEntries(any(), any()))
        .thenReturn(testUtil.getTransitTimeEntriesDtoBaseResponse(0));

    PagePayload<CarrierTransitDto> response =
        carrierTransitTimeService.getCarrierTransitTimeList(
            TestUtil.ORG_ID, 1, 1, "carrierId", "ASC");

    assertEquals(2, response.getPagination().getTotalPages());
    assertEquals(1, response.getPagination().getCurrentPage());
    assertEquals(2, response.getPagination().getTotalRecords());
    assertNotNull(response.getPagination().getNext());
    assertNull(response.getPagination().getPrevious());
    assertFalse(response.getData().get((0)).getIsCarrierActive());
    assertFalse(response.getData().get((0)).getIsCalendarAssigned());

    verify(carrierFeign, times(1))
        .getCarrierServiceListWithPagination(any(), any(), any(), any(), any());
    verify(calendarFeign, times(2)).getCarrierServiceCalendar(any(), any());
    verify(transitFeign, times(2)).getTransitTimeEntries(any(), any());
  }

  @Test
  void getCarrierTransitTimeListWithEffectiveDatesInFutureTest() {

    when(carrierFeign.getCarrierServiceListWithPagination(any(), any(), any(), any(), any()))
        .thenReturn(testUtil.getCarrierServiceListWithPaginationBaseResponse());
    when(calendarFeign.getCarrierServiceCalendar(TestUtil.ORG_ID, TestUtil.CARRIER_SERVICE_ID))
        .thenReturn(testUtil.getCarrierServiceCalendarWithFutureEffectiveDates());
    when(transitFeign.getTransitTimeEntries(any(), any()))
        .thenReturn(testUtil.getTransitTimeEntriesDtoBaseResponse(5));
    when(calendarFeign.getCalendar(TestUtil.ORG_ID, TestUtil.CALENDAR_ID))
        .thenReturn(testUtil.getBaseResponseOfCalendar());

    PagePayload<CarrierTransitDto> response =
        carrierTransitTimeService.getCarrierTransitTimeList(
            TestUtil.ORG_ID, 1, 1, "carrierId", "DESC");

    assertEquals(2, response.getPagination().getTotalPages());
    assertEquals(1, response.getPagination().getCurrentPage());
    assertEquals(2, response.getPagination().getTotalRecords());
    assertNotNull(response.getPagination().getNext());
    assertNull(response.getPagination().getPrevious());
    assertTrue(response.getData().get((0)).getIsCarrierActive());
    assertTrue(response.getData().get((0)).getIsCalendarAssigned());
    assertEquals(
        TestUtil.EFFECTIVE_DATE_2,
        response.getData().get((0)).getCarrierServiceCalendar().getEffectiveDate());

    verify(carrierFeign, times(1))
        .getCarrierServiceListWithPagination(any(), any(), any(), any(), any());
    verify(calendarFeign, times(2)).getCarrierServiceCalendar(any(), any());
    verify(transitFeign, times(2)).getTransitTimeEntries(any(), any());
  }
}
