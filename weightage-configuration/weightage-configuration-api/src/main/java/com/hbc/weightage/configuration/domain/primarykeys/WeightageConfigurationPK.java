package com.hbc.weightage.configuration.domain.primarykeys;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WeightageConfigurationPK implements Serializable {
  private String orgId;
  private String type;
  private String key;
}
