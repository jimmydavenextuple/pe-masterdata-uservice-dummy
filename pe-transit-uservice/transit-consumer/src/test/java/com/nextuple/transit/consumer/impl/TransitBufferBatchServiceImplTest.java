/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.transit.consumer.impl;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.util.DateUtil;
import com.nextuple.master.data.integration.dto.ResponseDto;
import com.nextuple.master.data.integration.enums.ActionEnum;
import com.nextuple.master.data.integration.inbound.BatchRequest;
import com.nextuple.master.data.integration.outbound.BatchResponse;
import com.nextuple.master.data.integration.service.ErrorHandlingService;
import com.nextuple.transit.consumer.TestUtil;
import com.nextuple.transit.consumer.dto.TransitBufferFeedDto;
import com.nextuple.transit.domain.feign.TransitBufferV2Feign;
import com.nextuple.transit.persistence.domain.TransitBufferV2DomainDto;
import com.nextuple.transit.persistence.service.impl.TransitBufferV2PersistenceServiceImpl;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
class TransitBufferBatchServiceImplTest {

  @InjectMocks private TransitBufferBatchServiceImpl transitBufferBatchService;
  @InjectMocks private TestUtil testUtil;
  @Mock private TransitBufferV2Feign transitFeign;
  @Mock private ErrorHandlingService errorHandlingService;
  @Mock private TransitBufferV2PersistenceServiceImpl transitBufferV2PersistenceService;

  @BeforeEach
  void init() {
    MockitoAnnotations.openMocks(this);

    ReflectionTestUtils.setField(
        transitBufferBatchService, "errorHandlingService", errorHandlingService);
  }

  @Test
  @DisplayName("When transit buffer feed with create action is processed successfully")
  void processBatchRecordsTestWhenActionIsCreate() {
    List<BatchRequest<TransitBufferFeedDto>> transitBufferFeedRequests =
        List.of(testUtil.getTransitBufferFeedRequest(ActionEnum.CREATE));
    Mockito.when(transitFeign.createTransitBuffer(any()))
        .thenReturn(
            testUtil.getBaseResponseOfTransitBufferFeed("Transit buffer created successfully"));
    ResponseDto responseDto =
        testUtil.createResponseDto(1, 200, "Transit buffer created successfully");
    responseDto.setMessage(
        testUtil
            .getBaseResponseOfTransitBufferFeed("Transit buffer created successfully")
            .getMessage());
    List<ResponseDto> responseDtoList = List.of(responseDto);
    BatchResponse batchResponse = testUtil.getTransitBufferBatchResponse(1, 1, 0);
    batchResponse.setResponses(responseDtoList);
    Mockito.doNothing()
        .when(errorHandlingService)
        .handleSuccessResponse(any(), any(), any(), any());
    Assertions.assertEquals(
        batchResponse,
        transitBufferBatchService.processRecordsWithRetry(transitBufferFeedRequests));
    verify(transitFeign, times(1)).createTransitBuffer(any());
  }

  @Test
  @DisplayName("When the batch records are processed successfully")
  void processBatchRecordsTestWithNoRetry() {
    List<BatchRequest<TransitBufferFeedDto>> transitBufferFeedRequests =
        List.of(testUtil.getTransitBufferFeedRequest(ActionEnum.CREATE));
    Mockito.when(transitFeign.createTransitBuffer(any()))
        .thenReturn(
            testUtil.getBaseResponseOfTransitBufferFeed("Transit buffer created successfully"));
    ResponseDto responseDto =
        testUtil.createResponseDto(1, 200, "Transit buffer created successfully");
    responseDto.setMessage(
        testUtil
            .getBaseResponseOfTransitBufferFeed("Transit buffer created successfully")
            .getMessage());
    List<ResponseDto> responseDtoList = List.of(responseDto);
    BatchResponse batchResponse = testUtil.getTransitBufferBatchResponse(1, 1, 0);
    batchResponse.setResponses(responseDtoList);
    Assertions.assertEquals(
        batchResponse,
        transitBufferBatchService.processRecordsWithRetry(transitBufferFeedRequests));
    verify(transitFeign, times(1)).createTransitBuffer(any());
  }

  @Test
  @DisplayName("When the batch records are processed for the delete action")
  void processBatchRecordsTestWhenActionIsDelete() {
    List<BatchRequest<TransitBufferFeedDto>> transitBufferFeedRequests =
        List.of(testUtil.getTransitBufferFeedRequest(ActionEnum.DELETE));
    Mockito.when(transitFeign.deleteTransitBufferRecord(any()))
        .thenReturn(
            testUtil.getBaseResponseOfTransitBufferFeed("Transit buffer deleted successfully"));
    ResponseDto responseDto =
        testUtil.createResponseDto(1, 200, "Transit buffer deleted successfully");
    responseDto.setMessage(
        testUtil
            .getBaseResponseOfTransitBufferFeed("Transit buffer deleted successfully")
            .getMessage());
    List<ResponseDto> responseDtoList = List.of(responseDto);
    BatchResponse batchResponse = testUtil.getTransitBufferBatchResponse(1, 1, 0);
    batchResponse.setResponses(responseDtoList);
    Mockito.doNothing()
        .when(errorHandlingService)
        .handleSuccessResponse(any(), any(), any(), any());
    Assertions.assertEquals(
        batchResponse,
        transitBufferBatchService.processRecordsWithRetry(transitBufferFeedRequests));
    verify(transitFeign, times(1)).deleteTransitBufferRecord(any());
  }

  @Test
  @DisplayName("When the batch records cannot be processed as they are outdated")
  void processBatchRecordsTestForOutdatedRecords() throws CommonServiceException {
    BatchRequest<TransitBufferFeedDto> batchRequest =
        testUtil.getTransitBufferFeedRequest(ActionEnum.UPDATE);
    batchRequest.setReceivedTimestamp(new Date());
    List<BatchRequest<TransitBufferFeedDto>> transitBufferFeedRequests = List.of(batchRequest);
    ResponseDto responseDto =
        testUtil.createResponseDto(
            1, HttpStatus.BAD_REQUEST.value(), "Can't process the record as it's outdated");
    List<ResponseDto> responseDtoList = List.of(responseDto);
    BatchResponse batchResponse = testUtil.getTransitBufferBatchResponse(1, 0, 1);
    batchResponse.setResponses(responseDtoList);
    Mockito.doNothing()
        .when(errorHandlingService)
        .handleExceptions(anyInt(), any(), any(), any(), any(), any());
    TransitBufferV2DomainDto transitBufferDomainDto = testUtil.getTransitBufferDomainDto();
    transitBufferDomainDto.setLastModifiedDate(DateUtil.addDaysToDate(new Date(), 1));
    when(transitBufferV2PersistenceService
            .fetchTransitBufferByOrgIdAndDestinationGeozoneAndSourceGeozoneAndCarrierServiceIdAndBufferStartDateAndBufferEndDate(
                anyString(), anyString(), anyString(), anyString(), any(), any()))
        .thenReturn(Optional.of(transitBufferDomainDto));
    Assertions.assertEquals(
        batchResponse,
        transitBufferBatchService.processRecordsWithRetry(transitBufferFeedRequests));
    verify(transitFeign, times(0)).createTransitBuffer(any());
  }

  @Test
  @DisplayName("When the batch records are retried")
  void handleTransitBufferRetryTest() throws CommonServiceException {
    BatchRequest<?> inputFeedRequest = testUtil.getTransitBufferFeedRequest(ActionEnum.CREATE);
    Mockito.when(transitFeign.createTransitBuffer(any()))
        .thenReturn(
            testUtil.getBaseResponseOfTransitBufferFeed("Transit buffer created successfully"));
    String responseMessage = transitBufferBatchService.handleRetry(inputFeedRequest);

    Assertions.assertEquals("Transit buffer created successfully", responseMessage);

    verify(transitFeign, times(1)).createTransitBuffer(any());
  }
}
