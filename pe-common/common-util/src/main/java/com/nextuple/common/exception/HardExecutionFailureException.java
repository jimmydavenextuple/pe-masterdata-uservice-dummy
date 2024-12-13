package com.nextuple.common.exception;

import static com.nextuple.common.constants.CommonConstants.HARD_EXECUTION_ERROR_MESSAGE;

public class HardExecutionFailureException extends RuntimeException {
  public HardExecutionFailureException() {
    super(HARD_EXECUTION_ERROR_MESSAGE);
  }

  public HardExecutionFailureException(String message) {
    super(message);
  }

  public HardExecutionFailureException(Throwable cause) {
    super(cause);
  }
}
