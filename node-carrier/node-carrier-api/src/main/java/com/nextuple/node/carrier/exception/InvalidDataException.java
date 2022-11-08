package com.nextuple.node.carrier.exception;

import lombok.Data;

@Data
public class InvalidDataException extends Exception {
  private final String lastPickupTime;
  private final Double processingLeadTime;

  public InvalidDataException(String message, String lastPickupTime, Double processingLeadTime) {
    super(message);
    this.lastPickupTime = lastPickupTime;
    this.processingLeadTime = processingLeadTime;
  }
}
