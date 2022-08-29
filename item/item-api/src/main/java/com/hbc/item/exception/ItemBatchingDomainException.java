package com.hbc.item.exception;

import java.util.List;
import lombok.Data;

@Data
public class ItemBatchingDomainException extends Exception {

  private final List<String> itemList;
  private final String orgId;
  private final String uom;

  public ItemBatchingDomainException(
      String message, List<String> itemList, String orgId, String uom) {
    super(message);
    this.itemList = itemList;
    this.orgId = orgId;
    this.uom = uom;
  }
}
