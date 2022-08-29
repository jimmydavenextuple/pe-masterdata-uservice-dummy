package com.hbc.jobs.consumers.exception;

import lombok.Data;

@Data
public class JobException extends Exception {
  private final String jobId;
  private final Integer pageNo;

  public JobException(String jobId, Integer pageNo) {
    this.jobId = jobId;
    this.pageNo = pageNo;
  }

  public JobException(String message, String jobId, Integer pageNo) {
    super(message);
    this.jobId = jobId;
    this.pageNo = pageNo;
  }

  public JobException(String message, Throwable cause, String jobId, Integer pageNo) {
    super(message, cause);
    this.jobId = jobId;
    this.pageNo = pageNo;
  }

  public JobException(Throwable cause, String jobId, Integer pageNo) {
    super(cause);
    this.jobId = jobId;
    this.pageNo = pageNo;
  }

  public JobException(
      String message,
      Throwable cause,
      boolean enableSuppression,
      boolean writableStackTrace,
      String jobId,
      Integer pageNo) {
    super(message, cause, enableSuppression, writableStackTrace);
    this.jobId = jobId;
    this.pageNo = pageNo;
  }
}
