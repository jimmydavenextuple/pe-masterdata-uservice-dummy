package com.hbc.calendar.domain.outbound;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CarrierServiceCalendarResponse {

  private String calendarId;
  private String orgId;
  private String carrierServiceId;
  private String shippingStage;
  private String effectiveDate;
  private String description;
}
