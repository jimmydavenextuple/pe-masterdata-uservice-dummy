/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.pe.masterdata.calendar.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.nextuple.calendar.domain.dto.NodeCarrierCalendarCacheKeyDto;
import com.nextuple.calendar.domain.inbound.NodeCarrierServiceCalendarRequest;
import com.nextuple.calendar.domain.outbound.NodeCarrierServiceCalendarResponse;
import com.nextuple.calendar.persistence.domain.NodeCarrierServiceCalendarDomainDto;
import com.nextuple.calendar.persistence.exception.CalendarDomainException;
import com.nextuple.calendar.persistence.exception.DateException;
import com.nextuple.calendar.persistence.service.CalendarPersistenceService;
import com.nextuple.calendar.persistence.service.NodeCarrierServiceCalendarPersistenceService;
import com.nextuple.carrier.domain.outbound.CarrierServiceResponse;
import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.node.domain.outbound.NodeResponse;
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
import org.springframework.context.annotation.Description;

class NodeCarrierServiceCalendarServiceTest {

  @Mock
  private NodeCarrierServiceCalendarPersistenceService nodeCarrierServiceCalendarPersistenceService;

  @Mock private CalendarPersistenceService calendarPersistenceService;
  @Mock private DateValidation dateValidation;
  @Mock private CalendarValidation calendarValidation;
  @InjectMocks private NodeCarrierServiceCalendarService nodeCarrierServiceCalendarService;
  @InjectMocks private TestUtil testUtil;

  @BeforeEach
  public void init() {

    MockitoAnnotations.openMocks(this);
  }

  @Test
  void processCreateNodeCarrierServiceCalendarTest()
      throws CalendarDomainException, DateException, CommonServiceException {
    when(nodeCarrierServiceCalendarPersistenceService.saveNodeCarrierServiceCalendar(any()))
        .thenReturn(testUtil.getNodeCarrierServiceCalendarDomainDto());
    when(dateValidation.validateDate(any())).thenReturn(Boolean.TRUE);
    when(calendarPersistenceService.getCalendar(any(), any()))
        .thenReturn(testUtil.getCalendarDomainDto());
    when(nodeCarrierServiceCalendarPersistenceService
            .findByCalendarIdAndOrgIdAndNodeIdAndCarrierServiceIdAndEffectiveDate(
                any(), any(), any(), any(), any()))
        .thenReturn(Optional.empty());
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
    verify(nodeCarrierServiceCalendarPersistenceService, times(1))
        .saveNodeCarrierServiceCalendar(any());
  }

  @Test
  @DisplayName("When node carrier service calendar to be created already exists")
  void createNodeCarrierServiceCalendarTestException()
      throws CalendarDomainException, CommonServiceException {
    NodeCarrierServiceCalendarRequest nodeCarrierServiceCalendarRequest =
        testUtil.getNodeCarrierServiceCalendarRequest();
    when(calendarPersistenceService.getCalendar(any(), any()))
        .thenReturn(testUtil.getCalendarDomainDto());
    when(dateValidation.validateDate(any())).thenReturn(Boolean.TRUE);
    when(nodeCarrierServiceCalendarPersistenceService
            .findByCalendarIdAndOrgIdAndNodeIdAndCarrierServiceIdAndEffectiveDate(
                any(), any(), any(), any(), any()))
        .thenReturn(Optional.of(testUtil.getNodeCarrierServiceCalendarDomainDto()));

    Exception ex =
        Assertions.assertThrows(
            CommonServiceException.class,
            () ->
                nodeCarrierServiceCalendarService.processCreateNodeCarrierServiceCalendarResponse(
                    nodeCarrierServiceCalendarRequest));

    Assertions.assertEquals(
        "Node Carrier Service Calendar already exists for the given details", ex.getMessage());
    verify(nodeCarrierServiceCalendarPersistenceService, times(1))
        .findByCalendarIdAndOrgIdAndNodeIdAndCarrierServiceIdAndEffectiveDate(
            any(), any(), any(), any(), any());
    verify(nodeCarrierServiceCalendarPersistenceService, times(0))
        .saveNodeCarrierServiceCalendar(any());
  }

  @Test
  void processCreateNodeCarrierServiceCalendarInvalidNodeTest()
      throws CalendarDomainException, CommonServiceException {
    when(dateValidation.validateDate(any())).thenReturn(Boolean.TRUE);
    when(calendarPersistenceService.getCalendar(any(), any()))
        .thenReturn(testUtil.getCalendarDomainDto());
    doThrow(CommonServiceException.class).when(calendarValidation).validateNodeId(any(), any());
    CommonServiceException exception =
        Assertions.assertThrows(
            CommonServiceException.class,
            () -> {
              nodeCarrierServiceCalendarService.processCreateNodeCarrierServiceCalendarResponse(
                  testUtil.getNodeCarrierServiceCalendarRequest());
            });
    verify(nodeCarrierServiceCalendarPersistenceService, times(0))
        .saveNodeCarrierServiceCalendar(any());
  }

  @Test
  void processCreateNodeCarrierServiceCalendarInactiveNodeTest()
      throws CalendarDomainException, CommonServiceException {
    when(dateValidation.validateDate(any())).thenReturn(Boolean.TRUE);
    when(calendarPersistenceService.getCalendar(any(), any()))
        .thenReturn(testUtil.getCalendarDomainDto());
    doThrow(CommonServiceException.class).when(calendarValidation).validateNodeId(any(), any());
    CommonServiceException exception =
        Assertions.assertThrows(
            CommonServiceException.class,
            () -> {
              nodeCarrierServiceCalendarService.processCreateNodeCarrierServiceCalendarResponse(
                  testUtil.getNodeCarrierServiceCalendarRequest());
            });
    verify(nodeCarrierServiceCalendarPersistenceService, times(0))
        .saveNodeCarrierServiceCalendar(any());
  }

  @Test
  void processCreateNodeCarrierServiceCalendarNodeResponseInvalidTest()
      throws CalendarDomainException, CommonServiceException {
    when(dateValidation.validateDate(any())).thenReturn(Boolean.TRUE);
    when(calendarPersistenceService.getCalendar(any(), any()))
        .thenReturn(testUtil.getCalendarDomainDto());
    BaseResponse<NodeResponse> nodeResponse = testUtil.getNodeDetails(Boolean.TRUE);
    nodeResponse.setSuccess(false);
    doThrow(CommonServiceException.class).when(calendarValidation).validateNodeId(any(), any());
    CommonServiceException exception =
        Assertions.assertThrows(
            CommonServiceException.class,
            () -> {
              nodeCarrierServiceCalendarService.processCreateNodeCarrierServiceCalendarResponse(
                  testUtil.getNodeCarrierServiceCalendarRequest());
            });
    verify(nodeCarrierServiceCalendarPersistenceService, times(0))
        .saveNodeCarrierServiceCalendar(any());
  }

  @Test
  void processCreateNodeCarrierServiceCalendarInvalidCarrierServiceIdTest()
      throws CalendarDomainException, CommonServiceException {
    when(dateValidation.validateDate(any())).thenReturn(Boolean.TRUE);
    when(calendarPersistenceService.getCalendar(any(), any()))
        .thenReturn(testUtil.getCalendarDomainDto());
    BaseResponse<List<CarrierServiceResponse>> response = testUtil.getCarrierServiceResponse();
    response.getPayload().get(0).setCarrierServiceId("INVALID");
    doThrow(CommonServiceException.class)
        .when(calendarValidation)
        .validateCarrierServiceId(any(), any());
    CommonServiceException exception =
        Assertions.assertThrows(
            CommonServiceException.class,
            () -> {
              nodeCarrierServiceCalendarService.processCreateNodeCarrierServiceCalendarResponse(
                  testUtil.getNodeCarrierServiceCalendarRequest());
            });
    verify(nodeCarrierServiceCalendarPersistenceService, times(0))
        .saveNodeCarrierServiceCalendar(any());
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
    when(calendarPersistenceService.getCalendar(any(), any()))
        .thenReturn(testUtil.getCalendarDomainDto());

    nodeCarrierServiceCalendarService.validateCalendarId(TestUtil.CALENDAR_ID, TestUtil.ORG_ID);

    verify(calendarPersistenceService, times(1)).getCalendar(any(), any());
  }

  @Test
  void processValidateCalendarIdTestException() throws CalendarDomainException {
    when(calendarPersistenceService.getCalendar(any(), any())).thenReturn(null);
    Exception exception =
        Assertions.assertThrows(
            CommonServiceException.class,
            () ->
                nodeCarrierServiceCalendarService.validateCalendarId(
                    TestUtil.CALENDAR_ID, TestUtil.ORG_ID));
    Assertions.assertEquals(
        "Cannot create a node carrier service calendar as calendarId/orgId is invalid",
        exception.getMessage());
    verify(calendarPersistenceService, times(1)).getCalendar(any(), any());
  }

  @Test
  void processGetNodeCarrierServiceCalendarWithServiceOptionTest() throws CalendarDomainException {
    when(nodeCarrierServiceCalendarPersistenceService.getNodeCarrierServiceCalendar(
            any(), any(), any(), any()))
        .thenReturn(List.of(testUtil.getNodeCarrierServiceCalendarDomainDto()));

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
    verify(nodeCarrierServiceCalendarPersistenceService, times(1))
        .getNodeCarrierServiceCalendar(any(), any(), any(), any());
  }

  @Test
  void processGetNodeCarrierServiceCalendarTest() throws CalendarDomainException {
    when(nodeCarrierServiceCalendarPersistenceService.getNodeCarrierServiceCalendar(
            any(), any(), any()))
        .thenReturn(List.of(testUtil.getNodeCarrierServiceCalendarDomainDto()));

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
    verify(nodeCarrierServiceCalendarPersistenceService, times(1))
        .getNodeCarrierServiceCalendar(any(), any(), any());
  }

  @Test
  void getAllNodeCarrierCalendarCacheKeysTest() throws CalendarDomainException {
    List<NodeCarrierServiceCalendarDomainDto> nodeCarrierServiceCalendarDomainDtos =
        testUtil.getNodeCarrierServiceCalendarDomainDtoList();

    when(nodeCarrierServiceCalendarPersistenceService.getAllNodeCarrierServiceCalendars(any()))
        .thenReturn(nodeCarrierServiceCalendarDomainDtos);

    List<NodeCarrierCalendarCacheKeyDto> response =
        nodeCarrierServiceCalendarService.getAllNodeCarrierCalendarCacheKeys(2);

    Assertions.assertEquals(2, response.size());
    Assertions.assertEquals(
        nodeCarrierServiceCalendarDomainDtos.get(0).getCarrierServiceId(),
        response.get(0).getCarrierServiceId());
    verify(nodeCarrierServiceCalendarPersistenceService, times(1))
        .getAllNodeCarrierServiceCalendars(any());
  }

  @Test
  @Description("Get Node Carrier Service calendars by org and nodeId")
  void processGetNodeCarrierServiceCalendarByNodeId() throws CalendarDomainException {
    when(nodeCarrierServiceCalendarPersistenceService.getNodeCarrierServiceCalendar(any(), any()))
        .thenReturn(List.of(testUtil.getNodeCarrierServiceCalendarDomainDto()));

    List<NodeCarrierServiceCalendarResponse> resp =
        nodeCarrierServiceCalendarService
            .processGetNodeCarrierServiceCalendarByNodeIdForDistCarrierServiceId(
                TestUtil.ORG_ID, TestUtil.NODE_ID);

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
    verify(nodeCarrierServiceCalendarPersistenceService, times(1))
        .getNodeCarrierServiceCalendar(any(), any());
  }

  @Test
  @Description("Test get all node carrier service calendars for OrgId")
  void processGetAllNodeCarrierServiceCalendar() throws CalendarDomainException {
    when(nodeCarrierServiceCalendarPersistenceService.getAllNodeCarrierServiceCalendarsByOrgId(
            any()))
        .thenReturn(List.of(testUtil.getNodeCarrierServiceCalendarDomainDto()));
    List<NodeCarrierServiceCalendarResponse> resp =
        nodeCarrierServiceCalendarService.processGetAllNodeCarrierServiceCalendar(TestUtil.ORG_ID);
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
    verify(nodeCarrierServiceCalendarPersistenceService, times(1))
        .getAllNodeCarrierServiceCalendarsByOrgId(any());
  }
}
