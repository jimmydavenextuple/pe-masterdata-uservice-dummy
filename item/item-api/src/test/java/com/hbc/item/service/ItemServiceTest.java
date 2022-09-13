package com.hbc.item.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.hbc.common.exception.CommonServiceException;
import com.hbc.item.TestUtil;
import com.hbc.item.domain.ItemDomain;
import com.hbc.item.domain.entity.ItemEntity;
import com.hbc.item.domain.inbound.ItemCreationRequest;
import com.hbc.item.domain.outbound.ItemResponse;
import com.hbc.item.exception.ItemBatchingDomainException;
import com.hbc.item.exception.ItemDomainException;
import com.hbc.item.util.ItemUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.joda.time.DateTime;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class ItemServiceTest {

  @InjectMocks private ItemService itemService;

  @InjectMocks private TestUtil testUtil;

  @Mock private ItemDomain itemDomain;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void addItemTest() throws ItemDomainException {
    ItemEntity itemEntity = testUtil.getItemEntity();
    ItemCreationRequest itemCreationRequest = testUtil.getItemCreationRequest();
    itemCreationRequest.setLastModifiedDate(new DateTime());
    when(itemDomain.saveItemEntity(any(ItemEntity.class))).thenReturn(itemEntity);

    ItemResponse received_dto = itemService.createItem(itemCreationRequest);
    Assertions.assertEquals(itemCreationRequest.getItemId(), received_dto.getItemId());
    verify(itemDomain, times(1)).saveItemEntity(any(ItemEntity.class));
  }

  @Test
  void addItemExceptionTest() throws ItemDomainException {
    ItemEntity itemEntity = testUtil.getItemEntity();
    itemEntity.setServiceOptionEligibilities(TestUtil.getServiceOptEligiblityMapForExceptionTest());
    itemEntity.setInventoryNodeTypes(
        ItemUtils.getInventoryNodeTypeMap(TestUtil.getServiceOptEligiblityMapForExceptionTest()));
    ItemCreationRequest itemCreationRequest = testUtil.getItemCreationRequest();
    itemCreationRequest.setUom(null);
    when(itemDomain.saveItemEntity(any(ItemEntity.class))).thenReturn(itemEntity);

    Exception ex =
        Assertions.assertThrows(Exception.class, () -> itemService.createItem(itemCreationRequest));
    Assertions.assertEquals("Error occurred: uom - uom can't be blank", ex.getMessage());
    verify(itemDomain, times(0)).saveItemEntity(any(ItemEntity.class));
  }

  @Test
  void updateItemDetailsTest() throws ItemDomainException, CommonServiceException {
    ItemEntity itemEntity = testUtil.getItemEntity();
    ItemEntity updatedItemEntity = testUtil.getUpdatedItemEntity();
    when(itemDomain.findItemByItemIdAndOrgIdAndUom(any(), any(), any()))
        .thenReturn(Optional.of(itemEntity));
    when(itemDomain.saveItemEntity(any())).thenReturn(updatedItemEntity);

    ItemResponse itemResponse =
        itemService.updateItemDetails(
            TestUtil.ITEM_ID, TestUtil.ORG_ID, TestUtil.UOM, testUtil.getItemUpdationRequest());
    Assertions.assertEquals(testUtil.getUpdatedItemResponse(), itemResponse);

    verify(itemDomain, times(1)).saveItemEntity(any(ItemEntity.class));
    verify(itemDomain, times(1)).findItemByItemIdAndOrgIdAndUom(any(), any(), any());
  }

  @Test
  void updateItemDetailsTestCommonServiceException() throws ItemDomainException {
    when(itemDomain.findItemByItemIdAndOrgIdAndUom(any(), any(), any()))
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
    Assertions.assertEquals("Item not found with given details", exception.getMessage());

    verify(itemDomain, times(0)).saveItemEntity(any(ItemEntity.class));
    verify(itemDomain, times(1)).findItemByItemIdAndOrgIdAndUom(any(), any(), any());
  }

  @Test
  void getItemDetailsByItemIdAndOrgIdAndUomTest()
      throws ItemDomainException, CommonServiceException {
    ItemEntity itemEntity = testUtil.getItemEntity();
    when(itemDomain.findItemByItemIdAndOrgIdAndUom(any(), any(), any()))
        .thenReturn(Optional.of(itemEntity));

    ItemResponse itemResponse =
        itemService.getItemDetails(TestUtil.ITEM_ID, TestUtil.ORG_ID, TestUtil.UOM);
    Assertions.assertEquals(testUtil.getItemResponse(), itemResponse);
    verify(itemDomain, times(1)).findItemByItemIdAndOrgIdAndUom(any(), any(), any());
  }

  @Test
  void getItemDetailsByItemIdAndOrgIdAndUomTestException() throws ItemDomainException {
    when(itemDomain.findItemByItemIdAndOrgIdAndUom(any(), any(), any()))
        .thenReturn(Optional.empty());

    Exception exception =
        Assertions.assertThrows(
            CommonServiceException.class,
            () -> itemService.getItemDetails(TestUtil.ITEM_ID, TestUtil.ORG_ID, TestUtil.UOM));
    Assertions.assertEquals("Item not found with given details", exception.getMessage());

    verify(itemDomain, times(1)).findItemByItemIdAndOrgIdAndUom(any(), any(), any());
  }

  @Test
  void deleteItemTest() throws ItemDomainException, CommonServiceException {
    ItemEntity itemEntity = testUtil.getItemEntity();
    when(itemDomain.findItemByItemIdAndOrgIdAndUom(any(), any(), any()))
        .thenReturn(Optional.of(itemEntity));

    ItemResponse itemResponse =
        itemService.deleteItem(TestUtil.ITEM_ID, TestUtil.ORG_ID, TestUtil.UOM);
    Assertions.assertEquals(testUtil.getItemResponse(), itemResponse);
    verify(itemDomain, times(1)).findItemByItemIdAndOrgIdAndUom(any(), any(), any());
  }

  @Test
  void deleteItemTestException() throws ItemDomainException {
    when(itemDomain.findItemByItemIdAndOrgIdAndUom(any(), any(), any()))
        .thenReturn(Optional.empty());

    Exception exception =
        Assertions.assertThrows(
            CommonServiceException.class,
            () -> itemService.deleteItem(TestUtil.ITEM_ID, TestUtil.ORG_ID, TestUtil.UOM));
    Assertions.assertEquals("Item not found with given details", exception.getMessage());

    verify(itemDomain, times(1)).findItemByItemIdAndOrgIdAndUom(any(), any(), any());
  }

  @Test
  void getItemListByItemIdAndOrgIdAndUomTest()
      throws CommonServiceException, ItemBatchingDomainException {
    ItemEntity itemEntity = testUtil.getItemEntity();
    List<ItemEntity> itemEntityList = new ArrayList<>();
    itemEntityList.add(itemEntity);
    List<String> itemList = new ArrayList<>();
    itemList.add(TestUtil.ITEM_ID);
    List<ItemResponse> itemResponseList = new ArrayList<>();
    itemResponseList.add(testUtil.getItemResponse());
    when(itemDomain.findItemListByItemIdsAndOrgIdAndUom(any(), any())).thenReturn(itemEntityList);

    List<ItemResponse> itemResponse = itemService.getItemList(itemList, TestUtil.ORG_ID);
    Assertions.assertEquals(itemResponseList, itemResponse);
    verify(itemDomain, times(1)).findItemListByItemIdsAndOrgIdAndUom(any(), any());
  }

  @Test
  void getItemListByItemIdAndOrgIdAndUomTestException() throws ItemBatchingDomainException {
    List<ItemEntity> itemEntityList = new ArrayList<>();
    List<String> itemList = new ArrayList<>();
    itemList.add(TestUtil.ITEM_ID);
    when(itemDomain.findItemListByItemIdsAndOrgIdAndUom(any(), any())).thenReturn(itemEntityList);

    Exception exception =
        Assertions.assertThrows(
            CommonServiceException.class, () -> itemService.getItemList(itemList, TestUtil.ORG_ID));
    Assertions.assertEquals("Items not found with given details", exception.getMessage());

    verify(itemDomain, times(1)).findItemListByItemIdsAndOrgIdAndUom(any(), any());
  }
}
