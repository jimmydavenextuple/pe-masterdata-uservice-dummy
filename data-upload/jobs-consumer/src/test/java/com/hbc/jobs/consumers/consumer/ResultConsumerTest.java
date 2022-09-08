package com.hbc.jobs.consumers.consumer;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hbc.jobs.consumers.exception.JobException;
import com.hbc.jobs.consumers.service.JobConsumerService;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.support.converter.KafkaMessageHeaders;

@ExtendWith(MockitoExtension.class)
class ResultConsumerTest {

  @Mock private JobConsumerService jobConsumerService;

  @Mock private KafkaMessageHeaders headers;

  @InjectMocks private ResultConsumer resultConsumer;

  @Test
  void receiveResultFromAnotherConsumer() throws JobException {
    doNothing().when(jobConsumerService).updateJobStatus(any());
    when(headers.getRawHeaders()).thenReturn(Map.of("jwtToken", "token"));

    assertDoesNotThrow(() -> resultConsumer.receiveResultFromAnotherConsumer(null, headers));
    verify(jobConsumerService, times(1)).updateJobStatus(any());
  }

  @Test
  void receiveResultFromAnotherConsumerError() throws JobException {
    doThrow(RuntimeException.class).when(jobConsumerService).updateJobStatus(any());
    when(headers.getRawHeaders()).thenReturn(Map.of("jwtToken", "token"));

    JobException e =
        assertThrows(
            JobException.class,
            () -> resultConsumer.receiveResultFromAnotherConsumer(null, headers));
    Assertions.assertEquals("Exception while persisting the job record", e.getMessage());
    assertNull(e.getJobId());
    verify(jobConsumerService, times(1)).updateJobStatus(any());
  }
}
