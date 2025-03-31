/*
 * Copyright (c) 2025., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.transit.consumer.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.master.data.integration.enums.ActionEnum;
import com.nextuple.master.data.integration.inbound.BatchRequest;
import com.nextuple.master.data.integration.service.ErrorHandlingService;
import com.nextuple.transit.consumer.TestUtil;
import com.nextuple.transit.domain.feign.TransferScheduleFeign;
import com.nextuple.transit.persistence.repository.TransferScheduleRepository;
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
import org.springframework.test.util.ReflectionTestUtils;

@RunWith(MockitoJUnitRunner.class)
public class TransferScheduleBatchServiceImplTest {
  @InjectMocks private TransferScheduleBatchServiceImpl transferScheduleBatchService;
  @InjectMocks private TestUtil testUtil;
  @Mock private ErrorHandlingService errorHandlingService;
  @Mock private TransferScheduleFeign transferScheduleFeign;
  @Mock private TransferScheduleRepository transferScheduleRepository;

  @BeforeEach
  void init() {

    MockitoAnnotations.openMocks(this);
    ReflectionTestUtils.setField(
        transferScheduleBatchService, "errorHandlingService", errorHandlingService);
    ReflectionTestUtils.setField(
        transferScheduleBatchService, "transferScheduleFeign", transferScheduleFeign);
    ReflectionTestUtils.setField(transferScheduleBatchService, "defaultApplyValidation", true);

    ReflectionTestUtils.setField(
        transferScheduleBatchService, "transferScheduleRepository", transferScheduleRepository);
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JodaModule());
    ReflectionTestUtils.setField(transferScheduleBatchService, "objectMapper", objectMapper);
  }

  @Test
  @DisplayName("When the batch records are retried")
  void handleTransitRetryTest() throws CommonServiceException {
    BatchRequest<?> inputFeedRequest = testUtil.getTransferScheduleFeedRequest(ActionEnum.CREATE);
    Mockito.when(transferScheduleFeign.createTransferSchedule(any()))
        .thenReturn(
            testUtil.getBaseResponseOfTransferScheduleFeed(
                "Transfer Schedule created successfully"));
    String responseMessage = transferScheduleBatchService.handleRetry(inputFeedRequest);

    Assertions.assertEquals("Transfer Schedule created successfully", responseMessage);

    verify(transferScheduleFeign, times(1)).createTransferSchedule(any());
  }

  //  @Test
  //  @DisplayName("When the transferEntity is not found for the transfer schedule feed")
  //  void processBatchRecordsTestForOutdatedRecordsTransferEntityNotFound() {
  //    BatchRequest<TransferScheduleDto> batchRequest =
  //            testUtil.getTransferScheduleFeedRequest(ActionEnum.CREATE);
  //    when(transferScheduleRepository.findBySourceNodeIdAndDropoffNodeIdAndStartTimeAndOrgId(
  //            any(), any(), any(), any()))
  //            .thenReturn(Optional.empty());
  //    transferScheduleBatchService.processRecordsWithRetry(List.of(batchRequest));
  //    verify(transferScheduleFeign, times(1)).createTransferSchedule(any());
  //  }
}
