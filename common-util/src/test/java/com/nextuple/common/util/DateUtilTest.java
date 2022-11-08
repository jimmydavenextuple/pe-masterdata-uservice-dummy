package com.nextuple.common.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Date;
import java.util.List;
import org.joda.time.LocalDateTime;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class DateUtilTest {

  @DisplayName("Test to check if the date dt1 is before date dt2 ")
  @Test
  void isDateBefore() {

    Date dt1 = new Date();
    long millis = dt1.getTime();
    Date dt2 = new Date(millis + 10000);

    /** check the positive use case */
    assertTrue(DateUtil.isDateBefore(dt1, dt2));

    /** check the negative use case */
    assertFalse(DateUtil.isDateBefore(null, dt1));

    assertFalse(DateUtil.isDateBefore(null, null));
  }

  @DisplayName("Test to check if the date dt1 is after date dt2 ")
  @Test
  void isDateAfter() {
    Date dt1 = new Date();
    long millis = dt1.getTime();
    Date dt2 = new Date(millis - 10000);

    /** check the positive use case */
    assertTrue(DateUtil.isDateAfter(dt1, dt2));

    /** check the negative use case */
    assertFalse(DateUtil.isDateAfter(null, dt1));

    assertFalse(DateUtil.isDateAfter(null, null));
  }

  @DisplayName("Test to check if the date is before today")
  @Test
  void isDateBeforeToday() {
    Date dt1 = new Date();
    long millis = dt1.getTime();
    Date dt2 = new Date(millis - 172800000);
    Date dt3 = new Date(millis + 172800000);

    /** check the positive use case */
    assertTrue(DateUtil.isDateBeforeToday(dt2));

    /** check the negative use case */
    assertFalse(DateUtil.isDateBeforeToday(null));
  }

  @DisplayName("Test to check if the date is after today ")
  @Test
  void isDateAfterToday() {
    Date dt1 = new Date();
    long millis = dt1.getTime();
    Date dt2 = new Date(millis - 172800000);
    Date dt3 = new Date(millis + 172800000);

    /** check the positive use case */
    assertTrue(DateUtil.isDateAfterToday(dt3));

    /** check the negative use case */
    assertFalse(DateUtil.isDateAfterToday(dt2));

    /** check if null */
    assertFalse(DateUtil.isDateAfterToday(null));
  }

  @DisplayName("Test to check if the date dt1 is same day as date dt2 ")
  @Test
  void isSameDayIgnoreTime() {
    Date dt1 = new Date();
    Date dt2 = DateUtil.addMinutesToDate(dt1, 2);
    Date dt3 = DateUtil.addDaysToDate(dt1, 2);

    /** check the positive use case */
    assertTrue(DateUtil.isSameDayIgnoreTime(dt1, dt2));

    /** check the negative use case */
    assertFalse(DateUtil.isSameDayIgnoreTime(dt1, dt3));

    assertFalse(DateUtil.isSameDayIgnoreTime(null, null));
  }

  @DisplayName("Test to get the currentDate ")
  @Test
  void getCurrentDate() {
    Date dt = DateUtil.getCurrentDate();
    /** check the positive use case */
    assertNotNull(dt);
  }

  // TODO: FIX ME
  @DisplayName("Test to get utc date ")
  @Test
  void getDateUTC() {
    String dateStr1 = "2019-08-23";
    assertNull(DateUtil.getDateUTC(dateStr1));
  }

  @DisplayName("Test to get the date and time")
  @Test
  void getDateAndTime() {
    String dateStr2 = "2019";

    /** check the negative use case */
    assertNull(DateUtil.getDateAndTime(dateStr2));
  }

  @DisplayName("Test to get the date from short date format ")
  @Test
  void getDate() {
    String dateStr1 = "2019-08-23";
    String dateStr2 = "2019";

    /** check the positive use */
    assertNotNull(DateUtil.getDate(dateStr1));

    /** check the negative use case */
    assertNull(DateUtil.getDate(dateStr2));
  }

  @DisplayName("Test to get the date now ")
  @Test
  void getDateNow() {
    /** check the positive use */
    assertNotNull(DateUtil.getDateNow());
  }

  @DisplayName("Test to check add minutes to current date ")
  @Test
  void addMinutesToCurrentDate() {
    Date dt = new Date();
    Date dt2 = DateUtil.addMinutesToCurrentDate(10);
    /** check the positive uses */
    assertNotEquals(dt, dt2);
    assertNotNull(dt2);
  }

  @DisplayName("Test to check add hours to current date ")
  @Test
  void addHoursToCurrentDate() {
    Date dt = new Date();
    Date dt2 = DateUtil.addHoursToCurrentDate(10);
    /** check the positive uses */
    assertNotEquals(dt, dt2);
    assertNotNull(dt2);
  }

  @DisplayName("Test to check add days to current date ")
  @Test
  void addDaysToCurrentDate() {
    Date dt = new Date();
    Date dt2 = DateUtil.addDaysToCurrentDate(10);
    /** check the positive uses */
    assertNotEquals(dt, dt2);
    assertNotNull(dt2);
  }

  @DisplayName("Test to check add minutes to a date ")
  @Test
  void addMinutesToDate() {
    Date dt = new Date();
    Date dt2 = DateUtil.addMinutesToDate(dt, 10);
    /** check the positive uses */
    assertNotEquals(dt, dt2);
    assertNotNull(dt2);

    Date dt3 = DateUtil.addMinutesToDate(dt, null);
    assertEquals(dt, dt3);
  }

  @DisplayName("Test to check add hours to a date ")
  @Test
  void addHoursToDate() {
    Date dt = new Date();
    Date dt2 = DateUtil.addHoursToDate(dt, 10);
    /** check the positive uses */
    assertNotEquals(dt, dt2);
    assertNotNull(dt2);
  }

  @DisplayName("Test to check add days to a date ")
  @Test
  void addDaysToDate() {
    Date dt = new Date();
    Date dt2 = DateUtil.addDaysToDate(dt, 10);
    /** check the positive uses */
    assertNotEquals(dt, dt2);
    assertNotNull(dt2);
  }

  @DisplayName("Test to check date conversion to a timezone")
  @Test
  void convertDateToTimeZone() {
    Date dt = new Date();
    Date dt2 = DateUtil.convertDateToTimeZone(dt, "UTC");
    /** check the positive uses */
    assertNotNull(dt2);
  }

  @DisplayName("Test to check date and time conversion to a timezone ")
  @Test
  void convertDateAndTimeToTimeZone() {
    Date dt = new Date();
    Date dt2 = DateUtil.convertDateAndTimeToTimeZone(dt, "UTC");
    /** check the positive uses */
    assertNotNull(dt2);
  }

  @DisplayName("Test to get todays date and time by timezone.")
  @Test
  void getTodaysDateWithTimeZone() {
    Date dt = DateUtil.getTodaysDateWithTimeZone("America/New_York");
    /** check the positive uses */
    assertNotNull(dt);
  }

  @DisplayName("Test to get today's end of day by timezone.")
  @Test
  void getEndOfDayWithTimeZone() {
    Date dt = DateUtil.getEndOfDayWithTimeZone("America/New_York");
    /** check the positive uses */
    assertNotNull(dt);
  }

  @DisplayName("Test to get deprecated timezone.")
  @Test
  void getDeprecatedTimezones() {
    List<String> dt = DateUtil.getDeprecatedTimezones();
    /** check the positive uses */
    assertNotNull(dt);
  }

  @DisplayName("Test to get the utc time given all date components ")
  @Test
  void getUTCTimeInDate() {
    Date dt = DateUtil.getUTCTimeInDate(2019, 10, 10, 5, 27, "UTC");
    /** check the positive uses */
    assertNotNull(dt);
  }

  @DisplayName("Test to get the current utc date time stamp ")
  @Test
  void getCurrentUTCTimeStampInDate() {
    Date dt = DateUtil.getCurrentUTCTimeStampInDate();
    /** check the positive uses */
    assertNotNull(dt);
  }

  @DisplayName("Test to convert the date short form ")
  @Test
  void convertDateShortForm() {
    String str = DateUtil.convertDateShortForm(new Date());
    /** positive case */
    assertNotNull(str);

    /** negative case */
    assertEquals("", DateUtil.convertDateShortForm(null));
  }

  @DisplayName("Test to convert the date long form ")
  @Test
  void convertDateLongForm() {
    String str = DateUtil.convertDateLongForm(new Date());
    /** positive case */
    assertNotNull(str);
  }

  @DisplayName("Test to format date.")
  @Test
  void formatDate() {
    String str = DateUtil.formatDate(new Date(), "yy-MM-dd HH:mm:ss");
    /** positive case */
    assertNotNull(str);
  }

  @DisplayName("Test to convert date long form utc ")
  @Test
  void convertDateLongFormUTC() {
    String str = DateUtil.convertDateLongFormUTC(new Date());
    /** positive case */
    assertNotNull(str);
  }

  @DisplayName("Test to get current timestamp in utc")
  @Test
  void getCurrentUTCTimeStampInString() {
    String utcString = DateUtil.getCurrentUTCTimeStampInString();
    /** positive case */
    assertNotNull(utcString);
  }

  @DisplayName("Test to get utc date time from a date ")
  @Test
  void getUTCTimstampFromDate() {
    String utcString = DateUtil.getUTCTimestampFromDate(new Date());
    /** positive case */
    assertNotNull(utcString);
  }

  @DisplayName("Test to get utc date time from all the given components ")
  @Test
  void getUTCTimeInString() {
    String utcString = DateUtil.getUTCTimeInString(2019, 11, 28, 13, 58, "UTC");
    /** positive case */
    assertNotNull(utcString);
  }

  @DisplayName("Test to get utc date time from the millis ")
  @Test
  void testGetUTCTimeInString() {
    Date dt1 = new Date();
    long millis = dt1.getTime();
    String utcString = DateUtil.getUTCTimeInString(millis);
    /** positive case */
    assertNotNull(utcString);
  }

  @DisplayName("Test to get the local time with offset")
  @Test
  void getLocalTimeFormatWithOffset() {
    String dateStr = DateUtil.getLocalTimeFormatWithOffset(2019, 10, 29, 12, 39, "UTC");
    /** positive case */
    assertNotNull(dateStr);
  }

  @DisplayName("Test to subtract hours from current date ")
  @Test
  void minusHoursFromCurrentDate() {
    Date dt = DateUtil.minusHoursFromCurrentDate(4);
    /** positive case */
    assertNotNull(dt);
  }

  @DisplayName("Test to convert date and time given the date and time in strings")
  @Test
  void convertDateAndTime() {
    String dateStr = "2019-11-10";
    String timeStr = "11:46";
    LocalDateTime dt = DateUtil.convertDateAndTime(dateStr, timeStr);
    /** positive case */
    assertNotNull(dt);
  }

  @DisplayName("Test to check difference in mins for date dt1 and date dt2 ")
  @Test
  void getDifferenceInMinutes() {
    Date dt1 = new Date();
    Date dt2 = DateUtil.addMinutesToDate(dt1, 10);
    /** positive case */
    assertNotNull(dt2);
    assertEquals(10, DateUtil.getDifferenceInMinutes(dt1, dt2));
    /** negative case */
    assertNotEquals(20, DateUtil.getDifferenceInMinutes(dt1, dt2));
  }

  @DisplayName("Test to check if date is in valid date format or not")
  @Test
  void isDateFormatValid() {
    String date = "2021-08-05";
    String format = "yyyy-MM-dd";

    assertTrue(DateUtil.isDateFormatValid(date, format));

    format = "dd-MM-yyyy";

    assertFalse(DateUtil.isDateFormatValid(date, format));
  }

  @DisplayName("Test to get ttl in milliseconds")
  @Test
  void getTTLFromNowInMilliSec() {
    Date dt1 = new Date();
    long millis = dt1.getTime();
    Date dt2 = new Date(millis + 86400000);
    Date dt3 = new Date(millis - 864000000);

    assertEquals(
        (double) 86400000, (double) DateUtil.getTTLFromNowInMilliSec(dt2.toString(), 1), 5);
    assertEquals((double) 0, (double) DateUtil.getTTLFromNowInMilliSec("2022-02-01", 1));
  }

  @DisplayName("Test to get decimal by pattern")
  @Test
  void getDecimalByPattern() {
    String decimalInPattern = DateUtil.getDecimalByPattern("000", 1);
    Assertions.assertEquals("001", decimalInPattern);

    String decimalInPattern2 = DateUtil.getDecimalByPattern("00", 2);
    Assertions.assertEquals("02", decimalInPattern2);
  }
}
