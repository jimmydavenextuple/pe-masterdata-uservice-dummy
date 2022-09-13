package com.hbc.item.consumer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hbc.item.TestUtil;
import com.hbc.item.domain.inbound.ItemCreationRequest;
import com.hbc.item.exception.ItemDomainException;
import com.hbc.item.service.ItemService;
import javax.validation.ConstraintViolationException;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class ItemMasterConsumerTest {
  @InjectMocks private ItemMasterConsumer itemMasterConsumer;

  @InjectMocks private TestUtil testUtil;

  @Mock private ItemService itemService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void onItemMasterEventConsumptionTest() throws ItemDomainException {
    when(itemService.createItem(any())).thenReturn(testUtil.getItemResponse());
    ArgumentCaptor<ItemCreationRequest> captor = ArgumentCaptor.forClass(ItemCreationRequest.class);
    itemMasterConsumer.onItemMasterEventConsumption(testUtil.getItemMasterEvent(), null);

    verify(itemService, times(1)).createItem(captor.capture());
    Assertions.assertEquals(TestUtil.ITEM_ID, captor.getValue().getItemId());
    Assertions.assertEquals(2, captor.getValue().getServiceOptionEligibilities().size());
  }

  @Test
  void onItemMasterEventConsumptionExceptionTest1() throws ItemDomainException {
    when(itemService.createItem(any())).thenThrow(new ConstraintViolationException("error", null));
    ArgumentCaptor<ItemCreationRequest> captor = ArgumentCaptor.forClass(ItemCreationRequest.class);
    itemMasterConsumer.onItemMasterEventConsumption(testUtil.getItemMasterEvent(), null);

    verify(itemService, times(1)).createItem(captor.capture());
    Assertions.assertEquals(TestUtil.ITEM_ID, captor.getValue().getItemId());
  }

  @Test
  void onItemMasterEventConsumptionTest2() throws ItemDomainException {
    when(itemService.createItem(any())).thenThrow(new RuntimeException("error"));

    Assert.assertThrows(
        Exception.class,
        () -> itemMasterConsumer.onItemMasterEventConsumption(testUtil.getItemMasterEvent(), null));
  }

  @Test
  void onItemRecordConsumptionTest() throws ItemDomainException {
    when(itemService.createItem(any())).thenReturn(testUtil.getItemResponse());
    ArgumentCaptor<ItemCreationRequest> captor = ArgumentCaptor.forClass(ItemCreationRequest.class);
    itemMasterConsumer.onItemRecordConsumption(testUtil.getItemRecord(), null);

    verify(itemService, times(1)).createItem(captor.capture());
    Assertions.assertEquals(TestUtil.ITEM_ID, captor.getValue().getItemId());
  }

  @Test
  void onItemRecordConsumptionExceptionTest1() throws ItemDomainException {
    when(itemService.createItem(any())).thenThrow(new ConstraintViolationException("error", null));
    ArgumentCaptor<ItemCreationRequest> captor = ArgumentCaptor.forClass(ItemCreationRequest.class);
    itemMasterConsumer.onItemRecordConsumption(testUtil.getItemRecord(), null);

    verify(itemService, times(1)).createItem(captor.capture());
    Assertions.assertEquals(TestUtil.ITEM_ID, captor.getValue().getItemId());
  }

  @Test
  void onItemRecordConsumptionTest2() throws ItemDomainException {
    when(itemService.createItem(any())).thenThrow(new RuntimeException("error"));

    Assert.assertThrows(
        Exception.class,
        () -> itemMasterConsumer.onItemRecordConsumption(testUtil.getItemRecord(), null));
  }

  @Test
  void onItemRecordConsumptionTestItemEligibleStore() throws ItemDomainException {
    when(itemService.createItem(any())).thenReturn(testUtil.getItemResponse());
    ArgumentCaptor<ItemCreationRequest> captor = ArgumentCaptor.forClass(ItemCreationRequest.class);
    itemMasterConsumer.onItemRecordConsumption(testUtil.getItemRecordItemEligibleStore(), null);

    verify(itemService, times(1)).createItem(captor.capture());
    Assertions.assertEquals(TestUtil.ITEM_ID, captor.getValue().getItemId());
  }

  @Test
  void onItemRecordConsumptionTestItemEligibleDC() throws ItemDomainException {
    when(itemService.createItem(any())).thenReturn(testUtil.getItemResponse());
    ArgumentCaptor<ItemCreationRequest> captor = ArgumentCaptor.forClass(ItemCreationRequest.class);
    itemMasterConsumer.onItemRecordConsumption(testUtil.getItemRecordItemEligibleDC(), null);

    verify(itemService, times(1)).createItem(captor.capture());
    Assertions.assertEquals(TestUtil.ITEM_ID, captor.getValue().getItemId());
  }

  @Test
  void onItemRecordConsumptionTestItemEligibleNone() throws ItemDomainException {
    when(itemService.createItem(any())).thenReturn(testUtil.getItemResponse());
    ArgumentCaptor<ItemCreationRequest> captor = ArgumentCaptor.forClass(ItemCreationRequest.class);
    itemMasterConsumer.onItemRecordConsumption(testUtil.getItemRecordItemEligibleNone(), null);

    verify(itemService, times(1)).createItem(captor.capture());
    Assertions.assertEquals(TestUtil.ITEM_ID, captor.getValue().getItemId());
  }
}
