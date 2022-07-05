package com.hbc.promise.sourcing.rule.api.domain.inbound;

import java.io.Serializable;
import java.util.Set;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdatePromiseSourcingRuleRequest implements Serializable {
  private static final long serialVersionUID = 8787181446707914255L;

  @NotEmpty(message = "sourceNodes can't be empty.")
  @Size(max = 1000, message = "sourceNodes' size should not be more than 1000.")
  private Set<String> sourceNodes;
}
