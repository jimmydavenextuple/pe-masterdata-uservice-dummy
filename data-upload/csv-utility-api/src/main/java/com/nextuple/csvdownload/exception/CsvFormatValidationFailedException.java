package com.nextuple.csvdownload.exception;

import lombok.Data;

@Data
public class CsvFormatValidationFailedException extends Exception {

  private final String columnName;

  public CsvFormatValidationFailedException(String message, String columnName) {
    super(message);
    this.columnName = columnName;
  }

  public CsvFormatValidationFailedException(String message, Throwable cause, String columnName) {
    super(message, cause);
    this.columnName = columnName;
  }
}
