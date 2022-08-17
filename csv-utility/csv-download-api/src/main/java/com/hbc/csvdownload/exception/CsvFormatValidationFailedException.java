package com.hbc.csvdownload.exception;

public class CsvFormatValidationFailedException extends Exception {

  public CsvFormatValidationFailedException(String message) {
    super(message);
  }

  public CsvFormatValidationFailedException(String message, Throwable cause) {
    super(message, cause);
  }
}
