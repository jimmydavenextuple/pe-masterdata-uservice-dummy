package com.nextuple.jobs.consumers.domain.repository;

import com.nextuple.jobs.consumers.domain.entity.JobRecordEntity;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface JobRecordRepository extends JpaRepository<JobRecordEntity, String> {

  @Query(
      value =
          "SELECT * FROM job_records "
              + "WHERE org_id = CAST(?1 AS TEXT) "
              + "and job_id = CAST(?2 AS TEXT) "
              + "and (?3 is null or status = CAST(?3 AS TEXT)) ",
      nativeQuery = true)
  List<JobRecordEntity> findJobRecordsByFilters(String orgId, String jobId, String status);

  @Query(
      value =
          "SELECT * FROM job_records "
              + "WHERE job_id = CAST(?1 AS TEXT) "
              + "and org_id = CAST(?2 AS TEXT) "
              + "and (status = CAST(?3 AS TEXT)) ",
      nativeQuery = true)
  Page<JobRecordEntity> findJobRecordsByJobParam(
      String jobId, String orgId, String status, Pageable pageableElement);
}
