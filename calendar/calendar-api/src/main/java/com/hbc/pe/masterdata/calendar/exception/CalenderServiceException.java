package com.hbc.pe.masterdata.calendar.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class CalenderServiceException extends Exception {

  final String calendarId;
  final String orgId;
  final String carrierServiceId;

  public CalenderServiceException(
      String message, Throwable cause, String calendarId, String orgId, String carrierServiceId) {
    super(message, cause);
    this.calendarId = calendarId;
    this.orgId = orgId;
    this.carrierServiceId = carrierServiceId;
  }
}
