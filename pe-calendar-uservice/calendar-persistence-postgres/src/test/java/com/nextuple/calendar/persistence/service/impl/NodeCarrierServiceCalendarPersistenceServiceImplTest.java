/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.calendar.persistence.service.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nextuple.calendar.persistence.domain.NodeCarrierServiceCalendarDomainDto;
import com.nextuple.calendar.persistence.entity.NodeCarrierServiceCalendarEntity;
import com.nextuple.calendar.persistence.exception.CalendarDomainException;
import com.nextuple.calendar.persistence.mapper.NodeCarrierServiceCalendarEntityMapper;
import com.nextuple.calendar.persistence.repository.NodeCarrierServiceCalendarRepository;
import com.nextuple.calendar.persistence.service.NodeCarrierServiceCalendarPersistenceServiceImpl;
import com.nextuple.calendar.persistence.util.TestUtil;
import java.util.List;
import java.util.Objects;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.annotation.Description;
import org.springframework.test.util.ReflectionTestUtils;

class NodeCarrierServiceCalendarPersistenceServiceImplTest {

  @Mock private NodeCarrierServiceCalendarRepository nodeCarrierServiceCalendarRepository;
  @Mock private NodeCarrierServiceCalendarEntityMapper nodeCarrierServiceCalendarEntityMapper;

  @InjectMocks
  private NodeCarrierServiceCalendarPersistenceServiceImpl
      nodeCarrierServiceCalendarPersistenceService;

  @InjectMocks private TestUtil testUtil;

  @BeforeEach
  public void init() {
    MockitoAnnotations.openMocks(this);
    ReflectionTestUtils.setField(
        nodeCarrierServiceCalendarPersistenceService,
        "repository",
        nodeCarrierServiceCalendarRepository);
    ReflectionTestUtils.setField(
        nodeCarrierServiceCalendarPersistenceService,
        "mapper",
        nodeCarrierServiceCalendarEntityMapper);
  }

  @Test
  void saveCalendarEntityTest() throws CalendarDomainException {
    when(nodeCarrierServiceCalendarRepository.save(any()))
        .thenReturn(testUtil.getNodeCarrierServiceCalendarEntity());

    when(nodeCarrierServiceCalendarEntityMapper.toEntity(
            any(NodeCarrierServiceCalendarDomainDto.class)))
        .thenReturn(testUtil.getNodeCarrierServiceCalendarEntity());
    when(nodeCarrierServiceCalendarEntityMapper.toDomain(
            any(NodeCarrierServiceCalendarEntity.class)))
        .thenReturn(testUtil.getNodeCarrierServiceCalendarDomainDto());

    NodeCarrierServiceCalendarDomainDto resp =
        nodeCarrierServiceCalendarPersistenceService.saveNodeCarrierServiceCalendar(
            testUtil.getNodeCarrierServiceCalendarDomainDto());

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
                nodeCarrierServiceCalendarPersistenceService.saveNodeCarrierServiceCalendar(
                    testUtil.getNodeCarrierServiceCalendarDomainDto()));

    Assertions.assertEquals("Unable to create node carrier service calendar", ex.getMessage());
    Assertions.assertEquals(TestUtil.CALENDAR_ID, ex.getCalendarId());
    verify(nodeCarrierServiceCalendarRepository, times(1)).save(any());
  }

  @Test
  void getNodeCarrierServiceCalendarWithServiceOptionTest() throws CalendarDomainException {
    when(nodeCarrierServiceCalendarRepository.findNodeCarrierServiceCalendar(
            any(), any(), any(), any()))
        .thenReturn(
            List.of(
                testUtil.getNodeCarrierServiceCalendarEntity1(),
                testUtil.getNodeCarrierServiceCalendarEntity()));

    when(nodeCarrierServiceCalendarEntityMapper.toDomain(anyList()))
        .thenReturn(
            List.of(
                testUtil.getNodeCarrierServiceCalendarDomainDto1(),
                testUtil.getNodeCarrierServiceCalendarDomainDto()));
    List<NodeCarrierServiceCalendarDomainDto> resp =
        nodeCarrierServiceCalendarPersistenceService.getNodeCarrierServiceCalendar(
            TestUtil.ORG_ID,
            TestUtil.NODE_ID,
            TestUtil.CARRIER_SERVICE_ID,
            TestUtil.SERVICE_OPTION);

    Assertions.assertEquals(
        TestUtil.CALENDAR_ID, Objects.requireNonNull(resp.get(0).getCalendarId()));
    Assertions.assertEquals(TestUtil.ORG_ID, Objects.requireNonNull(resp.get(0).getOrgId()));
    Assertions.assertEquals(TestUtil.NODE_ID, Objects.requireNonNull(resp.get(0).getNodeId()));
    Assertions.assertEquals(
        TestUtil.EFFECTIVE_DATE_2, Objects.requireNonNull(resp.get(0).getEffectiveDate()));
    Assertions.assertEquals(
        TestUtil.EFFECTIVE_DATE, Objects.requireNonNull(resp.get(1).getEffectiveDate()));
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
                nodeCarrierServiceCalendarPersistenceService.getNodeCarrierServiceCalendar(
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
        .thenReturn(
            List.of(
                testUtil.getNodeCarrierServiceCalendarEntity1(),
                testUtil.getNodeCarrierServiceCalendarEntity()));

    when(nodeCarrierServiceCalendarEntityMapper.toDomain(anyList()))
        .thenReturn(
            List.of(
                testUtil.getNodeCarrierServiceCalendarDomainDto1(),
                testUtil.getNodeCarrierServiceCalendarDomainDto()));
    List<NodeCarrierServiceCalendarDomainDto> resp =
        nodeCarrierServiceCalendarPersistenceService.getNodeCarrierServiceCalendar(
            TestUtil.ORG_ID, TestUtil.NODE_ID, TestUtil.CARRIER_SERVICE_ID);

    Assertions.assertEquals(
        TestUtil.CALENDAR_ID, Objects.requireNonNull(resp.get(0).getCalendarId()));
    Assertions.assertEquals(TestUtil.ORG_ID, Objects.requireNonNull(resp.get(0).getOrgId()));
    Assertions.assertEquals(TestUtil.NODE_ID, Objects.requireNonNull(resp.get(0).getNodeId()));
    Assertions.assertEquals(
        TestUtil.EFFECTIVE_DATE_2, Objects.requireNonNull(resp.get(0).getEffectiveDate()));
    Assertions.assertEquals(
        TestUtil.EFFECTIVE_DATE, Objects.requireNonNull(resp.get(1).getEffectiveDate()));
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
                nodeCarrierServiceCalendarPersistenceService.getNodeCarrierServiceCalendar(
                    TestUtil.ORG_ID, TestUtil.NODE_ID, TestUtil.CARRIER_SERVICE_ID));

    Assertions.assertEquals("Unable to fetch node carrier service calendar", ex.getMessage());
    Assertions.assertEquals(TestUtil.NODE_ID, ex.getNodeId());
    Assertions.assertEquals(TestUtil.CARRIER_SERVICE_ID, ex.getCarrierServiceId());
    verify(nodeCarrierServiceCalendarRepository, times(1))
        .findAllNodeCarrierServiceCalendar(any(), any(), any());
  }

  @Test
  void getAllNodeCarrierServiceCalendarsTest() throws CalendarDomainException {
    List<NodeCarrierServiceCalendarEntity> nodeCarrierServiceCalendarEntities =
        testUtil.getNodeCarrierServiceCalendarEntityList();

    when(nodeCarrierServiceCalendarRepository.findAllNodeCarrierServiceCalendars(any()))
        .thenReturn(nodeCarrierServiceCalendarEntities);
    when(nodeCarrierServiceCalendarEntityMapper.toDomain(anyList()))
        .thenReturn(testUtil.getNodeCarrierServiceCalendarDomainDtoList());

    List<NodeCarrierServiceCalendarDomainDto> response =
        nodeCarrierServiceCalendarPersistenceService.getAllNodeCarrierServiceCalendars(2);

    Assertions.assertEquals(2, response.size());
    Assertions.assertEquals(
        nodeCarrierServiceCalendarEntities.get(0).getCalendarId(), response.get(0).getCalendarId());
    verify(nodeCarrierServiceCalendarRepository, times(1))
        .findAllNodeCarrierServiceCalendars(any());
  }

  @Test
  void getAllNodeCarrierServiceCalendarsExceptionTest() {
    when(nodeCarrierServiceCalendarRepository.findAllNodeCarrierServiceCalendars(any()))
        .thenThrow(new RuntimeException("Unable to fetch all node carrier service calendars"));

    CalendarDomainException ex =
        Assertions.assertThrows(
            CalendarDomainException.class,
            () ->
                nodeCarrierServiceCalendarPersistenceService.getAllNodeCarrierServiceCalendars(2));

    Assertions.assertEquals("Unable to fetch all node carrier service calendars", ex.getMessage());
    verify(nodeCarrierServiceCalendarRepository, times(1))
        .findAllNodeCarrierServiceCalendars(any());
  }

  @Test
  @Description("Get node carrier service calendars for orgId and nodeId -Happy Path Scenario")
  void getAllNodeCarrierServiceCalendarsByOrgAndNodeIdTest() throws CalendarDomainException {
    List<NodeCarrierServiceCalendarEntity> nodeCarrierServiceCalendarEntities =
        testUtil.getNodeCarrierServiceCalendarEntityList();

    when(nodeCarrierServiceCalendarRepository.findNodeCarrierServiceCalendarByOrgIdAndNodeId(
            any(), any()))
        .thenReturn(nodeCarrierServiceCalendarEntities);
    when(nodeCarrierServiceCalendarEntityMapper.toDomain(anyList()))
        .thenReturn(testUtil.getNodeCarrierServiceCalendarDomainDtoList());

    List<NodeCarrierServiceCalendarDomainDto> response =
        nodeCarrierServiceCalendarPersistenceService.getNodeCarrierServiceCalendar(
            TestUtil.ORG_ID, TestUtil.NODE_ID);

    Assertions.assertEquals(2, response.size());
    Assertions.assertEquals(
        nodeCarrierServiceCalendarEntities.get(0).getCalendarId(), response.get(0).getCalendarId());
    verify(nodeCarrierServiceCalendarRepository, times(1))
        .findNodeCarrierServiceCalendarByOrgIdAndNodeId(any(), any());
  }

  @Test
  @Description("Get node carrier service calendars for orgId and nodeId -Exception Scenario")
  void getAllNodeCarrierServiceCalendarsByOrgAndNodeIdExceptionTest() {
    when(nodeCarrierServiceCalendarRepository.findNodeCarrierServiceCalendarByOrgIdAndNodeId(
            any(), any()))
        .thenThrow(new RuntimeException("Unable to fetch node carrier service calendars"));

    CalendarDomainException ex =
        Assertions.assertThrows(
            CalendarDomainException.class,
            () ->
                nodeCarrierServiceCalendarPersistenceService.getNodeCarrierServiceCalendar(
                    TestUtil.ORG_ID, TestUtil.NODE_ID));

    Assertions.assertEquals("Unable to fetch node carrier service calendars", ex.getMessage());
    verify(nodeCarrierServiceCalendarRepository, times(1))
        .findNodeCarrierServiceCalendarByOrgIdAndNodeId(any(), any());
  }

  @Test
  @Description("Get all Node Carrier Service Calendars by OrgId - Happy Path")
  void getAllNodeCarrierServiceCalendarsByOrgId() throws CalendarDomainException {
    List<NodeCarrierServiceCalendarEntity> nodeCarrierServiceCalendarEntities =
        testUtil.getNodeCarrierServiceCalendarEntityList();
    when(nodeCarrierServiceCalendarRepository.findAllNodeCarrierServiceCalendarsByOrgId(any()))
        .thenReturn(nodeCarrierServiceCalendarEntities);
    when(nodeCarrierServiceCalendarEntityMapper.toDomain(anyList()))
        .thenReturn(testUtil.getNodeCarrierServiceCalendarDomainDtoList());
    List<NodeCarrierServiceCalendarDomainDto> response =
        nodeCarrierServiceCalendarPersistenceService.getAllNodeCarrierServiceCalendarsByOrgId(
            TestUtil.ORG_ID);
    Assertions.assertEquals(2, response.size());
    Assertions.assertEquals(
        nodeCarrierServiceCalendarEntities.get(0).getCalendarId(), response.get(0).getCalendarId());
    verify(nodeCarrierServiceCalendarRepository, times(1))
        .findAllNodeCarrierServiceCalendarsByOrgId(any());
  }

  @Test
  @Description("Get all Node Carrier Service Calendars by OrgId - Exception Scenario")
  void getAllNodeCarrierServiceCalendarsByOrgIdException() {
    when(nodeCarrierServiceCalendarRepository.findAllNodeCarrierServiceCalendarsByOrgId(any()))
        .thenThrow(new RuntimeException("Unable to fetch all node carrier service calendars"));
    CalendarDomainException ex =
        Assertions.assertThrows(
            CalendarDomainException.class,
            () ->
                nodeCarrierServiceCalendarPersistenceService
                    .getAllNodeCarrierServiceCalendarsByOrgId(TestUtil.ORG_ID));
    Assertions.assertEquals("Unable to fetch all node carrier service calendars", ex.getMessage());
    verify(nodeCarrierServiceCalendarRepository, times(1))
        .findAllNodeCarrierServiceCalendarsByOrgId(any());
  }
}
