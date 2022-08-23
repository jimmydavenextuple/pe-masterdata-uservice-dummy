package com.hbc.jobs.consumers.consumer;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.hbc.jobs.consumers.exception.JobException;
import com.hbc.jobs.consumers.service.JobService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ResultConsumerTest {

  @Mock private JobService jobService;

  @InjectMocks private ResultConsumer resultConsumer;

  @Test
  void receiveResultFromAnotherConsumer() throws JobException {
    doNothing().when(jobService).updateJobStatus(any());

    assertDoesNotThrow(() -> resultConsumer.receiveResultFromAnotherConsumer(null, null));
    verify(jobService, times(1)).updateJobStatus(any());
  }

  @Test
  void receiveResultFromAnotherConsumerError() throws JobException {
    doThrow(RuntimeException.class).when(jobService).updateJobStatus(any());

    JobException e =
        assertThrows(
            JobException.class, () -> resultConsumer.receiveResultFromAnotherConsumer(null, null));
    Assertions.assertEquals("Exception while persisting the job record", e.getMessage());
    assertNull(e.getJobId());
    verify(jobService, times(1)).updateJobStatus(any());
  }
}
