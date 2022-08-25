package com.hbc.item.controller;

import com.hbc.common.exception.CommonServiceException;
import com.hbc.common.response.BaseResponse;
import com.hbc.item.domain.inbound.ItemCreationRequest;
import com.hbc.item.domain.inbound.ItemUpdationRequest;
import com.hbc.item.domain.outbound.ItemResponse;
import com.hbc.item.exception.ItemDomainException;
import com.hbc.item.service.ItemService;
import java.util.List;
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
import org.springframework.web.bind.annotation.RequestParam;
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
    logger.debug("Processing item creation request");
    try {

      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Item successfully created")
              .payload(itemService.createItem(itemCreationRequest))
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
    logger.debug("Processing update item details");
    try {
      var itemResponse = itemService.updateItemDetails(itemId, orgId, uom, itemUpdationRequest);
      logger.info("Response after updating of item data :{}", itemResponse);
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
    logger.debug("Processing get item details");
    try {

      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Item details fetched successfully")
              .payload(itemService.getItemDetails(itemId, orgId, uom))
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
    logger.debug("Processing delete item");
    try {
      var itemResponse = itemService.deleteItem(itemId, orgId, uom);
      logger.info("Response after deleting of item data :{}", itemResponse);

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

  @GetMapping("/{orgId}/{uom}")
  public List<ItemResponse> getItemList(
      @NotBlank @PathVariable String orgId,
      @NotBlank @PathVariable String uom,
      @NotBlank @RequestParam List<String> itemList)
      throws ItemDomainException, CommonServiceException {
    logger.debug("Processing get item details");
    try {

      return itemService.getItemList(itemList, orgId, uom);
    } catch (Exception e) {
      logger.error("Failed to fetch list of item details");
      throw e;
    }
  }
}
