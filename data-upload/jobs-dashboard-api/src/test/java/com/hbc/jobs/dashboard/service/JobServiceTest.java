package com.hbc.jobs.dashboard.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hbc.common.base.PagePayload;
import com.hbc.common.response.BaseResponse;
import com.hbc.jobs.dashboard.common.TestUtil;
import com.hbc.jobs.dashboard.exception.JobException;
import com.hbc.jobs.framework.common.clients.JobsConsumerClient;
import com.hbc.jobs.framework.common.domain.enums.JobStatusEnum;
import com.hbc.jobs.framework.common.domain.enums.JobTypeEnum;
import com.hbc.jobs.framework.common.domain.outbound.JobResponse;
import com.hbc.jobs.framework.common.domain.pojo.JobDto;
import com.hbc.jobs.framework.common.domain.pojo.RecordStatusDto;
import feign.FeignException;
import feign.Request;
import feign.Request.HttpMethod;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
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
import org.springframework.util.concurrent.ListenableFuture;

class JobServiceTest {

  @InjectMocks private JobService jobService;

  @InjectMocks private TestUtil testUtil;

  @Mock private JobsConsumerClient jobsConsumerClient;

  @Mock private KafkaTemplate<String, Object> kafkaTemplate;

  @BeforeEach
  public void init() {
    MockitoAnnotations.openMocks(this);
    MockMvcBuilders.standaloneSetup(jobService).build();
    ReflectionTestUtils.setField(jobService, "dashboardProducerName", "dashboardProcuderTopicName");
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
              JobTypeEnum.UPLOAD_PROCESSING_LEAD_TIMES);
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
              JobTypeEnum.UPLOAD_PROCESSING_LEAD_TIMES);

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
    void processJobJsonOfflineSuccess() throws JobException {
      JobResponse jobResponse =
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
                  .payload(jobResponse)
                  .build());

      jobResponse.setTotalRecords(5);
      jobResponse.setRemainingRecords(5);
      when(jobsConsumerClient.updateJob(any()))
          .thenReturn(BaseResponse.builder().payload(jobResponse).build());

      ListenableFuture<SendResult<String, Object>> future = mock(ListenableFuture.class);

      doNothing().when(future).addCallback(any());
      when(kafkaTemplate.send(any(Message.class))).thenReturn(future);

      JobResponse jobDto =
          jobService.processJobJsonOffline(
              UPLOAD_PROCESSING_LEAD_TIME_LIST,
              TestUtil.ORG_ID,
              JobTypeEnum.UPLOAD_PROCESSING_LEAD_TIMES,
              Optional.empty());
      jobDto.setJobId("jobId1");
      jobDto.setTotalRecords(1);

      Assertions.assertEquals(0, jobDto.getFailureCount(), "Job Id");

      Assertions.assertEquals(1, jobDto.getTotalRecords(), "Job Id");
      verify(kafkaTemplate, times(1)).send(any(Message.class));
      verify(jobsConsumerClient, times(1)).createJob(any());
    }

    @Test
    void processJobJsonOfflineUpdateExistingJob() throws JobException {

      JobDto job =
          testUtil.createJob(
              "jobId1",
              TestUtil.ORG_ID,
              JobStatusEnum.SUBMITTED,
              Collections.singletonList(testUtil.createAuditLog(JobStatusEnum.SUBMITTED)),
              JobTypeEnum.UPLOAD_PROCESSING_LEAD_TIMES);

      JobResponse jobResponse =
          testUtil.createJobResponse(
              "jobId1",
              TestUtil.ORG_ID,
              JobStatusEnum.SUBMITTED,
              Collections.singletonList(testUtil.createAuditLog(JobStatusEnum.SUBMITTED)),
              JobTypeEnum.UPLOAD_PROCESSING_LEAD_TIMES);

      when(jobsConsumerClient.getJob(any(), any()))
          .thenReturn(
              BaseResponse.builder()
                  .message("Retrieved job" + " id " + " " + "successfully!!")
                  .payload(job)
                  .build());

      job.setTotalRecords(5);
      job.setRemainingRecords(5);
      when(jobsConsumerClient.updateJob(any()))
          .thenReturn(BaseResponse.builder().payload(jobResponse).build());

      ListenableFuture<SendResult<String, Object>> future = mock(ListenableFuture.class);

      doNothing().when(future).addCallback(any());
      when(kafkaTemplate.send(any(Message.class))).thenReturn(future);

      String UPLOAD_PROCESSING_LEAD_TIME_LIST_WITH_INPUTS =
          "[{\"nodeId\": \"node-1\",\"orgId\": \"BAY\",\"carrierServiceId\": \"ALL-SDND\",\"serviceOption\": \"SDND\",\"processingTime\": 20.32,\"lastPickupTime\": \"12:22\",\"inputs\": {\"retryCount\" : \"7\"}}]";
      JobResponse jobDto =
          jobService.processJobJsonOffline(
              UPLOAD_PROCESSING_LEAD_TIME_LIST_WITH_INPUTS,
              TestUtil.ORG_ID,
              JobTypeEnum.UPLOAD_PROCESSING_LEAD_TIMES,
              Optional.of("jobId1"));
      jobDto.setJobId("jobId1");
      jobDto.setTotalRecords(1);

      Assertions.assertEquals(0, jobDto.getFailureCount(), "Job Id");

      Assertions.assertEquals(1, jobDto.getTotalRecords(), "Job Id");
      verify(kafkaTemplate, times(1)).send(any(Message.class));
      verify(jobsConsumerClient, times(1)).getJob(any(), any());
    }

    @Test
    void processJobJsonOfflineSuccessWithRetryCountInInputs() throws JobException {
      JobResponse jobResponse =
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
                  .payload(jobResponse)
                  .build());

      ListenableFuture<SendResult<String, Object>> future = mock(ListenableFuture.class);

      doNothing().when(future).addCallback(any());
      when(kafkaTemplate.send(any(Message.class))).thenReturn(future);

      String UPLOAD_TRANSIT_TIMES_LIST =
          "[{\"orgId\": \"BAY\",\"sourceGeozone\": \"SFSA\",\"destinationGeozone\": \"DSFA\",\"carrierServiceId\": \"ALL-SDND\",\"transitDays\": \"2\"}]";
      JobResponse jobDto =
          jobService.processJobJsonOffline(
              UPLOAD_TRANSIT_TIMES_LIST,
              TestUtil.ORG_ID,
              JobTypeEnum.UPLOAD_PROCESSING_LEAD_TIMES,
              Optional.empty());
      jobDto.setJobId("jobId1");
      jobDto.setTotalRecords(1);

      Assertions.assertEquals(0, jobDto.getFailureCount(), "Job Id");

      Assertions.assertEquals(1, jobDto.getTotalRecords(), "Job Id");
      verify(kafkaTemplate, times(1)).send(any(Message.class));
      verify(jobsConsumerClient, times(1)).createJob(any());
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
      when(jobsConsumerClient.createJob(any())).thenThrow(new RuntimeException());

      JobException exception =
          assertThrows(
              JobException.class,
              () ->
                  jobService.processJobJsonOffline(
                      UPLOAD_PROCESSING_LEAD_TIME_LIST,
                      TestUtil.ORG_ID,
                      JobTypeEnum.UPLOAD_PROCESSING_LEAD_TIMES,
                      Optional.empty()));

      Assertions.assertEquals(
          "Exception while processing job json request",
          exception.getMessage(),
          "Exception message");

      Assertions.assertEquals(
          JobTypeEnum.UPLOAD_PROCESSING_LEAD_TIMES, exception.getJobType(), "Exception message");
    }

    @Test
    void processJobJsonOfflineKafkaPublishFails() {

      JobDto job =
          testUtil.createJob(
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

      when(kafkaTemplate.send(any(Message.class))).thenThrow(new RuntimeException());

      JobException exception =
          assertThrows(
              JobException.class,
              () ->
                  jobService.processJobJsonOffline(
                      UPLOAD_PROCESSING_LEAD_TIME_LIST,
                      TestUtil.ORG_ID,
                      JobTypeEnum.UPLOAD_PROCESSING_LEAD_TIMES,
                      Optional.empty()));

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
}
