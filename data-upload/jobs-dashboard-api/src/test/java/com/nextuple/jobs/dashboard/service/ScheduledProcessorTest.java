package com.nextuple.jobs.dashboard.service;

import static org.mockito.Mockito.*;

import com.nextuple.common.util.DateUtil;
import com.nextuple.jobs.consumers.domain.JobDomain;
import com.nextuple.jobs.consumers.domain.entity.JobEntity;
import com.nextuple.jobs.consumers.exception.JobDomainException;
import com.nextuple.jobs.consumers.exception.PublishJobEventException;
import com.nextuple.jobs.consumers.feign.AuthTokenAPI;
import com.nextuple.jobs.consumers.service.PublishJobEventService;
import com.nextuple.jobs.dashboard.common.TestUtil;
import com.nextuple.jobs.dashboard.exception.JobException;
import com.nextuple.jobs.framework.common.clients.JobsDashboardClient;
import com.nextuple.jobs.framework.common.domain.enums.JobStatusEnum;
import com.nextuple.jobs.framework.common.domain.enums.JobTypeEnum;
import com.nextuple.jobs.framework.common.domain.pojo.JobDto;
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

  @Mock private JobsDashboardClient jobsDashboardClient;

  @Mock private PublishJobEventService publishJobEventService;

  @Mock private AuthTokenAPI authTokenAPI;

  @InjectMocks private ScheduledProcessor scheduledProcessor;

  @InjectMocks private TestUtil testUtil;

  @Mock private JobService jobService;

  @Test
  void processJobOffline() throws JobDomainException, JobException {
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

    when(jobService.processJobJsonOffline(any(), any(), any()))
        .thenReturn(testUtil.createJobResponse(JobTypeEnum.UPLOAD_PROCESSING_LEAD_TIMES, 5));

    Assertions.assertDoesNotThrow(() -> scheduledProcessor.processJobOffline());
  }

  @Test
  void processJobOfflineQueuedJob() throws JobDomainException, JobException {
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
    when(jobService.processJobJsonOffline(any(), any(), any()))
        .thenReturn(testUtil.createJobResponse(JobTypeEnum.UPLOAD_PROCESSING_LEAD_TIMES, 2));
    jobEntity.setStatus(JobStatusEnum.PROCESSED);

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
  void processJobOfflineException()
      throws JobDomainException, PublishJobEventException, JobException {
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

    doNothing().when(publishJobEventService).publishJobDetailsEvent(any());

    when(jobDomain.getAndUpdateJobStatusByOrgIdAndStatus(any(), any(), any()))
        .thenReturn(jobEntity);
    when(authTokenAPI.getAuthToken(any())).thenReturn(testUtil.getAuthTokenResponse());
    when(jobService.processJobJsonOffline(any(), any(), any()))
        .thenThrow(new RuntimeException("Error while fetching and updating job"));
    jobEntity.setStatus(JobStatusEnum.FAILED);
    jobEntity.setErrorMessage("Error while fetching and updating job");
    when(jobDomain.save(jobEntity)).thenReturn(jobEntity);

    Assertions.assertDoesNotThrow(() -> scheduledProcessor.processJobOffline());
  }
}
