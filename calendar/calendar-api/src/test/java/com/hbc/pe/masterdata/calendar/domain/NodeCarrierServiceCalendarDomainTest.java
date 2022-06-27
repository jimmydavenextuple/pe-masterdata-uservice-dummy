package com.hbc.pe.masterdata.calendar.domain;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hbc.pe.masterdata.calendar.domain.entity.NodeCarrierServiceCalendarEntity;
import com.hbc.pe.masterdata.calendar.domain.repository.NodeCarrierServiceCalendarRepository;
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

class NodeCarrierServiceCalendarDomainTest {

  @Mock private NodeCarrierServiceCalendarRepository nodeCarrierServiceCalendarRepository;
  @InjectMocks private NodeCarrierServiceCalendarDomain nodeCarrierServiceCalendarDomain;
  @InjectMocks private TestUtil testUtil;

  @BeforeEach
  public void init() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void saveCalendarEntityTest() throws CalendarDomainException {
    when(nodeCarrierServiceCalendarRepository.save(any()))
        .thenReturn(testUtil.getNodeCarrierServiceCalendarEntity());

    NodeCarrierServiceCalendarEntity resp =
        nodeCarrierServiceCalendarDomain.saveNodeCarrierServiceCalendarEntity(
            testUtil.getNodeCarrierServiceCalendarEntity());

    Assertions.assertEquals(TestUtil.CALENDAR_ID, Objects.requireNonNull(resp.getCalendarId()));
    Assertions.assertEquals(TestUtil.ORG_ID, Objects.requireNonNull(resp.getOrgId()));
    Assertions.assertEquals(TestUtil.DESCRIPTION, Objects.requireNonNull(resp.getDescription()));
    Assertions.assertEquals(
        TestUtil.CARRIER_SERVICE_ID, Objects.requireNonNull(resp.getCarrierServiceId()));
    Assertions.assertEquals(TestUtil.NODE_ID, Objects.requireNonNull(resp.getNodeId()));
    Assertions.assertEquals(
        TestUtil.EFFECTIVE_DATE, Objects.requireNonNull(resp.getEffectiveDate()));
    verify(nodeCarrierServiceCalendarRepository, times(1)).save(any());
  }

  @Test
  void saveCalendarEntityExceptionTest() {
    when(nodeCarrierServiceCalendarRepository.save(any())).thenThrow(new RuntimeException("error"));

    CalendarDomainException ex =
        Assertions.assertThrows(
            CalendarDomainException.class,
            () ->
                nodeCarrierServiceCalendarDomain.saveNodeCarrierServiceCalendarEntity(
                    testUtil.getNodeCarrierServiceCalendarEntity()));

    Assertions.assertEquals("Unable to create node carrier service calendar", ex.getMessage());
    Assertions.assertEquals(TestUtil.CALENDAR_ID, ex.getCalendarId());
    verify(nodeCarrierServiceCalendarRepository, times(1)).save(any());
  }

  @Test
  void getNodeCarrierServiceCalendarWithServiceOptionTest() throws CalendarDomainException {
    when(nodeCarrierServiceCalendarRepository.findNodeCarrierServiceCalendar(
            any(), any(), any(), any()))
        .thenReturn(List.of(testUtil.getNodeCarrierServiceCalendarEntity()));

    List<NodeCarrierServiceCalendarEntity> resp =
        nodeCarrierServiceCalendarDomain.getNodeCarrierServiceCalendar(
            TestUtil.ORG_ID,
            TestUtil.NODE_ID,
            TestUtil.CARRIER_SERVICE_ID,
            TestUtil.SERVICE_OPTION);

    Assertions.assertEquals(
        TestUtil.CALENDAR_ID, Objects.requireNonNull(resp.get(0).getCalendarId()));
    Assertions.assertEquals(TestUtil.ORG_ID, Objects.requireNonNull(resp.get(0).getOrgId()));
    Assertions.assertEquals(TestUtil.NODE_ID, Objects.requireNonNull(resp.get(0).getNodeId()));
    verify(nodeCarrierServiceCalendarRepository, times(1))
        .findNodeCarrierServiceCalendar(any(), any(), any(), any());
  }

  @Test
  void getNodeCarrierServiceCalendarWithServiceOptionExceptionTest() {
    when(nodeCarrierServiceCalendarRepository.findNodeCarrierServiceCalendar(
            any(), any(), any(), any()))
        .thenThrow(new RuntimeException("error"));

    CalendarDomainException ex =
        Assertions.assertThrows(
            CalendarDomainException.class,
            () ->
                nodeCarrierServiceCalendarDomain.getNodeCarrierServiceCalendar(
                    TestUtil.ORG_ID,
                    TestUtil.NODE_ID,
                    TestUtil.CARRIER_SERVICE_ID,
                    TestUtil.SERVICE_OPTION));

    Assertions.assertEquals("Unable to fetch node carrier service calendar", ex.getMessage());
    Assertions.assertEquals(TestUtil.NODE_ID, ex.getNodeId());
    Assertions.assertEquals(TestUtil.CARRIER_SERVICE_ID, ex.getCarrierServiceId());
    verify(nodeCarrierServiceCalendarRepository, times(1))
        .findNodeCarrierServiceCalendar(any(), any(), any(), any());
  }

  @Test
  void getNodeCarrierServiceCalendarTest() throws CalendarDomainException {
    when(nodeCarrierServiceCalendarRepository.findAllNodeCarrierServiceCalendar(
            any(), any(), any()))
        .thenReturn(List.of(testUtil.getNodeCarrierServiceCalendarEntity()));

    List<NodeCarrierServiceCalendarEntity> resp =
        nodeCarrierServiceCalendarDomain.getNodeCarrierServiceCalendar(
            TestUtil.ORG_ID, TestUtil.NODE_ID, TestUtil.CARRIER_SERVICE_ID);

    Assertions.assertEquals(
        TestUtil.CALENDAR_ID, Objects.requireNonNull(resp.get(0).getCalendarId()));
    Assertions.assertEquals(TestUtil.ORG_ID, Objects.requireNonNull(resp.get(0).getOrgId()));
    Assertions.assertEquals(TestUtil.NODE_ID, Objects.requireNonNull(resp.get(0).getNodeId()));
    verify(nodeCarrierServiceCalendarRepository, times(1))
        .findAllNodeCarrierServiceCalendar(any(), any(), any());
  }

  @Test
  void getNodeCarrierServiceCalendarExceptionTest() {
    when(nodeCarrierServiceCalendarRepository.findAllNodeCarrierServiceCalendar(
            any(), any(), any()))
        .thenThrow(new RuntimeException("error"));

    CalendarDomainException ex =
        Assertions.assertThrows(
            CalendarDomainException.class,
            () ->
                nodeCarrierServiceCalendarDomain.getNodeCarrierServiceCalendar(
                    TestUtil.ORG_ID, TestUtil.NODE_ID, TestUtil.CARRIER_SERVICE_ID));

    Assertions.assertEquals("Unable to fetch node carrier service calendar", ex.getMessage());
    Assertions.assertEquals(TestUtil.NODE_ID, ex.getNodeId());
    Assertions.assertEquals(TestUtil.CARRIER_SERVICE_ID, ex.getCarrierServiceId());
    verify(nodeCarrierServiceCalendarRepository, times(1))
        .findAllNodeCarrierServiceCalendar(any(), any(), any());
  }
}
