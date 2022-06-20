package com.nextuple.promise.sourcing.rule.domain.inbound;

import java.io.Serializable;
import java.util.Set;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class CreatePromiseSourcingRuleRequest implements Serializable {
  private static final long serialVersionUID = 8787181446707914255L;

  @NotBlank(message = "serviceOptionEnum can't be empty.")
  private String serviceOption;

  @NotBlank(message = "orgId can't be empty.")
  private String orgId;

  @NotBlank(message = "destinationGeoZone can't be empty.")
  private String destinationGeoZone;

  @NotEmpty(message = "sourceNodes can't be empty.")
  @Size(max = 1000, message = "sourceNodes' size should not be more than 1000.")
  private Set<String> sourceNodes;

  @NotNull(message = "priority can't be null.")
  @Min(value = 0, message = "priority should be more than 0.")
  private int priority;

  private String allocationRuleId;
}
