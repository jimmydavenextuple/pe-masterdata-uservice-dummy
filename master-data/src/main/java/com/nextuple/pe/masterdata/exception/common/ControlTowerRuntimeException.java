package com.nextuple.pe.masterdata.exception.common;

public class ControlTowerRuntimeException extends RuntimeException {
  public ControlTowerRuntimeException() {}

  public ControlTowerRuntimeException(String message) {
    super(message);
  }

  public ControlTowerRuntimeException(String message, Throwable cause) {
    super(message, cause);
  }

  public ControlTowerRuntimeException(Throwable cause) {
    super(cause);
  }

  public ControlTowerRuntimeException(
      String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
