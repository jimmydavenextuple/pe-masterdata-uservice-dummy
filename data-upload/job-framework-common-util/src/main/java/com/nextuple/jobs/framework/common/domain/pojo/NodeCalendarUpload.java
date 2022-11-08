package com.nextuple.jobs.framework.common.domain.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NodeCalendarUpload {

  private String action;
  private String calendarId;
  private String nodeId;
  private String orgId;
  private String effectiveDate;
  private String description;
}
