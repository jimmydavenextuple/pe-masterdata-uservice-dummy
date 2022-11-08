package com.nextuple.csvdownload.exception;

import lombok.Data;

@Data
public class JsonParsingException extends Exception {

  private final String orgId;

  public JsonParsingException(String message, String orgId) {
    super(message);
    this.orgId = orgId;
  }

  public JsonParsingException(String message, Throwable cause, String orgId) {
    super(message, cause);
    this.orgId = orgId;
  }
}
