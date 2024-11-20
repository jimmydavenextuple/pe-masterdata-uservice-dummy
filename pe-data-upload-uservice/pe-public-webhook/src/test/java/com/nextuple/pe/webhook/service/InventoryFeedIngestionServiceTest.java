package com.nextuple.pe.webhook.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.nextuple.pe.webhook.domain.dtos.InventoryATP;
import com.nextuple.pe.webhook.domain.inbound.FeedRequest;
import com.nextuple.pe.webhook.producer.KafkaProducer;
import com.nextuple.pe.webhook.service.impl.InventoryFeedIngestionService;
import com.nextuple.pe.webhook.util.TestUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

class InventoryFeedIngestionServiceTest {
  @Mock KafkaProducer kafkaProducer;
  @InjectMocks InventoryFeedIngestionService inventoryPublishingService;

  @BeforeEach
  public void init() {
    MockitoAnnotations.openMocks(this);
    ReflectionTestUtils.setField(inventoryPublishingService, "ivFeedTopic", TestUtil.IV_TOPIC);
  }

  @Test
  @DisplayName("Case when message not published since it is disabled")
  void publishFeedToKafkaDisabledTest() {
    ReflectionTestUtils.setField(inventoryPublishingService, "isPublishEnabled", false);
    FeedRequest<InventoryATP> inventoryATPFeedRequest = new FeedRequest<>();
    inventoryATPFeedRequest.setData(TestUtil.createInventoryATPs(2));
    inventoryPublishingService.publishFeedToKafka(TestUtil.ORG_ID, inventoryATPFeedRequest);
    verify(kafkaProducer, times(0)).publishFeedToKafka(any(), any(), any());
  }

  @Test
  @DisplayName("Case when message is published since it is enabled")
  void publishFeedToKafkaTest() {
    ReflectionTestUtils.setField(inventoryPublishingService, "isPublishEnabled", true);
    FeedRequest<InventoryATP> inventoryATPFeedRequest = new FeedRequest<>();
    inventoryATPFeedRequest.setData(TestUtil.createInventoryATPs(2));
    doNothing().when(kafkaProducer).publishFeedToKafka(any(), any(), any());
    inventoryPublishingService.publishFeedToKafka(TestUtil.ORG_ID, inventoryATPFeedRequest);
    verify(kafkaProducer, times(2)).publishFeedToKafka(any(), any(), any());
  }
}
