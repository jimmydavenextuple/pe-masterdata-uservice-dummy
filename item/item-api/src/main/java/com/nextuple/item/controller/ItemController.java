package com.nextuple.item.controller;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.item.domain.inbound.ItemCreationRequest;
import com.nextuple.item.domain.inbound.ItemUpdationRequest;
import com.nextuple.item.domain.outbound.ItemResponse;
import com.nextuple.item.exception.ItemDomainException;
import com.nextuple.item.service.ItemService;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/item")
@RequiredArgsConstructor
public class ItemController {

  private static final Logger logger = LoggerFactory.getLogger(ItemController.class);
  private final ItemService itemService;

  @PostMapping
  public ResponseEntity<BaseResponse<ItemResponse>> addItem(
      @Valid @RequestBody ItemCreationRequest itemCreationRequest) throws ItemDomainException {
    logger.info("Processing item creation request");
    try {
      ItemResponse itemResponse = itemService.createItem(itemCreationRequest);

      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Item successfully created")
              .payload(itemResponse)
              .build());
    } catch (Exception e) {
      logger.error("Failed to add item");
      throw e;
    }
  }

  @PutMapping("/{itemId}/{orgId}/{uom}")
  public ResponseEntity<BaseResponse<ItemResponse>> updateItemDetails(
      @NotBlank @PathVariable String itemId,
      @NotBlank @PathVariable String orgId,
      @NotBlank @PathVariable String uom,
      @Valid @RequestBody ItemUpdationRequest itemUpdationRequest)
      throws ItemDomainException, CommonServiceException {
    logger.info("Processing update item details");
    try {

      ItemResponse itemResponse =
          itemService.updateItemDetails(itemId, orgId, uom, itemUpdationRequest);

      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Item details updated successfully")
              .payload(itemResponse)
              .build());
    } catch (Exception e) {
      logger.error("Failed to update item details");
      throw e;
    }
  }

  @GetMapping("/{itemId}/{orgId}/{uom}")
  public ResponseEntity<BaseResponse<ItemResponse>> getItemDetails(
      @NotBlank @PathVariable String itemId,
      @NotBlank @PathVariable String orgId,
      @NotBlank @PathVariable String uom)
      throws ItemDomainException, CommonServiceException {
    logger.info("Processing get item details");
    try {

      ItemResponse itemResponse = itemService.getItemDetails(itemId, orgId, uom);

      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Item details fetched successfully")
              .payload(itemResponse)
              .build());
    } catch (Exception e) {
      logger.error("Failed to fetch item details");
      throw e;
    }
  }

  @DeleteMapping("/{itemId}/{orgId}/{uom}")
  public ResponseEntity<BaseResponse<ItemResponse>> deleteItem(
      @NotBlank @PathVariable String itemId,
      @NotBlank @PathVariable String orgId,
      @NotBlank @PathVariable String uom)
      throws ItemDomainException, CommonServiceException {
    logger.info("Processing delete item");
    try {

      ItemResponse itemResponse = itemService.deleteItem(itemId, orgId, uom);

      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Item deleted successfully")
              .payload(itemResponse)
              .build());
    } catch (Exception e) {
      logger.error("Failed to delete item");
      throw e;
    }
  }
}
