package com.hbc.jobs.consumers.service;

import com.hbc.common.context.CurrentThreadContext;
import com.hbc.common.context.Logger;
import com.hbc.common.context.LoggerFactory;
import com.hbc.common.util.DateUtil;
import com.hbc.jobs.consumers.domain.JobDomain;
import com.hbc.jobs.consumers.domain.mapper.JobMapper;
import com.hbc.jobs.consumers.exception.JobDomainException;
import com.hbc.jobs.consumers.exception.PublishJobEventException;
import com.hbc.jobs.consumers.feign.AuthTokenAPI;
import com.hbc.jobs.consumers.feign.AuthTokenRequest;
import com.hbc.jobs.consumers.feign.AuthTokenResponse;
import com.hbc.jobs.framework.common.clients.JobsDashboardClient;
import com.hbc.jobs.framework.common.domain.enums.JobStatusEnum;
import com.hbc.jobs.framework.common.domain.pojo.JobDetailsDto;
import com.hbc.jobs.framework.common.domain.pojo.JobDto;
import com.hbc.jobs.framework.common.utils.ExceptionUtils;
import feign.FeignException;
import java.util.Objects;
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

  private final JobsDashboardClient jobsDashboardClient;

  private final AuthTokenAPI authTokenAPI;

  private final PublishJobEventService publishJobEventService;

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
      jobsDashboardClient.processJobsJsonOffline(ORG_ID, jobDto.getJobType(), jobDto.getJobId());
    } catch (FeignException e) {
      logger.error("Feign exception while processing the job", e);
      var errorResponse = ExceptionUtils.parseFeignException(e);
      jobDto.setStatus(JobStatusEnum.FAILED);
      jobDto.setErrorMessage(errorResponse.getMessage());
      jobDomain.save(INSTANCE.toJobEntity(jobDto));
      publishJobFailureEvent(jobDto);
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
