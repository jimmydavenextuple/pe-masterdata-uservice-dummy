package com.hbc.pe.masterdata.calendar.util;

import com.hbc.calendar.domain.pojo.ExceptionDays;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Date;
import java.util.List;
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

  public static boolean validateDate(String date) {
    boolean valid = false;
    try {
      LocalDate.parse(
          date, DateTimeFormatter.ofPattern("uuuu-M-d").withResolverStyle(ResolverStyle.STRICT));
      valid = true;
    } catch (DateTimeParseException e) {
      valid = false;
    }
    return valid;
  }

  public static boolean validateExceptionDays(List<ExceptionDays> exceptionDays) {
    boolean valid = false;
    for (int i = 0; i < exceptionDays.size(); i++) {
      try {
        LocalDate.parse(
            exceptionDays.get(i).getDate(),
            DateTimeFormatter.ofPattern("uuuu-M-d").withResolverStyle(ResolverStyle.STRICT));
        valid = true;
      } catch (DateTimeParseException e) {
        valid = false;
        break;
      }
    }
    return valid;
  }
}
