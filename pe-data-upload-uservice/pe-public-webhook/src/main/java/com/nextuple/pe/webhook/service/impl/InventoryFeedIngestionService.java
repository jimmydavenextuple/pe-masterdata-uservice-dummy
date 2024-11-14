package com.nextuple.pe.webhook.service.impl;

import com.nextuple.pe.webhook.domain.dtos.InventoryATP;
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
public class InventoryFeedIngestionService implements FeedIngestionService<InventoryATP> {
  private final KafkaProducer kafkaProducer;

  @Value("${pe-public-webhook.iv-feed.topic:null}")
  private String ivFeedTopic;

  @Value("${kafka-topic-flags.pe-public-webhook.iv-feed.enabled:true}")
  private boolean isPublishEnabled;

  @Override
  public void publish(String orgId, FeedRequest<InventoryATP> inventoryATPFeedRequest) {
    log.debug("--Inside inventory publishing service--");
    inventoryATPFeedRequest.getData().stream()
        .forEach(
            ivFeed -> {
              ivFeed.setOrgId(orgId);
              kafkaProducer.publishFeedToKafka(ivFeed, ivFeed.getItemId(), ivFeedTopic);
            });
  }

  @Override
  public boolean isPublishEnabled() {
    if (Boolean.TRUE.equals(isPublishEnabled)) return true;
    log.warn("Not publishing inventory feed messages since it is disabled.");
    return false;
  }
}
