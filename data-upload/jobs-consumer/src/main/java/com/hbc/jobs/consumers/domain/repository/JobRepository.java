package com.hbc.jobs.consumers.domain.repository;

import com.hbc.jobs.consumers.domain.entity.JobEntity;
import java.util.Date;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface JobRepository extends JpaRepository<JobEntity, String> {

  Optional<JobEntity> findJobByJobIdAndOrgId(String jobId, String orgId);

  @Query(
      value =
          "SELECT * FROM jobs "
              + "WHERE org_id = CAST(?1 AS TEXT) "
              + "and (?2 is null or job_type = CAST(?2 AS TEXT)) "
              + "and (cast(cast(?3 as text) as timestamp) is null or created_date >= cast(cast(?3 as text) as timestamp)) ",
      nativeQuery = true)
  Page<JobEntity> findJobsByJobParam(
      String orgId, String jobType, Date pastDays, Pageable pageableElement);

  @Query(
      value = "SELECT * FROM JOBS WHERE org_id = ?1 AND status = ?2 FOR UPDATE SKIP LOCKED LIMIT 1",
      nativeQuery = true)
  JobEntity getJobStatusByOrgIdAndStatus(String orgId, String status);

  @Query(
      value =
          "SELECT * FROM JOBS WHERE processing_started_at <= ?3 AND org_id = ?1 AND status = ?2 FOR UPDATE SKIP LOCKED LIMIT 1",
      nativeQuery = true)
  JobEntity fetchJobRecordInTimeRange(String orgId, String status, Date date);
}
