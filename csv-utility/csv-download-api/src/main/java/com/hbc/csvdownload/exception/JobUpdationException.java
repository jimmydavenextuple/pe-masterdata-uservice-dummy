package com.hbc.csvdownload.exception;

import lombok.Data;

@Data
public class JobUpdationException extends Exception {

  private final String orgId;
  private final String jobId;
  private final String jobType;

  public JobUpdationException(String message, String orgId, String jobId, String jobType) {
    super(message);
    this.orgId = orgId;
    this.jobId = jobId;
    this.jobType = jobType;
  }

  public JobUpdationException(
      String message, Throwable cause, String orgId, String jobId, String jobType) {
    super(message, cause);
    this.orgId = orgId;
    this.jobId = jobId;
    this.jobType = jobType;
  }
}
