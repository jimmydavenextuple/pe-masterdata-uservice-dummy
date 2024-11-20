/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */
package com.nextuple.dataupload.service;

import static com.nextuple.common.constants.CommonConstants.DEFAULT_SORT_ORDER;
import static com.nextuple.item.domain.constants.ItemConstants.ITEM_SORT_BY;

import com.nextuple.common.base.PagePayload;
import com.nextuple.common.pojo.PageParams;
import com.nextuple.common.pojo.PageProperties;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.item.domain.feign.ItemFeign;
import com.nextuple.item.domain.outbound.ItemListResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemDetailsService {

  private final PageProperties pageProperties;
  private final ItemFeign itemFeign;

  public PagePayload<ItemListResponse> getItemDetailsListpaginated(
      String itemIds, String orgId, PageParams pageParams) {
    PagePayload<ItemListResponse> itemListResponsePagePayload = null;
    try {
      BaseResponse<PagePayload<ItemListResponse>> itemFeignItemsListPaginated =
          itemFeign.getItemsListPaginated(
              orgId,
              itemIds,
              pageParams.getPageNo().orElse(pageProperties.getPageNo()),
              pageParams.getPageSize().orElse(pageProperties.getPageSize()),
              pageParams.getSortBy().orElse(ITEM_SORT_BY),
              pageParams.getSortOrder().orElse(DEFAULT_SORT_ORDER));
      if (itemFeignItemsListPaginated.isSuccess()) {
        itemListResponsePagePayload = itemFeignItemsListPaginated.getPayload();
      }
    } catch (Exception e) {
      log.error("Error fetching item details Response.", e);
    }
    return itemListResponsePagePayload;
  }
}
