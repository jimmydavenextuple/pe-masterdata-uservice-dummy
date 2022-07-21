package com.hbc.pe.masterdata.calendar.util;

import com.hbc.calendar.domain.pojo.ExceptionDays;
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
