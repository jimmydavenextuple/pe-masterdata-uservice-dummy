package com.nextuple.node.persistence.exception;

import lombok.Data;

@Data
public class VendorDomainException extends Exception {
  private final String vendorId;
  private final String orgId;

  public VendorDomainException(String message, String vendorId, String orgId) {
    super(message);
    this.vendorId = vendorId;
    this.orgId = orgId;
  }
}
