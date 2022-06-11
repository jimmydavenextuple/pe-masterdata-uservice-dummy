package com.nextuple.weightage.configuration.domain.inbound;

import java.io.Serializable;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class CreateWeightageConfigurationRequest implements Serializable {
  private static final long serialVersionUID = -2330679446163300146L;

  @NotBlank(message = "orgId can't be empty.")
  private String orgId;

  @NotBlank(message = "type can't be empty.")
  private String type;

  @NotBlank(message = "key can't be empty.")
  private String key;

  @NotNull(message = "weightage can't be null.")
  private Float weightage;
}
