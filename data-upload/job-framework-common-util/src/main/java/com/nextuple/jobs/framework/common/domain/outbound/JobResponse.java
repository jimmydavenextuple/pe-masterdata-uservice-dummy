package com.nextuple.jobs.framework.common.domain.outbound;

import com.nextuple.jobs.framework.common.domain.enums.JobStatusEnum;
import com.nextuple.jobs.framework.common.domain.enums.JobTypeEnum;
import com.nextuple.jobs.framework.common.domain.pojo.AuditLog;
import com.nextuple.jobs.framework.common.domain.pojo.Metadata;
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
  private JobTypeEnum jobType;
  private JobStatusEnum status;
  private Date processingStartedAt;
  private Integer processedRecords;
  private Integer remainingRecords;
  private Integer failureCount;
  private Integer successCount;
  private Integer totalRecords;
  private String userId;
  private String orgId;
  private Metadata metadata;
  private List<AuditLog> auditLog;
  private String errorMessage;
}
