package com.nextuple.jobs.framework.common.domain.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.nextuple.jobs.framework.common.domain.enums.JobTypeEnum;
import com.nextuple.jobs.framework.common.domain.enums.RecordDataTypeEnum;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RecordDto implements Serializable {

  private static final long serialVersionUID = -5877870236448573862L;
  private Integer recordId;
  private String recordData;
  private RecordInputDto inputs;
  private String jobId;
  private String orgId;
  private JobTypeEnum jobType;
  private Integer totalRecords;
  private RecordDataTypeEnum recordType;
}
