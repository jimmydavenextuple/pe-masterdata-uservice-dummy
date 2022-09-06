package com.hbc.csvdownload.exception;

import lombok.Data;

@Data
public class CsvDownloadUtilityServiceException extends Exception {

  private final String orgId;

  public CsvDownloadUtilityServiceException(String message, String orgId) {
    super(message);
    this.orgId = orgId;
  }

  public CsvDownloadUtilityServiceException(String message, Throwable cause, String orgId) {
    super(message, cause);
    this.orgId = orgId;
  }
}
