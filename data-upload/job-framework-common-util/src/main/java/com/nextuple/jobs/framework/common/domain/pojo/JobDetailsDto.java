package com.nextuple.jobs.framework.common.domain.pojo;

import com.nextuple.jobs.framework.common.domain.enums.JobStatusEnum;
import com.nextuple.jobs.framework.common.domain.enums.JobTypeEnum;
import java.io.Serializable;
import lombok.Data;

@Data
public class JobDetailsDto implements Serializable {

  private static final long serialVersionUID = 8688346380073390592L;
  private String jobId;
  private Integer totalRecords;
  private JobTypeEnum jobType;
  private JobStatusEnum status;
  private String orgId;
}
