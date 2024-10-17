/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.jobs.consumers.consumer;

import com.nextuple.jobs.consumers.authentication.AuthService;
import com.nextuple.jobs.consumers.exception.JobException;
import com.nextuple.jobs.consumers.service.JobConsumerService;
import com.nextuple.jobs.framework.common.domain.pojo.RecordDto;
import com.nextuple.jobs.framework.common.domain.pojo.RecordStatusDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.converter.KafkaMessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@KafkaListener(
    topics = "${jobs-framework.record-consumer.topic-name}",
    groupId = "${jobs-framework.record-consumer.group-id}",
    autoStartup = "${kafka-topic-flags.jobs-framework.record-consumer.enabled:true}")
@Component
@Slf4j
@RequiredArgsConstructor
public class TaskConsumer {

  private final JobConsumerService jobConsumerService;
  private final AuthService authService;

  private static final Logger logger = LoggerFactory.getLogger(TaskConsumer.class);

  @KafkaHandler(isDefault = true)
  public void receiveRecordFromDashboardProducer(
      @Payload RecordDto recordDto, @Headers KafkaMessageHeaders headers) throws JobException {
    logger.debug("Inside receiveRecordFromDashboardProducer service");

    try {
      long systime = System.currentTimeMillis();
      logger.debug("Auth token received in consumer");
      authService.checkAuthExpiry(headers);
      long executTime = System.currentTimeMillis();
      RecordStatusDto recordStatus = jobConsumerService.executeTask(recordDto);

      logger.debug(
          "Execute task took {} for jobId: {}",
          System.currentTimeMillis() - executTime,
          recordStatus.getJobId());

      long jobUpdTime = System.currentTimeMillis();
      jobConsumerService.updateJobStatus(recordStatus);

      logger.debug(
          "Update job status took {} for jobId: {}",
          System.currentTimeMillis() - jobUpdTime,
          recordStatus.getJobId());
      logger.debug(
          "Overall record consumption took: {} for jobId: {}",
          System.currentTimeMillis() - systime,
          recordStatus.getJobId());
      logger.debug("receiveRecordFromDashboardProducer service ends");

    } catch (Exception e) {
      log.error("Error while receiving the job record from the kafka producer", e);
      throw new JobException(
          "Exception while receiving the job record from the kafka producer",
          e,
          recordDto.getJobId(),
          null);
    }
  }
}
