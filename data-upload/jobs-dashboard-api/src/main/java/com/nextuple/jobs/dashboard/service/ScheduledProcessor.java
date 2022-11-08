package com.nextuple.jobs.dashboard.service;

import com.nextuple.common.context.CurrentThreadContext;
import com.nextuple.common.context.Logger;
import com.nextuple.common.context.LoggerFactory;
import com.nextuple.common.util.DateUtil;
import com.nextuple.jobs.consumers.domain.JobDomain;
import com.nextuple.jobs.consumers.domain.mapper.JobMapper;
import com.nextuple.jobs.consumers.exception.JobDomainException;
import com.nextuple.jobs.consumers.exception.PublishJobEventException;
import com.nextuple.jobs.consumers.feign.AuthTokenAPI;
import com.nextuple.jobs.consumers.feign.AuthTokenRequest;
import com.nextuple.jobs.consumers.feign.AuthTokenResponse;
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

  private final AuthTokenAPI authTokenAPI;

  private final PublishJobEventService publishJobEventService;

  private final JobService jobService;

  @Value("${auth-token.grant-type:client_credentials}")
  private String grantType;

  @Value("${auth-token.scope:sfcc-resources/edd}")
  private String scope;

  @Value("${scheduled-processor.timeRangeInHours}")
  private int timeRangeInHours;

  private static final JobMapper INSTANCE = Mappers.getMapper(JobMapper.class);
  private static final String ORG_ID = "BAY";

  @Scheduled(
      fixedRateString = "${scheduled-processor.fixed-rate.minutes:2}",
      timeUnit = TimeUnit.MINUTES)
  @Transactional
  public void processJobOffline() throws JobDomainException, PublishJobEventException {
    String authToken = getAuthToken();
    CurrentThreadContext.getLogContext().setAuthorizationHeader(authToken);

    var jobDto = getJobInSubmittedState();
    if (Objects.isNull(jobDto)) {
      jobDto = getJobInProcessedStateForADay(JobStatusEnum.PROCESSED.name(), timeRangeInHours);
    }

    if (jobDto == null) {
      return;
    }

    try {
      jobService.processJobJsonOffline(
          ORG_ID, jobDto.getJobType(), Optional.ofNullable(jobDto.getJobId()));
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
        jobDomain.getAndUpdateJobStatusByOrgIdAndStatus(
            ORG_ID, JobStatusEnum.SUBMITTED, JobStatusEnum.PROCESSING));
  }

  private JobDto getJobInProcessedStateForADay(String status, int noOfHoursInPast)
      throws JobDomainException {
    logger.debug("Fetching job in PROCESSED status for more than: {}", noOfHoursInPast);
    var date = DateUtil.minusHoursFromCurrentDate(noOfHoursInPast);
    return INSTANCE.toJob(jobDomain.fetchJobRecordInTimeRange(ORG_ID, status, date));
  }

  private String getAuthToken() {
    logger.debug("Generating auth token");
    var authTokenRequest = AuthTokenRequest.builder().grant_type(grantType).scope(scope).build();

    AuthTokenResponse response = authTokenAPI.getAuthToken(authTokenRequest);
    String authToken = response.getAccessToken();
    logger.debug("Auth Token generated is : {}", authToken);

    return "Bearer " + authToken;
  }
}
