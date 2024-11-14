/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.pe.webhook.service.impl;

import com.nextuple.master.data.integration.inbound.BatchRequest;
import com.nextuple.node.carrier.consumer.dto.ProcessingLeadTimeFeedDto;
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
public class NodeServiceOptionFeedIngestionService
    implements FeedIngestionService<MasterDataIngestionDto<ProcessingLeadTimeFeedDto>> {
  @Value("${master-data.processing-lead-time.topic-name:null}")
  private String nodeServiceOptionFeedTopic;

  @Value("${kafka-topic-flags.master-data.processing-lead-time.enabled:false}")
  private boolean isPublishEnabled;

  private final KafkaProducer kafkaProducer;

  @Override
  public void publish(
      String orgId,
      FeedRequest<MasterDataIngestionDto<ProcessingLeadTimeFeedDto>> nodeServiceOptionFeed) {
    log.debug(
        "Inside node service option publishing service with feed : {}", nodeServiceOptionFeed);
    nodeServiceOptionFeed
        .getData()
        .forEach(
            nodeServiceOptionDto -> {
              nodeServiceOptionDto.getPayload().setOrgId(orgId);
              BatchRequest<ProcessingLeadTimeFeedDto> batchRequest = new BatchRequest<>();
              batchRequest.setAction(nodeServiceOptionDto.getAction());
              batchRequest.setProducedTimestamp(nodeServiceOptionDto.getProducedTimestamp());
              batchRequest.setPayload(nodeServiceOptionDto.getPayload());
              kafkaProducer.publishFeedToKafka(
                  batchRequest,
                  nodeServiceOptionDto.getPayload().getNodeId(),
                  nodeServiceOptionFeedTopic);
            });
  }

  @Override
  public boolean isPublishEnabled() {
    if (Boolean.TRUE.equals(isPublishEnabled)) return true;
    log.warn("Not publishing node service option feed messages since it is disabled.");
    return false;
  }
}
