package com.nextuple.pe.masterdata.service.service;

import com.nextuple.pe.masterdata.domain.ItemDomain;
import com.nextuple.pe.masterdata.domain.entity.ItemEntity;
import com.nextuple.pe.masterdata.domain.inbound.ItemCreationRequest;
import com.nextuple.pe.masterdata.domain.outbound.ItemResponse;
import com.nextuple.pe.masterdata.exception.ItemDomainException;
import com.nextuple.pe.masterdata.exception.common.CommonServiceException;
import com.nextuple.pe.masterdata.service.ItemService;
import com.nextuple.pe.masterdata.service.TestUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

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
    when(itemDomain.saveItemEntity(any(ItemEntity.class))).thenReturn(itemEntity);

    ItemResponse received_dto = itemService.createItem(itemCreationRequest);
    Assertions.assertEquals(itemCreationRequest.getItemId(), received_dto.getItemId());
    verify(itemDomain, times(1)).saveItemEntity(any(ItemEntity.class));
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
}
