package com.hbc.jobs.dashboard.controller;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hbc.common.base.PagePayload;
import com.hbc.common.response.BaseResponse;
import com.hbc.jobs.dashboard.common.TestUtil;
import com.hbc.jobs.dashboard.exception.JobException;
import com.hbc.jobs.dashboard.service.JobService;
import com.hbc.jobs.framework.common.domain.enums.JobStatusEnum;
import com.hbc.jobs.framework.common.domain.enums.JobTypeEnum;
import com.hbc.jobs.framework.common.domain.pojo.AuditLog;
import com.hbc.jobs.framework.common.domain.pojo.JobDto;
import com.hbc.jobs.framework.common.domain.pojo.JobFilters;
import com.hbc.jobs.framework.common.domain.pojo.RecordStatusDto;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.multipart.MultipartFile;

class JobControllerTest {

  @InjectMocks private JobController jobController;

  @Mock private JobService jobService;

  @InjectMocks private TestUtil testUtil;

  @BeforeEach
  public void init() {
    MockitoAnnotations.openMocks(this);
    MockMvcBuilders.standaloneSetup(jobController).build();
  }

  @Test
  void processJobOffline() throws JobException {
    MultipartFile csvFile = Mockito.mock(MultipartFile.class);

    when(jobService.processJobOffline(
            csvFile, TestUtil.ORG_ID, TestUtil.JOB_TYPE_UPLOAD_PROCESSING_LEAD_TIMES))
        .thenReturn(new JobDto());
    ResponseEntity<BaseResponse<JobDto>> responseEntity =
        jobController.processJobOffline(
            TestUtil.ORG_ID, TestUtil.JOB_TYPE_UPLOAD_PROCESSING_LEAD_TIMES, csvFile);
    Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode(), "Status code");
  }

  @Test
  void processJobOfflineException() throws JobException {
    MultipartFile csvFile = Mockito.mock(MultipartFile.class);

    when(jobService.processJobOffline(any(), any(), any()))
        .thenThrow(
            new JobException(
                "Error while processing csv/json file",
                "jobId1",
                JobTypeEnum.UPLOAD_PROCESSING_LEAD_TIMES));

    JobException exception =
        assertThrows(
            JobException.class,
            () ->
                jobController.processJobOffline(
                    TestUtil.JOB_ID, TestUtil.JOB_TYPE_UPLOAD_PROCESSING_LEAD_TIMES, csvFile));

    Assertions.assertEquals(
        "Error while processing csv/json file", exception.getMessage(), "Exception Message");

    Assertions.assertEquals(
        JobTypeEnum.UPLOAD_PROCESSING_LEAD_TIMES, exception.getJobType(), "Exception Job type");
  }

  @Test
  void processJobJsonOfflineCreatingNewJob() throws JobException {
    String request =
        "[{\"carrierServiceId\" : \"ALL\", \"nodeId\" : \"DC-963-578\" , \"orgId\" : \"BAY\", \"serviceOption\" : \"STANDARD\", \"lastPickupTime\" : \"15:00\", \"processingTime\" : 15}]";
    when(jobService.processJobJsonOffline(
            anyString(), anyString(), any(JobTypeEnum.class), any(Optional.class)))
        .thenReturn(
            testUtil.createJob(
                TestUtil.JOB_ID,
                TestUtil.ORG_ID,
                JobStatusEnum.SUBMITTED,
                Collections.singletonList(new AuditLog()),
                TestUtil.JOB_TYPE_UPLOAD_PROCESSING_LEAD_TIMES));

    ResponseEntity<BaseResponse<JobDto>> responseEntity =
        jobController.processJobJsonOffline(
            JobTypeEnum.UPLOAD_PROCESSING_LEAD_TIMES, TestUtil.ORG_ID, request);

    Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    Assertions.assertNotNull(responseEntity.getBody());
    Assertions.assertNotNull(responseEntity.getBody().getPayload());
    verify(jobService, times(1))
        .processJobJsonOffline(
            anyString(), anyString(), any(JobTypeEnum.class), any(Optional.class));
  }

  @Test
  void processJobJsonOfflineCreatingNewJobException() throws JobException {
    String request =
        "[{\"carrierServiceId\" : \"ALL\", \"nodeId\" : \"DC-963-578\" , \"orgId\" : \"BAY\", \"serviceOption\" : \"STANDARD\", \"lastPickupTime\" : \"15:00\", \"processingTime\" : 15}]";
    when(jobService.processJobJsonOffline(
            anyString(), anyString(), any(JobTypeEnum.class), any(Optional.class)))
        .thenThrow(new JobException("Error while creating job", null));
    Exception exception =
        Assertions.assertThrows(
            JobException.class,
            () ->
                jobController.processJobJsonOffline(
                    JobTypeEnum.UPLOAD_PROCESSING_LEAD_TIMES, TestUtil.ORG_ID, request));

    Assertions.assertNotNull(exception);

    verify(jobService, times(1))
        .processJobJsonOffline(
            anyString(), anyString(), any(JobTypeEnum.class), any(Optional.class));
  }

  @Test
  void processJobJsonOfflineUpdateExistingJob() throws JobException {

    when(jobService.processJobJsonOffline(any(), any(), any(), any())).thenReturn(new JobDto());
    ResponseEntity<BaseResponse<JobDto>> responseEntity =
        jobController.processJobJsonOffline(
            TestUtil.ORG_ID,
            TestUtil.JOB_TYPE_UPLOAD_PROCESSING_LEAD_TIMES,
            TestUtil.ORG_ID,
            "req");
    Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode(), "Status code");

    verify(jobService, times(1)).processJobJsonOffline(any(), any(), any(), any());
  }

  @Test
  void processJobJsonOffline2() throws JobException {

    when(jobService.processJobJsonOffline(any(), any(), any(), any())).thenReturn(new JobDto());
    ResponseEntity<BaseResponse<JobDto>> responseEntity =
        jobController.processJobJsonOffline(
            TestUtil.ORG_ID, TestUtil.JOB_TYPE_UPLOAD_PROCESSING_LEAD_TIMES, "", "");
    Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode(), "Status code");

    verify(jobService, times(1)).processJobJsonOffline(any(), any(), any(), any());
  }

  @Test
  void processJobJsonOffline2Exception() throws JobException {

    when(jobService.processJobJsonOffline(any(), any(), any(), any()))
        .thenThrow(
            new JobException(
                "Error while processing json request", "jobId1", JobTypeEnum.UPLOAD_TRANSIT_TIMES));

    JobException exception =
        assertThrows(
            JobException.class,
            () ->
                jobController.processJobJsonOffline(
                    TestUtil.ORG_ID, TestUtil.JOB_TYPE_UPLOAD_PROCESSING_LEAD_TIMES, "", ""));

    Assertions.assertEquals(
        "Error while processing json request", exception.getMessage(), "Exception Message");

    Assertions.assertEquals(
        JobTypeEnum.UPLOAD_TRANSIT_TIMES, exception.getJobType(), "Exception Job type");
    verify(jobService, times(1)).processJobJsonOffline(any(), any(), any(), any());
  }

  @Test
  void processJobJsonOfflineException() throws JobException {

    when(jobService.processJobJsonOffline(any(), any(), any(), any()))
        .thenThrow(
            new JobException(
                "Error while processing json request",
                "jobId1",
                JobTypeEnum.UPLOAD_PROCESSING_LEAD_TIMES));

    JobException exception =
        assertThrows(
            JobException.class,
            () ->
                jobController.processJobJsonOffline(
                    TestUtil.ORG_ID, TestUtil.JOB_TYPE_UPLOAD_PROCESSING_LEAD_TIMES, "", ""));

    Assertions.assertEquals(
        "Error while processing json request", exception.getMessage(), "Exception Message");

    Assertions.assertEquals(
        JobTypeEnum.UPLOAD_PROCESSING_LEAD_TIMES, exception.getJobType(), "Exception Job type");
  }

  @Test
  void getJob() throws JobException {
    String jobId = "Get_Job";
    JobDto job =
        testUtil.createJob(
            jobId,
            TestUtil.ORG_ID,
            JobStatusEnum.RUNNING,
            Collections.singletonList(new AuditLog()),
            JobTypeEnum.UPLOAD_TRANSIT_TIMES);

    when(jobService.getJob(TestUtil.ORG_ID, jobId)).thenReturn(job);
    ResponseEntity<BaseResponse<JobDto>> responseEntity =
        jobController.getJob(TestUtil.ORG_ID, jobId);
    JobDto jobResponse = Objects.requireNonNull(responseEntity.getBody()).getPayload();

    verify(jobService, times(1)).getJob(TestUtil.ORG_ID, jobId);
    Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode(), "Success response");
    Assertions.assertEquals(jobId, jobResponse.getJobId(), "Job Id");
  }

  @Test
  void getJobException() throws JobException {
    String jobId = "Get_Job";

    when(jobService.getJob(TestUtil.ORG_ID, jobId))
        .thenThrow(new JobException("Error while retrieving the job", jobId, null));

    JobException exception =
        assertThrows(JobException.class, () -> jobController.getJob(TestUtil.ORG_ID, jobId));

    Assertions.assertEquals(
        "Error while retrieving the job", exception.getMessage(), "Exception message");

    Assertions.assertEquals(jobId, exception.getJobId(), "Exception JobId");
  }

  @Test
  void getJobsByFilterSuccess() throws JobException {
    List<JobDto> jobList =
        testUtil.createJobList(TestUtil.JOB_TYPE_UPLOAD_TRANSIT_TIMES.name(), TestUtil.ORG_ID);
    PagePayload<JobDto> pagePayloadJobDto =
        testUtil.createPagePayloadJobDto(jobList, jobList.size(), jobList.size(), 1);

    when(jobService.getJobsByJobInfo(any(), any(), any(), any(), any(), anyInt(), anyInt()))
        .thenReturn(pagePayloadJobDto);

    ResponseEntity<BaseResponse<PagePayload<JobDto>>> response =
        jobController.getJobsByFilter(TestUtil.ORG_ID, testUtil.getJobFilters());
    PagePayload<JobDto> responsePage = Objects.requireNonNull(response.getBody()).getPayload();

    verify(jobService, times(1))
        .getJobsByJobInfo(any(), any(), any(), any(), any(), anyInt(), anyInt());

    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode(), "Success response");
    Assertions.assertEquals(
        2, responsePage.getPagination().getTotalPages().intValue(), "Pagination Total pages");
    Assertions.assertEquals(jobList.size(), responsePage.getData().size(), "Paginated Jobs ");
  }

  @Test
  void getJobsByFilterException() throws JobException {
    when(jobService.getJobsByJobInfo(any(), any(), any(), any(), any(), anyInt(), anyInt()))
        .thenThrow(
            new JobException(
                "Error while retrieving the jobs", "jobId1", JobTypeEnum.UPLOAD_TRANSIT_TIMES));

    JobException exception =
        assertThrows(
            JobException.class,
            () -> jobController.getJobsByFilter(TestUtil.ORG_ID, testUtil.getJobFilters()));

    Assertions.assertEquals(
        "Error while retrieving the jobs", exception.getMessage(), "Exception Message");

    Assertions.assertEquals(
        JobTypeEnum.UPLOAD_TRANSIT_TIMES, exception.getJobType(), "Exception Job Type");
  }

  @Test
  void getJobsByFilterPageNoNotAllowedException() throws JobException {
    JobFilters jobFilters = testUtil.getJobFilters();
    jobFilters.setJobType(Optional.of(TestUtil.JOB_TYPE_UPLOAD_TRANSIT_TIMES.name()));
    jobFilters.setPageNo(Optional.of(0));
    when(jobService.getJobsByJobInfo(any(), any(), any(), any(), any(), anyInt(), anyInt()))
        .thenThrow(new RuntimeException());

    JobException exception =
        assertThrows(
            JobException.class, () -> jobController.getJobsByFilter(TestUtil.ORG_ID, jobFilters));

    Assertions.assertEquals(
        "PageNo can not be less than one", exception.getMessage(), "Exception Message");

    Assertions.assertEquals(
        JobTypeEnum.UPLOAD_TRANSIT_TIMES, exception.getJobType(), "Exception Job type");
  }

  @Test
  void getJobRecordsByFilterSuccess() throws JobException {
    List<RecordStatusDto> recordStatusDtos =
        testUtil.createRecordStatusDtoList(
            TestUtil.JOB_ID, TestUtil.JOB_TYPE_UPLOAD_TRANSIT_TIMES, 5, 3);

    when(jobService.getJobResults(any(), any(), any())).thenReturn(recordStatusDtos);

    ResponseEntity<BaseResponse<List<RecordStatusDto>>> response =
        jobController.getJobRecordsByFilter(
            TestUtil.ORG_ID, TestUtil.JOB_ID, Optional.of("SUCCESS"));
    List<RecordStatusDto> responsePage = Objects.requireNonNull(response.getBody()).getPayload();

    verify(jobService, times(1)).getJobResults(any(), any(), any());

    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode(), "Success response");
  }

  @Test
  void getJobRecordsByFilterException() throws JobException {
    when(jobService.getJobResults(any(), any(), any()))
        .thenThrow(
            new JobException(
                "Error while retrieving the job records",
                TestUtil.JOB_ID,
                JobTypeEnum.UPLOAD_TRANSIT_TIMES));

    JobException exception =
        assertThrows(
            JobException.class,
            () ->
                jobController.getJobRecordsByFilter(
                    TestUtil.ORG_ID, TestUtil.JOB_ID, Optional.of("SUCCESS")));

    Assertions.assertEquals(
        "Error while retrieving the job records", exception.getMessage(), "Exception Message");

    Assertions.assertEquals(TestUtil.JOB_ID, exception.getJobId(), "Exception Job Type");
  }
}
