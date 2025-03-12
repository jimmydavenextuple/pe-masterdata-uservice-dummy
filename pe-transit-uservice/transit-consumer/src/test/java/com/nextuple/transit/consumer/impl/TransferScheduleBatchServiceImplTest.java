/*
 * Copyright (c) 2025., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.transit.consumer.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.util.DateUtil;
import com.nextuple.master.data.integration.dto.ResponseDto;
import com.nextuple.master.data.integration.enums.ActionEnum;
import com.nextuple.master.data.integration.inbound.BatchRequest;
import com.nextuple.master.data.integration.outbound.BatchResponse;
import com.nextuple.master.data.integration.service.ErrorHandlingService;
import com.nextuple.transit.consumer.TestUtil;
import com.nextuple.transit.consumer.dto.TransferScheduleDto;
import com.nextuple.transit.domain.feign.TransferScheduleFeign;
import com.nextuple.transit.persistence.entity.TransferScheduleEntity;
import com.nextuple.transit.persistence.repository.TransferScheduleRepository;
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
public class TransferScheduleBatchServiceImplTest {
  @InjectMocks private TransferScheduleBatchServiceImpl transferScheduleBatchService;
  @InjectMocks private TestUtil testUtil;
  @Mock private ErrorHandlingService errorHandlingService;
  @Mock private TransferScheduleFeign transferScheduleFeign;
  @Mock private TransferScheduleRepository transferScheduleRepository;

  @BeforeEach
  void init() {

    MockitoAnnotations.openMocks(this);
    ReflectionTestUtils.setField(
        transferScheduleBatchService, "errorHandlingService", errorHandlingService);
    ReflectionTestUtils.setField(
        transferScheduleBatchService, "transferScheduleFeign", transferScheduleFeign);

    ReflectionTestUtils.setField(
        transferScheduleBatchService, "transferScheduleRepository", transferScheduleRepository);
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JodaModule());
    ReflectionTestUtils.setField(transferScheduleBatchService, "objectMapper", objectMapper);
  }

  @Test
  @DisplayName("When transfer schedule feed with create action is processed successfully")
  void processBatchRecordsTestWhenActionIsCreate() {
    List<BatchRequest<TransferScheduleDto>> transferScheduleFeedRequests =
        List.of(testUtil.getTransferScheduleFeedRequest(ActionEnum.CREATE));
    Mockito.when(transferScheduleFeign.createTransferSchedule(any()))
        .thenReturn(
            testUtil.getBaseResponseOfTransferScheduleFeed(
                "Transfer Schedule created successfully"));
    ResponseDto responseDto =
        testUtil.createResponseDto(1, 200, "Transfer Schedule created successfully");
    responseDto.setMessage(
        testUtil
            .getBaseResponseOfTransitFeed("Transfer Schedule created successfully")
            .getMessage());
    List<ResponseDto> responseDtoList = List.of(responseDto);
    BatchResponse batchResponse = testUtil.getTransferScheduleBatchResponse(1, 1, 0);
    batchResponse.setResponses(responseDtoList);
    Mockito.doNothing()
        .when(errorHandlingService)
        .handleSuccessResponse(any(), any(), any(), any());
    Assertions.assertEquals(
        batchResponse,
        transferScheduleBatchService.processRecordsWithRetry(transferScheduleFeedRequests));
    verify(transferScheduleFeign, times(1)).createTransferSchedule(any());
  }

  @Test
  @DisplayName("When the batch records are processed successfully")
  void processBatchRecordsTestWithNoRetry() {
    List<BatchRequest<TransferScheduleDto>> transferScheduleBatchRequest =
        List.of(testUtil.getTransferScheduleFeedRequest(ActionEnum.CREATE));
    Mockito.when(transferScheduleFeign.createTransferSchedule(any()))
        .thenReturn(
            testUtil.getBaseResponseOfTransferScheduleFeed(
                "Transfer Schedule created successfully"));
    ResponseDto responseDto =
        testUtil.createResponseDto(1, 200, "Transfer Schedule created successfully");
    responseDto.setMessage(
        testUtil
            .getBaseResponseOfTransitFeed("Transfer Schedule created successfully")
            .getMessage());
    List<ResponseDto> responseDtoList = List.of(responseDto);
    BatchResponse batchResponse = testUtil.getTransferScheduleBatchResponse(1, 1, 0);
    batchResponse.setResponses(responseDtoList);
    Assertions.assertEquals(
        batchResponse,
        transferScheduleBatchService.processRecordsWithRetry(transferScheduleBatchRequest));
    verify(transferScheduleFeign, times(1)).createTransferSchedule(any());
  }

  @Test
  @DisplayName("When the batch records are processed for the update action")
  void processBatchRecordsTestWhenActionIsUpdate() {
    List<BatchRequest<TransferScheduleDto>> transferScheduleBatchRequest =
        List.of(testUtil.getTransferScheduleFeedRequest(ActionEnum.UPDATE));
    ResponseDto responseDto = testUtil.createResponseDto(1, 400, "Action not supported : UPDATE");
    responseDto.setMessage(
        testUtil
            .getBaseResponseOfTransferScheduleFeed("Action not supported : UPDATE")
            .getMessage());
    List<ResponseDto> responseDtoList = List.of(responseDto);
    BatchResponse batchResponse = testUtil.getTransferScheduleBatchResponse(1, 0, 1);
    batchResponse.setResponses(responseDtoList);
    Mockito.doNothing()
        .when(errorHandlingService)
        .handleSuccessResponse(any(), any(), any(), any());
    Assertions.assertEquals(
        batchResponse,
        transferScheduleBatchService.processRecordsWithRetry(transferScheduleBatchRequest));
  }

  @Test
  @DisplayName("When the batch records are processed for the delete action")
  void processBatchRecordsTestWhenActionIsDelete() {
    List<BatchRequest<TransferScheduleDto>> transferScheduleBatchRequest =
        List.of(testUtil.getTransferScheduleFeedRequest(ActionEnum.DELETE));
    Mockito.when(transferScheduleFeign.deleteTransferSchedule(any()))
        .thenReturn(
            testUtil.getBaseResponseOfTransferScheduleFeed(
                "Transfer Schedule deleted successfully"));
    ResponseDto responseDto =
        testUtil.createResponseDto(1, 200, "Transfer Schedule deleted successfully");
    responseDto.setMessage(
        testUtil
            .getBaseResponseOfTransitBufferFeed("Transfer Schedule deleted successfully")
            .getMessage());
    List<ResponseDto> responseDtoList = List.of(responseDto);
    BatchResponse batchResponse = testUtil.getTransitBatchResponse(1, 1, 0);
    batchResponse.setResponses(responseDtoList);
    Mockito.doNothing()
        .when(errorHandlingService)
        .handleSuccessResponse(any(), any(), any(), any());
    Assertions.assertEquals(
        batchResponse,
        transferScheduleBatchService.processRecordsWithRetry(transferScheduleBatchRequest));
    verify(transferScheduleFeign, times(1)).deleteTransferSchedule(any());
  }

  @Test
  @DisplayName("When the batch records are retried")
  void handleTransitRetryTest() throws CommonServiceException {
    BatchRequest<?> inputFeedRequest = testUtil.getTransferScheduleFeedRequest(ActionEnum.CREATE);
    Mockito.when(transferScheduleFeign.createTransferSchedule(any()))
        .thenReturn(
            testUtil.getBaseResponseOfTransferScheduleFeed(
                "Transfer Schedule created successfully"));
    String responseMessage = transferScheduleBatchService.handleRetry(inputFeedRequest);

    Assertions.assertEquals("Transfer Schedule created successfully", responseMessage);

    verify(transferScheduleFeign, times(1)).createTransferSchedule(any());
  }

  @Test
  @DisplayName("When the batch records cannot be processed as they are outdated")
  void processBatchRecordsTestForOutdatedRecords() throws CommonServiceException {
    BatchRequest<TransferScheduleDto> batchRequest =
        testUtil.getTransferScheduleFeedRequest(ActionEnum.CREATE);
    batchRequest.setReceivedTimestamp(new Date());
    List<BatchRequest<TransferScheduleDto>> transferScheduleFeedRequest = List.of(batchRequest);
    ResponseDto responseDto =
        testUtil.createResponseDto(
            1, HttpStatus.BAD_REQUEST.value(), "Can't process the record as it's outdated");
    List<ResponseDto> responseDtoList = List.of(responseDto);
    BatchResponse batchResponse = testUtil.getTransferScheduleBatchResponse(1, 0, 1);
    batchResponse.setResponses(responseDtoList);
    Mockito.doNothing()
        .when(errorHandlingService)
        .handleExceptions(anyInt(), any(), any(), any(), any(), any());
    Optional<TransferScheduleEntity> transferScheduleEntity =
        Optional.of(new TransferScheduleEntity());
    transferScheduleEntity.get().setLastModifiedDate(DateUtil.addDaysToDate(new Date(), 1));
    when(transferScheduleRepository.findBySourceNodeIdAndDropoffNodeIdAndStartTimeAndOrgId(
            any(), any(), any(), any()))
        .thenReturn(transferScheduleEntity);
    Assertions.assertEquals(
        batchResponse,
        transferScheduleBatchService.processRecordsWithRetry(transferScheduleFeedRequest));
    verify(transferScheduleFeign, times(0)).createTransferSchedule(any());
  }

  @Test
  @DisplayName("When the batch records cannot be processed as they are up to date")
  void processBatchRecordsTestForUpdatedRecords() throws CommonServiceException {
    BatchRequest<TransferScheduleDto> batchRequest =
        testUtil.getTransferScheduleFeedRequest(ActionEnum.CREATE);
    Date modifiedDate = new Date();
    Date receivedDate = DateUtil.addHoursToDate(modifiedDate, 1);
    batchRequest.setReceivedTimestamp(receivedDate);
    Mockito.doNothing()
        .when(errorHandlingService)
        .handleExceptions(anyInt(), any(), any(), any(), any(), any());
    TransferScheduleEntity transferEntityObj = new TransferScheduleEntity();
    transferEntityObj.setLastModifiedDate(modifiedDate);
    Optional<TransferScheduleEntity> transferScheduleEntity = Optional.of(transferEntityObj);
    when(transferScheduleRepository.findBySourceNodeIdAndDropoffNodeIdAndStartTimeAndOrgId(
            any(), any(), any(), any()))
        .thenReturn(transferScheduleEntity);
    transferScheduleBatchService.processRecordsWithRetry(List.of(batchRequest));
    verify(transferScheduleFeign, times(1)).createTransferSchedule(any());
  }

  @Test
  @DisplayName("When the batch records cannot be processed as they are up to date for rule")
  void processBatchRecordsTestForUpdatedRecordsForRule() throws CommonServiceException {
    BatchRequest<TransferScheduleDto> batchRequest =
        testUtil.getTransferScheduleFeedRequest(ActionEnum.CREATE);
    batchRequest.getPayload().setRule("rule");
    Date modifiedDate = new Date();
    Date receivedDate = DateUtil.addHoursToDate(modifiedDate, 1);
    batchRequest.setReceivedTimestamp(receivedDate);
    Mockito.doNothing()
        .when(errorHandlingService)
        .handleExceptions(anyInt(), any(), any(), any(), any(), any());
    TransferScheduleEntity transferEntityObj = new TransferScheduleEntity();
    transferEntityObj.setLastModifiedDate(modifiedDate);
    Optional<TransferScheduleEntity> transferScheduleEntity = Optional.of(transferEntityObj);
    when(transferScheduleRepository.findBySourceNodeIdAndDropoffNodeIdAndStartTimeAndOrgIdAndRule(
            any(), any(), any(), any(), any()))
        .thenReturn(transferScheduleEntity);
    transferScheduleBatchService.processRecordsWithRetry(List.of(batchRequest));
    verify(transferScheduleFeign, times(1)).createTransferSchedule(any());
  }

  @Test
  @DisplayName("When the required fields are not provided for the transfer schedule feed")
  void processBatchRecordsTestForOutdatedRecordsRequiredFieldsNotProvided() {
    BatchRequest<TransferScheduleDto> batchRequest =
        testUtil.getTransferScheduleFeedRequest(ActionEnum.CREATE);
    TransferScheduleDto transferScheduleDto = batchRequest.getPayload();
    transferScheduleDto.setOrgId(null);
    batchRequest.setReceivedTimestamp(new Date());
    transferScheduleBatchService.processRecordsWithRetry(List.of(batchRequest));
    transferScheduleDto.setOrgId("NEXTUPLE_GR");
    transferScheduleDto.setSourceNodeId(null);
    transferScheduleBatchService.processRecordsWithRetry(List.of(batchRequest));
    transferScheduleDto.setSourceNodeId("Node 1");
    transferScheduleDto.setDropoffNodeId(null);
    transferScheduleBatchService.processRecordsWithRetry(List.of(batchRequest));
    transferScheduleDto.setDropoffNodeId("Node 2");
    transferScheduleDto.setStartTime(null);
    transferScheduleBatchService.processRecordsWithRetry(List.of(batchRequest));
    verify(transferScheduleFeign, times(4)).createTransferSchedule(any());
  }

  @Test
  @DisplayName("When the transferEntity is not found for the transfer schedule feed")
  void processBatchRecordsTestForOutdatedRecordsTransferEntityNotFound() {
    BatchRequest<TransferScheduleDto> batchRequest =
        testUtil.getTransferScheduleFeedRequest(ActionEnum.CREATE);
    batchRequest.setReceivedTimestamp(new Date());
    when(transferScheduleRepository.findBySourceNodeIdAndDropoffNodeIdAndStartTimeAndOrgId(
            any(), any(), any(), any()))
        .thenReturn(Optional.empty());
    transferScheduleBatchService.processRecordsWithRetry(List.of(batchRequest));
    verify(transferScheduleFeign, times(1)).createTransferSchedule(any());
  }

  //  @Test
  //  @DisplayName("When the transferEntity is not found for the transfer schedule feed")
  //  void processBatchRecordsTestForOutdatedRecordsTransferEntityNotFound() {
  //    BatchRequest<TransferScheduleDto> batchRequest =
  //            testUtil.getTransferScheduleFeedRequest(ActionEnum.CREATE);
  //    when(transferScheduleRepository.findBySourceNodeIdAndDropoffNodeIdAndStartTimeAndOrgId(
  //            any(), any(), any(), any()))
  //            .thenReturn(Optional.empty());
  //    transferScheduleBatchService.processRecordsWithRetry(List.of(batchRequest));
  //    verify(transferScheduleFeign, times(1)).createTransferSchedule(any());
  //  }
}
