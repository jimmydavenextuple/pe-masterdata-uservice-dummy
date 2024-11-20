/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.pe.masterdata.calendar.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;
import java.util.TimeZone;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

public class DateUtil {
  public static final String SIMPLE_DATE_FORMAT = "yyyy-MM-dd";

  private DateUtil() {}

  public static String convertDateShortForm(Date dt1, String timeZone) {
    var val = "";
    if (dt1 != null) {
      DateFormat df = new SimpleDateFormat(SIMPLE_DATE_FORMAT);
      df.setTimeZone(TimeZone.getTimeZone(timeZone));
      val = df.format(dt1);
    }
    return val;
  }

  public static String addDaysToCurrentDate(int toAddDays) {
    var dt = new DateTime();
    var added = dt.withZone(DateTimeZone.forID("UTC")).plusDays(toAddDays);

    return added.toString(SIMPLE_DATE_FORMAT);
  }

  public static String addDaysToGivenDate(Optional<String> fromDate, int toAddDays) {
    if (Objects.isNull(fromDate) || fromDate.isEmpty()) return addDaysToCurrentDate(toAddDays);
    var added = new DateTime(fromDate.get()).plusDays(toAddDays);
    return added.toString(SIMPLE_DATE_FORMAT);
  }

  public static int getDayOfWeek(String date) {
    var dt = DateTime.parse(date);
    return dt.getDayOfWeek();
  }
}
