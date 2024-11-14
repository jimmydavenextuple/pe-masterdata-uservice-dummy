/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.calendar.persistence.exception;

import com.nextuple.common.response.error.ErrorResponse;
import com.nextuple.common.response.error.ErrorType;
import com.nextuple.common.response.error.FieldError;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Component
@ControllerAdvice
public class CalendarExceptionHandler {

  public static final String CALENDAR_ID = "calendarId";

  @ExceptionHandler(CalendarDomainException.class)
  public ResponseEntity<ErrorResponse> handleCalendarDomainException(CalendarDomainException e) {
    return ResponseEntity.badRequest()
        .body(
            ErrorResponse.builder(ErrorType.ERROR, 0xfffff1)
                .message(e.getMessage())
                .errorField(
                    CALENDAR_ID, FieldError.builder().rejectedValue(e.getCalendarId()).build())
                .errorField("nodeId", FieldError.builder().rejectedValue(e.getNodeId()).build())
                .errorField(
                    "carrierServiceId",
                    FieldError.builder().rejectedValue(e.getCarrierServiceId()).build())
                .build());
  }

  @ExceptionHandler(CalenderServiceException.class)
  public ResponseEntity<ErrorResponse> handleCalenderServiceException(CalenderServiceException e) {
    return ResponseEntity.badRequest()
        .body(
            ErrorResponse.builder(ErrorType.ERROR, 0xfffff1)
                .message(e.getMessage())
                .errorField("orgId", FieldError.builder().rejectedValue(e.getOrgId()).build())
                .errorField(
                    "carrierServiceId",
                    FieldError.builder().rejectedValue(e.getCarrierServiceId()).build())
                .build());
  }

  @ExceptionHandler(DateException.class)
  public ResponseEntity<ErrorResponse> handleDateException(DateException e) {
    return ResponseEntity.badRequest()
        .body(
            ErrorResponse.builder(ErrorType.ERROR, 0xfffff1)
                .message(e.getMessage())
                .errorField(
                    CALENDAR_ID, FieldError.builder().rejectedValue(e.getCalendarId()).build())
                .errorField("orgId", FieldError.builder().rejectedValue(e.getOrgId()).build())
                .build());
  }
}
