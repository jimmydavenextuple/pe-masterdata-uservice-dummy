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
import com.nextuple.calendar.consumer.dto.PickupCalendarFeedDto;
import com.nextuple.calendar.consumer.impl.PickupCalendarBatchServiceImpl;
import com.nextuple.master.data.integration.dto.ResponseDto;
import com.nextuple.master.data.integration.enums.ActionEnum;
import com.nextuple.master.data.integration.inbound.BatchRequest;
import com.nextuple.master.data.integration.outbound.BatchResponse;
import com.nextuple.pe.webhook.domain.dtos.MasterDataIngestionDto;
import com.nextuple.pe.webhook.domain.inbound.FeedRequest;
import com.nextuple.pe.webhook.util.TestUtil;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

class TransitBufferFeedHandlingServiceTest {
  @Mock PickupCalendarBatchServiceImpl pickupCalendarBatchService;
  @Mock PickupCalendarFeedIngestionService pickupCalendarFeedIngestionService;
  @InjectMocks PickupCalendarFeedHandlingService pickupCalendarFeedHandlingService;
  @InjectMocks TestUtil testUtil;
  @Mock ObjectMapper objectMapper;

  @BeforeEach
  public void init() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void processPickupCalendarRecordsTest() {
    ResponseDto responseDto =
        testUtil.createResponseDto(1, 200, "Pickup Calendar created successfully");
    responseDto.setMessage("Pickup Calendar created successfully");
    List<ResponseDto> responseDtoList = List.of(responseDto);
    BatchResponse batchResponse = testUtil.getBatchResponse(1, 1, 0);
    batchResponse.setResponses(responseDtoList);
    BatchRequest<?> batchRequest = testUtil.getPickupCalendarFeedRequest(ActionEnum.CREATE);
    List<BatchRequest<?>> batchFeed = Collections.singletonList(batchRequest);
    List<BatchRequest<PickupCalendarFeedDto>> calendarBatchFeed =
        Collections.singletonList(testUtil.getPickupCalendarFeedRequest(ActionEnum.CREATE));
    Mockito.when(objectMapper.convertValue(Mockito.eq(batchFeed), Mockito.any(TypeReference.class)))
        .thenReturn(calendarBatchFeed);

    Mockito.when(pickupCalendarBatchService.processRecordsWithoutRetry(any()))
        .thenReturn(batchResponse);

    BatchResponse result =
        pickupCalendarFeedHandlingService.processRecords(batchFeed, TestUtil.ORG_ID);
    assertEquals(batchResponse, result);

    Mockito.verify(pickupCalendarBatchService).processRecordsWithoutRetry(any());
  }

  @Test
  void publishPickupCalendarRecordsTest() {
    FeedRequest<MasterDataIngestionDto<?>> batchRequest =
        testUtil.getPickupCalendarFeedIngestionRequest(ActionEnum.CREATE);

    Mockito.doNothing().when(pickupCalendarFeedIngestionService).publishFeedToKafka(any(), any());
    Mockito.when(
            objectMapper.convertValue(Mockito.eq(batchRequest), Mockito.any(TypeReference.class)))
        .thenReturn(testUtil.getNodeFeedIngestionRequest(ActionEnum.CREATE));

    Assertions.assertDoesNotThrow(
        () -> pickupCalendarFeedHandlingService.publishRecords(batchRequest, TestUtil.ORG_ID));

    Mockito.verify(pickupCalendarFeedIngestionService).publishFeedToKafka(any(), any());
  }
}
