/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.calendar.consumer.consumer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.nextuple.calendar.consumer.TestUtil;
import com.nextuple.calendar.consumer.dto.NodeCalendarFeedDto;
import com.nextuple.calendar.consumer.impl.NodeCalendarBatchServiceImpl;
import com.nextuple.common.enums.ActionEnum;
import com.nextuple.master.data.integration.dto.ResponseDto;
import com.nextuple.master.data.integration.inbound.BatchRequest;
import com.nextuple.master.data.integration.outbound.BatchResponse;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class NodeCalendarConsumerTest {

  @InjectMocks private NodeCalendarConsumer nodeCalendarConsumer;
  @Mock private NodeCalendarBatchServiceImpl nodeCalendarBatchService;
  @InjectMocks private TestUtil testUtil;

  @Test
  @DisplayName("When the node calendar feed gets consumed successfully.")
  void consumeMasterDataFeedTest() {
    List<BatchRequest<NodeCalendarFeedDto>> nodeCalendarFeedRequests =
        List.of(testUtil.getNodeCalendarFeedRequest(ActionEnum.CREATE));
    ResponseDto responseDto =
        testUtil.createResponseDto(1, 200, "Node Calendar created successfully");
    List<ResponseDto> responseDtoList = List.of(responseDto);
    BatchResponse batchResponse = testUtil.getCalendarBatchResponse(1, 1, 0);
    batchResponse.setResponses(responseDtoList);
    when(nodeCalendarBatchService.processRecordsWithRetry(any())).thenReturn(batchResponse);
    nodeCalendarConsumer.consumeNodeCalendarFeed(nodeCalendarFeedRequests, null);

    verify(nodeCalendarBatchService, times(1)).processRecordsWithRetry(any());
  }
}
