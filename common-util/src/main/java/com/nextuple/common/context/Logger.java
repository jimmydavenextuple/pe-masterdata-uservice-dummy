package com.nextuple.common.context;

import java.util.Collections;
import java.util.Map;
import org.slf4j.MDC;
import org.springframework.util.CollectionUtils;

/** A wrapper of {@link org.slf4j.Logger} - it introduces log with metadata feature */
public class Logger {

  private static final Map<String, String> EMPTY_MAP = Collections.emptyMap();

  private org.slf4j.Logger slf4jLogger;

  /**
   * Initialize logger with {@link org.slf4j.Logger}
   *
   * @param logger
   */
  public Logger(org.slf4j.Logger logger) {
    this.slf4jLogger = logger;
  }

  /**
   * Log a message at DEBUG level
   *
   * @param message A message to log
   * @param params String parameters to format log message
   */
  public void debug(String message, Object... params) {
    debug(EMPTY_MAP, message, params);
  }

  /**
   * Log a message with metadata at DEBUG level
   *
   * @param metadata is used to present log metadata
   * @param message A message to log
   * @param params String parameters to format log message
   */
  public void debug(Map<String, String> metadata, String message, Object... params) {
    logStatement(() -> slf4jLogger.debug(message, params), metadata);
  }

  /**
   * Log a message at ERROR level
   *
   * @param message A message to log
   * @param params String parameters to format log message
   */
  public void error(String message, Object... params) {
    error(null, EMPTY_MAP, message, params);
  }

  /**
   * Log a message with metadata at ERROR level
   *
   * @param metadata is used to present log metadata
   * @param message A message to log
   * @param params String parameters to format log message
   */
  public void error(Map<String, String> metadata, String message, Object... params) {
    error(null, metadata, message, params);
  }

  /**
   * Log an exception at ERROR level
   *
   * @param t Throwable object to be logged
   */
  public void error(Throwable t) {
    error(t, t.getMessage());
  }

  /**
   * Log an exception with message at ERROR level
   *
   * @param t Throwable object to be logged
   * @param message A message to log along with exception object
   * @param params String parameters to format log message
   */
  public void error(Throwable t, String message, Object... params) {
    error(t, EMPTY_MAP, message, params);
  }

  /**
   * Log an exception with metadata at ERROR level
   *
   * @param t Throwable object to be logged
   * @param metadata
   */
  public void error(Throwable t, Map<String, String> metadata) {
    error(t, metadata, t.getMessage());
  }

  /**
   * Log an exception with metadata and message at ERROR level
   *
   * @param t Throwable object to be logged
   * @param metadata is used to present log metadata
   * @param message A message to log along with exception object
   * @param params String parameters to format log message
   */
  public void error(Throwable t, Map<String, String> metadata, String message, Object... params) {
    logStatement(
        () -> {
          if (t != null) {
            slf4jLogger.error("Exception thrown : ", t);
          }
          slf4jLogger.error(message, params);
        },
        metadata);
  }

  /**
   * Log a message at TRACE level
   *
   * @param message A message to log
   * @param params String parameters to format log message
   */
  public void trace(String message, Object... params) {
    trace(EMPTY_MAP, message, params);
  }

  /**
   * Log a message with metadata at TRACE level
   *
   * @param metadata is used to present log metadata
   * @param message A message to log
   * @param params String parameters to format log message
   */
  public void trace(Map<String, String> metadata, String message, Object... params) {
    logStatement(() -> slf4jLogger.trace(message, params), metadata);
  }

  /**
   * Log a message at INFO level
   *
   * @param message A message to log
   * @param params String parameters to format log message
   */
  public void info(String message, Object... params) {
    info(EMPTY_MAP, message, params);
  }

  /**
   * Log a message with metadata at INFO level
   *
   * @param metadata is used to present log metadata
   * @param message A message to log
   * @param params String parameters to format log message
   */
  public void info(Map<String, String> metadata, String message, Object... params) {
    logStatement(() -> slf4jLogger.info(message, params), metadata);
  }

  /**
   * Log a message at WARN level
   *
   * @param message A message to log
   * @param params String parameters to format log message
   */
  public void warn(String message, Object... params) {
    warn(EMPTY_MAP, message, params);
  }

  /**
   * Log a message with metadata at WARN level
   *
   * @param metadata is used to present log metadata
   * @param message A message to log
   * @param params String parameters to format log message
   */
  public void warn(Map<String, String> metadata, String message, Object... params) {
    logStatement(() -> slf4jLogger.warn(message, params), metadata);
  }

  /**
   * Encapsulates log statement and wraps common features around it
   *
   * @param statement actual logger statement
   * @param metadata metadata map
   */
  private void logStatement(Runnable statement, Map<String, String> metadata) {
    populateLogContext();
    attachMetadata(metadata);
    statement.run();
    clearMetadata(metadata);
  }

  /** Populate {@link LogContext} into logging context */
  private void populateLogContext() {
    // Populate MDC with fields from LogContext
    CurrentThreadContext.getLogContext().toMap().forEach(MDC::put);
  }

  /**
   * Add objects from given metadata map to MDC map
   *
   * @param metadata Map object to be added into MDC
   */
  private void attachMetadata(Map<String, String> metadata) {
    if (!CollectionUtils.isEmpty(metadata)) {
      metadata.forEach((key, value) -> MDC.put("metadata_" + key, value));
    }
  }

  /**
   * Remove objects from MDC map which is in given metadata map
   *
   * @param metadata Map object to be removed from MDC map
   */
  private void clearMetadata(Map<String, String> metadata) {
    if (!CollectionUtils.isEmpty(metadata)) {
      metadata.forEach((key, value) -> MDC.remove("metadata_" + key));
    }
  }
}
