package com.nextuple.calendar.domain.outbound;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NodeCalendarResponse implements Serializable {

  private String calendarId;
  private String orgId;
  private String nodeId;
  private String effectiveDate;
  private String description;
}
