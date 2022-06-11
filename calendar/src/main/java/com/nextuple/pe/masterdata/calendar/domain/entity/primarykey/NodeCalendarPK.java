package com.nextuple.pe.masterdata.calendar.domain.entity.primarykey;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NodeCalendarPK implements Serializable {

  private String calendarId;
  private String orgId;
  private String nodeId;
  private String effectiveDate;
}
