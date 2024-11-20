/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.pe.webhook.service.impl;

import com.nextuple.calendar.consumer.dto.NodeCalendarFeedDto;
import com.nextuple.master.data.integration.inbound.BatchRequest;
import com.nextuple.pe.webhook.domain.dtos.MasterDataIngestionDto;
import com.nextuple.pe.webhook.domain.inbound.FeedRequest;
import com.nextuple.pe.webhook.producer.KafkaProducer;
import com.nextuple.pe.webhook.service.FeedIngestionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class NodeCalendarFeedIngestionService
    implements FeedIngestionService<MasterDataIngestionDto<NodeCalendarFeedDto>> {
  @Value("${master-data.node-calendar.topic-name:null}")
  private String nodeCalendarFeedTopic;

  @Value("${kafka-topic-flags.master-data.node-calendar.enabled:false}")
  private boolean isPublishEnabled;

  private final KafkaProducer kafkaProducer;

  @Override
  public void publish(
      String orgId, FeedRequest<MasterDataIngestionDto<NodeCalendarFeedDto>> nodeCalendarFeed) {
    log.debug("Inside node calendar publishing service with feed : {}", nodeCalendarFeed);
    nodeCalendarFeed
        .getData()
        .forEach(
            calendarDto -> {
              calendarDto.getPayload().setOrgId(orgId);
              BatchRequest<NodeCalendarFeedDto> batchRequest = new BatchRequest<>();
              batchRequest.setAction(calendarDto.getAction());
              batchRequest.setProducedTimestamp(calendarDto.getProducedTimestamp());
              batchRequest.setPayload(calendarDto.getPayload());
              kafkaProducer.publishFeedToKafka(
                  batchRequest, calendarDto.getPayload().getCalendarId(), nodeCalendarFeedTopic);
            });
  }

  @Override
  public boolean isPublishEnabled() {
    if (Boolean.TRUE.equals(isPublishEnabled)) return true;
    log.warn("Not publishing node calendar feed messages since it is disabled.");
    return false;
  }
}
