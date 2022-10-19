package com.hbc.transit.exception;

import lombok.Data;

@Data
public class TransitBufferJobException extends Exception {

  private final String jobId;

  public TransitBufferJobException(String message, Throwable cause, String jobId) {
    super(message, cause);
    this.jobId = jobId;
  }
}
