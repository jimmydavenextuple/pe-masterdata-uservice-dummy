/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.node.consumer.consumer;

import com.fasterxml.jackson.core.type.TypeReference;
import com.nextuple.master.data.integration.consumer.MasterDataFeedConsumer;
import com.nextuple.master.data.integration.inbound.BatchRequest;
import com.nextuple.node.consumer.dto.NodeFeedDto;
import com.nextuple.node.consumer.impl.NodeBatchServiceImpl;
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
    topics = "#{'${master-data.node.topic-names}'.split(',')}",
    groupId = "${master-data.node.group-id}",
    batch = "true",
    containerFactory = "nodeDeserializerConsumer")
public class NodeFeedConsumer extends MasterDataFeedConsumer<NodeFeedDto> {

  private final NodeBatchServiceImpl nodeBatchService;
  private final TypeReference<List<BatchRequest<NodeFeedDto>>> nodeTypeReference =
      new TypeReference<>() {};

  @KafkaHandler
  public void consumeNodeFeed(
      @Payload List<BatchRequest<NodeFeedDto>> nodeFeed, @Headers KafkaMessageHeaders headers) {
    try {
      consumeMasterDataFeed(nodeFeed);
    } catch (Exception e) {
      log.error("Exception occurred while consuming node feed : {}", nodeFeed);
      throw e;
    }
  }

  @Override
  public void invokeFeedProcessingService(List<BatchRequest<NodeFeedDto>> nodeFeed) {
    nodeBatchService.processRecordsWithRetry(nodeFeed);
  }

  @Override
  public TypeReference<List<BatchRequest<NodeFeedDto>>> getTypeReference() {
    return nodeTypeReference;
  }
}
