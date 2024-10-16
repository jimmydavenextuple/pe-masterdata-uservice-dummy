package com.nextuple.pe.masterdata.calendar.domain;

import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NodeCarrierServiceCalendarDto {
  private String orgId;
  private Optional<String> nodeId;
  private Optional<String> carrierServiceId;
  private Optional<String> serviceOption;
  private Optional<Integer> numberOfDaysInFuture;
  private Optional<String> shippingStage;
  private Optional<String> fromDate;
}
