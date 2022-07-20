package com.hbc.pe.masterdata.calendar.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hbc.calendar.domain.outbound.CarrierServiceCalendarResponse;
import com.hbc.common.exception.CommonServiceException;
import com.hbc.common.response.BaseResponse;
import com.hbc.pe.masterdata.calendar.exception.CalendarDomainException;
import com.hbc.pe.masterdata.calendar.exception.DateException;
import com.hbc.pe.masterdata.calendar.service.CarrierServiceCalendarService;
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

class CarrierServiceCalendarControllerTest {

  @Mock private CarrierServiceCalendarService carrierServiceCalendarService;
  @InjectMocks private CarrierServiceCalendarController carrierServiceCalendarController;
  @InjectMocks private TestUtil testUtil;

  @BeforeEach
  public void init() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void handleCreateCarrierServiceCalendarTest()
      throws CalendarDomainException, DateException, CommonServiceException {
    when(carrierServiceCalendarService.processCreateCarrierServiceCalendar(any()))
        .thenReturn(testUtil.getCarrierServiceCalendarResponse());

    ResponseEntity<BaseResponse<CarrierServiceCalendarResponse>> resp =
        carrierServiceCalendarController.handleCreateCarrierServiceCalendar(
            testUtil.getCarrierServiceCalendarRequest());

    Assertions.assertEquals(HttpStatus.OK, resp.getStatusCode());
    Assertions.assertEquals(
        TestUtil.CALENDAR_ID, Objects.requireNonNull(resp.getBody()).getPayload().getCalendarId());
    verify(carrierServiceCalendarService, times(1)).processCreateCarrierServiceCalendar(any());
  }

  @Test
  void handleCreateCarrierServiceCalendarExceptionTest()
      throws CalendarDomainException, CommonServiceException, DateException {
    when(carrierServiceCalendarService.processCreateCarrierServiceCalendar(any()))
        .thenThrow(new NullPointerException("error"));

    Exception ex =
        Assertions.assertThrows(
            Exception.class,
            () ->
                carrierServiceCalendarController.handleCreateCarrierServiceCalendar(
                    testUtil.getCarrierServiceCalendarRequest()));

    Assertions.assertEquals("error", ex.getMessage());
    verify(carrierServiceCalendarService, times(1)).processCreateCarrierServiceCalendar(any());
  }

  @Test
  void handleGetCarrierServiceCalendarTest() throws CalendarDomainException {
    when(carrierServiceCalendarService.processGetCarrierServiceCalendar(any(), any(), any(), any()))
        .thenReturn(List.of(testUtil.getCarrierServiceCalendarResponse()));

    ResponseEntity<BaseResponse<List<CarrierServiceCalendarResponse>>> resp =
        carrierServiceCalendarController.handleGetCarrierServiceCalendar(
            TestUtil.ORG_ID, TestUtil.CARRIER_SERVICE_ID, Optional.empty(), Optional.empty());

    Assertions.assertEquals(HttpStatus.OK, resp.getStatusCode());
    Assertions.assertEquals(
        TestUtil.CALENDAR_ID,
        Objects.requireNonNull(resp.getBody()).getPayload().get(0).getCalendarId());
    verify(carrierServiceCalendarService, times(1))
        .processGetCarrierServiceCalendar(any(), any(), any(), any());
  }

  @Test
  void handleGetCarrierServiceCalendarExceptionTest() throws CalendarDomainException {

    when(carrierServiceCalendarService.processGetCarrierServiceCalendar(any(), any(), any(), any()))
        .thenThrow(new NullPointerException("error"));

    Exception ex =
        Assertions.assertThrows(
            Exception.class,
            () ->
                carrierServiceCalendarController.handleGetCarrierServiceCalendar(
                    TestUtil.ORG_ID,
                    TestUtil.CARRIER_SERVICE_ID,
                    Optional.empty(),
                    Optional.empty()));

    Assertions.assertEquals("error", ex.getMessage());
    verify(carrierServiceCalendarService, times(1))
        .processGetCarrierServiceCalendar(any(), any(), any(), any());
  }
}
