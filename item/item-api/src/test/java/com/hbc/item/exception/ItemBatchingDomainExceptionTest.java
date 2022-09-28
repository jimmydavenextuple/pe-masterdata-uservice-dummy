package com.hbc.item.exception;

import com.hbc.item.TestUtil;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ItemBatchingDomainExceptionTest {
  @Test
  @DisplayName("Testing ItemBatchingDomainException")
  void constructTest() {
    List<String> itemList = new ArrayList<>();
    itemList.add(TestUtil.ITEM_ID);
    ItemBatchingDomainException itemDomainException =
        new ItemBatchingDomainException("test", itemList, TestUtil.ORG_ID);
    Assertions.assertEquals("test", itemDomainException.getMessage());
    Assertions.assertEquals(TestUtil.ITEM_ID, itemDomainException.getItemList().get(0));
    Assertions.assertEquals(TestUtil.ORG_ID, itemDomainException.getOrgId());
  }
}
