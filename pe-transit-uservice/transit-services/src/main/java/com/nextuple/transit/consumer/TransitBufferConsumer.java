/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.transit.consumer;

import com.nextuple.jobs.framework.common.domain.pojo.JobDetailsDto;
import com.nextuple.transit.persistence.exception.TransitBufferJobException;
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
    topics = "#{'${jobs-framework.job-completion-consumer.topic-name}'.split(',')}",
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
