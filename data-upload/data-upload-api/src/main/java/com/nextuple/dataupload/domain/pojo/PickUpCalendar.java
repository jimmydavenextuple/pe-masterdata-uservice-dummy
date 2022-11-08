package com.nextuple.dataupload.domain.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PickUpCalendar {

  private String nodeId;
  private String carrierServiceId;
  private String calendarId;
}
