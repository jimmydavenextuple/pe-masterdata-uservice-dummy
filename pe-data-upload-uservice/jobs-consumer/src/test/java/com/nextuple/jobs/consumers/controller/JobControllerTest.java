/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.jobs.consumers.controller;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nextuple.common.base.PagePayload;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.jobs.consumers.common.TestUtil;
import com.nextuple.jobs.consumers.exception.JobDomainException;
import com.nextuple.jobs.consumers.exception.JobException;
import com.nextuple.jobs.consumers.service.JobConsumerService;
import com.nextuple.jobs.framework.common.domain.enums.ApiStatusEnum;
import com.nextuple.jobs.framework.common.domain.enums.JobTypeEnum;
import com.nextuple.jobs.framework.common.domain.outbound.JobResponse;
import com.nextuple.jobs.framework.common.domain.pojo.DefaultPageProperties;
import com.nextuple.jobs.framework.common.domain.pojo.JobDto;
import com.nextuple.jobs.framework.common.domain.pojo.JobFilters;
import com.nextuple.jobs.framework.common.domain.pojo.RecordStatusDto;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

class JobControllerTest {

  @InjectMocks private JobsConsumerController jobsConsumerController;

  @InjectMocks private TestUtil testUtil;

  @Mock private JobConsumerService jobConsumerService;

  @Mock private DefaultPageProperties defaultPageProperties;

  @BeforeEach
  public void init() {
    MockitoAnnotations.openMocks(this);
    //    MockMvcBuilders.standaloneSetup(jobsConsumerController).build();
  }

  @Test
  void getJobRecordsByFilterSuccess() throws JobException {

    Optional<String> status = Optional.of(ApiStatusEnum.SUCCESS.toString());
    List<RecordStatusDto> recordStatusList = testUtil.createRecordStatusDtoList(TestUtil.ORG_ID);

    when(jobConsumerService.getJobResults(any(), any(), any())).thenReturn(recordStatusList);
    ResponseEntity<BaseResponse<List<RecordStatusDto>>> response =
        jobsConsumerController.getJobRecordsByFilter(TestUtil.ORG_ID, TestUtil.JOB_ID, status);
    List<RecordStatusDto> responsePage = Objects.requireNonNull(response.getBody()).getPayload();

    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode(), "Success response");
    Assertions.assertFalse(CollectionUtils.isEmpty(responsePage));
    Assertions.assertEquals(recordStatusList.size(), responsePage.size(), "Paginated data");
  }

  @Test
  void getJobRecordsByFilterFails() throws JobException {

    String jobId = "jobId1";
    when(jobConsumerService.getJobResults(any(), any(), any()))
        .thenThrow(new JobException("Exception while retrieving the job records", jobId, null));

    JobException exception =
        assertThrows(
            JobException.class,
            () ->
                jobsConsumerController.getJobRecordsByFilter(
                    TestUtil.ORG_ID, TestUtil.JOB_ID, Optional.of("SUCCESS")));

    Assertions.assertEquals(jobId, exception.getJobId(), "Job Id");
  }

  @Test
  void createJobSuccess() throws JobException {

    JobDto job = testUtil.createJob(JobTypeEnum.UPLOAD_PROCESSING_LEAD_TIMES, 5);
    when(jobConsumerService.createJob(any()))
        .thenReturn(testUtil.createJobResponse(JobTypeEnum.UPLOAD_PROCESSING_LEAD_TIMES, 5));

    ResponseEntity<BaseResponse<JobResponse>> response = jobsConsumerController.createJob(job);

    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode(), "Success response");

    verify(jobConsumerService, times(1)).createJob(any());
  }

  @Test
  void createJobDuplicateJobException() throws JobException {

    JobDto job = testUtil.createJob(JobTypeEnum.UPLOAD_TRANSIT_TIMES, 5);

    when(jobConsumerService.createJob(any()))
        .thenThrow(
            new JobException("Job already exists for the same job Id", job.getJobId(), null));

    JobException exception =
        assertThrows(JobException.class, () -> jobsConsumerController.createJob(job));

    Assertions.assertEquals(
        "Job already exists for the same job Id", exception.getMessage(), "Expected Error");
  }

  @Test
  void createJobDuplicateRuntimeException() throws JobException {

    JobDto job = testUtil.createJob(JobTypeEnum.UPLOAD_PROCESSING_LEAD_TIMES, 5);

    when(jobConsumerService.createJob(any()))
        .thenThrow(new JobException("Exception while updating the job ", job.getJobId(), null));

    JobException exception =
        assertThrows(JobException.class, () -> jobsConsumerController.createJob(job));

    Assertions.assertEquals(
        "Exception while updating the job ", exception.getMessage(), "Expected Error");
  }

  @Test
  void getJobSuccess() throws JobException {

    JobDto job = testUtil.createJob(JobTypeEnum.UPLOAD_TRANSIT_TIMES, 5);
    when(jobConsumerService.getJob(any(), any())).thenReturn(job);

    ResponseEntity<BaseResponse<JobDto>> response =
        jobsConsumerController.getJob(TestUtil.ORG_ID, job.getJobId());

    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode(), "Success response");

    verify(jobConsumerService, times(1)).getJob(any(), any());
  }

  @Test
  void updateJob() throws JobDomainException {
    JobResponse job = testUtil.createJobResponse(JobTypeEnum.UPLOAD_PROCESSING_LEAD_TIMES, 5);
    when(jobConsumerService.saveJob(any()))
        .thenReturn(testUtil.createJobEntity(JobTypeEnum.UPLOAD_PROCESSING_LEAD_TIMES, 5));

    ResponseEntity<BaseResponse<JobResponse>> response = jobsConsumerController.updateJob(job);
    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    Assertions.assertNotNull(response);
    Assertions.assertNotNull(response.getBody());
    Assertions.assertNotNull(response.getBody().getPayload());
  }

  @Test
  void getJobNotFoundException() throws JobException {

    String jobId = "job123";
    when(jobConsumerService.getJob(any(), any()))
        .thenThrow(new JobException("Job is not found!", jobId, null));

    JobException exception =
        assertThrows(
            JobException.class, () -> jobsConsumerController.getJob(TestUtil.ORG_ID, jobId));

    Assertions.assertEquals("Job is not found!", exception.getMessage(), "Expected Error");
  }

  @Test
  void getJobCTEException() throws JobException {

    String jobId = "job123";
    when(jobConsumerService.getJob(any(), any()))
        .thenThrow(new JobException("Error while retrieving the job", jobId, null));

    JobException exception =
        assertThrows(
            JobException.class, () -> jobsConsumerController.getJob(TestUtil.ORG_ID, jobId));

    Assertions.assertEquals(
        "Error while retrieving the job", exception.getMessage(), "Expected Error");

    Assertions.assertEquals(jobId, exception.getJobId(), "Job Id");
  }

  @Test
  void getJobsByFilterSuccess() throws JobException {

    List<JobResponse> jobDtoList = testUtil.createJobResponseList();
    Page<JobResponse> pageResp = testUtil.createPageJobDto(2, jobDtoList, jobDtoList.size());

    when(defaultPageProperties.getPageNo()).thenReturn(1);
    when(defaultPageProperties.getPageSize()).thenReturn(15);
    when(defaultPageProperties.getSortBy()).thenReturn("created_date");
    when(defaultPageProperties.getSortOrder()).thenReturn("ASC");

    when(jobConsumerService.getJobs(any(), any(), any(), any(), any(), anyInt(), anyInt()))
        .thenReturn(pageResp);

    ResponseEntity<BaseResponse<PagePayload<JobResponse>>> response =
        jobsConsumerController.getJobsByFilter(TestUtil.ORG_ID, testUtil.getJobFilters());
    PagePayload<JobResponse> responsePage = Objects.requireNonNull(response.getBody()).getPayload();

    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode(), "Success response");
    Assertions.assertEquals(
        2, (int) responsePage.getPagination().getTotalPages(), "Pagination Total pages");
    Assertions.assertEquals(
        jobDtoList.size(), (int) responsePage.getPagination().getTotalRecords(), "Total Elements");
    Assertions.assertEquals(
        1, (int) responsePage.getPagination().getCurrentPage(), "Current page number");
    Assertions.assertEquals(jobDtoList.size(), responsePage.getData().size(), "Paginated data");
    Assertions.assertTrue(
        ObjectUtils.isEmpty(responsePage.getPagination().getPrevious()), "Previous Uri");
    Assertions.assertEquals(
        Boolean.TRUE,
        Objects.nonNull(responsePage.getPagination().getNext()),
        "Next Uri should not be null");
  }

  @Test
  void getJobsByFilterLastPageSuccess() throws JobException {

    JobFilters jobFilters = testUtil.getJobFilters();
    jobFilters.setPageNo(Optional.of(2));
    List<JobResponse> jobDtoList = testUtil.createJobResponseList();
    Page<JobResponse> pageResp = testUtil.createPageJobDto(2, jobDtoList, jobDtoList.size());

    when(defaultPageProperties.getPageNo()).thenReturn(1);
    when(defaultPageProperties.getPageSize()).thenReturn(15);
    when(defaultPageProperties.getSortBy()).thenReturn("created_date");
    when(defaultPageProperties.getSortOrder()).thenReturn("ASC");
    when(jobConsumerService.getJobs(any(), any(), any(), any(), any(), anyInt(), anyInt()))
        .thenReturn(pageResp);

    ResponseEntity<BaseResponse<PagePayload<JobResponse>>> response =
        jobsConsumerController.getJobsByFilter(TestUtil.ORG_ID, jobFilters);
    PagePayload<JobResponse> responsePage = Objects.requireNonNull(response.getBody()).getPayload();

    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode(), "Success response");
    Assertions.assertEquals(
        2, (int) responsePage.getPagination().getTotalPages(), "Pagination Total pages");
    Assertions.assertEquals(
        jobDtoList.size(), (int) responsePage.getPagination().getTotalRecords(), "Total Elements");
    Assertions.assertEquals(
        2, (int) responsePage.getPagination().getCurrentPage(), "Current page number");
    Assertions.assertEquals(jobDtoList.size(), responsePage.getData().size(), "Paginated data");
    Assertions.assertTrue(ObjectUtils.isEmpty(responsePage.getPagination().getNext()), "Next Uri");
    Assertions.assertEquals(
        Boolean.TRUE,
        Objects.nonNull(responsePage.getPagination().getPrevious()),
        "Previous Uri should not be null");
  }

  @Test
  void getJobsByFilterPageNoNotAllowed() {

    when(defaultPageProperties.getPageNo()).thenReturn(1);
    when(defaultPageProperties.getPageSize()).thenReturn(15);
    when(defaultPageProperties.getSortBy()).thenReturn("created_date");
    when(defaultPageProperties.getSortOrder()).thenReturn("ASC");
    JobFilters jobFilters = testUtil.getJobFilters();
    jobFilters.setPageNo(Optional.of(0));
    JobException exception =
        assertThrows(
            JobException.class,
            () -> jobsConsumerController.getJobsByFilter(TestUtil.ORG_ID, jobFilters));

    Assertions.assertEquals(
        "PageNo can not be less than one", exception.getMessage(), "Exception message");

    Assertions.assertEquals(0, exception.getPageNo().intValue(), "Exception pageNo");
  }

  @Test
  void getJobsByFilterException() throws JobException {
    when(defaultPageProperties.getPageNo()).thenReturn(1);
    when(defaultPageProperties.getPageSize()).thenReturn(15);
    when(defaultPageProperties.getSortBy()).thenReturn("created_date");
    when(defaultPageProperties.getSortOrder()).thenReturn("ASC");
    when(jobConsumerService.getJobs(any(), any(), any(), any(), any(), anyInt(), anyInt()))
        .thenThrow(new JobException("Exception while retrieving the list of jobs", null, 1));

    JobException exception =
        assertThrows(
            JobException.class,
            () ->
                jobsConsumerController.getJobsByFilter(TestUtil.ORG_ID, testUtil.getJobFilters()));

    Assertions.assertEquals(
        "Exception while retrieving the list of jobs", exception.getMessage(), "Exception message");

    Assertions.assertEquals(1, exception.getPageNo().intValue(), "Exception pageNo");
  }

  @Test
  void getJobRecordsByFilters() throws JobException {
    RecordStatusDto recordStatusDto =
        testUtil.createRecordStatus(
            TestUtil.JOB_ID,
            TestUtil.ORG_ID,
            ApiStatusEnum.FAILURE,
            HttpStatus.BAD_REQUEST,
            null,
            JobTypeEnum.UPLOAD_NODE_CARRIER,
            1);
    when(defaultPageProperties.getPageNo()).thenReturn(1);
    when(defaultPageProperties.getPageSize()).thenReturn(15);
    when(jobConsumerService.getJobResults(anyString(), anyString(), any(), anyInt(), anyInt()))
        .thenReturn(testUtil.createPageRecordStatusDtoDto(5, List.of(recordStatusDto), 20));

    ResponseEntity<BaseResponse<PagePayload<RecordStatusDto>>> response =
        jobsConsumerController.getJobRecordsByFilters(
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
  void getJobRecordsByFiltersInvalidPageNo() {
    RecordStatusDto recordStatusDto =
        testUtil.createRecordStatus(
            TestUtil.JOB_ID,
            TestUtil.ORG_ID,
            ApiStatusEnum.FAILURE,
            HttpStatus.BAD_REQUEST,
            null,
            JobTypeEnum.UPLOAD_NODE_CARRIER,
            1);

    Exception exception =
        Assertions.assertThrows(
            JobException.class,
            () ->
                jobsConsumerController.getJobRecordsByFilters(
                    TestUtil.ORG_ID,
                    TestUtil.JOB_ID,
                    Optional.of(ApiStatusEnum.FAILURE.name()),
                    Optional.of(0),
                    Optional.of(15)));

    Assertions.assertNotNull(exception);
  }
}
