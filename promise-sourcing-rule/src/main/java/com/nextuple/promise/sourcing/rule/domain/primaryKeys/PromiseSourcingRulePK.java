package com.nextuple.promise.sourcing.rule.domain.primaryKeys;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class PromiseSourcingRulePK implements Serializable {
  private String orgId;
  private String serviceOption;
  private String destinationGeoZone;
  private String allocationRuleId;
  private int priority;
}
