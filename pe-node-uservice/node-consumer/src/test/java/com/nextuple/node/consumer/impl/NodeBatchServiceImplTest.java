/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.node.consumer.impl;

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
import com.nextuple.node.consumer.TestUtil;
import com.nextuple.node.consumer.dto.NodeFeedDto;
import com.nextuple.node.domain.feign.NodeFeign;
import com.nextuple.node.persistence.domain.NodeDomainDto;
import com.nextuple.node.persistence.exception.NodeDomainException;
import com.nextuple.node.persistence.service.NodePersistenceService;
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
class NodeBatchServiceImplTest {

  @InjectMocks private NodeBatchServiceImpl nodeBatchService;
  @InjectMocks private TestUtil testUtil;
  @Mock private NodeFeign nodeFeign;
  @Mock private ErrorHandlingService errorHandlingService;
  @Mock private NodePersistenceService nodePersistenceService;

  @BeforeEach
  void init() {
    MockitoAnnotations.openMocks(this);

    ReflectionTestUtils.setField(nodeBatchService, "errorHandlingService", errorHandlingService);
  }

  @Test
  @DisplayName("When node feed with create action is processed successfully")
  void processBatchRecordsTestWhenActionIsCreate() {
    List<BatchRequest<NodeFeedDto>> nodeFeedRequests =
        List.of(testUtil.getNodeFeedRequest(ActionEnum.CREATE));
    Mockito.when(nodeFeign.createNode(any()))
        .thenReturn(testUtil.getBaseResponseOfNodeFeed("Node created successfully"));
    ResponseDto responseDto = testUtil.createResponseDto(1, 200, "Node created successfully");
    responseDto.setMessage(
        testUtil.getBaseResponseOfNodeFeed("Node created successfully").getMessage());
    List<ResponseDto> responseDtoList = List.of(responseDto);
    BatchResponse batchResponse = testUtil.getNodeBatchResponse(1, 1, 0);
    batchResponse.setResponses(responseDtoList);
    Mockito.doNothing()
        .when(errorHandlingService)
        .handleSuccessResponse(any(), any(), any(), any());
    Assertions.assertEquals(
        batchResponse, nodeBatchService.processRecordsWithRetry(nodeFeedRequests));
    verify(nodeFeign, times(1)).createNode(any());
  }

  @Test
  void processBatchRecordsTestWithNoRetry() {
    List<BatchRequest<NodeFeedDto>> nodeFeedRequests =
        List.of(testUtil.getNodeFeedRequest(ActionEnum.CREATE));
    Mockito.when(nodeFeign.createNode(any()))
        .thenReturn(testUtil.getBaseResponseOfNodeFeed("Node created successfully"));
    ResponseDto responseDto = testUtil.createResponseDto(1, 200, "Node created successfully");
    responseDto.setMessage(
        testUtil.getBaseResponseOfNodeFeed("Node created successfully").getMessage());
    List<ResponseDto> responseDtoList = List.of(responseDto);
    BatchResponse batchResponse = testUtil.getNodeBatchResponse(1, 1, 0);
    batchResponse.setResponses(responseDtoList);
    Assertions.assertEquals(
        batchResponse, nodeBatchService.processRecordsWithRetry(nodeFeedRequests));
    verify(nodeFeign, times(1)).createNode(any());
  }

  @Test
  void processBatchRecordsTestWhenActionIsUpdate() {
    List<BatchRequest<NodeFeedDto>> nodeFeedRequests =
        List.of(testUtil.getNodeFeedRequest(ActionEnum.UPDATE));
    Mockito.when(nodeFeign.updateNodeDetails(any(), any(), any()))
        .thenReturn(testUtil.getBaseResponseOfNodeFeed("Node details updated successfully"));
    ResponseDto responseDto =
        testUtil.createResponseDto(1, 200, "Node details updated successfully");
    responseDto.setMessage(
        testUtil.getBaseResponseOfNodeFeed("Node details updated successfully").getMessage());
    List<ResponseDto> responseDtoList = List.of(responseDto);
    BatchResponse batchResponse = testUtil.getNodeBatchResponse(1, 1, 0);
    batchResponse.setResponses(responseDtoList);
    Mockito.doNothing()
        .when(errorHandlingService)
        .handleSuccessResponse(any(), any(), any(), any());
    Assertions.assertEquals(
        batchResponse, nodeBatchService.processRecordsWithRetry(nodeFeedRequests));
    verify(nodeFeign, times(1)).updateNodeDetails(any(), any(), any());
  }

  @Test
  void processBatchRecordsTestWhenActionIsDelete() {
    List<BatchRequest<NodeFeedDto>> nodeFeedRequests =
        List.of(testUtil.getNodeFeedRequest(ActionEnum.DELETE));
    Mockito.when(nodeFeign.deleteNode(any(), any()))
        .thenReturn(testUtil.getBaseResponseOfNodeFeed("Node deleted successfully"));
    ResponseDto responseDto = testUtil.createResponseDto(1, 200, "Node deleted successfully");
    responseDto.setMessage(
        testUtil.getBaseResponseOfNodeFeed("Node deleted successfully").getMessage());
    List<ResponseDto> responseDtoList = List.of(responseDto);
    BatchResponse batchResponse = testUtil.getNodeBatchResponse(1, 1, 0);
    batchResponse.setResponses(responseDtoList);
    Mockito.doNothing()
        .when(errorHandlingService)
        .handleSuccessResponse(any(), any(), any(), any());
    Assertions.assertEquals(
        batchResponse, nodeBatchService.processRecordsWithRetry(nodeFeedRequests));
    verify(nodeFeign, times(1)).deleteNode(any(), any());
  }

  @Test
  void processBatchRecordsTestForOutdatedRecords()
      throws CommonServiceException, NodeDomainException {
    BatchRequest<NodeFeedDto> batchRequest = testUtil.getNodeFeedRequest(ActionEnum.UPDATE);
    batchRequest.setReceivedTimestamp(new Date());
    List<BatchRequest<NodeFeedDto>> nodeFeedRequests = List.of(batchRequest);
    ResponseDto responseDto =
        testUtil.createResponseDto(
            1, HttpStatus.BAD_REQUEST.value(), "Can't process the record as it's outdated");
    List<ResponseDto> responseDtoList = List.of(responseDto);
    BatchResponse batchResponse = testUtil.getNodeBatchResponse(1, 0, 1);
    batchResponse.setResponses(responseDtoList);
    Mockito.doNothing()
        .when(errorHandlingService)
        .handleExceptions(anyInt(), any(), any(), any(), any(), any());
    NodeDomainDto nodeDomainDto = testUtil.getNodeDomainDto();
    nodeDomainDto.setLastModifiedDate(DateUtil.addDaysToDate(new Date(), 1));
    when(nodePersistenceService.findNodeByNodeIdAndOrgId(anyString(), anyString()))
        .thenReturn(Optional.of(nodeDomainDto));
    Assertions.assertEquals(
        batchResponse, nodeBatchService.processRecordsWithRetry(nodeFeedRequests));
    verify(nodeFeign, times(0)).createNode(any());
  }

  @Test
  void handleNodeRetryTest() throws CommonServiceException {
    BatchRequest<?> inputFeedRequest = testUtil.getNodeFeedRequest(ActionEnum.CREATE);
    Mockito.when(nodeFeign.createNode(any()))
        .thenReturn(testUtil.getBaseResponseOfNodeFeed("Node created successfully"));
    String responseMessage = nodeBatchService.handleRetry(inputFeedRequest);

    Assertions.assertEquals("Node created successfully", responseMessage);

    verify(nodeFeign, times(1)).createNode(any());
  }
}
