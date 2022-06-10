package com.nextuple.pe.masterdata.calendar.domain.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CalendarDaysStatusInfo implements Serializable {

  private String date;
  private Boolean isActive;
}
