package com.hbc.jobs.consumers.service;

import com.hbc.common.util.DateUtil;
import com.hbc.jobs.consumers.domain.JobDomain;
import com.hbc.jobs.consumers.domain.JobRecordDomain;
import com.hbc.jobs.consumers.domain.entity.JobEntity;
import com.hbc.jobs.consumers.domain.mapper.JobMapper;
import com.hbc.jobs.consumers.domain.mapper.JobRecordMapper;
import com.hbc.jobs.consumers.exception.DuplicateJobException;
import com.hbc.jobs.consumers.exception.InvalidJobTypeException;
import com.hbc.jobs.consumers.exception.JobDomainException;
import com.hbc.jobs.consumers.exception.JobException;
import com.hbc.jobs.consumers.exception.JobIdNotFoundException;
import com.hbc.jobs.framework.common.aop.DBTransaction;
import com.hbc.jobs.framework.common.domain.enums.ApiStatusEnum;
import com.hbc.jobs.framework.common.domain.enums.JobStatusEnum;
import com.hbc.jobs.framework.common.domain.outbound.JobResponse;
import com.hbc.jobs.framework.common.domain.pojo.AuditLog;
import com.hbc.jobs.framework.common.domain.pojo.JobDto;
import com.hbc.jobs.framework.common.domain.pojo.RecordDto;
import com.hbc.jobs.framework.common.domain.pojo.RecordStatusDto;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class JobConsumerService {

  private final FeignClientMapperFactory feignClientMapperFactory;

  private final JobRecordDomain jobRecordDomain;

  private final JobDomain jobDomain;

  private final JobDashboardService jobDashboardService;

  /**
   * @param recordDto
   * @throws JobException
   */
  public void processRecord(RecordDto recordDto) throws JobException {
    log.debug("Inside processRecord service");

    try {
      RecordStatusDto recordStatus = getRecordStatus(recordDto);
      publishRecordStatusToKafka(recordStatus);
      log.debug("Inside processRecord service ends");
    } catch (Exception e) {
      log.error("Error while processing the job record.", e);
      throw new JobException(
          "Exception while processing the job record", e, recordDto.getJobId(), null);
    }
  }

  /**
   * @param recordStatus
   * @throws JobException
   */
  private void publishRecordStatusToKafka(RecordStatusDto recordStatus) throws JobException {
    log.debug("Inside publishRecordStatusToKafka service");

    try {
      jobDashboardService.publishJobRecord(recordStatus);
    } catch (Exception e) {
      log.error("Error while publishing the job record to kafka", e);
      throw new JobException(
          "Exception while publishing the job record to kafka", e, recordStatus.getJobId(), null);
    }
  }

  /**
   * @param recordDto
   * @return
   * @throws JobException
   */
  public RecordStatusDto getRecordStatus(RecordDto recordDto) throws JobException {
    log.debug("Inside getRecordStatus service");

    try {
      var feignClientMapper = feignClientMapperFactory.getMapper(recordDto.getJobType());
      if (Objects.isNull(feignClientMapper)) {
        throw new InvalidJobTypeException(
            "Job type is not correct", recordDto.getJobType().toString());
      }
      return feignClientMapper.getResponseFromAPI(recordDto);
    } catch (Exception e) {
      log.error("Error while retrieving the job record", e);
      throw new JobException(
          "Exception while retrieving the job record", e, recordDto.getJobId(), null);
    }
  }

  /**
   * @param recordStatusDto
   * @throws JobException
   */
  @DBTransaction
  public void updateJobStatus(RecordStatusDto recordStatusDto) throws JobException {
    log.debug("Inside updateJobStatus service");

    try {
      jobRecordDomain.create(JobRecordMapper.INSTANCE.toJobRecordEntity(recordStatusDto));
    } catch (Exception e) {
      log.error("Error while persisting job record entity.", e);
      throw new JobException(
          "Exception while persisting the job record", e, recordStatusDto.getJobId(), null);
    }

    updateJob(recordStatusDto, recordStatusDto.getTotalRecordsInJob());
  }

  /**
   * @param recordStatusDto
   * @param retryCount
   * @throws JobException
   */
  public void updateJob(RecordStatusDto recordStatusDto, int retryCount) throws JobException {
    try {
      var jobEntity = getJobEntity(recordStatusDto.getJobId(), recordStatusDto.getOrgId());
      var updateJobEntity = processJobStatus(jobEntity, recordStatusDto);
      jobDomain.save(updateJobEntity);
    } catch (Exception e) {
      if (e.getCause() instanceof OptimisticLockingFailureException) {
        if (retryCount > 0) {
          log.warn(
              "Retrying update job status task (retryCount={}) for job id {}",
              retryCount,
              recordStatusDto.getJobId());
          updateJob(recordStatusDto, retryCount - 1);
        } else {
          log.error("Error while updating job entity due to wrong document version.", e);
          throw new JobException(
              "Attempt to update job with wrong version", e, recordStatusDto.getJobId(), null);
        }
      }
      log.error("Error while updating job entity.", e);
      throw new JobException(
          "Exception while updating job entity", e, recordStatusDto.getJobId(), null);
    }
  }

  /**
   * @param jobEntity
   * @param recordStatusDto
   * @return
   */
  private JobEntity processJobStatus(JobEntity jobEntity, RecordStatusDto recordStatusDto) {
    log.debug("Inside processJobStatus service");

    if (recordStatusDto.getStatus().equals(ApiStatusEnum.FAILURE)) {
      jobEntity.setFailureCount(jobEntity.getFailureCount() + 1);
    } else {
      jobEntity.setSuccessCount(jobEntity.getSuccessCount() + 1);
    }
    jobEntity.setProcessedRecords(jobEntity.getProcessedRecords() + 1);

    jobEntity.setRemainingRecords(jobEntity.getTotalRecords() - jobEntity.getProcessedRecords());

    List<AuditLog> oldAuditLog = new ArrayList<>(Arrays.asList(jobEntity.getAuditLog()));

    if (jobEntity.getProcessedRecords() == jobEntity.getTotalRecords()) {
      var auditLog = new AuditLog();
      auditLog.setStatus(JobStatusEnum.COMPLETED);
      jobEntity.setStatus(JobStatusEnum.COMPLETED);
      auditLog.setTimeStamp(new Date());
      oldAuditLog.add(auditLog);
    } else if (jobEntity.getStatus().equals(JobStatusEnum.PROCESSED)) {
      var auditLog = new AuditLog();
      auditLog.setStatus(JobStatusEnum.RUNNING);
      jobEntity.setStatus(JobStatusEnum.RUNNING);
      auditLog.setTimeStamp(new Date());
      oldAuditLog.add(auditLog);
    }

    jobEntity.setAuditLog(oldAuditLog.toArray(new AuditLog[0]));
    return jobEntity;
  }

  /**
   * @param orgId
   * @param jobId
   * @param status
   * @return
   * @throws JobException
   */
  public List<RecordStatusDto> getJobResults(String orgId, String jobId, Optional<String> status)
      throws JobException {
    log.debug("--Inside getJobResults()--");
    try {
      return jobRecordDomain.findConsumerJobsByJobParam(orgId, jobId, status);
    } catch (Exception e) {
      throw new JobException("Exception while retrieving the job records", e, jobId, null);
    }
  }

  /**
   * @param jobDto
   * @return
   * @throws JobException
   */
  public JobResponse createJob(JobDto jobDto) throws JobException {
    log.debug("--Inside createJob service--");

    try {
      var existingJobEntity = getJobEntity(jobDto.getJobId(), jobDto.getOrgId());

      if (Objects.nonNull(existingJobEntity)) {
        log.error("Job already exists for job id {}", existingJobEntity.getJobId());
        throw new DuplicateJobException(
            "Job already exists for the same jobId", existingJobEntity.getJobId());
      }

      var jobEntity = jobDomain.save(JobMapper.INSTANCE.toJobEntity(jobDto));
      log.debug("--Inside createJob service ends--");
      return JobMapper.INSTANCE.toJobResponse(jobEntity);
    } catch (Exception e) {
      log.error("Error while creating a job", e);
      throw new JobException("Exception while creating the job ", e, jobDto.getJobId(), null);
    }
  }

  private JobEntity getJobEntity(String jobDto, String jobDto1) throws JobDomainException {
    return jobDomain.findJobByJobIdAndOrgId(jobDto, jobDto1);
  }

  public JobEntity saveJob(JobResponse jobResponse) throws JobDomainException {
    var jobEntity = getJobEntity(jobResponse.getJobId(), jobResponse.getOrgId());
    return jobDomain.save(JobMapper.INSTANCE.updateJobEntity(jobResponse, jobEntity));
  }

  /**
   * @param jobId
   * @param orgId
   * @return
   * @throws JobException
   */
  public JobDto getJob(String jobId, String orgId) throws JobException {
    log.debug("-- Inside getJob service --");

    try {
      var jobEntity = getJobEntity(jobId, orgId);
      if (Objects.isNull(jobEntity)) {
        throw new JobIdNotFoundException("Job is not found!", jobId);
      }
      return JobMapper.INSTANCE.toJob(jobEntity);
    } catch (Exception e) {
      log.error("Error while retrieving the job by jobId.", e);
      throw new JobException("Exception while retrieving the job by jobId", e, jobId, null);
    }
  }

  /**
   * @param orgId
   * @param jobType
   * @param days
   * @param sortField
   * @param sortOrder
   * @param pageNo
   * @param pageSize
   * @return
   * @throws JobException
   */
  @SuppressWarnings("squid:S107")
  public Page<JobResponse> getJobs(
      String orgId,
      Optional<String> jobType,
      Optional<Integer> days,
      String sortField,
      String sortOrder,
      int pageNo,
      int pageSize)
      throws JobException {
    log.debug("-- Inside getJobs service --");
    Page<JobResponse> page;
    Optional<Date> pastDays = Optional.empty();

    if (days.isPresent()) {
      pastDays = Optional.ofNullable(DateUtil.addDaysToDate(new Date(), -days.get()));
    }

    try {
      page =
          jobDomain.findJobsByJobParam(
              orgId, jobType, pastDays, sortField, sortOrder, pageNo, pageSize);
      return page;
    } catch (Exception e) {
      log.error("Error while retrieving the jobs", e);
      throw new JobException("Exception while retrieving the list of jobs", e, null, pageNo);
    }
  }
}
