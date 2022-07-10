package com.hbc.common.exception;

import com.hbc.common.ApplicationLayer;
import com.hbc.common.ExceptionCodeMapping;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

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
