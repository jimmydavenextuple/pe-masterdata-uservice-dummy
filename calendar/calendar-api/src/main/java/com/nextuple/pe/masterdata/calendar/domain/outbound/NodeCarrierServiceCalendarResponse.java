package com.nextuple.pe.masterdata.calendar.domain.outbound;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NodeCarrierServiceCalendarResponse {

  private String calendarId;
  private String orgId;
  private String nodeId;
  private String carrierServiceId;
  private String effectiveDate;
  private String description;
}
