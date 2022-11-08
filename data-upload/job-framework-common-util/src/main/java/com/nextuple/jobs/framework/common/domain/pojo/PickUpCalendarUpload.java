package com.nextuple.jobs.framework.common.domain.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PickUpCalendarUpload {

  private String action;
  private String calendarId;
  private String orgId;
  private String nodeId;
  private String carrierServiceId;
  private String description;
  private String effectiveDate;
}
