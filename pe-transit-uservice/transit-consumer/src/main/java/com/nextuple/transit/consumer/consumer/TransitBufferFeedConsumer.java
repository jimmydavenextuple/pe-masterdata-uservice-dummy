/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.transit.consumer.consumer;

import com.fasterxml.jackson.core.type.TypeReference;
import com.nextuple.master.data.integration.consumer.MasterDataFeedConsumer;
import com.nextuple.master.data.integration.inbound.BatchRequest;
import com.nextuple.transit.consumer.dto.TransitBufferFeedDto;
import com.nextuple.transit.consumer.impl.TransitBufferBatchServiceImpl;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.converter.KafkaMessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@KafkaListener(
    topics = "#{'${master-data.transit-buffer.topic-names}'.split(',')}",
    groupId = "${master-data.transit-buffer.group-id}",
    batch = "true",
    containerFactory = "transitBufferFeedDeserializerConsumer",
    autoStartup = "${kafka-topic-flags.master-data.transit-buffer.enabled:false}")
public class TransitBufferFeedConsumer extends MasterDataFeedConsumer<TransitBufferFeedDto> {

  private final TransitBufferBatchServiceImpl transitBufferBatchService;
  private final TypeReference<List<BatchRequest<TransitBufferFeedDto>>> transitBufferTypeReference =
      new TypeReference<>() {};

  @KafkaHandler
  public void consumeTransitBufferFeed(
      @Payload List<BatchRequest<TransitBufferFeedDto>> transitBufferFeed,
      @Headers KafkaMessageHeaders headers) {
    try {
      consumeMasterDataFeed(transitBufferFeed);
    } catch (Exception e) {
      log.error("Exception occurred while consuming transit buffer feed : {}", transitBufferFeed);
      throw e;
    }
  }

  @Override
  public void invokeFeedProcessingService(
      List<BatchRequest<TransitBufferFeedDto>> transitBufferFeed) {
    transitBufferBatchService.processRecordsWithRetry(transitBufferFeed);
  }

  @Override
  public TypeReference<List<BatchRequest<TransitBufferFeedDto>>> getTypeReference() {
    return transitBufferTypeReference;
  }
}
