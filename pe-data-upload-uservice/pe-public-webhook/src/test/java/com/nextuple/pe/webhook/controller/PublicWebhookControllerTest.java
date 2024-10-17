package com.nextuple.pe.webhook.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.pe.webhook.domain.dtos.InventoryATP;
import com.nextuple.pe.webhook.domain.dtos.Item;
import com.nextuple.pe.webhook.domain.inbound.FeedRequest;
import com.nextuple.pe.webhook.service.FeedIngestionService;
import com.nextuple.pe.webhook.util.TestUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

class PublicWebhookControllerTest {
  @Mock FeedIngestionService<InventoryATP> inventoryATPFeedIngestionService;
  @Mock FeedIngestionService<Item> itemFeedIngestionService;
  @InjectMocks PublicWebhookController publicWebhookController;

  @BeforeEach
  public void init() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  @DisplayName("OK: Happy Path")
  void publishInventoryTest1() {
    FeedRequest<InventoryATP> inventoryATPFeedRequest = new FeedRequest<>();
    inventoryATPFeedRequest.setData(TestUtil.createInventoryATPs(2));
    doNothing()
        .when(inventoryATPFeedIngestionService)
        .publishFeedToKafka(TestUtil.ORG_ID, inventoryATPFeedRequest);
    ResponseEntity<BaseResponse<String>> response =
        publicWebhookController.publishInventory(TestUtil.ORG_ID, inventoryATPFeedRequest);
    assertTrue(response.hasBody());
    assertTrue(response.getBody().isSuccess());
    assertEquals(TestUtil.IV_RESPONSE_MESSAGE, response.getBody().getMessage());
  }

  @Test
  @DisplayName("OK: Happy Path")
  void publishItemTest1() {
    FeedRequest<Item> itemFeedRequest = new FeedRequest<>();
    itemFeedRequest.setData(TestUtil.createItems(2));
    doNothing().when(itemFeedIngestionService).publishFeedToKafka(TestUtil.ORG_ID, itemFeedRequest);
    ResponseEntity<BaseResponse<String>> response =
        publicWebhookController.publishItem(TestUtil.ORG_ID, itemFeedRequest);
    assertTrue(response.hasBody());
    assertTrue(response.getBody().isSuccess());
    assertEquals(TestUtil.ITEM_RESPONSE_MESSAGE, response.getBody().getMessage());
  }
}
