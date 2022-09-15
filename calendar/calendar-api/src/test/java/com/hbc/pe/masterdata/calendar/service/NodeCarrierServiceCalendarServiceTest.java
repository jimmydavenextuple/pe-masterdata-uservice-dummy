package com.hbc.pe.masterdata.calendar.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hbc.calendar.domain.dto.NodeCarrierCalendarCacheKeyDto;
import com.hbc.calendar.domain.outbound.NodeCarrierServiceCalendarResponse;
import com.hbc.common.exception.CommonServiceException;
import com.hbc.pe.masterdata.calendar.domain.CalendarDomain;
import com.hbc.pe.masterdata.calendar.domain.NodeCarrierServiceCalendarDomain;
import com.hbc.pe.masterdata.calendar.domain.entity.NodeCarrierServiceCalendarEntity;
import com.hbc.pe.masterdata.calendar.exception.CalendarDomainException;
import com.hbc.pe.masterdata.calendar.exception.DateException;
import com.hbc.pe.masterdata.calendar.util.DateValidation;
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

class NodeCarrierServiceCalendarServiceTest {

  @Mock private NodeCarrierServiceCalendarDomain nodeCarrierServiceCalendarDomain;
  @Mock private CalendarDomain calendarDomain;
  @Mock private DateValidation dateValidation;
  @InjectMocks private NodeCarrierServiceCalendarService nodeCarrierServiceCalendarService;
  @InjectMocks private TestUtil testUtil;

  @BeforeEach
  public void init() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void processCreateNodeCarrierServiceCalendarTest()
      throws CalendarDomainException, DateException, CommonServiceException {
    when(nodeCarrierServiceCalendarDomain.saveNodeCarrierServiceCalendarEntity(any()))
        .thenReturn(testUtil.getNodeCarrierServiceCalendarEntity());
    when(dateValidation.validateDate(any())).thenReturn(Boolean.TRUE);
    when(calendarDomain.getCalendar(any(), any())).thenReturn(testUtil.getCalendarEntity());
    NodeCarrierServiceCalendarResponse resp =
        nodeCarrierServiceCalendarService.processCreateNodeCarrierServiceCalendarResponse(
            testUtil.getNodeCarrierServiceCalendarRequest());

    Assertions.assertEquals(TestUtil.CALENDAR_ID, Objects.requireNonNull(resp.getCalendarId()));
    Assertions.assertEquals(TestUtil.ORG_ID, Objects.requireNonNull(resp.getOrgId()));
    Assertions.assertEquals(
        TestUtil.CARRIER_SERVICE_ID, Objects.requireNonNull(resp.getCarrierServiceId()));
    Assertions.assertEquals(TestUtil.NODE_ID, Objects.requireNonNull(resp.getNodeId()));
    Assertions.assertEquals(
        TestUtil.EFFECTIVE_DATE, Objects.requireNonNull(resp.getEffectiveDate()));
    Assertions.assertEquals(TestUtil.DESCRIPTION, Objects.requireNonNull(resp.getDescription()));
    verify(nodeCarrierServiceCalendarDomain, times(1)).saveNodeCarrierServiceCalendarEntity(any());
  }

  @Test
  void processCreateNodeCarrierServiceCalendarWithInvalidDateTest()
      throws CalendarDomainException, DateException {
    when(dateValidation.validateDate(any())).thenReturn(Boolean.FALSE);
    Exception exception =
        Assertions.assertThrows(
            DateException.class,
            () ->
                nodeCarrierServiceCalendarService.processCreateNodeCarrierServiceCalendarResponse(
                    testUtil.getNodeCarrierServiceCalendarRequest()));
    Assertions.assertEquals("Invalid Date", exception.getMessage());
  }

  @Test
  void processValidateCalendarIdTest() throws CalendarDomainException, CommonServiceException {
    when(calendarDomain.getCalendar(any(), any())).thenReturn(testUtil.getCalendarEntity());

    nodeCarrierServiceCalendarService.validateCalendarId(TestUtil.CALENDAR_ID, TestUtil.ORG_ID);

    verify(calendarDomain, times(1)).getCalendar(any(), any());
  }

  @Test
  void processValidateCalendarIdTestException() throws CalendarDomainException {
    when(calendarDomain.getCalendar(any(), any())).thenReturn(null);
    Exception exception =
        Assertions.assertThrows(
            CommonServiceException.class,
            () ->
                nodeCarrierServiceCalendarService.validateCalendarId(
                    TestUtil.CALENDAR_ID, TestUtil.ORG_ID));
    Assertions.assertEquals(
        "Cannot create a node carrier service calendar as calendarId/orgId is invalid",
        exception.getMessage());
    verify(calendarDomain, times(1)).getCalendar(any(), any());
  }

  @Test
  void processGetNodeCarrierServiceCalendarWithServiceOptionTest() throws CalendarDomainException {
    when(nodeCarrierServiceCalendarDomain.getNodeCarrierServiceCalendar(any(), any(), any(), any()))
        .thenReturn(List.of(testUtil.getNodeCarrierServiceCalendarEntity()));

    List<NodeCarrierServiceCalendarResponse> resp =
        nodeCarrierServiceCalendarService.processGetNodeCarrierServiceCalendar(
            TestUtil.ORG_ID,
            TestUtil.NODE_ID,
            TestUtil.CARRIER_SERVICE_ID,
            Optional.of(TestUtil.SERVICE_OPTION));

    Assertions.assertEquals(
        TestUtil.CALENDAR_ID, Objects.requireNonNull(resp.get(0).getCalendarId()));
    Assertions.assertEquals(TestUtil.ORG_ID, Objects.requireNonNull(resp.get(0).getOrgId()));
    Assertions.assertEquals(
        TestUtil.CARRIER_SERVICE_ID, Objects.requireNonNull(resp.get(0).getCarrierServiceId()));
    Assertions.assertEquals(TestUtil.NODE_ID, Objects.requireNonNull(resp.get(0).getNodeId()));
    Assertions.assertEquals(
        TestUtil.EFFECTIVE_DATE, Objects.requireNonNull(resp.get(0).getEffectiveDate()));
    Assertions.assertEquals(
        TestUtil.DESCRIPTION, Objects.requireNonNull(resp.get(0).getDescription()));
    verify(nodeCarrierServiceCalendarDomain, times(1))
        .getNodeCarrierServiceCalendar(any(), any(), any(), any());
  }

  @Test
  void processGetNodeCarrierServiceCalendarTest() throws CalendarDomainException {
    when(nodeCarrierServiceCalendarDomain.getNodeCarrierServiceCalendar(any(), any(), any()))
        .thenReturn(List.of(testUtil.getNodeCarrierServiceCalendarEntity()));

    List<NodeCarrierServiceCalendarResponse> resp =
        nodeCarrierServiceCalendarService.processGetNodeCarrierServiceCalendar(
            TestUtil.ORG_ID, TestUtil.NODE_ID, TestUtil.CARRIER_SERVICE_ID, Optional.empty());

    Assertions.assertEquals(
        TestUtil.CALENDAR_ID, Objects.requireNonNull(resp.get(0).getCalendarId()));
    Assertions.assertEquals(TestUtil.ORG_ID, Objects.requireNonNull(resp.get(0).getOrgId()));
    Assertions.assertEquals(
        TestUtil.CARRIER_SERVICE_ID, Objects.requireNonNull(resp.get(0).getCarrierServiceId()));
    Assertions.assertEquals(TestUtil.NODE_ID, Objects.requireNonNull(resp.get(0).getNodeId()));
    Assertions.assertEquals(
        TestUtil.EFFECTIVE_DATE, Objects.requireNonNull(resp.get(0).getEffectiveDate()));
    Assertions.assertEquals(
        TestUtil.DESCRIPTION, Objects.requireNonNull(resp.get(0).getDescription()));
    verify(nodeCarrierServiceCalendarDomain, times(1))
        .getNodeCarrierServiceCalendar(any(), any(), any());
  }

  @Test
  void getAllNodeCarrierCalendarCacheKeysTest() throws CalendarDomainException {
    List<NodeCarrierServiceCalendarEntity> nodeCarrierServiceCalendarEntities =
        testUtil.getNodeCarrierServiceCalendarEntityList();

    when(nodeCarrierServiceCalendarDomain.getAllNodeCarrierServiceCalendars(any()))
        .thenReturn(nodeCarrierServiceCalendarEntities);

    List<NodeCarrierCalendarCacheKeyDto> response =
        nodeCarrierServiceCalendarService.getAllNodeCarrierCalendarCacheKeys(2);

    Assertions.assertEquals(2, response.size());
    Assertions.assertEquals(
        nodeCarrierServiceCalendarEntities.get(0).getCarrierServiceId(),
        response.get(0).getCarrierServiceId());
    verify(nodeCarrierServiceCalendarDomain, times(1)).getAllNodeCarrierServiceCalendars(any());
  }
}
