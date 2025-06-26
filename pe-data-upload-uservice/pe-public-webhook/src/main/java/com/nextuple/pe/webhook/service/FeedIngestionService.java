package com.nextuple.pe.webhook.service;

import com.nextuple.pe.webhook.domain.inbound.FeedRequest;

public interface FeedIngestionService<T> {

  default void publishFeedToKafka(String orgId, FeedRequest<T> feedRequest) {
    if (!isPublishEnabled()) return;
    publish(orgId, feedRequest);
  }

  boolean isPublishEnabled();

  void publish(String orgId, FeedRequest<T> feedRequest);
}
