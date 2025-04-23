/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.item.domain.feign;

import com.nextuple.common.base.PagePayload;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.item.domain.inbound.ItemBaseRequest;
import com.nextuple.item.domain.inbound.ItemCreationRequest;
import com.nextuple.item.domain.outbound.ItemListResponse;
import com.nextuple.item.domain.outbound.ItemResponse;
import java.util.Date;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
    name = "pe-item-uservice",
    url = "${spring.application.dependencies.item:http://pe-item-uservice:8080/}")
public interface ItemFeign {

  @PostMapping("/item/upsert")
  BaseResponse<ItemResponse> addOrUpdateItem(@RequestBody ItemCreationRequest itemCreationRequest);

  @PostMapping("/item")
  BaseResponse<ItemResponse> addItem(@RequestBody ItemCreationRequest itemCreationRequest);

  @PutMapping("/item/{itemId}/{orgId}/{uom}")
  BaseResponse<ItemResponse> updateItemDetails(
      @PathVariable String itemId,
      @PathVariable String orgId,
      @PathVariable String uom,
      @RequestBody ItemBaseRequest itemBaseRequest);

  @GetMapping("/item/{itemId}/{orgId}/{uom}")
  BaseResponse<ItemResponse> getItemDetails(
      @PathVariable String itemId, @PathVariable String orgId, @PathVariable String uom);

  @DeleteMapping("/item/{itemId}/{orgId}/{uom}")
  BaseResponse<ItemResponse> deleteItem(
      @PathVariable String itemId, @PathVariable String orgId, @PathVariable String uom);

  @GetMapping("/item/{orgId}")
  List<ItemResponse> getItemList(@PathVariable String orgId, @RequestParam List<String> itemList);

  @GetMapping("/item/{orgId}")
  List<ItemResponse> getItemList(
      @PathVariable String orgId,
      @RequestParam List<String> itemList,
      @RequestParam Boolean isItemBufferEnabled,
      @RequestParam Date promisingEngineDate);

  @GetMapping("/item/{orgId}/itemList")
  BaseResponse<PagePayload<ItemListResponse>> getItemsListPaginated(
      @PathVariable String orgId,
      @RequestParam String itemIds,
      @RequestParam(required = false) Integer pageNo,
      @RequestParam(required = false) Integer pageSize,
      @RequestParam(required = false) String sortBy,
      @RequestParam(required = false) String sortOrder);
}
