package com.nextuple.pe.webhook.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.nextuple.pe.webhook.domain.dtos.Item;
import com.nextuple.pe.webhook.domain.inbound.FeedRequest;
import com.nextuple.pe.webhook.producer.KafkaProducer;
import com.nextuple.pe.webhook.service.impl.ItemFeedIngestionService;
import com.nextuple.pe.webhook.util.TestUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

class ItemFeedIngestionServiceTest {
  @Mock KafkaProducer kafkaProducer;
  @InjectMocks ItemFeedIngestionService itemPublishingService;

  @BeforeEach
  public void init() {
    MockitoAnnotations.openMocks(this);
    ReflectionTestUtils.setField(itemPublishingService, "itemFeedTopic", TestUtil.ITEM_TOPIC);
  }

  @Test
  @DisplayName("Case when message not published since it is disabled")
  void publishFeedToKafkaDisabledTest() {
    ReflectionTestUtils.setField(itemPublishingService, "isPublishEnabled", false);
    FeedRequest<Item> itemFeedRequest = new FeedRequest<>();
    itemFeedRequest.setData(TestUtil.createItems(2));
    itemPublishingService.publishFeedToKafka(TestUtil.ORG_ID, itemFeedRequest);
    verify(kafkaProducer, times(0)).publishFeedToKafka(any(), any(), any());
  }

  @Test
  @DisplayName("Case when message is published since it is enabled")
  void publishFeedToKafkaEnabledTest() {
    ReflectionTestUtils.setField(itemPublishingService, "isPublishEnabled", true);
    FeedRequest<Item> itemFeedRequest = new FeedRequest<>();
    itemFeedRequest.setData(TestUtil.createItems(2));
    doNothing().when(kafkaProducer).publishFeedToKafka(any(), any(), any());
    itemPublishingService.publishFeedToKafka(TestUtil.ORG_ID, itemFeedRequest);
    verify(kafkaProducer, times(2)).publishFeedToKafka(any(), any(), any());
  }
}
