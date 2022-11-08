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
import com.nextuple.transit.exception.TransitBufferReqJobRefDomainException;
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
            testUtil.getTransitBufferConfigRequestEntity(
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
            testUtil.getTransitBufferConfigRequestEntity(
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
            testUtil.getTransitBufferConfigRequestEntity(
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
            testUtil.getTransitBufferConfigRequestEntity(
                TransitBufferConfigRequestStatusEnum.INPROGRESS));
    transitBufferConsumerService.processJobRecordForTransitBuffer(
        testUtil.getJobDetailsDto(JobStatusEnum.FAILED));
    verify(transitBufferConfigRequestService, times(1)).getTransitBufferRequest(anyLong());
  }
}
