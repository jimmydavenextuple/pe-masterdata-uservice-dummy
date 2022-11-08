package com.nextuple.carrier.exception;

import lombok.Data;

@Data
public class CarrierServiceDomainException extends Exception {

  private final String carrierId;
  private final String carrierServiceId;
  private final String orgId;

  public CarrierServiceDomainException(
      String message, String carrierId, String carrierServiceId, String orgId) {
    super(message);
    this.carrierId = carrierId;
    this.carrierServiceId = carrierServiceId;
    this.orgId = orgId;
  }
}
