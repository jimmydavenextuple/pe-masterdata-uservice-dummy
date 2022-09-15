package com.hbc.weightage.configuration.api.domain.dto;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WeightageCacheKeyDto implements Serializable {
  private static final long serialVersionUID = 2756493540868654647L;

  private String orgId;
  private String type;
  private String key;
}
