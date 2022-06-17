package com.nextuple.transit.exception;

import lombok.Data;

@Data
public class TransitDomainException extends Exception {

  private final String orgId;
  private final String sourceGeozone;
  private final String destinationGeozone;
  private final String carrierServiceId;

  public TransitDomainException(
      String message,
      String orgId,
      String sourceGeozone,
      String destinationGeozone,
      String carrierServiceId) {
    super(message);
    this.orgId = orgId;
    this.sourceGeozone = sourceGeozone;
    this.destinationGeozone = destinationGeozone;
    this.carrierServiceId = carrierServiceId;
  }
}
