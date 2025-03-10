/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.transit.consumer.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.nextuple.master.data.integration.dto.ResponseDto;
import com.nextuple.master.data.integration.enums.ActionEnum;
import com.nextuple.master.data.integration.inbound.BatchRequest;
import com.nextuple.master.data.integration.outbound.BatchResponse;
import com.nextuple.transit.consumer.TestUtil;
import com.nextuple.transit.consumer.dto.TransferScheduleDto;
import com.nextuple.transit.consumer.impl.TransferScheduleBatchServiceImpl;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class TransferScheduleConsumerTest {

  @InjectMocks private TransferScheduleConsumer transferScheduleConsumer;
  @Mock private TransferScheduleBatchServiceImpl transferScheduleBatchService;
  @InjectMocks TestUtil testUtil;

  @Test
  @DisplayName("When the transfer schedule feed gets consumed successfully.")
  void consumeMasterDataFeedTest() {
    List<BatchRequest<TransferScheduleDto>> transferScheduleFeedRequest =
        List.of(testUtil.getTransferScheduleFeedRequest(ActionEnum.CREATE));
    ResponseDto responseDto =
        testUtil.createResponseDto(1, 200, "Transfer Schedule created successfully");
    List<ResponseDto> responseDtoList = List.of(responseDto);
    BatchResponse batchResponse = testUtil.getTransferScheduleBatchResponse(1, 1, 0);
    batchResponse.setResponses(responseDtoList);
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JodaModule());
    ReflectionTestUtils.setField(transferScheduleConsumer, "objectMapper", objectMapper);
    Mockito.when(transferScheduleBatchService.processRecordsWithRetry(Mockito.any()))
        .thenReturn(batchResponse);
    transferScheduleConsumer.consumeTransferScheduleFeed(transferScheduleFeedRequest, null);

    Mockito.verify(transferScheduleBatchService, Mockito.times(1))
        .processRecordsWithRetry(Mockito.any());
  }
}
