package com.hbc.jobs.consumers.exception;

import lombok.Data;

@Data
public class JobDomainException extends Exception {

  private final String jobId;

  public JobDomainException(String jobId) {
    this.jobId = jobId;
  }

  public JobDomainException(String message, String jobId) {
    super(message);
    this.jobId = jobId;
  }

  public JobDomainException(String message, Throwable cause, String jobId) {
    super(message, cause);
    this.jobId = jobId;
  }

  public JobDomainException(Throwable cause, String jobId) {
    super(cause);
    this.jobId = jobId;
  }

  public JobDomainException(
      String message,
      Throwable cause,
      boolean enableSuppression,
      boolean writableStackTrace,
      String jobId) {
    super(message, cause, enableSuppression, writableStackTrace);
    this.jobId = jobId;
  }
}
