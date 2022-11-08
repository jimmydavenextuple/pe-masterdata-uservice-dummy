package com.nextuple.jobs.consumers.exception;

import com.nextuple.jobs.framework.common.domain.enums.RecordDataTypeEnum;
import lombok.Data;

@Data
public class FeignClientMapperException extends Exception {
  private final RecordDataTypeEnum recordDataType;

  public FeignClientMapperException(RecordDataTypeEnum recordDataType) {
    this.recordDataType = recordDataType;
  }

  public FeignClientMapperException(String message, RecordDataTypeEnum recordDataType) {
    super(message);
    this.recordDataType = recordDataType;
  }

  public FeignClientMapperException(
      String message, Throwable cause, RecordDataTypeEnum recordDataType) {
    super(message, cause);
    this.recordDataType = recordDataType;
  }

  public FeignClientMapperException(Throwable cause, RecordDataTypeEnum recordDataType) {
    super(cause);
    this.recordDataType = recordDataType;
  }

  public FeignClientMapperException(
      String message,
      Throwable cause,
      boolean enableSuppression,
      boolean writableStackTrace,
      RecordDataTypeEnum recordDataType) {
    super(message, cause, enableSuppression, writableStackTrace);
    this.recordDataType = recordDataType;
  }
}
