package com.hbc.item.domain.entity;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.hbc.item.TestUtil;
import org.junit.jupiter.api.Test;

class ItemEntityTest {

  @Test
  void itemEntityTest() {
    ItemEntity itemEntity = new ItemEntity();
    assertNull(itemEntity.getItemId());
    assertNull(itemEntity.getItemSource());
    assertNull(itemEntity.getBopisEligible());
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
    ItemId itemId = new ItemId();
    assertNull(itemId.getItemId());
    assertNull(itemId.getOrgId());
    assertNull(itemId.getUom());
  }

  @Test
  void itemIdTest() {
    ItemId itemId = new ItemId(TestUtil.ITEM_ID, TestUtil.ORG_ID, TestUtil.UOM);
    assertNotNull(itemId.getItemId());
    assertNotNull(itemId.getOrgId());
    assertNotNull(itemId.getUom());
  }
}
