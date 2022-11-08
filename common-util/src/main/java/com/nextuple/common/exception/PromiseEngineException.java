package com.nextuple.common.exception;

import com.nextuple.common.enums.ApplicationLayer;
import com.nextuple.common.enums.ExceptionCodeMapping;
import java.util.List;
import lombok.Getter;

/** This is the Promise Engine Exception that should be used accross the board. */
@Getter
public class PromiseEngineException extends Exception {
  private static final long serialVersionUID = 1L;
  private final ApplicationLayer applicationLayer;
  private final ExceptionCodeMapping exceptionCode;
  private final List<String> arguments;

  public PromiseEngineException(
      ApplicationLayer applicationLayer, ExceptionCodeMapping exceptionCode, String message) {
    super(message);
    this.applicationLayer = applicationLayer;
    this.exceptionCode = exceptionCode;
    this.arguments = null;
  }
}
