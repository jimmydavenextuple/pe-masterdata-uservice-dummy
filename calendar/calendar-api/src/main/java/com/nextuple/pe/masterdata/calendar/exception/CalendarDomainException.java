package com.nextuple.pe.masterdata.calendar.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class CalendarDomainException extends Exception {
  final String calendarId;
  final String orgId;
  final String nodeId;
  final String carrierServiceId;

  public CalendarDomainException(
      String message,
      Throwable cause,
      String calendarId,
      String orgId,
      String nodeId,
      String carrierServiceId) {
    super(message, cause);
    this.calendarId = calendarId;
    this.orgId = orgId;
    this.nodeId = nodeId;
    this.carrierServiceId = carrierServiceId;
  }
}
