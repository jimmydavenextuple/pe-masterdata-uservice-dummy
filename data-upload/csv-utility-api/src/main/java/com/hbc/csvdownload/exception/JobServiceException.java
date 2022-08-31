package com.hbc.csvdownload.exception;

import lombok.Data;

@Data
public class JobServiceException extends Exception {

  private final String jobType;

  public JobServiceException(String message, String jobType) {
    super(message);
    this.jobType = jobType;
  }

  public JobServiceException(String message, Throwable cause, String jobType) {
    super(message, cause);
    this.jobType = jobType;
  }
}
