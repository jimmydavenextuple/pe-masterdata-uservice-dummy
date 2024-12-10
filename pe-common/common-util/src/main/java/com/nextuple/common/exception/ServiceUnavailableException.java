package com.nextuple.common.exception;

import static com.nextuple.common.constants.CommonConstants.SERVER_UNAVAILABLE_ERROR_MESSAGE;

public class ServiceUnavailableException extends RuntimeException {
  public ServiceUnavailableException() {
    super(SERVER_UNAVAILABLE_ERROR_MESSAGE);
  }

  public ServiceUnavailableException(String message) {
    super(message);
  }

  public ServiceUnavailableException(Throwable cause) {
    super(cause);
  }
}
