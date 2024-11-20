/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.pe.webhook.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.master.data.integration.dto.ResponseDto;
import com.nextuple.master.data.integration.inbound.BatchRequest;
import com.nextuple.master.data.integration.outbound.BatchResponse;
import com.nextuple.pe.webhook.service.BatchProcessingService;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class BatchControllerTest {
  @Mock BatchProcessingService batchProcessingService;
  @InjectMocks BatchController batchController;
  @InjectMocks TestUtil testUtil;

  @BeforeEach
  public void init() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void batchApiTest() throws CommonServiceException {
    ResponseDto responseDto = testUtil.createResponseDto(1, 200, "Node created successfully");
    responseDto.setMessage("Node created successfully");
    List<ResponseDto> responseDtoList = List.of(responseDto);
    BatchResponse batchResponse = testUtil.getBatchResponse(1, 1, 0);
    batchResponse.setResponses(responseDtoList);
    Mockito.when(batchProcessingService.processRecords(any(), any(), any()))
        .thenReturn(batchResponse);
    List<BatchRequest<?>> batchFeed = Collections.singletonList(new BatchRequest<>());
    ResponseEntity<BaseResponse<BatchResponse>> responseEntity =
        batchController.batchApi(TestUtil.ORG_ID, "nodes", batchFeed);

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertNotNull(responseEntity.getBody());
    assertEquals("Batch call processed successfully", responseEntity.getBody().getMessage());
    assertEquals(batchResponse, responseEntity.getBody().getPayload());

    Mockito.verify(batchProcessingService).processRecords(any(), any(), any());
  }

  @Test
  void batchApiExceptionTest() throws CommonServiceException {
    Mockito.when(batchProcessingService.processRecords(any(), any(), any()))
        .thenThrow(new RuntimeException("Failed to process batch call"));
    List<BatchRequest<?>> batchFeed = Collections.singletonList(new BatchRequest<>());
    Exception exception =
        Assertions.assertThrows(
            Exception.class, () -> batchController.batchApi(TestUtil.ORG_ID, "nodes", batchFeed));
    Assertions.assertEquals("Failed to process batch call", exception.getMessage());
    Mockito.verify(batchProcessingService).processRecords(any(), any(), any());
  }
}
