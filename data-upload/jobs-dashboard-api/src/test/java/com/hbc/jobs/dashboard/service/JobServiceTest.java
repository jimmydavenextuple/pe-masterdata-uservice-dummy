package com.hbc.jobs.dashboard.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hbc.common.base.PagePayload;
import com.hbc.common.exception.CommonServiceException;
import com.hbc.common.response.BaseResponse;
import com.hbc.csvdownload.common.pojo.TransitDataUpload;
import com.hbc.jobs.consumers.domain.JobDomain;
import com.hbc.jobs.consumers.domain.entity.JobEntity;
import com.hbc.jobs.consumers.exception.JobDomainException;
import com.hbc.jobs.consumers.service.AuthTokenService;
import com.hbc.jobs.dashboard.common.TestUtil;
import com.hbc.jobs.dashboard.exception.JobException;
import com.hbc.jobs.framework.common.clients.FileMetaDataClient;
import com.hbc.jobs.framework.common.clients.JobsConsumerClient;
import com.hbc.jobs.framework.common.domain.enums.JobStatusEnum;
import com.hbc.jobs.framework.common.domain.enums.JobTypeEnum;
import com.hbc.jobs.framework.common.domain.outbound.FileMetaDataResponse;
import com.hbc.jobs.framework.common.domain.outbound.JobResponse;
import com.hbc.jobs.framework.common.domain.pojo.JobDto;
import com.hbc.jobs.framework.common.domain.pojo.RecordStatusDto;
import com.hbc.jobs.framework.common.domain.pojo.TransitBufferUpload;
import com.hbc.jobs.framework.common.service.FileService;
import com.opencsv.exceptions.CsvException;
import feign.FeignException;
import feign.Request;
import feign.Request.HttpMethod;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
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
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.messaging.Message;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.concurrent.ListenableFuture;

class JobServiceTest {

  @InjectMocks private JobService jobService;

  @InjectMocks private TestUtil testUtil;

  @Mock private JobsConsumerClient jobsConsumerClient;

  @Mock private JobDomain jobDomain;

  @Mock private AuthTokenService authTokenService;

  @Mock private KafkaTemplate<String, Object> kafkaTemplate;

  @Mock private FileMetaDataClient fileMetaDataClient;

  @Mock private FileService fileService;

  @Mock private ProcessFileContentsMapperFactory processFileContentsMapperFactory;

  @Mock private ProcessFileContents processFileContents;

  @Captor ArgumentCaptor<List<Object>> objectListCaptor;

  @BeforeEach
  public void init() {
    MockitoAnnotations.openMocks(this);
    MockMvcBuilders.standaloneSetup(jobService).build();
    ReflectionTestUtils.setField(jobService, "dashboardProducerName", "dashboardProcuderTopicName");
    when(authTokenService.generateAuthToken()).thenReturn(testUtil.getAuthTokenResponse());
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

      when(jobsConsumerClient.createJob(any()))
          .thenThrow(
              new FeignException.BadRequest(
                  "Error when fetching fsaList",
                  Request.create(HttpMethod.POST, "", new HashMap<>(), null, null, null),
                  "Error while creating job".getBytes()));

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

      when(jobsConsumerClient.getJob(TestUtil.ORG_ID, job.getJobId()))
          .thenThrow(
              new FeignException.BadRequest(
                  "",
                  Request.create(HttpMethod.GET, "", new HashMap<>(), null, null, null),
                  "Error while retrieving the job".getBytes()));

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
      when(jobsConsumerClient.getJobsByFilter(
              any(), any(), any(), any(), any(), anyInt(), anyInt()))
          .thenThrow(
              new FeignException.BadRequest(
                  "",
                  Request.create(HttpMethod.GET, "", new HashMap<>(), null, null, null),
                  "Error while retrieving the job".getBytes()));

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
        throws JobException, JobDomainException, IOException, CsvException {
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
      job.setFailureCount(0);

      when(jobsConsumerClient.getJob(any(), any()))
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
      when(jobsConsumerClient.updateJob(any()))
          .thenReturn(BaseResponse.builder().payload(jobResponse).build());

      JobEntity jobEntity =
          testUtil.createJobEntity(
              "jobId1",
              TestUtil.ORG_ID,
              JobStatusEnum.PROCESSED,
              List.of(testUtil.createAuditLog(JobStatusEnum.SUBMITTED)),
              JobTypeEnum.UPLOAD_PROCESSING_LEAD_TIMES);
      when(jobDomain.getAndUpdateJobStatusByOrgIdAndStatus(any(), any(), any()))
          .thenReturn(jobEntity);
      ListenableFuture<SendResult<String, Object>> future = mock(ListenableFuture.class);

      doNothing().when(future).addCallback(any());
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

      when(jobsConsumerClient.getJob(any(), any()))
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
      when(jobsConsumerClient.updateJob(any()))
          .thenReturn(BaseResponse.builder().payload(jobResponse).build());

      JobEntity jobEntity =
          testUtil.createJobEntity(
              "jobId1",
              TestUtil.ORG_ID,
              JobStatusEnum.PROCESSED,
              List.of(testUtil.createAuditLog(JobStatusEnum.SUBMITTED)),
              JobTypeEnum.TRANSIT_BUFFER_REQUEST,
              23456L);
      when(jobDomain.getAndUpdateJobStatusByOrgIdAndStatus(any(), any(), any()))
          .thenReturn(jobEntity);
      ListenableFuture<SendResult<String, Object>> future = mock(ListenableFuture.class);

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

      doNothing().when(future).addCallback(any());
      when(kafkaTemplate.send(any(Message.class))).thenReturn(future);

      when(fileMetaDataClient.findFileMetadataById(any()))
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

      when(jobsConsumerClient.getJob(any(), any()))
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
      when(jobsConsumerClient.updateJob(any()))
          .thenReturn(BaseResponse.builder().payload(jobResponse).build());

      JobEntity jobEntity =
          testUtil.createJobEntity(
              "jobId1",
              TestUtil.ORG_ID,
              JobStatusEnum.PROCESSED,
              List.of(testUtil.createAuditLog(JobStatusEnum.SUBMITTED)),
              JobTypeEnum.TRANSIT_BUFFER_REQUEST,
              23456L);
      when(jobDomain.getAndUpdateJobStatusByOrgIdAndStatus(any(), any(), any()))
          .thenReturn(jobEntity);
      ListenableFuture<SendResult<String, Object>> future = mock(ListenableFuture.class);

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

      doNothing().when(future).addCallback(any());
      when(kafkaTemplate.send(any(Message.class))).thenReturn(future);

      when(fileMetaDataClient.findFileMetadataById(any()))
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

      when(jobsConsumerClient.getJob(any(), any()))
          .thenThrow(
              new FeignException.BadRequest(
                  "Failed to submit the job",
                  Request.create(HttpMethod.GET, "", new HashMap<>(), null, null, null),
                  "Error while submitting the job".getBytes()));

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
      verify(jobsConsumerClient, times(1)).getJob(any(), any());
    }

    @Test
    void processJobJsonOfflineException() {
      when(jobsConsumerClient.getJob(any(), any())).thenThrow(new RuntimeException());

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
    void processJobJsonOfflineKafkaPublishFails() throws JobDomainException {

      JobDto job =
          testUtil.createJob(
              "jobId1",
              TestUtil.ORG_ID,
              JobStatusEnum.SUBMITTED,
              Collections.singletonList(testUtil.createAuditLog(JobStatusEnum.SUBMITTED)),
              JobTypeEnum.UPLOAD_PROCESSING_LEAD_TIMES,
              null);

      JobResponse jobResponse =
          testUtil.createJobResponse(
              "jobId1",
              TestUtil.ORG_ID,
              JobStatusEnum.SUBMITTED,
              Collections.singletonList(testUtil.createAuditLog(JobStatusEnum.SUBMITTED)),
              JobTypeEnum.DELETE_TRANSIT_BUFFER);

      when(jobsConsumerClient.createJob(any()))
          .thenReturn(
              BaseResponse.builder()
                  .message("Retrieved job" + " id " + " " + "successfully!!")
                  .payload(job)
                  .build());

      JobEntity jobEntity =
          testUtil.createJobEntity(
              "jobId1",
              TestUtil.ORG_ID,
              JobStatusEnum.PROCESSED,
              List.of(testUtil.createAuditLog(JobStatusEnum.SUBMITTED)),
              JobTypeEnum.UPLOAD_TRANSIT_TIMES);
      when(jobDomain.getAndUpdateJobStatusByOrgIdAndStatus(any(), any(), any()))
          .thenReturn(jobEntity);
      job.setTotalRecords(5);
      job.setRemainingRecords(5);
      when(jobsConsumerClient.updateJob(any()))
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
      when(jobsConsumerClient.getJobRecordsByFilter(any(), any(), any()))
          .thenThrow(
              new FeignException.BadRequest(
                  "",
                  Request.create(HttpMethod.GET, "", new HashMap<>(), null, null, null),
                  "Error while retrieving the job results".getBytes()));

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
  void parseJSONFails() {
    JobException exception =
        Assertions.assertThrows(
            JobException.class, () -> jobService.parseJSON(ERRONEOUS_JSON_LIST));

    Assertions.assertEquals(
        "Error while parsing json file", exception.getMessage(), "Exception message");
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
}
