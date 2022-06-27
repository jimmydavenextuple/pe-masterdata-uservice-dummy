package com.hbc.weightage.configuration.domain.primaryKeys;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class WeightageConfigurationPK implements Serializable {
  private String orgId;
  private String type;
  private String key;
}
