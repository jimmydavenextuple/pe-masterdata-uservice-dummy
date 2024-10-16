/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.calendar.persistence.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.nextuple.common.response.error.ErrorResponse;
import com.nextuple.common.response.error.ErrorType;
import java.util.Objects;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class CalendarExceptionHandlerTest {

  @InjectMocks private CalendarExceptionHandler calendarExceptionHandler;

  @BeforeEach
  void init() {
    MockitoAnnotations.openMocks(this);
  }

  public static final String CALENDAR_ID = "C001";
  public static final String ORG_ID = "Bay";

  @Test
  void handleCalendarDomainExceptionException() {
    CalendarDomainException e =
        new CalendarDomainException("error", null, CALENDAR_ID, null, null, null);

    ResponseEntity<ErrorResponse> responseEntity =
        calendarExceptionHandler.handleCalendarDomainException(e);

    assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    assertEquals(
        ErrorType.ERROR, Objects.requireNonNull(responseEntity.getBody()).getPayload().getType());
  }

  @Test
  void handleCalenderServiceException() {
    CalenderServiceException e = new CalenderServiceException("error", ORG_ID, null);

    ResponseEntity<ErrorResponse> responseEntity =
        calendarExceptionHandler.handleCalenderServiceException(e);

    assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    assertEquals(
        ErrorType.ERROR, Objects.requireNonNull(responseEntity.getBody()).getPayload().getType());
  }

  @Test
  void handleDateExceptionException() {
    DateException e = new DateException("error", CALENDAR_ID, ORG_ID);

    ResponseEntity<ErrorResponse> responseEntity = calendarExceptionHandler.handleDateException(e);

    assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    assertEquals(
        ErrorType.ERROR, Objects.requireNonNull(responseEntity.getBody()).getPayload().getType());
  }
}
