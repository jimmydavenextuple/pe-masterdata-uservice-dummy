/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.carrier.consumer.consumer;

import com.fasterxml.jackson.core.type.TypeReference;
import com.nextuple.carrier.consumer.dto.CarrierFeedDto;
import com.nextuple.carrier.consumer.impl.CarrierBatchServiceImpl;
import com.nextuple.master.data.integration.consumer.MasterDataFeedConsumer;
import com.nextuple.master.data.integration.inbound.BatchRequest;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.converter.KafkaMessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@KafkaListener(
    topics = "${master-data.carrier.topic-name}",
    groupId = "${master-data.carrier.group-id}",
    batch = "true")
public class CarrierFeedConsumer extends MasterDataFeedConsumer<CarrierFeedDto> {
  private final CarrierBatchServiceImpl carrierBatchService;
  private final TypeReference<List<BatchRequest<CarrierFeedDto>>> carrierTypeReference =
      new TypeReference<>() {};

  @KafkaHandler
  public void consumeCarrierFeed(
      @Payload List<BatchRequest<CarrierFeedDto>> carrierFeed,
      @Headers KafkaMessageHeaders headers) {
    consumeMasterDataFeed(carrierFeed);
  }

  @Override
  public void invokeFeedProcessingService(List<BatchRequest<CarrierFeedDto>> carrierFeed) {
    carrierBatchService.processRecordsWithRetry(carrierFeed);
  }

  @Override
  public TypeReference<List<BatchRequest<CarrierFeedDto>>> getTypeReference() {
    return carrierTypeReference;
  }
}
