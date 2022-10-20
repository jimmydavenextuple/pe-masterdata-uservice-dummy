package com.hbc.jobs.consumers.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class PublishJobEventException extends Exception {

  private final String jobId;

  public PublishJobEventException(String jobId) {
    this.jobId = jobId;
  }

  public PublishJobEventException(String message, String jobId) {
    super(message);
    this.jobId = jobId;
  }

  public PublishJobEventException(String message, Throwable cause, String jobId) {
    super(message, cause);
    this.jobId = jobId;
  }

  public PublishJobEventException(Throwable cause, String jobId) {
    super(cause);
    this.jobId = jobId;
  }

  public PublishJobEventException(
      String message,
      Throwable cause,
      boolean enableSuppression,
      boolean writableStackTrace,
      String jobId) {
    super(message, cause, enableSuppression, writableStackTrace);
    this.jobId = jobId;
  }
}
