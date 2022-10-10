package com.hbc.csvdownload.exception;

import lombok.Data;

@Data
public class PostalCodeTimezoneServiceException extends Exception {

  private final String orgId;
  private final String state;

  private final String country;

  public PostalCodeTimezoneServiceException(
      String message, String orgId, String state, String country) {
    super(message);
    this.orgId = orgId;
    this.state = state;
    this.country = country;
  }
}
