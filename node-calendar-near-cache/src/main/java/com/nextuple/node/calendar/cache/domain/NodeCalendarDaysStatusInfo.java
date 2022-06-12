package com.nextuple.node.calendar.cache.domain;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NodeCalendarDaysStatusInfo implements Serializable {

  private String date;
  private Boolean isActive;
}
