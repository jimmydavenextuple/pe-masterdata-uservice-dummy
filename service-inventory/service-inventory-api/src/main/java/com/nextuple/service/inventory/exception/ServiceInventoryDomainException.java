package com.nextuple.service.inventory.exception;

import lombok.Data;

@Data
public class ServiceInventoryDomainException extends Exception {

  private final String orgId;
  private final String serviceOption;

  public ServiceInventoryDomainException(String message, String orgId, String serviceOption) {
    super(message);
    this.serviceOption = serviceOption;
    this.orgId = orgId;
  }
}
