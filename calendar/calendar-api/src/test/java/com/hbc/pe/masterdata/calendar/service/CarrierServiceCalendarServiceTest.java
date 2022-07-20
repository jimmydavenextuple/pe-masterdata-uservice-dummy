package com.hbc.pe.masterdata.calendar.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hbc.calendar.domain.outbound.CarrierServiceCalendarResponse;
import com.hbc.common.exception.CommonServiceException;
import com.hbc.pe.masterdata.calendar.domain.CalendarDomain;
import com.hbc.pe.masterdata.calendar.domain.CarrierServiceCalendarDomain;
import com.hbc.pe.masterdata.calendar.exception.CalendarDomainException;
import com.hbc.pe.masterdata.calendar.exception.DateException;
import com.hbc.pe.masterdata.calendar.util.TestUtil;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class CarrierServiceCalendarServiceTest {

  @Mock private CarrierServiceCalendarDomain carrierServiceCalendarDomain;
  @Mock private CalendarDomain calendarDomain;
  @InjectMocks private CarrierServiceCalendarService carrierServiceCalendarService;
  @InjectMocks private TestUtil testUtil;

  @BeforeEach
  public void init() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void processCreateCarrierServiceCalendarTest()
      throws CalendarDomainException, DateException, CommonServiceException {
    when(carrierServiceCalendarDomain.saveCarrierServiceCalendarEntity(any()))
        .thenReturn(testUtil.getCarrierServiceCalendarEntity());
    when(calendarDomain.getCalendar(any(), any())).thenReturn(testUtil.getCalendarEntity());
    CarrierServiceCalendarResponse resp =
        carrierServiceCalendarService.processCreateCarrierServiceCalendar(
            testUtil.getCarrierServiceCalendarRequest());

    Assertions.assertEquals(TestUtil.CALENDAR_ID, Objects.requireNonNull(resp.getCalendarId()));
    Assertions.assertEquals(TestUtil.ORG_ID, Objects.requireNonNull(resp.getOrgId()));
    Assertions.assertEquals(
        TestUtil.CARRIER_SERVICE_ID, Objects.requireNonNull(resp.getCarrierServiceId()));
    Assertions.assertEquals(
        TestUtil.SHIPPING_STAGE, Objects.requireNonNull(resp.getShippingStage()));
    Assertions.assertEquals(
        TestUtil.EFFECTIVE_DATE, Objects.requireNonNull(resp.getEffectiveDate()));
    Assertions.assertEquals(TestUtil.DESCRIPTION, Objects.requireNonNull(resp.getDescription()));
    verify(carrierServiceCalendarDomain, times(1)).saveCarrierServiceCalendarEntity(any());
  }

  @Test
  void processValidateCalendarIdTest() throws CalendarDomainException, CommonServiceException {
    when(calendarDomain.getCalendar(any(), any())).thenReturn(testUtil.getCalendarEntity());

    carrierServiceCalendarService.validateCalendarId(TestUtil.CALENDAR_ID, TestUtil.ORG_ID);

    verify(calendarDomain, times(1)).getCalendar(any(), any());
  }

  @Test
  void processValidateCalendarIdTestException() throws CalendarDomainException {
    when(calendarDomain.getCalendar(any(), any())).thenReturn(null);
    Exception exception =
        Assertions.assertThrows(
            CommonServiceException.class,
            () ->
                carrierServiceCalendarService.validateCalendarId(
                    TestUtil.CALENDAR_ID, TestUtil.ORG_ID));
    Assertions.assertEquals(
        "Cannot create a carrier service calendar as calendarId/orgId is invalid",
        exception.getMessage());
    verify(calendarDomain, times(1)).getCalendar(any(), any());
  }

  @Test
  void processGetCarrierServiceCalendarWithServiceOptionTest() throws CalendarDomainException {
    when(carrierServiceCalendarDomain.getCarrierServiceCalendar(any(), any(), any(), any()))
        .thenReturn(Collections.singletonList(testUtil.getCarrierServiceCalendarEntity()));

    List<CarrierServiceCalendarResponse> resp =
        carrierServiceCalendarService.processGetCarrierServiceCalendar(
            TestUtil.ORG_ID,
            TestUtil.CARRIER_SERVICE_ID,
            Optional.of(TestUtil.SERVICE_OPTION),
            Optional.empty());

    Assertions.assertEquals(
        TestUtil.CALENDAR_ID, Objects.requireNonNull(resp.get(0).getCalendarId()));
    Assertions.assertEquals(TestUtil.ORG_ID, Objects.requireNonNull(resp.get(0).getOrgId()));
    Assertions.assertEquals(
        TestUtil.CARRIER_SERVICE_ID, Objects.requireNonNull(resp.get(0).getCarrierServiceId()));
    Assertions.assertEquals(
        TestUtil.SHIPPING_STAGE, Objects.requireNonNull(resp.get(0).getShippingStage()));
    Assertions.assertEquals(
        TestUtil.EFFECTIVE_DATE, Objects.requireNonNull(resp.get(0).getEffectiveDate()));
    Assertions.assertEquals(
        TestUtil.DESCRIPTION, Objects.requireNonNull(resp.get(0).getDescription()));
    verify(carrierServiceCalendarDomain, times(1))
        .getCarrierServiceCalendar(any(), any(), any(), any());
  }

  @Test
  void processGetCarrierServiceCalendarTest() throws CalendarDomainException {
    when(carrierServiceCalendarDomain.getCarrierServiceCalendar(any(), any(), any()))
        .thenReturn(Collections.singletonList(testUtil.getCarrierServiceCalendarEntity()));

    List<CarrierServiceCalendarResponse> resp =
        carrierServiceCalendarService.processGetCarrierServiceCalendar(
            TestUtil.ORG_ID, TestUtil.CARRIER_SERVICE_ID, Optional.empty(), Optional.empty());

    Assertions.assertEquals(
        TestUtil.CALENDAR_ID, Objects.requireNonNull(resp.get(0).getCalendarId()));
    Assertions.assertEquals(TestUtil.ORG_ID, Objects.requireNonNull(resp.get(0).getOrgId()));
    Assertions.assertEquals(
        TestUtil.CARRIER_SERVICE_ID, Objects.requireNonNull(resp.get(0).getCarrierServiceId()));
    Assertions.assertEquals(
        TestUtil.SHIPPING_STAGE, Objects.requireNonNull(resp.get(0).getShippingStage()));
    Assertions.assertEquals(
        TestUtil.EFFECTIVE_DATE, Objects.requireNonNull(resp.get(0).getEffectiveDate()));
    Assertions.assertEquals(
        TestUtil.DESCRIPTION, Objects.requireNonNull(resp.get(0).getDescription()));
    verify(carrierServiceCalendarDomain, times(1)).getCarrierServiceCalendar(any(), any(), any());
  }
}
