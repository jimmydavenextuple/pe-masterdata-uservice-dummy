package com.nextuple.weightage.configuration.domain.inbound;

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
public class FetchWeightageRequest {
  @NotBlank(message = "orgId can't be empty.")
  private String orgId;

  @NotBlank(message = "type can't be empty.")
  private String type;

  @NotEmpty(message = "keys can't be empty.")
  private List<String> keys;
}
