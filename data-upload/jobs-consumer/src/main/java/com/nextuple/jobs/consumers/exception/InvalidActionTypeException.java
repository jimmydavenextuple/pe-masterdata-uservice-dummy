package com.nextuple.jobs.consumers.exception;

import lombok.Data;

@Data
public class InvalidActionTypeException extends Exception {

  private static final long serialVersionUID = -2762637888925614339L;
  private final String action;

  public InvalidActionTypeException(String message, String action) {
    super(message);
    this.action = action;
  }
}
