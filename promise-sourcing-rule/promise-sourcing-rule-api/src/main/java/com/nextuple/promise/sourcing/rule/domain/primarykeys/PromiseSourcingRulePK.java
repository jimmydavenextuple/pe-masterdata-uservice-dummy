package com.nextuple.promise.sourcing.rule.domain.primarykeys;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PromiseSourcingRulePK implements Serializable {
  private String orgId;
  private String serviceOption;
  private String destinationGeoZone;
  private String allocationRuleId;
  private int priority;
}
