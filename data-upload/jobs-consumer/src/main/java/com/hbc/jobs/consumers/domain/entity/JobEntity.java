package com.hbc.jobs.consumers.domain.entity;

import com.hbc.common.base.BaseEntity;
import com.hbc.jobs.framework.common.domain.enums.JobStatusEnum;
import com.hbc.jobs.framework.common.domain.enums.JobTypeEnum;
import com.hbc.jobs.framework.common.domain.pojo.AuditLog;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.TypeDef;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@DynamicInsert
@Table(name = "jobs", indexes = @Index(name = "job_id", columnList = "job_id"))
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
public class JobEntity extends BaseEntity {

  @Id
  @Column(name = "job_id")
  private String jobId;

  @Column(name = "org_id")
  private String orgId;

  @Column(name = "status")
  @Enumerated(EnumType.STRING)
  private JobStatusEnum status;

  @Column(name = "total_records")
  private int totalRecords;

  @Column(name = "processed_records")
  private int processedRecords;

  @Column(name = "remaining_records")
  private int remainingRecords;

  @Column(name = "job_type")
  @Enumerated(EnumType.STRING)
  private JobTypeEnum jobType;

  @Column(name = "failure_count")
  private int failureCount;

  @Column(name = "success_count")
  private int successCount;

  @Column(name = "user_id")
  private String userId;

  @Column(name = "audit_log")
  private AuditLog[] auditLog;
}
