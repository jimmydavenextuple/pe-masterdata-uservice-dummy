package com.nextuple.pe.webhook.controller;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.pe.webhook.domain.dtos.InventoryATP;
import com.nextuple.pe.webhook.domain.dtos.Item;
import com.nextuple.pe.webhook.domain.inbound.FeedRequest;
import com.nextuple.pe.webhook.service.FeedIngestionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/pe-webhook/ingest-data")
@RestController
@Tag(name = "PE Public Webhooks")
public class PublicWebhookController {
  private static final Logger logger = LoggerFactory.getLogger(PublicWebhookController.class);
  @Autowired private FeedIngestionService<InventoryATP> inventoryFeedIngestionService;
  @Autowired private FeedIngestionService<Item> itemFeedIngestionService;
  private static final String TENANT = "x-tenant-id";
  private static final String TENANT_MISSING_MESSAGE = "Tenant ID not passed";

  @Operation(summary = "Ingest Inventory", description = "Ingest inventory feed to kafka")
  @PostMapping("/inventory")
  public ResponseEntity<BaseResponse<String>> publishInventory(
      @NotBlank(message = TENANT_MISSING_MESSAGE) @RequestHeader(value = TENANT) String orgId,
      @Valid @RequestBody FeedRequest<InventoryATP> inventoryATPFeedRequest) {
    logger.info("--Inside publishInventory--");
    inventoryFeedIngestionService.publishFeedToKafka(orgId, inventoryATPFeedRequest);
    return ResponseEntity.ok(
        BaseResponse.builder()
            .success(true)
            .message("Inventory feed published successfully")
            .build());
  }

  @Operation(summary = "Ingest Items", description = "Ingest item feed to kafka")
  @PostMapping("/item")
  public ResponseEntity<BaseResponse<String>> publishItem(
      @NotBlank(message = TENANT_MISSING_MESSAGE) @RequestHeader(value = TENANT) String orgId,
      @Valid @RequestBody FeedRequest<Item> itemFeedRequest) {
    logger.info("--Inside publishItem--");
    itemFeedIngestionService.publishFeedToKafka(orgId, itemFeedRequest);
    return ResponseEntity.ok(
        BaseResponse.builder().success(true).message("Item feed published successfully").build());
  }
}
