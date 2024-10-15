/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.transit.consumer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.jobs.framework.common.domain.enums.JobStatusEnum;
import com.nextuple.jobs.framework.common.domain.pojo.JobDetailsDto;
import com.nextuple.transit.TestUtil;
import com.nextuple.transit.persistence.exception.TransitBufferJobException;
import com.nextuple.transit.persistence.exception.TransitBufferReqJobRefDomainException;
import com.nextuple.transit.service.TransitBufferConsumerService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class TransitBufferConsumerTest {

  @InjectMocks private TransitBufferConsumer transitBufferConsumer;
  @InjectMocks private TestUtil testUtil;

  @Mock private TransitBufferConsumerService transitBufferConsumerService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void transitBufferConsumerTest()
      throws CommonServiceException,
          TransitBufferReqJobRefDomainException,
          TransitBufferJobException {
    doNothing()
        .when(transitBufferConsumerService)
        .processJobRecordForTransitBuffer(any(JobDetailsDto.class));

    transitBufferConsumer.receiveRecordForTransitBuffer(
        testUtil.getJobDetailsDto(JobStatusEnum.COMPLETED), null);
    verify(transitBufferConsumerService, times(1)).processJobRecordForTransitBuffer(any());
  }

  @Test
  void transitBufferConsumerExceptionTest()
      throws CommonServiceException, TransitBufferReqJobRefDomainException {
    doThrow(new RuntimeException("error"))
        .when(transitBufferConsumerService)
        .processJobRecordForTransitBuffer(any(JobDetailsDto.class));

    Exception exception =
        Assertions.assertThrows(
            Exception.class,
            () ->
                transitBufferConsumer.receiveRecordForTransitBuffer(
                    testUtil.getJobDetailsDto(JobStatusEnum.COMPLETED), null));
    Assertions.assertEquals(
        "Exception while receiving the job record from the kafka producer for transit buffer",
        exception.getMessage());
    verify(transitBufferConsumerService, times(1)).processJobRecordForTransitBuffer(any());
  }
}
