/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.transit.service;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.jobs.framework.common.domain.enums.JobStatusEnum;
import com.nextuple.transit.TestUtil;
import com.nextuple.transit.domain.enums.TransitBufferConfigRequestStatusEnum;
import com.nextuple.transit.domain.enums.TransitBufferReqJobRefEnum;
import com.nextuple.transit.persistence.exception.TransitBufferReqJobRefDomainException;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class TransitBufferConsumerServiceTest {

  @InjectMocks private TransitBufferConsumerService transitBufferConsumerService;

  @InjectMocks private TestUtil testUtil;

  @Mock private TransitBufferConfigRequestService transitBufferConfigRequestService;

  @Mock private TransitBufferReqJobRefService transitBufferReqJobRefService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void processJobRecordForTransitBufferTest1()
      throws CommonServiceException, TransitBufferReqJobRefDomainException {
    when(transitBufferReqJobRefService.getTransitBufferReqJobRefByExtReferenceId(anyString()))
        .thenReturn(
            List.of(testUtil.getTransBufferReqJobRefResponse1(TransitBufferReqJobRefEnum.CREATE)));
    when(transitBufferConfigRequestService.getTransitBufferRequest(anyLong()))
        .thenReturn(
            testUtil.getTransitBufferConfigRequestDomainDto(
                TransitBufferConfigRequestStatusEnum.INPROGRESS));
    transitBufferConsumerService.processJobRecordForTransitBuffer(
        testUtil.getJobDetailsDto(JobStatusEnum.COMPLETED));
    verify(transitBufferConfigRequestService, times(1)).getTransitBufferRequest(anyLong());
  }

  @Test
  void processJobRecordForTransitBufferTest2()
      throws CommonServiceException, TransitBufferReqJobRefDomainException {
    when(transitBufferReqJobRefService.getTransitBufferReqJobRefByExtReferenceId(anyString()))
        .thenReturn(
            List.of(testUtil.getTransBufferReqJobRefResponse1(TransitBufferReqJobRefEnum.UPDATE)));
    when(transitBufferConfigRequestService.getTransitBufferRequest(anyLong()))
        .thenReturn(
            testUtil.getTransitBufferConfigRequestDomainDto(
                TransitBufferConfigRequestStatusEnum.INPROGRESS));
    transitBufferConsumerService.processJobRecordForTransitBuffer(
        testUtil.getJobDetailsDto(JobStatusEnum.COMPLETED));
    verify(transitBufferConfigRequestService, times(1)).getTransitBufferRequest(anyLong());
  }

  @Test
  void processJobRecordForTransitBufferTest3()
      throws CommonServiceException, TransitBufferReqJobRefDomainException {
    when(transitBufferReqJobRefService.getTransitBufferReqJobRefByExtReferenceId(anyString()))
        .thenReturn(
            List.of(testUtil.getTransBufferReqJobRefResponse1(TransitBufferReqJobRefEnum.DELETE)));
    when(transitBufferConfigRequestService.getTransitBufferRequest(anyLong()))
        .thenReturn(
            testUtil.getTransitBufferConfigRequestDomainDto(
                TransitBufferConfigRequestStatusEnum.INPROGRESS));
    transitBufferConsumerService.processJobRecordForTransitBuffer(
        testUtil.getJobDetailsDto(JobStatusEnum.COMPLETED));
    verify(transitBufferConfigRequestService, times(1)).getTransitBufferRequest(anyLong());
  }

  @Test
  void processJobRecordForTransitBufferTest4()
      throws CommonServiceException, TransitBufferReqJobRefDomainException {
    when(transitBufferReqJobRefService.getTransitBufferReqJobRefByExtReferenceId(anyString()))
        .thenReturn(
            List.of(testUtil.getTransBufferReqJobRefResponse1(TransitBufferReqJobRefEnum.CREATE)));
    when(transitBufferConfigRequestService.getTransitBufferRequest(anyLong()))
        .thenReturn(
            testUtil.getTransitBufferConfigRequestDomainDto(
                TransitBufferConfigRequestStatusEnum.INPROGRESS));
    transitBufferConsumerService.processJobRecordForTransitBuffer(
        testUtil.getJobDetailsDto(JobStatusEnum.FAILED));
    verify(transitBufferConfigRequestService, times(1)).getTransitBufferRequest(anyLong());
  }
}
