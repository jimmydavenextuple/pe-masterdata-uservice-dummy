package com.nextuple.carrier.calendar.cache.domain;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CarrierCalendarDaysStatusInfo implements Serializable {

  private String date;
  private Boolean isActive;
}
