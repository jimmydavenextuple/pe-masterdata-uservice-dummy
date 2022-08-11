package com.hbc.jobs.consumers.consumer;

import com.hbc.jobs.consumers.exception.JobException;
import com.hbc.jobs.consumers.service.JobService;
import com.hbc.jobs.framework.common.domain.pojo.RecordDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.converter.KafkaMessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@KafkaListener(
    topics = "${jobs-framework.record-consumer.topic-name}",
    groupId = "${jobs-framework.record-consumer.group-id}")
@Component
@Slf4j
public class TaskConsumer {

  private final JobService jobService;

  public TaskConsumer(JobService jobService) {
    this.jobService = jobService;
  }

  @KafkaHandler(isDefault = true)
  public void receiveRecordFromDashboardProducer(
      @Payload RecordDto record, @Headers KafkaMessageHeaders headers) throws JobException {
    log.info("Inside receiveRecordFromDashboardProducer service");

    try {
      jobService.processRecord(record);
      log.info("receiveRecordFromDashboardProducer service ends");
    } catch (Exception e) {
      log.error("Error while receiving the job record from the kafka producer", e);
      throw new JobException(
          "Exception while receiving the job record from the kafka producer",
          e,
          record.getJob().getJobId(),
          null);
    }
  }
}
