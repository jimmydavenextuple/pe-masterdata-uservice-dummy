/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.jobs.dashboard.service;

import com.nextuple.common.context.CurrentThreadContext;
import com.nextuple.common.context.Logger;
import com.nextuple.common.context.LoggerFactory;
import com.nextuple.common.util.DateUtil;
import com.nextuple.jobs.consumers.authentication.AuthService;
import com.nextuple.jobs.consumers.domain.JobDomain;
import com.nextuple.jobs.consumers.domain.mapper.JobMapper;
import com.nextuple.jobs.consumers.exception.JobDomainException;
import com.nextuple.jobs.consumers.exception.PublishJobEventException;
import com.nextuple.jobs.consumers.service.PublishJobEventService;
import com.nextuple.jobs.framework.common.domain.enums.JobStatusEnum;
import com.nextuple.jobs.framework.common.domain.pojo.JobDetailsDto;
import com.nextuple.jobs.framework.common.domain.pojo.JobDto;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ScheduledProcessor {

  private final Logger logger = LoggerFactory.getLogger(ScheduledProcessor.class);

  private final JobDomain jobDomain;

  private final PublishJobEventService publishJobEventService;

  private final JobService jobService;

  @Value("${scheduled-processor.timeRangeInHours}")
  private int timeRangeInHours;

  private static final JobMapper INSTANCE = Mappers.getMapper(JobMapper.class);

  private final AuthService authService;

  @Scheduled(
      fixedRateString = "${scheduled-processor.fixed-rate.minutes:2}",
      timeUnit = TimeUnit.MINUTES)
  @Transactional
  public void processJobOffline() throws JobDomainException, PublishJobEventException {
    authService.setAuthTokenInThreadContext();
    var jobDto = getJobInSubmittedState();
    if (Objects.isNull(jobDto)) {
      jobDto = getJobInProcessedStateForADay(JobStatusEnum.PROCESSED.name(), timeRangeInHours);
    }

    if (jobDto == null) {
      return;
    }

    try {
      String orgId = jobDto.getOrgId();
      CurrentThreadContext.getLogContext().setTenantId(orgId);
      jobService.processJobJsonOffline(
          orgId, jobDto.getJobType(), Optional.ofNullable(jobDto.getJobId()));
    } catch (Exception e) {
      logger.error("Error while processing csv data for job: {}", jobDto.getJobId(), e);
      jobDto.setStatus(JobStatusEnum.FAILED);
      jobDto.setErrorMessage(e.getMessage());
      jobDomain.save(INSTANCE.toJobEntity(jobDto));
      publishJobFailureEvent(jobDto);
    }
  }

  private void publishJobFailureEvent(JobDto jobDto) throws PublishJobEventException {
    var jobDetailsDto = new JobDetailsDto();
    jobDetailsDto.setJobId(jobDto.getJobId());
    jobDetailsDto.setTotalRecords(jobDto.getTotalRecords());
    jobDetailsDto.setJobType(jobDto.getJobType());
    jobDetailsDto.setStatus(jobDto.getStatus());
    jobDetailsDto.setOrgId(jobDto.getOrgId());
    publishJobEventService.publishJobDetailsEvent(jobDetailsDto);
  }

  private JobDto getJobInSubmittedState() throws JobDomainException {
    logger.debug("Fetching job in SUBMITTED status");
    return INSTANCE.toJob(
        jobDomain.getAndUpdateJobStatusByStatus(JobStatusEnum.SUBMITTED, JobStatusEnum.PROCESSING));
  }

  private JobDto getJobInProcessedStateForADay(String status, int noOfHoursInPast)
      throws JobDomainException {
    logger.debug("Fetching job in PROCESSED status for more than: {}", noOfHoursInPast);
    var date = DateUtil.minusHoursFromCurrentDate(noOfHoursInPast);
    return INSTANCE.toJob(jobDomain.fetchJobRecordInTimeRange(status, date));
  }
}
