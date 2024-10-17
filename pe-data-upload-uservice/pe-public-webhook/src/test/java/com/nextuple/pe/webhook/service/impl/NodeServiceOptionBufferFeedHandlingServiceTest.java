/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.pe.webhook.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nextuple.master.data.integration.dto.ResponseDto;
import com.nextuple.master.data.integration.enums.ActionEnum;
import com.nextuple.master.data.integration.inbound.BatchRequest;
import com.nextuple.master.data.integration.outbound.BatchResponse;
import com.nextuple.node.carrier.consumer.dto.NodeServiceOptionBufferFeedDto;
import com.nextuple.node.carrier.consumer.impl.NodeServiceOptionBufferBatchServiceImpl;
import com.nextuple.pe.webhook.domain.dtos.MasterDataIngestionDto;
import com.nextuple.pe.webhook.domain.inbound.FeedRequest;
import com.nextuple.pe.webhook.util.TestUtil;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

class NodeServiceOptionBufferFeedHandlingServiceTest {
  @Mock NodeServiceOptionBufferBatchServiceImpl nodeServiceOptionBufferBatchService;
  @Mock NodeServiceOptionBufferFeedIngestionService nodeServiceOptionBufferFeedIngestionService;

  @InjectMocks
  NodeServiceOptionBufferFeedHandlingService nodeServiceOptionBufferFeedHandlingService;

  @InjectMocks TestUtil testUtil;
  @Mock ObjectMapper objectMapper;

  @BeforeEach
  public void init() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  @DisplayName("When the node service option buffer records are processed through batch request")
  void processNodeServiceOptionBufferRecordsTest() {
    ResponseDto responseDto =
        testUtil.createResponseDto(1, 200, "Node service option buffer created successfully");
    responseDto.setMessage("Node service option buffer created successfully");
    List<ResponseDto> responseDtoList = List.of(responseDto);
    BatchResponse batchResponse = testUtil.getBatchResponse(1, 1, 0);
    batchResponse.setResponses(responseDtoList);
    BatchRequest<?> batchRequest =
        testUtil.getNodeServiceOptionBufferFeedRequest(ActionEnum.CREATE);
    List<BatchRequest<?>> batchFeed = Collections.singletonList(batchRequest);
    List<BatchRequest<NodeServiceOptionBufferFeedDto>> nodeServiceOptionBufferFeedRequest =
        Collections.singletonList(
            testUtil.getNodeServiceOptionBufferFeedRequest(ActionEnum.CREATE));
    Mockito.when(objectMapper.convertValue(Mockito.eq(batchFeed), Mockito.any(TypeReference.class)))
        .thenReturn(nodeServiceOptionBufferFeedRequest);

    Mockito.when(nodeServiceOptionBufferBatchService.processRecordsWithoutRetry(any()))
        .thenReturn(batchResponse);

    BatchResponse result =
        nodeServiceOptionBufferFeedHandlingService.processRecords(batchFeed, TestUtil.ORG_ID);
    assertEquals(batchResponse, result);

    Mockito.verify(nodeServiceOptionBufferBatchService).processRecordsWithoutRetry(any());
  }

  @Test
  @DisplayName("When the node service option buffer feed is ingested successfully")
  void publishNodeServiceOptionBufferRecordsTest() {
    FeedRequest<MasterDataIngestionDto<?>> batchRequest =
        testUtil.getNodeServiceOptionBufferFeedIngestionRequest(ActionEnum.CREATE);

    Mockito.doNothing()
        .when(nodeServiceOptionBufferFeedIngestionService)
        .publishFeedToKafka(any(), any());
    Mockito.when(
            objectMapper.convertValue(Mockito.eq(batchRequest), Mockito.any(TypeReference.class)))
        .thenReturn(testUtil.getNodeServiceOptionBufferFeedIngestionRequest(ActionEnum.CREATE));

    Assertions.assertDoesNotThrow(
        () ->
            nodeServiceOptionBufferFeedHandlingService.publishRecords(
                batchRequest, TestUtil.ORG_ID));

    Mockito.verify(nodeServiceOptionBufferFeedIngestionService).publishFeedToKafka(any(), any());
  }
}
