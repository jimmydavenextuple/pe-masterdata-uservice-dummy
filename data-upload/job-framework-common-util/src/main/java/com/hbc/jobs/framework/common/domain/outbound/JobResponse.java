package com.hbc.jobs.framework.common.domain.outbound;

import com.hbc.jobs.framework.common.domain.enums.JobStatusEnum;
import com.hbc.jobs.framework.common.domain.enums.JobTypeEnum;
import com.hbc.jobs.framework.common.domain.pojo.AuditLog;
import com.hbc.jobs.framework.common.domain.pojo.Metadata;
import java.util.Date;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JobResponse {

  private String jobId;
  private String fileName;
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
