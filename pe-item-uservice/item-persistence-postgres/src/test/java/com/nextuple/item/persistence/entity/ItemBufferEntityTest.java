/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.item.persistence.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.nextuple.item.persistence.TestUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ItemBufferEntityTest {

  @Test
  @DisplayName("Default Constructor Test - All Fields Null")
  void itemBufferEntityDefaultConstructorTest() {
    ItemBufferEntity itemBufferEntity = new ItemBufferEntity();
    assertNull(itemBufferEntity.getId());
    assertNull(itemBufferEntity.getOrgId());
    assertNull(itemBufferEntity.getItemId());
    assertNull(itemBufferEntity.getUom());
    assertNull(itemBufferEntity.getBufferHours());
    assertNull(itemBufferEntity.getBufferStartDate());
    assertNull(itemBufferEntity.getBufferEndDate());
    assertNull(itemBufferEntity.getCustomAttributes());
  }

  @Test
  @DisplayName("Set and Get Id Test - Not Null")
  void idNotNullTest() {
    ItemBufferEntity itemBufferEntity = new ItemBufferEntity();
    itemBufferEntity.setId(2L);
    assertNotNull(itemBufferEntity.getId());
  }

  @Test
  @DisplayName("Setters and Getters Test - OrgId, ItemId, Uom")
  void settersAndGettersTest() {
    ItemBufferEntity itemBufferEntity = new ItemBufferEntity();
    itemBufferEntity.setOrgId(TestUtil.ORG_ID);
    itemBufferEntity.setItemId(TestUtil.ITEM_ID);
    itemBufferEntity.setUom(TestUtil.UOM);

    assertEquals(TestUtil.ORG_ID, itemBufferEntity.getOrgId());
    assertEquals(TestUtil.ITEM_ID, itemBufferEntity.getItemId());
    assertEquals(TestUtil.UOM, itemBufferEntity.getUom());
  }
}
