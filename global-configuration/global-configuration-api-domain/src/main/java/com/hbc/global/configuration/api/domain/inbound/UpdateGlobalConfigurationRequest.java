package com.hbc.global.configuration.api.domain.inbound;

import java.io.Serializable;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateGlobalConfigurationRequest implements Serializable {

  private static final long serialVersionUID = -8842939820758196823L;

  @NotNull(message = "value can't be null.")
  private String value;
}
