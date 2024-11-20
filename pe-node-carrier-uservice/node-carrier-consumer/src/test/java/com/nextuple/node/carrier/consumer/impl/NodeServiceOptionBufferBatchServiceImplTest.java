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

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.util.DateUtil;
import com.nextuple.master.data.integration.dto.ResponseDto;
import com.nextuple.master.data.integration.enums.ActionEnum;
import com.nextuple.master.data.integration.inbound.BatchRequest;
import com.nextuple.master.data.integration.outbound.BatchResponse;
import com.nextuple.master.data.integration.service.ErrorHandlingService;
import com.nextuple.node.carrier.consumer.TestUtil;
import com.nextuple.node.carrier.consumer.dto.NodeServiceOptionBufferFeedDto;
import com.nextuple.node.carrier.domain.entity.NodeServiceOptionBufferEntity;
import com.nextuple.node.carrier.domain.feign.NodeServiceOptionBufferFeign;
import com.nextuple.node.carrier.repository.NodeServiceOptionBufferRepository;
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
class NodeServiceOptionBufferBatchServiceImplTest {

  @InjectMocks private NodeServiceOptionBufferBatchServiceImpl nodeServiceOptionBufferBatchService;
  @InjectMocks private TestUtil testUtil;
  @Mock private NodeServiceOptionBufferFeign nodeServiceOptionBufferFeign;
  @Mock private ErrorHandlingService errorHandlingService;
  @Mock private NodeServiceOptionBufferRepository nodeServiceOptionBufferRepository;

  @BeforeEach
  void init() {
    MockitoAnnotations.openMocks(this);

    ReflectionTestUtils.setField(
        nodeServiceOptionBufferBatchService, "errorHandlingService", errorHandlingService);
  }

  @Test
  @DisplayName("When the batch records are processed for the create action")
  void processBatchRecordsTestWhenActionIsCreate() {
    List<BatchRequest<NodeServiceOptionBufferFeedDto>> nodeServiceOptionBufferFeedRequests =
        List.of(testUtil.getNodeServiceOptionBufferFeedRequest(ActionEnum.CREATE));
    Mockito.when(nodeServiceOptionBufferFeign.createNodeServiceOptionBuffer(any()))
        .thenReturn(
            testUtil.getBaseResponseOfNodeServiceOptionBufferFeed(
                "Node service option buffer added successfully"));
    ResponseDto responseDto =
        testUtil.createResponseDto(1, 200, "Node service option buffer added successfully");
    List<ResponseDto> responseDtoList = List.of(responseDto);
    BatchResponse batchResponse = testUtil.getBatchApiResponse(1, 1, 0);
    batchResponse.setResponses(responseDtoList);
    Mockito.doNothing()
        .when(errorHandlingService)
        .handleSuccessResponse(any(), any(), any(), any());
    Assertions.assertEquals(
        batchResponse,
        nodeServiceOptionBufferBatchService.processRecordsWithRetry(
            nodeServiceOptionBufferFeedRequests));
    verify(nodeServiceOptionBufferFeign, times(1)).createNodeServiceOptionBuffer(any());
  }

  @Test
  @DisplayName("When the batch records are processed successfully")
  void processBatchRecordsTestWithNoRetry() {
    List<BatchRequest<NodeServiceOptionBufferFeedDto>> nodeServiceOptionBufferFeedRequests =
        List.of(testUtil.getNodeServiceOptionBufferFeedRequest(ActionEnum.CREATE));
    Mockito.when(nodeServiceOptionBufferFeign.createNodeServiceOptionBuffer(any()))
        .thenReturn(
            testUtil.getBaseResponseOfNodeServiceOptionBufferFeed(
                "Node service option buffer added successfully"));
    ResponseDto responseDto =
        testUtil.createResponseDto(1, 200, "Node service option buffer added successfully");
    List<ResponseDto> responseDtoList = List.of(responseDto);
    BatchResponse batchResponse = testUtil.getBatchApiResponse(1, 1, 0);
    batchResponse.setResponses(responseDtoList);
    Assertions.assertEquals(
        batchResponse,
        nodeServiceOptionBufferBatchService.processRecordsWithRetry(
            nodeServiceOptionBufferFeedRequests));
    verify(nodeServiceOptionBufferFeign, times(1)).createNodeServiceOptionBuffer(any());
  }

  @Test
  @DisplayName("When the batch records are processed for delete action")
  void processBatchRecordsTestWhenActionIsDelete() {
    List<BatchRequest<NodeServiceOptionBufferFeedDto>> nodeServiceOptionBufferFeedRequests =
        List.of(testUtil.getNodeServiceOptionBufferFeedRequest(ActionEnum.DELETE));
    Mockito.when(nodeServiceOptionBufferFeign.deleteNodeServiceOptionBuffer(any()))
        .thenReturn(
            testUtil.getBaseResponseOfNodeServiceOptionBufferFeed(
                "Node service option buffer deleted successfully"));
    ResponseDto responseDto =
        testUtil.createResponseDto(1, 200, "Node service option buffer deleted successfully");
    List<ResponseDto> responseDtoList = List.of(responseDto);
    BatchResponse batchResponse = testUtil.getBatchApiResponse(1, 1, 0);
    batchResponse.setResponses(responseDtoList);
    Mockito.doNothing()
        .when(errorHandlingService)
        .handleSuccessResponse(any(), any(), any(), any());
    Assertions.assertEquals(
        batchResponse,
        nodeServiceOptionBufferBatchService.processRecordsWithRetry(
            nodeServiceOptionBufferFeedRequests));
    verify(nodeServiceOptionBufferFeign, times(1)).deleteNodeServiceOptionBuffer(any());
  }

  @Test
  @DisplayName("When the batch records cannot be processed for the outdated records")
  void processBatchRecordsTestForOutdatedRecords() {
    BatchRequest<NodeServiceOptionBufferFeedDto> batchRequest =
        testUtil.getNodeServiceOptionBufferFeedRequest(ActionEnum.CREATE);
    batchRequest.setReceivedTimestamp(new Date());
    List<BatchRequest<NodeServiceOptionBufferFeedDto>> nodeServiceOptionBufferFeedRequests =
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
    NodeServiceOptionBufferEntity nodeServiceOptionBufferEntity =
        testUtil.getNodeServiceOptionBufferEntity();
    nodeServiceOptionBufferEntity.setLastModifiedDate(DateUtil.addDaysToDate(new Date(), 1));
    when(nodeServiceOptionBufferRepository
            .findByOrgIdAndNodeIdAndServiceOptionAndBufferStartDateAndBufferEndDate(
                anyString(), anyString(), anyString(), any(), any()))
        .thenReturn(Optional.of(nodeServiceOptionBufferEntity));
    Assertions.assertEquals(
        batchResponse,
        nodeServiceOptionBufferBatchService.processRecordsWithRetry(
            nodeServiceOptionBufferFeedRequests));
    verify(nodeServiceOptionBufferFeign, times(0)).createNodeServiceOptionBuffer(any());
  }

  @Test
  @DisplayName("When the batch records are retried")
  void handleNodeServiceOptionBufferRetryTest() throws CommonServiceException {
    BatchRequest<?> inputFeedRequest =
        testUtil.getNodeServiceOptionBufferFeedRequest(ActionEnum.CREATE);
    Mockito.when(nodeServiceOptionBufferFeign.createNodeServiceOptionBuffer(any()))
        .thenReturn(
            testUtil.getBaseResponseOfNodeServiceOptionBufferFeed(
                "Node service option buffer added successfully"));
    String responseMessage = nodeServiceOptionBufferBatchService.handleRetry(inputFeedRequest);

    Assertions.assertEquals("Node service option buffer added successfully", responseMessage);

    verify(nodeServiceOptionBufferFeign, times(1)).createNodeServiceOptionBuffer(any());
  }

  @Test
  @DisplayName("Delete node service option buffer")
  void deleteRecordImplTest() {
    when(nodeServiceOptionBufferFeign.deleteNodeServiceOptionBuffer(any()))
        .thenReturn(
            testUtil.getBaseResponseOfNodeServiceOptionBufferFeed(
                "Node service option buffer deleted successfully"));
    Assertions.assertEquals(
        "Node service option buffer deleted successfully",
        nodeServiceOptionBufferBatchService.deleteRecordImpl(
            testUtil.getNodeServiceOptionBufferFeedRequest(ActionEnum.DELETE).getPayload()));
    verify(nodeServiceOptionBufferFeign, times(1)).deleteNodeServiceOptionBuffer(any());
  }
}
