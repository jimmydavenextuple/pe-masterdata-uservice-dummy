package com.hbc.node.carrier.exception;

import lombok.Data;

@Data
public class InvalidDataException extends Exception {
  private final String lastPickupTime;

  public InvalidDataException(String message, String lastPickupTime) {
    super(message);
    this.lastPickupTime = lastPickupTime;
  }
}
