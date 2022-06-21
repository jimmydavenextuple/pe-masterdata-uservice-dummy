package com.nextuple.weightage.configuration.api.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeightageConfigurationDto {
  private String orgId;
  private String type;
  private String key;
  private Float weightage;
}
