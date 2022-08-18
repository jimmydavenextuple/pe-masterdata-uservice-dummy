package com.hbc.global.configuration.domain.primarykeys;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GlobalConfigurationPK implements Serializable {
  private String orgId;
  private String type;
  private String key;
}
