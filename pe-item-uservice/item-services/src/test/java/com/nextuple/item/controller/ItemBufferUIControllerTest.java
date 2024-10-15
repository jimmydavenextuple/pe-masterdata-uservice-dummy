/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.item.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.pojo.PageParams;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.item.TestUtil;
import com.nextuple.item.domain.outbound.ItemBufferResponse;
import com.nextuple.item.domain.outbound.PageResponseForItemBuffer;
import com.nextuple.item.service.ItemBufferService;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class ItemBufferUIControllerTest {

  @InjectMocks private ItemBufferUIController itemBufferUIController;

  @InjectMocks private TestUtil testUtil;

  @Mock private ItemBufferService itemBufferService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  @DisplayName("Happy path - Get Item Buffers By ItemId and OrgId And Uom")
  void getItemBuffersByOrgIdAndItemIdAndUomTest() throws CommonServiceException {

    when(itemBufferService.getItemBuffersByItemIdAndOrgIdAndUom(
            TestUtil.ITEM_ID, TestUtil.ORG_ID, TestUtil.UOM))
        .thenReturn(testUtil.getListOfItemBufferResponse());

    ResponseEntity<BaseResponse<List<ItemBufferResponse>>> responseEntity =
        itemBufferUIController.getItemBuffersByOrgIdAndItemIdAndUom(
            TestUtil.ORG_ID, TestUtil.ITEM_ID, TestUtil.UOM);

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals("Item Buffer list fetched successfully", responseEntity.getBody().getMessage());
    verify(itemBufferService, times(1))
        .getItemBuffersByItemIdAndOrgIdAndUom(TestUtil.ITEM_ID, TestUtil.ORG_ID, TestUtil.UOM);
  }

  @Test
  @DisplayName("Happy path - Get Items List With Configured Buffers")
  void getItemsListWithConfiguredBuffersTest() throws CommonServiceException {
    PageParams pageParams = new PageParams();
    pageParams.setPageNo(Optional.of(1));
    pageParams.setPageSize(Optional.of(10));
    when(itemBufferService.getItemsListWithConfiguredBuffers(TestUtil.ORG_ID, null, pageParams))
        .thenReturn(new PageResponseForItemBuffer());

    ResponseEntity<BaseResponse<PageResponseForItemBuffer>> responseEntity =
        itemBufferUIController.getItemsListWithConfiguredBuffers(TestUtil.ORG_ID, 1, 10);

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals("Item details fetched successfully", responseEntity.getBody().getMessage());
    assertEquals(new PageResponseForItemBuffer(), responseEntity.getBody().getPayload());

    verify(itemBufferService, times(1))
        .getItemsListWithConfiguredBuffers(TestUtil.ORG_ID, null, pageParams);
  }

  @Test
  @DisplayName("Happy path - Get Items List With Configured Buffers for given itemIds")
  void getItemsListWithConfiguredBuffersV1Test() throws CommonServiceException {
    PageParams pageParams = new PageParams();
    pageParams.setPageNo(Optional.of(1));
    pageParams.setPageSize(Optional.of(10));
    when(itemBufferService.getItemsListWithConfiguredBuffers(
            TestUtil.ORG_ID, TestUtil.ITEM_ID_1, pageParams))
        .thenReturn(testUtil.getPageResponseForItemBuffer());

    ResponseEntity<BaseResponse<PageResponseForItemBuffer>> responseEntity =
        itemBufferUIController.getItemsListWithConfiguredBuffersV1(
            TestUtil.ORG_ID, TestUtil.ITEM_ID_1, 1, 10);

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals("Item details fetched successfully", responseEntity.getBody().getMessage());
    assertEquals(
        testUtil.getPageResponseForItemBuffer().getData(),
        responseEntity.getBody().getPayload().getData());

    verify(itemBufferService, times(1))
        .getItemsListWithConfiguredBuffers(TestUtil.ORG_ID, TestUtil.ITEM_ID_1, pageParams);
  }
}
