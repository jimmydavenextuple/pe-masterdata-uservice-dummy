/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.pe.masterdata.calendar.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nextuple.calendar.domain.dto.NodeCalendarCacheKeyDto;
import com.nextuple.calendar.domain.inbound.NodeCalendarRequest;
import com.nextuple.calendar.domain.outbound.NodeCalendarResponse;
import com.nextuple.calendar.persistence.domain.NodeCalendarDomainDto;
import com.nextuple.calendar.persistence.exception.CalendarDomainException;
import com.nextuple.calendar.persistence.exception.DateException;
import com.nextuple.calendar.persistence.service.CalendarPersistenceService;
import com.nextuple.calendar.persistence.service.NodeCalendarPersistenceService;
import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.pe.masterdata.calendar.util.CalendarValidation;
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

  @Mock private NodeCalendarPersistenceService nodeCalendarPersistenceService;
  @Mock private CalendarPersistenceService calendarPersistenceService;
  @Mock private DateValidation dateValidation;
  @Mock private CalendarValidation calendarValidation;
  @InjectMocks private NodeCalendarService nodeCalendarService;
  @InjectMocks private TestUtil testUtil;

  @BeforeEach
  public void init() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void processCreateNodeCalendarTest()
      throws CalendarDomainException, DateException, CommonServiceException {
    when(nodeCalendarPersistenceService.saveNodeCalendar(any()))
        .thenReturn(testUtil.getNodeCalendarDomainDto());
    when(dateValidation.validateDate(any())).thenReturn(Boolean.TRUE);
    when(calendarPersistenceService.getCalendar(any(), any()))
        .thenReturn(testUtil.getCalendarDomainDto());
    when(nodeCalendarPersistenceService.findByCalendarIdAndNodeIdAndOrgIdAndEffectiveDate(
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
    verify(nodeCalendarPersistenceService, times(1)).saveNodeCalendar(any());
  }

  @Test
  @DisplayName("When node calendar to be created already exists - should update")
  void processCreateNodeCalendarUpdateTest() throws CalendarDomainException, CommonServiceException, DateException {
    NodeCalendarRequest nodeCalendarRequest = testUtil.getNodeCalendarRequest();
    NodeCalendarDomainDto existingDto = testUtil.getNodeCalendarDomainDto();
    NodeCalendarDomainDto updatedDto = testUtil.getNodeCalendarDomainDto();
    updatedDto.setDescription(nodeCalendarRequest.getDescription());
    
    when(calendarPersistenceService.getCalendar(any(), any()))
        .thenReturn(testUtil.getCalendarDomainDto());
    when(dateValidation.validateDate(any())).thenReturn(Boolean.TRUE);
    when(nodeCalendarPersistenceService.findByCalendarIdAndNodeIdAndOrgIdAndEffectiveDate(
            any(), any(), any(), any()))
        .thenReturn(Optional.of(existingDto));
    when(nodeCalendarPersistenceService.saveNodeCalendar(any()))
        .thenReturn(updatedDto);

    NodeCalendarResponse resp = nodeCalendarService.processCreateNodeCalendar(nodeCalendarRequest);

    Assertions.assertEquals(TestUtil.CALENDAR_ID, Objects.requireNonNull(resp.getCalendarId()));
    Assertions.assertEquals(TestUtil.ORG_ID, Objects.requireNonNull(resp.getOrgId()));
    Assertions.assertEquals(TestUtil.NODE_ID, Objects.requireNonNull(resp.getNodeId()));
    Assertions.assertEquals(TestUtil.EFFECTIVE_DATE, Objects.requireNonNull(resp.getEffectiveDate()));
    Assertions.assertEquals(TestUtil.DESCRIPTION, Objects.requireNonNull(resp.getDescription()));
    
    verify(nodeCalendarPersistenceService, times(1))
        .findByCalendarIdAndNodeIdAndOrgIdAndEffectiveDate(any(), any(), any(), any());
    verify(nodeCalendarPersistenceService, times(1)).saveNodeCalendar(any());
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
    when(calendarPersistenceService.getCalendar(any(), any()))
        .thenReturn(testUtil.getCalendarDomainDto());

    nodeCalendarService.validateCalendarId(TestUtil.CALENDAR_ID, TestUtil.ORG_ID);

    verify(calendarPersistenceService, times(1)).getCalendar(any(), any());
  }

  @Test
  void processValidateCalendarIdTestException() throws CalendarDomainException {
    when(calendarPersistenceService.getCalendar(any(), any())).thenReturn(null);
    Exception exception =
        Assertions.assertThrows(
            CommonServiceException.class,
            () -> nodeCalendarService.validateCalendarId(TestUtil.CALENDAR_ID, TestUtil.ORG_ID));
    Assertions.assertEquals(
        "Cannot create a node calendar as calendarId/orgId is invalid", exception.getMessage());
    verify(calendarPersistenceService, times(1)).getCalendar(any(), any());
  }

  @Test
  void processGetNodeCalendarTest() throws CalendarDomainException {
    when(nodeCalendarPersistenceService.getNodeCalendar(any(), any()))
        .thenReturn(List.of(testUtil.getNodeCalendarDomainDto()));

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
    verify(nodeCalendarPersistenceService, times(1)).getNodeCalendar(any(), any());
  }

  @Test
  void getAllNodeCalendarCacheKeysTest() throws CalendarDomainException {
    List<NodeCalendarDomainDto> nodeCalendarDomainDtoList = testUtil.getNodeCalendarDomainDtoList();

    when(nodeCalendarPersistenceService.getAllNodeCalendar(any()))
        .thenReturn(nodeCalendarDomainDtoList);

    List<NodeCalendarCacheKeyDto> response = nodeCalendarService.getAllNodeCalendarCacheKeys(2);

    Assertions.assertEquals(2, response.size());
    Assertions.assertEquals(
        nodeCalendarDomainDtoList.get(0).getNodeId(), response.get(0).getNodeId());
    verify(nodeCalendarPersistenceService, times(1)).getAllNodeCalendar(any());
  }

  @Test
  void getNodeAssociationWithCalendarTest() throws CalendarDomainException {
    when(nodeCalendarPersistenceService.getNodeServiceCalendarByOrgIdAndCalendarId(any(), any()))
        .thenReturn(
            List.of(testUtil.getNodeCalendarDomainDto(), testUtil.getNodeCalendarDomainDto1()));

    List<NodeCalendarResponse> response =
        nodeCalendarService.getNodeAssociationWithCalendar(TestUtil.CALENDAR_ID, TestUtil.ORG_ID);

    Assertions.assertEquals(2, response.size());
    Assertions.assertEquals(
        TestUtil.CALENDAR_ID, Objects.requireNonNull(response.get(0).getCalendarId()));
    Assertions.assertEquals(TestUtil.ORG_ID, Objects.requireNonNull(response.get(0).getOrgId()));
    verify(nodeCalendarPersistenceService, times(1))
        .getNodeServiceCalendarByOrgIdAndCalendarId(any(), any());
  }

  @Test
  void isCreateOperationTest_WhenRecordExists_ReturnsFalse() {
    NodeCalendarRequest nodeCalendarRequest = testUtil.getNodeCalendarRequest();
    when(nodeCalendarPersistenceService.findByCalendarIdAndNodeIdAndOrgIdAndEffectiveDate(
            any(), any(), any(), any()))
        .thenReturn(Optional.of(testUtil.getNodeCalendarDomainDto()));

    boolean result = nodeCalendarService.isCreateOperation(nodeCalendarRequest);

    Assertions.assertFalse(result);
    verify(nodeCalendarPersistenceService, times(1))
        .findByCalendarIdAndNodeIdAndOrgIdAndEffectiveDate(any(), any(), any(), any());
  }

  @Test
  void isCreateOperationTest_WhenRecordDoesNotExist_ReturnsTrue() {
    NodeCalendarRequest nodeCalendarRequest = testUtil.getNodeCalendarRequest();
    when(nodeCalendarPersistenceService.findByCalendarIdAndNodeIdAndOrgIdAndEffectiveDate(
            any(), any(), any(), any()))
        .thenReturn(Optional.empty());

    boolean result = nodeCalendarService.isCreateOperation(nodeCalendarRequest);

    Assertions.assertTrue(result);
    verify(nodeCalendarPersistenceService, times(1))
        .findByCalendarIdAndNodeIdAndOrgIdAndEffectiveDate(any(), any(), any(), any());
  }
}
