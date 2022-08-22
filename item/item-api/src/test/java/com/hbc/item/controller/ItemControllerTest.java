package com.hbc.item.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.hbc.common.exception.CommonServiceException;
import com.hbc.common.response.BaseResponse;
import com.hbc.item.TestUtil;
import com.hbc.item.domain.inbound.ItemCreationRequest;
import com.hbc.item.domain.inbound.ItemUpdationRequest;
import com.hbc.item.domain.outbound.ItemResponse;
import com.hbc.item.exception.ItemDomainException;
import com.hbc.item.service.ItemService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

class ItemControllerTest {

  @InjectMocks private ItemController itemController;

  @InjectMocks private TestUtil testUtil;

  @Mock private ItemService itemService;

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
    ItemUpdationRequest itemUpdationRequest = new ItemUpdationRequest();
    when(itemService.updateItemDetails(any(), any(), any(), any(ItemUpdationRequest.class)))
        .thenReturn(testUtil.getUpdatedItemResponse());

    ResponseEntity<BaseResponse<ItemResponse>> responseEntity =
        itemController.updateItemDetails(
            TestUtil.ITEM_ID, TestUtil.ORG_ID, TestUtil.UOM, itemUpdationRequest);

    Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    Assertions.assertEquals(
        testUtil.getUpdatedItemResponse(), responseEntity.getBody().getPayload());

    verify(itemService, times(1)).updateItemDetails(any(), any(), any(), any());
  }

  @Test
  void updateItemExceptionTest() throws ItemDomainException, CommonServiceException {
    ItemUpdationRequest itemUpdationRequest = new ItemUpdationRequest();
    when(itemService.updateItemDetails(any(), any(), any(), any(ItemUpdationRequest.class)))
        .thenThrow(new RuntimeException("Unable to update the item details"));

    Exception exception =
        Assertions.assertThrows(
            Exception.class,
            () ->
                itemController.updateItemDetails(
                    TestUtil.ITEM_ID, TestUtil.ORG_ID, TestUtil.UOM, itemUpdationRequest));
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
  void getItemDetailsListByItemIdAndOrgIdAndUomTest()
          throws ItemDomainException, CommonServiceException {
    ItemResponse itemResponse = testUtil.getItemResponse();
    List<ItemResponse> itemResponseList = new ArrayList<>();
    List<String> itemList = new ArrayList<>();
    itemList.add(TestUtil.ITEM_ID);
    itemResponseList.add(itemResponse);
    when(itemService.getListOfItemDetails(any(), any(), any())).thenReturn(itemResponseList);

    List<ItemResponse> responseEntity =
            itemController.getItemDetailsList(TestUtil.ORG_ID, TestUtil.UOM, itemList);

    Assertions.assertEquals(1, responseEntity.size());

    verify(itemService, times(1)).getListOfItemDetails(any(), any(), any());
  }


  @Test
  void getItemDetailsListByItemIdAndOrgIdAndUomExceptionTest()
          throws ItemDomainException, CommonServiceException {
    when(itemService.getListOfItemDetails(any(), any(), any()))
            .thenThrow(new RuntimeException("Failed to fetch list of item details"));
    List<String> itemList = new ArrayList<>();
    itemList.add(TestUtil.ITEM_ID);
    Exception exception =
            Assertions.assertThrows(
                    Exception.class,
                    () -> itemController.getItemDetailsList(TestUtil.ORG_ID, TestUtil.UOM, itemList));
    Assertions.assertEquals("Failed to fetch list of item details", exception.getMessage());
    verify(itemService, times(1)).getListOfItemDetails(any(), any(), any());
  }

}
