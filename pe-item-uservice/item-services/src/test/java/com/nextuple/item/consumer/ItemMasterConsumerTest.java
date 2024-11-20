/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.item.consumer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nextuple.item.TestUtil;
import com.nextuple.item.domain.inbound.ItemCreationRequest;
import com.nextuple.item.persistence.exception.ItemDomainException;
import com.nextuple.item.service.ItemService;
import jakarta.validation.ConstraintViolationException;
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
    Assertions.assertEquals(3, captor.getValue().getServiceOptionEligibilities().size());
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

    Assertions.assertThrows(
        Exception.class,
        () -> itemMasterConsumer.onItemMasterEventConsumption(testUtil.getItemMasterEvent(), null));
  }
}
