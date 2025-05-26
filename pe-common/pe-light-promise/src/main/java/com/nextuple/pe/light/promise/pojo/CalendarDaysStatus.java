package com.nextuple.pe.light.promise.pojo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class CalendarDaysStatus {

  @Schema(description = "Date for which status needs to be checked", example = "2023-01-01")
  private String date;

  @Schema(
      description = "Flag indicating if the calendar day is considered as a working day",
      example = "true")
  private Boolean isActive;
}
