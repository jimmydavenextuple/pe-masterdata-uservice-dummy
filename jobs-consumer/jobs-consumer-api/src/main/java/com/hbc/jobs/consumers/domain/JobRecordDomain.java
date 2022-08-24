package com.hbc.jobs.consumers.domain;

import com.hbc.jobs.consumers.domain.entity.JobRecordEntity;
import com.hbc.jobs.consumers.domain.mapper.JobRecordMapper;
import com.hbc.jobs.consumers.domain.repository.JobRecordRepository;
import com.hbc.jobs.consumers.exception.JobRecordDomainException;
import com.hbc.jobs.framework.common.domain.pojo.RecordStatusDto;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class JobRecordDomain {

  private final JobRecordRepository jobRecordRepository;

  public JobRecordDomain(JobRecordRepository jobRecordRepository) {
    this.jobRecordRepository = jobRecordRepository;
  }

  /**
   * Create job resource
   *
   * @param jobRecordEntity Entity to be created
   * @return Created Job ID
   */
  public JobRecordEntity create(JobRecordEntity jobRecordEntity) throws JobRecordDomainException {
    try {
      return jobRecordRepository.save(jobRecordEntity);
    } catch (Exception e) {
      log.error("Error while persisting job record entity.", e);
      throw new JobRecordDomainException(
          "Exception while persisting the job record", e, jobRecordEntity.getId());
    }
  }

  public List<RecordStatusDto> findConsumerJobsByJobParam(
      String orgId, String jobId, Optional<String> status) {

    return JobRecordMapper.INSTANCE.toRecordStatusDtoList(
        jobRecordRepository.findJobRecordsByFilters(orgId, jobId, status.orElse(null)));
  }
}
