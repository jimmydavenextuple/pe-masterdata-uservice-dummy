package com.hbc.pe.masterdata.calendar.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hbc.common.exception.CommonServiceException;
import com.hbc.common.response.BaseResponse;
import com.hbc.pe.masterdata.calendar.domain.outbound.NodeCarrierServiceCalendarResponse;
import com.hbc.pe.masterdata.calendar.exception.CalendarDomainException;
import com.hbc.pe.masterdata.calendar.service.NodeCarrierServiceCalendarService;
import com.hbc.pe.masterdata.calendar.util.TestUtil;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
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
  void handleNodeCreateCarrierServiceCalendarTest() throws CalendarDomainException, CommonServiceException {
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
  void handleNodeCreateCarrierServiceCalendarExceptionTest() throws CalendarDomainException, CommonServiceException {

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
}
