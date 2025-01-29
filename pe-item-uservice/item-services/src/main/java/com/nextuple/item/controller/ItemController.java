/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.item.controller;

import static com.nextuple.common.constants.CommonConstants.DEFAULT_SORT_ORDER;
import static com.nextuple.item.domain.constants.ItemConstants.*;

import com.nextuple.common.base.PagePayload;
import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.pojo.PageParams;
import com.nextuple.common.pojo.PageProperties;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.common.util.PaginationUtil;
import com.nextuple.item.controller.docs.*;
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
import org.springframework.web.bind.annotation.*;

/**
 * Controller for managing Item operations.
 *
 * <p>This controller provides APIs for performing CRUD operations on items, such as adding,
 * updating, retrieving, and deleting items. It also includes APIs for fetching item lists and
 * paginated lists of items, supporting various filtering and sorting options.
 *
 * <p>The controller is tagged with "Item APIs" for easy categorization in API documentation.
 */
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

  /**
   * Adds a new item to the system.
   *
   * <p>This endpoint processes a request to create a new item with the specified details. If the
   * item is created successfully, it returns a response with the item details. If there is an error
   * during creation, an exception is thrown.
   *
   * @param itemCreationRequest the request body containing the details of the item to be created.
   * @return a {@link ResponseEntity} containing a {@link BaseResponse} with the {@link
   *     ItemResponse} payload.
   * @throws ItemDomainException if an error occurs while processing the item creation.
   */
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

  /**
   * Updates the details of an existing item.
   *
   * <p>This endpoint processes a request to update the details of a specific item, identified by
   * its item ID, organization ID, and unit of measurement (UOM). If the item details are
   * successfully updated, it returns the updated item information. If there is an error during the
   * update, an exception is thrown.
   *
   * @param itemId the ID of the item to be updated.
   * @param orgId the organization ID to which the item belongs.
   * @param uom the unit of measurement (UOM) for the item.
   * @param itemUpdationRequest the request body containing the details to update the item with.
   * @return a {@link ResponseEntity} containing a {@link BaseResponse} with the updated {@link
   *     ItemResponse}.
   * @throws ItemDomainException if an error occurs during the item update process.
   * @throws CommonServiceException if a service-related error occurs while updating the item
   *     details.
   */
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

  /**
   * Retrieves the details of a specific item based on the provided item ID, organization ID, and
   * unit of measurement (UOM).
   *
   * <p>This endpoint processes a request to fetch the details of a specific item identified by its
   * item ID, organization ID, and UOM. If the item is successfully found, the details are returned
   * in the response. If an error occurs while fetching the item details, an exception is thrown.
   *
   * @param itemId the ID of the item whose details are to be fetched.
   * @param orgId the organization ID to which the item belongs.
   * @param uom the unit of measurement (UOM) for the item.
   * @return a {@link ResponseEntity} containing a {@link BaseResponse} with the fetched {@link
   *     ItemResponse}.
   * @throws ItemDomainException if an error occurs during the process of retrieving item details.
   * @throws CommonServiceException if a service-related error occurs while fetching the item
   *     details.
   */
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

  /**
   * Deletes a specific item identified by its item ID, organization ID, and unit of measurement
   * (UOM).
   *
   * <p>This endpoint processes a request to delete a specific item based on the provided item ID,
   * organization ID, and UOM. If the item is successfully deleted, a response with the result is
   * returned. If an error occurs during the deletion process, an exception is thrown.
   *
   * @param itemId the ID of the item to be deleted.
   * @param orgId the organization ID to which the item belongs.
   * @param uom the unit of measurement (UOM) for the item.
   * @return a {@link ResponseEntity} containing a {@link BaseResponse} with the {@link
   *     ItemResponse} of the deleted item.
   * @throws ItemDomainException if an error occurs during the process of deleting the item.
   * @throws CommonServiceException if a service-related error occurs while deleting the item.
   */
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

  /**
   * Fetches a list of items based on the provided organization ID, list of item IDs, and optional
   * parameters such as whether the item buffer is enabled and the date for promising engine
   * computation.
   *
   * <p>This endpoint processes a request to fetch a list of items for a given organization, item
   * IDs, and optional parameters like enabling item buffer and promising engine date. The method
   * returns the list of item details if successfully retrieved, or throws an exception if an error
   * occurs.
   *
   * @param orgId the ID of the organization to fetch items for.
   * @param itemList the list of item IDs for which details are to be fetched.
   * @param isItemBufferEnabled flag indicating whether the item buffer should be enabled (optional,
   *     default is false).
   * @param promisingEngineDate the date used for promising engine computation (optional).
   * @return a {@link List} of {@link ItemResponse} containing the details of the requested items.
   * @throws CommonServiceException if a general service-related error occurs while fetching the
   *     items.
   * @throws ItemBatchingDomainException if an error related to item batching occurs.
   */
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

  /**
   * Retrieves a paginated list of items based on the provided organization ID, item IDs, and
   * pagination parameters.
   *
   * <p>This endpoint processes a request to fetch a paginated list of items for a given
   * organization, item IDs, and optional pagination parameters such as page number, page size,
   * sorting fields, and sort order. The method returns the paginated list of item details if
   * successfully retrieved, or throws an exception if an error occurs.
   *
   * @param orgId the ID of the organization to fetch items for.
   * @param itemIds a comma-separated string containing the list of item IDs for which details are
   *     to be fetched.
   * @param pageParams pagination and sorting parameters (e.g., page number, page size, sort field,
   *     sort order).
   * @return a {@link ResponseEntity} containing a {@link BaseResponse} with the paginated item list
   *     payload.
   * @throws CommonServiceException if a general service-related error occurs while fetching the
   *     item list.
   * @throws ItemBatchingDomainException if an error related to item batching occurs.
   */
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
