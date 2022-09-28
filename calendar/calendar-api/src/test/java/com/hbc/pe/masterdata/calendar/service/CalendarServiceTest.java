package com.hbc.pe.masterdata.calendar.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hbc.calendar.domain.CalendarDaysStatusInfo;
import com.hbc.calendar.domain.inbound.CalendarRequest;
import com.hbc.calendar.domain.outbound.CalendarResponse;
import com.hbc.common.exception.CommonServiceException;
import com.hbc.pe.masterdata.calendar.domain.CalendarDomain;
import com.hbc.pe.masterdata.calendar.domain.NodeCalendarDomain;
import com.hbc.pe.masterdata.calendar.domain.entity.CalendarEntity;
import com.hbc.pe.masterdata.calendar.domain.entity.CarrierServiceCalendarEntity;
import com.hbc.pe.masterdata.calendar.domain.entity.NodeCalendarEntity;
import com.hbc.pe.masterdata.calendar.domain.entity.NodeCarrierServiceCalendarEntity;
import com.hbc.pe.masterdata.calendar.domain.repository.CalendarRepository;
import com.hbc.pe.masterdata.calendar.exception.CalendarDomainException;
import com.hbc.pe.masterdata.calendar.exception.CalenderServiceException;
import com.hbc.pe.masterdata.calendar.exception.DateException;
import com.hbc.pe.masterdata.calendar.util.DateUtil;
import com.hbc.pe.masterdata.calendar.util.DateValidation;
import com.hbc.pe.masterdata.calendar.util.TestUtil;
import java.util.ArrayList;
import java.util.Arrays;
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
import org.springframework.test.util.ReflectionTestUtils;

class CalendarServiceTest {

  @Mock private CalendarDomain calendarDomain;
  @Mock private NodeCalendarDomain nodeCalendarDomain;
  @Mock private CalendarRepository calendarRepository;
  @Mock private CarrierServiceCalendarService carrierServiceCalendarService;
  @Mock private NodeCarrierServiceCalendarService nodeCarrierServiceCalendarService;
  @Mock private DateValidation dateValidation;
  @InjectMocks private CalendarService calendarService;
  @InjectMocks private TestUtil testUtil;

  @BeforeEach
  public void init() {
    MockitoAnnotations.openMocks(this);
    ReflectionTestUtils.setField(calendarService, "defaultNumberOfDaysInFuture", 10);
  }

  @Test
  void processCreateCalendarTest()
      throws CalendarDomainException, DateException, CommonServiceException {
    when(calendarDomain.saveCalendarEntity(any())).thenReturn(testUtil.getCalendarEntity());
    when(dateValidation.validateExceptionDays(any())).thenReturn(Boolean.TRUE);
    when(calendarRepository.findCalendarDetailsByCalendarIdAndOrgId(any(), any()))
        .thenReturn(Optional.empty());
    CalendarResponse resp = calendarService.processCreateCalendar(testUtil.getCalendarRequest());

    Assertions.assertEquals(TestUtil.CALENDAR_ID, Objects.requireNonNull(resp.getCalendarId()));
    Assertions.assertEquals(TestUtil.ORG_ID, Objects.requireNonNull(resp.getOrgId()));
    Assertions.assertEquals(TestUtil.DESCRIPTION, Objects.requireNonNull(resp.getDescription()));
    Assertions.assertEquals(Boolean.TRUE, Objects.requireNonNull(resp.getIsMondayWorking()));
    Assertions.assertEquals(TestUtil.EXCEPTION_DATE, resp.getExceptionDays().get(0).getDate());
    verify(calendarDomain, times(1)).saveCalendarEntity(any());
  }

  @Test
  void processCreateCalendarWithInvalidDateTest() throws CalendarDomainException, DateException {
    when(dateValidation.validateExceptionDays(any())).thenReturn(Boolean.FALSE);
    Exception exception =
        Assertions.assertThrows(
            DateException.class,
            () -> calendarService.processCreateCalendar(testUtil.getCalendarRequest()));
    Assertions.assertEquals("Date is invalid / missing", exception.getMessage());
  }

  @Test
  @DisplayName("When calendar to be created already exists")
  void createCalendarTestException() throws CalendarDomainException {
    CalendarRequest calendarRequest = testUtil.getCalendarRequest();
    when(dateValidation.validateExceptionDays(any())).thenReturn(Boolean.TRUE);
    when(calendarRepository.findCalendarDetailsByCalendarIdAndOrgId(any(), any()))
        .thenReturn(Optional.of(testUtil.getCalendarEntity()));

    Exception ex =
        Assertions.assertThrows(
            CommonServiceException.class,
            () -> calendarService.processCreateCalendar(calendarRequest));

    Assertions.assertEquals("Calendar already exists for the given details", ex.getMessage());
    verify(calendarRepository, times(1)).findCalendarDetailsByCalendarIdAndOrgId(any(), any());
    verify(calendarDomain, times(0)).saveCalendarEntity(any());
  }

  @Test
  void processGetCalendarTest() throws CalendarDomainException, CommonServiceException {
    when(calendarDomain.getCalendar(any(), any())).thenReturn(testUtil.getCalendarEntity());

    CalendarResponse resp =
        calendarService.processGetCalendar(TestUtil.ORG_ID, TestUtil.CALENDAR_ID);

    Assertions.assertEquals(TestUtil.CALENDAR_ID, Objects.requireNonNull(resp.getCalendarId()));
    Assertions.assertEquals(TestUtil.ORG_ID, Objects.requireNonNull(resp.getOrgId()));
    Assertions.assertEquals(TestUtil.DESCRIPTION, Objects.requireNonNull(resp.getDescription()));
    Assertions.assertEquals(Boolean.TRUE, Objects.requireNonNull(resp.getIsMondayWorking()));
    Assertions.assertEquals(TestUtil.EXCEPTION_DATE, resp.getExceptionDays().get(0).getDate());
    verify(calendarDomain, times(2)).getCalendar(any(), any());
  }

  @Test
  void processValidateCalendarIdTest() throws CalendarDomainException, CommonServiceException {
    when(calendarDomain.getCalendar(any(), any())).thenReturn(testUtil.getCalendarEntity());

    calendarService.validateCalendarId(TestUtil.CALENDAR_ID, TestUtil.ORG_ID);

    verify(calendarDomain, times(1)).getCalendar(any(), any());
  }

  @Test
  void processValidateCalendarIdTestException()
      throws CalendarDomainException, CommonServiceException {
    when(calendarDomain.getCalendar(any(), any())).thenReturn(null);
    Exception exception =
        Assertions.assertThrows(
            CommonServiceException.class,
            () -> calendarService.validateCalendarId(TestUtil.CALENDAR_ID, TestUtil.ORG_ID));
    Assertions.assertEquals("Calendar does not exists", exception.getMessage());
    verify(calendarDomain, times(1)).getCalendar(any(), any());
  }

  @Test
  void processGetUpcomingDaysNodeCalendarStatusTest()
      throws CalendarDomainException, CommonServiceException, CalenderServiceException {
    NodeCalendarEntity entity = testUtil.getNodeCalendarEntity();
    entity.setEffectiveDate(DateUtil.addDaysToCurrentDate(5));
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
            Optional.empty());

    Assertions.assertEquals(11, resp.size());
    verify(nodeCalendarDomain, times(1)).getNodeCalendar(any(), any());
    verify(calendarDomain, times(2)).getCalendar(any(), any());
  }

  @Test
  void processGetUpcomingDaysTestWithoutExceptionDays()
      throws CalendarDomainException, CommonServiceException, CalenderServiceException {
    NodeCalendarEntity entity = testUtil.getNodeCalendarEntity();
    entity.setEffectiveDate(DateUtil.addDaysToCurrentDate(4));
    when(nodeCalendarDomain.getNodeCalendar(any(), any()))
        .thenReturn(Arrays.asList(testUtil.getNodeCalendarEntity(), entity));
    CalendarEntity calendarEntity = testUtil.getCalendarEntity();
    calendarEntity.setExceptionDays(null);
    when(calendarDomain.getCalendar(any(), any())).thenReturn(calendarEntity);

    List<CalendarDaysStatusInfo> resp =
        calendarService.processGetUpcomingDaysCalendarStatus(
            TestUtil.ORG_ID,
            Optional.of(TestUtil.NODE_ID),
            Optional.empty(),
            Optional.empty(),
            Optional.empty(),
            Optional.empty());

    Assertions.assertEquals(11, resp.size());
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
                    Optional.empty()));

    Assertions.assertEquals("Either nodeId or carrierServiceId must pe provided", cse.getMessage());
    verify(nodeCalendarDomain, times(0)).getNodeCalendar(any(), any());
    verify(calendarDomain, times(0)).getCalendar(any(), any());
  }

  @Test
  void processGetUpcomingDaysCarrierServiceCalendarStatusTest()
      throws CalendarDomainException, CommonServiceException, CalenderServiceException {
    CarrierServiceCalendarEntity entity = testUtil.getCarrierServiceCalendarEntity();
    entity.setEffectiveDate(DateUtil.addDaysToCurrentDate(5));
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
            Optional.empty());

    Assertions.assertEquals(11, resp.size());
    verify(carrierServiceCalendarService, times(1))
        .getAndFilterCarrierServiceCalendar(any(), any(), any(), any());
    verify(calendarDomain, times(2)).getCalendar(any(), any());
  }

  @Test
  void processGetUpcomingDaysCarrierServiceCalendarStatusExceptionTest()
      throws CalendarDomainException, CalenderServiceException {
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
                    Optional.empty()));

    Assertions.assertEquals(
        "No active calendar associated to the carrier & service", cse.getMessage());
    verify(carrierServiceCalendarService, times(1))
        .getAndFilterCarrierServiceCalendar(any(), any(), any(), any());
    verify(calendarDomain, times(0)).getCalendar(any(), any());
  }

  @Test
  void processGetUpcomingDaysNodeCarrierServiceCalendarStatusTest()
      throws CalendarDomainException, CommonServiceException, CalenderServiceException {
    NodeCarrierServiceCalendarEntity entity = testUtil.getNodeCarrierServiceCalendarEntity();
    entity.setEffectiveDate(DateUtil.addDaysToCurrentDate(5));
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
            Optional.empty());

    Assertions.assertEquals(11, resp.size());
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
                    Optional.empty()));

    Assertions.assertEquals(
        "No active calendar associated to the node, carrier & service", cse.getMessage());
    verify(nodeCarrierServiceCalendarService, times(1))
        .getAndFilterNodeCarrierServiceCalendar(any(), any(), any(), any());
    verify(calendarDomain, times(0)).getCalendar(any(), any());
  }
}
