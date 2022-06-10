package com.nextuple.pe.masterdata.exception;

import lombok.Data;

@Data
public class ItemDomainException extends Exception {

  private final String itemId;
  private final String orgId;
  private final String uom;

  public ItemDomainException(String message, String itemId, String orgId, String uom) {
    super(message);
    this.itemId = itemId;
    this.orgId = orgId;
    this.uom = uom;
  }
}
