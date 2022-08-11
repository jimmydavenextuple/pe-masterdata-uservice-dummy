package com.hbc.jobs.consumers.domain;

import com.hbc.jobs.consumers.domain.entity.JobEntity;
import com.hbc.jobs.consumers.domain.mapper.JobMapper;
import com.hbc.jobs.consumers.domain.repository.JobRepository;
import com.hbc.jobs.consumers.exception.JobDomainException;
import com.hbc.jobs.framework.common.domain.pojo.JobDto;
import java.util.Date;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class JobDomain {

  private final JobRepository jobRepository;

  public JobDomain(JobRepository jobRepository) {
    this.jobRepository = jobRepository;
  }

  /**
   * @param jobId
   * @param orgId
   * @return
   * @throws JobDomainException
   */
  public JobEntity findJobByJobIdAndOrgId(String jobId, String orgId) throws JobDomainException {
    try {
      Optional<JobEntity> jobEntity = jobRepository.findJobByJobIdAndOrgId(jobId, orgId);

      return jobEntity.orElse(null);

    } catch (Exception e) {
      log.error("Error while retrieving the job with id {}", jobId, e);
      throw new JobDomainException("Exception while retrieving the job", e, jobId);
    }
  }

  /**
   * @param jobEntity
   * @return
   * @throws JobDomainException
   */
  public JobEntity save(JobEntity jobEntity) throws JobDomainException {
    try {
      return jobRepository.save(jobEntity);
    } catch (Exception e) {
      log.error("Error while saving job entity.", e);
      throw new JobDomainException("Exception while saving the job", e, jobEntity.getJobId());
    }
  }

  /**
   * @param orgId
   * @param jobType
   * @param pastDays
   * @param sortField
   * @param sortOrder
   * @param pageNo
   * @param pageSize
   * @return
   */
  @SuppressWarnings("squid:S107")
  public Page<JobDto> findJobsByJobParam(
      String orgId,
      Optional<String> jobType,
      Optional<Date> pastDays,
      String sortField,
      Optional<String> sortOrder,
      int pageNo,
      int pageSize) {
    Sort sort =
        Sort.by(
            Sort.Direction.fromOptionalString(sortOrder.orElse("")).orElse(Sort.DEFAULT_DIRECTION),
            sortField);

    Pageable element = PageRequest.of(pageNo - 1, pageSize, sort);
    Page<JobEntity> entityPage =
        jobRepository.findJobsByJobParam(
            orgId, jobType.orElse(null), pastDays.orElse(null), element);
    return new PageImpl<>(
        JobMapper.INSTANCE.toJobList(entityPage.getContent()),
        entityPage.getPageable(),
        entityPage.getTotalElements());
  }
}
