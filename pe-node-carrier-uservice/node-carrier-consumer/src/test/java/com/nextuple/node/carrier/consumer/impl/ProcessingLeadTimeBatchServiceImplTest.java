/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.node.carrier.consumer.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.nextuple.common.enums.ActionEnum;
import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.util.DateUtil;
import com.nextuple.master.data.integration.dto.ResponseDto;
import com.nextuple.master.data.integration.inbound.BatchRequest;
import com.nextuple.master.data.integration.outbound.BatchResponse;
import com.nextuple.master.data.integration.service.ErrorHandlingService;
import com.nextuple.node.carrier.consumer.TestUtil;
import com.nextuple.node.carrier.consumer.dto.ProcessingLeadTimeFeedDto;
import com.nextuple.node.carrier.domain.entity.NodeServiceOptionEntity;
import com.nextuple.node.carrier.domain.feign.NodeServiceOptionsFeign;
import com.nextuple.node.carrier.repository.NodeServiceOptionRepository;
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
class ProcessingLeadTimeBatchServiceImplTest {

  @InjectMocks private ProcessingLeadTimeBatchServiceImpl processingLeadTimeBatchService;
  @InjectMocks private TestUtil testUtil;
  @Mock private NodeServiceOptionsFeign nodeServiceOptionFeign;
  @Mock private ErrorHandlingService errorHandlingService;
  @Mock private NodeServiceOptionRepository nodeServiceOptionRepository;

  @BeforeEach
  void init() {
    MockitoAnnotations.openMocks(this);

    ReflectionTestUtils.setField(
        processingLeadTimeBatchService, "errorHandlingService", errorHandlingService);
  }

  @Test
  @DisplayName("When the batch records are processed for the create action")
  void processBatchRecordsTestWhenActionIsCreate() {
    List<BatchRequest<ProcessingLeadTimeFeedDto>> processingLeadTimeFeedRequests =
        List.of(testUtil.getProcessingLeadTimeFeedRequest(ActionEnum.CREATE));
    Mockito.when(nodeServiceOptionFeign.createNodeServiceOption(any()))
        .thenReturn(
            testUtil.getBaseResponseOfNodeServiceOptionFeed(
                "Processing lead time added successfully"));
    ResponseDto responseDto =
        testUtil.createResponseDto(1, 200, "Processing lead time added successfully");
    List<ResponseDto> responseDtoList = List.of(responseDto);
    BatchResponse batchResponse = testUtil.getBatchApiResponse(1, 1, 0);
    batchResponse.setResponses(responseDtoList);
    Mockito.doNothing()
        .when(errorHandlingService)
        .handleSuccessResponse(any(), any(), any(), any());
    Assertions.assertEquals(
        batchResponse,
        processingLeadTimeBatchService.processRecordsWithRetry(processingLeadTimeFeedRequests));
    verify(nodeServiceOptionFeign, times(1)).createNodeServiceOption(any());
  }

  @Test
  @DisplayName("When the batch records are processed successfully")
  void processBatchRecordsTestWithNoRetry() {
    List<BatchRequest<ProcessingLeadTimeFeedDto>> processingLeadTimeFeedRequests =
        List.of(testUtil.getProcessingLeadTimeFeedRequest(ActionEnum.CREATE));
    Mockito.when(nodeServiceOptionFeign.createNodeServiceOption(any()))
        .thenReturn(
            testUtil.getBaseResponseOfNodeServiceOptionFeed(
                "Processing lead time added successfully"));
    ResponseDto responseDto =
        testUtil.createResponseDto(1, 200, "Processing lead time added successfully");
    List<ResponseDto> responseDtoList = List.of(responseDto);
    BatchResponse batchResponse = testUtil.getBatchApiResponse(1, 1, 0);
    batchResponse.setResponses(responseDtoList);
    Assertions.assertEquals(
        batchResponse,
        processingLeadTimeBatchService.processRecordsWithRetry(processingLeadTimeFeedRequests));
    verify(nodeServiceOptionFeign, times(1)).createNodeServiceOption(any());
  }

  @Test
  @DisplayName("When the batch records are processed for update action")
  void processBatchRecordsTestWhenActionIsUpdate() {
    List<BatchRequest<ProcessingLeadTimeFeedDto>> processingLeadTimeFeedRequests =
        List.of(testUtil.getProcessingLeadTimeFeedRequest(ActionEnum.UPDATE));
    Mockito.when(nodeServiceOptionFeign.updateNodeServiceOption(any(), any(), any(), any()))
        .thenReturn(
            testUtil.getBaseResponseOfNodeServiceOptionFeed(
                "Processing lead time updated successfully"));
    ResponseDto responseDto =
        testUtil.createResponseDto(1, 200, "Processing lead time updated successfully");
    List<ResponseDto> responseDtoList = List.of(responseDto);
    BatchResponse batchResponse = testUtil.getBatchApiResponse(1, 1, 0);
    batchResponse.setResponses(responseDtoList);
    Mockito.doNothing()
        .when(errorHandlingService)
        .handleSuccessResponse(any(), any(), any(), any());
    Assertions.assertEquals(
        batchResponse,
        processingLeadTimeBatchService.processRecordsWithRetry(processingLeadTimeFeedRequests));
    verify(nodeServiceOptionFeign, times(1)).updateNodeServiceOption(any(), any(), any(), any());
  }

  @Test
  @DisplayName("When the batch records are processed for delete action")
  void processBatchRecordsTestWhenActionIsDelete() {
    List<BatchRequest<ProcessingLeadTimeFeedDto>> processingLeadTimeFeedRequests =
        List.of(testUtil.getProcessingLeadTimeFeedRequest(ActionEnum.DELETE));
    Mockito.when(nodeServiceOptionFeign.deleteNodeServiceOption(any(), any(), any()))
        .thenReturn(
            testUtil.getBaseResponseOfNodeServiceOptionFeed(
                "Processing lead time deleted successfully"));
    ResponseDto responseDto =
        testUtil.createResponseDto(1, 200, "Processing lead time deleted successfully");
    List<ResponseDto> responseDtoList = List.of(responseDto);
    BatchResponse batchResponse = testUtil.getBatchApiResponse(1, 1, 0);
    batchResponse.setResponses(responseDtoList);
    Mockito.doNothing()
        .when(errorHandlingService)
        .handleSuccessResponse(any(), any(), any(), any());
    Assertions.assertEquals(
        batchResponse,
        processingLeadTimeBatchService.processRecordsWithRetry(processingLeadTimeFeedRequests));
    verify(nodeServiceOptionFeign, times(1)).deleteNodeServiceOption(any(), any(), any());
  }

  @Test
  @DisplayName("When the batch records cannot be processed as they are outdated")
  void processBatchRecordsTestForOutdatedRecords() {
    BatchRequest<ProcessingLeadTimeFeedDto> batchRequest =
        testUtil.getProcessingLeadTimeFeedRequest(ActionEnum.UPDATE);
    batchRequest.setReceivedTimestamp(new Date());
    List<BatchRequest<ProcessingLeadTimeFeedDto>> processingLeadTimeFeedRequests =
        List.of(batchRequest);
    ResponseDto responseDto =
        testUtil.createResponseDto(
            1, HttpStatus.BAD_REQUEST.value(), "Can't process the record as it's outdated");
    List<ResponseDto> responseDtoList = List.of(responseDto);
    BatchResponse batchResponse = testUtil.getBatchApiResponse(1, 0, 1);
    batchResponse.setResponses(responseDtoList);
    Mockito.doNothing()
        .when(errorHandlingService)
        .handleExceptions(anyInt(), any(), any(), any(), any(), any());
    NodeServiceOptionEntity nodeServiceOptionEntity = testUtil.getNodeServiceOptionEntity();
    nodeServiceOptionEntity.setLastModifiedDate(DateUtil.addDaysToDate(new Date(), 1));
    when(nodeServiceOptionRepository.findByOrgIdAndNodeIdAndServiceOption(
            anyString(), anyString(), anyString()))
        .thenReturn(Optional.of(nodeServiceOptionEntity));
    Assertions.assertEquals(
        batchResponse,
        processingLeadTimeBatchService.processRecordsWithRetry(processingLeadTimeFeedRequests));
    verify(nodeServiceOptionFeign, times(0)).updateNodeServiceOption(any(), any(), any(), any());
  }

  @Test
  @DisplayName("When the batch records are retried")
  void handleProcessingLeadTimeRetryTest() throws CommonServiceException {
    BatchRequest<?> inputFeedRequest = testUtil.getProcessingLeadTimeFeedRequest(ActionEnum.CREATE);
    Mockito.when(nodeServiceOptionFeign.createNodeServiceOption(any()))
        .thenReturn(
            testUtil.getBaseResponseOfNodeServiceOptionFeed(
                "Processing lead time added successfully"));
    String responseMessage = processingLeadTimeBatchService.handleRetry(inputFeedRequest);

    Assertions.assertEquals("Processing lead time added successfully", responseMessage);

    verify(nodeServiceOptionFeign, times(1)).createNodeServiceOption(any());
  }
}
