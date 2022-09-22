package com.hbc.jobs.consumers.service;

import com.hbc.common.context.CurrentThreadContext;
import com.hbc.common.context.Logger;
import com.hbc.common.context.LoggerFactory;
import com.hbc.common.util.DateUtil;
import com.hbc.jobs.consumers.domain.JobDomain;
import com.hbc.jobs.consumers.domain.mapper.JobMapper;
import com.hbc.jobs.consumers.exception.JobDomainException;
import com.hbc.jobs.consumers.feign.AuthTokenAPI;
import com.hbc.jobs.consumers.feign.AuthTokenRequest;
import com.hbc.jobs.consumers.feign.AuthTokenResponse;
import com.hbc.jobs.framework.common.clients.JobsDashboardClient;
import com.hbc.jobs.framework.common.domain.enums.JobStatusEnum;
import com.hbc.jobs.framework.common.domain.pojo.JobDto;
import com.hbc.jobs.framework.common.utils.ExceptionUtils;
import feign.FeignException;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
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
  private final CsvProcessingService csvProcessingService;

  private final JobsDashboardClient jobsDashboardClient;

  private final AuthTokenAPI authTokenAPI;

  @Value("${auth-token.grant-type:client_credentials}")
  private String grantType;

  @Value("${auth-token.scope:sfcc-resources/edd}")
  private String scope;

  @Value("${scheduled-processor.timeRangeInHours}")
  private int timeRangeInHours;

  private static final JobMapper INSTANCE = Mappers.getMapper(JobMapper.class);
  private static final String ORG_ID = "BAY";

  @Scheduled(fixedRateString = "${scheduled-processor.fixed-rate:2}", timeUnit = TimeUnit.MINUTES)
  @Transactional
  public void processJobOffline() throws JobDomainException {
    var jobDto = getJobInSubmittedState();
    if (Objects.isNull(jobDto)) {
      jobDto = getJobInProcessedStateForADay(JobStatusEnum.PROCESSED.name(), timeRangeInHours);
    }

    if (jobDto == null) {
      return;
    }

    InputStream inputStream = new ByteArrayInputStream(jobDto.getFile());

    var jobTypeEnum = jobDto.getJobType();

    try {
      String authToken = getAuthToken();
      CurrentThreadContext.getLogContext().setAuthorizationHeader(authToken);
      logger.debug("Processing of the csv data started");
      String jobRequest =
          csvProcessingService.processInputCsvFile(inputStream, jobTypeEnum, jobDto.getOrgId());
      logger.debug("Processing of the csv data completed");
      jobDto =
          INSTANCE.toJob(
              jobDomain.updateJobStatusByOrgIdAndStatus(
                  ORG_ID, JobStatusEnum.PROCESSING, JobStatusEnum.PROCESSED));
      jobsDashboardClient.processJobJsonOffline(
          ORG_ID, jobDto.getJobType(), jobRequest, jobDto.getJobId());
    } catch (FeignException e) {
      logger.error("Feign exception while processing the job", e);
      var errorResponse = ExceptionUtils.parseFeignException(e);
      jobDto.setStatus(JobStatusEnum.FAILED);
      jobDto.setErrorMessage(errorResponse.getMessage());
      jobDomain.save(INSTANCE.toJobEntity(jobDto));
    } catch (Exception e) {
      logger.error("Error while processing csv data for job: {}", jobDto.getJobId(), e);
      jobDto.setStatus(JobStatusEnum.FAILED);
      jobDto.setErrorMessage(e.getMessage());
      jobDomain.save(INSTANCE.toJobEntity(jobDto));
    }
  }

  private JobDto getJobInSubmittedState() throws JobDomainException {
    logger.debug("Fetching job in SUBMITTED status");
    return INSTANCE.toJob(
        jobDomain.updateJobStatusByOrgIdAndStatus(
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
