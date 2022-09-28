package com.hbc.jobs.consumers.exception;

import lombok.Data;

@Data
public class DuplicateJobException extends Exception {
  private final String jobId;

  public DuplicateJobException(String jobId) {
    this.jobId = jobId;
  }

  public DuplicateJobException(String message, String jobId) {
    super(message);
    this.jobId = jobId;
  }

  public DuplicateJobException(String message, Throwable cause, String jobId) {
    super(message, cause);
    this.jobId = jobId;
  }

  public DuplicateJobException(Throwable cause, String jobId) {
    super(cause);
    this.jobId = jobId;
  }

  public DuplicateJobException(
      String message,
      Throwable cause,
      boolean enableSuppression,
      boolean writableStackTrace,
      String jobId) {
    super(message, cause, enableSuppression, writableStackTrace);
    this.jobId = jobId;
  }
}
