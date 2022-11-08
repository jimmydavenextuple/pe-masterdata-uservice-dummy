package com.nextuple.jobs.consumers.domain.entity;

import com.nextuple.common.base.BaseEntity;
import com.nextuple.jobs.framework.common.domain.enums.ApiStatusEnum;
import com.nextuple.jobs.framework.common.domain.enums.JobTypeEnum;
import com.nextuple.jobs.framework.common.domain.pojo.JobReferences;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.TypeDef;
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
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
public class JobRecordEntity extends BaseEntity {
  // Common fields
  @Id
  @Column(name = "id")
  @GeneratedValue(generator = "system-uuid")
  @GenericGenerator(name = "system-uuid", strategy = "uuid")
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
