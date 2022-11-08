package com.nextuple.item.exception;

import java.util.List;
import lombok.Data;

@Data
public class ItemBatchingDomainException extends Exception {

  private final List<String> itemList;
  private final String orgId;

  public ItemBatchingDomainException(String message, List<String> itemList, String orgId) {
    super(message);
    this.itemList = itemList;
    this.orgId = orgId;
  }
}
