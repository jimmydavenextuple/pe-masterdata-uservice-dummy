package com.nextuple.jobs.dashboard.exception;

import lombok.Data;

@Data
public class FileMetaDataException extends Exception {

  private final Long id;

  private final String message;

  public FileMetaDataException(String message, Long id) {
    this.id = id;
    this.message = message;
  }
}
