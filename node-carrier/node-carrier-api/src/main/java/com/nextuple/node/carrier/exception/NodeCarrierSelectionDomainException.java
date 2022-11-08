package com.nextuple.node.carrier.exception;

import lombok.Data;

@Data
public class NodeCarrierSelectionDomainException extends Exception {

  private final String orgId;
  private final String serviceOption;
  private final String sourceGeozone;
  private final String destinationGeozone;

  public NodeCarrierSelectionDomainException(
      String message,
      String orgId,
      String serviceOption,
      String sourceGeozone,
      String destinationGeozone) {
    super(message);
    this.orgId = orgId;
    this.serviceOption = serviceOption;
    this.sourceGeozone = sourceGeozone;
    this.destinationGeozone = destinationGeozone;
  }
}
