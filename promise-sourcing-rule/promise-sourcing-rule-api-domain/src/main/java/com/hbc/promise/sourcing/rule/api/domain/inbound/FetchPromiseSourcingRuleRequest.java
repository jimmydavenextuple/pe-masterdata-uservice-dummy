package com.hbc.promise.sourcing.rule.api.domain.inbound;

import java.io.Serializable;
import java.util.List;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class FetchPromiseSourcingRuleRequest implements Serializable {
  private static final long serialVersionUID = -48664604888463354L;

  @NotEmpty(message = "serviceOptions can't be empty.")
  private List<String> serviceOptions;

  @NotBlank(message = "orgId can't be empty.")
  private String orgId;

  @NotBlank(message = "destinationGeoZone can't be empty.")
  private String destinationGeoZone;

  @NotBlank(message = "allocationRuleId can't be empty.")
  private String allocationRuleId;
}
