package com.nextuple.item.domain.entity;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.nextuple.item.TestUtil;
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
    ItemPK itemId = new ItemPK();
    assertNull(itemId.getItemId());
    assertNull(itemId.getOrgId());
    assertNull(itemId.getUom());
  }

  @Test
  void itemIdTest() {
    ItemPK itemId = new ItemPK(TestUtil.ITEM_ID, TestUtil.ORG_ID, TestUtil.UOM);
    assertNotNull(itemId.getItemId());
    assertNotNull(itemId.getOrgId());
    assertNotNull(itemId.getUom());
  }
}
