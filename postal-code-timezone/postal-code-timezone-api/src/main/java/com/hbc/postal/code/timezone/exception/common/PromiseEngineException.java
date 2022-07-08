package com.hbc.postal.code.timezone.exception.common;

import lombok.Getter;

/** This is the Promise Engine Exception that should be used accross the board. */
@Getter
public class PromiseEngineException extends Exception {
  private static final long serialVersionUID = 1L;
  private final ApplicationLayer applicationLayer;
  private final ExceptionCodeMapping exceptionCode;

  public PromiseEngineException(
      ApplicationLayer applicationLayer, ExceptionCodeMapping exceptionCode, String message) {
    super(message);
    this.applicationLayer = applicationLayer;
    this.exceptionCode = exceptionCode;
  }
}
