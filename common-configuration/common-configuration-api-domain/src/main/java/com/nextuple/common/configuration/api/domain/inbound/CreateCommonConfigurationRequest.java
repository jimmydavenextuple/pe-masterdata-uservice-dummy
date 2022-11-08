package com.nextuple.common.configuration.api.domain.inbound;

import java.io.Serializable;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class CreateCommonConfigurationRequest implements Serializable {

  private static final long serialVersionUID = -3230582761476398092L;

  @NotBlank(message = "orgId can't be empty.")
  private String orgId;

  @NotBlank(message = "type can't be empty.")
  private String type;

  @NotBlank(message = "key can't be empty.")
  private String key;

  @NotNull(message = "value can't be null.")
  private String value;
}
