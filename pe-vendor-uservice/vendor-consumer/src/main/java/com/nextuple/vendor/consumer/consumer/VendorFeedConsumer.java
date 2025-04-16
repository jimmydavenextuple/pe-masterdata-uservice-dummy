/*
 * Copyright (c) 2025., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.vendor.consumer.consumer;

import com.fasterxml.jackson.core.type.TypeReference;
import com.nextuple.master.data.integration.consumer.MasterDataFeedConsumer;
import com.nextuple.master.data.integration.inbound.BatchRequest;
import com.nextuple.vendor.consumer.dto.VendorFeedDto;
import com.nextuple.vendor.consumer.impl.VendorBatchServiceImpl;
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
    topics = "#{'${master-data.vendor.topic-names:null}'.split(',')}",
    groupId = "${master-data.vendor.group-id}",
    batch = "true",
    autoStartup = "${kafka-topic-flags.master-data.vendor.enabled:false}",
    containerFactory = "vendorDeserializerConsumer")
public class VendorFeedConsumer extends MasterDataFeedConsumer<VendorFeedDto> {

  private final VendorBatchServiceImpl vendorBatchService;
  private final TypeReference<List<BatchRequest<VendorFeedDto>>> vendorTypeReference =
      new TypeReference<>() {};

  @KafkaHandler
  public void consumeVendorFeed(
      @Payload List<BatchRequest<VendorFeedDto>> vendorFeed, @Headers KafkaMessageHeaders headers) {
    try {
      consumeMasterDataFeed(vendorFeed);
    } catch (Exception e) {
      log.error("Exception occurred while consuming vendor feed : {}", vendorFeed);
      throw e;
    }
  }

  @Override
  public void invokeFeedProcessingService(List<BatchRequest<VendorFeedDto>> vendorFeed) {
    vendorBatchService.processRecordsWithRetry(vendorFeed);
  }

  @Override
  public TypeReference<List<BatchRequest<VendorFeedDto>>> getTypeReference() {
    return vendorTypeReference;
  }
}
