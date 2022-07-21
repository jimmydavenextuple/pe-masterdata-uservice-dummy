package com.hbc.pe.masterdata.calendar.util;

import com.hbc.calendar.domain.pojo.ExceptionDays;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

class DateValidationTest {

  @InjectMocks private DateValidation dateValidation;

  @BeforeEach
  public void init() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void dateIsValidTest() {
    Assertions.assertTrue(dateValidation.validateDate("1998-09-30"));
    Assertions.assertTrue(dateValidation.validateDate("1998-9-30"));
    Assertions.assertTrue(dateValidation.validateDate("2020-09-1"));
    Assertions.assertTrue(dateValidation.validateDate("2020-09-01"));
    Assertions.assertTrue(dateValidation.validateDate("2020-9-1"));
  }

  @Test
  void dateIsValidTest2() {
    Assertions.assertTrue(dateValidation.validateDate("2020-9-01"));
    Assertions.assertTrue(dateValidation.validateDate("2020-2-29")); // leap year
    Assertions.assertTrue(dateValidation.validateDate("2020-2-28")); // leap year
    Assertions.assertTrue(dateValidation.validateDate("2019-2-28")); // common year
    Assertions.assertTrue(
        dateValidation.validateDate("2000-02-29")); // 2000 is a leap year, % 400 == 0
  }

  @Test
  void dateIsValidTest3() {
    Assertions.assertTrue(dateValidation.validateDate("1900-02-28")); // 1900 is a common year
    Assertions.assertTrue(dateValidation.validateDate("2020-07-31"));
    Assertions.assertTrue(dateValidation.validateDate("2020-06-30"));
    Assertions.assertTrue(dateValidation.validateDate("1900-01-01"));
    Assertions.assertTrue(dateValidation.validateDate("2099-12-31"));
  }

  void dateIsInvalidTest() {
    Assertions.assertFalse(dateValidation.validateDate("1998-09-31")); // invalid day, sep max 30
    Assertions.assertFalse(dateValidation.validateDate("1998-11-31")); // invalid day, nov max 30
    Assertions.assertFalse(dateValidation.validateDate("2008-02-2x")); // invalid day 2x
    Assertions.assertFalse(dateValidation.validateDate("2008-0x-28")); // invalid month 0x
    Assertions.assertFalse(dateValidation.validateDate("20xx-02-28")); // invalid year 20xx
  }

  void dateIsInvalidTest2() {
    Assertions.assertFalse(
        dateValidation.validateDate("20-11-02")); // invalid year 20, must be yyyy
    Assertions.assertFalse(
        dateValidation.validateDate("2020/11/02")); // invalid date format, yyyy-mm-dd
    Assertions.assertFalse(dateValidation.validateDate("2020-11-32")); // invalid day, 32
    Assertions.assertFalse(dateValidation.validateDate("2020-13-30")); // invalid month 13
    Assertions.assertFalse(dateValidation.validateDate("2020-A-20")); // invalid month A
  }

  void dateIsInvalidTest3() {
    Assertions.assertFalse(dateValidation.validateDate("2020-2-30")); // leap year, feb max 29
    Assertions.assertFalse(dateValidation.validateDate("2019-2-29")); // common year, feb max 28
    Assertions.assertFalse(
        dateValidation.validateDate("1900-02-29")); // 1900 is a common year, feb max 28
    Assertions.assertFalse(
        dateValidation.validateDate("12012-04-05")); // support only 4 digits years
    Assertions.assertFalse(dateValidation.validateDate(" ")); // empty
  }

  @Test
  void validateExceptionDaysValidTest() {
    ExceptionDays exceptionDays = new ExceptionDays();
    exceptionDays.setDate("2022-12-12");
    exceptionDays.setReason("Labor Day");
    List<ExceptionDays> exceptionDaysList = new ArrayList<>();
    exceptionDaysList.add(0, exceptionDays);
    Assertions.assertTrue(dateValidation.validateExceptionDays(exceptionDaysList));
  }

  @Test
  void validateExceptionDaysInvalidTest() {
    ExceptionDays exceptionDays = new ExceptionDays();
    exceptionDays.setDate("202-12-12");
    exceptionDays.setReason("Labor Day");
    List<ExceptionDays> exceptionDaysList = new ArrayList<>();
    exceptionDaysList.add(0, exceptionDays);
    Assertions.assertFalse(dateValidation.validateExceptionDays(exceptionDaysList));
  }
}
