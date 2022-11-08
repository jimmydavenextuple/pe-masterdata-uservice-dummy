package com.nextuple.csvdownload.common.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProcessingLeadTime {

  private String nodeId;
  private String orgId;
  private String serviceOption;
  private String carrierServiceId;
  private Double processingTime;
  private String actionType;
}
