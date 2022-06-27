package com.hbc.pe.masterdata.calendar.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hbc.calendar.domain.CalendarDaysStatusInfo;
import com.hbc.pe.masterdata.calendar.domain.CalendarDomain;
import com.hbc.pe.masterdata.calendar.domain.NodeCalendarDomain;
import com.hbc.pe.masterdata.calendar.domain.entity.CarrierServiceCalendarEntity;
import com.hbc.pe.masterdata.calendar.domain.entity.NodeCalendarEntity;
import com.hbc.pe.masterdata.calendar.domain.entity.NodeCarrierServiceCalendarEntity;
import com.hbc.pe.masterdata.calendar.domain.outbound.CalendarResponse;
import com.hbc.pe.masterdata.calendar.exception.CalendarDomainException;
import com.hbc.pe.masterdata.calendar.exception.CommonServiceException;
import com.hbc.pe.masterdata.calendar.util.DateUtil;
import com.hbc.pe.masterdata.calendar.util.TestUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

class CalendarServiceTest {

  @Mock private CalendarDomain calendarDomain;
  @Mock private NodeCalendarDomain nodeCalendarDomain;
  @Mock private CarrierServiceCalendarService carrierServiceCalendarService;
  @Mock private NodeCarrierServiceCalendarService nodeCarrierServiceCalendarService;
  @InjectMocks private CalendarService calendarService;
  @InjectMocks private TestUtil testUtil;

  @BeforeEach
  public void init() {
    MockitoAnnotations.openMocks(this);
    ReflectionTestUtils.setField(calendarService, "defaultNumberOfDaysInFuture", 10);
  }

  @Test
  void processCreateCalendarTest() throws CalendarDomainException {
    when(calendarDomain.saveCalendarEntity(any())).thenReturn(testUtil.getCalendarEntity());

    CalendarResponse resp = calendarService.processCreateCalendar(testUtil.getCalendarRequest());

    Assertions.assertEquals(TestUtil.CALENDAR_ID, Objects.requireNonNull(resp.getCalendarId()));
    Assertions.assertEquals(TestUtil.ORG_ID, Objects.requireNonNull(resp.getOrgId()));
    Assertions.assertEquals(TestUtil.DESCRIPTION, Objects.requireNonNull(resp.getDescription()));
    Assertions.assertEquals(Boolean.TRUE, Objects.requireNonNull(resp.getIsMondayWorking()));
    Assertions.assertEquals(TestUtil.EXCEPTION_DATE, resp.getExceptionDays().get(0).getDate());
    verify(calendarDomain, times(1)).saveCalendarEntity(any());
  }

  @Test
  void processGetCalendarTest() throws CalendarDomainException {
    when(calendarDomain.getCalendar(any(), any())).thenReturn(testUtil.getCalendarEntity());

    CalendarResponse resp =
        calendarService.processGetCalendar(TestUtil.ORG_ID, TestUtil.CALENDAR_ID);

    Assertions.assertEquals(TestUtil.CALENDAR_ID, Objects.requireNonNull(resp.getCalendarId()));
    Assertions.assertEquals(TestUtil.ORG_ID, Objects.requireNonNull(resp.getOrgId()));
    Assertions.assertEquals(TestUtil.DESCRIPTION, Objects.requireNonNull(resp.getDescription()));
    Assertions.assertEquals(Boolean.TRUE, Objects.requireNonNull(resp.getIsMondayWorking()));
    Assertions.assertEquals(TestUtil.EXCEPTION_DATE, resp.getExceptionDays().get(0).getDate());
    verify(calendarDomain, times(1)).getCalendar(any(), any());
  }

  @Test
  void processGetUpcomingDaysNodeCalendarStatusTest()
      throws CalendarDomainException, CommonServiceException {
    NodeCalendarEntity entity = testUtil.getNodeCalendarEntity();
    entity.setEffectiveDate(DateUtil.addDaysToCurrentDate(5, "UTC"));
    when(nodeCalendarDomain.getNodeCalendar(any(), any()))
        .thenReturn(Arrays.asList(testUtil.getNodeCalendarEntity(), entity));
    when(calendarDomain.getCalendar(any(), any())).thenReturn(testUtil.getCalendarEntity());

    List<CalendarDaysStatusInfo> resp =
        calendarService.processGetUpcomingDaysCalendarStatus(
            TestUtil.ORG_ID,
            Optional.of(TestUtil.NODE_ID),
            Optional.empty(),
            Optional.empty(),
            Optional.empty(),
            Optional.empty(),
            Optional.empty());

    Assertions.assertEquals(10, resp.size());
    verify(nodeCalendarDomain, times(1)).getNodeCalendar(any(), any());
    verify(calendarDomain, times(2)).getCalendar(any(), any());
  }

  @Test
  void processGetUpcomingDaysNodeCalendarStatusExceptionTest() throws CalendarDomainException {
    when(nodeCalendarDomain.getNodeCalendar(any(), any())).thenReturn(new ArrayList<>());

    CommonServiceException cse =
        Assertions.assertThrows(
            CommonServiceException.class,
            () ->
                calendarService.processGetUpcomingDaysCalendarStatus(
                    TestUtil.ORG_ID,
                    Optional.of(TestUtil.NODE_ID),
                    Optional.empty(),
                    Optional.empty(),
                    Optional.empty(),
                    Optional.empty(),
                    Optional.empty()));

    Assertions.assertEquals("No active calendar associated to the node", cse.getMessage());
    verify(nodeCalendarDomain, times(1)).getNodeCalendar(any(), any());
    verify(calendarDomain, times(0)).getCalendar(any(), any());
  }

  @Test
  void processGetUpcomingDaysCalendarStatusExceptionTest()
      throws CalendarDomainException, CommonServiceException {

    CommonServiceException cse =
        Assertions.assertThrows(
            CommonServiceException.class,
            () ->
                calendarService.processGetUpcomingDaysCalendarStatus(
                    TestUtil.ORG_ID,
                    Optional.empty(),
                    Optional.empty(),
                    Optional.empty(),
                    Optional.empty(),
                    Optional.empty(),
                    Optional.empty()));

    Assertions.assertEquals("Either nodeId or carrierServiceId must pe provided", cse.getMessage());
    verify(nodeCalendarDomain, times(0)).getNodeCalendar(any(), any());
    verify(calendarDomain, times(0)).getCalendar(any(), any());
  }

  @Test
  void processGetUpcomingDaysCarrierServiceCalendarStatusTest()
      throws CalendarDomainException, CommonServiceException {
    CarrierServiceCalendarEntity entity = testUtil.getCarrierServiceCalendarEntity();
    entity.setEffectiveDate(DateUtil.addDaysToCurrentDate(5, "UTC"));
    when(carrierServiceCalendarService.getAndFilterCarrierServiceCalendar(
            any(), any(), any(), any()))
        .thenReturn(Arrays.asList(testUtil.getCarrierServiceCalendarEntity(), entity));
    when(calendarDomain.getCalendar(any(), any())).thenReturn(testUtil.getCalendarEntity());

    List<CalendarDaysStatusInfo> resp =
        calendarService.processGetUpcomingDaysCalendarStatus(
            TestUtil.ORG_ID,
            Optional.empty(),
            Optional.of(TestUtil.CARRIER_SERVICE_ID),
            Optional.empty(),
            Optional.empty(),
            Optional.empty(),
            Optional.empty());

    Assertions.assertEquals(10, resp.size());
    verify(carrierServiceCalendarService, times(1))
        .getAndFilterCarrierServiceCalendar(any(), any(), any(), any());
    verify(calendarDomain, times(2)).getCalendar(any(), any());
  }

  @Test
  void processGetUpcomingDaysCarrierServiceCalendarStatusExceptionTest()
      throws CalendarDomainException {
    when(carrierServiceCalendarService.getAndFilterCarrierServiceCalendar(
            any(), any(), any(), any()))
        .thenReturn(new ArrayList<>());

    CommonServiceException cse =
        Assertions.assertThrows(
            CommonServiceException.class,
            () ->
                calendarService.processGetUpcomingDaysCalendarStatus(
                    TestUtil.ORG_ID,
                    Optional.empty(),
                    Optional.of(TestUtil.CARRIER_SERVICE_ID),
                    Optional.empty(),
                    Optional.empty(),
                    Optional.empty(),
                    Optional.empty()));

    Assertions.assertEquals(
        "No active calendar associated to the carrier & service", cse.getMessage());
    verify(carrierServiceCalendarService, times(1))
        .getAndFilterCarrierServiceCalendar(any(), any(), any(), any());
    verify(calendarDomain, times(0)).getCalendar(any(), any());
  }

  @Test
  void processGetUpcomingDaysNodeCarrierServiceCalendarStatusTest()
      throws CalendarDomainException, CommonServiceException {
    NodeCarrierServiceCalendarEntity entity = testUtil.getNodeCarrierServiceCalendarEntity();
    entity.setEffectiveDate(DateUtil.addDaysToCurrentDate(5, "UTC"));
    when(nodeCarrierServiceCalendarService.getAndFilterNodeCarrierServiceCalendar(
            any(), any(), any(), any()))
        .thenReturn(Arrays.asList(testUtil.getNodeCarrierServiceCalendarEntity(), entity));
    when(calendarDomain.getCalendar(any(), any())).thenReturn(testUtil.getCalendarEntity());

    List<CalendarDaysStatusInfo> resp =
        calendarService.processGetUpcomingDaysCalendarStatus(
            TestUtil.ORG_ID,
            Optional.of(TestUtil.NODE_ID),
            Optional.of(TestUtil.CARRIER_SERVICE_ID),
            Optional.empty(),
            Optional.empty(),
            Optional.empty(),
            Optional.empty());

    Assertions.assertEquals(10, resp.size());
    verify(nodeCarrierServiceCalendarService, times(1))
        .getAndFilterNodeCarrierServiceCalendar(any(), any(), any(), any());
    verify(calendarDomain, times(2)).getCalendar(any(), any());
  }

  @Test
  void processGetUpcomingDaysNodeCarrierServiceCalendarStatusExceptionTest()
      throws CalendarDomainException {
    when(nodeCarrierServiceCalendarService.getAndFilterNodeCarrierServiceCalendar(
            any(), any(), any(), any()))
        .thenReturn(new ArrayList<>());

    CommonServiceException cse =
        Assertions.assertThrows(
            CommonServiceException.class,
            () ->
                calendarService.processGetUpcomingDaysCalendarStatus(
                    TestUtil.ORG_ID,
                    Optional.of(TestUtil.NODE_ID),
                    Optional.of(TestUtil.CARRIER_SERVICE_ID),
                    Optional.empty(),
                    Optional.empty(),
                    Optional.empty(),
                    Optional.empty()));

    Assertions.assertEquals(
        "No active calendar associated to the node, carrier & service", cse.getMessage());
    verify(nodeCarrierServiceCalendarService, times(1))
        .getAndFilterNodeCarrierServiceCalendar(any(), any(), any(), any());
    verify(calendarDomain, times(0)).getCalendar(any(), any());
  }
}
