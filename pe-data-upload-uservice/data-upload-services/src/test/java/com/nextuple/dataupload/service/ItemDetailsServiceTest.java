/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.dataupload.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static reactor.core.publisher.Mono.when;

import com.nextuple.common.base.PagePayload;
import com.nextuple.common.pojo.PageProperties;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.dataupload.util.TestUtil;
import com.nextuple.item.domain.feign.ItemFeign;
import com.nextuple.item.domain.outbound.ItemListResponse;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ItemDetailsServiceTest {

  @InjectMocks private ItemDetailsService itemDetailsService;

  @InjectMocks private TestUtil testUtil;

  @Mock private ItemFeign itemFeign;

  @Mock private PageProperties pageProperties;

  @Test
  @DisplayName("Get Item List Paginated")
  void getItemDetailsListpaginated() {
    PagePayload<ItemListResponse> pagePayload = new PagePayload<>();
    BaseResponse<PagePayload<ItemListResponse>> itemFeignItemsListPaginated =
        BaseResponse.builder().success(true).payload(pagePayload).build();
    when(itemFeign.getItemsListPaginated(any(), any(), any(), any(), any(), any()))
        .thenReturn(itemFeignItemsListPaginated);
    PagePayload<ItemListResponse> responsePagePayload =
        itemDetailsService.getItemDetailsListpaginated(
            "item-01,item-02",
            TestUtil.ORG_ID,
            testUtil.getPageParams(
                Optional.of(2), Optional.of(1), Optional.of("itemId"), Optional.of("ASC")));
    assertNotNull(responsePagePayload);
    assertEquals(responsePagePayload, pagePayload);
    verify(itemFeign, times(1)).getItemsListPaginated(any(), any(), any(), any(), any(), any());
  }

  @Test
  @DisplayName("Get Item List Paginated when response is null")
  void getItemDetailsListpaginatedNull() {
    when(itemFeign.getItemsListPaginated(any(), any(), any(), any(), any(), any()))
        .thenReturn(null);
    PagePayload<ItemListResponse> responsePagePayload =
        itemDetailsService.getItemDetailsListpaginated(
            "item-01,item-02",
            TestUtil.ORG_ID,
            testUtil.getPageParams(
                Optional.of(2), Optional.of(1), Optional.of("itemId"), Optional.of("ASC")));
    assertNull(responsePagePayload);
    verify(itemFeign, times(1)).getItemsListPaginated(any(), any(), any(), any(), any(), any());
  }

  @Test
  @DisplayName("Get Item List Paginated not sccess")
  void getItemDetailsListpaginatedNotSuccess() {
    PagePayload<ItemListResponse> pagePayload = new PagePayload<>();
    BaseResponse<PagePayload<ItemListResponse>> itemFeignItemsListPaginated =
        BaseResponse.builder().success(false).payload(pagePayload).build();
    when(itemFeign.getItemsListPaginated(any(), any(), any(), any(), any(), any()))
        .thenReturn(itemFeignItemsListPaginated);
    PagePayload<ItemListResponse> responsePagePayload =
        itemDetailsService.getItemDetailsListpaginated(
            "item-01,item-02",
            TestUtil.ORG_ID,
            testUtil.getPageParams(
                Optional.of(2), Optional.of(1), Optional.of("itemId"), Optional.of("ASC")));
    assertNull(responsePagePayload);
    verify(itemFeign, times(1)).getItemsListPaginated(any(), any(), any(), any(), any(), any());
  }
}
