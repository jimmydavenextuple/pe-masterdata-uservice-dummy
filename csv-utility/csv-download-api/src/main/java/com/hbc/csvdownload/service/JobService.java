package com.hbc.csvdownload.service;

import com.hbc.common.context.CurrentThreadContext;
import com.hbc.common.response.error.ErrorResponse;
import com.hbc.csvdownload.exception.JobServiceException;
import com.hbc.jobs.framework.common.clients.JobsConsumerClient;
import com.hbc.jobs.framework.common.domain.enums.JobStatusEnum;
import com.hbc.jobs.framework.common.domain.enums.JobTypeEnum;
import com.hbc.jobs.framework.common.domain.pojo.AuditLog;
import com.hbc.jobs.framework.common.domain.pojo.JobDto;
import com.hbc.jobs.framework.common.utils.ExceptionUtils;
import feign.FeignException;
import java.util.Collections;
import java.util.Date;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class JobService {

  private final JobsConsumerClient jobsConsumerClient;

  public JobDto createJob(String orgId, int totalTasks, JobTypeEnum jobTypeEnum)
      throws JobServiceException {
    JobDto job = new JobDto();
    job.setJobId(UUID.randomUUID().toString());
    job.setTotalRecords(totalTasks);
    job.setJobType(jobTypeEnum);
    job.setProcessedRecords(0);
    job.setFailureCount(0);
    job.setSuccessCount(0);
    job.setStatus(JobStatusEnum.SUBMITTED);
    job.setOrgId(orgId);
    job.setUserId(CurrentThreadContext.getLogContext().getUsername());
    AuditLog auditLog = new AuditLog();
    auditLog.setStatus(JobStatusEnum.SUBMITTED);
    auditLog.setTimeStamp(new Date());
    job.setAuditLog(Collections.singletonList(auditLog));
    try {
      return jobsConsumerClient.createJob(job).getPayload();
    } catch (FeignException e) {
      log.error("Feign exception when creating job", e);
      ErrorResponse errorResponse = ExceptionUtils.parseFeignException(e);
      throw new JobServiceException(errorResponse.getMessage(), e, jobTypeEnum.name());
    }
  }
}
