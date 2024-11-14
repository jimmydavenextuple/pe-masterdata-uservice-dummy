/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.dataupload.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.dataupload.service.ItemBufferDetailsService;
import com.nextuple.dataupload.util.TestUtil;
import com.nextuple.item.domain.outbound.ItemBufferResponse;
import com.nextuple.item.domain.outbound.PageResponseForItemBuffer;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class ItemBufferDashboardControllerTest {

  @InjectMocks ItemBufferDashboardController itemBufferDashboardController;

  @Mock ItemBufferDetailsService itemBufferDetailsService;

  @Test
  @DisplayName("Get Item details Controller test")
  void getItemDetailsTest() {
    Integer pageNo = 1;
    Integer pageSize = 10;
    String orgId = TestUtil.ORG_ID;
    PageResponseForItemBuffer pageResponse = new PageResponseForItemBuffer();

    when(itemBufferDetailsService.getItemsListWithConfiguredBuffers(orgId, null, pageNo, pageSize))
        .thenReturn(pageResponse);

    ResponseEntity<BaseResponse<PageResponseForItemBuffer>> responseEntity =
        itemBufferDashboardController.getItemDetails(orgId, null, pageNo, pageSize);

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals("Item Details fetched successfully", responseEntity.getBody().getMessage());
    assertEquals(pageResponse, responseEntity.getBody().getPayload());

    verify(itemBufferDetailsService, times(1))
        .getItemsListWithConfiguredBuffers(orgId, null, pageNo, pageSize);
  }

  @Test
  @DisplayName("Get Item buffer details Controller test")
  void getItemBufferDetailsTest() {
    List<ItemBufferResponse> bufferResponseList = new ArrayList<>();

    when(itemBufferDetailsService.getItemBuffersList(
            TestUtil.ORG_ID, TestUtil.ITEM_ID, TestUtil.UOM))
        .thenReturn(bufferResponseList);

    ResponseEntity<BaseResponse<List<ItemBufferResponse>>> responseEntity =
        itemBufferDashboardController.getItemBufferDetails(
            TestUtil.ORG_ID, TestUtil.ITEM_ID, TestUtil.UOM);

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals("Item buffer list fetched successfully", responseEntity.getBody().getMessage());
    assertEquals(bufferResponseList, responseEntity.getBody().getPayload());

    verify(itemBufferDetailsService, times(1))
        .getItemBuffersList(TestUtil.ORG_ID, TestUtil.ITEM_ID, TestUtil.UOM);
  }
}
