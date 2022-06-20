package com.nextuple.sourcing.rule.cache.domain;

import java.util.List;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FetchPromiseSourcingRuleRequest {

  @NotEmpty(message = "serviceOptions can't be empty.")
  private List<String> serviceOptions;

  @NotBlank(message = "orgId can't be empty.")
  private String orgId;

  @NotBlank(message = "destinationGeoZone can't be empty.")
  private String destinationGeoZone;

  @NotBlank(message = "allocationRuleId can't be empty.")
  private String allocationRuleId;
}
