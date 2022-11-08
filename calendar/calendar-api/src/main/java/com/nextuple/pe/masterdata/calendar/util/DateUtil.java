package com.nextuple.pe.masterdata.calendar.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

public class DateUtil {

  private DateUtil() {}

  public static String convertDateShortForm(Date dt1, String timeZone) {
    var val = "";
    if (dt1 != null) {
      DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
      df.setTimeZone(TimeZone.getTimeZone(timeZone));
      val = df.format(dt1);
    }
    return val;
  }

  public static String addDaysToCurrentDate(int toAddDays) {
    var dt = new DateTime();
    var added = dt.withZone(DateTimeZone.forID("UTC")).plusDays(toAddDays);

    return added.toString("yyyy-MM-dd");
  }

  public static int getDayOfWeek(String date) {
    var dt = DateTime.parse(date);
    return dt.getDayOfWeek();
  }
}
