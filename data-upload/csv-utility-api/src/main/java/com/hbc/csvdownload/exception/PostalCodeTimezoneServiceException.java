package com.hbc.csvdownload.exception;

import lombok.Data;

@Data
public class PostalCodeTimezoneServiceException extends Exception {

  private final String orgId;
  private final String state;

  public PostalCodeTimezoneServiceException(String message, String orgId, String state) {
    super(message);
    this.orgId = orgId;
    this.state = state;
  }

  public PostalCodeTimezoneServiceException(
      String message, Throwable cause, String orgId, String state) {
    super(message, cause);
    this.orgId = orgId;
    this.state = state;
  }
}
