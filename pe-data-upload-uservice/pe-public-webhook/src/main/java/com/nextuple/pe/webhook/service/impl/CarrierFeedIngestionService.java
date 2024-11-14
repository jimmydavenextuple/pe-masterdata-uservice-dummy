/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.pe.webhook.service.impl;

import com.nextuple.carrier.consumer.dto.CarrierFeedDto;
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
public class CarrierFeedIngestionService
    implements FeedIngestionService<MasterDataIngestionDto<CarrierFeedDto>> {
  @Value("${master-data.carrier.topic-name:null}")
  private String carrierFeedTopic;

  @Value("${kafka-topic-flags.master-data.carrier.enabled:false}")
  private boolean isPublishEnabled;

  private final KafkaProducer kafkaProducer;

  @Override
  public void publish(
      String orgId, FeedRequest<MasterDataIngestionDto<CarrierFeedDto>> carrierFeed) {
    log.debug("Inside carrier publishing service with feed : {}", carrierFeed);

    carrierFeed
        .getData()
        .forEach(
            carrierDto -> {
              carrierDto.getPayload().setOrgId(orgId);
              BatchRequest<CarrierFeedDto> batchRequest = new BatchRequest<>();
              batchRequest.setAction(carrierDto.getAction());
              batchRequest.setProducedTimestamp(carrierDto.getProducedTimestamp());
              batchRequest.setPayload(carrierDto.getPayload());
              kafkaProducer.publishFeedToKafka(
                  batchRequest, carrierDto.getPayload().getCarrierId(), carrierFeedTopic);
            });
  }

  @Override
  public boolean isPublishEnabled() {
    if (Boolean.TRUE.equals(isPublishEnabled)) return true;
    log.warn("Not publishing carrier feed messages since it is disabled.");
    return false;
  }
}
