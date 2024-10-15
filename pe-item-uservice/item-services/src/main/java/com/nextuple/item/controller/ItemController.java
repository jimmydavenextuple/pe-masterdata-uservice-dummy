/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.item.controller;

import static com.nextuple.common.constants.CommonConstants.DEFAULT_SORT_ORDER;
import static com.nextuple.item.domain.constants.ItemConstants.ITEM_SORT_BY;
import static com.nextuple.item.domain.constants.ItemConstants.PAGINATION_NEXT_URL;
import static com.nextuple.item.domain.constants.ItemConstants.PAGINATION_PREVIOUS_URL;

import com.nextuple.common.base.PagePayload;
import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.pojo.PageParams;
import com.nextuple.common.pojo.PageProperties;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.common.util.PaginationUtil;
import com.nextuple.item.controller.docs.AddItemDoc;
import com.nextuple.item.controller.docs.DeleteItemDoc;
import com.nextuple.item.controller.docs.GetItemDetailsDoc;
import com.nextuple.item.controller.docs.GetItemListDoc;
import com.nextuple.item.controller.docs.GetItemListPaginatedDoc;
import com.nextuple.item.controller.docs.UpdateItemDetailsDoc;
import com.nextuple.item.domain.constants.ItemConstants;
import com.nextuple.item.domain.inbound.ItemCreationRequest;
import com.nextuple.item.domain.inbound.ItemUpdationRequest;
import com.nextuple.item.domain.outbound.ItemListResponse;
import com.nextuple.item.domain.outbound.ItemResponse;
import com.nextuple.item.persistence.exception.ItemBatchingDomainException;
import com.nextuple.item.persistence.exception.ItemDomainException;
import com.nextuple.item.service.ItemService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.Date;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
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
@Tag(name = "Item APIs")
@RequestMapping("/item")
@RequiredArgsConstructor
public class ItemController {

  private static final Logger logger = LoggerFactory.getLogger(ItemController.class);
  private final ItemService itemService;
  private final PageProperties pageProperties;

  private static final String PAGINATION_URL = "/%s/itemList?itemIds=%s&pageNo=%d&pageSize=%d";

  @AddItemDoc
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

  @UpdateItemDetailsDoc
  @PutMapping("/{itemId}/{orgId}/{uom}")
  public ResponseEntity<BaseResponse<ItemResponse>> updateItemDetails(
      @NotBlank(message = "itemId can't be empty")
          @Parameter(description = ItemConstants.ITEM_ID, example = ItemConstants.ITEM_ID_EXAMPLE)
          @PathVariable
          String itemId,
      @NotBlank(message = "orgId can't be empty")
          @Parameter(description = ItemConstants.ORG_ID, example = ItemConstants.ORG_ID_EXAMPLE)
          @PathVariable
          String orgId,
      @NotBlank(message = "uom can't be empty")
          @Parameter(description = ItemConstants.UOM, example = ItemConstants.UOM_EXAMPLE)
          @PathVariable
          String uom,
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

  @GetItemDetailsDoc
  @GetMapping("/{itemId}/{orgId}/{uom}")
  public ResponseEntity<BaseResponse<ItemResponse>> getItemDetails(
      @NotBlank(message = "itemId can't be empty")
          @Parameter(description = ItemConstants.ITEM_ID, example = ItemConstants.ITEM_ID_EXAMPLE)
          @PathVariable
          String itemId,
      @NotBlank(message = "orgId can't be empty")
          @Parameter(description = ItemConstants.ORG_ID, example = ItemConstants.ORG_ID_EXAMPLE)
          @PathVariable
          String orgId,
      @NotBlank(message = "uom can't be empty")
          @Parameter(description = ItemConstants.UOM, example = ItemConstants.UOM_EXAMPLE)
          @PathVariable
          String uom)
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

  @DeleteItemDoc
  @DeleteMapping("/{itemId}/{orgId}/{uom}")
  public ResponseEntity<BaseResponse<ItemResponse>> deleteItem(
      @NotBlank(message = "itemId can't be empty")
          @Parameter(description = ItemConstants.ITEM_ID, example = ItemConstants.ITEM_ID_EXAMPLE)
          @PathVariable
          String itemId,
      @NotBlank(message = "orgId can't be empty")
          @Parameter(description = ItemConstants.ORG_ID, example = ItemConstants.ORG_ID_EXAMPLE)
          @PathVariable
          String orgId,
      @NotBlank(message = "uom can't be empty")
          @Parameter(description = ItemConstants.UOM, example = ItemConstants.UOM_EXAMPLE)
          @PathVariable
          String uom)
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

  @GetItemListDoc
  @GetMapping("/{orgId}")
  public List<ItemResponse> getItemList(
      @NotBlank(message = "orgId can't be empty")
          @Parameter(description = ItemConstants.ORG_ID, example = ItemConstants.ORG_ID_EXAMPLE)
          @PathVariable
          String orgId,
      @NotEmpty
          @Parameter(description = "List of the item IDs.", example = "ITEM-01,ITEM-02")
          @RequestParam
          List<String> itemList,
      @Parameter(
              description = "Flag indicating whether item buffer should be enabled.",
              example = "true")
          @RequestParam(defaultValue = "false", required = false)
          Boolean isItemBufferEnabled,
      @Parameter(
              description = "Date used for promising engine computation.",
              example = "2030-05-30T22:00:00Z")
          @RequestParam(required = false)
          Date promisingEngineDate)
      throws CommonServiceException, ItemBatchingDomainException {
    logger.debug("Processing get item details");
    try {
      return itemService.getItemList(itemList, orgId, isItemBufferEnabled, promisingEngineDate);
    } catch (Exception e) {
      logger.error("Failed to fetch list of item details");
      throw e;
    }
  }

  @GetItemListPaginatedDoc
  @GetMapping("/{orgId}/itemList")
  public ResponseEntity<BaseResponse<PagePayload<ItemListResponse>>> getItemsListPaginated(
      @NotBlank(message = "orgId can't be empty")
          @Parameter(description = ItemConstants.ORG_ID, example = ItemConstants.ORG_ID_EXAMPLE)
          @PathVariable
          String orgId,
      @NotBlank(message = "itemIds cannot be blank")
          @Parameter(description = "List of the item IDs.", example = "ITEM-01,ITEM-02")
          @RequestParam
          String itemIds,
      PageParams pageParams)
      throws CommonServiceException, ItemBatchingDomainException {
    logger.debug("Processing find itemList for an orgId");

    Page<ItemListResponse> itemListResponsePage =
        itemService.getItemListByItemIdAndOrgId(
            itemIds,
            orgId,
            pageParams.getPageNo().orElse(pageProperties.getPageNo()),
            pageParams.getPageSize().orElse(pageProperties.getPageSize()),
            pageParams.getSortBy().orElse(ITEM_SORT_BY),
            pageParams.getSortOrder().orElse(DEFAULT_SORT_ORDER));
    PagePayload<ItemListResponse> pagePayload =
        setItemPagePayload(itemListResponsePage, itemIds, pageParams, orgId);

    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Item List fetched successfully")
            .payload(pagePayload)
            .build());
  }

  private PagePayload<ItemListResponse> setItemPagePayload(
      Page<ItemListResponse> itemListResponsePage,
      String items,
      PageParams pageParams,
      String orgId) {
    PagePayload<ItemListResponse> pagePayload = new PagePayload<>();
    var pagination = new PagePayload.Pagination();
    pagination.setTotalRecords((int) itemListResponsePage.getTotalElements());
    pagination.setTotalPages(itemListResponsePage.getTotalPages());
    pagination.setCurrentPage(pageParams.getPageNo().orElse(pageProperties.getPageNo()));
    pagination.setSortOrder(pageParams.getSortOrder().orElse(DEFAULT_SORT_ORDER));
    pagination.setSortBy(pageParams.getSortBy().orElse(ITEM_SORT_BY));
    String nextUri =
        PaginationUtil.buildUriForPagination(
            pageParams.getPageNo().orElse(pageProperties.getPageNo()),
            itemListResponsePage.getTotalPages(),
            PAGINATION_NEXT_URL,
            PAGINATION_URL.formatted(
                orgId,
                items,
                (pageParams.getPageNo().orElse(pageProperties.getPageNo()) + 1),
                pageParams.getPageSize().orElse(pageProperties.getPageSize())));
    String previousUri =
        PaginationUtil.buildUriForPagination(
            pageParams.getPageNo().orElse(pageProperties.getPageNo()),
            itemListResponsePage.getTotalPages(),
            PAGINATION_PREVIOUS_URL,
            PAGINATION_URL.formatted(
                orgId,
                items,
                (pageParams.getPageNo().orElse(pageProperties.getPageNo()) - 1),
                pageParams.getPageSize().orElse(pageProperties.getPageSize())));
    pagination.setNext(nextUri);
    pagination.setPrevious(previousUri);
    pagePayload.setPagination(pagination);
    pagePayload.setData(itemListResponsePage.getContent());
    return pagePayload;
  }
}
