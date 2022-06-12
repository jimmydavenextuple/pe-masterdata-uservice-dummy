package com.nextuple.node.carrier.calendar.cache.domain;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NodeCarrierCalendarDaysStatusInfo implements Serializable {

  private String date;
  private Boolean isActive;
}
