package com.nextuple.pe.masterdata.calendar.domain.entity.primarykey;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CalendarPK implements Serializable {

  private String calendarId;
  private String orgId;
}
