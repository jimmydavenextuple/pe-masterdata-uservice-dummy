package com.nextuple.jobs.framework.common.domain.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProcessingTimeBufferUpload {

  private String orgId;
  private String nodeId;
  private String serviceOption;
  private String bufferHours;
  private String bufferStartDate;
  private String bufferEndDate;
}
