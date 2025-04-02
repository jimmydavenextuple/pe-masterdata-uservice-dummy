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
import com.nextuple.common.enums.ActionEnum;
import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.master.data.integration.dto.ResponseDto;
import com.nextuple.master.data.integration.inbound.BatchRequest;
import com.nextuple.master.data.integration.outbound.BatchResponse;
import com.nextuple.master.data.integration.service.ErrorHandlingService;
import com.nextuple.transit.consumer.TestUtil;
import com.nextuple.transit.consumer.dto.TransferScheduleDto;
import com.nextuple.transit.domain.feign.TransferScheduleFeign;
import com.nextuple.transit.persistence.entity.TransferScheduleEntity;
import com.nextuple.transit.persistence.repository.TransferScheduleRepository;
import java.util.*;
import java.util.List;
import org.joda.time.DateTime;
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
    ReflectionTestUtils.setField(transferScheduleBatchService, "defaultApplyValidation", true);

    ReflectionTestUtils.setField(
        transferScheduleBatchService, "transferScheduleRepository", transferScheduleRepository);
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JodaModule());
    ReflectionTestUtils.setField(transferScheduleBatchService, "objectMapper", objectMapper);
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
  @DisplayName("When transfer schedule feed with create action is processed successfully")
  void processBatchRecordsTestWhenActionIsCreate() {
    List<BatchRequest<TransferScheduleDto>> transferScheduleFeedRequests =
        List.of(testUtil.getTransferScheduleFeedRequest(ActionEnum.CREATE));
    Mockito.when(transferScheduleFeign.batchTransferSchedules(any(), any()))
        .thenReturn(
            testUtil.getFeignTransferScheduleBatchResponse(
                "Transfer Schedule batch created successfully"));
    ResponseDto responseDto =
        testUtil.createResponseDto(0, 200, "Transfer Schedule created successfully");
    List<ResponseDto> responseDtoList = List.of(responseDto);
    BatchResponse batchResponse = testUtil.getTransferScheduleBatchResponse(1, 1, 0);
    batchResponse.setResponses(responseDtoList);
    BatchResponse result =
        transferScheduleBatchService.processRecordsWithRetry(transferScheduleFeedRequests);
    Assertions.assertEquals(batchResponse, result);
    verify(transferScheduleFeign, times(1)).batchTransferSchedules(any(), any());
  }

  @Test
  @DisplayName("When an error occurs while processing batch request")
  void processBatchRecordsTestWhenErrorOccurs() {
    List<BatchRequest<TransferScheduleDto>> transferScheduleFeedRequests =
        List.of(testUtil.getTransferScheduleFeedRequest(ActionEnum.CREATE));
    Mockito.when(transferScheduleFeign.batchTransferSchedules(any(), any()))
        .thenThrow(new RuntimeException("Feign client error"));

    BatchResponse result =
        transferScheduleBatchService.processBatchRecords(transferScheduleFeedRequests, true);

    Assertions.assertEquals(1, result.getFailedRecords());
    Assertions.assertEquals(0, result.getSuccessfulRecords());
    Assertions.assertEquals(
        "Error occurred while processing batch request: Feign client error",
        result.getResponses().get(0).getMessage());
    verify(transferScheduleFeign, times(1)).batchTransferSchedules(any(), any());
  }

  @Test
  @DisplayName("When a transfer schedule is created successfully")
  void createRecordImplTest() throws CommonServiceException {
    TransferScheduleDto transferScheduleDto = new TransferScheduleDto();
    // Set necessary fields for transferScheduleDto
    transferScheduleDto.setOrgId("org123");
    transferScheduleDto.setSourceNodeId("sourceNode123");
    transferScheduleDto.setDropoffNodeId("dropoffNode123");
    transferScheduleDto.setStartTime(new DateTime());
    Mockito.when(transferScheduleFeign.createTransferSchedule(any()))
        .thenReturn(
            testUtil.getBaseResponseOfTransferScheduleFeed(
                "Transfer Schedule created successfully"));

    String responseMessage = transferScheduleBatchService.createRecordImpl(transferScheduleDto);

    Assertions.assertEquals("Transfer Schedule created successfully", responseMessage);
    verify(transferScheduleFeign, times(1)).createTransferSchedule(any());
  }

  @Test
  @DisplayName("When updateRecordImpl is called, it should throw CommonServiceException")
  void updateRecordImplTest() {
    TransferScheduleDto transferScheduleDto = new TransferScheduleDto();
    // Set necessary fields for transferScheduleDto
    transferScheduleDto.setOrgId("org123");
    transferScheduleDto.setSourceNodeId("sourceNode123");
    transferScheduleDto.setDropoffNodeId("dropoffNode123");
    transferScheduleDto.setStartTime(new DateTime());

    CommonServiceException exception =
        Assertions.assertThrows(
            CommonServiceException.class,
            () -> transferScheduleBatchService.updateRecordImpl(transferScheduleDto));

    Assertions.assertEquals("Action not supported : UPDATE", exception.getMessage());
  }

  @Test
  @DisplayName("When a transfer schedule is deleted successfully")
  void deleteRecordImplTest() throws CommonServiceException {
    TransferScheduleDto transferScheduleDto = new TransferScheduleDto();
    // Set necessary fields for transferScheduleDto
    transferScheduleDto.setOrgId("org123");
    transferScheduleDto.setSourceNodeId("sourceNode123");
    transferScheduleDto.setDropoffNodeId("dropoffNode123");
    transferScheduleDto.setStartTime(new DateTime());

    Mockito.when(transferScheduleFeign.deleteTransferSchedule(any()))
        .thenReturn(
            testUtil.getBaseResponseOfTransferScheduleFeed(
                "Transfer Schedule deleted successfully"));

    String responseMessage = transferScheduleBatchService.deleteRecordImpl(transferScheduleDto);

    Assertions.assertEquals("Transfer Schedule deleted successfully", responseMessage);
    verify(transferScheduleFeign, times(1)).deleteTransferSchedule(any());
  }

  @Test
  @DisplayName("When the record is not outdated")
  void checkForOutdatedRecordTestNotOutdated() throws CommonServiceException {
    TransferScheduleDto transferScheduleDto = new TransferScheduleDto();
    transferScheduleDto.setOrgId("org123");
    transferScheduleDto.setSourceNodeId("sourceNode123");
    transferScheduleDto.setDropoffNodeId("dropoffNode123");
    transferScheduleDto.setStartTime(new DateTime());
    BatchRequest<TransferScheduleDto> batchRequest = new BatchRequest<>();
    batchRequest.setPayload(transferScheduleDto);
    batchRequest.setReceivedTimestamp(new Date(System.currentTimeMillis() + 3600 * 1000));

    TransferScheduleEntity transferScheduleEntity = new TransferScheduleEntity();
    transferScheduleEntity.setLastModifiedDate(new Date());

    Mockito.when(
            transferScheduleRepository.findBySourceNodeIdAndDropoffNodeIdAndStartTimeAndOrgId(
                anyString(), anyString(), any(Date.class), anyString()))
        .thenReturn(Optional.of(transferScheduleEntity));

    Assertions.assertDoesNotThrow(
        () -> transferScheduleBatchService.checkForOutdatedRecord(batchRequest));
  }

  @Test
  @DisplayName("When the record is outdated")
  void checkForOutdatedRecordTestOutdated() {
    TransferScheduleDto transferScheduleDto = new TransferScheduleDto();
    transferScheduleDto.setOrgId("org123");
    transferScheduleDto.setSourceNodeId("sourceNode123");
    transferScheduleDto.setDropoffNodeId("dropoffNode123");
    transferScheduleDto.setStartTime(new DateTime());
    BatchRequest<TransferScheduleDto> batchRequest = new BatchRequest<>();
    batchRequest.setPayload(transferScheduleDto);
    batchRequest.setReceivedTimestamp(new Date());

    TransferScheduleEntity transferScheduleEntity = new TransferScheduleEntity();
    transferScheduleEntity.setLastModifiedDate(new Date(System.currentTimeMillis() + 10000));

    Mockito.when(
            transferScheduleRepository.findBySourceNodeIdAndDropoffNodeIdAndStartTimeAndOrgId(
                anyString(), anyString(), any(Date.class), anyString()))
        .thenReturn(Optional.of(transferScheduleEntity));

    CommonServiceException exception =
        Assertions.assertThrows(
            CommonServiceException.class,
            () -> transferScheduleBatchService.checkForOutdatedRecord(batchRequest));

    Assertions.assertEquals("Can't process the record as it's outdated", exception.getMessage());
  }

  @Test
  @DisplayName("When required fields are missing")
  void checkForOutdatedRecordTestMissingFields() {
    TransferScheduleDto transferScheduleDto = new TransferScheduleDto();
    BatchRequest<TransferScheduleDto> batchRequest = new BatchRequest<>();
    batchRequest.setPayload(transferScheduleDto);

    Assertions.assertDoesNotThrow(
        () -> transferScheduleBatchService.checkForOutdatedRecord(batchRequest));
  }
}
