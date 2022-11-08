package com.nextuple.promise.sourcing.rule.api.domain.dto;

import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class PromiseSourcingRuleDto {
  private String orgId;
  private String serviceOption;
  private String destinationGeoZone;
  private Set<String> sourceNodes;
  private int priority;
  private String allocationRuleId;
}
