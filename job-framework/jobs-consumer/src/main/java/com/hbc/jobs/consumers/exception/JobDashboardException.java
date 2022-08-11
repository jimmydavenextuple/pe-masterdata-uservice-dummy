package com.hbc.jobs.consumers.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class JobDashboardException extends Exception {

  private final String jobId;

  public JobDashboardException(String jobId) {
    this.jobId = jobId;
  }

  public JobDashboardException(String message, String jobId) {
    super(message);
    this.jobId = jobId;
  }

  public JobDashboardException(String message, Throwable cause, String jobId) {
    super(message, cause);
    this.jobId = jobId;
  }

  public JobDashboardException(Throwable cause, String jobId) {
    super(cause);
    this.jobId = jobId;
  }

  public JobDashboardException(
      String message,
      Throwable cause,
      boolean enableSuppression,
      boolean writableStackTrace,
      String jobId) {
    super(message, cause, enableSuppression, writableStackTrace);
    this.jobId = jobId;
  }
}
