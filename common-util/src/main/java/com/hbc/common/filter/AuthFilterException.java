package com.hbc.common.filter;

public class AuthFilterException extends RuntimeException {

  public AuthFilterException(String message) {
    super(message, null, true, false);
  }
}
