/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.item.controller;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.pojo.PageParams;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.item.domain.constants.ItemConstants;
import com.nextuple.item.domain.outbound.ItemBufferResponse;
import com.nextuple.item.domain.outbound.PageResponseForItemBuffer;
import com.nextuple.item.service.ItemBufferService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@Tag(name = "Item Buffer Dashboard APIs")
@RequestMapping("/item-buffer/ui")
@RequiredArgsConstructor
@Slf4j
public class ItemBufferUIController {
  private final ItemBufferService itemBufferService;

  @GetMapping("/{orgId}/{itemId}/{uom}")
  public ResponseEntity<BaseResponse<List<ItemBufferResponse>>>
      getItemBuffersByOrgIdAndItemIdAndUom(
          @NotBlank(message = "orgId can't be empty")
              @Parameter(description = ItemConstants.ORG_ID, example = ItemConstants.ORG_ID_EXAMPLE)
              @PathVariable
              String orgId,
          @NotBlank(message = "itemId can't be empty")
              @Parameter(
                  description = ItemConstants.ITEM_ID,
                  example = ItemConstants.ITEM_ID_EXAMPLE)
              @PathVariable
              String itemId,
          @NotBlank(message = "uom can't be empty")
              @Parameter(description = ItemConstants.UOM, example = ItemConstants.UOM_EXAMPLE)
              @PathVariable
              String uom)
          throws CommonServiceException {
    log.debug("Processing get item buffer request {}");
    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Item Buffer list fetched successfully")
            .payload(itemBufferService.getItemBuffersByItemIdAndOrgIdAndUom(itemId, orgId, uom))
            .build());
  }

  @GetMapping("/{orgId}")
  public ResponseEntity<BaseResponse<PageResponseForItemBuffer>> getItemsListWithConfiguredBuffers(
      @NotBlank(message = "orgId can't be empty")
          @Parameter(description = ItemConstants.ORG_ID, example = ItemConstants.ORG_ID_EXAMPLE)
          @PathVariable
          String orgId,
      @RequestParam(required = false, defaultValue = "1") Integer pageNo,
      @RequestParam(required = false, defaultValue = "10") Integer pageSize)
      throws CommonServiceException {
    log.debug("Processing get items list request {}");
    PageParams pageParams = new PageParams();
    pageParams.setPageNo(Optional.ofNullable(pageNo));
    pageParams.setPageSize(Optional.ofNullable(pageSize));
    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Item details fetched successfully")
            .payload(itemBufferService.getItemsListWithConfiguredBuffers(orgId, null, pageParams))
            .build());
  }

  @GetMapping("/v1/{orgId}")
  public ResponseEntity<BaseResponse<PageResponseForItemBuffer>>
      getItemsListWithConfiguredBuffersV1(
          @NotBlank(message = "orgId can't be empty")
              @Parameter(description = ItemConstants.ORG_ID, example = ItemConstants.ORG_ID_EXAMPLE)
              @PathVariable
              String orgId,
          @RequestParam(required = false)
              @Parameter(
                  description =
                      "Comma separated string that contains references of the items to be searched for.")
              String itemIds,
          @RequestParam(required = false, defaultValue = "1") Integer pageNo,
          @RequestParam(required = false, defaultValue = "10") Integer pageSize)
          throws CommonServiceException {
    PageParams pageParams = new PageParams();
    pageParams.setPageNo(Optional.ofNullable(pageNo));
    pageParams.setPageSize(Optional.ofNullable(pageSize));
    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Item details fetched successfully")
            .payload(
                itemBufferService.getItemsListWithConfiguredBuffers(orgId, itemIds, pageParams))
            .build());
  }
}
