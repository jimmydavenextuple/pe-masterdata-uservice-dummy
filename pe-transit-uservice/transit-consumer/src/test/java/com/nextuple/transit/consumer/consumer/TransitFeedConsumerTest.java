/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.transit.consumer.consumer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nextuple.common.enums.ActionEnum;
import com.nextuple.master.data.integration.dto.ResponseDto;
import com.nextuple.master.data.integration.inbound.BatchRequest;
import com.nextuple.master.data.integration.outbound.BatchResponse;
import com.nextuple.transit.consumer.TestUtil;
import com.nextuple.transit.consumer.dto.TransitFeedDto;
import com.nextuple.transit.consumer.impl.TransitBatchServiceImpl;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TransitFeedConsumerTest {

  @InjectMocks private TransitFeedConsumer transitFeedConsumer;
  @Mock private TransitBatchServiceImpl transitBatchService;
  @InjectMocks private TestUtil testUtil;
  @Mock ObjectMapper objectMapper;

  @Test
  @DisplayName("When the transit feed gets consumed successfully.")
  void consumeMasterDataFeedTest() {
    List<BatchRequest<TransitFeedDto>> transitFeedRequests =
        List.of(testUtil.getTransitFeedRequest(ActionEnum.CREATE));
    ResponseDto responseDto = testUtil.createResponseDto(1, 200, "Transit created successfully");
    List<ResponseDto> responseDtoList = List.of(responseDto);
    BatchResponse batchResponse = testUtil.getTransitBatchResponse(1, 1, 0);
    batchResponse.setResponses(responseDtoList);
    when(transitBatchService.processRecordsWithRetry(any())).thenReturn(batchResponse);
    transitFeedConsumer.consumeTransitFeed(transitFeedRequests, null);

    verify(transitBatchService, times(1)).processRecordsWithRetry(any());
  }
}
