package com.hbc.jobs.dashboard.exception;

import com.hbc.jobs.framework.common.domain.enums.JobTypeEnum;
import lombok.Data;

@Data
public class JobException extends Exception {
  private final String jobId;
  private final JobTypeEnum jobType;

  public JobException(String jobId, JobTypeEnum jobType) {
    this.jobId = jobId;
    this.jobType = jobType;
  }

  public JobException(String message, String jobId, JobTypeEnum jobType) {
    super(message);
    this.jobId = jobId;
    this.jobType = jobType;
  }

  public JobException(String message, Throwable cause, String jobId, JobTypeEnum jobType) {
    super(message, cause);
    this.jobId = jobId;
    this.jobType = jobType;
  }

  public JobException(Throwable cause, String jobId, JobTypeEnum jobType) {
    super(cause);
    this.jobId = jobId;
    this.jobType = jobType;
  }

  public JobException(
      String message,
      Throwable cause,
      boolean enableSuppression,
      boolean writableStackTrace,
      String jobId,
      JobTypeEnum jobType) {
    super(message, cause, enableSuppression, writableStackTrace);
    this.jobId = jobId;
    this.jobType = jobType;
  }
}
