/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.item.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.nextuple.common.base.PagePayload;
import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.pojo.PageProperties;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.common.validation.ValidatorUtil;
import com.nextuple.item.TestUtil;
import com.nextuple.item.domain.inbound.ItemBaseRequest;
import com.nextuple.item.domain.inbound.ItemCreationRequest;
import com.nextuple.item.domain.inbound.ItemDetailsWithSubstitutionRequest;
import com.nextuple.item.domain.outbound.ItemListResponse;
import com.nextuple.item.domain.outbound.ItemResponse;
import com.nextuple.item.persistence.exception.ItemBatchingDomainException;
import com.nextuple.item.persistence.exception.ItemDomainException;
import com.nextuple.item.service.ItemService;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class ItemControllerTest {

  @InjectMocks private ItemController itemController;

  @InjectMocks private TestUtil testUtil;

  @Mock private ItemService itemService;
  @Mock private PageProperties pageProperties;
  @Mock ValidatorUtil validatorUtil;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void addItemTest() throws ItemDomainException {
    ItemCreationRequest itemCreationRequest = testUtil.getItemCreationRequest();
    when(itemService.createItem(any(ItemCreationRequest.class)))
        .thenReturn(testUtil.getItemResponse());

    ResponseEntity<BaseResponse<ItemResponse>> responseEntity =
        itemController.addItem(itemCreationRequest);

    Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    Assertions.assertEquals(testUtil.getItemResponse(), responseEntity.getBody().getPayload());

    verify(itemService, times(1)).createItem(any());
  }

  @Test
  void addItemExceptionTest() throws ItemDomainException {
    ItemCreationRequest itemCreationRequest = testUtil.getItemCreationRequest();
    when(itemService.createItem(any(ItemCreationRequest.class)))
        .thenThrow(new RuntimeException("Failed to add item"));

    Exception exception =
        Assertions.assertThrows(Exception.class, () -> itemController.addItem(itemCreationRequest));
    Assertions.assertEquals("Failed to add item", exception.getMessage());

    verify(itemService, times(1)).createItem(any());
  }

  @Test
  void getItemDetailsByItemIdAndOrgIdAndUomTest()
      throws ItemDomainException, CommonServiceException {
    ItemResponse itemResponse = testUtil.getItemResponse();
    when(itemService.getItemDetails(any(), any(), any())).thenReturn(itemResponse);

    ResponseEntity<BaseResponse<ItemResponse>> responseEntity =
        itemController.getItemDetails(TestUtil.ITEM_ID, TestUtil.ORG_ID, TestUtil.UOM);

    Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    Assertions.assertEquals(itemResponse, responseEntity.getBody().getPayload());

    verify(itemService, times(1)).getItemDetails(any(), any(), any());
  }

  @Test
  void getItemDetailsByItemIdAndOrgIdAndUomExceptionTest()
      throws ItemDomainException, CommonServiceException {
    when(itemService.getItemDetails(any(), any(), any()))
        .thenThrow(new RuntimeException("Failed to fetch item details"));

    Exception exception =
        Assertions.assertThrows(
            Exception.class,
            () -> itemController.getItemDetails(TestUtil.ITEM_ID, TestUtil.ORG_ID, TestUtil.UOM));
    Assertions.assertEquals("Failed to fetch item details", exception.getMessage());
    verify(itemService, times(1)).getItemDetails(any(), any(), any());
  }

  @Test
  void updateItemTest() throws ItemDomainException, CommonServiceException {
    ItemBaseRequest itemBaseRequest = new ItemBaseRequest();
    when(itemService.updateItemDetails(any(), any(), any(), any(ItemBaseRequest.class)))
        .thenReturn(testUtil.getUpdatedItemResponse());

    ResponseEntity<BaseResponse<ItemResponse>> responseEntity =
        itemController.updateItemDetails(
            TestUtil.ITEM_ID, TestUtil.ORG_ID, TestUtil.UOM, itemBaseRequest);

    Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    Assertions.assertEquals(
        testUtil.getUpdatedItemResponse(), responseEntity.getBody().getPayload());

    verify(itemService, times(1)).updateItemDetails(any(), any(), any(), any());
  }

  @Test
  void updateItemExceptionTest() throws ItemDomainException, CommonServiceException {
    ItemBaseRequest itemBaseRequest = new ItemBaseRequest();
    when(itemService.updateItemDetails(any(), any(), any(), any(ItemBaseRequest.class)))
        .thenThrow(new RuntimeException("Unable to update the item details"));

    Exception exception =
        Assertions.assertThrows(
            Exception.class,
            () ->
                itemController.updateItemDetails(
                    TestUtil.ITEM_ID, TestUtil.ORG_ID, TestUtil.UOM, itemBaseRequest));
    Assertions.assertEquals("Unable to update the item details", exception.getMessage());

    verify(itemService, times(1)).updateItemDetails(any(), any(), any(), any());
  }

  @Test
  void deleteItemTest() throws CommonServiceException, ItemDomainException {
    when(itemService.deleteItem(any(), any(), any())).thenReturn(testUtil.getItemResponse());

    ResponseEntity<BaseResponse<ItemResponse>> responseEntity =
        itemController.deleteItem(TestUtil.ITEM_ID, TestUtil.ORG_ID, TestUtil.UOM);

    Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    Assertions.assertEquals(testUtil.getItemResponse(), responseEntity.getBody().getPayload());

    verify(itemService, times(1)).deleteItem(any(), any(), any());
  }

  @Test
  void deleteItemExceptionTest() throws ItemDomainException, CommonServiceException {
    when(itemService.deleteItem(any(), any(), any()))
        .thenThrow(new RuntimeException("Error while deleting item"));

    Exception exception =
        Assertions.assertThrows(
            Exception.class,
            () -> itemController.deleteItem(TestUtil.ITEM_ID, TestUtil.ORG_ID, TestUtil.UOM));
    Assertions.assertEquals("Error while deleting item", exception.getMessage());

    verify(itemService, times(1)).deleteItem(any(), any(), any());
  }

  @Test
  void getItemListByItemIdAndOrgIdAndUomTest()
      throws CommonServiceException, ItemBatchingDomainException {
    ItemResponse itemResponse = testUtil.getItemResponse();
    List<ItemResponse> itemResponseList = new ArrayList<>();
    List<String> itemList = new ArrayList<>();
    itemList.add(TestUtil.ITEM_ID);
    itemResponseList.add(itemResponse);
    when(itemService.getItemList(any(), any(), any(), any(), any())).thenReturn(itemResponseList);

    List<ItemResponse> responseEntity =
        itemController.getItemList(TestUtil.ORG_ID, itemList, true, new Date());

    Assertions.assertEquals(1, responseEntity.size());

    verify(itemService, times(1)).getItemList(any(), any(), any(), any(), any());
  }

  @Test
  void getItemListByItemIdAndOrgIdAndUomExceptionTest()
      throws CommonServiceException, ItemBatchingDomainException {
    when(itemService.getItemList(any(), any(), any(), any(), any()))
        .thenThrow(new RuntimeException("Failed to fetch list of item details"));
    List<String> itemList = new ArrayList<>();
    itemList.add(TestUtil.ITEM_ID);
    Exception exception =
        Assertions.assertThrows(
            Exception.class,
            () -> itemController.getItemList(TestUtil.ORG_ID, itemList, true, new Date()));
    Assertions.assertEquals("Failed to fetch list of item details", exception.getMessage());
    verify(itemService, times(1)).getItemList(any(), any(), any(), any(), any());
  }

  @Test
  @DisplayName("Happy path: Get paginated item list based on itemIds provided")
  void getPaginatedItemList() throws CommonServiceException, ItemBatchingDomainException {
    List<ItemListResponse> itemListResponses = testUtil.getItemList();

    when(itemService.getItemListByItemIdAndOrgId(any(), any(), any(), any(), any(), any()))
        .thenReturn(testUtil.getItemListPage(4, itemListResponses, itemListResponses.size()));
    ResponseEntity<BaseResponse<PagePayload<ItemListResponse>>> response =
        itemController.getItemsListPaginated(
            TestUtil.ORG_ID,
            "item-01,item-02",
            testUtil.getPageParams(
                Optional.of(2), Optional.of(1), Optional.of("itemId"), Optional.of("ASC")));

    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode(), "Success response");
    Assertions.assertEquals(
        4,
        (int) response.getBody().getPayload().getPagination().getTotalPages(),
        "Pagination Total pages");
    Assertions.assertEquals(
        itemListResponses.size(),
        (int) response.getBody().getPayload().getPagination().getTotalRecords(),
        "Total Elements");
    Assertions.assertEquals(
        2,
        (int) response.getBody().getPayload().getPagination().getCurrentPage(),
        "Current page number");
    Assertions.assertEquals(
        itemListResponses.size(),
        response.getBody().getPayload().getData().size(),
        "Paginated data");
    Assertions.assertEquals(
        Boolean.TRUE,
        Objects.nonNull(response.getBody().getPayload().getPagination().getPrevious()),
        "Previous Uri should not be null");

    verify(itemService, times(1))
        .getItemListByItemIdAndOrgId(any(), any(), any(), any(), any(), any());
  }

  @Test
  @DisplayName("Test: Add or Update Item when item does not exist")
  void addOrUpdateItemWhenItemDoesNotExist() throws ItemDomainException, CommonServiceException {
    when(itemService.upsertItem(any(ItemCreationRequest.class)))
        .thenReturn(testUtil.getItemResponse());

    ResponseEntity<BaseResponse<ItemResponse>> responseEntity =
        itemController.addOrUpdateItem(testUtil.getItemCreationRequest());

    Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    Assertions.assertEquals("Item saved successfully", responseEntity.getBody().getMessage());
    Assertions.assertEquals(testUtil.getItemResponse(), responseEntity.getBody().getPayload());
    verify(itemService, times(1)).upsertItem(any(ItemCreationRequest.class));
  }

  @Test
  @DisplayName("Test: Add or Update Item when item exists and is updated")
  void addOrUpdateItemWhenItemExistsAndIsUpdated()
      throws ItemDomainException, CommonServiceException {
    when(itemService.upsertItem(any(ItemCreationRequest.class)))
        .thenReturn(testUtil.getItemResponse());

    ResponseEntity<BaseResponse<ItemResponse>> responseEntity =
        itemController.addOrUpdateItem(testUtil.getItemCreationRequest());

    Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    Assertions.assertEquals("Item saved successfully", responseEntity.getBody().getMessage());
    Assertions.assertEquals(testUtil.getItemResponse(), responseEntity.getBody().getPayload());
    verify(itemService, times(1)).upsertItem(any(ItemCreationRequest.class));
  }

  @Test
  @DisplayName("Test: Get Item details with all the fields populated")
  void getItemDetailsList() throws CommonServiceException, ItemBatchingDomainException {
    List<ItemResponse> itemResponseList = List.of(testUtil.getItemResponse());
    when(itemService.getItemList(any(), any(), any(), any(), any())).thenReturn(itemResponseList);
    ItemDetailsWithSubstitutionRequest itemDetailsWithSubstitutionRequest =
        ItemDetailsWithSubstitutionRequest.builder()
            .itemList(List.of(TestUtil.ITEM_ID))
            .isItemBufferEnabled(true)
            .itemSubstitutionMap(Map.of())
            .promisingEngineDate(new Date())
            .orgId("NEXTUPLE_GR")
            .build();

    List<ItemResponse> responseEntity =
        itemController.getItemDetailsList(itemDetailsWithSubstitutionRequest);

    Assertions.assertEquals(1, responseEntity.size());
    Assertions.assertEquals(itemResponseList.getFirst(), responseEntity.getFirst());

    verify(itemService, times(1)).getItemList(any(), any(), any(), any(), any());
  }

  @Test
  @DisplayName("Test: Get Item details with buffer enabled flag as null the fields populated")
  void getItemDetailsListBufferDisabledTest()
      throws CommonServiceException, ItemBatchingDomainException {
    List<ItemResponse> itemResponseList = List.of(testUtil.getItemResponse());
    when(itemService.getItemList(any(), any(), any(), any(), any())).thenReturn(itemResponseList);
    ItemDetailsWithSubstitutionRequest itemDetailsWithSubstitutionRequest =
        ItemDetailsWithSubstitutionRequest.builder()
            .itemList(List.of(TestUtil.ITEM_ID))
            .itemSubstitutionMap(Map.of())
            .promisingEngineDate(new Date())
            .orgId("NEXTUPLE_GR")
            .build();

    List<ItemResponse> responseEntity =
        itemController.getItemDetailsList(itemDetailsWithSubstitutionRequest);

    Assertions.assertEquals(1, responseEntity.size());
    Assertions.assertEquals(itemResponseList.getFirst(), responseEntity.getFirst());

    verify(itemService, times(1)).getItemList(any(), any(), any(), any(), any());
  }
}
