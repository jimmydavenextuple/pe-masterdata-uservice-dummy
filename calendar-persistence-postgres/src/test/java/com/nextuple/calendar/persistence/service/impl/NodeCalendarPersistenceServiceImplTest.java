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

import com.nextuple.calendar.persistence.domain.NodeCalendarDomainDto;
import com.nextuple.calendar.persistence.entity.NodeCalendarEntity;
import com.nextuple.calendar.persistence.exception.CalendarDomainException;
import com.nextuple.calendar.persistence.mapper.NodeCalendarEntityMapper;
import com.nextuple.calendar.persistence.repository.NodeCalendarRepository;
import com.nextuple.calendar.persistence.service.NodeCalendarPersistenceServiceImpl;
import com.nextuple.calendar.persistence.util.TestUtil;
import java.util.List;
import java.util.Objects;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

class NodeCalendarPersistenceServiceImplTest {

  @Mock private NodeCalendarRepository nodeCalendarRepository;
  @Mock private NodeCalendarEntityMapper nodeCalendarEntityMapper;
  @InjectMocks private NodeCalendarPersistenceServiceImpl nodeCalendarPersistenceServiceImpl;
  @InjectMocks private TestUtil testUtil;

  @BeforeEach
  public void init() {
    MockitoAnnotations.openMocks(this);
    // Added this
    ReflectionTestUtils.setField(
        nodeCalendarPersistenceServiceImpl, "repository", nodeCalendarRepository);
    ReflectionTestUtils.setField(
        nodeCalendarPersistenceServiceImpl, "mapper", nodeCalendarEntityMapper);
  }

  @Test
  void saveNodeCalendarEntityTest() throws CalendarDomainException {
    when(nodeCalendarRepository.save(any())).thenReturn(testUtil.getNodeCalendarEntity());
    when(nodeCalendarEntityMapper.toEntity(any(NodeCalendarDomainDto.class)))
        .thenReturn(testUtil.getNodeCalendarEntity());
    when(nodeCalendarEntityMapper.toDomain(any(NodeCalendarEntity.class)))
        .thenReturn(testUtil.getNodeCalendarDomainDto());

    NodeCalendarDomainDto resp =
        nodeCalendarPersistenceServiceImpl.saveNodeCalendar(testUtil.getNodeCalendarDomainDto());

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
            () ->
                nodeCalendarPersistenceServiceImpl.saveNodeCalendar(
                    testUtil.getNodeCalendarDomainDto()));

    Assertions.assertEquals("Unable to create node calendar", ex.getMessage());
    Assertions.assertEquals(TestUtil.CALENDAR_ID, ex.getCalendarId());
    verify(nodeCalendarRepository, times(1)).save(any());
  }

  @Test
  void getNodeCalendarTest() throws CalendarDomainException {
    when(nodeCalendarRepository.findByOrgIdAndNodeId(any(), any()))
        .thenReturn(List.of(testUtil.getNodeCalendarEntity1(), testUtil.getNodeCalendarEntity()));

    when(nodeCalendarEntityMapper.toDomain(anyList()))
        .thenReturn(
            List.of(testUtil.getNodeCalendarDomainDto1(), testUtil.getNodeCalendarDomainDto()));
    List<NodeCalendarDomainDto> resp =
        nodeCalendarPersistenceServiceImpl.getNodeCalendar(TestUtil.ORG_ID, TestUtil.NODE_ID);

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
            () ->
                nodeCalendarPersistenceServiceImpl.getNodeCalendar(
                    TestUtil.ORG_ID, TestUtil.NODE_ID));

    Assertions.assertEquals("Unable to fetch node calendar", ex.getMessage());
    Assertions.assertEquals(TestUtil.NODE_ID, ex.getNodeId());
    verify(nodeCalendarRepository, times(1)).findByOrgIdAndNodeId(any(), any());
  }

  @Test
  void getAllNodeCalendarTest() throws CalendarDomainException {
    List<NodeCalendarEntity> nodeCalendarEntityList = testUtil.getNodeCalendarEntityList();

    when(nodeCalendarRepository.findAllNodeCalendarByLimit(any()))
        .thenReturn(nodeCalendarEntityList);
    when(nodeCalendarEntityMapper.toDomain(anyList()))
        .thenReturn(testUtil.getNodeCalendarDomainDtoList());

    List<NodeCalendarDomainDto> response = nodeCalendarPersistenceServiceImpl.getAllNodeCalendar(2);

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
            CalendarDomainException.class,
            () -> nodeCalendarPersistenceServiceImpl.getAllNodeCalendar(2));

    Assertions.assertEquals("Unable to fetch all node calendars", ex.getMessage());
    verify(nodeCalendarRepository, times(1)).findAllNodeCalendarByLimit(any());
  }

  @Test
  void getNodeServiceCalendarByOrgIdAndCalendarIdTest() throws CalendarDomainException {
    when(nodeCalendarRepository.findNodeCalendarByCalendarIdAndOrgId(any(), any()))
        .thenReturn(List.of(testUtil.getNodeCalendarEntity(), testUtil.getNodeCalendarEntity1()));

    when(nodeCalendarEntityMapper.toDomain(anyList()))
        .thenReturn(
            List.of(testUtil.getNodeCalendarDomainDto(), testUtil.getNodeCalendarDomainDto1()));
    List<NodeCalendarDomainDto> response =
        nodeCalendarPersistenceServiceImpl.getNodeServiceCalendarByOrgIdAndCalendarId(
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
                nodeCalendarPersistenceServiceImpl.getNodeServiceCalendarByOrgIdAndCalendarId(
                    TestUtil.CALENDAR_ID, TestUtil.ORG_ID));

    Assertions.assertEquals("Unable to fetch node calendar list", ex.getMessage());
    verify(nodeCalendarRepository, times(1)).findNodeCalendarByCalendarIdAndOrgId(any(), any());
  }
}
