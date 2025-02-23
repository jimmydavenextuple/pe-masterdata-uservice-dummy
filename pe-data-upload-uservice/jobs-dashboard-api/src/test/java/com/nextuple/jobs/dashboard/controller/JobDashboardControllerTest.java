/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.jobs.dashboard.controller;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nextuple.common.base.PagePayload;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.jobs.dashboard.common.TestUtil;
import com.nextuple.jobs.dashboard.exception.JobException;
import com.nextuple.jobs.dashboard.service.JobService;
import com.nextuple.jobs.dashboard.service.JobsFpmService;
import com.nextuple.jobs.framework.common.domain.enums.ApiStatusEnum;
import com.nextuple.jobs.framework.common.domain.enums.JobStatusEnum;
import com.nextuple.jobs.framework.common.domain.enums.JobTypeEnum;
import com.nextuple.jobs.framework.common.domain.outbound.JobResponse;
import com.nextuple.jobs.framework.common.domain.outbound.JobResponseForNotification;
import com.nextuple.jobs.framework.common.domain.pojo.AuditLog;
import com.nextuple.jobs.framework.common.domain.pojo.DefaultPageProperties;
import com.nextuple.jobs.framework.common.domain.pojo.JobDto;
import com.nextuple.jobs.framework.common.domain.pojo.JobFilters;
import com.nextuple.jobs.framework.common.domain.pojo.RecordStatusDto;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

class JobDashboardControllerTest {

  @InjectMocks private JobDashboardController jobDashboardController;

  @Mock private JobService jobService;
  @Mock private JobsFpmService jobsFpmService;

  @Mock private DefaultPageProperties defaultPageProperties;

  @InjectMocks private TestUtil testUtil;

  @BeforeEach
  public void init() {
    MockitoAnnotations.openMocks(this);
    //    MockMvcBuilders.standaloneSetup(jobDashboardController).build();
  }

  @Test
  void processJobOffline() throws JobException {
    String csvContents = TestUtil.CSV_CONTENTS_PROCESSING_LEAD_TIMES;
    ByteArrayResource byteArrayResource = new ByteArrayResource(csvContents.getBytes());

    when(jobService.processJobOffline(
            byteArrayResource,
            TestUtil.ORG_ID,
            TestUtil.JOB_TYPE_UPLOAD_PROCESSING_LEAD_TIMES,
            TestUtil.fileName))
        .thenReturn(new JobResponse());
    ResponseEntity<BaseResponse<JobResponse>> responseEntity =
        jobDashboardController.processJobOffline(
            TestUtil.ORG_ID,
            TestUtil.JOB_TYPE_UPLOAD_PROCESSING_LEAD_TIMES,
            byteArrayResource,
            TestUtil.fileName);
    Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode(), "Status code");
  }

  @Test
  void processJobOfflineException() throws JobException {
    String csvContents = TestUtil.CSV_CONTENTS_PROCESSING_LEAD_TIMES;
    ByteArrayResource byteArrayResource = new ByteArrayResource(csvContents.getBytes());

    when(jobService.processJobOffline(any(), any(), any(), any()))
        .thenThrow(
            new JobException(
                "Error while processing csv/json file",
                "jobId1",
                JobTypeEnum.UPLOAD_PROCESSING_LEAD_TIMES));

    JobException exception =
        assertThrows(
            JobException.class,
            () ->
                jobDashboardController.processJobOffline(
                    TestUtil.JOB_ID,
                    TestUtil.JOB_TYPE_UPLOAD_PROCESSING_LEAD_TIMES,
                    byteArrayResource,
                    TestUtil.fileName));

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
            testUtil.createJobResponse(
                TestUtil.JOB_ID,
                TestUtil.ORG_ID,
                JobStatusEnum.SUBMITTED,
                Collections.singletonList(new AuditLog()),
                TestUtil.JOB_TYPE_UPLOAD_PROCESSING_LEAD_TIMES));

    ResponseEntity<BaseResponse<JobResponse>> responseEntity =
        jobDashboardController.processJobJsonOffline(
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
                jobDashboardController.processJobJsonOffline(
                    JobTypeEnum.UPLOAD_PROCESSING_LEAD_TIMES, TestUtil.ORG_ID, request));

    Assertions.assertNotNull(exception);

    verify(jobService, times(1))
        .processJobJsonOffline(
            anyString(), anyString(), any(JobTypeEnum.class), any(Optional.class));
  }

  @Test
  void processJobJsonOfflineUpdateExistingJob() throws JobException {

    when(jobService.processJobJsonOffline(any(), any(), any())).thenReturn(new JobResponse());
    ResponseEntity<BaseResponse<JobResponse>> responseEntity =
        jobDashboardController.processJobJsonByScheduler(
            TestUtil.ORG_ID, JobTypeEnum.UPLOAD_PROCESSING_LEAD_TIMES, TestUtil.JOB_ID);
    Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode(), "Status code");

    verify(jobService, times(1)).processJobJsonOffline(any(), any(), any());
  }

  @Test
  void processJobJsonOffline2() throws JobException {

    when(jobService.processJobJsonOffline(any(), any(), any())).thenReturn(new JobResponse());
    ResponseEntity<BaseResponse<JobResponse>> responseEntity =
        jobDashboardController.processJobJsonByScheduler(
            TestUtil.ORG_ID, JobTypeEnum.UPLOAD_PROCESSING_LEAD_TIMES, "");
    Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode(), "Status code");

    verify(jobService, times(1)).processJobJsonOffline(any(), any(), any());
  }

  @Test
  void processJobJsonOffline2Exception() throws JobException {

    when(jobService.processJobJsonOffline(any(), any(), any()))
        .thenThrow(
            new JobException(
                "Error while processing json request", "jobId1", JobTypeEnum.UPLOAD_TRANSIT_TIMES));

    JobException exception =
        assertThrows(
            JobException.class,
            () ->
                jobDashboardController.processJobJsonByScheduler(
                    TestUtil.ORG_ID, JobTypeEnum.UPLOAD_PROCESSING_LEAD_TIMES, ""));

    Assertions.assertEquals(
        "Error while processing json request", exception.getMessage(), "Exception Message");

    Assertions.assertEquals(
        JobTypeEnum.UPLOAD_TRANSIT_TIMES, exception.getJobType(), "Exception Job type");
    verify(jobService, times(1)).processJobJsonOffline(any(), any(), any());
  }

  @Test
  void processJobJsonOfflineException() throws JobException {

    when(jobService.processJobJsonOffline(any(), any(), any()))
        .thenThrow(
            new JobException(
                "Error while processing json request",
                "jobId1",
                JobTypeEnum.UPLOAD_PROCESSING_LEAD_TIMES));

    JobException exception =
        assertThrows(
            JobException.class,
            () ->
                jobDashboardController.processJobJsonByScheduler(
                    TestUtil.ORG_ID, JobTypeEnum.UPLOAD_PROCESSING_LEAD_TIMES, ""));

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
            JobTypeEnum.UPLOAD_TRANSIT_TIMES,
            null);

    when(jobService.getJob(TestUtil.ORG_ID, jobId)).thenReturn(job);
    ResponseEntity<BaseResponse<JobDto>> responseEntity =
        jobDashboardController.getJob(TestUtil.ORG_ID, jobId);
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
        assertThrows(
            JobException.class, () -> jobDashboardController.getJob(TestUtil.ORG_ID, jobId));

    Assertions.assertEquals(
        "Error while retrieving the job", exception.getMessage(), "Exception message");

    Assertions.assertEquals(jobId, exception.getJobId(), "Exception JobId");
  }

  @Test
  void getJobsByFilterSuccess() throws JobException {
    List<JobDto> jobList =
        testUtil.createJobList(TestUtil.JOB_TYPE_UPLOAD_TRANSIT_TIMES.name(), TestUtil.ORG_ID);
    PagePayload<JobResponse> pagePayloadJobDto =
        testUtil.createPagePayloadJobDto(jobList, jobList.size(), jobList.size(), 1);
    when(defaultPageProperties.getPageNo()).thenReturn(1);
    when(defaultPageProperties.getPageSize()).thenReturn(15);
    when(jobService.getJobsByJobInfo(any(), any(), any(), any(), any(), anyInt(), anyInt()))
        .thenReturn(pagePayloadJobDto);

    ResponseEntity<BaseResponse<PagePayload<JobResponse>>> response =
        jobDashboardController.getJobsByFilter(TestUtil.ORG_ID, testUtil.getJobFilters());
    PagePayload<JobResponse> responsePage = Objects.requireNonNull(response.getBody()).getPayload();

    verify(jobService, times(1))
        .getJobsByJobInfo(any(), any(), any(), any(), any(), anyInt(), anyInt());

    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode(), "Success response");
    Assertions.assertEquals(
        2, responsePage.getPagination().getTotalPages().intValue(), "Pagination Total pages");
    Assertions.assertEquals(jobList.size(), responsePage.getData().size(), "Paginated Jobs ");
  }

  @Test
  void getJobsByFilterException() throws JobException {
    when(defaultPageProperties.getPageNo()).thenReturn(1);
    when(defaultPageProperties.getPageSize()).thenReturn(15);
    when(jobService.getJobsByJobInfo(any(), any(), any(), any(), any(), anyInt(), anyInt()))
        .thenThrow(
            new JobException(
                "Error while retrieving the jobs", "jobId1", JobTypeEnum.UPLOAD_TRANSIT_TIMES));

    JobException exception =
        assertThrows(
            JobException.class,
            () ->
                jobDashboardController.getJobsByFilter(TestUtil.ORG_ID, testUtil.getJobFilters()));

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
    when(defaultPageProperties.getPageNo()).thenReturn(1);
    when(defaultPageProperties.getPageSize()).thenReturn(15);
    when(jobService.getJobsByJobInfo(any(), any(), any(), any(), any(), anyInt(), anyInt()))
        .thenThrow(new RuntimeException());

    JobException exception =
        assertThrows(
            JobException.class,
            () -> jobDashboardController.getJobsByFilter(TestUtil.ORG_ID, jobFilters));

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
        jobDashboardController.getJobRecordsByFilter(
            TestUtil.ORG_ID, TestUtil.JOB_ID, Optional.of("SUCCESS"));

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
                jobDashboardController.getJobRecordsByFilter(
                    TestUtil.ORG_ID, TestUtil.JOB_ID, Optional.of("SUCCESS")));

    Assertions.assertEquals(
        "Error while retrieving the job records", exception.getMessage(), "Exception Message");

    Assertions.assertEquals(TestUtil.JOB_ID, exception.getJobId(), "Exception Job Type");
  }

  @Test
  void processJobOfflineWith() throws JobException {
    JobResponse job =
        testUtil.createJobResponse(
            "jobId1",
            TestUtil.ORG_ID,
            JobStatusEnum.SUBMITTED,
            Collections.singletonList(testUtil.createAuditLog(JobStatusEnum.SUBMITTED)),
            JobTypeEnum.TRANSIT_BUFFER_REQUEST);

    when(jobService.processJobOffline(anyString(), any(), anyLong())).thenReturn(job);

    ResponseEntity<BaseResponse<JobResponse>> response =
        jobDashboardController.processJobOfflineWithFileMetaDataId(
            TestUtil.ORG_ID, JobTypeEnum.TRANSIT_BUFFER_REQUEST, 23456L);

    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    Assertions.assertNotNull(response);
    Assertions.assertNotNull(response.getBody());
    Assertions.assertNotNull(response.getBody().getPayload());
    Assertions.assertFalse(ObjectUtils.isEmpty(response.getBody().getPayload().getJobId()));
  }

  @Test
  void getJobRecordsByFilters() throws JobException {
    RecordStatusDto recordStatusDto =
        testUtil.createRecordStatusDto(
            TestUtil.JOB_ID, JobTypeEnum.UPLOAD_NODE_CARRIER, 1, HttpStatus.OK.value());
    PagePayload<RecordStatusDto> payload =
        testUtil.createPagePayloadRecordStatusDto(List.of(recordStatusDto), 5, 10, 1);
    when(defaultPageProperties.getPageNo()).thenReturn(1);
    when(defaultPageProperties.getPageSize()).thenReturn(15);
    when(jobService.getJobRecordsByFilters(anyString(), anyString(), any(), anyInt(), anyInt()))
        .thenReturn(payload);

    ResponseEntity<BaseResponse<PagePayload<RecordStatusDto>>> response =
        jobDashboardController.getJobRecordsByFilters(
            TestUtil.ORG_ID,
            TestUtil.JOB_ID,
            Optional.of(ApiStatusEnum.FAILURE.name()),
            Optional.of(1),
            Optional.of(15));

    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    Assertions.assertNotNull(response);
    Assertions.assertNotNull(response.getBody());
    Assertions.assertNotNull(response.getBody().getPayload());
    Assertions.assertFalse(CollectionUtils.isEmpty(response.getBody().getPayload().getData()));
  }

  @Test
  @DisplayName("Test for successful retrieval of jobs")
  void getJobsByFilterV2_Success() throws JobException, com.nextuple.plt.exception.JobException {
    JobFilters jobFilters = new JobFilters();
    jobFilters.setPageNo(Optional.of(1));
    jobFilters.setPageSize(Optional.of(10));

    PagePayload<JobResponseForNotification> expectedPayload = new PagePayload<>();
    when(jobsFpmService.getJobResponseForNotificationPagePayload(
            TestUtil.ORG_ID, jobFilters, 1, 10))
        .thenReturn(expectedPayload);

    when(defaultPageProperties.getPageNo()).thenReturn(1);
    when(defaultPageProperties.getPageSize()).thenReturn(15);

    ResponseEntity<BaseResponse<PagePayload<JobResponseForNotification>>> response =
        jobDashboardController.getJobsByFilterV2(TestUtil.ORG_ID, jobFilters);

    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    Assertions.assertNotNull(response.getBody());
    Assertions.assertEquals(expectedPayload, response.getBody().getPayload());
    verify(jobsFpmService, times(1))
        .getJobResponseForNotificationPagePayload(any(), any(), anyInt(), anyInt());
    verify(defaultPageProperties, times(1)).getPageNo();
    verify(defaultPageProperties, times(1)).getPageSize();
  }

  @Test
  @DisplayName("Test for default page number and size handling")
  void getJobsByFilterV2_DefaultPageNumberAndSize()
      throws JobException, com.nextuple.plt.exception.JobException {
    JobFilters jobFilters = new JobFilters();

    PagePayload<JobResponseForNotification> expectedPayload = new PagePayload<>();
    when(jobsFpmService.getJobResponseForNotificationPagePayload(
            TestUtil.ORG_ID, jobFilters, 1, 15))
        .thenReturn(expectedPayload);

    when(defaultPageProperties.getPageNo()).thenReturn(1);
    when(defaultPageProperties.getPageSize()).thenReturn(15);

    ResponseEntity<BaseResponse<PagePayload<JobResponseForNotification>>> response =
        jobDashboardController.getJobsByFilterV2(TestUtil.ORG_ID, jobFilters);

    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    Assertions.assertNotNull(response.getBody());
    Assertions.assertEquals(expectedPayload, response.getBody().getPayload());
    verify(jobsFpmService, times(1))
        .getJobResponseForNotificationPagePayload(any(), any(), anyInt(), anyInt());
    verify(defaultPageProperties, times(1)).getPageNo();
    verify(defaultPageProperties, times(1)).getPageSize();
  }

  @Test
  @DisplayName("Test for invalid page number handling")
  void getJobsByFilterV2_InvalidPageNumber() throws JobException {
    JobFilters jobFilters = new JobFilters();
    jobFilters.setPageNo(Optional.of(0));
    when(defaultPageProperties.getPageNo()).thenReturn(1);
    when(defaultPageProperties.getPageSize()).thenReturn(15);
    JobException exception =
        assertThrows(
            JobException.class,
            () -> jobDashboardController.getJobsByFilterV2(TestUtil.ORG_ID, jobFilters));
    Assertions.assertEquals("PageNo can not be less than one", exception.getMessage());
    Assertions.assertNull(exception.getJobType());
    verify(defaultPageProperties, times(1)).getPageNo();
    verify(defaultPageProperties, times(1)).getPageSize();
  }

  @Test
  @DisplayName("Test for service exception handling")
  void getJobsByFilterV2_ServiceException()
      throws JobException, com.nextuple.plt.exception.JobException {
    JobFilters jobFilters = new JobFilters();
    when(jobsFpmService.getJobResponseForNotificationPagePayload(any(), any(), anyInt(), anyInt()))
        .thenThrow(new JobException("Error from service", null));

    when(defaultPageProperties.getPageNo()).thenReturn(1);
    when(defaultPageProperties.getPageSize()).thenReturn(15);
    assertThrows(
        JobException.class,
        () -> jobDashboardController.getJobsByFilterV2(TestUtil.ORG_ID, jobFilters));
    verify(jobsFpmService, times(1))
        .getJobResponseForNotificationPagePayload(any(), any(), anyInt(), anyInt());
  }
}
