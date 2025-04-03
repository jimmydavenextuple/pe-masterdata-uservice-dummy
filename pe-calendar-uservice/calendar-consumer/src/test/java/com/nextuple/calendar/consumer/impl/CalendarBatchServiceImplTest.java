/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.calendar.consumer.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.nextuple.calendar.consumer.TestUtil;
import com.nextuple.calendar.consumer.dto.CalendarFeedDto;
import com.nextuple.calendar.domain.feign.CalendarFeign;
import com.nextuple.calendar.persistence.domain.CalendarDomainDto;
import com.nextuple.calendar.persistence.service.CalendarPersistenceService;
import com.nextuple.common.enums.ActionEnum;
import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.util.DateUtil;
import com.nextuple.master.data.integration.dto.ResponseDto;
import com.nextuple.master.data.integration.inbound.BatchRequest;
import com.nextuple.master.data.integration.outbound.BatchResponse;
import com.nextuple.master.data.integration.service.ErrorHandlingService;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;

@RunWith(MockitoJUnitRunner.class)
class CalendarBatchServiceImplTest {

  @InjectMocks private CalendarBatchServiceImpl calendarBatchService;
  @InjectMocks private TestUtil testUtil;
  @Mock private CalendarFeign calendarFeign;
  @Mock private ErrorHandlingService errorHandlingService;
  @Mock private CalendarPersistenceService calendarPersistenceService;

  @BeforeEach
  void init() {
    MockitoAnnotations.openMocks(this);

    ReflectionTestUtils.setField(
        calendarBatchService, "errorHandlingService", errorHandlingService);
  }

  @Test
  void processBatchRecordsTestWhenActionIsCreate() {
    List<BatchRequest<CalendarFeedDto>> nodeFeedRequests =
        List.of(testUtil.getCalendarFeedRequest(ActionEnum.CREATE));
    Mockito.when(calendarFeign.createCalendar(any()))
        .thenReturn(testUtil.getBaseResponseOfCalendarFeed("Calendar created successfully"));
    ResponseDto responseDto = testUtil.createResponseDto(1, 200, "Calendar created successfully");
    responseDto.setMessage(
        testUtil.getBaseResponseOfCalendarFeed("Calendar created successfully").getMessage());
    List<ResponseDto> responseDtoList = List.of(responseDto);
    BatchResponse batchResponse = testUtil.getCalendarBatchResponse(1, 1, 0);
    batchResponse.setResponses(responseDtoList);
    Mockito.doNothing()
        .when(errorHandlingService)
        .handleSuccessResponse(any(), any(), any(), any());
    Assertions.assertEquals(
        batchResponse, calendarBatchService.processRecordsWithRetry(nodeFeedRequests));
    verify(calendarFeign, times(1)).createCalendar(any());
  }

  @Test
  void processBatchRecordsTestWithNoRetry() {
    List<BatchRequest<CalendarFeedDto>> nodeFeedRequests =
        List.of(testUtil.getCalendarFeedRequest(ActionEnum.CREATE));
    Mockito.when(calendarFeign.createCalendar(any()))
        .thenReturn(testUtil.getBaseResponseOfCalendarFeed("Calendar created successfully"));
    ResponseDto responseDto = testUtil.createResponseDto(1, 200, "Calendar created successfully");
    responseDto.setMessage(
        testUtil.getBaseResponseOfCalendarFeed("Calendar created successfully").getMessage());
    List<ResponseDto> responseDtoList = List.of(responseDto);
    BatchResponse batchResponse = testUtil.getCalendarBatchResponse(1, 1, 0);
    batchResponse.setResponses(responseDtoList);
    Assertions.assertEquals(
        batchResponse, calendarBatchService.processRecordsWithRetry(nodeFeedRequests));
    verify(calendarFeign, times(1)).createCalendar(any());
  }

  @Test
  void processBatchRecordsTestForInvalidAction() {
    List<BatchRequest<CalendarFeedDto>> nodeFeedRequests =
        List.of(testUtil.getCalendarFeedRequest(ActionEnum.UPDATE));
    ResponseDto responseDto =
        testUtil.createResponseDto(
            1, HttpStatus.BAD_REQUEST.value(), "Action not supported : UPDATE");
    List<ResponseDto> responseDtoList = List.of(responseDto);
    BatchResponse batchResponse = testUtil.getCalendarBatchResponse(1, 0, 1);
    batchResponse.setResponses(responseDtoList);
    Mockito.doNothing()
        .when(errorHandlingService)
        .handleExceptions(anyInt(), any(), any(), any(), any(), any());
    Assertions.assertEquals(
        batchResponse, calendarBatchService.processRecordsWithRetry(nodeFeedRequests));
    verify(calendarFeign, times(0)).createCalendar(any());
  }

  @Test
  void processBatchRecordsTestForOutdatedRecords() {
    BatchRequest<CalendarFeedDto> batchRequest = testUtil.getCalendarFeedRequest(ActionEnum.CREATE);
    batchRequest.setReceivedTimestamp(new Date());
    List<BatchRequest<CalendarFeedDto>> nodeFeedRequests = List.of(batchRequest);
    ResponseDto responseDto =
        testUtil.createResponseDto(
            1, HttpStatus.BAD_REQUEST.value(), "Can't process the record as it's outdated");
    List<ResponseDto> responseDtoList = List.of(responseDto);
    BatchResponse batchResponse = testUtil.getCalendarBatchResponse(1, 0, 1);
    batchResponse.setResponses(responseDtoList);
    Mockito.doNothing()
        .when(errorHandlingService)
        .handleExceptions(anyInt(), any(), any(), any(), any(), any());
    CalendarDomainDto calendarDomainDto = testUtil.getCalendarDomainDto();
    calendarDomainDto.setLastModifiedDate(DateUtil.addDaysToDate(new Date(), 1));
    when(calendarPersistenceService.findCalendarDetailsByCalendarIdAndOrgId(
            anyString(), anyString()))
        .thenReturn(Optional.of(calendarDomainDto));
    Assertions.assertEquals(
        batchResponse, calendarBatchService.processRecordsWithRetry(nodeFeedRequests));
    verify(calendarFeign, times(0)).createCalendar(any());
  }

  @Test
  void handleCalendarRetryTest() throws CommonServiceException {
    BatchRequest<?> inputFeedRequest = testUtil.getCalendarFeedRequest(ActionEnum.CREATE);
    Mockito.when(calendarFeign.createCalendar(any()))
        .thenReturn(testUtil.getBaseResponseOfCalendarFeed("Calendar created successfully"));
    String responseMessage = calendarBatchService.handleRetry(inputFeedRequest);

    Assertions.assertEquals("Calendar created successfully", responseMessage);

    verify(calendarFeign, times(1)).createCalendar(any());
  }
}
