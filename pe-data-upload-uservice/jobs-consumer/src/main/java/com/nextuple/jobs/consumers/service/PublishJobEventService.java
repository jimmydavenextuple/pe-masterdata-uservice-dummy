/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.jobs.consumers.service;

import com.nextuple.jobs.consumers.exception.PublishJobEventException;
import com.nextuple.jobs.framework.common.domain.pojo.JobDetailsDto;
import com.nextuple.jobs.framework.common.domain.pojo.RecordStatusDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PublishJobEventService {

  @Qualifier("JsonSerializerProducerObj")
  private final KafkaTemplate<Object, Object> kafkaTemplate;

  @Value("${jobs-framework.result-publish.topic-name}")
  private String resultPublishTopicName;

  @Value("${jobs-framework.job-completion-consumer.topic-name}")
  private String jobCompletionPublishTopic;

  public PublishJobEventService(KafkaTemplate<Object, Object> kafkaTemplate) {
    this.kafkaTemplate = kafkaTemplate;
  }

  public void publishJobRecord(RecordStatusDto recordStatus) throws PublishJobEventException {
    try {
      Message<RecordStatusDto> message =
          MessageBuilder.withPayload(recordStatus)
              .setHeader(KafkaHeaders.TOPIC, resultPublishTopicName)
              .setHeader(KafkaHeaders.KEY, recordStatus.getJobId())
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
              .setHeader(KafkaHeaders.KEY, jobDetailsDto.getJobId())
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
        .whenComplete(
            (sr, ex) -> {
              if (ex != null) {
                log.error("Sending record to Consumer topic failed", ex);
              } else {
                log.debug("Record to Consumer topic successfully sent");
              }
            });
  }
}
