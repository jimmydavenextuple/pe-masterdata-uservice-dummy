/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.pe.masterdata.calendar.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nextuple.calendar.domain.CalendarDaysStatusInfo;
import com.nextuple.calendar.domain.outbound.CalendarResponse;
import com.nextuple.calendar.persistence.exception.CalendarDomainException;
import com.nextuple.calendar.persistence.exception.CalenderServiceException;
import com.nextuple.calendar.persistence.exception.DateException;
import com.nextuple.common.base.PagePayload;
import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.pojo.PageProperties;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.pe.masterdata.calendar.service.CalendarService;
import com.nextuple.pe.masterdata.calendar.util.TestUtil;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.verification.VerificationModeFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class CalendarControllerTest {

  @Mock private CalendarService calendarService;
  @Mock private PageProperties pageProperties;
  @InjectMocks private CalendarController calendarController;
  @InjectMocks private TestUtil testUtil;

  @BeforeEach
  public void init() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void handleCreateCalendarTest()
      throws CalendarDomainException, DateException, CommonServiceException {
    when(calendarService.processCreateCalendar(any())).thenReturn(testUtil.getCalendarResponse());

    ResponseEntity<BaseResponse<CalendarResponse>> resp =
        calendarController.handleCreateCalendar(testUtil.getCalendarRequest());

    Assertions.assertEquals(HttpStatus.OK, resp.getStatusCode());
    Assertions.assertEquals(
        TestUtil.CALENDAR_ID, Objects.requireNonNull(resp.getBody()).getPayload().getCalendarId());
    verify(calendarService, times(1)).processCreateCalendar(any());
  }

  @Test
  void handleCreateCalendarExceptionTest()
      throws CalendarDomainException, DateException, CommonServiceException {

    when(calendarService.processCreateCalendar(any())).thenThrow(new NullPointerException("error"));

    Exception ex =
        Assertions.assertThrows(
            Exception.class,
            () -> calendarController.handleCreateCalendar(testUtil.getCalendarRequest()));

    Assertions.assertEquals("error", ex.getMessage());
    verify(calendarService, times(1)).processCreateCalendar(any());
  }

  @Test
  void handleGetCalendarTest() throws CalendarDomainException, CommonServiceException {
    when(calendarService.processGetCalendar(any(), any()))
        .thenReturn(testUtil.getCalendarResponse());

    ResponseEntity<BaseResponse<CalendarResponse>> resp =
        calendarController.handleGetCalendar(TestUtil.ORG_ID, TestUtil.CALENDAR_ID);

    Assertions.assertEquals(HttpStatus.OK, resp.getStatusCode());
    Assertions.assertEquals(
        TestUtil.CALENDAR_ID, Objects.requireNonNull(resp.getBody()).getPayload().getCalendarId());
    verify(calendarService, times(1)).processGetCalendar(any(), any());
  }

  @Test
  void handleGetCalendarExceptionTest() throws CalendarDomainException, CommonServiceException {

    when(calendarService.processGetCalendar(any(), any()))
        .thenThrow(new NullPointerException("error"));

    Exception ex =
        Assertions.assertThrows(
            Exception.class,
            () -> calendarController.handleGetCalendar(TestUtil.ORG_ID, TestUtil.CALENDAR_ID));

    Assertions.assertEquals("error", ex.getMessage());
    verify(calendarService, times(1)).processGetCalendar(any(), any());
  }

  @Test
  void handleGetUpcomingDaysCalendarStatusTest()
      throws CalendarDomainException, CommonServiceException, CalenderServiceException {
    when(calendarService.processGetUpcomingDaysCalendarStatus(any()))
        .thenReturn(testUtil.getCalendarDaysStatusInfoList());

    ResponseEntity<BaseResponse<List<CalendarDaysStatusInfo>>> resp =
        calendarController.handleGetUpcomingDaysCalendarStatus(
            TestUtil.ORG_ID,
            Optional.of(TestUtil.NODE_ID),
            Optional.empty(),
            Optional.empty(),
            Optional.empty(),
            Optional.empty(),
            Optional.empty());

    Assertions.assertEquals(HttpStatus.OK, resp.getStatusCode());
    Assertions.assertEquals(
        Boolean.TRUE, Objects.requireNonNull(resp.getBody()).getPayload().get(0).getIsActive());
    verify(calendarService, times(1)).processGetUpcomingDaysCalendarStatus(any());
  }

  @Test
  void handleGetUpcomingDaysCalendarStatusExceptionTest()
      throws CalendarDomainException, CommonServiceException, CalenderServiceException {

    when(calendarService.processGetUpcomingDaysCalendarStatus(any()))
        .thenThrow(new NullPointerException("error"));

    Exception ex =
        Assertions.assertThrows(
            Exception.class,
            () ->
                calendarController.handleGetUpcomingDaysCalendarStatus(
                    TestUtil.ORG_ID,
                    Optional.of(TestUtil.NODE_ID),
                    Optional.empty(),
                    Optional.empty(),
                    Optional.empty(),
                    Optional.empty(),
                    Optional.empty()));

    Assertions.assertEquals("error", ex.getMessage());
    verify(calendarService, times(1)).processGetUpcomingDaysCalendarStatus(any());
  }

  @Test
  void getCalendarListWithPaginationTest() throws CommonServiceException, CalendarDomainException {
    List<CalendarResponse> calendarResponseList =
        List.of(testUtil.getCalendarResponse(), testUtil.getCalendarResponse1());

    when(calendarService.getCalendarList(any(), any(), any(), any(), any()))
        .thenReturn(
            testUtil.getCalendarPageResponses(
                2, calendarResponseList, calendarResponseList.size(), TestUtil.SORT_ORDER_DESC));

    ResponseEntity<BaseResponse<PagePayload<CalendarResponse>>> response =
        calendarController.getCalendarListWithPagination(
            TestUtil.ORG_ID,
            testUtil.getPageParams(
                Optional.of(2),
                Optional.of(1),
                Optional.of(TestUtil.SORT_BY),
                Optional.of(TestUtil.SORT_ORDER_DESC)));

    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode(), "Success response");
    Assertions.assertEquals(
        2,
        (int) response.getBody().getPayload().getPagination().getTotalPages(),
        "Pagination Total pages");
    Assertions.assertEquals(
        calendarResponseList.size(),
        (int) response.getBody().getPayload().getPagination().getTotalRecords(),
        "Total Elements");
    Assertions.assertEquals(
        2,
        (int) response.getBody().getPayload().getPagination().getCurrentPage(),
        "Current page number");
    Assertions.assertEquals(
        calendarResponseList.size(),
        response.getBody().getPayload().getData().size(),
        "Paginated data");

    verify(calendarService, VerificationModeFactory.times(1))
        .getCalendarList(any(), any(), any(), any(), any());
  }
}
