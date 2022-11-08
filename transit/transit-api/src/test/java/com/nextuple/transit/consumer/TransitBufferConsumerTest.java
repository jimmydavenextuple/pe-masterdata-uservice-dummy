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
import com.nextuple.transit.exception.TransitBufferJobException;
import com.nextuple.transit.exception.TransitBufferReqJobRefDomainException;
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
      throws CommonServiceException, TransitBufferReqJobRefDomainException,
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
