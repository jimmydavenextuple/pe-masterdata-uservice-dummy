package com.hbc.jobs.consumers.service;

import com.hbc.jobs.consumers.exception.PublishJobEventException;
import com.hbc.jobs.framework.common.domain.pojo.JobDetailsDto;
import com.hbc.jobs.framework.common.domain.pojo.RecordStatusDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PublishJobEventService {

  private final KafkaTemplate<Object, Object> kafkaTemplate;

  @Value("${jobs-framework.result-publish.topic-name}")
  private String resultPublishTopicName;

  @Value("${jobs-framework.job-completion-publish.topic-name}")
  private String jobCompletionPublishTopic;

  public PublishJobEventService(KafkaTemplate<Object, Object> kafkaTemplate) {
    this.kafkaTemplate = kafkaTemplate;
  }

  public void publishJobRecord(RecordStatusDto recordStatus) throws PublishJobEventException {
    try {
      Message<RecordStatusDto> message =
          MessageBuilder.withPayload(recordStatus)
              .setHeader(KafkaHeaders.TOPIC, resultPublishTopicName)
              .setHeader(KafkaHeaders.MESSAGE_KEY, recordStatus.getJobId())
              .build();
      publishToKafka(message);
    } catch (Exception e) {
      log.error("Error while publishing record from consumer to consumer", e);
      throw new PublishJobEventException(
          "Exception while publishing the job record", recordStatus.getJobId());
    }
  }

  public void publishJobDetailsEvent(JobDetailsDto jobDetailsDto) throws PublishJobEventException {
    try {
      Message<JobDetailsDto> message =
          MessageBuilder.withPayload(jobDetailsDto)
              .setHeader(KafkaHeaders.TOPIC, jobCompletionPublishTopic)
              .setHeader(KafkaHeaders.MESSAGE_KEY, jobDetailsDto.getJobId())
              .build();

      publishToKafka(message);
    } catch (Exception e) {
      log.error(
          "Error while publishing job completion event for job: {}", jobDetailsDto.getJobId(), e);
      throw new PublishJobEventException(
          "Error while publishing job completion event", jobDetailsDto.getJobId());
    }
  }

  private void publishToKafka(Message<?> message) {
    kafkaTemplate
        .send(message)
        .addCallback(
            e -> log.debug("Record to Consumer topic successfully sent"),
            e -> log.error("Sending record to Consumer topic failed", e));
  }
}
