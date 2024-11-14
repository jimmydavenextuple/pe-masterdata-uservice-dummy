/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.jobs.consumers.domain.entity;

import com.nextuple.common.base.BaseEntity;
import com.nextuple.jobs.framework.common.domain.enums.JobStatusEnum;
import com.nextuple.jobs.framework.common.domain.enums.JobTypeEnum;
import com.nextuple.jobs.framework.common.domain.pojo.AuditLog;
import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import java.util.Date;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.Type;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@DynamicInsert
@Table(
    name = "jobs",
    indexes = {
      @Index(name = "status_key", columnList = "status"),
      @Index(name = "orgId_jobId", columnList = "org_id,job_id")
    })
public class JobEntity extends BaseEntity {

  @Id
  @Column(name = "job_id")
  private String jobId;

  @Column(name = "org_id")
  private String orgId;

  @Column(name = "processing_started_at")
  private Date processingStartedAt;

  @Column(name = "file_name")
  private String fileName;

  @Column(name = "file")
  @Lob
  private byte[] file;

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

  @Type(JsonBinaryType.class)
  @Column(columnDefinition = "jsonb", name = "audit_log")
  private List<AuditLog> auditLog;

  @Column(name = "error_message")
  private String errorMessage;

  @Column(name = "file_metadata_id")
  private Long fileMetaDataId;
}
