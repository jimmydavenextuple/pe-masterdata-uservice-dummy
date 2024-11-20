/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.jobs.consumers.domain.entity;

import com.nextuple.common.base.BaseEntity;
import com.nextuple.jobs.framework.common.domain.enums.ApiStatusEnum;
import com.nextuple.jobs.framework.common.domain.enums.JobTypeEnum;
import com.nextuple.jobs.framework.common.domain.pojo.JobReferences;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.Version;

@Entity
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "job_records", indexes = @Index(name = "jobId_key", columnList = "job_id"))
public class JobRecordEntity extends BaseEntity {
  // Common fields
  @Id
  @Column(name = "id")
  @GeneratedValue(generator = "system-uuid")
  @UuidGenerator
  private String id;

  @CreatedBy private String createdBy;
  @LastModifiedBy private String updatedBy;

  @Column(name = "deleted")
  private boolean deleted;

  @Version private int documentVersion;

  @Column(name = "job_id")
  private String jobId;

  @Column(name = "record_no")
  private int recordNo;

  @Column(name = "job_type")
  @Enumerated(EnumType.STRING)
  private JobTypeEnum jobType;

  @Column(name = "status")
  @Enumerated(EnumType.STRING)
  private ApiStatusEnum status;

  @Column(name = "request_body")
  @Length(max = 10000)
  private String requestBody;

  @Column(name = "response_body")
  @Length(max = 10000)
  private String responseBody;

  @Column(name = "error_message")
  private String errorMessage;

  @Column(name = "response_time")
  private long responseTime;

  @Column(name = "status_code")
  private int statusCode;

  @Column(name = "org_id")
  private String orgId;

  @Column(name = "user_id")
  private String userId;

  @Column(name = "job_references")
  private JobReferences jobReferences;

  @Column(name = "correlation_id")
  private String correlationId;

  @Column(name = "service_correlation_id")
  private String serviceCorrelationId;

  @Column(name = "response_body_present")
  private Boolean responseBodyPresent;

  @Column(name = "exception")
  private String exception = "None";
}
