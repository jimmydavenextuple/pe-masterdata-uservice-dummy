package com.nextuple.calendar.domain.dto;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CarrierCalendarCacheKeyDto implements Serializable {
  private static final long serialVersionUID = -1961916076991906613L;

  private String carrierServiceId;
  private String orgId;
  private String serviceOption;
}
