/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.item.persistence.entity;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.nextuple.item.persistence.entity.key.ItemKey;
import org.junit.jupiter.api.Test;

class ItemEntityTest {

  private static final String ORG_ID = "org-1";
  private static String ITEM_ID = "item-1";
  private static String UOM = "uom-1";

  @Test
  void itemEntityTest() {
    ItemEntity itemEntity = new ItemEntity();
    assertNull(itemEntity.getItemId());
    assertNull(itemEntity.getItemSource());
    assertNull(itemEntity.getPickEligible());
    assertNull(itemEntity.getColor());
    assertNull(itemEntity.getCost());
    assertNull(itemEntity.getDepartmentName());
    assertNull(itemEntity.getDepartmentNumber());
    assertNull(itemEntity.getDimensionUom());
    assertNull(itemEntity.getHeight());
    assertNull(itemEntity.getImageUrl());
    assertNull(itemEntity.getIsDSVEligible());
    assertNull(itemEntity.getIsHazmat());
    assertNull(itemEntity.getIsWhiteGlove());
    assertNull(itemEntity.getLeadTime());
    assertNull(itemEntity.getLength());
    assertNull(itemEntity.getOrgId());
    assertNull(itemEntity.getParcelShipmentEligible());
    assertNull(itemEntity.getProcessingTime());
    assertNull(itemEntity.getProduct());
    assertNull(itemEntity.getShortDescription());
  }

  @Test
  void itemIdNullTest() {
    ItemKey itemId = new ItemKey();
    assertNull(itemId.getItemId());
    assertNull(itemId.getOrgId());
    assertNull(itemId.getUom());
  }

  @Test
  void itemIdTest() {
    ItemKey itemId = new ItemKey(ITEM_ID, ORG_ID, UOM);
    assertNotNull(itemId.getItemId());
    assertNotNull(itemId.getOrgId());
    assertNotNull(itemId.getUom());
  }
}
