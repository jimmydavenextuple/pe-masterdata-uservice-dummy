package com.hbc.node.carrier.exception;

import lombok.Data;

@Data
public class NodeCarrierDomainException extends Exception {

  private final String nodeId;
  private final String orgId;
  private final String carrierServiceId;
  private final String serviceOption;

  public NodeCarrierDomainException(
      String message, String nodeId, String orgId, String carrierServiceId, String serviceOption) {
    super(message);
    this.nodeId = nodeId;
    this.orgId = orgId;
    this.carrierServiceId = carrierServiceId;
    this.serviceOption = serviceOption;
  }
}
