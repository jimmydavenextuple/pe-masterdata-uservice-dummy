package com.hbc.csvdownload.exception;

import lombok.Data;

@Data
public class TransitServiceException extends Exception {

  private final String orgId;
  private final String carrierServiceId;

  public TransitServiceException(String message, String orgId, String carrierServiceId) {
    super(message);
    this.orgId = orgId;
    this.carrierServiceId = carrierServiceId;
  }

  public TransitServiceException(
      String message, Throwable cause, String orgId, String carrierServiceId) {
    super(message, cause);
    this.orgId = orgId;
    this.carrierServiceId = carrierServiceId;
  }
}
