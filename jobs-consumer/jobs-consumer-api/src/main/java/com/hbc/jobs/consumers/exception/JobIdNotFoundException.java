package com.hbc.jobs.consumers.exception;

import lombok.Data;

@Data
public class JobIdNotFoundException extends Exception {
  private final String jobId;

  public JobIdNotFoundException(String jobId) {
    this.jobId = jobId;
  }

  public JobIdNotFoundException(String message, String jobId) {
    super(message);
    this.jobId = jobId;
  }

  public JobIdNotFoundException(String message, Throwable cause, String jobId) {
    super(message, cause);
    this.jobId = jobId;
  }

  public JobIdNotFoundException(Throwable cause, String jobId) {
    super(cause);
    this.jobId = jobId;
  }

  public JobIdNotFoundException(
      String message,
      Throwable cause,
      boolean enableSuppression,
      boolean writableStackTrace,
      String jobId) {
    super(message, cause, enableSuppression, writableStackTrace);
    this.jobId = jobId;
  }
}
