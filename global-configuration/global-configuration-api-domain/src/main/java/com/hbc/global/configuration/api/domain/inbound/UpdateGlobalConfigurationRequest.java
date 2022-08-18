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
public class UpdateGlobalConfigurationRequest implements Serializable {

  private static final long serialVersionUID = -8842939820758196823L;

  @NotNull(message = "value can't be null.")
  private String value;
}
