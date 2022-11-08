package com.nextuple.item.controller;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.item.domain.inbound.ItemCreationRequest;
import com.nextuple.item.domain.inbound.ItemUpdationRequest;
import com.nextuple.item.domain.outbound.ItemResponse;
import com.nextuple.item.exception.ItemBatchingDomainException;
import com.nextuple.item.exception.ItemDomainException;
import com.nextuple.item.service.ItemService;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
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
      @NotBlank(message = "itemId can't be empty") @PathVariable String itemId,
      @NotBlank(message = "orgId can't be empty") @PathVariable String orgId,
      @NotBlank(message = "uom can't be empty") @PathVariable String uom,
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
      @NotBlank(message = "itemId can't be empty") @PathVariable String itemId,
      @NotBlank(message = "orgId can't be empty") @PathVariable String orgId,
      @NotBlank(message = "uom can't be empty") @PathVariable String uom)
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
      @NotBlank(message = "itemId can't be empty") @PathVariable String itemId,
      @NotBlank(message = "orgId can't be empty") @PathVariable String orgId,
      @NotBlank(message = "uom can't be empty") @PathVariable String uom)
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

  @GetMapping("/{orgId}")
  public List<ItemResponse> getItemList(
      @NotBlank(message = "orgId can't be empty") @PathVariable String orgId,
      @NotEmpty @RequestParam List<String> itemList)
      throws CommonServiceException, ItemBatchingDomainException {
    logger.debug("Processing get item details");
    try {

      return itemService.getItemList(itemList, orgId);
    } catch (Exception e) {
      logger.error("Failed to fetch list of item details");
      throw e;
    }
  }
}
