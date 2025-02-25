/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.jobs.dashboard.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nextuple.common.base.PagePayload;
import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.csvdownload.common.pojo.TransitDataUpload;
import com.nextuple.jobs.consumers.authentication.AuthService;
import com.nextuple.jobs.consumers.domain.JobDomain;
import com.nextuple.jobs.consumers.domain.entity.JobEntity;
import com.nextuple.jobs.consumers.exception.JobDomainException;
import com.nextuple.jobs.consumers.service.AuthTokenService;
import com.nextuple.jobs.dashboard.common.TestUtil;
import com.nextuple.jobs.dashboard.exception.JobException;
import com.nextuple.jobs.framework.common.clients.FileMetaDataClient;
import com.nextuple.jobs.framework.common.clients.JobsConsumerClient;
import com.nextuple.jobs.framework.common.domain.enums.ApiStatusEnum;
import com.nextuple.jobs.framework.common.domain.enums.JobStatusEnum;
import com.nextuple.jobs.framework.common.domain.enums.JobTypeEnum;
import com.nextuple.jobs.framework.common.domain.outbound.FileMetaDataResponse;
import com.nextuple.jobs.framework.common.domain.outbound.JobResponse;
import com.nextuple.jobs.framework.common.domain.pojo.JobDto;
import com.nextuple.jobs.framework.common.domain.pojo.RecordDto;
import com.nextuple.jobs.framework.common.domain.pojo.RecordStatusDto;
import com.nextuple.jobs.framework.common.domain.pojo.TransitBufferUpload;
import com.nextuple.jobs.framework.common.service.FileService;
import com.opencsv.exceptions.CsvException;
import feign.FeignException;
import feign.Request;
import feign.Request.HttpMethod;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

class JobServiceTest {

  @InjectMocks private JobService jobService;

  @InjectMocks private TestUtil testUtil;

  @Mock private JobsConsumerClient jobsConsumerClient;

  @Mock private JobDomain jobDomain;

  @Mock private AuthTokenService authTokenService;

  @Mock private KafkaTemplate<String, Object> kafkaTemplate;

  @Mock private FileMetaDataClient fileMetaDataClient;

  @Mock private FileService fileService;

  @Mock private AuthService authService;

  @Mock private ProcessFileContentsMapperFactory processFileContentsMapperFactory;

  @Mock private ProcessFileContents processFileContents;

  @Captor ArgumentCaptor<List<Object>> objectListCaptor;

  @BeforeEach
  public void init() {
    MockitoAnnotations.openMocks(this);
    when(authTokenService.generateAuthToken()).thenReturn(testUtil.getAuthTokenResponse());
    Message<RecordDto> message = MessageBuilder.withPayload(testUtil.getRecordDto()).build();
    when(authService.setAuthHeaders(any())).thenReturn(message);
  }

  private final String UPLOAD_PROCESSING_LEAD_TIME_LIST =
      "[{\"nodeId\": \"node-1\",\"orgId\": \"BAY\",\"carrierServiceId\": \"ALL-SDND\",\"serviceOption\": \"SDND\",\"processingTime\": 20.32,\"lastPickupTime\": \"12:22\"}]";

  private static final String ERRONEOUS_JSON_LIST =
      "[{\"nodeId\": ,\"orgId\": \"BAY\",\"carrierServiceId\": \"ALL-SDND\",\"serviceOption\": \"SDND\"}]";

  @Nested
  class ProcessJobOffline {

    @Test
    void processJobOffline() throws JobException {
      String csvContents = TestUtil.CSV_CONTENTS_PROCESSING_LEAD_TIMES;
      ByteArrayResource byteArrayResource = new ByteArrayResource(csvContents.getBytes());

      JobResponse job =
          testUtil.createJobResponse(
              "jobId1",
              TestUtil.ORG_ID,
              JobStatusEnum.SUBMITTED,
              Collections.singletonList(testUtil.createAuditLog(JobStatusEnum.SUBMITTED)),
              JobTypeEnum.UPLOAD_PROCESSING_LEAD_TIMES);

      when(jobsConsumerClient.createJob(any()))
          .thenReturn(
              BaseResponse.builder()
                  .message("Retrieved job" + " id " + " " + "successfully!!")
                  .payload(job)
                  .build());

      JobResponse jobDto =
          jobService.processJobOffline(
              byteArrayResource,
              TestUtil.ORG_ID,
              JobTypeEnum.UPLOAD_PROCESSING_LEAD_TIMES,
              TestUtil.fileName);

      Assertions.assertNotNull(jobDto);
      verify(jobsConsumerClient, times(1)).createJob(any());
    }

    @Test
    void processJobOfflineException() {
      String csvContents = TestUtil.CSV_CONTENTS_PROCESSING_LEAD_TIMES;
      ByteArrayResource byteArrayResource = new ByteArrayResource(csvContents.getBytes());

      when(jobsConsumerClient.createJob(any())).thenThrow(new RuntimeException());

      Exception exception =
          Assertions.assertThrows(
              JobException.class,
              () ->
                  jobService.processJobOffline(
                      byteArrayResource,
                      TestUtil.ORG_ID,
                      JobTypeEnum.UPLOAD_PROCESSING_LEAD_TIMES,
                      TestUtil.fileName));

      Assertions.assertNotNull(exception);
    }

    @Test
    void processJobOfflineFeignException() {
      String csvContents = TestUtil.CSV_CONTENTS_PROCESSING_LEAD_TIMES;
      ByteArrayResource byteArrayResource = new ByteArrayResource(csvContents.getBytes());
      Map<String, Collection<String>> headers = new HashMap<>();
      when(jobsConsumerClient.createJob(any()))
          .thenThrow(
              new FeignException.BadRequest(
                  "Error when fetching fsaList",
                  Request.create(HttpMethod.POST, "", new HashMap<>(), null, null, null),
                  "Error while creating job".getBytes(),
                  headers));

      Exception exception =
          Assertions.assertThrows(
              JobException.class,
              () ->
                  jobService.processJobOffline(
                      byteArrayResource,
                      TestUtil.ORG_ID,
                      JobTypeEnum.UPLOAD_PROCESSING_LEAD_TIMES,
                      TestUtil.fileName));

      Assertions.assertNotNull(exception);
    }
  }

  @Nested
  class GetJob {

    @Test
    void getJob() throws JobException {
      String jobId = "Job1";
      JobDto job =
          testUtil.createJob(
              jobId,
              TestUtil.ORG_ID,
              JobStatusEnum.SUBMITTED,
              Collections.singletonList(testUtil.createAuditLog(JobStatusEnum.SUBMITTED)),
              JobTypeEnum.UPLOAD_PROCESSING_LEAD_TIMES,
              null);
      when(jobsConsumerClient.getJob(TestUtil.ORG_ID, job.getJobId()))
          .thenReturn(
              BaseResponse.builder()
                  .message("Retrieved job" + " id " + " " + "successfully!!")
                  .payload(job)
                  .build());
      JobDto result = jobService.getJob(TestUtil.ORG_ID, jobId);

      Assertions.assertEquals(jobId, result.getJobId(), "Job Id");
      verify(jobsConsumerClient, times(1)).getJob(any(), any());
    }

    @Test
    void getJobException() {
      String jobId = "Job1";
      when(jobsConsumerClient.getJob(anyString(), anyString())).thenThrow(new RuntimeException());

      JobException exception =
          assertThrows(JobException.class, () -> jobService.getJob(TestUtil.ORG_ID, jobId));

      Assertions.assertEquals(
          "Error while retrieving the job", exception.getMessage(), "Expected Error");

      Assertions.assertEquals(jobId, exception.getJobId(), "Expected JobId");
      verify(jobsConsumerClient, times(1)).getJob(any(), any());
    }

    @Test
    void getJobFeignException() {
      String jobId = "Job1";
      JobDto job =
          testUtil.createJob(
              jobId,
              TestUtil.ORG_ID,
              JobStatusEnum.SUBMITTED,
              Collections.singletonList(testUtil.createAuditLog(JobStatusEnum.SUBMITTED)),
              JobTypeEnum.UPLOAD_PROCESSING_LEAD_TIMES,
              null);
      Map<String, Collection<String>> headers = new HashMap<>();
      when(jobsConsumerClient.getJob(TestUtil.ORG_ID, job.getJobId()))
          .thenThrow(
              new FeignException.BadRequest(
                  "",
                  Request.create(HttpMethod.GET, "", new HashMap<>(), null, null, null),
                  "Error while retrieving the job".getBytes(),
                  headers));

      JobException exception =
          assertThrows(JobException.class, () -> jobService.getJob(TestUtil.ORG_ID, jobId));

      Assertions.assertEquals(
          "Upstream error : Error while retrieving the job",
          exception.getMessage(),
          "Expected Error");
      verify(jobsConsumerClient, times(1)).getJob(any(), any());
    }
  }

  @Nested
  class GetJobsByJobInfo {

    @Test
    void getJobsByJobInfo() throws JobException {

      List<JobDto> jobList =
          testUtil.createJobList(
              TestUtil.JOB_TYPE_UPLOAD_PROCESSING_LEAD_TIMES.name(), TestUtil.ORG_ID);
      PagePayload<JobResponse> pagePayloadJobDto =
          testUtil.createPagePayloadJobDto(jobList, jobList.size(), jobList.size(), 1);

      when(jobsConsumerClient.getJobsByFilter(
              any(), any(), any(), any(), any(), anyInt(), anyInt()))
          .thenReturn(
              BaseResponse.builder()
                  .message("Retrieval of jobs is successful")
                  .payload(pagePayloadJobDto)
                  .build());

      PagePayload<JobResponse> jobsByJobInfo =
          jobService.getJobsByJobInfo(
              TestUtil.ORG_ID,
              Optional.of(TestUtil.JOB_TYPE_UPLOAD_PROCESSING_LEAD_TIMES.name()),
              Optional.of(5),
              Optional.empty(),
              Optional.empty(),
              1,
              2);

      Assertions.assertEquals(
          2, jobsByJobInfo.getPagination().getTotalPages().intValue(), "Pagination Total pages");
      Assertions.assertEquals(jobList, jobsByJobInfo.getData(), "Paginated Jobs ");
      verify(jobsConsumerClient, times(1))
          .getJobsByFilter(any(), any(), any(), any(), any(), anyInt(), anyInt());
    }

    @Test
    void getJobsByJobInfoException() {
      when(jobsConsumerClient.getJobsByFilter(
              any(), any(), any(), any(), any(), anyInt(), anyInt()))
          .thenThrow(new RuntimeException());

      JobException exception =
          assertThrows(
              JobException.class,
              () ->
                  jobService.getJobsByJobInfo(
                      TestUtil.ORG_ID,
                      Optional.of(TestUtil.JOB_TYPE_UPLOAD_TRANSIT_TIMES.name()),
                      Optional.of(5),
                      Optional.empty(),
                      Optional.empty(),
                      1,
                      2));

      Assertions.assertEquals(
          "Exception while retrieving the jobs", exception.getMessage(), "Exception message ");
      verify(jobsConsumerClient, times(1))
          .getJobsByFilter(any(), any(), any(), any(), any(), anyInt(), anyInt());
    }

    @Test
    void getJobsByJobInfoFeignException() {
      Map<String, Collection<String>> headers = new HashMap<>();
      when(jobsConsumerClient.getJobsByFilter(
              any(), any(), any(), any(), any(), anyInt(), anyInt()))
          .thenThrow(
              new FeignException.BadRequest(
                  "",
                  Request.create(HttpMethod.GET, "", new HashMap<>(), null, null, null),
                  "Error while retrieving the job".getBytes(),
                  headers));

      JobException exception =
          assertThrows(
              JobException.class,
              () ->
                  jobService.getJobsByJobInfo(
                      TestUtil.ORG_ID,
                      Optional.of(TestUtil.JOB_TYPE_UPLOAD_TRANSIT_TIMES.name()),
                      Optional.of(5),
                      Optional.empty(),
                      Optional.empty(),
                      1,
                      2));

      Assertions.assertEquals(
          "Upstream error : Error while retrieving the job",
          exception.getMessage(),
          "Exception message ");
      verify(jobsConsumerClient, times(1))
          .getJobsByFilter(any(), any(), any(), any(), any(), anyInt(), anyInt());
    }
  }

  @Nested
  class ProcessJobJsonOffline {

    @Test
    void processJobJsonOfflineSuccess()
        throws JobException, JobDomainException, IOException, CsvException, CommonServiceException {
      TransitDataUpload transitDataUpload = testUtil.getTransitDataUpload();
      JobResponse jobResponse =
          testUtil.createJobResponse(
              "jobId1",
              TestUtil.ORG_ID,
              JobStatusEnum.SUBMITTED,
              Collections.singletonList(testUtil.createAuditLog(JobStatusEnum.SUBMITTED)),
              JobTypeEnum.UPLOAD_PROCESSING_LEAD_TIMES);
      jobResponse.setFailureCount(0);

      JobDto job =
          testUtil.createJob(
              "jobId1",
              TestUtil.ORG_ID,
              JobStatusEnum.SUBMITTED,
              Collections.singletonList(testUtil.createAuditLog(JobStatusEnum.SUBMITTED)),
              JobTypeEnum.UPLOAD_PROCESSING_LEAD_TIMES,
              null);
      job.setFile(TestUtil.CSV_CONTENTS_PROCESSING_LEAD_TIMES.getBytes());
      job.setTotalRecords(3);
      job.setRemainingRecords(3);
      job.setFileMetaDataId(1L);
      job.setFailureCount(0);

      when(fileMetaDataClient.findFileMetadataById(any(), any()))
          .thenReturn(
              BaseResponse.builder().payload(testUtil.getFileMetaDataResponse(23456L)).build());

      when(fileService.getFile(any(), any())).thenReturn(testUtil.getFileResponse());

      when(jobsConsumerClient.getJob(any(), any(), any()))
          .thenReturn(
              BaseResponse.builder()
                  .message("Retrieved job" + " id " + " " + "successfully!!")
                  .payload(job)
                  .build());

      when(processFileContentsMapperFactory.getProcessFileContentsMapper(any()))
          .thenReturn(processFileContents);
      when(processFileContents.updateRequestObjectsList(any(), any()))
          .thenReturn(List.of(transitDataUpload, transitDataUpload, transitDataUpload));

      when(jobsConsumerClient.createJob(any()))
          .thenReturn(
              BaseResponse.builder()
                  .message("Retrieved job" + " id " + " " + "successfully!!")
                  .payload(jobResponse)
                  .build());

      jobResponse.setTotalRecords(5);
      jobResponse.setRemainingRecords(5);
      when(jobsConsumerClient.updateJob(any(), any()))
          .thenReturn(BaseResponse.builder().payload(jobResponse).build());

      JobEntity jobEntity =
          testUtil.createJobEntity(
              "jobId1",
              TestUtil.ORG_ID,
              JobStatusEnum.PROCESSED,
              List.of(testUtil.createAuditLog(JobStatusEnum.SUBMITTED)),
              JobTypeEnum.UPLOAD_PROCESSING_LEAD_TIMES);
      when(jobDomain.getAndUpdateJobStatusByStatus(any(), any(), any(), any()))
          .thenReturn(jobEntity);
      CompletableFuture<SendResult<String, Object>> future = mock(CompletableFuture.class);

      when(future.whenComplete(any())).thenReturn(null);
      when(kafkaTemplate.send(any(Message.class))).thenReturn(future);

      JobResponse jobDto =
          jobService.processJobJsonOffline(
              UPLOAD_PROCESSING_LEAD_TIME_LIST,
              TestUtil.ORG_ID,
              JobTypeEnum.UPLOAD_PROCESSING_LEAD_TIMES,
              Optional.of(TestUtil.JOB_ID));
      jobDto.setJobId("jobId1");
      jobDto.setTotalRecords(1);

      Assertions.assertEquals(0, jobDto.getFailureCount(), "failed count");

      Assertions.assertEquals(1, jobDto.getTotalRecords(), "Job Id");
      verify(kafkaTemplate, times(3)).send(any(Message.class));
    }

    @Test
    void processTransitBufferRequestJsonOfflineSuccess()
        throws JobException, JobDomainException, CommonServiceException, IOException, CsvException {
      TransitBufferUpload transitBufferUpload = testUtil.getTransitBufferUpload("2", "C");
      JobResponse jobResponse =
          testUtil.createJobResponse(
              "jobId1",
              TestUtil.ORG_ID,
              JobStatusEnum.SUBMITTED,
              Collections.singletonList(testUtil.createAuditLog(JobStatusEnum.SUBMITTED)),
              JobTypeEnum.TRANSIT_BUFFER_REQUEST);
      jobResponse.setFailureCount(0);

      JobDto job =
          testUtil.createJob(
              "jobId1",
              TestUtil.ORG_ID,
              JobStatusEnum.SUBMITTED,
              Collections.singletonList(testUtil.createAuditLog(JobStatusEnum.SUBMITTED)),
              JobTypeEnum.TRANSIT_BUFFER_REQUEST,
              23456L);
      job.setFile(TestUtil.CSV_CONTENTS_TRANSIT_BUFFER_REQUEST.getBytes());
      job.setTotalRecords(3);
      job.setRemainingRecords(3);
      job.setFailureCount(0);

      when(jobsConsumerClient.getJob(any(), any(), any()))
          .thenReturn(
              BaseResponse.builder()
                  .message("Retrieved job" + " id " + " " + "successfully!!")
                  .payload(job)
                  .build());

      when(jobsConsumerClient.createJob(any()))
          .thenReturn(
              BaseResponse.builder()
                  .message("Retrieved job" + " id " + " " + "successfully!!")
                  .payload(jobResponse)
                  .build());

      jobResponse.setTotalRecords(5);
      jobResponse.setRemainingRecords(5);
      when(jobsConsumerClient.updateJob(any(), any()))
          .thenReturn(BaseResponse.builder().payload(jobResponse).build());

      JobEntity jobEntity =
          testUtil.createJobEntity(
              "jobId1",
              TestUtil.ORG_ID,
              JobStatusEnum.PROCESSED,
              List.of(testUtil.createAuditLog(JobStatusEnum.SUBMITTED)),
              JobTypeEnum.TRANSIT_BUFFER_REQUEST,
              23456L);
      when(jobDomain.getAndUpdateJobStatusByStatus(any(), any(), any(), any()))
          .thenReturn(jobEntity);
      CompletableFuture<SendResult<String, Object>> future = mock(CompletableFuture.class);

      when(processFileContentsMapperFactory.getProcessFileContentsMapper(any()))
          .thenReturn(processFileContents);
      doReturn(
              List.of(
                  transitBufferUpload,
                  transitBufferUpload,
                  transitBufferUpload,
                  transitBufferUpload,
                  transitBufferUpload,
                  transitBufferUpload))
          .when(processFileContents)
          .updateRequestObjectsList(any(), any());

      when(future.whenComplete(any())).thenReturn(null);

      when(kafkaTemplate.send(any(Message.class))).thenReturn(future);

      when(fileMetaDataClient.findFileMetadataById(any(), any()))
          .thenReturn(
              BaseResponse.builder().payload(testUtil.getFileMetaDataResponse(23456L)).build());

      when(fileService.getFile(any(), any())).thenReturn(testUtil.getFileResponse());

      JobResponse jobDto =
          jobService.processJobJsonOffline(
              TestUtil.ORG_ID, JobTypeEnum.TRANSIT_BUFFER_REQUEST, Optional.of(TestUtil.JOB_ID));
      jobDto.setJobId("jobId1");
      jobDto.setTotalRecords(1);

      Assertions.assertEquals(0, jobDto.getFailureCount(), "failed count");

      Assertions.assertEquals(1, jobDto.getTotalRecords(), "Job Id");
      verify(kafkaTemplate, times(6)).send(any(Message.class));
    }

    @Test
    void processTransitBufferRequestJsonOfflineWithEmptyPayloadFromFileMetaData()
        throws JobDomainException, IOException, CsvException, CommonServiceException {
      TransitBufferUpload transitBufferUpload = testUtil.getTransitBufferUpload("2", "C");
      JobResponse jobResponse =
          testUtil.createJobResponse(
              "jobId1",
              TestUtil.ORG_ID,
              JobStatusEnum.SUBMITTED,
              Collections.singletonList(testUtil.createAuditLog(JobStatusEnum.SUBMITTED)),
              JobTypeEnum.TRANSIT_BUFFER_REQUEST);
      jobResponse.setFailureCount(0);

      JobDto job =
          testUtil.createJob(
              "jobId1",
              TestUtil.ORG_ID,
              JobStatusEnum.SUBMITTED,
              Collections.singletonList(testUtil.createAuditLog(JobStatusEnum.SUBMITTED)),
              JobTypeEnum.TRANSIT_BUFFER_REQUEST,
              23456L);
      job.setFile(TestUtil.CSV_CONTENTS_TRANSIT_BUFFER_REQUEST.getBytes());
      job.setTotalRecords(3);
      job.setRemainingRecords(3);
      job.setFailureCount(0);

      when(jobsConsumerClient.getJob(any(), any(), any()))
          .thenReturn(
              BaseResponse.builder()
                  .message("Retrieved job" + " id " + " " + "successfully!!")
                  .payload(job)
                  .build());

      when(jobsConsumerClient.createJob(any()))
          .thenReturn(
              BaseResponse.builder()
                  .message("Retrieved job" + " id " + " " + "successfully!!")
                  .payload(jobResponse)
                  .build());
      when(fileService.getFile(any(), any())).thenReturn(testUtil.getFileResponse());
      jobResponse.setTotalRecords(5);
      jobResponse.setRemainingRecords(5);
      when(jobsConsumerClient.updateJob(any(), any()))
          .thenReturn(BaseResponse.builder().payload(jobResponse).build());

      JobEntity jobEntity =
          testUtil.createJobEntity(
              "jobId1",
              TestUtil.ORG_ID,
              JobStatusEnum.PROCESSED,
              List.of(testUtil.createAuditLog(JobStatusEnum.SUBMITTED)),
              JobTypeEnum.TRANSIT_BUFFER_REQUEST,
              23456L);
      when(jobDomain.getAndUpdateJobStatusByStatus(any(), any(), any(), any()))
          .thenReturn(jobEntity);
      CompletableFuture<SendResult<String, Object>> future = mock(CompletableFuture.class);

      when(processFileContentsMapperFactory.getProcessFileContentsMapper(any()))
          .thenReturn(processFileContents);
      doReturn(
              List.of(
                  transitBufferUpload,
                  transitBufferUpload,
                  transitBufferUpload,
                  transitBufferUpload,
                  transitBufferUpload,
                  transitBufferUpload))
          .when(processFileContents)
          .updateRequestObjectsList(any(), any());

      when(future.whenComplete(any())).thenReturn(null);

      when(kafkaTemplate.send(any(Message.class))).thenReturn(future);

      when(fileMetaDataClient.findFileMetadataById(any(), any()))
          .thenReturn(BaseResponse.builder().payload(new FileMetaDataResponse()).build());

      JobException exception =
          assertThrows(
              JobException.class,
              () ->
                  jobService.processJobJsonOffline(
                      TestUtil.ORG_ID,
                      JobTypeEnum.TRANSIT_BUFFER_REQUEST,
                      Optional.of(TestUtil.JOB_ID)));

      Assertions.assertEquals(
          "Exception while processing job json request",
          exception.getMessage(),
          "Exception message");
    }

    @Test
    void processJobJsonOfflineFeignException() {
      Map<String, Collection<String>> headers = new HashMap<>();
      when(jobsConsumerClient.getJob(any(), any(), any()))
          .thenThrow(
              new FeignException.BadRequest(
                  "Failed to submit the job",
                  Request.create(HttpMethod.GET, "", new HashMap<>(), null, null, null),
                  "Error while submitting the job".getBytes(),
                  headers));

      JobException exception =
          assertThrows(
              JobException.class,
              () ->
                  jobService.processJobJsonOffline(
                      UPLOAD_PROCESSING_LEAD_TIME_LIST,
                      TestUtil.ORG_ID,
                      JobTypeEnum.UPLOAD_PROCESSING_LEAD_TIMES,
                      Optional.of("testVal")));

      Assertions.assertEquals(
          "Failed to submit the job", exception.getMessage(), "Exception message");
      verify(jobsConsumerClient, times(1)).getJob(any(), any(), any());
    }

    @Test
    void processJobJsonOfflineException() {
      when(jobsConsumerClient.getJob(any(), any(), any())).thenThrow(new RuntimeException());

      JobException exception =
          assertThrows(
              JobException.class,
              () ->
                  jobService.processJobJsonOffline(
                      UPLOAD_PROCESSING_LEAD_TIME_LIST,
                      TestUtil.ORG_ID,
                      JobTypeEnum.UPLOAD_PROCESSING_LEAD_TIMES,
                      Optional.of(TestUtil.JOB_ID)));

      Assertions.assertEquals(
          "Exception while processing job json request",
          exception.getMessage(),
          "Exception message");

      Assertions.assertEquals(
          JobTypeEnum.UPLOAD_PROCESSING_LEAD_TIMES, exception.getJobType(), "Exception message");
    }

    @Test
    void processJobJsonOfflineKafkaPublishFails()
        throws JobDomainException, IOException, CsvException, CommonServiceException {

      TransitBufferUpload transitBufferUpload = testUtil.getTransitBufferUpload("2", "C");
      JobDto job =
          testUtil.createJob(
              "jobId1",
              TestUtil.ORG_ID,
              JobStatusEnum.SUBMITTED,
              Collections.singletonList(testUtil.createAuditLog(JobStatusEnum.SUBMITTED)),
              JobTypeEnum.UPLOAD_PROCESSING_LEAD_TIMES,
              null);
      job.setFile(TestUtil.CSV_CONTENTS_TRANSIT_BUFFER_REQUEST.getBytes());
      job.setTotalRecords(3);
      job.setRemainingRecords(3);
      job.setFailureCount(0);

      JobResponse jobResponse =
          testUtil.createJobResponse(
              "jobId1",
              TestUtil.ORG_ID,
              JobStatusEnum.SUBMITTED,
              Collections.singletonList(testUtil.createAuditLog(JobStatusEnum.SUBMITTED)),
              JobTypeEnum.TRANSIT_BUFFER_REQUEST);
      jobResponse.setFailureCount(0);

      when(jobsConsumerClient.createJob(any()))
          .thenReturn(
              BaseResponse.builder()
                  .message("Retrieved job" + " id " + " " + "successfully!!")
                  .payload(jobResponse)
                  .build());

      jobResponse.setTotalRecords(5);
      jobResponse.setRemainingRecords(5);
      when(jobsConsumerClient.updateJob(any(), any()))
          .thenReturn(BaseResponse.builder().payload(jobResponse).build());

      JobEntity jobEntity =
          testUtil.createJobEntity(
              "jobId1",
              TestUtil.ORG_ID,
              JobStatusEnum.PROCESSED,
              List.of(testUtil.createAuditLog(JobStatusEnum.SUBMITTED)),
              JobTypeEnum.TRANSIT_BUFFER_REQUEST,
              23456L);
      when(jobDomain.getAndUpdateJobStatusByStatus(any(), any(), any(), any()))
          .thenReturn(jobEntity);

      when(processFileContentsMapperFactory.getProcessFileContentsMapper(any()))
          .thenReturn(processFileContents);
      doReturn(
              List.of(
                  transitBufferUpload,
                  transitBufferUpload,
                  transitBufferUpload,
                  transitBufferUpload,
                  transitBufferUpload,
                  transitBufferUpload))
          .when(processFileContents)
          .updateRequestObjectsList(any(), any());

      when(jobsConsumerClient.getJob(any(), any(), any()))
          .thenReturn(
              BaseResponse.builder()
                  .message("Retrieval of job records is successful")
                  .payload(job)
                  .build());

      when(jobsConsumerClient.createJob(any()))
          .thenReturn(
              BaseResponse.builder()
                  .message("Retrieved job" + " id " + " " + "successfully!!")
                  .payload(job)
                  .build());
      when(jobDomain.getAndUpdateJobStatusByStatus(any(), any(), any(), any()))
          .thenReturn(jobEntity);
      job.setTotalRecords(5);
      job.setRemainingRecords(5);
      when(jobsConsumerClient.updateJob(any(), any()))
          .thenReturn(BaseResponse.builder().payload(jobResponse).build());

      when(kafkaTemplate.send(any(Message.class))).thenThrow(new RuntimeException());

      JobException exception =
          assertThrows(
              JobException.class,
              () ->
                  jobService.processJobJsonOffline(
                      UPLOAD_PROCESSING_LEAD_TIME_LIST,
                      TestUtil.ORG_ID,
                      JobTypeEnum.UPLOAD_PROCESSING_LEAD_TIMES,
                      Optional.of(TestUtil.JOB_ID)));

      Assertions.assertEquals(
          "Exception while processing job json request",
          exception.getMessage(),
          "Exception message");
    }
  }

  @Nested
  class GetJobResults {

    @Test
    void getJobResults() throws JobException {

      List<RecordStatusDto> recordStatusDtos =
          testUtil.createRecordStatusDtoList(
              TestUtil.JOB_ID, TestUtil.JOB_TYPE_UPLOAD_PROCESSING_LEAD_TIMES, 5, 3);

      when(jobsConsumerClient.getJobRecordsByFilter(any(), any(), any()))
          .thenReturn(
              BaseResponse.builder()
                  .message("Retrieval of job records is successful")
                  .payload(recordStatusDtos)
                  .build());

      List<RecordStatusDto> recordStatusDtoPagePayload =
          jobService.getJobResults(TestUtil.ORG_ID, TestUtil.JOB_ID, Optional.of("SUCCESS"));

      Assertions.assertFalse(CollectionUtils.isEmpty(recordStatusDtoPagePayload));
      verify(jobsConsumerClient, times(1)).getJobRecordsByFilter(any(), any(), any());
    }

    @Test
    void getJobResultsException() {
      when(jobsConsumerClient.getJobRecordsByFilter(any(), any(), any()))
          .thenThrow(new RuntimeException());

      JobException exception =
          assertThrows(
              JobException.class,
              () ->
                  jobService.getJobResults(
                      TestUtil.ORG_ID, TestUtil.JOB_ID, Optional.of("SUCCESS")));

      Assertions.assertEquals(
          "Exception while retrieving the job results",
          exception.getMessage(),
          "Exception message ");
      verify(jobsConsumerClient, times(1)).getJobRecordsByFilter(any(), any(), any());
    }

    @Test
    void getJobResultsFeignException() {
      Map<String, Collection<String>> headers = new HashMap<>();
      when(jobsConsumerClient.getJobRecordsByFilter(any(), any(), any()))
          .thenThrow(
              new FeignException.BadRequest(
                  "",
                  Request.create(HttpMethod.GET, "", new HashMap<>(), null, null, null),
                  "Error while retrieving the job results".getBytes(),
                  headers));

      JobException exception =
          assertThrows(
              JobException.class,
              () ->
                  jobService.getJobResults(
                      TestUtil.ORG_ID, TestUtil.JOB_ID, Optional.of("SUCCESS")));

      Assertions.assertEquals(
          "Upstream error : Error while retrieving the job results",
          exception.getMessage(),
          "Exception message ");
      verify(jobsConsumerClient, times(1)).getJobRecordsByFilter(any(), any(), any());
    }
  }

  @Test
  void processJobOffline() throws JobException {
    JobResponse job =
        testUtil.createJobResponse(
            "jobId1",
            TestUtil.ORG_ID,
            JobStatusEnum.SUBMITTED,
            Collections.singletonList(testUtil.createAuditLog(JobStatusEnum.SUBMITTED)),
            JobTypeEnum.TRANSIT_BUFFER_REQUEST);

    when(jobsConsumerClient.createJob(any()))
        .thenReturn(BaseResponse.builder().payload(job).build());

    JobResponse jobResponse =
        jobService.processJobOffline(TestUtil.ORG_ID, JobTypeEnum.TRANSIT_BUFFER_REQUEST, 23456L);

    Assertions.assertNotNull(jobResponse);
    Assertions.assertFalse(ObjectUtils.isEmpty(jobResponse.getJobId()));
    verify(jobsConsumerClient, times(1)).createJob(any());
  }

  @Test
  void getJobRecordsByFilters() throws JobException {
    RecordStatusDto recordStatusDto =
        testUtil.createRecordStatusDto(
            TestUtil.JOB_ID, JobTypeEnum.UPLOAD_NODE_CARRIER, 3, HttpStatus.OK.value());
    when(jobsConsumerClient.getJobRecordsByFilters(
            anyString(), anyString(), any(), anyInt(), anyInt()))
        .thenReturn(
            BaseResponse.builder()
                .payload(
                    testUtil.createPagePayloadRecordStatusDto(List.of(recordStatusDto), 5, 20, 1))
                .build());

    PagePayload<RecordStatusDto> payload =
        jobService.getJobRecordsByFilters(
            TestUtil.ORG_ID, TestUtil.JOB_ID, Optional.of(ApiStatusEnum.SUCCESS.name()), 1, 15);

    Assertions.assertNotNull(payload);
    Assertions.assertFalse(CollectionUtils.isEmpty(payload.getData()));
  }

  @Test
  void getJobRecordsByFiltersException() {
    when(jobsConsumerClient.getJobRecordsByFilters(
            anyString(), anyString(), any(), anyInt(), anyInt()))
        .thenThrow(new RuntimeException("Error while fetching job records"));

    Exception exception =
        Assertions.assertThrows(
            JobException.class,
            () ->
                jobService.getJobRecordsByFilters(
                    TestUtil.ORG_ID,
                    TestUtil.JOB_ID,
                    Optional.of(ApiStatusEnum.SUCCESS.name()),
                    1,
                    15));

    Assertions.assertNotNull(exception);
  }

  @Test
  void getJobRecordsByFiltersFeignException() {
    Map<String, Collection<String>> headers = new HashMap<>();

    when(jobsConsumerClient.getJobRecordsByFilters(
            anyString(), anyString(), any(), anyInt(), anyInt()))
        .thenThrow(
            new FeignException.BadRequest(
                "",
                Request.create(HttpMethod.GET, "", new HashMap<>(), null, null, null),
                "Error while retrieving the job records".getBytes(),
                headers));

    Exception exception =
        Assertions.assertThrows(
            JobException.class,
            () ->
                jobService.getJobRecordsByFilters(
                    TestUtil.ORG_ID,
                    TestUtil.JOB_ID,
                    Optional.of(ApiStatusEnum.SUCCESS.name()),
                    1,
                    15));

    Assertions.assertNotNull(exception);
  }
}
