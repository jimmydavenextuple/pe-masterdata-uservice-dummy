package com.nextuple.pe.webhook.service.impl;

import com.nextuple.master.data.integration.inbound.BatchRequest;
import com.nextuple.pe.webhook.domain.dtos.MasterDataIngestionDto;
import com.nextuple.pe.webhook.domain.inbound.FeedRequest;
import com.nextuple.pe.webhook.producer.KafkaProducer;
import com.nextuple.pe.webhook.service.FeedIngestionService;
import com.nextuple.vendor.consumer.dto.VendorFeedDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class VendorFeedIngestionService
    implements FeedIngestionService<MasterDataIngestionDto<VendorFeedDto>> {
  @Value("${master-data.vendor.topic-names:null}")
  private String vendorFeedTopic;

  @Value("${kafka-topic-flags.master-data.vendor.enabled:false}")
  private boolean isPublishEnabled;

  private final KafkaProducer kafkaProducer;

  @Override
  public void publish(String orgId, FeedRequest<MasterDataIngestionDto<VendorFeedDto>> vendorFeed) {
    log.debug("Inside vendor publishing service with feed : {}", vendorFeed);
    vendorFeed
        .getData()
        .forEach(
            vendorDto -> {
              vendorDto.getPayload().setOrgId(orgId);
              BatchRequest<VendorFeedDto> batchRequest = new BatchRequest<>();
              batchRequest.setAction(vendorDto.getAction());
              batchRequest.setProducedTimestamp(vendorDto.getProducedTimestamp());
              batchRequest.setPayload(vendorDto.getPayload());
              kafkaProducer.publishFeedToKafka(
                  batchRequest, vendorDto.getPayload().getVendorId(), vendorFeedTopic);
            });
  }

  @Override
  public boolean isPublishEnabled() {
    if (Boolean.TRUE.equals(isPublishEnabled)) return true;
    log.warn("Not publishing vendor feed messages since it is disabled.");
    return false;
  }
}
