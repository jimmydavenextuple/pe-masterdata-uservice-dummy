package com.hbc.csvdownload.domain.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProcessingLeadTime {

  private String nodeId;
  private String orgId;
  private String serviceOption;
  private int processingTime;
}
