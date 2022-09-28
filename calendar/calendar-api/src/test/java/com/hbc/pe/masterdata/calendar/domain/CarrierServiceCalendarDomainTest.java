package com.hbc.pe.masterdata.calendar.domain;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hbc.pe.masterdata.calendar.domain.entity.CarrierServiceCalendarEntity;
import com.hbc.pe.masterdata.calendar.domain.repository.CarrierServiceCalendarRepository;
import com.hbc.pe.masterdata.calendar.exception.CalendarDomainException;
import com.hbc.pe.masterdata.calendar.util.TestUtil;
import java.util.List;
import java.util.Objects;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class CarrierServiceCalendarDomainTest {

  @Mock private CarrierServiceCalendarRepository carrierServiceCalendarRepository;
  @InjectMocks private CarrierServiceCalendarDomain carrierServiceCalendarDomain;
  @InjectMocks private TestUtil testUtil;

  @BeforeEach
  public void init() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void saveCalendarEntityTest() throws CalendarDomainException {
    when(carrierServiceCalendarRepository.save(any()))
        .thenReturn(testUtil.getCarrierServiceCalendarEntity());

    CarrierServiceCalendarEntity resp =
        carrierServiceCalendarDomain.saveCarrierServiceCalendarEntity(
            testUtil.getCarrierServiceCalendarEntity());

    Assertions.assertEquals(TestUtil.CALENDAR_ID, Objects.requireNonNull(resp.getCalendarId()));
    Assertions.assertEquals(TestUtil.ORG_ID, Objects.requireNonNull(resp.getOrgId()));
    Assertions.assertEquals(TestUtil.DESCRIPTION, Objects.requireNonNull(resp.getDescription()));
    Assertions.assertEquals(
        TestUtil.CARRIER_SERVICE_ID, Objects.requireNonNull(resp.getCarrierServiceId()));
    Assertions.assertEquals(
        TestUtil.SHIPPING_STAGE, Objects.requireNonNull(resp.getShippingStage()));
    Assertions.assertEquals(
        TestUtil.EFFECTIVE_DATE, Objects.requireNonNull(resp.getEffectiveDate()));
    verify(carrierServiceCalendarRepository, times(1)).save(any());
  }

  @Test
  void saveCalendarEntityExceptionTest() {
    when(carrierServiceCalendarRepository.save(any())).thenThrow(new RuntimeException("error"));

    CalendarDomainException ex =
        Assertions.assertThrows(
            CalendarDomainException.class,
            () ->
                carrierServiceCalendarDomain.saveCarrierServiceCalendarEntity(
                    testUtil.getCarrierServiceCalendarEntity()));

    Assertions.assertEquals("Unable to create carrier service calendar", ex.getMessage());
    Assertions.assertEquals(TestUtil.CALENDAR_ID, ex.getCalendarId());
    verify(carrierServiceCalendarRepository, times(1)).save(any());
  }

  @Test
  void getCarrierServiceCalendarWithServiceOptionTest() throws CalendarDomainException {
    when(carrierServiceCalendarRepository.findCarrierServiceCalendar(any(), any(), any(), any()))
        .thenReturn(
            List.of(
                testUtil.getCarrierServiceCalendarEntity1(),
                testUtil.getCarrierServiceCalendarEntity()));

    List<CarrierServiceCalendarEntity> resp =
        carrierServiceCalendarDomain.getCarrierServiceCalendar(
            TestUtil.ORG_ID,
            TestUtil.CARRIER_SERVICE_ID,
            TestUtil.SERVICE_OPTION,
            TestUtil.ALL_SHIPPING_STAGE);

    Assertions.assertEquals(
        TestUtil.CALENDAR_ID, Objects.requireNonNull(resp.get(0).getCalendarId()));
    Assertions.assertEquals(TestUtil.ORG_ID, Objects.requireNonNull(resp.get(0).getOrgId()));
    Assertions.assertEquals(
        TestUtil.EFFECTIVE_DATE_2, Objects.requireNonNull(resp.get(0).getEffectiveDate()));
    Assertions.assertEquals(
        TestUtil.EFFECTIVE_DATE, Objects.requireNonNull(resp.get(1).getEffectiveDate()));
    verify(carrierServiceCalendarRepository, times(1))
        .findCarrierServiceCalendar(any(), any(), any(), any());
  }

  @Test
  void getCarrierServiceCalendarWithServiceOptionExceptionTest() {
    when(carrierServiceCalendarRepository.findCarrierServiceCalendar(any(), any(), any(), any()))
        .thenThrow(new RuntimeException("error"));

    CalendarDomainException ex =
        Assertions.assertThrows(
            CalendarDomainException.class,
            () ->
                carrierServiceCalendarDomain.getCarrierServiceCalendar(
                    TestUtil.ORG_ID,
                    TestUtil.CARRIER_SERVICE_ID,
                    TestUtil.SERVICE_OPTION,
                    TestUtil.ALL_SHIPPING_STAGE));

    Assertions.assertEquals("Unable to fetch carrier service calendar", ex.getMessage());
    Assertions.assertEquals(TestUtil.CARRIER_SERVICE_ID, ex.getCarrierServiceId());
    verify(carrierServiceCalendarRepository, times(1))
        .findCarrierServiceCalendar(any(), any(), any(), any());
  }

  @Test
  void getCarrierServiceCalendarTest() throws CalendarDomainException {
    when(carrierServiceCalendarRepository.findAllCarrierServiceCalendar(any(), any(), any()))
        .thenReturn(
            List.of(
                testUtil.getCarrierServiceCalendarEntity1(),
                testUtil.getCarrierServiceCalendarEntity()));

    List<CarrierServiceCalendarEntity> resp =
        carrierServiceCalendarDomain.getCarrierServiceCalendar(
            TestUtil.ORG_ID, TestUtil.CARRIER_SERVICE_ID, TestUtil.ALL_SHIPPING_STAGE);

    Assertions.assertEquals(
        TestUtil.CALENDAR_ID, Objects.requireNonNull(resp.get(0).getCalendarId()));
    Assertions.assertEquals(TestUtil.ORG_ID, Objects.requireNonNull(resp.get(0).getOrgId()));
    Assertions.assertEquals(
        TestUtil.EFFECTIVE_DATE_2, Objects.requireNonNull(resp.get(0).getEffectiveDate()));
    Assertions.assertEquals(
        TestUtil.EFFECTIVE_DATE, Objects.requireNonNull(resp.get(1).getEffectiveDate()));
    verify(carrierServiceCalendarRepository, times(1))
        .findAllCarrierServiceCalendar(any(), any(), any());
  }

  @Test
  void getCarrierServiceCalendarExceptionTest() {
    when(carrierServiceCalendarRepository.findAllCarrierServiceCalendar(any(), any(), any()))
        .thenThrow(new RuntimeException("error"));

    CalendarDomainException ex =
        Assertions.assertThrows(
            CalendarDomainException.class,
            () ->
                carrierServiceCalendarDomain.getCarrierServiceCalendar(
                    TestUtil.ORG_ID, TestUtil.CARRIER_SERVICE_ID, TestUtil.ALL_SHIPPING_STAGE));

    Assertions.assertEquals("Unable to fetch carrier service calendar", ex.getMessage());
    Assertions.assertEquals(TestUtil.CARRIER_SERVICE_ID, ex.getCarrierServiceId());
    verify(carrierServiceCalendarRepository, times(1))
        .findAllCarrierServiceCalendar(any(), any(), any());
  }

  @Test
  void getAllCarrierServiceCalendarsTest() throws CalendarDomainException {
    List<CarrierServiceCalendarEntity> carrierServiceCalendarEntities =
        testUtil.getCarrierServiceCalendarEntityList();

    when(carrierServiceCalendarRepository.findAllCarrierServiceCalendarsByLimit(any()))
        .thenReturn(carrierServiceCalendarEntities);

    List<CarrierServiceCalendarEntity> response =
        carrierServiceCalendarDomain.getAllCarrierServiceCalendars(2);

    Assertions.assertEquals(2, response.size());
    Assertions.assertEquals(
        carrierServiceCalendarEntities.get(0).getCalendarId(), response.get(0).getCalendarId());
    verify(carrierServiceCalendarRepository, times(1)).findAllCarrierServiceCalendarsByLimit(any());
  }

  @Test
  void getAllCarrierServiceCalendarsExceptionTest() {
    when(carrierServiceCalendarRepository.findAllCarrierServiceCalendarsByLimit(any()))
        .thenThrow(new RuntimeException("Unable to fetch all carrier calendars"));

    CalendarDomainException ex =
        Assertions.assertThrows(
            CalendarDomainException.class,
            () -> carrierServiceCalendarDomain.getAllCarrierServiceCalendars(2));

    Assertions.assertEquals("Unable to fetch all carrier calendars", ex.getMessage());
    verify(carrierServiceCalendarRepository, times(1)).findAllCarrierServiceCalendarsByLimit(any());
  }
}
