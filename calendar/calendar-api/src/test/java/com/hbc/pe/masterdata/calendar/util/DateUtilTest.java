package com.hbc.pe.masterdata.calendar.util;

import com.hbc.calendar.domain.pojo.ExceptionDays;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class DateUtilTest {

  @Test
  void dateIsValidTest() {
    Assertions.assertTrue(DateUtil.validateDate("1998-09-30"));
    Assertions.assertTrue(DateUtil.validateDate("1998-9-30"));
    Assertions.assertTrue(DateUtil.validateDate("2020-09-1"));
    Assertions.assertTrue(DateUtil.validateDate("2020-09-01"));
    Assertions.assertTrue(DateUtil.validateDate("2020-9-1"));
    Assertions.assertTrue(DateUtil.validateDate("2020-9-01"));
    Assertions.assertTrue(DateUtil.validateDate("2020-2-29")); // leap year
    Assertions.assertTrue(DateUtil.validateDate("2020-2-28")); // leap year
    Assertions.assertTrue(DateUtil.validateDate("2019-2-28")); // common year
    Assertions.assertTrue(DateUtil.validateDate("2000-02-29")); // 2000 is a leap year, % 400 == 0
    Assertions.assertTrue(DateUtil.validateDate("1900-02-28")); // 1900 is a common year
    Assertions.assertTrue(DateUtil.validateDate("2020-07-31"));
    Assertions.assertTrue(DateUtil.validateDate("2020-06-30"));
    Assertions.assertTrue(DateUtil.validateDate("1900-01-01"));
    Assertions.assertTrue(DateUtil.validateDate("2099-12-31"));
  }

  @Test
  void dateIsInvalidTest() {
    Assertions.assertFalse(DateUtil.validateDate("1998-09-31")); // invalid day, sep max 30
    Assertions.assertFalse(DateUtil.validateDate("1998-11-31")); // invalid day, nov max 30
    Assertions.assertFalse(DateUtil.validateDate("2008-02-2x")); // invalid day 2x
    Assertions.assertFalse(DateUtil.validateDate("2008-0x-28")); // invalid month 0x
    Assertions.assertFalse(DateUtil.validateDate("20xx-02-28")); // invalid year 20xx
    Assertions.assertFalse(DateUtil.validateDate("20-11-02")); // invalid year 20, must be yyyy
    Assertions.assertFalse(DateUtil.validateDate("2020/11/02")); // invalid date format, yyyy-mm-dd
    Assertions.assertFalse(DateUtil.validateDate("2020-11-32")); // invalid day, 32
    Assertions.assertFalse(DateUtil.validateDate("2020-13-30")); // invalid month 13
    Assertions.assertFalse(DateUtil.validateDate("2020-A-20")); // invalid month A
    Assertions.assertFalse(DateUtil.validateDate("2020-2-30")); // leap year, feb max 29
    Assertions.assertFalse(DateUtil.validateDate("2019-2-29")); // common year, feb max 28
    Assertions.assertFalse(
        DateUtil.validateDate("1900-02-29")); // 1900 is a common year, feb max 28
    Assertions.assertFalse(DateUtil.validateDate("12012-04-05")); // support only 4 digits years
    Assertions.assertFalse(DateUtil.validateDate(" ")); // empty
    Assertions.assertFalse(DateUtil.validateDate("")); // empty
  }

  @Test
  void validateExceptionDaysValidTest() {
    ExceptionDays exceptionDays = new ExceptionDays();
    exceptionDays.setDate("2022-12-12");
    exceptionDays.setReason("Labor Day");
    List<ExceptionDays> exceptionDaysList = new ArrayList<>();
    exceptionDaysList.add(0, exceptionDays);
    Assertions.assertTrue(DateUtil.validateExceptionDays(exceptionDaysList));
  }

  @Test
  void validateExceptionDaysInvalidTest() {
    ExceptionDays exceptionDays = new ExceptionDays();
    exceptionDays.setDate("202-12-12");
    exceptionDays.setReason("Labor Day");
    List<ExceptionDays> exceptionDaysList = new ArrayList<>();
    exceptionDaysList.add(0, exceptionDays);
    Assertions.assertFalse(DateUtil.validateExceptionDays(exceptionDaysList));
  }
}
