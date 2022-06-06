package com.nextuple.common.util;

import com.nextuple.controltower.common.constants.FormatterConstants;
import org.joda.time.*;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * This class provides all the helper methods for the date
 *
 * @author Sridhar Kandimalla
 */
public class DateUtil {

  private static final List<String> DEPRECATED_TIMEZONES;

  static {
    DEPRECATED_TIMEZONES =
        Collections.unmodifiableList(
            Arrays.asList(
                "Africa/Asmera",
                "Africa/Timbuktu",
                "America/Argentina/ComodRivadavia",
                "America/Atka",
                "America/Buenos_Aires",
                "America/Catamarca",
                "America/Coral_Harbour",
                "America/Cordoba",
                "America/Ensenada",
                "America/Fort_Wayne",
                "America/Godthab",
                "America/Indianapolis",
                "America/Jujuy",
                "America/Knox_IN",
                "America/Louisville",
                "America/Mendoza",
                "America/Montreal",
                "America/Porto_Acre",
                "America/Rosario",
                "America/Santa_Isabel",
                "America/Shiprock",
                "America/Virgin",
                "Antarctica/South_Pole",
                "Asia/Ashkhabad",
                "Asia/Calcutta",
                "Asia/Chongqing",
                "Asia/Chungking",
                "Asia/Dacca",
                "Asia/Harbin",
                "Asia/Kashgar",
                "Asia/Katmandu",
                "Asia/Macao",
                "Asia/Rangoon",
                "Asia/Saigon",
                "Asia/Tel_Aviv",
                "Asia/Thimbu",
                "Asia/Ujung_Pandang",
                "Asia/Ulan_Bator",
                "Atlantic/Faeroe",
                "Atlantic/Jan_Mayen",
                "Australia/ACT",
                "Australia/Canberra",
                "Australia/LHI",
                "Australia/North",
                "Australia/NSW",
                "Australia/Queensland",
                "Australia/South",
                "Australia/Tasmania",
                "Australia/Victoria",
                "Australia/West",
                "Australia/Yancowinna",
                "Brazil/Acre",
                "Brazil/DeNoronha",
                "Brazil/East",
                "Brazil/West",
                "Canada/Atlantic",
                "Canada/Central",
                "Canada/Eastern",
                "Canada/Mountain",
                "Canada/Newfoundland",
                "Canada/Pacific",
                "Canada/Saskatchewan",
                "Canada/Yukon",
                "CET",
                "Chile/Continental",
                "Chile/EasterIsland",
                "CST6CDT",
                "Cuba",
                "EET",
                "Egypt",
                "Eire",
                "EST",
                "EST5EDT",
                "Etc/Greenwich",
                "Etc/UCT",
                "Etc/Universal",
                "Etc/Zulu",
                "Europe/Belfast",
                "Europe/Tiraspol",
                "GB",
                "GB-Eire",
                "GMT+0",
                "GMT-0",
                "GMT0",
                "Greenwich",
                "Hongkong",
                "HST",
                "Iceland",
                "Iran",
                "Israel",
                "Jamaica",
                "Japan",
                "Kwajalein",
                "Libya",
                "MET",
                "Mexico/BajaNorte",
                "Mexico/BajaSur",
                "Mexico/General",
                "MST",
                "MST7MDT",
                "Navajo",
                "NZ",
                "NZ-CHAT",
                "Pacific/Johnston",
                "Pacific/Ponape",
                "Pacific/Samoa",
                "Pacific/Truk",
                "Pacific/Yap",
                "Poland",
                "Portugal",
                "PRC",
                "PST8PDT",
                "ROC",
                "ROK",
                "Singapore",
                "Turkey",
                "UCT",
                "Universal",
                "US/Alaska",
                "US/Aleutian",
                "US/Arizona",
                "US/Central",
                "US/East-Indiana",
                "US/Eastern",
                "US/Hawaii",
                "US/Indiana-Starke",
                "US/Michigan",
                "US/Mountain",
                "US/Pacific",
                "US/Samoa",
                "W-SU",
                "WET",
                "Zulu"));
  }

  private DateUtil() {
    // Everyone should be using static methods only
  }

  public static List<String> getDeprecatedTimezones() {
    return DEPRECATED_TIMEZONES;
  }

  /**
   * returns true if dt1 is before dt2
   *
   * @param dt1
   * @param dt2
   * @return
   */
  public static boolean isDateBefore(Date dt1, Date dt2) {
    if (dt1 != null && dt2 != null) {
      return dt1.before(dt2);
    }
    return Boolean.FALSE;
  }

  /**
   * returns true if dt1 is after dt2
   *
   * @param dt1
   * @param dt2
   * @return
   */
  public static boolean isDateAfter(Date dt1, Date dt2) {
    if (dt1 != null && dt2 != null) {
      return dt1.after(dt2);
    }
    return Boolean.FALSE;
  }

  /**
   * returns true if dt1 is before today
   *
   * @param dt1
   * @return
   */
  public static boolean isDateBeforeToday(Date dt1) {
    if (dt1 != null) {
      return dt1.before(getDateNow());
    }
    return Boolean.FALSE;
  }

  /**
   * returns true if dt1 is after today
   *
   * @param dt1
   * @return
   */
  public static boolean isDateAfterToday(Date dt1) {
    if (dt1 != null) {
      return dt1.after(getDateNow());
    }
    return Boolean.FALSE;
  }

  /**
   * returns true if dt1 and dt2 belong to the same day. This will ignore the time components
   *
   * @param dt1
   * @param dt2
   * @return
   */
  public static boolean isSameDayIgnoreTime(Date dt1, Date dt2) {
    boolean isSameDay = Boolean.FALSE;
    if (dt1 != null && dt2 != null) {
      String val1 = convertDateShortForm(dt1);
      String val2 = convertDateShortForm(dt2);
      if (val1 != null && val1.equals(val2)) {
        isSameDay = Boolean.TRUE;
      }
    }
    return isSameDay;
  }

  /**
   * returns the current date and time
   *
   * @return
   */
  public static Date getCurrentDate() {
    return new Date();
  }

  /**
   * returns the date for provided dateStr. dateStr should be in yyyy-MM-dd'T'HH:mm:ssZ format
   *
   * @param dateStr
   * @return
   */
  public static Date getDateUTC(String dateStr) {
    return getDateByFormat(dateStr, FormatterConstants.LONG_DATE_FORMAT_UTC);
  }

  /**
   * returns the date for provided dateStr. dateStr should be in yyyy-MM-dd'T'HH:mm:ss'Z' format
   *
   * @param dateStr
   * @return
   */
  public static Date getDateAndTime(String dateStr) {
    return getDateByFormat(dateStr, FormatterConstants.LONG_DATE_FORMAT);
  }

  /**
   * returns the date for provided dateStr. dateStr should be in yyyy-MM-dd format
   *
   * @param dateStr
   * @return
   */
  public static Date getDate(String dateStr) {
    return getDateByFormat(dateStr, FormatterConstants.SHORT_DATE_FORMAT);
  }

  /**
   * returns the current date and time
   *
   * @return
   */
  public static Date getDateNow() {
    Calendar time = Calendar.getInstance();
    time.add(Calendar.MILLISECOND, -time.getTimeZone().getOffset(time.getTimeInMillis()));
    return time.getTime();
  }

  /**
   * returns current date with the minutes provided added
   *
   * @param toAddMinutes
   * @return
   */
  public static Date addMinutesToCurrentDate(int toAddMinutes) {
    DateTime dt = new DateTime();
    DateTime added = dt.plusMinutes(toAddMinutes);

    return added.toDate();
  }

  /**
   * returns current date with the hours provided added
   *
   * @param toAddHours
   * @return
   */
  public static Date addHoursToCurrentDate(int toAddHours) {
    DateTime dt = new DateTime();
    DateTime added = dt.plusHours(toAddHours);

    return added.toDate();
  }

  /**
   * returns current date with the days provided added
   *
   * @param toAddDays
   * @return
   */
  public static Date addDaysToCurrentDate(int toAddDays) {
    DateTime dt = new DateTime();
    DateTime added = dt.plusDays(toAddDays);

    return added.toDate();
  }

  /**
   * returns the date with the minutes provided added to the date provided
   *
   * @param toAddMinutes
   * @return
   */
  public static Date addMinutesToDate(Date dt, Integer toAddMinutes) {
    if (Objects.isNull(toAddMinutes)) {
      return dt;
    }
    DateTime jodaTime = new DateTime(dt);
    DateTime added = jodaTime.plusMinutes(toAddMinutes);

    return added.toDate();
  }

  /**
   * returns the date with the hours provided added to the date provided
   *
   * @param toAddHours
   * @return
   */
  public static Date addHoursToDate(Date dt, int toAddHours) {
    DateTime jodaTime = new DateTime(dt);
    DateTime added = jodaTime.plusHours(toAddHours);

    return added.toDate();
  }

  /**
   * returns the date with the days provided added to the date provided
   *
   * @param toAddDays
   * @return
   */
  public static Date addDaysToDate(Date dt, int toAddDays) {
    DateTime jodaTime = new DateTime(dt);
    DateTime added = jodaTime.plusDays(toAddDays);

    return added.toDate();
  }

  /**
   * converts the date into the provided format
   *
   * @param dateStr
   * @param formatStr
   * @return
   */
  public static Date getDateByFormat(String dateStr, String formatStr) {
    Date returnDate = null;
    if (dateStr != null) {
      DateFormat format = new SimpleDateFormat(formatStr);
      try {
        returnDate = format.parse(dateStr);
      } catch (ParseException pe) {
        // do nothing
      }
    }

    return returnDate;
  }

  /**
   * Format input date as per give date format
   *
   * @param inputDate
   * @param dateFormat
   * @return
   */
  public static String formatDate(Date inputDate, String dateFormat) {
    DateFormat format = new SimpleDateFormat(dateFormat);
    return format.format(inputDate);
  }

  /**
   * returns the date without time converted into the provided timezone
   *
   * @param date
   * @param timeZone
   * @return
   */
  public static Date convertDateToTimeZone(Date date, String timeZone) {
    SimpleDateFormat sdf = new SimpleDateFormat(FormatterConstants.LONG_DATE_FORMAT);
    sdf.setTimeZone(TimeZone.getTimeZone(timeZone));
    return getDateByFormat(sdf.format(date), FormatterConstants.SHORT_DATE_FORMAT);
  }

  /**
   * returns the date with time converted into the provided timezone
   *
   * @param date
   * @param timeZone
   * @return
   */
  public static Date convertDateAndTimeToTimeZone(Date date, String timeZone) {
    DateTime dt = new DateTime(date);
    DateTimeZone dtZone = DateTimeZone.forID(timeZone);
    DateTime dtus = dt.withZone(dtZone);
    return dtus.toLocalDateTime().toDate();
  }

  /**
   * returns date in the provided timezone
   *
   * @param year
   * @param month
   * @param day
   * @param hours
   * @param minutes
   * @param timeZone
   * @return
   */
  public static Date getUTCTimeInDate(
      int year, int month, int day, int hours, int minutes, String timeZone) {
    DateTime dt = new DateTime(year, month, day, hours, minutes, DateTimeZone.forID(timeZone));
    return dt.toDate();
  }

  /**
   * returns current utc time in Date
   *
   * @return
   */
  public static Date getCurrentUTCTimeStampInDate() {
    DateFormat dateFormat = new SimpleDateFormat(FormatterConstants.LONG_DATE_FORMAT);
    dateFormat.setTimeZone(TimeZone.getTimeZone(FormatterConstants.UTC_FORMAT));
    return getDateByFormat(dateFormat.format(new Date()), FormatterConstants.LONG_DATE_FORMAT);
  }

  public static Date convertZoneDateTime(String date, String time, String zone) {
    java.time.LocalDate localDate = java.time.LocalDate.parse(date);
    java.time.LocalTime localTime = java.time.LocalTime.parse(time);
    ZoneId timeZone = ZoneId.of(zone);
    ZonedDateTime zonedDateTime = ZonedDateTime.of(localDate, localTime, timeZone);
    return Date.from(java.time.Instant.from(zonedDateTime));
  }

  public static Date getTodaysDateWithTimeZone(String zone) {
    return convertZoneDateTime(DateUtil.convertDateShortForm(new Date()), "00:00:00", zone);
  }

  public static Date getEndOfDayWithTimeZone(String zone) {
    return convertZoneDateTime(DateUtil.convertDateShortForm(new Date()), "23:59:59", zone);
  }

  /**
   * returns string in yyyy-MM-dd for the provided date
   *
   * @param dt1
   * @return
   */
  public static String convertDateShortForm(Date dt1) {
    String val = "";
    if (dt1 != null) {
      DateFormat df = new SimpleDateFormat(FormatterConstants.SHORT_DATE_FORMAT);
      val = df.format(dt1);
    }
    return val;
  }

  /**
   * returns string in yyyy-MM-dd'T'HH:mm:ssZ for the provided date
   *
   * @param dt1
   * @return
   */
  public static String convertDateLongForm(Date dt1) {
    String val = "";
    if (dt1 != null) {
      DateFormat df = new SimpleDateFormat(FormatterConstants.LONG_DATE_FORMAT);
      val = df.format(dt1);
    }
    return val;
  }

  /**
   * returns string in yyyy-MM-dd'T'HH:mm:ss'Z' for the provided date
   *
   * @param dt1
   * @return
   */
  public static String convertDateLongFormUTC(Date dt1) {
    String val = "";
    if (dt1 != null) {
      DateFormat df = new SimpleDateFormat(FormatterConstants.LONG_DATE_FORMAT_UTC);
      df.setTimeZone(TimeZone.getTimeZone(FormatterConstants.UTC_FORMAT));
      val = df.format(dt1);
    }
    return val;
  }

  /**
   * returns string in yyyy-MM-dd
   *
   * @return
   */
  public static String getCurrentUTCTimeStampInString() {
    DateFormat dateFormat = new SimpleDateFormat(FormatterConstants.LONG_DATE_FORMAT);
    dateFormat.setTimeZone(TimeZone.getTimeZone(FormatterConstants.UTC_FORMAT));
    return dateFormat.format(new Date());
  }

  /**
   * returns string in yyyy-MM-dd'T'HH:mm:ssZ for the provided date
   *
   * @param date
   * @return
   */
  public static String getUTCTimestampFromDate(Date date) {
    DateFormat dateFormat = new SimpleDateFormat(FormatterConstants.LONG_DATE_FORMAT);
    dateFormat.setTimeZone(TimeZone.getTimeZone(FormatterConstants.UTC_FORMAT));
    return dateFormat.format(date);
  }

  /**
   * returns string in yyyy-MM-dd'T'HH:mm:ssZ for the provided data
   *
   * @param year
   * @param month
   * @param day
   * @param hours
   * @param minutes
   * @param timeZone
   * @return
   */
  public static String getUTCTimeInString(
      int year, int month, int day, int hours, int minutes, String timeZone) {
    DateTime dt = new DateTime(year, month, day, hours, minutes, DateTimeZone.forID(timeZone));
    DateFormat dateFormat = new SimpleDateFormat(FormatterConstants.LONG_DATE_FORMAT);
    dateFormat.setTimeZone(TimeZone.getTimeZone(FormatterConstants.UTC_FORMAT));
    return dateFormat.format(dt.toDate());
  }

  /**
   * returns string in yyyy-MM-dd'T'HH:mm:ssZ for the provided time in long
   *
   * @param time
   * @return
   */
  public static String getUTCTimeInString(long time) {
    DateTime dt = new DateTime(time);
    DateFormat dateFormat = new SimpleDateFormat(FormatterConstants.LONG_DATE_FORMAT);
    dateFormat.setTimeZone(TimeZone.getTimeZone(FormatterConstants.UTC_FORMAT));
    return dateFormat.format(dt.toDate());
  }

  /**
   * returns string in yyyy-MM-dd'T'HH:mm:ssZ in the timezone for the provided data
   *
   * @param year
   * @param month
   * @param day
   * @param hours
   * @param minutes
   * @param timeZone
   * @return
   */
  public static String getLocalTimeFormatWithOffset(
      int year, int month, int day, int hours, int minutes, String timeZone) {
    DateTime dt = new DateTime(year, month, day, hours, minutes, DateTimeZone.forID(timeZone));
    DateFormat dateFormat = new SimpleDateFormat(FormatterConstants.LONG_DATE_FORMAT);
    dateFormat.setTimeZone(TimeZone.getTimeZone(timeZone));
    return dateFormat.format(dt.toDate());
  }

  /**
   * returns current date with the hours subtracted
   *
   * @param minusHours
   * @return
   */
  public static Date minusHoursFromCurrentDate(int minusHours) {
    DateTime dt = new DateTime();
    DateTime added = dt.minusHours(minusHours);

    return added.toDate();
  }

  /**
   * returns Date Time for the date and time provided
   *
   * @param date of the format YYYY-MM-DD
   * @param time of format HH:MM
   * @return
   */
  public static LocalDateTime convertDateAndTime(String date, String time) {
    LocalDate datePart = LocalDate.parse(date);
    LocalTime timePart = LocalTime.parse(time);

    return new LocalDateTime(
        datePart.getYear(),
        datePart.getMonthOfYear(),
        datePart.getDayOfMonth(),
        timePart.getHourOfDay(),
        timePart.getMinuteOfHour());
  }

  /**
   * calculates the time difference between two dates
   *
   * @param d1
   * @param d2
   * @return
   */
  public static long getDifferenceInMinutes(Date d1, Date d2) {
    long val = 0;
    if (d1 != null && d2 != null) {
      long diff = d2.getTime() - d1.getTime(); // as given

      val = TimeUnit.MILLISECONDS.toMinutes(diff);
    }
    return val;
  }

  /**
   * Check if the given date is in valid format or not
   *
   * @param date
   * @param format
   * @return
   */
  public static boolean isDateFormatValid(String date, String format) {
    DateFormat sdf = new SimpleDateFormat(format);
    sdf.setLenient(false);

    try {
      sdf.parse(date);
    } catch (ParseException e) {
      // Date format is invalid
      return Boolean.FALSE;
    }

    // Return true if date format is valid
    return Boolean.TRUE;
  }

  /**
   * Return time to live in milliseconds, input days from input date
   *
   * @param date as reference date to calculate ttl, must be in "yyyy-mm-dd" format
   * @param daysToLive as number of days after reference date to be counted in ttl
   * @return time to live in milliseconds
   */
  public static long getTTLFromNowInMilliSec(String date, int daysToLive) {
    Date ttlDate = DateUtil.addDaysToDate(DateUtil.getDate(date), daysToLive);
    Date currentDate = DateUtil.getCurrentDate();
    long ttl = ttlDate.getTime() - currentDate.getTime();
    if (ttl < 0) return 0;
    return ttl;
  }

  /**
   * Returns String for the given decimal for pattern
   *
   * @param pattern
   * @param decimal
   * @return
   */
  public static String getDecimalByPattern(String pattern, int decimal) {
    DecimalFormat formatter = new DecimalFormat(pattern);

    return formatter.format(decimal);
  }
}
