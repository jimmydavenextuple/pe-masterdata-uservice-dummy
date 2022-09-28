package com.hbc.jobs.consumers.service;

import static org.mockito.Mockito.*;

import com.hbc.common.response.BaseResponse;
import com.hbc.common.response.error.ErrorResponse;
import com.hbc.common.util.DateUtil;
import com.hbc.csvdownload.exception.JsonParsingException;
import com.hbc.jobs.consumers.common.TestUtil;
import com.hbc.jobs.consumers.domain.JobDomain;
import com.hbc.jobs.consumers.domain.entity.JobEntity;
import com.hbc.jobs.consumers.exception.JobDomainException;
import com.hbc.jobs.consumers.feign.AuthTokenAPI;
import com.hbc.jobs.framework.common.clients.JobsDashboardClient;
import com.hbc.jobs.framework.common.domain.enums.JobStatusEnum;
import com.hbc.jobs.framework.common.domain.enums.JobTypeEnum;
import com.hbc.jobs.framework.common.domain.pojo.JobDto;
import com.hbc.jobs.framework.common.utils.ExceptionUtils;
import com.opencsv.exceptions.CsvException;
import feign.FeignException;
import feign.Request;
import feign.Request.HttpMethod;
import java.io.IOException;
import java.util.HashMap;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class ScheduledProcessorTest {

  @Mock private JobDomain jobDomain;

  @Mock private CsvProcessingService csvProcessingService;

  @Mock private JobsDashboardClient jobsDashboardClient;

  @Mock private AuthTokenAPI authTokenAPI;

  @InjectMocks private ScheduledProcessor scheduledProcessor;

  @InjectMocks private TestUtil testUtil;

  @Test
  void processJobOffline()
      throws JobDomainException, IOException, JsonParsingException, CsvException {
    ReflectionTestUtils.setField(scheduledProcessor, "grantType", "grant");
    ReflectionTestUtils.setField(scheduledProcessor, "scope", "scope");
    ReflectionTestUtils.setField(scheduledProcessor, "timeRangeInHours", 24);

    JobEntity jobEntity = testUtil.createJobEntity(JobTypeEnum.UPLOAD_PROCESSING_LEAD_TIMES, 5);
    JobDto jobDto = testUtil.createJob(JobTypeEnum.UPLOAD_PROCESSING_LEAD_TIMES, 5);
    jobDto.setStatus(JobStatusEnum.PROCESSED);
    jobEntity.setStatus(JobStatusEnum.PROCESSING);
    String csvContents = TestUtil.CSV_CONTENTS_PROCESSING_LEAD_TIMES;
    jobEntity.setFile(csvContents.getBytes());
    jobDto.setFile(csvContents.getBytes());
    when(jobDomain.getAndUpdateJobStatusByOrgIdAndStatus(any(), any(), any()))
        .thenReturn(jobEntity);
    when(authTokenAPI.getAuthToken(any())).thenReturn(testUtil.getAuthTokenResponse());
    when(csvProcessingService.processInputCsvFile(any(), any(), any()))
        .thenReturn(TestUtil.JOB_STRING);
    when(jobsDashboardClient.processJobJsonOffline(any(), any(), any(), any()))
        .thenReturn(BaseResponse.builder().payload(jobDto).build());

    Assertions.assertDoesNotThrow(() -> scheduledProcessor.processJobOffline());
  }

  @Test
  void processJobOfflineQueuedJob()
      throws JobDomainException, IOException, JsonParsingException, CsvException {
    ReflectionTestUtils.setField(scheduledProcessor, "grantType", "grant");
    ReflectionTestUtils.setField(scheduledProcessor, "scope", "scope");
    ReflectionTestUtils.setField(scheduledProcessor, "timeRangeInHours", 24);

    String csvContents = TestUtil.CSV_CONTENTS_PROCESSING_LEAD_TIMES;

    JobEntity jobEntity = testUtil.createJobEntity(JobTypeEnum.UPLOAD_PROCESSING_LEAD_TIMES, 5);
    jobEntity.setStatus(JobStatusEnum.PROCESSING);
    jobEntity.setFile(csvContents.getBytes());
    jobEntity.setProcessingStartedAt(DateUtil.minusHoursFromCurrentDate(25));

    JobDto jobDto = testUtil.createJob(JobTypeEnum.UPLOAD_PROCESSING_LEAD_TIMES, 5);
    jobDto.setStatus(JobStatusEnum.PROCESSED);
    jobDto.setFile(csvContents.getBytes());

    when(jobDomain.getAndUpdateJobStatusByOrgIdAndStatus(
            TestUtil.ORG_ID, JobStatusEnum.SUBMITTED, JobStatusEnum.PROCESSING))
        .thenReturn(null);
    when(jobDomain.fetchJobRecordInTimeRange(any(), any(), any())).thenReturn(jobEntity);
    when(authTokenAPI.getAuthToken(any())).thenReturn(testUtil.getAuthTokenResponse());
    when(csvProcessingService.processInputCsvFile(any(), any(), any()))
        .thenReturn(TestUtil.JOB_STRING);
    when(jobsDashboardClient.processJobJsonOffline(any(), any(), any(), any()))
        .thenReturn(BaseResponse.builder().payload(jobDto).build());
    jobEntity.setStatus(JobStatusEnum.PROCESSED);
    when(jobDomain.getAndUpdateJobStatusByOrgIdAndStatus(
            TestUtil.ORG_ID, JobStatusEnum.PROCESSING, JobStatusEnum.PROCESSED))
        .thenReturn(jobEntity);

    Assertions.assertDoesNotThrow(() -> scheduledProcessor.processJobOffline());
  }

  @Test
  void processJobOfflineNoJobAvailableForProcessing() throws JobDomainException {
    ReflectionTestUtils.setField(scheduledProcessor, "timeRangeInHours", 24);

    when(authTokenAPI.getAuthToken(any())).thenReturn(testUtil.getAuthTokenResponse());
    when(jobDomain.getAndUpdateJobStatusByOrgIdAndStatus(
            TestUtil.ORG_ID, JobStatusEnum.SUBMITTED, JobStatusEnum.PROCESSING))
        .thenReturn(null);
    when(jobDomain.fetchJobRecordInTimeRange(any(), any(), any())).thenReturn(null);

    Assertions.assertDoesNotThrow(() -> scheduledProcessor.processJobOffline());
  }

  @Test
  void processJobOfflineFeignException()
      throws JobDomainException, IOException, JsonParsingException, CsvException {
    ReflectionTestUtils.setField(scheduledProcessor, "grantType", "grant");
    ReflectionTestUtils.setField(scheduledProcessor, "scope", "scope");
    ReflectionTestUtils.setField(scheduledProcessor, "timeRangeInHours", 24);

    String csvContents = TestUtil.CSV_CONTENTS_PROCESSING_LEAD_TIMES;

    JobEntity jobEntity = testUtil.createJobEntity(JobTypeEnum.UPLOAD_PROCESSING_LEAD_TIMES, 5);
    jobEntity.setStatus(JobStatusEnum.PROCESSING);
    jobEntity.setFile(csvContents.getBytes());

    JobDto jobDto = testUtil.createJob(JobTypeEnum.UPLOAD_PROCESSING_LEAD_TIMES, 5);
    jobDto.setStatus(JobStatusEnum.PROCESSED);
    jobDto.setFile(csvContents.getBytes());

    FeignException exception =
        new FeignException.BadRequest(
            "Feign exception while processing the job",
            Request.create(HttpMethod.PUT, "", new HashMap<>(), null, null, null),
            "Feign exception while processing the job".getBytes());

    when(jobDomain.getAndUpdateJobStatusByOrgIdAndStatus(any(), any(), any()))
        .thenReturn(jobEntity);
    when(authTokenAPI.getAuthToken(any())).thenReturn(testUtil.getAuthTokenResponse());
    when(csvProcessingService.processInputCsvFile(any(), any(), any()))
        .thenReturn(TestUtil.JOB_STRING);
    when(jobsDashboardClient.processJobJsonOffline(any(), any(), any(), any()))
        .thenThrow(exception);
    jobEntity.setStatus(JobStatusEnum.FAILED);
    ErrorResponse errorResponse = ExceptionUtils.parseFeignException(exception);
    jobEntity.setErrorMessage(errorResponse.getMessage());
    when(jobDomain.save(jobEntity)).thenReturn(jobEntity);

    Assertions.assertDoesNotThrow(() -> scheduledProcessor.processJobOffline());
  }

  @Test
  void processJobOfflineException()
      throws JobDomainException, IOException, JsonParsingException, CsvException {
    ReflectionTestUtils.setField(scheduledProcessor, "grantType", "grant");
    ReflectionTestUtils.setField(scheduledProcessor, "scope", "scope");
    ReflectionTestUtils.setField(scheduledProcessor, "timeRangeInHours", 24);

    String csvContents = TestUtil.CSV_CONTENTS_PROCESSING_LEAD_TIMES;

    JobEntity jobEntity = testUtil.createJobEntity(JobTypeEnum.UPLOAD_PROCESSING_LEAD_TIMES, 5);
    jobEntity.setStatus(JobStatusEnum.PROCESSING);
    jobEntity.setFile(csvContents.getBytes());

    JobDto jobDto = testUtil.createJob(JobTypeEnum.UPLOAD_PROCESSING_LEAD_TIMES, 5);
    jobDto.setStatus(JobStatusEnum.PROCESSED);
    jobDto.setFile(csvContents.getBytes());

    when(jobDomain.getAndUpdateJobStatusByOrgIdAndStatus(any(), any(), any()))
        .thenReturn(jobEntity);
    when(authTokenAPI.getAuthToken(any())).thenReturn(testUtil.getAuthTokenResponse());
    when(csvProcessingService.processInputCsvFile(any(), any(), any()))
        .thenThrow(new RuntimeException("Error while forming job string"));
    jobEntity.setStatus(JobStatusEnum.FAILED);
    jobEntity.setErrorMessage("Error while forming job string");
    when(jobDomain.save(jobEntity)).thenReturn(jobEntity);

    Assertions.assertDoesNotThrow(() -> scheduledProcessor.processJobOffline());
  }
}
