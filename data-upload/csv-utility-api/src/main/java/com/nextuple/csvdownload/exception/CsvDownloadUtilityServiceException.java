package com.nextuple.csvdownload.exception;

import lombok.Data;

@Data
public class CsvDownloadUtilityServiceException extends Exception {

  private final String orgId;

  public CsvDownloadUtilityServiceException(String message, String orgId) {
    super(message);
    this.orgId = orgId;
  }
}
