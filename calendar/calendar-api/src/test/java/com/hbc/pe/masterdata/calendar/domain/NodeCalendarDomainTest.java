package com.hbc.pe.masterdata.calendar.domain;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hbc.pe.masterdata.calendar.domain.entity.NodeCalendarEntity;
import com.hbc.pe.masterdata.calendar.domain.repository.NodeCalendarRepository;
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

class NodeCalendarDomainTest {

  @Mock private NodeCalendarRepository nodeCalendarRepository;
  @InjectMocks private NodeCalendarDomain nodeCalendarDomain;
  @InjectMocks private TestUtil testUtil;

  @BeforeEach
  public void init() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void saveNodeCalendarEntityTest() throws CalendarDomainException {
    when(nodeCalendarRepository.save(any())).thenReturn(testUtil.getNodeCalendarEntity());

    NodeCalendarEntity resp =
        nodeCalendarDomain.saveNodeCalendarEntity(testUtil.getNodeCalendarEntity());

    Assertions.assertEquals(TestUtil.CALENDAR_ID, Objects.requireNonNull(resp.getCalendarId()));
    Assertions.assertEquals(TestUtil.ORG_ID, Objects.requireNonNull(resp.getOrgId()));
    Assertions.assertEquals(TestUtil.DESCRIPTION, Objects.requireNonNull(resp.getDescription()));
    Assertions.assertEquals(TestUtil.NODE_ID, Objects.requireNonNull(resp.getNodeId()));
    Assertions.assertEquals(
        TestUtil.EFFECTIVE_DATE, Objects.requireNonNull(resp.getEffectiveDate()));
    verify(nodeCalendarRepository, times(1)).save(any());
  }

  @Test
  void saveNodeCalendarEntityExceptionTest() {
    when(nodeCalendarRepository.save(any())).thenThrow(new RuntimeException("error"));

    CalendarDomainException ex =
        Assertions.assertThrows(
            CalendarDomainException.class,
            () -> nodeCalendarDomain.saveNodeCalendarEntity(testUtil.getNodeCalendarEntity()));

    Assertions.assertEquals("Unable to create node calendar", ex.getMessage());
    Assertions.assertEquals(TestUtil.CALENDAR_ID, ex.getCalendarId());
    verify(nodeCalendarRepository, times(1)).save(any());
  }

  @Test
  void getNodeCalendarTest() throws CalendarDomainException {
    when(nodeCalendarRepository.findByOrgIdAndNodeId(any(), any()))
        .thenReturn(List.of(testUtil.getNodeCalendarEntity1(), testUtil.getNodeCalendarEntity()));

    List<NodeCalendarEntity> resp =
        nodeCalendarDomain.getNodeCalendar(TestUtil.ORG_ID, TestUtil.NODE_ID);

    Assertions.assertEquals(
        TestUtil.CALENDAR_ID, Objects.requireNonNull(resp.get(0).getCalendarId()));
    Assertions.assertEquals(TestUtil.ORG_ID, Objects.requireNonNull(resp.get(0).getOrgId()));
    Assertions.assertEquals(
        TestUtil.DESCRIPTION, Objects.requireNonNull(resp.get(0).getDescription()));
    Assertions.assertEquals(TestUtil.NODE_ID, Objects.requireNonNull(resp.get(0).getNodeId()));
    Assertions.assertEquals(
        TestUtil.EFFECTIVE_DATE_2, Objects.requireNonNull(resp.get(0).getEffectiveDate()));
    Assertions.assertEquals(
        TestUtil.EFFECTIVE_DATE, Objects.requireNonNull(resp.get(1).getEffectiveDate()));
    verify(nodeCalendarRepository, times(1)).findByOrgIdAndNodeId(any(), any());
  }

  @Test
  void getNodeCalendarExceptionTest() {
    when(nodeCalendarRepository.findByOrgIdAndNodeId(any(), any()))
        .thenThrow(new RuntimeException("error"));

    CalendarDomainException ex =
        Assertions.assertThrows(
            CalendarDomainException.class,
            () -> nodeCalendarDomain.getNodeCalendar(TestUtil.ORG_ID, TestUtil.NODE_ID));

    Assertions.assertEquals("Unable to fetch node calendar", ex.getMessage());
    Assertions.assertEquals(TestUtil.NODE_ID, ex.getNodeId());
    verify(nodeCalendarRepository, times(1)).findByOrgIdAndNodeId(any(), any());
  }

  @Test
  void getAllNodeCalendarTest() throws CalendarDomainException {
    List<NodeCalendarEntity> nodeCalendarEntityList = testUtil.getNodeCalendarEntityList();

    when(nodeCalendarRepository.findAllNodeCalendarByLimit(any()))
        .thenReturn(nodeCalendarEntityList);

    List<NodeCalendarEntity> response = nodeCalendarDomain.getAllNodeCalendar(2);

    Assertions.assertEquals(2, response.size());
    Assertions.assertEquals(
        nodeCalendarEntityList.get(0).getCalendarId(), response.get(0).getCalendarId());
    verify(nodeCalendarRepository, times(1)).findAllNodeCalendarByLimit(any());
  }

  @Test
  void getAllNodeCalendarExceptionTest() {
    when(nodeCalendarRepository.findAllNodeCalendarByLimit(any()))
        .thenThrow(new RuntimeException("Unable to fetch all node calendars"));

    CalendarDomainException ex =
        Assertions.assertThrows(
            CalendarDomainException.class, () -> nodeCalendarDomain.getAllNodeCalendar(2));

    Assertions.assertEquals("Unable to fetch all node calendars", ex.getMessage());
    verify(nodeCalendarRepository, times(1)).findAllNodeCalendarByLimit(any());
  }

  @Test
  void getNodeServiceCalendarByOrgIdAndCalendarIdTest() throws CalendarDomainException {
    when(nodeCalendarRepository.findNodeCalendarByCalendarIdAndOrgId(any(), any()))
        .thenReturn(List.of(testUtil.getNodeCalendarEntity(), testUtil.getNodeCalendarEntity1()));

    List<NodeCalendarEntity> response =
        nodeCalendarDomain.getNodeServiceCalendarByOrgIdAndCalendarId(
            TestUtil.ORG_ID, TestUtil.CARRIER_SERVICE_ID);

    Assertions.assertEquals(2, response.size());
    Assertions.assertEquals(
        TestUtil.CALENDAR_ID, Objects.requireNonNull(response.get(0).getCalendarId()));
    Assertions.assertEquals(TestUtil.ORG_ID, Objects.requireNonNull(response.get(0).getOrgId()));
    Assertions.assertEquals(
        TestUtil.EFFECTIVE_DATE, Objects.requireNonNull(response.get(0).getEffectiveDate()));
    verify(nodeCalendarRepository, times(1)).findNodeCalendarByCalendarIdAndOrgId(any(), any());
  }

  @Test
  void getNodeServiceCalendarByOrgIdAndCalendarIdExceptionTest() {
    when(nodeCalendarRepository.findNodeCalendarByCalendarIdAndOrgId(any(), any()))
        .thenThrow(new RuntimeException("Unable to fetch node calendar list"));

    CalendarDomainException ex =
        Assertions.assertThrows(
            CalendarDomainException.class,
            () ->
                nodeCalendarDomain.getNodeServiceCalendarByOrgIdAndCalendarId(
                    TestUtil.CALENDAR_ID, TestUtil.ORG_ID));

    Assertions.assertEquals("Unable to fetch node calendar list", ex.getMessage());
    verify(nodeCalendarRepository, times(1)).findNodeCalendarByCalendarIdAndOrgId(any(), any());
  }
}
