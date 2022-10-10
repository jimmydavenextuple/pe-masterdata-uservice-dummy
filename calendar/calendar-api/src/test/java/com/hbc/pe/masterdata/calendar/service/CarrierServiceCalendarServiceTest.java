package com.hbc.pe.masterdata.calendar.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hbc.calendar.domain.dto.CarrierCalendarCacheKeyDto;
import com.hbc.calendar.domain.inbound.CarrierServiceCalendarRequest;
import com.hbc.calendar.domain.outbound.CarrierServiceCalendarResponse;
import com.hbc.common.exception.CommonServiceException;
import com.hbc.pe.masterdata.calendar.domain.CalendarDomain;
import com.hbc.pe.masterdata.calendar.domain.CarrierServiceCalendarDomain;
import com.hbc.pe.masterdata.calendar.domain.entity.CarrierServiceCalendarEntity;
import com.hbc.pe.masterdata.calendar.domain.repository.CarrierServiceCalendarRepository;
import com.hbc.pe.masterdata.calendar.exception.CalendarDomainException;
import com.hbc.pe.masterdata.calendar.exception.CalenderServiceException;
import com.hbc.pe.masterdata.calendar.exception.DateException;
import com.hbc.pe.masterdata.calendar.util.DateValidation;
import com.hbc.pe.masterdata.calendar.util.TestUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class CarrierServiceCalendarServiceTest {

  @Mock private CarrierServiceCalendarDomain carrierServiceCalendarDomain;
  @Mock private CalendarDomain calendarDomain;
  @Mock private CarrierServiceCalendarRepository carrierServiceCalendarRepository;
  @Mock private DateValidation dateValidation;
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
    when(dateValidation.validateDate(any())).thenReturn(Boolean.TRUE);
    when(calendarDomain.getCalendar(any(), any())).thenReturn(testUtil.getCalendarEntity());
    when(carrierServiceCalendarRepository
            .findByCalendarIdAndOrgIdAndCarrierServiceIdAndShippingStageAndEffectiveDate(
                any(), any(), any(), any(), any()))
        .thenReturn(Optional.empty());
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
  @DisplayName("When node calendar to be created already exists")
  void createNodeCalendarTestException() throws CalendarDomainException, CommonServiceException {
    CarrierServiceCalendarRequest carrierServiceCalendarRequest =
        testUtil.getCarrierServiceCalendarRequest();
    when(calendarDomain.getCalendar(any(), any())).thenReturn(testUtil.getCalendarEntity());
    when(dateValidation.validateDate(any())).thenReturn(Boolean.TRUE);
    when(carrierServiceCalendarRepository
            .findByCalendarIdAndOrgIdAndCarrierServiceIdAndShippingStageAndEffectiveDate(
                any(), any(), any(), any(), any()))
        .thenReturn(Optional.of(testUtil.getCarrierServiceCalendarEntity()));

    Exception ex =
        Assertions.assertThrows(
            CommonServiceException.class,
            () ->
                carrierServiceCalendarService.processCreateCarrierServiceCalendar(
                    carrierServiceCalendarRequest));

    Assertions.assertEquals(
        "Carrier Service Calendar already exists for the given details", ex.getMessage());
    verify(carrierServiceCalendarRepository, times(1))
        .findByCalendarIdAndOrgIdAndCarrierServiceIdAndShippingStageAndEffectiveDate(
            any(), any(), any(), any(), any());
    verify(carrierServiceCalendarDomain, times(0)).saveCarrierServiceCalendarEntity(any());
  }

  @Test
  void processCreateCarrierServiceCalendarWithInvalidDateTest()
      throws CalendarDomainException, DateException {
    when(dateValidation.validateDate(any())).thenReturn(Boolean.FALSE);
    Exception exception =
        Assertions.assertThrows(
            DateException.class,
            () ->
                carrierServiceCalendarService.processCreateCarrierServiceCalendar(
                    testUtil.getCarrierServiceCalendarRequest()));
    Assertions.assertEquals("Invalid Date", exception.getMessage());
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
  void processGetCarrierServiceCalendarWithServiceOptionTest()
      throws CalendarDomainException, CalenderServiceException {
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
  void processGetCarrierServiceCalendarWithServiceOptionTest_Exception()
      throws CalendarDomainException, CalenderServiceException {
    when(carrierServiceCalendarDomain.getCarrierServiceCalendar(any(), any(), any(), any()))
        .thenReturn(new ArrayList<>());

    Assertions.assertThrows(
        CalenderServiceException.class,
        () ->
            carrierServiceCalendarService.processGetCarrierServiceCalendar(
                TestUtil.ORG_ID,
                TestUtil.CARRIER_SERVICE_ID,
                Optional.of(TestUtil.SERVICE_OPTION),
                Optional.empty()));
  }

  @Test
  void processGetCarrierServiceCalendarTest()
      throws CalendarDomainException, CalenderServiceException {
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

  @Test
  void processGetCarrierServiceCalendarTest_Exception()
      throws CalendarDomainException, CalenderServiceException {
    when(carrierServiceCalendarDomain.getCarrierServiceCalendar(any(), any(), any()))
        .thenReturn(new ArrayList<>());

    Assertions.assertThrows(
        CalenderServiceException.class,
        () ->
            carrierServiceCalendarService.processGetCarrierServiceCalendar(
                TestUtil.ORG_ID, TestUtil.CARRIER_SERVICE_ID, Optional.empty(), Optional.empty()));
    verify(carrierServiceCalendarDomain, times(1)).getCarrierServiceCalendar(any(), any(), any());
  }

  @Test
  void getAllCarrierCalendarCacheKeysTest() throws CalendarDomainException {
    List<CarrierServiceCalendarEntity> carrierServiceCalendarEntities =
        testUtil.getCarrierServiceCalendarEntityList();

    when(carrierServiceCalendarDomain.getAllCarrierServiceCalendars(any()))
        .thenReturn(carrierServiceCalendarEntities);

    List<CarrierCalendarCacheKeyDto> response =
        carrierServiceCalendarService.getAllCarrierCalendarCacheKeys(2);

    Assertions.assertEquals(2, response.size());
    Assertions.assertEquals(
        carrierServiceCalendarEntities.get(0).getCarrierServiceId(),
        response.get(0).getCarrierServiceId());
    verify(carrierServiceCalendarDomain, times(1)).getAllCarrierServiceCalendars(any());
  }

  @Test
  void getCarrierServiceAssociationWithCalendarTest() throws CalendarDomainException {
    when(carrierServiceCalendarDomain.getCarrierServiceCalendarByOrgIdAndCalendarId(any(), any()))
        .thenReturn(
            List.of(
                testUtil.getCarrierServiceCalendarEntity(),
                testUtil.getCarrierServiceCalendarEntity1()));

    List<CarrierServiceCalendarResponse> response =
        carrierServiceCalendarService.getCarrierServiceAssociationWithCalendar(
            TestUtil.CALENDAR_ID, TestUtil.ORG_ID);

    Assertions.assertEquals(2, response.size());
    Assertions.assertEquals(
        TestUtil.CALENDAR_ID, Objects.requireNonNull(response.get(0).getCalendarId()));
    Assertions.assertEquals(TestUtil.ORG_ID, Objects.requireNonNull(response.get(0).getOrgId()));
    verify(carrierServiceCalendarDomain, times(1))
        .getCarrierServiceCalendarByOrgIdAndCalendarId(any(), any());
  }
}
