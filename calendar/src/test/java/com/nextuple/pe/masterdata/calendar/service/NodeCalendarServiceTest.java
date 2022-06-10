package com.nextuple.pe.masterdata.calendar.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nextuple.pe.masterdata.calendar.domain.NodeCalendarDomain;
import com.nextuple.pe.masterdata.calendar.domain.outbound.NodeCalendarResponse;
import com.nextuple.pe.masterdata.calendar.exception.CalendarDomainException;
import com.nextuple.pe.masterdata.calendar.util.TestUtil;
import java.util.List;
import java.util.Objects;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class NodeCalendarServiceTest {

  @Mock private NodeCalendarDomain nodeCalendarDomain;
  @InjectMocks private NodeCalendarService nodeCalendarService;
  @InjectMocks private TestUtil testUtil;

  @BeforeEach
  public void init() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void processCreateNodeCalendarTest() throws CalendarDomainException {
    when(nodeCalendarDomain.saveNodeCalendarEntity(any()))
        .thenReturn(testUtil.getNodeCalendarEntity());

    NodeCalendarResponse resp =
        nodeCalendarService.processCreateNodeCalendar(testUtil.getNodeCalendarRequest());

    Assertions.assertEquals(TestUtil.CALENDAR_ID, Objects.requireNonNull(resp.getCalendarId()));
    Assertions.assertEquals(TestUtil.ORG_ID, Objects.requireNonNull(resp.getOrgId()));
    Assertions.assertEquals(TestUtil.NODE_ID, Objects.requireNonNull(resp.getNodeId()));
    Assertions.assertEquals(
        TestUtil.EFFECTIVE_DATE, Objects.requireNonNull(resp.getEffectiveDate()));
    Assertions.assertEquals(TestUtil.DESCRIPTION, Objects.requireNonNull(resp.getDescription()));
    verify(nodeCalendarDomain, times(1)).saveNodeCalendarEntity(any());
  }

  @Test
  void processGetNodeCalendarTest() throws CalendarDomainException {
    when(nodeCalendarDomain.getNodeCalendar(any(), any()))
        .thenReturn(List.of(testUtil.getNodeCalendarEntity()));

    List<NodeCalendarResponse> resp =
        nodeCalendarService.processGetNodeCalendar(TestUtil.ORG_ID, TestUtil.NODE_ID);

    Assertions.assertEquals(
        TestUtil.CALENDAR_ID, Objects.requireNonNull(resp.get(0).getCalendarId()));
    Assertions.assertEquals(TestUtil.ORG_ID, Objects.requireNonNull(resp.get(0).getOrgId()));
    Assertions.assertEquals(TestUtil.NODE_ID, Objects.requireNonNull(resp.get(0).getNodeId()));
    Assertions.assertEquals(
        TestUtil.EFFECTIVE_DATE, Objects.requireNonNull(resp.get(0).getEffectiveDate()));
    Assertions.assertEquals(
        TestUtil.DESCRIPTION, Objects.requireNonNull(resp.get(0).getDescription()));
    verify(nodeCalendarDomain, times(1)).getNodeCalendar(any(), any());
  }
}
