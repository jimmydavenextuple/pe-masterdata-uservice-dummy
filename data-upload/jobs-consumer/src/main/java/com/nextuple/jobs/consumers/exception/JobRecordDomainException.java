package com.nextuple.jobs.consumers.exception;

import lombok.Data;

@Data
public class JobRecordDomainException extends Exception {
  private final String jobRecordId;

  public JobRecordDomainException(String jobRecordId) {
    this.jobRecordId = jobRecordId;
  }

  public JobRecordDomainException(String message, String jobRecordId) {
    super(message);
    this.jobRecordId = jobRecordId;
  }

  public JobRecordDomainException(String message, Throwable cause, String jobRecordId) {
    super(message, cause);
    this.jobRecordId = jobRecordId;
  }

  public JobRecordDomainException(Throwable cause, String jobRecordId) {
    super(cause);
    this.jobRecordId = jobRecordId;
  }

  public JobRecordDomainException(
      String message,
      Throwable cause,
      boolean enableSuppression,
      boolean writableStackTrace,
      String jobRecordId) {
    super(message, cause, enableSuppression, writableStackTrace);
    this.jobRecordId = jobRecordId;
  }
}
