package com.hbc.jobs.consumers.consumer;

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

import com.hbc.jobs.consumers.exception.JobException;
import com.hbc.jobs.consumers.service.JobConsumerService;
import com.hbc.jobs.framework.common.domain.pojo.JobDto;
import com.hbc.jobs.framework.common.domain.pojo.RecordDto;
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

  @Test
  void receiveRecordFromDashboardProducer() throws JobException {
    doNothing().when(jobConsumerService).processRecord(any());

    assertDoesNotThrow(() -> taskConsumer.receiveRecordFromDashboardProducer(null, null));
    verify(jobConsumerService, times(1)).processRecord(any());
  }

  @Test
  void receiveRecordFromDashboardProducerError() throws JobException {
    RecordDto record = mock(RecordDto.class);

    when(record.getJob()).thenReturn(mock(JobDto.class));
    doThrow(RuntimeException.class).when(jobConsumerService).processRecord(any());

    JobException e =
        assertThrows(
            JobException.class,
            () -> taskConsumer.receiveRecordFromDashboardProducer(record, null));
    Assertions.assertEquals(
        "Exception while receiving the job record from the kafka producer", e.getMessage());
    assertNull(e.getJobId());
    verify(jobConsumerService, times(1)).processRecord(any());
  }
}
