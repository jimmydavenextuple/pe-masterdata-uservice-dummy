/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.jobs.consumers.domain.repository;

import com.nextuple.jobs.consumers.domain.entity.JobEntity;
import com.nextuple.jobs.framework.common.domain.enums.JobStatusEnum;
import com.nextuple.jobs.framework.common.domain.pojo.AuditLog;
import java.util.Date;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface JobRepository extends JpaRepository<JobEntity, String> {

  Optional<JobEntity> findJobByJobIdAndOrgId(String jobId, String orgId);

  JobEntity findJobByJobIdAndOrgIdAndStatus(String jobId, String orgId, JobStatusEnum status);

  @Query(
      value =
          "SELECT * FROM jobs "
              + "WHERE org_id = CAST(?1 AS TEXT) "
              + "and (?2 is null or job_type = CAST(?2 AS TEXT)) "
              + "and (cast(cast(?3 as text) as timestamp) is null or created_date >= cast(cast(?3 as text) as timestamp)) ",
      nativeQuery = true)
  Page<JobEntity> findJobsByJobParam(
      String orgId, String jobType, Date pastDays, Pageable pageableElement);

  @Modifying
  @Query(
      value =
          "update jobs set failure_count = failure_count + 1, processed_records = processed_records + 1, "
              + " remaining_records = total_records - processed_records - 1,"
              + " status = :status, last_modified_date = :date"
              + " where job_id= :jobid",
      nativeQuery = true)
  void updateJobStatusForFailure(
      @Param("jobid") String jobId,
      @Param("status") String status,
      @Param("date") Date modifiedDate);

  @Modifying
  @Query(
      value =
          "update jobs set success_count = success_count + 1, processed_records = processed_records + 1, "
              + " remaining_records = total_records - processed_records - 1,"
              + " status = :status, last_modified_date = :date"
              + " where job_id= :jobid",
      nativeQuery = true)
  void updateJobStatusForSuccess(
      @Param("jobid") String jobId,
      @Param("status") String status,
      @Param("date") Date modifiedDate);

  @Modifying
  @Query(
      value =
          "update jobs set "
              + " audit_log = :audit, status = :status, last_modified_date = :date"
              + " where job_id= :jobid",
      nativeQuery = true)
  void updateJobStatus(
      @Param("jobid") String jobId,
      @Param("audit") AuditLog[] auditLog,
      @Param("status") JobStatusEnum statusEnum,
      @Param("date") Date modifiedDate);

  @Query(
      value = "SELECT * FROM JOBS WHERE status = ?1 FOR UPDATE SKIP LOCKED LIMIT 1",
      nativeQuery = true)
  JobEntity getJobStatusByStatus(String status);

  @Query(
      value =
          "SELECT * FROM JOBS WHERE processing_started_at <= ?2 AND status = ?1 FOR UPDATE SKIP LOCKED LIMIT 1",
      nativeQuery = true)
  JobEntity fetchJobRecordInTimeRange(String status, Date date);
}
