package com.nextuple.pe.masterdata.calendar.util;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class DateUtil {

  public static String convertDateShortForm(Date dt1, String timeZone) {
    String val = "";
    if (dt1 != null) {
      DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
      df.setTimeZone(TimeZone.getTimeZone(timeZone));
      val = df.format(dt1);
    }
    return val;
  }

  public static String addDaysToCurrentDate(int toAddDays, String timezone) {
    DateTime dt = new DateTime();
    DateTime added = dt.withZone(DateTimeZone.forID(timezone)).plusDays(toAddDays);

    return added.toString("yyyy-MM-dd");
  }

  public static int getDayOfWeek(String date) {
    DateTime dt = DateTime.parse(date);
    return dt.getDayOfWeek();
  }
}
