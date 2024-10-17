package com.nextuple.pe.webhook.service.impl;

import com.nextuple.pe.webhook.domain.dtos.Item;
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
public class ItemFeedIngestionService implements FeedIngestionService<Item> {
  @Value("${pe-public-webhook.item-feed.topic:null}")
  private String itemFeedTopic;

  @Value("${kafka-topic-flags.pe-public-webhook.item-feed.enabled:true}")
  private boolean isPublishEnabled;

  private final KafkaProducer kafkaProducer;

  @Override
  public void publish(String orgId, FeedRequest<Item> itemFeedRequest) {
    log.debug("--Inside item publishing service--");
    itemFeedRequest.getData().stream()
        .forEach(
            item -> {
              item.setOrgId(orgId);
              kafkaProducer.publishFeedToKafka(item, item.getItemId(), itemFeedTopic);
            });
  }

  @Override
  public boolean isPublishEnabled() {
    if (Boolean.TRUE.equals(isPublishEnabled)) return true;
    log.warn("Not publishing item feed messages since it is disabled.");
    return false;
  }
}
