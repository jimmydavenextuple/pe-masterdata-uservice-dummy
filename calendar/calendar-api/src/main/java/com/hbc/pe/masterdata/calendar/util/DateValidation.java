package com.hbc.pe.masterdata.calendar.util;

import com.hbc.calendar.domain.pojo.ExceptionDays;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DateValidation {

  public boolean validateDate(String date) {
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

  public boolean validateExceptionDays(List<ExceptionDays> exceptionDays) {
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
