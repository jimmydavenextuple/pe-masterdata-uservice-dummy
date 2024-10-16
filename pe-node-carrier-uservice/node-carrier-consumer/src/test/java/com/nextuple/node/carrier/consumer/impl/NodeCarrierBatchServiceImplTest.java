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
import com.nextuple.node.carrier.consumer.dto.NodeCarrierFeedDto;
import com.nextuple.node.carrier.domain.entity.NodeCarriersEntity;
import com.nextuple.node.carrier.domain.feign.NodeCarriersFeign;
import com.nextuple.node.carrier.repository.NodeCarriersRepository;
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
class NodeCarrierBatchServiceImplTest {

  @InjectMocks private NodeCarrierBatchServiceImpl nodeCarrierBatchService;
  @InjectMocks private TestUtil testUtil;
  @Mock private NodeCarriersFeign nodeCarrierFeign;
  @Mock private ErrorHandlingService errorHandlingService;
  @Mock private NodeCarriersRepository nodeCarriersRepository;

  @BeforeEach
  void init() {
    MockitoAnnotations.openMocks(this);

    ReflectionTestUtils.setField(
        nodeCarrierBatchService, "errorHandlingService", errorHandlingService);
  }

  @Test
  @DisplayName("When the batch records are processed for the create action")
  void processBatchRecordsTestWhenActionIsCreate() {
    List<BatchRequest<NodeCarrierFeedDto>> nodeCarrierFeedRequests =
        List.of(testUtil.getNodeCarrierFeedRequest(ActionEnum.CREATE));
    Mockito.when(nodeCarrierFeign.createNodeCarrier(any()))
        .thenReturn(testUtil.getBaseResponseOfNodeCarrierFeed("Node carrier created successfully"));
    ResponseDto responseDto =
        testUtil.createResponseDto(1, 200, "Node carrier created successfully");
    List<ResponseDto> responseDtoList = List.of(responseDto);
    BatchResponse batchResponse = testUtil.getBatchApiResponse(1, 1, 0);
    batchResponse.setResponses(responseDtoList);
    Mockito.doNothing()
        .when(errorHandlingService)
        .handleSuccessResponse(any(), any(), any(), any());
    Assertions.assertEquals(
        batchResponse, nodeCarrierBatchService.processRecordsWithRetry(nodeCarrierFeedRequests));
    verify(nodeCarrierFeign, times(1)).createNodeCarrier(any());
  }

  @Test
  @DisplayName("When the batch records are processed successfully")
  void processBatchRecordsTestWithNoRetry() {
    List<BatchRequest<NodeCarrierFeedDto>> nodeCarrierFeedRequests =
        List.of(testUtil.getNodeCarrierFeedRequest(ActionEnum.CREATE));
    Mockito.when(nodeCarrierFeign.createNodeCarrier(any()))
        .thenReturn(testUtil.getBaseResponseOfNodeCarrierFeed("Node carrier created successfully"));
    ResponseDto responseDto =
        testUtil.createResponseDto(1, 200, "Node carrier created successfully");
    List<ResponseDto> responseDtoList = List.of(responseDto);
    BatchResponse batchResponse = testUtil.getBatchApiResponse(1, 1, 0);
    batchResponse.setResponses(responseDtoList);
    Assertions.assertEquals(
        batchResponse, nodeCarrierBatchService.processRecordsWithRetry(nodeCarrierFeedRequests));
    verify(nodeCarrierFeign, times(1)).createNodeCarrier(any());
  }

  @Test
  @DisplayName("When the batch records are processed for update action.")
  void processBatchRecordsTestWhenActionIsUpdate() {
    List<BatchRequest<NodeCarrierFeedDto>> nodeCarrierFeedRequests =
        List.of(testUtil.getNodeCarrierFeedRequest(ActionEnum.UPDATE));
    Mockito.when(nodeCarrierFeign.updateNodeCarrier(any(), any(), any(), any(), any()))
        .thenReturn(
            testUtil.getBaseResponseOfNodeCarrierFeed("Node carrier details updated successfully"));
    ResponseDto responseDto =
        testUtil.createResponseDto(1, 200, "Node carrier details updated successfully");
    List<ResponseDto> responseDtoList = List.of(responseDto);
    BatchResponse batchResponse = testUtil.getBatchApiResponse(1, 1, 0);
    batchResponse.setResponses(responseDtoList);
    Mockito.doNothing()
        .when(errorHandlingService)
        .handleSuccessResponse(any(), any(), any(), any());
    Assertions.assertEquals(
        batchResponse, nodeCarrierBatchService.processRecordsWithRetry(nodeCarrierFeedRequests));
    verify(nodeCarrierFeign, times(1)).updateNodeCarrier(any(), any(), any(), any(), any());
  }

  @Test
  @DisplayName("When the batch records are processed for delete action.")
  void processBatchRecordsTestWhenActionIsDelete() {
    List<BatchRequest<NodeCarrierFeedDto>> nodeCarrierFeedRequests =
        List.of(testUtil.getNodeCarrierFeedRequest(ActionEnum.DELETE));
    Mockito.when(nodeCarrierFeign.deleteNodeCarrier(any(), any(), any(), any()))
        .thenReturn(testUtil.getBaseResponseOfNodeCarrierFeed("Node carrier deleted successfully"));
    ResponseDto responseDto =
        testUtil.createResponseDto(1, 200, "Node carrier deleted successfully");
    List<ResponseDto> responseDtoList = List.of(responseDto);
    BatchResponse batchResponse = testUtil.getBatchApiResponse(1, 1, 0);
    batchResponse.setResponses(responseDtoList);
    Mockito.doNothing()
        .when(errorHandlingService)
        .handleSuccessResponse(any(), any(), any(), any());
    Assertions.assertEquals(
        batchResponse, nodeCarrierBatchService.processRecordsWithRetry(nodeCarrierFeedRequests));
    verify(nodeCarrierFeign, times(1)).deleteNodeCarrier(any(), any(), any(), any());
  }

  @Test
  @DisplayName("When the batch records cannot be processed as they are outdated")
  void processBatchRecordsTestForOutdatedRecords() {
    BatchRequest<NodeCarrierFeedDto> batchRequest =
        testUtil.getNodeCarrierFeedRequest(ActionEnum.UPDATE);
    batchRequest.setReceivedTimestamp(new Date());
    List<BatchRequest<NodeCarrierFeedDto>> nodeCarrierFeedRequests = List.of(batchRequest);
    ResponseDto responseDto =
        testUtil.createResponseDto(
            1, HttpStatus.BAD_REQUEST.value(), "Can't process the record as it's outdated");
    List<ResponseDto> responseDtoList = List.of(responseDto);
    BatchResponse batchResponse = testUtil.getBatchApiResponse(1, 0, 1);
    batchResponse.setResponses(responseDtoList);
    Mockito.doNothing()
        .when(errorHandlingService)
        .handleExceptions(anyInt(), any(), any(), any(), any(), any());
    NodeCarriersEntity nodeEntity = testUtil.getNodeCarriersEntity();
    nodeEntity.setLastModifiedDate(DateUtil.addDaysToDate(new Date(), 1));
    when(nodeCarriersRepository.findByOrgIdAndNodeIdAndCarrierServiceIdAndServiceOption(
            anyString(), anyString(), anyString(), anyString()))
        .thenReturn(Optional.of(nodeEntity));
    Assertions.assertEquals(
        batchResponse, nodeCarrierBatchService.processRecordsWithRetry(nodeCarrierFeedRequests));
    verify(nodeCarrierFeign, times(0)).createNodeCarrier(any());
  }

  @Test
  @DisplayName("When the batch records are retried")
  void handleNodeCarrierRetryTest() throws CommonServiceException {
    BatchRequest<?> inputFeedRequest = testUtil.getNodeCarrierFeedRequest(ActionEnum.CREATE);
    Mockito.when(nodeCarrierFeign.createNodeCarrier(any()))
        .thenReturn(testUtil.getBaseResponseOfNodeCarrierFeed("Node carrier created successfully"));
    String responseMessage = nodeCarrierBatchService.handleRetry(inputFeedRequest);

    Assertions.assertEquals("Node carrier created successfully", responseMessage);

    verify(nodeCarrierFeign, times(1)).createNodeCarrier(any());
  }
}
