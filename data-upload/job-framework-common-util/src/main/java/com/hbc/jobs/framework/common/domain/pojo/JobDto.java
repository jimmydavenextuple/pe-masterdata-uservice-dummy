package com.hbc.jobs.framework.common.domain.pojo;

import com.hbc.jobs.framework.common.domain.enums.JobStatusEnum;
import com.hbc.jobs.framework.common.domain.enums.JobTypeEnum;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import lombok.Data;

@Data
public class JobDto implements Serializable {

  private static final long serialVersionUID = 2806054788561367739L;
  private String jobId;
  private String fileName;
  private byte[] file;
  private Integer totalRecords;
  private JobTypeEnum jobType;
  private Integer processedRecords;
  private Integer remainingRecords;
  private Integer failureCount;
  private Integer successCount;
  private JobStatusEnum status;
  private String userId;
  private String orgId;
  private Date processingStartedAt;
  private Metadata metadata;
  private List<AuditLog> auditLog;
  private String errorMessage;
}
