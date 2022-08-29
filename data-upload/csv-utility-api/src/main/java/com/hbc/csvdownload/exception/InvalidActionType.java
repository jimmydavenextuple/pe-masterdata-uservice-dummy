package com.hbc.csvdownload.exception;

import lombok.Data;

@Data
public class InvalidActionType extends RuntimeException {

  private static final long serialVersionUID = -2762637888925614339L;
  private final String action;
  private final int rowIndex;

  public InvalidActionType(String message, String action, int rowIndex) {
    super(message);
    this.action = action;
    this.rowIndex = rowIndex;
  }

  public InvalidActionType(String message, Throwable cause, String action, int rowIndex) {
    super(message, cause);
    this.action = action;
    this.rowIndex = rowIndex;
  }
}
