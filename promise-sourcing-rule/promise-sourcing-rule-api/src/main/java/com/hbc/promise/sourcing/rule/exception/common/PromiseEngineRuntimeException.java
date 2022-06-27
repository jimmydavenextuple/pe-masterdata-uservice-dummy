package com.hbc.promise.sourcing.rule.exception.common;

public class PromiseEngineRuntimeException extends RuntimeException {
  public PromiseEngineRuntimeException() {}

  public PromiseEngineRuntimeException(String message) {
    super(message);
  }

  public PromiseEngineRuntimeException(String message, Throwable cause) {
    super(message, cause);
  }

  public PromiseEngineRuntimeException(Throwable cause) {
    super(cause);
  }

  public PromiseEngineRuntimeException(
      String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
