package com.nextuple.pe.masterdata.exception;

import lombok.Data;

@Data
public class CarrierServiceDomainException extends Exception {

  private final String carrierId;
  private final String serviceId;
  private final String orgId;

  public CarrierServiceDomainException(
      String message, String carrierId, String serviceId, String orgId) {
    super(message);
    this.carrierId = carrierId;
    this.serviceId = serviceId;
    this.orgId = orgId;
  }
}
