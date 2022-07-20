package com.hbc.pe.masterdata.calendar.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class DateException extends Exception {
  final String calendarId;
  final String orgId;

  public DateException(String message, String calendarId, String orgId) {
    super(message);
    this.calendarId = calendarId;
    this.orgId = orgId;
  }
}
