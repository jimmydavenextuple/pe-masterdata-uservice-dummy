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

import com.nextuple.calendar.domain.dto.NodeCalendarCacheKeyDto;
import com.nextuple.calendar.domain.outbound.NodeCalendarResponse;
import com.nextuple.calendar.persistence.exception.CalendarDomainException;
import com.nextuple.calendar.persistence.exception.DateException;
import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.pe.masterdata.calendar.service.NodeCalendarService;
import com.nextuple.pe.masterdata.calendar.util.TestUtil;
import java.util.List;
import java.util.Objects;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class NodeCalendarControllerTest {

  @Mock private NodeCalendarService nodeCalendarService;
  @InjectMocks private NodeCalendarController nodeCalendarController;
  @InjectMocks private TestUtil testUtil;

  @BeforeEach
  public void init() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void handleCreateNodeCalendarTest()
      throws CalendarDomainException, DateException, CommonServiceException {
    when(nodeCalendarService.processCreateNodeCalendar(any()))
        .thenReturn(testUtil.getNodeCalendarResponse());

    ResponseEntity<BaseResponse<NodeCalendarResponse>> resp =
        nodeCalendarController.handleCreateNodeCalendar(testUtil.getNodeCalendarRequest());

    Assertions.assertEquals(HttpStatus.OK, resp.getStatusCode());
    Assertions.assertEquals(
        TestUtil.CALENDAR_ID, Objects.requireNonNull(resp.getBody()).getPayload().getCalendarId());
    verify(nodeCalendarService, times(1)).processCreateNodeCalendar(any());
  }

  @Test
  void handleCreateNodeCalendarExceptionTest()
      throws CalendarDomainException, CommonServiceException, DateException {
    when(nodeCalendarService.processCreateNodeCalendar(any()))
        .thenThrow(new NullPointerException("error"));

    Exception ex =
        Assertions.assertThrows(
            Exception.class,
            () ->
                nodeCalendarController.handleCreateNodeCalendar(testUtil.getNodeCalendarRequest()));

    Assertions.assertEquals("error", ex.getMessage());
    verify(nodeCalendarService, times(1)).processCreateNodeCalendar(any());
  }

  @Test
  void handleGetNodeCalendarTest() throws CalendarDomainException {
    when(nodeCalendarService.processGetNodeCalendar(any(), any()))
        .thenReturn(List.of(testUtil.getNodeCalendarResponse()));

    ResponseEntity<BaseResponse<List<NodeCalendarResponse>>> resp =
        nodeCalendarController.handleGetNodeCalendar(TestUtil.ORG_ID, TestUtil.NODE_ID);

    Assertions.assertEquals(HttpStatus.OK, resp.getStatusCode());
    Assertions.assertEquals(
        TestUtil.CALENDAR_ID,
        Objects.requireNonNull(resp.getBody()).getPayload().get(0).getCalendarId());
    verify(nodeCalendarService, times(1)).processGetNodeCalendar(any(), any());
  }

  @Test
  void handleGetNodeCalendarExceptionTest() throws CalendarDomainException {

    when(nodeCalendarService.processGetNodeCalendar(any(), any()))
        .thenThrow(new NullPointerException("error"));

    Exception ex =
        Assertions.assertThrows(
            Exception.class,
            () -> nodeCalendarController.handleGetNodeCalendar(TestUtil.ORG_ID, TestUtil.NODE_ID));

    Assertions.assertEquals("error", ex.getMessage());
    verify(nodeCalendarService, times(1)).processGetNodeCalendar(any(), any());
  }

  @Test
  void getNodeCalendarCacheKeysTest() throws CalendarDomainException {
    List<NodeCalendarCacheKeyDto> nodeCalendarCacheKeyDtoList =
        testUtil.getNodeCalendarCacheKeyDtoList();

    when(nodeCalendarService.getAllNodeCalendarCacheKeys(any()))
        .thenReturn(nodeCalendarCacheKeyDtoList);

    ResponseEntity<BaseResponse<List<NodeCalendarCacheKeyDto>>> responseEntity =
        nodeCalendarController.getNodeCalendarCacheKeys(2);

    Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    Assertions.assertEquals(
        nodeCalendarCacheKeyDtoList.size(),
        Objects.requireNonNull(responseEntity.getBody()).getPayload().size());
    verify(nodeCalendarService, times(1)).getAllNodeCalendarCacheKeys(any());
  }

  @Test
  void getNodeCalendarsTest() throws CalendarDomainException {
    when(nodeCalendarService.getNodeAssociationWithCalendar(any(), any()))
        .thenReturn(List.of(testUtil.getNodeCalendarResponse()));

    ResponseEntity<BaseResponse<List<NodeCalendarResponse>>> response =
        nodeCalendarController.getNodeCalendars(TestUtil.CALENDAR_ID, TestUtil.ORG_ID);

    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    Assertions.assertEquals(
        TestUtil.CALENDAR_ID,
        Objects.requireNonNull(response.getBody()).getPayload().get(0).getCalendarId());
    Assertions.assertEquals(
        TestUtil.ORG_ID, Objects.requireNonNull(response.getBody()).getPayload().get(0).getOrgId());
    verify(nodeCalendarService, times(1)).getNodeAssociationWithCalendar(any(), any());
  }
}
