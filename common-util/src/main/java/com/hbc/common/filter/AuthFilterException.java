package com.hbc.common.filter;

public class AuthFilterException extends RuntimeException {

  public AuthFilterException(
      String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
