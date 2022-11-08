package com.nextuple.jobs.dashboard.exception;

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
}
