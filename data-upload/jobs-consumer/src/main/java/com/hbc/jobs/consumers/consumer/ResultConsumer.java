package com.hbc.jobs.consumers.consumer;

import com.hbc.common.context.CurrentThreadContext;
import com.hbc.jobs.consumers.exception.JobException;
import com.hbc.jobs.consumers.service.JobConsumerService;
import com.hbc.jobs.framework.common.domain.pojo.RecordStatusDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.converter.KafkaMessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@KafkaListener(
    topics = "${jobs-framework.result-consumer.topic-name}",
    groupId = "${jobs-framework.result-consumer.group-id}")
@Component
@Slf4j
public class ResultConsumer {

  private final JobConsumerService jobConsumerService;

  public ResultConsumer(JobConsumerService jobConsumerService) {
    this.jobConsumerService = jobConsumerService;
  }

  @KafkaHandler(isDefault = true)
  public void receiveResultFromAnotherConsumer(
      @Payload RecordStatusDto recordStatus, @Headers KafkaMessageHeaders headers)
      throws JobException {
    log.debug("Inside receiveResultFromAnotherConsumer service");

    try {
      log.debug("JWT token received in consumer");
      CurrentThreadContext.getLogContext()
          .setAuthorizationHeader(headers.getRawHeaders().get("jwtToken").toString());
      jobConsumerService.updateJobStatus(recordStatus);
    } catch (Exception e) {
      log.error("Error while receiving the job from kafka", e);
      throw new JobException("Exception while persisting the job record", e, null, null);
    }
  }
}
