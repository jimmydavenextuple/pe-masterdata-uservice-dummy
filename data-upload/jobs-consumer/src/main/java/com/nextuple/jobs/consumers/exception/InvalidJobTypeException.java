package com.nextuple.jobs.consumers.exception;

import lombok.Data;

@Data
public class InvalidJobTypeException extends Exception {
  private final String jobType;

  public InvalidJobTypeException(String message, String jobType) {
    super(message);
    this.jobType = jobType;
  }
}
