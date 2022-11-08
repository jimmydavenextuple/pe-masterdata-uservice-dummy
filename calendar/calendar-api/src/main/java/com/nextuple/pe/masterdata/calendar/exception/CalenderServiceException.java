package com.nextuple.pe.masterdata.calendar.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class CalenderServiceException extends Exception {

  final String orgId;
  final String carrierServiceId;

  public CalenderServiceException(String message, String orgId, String carrierServiceId) {
    super(message);
    this.orgId = orgId;
    this.carrierServiceId = carrierServiceId;
  }
}
