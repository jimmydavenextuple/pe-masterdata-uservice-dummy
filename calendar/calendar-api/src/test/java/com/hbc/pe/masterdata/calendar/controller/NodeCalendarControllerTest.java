package com.hbc.pe.masterdata.calendar.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hbc.common.response.BaseResponse;
import com.hbc.pe.masterdata.calendar.domain.outbound.NodeCalendarResponse;
import com.hbc.pe.masterdata.calendar.exception.CalendarDomainException;
import com.hbc.pe.masterdata.calendar.service.NodeCalendarService;
import com.hbc.pe.masterdata.calendar.util.TestUtil;
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
  void handleCreateNodeCalendarTest() throws CalendarDomainException {
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
  void handleCreateNodeCalendarExceptionTest() throws CalendarDomainException {

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
}
