/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.jobs.consumers.consumer;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nextuple.jobs.consumers.authentication.AuthServiceAWS;
import com.nextuple.jobs.consumers.common.TestUtil;
import com.nextuple.jobs.consumers.exception.InvalidJobTypeException;
import com.nextuple.jobs.consumers.exception.JobException;
import com.nextuple.jobs.consumers.service.JobConsumerService;
import com.nextuple.jobs.framework.common.domain.pojo.RecordDto;
import com.nextuple.jobs.framework.common.domain.pojo.RecordStatusDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TaskConsumerTest {

  @Mock private JobConsumerService jobConsumerService;
  @InjectMocks private TaskConsumer taskConsumer;
  @Mock private AuthServiceAWS authServiceAWS;
  @InjectMocks private TestUtil testUtil;

  @Test
  void receiveRecordFromDashboardProducer() throws JobException, InvalidJobTypeException {
    doNothing().when(authServiceAWS).checkAuthExpiry(any());
    when(jobConsumerService.executeTask(any())).thenReturn(RecordStatusDto.builder().build());
    doNothing().when(jobConsumerService).updateJobStatus(any());
    assertDoesNotThrow(() -> taskConsumer.receiveRecordFromDashboardProducer(null, null));
    verify(jobConsumerService, times(1)).updateJobStatus(any());
  }

  @Test
  void receiveRecordFromDashboardProducerAzure() throws JobException, InvalidJobTypeException {
    when(jobConsumerService.executeTask(any())).thenReturn(RecordStatusDto.builder().build());
    doNothing().when(jobConsumerService).updateJobStatus(any());
    assertDoesNotThrow(() -> taskConsumer.receiveRecordFromDashboardProducer(null, null));
    verify(jobConsumerService, times(1)).updateJobStatus(any());
  }

  @Test
  void receiveRecordFromDashboardProducerError() throws JobException, InvalidJobTypeException {
    RecordDto record = mock(RecordDto.class);
    doThrow(RuntimeException.class).when(jobConsumerService).executeTask(any());
    JobException e =
        assertThrows(
            JobException.class,
            () -> taskConsumer.receiveRecordFromDashboardProducer(record, null));
    Assertions.assertEquals(
        "Exception while receiving the job record from the kafka producer", e.getMessage());
    assertNull(e.getJobId());
    verify(jobConsumerService, times(1)).executeTask(any());
  }
}
