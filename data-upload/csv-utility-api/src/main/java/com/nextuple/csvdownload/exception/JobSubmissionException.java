package com.nextuple.csvdownload.exception;

import lombok.Data;

@Data
public class JobSubmissionException extends Exception {

  private final String orgId;

  public JobSubmissionException(String message, String orgId) {
    super(message);
    this.orgId = orgId;
  }

  public JobSubmissionException(String message, Throwable cause, String orgId) {
    super(message, cause);
    this.orgId = orgId;
  }
}
