package com.hbc.jobs.consumers.exception;

import lombok.Data;

@Data
public class InvalidActionTypeException extends RuntimeException {

  private static final long serialVersionUID = -2762637888925614339L;
  private final String action;
  private final int rowIndex;

  public InvalidActionTypeException(String message, String action, int rowIndex) {
    super(message);
    this.action = action;
    this.rowIndex = rowIndex;
  }
}
