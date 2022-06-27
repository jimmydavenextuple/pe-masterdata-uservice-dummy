package com.hbc.core.exception;

import lombok.Data;

@Data
public class LocalCacheUpdateEventException extends Exception {

  private final String entityName;

  public LocalCacheUpdateEventException(String message, String entityName) {
    super(message);
    this.entityName = entityName;
  }
}
