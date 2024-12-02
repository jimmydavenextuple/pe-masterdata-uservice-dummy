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

import com.nextuple.calendar.domain.dto.NodeCarrierCalendarCacheKeyDto;
import com.nextuple.calendar.domain.outbound.NodeCarrierServiceCalendarResponse;
import com.nextuple.calendar.persistence.exception.CalendarDomainException;
import com.nextuple.calendar.persistence.exception.DateException;
import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.pe.masterdata.calendar.service.NodeCarrierServiceCalendarService;
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
import org.springframework.context.annotation.Description;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class NodeCarrierServiceCalendarControllerTest {

  @Mock private NodeCarrierServiceCalendarService nodeCarrierServiceCalendarService;
  @InjectMocks private NodeCarrierServiceCalendarController nodeCarrierServiceCalendarController;
  @InjectMocks private TestUtil testUtil;

  @BeforeEach
  public void init() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void handleNodeCreateCarrierServiceCalendarTest()
      throws CalendarDomainException, CommonServiceException, DateException {
    when(nodeCarrierServiceCalendarService.processCreateNodeCarrierServiceCalendarResponse(any()))
        .thenReturn(testUtil.getNodeCarrierServiceCalendarResponse());

    ResponseEntity<BaseResponse<NodeCarrierServiceCalendarResponse>> resp =
        nodeCarrierServiceCalendarController.handleCreateNodeCarrierServiceCalendar(
            testUtil.getNodeCarrierServiceCalendarRequest());

    Assertions.assertEquals(HttpStatus.OK, resp.getStatusCode());
    Assertions.assertEquals(
        TestUtil.CALENDAR_ID, Objects.requireNonNull(resp.getBody()).getPayload().getCalendarId());
    verify(nodeCarrierServiceCalendarService, times(1))
        .processCreateNodeCarrierServiceCalendarResponse(any());
  }

  @Test
  void handleNodeCreateCarrierServiceCalendarExceptionTest()
      throws CalendarDomainException, CommonServiceException, DateException {
    when(nodeCarrierServiceCalendarService.processCreateNodeCarrierServiceCalendarResponse(any()))
        .thenThrow(new NullPointerException("error"));

    Exception ex =
        Assertions.assertThrows(
            Exception.class,
            () ->
                nodeCarrierServiceCalendarController.handleCreateNodeCarrierServiceCalendar(
                    testUtil.getNodeCarrierServiceCalendarRequest()));

    Assertions.assertEquals("error", ex.getMessage());
    verify(nodeCarrierServiceCalendarService, times(1))
        .processCreateNodeCarrierServiceCalendarResponse(any());
  }

  @Test
  void handleGetNodeCarrierServiceCalendarTest() throws CalendarDomainException {
    when(nodeCarrierServiceCalendarService.processGetNodeCarrierServiceCalendar(
            any(), any(), any(), any()))
        .thenReturn(List.of(testUtil.getNodeCarrierServiceCalendarResponse()));

    ResponseEntity<BaseResponse<List<NodeCarrierServiceCalendarResponse>>> resp =
        nodeCarrierServiceCalendarController.handleGetNodeCarrierServiceCalendar(
            TestUtil.ORG_ID, TestUtil.NODE_ID, TestUtil.CARRIER_SERVICE_ID, Optional.empty());

    Assertions.assertEquals(HttpStatus.OK, resp.getStatusCode());
    Assertions.assertEquals(
        TestUtil.CALENDAR_ID,
        Objects.requireNonNull(resp.getBody()).getPayload().get(0).getCalendarId());
    verify(nodeCarrierServiceCalendarService, times(1))
        .processGetNodeCarrierServiceCalendar(any(), any(), any(), any());
  }

  @Test
  void handleGetNodeCarrierServiceCalendarExceptionTest() throws CalendarDomainException {

    when(nodeCarrierServiceCalendarService.processGetNodeCarrierServiceCalendar(
            any(), any(), any(), any()))
        .thenThrow(new NullPointerException("error"));

    Exception ex =
        Assertions.assertThrows(
            Exception.class,
            () ->
                nodeCarrierServiceCalendarController.handleGetNodeCarrierServiceCalendar(
                    TestUtil.ORG_ID,
                    TestUtil.NODE_ID,
                    TestUtil.CARRIER_SERVICE_ID,
                    Optional.empty()));

    Assertions.assertEquals("error", ex.getMessage());
    verify(nodeCarrierServiceCalendarService, times(1))
        .processGetNodeCarrierServiceCalendar(any(), any(), any(), any());
  }

  @Test
  void getNodeCarrierCalendarCacheKeysTest() throws CalendarDomainException {
    List<NodeCarrierCalendarCacheKeyDto> nodeCarrierCalendarCacheKeyDtoList =
        testUtil.getNodeCarrierCalendarCacheKeyDtoList();

    when(nodeCarrierServiceCalendarService.getAllNodeCarrierCalendarCacheKeys(any()))
        .thenReturn(nodeCarrierCalendarCacheKeyDtoList);

    ResponseEntity<BaseResponse<List<NodeCarrierCalendarCacheKeyDto>>> responseEntity =
        nodeCarrierServiceCalendarController.getNodeCarrierCalendarCacheKeys(2);

    Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    Assertions.assertEquals(
        nodeCarrierCalendarCacheKeyDtoList.size(),
        Objects.requireNonNull(responseEntity.getBody()).getPayload().size());
    verify(nodeCarrierServiceCalendarService, times(1)).getAllNodeCarrierCalendarCacheKeys(any());
  }

  @Test
  @Description("Handle get node carrier service calendars for org and nodeId - Happy Path")
  void handleGetNodeCarrierServiceCalendarForNodeIdTest() throws CalendarDomainException {
    when(nodeCarrierServiceCalendarService.processGetNodeCarrierServiceCalendarByNodeId(
            any(), any()))
        .thenReturn(List.of(testUtil.getNodeCarrierServiceCalendarResponse()));

    ResponseEntity<BaseResponse<List<NodeCarrierServiceCalendarResponse>>> resp =
        nodeCarrierServiceCalendarController.handleGetNodeCarrierServiceCalendarForNodeId(
            TestUtil.ORG_ID, TestUtil.NODE_ID);

    Assertions.assertEquals(HttpStatus.OK, resp.getStatusCode());
    Assertions.assertEquals(
        TestUtil.CALENDAR_ID,
        Objects.requireNonNull(resp.getBody()).getPayload().get(0).getCalendarId());
    verify(nodeCarrierServiceCalendarService, times(1))
        .processGetNodeCarrierServiceCalendarByNodeId(any(), any());
  }

  @Test
  @Description("Handle get node carrier service calendars for org and nodeId - Exception Scenario")
  void handleGetNodeCarrierServiceCalendarForNodeIdTestException() throws CalendarDomainException {

    when(nodeCarrierServiceCalendarService.processGetNodeCarrierServiceCalendarByNodeId(
            any(), any()))
        .thenThrow(new NullPointerException("error"));

    Exception ex =
        Assertions.assertThrows(
            Exception.class,
            () ->
                nodeCarrierServiceCalendarController.handleGetNodeCarrierServiceCalendarForNodeId(
                    TestUtil.ORG_ID, TestUtil.NODE_ID));

    Assertions.assertEquals("error", ex.getMessage());
    verify(nodeCarrierServiceCalendarService, times(1))
        .processGetNodeCarrierServiceCalendarByNodeId(any(), any());
  }

  @Test
  @Description("Test Get all node carriers service Calendars")
  void getAllNodeCarrierServiceCalendarsTest() throws CalendarDomainException {
    when(nodeCarrierServiceCalendarService.processGetAllNodeCarrierServiceCalendar(any()))
        .thenReturn(List.of(testUtil.getNodeCarrierServiceCalendarResponse()));
    ResponseEntity<BaseResponse<List<NodeCarrierServiceCalendarResponse>>> resp =
        nodeCarrierServiceCalendarController.getAllNodeCarrierServiceCalendarsByOrgId(
            TestUtil.ORG_ID);
    Assertions.assertEquals(HttpStatus.OK, resp.getStatusCode());
    Assertions.assertEquals(
        TestUtil.CALENDAR_ID,
        Objects.requireNonNull(resp.getBody()).getPayload().get(0).getCalendarId());
    verify(nodeCarrierServiceCalendarService, times(1))
        .processGetAllNodeCarrierServiceCalendar(any());
  }
}
