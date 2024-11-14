/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.dataupload.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.dataupload.util.TestUtil;
import com.nextuple.item.domain.feign.ItemBufferUIFeign;
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
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class ItemBufferDetailsServiceTest {

  @InjectMocks ItemBufferDetailsService itemBufferDetailsService;

  @Mock ItemBufferUIFeign itemBufferUIFeign;

  @Test
  @DisplayName("Get Items List with Configured Buffers")
  void getItemsListWithConfiguredBuffersTest() {
    Integer pageNo = 1;
    Integer pageSize = 10;
    PageResponseForItemBuffer feignResponse = new PageResponseForItemBuffer();
    ResponseEntity<BaseResponse<PageResponseForItemBuffer>> responseEntity =
        ResponseEntity.ok(BaseResponse.builder().payload(feignResponse).build());
    when(itemBufferUIFeign.getItemsListWithConfiguredBuffers(TestUtil.ORG_ID, pageNo, pageSize))
        .thenReturn(responseEntity);
    PageResponseForItemBuffer response =
        itemBufferDetailsService.getItemsListWithConfiguredBuffers(
            TestUtil.ORG_ID, null, pageNo, pageSize);
    assertNotNull(response);
    assertEquals(feignResponse, response);
    verify(itemBufferUIFeign, times(1))
        .getItemsListWithConfiguredBuffers(TestUtil.ORG_ID, pageNo, pageSize);
  }

  @Test
  @DisplayName("Get Items List by itemIds with Configured Buffers")
  void getItemsListByItemIdsWithConfiguredBuffersTest() {
    Integer pageNo = 1;
    Integer pageSize = 10;
    PageResponseForItemBuffer feignResponse = new PageResponseForItemBuffer();
    ResponseEntity<BaseResponse<PageResponseForItemBuffer>> responseEntity =
        ResponseEntity.ok(BaseResponse.builder().payload(feignResponse).build());
    when(itemBufferUIFeign.getItemsListWithConfiguredBuffersV1(
            TestUtil.ORG_ID, TestUtil.ITEM_IDS, pageNo, pageSize))
        .thenReturn(responseEntity);
    PageResponseForItemBuffer response =
        itemBufferDetailsService.getItemsListWithConfiguredBuffers(
            TestUtil.ORG_ID, TestUtil.ITEM_IDS, pageNo, pageSize);
    assertNotNull(response);
    assertEquals(feignResponse, response);
    verify(itemBufferUIFeign, times(1))
        .getItemsListWithConfiguredBuffersV1(TestUtil.ORG_ID, TestUtil.ITEM_IDS, pageNo, pageSize);
    verify(itemBufferUIFeign, times(0))
        .getItemsListWithConfiguredBuffers(TestUtil.ORG_ID, pageNo, pageSize);
  }

  @Test
  @DisplayName("Get Item Buffers List")
  void getItemBuffersListTest() {
    List<ItemBufferResponse> bufferResponseList = new ArrayList<>();
    ResponseEntity<BaseResponse<List<ItemBufferResponse>>> responseEntity =
        ResponseEntity.ok(BaseResponse.builder().payload(bufferResponseList).build());
    when(itemBufferUIFeign.getItemBuffersByOrgIdAndItemIdAndUom(
            TestUtil.ORG_ID, TestUtil.ITEM_ID, TestUtil.UOM))
        .thenReturn(responseEntity);
    List<ItemBufferResponse> response =
        itemBufferDetailsService.getItemBuffersList(
            TestUtil.ORG_ID, TestUtil.ITEM_ID, TestUtil.UOM);
    assertNotNull(response);
    assertEquals(bufferResponseList, response);
    verify(itemBufferUIFeign, times(1))
        .getItemBuffersByOrgIdAndItemIdAndUom(TestUtil.ORG_ID, TestUtil.ITEM_ID, TestUtil.UOM);
  }

  @Test
  @DisplayName("Get Items List with Configured Buffers when Response is Null")
  void getItemsListWithConfiguredBuffersWhenResponseIsNullTest() {
    Integer pageNo = 1;
    Integer pageSize = 10;
    when(itemBufferUIFeign.getItemsListWithConfiguredBuffers(TestUtil.ORG_ID, pageNo, pageSize))
        .thenReturn(null);
    PageResponseForItemBuffer response =
        itemBufferDetailsService.getItemsListWithConfiguredBuffers(
            TestUtil.ORG_ID, null, pageNo, pageSize);
    assertNull(response);
    verify(itemBufferUIFeign, times(1))
        .getItemsListWithConfiguredBuffers(TestUtil.ORG_ID, pageNo, pageSize);
  }

  @Test
  @DisplayName("Get Items List with Configured Buffers when Payload is Null")
  void getItemsListWithConfiguredBuffersWhenPayloadIsNullTest() {
    Integer pageNo = 1;
    Integer pageSize = 10;
    ResponseEntity<BaseResponse<PageResponseForItemBuffer>> responseEntity =
        ResponseEntity.ok(BaseResponse.builder().build());
    when(itemBufferUIFeign.getItemsListWithConfiguredBuffers(TestUtil.ORG_ID, pageNo, pageSize))
        .thenReturn(responseEntity);
    PageResponseForItemBuffer response =
        itemBufferDetailsService.getItemsListWithConfiguredBuffers(
            TestUtil.ORG_ID, null, pageNo, pageSize);
    assertNull(response);
    verify(itemBufferUIFeign, times(1))
        .getItemsListWithConfiguredBuffers(TestUtil.ORG_ID, pageNo, pageSize);
  }

  @Test
  @DisplayName("Get Item Buffers List when Response is Null")
  void getItemBuffersListWhenResponseIsNullTest() {
    ResponseEntity<BaseResponse<List<ItemBufferResponse>>> responseEntity =
        ResponseEntity.ok().body(null);
    when(itemBufferUIFeign.getItemBuffersByOrgIdAndItemIdAndUom(
            TestUtil.ORG_ID, TestUtil.ITEM_ID, TestUtil.UOM))
        .thenReturn(responseEntity);
    List<ItemBufferResponse> response =
        itemBufferDetailsService.getItemBuffersList(
            TestUtil.ORG_ID, TestUtil.ITEM_ID, TestUtil.UOM);
    assertNull(response);
    verify(itemBufferUIFeign, times(1))
        .getItemBuffersByOrgIdAndItemIdAndUom(TestUtil.ORG_ID, TestUtil.ITEM_ID, TestUtil.UOM);
  }

  @Test
  @DisplayName("Get Item Buffers List when Payload is Null")
  void getItemBuffersListWhenPayloadIsNullTest() {
    List<ItemBufferResponse> bufferResponseList = null;
    ResponseEntity<BaseResponse<List<ItemBufferResponse>>> responseEntity =
        ResponseEntity.ok(BaseResponse.builder().payload(bufferResponseList).build());
    when(itemBufferUIFeign.getItemBuffersByOrgIdAndItemIdAndUom(
            TestUtil.ORG_ID, TestUtil.ITEM_ID, TestUtil.UOM))
        .thenReturn(responseEntity);
    List<ItemBufferResponse> response =
        itemBufferDetailsService.getItemBuffersList(
            TestUtil.ORG_ID, TestUtil.ITEM_ID, TestUtil.UOM);
    assertNull(response);
    verify(itemBufferUIFeign, times(1))
        .getItemBuffersByOrgIdAndItemIdAndUom(TestUtil.ORG_ID, TestUtil.ITEM_ID, TestUtil.UOM);
  }

  @Test
  @DisplayName("Get Item Buffers List when Feign Client throws Exception")
  void getItemBuffersListWhenFeignThrowsExceptionTest() {
    when(itemBufferUIFeign.getItemBuffersByOrgIdAndItemIdAndUom(
            TestUtil.ORG_ID, TestUtil.ITEM_ID, TestUtil.UOM))
        .thenThrow(new RuntimeException("Feign client exception"));
    List<ItemBufferResponse> response =
        itemBufferDetailsService.getItemBuffersList(
            TestUtil.ORG_ID, TestUtil.ITEM_ID, TestUtil.UOM);
    assertNull(response);
    verify(itemBufferUIFeign, times(1))
        .getItemBuffersByOrgIdAndItemIdAndUom(TestUtil.ORG_ID, TestUtil.ITEM_ID, TestUtil.UOM);
  }
}
