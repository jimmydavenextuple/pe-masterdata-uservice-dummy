package com.hbc.jobs.consumers.service;

import com.hbc.jobs.consumers.exception.JobDashboardException;
import com.hbc.jobs.framework.common.domain.pojo.RecordStatusDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class JobDashboardService {

  private final KafkaTemplate<Object, Object> kafkaTemplate;

  @Value("${jobs-framework.result-publish.topic-name}")
  private String resultPublishTopicName;

  public JobDashboardService(KafkaTemplate<Object, Object> kafkaTemplate) {
    this.kafkaTemplate = kafkaTemplate;
  }

  public void publishJobRecord(RecordStatusDto recordStatus) throws JobDashboardException {
    try {
      kafkaTemplate
          .send(
              MessageBuilder.withPayload(recordStatus)
                  .setHeader(KafkaHeaders.TOPIC, resultPublishTopicName)
                  .setHeader(KafkaHeaders.MESSAGE_KEY, String.valueOf(recordStatus.getRecordNo()))
                  .build())
          .addCallback(
              e -> log.debug("Record to Consumer topic successfully sent"),
              e -> log.error("Sending record to Consumer topic failed", e));
    } catch (Exception e) {
      log.error("Error while publishing record from consumer to consumer", e);
      throw new JobDashboardException(
          "Exception while publishing the job record", recordStatus.getJobId());
    }
  }
}
