/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.dataupload.controller;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.dataupload.controller.docs.GetItemBuffersDetailsForOrgIdAndItemIdDoc;
import com.nextuple.dataupload.controller.docs.GetItemListWithBufferConfiguredDoc;
import com.nextuple.dataupload.service.ItemBufferDetailsService;
import com.nextuple.item.domain.constants.ItemConstants;
import com.nextuple.item.domain.outbound.ItemBufferResponse;
import com.nextuple.item.domain.outbound.PageResponseForItemBuffer;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for item buffer UI APIs.
 *
 * <p>This controller provides APIs to fetch item buffer configurations and buffer details for
 * organizations, with support for pagination and filtering.
 *
 * <p>The controller is tagged with "Item Buffer UI APIs" for easy categorization in API
 * documentation.
 */
@RestController
@RequestMapping("/item-buffer/ui/orgId/{orgId}")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Item Buffer UI APIs")
public class ItemBufferDashboardController {

  private final ItemBufferDetailsService itemBufferDetailsService;

  /**
   * Retrieves a paginated list of items with buffer configurations for the specified organization.
   *
   * <p>This method processes a GET request to fetch a list of items with buffer configurations
   * based on the provided organization ID and optional item IDs, with support for pagination.
   *
   * @param orgId The unique identifier for the organization (e.g., "NEXTUPLE_GR").
   * @param itemIds The optional comma-separated string containing references of the items to be
   *     searched for.
   * @param pageNo The page number for pagination (default is 1).
   * @param pageSize The page size for pagination (default is 10).
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with the paginated list of
   *     items and buffer configurations.
   */
  @GetMapping
  @GetItemListWithBufferConfiguredDoc
  public ResponseEntity<BaseResponse<PageResponseForItemBuffer>> getItemDetails(
      @PathVariable @NotBlank @Parameter(description = ItemConstants.ORG_ID) String orgId,
      @RequestParam(required = false)
          @Parameter(
              description =
                  "Comma separated string that contains references of the items to be searched for.")
          String itemIds,
      @RequestParam(required = false, defaultValue = "1") Integer pageNo,
      @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
    log.debug("Processing get item details");
    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Item Details fetched successfully")
            .payload(
                itemBufferDetailsService.getItemsListWithConfiguredBuffers(
                    orgId, itemIds, pageNo, pageSize))
            .build());
  }

  /**
   * Retrieves the buffer details for the specified item in the specified organization and unit of
   * measure.
   *
   * <p>This method processes a GET request to fetch the buffer details for a specific item,
   * organization, and unit of measure (UOM).
   *
   * @param orgId The unique identifier for the organization (e.g., "NEXTUPLE_GR").
   * @param itemId The unique identifier for the item.
   * @param uom The unit of measure for the item.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with the list of buffer
   *     details for the item.
   */
  @GetMapping("/{itemId}/{uom}")
  @GetItemBuffersDetailsForOrgIdAndItemIdDoc
  public ResponseEntity<BaseResponse<List<ItemBufferResponse>>> getItemBufferDetails(
      @PathVariable @NotBlank @Parameter(description = ItemConstants.ORG_ID) String orgId,
      @PathVariable @NotBlank @Parameter(description = ItemConstants.ITEM_ID) String itemId,
      @PathVariable @NotBlank @Parameter(description = ItemConstants.UOM) String uom) {
    log.debug("Processing get buffer item details");
    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Item buffer list fetched successfully")
            .payload(itemBufferDetailsService.getItemBuffersList(orgId, itemId, uom))
            .build());
  }
}
