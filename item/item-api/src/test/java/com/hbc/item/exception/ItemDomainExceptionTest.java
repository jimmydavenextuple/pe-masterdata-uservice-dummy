package com.hbc.item.exception;

import com.hbc.item.TestUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ItemDomainExceptionTest {

  @Test
  @DisplayName("Testing ItemDomainException")
  void constructTest() {
    ItemDomainException itemDomainException =
        new ItemDomainException("test", TestUtil.ITEM_ID, TestUtil.ORG_ID, TestUtil.UOM);
    Assertions.assertEquals("test", itemDomainException.getMessage());
    Assertions.assertEquals(TestUtil.ITEM_ID, itemDomainException.getItemId());
    Assertions.assertEquals(TestUtil.ORG_ID, itemDomainException.getOrgId());
    Assertions.assertEquals(TestUtil.UOM, itemDomainException.getUom());
  }
}
