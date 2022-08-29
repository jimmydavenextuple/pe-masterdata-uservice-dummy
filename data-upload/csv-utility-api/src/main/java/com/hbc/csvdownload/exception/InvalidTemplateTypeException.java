package com.hbc.csvdownload.exception;

import lombok.Data;

@Data
public class InvalidTemplateTypeException extends Exception {

  private final String templateType;

  public InvalidTemplateTypeException(String message, String templateType) {
    super(message);
    this.templateType = templateType;
  }
}
