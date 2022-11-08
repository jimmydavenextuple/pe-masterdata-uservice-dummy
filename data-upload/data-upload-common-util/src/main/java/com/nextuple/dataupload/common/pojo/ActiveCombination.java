package com.nextuple.dataupload.common.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ActiveCombination {

  private String nodeId;
  private String carrierServiceId;
  private String serviceOption;
  private boolean isActive;
}
