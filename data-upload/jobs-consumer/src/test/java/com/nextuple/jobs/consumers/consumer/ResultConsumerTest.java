package com.nextuple.jobs.consumers.consumer;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nextuple.jobs.consumers.common.TestUtil;
import com.nextuple.jobs.consumers.exception.JobException;
import com.nextuple.jobs.consumers.feign.AuthTokenAPI;
import com.nextuple.jobs.consumers.service.JobConsumerService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ResultConsumerTest {

  @Mock private JobConsumerService jobConsumerService;

  @Mock private AuthTokenAPI authTokenAPI;

  @InjectMocks private ResultConsumer resultConsumer;

  @InjectMocks private TestUtil testUtil;

  @Test
  void receiveResultFromAnotherConsumer() throws JobException {
    doNothing().when(jobConsumerService).updateJobStatus(any());
    when(authTokenAPI.getAuthToken(any())).thenReturn(testUtil.getAuthTokenResponse());

    assertDoesNotThrow(() -> resultConsumer.receiveResultFromAnotherConsumer(null, null));
    verify(jobConsumerService, times(1)).updateJobStatus(any());
  }

  @Test
  void receiveResultFromAnotherConsumerError() throws JobException {
    doThrow(RuntimeException.class).when(jobConsumerService).updateJobStatus(any());
    when(authTokenAPI.getAuthToken(any())).thenReturn(testUtil.getAuthTokenResponse());

    JobException e =
        assertThrows(
            JobException.class, () -> resultConsumer.receiveResultFromAnotherConsumer(null, null));
    Assertions.assertEquals("Exception while persisting the job record", e.getMessage());
    assertNull(e.getJobId());
    verify(jobConsumerService, times(1)).updateJobStatus(any());
  }
}
