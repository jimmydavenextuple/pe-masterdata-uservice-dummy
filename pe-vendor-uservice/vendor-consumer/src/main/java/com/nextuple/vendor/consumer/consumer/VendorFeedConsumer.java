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
    topics = "#{'${master-data.vendor.topic-names}'.split(',')}",
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
