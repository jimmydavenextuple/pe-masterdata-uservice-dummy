/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */
package com.nextuple.item.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.validation.ValidatorUtil;
import com.nextuple.item.TestUtil;
import com.nextuple.item.domain.inbound.ItemBaseRequest;
import com.nextuple.item.domain.inbound.ItemCreationRequest;
import com.nextuple.item.domain.outbound.ActiveItemBufferResponse;
import com.nextuple.item.domain.outbound.ItemListResponse;
import com.nextuple.item.domain.outbound.ItemResponse;
import com.nextuple.item.persistence.domain.ItemBufferDomainDto;
import com.nextuple.item.persistence.domain.ItemDomainDto;
import com.nextuple.item.persistence.exception.ItemBatchingDomainException;
import com.nextuple.item.persistence.exception.ItemDomainException;
import com.nextuple.item.persistence.service.impl.ItemBufferPersistenceServiceImpl;
import com.nextuple.item.persistence.service.impl.ItemPersistenceServiceImpl;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.joda.time.DateTime;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

class ItemServiceTest {

  @InjectMocks private ItemService itemService;

  @InjectMocks private TestUtil testUtil;

  @Mock private ItemPersistenceServiceImpl itemPersistenceService;
  @Mock private ItemBufferPersistenceServiceImpl itemBufferPersistenceService;

  @Mock ValidatorUtil validatorUtil;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void addItemTest() throws ItemDomainException {
    ItemDomainDto itemEntity = testUtil.getItemDomainDto();
    ItemCreationRequest itemCreationRequest = testUtil.getItemCreationRequest();
    itemCreationRequest.setLastModifiedDate(new DateTime());
    when(itemPersistenceService.saveItem(any(ItemDomainDto.class))).thenReturn(itemEntity);

    ItemResponse received_dto = itemService.createItem(itemCreationRequest);
    assertEquals(itemCreationRequest.getItemId(), received_dto.getItemId());
    verify(itemPersistenceService, times(1)).saveItem(any(ItemDomainDto.class));
  }

  @Test
  void addItemExceptionTest() throws ItemDomainException {
    ItemDomainDto itemEntity = testUtil.getItemDomainDto();

    itemEntity.setServiceOptionEligibilities(TestUtil.getServiceOptEligiblityMapForExceptionTest());
    itemEntity.setInventoryNodeTypes(TestUtil.getInventoryNodeTypeMap());
    ItemCreationRequest itemCreationRequest = testUtil.getItemCreationRequest();
    itemCreationRequest.setUom(null);
    when(itemPersistenceService.saveItem(any(ItemDomainDto.class))).thenReturn(itemEntity);

    Exception ex =
        Assertions.assertThrows(Exception.class, () -> itemService.createItem(itemCreationRequest));
    assertEquals("Error occurred: uom - uom can't be blank", ex.getMessage());
    verify(itemPersistenceService, times(0)).saveItem(any(ItemDomainDto.class));
  }

  @Test
  void updateItemDetailsTest() throws ItemDomainException, CommonServiceException {

    ItemDomainDto itemEntity = testUtil.getItemDomainDto();
    ItemDomainDto updatedItemEntity = testUtil.getUpdatedItemDomainDto();
    when(itemPersistenceService.findItemByItemIdAndOrgIdAndUom(any(), any(), any()))
        .thenReturn(Optional.of(itemEntity));
    when(itemPersistenceService.saveItem(any())).thenReturn(updatedItemEntity);

    ItemResponse itemResponse =
        itemService.updateItemDetails(
            TestUtil.ITEM_ID, TestUtil.ORG_ID, TestUtil.UOM, testUtil.getItemUpdationRequest());
    assertEquals(testUtil.getUpdatedItemResponse(), itemResponse);

    verify(itemPersistenceService, times(1)).saveItem(any(ItemDomainDto.class));
    verify(itemPersistenceService, times(1)).findItemByItemIdAndOrgIdAndUom(any(), any(), any());
  }

  @Test
  @DisplayName("When item processing time is not passed in the update request")
  void updateItemDetailsTestWhenProcessingTimeIsNotPassed()
      throws ItemDomainException, CommonServiceException {

    ItemDomainDto itemEntity = testUtil.getItemDomainDto();
    ItemDomainDto updatedItemEntity = testUtil.getUpdatedItemDomainDto();
    when(itemPersistenceService.findItemByItemIdAndOrgIdAndUom(any(), any(), any()))
        .thenReturn(Optional.of(itemEntity));
    when(itemPersistenceService.saveItem(any())).thenReturn(updatedItemEntity);

    ItemResponse itemResponse =
        itemService.updateItemDetails(
            TestUtil.ITEM_ID, TestUtil.ORG_ID, TestUtil.UOM, testUtil.getItemUpdationRequest());
    assertEquals(testUtil.getUpdatedItemResponse(), itemResponse);
    assertEquals(TestUtil.PROCESSING_TIME, itemResponse.getProcessingTime());

    verify(itemPersistenceService, times(1)).saveItem(any(ItemDomainDto.class));
    verify(itemPersistenceService, times(1)).findItemByItemIdAndOrgIdAndUom(any(), any(), any());
  }

  @Test
  @DisplayName("When item processing time is passed as null in the update request")
  void updateItemDetailsTestWhenProcessingTimeIsPassedAsNull()
      throws ItemDomainException, CommonServiceException {
    ItemBaseRequest itemBaseRequest = testUtil.getItemUpdationRequest();
    itemBaseRequest.setProcessingTime(Optional.empty());

    ItemDomainDto itemEntity = testUtil.getItemDomainDto();
    ItemDomainDto updatedItemEntity = testUtil.getUpdatedItemDomainDto();
    updatedItemEntity.setProcessingTime(null);

    ItemResponse updatedItemResponse = testUtil.getUpdatedItemResponse();
    updatedItemResponse.setProcessingTime(null);

    when(itemPersistenceService.findItemByItemIdAndOrgIdAndUom(any(), any(), any()))
        .thenReturn(Optional.of(itemEntity));
    when(itemPersistenceService.saveItem(any())).thenReturn(updatedItemEntity);

    ItemResponse itemResponse =
        itemService.updateItemDetails(
            TestUtil.ITEM_ID, TestUtil.ORG_ID, TestUtil.UOM, itemBaseRequest);
    assertEquals(updatedItemResponse, itemResponse);
    Assertions.assertNull(itemResponse.getProcessingTime());

    verify(itemPersistenceService, times(1)).saveItem(any(ItemDomainDto.class));
    verify(itemPersistenceService, times(1)).findItemByItemIdAndOrgIdAndUom(any(), any(), any());
  }

  @Test
  @DisplayName("When item processing time is passed as non null in the update request")
  void updateItemDetailsTestWhenProcessingTimeIsPassedAsNonNull()
      throws ItemDomainException, CommonServiceException {
    ItemBaseRequest itemBaseRequest = testUtil.getItemUpdationRequest();
    itemBaseRequest.setProcessingTime(Optional.of(10.0));

    ItemDomainDto itemEntity = testUtil.getItemDomainDto();
    ItemDomainDto updatedItemEntity = testUtil.getUpdatedItemDomainDto();
    updatedItemEntity.setProcessingTime(10.0);

    ItemResponse updatedItemResponse = testUtil.getUpdatedItemResponse();
    updatedItemResponse.setProcessingTime(10.0);

    when(itemPersistenceService.findItemByItemIdAndOrgIdAndUom(any(), any(), any()))
        .thenReturn(Optional.of(itemEntity));
    when(itemPersistenceService.saveItem(any())).thenReturn(updatedItemEntity);

    ItemResponse itemResponse =
        itemService.updateItemDetails(
            TestUtil.ITEM_ID, TestUtil.ORG_ID, TestUtil.UOM, itemBaseRequest);
    assertEquals(updatedItemResponse, itemResponse);
    assertEquals(10.0, itemResponse.getProcessingTime());

    verify(itemPersistenceService, times(1)).saveItem(any(ItemDomainDto.class));
    verify(itemPersistenceService, times(1)).findItemByItemIdAndOrgIdAndUom(any(), any(), any());
  }

  @Test
  void updateItemDetailsTestCommonServiceException() throws ItemDomainException {
    when(itemPersistenceService.findItemByItemIdAndOrgIdAndUom(any(), any(), any()))
        .thenReturn(Optional.empty());

    Exception exception =
        Assertions.assertThrows(
            CommonServiceException.class,
            () ->
                itemService.updateItemDetails(
                    TestUtil.ITEM_ID,
                    TestUtil.ORG_ID,
                    TestUtil.UOM,
                    testUtil.getItemUpdationRequest()));
    assertEquals("Item not found with given details", exception.getMessage());

    verify(itemPersistenceService, times(0)).saveItem(any(ItemDomainDto.class));
    verify(itemPersistenceService, times(1)).findItemByItemIdAndOrgIdAndUom(any(), any(), any());
  }

  @Test
  void getItemDetailsByItemIdAndOrgIdAndUomTest()
      throws ItemDomainException, CommonServiceException {

    ItemDomainDto itemEntity = testUtil.getItemDomainDto();
    when(itemPersistenceService.findItemByItemIdAndOrgIdAndUom(any(), any(), any()))
        .thenReturn(Optional.of(itemEntity));

    ItemResponse itemResponse =
        itemService.getItemDetails(TestUtil.ITEM_ID, TestUtil.ORG_ID, TestUtil.UOM);
    assertEquals(testUtil.getItemResponse(), itemResponse);
    verify(itemPersistenceService, times(1)).findItemByItemIdAndOrgIdAndUom(any(), any(), any());
  }

  @Test
  void getItemDetailsByItemIdAndOrgIdAndUomTestException() throws ItemDomainException {
    when(itemPersistenceService.findItemByItemIdAndOrgIdAndUom(any(), any(), any()))
        .thenReturn(Optional.empty());

    Exception exception =
        Assertions.assertThrows(
            CommonServiceException.class,
            () -> itemService.getItemDetails(TestUtil.ITEM_ID, TestUtil.ORG_ID, TestUtil.UOM));
    assertEquals("Item not found with given details", exception.getMessage());

    verify(itemPersistenceService, times(1)).findItemByItemIdAndOrgIdAndUom(any(), any(), any());
  }

  @Test
  void deleteItemTest() throws ItemDomainException, CommonServiceException {

    ItemDomainDto itemEntity = testUtil.getItemDomainDto();
    when(itemPersistenceService.findItemByItemIdAndOrgIdAndUom(any(), any(), any()))
        .thenReturn(Optional.of(itemEntity));

    ItemResponse itemResponse =
        itemService.deleteItem(TestUtil.ITEM_ID, TestUtil.ORG_ID, TestUtil.UOM);
    assertEquals(testUtil.getItemResponse(), itemResponse);
    verify(itemPersistenceService, times(1)).findItemByItemIdAndOrgIdAndUom(any(), any(), any());
  }

  @Test
  void deleteItemTestException() throws ItemDomainException {
    when(itemPersistenceService.findItemByItemIdAndOrgIdAndUom(any(), any(), any()))
        .thenReturn(Optional.empty());

    Exception exception =
        Assertions.assertThrows(
            CommonServiceException.class,
            () -> itemService.deleteItem(TestUtil.ITEM_ID, TestUtil.ORG_ID, TestUtil.UOM));
    assertEquals("Item not found with given details", exception.getMessage());

    verify(itemPersistenceService, times(1)).findItemByItemIdAndOrgIdAndUom(any(), any(), any());
  }

  @Test
  @DisplayName("Get Item List - Happy path")
  void getItemListByItemIdAndOrgIdAndUomTest()
      throws CommonServiceException, ItemBatchingDomainException {

    ItemDomainDto itemEntity = testUtil.getItemDomainDto();
    List<ItemDomainDto> itemEntityList = new ArrayList<>();
    itemEntityList.add(itemEntity);
    List<String> itemList = new ArrayList<>();
    itemList.add(TestUtil.ITEM_ID);
    List<ItemResponse> itemResponseList = new ArrayList<>();
    itemResponseList.add(testUtil.getItemResponse());
    when(itemPersistenceService.findItemListByItemIdsAndOrgId(any(), any()))
        .thenReturn(itemEntityList);

    List<ItemResponse> itemResponse =
        itemService.getItemList(itemList, TestUtil.ORG_ID, false, new Date());
    assertEquals(itemResponseList, itemResponse);
    verify(itemPersistenceService, times(1)).findItemListByItemIdsAndOrgId(any(), any());
  }

  @Test
  @DisplayName("Get Item List - Exception")
  void getItemListByItemIdAndOrgIdAndUomTestException() throws ItemBatchingDomainException {
    List<ItemDomainDto> itemEntityList = new ArrayList<>();
    List<String> itemList = new ArrayList<>();
    itemList.add(TestUtil.ITEM_ID);
    when(itemPersistenceService.findItemListByItemIdsAndOrgId(any(), any()))
        .thenReturn(itemEntityList);

    Exception exception =
        Assertions.assertThrows(
            CommonServiceException.class,
            () -> itemService.getItemList(itemList, TestUtil.ORG_ID, false, new Date()));
    assertEquals("Items not found with given details", exception.getMessage());

    verify(itemPersistenceService, times(1)).findItemListByItemIdsAndOrgId(any(), any());
  }

  @Test
  @DisplayName("Get Item List - With isItemBufferEnabled flag enabled")
  void getItemListWithItemBufferEnabledTest()
      throws CommonServiceException, ItemBatchingDomainException {
    List<ItemDomainDto> itemDomainDtoList = new ArrayList<>();
    ItemDomainDto itemDomainDto = new ItemDomainDto();
    itemDomainDto.setItemId("ITEM-001");
    itemDomainDto.setUom("EA");
    itemDomainDtoList.add(itemDomainDto);

    List<String> itemList = List.of("ITEM-001");
    String orgId = "org1";
    Boolean isItemBufferEnabled = true;
    Date promisingEngineDate = new Date();

    when(itemPersistenceService.findItemListByItemIdsAndOrgId(itemList, orgId))
        .thenReturn(itemDomainDtoList);

    List<ItemBufferDomainDto> itemBufferDomainDtoList = new ArrayList<>();
    ItemBufferDomainDto itemBufferDomainDto = new ItemBufferDomainDto();
    itemBufferDomainDto.setItemId("ITEM-001");
    itemBufferDomainDto.setUom("EA");
    itemBufferDomainDto.setBufferHours(2.0);
    itemBufferDomainDto.setBufferStartDate(new Date());
    itemBufferDomainDto.setBufferEndDate(new Date(System.currentTimeMillis() + 3600000));
    itemBufferDomainDtoList.add(itemBufferDomainDto);

    when(itemBufferPersistenceService.findItemBuffersListByItemIdsAndOrgIdAndPromisingEngineDate(
            itemList, orgId, promisingEngineDate))
        .thenReturn(itemBufferDomainDtoList);

    List<ItemResponse> itemResponseList =
        itemService.getItemList(itemList, orgId, isItemBufferEnabled, promisingEngineDate);

    assertEquals(1, itemResponseList.size());
    ItemResponse itemResponse = itemResponseList.get(0);
    assertEquals(itemDomainDto.getItemId(), itemResponse.getItemId());
    assertEquals(itemDomainDto.getUom(), itemResponse.getUom());

    ActiveItemBufferResponse activeItemBufferResponse = itemResponse.getActiveItemBuffer();
    assertNotNull(activeItemBufferResponse);
    assertEquals(itemBufferDomainDto.getBufferHours(), activeItemBufferResponse.getBufferHours());
    assertEquals(
        itemBufferDomainDto.getBufferStartDate(), activeItemBufferResponse.getBufferStartDate());
    assertEquals(
        itemBufferDomainDto.getBufferEndDate(), activeItemBufferResponse.getBufferEndDate());

    verify(itemPersistenceService, times(1)).findItemListByItemIdsAndOrgId(itemList, orgId);
    verify(itemBufferPersistenceService, times(1))
        .findItemBuffersListByItemIdsAndOrgIdAndPromisingEngineDate(
            itemList, orgId, promisingEngineDate);
  }

  @Test
  @DisplayName("Get item list with default sorting order")
  void getItemListByItemIdAndOrgIdDefaultSortingOrder()
      throws CommonServiceException, ItemBatchingDomainException {

    List<ItemDomainDto> nodeDomainDtoList = testUtil.getItemDomainDtoList();
    Pageable pageable = PageRequest.of(1, 15, Sort.by(TestUtil.SORT_BY).ascending());
    Page<ItemDomainDto> itemDomainDtoPage =
        new PageImpl<>(nodeDomainDtoList, pageable, nodeDomainDtoList.size());

    when(itemPersistenceService.getItemByItemIdListAndOrgId(any(), any(), any()))
        .thenReturn(itemDomainDtoPage);
    String itemIds = TestUtil.ITEM_ID_1 + TestUtil.ITEM_ID_2;
    Page<ItemListResponse> itemListResponses =
        itemService.getItemListByItemIdAndOrgId(
            itemIds, TestUtil.ORG_ID, 1, 15, TestUtil.SORT_BY, "ASC");
    assertEquals(2, itemListResponses.getContent().size());
    assertEquals(TestUtil.ITEM_ID_1, itemListResponses.getContent().getFirst().getItemId());
    verify(itemPersistenceService, Mockito.times(1))
        .getItemByItemIdListAndOrgId(any(), any(), any());
  }

  @Test
  @DisplayName("Get item list with DESC sorting order")
  void getItemListByItemIdAndOrgIdDescSortingOrder()
      throws CommonServiceException, ItemBatchingDomainException {

    List<ItemDomainDto> nodeDomainDtoList = testUtil.getItemDomainDtoList().reversed();
    Pageable pageable = PageRequest.of(1, 15, Sort.by(TestUtil.SORT_BY).descending());
    Page<ItemDomainDto> itemDomainDtoPage =
        new PageImpl<>(nodeDomainDtoList, pageable, nodeDomainDtoList.size());

    when(itemPersistenceService.getItemByItemIdListAndOrgId(any(), any(), any()))
        .thenReturn(itemDomainDtoPage);
    String itemIds = TestUtil.ITEM_ID_1 + TestUtil.ITEM_ID_2;
    Page<ItemListResponse> itemListResponses =
        itemService.getItemListByItemIdAndOrgId(itemIds, TestUtil.ORG_ID, 1, 15, "itemId", "DESC");
    assertEquals(2, itemListResponses.getContent().size());
    assertEquals(TestUtil.ITEM_ID_2, itemListResponses.getContent().getFirst().getItemId());
    verify(itemPersistenceService, Mockito.times(1))
        .getItemByItemIdListAndOrgId(any(), any(), any());
  }

  @Test
  void getNodeListByOrgIdExceptionTest() {
    Exception exception =
        Assertions.assertThrows(
            CommonServiceException.class,
            () ->
                itemService.getItemListByItemIdAndOrgId(
                    "item-01,item-02", TestUtil.ORG_ID, 1, 1, "itemId", "invalid sort order"));

    assertEquals("Invalid sort order, consider giving either ASC or DESC", exception.getMessage());
    verifyNoInteractions(itemPersistenceService);
  }

  @Test
  @DisplayName("Test upsertItem - Item Exists (Update)")
  void upsertItemTest_ItemExists() throws ItemDomainException, CommonServiceException {
    ItemCreationRequest itemCreationRequest = testUtil.getItemCreationRequest();
    ItemDomainDto existingItemDto = testUtil.getItemDomainDto();

    when(itemPersistenceService.findItemByItemIdAndOrgIdAndUom(
            itemCreationRequest.getItemId(),
            itemCreationRequest.getOrgId(),
            itemCreationRequest.getUom()))
        .thenReturn(Optional.of(existingItemDto));

    ItemResponse updatedItemResponse = testUtil.getUpdatedItemResponse();
    when(itemPersistenceService.saveItem(any(ItemDomainDto.class))).thenReturn(existingItemDto);

    ItemResponse result = itemService.upsertItem(itemCreationRequest);

    assertEquals(updatedItemResponse.getItemId(), result.getItemId());
    assertEquals(updatedItemResponse.getUom(), result.getUom());

    verify(itemPersistenceService, times(2)).findItemByItemIdAndOrgIdAndUom(any(), any(), any());
    verify(itemPersistenceService, times(1)).saveItem(any(ItemDomainDto.class));
  }

  @Test
  @DisplayName("Test upsertItem - Item Does Not Exist (Create)")
  void upsertItemTest_ItemDoesNotExist() throws ItemDomainException, CommonServiceException {
    ItemCreationRequest itemCreationRequest = testUtil.getItemCreationRequest();

    when(itemPersistenceService.findItemByItemIdAndOrgIdAndUom(
            itemCreationRequest.getItemId(),
            itemCreationRequest.getOrgId(),
            itemCreationRequest.getUom()))
        .thenReturn(Optional.empty());

    ItemResponse createdItemResponse = testUtil.getItemResponse();
    when(itemPersistenceService.saveItem(any(ItemDomainDto.class)))
        .thenReturn(testUtil.getItemDomainDto());

    ItemResponse result = itemService.upsertItem(itemCreationRequest);

    assertEquals(createdItemResponse.getItemId(), result.getItemId());
    assertEquals(createdItemResponse.getUom(), result.getUom());
    verify(itemPersistenceService, times(1)).findItemByItemIdAndOrgIdAndUom(any(), any(), any());
    verify(itemPersistenceService, times(1)).saveItem(any(ItemDomainDto.class));
  }
}
