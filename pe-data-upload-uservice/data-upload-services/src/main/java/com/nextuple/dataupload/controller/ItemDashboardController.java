/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.dataupload.controller;

import com.nextuple.common.base.PagePayload;
import com.nextuple.common.pojo.PageParams;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.dataupload.controller.docs.GetItemListDoc;
import com.nextuple.dataupload.service.ItemDetailsService;
import com.nextuple.item.domain.constants.ItemConstants;
import com.nextuple.item.domain.outbound.ItemListResponse;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for item details UI APIs.
 *
 * <p>This controller provides APIs to fetch paginated item details for organizations based on
 * specified item IDs and pagination parameters.
 *
 * <p>The controller is tagged with "Item Details UI APIs" for easy categorization in API
 * documentation.
 */
@RestController
@RequestMapping("/ui/item")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Item Details UI APIs")
public class ItemDashboardController {

  private final ItemDetailsService itemDetailsService;

  /**
   * Retrieves a paginated list of item details for the specified organization and items.
   *
   * <p>This method processes a GET request to fetch a paginated list of item details based on the
   * provided organization ID and item IDs. Pagination support is included through the `pageParams`
   * parameter.
   *
   * @param orgId The unique identifier for the organization (e.g., "NEXTUPLE_GR").
   * @param itemIds The comma-separated string of item IDs for which the details are being fetched.
   * @param pageParams The pagination parameters, such as page number, page size, sorting criteria,
   *     and sort order.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with the paginated list of
   *     item details.
   */
  @GetItemListDoc
  @GetMapping("/{orgId}/itemList")
  public ResponseEntity<BaseResponse<PagePayload<ItemListResponse>>> getItemDetails(
      @NotBlank(message = "orgId can't be empty")
          @PathVariable
          @Parameter(description = ItemConstants.ORG_ID, example = "NEXTUPLE_GR")
          String orgId,
      @NotEmpty(message = "itemIds cannot be blank") @RequestParam String itemIds,
      PageParams pageParams) {
    log.debug("Processing item details with Pagination.");
    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Item Details fetched successfully")
            .payload(itemDetailsService.getItemDetailsListpaginated(itemIds, orgId, pageParams))
            .build());
  }
}
