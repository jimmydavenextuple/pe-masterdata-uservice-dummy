/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.pe.masterdata.calendar.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nextuple.calendar.domain.CalendarDaysStatusInfo;
import com.nextuple.calendar.domain.inbound.CalendarRequest;
import com.nextuple.calendar.domain.outbound.CalendarResponse;
import com.nextuple.calendar.persistence.domain.CalendarDomainDto;
import com.nextuple.calendar.persistence.exception.CalendarDomainException;
import com.nextuple.calendar.persistence.exception.CalenderServiceException;
import com.nextuple.calendar.persistence.exception.DateException;
import com.nextuple.calendar.persistence.service.CalendarPersistenceService;
import com.nextuple.calendar.persistence.service.NodeCalendarPersistenceService;
import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.pe.masterdata.calendar.domain.NodeCarrierServiceCalendarDto;
import com.nextuple.pe.masterdata.calendar.util.DateUtil;
import com.nextuple.pe.masterdata.calendar.util.DateValidation;
import com.nextuple.pe.masterdata.calendar.util.TestUtil;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;

class CalendarServiceTest {

  @Mock private CalendarPersistenceService calendarPersistenceService;
  @Mock private NodeCalendarPersistenceService nodeCalendarPersistenceService;
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
    when(calendarPersistenceService.saveCalendar(any()))
        .thenReturn(testUtil.getCalendarDomainDto());
    when(dateValidation.validateExceptionDays(any())).thenReturn(Boolean.TRUE);
    when(calendarPersistenceService.findCalendarDetailsByCalendarIdAndOrgId(any(), any()))
        .thenReturn(Optional.empty());
    CalendarResponse resp = calendarService.processCreateCalendar(testUtil.getCalendarRequest());

    Assertions.assertEquals(TestUtil.CALENDAR_ID, Objects.requireNonNull(resp.getCalendarId()));
    Assertions.assertEquals(TestUtil.ORG_ID, Objects.requireNonNull(resp.getOrgId()));
    Assertions.assertEquals(TestUtil.DESCRIPTION, Objects.requireNonNull(resp.getDescription()));
    Assertions.assertEquals(Boolean.TRUE, Objects.requireNonNull(resp.getIsMondayWorking()));
    Assertions.assertEquals(TestUtil.EXCEPTION_DATE, resp.getExceptionDays().get(0).getDate());
    verify(calendarPersistenceService, times(1)).saveCalendar(any());
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
    when(calendarPersistenceService.findCalendarDetailsByCalendarIdAndOrgId(any(), any()))
        .thenReturn(Optional.of(testUtil.getCalendarDomainDto()));

    Exception ex =
        Assertions.assertThrows(
            CommonServiceException.class,
            () -> calendarService.processCreateCalendar(calendarRequest));

    Assertions.assertEquals("Calendar already exists for the given details", ex.getMessage());
    verify(calendarPersistenceService, times(1))
        .findCalendarDetailsByCalendarIdAndOrgId(any(), any());
    verify(calendarPersistenceService, times(0)).saveCalendar(any());
  }

  @Test
  void processGetCalendarTest() throws CalendarDomainException, CommonServiceException {
    when(calendarPersistenceService.getCalendar(any(), any()))
        .thenReturn(testUtil.getCalendarDomainDto());

    CalendarResponse resp =
        calendarService.processGetCalendar(TestUtil.ORG_ID, TestUtil.CALENDAR_ID);

    Assertions.assertEquals(TestUtil.CALENDAR_ID, Objects.requireNonNull(resp.getCalendarId()));
    Assertions.assertEquals(TestUtil.ORG_ID, Objects.requireNonNull(resp.getOrgId()));
    Assertions.assertEquals(TestUtil.DESCRIPTION, Objects.requireNonNull(resp.getDescription()));
    Assertions.assertEquals(Boolean.TRUE, Objects.requireNonNull(resp.getIsMondayWorking()));
    Assertions.assertEquals(TestUtil.EXCEPTION_DATE, resp.getExceptionDays().get(0).getDate());
    verify(calendarPersistenceService, times(2)).getCalendar(any(), any());
  }

  @Test
  void processValidateCalendarIdTest() throws CalendarDomainException, CommonServiceException {
    when(calendarPersistenceService.getCalendar(any(), any()))
        .thenReturn(testUtil.getCalendarDomainDto());

    calendarService.validateCalendarId(TestUtil.CALENDAR_ID, TestUtil.ORG_ID);

    verify(calendarPersistenceService, times(1)).getCalendar(any(), any());
  }

  @Test
  void processValidateCalendarIdTestException()
      throws CalendarDomainException, CommonServiceException {
    when(calendarPersistenceService.getCalendar(any(), any())).thenReturn(null);
    Exception exception =
        Assertions.assertThrows(
            CommonServiceException.class,
            () -> calendarService.validateCalendarId(TestUtil.CALENDAR_ID, TestUtil.ORG_ID));
    Assertions.assertEquals("Calendar does not exists", exception.getMessage());
    verify(calendarPersistenceService, times(1)).getCalendar(any(), any());
  }

  @Test
  void processGetUpcomingDaysNodeCalendarStatusTest()
      throws CalendarDomainException, CommonServiceException, CalenderServiceException {
    var domianDto = testUtil.getNodeCalendarDomainDto();
    domianDto.setEffectiveDate(DateUtil.addDaysToCurrentDate(5));
    when(nodeCalendarPersistenceService.getNodeCalendar(any(), any()))
        .thenReturn(Arrays.asList(testUtil.getNodeCalendarDomainDto(), domianDto));
    when(calendarPersistenceService.getCalendar(any(), any()))
        .thenReturn(testUtil.getCalendarDomainDto());

    List<CalendarDaysStatusInfo> resp =
        calendarService.processGetUpcomingDaysCalendarStatus(
            NodeCarrierServiceCalendarDto.builder()
                .orgId(TestUtil.ORG_ID)
                .nodeId(Optional.of(TestUtil.NODE_ID))
                .carrierServiceId(Optional.empty())
                .serviceOption(Optional.empty())
                .numberOfDaysInFuture(Optional.empty())
                .shippingStage(Optional.empty())
                .build());

    Assertions.assertEquals(11, resp.size());
    verify(nodeCalendarPersistenceService, times(1)).getNodeCalendar(any(), any());
    verify(calendarPersistenceService, times(2)).getCalendar(any(), any());
  }

  @Test
  void processGetUpcomingDaysNodeCalendarStatusWithCurrentEffectiveDateTest()
      throws CalendarDomainException, CommonServiceException, CalenderServiceException {
    var domainDto = testUtil.getNodeCalendarDomainDto();
    domainDto.setEffectiveDate(DateUtil.addDaysToCurrentDate(0));
    when(nodeCalendarPersistenceService.getNodeCalendar(any(), any()))
        .thenReturn(Arrays.asList(testUtil.getNodeCalendarDomainDto(), domainDto));
    when(calendarPersistenceService.getCalendar(any(), any()))
        .thenReturn(testUtil.getCalendarDomainDto());

    List<CalendarDaysStatusInfo> resp =
        calendarService.processGetUpcomingDaysCalendarStatus(
            NodeCarrierServiceCalendarDto.builder()
                .orgId(TestUtil.ORG_ID)
                .nodeId(Optional.of(TestUtil.NODE_ID))
                .carrierServiceId(Optional.empty())
                .serviceOption(Optional.empty())
                .numberOfDaysInFuture(Optional.empty())
                .shippingStage(Optional.empty())
                .build());

    Assertions.assertEquals(11, resp.size());
    Assertions.assertTrue(resp.get(0).getIsActive());
    verify(nodeCalendarPersistenceService, times(1)).getNodeCalendar(any(), any());
    verify(calendarPersistenceService, times(2)).getCalendar(any(), any());
  }

  @Test
  void processGetUpcomingDaysTestWithoutExceptionDays()
      throws CalendarDomainException, CommonServiceException, CalenderServiceException {
    var domainDto = testUtil.getNodeCalendarDomainDto();
    domainDto.setEffectiveDate(DateUtil.addDaysToCurrentDate(4));
    when(nodeCalendarPersistenceService.getNodeCalendar(any(), any()))
        .thenReturn(Arrays.asList(testUtil.getNodeCalendarDomainDto(), domainDto));
    var calendarDomainDto = testUtil.getCalendarDomainDto();
    calendarDomainDto.setExceptionDays(null);
    when(calendarPersistenceService.getCalendar(any(), any())).thenReturn(calendarDomainDto);

    List<CalendarDaysStatusInfo> resp =
        calendarService.processGetUpcomingDaysCalendarStatus(
            NodeCarrierServiceCalendarDto.builder()
                .orgId(TestUtil.ORG_ID)
                .nodeId(Optional.of(TestUtil.NODE_ID))
                .carrierServiceId(Optional.empty())
                .serviceOption(Optional.empty())
                .numberOfDaysInFuture(Optional.empty())
                .shippingStage(Optional.empty())
                .build());

    Assertions.assertEquals(11, resp.size());
    verify(nodeCalendarPersistenceService, times(1)).getNodeCalendar(any(), any());
    verify(calendarPersistenceService, times(2)).getCalendar(any(), any());
  }

  @Test
  void processGetUpcomingDaysNodeCalendarStatusExceptionTest() throws CalendarDomainException {
    when(nodeCalendarPersistenceService.getNodeCalendar(any(), any()))
        .thenReturn(new ArrayList<>());

    CommonServiceException cse =
        Assertions.assertThrows(
            CommonServiceException.class,
            () ->
                calendarService.processGetUpcomingDaysCalendarStatus(
                    NodeCarrierServiceCalendarDto.builder()
                        .orgId(TestUtil.ORG_ID)
                        .nodeId(Optional.of(TestUtil.NODE_ID))
                        .carrierServiceId(Optional.empty())
                        .serviceOption(Optional.empty())
                        .numberOfDaysInFuture(Optional.empty())
                        .shippingStage(Optional.empty())
                        .build()));

    Assertions.assertEquals("No active calendar associated to the node", cse.getMessage());
    verify(nodeCalendarPersistenceService, times(1)).getNodeCalendar(any(), any());
    verify(calendarPersistenceService, times(0)).getCalendar(any(), any());
  }

  @Test
  void processGetUpcomingDaysCalendarStatusExceptionTest()
      throws CalendarDomainException, CommonServiceException {

    CommonServiceException cse =
        Assertions.assertThrows(
            CommonServiceException.class,
            () ->
                calendarService.processGetUpcomingDaysCalendarStatus(
                    NodeCarrierServiceCalendarDto.builder()
                        .orgId(TestUtil.ORG_ID)
                        .nodeId(Optional.empty())
                        .carrierServiceId(Optional.empty())
                        .serviceOption(Optional.empty())
                        .numberOfDaysInFuture(Optional.empty())
                        .shippingStage(Optional.empty())
                        .build()));

    Assertions.assertEquals("Either nodeId or carrierServiceId must pe provided", cse.getMessage());
    verify(nodeCalendarPersistenceService, times(0)).getNodeCalendar(any(), any());
    verify(calendarPersistenceService, times(0)).getCalendar(any(), any());
  }

  @Test
  void processGetUpcomingDaysCarrierServiceCalendarStatusTest()
      throws CalendarDomainException, CommonServiceException, CalenderServiceException {
    var domainDto = testUtil.getCarrierServiceCalendarDomainDto();
    domainDto.setEffectiveDate(DateUtil.addDaysToCurrentDate(5));
    when(carrierServiceCalendarService.getAndFilterCarrierServiceCalendar(
            any(), any(), any(), any()))
        .thenReturn(Arrays.asList(testUtil.getCarrierServiceCalendarDomainDto(), domainDto));
    when(calendarPersistenceService.getCalendar(any(), any()))
        .thenReturn(testUtil.getCalendarDomainDto());

    List<CalendarDaysStatusInfo> resp =
        calendarService.processGetUpcomingDaysCalendarStatus(
            NodeCarrierServiceCalendarDto.builder()
                .orgId(TestUtil.ORG_ID)
                .nodeId(Optional.empty())
                .carrierServiceId(Optional.of(TestUtil.CARRIER_SERVICE_ID))
                .serviceOption(Optional.empty())
                .numberOfDaysInFuture(Optional.empty())
                .shippingStage(Optional.empty())
                .build());

    Assertions.assertEquals(11, resp.size());
    verify(carrierServiceCalendarService, times(1))
        .getAndFilterCarrierServiceCalendar(any(), any(), any(), any());
    verify(calendarPersistenceService, times(2)).getCalendar(any(), any());
  }

  @Test
  void processGetUpcomingDaysCarrierServiceCalendarStatusWithCurrentEffectiveDateTest()
      throws CalendarDomainException, CommonServiceException, CalenderServiceException {
    var domainDto = testUtil.getCarrierServiceCalendarDomainDto();
    domainDto.setEffectiveDate(DateUtil.addDaysToCurrentDate(0));
    when(carrierServiceCalendarService.getAndFilterCarrierServiceCalendar(
            any(), any(), any(), any()))
        .thenReturn(Arrays.asList(testUtil.getCarrierServiceCalendarDomainDto(), domainDto));
    when(calendarPersistenceService.getCalendar(any(), any()))
        .thenReturn(testUtil.getCalendarDomainDto());

    List<CalendarDaysStatusInfo> resp =
        calendarService.processGetUpcomingDaysCalendarStatus(
            NodeCarrierServiceCalendarDto.builder()
                .orgId(TestUtil.ORG_ID)
                .nodeId(Optional.empty())
                .carrierServiceId(Optional.of(TestUtil.CARRIER_SERVICE_ID))
                .serviceOption(Optional.empty())
                .numberOfDaysInFuture(Optional.empty())
                .shippingStage(Optional.empty())
                .build());

    Assertions.assertEquals(11, resp.size());
    Assertions.assertTrue(resp.get(0).getIsActive());
    verify(carrierServiceCalendarService, times(1))
        .getAndFilterCarrierServiceCalendar(any(), any(), any(), any());
    verify(calendarPersistenceService, times(2)).getCalendar(any(), any());
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
                    NodeCarrierServiceCalendarDto.builder()
                        .orgId(TestUtil.ORG_ID)
                        .nodeId(Optional.empty())
                        .carrierServiceId(Optional.of(TestUtil.CARRIER_SERVICE_ID))
                        .serviceOption(Optional.empty())
                        .numberOfDaysInFuture(Optional.empty())
                        .shippingStage(Optional.empty())
                        .build()));

    Assertions.assertEquals(
        "No active calendar associated to the carrier & service", cse.getMessage());
    verify(carrierServiceCalendarService, times(1))
        .getAndFilterCarrierServiceCalendar(any(), any(), any(), any());
    verify(calendarPersistenceService, times(0)).getCalendar(any(), any());
  }

  @Test
  void processGetUpcomingDaysNodeCarrierServiceCalendarStatusTest()
      throws CalendarDomainException, CommonServiceException, CalenderServiceException {
    var domainDto = testUtil.getNodeCarrierServiceCalendarDomainDto();
    domainDto.setEffectiveDate(DateUtil.addDaysToCurrentDate(5));
    when(nodeCarrierServiceCalendarService.getAndFilterNodeCarrierServiceCalendar(
            any(), any(), any(), any()))
        .thenReturn(Arrays.asList(testUtil.getNodeCarrierServiceCalendarDomainDto(), domainDto));
    when(calendarPersistenceService.getCalendar(any(), any()))
        .thenReturn(testUtil.getCalendarDomainDto());

    List<CalendarDaysStatusInfo> resp =
        calendarService.processGetUpcomingDaysCalendarStatus(
            NodeCarrierServiceCalendarDto.builder()
                .orgId(TestUtil.ORG_ID)
                .nodeId(Optional.of(TestUtil.NODE_ID))
                .carrierServiceId(Optional.of(TestUtil.CARRIER_SERVICE_ID))
                .serviceOption(Optional.empty())
                .numberOfDaysInFuture(Optional.empty())
                .shippingStage(Optional.empty())
                .build());

    Assertions.assertEquals(11, resp.size());
    verify(nodeCarrierServiceCalendarService, times(1))
        .getAndFilterNodeCarrierServiceCalendar(any(), any(), any(), any());
    verify(calendarPersistenceService, times(2)).getCalendar(any(), any());
  }

  @Test
  void processGetUpcomingDaysNodeCarrierCalendarStatusWithCurrentEffectiveDateTest()
      throws CalendarDomainException, CommonServiceException, CalenderServiceException {
    var domainDto = testUtil.getNodeCarrierServiceCalendarDomainDto();
    domainDto.setEffectiveDate(DateUtil.addDaysToCurrentDate(0));
    when(nodeCarrierServiceCalendarService.getAndFilterNodeCarrierServiceCalendar(
            any(), any(), any(), any()))
        .thenReturn(Arrays.asList(testUtil.getNodeCarrierServiceCalendarDomainDto(), domainDto));
    when(calendarPersistenceService.getCalendar(any(), any()))
        .thenReturn(testUtil.getCalendarDomainDto());

    List<CalendarDaysStatusInfo> resp =
        calendarService.processGetUpcomingDaysCalendarStatus(
            NodeCarrierServiceCalendarDto.builder()
                .orgId(TestUtil.ORG_ID)
                .nodeId(Optional.of(TestUtil.NODE_ID))
                .carrierServiceId(Optional.of(TestUtil.CARRIER_SERVICE_ID))
                .serviceOption(Optional.empty())
                .numberOfDaysInFuture(Optional.empty())
                .shippingStage(Optional.empty())
                .build());

    Assertions.assertEquals(11, resp.size());
    Assertions.assertTrue(resp.get(0).getIsActive());
    verify(nodeCarrierServiceCalendarService, times(1))
        .getAndFilterNodeCarrierServiceCalendar(any(), any(), any(), any());
    verify(calendarPersistenceService, times(2)).getCalendar(any(), any());
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
                    NodeCarrierServiceCalendarDto.builder()
                        .orgId(TestUtil.ORG_ID)
                        .nodeId(Optional.of(TestUtil.NODE_ID))
                        .carrierServiceId(Optional.of(TestUtil.CARRIER_SERVICE_ID))
                        .serviceOption(Optional.empty())
                        .numberOfDaysInFuture(Optional.empty())
                        .shippingStage(Optional.empty())
                        .build()));

    Assertions.assertEquals(
        "No active calendar associated to the node, carrier & service", cse.getMessage());
    verify(nodeCarrierServiceCalendarService, times(1))
        .getAndFilterNodeCarrierServiceCalendar(any(), any(), any(), any());
    verify(calendarPersistenceService, times(0)).getCalendar(any(), any());
  }

  @Test
  @DisplayName("Get upcoming days of node calendar with from date")
  void processGetUpcomingDaysNodeCalendarStatusWithFromDateTest()
      throws CalendarDomainException, CalenderServiceException, CommonServiceException {
    when(nodeCalendarPersistenceService.getNodeCalendar(any(), any()))
        .thenReturn(testUtil.getNodeCalendarDomainDtoList());
    when(calendarPersistenceService.getCalendar(any(), any()))
        .thenReturn(testUtil.getCalendarDomainDtoWithFromDate());
    List<CalendarDaysStatusInfo> response =
        calendarService.processGetUpcomingDaysCalendarStatus(
            NodeCarrierServiceCalendarDto.builder()
                .orgId(TestUtil.ORG_ID)
                .nodeId(Optional.of(TestUtil.NODE_ID))
                .carrierServiceId(Optional.empty())
                .serviceOption(Optional.empty())
                .numberOfDaysInFuture(Optional.empty())
                .shippingStage(Optional.empty())
                .fromDate((Optional.of(TestUtil.FROM_DATE)))
                .build());

    Assertions.assertEquals(11, response.size());
    Assertions.assertEquals("2023-12-31", response.getFirst().getDate());
    Assertions.assertEquals("2024-01-10", response.getLast().getDate());
    Assertions.assertTrue(response.get(1).getIsActive());
    Assertions.assertTrue(response.get(2).getIsActive());
    Assertions.assertFalse(response.get(5).getIsActive());
    Assertions.assertFalse(response.get(6).getIsActive());
    Assertions.assertFalse(response.get(7).getIsActive());

    verify(nodeCalendarPersistenceService, times(1)).getNodeCalendar(any(), any());
    verify(calendarPersistenceService, times(1)).getCalendar(any(), any());
  }

  @Test
  @DisplayName("Get upcoming days of node calendar with invalid from date")
  void processGetUpcomingDaysNodeCalendarStatusWithInvalidFromDateTest()
      throws CalendarDomainException, CalenderServiceException, CommonServiceException {
    CommonServiceException ce =
        Assertions.assertThrows(
            CommonServiceException.class,
            () ->
                calendarService.processGetUpcomingDaysCalendarStatus(
                    NodeCarrierServiceCalendarDto.builder()
                        .orgId(TestUtil.ORG_ID)
                        .nodeId(Optional.of(TestUtil.NODE_ID))
                        .carrierServiceId(Optional.empty())
                        .serviceOption(Optional.empty())
                        .numberOfDaysInFuture(Optional.empty())
                        .shippingStage(Optional.empty())
                        .fromDate((Optional.of("2024-32-32")))
                        .build()));
    Assertions.assertEquals(
        "Invalid fromDate or fromDate format, expected yyyy-MM-dd format", ce.getMessage());
    Assertions.assertEquals(HttpStatus.BAD_REQUEST, ce.getHttpStatus());
    verify(nodeCalendarPersistenceService, times(0)).getNodeCalendar(any(), any());
    verify(calendarPersistenceService, times(0)).getCalendar(any(), any());
  }

  @Test
  void getCalendarListTest() throws CalendarDomainException, CommonServiceException {
    List<CalendarDomainDto> calendarDomainDtos =
        List.of(testUtil.getCalendarDomainDto(), testUtil.getCalendarDomainDto1());

    when(calendarPersistenceService.findCalendarListByOrgId(any(), any(), any(), any(), any()))
        .thenReturn(
            testUtil.getCalendarPageDomainDtos(
                2, calendarDomainDtos, calendarDomainDtos.size(), TestUtil.SORT_ORDER_DESC));

    Page<CalendarResponse> calendarResponsePage =
        calendarService.getCalendarList(
            TestUtil.ORG_ID, 1, 1, TestUtil.SORT_BY, TestUtil.SORT_ORDER_DESC);

    assertEquals(2, (int) calendarResponsePage.getTotalElements());
    assertEquals(2, calendarResponsePage.getTotalPages());
    assertEquals(calendarDomainDtos.size(), calendarResponsePage.getContent().size());
    assertEquals(Sort.by(Direction.DESC, TestUtil.SORT_BY), calendarResponsePage.getSort());
    verify(calendarPersistenceService, times(1))
        .findCalendarListByOrgId(any(), any(), any(), any(), any());
  }

  @Test
  void getCalendarListDefaultSortOrderTest()
      throws CalendarDomainException, CommonServiceException {
    List<CalendarDomainDto> calendarDomainDtos =
        List.of(testUtil.getCalendarDomainDto(), testUtil.getCalendarDomainDto1());

    when(calendarPersistenceService.findCalendarListByOrgId(any(), any(), any(), any(), any()))
        .thenReturn(
            testUtil.getCalendarPageDomainDtos(
                2, calendarDomainDtos, calendarDomainDtos.size(), TestUtil.SORT_ORDER_ASC));

    Page<CalendarResponse> calendarResponsePage =
        calendarService.getCalendarList(
            TestUtil.ORG_ID, 1, 1, TestUtil.SORT_BY, TestUtil.SORT_ORDER_ASC);

    assertEquals(2, (int) calendarResponsePage.getTotalElements());
    assertEquals(2, calendarResponsePage.getTotalPages());
    assertEquals(calendarDomainDtos.size(), calendarResponsePage.getContent().size());
    assertEquals(Sort.by(Direction.ASC, TestUtil.SORT_BY), calendarResponsePage.getSort());
    verify(calendarPersistenceService, times(1))
        .findCalendarListByOrgId(any(), any(), any(), any(), any());
  }

  @Test
  void getCalendarListExceptionTest() throws CalendarDomainException, CommonServiceException {
    Exception exception =
        Assertions.assertThrows(
            CommonServiceException.class,
            () ->
                calendarService.getCalendarList(
                    TestUtil.ORG_ID, 1, 1, TestUtil.SORT_BY, "invalid sort order"));

    assertEquals("Invalid sort order, consider giving either ASC or DESC", exception.getMessage());
    verify(calendarPersistenceService, times(0))
        .findCalendarListByOrgId(any(), any(), any(), any(), any());
  }
}
