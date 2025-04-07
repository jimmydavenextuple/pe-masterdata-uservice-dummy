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

/**
 * Controller for managing Item operations.
 *
 * <p>This controller provides REST endpoints for creating, updating, retrieving, and deleting
 * items. It supports both individual item operations and batch processing with pagination.
 *
 * <p>Key features include:
 *
 * <ul>
 *   <li>Item creation and modification
 *   <li>Detailed item information retrieval
 *   <li>Item deletion
 *   <li>Batch item retrieval with pagination
 *   <li>Support for item buffer configuration
 * </ul>
 *
 * <p>The controller includes comprehensive error handling and validation for all operations. It
 * uses {@link ItemService} for business logic implementation and supports both standard and
 * paginated responses.
 *
 * @see ItemService
 * @see ItemResponse
 * @see ItemCreationRequest
 * @see ItemUpdationRequest
 * @see PagePayload
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
   * Creates a new item based on the provided request.
   *
   * @param itemCreationRequest The request containing item details for creation
   * @return A {@link ResponseEntity} containing the created item details
   * @throws ItemDomainException If there is an error in item creation
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
   * Updates existing item details.
   *
   * @param itemId The unique identifier for the item
   * @param orgId The organization identifier
   * @param uom The unit of measure
   * @param itemUpdationRequest The request containing updated item details
   * @return A {@link ResponseEntity} containing the updated item details
   * @throws ItemDomainException If there is an error in item update
   * @throws CommonServiceException If there is a general service error
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
   * Retrieves detailed information for a specific item.
   *
   * <p>This method processes a GET request to fetch detailed information about an item identified
   * by the combination of item ID, organization ID, and unit of measure.
   *
   * @param itemId The unique identifier for the item. Must not be blank. Example value: "ITEM-01"
   * @param orgId The organization identifier. Must not be blank. Example value: "NEXTUPLE"
   * @param uom The unit of measure for the item. Must not be blank. Example value: "EACH"
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with the {@link
   *     ItemResponse} containing detailed item information.
   * @throws ItemDomainException If there is an error in the item domain processing
   * @throws CommonServiceException If there is a general service error
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
   * Deletes a specific item from the system.
   *
   * <p>This method processes a DELETE request to remove an item identified by the combination of
   * item ID, organization ID, and unit of measure. The method performs validation checks before
   * proceeding with the deletion.
   *
   * @param itemId The unique identifier for the item. Must not be blank. Example value: "ITEM-01"
   * @param orgId The organization identifier. Must not be blank. Example value: "NEXTUPLE"
   * @param uom The unit of measure for the item. Must not be blank. Example value: "EACH"
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with the {@link
   *     ItemResponse} of the deleted item.
   * @throws ItemDomainException If there is an error in the item domain processing
   * @throws CommonServiceException If there is a general service error or the item cannot be
   *     deleted
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
   * Retrieves a list of items based on specified criteria.
   *
   * <p>This method processes a GET request to fetch multiple items at once. It supports optional
   * item buffer configuration and promising engine date parameters for enhanced item information
   * retrieval.
   *
   * @param orgId The unique identifier for the organization. Must not be blank. Example value:
   *     "NEXTUPLE"
   * @param itemList List of item IDs to retrieve. Must not be empty. Example value: ["ITEM-01",
   *     "ITEM-02"]
   * @param isItemBufferEnabled Optional flag to enable item buffer information. Default value is
   *     false. Example value: true
   * @param promisingEngineDate Optional date used for promising engine computations. Format:
   *     ISO-8601 Example value: "2030-05-30T22:00:00Z"
   * @return A List of {@link ItemResponse} objects containing the requested item details
   * @throws CommonServiceException If there is a general service error
   * @throws ItemBatchingDomainException If there is an error in batch processing of items
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
   * Retrieves a paginated list of items for a specific organization.
   *
   * <p>This method processes a GET request to fetch a paginated list of items based on provided
   * item IDs and organization ID. It supports customizable pagination and sorting options through
   * the {@link PageParams}.
   *
   * <p>The pagination features include:
   *
   * <ul>
   *   <li>Configurable page size and number
   *   <li>Sorting by specified fields
   *   <li>Navigation links for next and previous pages
   *   <li>Total record count and page information
   * </ul>
   *
   * @param orgId The unique identifier for the organization. Must not be blank. Example value:
   *     "NEXTUPLE"
   * @param itemIds Comma-separated list of item IDs to retrieve. Must not be blank. Example value:
   *     "ITEM-01,ITEM-02"
   * @param pageParams The pagination parameters including:
   *     <ul>
   *       <li>pageNo - Page number (default: from properties)
   *       <li>pageSize - Records per page (default: from properties)
   *       <li>sortBy - Field to sort by (default: ITEM_SORT_BY)
   *       <li>sortOrder - Sort direction (default: DEFAULT_SORT_ORDER)
   *     </ul>
   *
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with a {@link PagePayload}
   *     of {@link ItemListResponse} objects.
   * @throws CommonServiceException If there is a general service error
   * @throws ItemBatchingDomainException If there is an error in batch processing of items
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
