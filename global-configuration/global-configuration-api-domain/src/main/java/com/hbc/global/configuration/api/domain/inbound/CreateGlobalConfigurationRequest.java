package com.hbc.global.configuration.api.domain.inbound;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
public class CreateGlobalConfigurationRequest implements Serializable {

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
