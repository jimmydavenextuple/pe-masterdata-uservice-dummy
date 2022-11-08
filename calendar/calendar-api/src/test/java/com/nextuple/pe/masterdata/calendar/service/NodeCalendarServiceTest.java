package com.nextuple.pe.masterdata.calendar.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nextuple.calendar.domain.dto.NodeCalendarCacheKeyDto;
import com.nextuple.calendar.domain.inbound.NodeCalendarRequest;
import com.nextuple.calendar.domain.outbound.NodeCalendarResponse;
import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.pe.masterdata.calendar.domain.CalendarDomain;
import com.nextuple.pe.masterdata.calendar.domain.NodeCalendarDomain;
import com.nextuple.pe.masterdata.calendar.domain.entity.NodeCalendarEntity;
import com.nextuple.pe.masterdata.calendar.domain.repository.NodeCalendarRepository;
import com.nextuple.pe.masterdata.calendar.exception.CalendarDomainException;
import com.nextuple.pe.masterdata.calendar.exception.DateException;
import com.nextuple.pe.masterdata.calendar.util.DateValidation;
import com.nextuple.pe.masterdata.calendar.util.TestUtil;
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

class NodeCalendarServiceTest {

  @Mock private NodeCalendarDomain nodeCalendarDomain;
  @Mock private CalendarDomain calendarDomain;
  @Mock private NodeCalendarRepository nodeCalendarRepository;
  @Mock private DateValidation dateValidation;
  @InjectMocks private NodeCalendarService nodeCalendarService;
  @InjectMocks private TestUtil testUtil;

  @BeforeEach
  public void init() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void processCreateNodeCalendarTest()
      throws CalendarDomainException, DateException, CommonServiceException {
    when(nodeCalendarDomain.saveNodeCalendarEntity(any()))
        .thenReturn(testUtil.getNodeCalendarEntity());
    when(dateValidation.validateDate(any())).thenReturn(Boolean.TRUE);
    when(calendarDomain.getCalendar(any(), any())).thenReturn(testUtil.getCalendarEntity());
    when(nodeCalendarRepository.findByCalendarIdAndNodeIdAndOrgIdAndEffectiveDate(
            any(), any(), any(), any()))
        .thenReturn(Optional.empty());
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
  @DisplayName("When node calendar to be created already exists")
  void createNodeCalendarTestException() throws CalendarDomainException, CommonServiceException {
    NodeCalendarRequest nodeCalendarRequest = testUtil.getNodeCalendarRequest();
    when(calendarDomain.getCalendar(any(), any())).thenReturn(testUtil.getCalendarEntity());
    when(dateValidation.validateDate(any())).thenReturn(Boolean.TRUE);
    when(nodeCalendarRepository.findByCalendarIdAndNodeIdAndOrgIdAndEffectiveDate(
            any(), any(), any(), any()))
        .thenReturn(Optional.of(testUtil.getNodeCalendarEntity()));

    Exception ex =
        Assertions.assertThrows(
            CommonServiceException.class,
            () -> nodeCalendarService.processCreateNodeCalendar(nodeCalendarRequest));

    Assertions.assertEquals("Node Calendar already exists for the given details", ex.getMessage());
    verify(nodeCalendarRepository, times(1))
        .findByCalendarIdAndNodeIdAndOrgIdAndEffectiveDate(any(), any(), any(), any());
    verify(nodeCalendarDomain, times(0)).saveNodeCalendarEntity(any());
  }

  @Test
  void processCreateNodeCalendarWithInvalidDateTest()
      throws CalendarDomainException, DateException {
    when(dateValidation.validateDate(any())).thenReturn(Boolean.FALSE);
    Exception exception =
        Assertions.assertThrows(
            DateException.class,
            () -> nodeCalendarService.processCreateNodeCalendar(testUtil.getNodeCalendarRequest()));
    Assertions.assertEquals("Invalid Date", exception.getMessage());
  }

  @Test
  void processValidateCalendarIdTest() throws CalendarDomainException, CommonServiceException {
    when(calendarDomain.getCalendar(any(), any())).thenReturn(testUtil.getCalendarEntity());

    nodeCalendarService.validateCalendarId(TestUtil.CALENDAR_ID, TestUtil.ORG_ID);

    verify(calendarDomain, times(1)).getCalendar(any(), any());
  }

  @Test
  void processValidateCalendarIdTestException() throws CalendarDomainException {
    when(calendarDomain.getCalendar(any(), any())).thenReturn(null);
    Exception exception =
        Assertions.assertThrows(
            CommonServiceException.class,
            () -> nodeCalendarService.validateCalendarId(TestUtil.CALENDAR_ID, TestUtil.ORG_ID));
    Assertions.assertEquals(
        "Cannot create a node calendar as calendarId/orgId is invalid", exception.getMessage());
    verify(calendarDomain, times(1)).getCalendar(any(), any());
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

  @Test
  void getAllNodeCalendarCacheKeysTest() throws CalendarDomainException {
    List<NodeCalendarEntity> nodeCalendarEntityList = testUtil.getNodeCalendarEntityList();

    when(nodeCalendarDomain.getAllNodeCalendar(any())).thenReturn(nodeCalendarEntityList);

    List<NodeCalendarCacheKeyDto> response = nodeCalendarService.getAllNodeCalendarCacheKeys(2);

    Assertions.assertEquals(2, response.size());
    Assertions.assertEquals(nodeCalendarEntityList.get(0).getNodeId(), response.get(0).getNodeId());
    verify(nodeCalendarDomain, times(1)).getAllNodeCalendar(any());
  }

  @Test
  void getNodeAssociationWithCalendarTest() throws CalendarDomainException {
    when(nodeCalendarDomain.getNodeServiceCalendarByOrgIdAndCalendarId(any(), any()))
        .thenReturn(List.of(testUtil.getNodeCalendarEntity(), testUtil.getNodeCalendarEntity1()));

    List<NodeCalendarResponse> response =
        nodeCalendarService.getNodeAssociationWithCalendar(TestUtil.CALENDAR_ID, TestUtil.ORG_ID);

    Assertions.assertEquals(2, response.size());
    Assertions.assertEquals(
        TestUtil.CALENDAR_ID, Objects.requireNonNull(response.get(0).getCalendarId()));
    Assertions.assertEquals(TestUtil.ORG_ID, Objects.requireNonNull(response.get(0).getOrgId()));
    verify(nodeCalendarDomain, times(1)).getNodeServiceCalendarByOrgIdAndCalendarId(any(), any());
  }
}
