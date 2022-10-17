package com.hbc.transit.exception;

import lombok.Data;

@Data
public class TransitBufferReqJobRefDomainException extends Exception {

  private final Long id;
  private final String extReferenceId;

  public TransitBufferReqJobRefDomainException(String message, Long id, String extReferenceId) {
    super(message);
    this.id = id;
    this.extReferenceId = extReferenceId;
  }
}
