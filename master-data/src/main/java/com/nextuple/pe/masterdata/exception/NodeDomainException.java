package com.nextuple.pe.masterdata.exception;

import lombok.Data;

@Data
public class NodeDomainException extends Exception {
  private final String nodeId;
  private final String orgId;

  public NodeDomainException(String message, String nodeId, String orgId) {
    super(message);
    this.nodeId = nodeId;
    this.orgId = orgId;
  }
}
