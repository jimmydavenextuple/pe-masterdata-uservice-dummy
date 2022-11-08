package com.nextuple.jobs.consumers.domain;

import com.nextuple.common.context.Logger;
import com.nextuple.common.context.LoggerFactory;
import com.nextuple.jobs.consumers.domain.entity.JobEntity;
import com.nextuple.jobs.consumers.domain.mapper.JobMapper;
import com.nextuple.jobs.consumers.domain.repository.JobRepository;
import com.nextuple.jobs.consumers.exception.JobDomainException;
import com.nextuple.jobs.framework.common.domain.enums.JobStatusEnum;
import com.nextuple.jobs.framework.common.domain.outbound.JobResponse;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class JobDomain {

  private final Logger logger = LoggerFactory.getLogger(JobDomain.class);

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
      logger.error("Error while retrieving the job with id {}", jobId, e);
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
      logger.error("Error while saving job entity.", e);
      throw new JobDomainException("Exception while saving the job", e, jobEntity.getJobId());
    }
  }

  public void updateJobStatus(String jobId, JobStatusEnum statusEnum, boolean incrementSuccess) {
    if (incrementSuccess) {
      jobRepository.updateJobStatusForSuccess(jobId, statusEnum.toString(), new Date());
    } else {
      jobRepository.updateJobStatusForFailure(jobId, statusEnum.toString(), new Date());
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
  public Page<JobResponse> findJobsByJobParam(
      String orgId,
      Optional<String> jobType,
      Optional<Date> pastDays,
      String sortField,
      String sortOrder,
      int pageNo,
      int pageSize) {
    var sort =
        Sort.by(
            Sort.Direction.fromOptionalString(sortOrder).orElse(Sort.DEFAULT_DIRECTION), sortField);

    Pageable element = PageRequest.of(pageNo - 1, pageSize, sort);
    Page<JobEntity> entityPage =
        jobRepository.findJobsByJobParam(
            orgId, jobType.orElse(null), pastDays.orElse(null), element);
    return new PageImpl<>(
        JobMapper.INSTANCE.toJobResponseList(entityPage.getContent()),
        entityPage.getPageable(),
        entityPage.getTotalElements());
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public JobEntity getAndUpdateJobStatusByOrgIdAndStatus(
      String orgId, JobStatusEnum oldStatus, JobStatusEnum newStatus) throws JobDomainException {
    try {
      var jobEntity = jobRepository.getJobStatusByOrgIdAndStatus(orgId, oldStatus.name());
      if (!Objects.isNull(jobEntity)) {
        jobEntity.setStatus(newStatus);
        jobEntity.setProcessingStartedAt(new Date());
        return save(jobEntity);
      }
      return null;
    } catch (Exception e) {
      logger.error(
          "Error while updating the job with for given orgId: {} and oldStatus: {}",
          orgId,
          oldStatus,
          e);
      throw new JobDomainException(
          "Error while updating the job with for given orgId and status", e, null);
    }
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public JobEntity fetchJobRecordInTimeRange(String orgId, String status, Date date)
      throws JobDomainException {
    try {
      return jobRepository.fetchJobRecordInTimeRange(orgId, status, date);
    } catch (Exception e) {
      logger.error("Error while fetching jobs in a time range");
      throw new JobDomainException("Error while fetching jobs in a time range", e, null);
    }
  }
}
