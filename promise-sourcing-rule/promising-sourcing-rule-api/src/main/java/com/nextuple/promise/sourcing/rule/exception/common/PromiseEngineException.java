package com.nextuple.promise.sourcing.rule.exception.common;

import java.util.Arrays;
import java.util.List;
import lombok.Getter;

/** This is the Promise Engine Exception that should be used accross the board. */
@Getter
public class PromiseEngineException extends Exception {
  private static final long serialVersionUID = 1L;
  private final ApplicationLayer applicationLayer;
  private final ExceptionCodeMapping exceptionCode;
  private final List<String> arguments;

  public PromiseEngineException() {
    this.applicationLayer = null;
    this.exceptionCode = null;
    this.arguments = null;
  }

  /**
   * @param applicationLayer
   * @param exceptionCode
   * @param message
   * @param arguments
   */
  public PromiseEngineException(
      ApplicationLayer applicationLayer,
      ExceptionCodeMapping exceptionCode,
      String message,
      String... arguments) {
    super(message);
    this.applicationLayer = applicationLayer;
    this.exceptionCode = exceptionCode;
    this.arguments = Arrays.asList(arguments);
  }

  /**
   * @param applicationLayer
   * @param exceptionCode
   * @param message
   * @param arguments
   */
  public PromiseEngineException(
      ApplicationLayer applicationLayer,
      ExceptionCodeMapping exceptionCode,
      String message,
      List<String> arguments) {
    super(message);
    this.applicationLayer = applicationLayer;
    this.exceptionCode = exceptionCode;
    this.arguments = arguments;
  }

  /**
   * @param applicationLayer
   * @param exceptionCode
   * @param message
   */
  public PromiseEngineException(
      ApplicationLayer applicationLayer, ExceptionCodeMapping exceptionCode, String message) {
    super(message);
    this.applicationLayer = applicationLayer;
    this.exceptionCode = exceptionCode;
    this.arguments = null;
  }

  /**
   * @param applicationLayer
   * @param exceptionCode
   * @param message
   * @param cause
   */
  public PromiseEngineException(
      ApplicationLayer applicationLayer,
      ExceptionCodeMapping exceptionCode,
      String message,
      Throwable cause) {
    super(message, cause);
    this.applicationLayer = applicationLayer;
    this.exceptionCode = exceptionCode;
    this.arguments = null;
  }

  /**
   * @param applicationLayer
   * @param exceptionCode
   * @param message
   * @param cause
   * @param arguments
   */
  public PromiseEngineException(
      ApplicationLayer applicationLayer,
      ExceptionCodeMapping exceptionCode,
      String message,
      Throwable cause,
      String... arguments) {
    super(message, cause);
    this.applicationLayer = applicationLayer;
    this.exceptionCode = exceptionCode;
    this.arguments = Arrays.asList(arguments);
  }

  /**
   * @param applicationLayer
   * @param exceptionCode
   * @param message
   * @param cause
   * @param arguments
   */
  public PromiseEngineException(
      ApplicationLayer applicationLayer,
      ExceptionCodeMapping exceptionCode,
      String message,
      Throwable cause,
      List<String> arguments) {
    super(message, cause);
    this.applicationLayer = applicationLayer;
    this.exceptionCode = exceptionCode;
    this.arguments = arguments;
  }

  /** @param message */
  public PromiseEngineException(String message) {
    super(message);
    this.applicationLayer = null;
    this.exceptionCode = null;
    this.arguments = null;
  }

  /**
   * @param message
   * @param cause
   */
  public PromiseEngineException(String message, Throwable cause) {
    super(message, cause);
    this.applicationLayer = null;
    this.exceptionCode = null;
    this.arguments = null;
  }
}
