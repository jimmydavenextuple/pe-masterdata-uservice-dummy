package com.nextuple.transit.consumer;

import com.nextuple.jobs.framework.common.domain.pojo.JobDetailsDto;
import com.nextuple.transit.exception.TransitBufferJobException;
import com.nextuple.transit.service.TransitBufferConsumerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.converter.KafkaMessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@KafkaListener(
    topics = "${jobs-framework.job-completion-consumer.topic-name}",
    groupId = "${jobs-framework.job-completion-consumer.group-id}")
@Component
@RequiredArgsConstructor
@Slf4j
public class TransitBufferConsumer {

  private final TransitBufferConsumerService transitBufferConsumerService;

  @KafkaHandler
  public void receiveRecordForTransitBuffer(
      @Payload JobDetailsDto jobDetailsDto, @Headers KafkaMessageHeaders headers)
      throws TransitBufferJobException {
    log.debug("Received job details for transit buffer");

    try {
      transitBufferConsumerService.processJobRecordForTransitBuffer(jobDetailsDto);
    } catch (Exception e) {
      log.error(
          "Error while receiving the job record from the kafka producer for transit buffer", e);
      throw new TransitBufferJobException(
          "Exception while receiving the job record from the kafka producer for transit buffer",
          e,
          jobDetailsDto.getJobId());
    }
  }
}
