/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.pe.masterdata.calendar.util;

import com.nextuple.calendar.domain.pojo.ExceptionDays;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DateValidation {

  public boolean validateDate(String date) {
    var valid = Boolean.FALSE;
    try {
      LocalDate.parse(
          date, DateTimeFormatter.ofPattern("uuuu-M-d").withResolverStyle(ResolverStyle.STRICT));
      valid = Boolean.TRUE;
    } catch (DateTimeParseException e) {
      valid = Boolean.FALSE;
    }
    return valid;
  }

  public boolean validateExceptionDays(List<ExceptionDays> exceptionDays) {
    var valid = Boolean.FALSE;
    for (var i = 0; i < exceptionDays.size(); i++) {
      try {
        if (Objects.nonNull(exceptionDays.get(i).getDate())) {
          LocalDate.parse(
              exceptionDays.get(i).getDate(),
              DateTimeFormatter.ofPattern("uuuu-M-d").withResolverStyle(ResolverStyle.STRICT));
          valid = Boolean.TRUE;
        }
      } catch (DateTimeParseException e) {
        valid = Boolean.FALSE;
        break;
      }
    }
    return valid;
  }
}
