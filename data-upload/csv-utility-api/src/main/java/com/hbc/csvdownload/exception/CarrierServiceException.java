package com.hbc.csvdownload.exception;

import lombok.Data;

@Data
public class CarrierServiceException extends Exception {
  private final String orgId;

  public CarrierServiceException(String message, String orgId) {
    super(message);
    this.orgId = orgId;
  }
}
