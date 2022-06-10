package com.nextuple.pe.masterdata.calendar.domain.entity.primarykey;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NodeCarrierServiceCalendarPK implements Serializable {

  private String calendarId;
  private String orgId;
  private String nodeId;
  private String carrierServiceId;
  private String effectiveDate;
}
