package com.hbc.jobs.consumers.domain.repository;

import com.hbc.jobs.consumers.domain.entity.JobRecordEntity;
import java.util.List;
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
}
