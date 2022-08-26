package com.hbc.jobs.consumers.exception;

import com.hbc.jobs.framework.common.domain.enums.JobTypeEnum;
import lombok.Data;

@Data
public class NodeCarrierMapperException extends Exception {

  private final JobTypeEnum jobType;

  public NodeCarrierMapperException(String message, JobTypeEnum jobType) {
    super(message);
    this.jobType = jobType;
  }

  public NodeCarrierMapperException(String message, Throwable cause, JobTypeEnum jobType) {
    super(message, cause);
    this.jobType = jobType;
  }
}
