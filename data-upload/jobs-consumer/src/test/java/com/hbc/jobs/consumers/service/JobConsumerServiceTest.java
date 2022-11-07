package com.hbc.jobs.consumers.service;

import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import com.hbc.jobs.consumers.common.TestUtil;
import com.hbc.jobs.consumers.domain.JobDomain;
import com.hbc.jobs.consumers.domain.JobRecordDomain;
import com.hbc.jobs.consumers.domain.entity.JobEntity;
import com.hbc.jobs.consumers.domain.entity.JobRecordEntity;
import com.hbc.jobs.consumers.domain.mapper.JobMapper;
import com.hbc.jobs.consumers.exception.InvalidJobTypeException;
import com.hbc.jobs.consumers.exception.JobDomainException;
import com.hbc.jobs.consumers.exception.JobException;
import com.hbc.jobs.consumers.exception.JobRecordDomainException;
import com.hbc.jobs.consumers.exception.PublishJobEventException;
import com.hbc.jobs.framework.common.domain.enums.ApiStatusEnum;
import com.hbc.jobs.framework.common.domain.enums.JobStatusEnum;
import com.hbc.jobs.framework.common.domain.enums.JobTypeEnum;
import com.hbc.jobs.framework.common.domain.enums.RecordDataTypeEnum;
import com.hbc.jobs.framework.common.domain.outbound.JobResponse;
import com.hbc.jobs.framework.common.domain.pojo.AuditLog;
import com.hbc.jobs.framework.common.domain.pojo.JobDto;
import com.hbc.jobs.framework.common.domain.pojo.RecordDto;
import com.hbc.jobs.framework.common.domain.pojo.RecordStatusDto;
import com.hbc.jobs.framework.common.enums.ModuleEnum;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.CollectionUtils;

class JobConsumerServiceTest {

  @InjectMocks private JobConsumerService jobConsumerService;

  @InjectMocks private TestUtil testUtil;

  @Mock private FeignClientMapperFactory feignClientMapperFactory;

  @Mock private Map<ModuleEnum, FeignClientMapper> feignClientMapperMap;

  @Mock private NodeCarrierMapper nodeCarrierMapper;
  @Mock private JobRecordDomain jobRecordDomain;

  @Mock private JobDomain jobDomain;

  @Mock private PublishJobEventService publishJobEventService;

  @BeforeEach
  public void init() {
    MockitoAnnotations.openMocks(this);
    MockMvcBuilders.standaloneSetup(jobConsumerService).build();
  }

  @Nested
  class ProcessRecord {

    @Test
    void processRecord() throws PublishJobEventException {
      RecordDto record = testUtil.getRecordDto(JobTypeEnum.UPLOAD_PROCESSING_LEAD_TIMES);
      FeignClientMapper feignClientMapper = mock(FeignClientMapper.class);
      RecordStatusDto recordStatusDto = mock(RecordStatusDto.class);

      when(feignClientMapperMap.get(ModuleEnum.NODE_CARRIER)).thenReturn(feignClientMapper);
      when(feignClientMapper.invokeAPI(any())).thenReturn(recordStatusDto);
      when(feignClientMapperFactory.getFeignClientMapper(JobTypeEnum.UPLOAD_PROCESSING_LEAD_TIMES))
          .thenReturn(feignClientMapper);

      Assertions.assertDoesNotThrow(() -> jobConsumerService.executeTask(record));
      verify(feignClientMapperFactory, times(1)).getFeignClientMapper(any());
    }

    @Test
    void processRecordError() {
      RecordDto record =
          RecordDto.builder()
              .recordId(1)
              .recordType(RecordDataTypeEnum.CSV)
              .jobType(JobTypeEnum.TRANSIT_BUFFER_REQUEST)
              .build();
      FeignClientMapper feignClientMapper = mock(FeignClientMapper.class);
      RecordStatusDto recordStatusDto = mock(RecordStatusDto.class);

      when(feignClientMapperMap.get(any(ModuleEnum.class))).thenReturn(null);
      when(feignClientMapper.invokeAPI(any())).thenReturn(recordStatusDto);
      when(feignClientMapperFactory.getFeignClientMapper(any(JobTypeEnum.class))).thenReturn(null);

      InvalidJobTypeException e =
          Assertions.assertThrows(
              InvalidJobTypeException.class, () -> jobConsumerService.executeTask(record));
      Assertions.assertEquals("Job type is not correct", e.getMessage());
    }
  }

  @Test
  void getRecordStatus() throws InvalidJobTypeException, JobException {
    RecordDto record = new RecordDto();
    record.setJobId(TestUtil.JOB_ID);
    record.setTotalRecords(2);
    record.setJobType(JobTypeEnum.UPLOAD_PROCESSING_LEAD_TIMES);
    record.setOrgId(TestUtil.ORG_ID);

    when(feignClientMapperFactory.getFeignClientMapper(any())).thenReturn(nodeCarrierMapper);
    when(nodeCarrierMapper.invokeAPI(any()))
        .thenReturn(
            testUtil.createRecordStatus(
                TestUtil.JOB_ID,
                TestUtil.ORG_ID,
                ApiStatusEnum.SUCCESS,
                HttpStatus.OK,
                "",
                JobTypeEnum.UPLOAD_PROCESSING_LEAD_TIMES,
                2));

    jobConsumerService.executeTask(record);

    verify(feignClientMapperFactory, times(1)).getFeignClientMapper(any());
    verify(nodeCarrierMapper, times(1)).invokeAPI(any());
  }

  @Test
  void getRecordStatusInvalidJobType() {
    RecordDto record = new RecordDto();
    record.setJobId(TestUtil.JOB_ID);
    record.setTotalRecords(2);
    record.setJobType(JobTypeEnum.UPLOAD_PROCESSING_LEAD_TIMES);
    record.setOrgId(TestUtil.ORG_ID);
    when(feignClientMapperFactory.getFeignClientMapper(any())).thenReturn(null);

    Exception exception =
        Assertions.assertThrows(
            InvalidJobTypeException.class, () -> jobConsumerService.executeTask(record));

    Assertions.assertNotNull(exception);
  }

  @Nested
  class UpdateJobStatus {

    @Test
    void updateJobStatus() throws JobRecordDomainException, JobException {
      JobConsumerService jobConsumerService =
          spy(
              new JobConsumerService(
                  feignClientMapperFactory, jobRecordDomain, jobDomain, publishJobEventService));

      doNothing().when(jobConsumerService).updateJob(any(), anyInt());
      JobRecordEntity jobRecordEntity = mock(JobRecordEntity.class);
      RecordStatusDto recordStatusDto = mock(RecordStatusDto.class);

      when(jobRecordDomain.create(any())).thenReturn(jobRecordEntity);

      Assertions.assertDoesNotThrow(() -> jobConsumerService.updateJobStatus(recordStatusDto));
      verify(jobRecordDomain, times(1)).create(any());
    }

    @Test
    void updateJobStatusJobCompletion()
        throws JobRecordDomainException, JobDomainException, PublishJobEventException {

      JobEntity jobEntity = testUtil.createJobEntity(JobTypeEnum.TRANSIT_BUFFER_REQUEST, 2);
      jobEntity.setProcessedRecords(1);

      RecordStatusDto recordStatusDto =
          testUtil.createRecordStatus(
              jobEntity.getJobId(),
              jobEntity.getOrgId(),
              ApiStatusEnum.SUCCESS,
              HttpStatus.OK,
              jobEntity.getUserId(),
              jobEntity.getJobType(),
              2);
      recordStatusDto.setTotalRecordsInJob(2);
      recordStatusDto.setJobType(JobTypeEnum.TRANSIT_BUFFER_REQUEST);

      when(jobRecordDomain.create(any())).thenReturn(testUtil.getJobRecordEntity());

      when(jobDomain.findJobByJobIdAndOrgId(any(), anyString())).thenReturn(jobEntity);

      doNothing().when(publishJobEventService).publishJobDetailsEvent(any());

      jobEntity.setProcessedRecords(2);

      Assertions.assertDoesNotThrow(() -> jobConsumerService.updateJobStatus(recordStatusDto));

      verify(jobRecordDomain, times(1)).create(any());
    }

    @Test
    void updateJobStatusError() throws JobRecordDomainException {
      RecordStatusDto recordStatusDto = mock(RecordStatusDto.class);

      when(jobRecordDomain.create(any())).thenThrow(RuntimeException.class);

      JobException e =
          Assertions.assertThrows(
              JobException.class, () -> jobConsumerService.updateJobStatus(recordStatusDto));
      Assertions.assertEquals("Exception while persisting the job record", e.getMessage());
      Assertions.assertNull(e.getJobId());
      verify(jobRecordDomain, times(1)).create(any());
    }
  }

  @Nested
  class GetJobResults {

    @Test
    void getJobResultsSuccess() throws JobException {

      String jobId = "JobId1";
      Optional<String> status = Optional.of(ApiStatusEnum.valueOf("SUCCESS").toString());

      List<RecordStatusDto> recordStatusDtoList =
          testUtil.createRecordStatusDtoList(TestUtil.ORG_ID);
      when(jobRecordDomain.findConsumerJobsByJobParam(TestUtil.ORG_ID, jobId, status))
          .thenReturn(recordStatusDtoList);
      List<RecordStatusDto> pageResponse =
          jobConsumerService.getJobResults(TestUtil.ORG_ID, jobId, status);

      Assertions.assertFalse(CollectionUtils.isEmpty(pageResponse));
      verify(jobRecordDomain, times(1)).findConsumerJobsByJobParam(any(), any(), any());
    }

    @Test
    void getJobResultsRuntimeException() {
      Optional<String> status = Optional.of(ApiStatusEnum.valueOf("SUCCESS").toString());
      when(jobRecordDomain.findConsumerJobsByJobParam(any(), any(), any()))
          .thenThrow(new RuntimeException());

      JobException exception =
          Assertions.assertThrows(
              JobException.class,
              () -> jobConsumerService.getJobResults(TestUtil.ORG_ID, "JobId1", status));

      Assertions.assertEquals(
          "Exception while retrieving the job records", exception.getMessage(), "Expected Error");

      Assertions.assertEquals("JobId1", exception.getJobId(), "Expected jobId");
    }
  }

  @Nested
  class CreateJob {

    @Test
    void createJobSuccess() throws JobDomainException, JobException {
      JobDto job = testUtil.createJob(JobTypeEnum.UPLOAD_PROCESSING_LEAD_TIMES, 2);
      JobEntity jobEntity = testUtil.createJobEntity(JobTypeEnum.UPLOAD_PROCESSING_LEAD_TIMES, 2);
      when(jobDomain.findJobByJobIdAndOrgId(job.getJobId(), TestUtil.ORG_ID)).thenReturn(null);

      when(jobDomain.save(any())).thenReturn(jobEntity);

      JobResponse job1 = jobConsumerService.createJob(job);

      Assertions.assertEquals(jobEntity.getJobId(), job1.getJobId(), "JobId");
    }

    @Test
    void createJobExists() throws JobDomainException {
      JobDto job = testUtil.createJob(JobTypeEnum.UPLOAD_PROCESSING_LEAD_TIMES, 2);
      JobEntity jobEntity = testUtil.createJobEntity(JobTypeEnum.UPLOAD_PROCESSING_LEAD_TIMES, 2);
      when(jobDomain.findJobByJobIdAndOrgId(job.getJobId(), TestUtil.ORG_ID)).thenReturn(jobEntity);

      JobException exception =
          Assertions.assertThrows(JobException.class, () -> jobConsumerService.createJob(job));

      Assertions.assertEquals(
          "Exception while creating the job ", exception.getMessage(), "Expected Error");
    }

    @Test
    void createJobException() throws JobDomainException {
      JobDto job = testUtil.createJob(JobTypeEnum.UPLOAD_PROCESSING_LEAD_TIMES, 2);
      when(jobDomain.findJobByJobIdAndOrgId(any(), any())).thenThrow(new RuntimeException());

      JobException exception =
          Assertions.assertThrows(JobException.class, () -> jobConsumerService.createJob(job));

      Assertions.assertEquals(
          "Exception while creating the job ", exception.getMessage(), "Expected Error");

      Assertions.assertEquals(job.getJobId(), exception.getJobId(), "Expected JobId");
    }
  }

  @Test
  void saveJob() throws JobDomainException {
    when(jobDomain.findJobByJobIdAndOrgId(any(), any()))
        .thenReturn(testUtil.createJobEntity(JobTypeEnum.UPLOAD_PROCESSING_LEAD_TIMES, 5));
    when(jobDomain.save(any()))
        .thenReturn(testUtil.createJobEntity(JobTypeEnum.UPLOAD_PROCESSING_LEAD_TIMES, 5));

    JobEntity jobEntity =
        jobConsumerService.saveJob(
            testUtil.createJobResponse(JobTypeEnum.UPLOAD_PROCESSING_LEAD_TIMES, 5));
    Assertions.assertNotNull(jobEntity);
    Assertions.assertNotNull(jobEntity.getFile());
    verify(jobDomain, times(1)).save(any());
  }

  @Nested
  class GetJob {

    @Test
    void getJobSuccess() throws JobDomainException, JobException {
      JobDto job = testUtil.createJob(JobTypeEnum.UPLOAD_PROCESSING_LEAD_TIMES, 2);
      JobEntity jobEntity = JobMapper.INSTANCE.toJobEntity(job);
      when(jobDomain.findJobByJobIdAndOrgId(job.getJobId(), TestUtil.ORG_ID)).thenReturn(jobEntity);

      JobDto job1 = jobConsumerService.getJob(job.getJobId(), TestUtil.ORG_ID);

      Assertions.assertEquals(job.getJobId(), job1.getJobId(), "JobId");
    }

    @Test
    void getJobNotFound() throws JobDomainException {
      JobDto job = testUtil.createJob(JobTypeEnum.UPLOAD_PROCESSING_LEAD_TIMES, 2);
      when(jobDomain.findJobByJobIdAndOrgId(job.getJobId(), TestUtil.ORG_ID)).thenReturn(null);

      JobException exception =
          Assertions.assertThrows(
              JobException.class, () -> jobConsumerService.getJob(job.getJobId(), TestUtil.ORG_ID));

      Assertions.assertEquals(
          "Exception while retrieving the job by jobId",
          exception.getMessage(),
          "Exception message");

      Assertions.assertEquals(job.getJobId(), exception.getJobId(), "Job Id");
    }

    @Test
    void getJobException() throws JobDomainException {
      JobDto job = testUtil.createJob(JobTypeEnum.UPLOAD_PROCESSING_LEAD_TIMES, 2);
      when(jobDomain.findJobByJobIdAndOrgId(any(), any())).thenThrow(new RuntimeException());

      JobException exception =
          Assertions.assertThrows(
              JobException.class, () -> jobConsumerService.getJob(job.getJobId(), TestUtil.ORG_ID));

      Assertions.assertEquals(
          "Exception while retrieving the job by jobId",
          exception.getMessage(),
          "Exception message");

      Assertions.assertEquals(job.getJobId(), exception.getJobId(), "Exception message");
    }
  }

  @Nested
  class GetJobs {

    @Test
    void getJobsSuccess() throws JobException {

      int pageNo = 1;
      int pageSize = 2;

      List<JobResponse> jobDtoList = testUtil.createJobResponseList();
      Page<JobResponse> page = testUtil.createPageJobDto(2, jobDtoList, jobDtoList.size());
      when(jobDomain.findJobsByJobParam(any(), any(), any(), any(), any(), anyInt(), anyInt()))
          .thenReturn(page);

      Page<JobResponse> pageResponse =
          jobConsumerService.getJobs(
              TestUtil.ORG_ID,
              Optional.empty(),
              Optional.of(2),
              TestUtil.DEFAULT_SORT_FIELD.orElse(""),
              TestUtil.DEFAULT_SORT_ORDER.orElse(""),
              pageNo,
              pageSize);

      Assertions.assertEquals(2, pageResponse.getTotalPages(), "Pagination Total pages");
      Assertions.assertEquals(jobDtoList, pageResponse.getContent(), "Paginated Jobs ");
      verify(jobDomain, times(1))
          .findJobsByJobParam(any(), any(), any(), any(), any(), anyInt(), anyInt());
    }

    @Test
    void getJobsRuntimeException() {
      when(jobDomain.findJobsByJobParam(any(), any(), any(), any(), any(), anyInt(), anyInt()))
          .thenThrow(new RuntimeException());

      JobException exception =
          Assertions.assertThrows(
              JobException.class,
              () ->
                  jobConsumerService.getJobs(
                      TestUtil.ORG_ID,
                      Optional.empty(),
                      Optional.of(2),
                      TestUtil.DEFAULT_SORT_FIELD.orElse(""),
                      TestUtil.DEFAULT_SORT_ORDER.orElse(""),
                      2,
                      5));

      Assertions.assertEquals(
          "Exception while retrieving the list of jobs", exception.getMessage(), "Expected Error");

      Assertions.assertEquals(2, exception.getPageNo().intValue(), "Expected pageNo");
    }
  }

  @Nested
  class UpdateJob {

    @Test
    void updateJobSuccess() throws Exception {
      JobEntity jobEntity = testUtil.createJobEntity(JobTypeEnum.UPLOAD_PROCESSING_LEAD_TIMES, 2);
      jobEntity.setJobId(TestUtil.JOB_ID);
      jobEntity.setStatus(JobStatusEnum.PROCESSED);
      RecordStatusDto recordStatus =
          testUtil.createRecordStatus(
              TestUtil.JOB_ID,
              TestUtil.ORG_ID,
              ApiStatusEnum.SUCCESS,
              HttpStatus.OK,
              null,
              JobTypeEnum.UPLOAD_PROCESSING_LEAD_TIMES,
              2);
      when(jobDomain.save(any())).thenReturn(jobEntity);
      when(jobDomain.findJobByJobIdAndOrgId(any(), any())).thenReturn(jobEntity);
      jobConsumerService.updateJob(recordStatus, jobEntity.getTotalRecords());

      AuditLog auditLog = new AuditLog();
      auditLog.setStatus(JobStatusEnum.RUNNING);
      jobEntity.setStatus(JobStatusEnum.RUNNING);

      JobEntity updatedJobEntity =
          testUtil.createJobEntity(JobTypeEnum.UPLOAD_PROCESSING_LEAD_TIMES, 2);
      updatedJobEntity.setJobId(TestUtil.JOB_ID);
      updatedJobEntity.setSuccessCount(1);
      updatedJobEntity.setProcessedRecords(1);
      updatedJobEntity.setStatus(JobStatusEnum.RUNNING);
      List<AuditLog> oldAuditLog = new ArrayList<>(Arrays.asList(jobEntity.getAuditLog()));
      oldAuditLog.add(auditLog);
      updatedJobEntity.setAuditLog(oldAuditLog.toArray(new AuditLog[0]));

      when(jobDomain.save(any(JobEntity.class)))
          .thenAnswer(
              je -> {
                JobEntity actualJobEntity = je.getArgument(0);
                Assertions.assertEquals(
                    updatedJobEntity.getJobId(), actualJobEntity.getJobId(), "Job Id");
                Assertions.assertEquals(
                    updatedJobEntity.getSuccessCount(),
                    actualJobEntity.getSuccessCount(),
                    "Success count");
                Assertions.assertEquals(
                    updatedJobEntity.getProcessedRecords(),
                    actualJobEntity.getProcessedRecords(),
                    "Total processed records");
                Assertions.assertEquals(
                    updatedJobEntity.getStatus(), actualJobEntity.getStatus(), "Status");
                Assertions.assertEquals(
                    updatedJobEntity.getAuditLog().length,
                    actualJobEntity.getAuditLog().length,
                    "Audit log length");
                return actualJobEntity;
              });
      verify(jobDomain, times(1)).updateJobStatus(any(), any(), anyBoolean());
    }

    @Test
    void updateJobSuccess2() throws Exception {
      JobEntity jobEntity = testUtil.createJobEntity(JobTypeEnum.UPLOAD_PROCESSING_LEAD_TIMES, 2);
      jobEntity.setProcessedRecords(1);
      jobEntity.setJobId(TestUtil.JOB_ID);
      jobEntity.setStatus(JobStatusEnum.PROCESSED);
      RecordStatusDto recordStatus =
          testUtil.createRecordStatus(
              TestUtil.JOB_ID,
              TestUtil.ORG_ID,
              ApiStatusEnum.SUCCESS,
              HttpStatus.OK,
              null,
              JobTypeEnum.UPLOAD_PROCESSING_LEAD_TIMES,
              1);
      when(jobDomain.save(any())).thenReturn(jobEntity);
      when(jobDomain.findJobByJobIdAndOrgId(any(), any())).thenReturn(jobEntity);
      jobConsumerService.updateJob(recordStatus, jobEntity.getTotalRecords());

      AuditLog auditLog = new AuditLog();
      auditLog.setStatus(JobStatusEnum.RUNNING);
      jobEntity.setStatus(JobStatusEnum.RUNNING);

      JobEntity updatedJobEntity =
          testUtil.createJobEntity(JobTypeEnum.UPLOAD_PROCESSING_LEAD_TIMES, 2);
      updatedJobEntity.setJobId(TestUtil.JOB_ID);
      updatedJobEntity.setSuccessCount(1);
      updatedJobEntity.setProcessedRecords(1);
      updatedJobEntity.setStatus(JobStatusEnum.RUNNING);
      List<AuditLog> oldAuditLog = new ArrayList<>(Arrays.asList(jobEntity.getAuditLog()));
      oldAuditLog.add(auditLog);
      updatedJobEntity.setAuditLog(oldAuditLog.toArray(new AuditLog[0]));

      when(jobDomain.save(any(JobEntity.class)))
          .thenAnswer(
              je -> {
                JobEntity actualJobEntity = je.getArgument(0);
                Assertions.assertEquals(
                    updatedJobEntity.getJobId(), actualJobEntity.getJobId(), "Job Id");
                Assertions.assertEquals(
                    updatedJobEntity.getSuccessCount(),
                    actualJobEntity.getSuccessCount(),
                    "Success count");
                Assertions.assertEquals(
                    updatedJobEntity.getProcessedRecords(),
                    actualJobEntity.getProcessedRecords(),
                    "Total processed records");
                Assertions.assertEquals(
                    updatedJobEntity.getStatus(), actualJobEntity.getStatus(), "Status");
                Assertions.assertEquals(
                    updatedJobEntity.getAuditLog().length,
                    actualJobEntity.getAuditLog().length,
                    "Audit log length");
                return actualJobEntity;
              });
      verify(jobDomain, times(1)).updateJobStatus(any(), any(), anyBoolean());
    }

    @Test
    void updateJobSuccess3() throws Exception {
      JobEntity jobEntity = testUtil.createJobEntity(JobTypeEnum.UPLOAD_PROCESSING_LEAD_TIMES, 2);
      jobEntity.setProcessedRecords(2);
      jobEntity.setJobId(TestUtil.JOB_ID);
      jobEntity.setStatus(JobStatusEnum.COMPLETED);

      RecordStatusDto recordStatus =
          testUtil.createRecordStatus(
              TestUtil.JOB_ID,
              TestUtil.ORG_ID,
              ApiStatusEnum.SUCCESS,
              HttpStatus.OK,
              null,
              JobTypeEnum.UPLOAD_PROCESSING_LEAD_TIMES,
              1);
      when(jobDomain.save(any())).thenReturn(jobEntity);
      when(jobDomain.findJobByJobIdAndOrgId(any(), any())).thenReturn(jobEntity);
      jobConsumerService.updateJob(recordStatus, jobEntity.getTotalRecords());

      AuditLog auditLog = new AuditLog();
      auditLog.setStatus(JobStatusEnum.RUNNING);
      jobEntity.setStatus(JobStatusEnum.RUNNING);

      JobEntity updatedJobEntity =
          testUtil.createJobEntity(JobTypeEnum.UPLOAD_PROCESSING_LEAD_TIMES, 2);
      updatedJobEntity.setJobId(TestUtil.JOB_ID);
      updatedJobEntity.setSuccessCount(1);
      updatedJobEntity.setProcessedRecords(1);
      updatedJobEntity.setStatus(JobStatusEnum.RUNNING);
      List<AuditLog> oldAuditLog = new ArrayList<>(Arrays.asList(jobEntity.getAuditLog()));
      oldAuditLog.add(auditLog);
      updatedJobEntity.setAuditLog(oldAuditLog.toArray(new AuditLog[0]));

      when(jobDomain.save(any(JobEntity.class)))
          .thenAnswer(
              je -> {
                JobEntity actualJobEntity = je.getArgument(0);
                Assertions.assertEquals(
                    updatedJobEntity.getJobId(), actualJobEntity.getJobId(), "Job Id");
                Assertions.assertEquals(
                    updatedJobEntity.getSuccessCount(),
                    actualJobEntity.getSuccessCount(),
                    "Success count");
                Assertions.assertEquals(
                    updatedJobEntity.getProcessedRecords(),
                    actualJobEntity.getProcessedRecords(),
                    "Total processed records");
                Assertions.assertEquals(
                    updatedJobEntity.getStatus(), actualJobEntity.getStatus(), "Status");
                Assertions.assertEquals(
                    updatedJobEntity.getAuditLog().length,
                    actualJobEntity.getAuditLog().length,
                    "Audit log length");
                return actualJobEntity;
              });
      verify(jobDomain, times(1)).updateJobStatus(any(), any(), anyBoolean());
    }

    @Test
    void updateJobRecordStatusFailure() throws JobDomainException {
      RecordStatusDto recordStatusDto = mock(RecordStatusDto.class);
      int retryCount = 5;
      JobEntity jobEntity = mock(JobEntity.class);

      when(recordStatusDto.getStatus()).thenReturn(ApiStatusEnum.FAILURE);
      when(jobEntity.getFailureCount()).thenReturn(2);
      when(jobEntity.getProcessedRecords()).thenReturn(3);
      when(jobEntity.getTotalRecords()).thenReturn(3);
      when(jobDomain.findJobByJobIdAndOrgId(any(), any())).thenReturn(jobEntity);
      when(jobDomain.save(any())).thenReturn(jobEntity);
      when(jobDomain.save(any())).thenReturn(null);

      Assertions.assertThrows(
          JobException.class, () -> jobConsumerService.updateJob(recordStatusDto, retryCount));
    }

    @Test
    void updateJobValidateAuditLogs() throws Exception {
      JobEntity jobEntity = testUtil.createJobEntity(JobTypeEnum.UPLOAD_PROCESSING_LEAD_TIMES, 2);
      jobEntity.setJobId(TestUtil.JOB_ID);
      jobEntity.setStatus(JobStatusEnum.RUNNING);
      RecordStatusDto recordStatus =
          testUtil.createRecordStatus(
              TestUtil.JOB_ID,
              TestUtil.ORG_ID,
              ApiStatusEnum.SUCCESS,
              HttpStatus.OK,
              null,
              JobTypeEnum.UPLOAD_PROCESSING_LEAD_TIMES,
              2);
      when(jobDomain.save(any())).thenReturn(jobEntity);
      when(jobDomain.findJobByJobIdAndOrgId(any(), any())).thenReturn(jobEntity);
      jobConsumerService.updateJob(recordStatus, jobEntity.getTotalRecords());

      jobEntity.setStatus(JobStatusEnum.RUNNING);

      JobEntity updatedJobEntity =
          testUtil.createJobEntity(JobTypeEnum.UPLOAD_PROCESSING_LEAD_TIMES, 2);
      updatedJobEntity.setJobId(TestUtil.JOB_ID);
      updatedJobEntity.setSuccessCount(1);
      updatedJobEntity.setProcessedRecords(1);
      updatedJobEntity.setStatus(JobStatusEnum.RUNNING);

      when(jobDomain.save(any(JobEntity.class)))
          .thenAnswer(
              je -> {
                JobEntity actualJobEntity = je.getArgument(0);
                Assertions.assertEquals(
                    updatedJobEntity.getJobId(), actualJobEntity.getJobId(), "Job Id");
                Assertions.assertEquals(
                    updatedJobEntity.getSuccessCount(),
                    actualJobEntity.getSuccessCount(),
                    "Success count");
                Assertions.assertEquals(
                    updatedJobEntity.getProcessedRecords(),
                    actualJobEntity.getProcessedRecords(),
                    "Total processed records");
                Assertions.assertEquals(
                    updatedJobEntity.getStatus(), actualJobEntity.getStatus(), "Status");
                Assertions.assertEquals(
                    updatedJobEntity.getAuditLog().length,
                    actualJobEntity.getAuditLog().length,
                    "Audit log length");
                return actualJobEntity;
              });
      verify(jobDomain, times(1)).updateJobStatus(any(), any(), anyBoolean());
    }

    @Test
    void updateJobDocumentVersionError() throws JobDomainException {
      JobEntity jobEntity = testUtil.createJobEntity(JobTypeEnum.UPLOAD_PROCESSING_LEAD_TIMES, 2);
      jobEntity.setJobId(TestUtil.JOB_ID);
      RecordStatusDto recordStatus =
          testUtil.createRecordStatus(
              TestUtil.JOB_ID,
              TestUtil.ORG_ID,
              ApiStatusEnum.SUCCESS,
              HttpStatus.OK,
              null,
              JobTypeEnum.UPLOAD_PROCESSING_LEAD_TIMES,
              2);

      Exception r =
          new OptimisticLockingFailureException(
              "org.springframework.dao.OptimisticLockingFailureException: Cannot save \" +\n"
                  + "                \"entity %s with version 1 to collection JOB. Has it been modified meanwhile?");

      when(jobDomain.findJobByJobIdAndOrgId(jobEntity.getJobId(), jobEntity.getOrgId()))
          .thenReturn(jobEntity);

      doThrow(new OptimisticLockingFailureException("Document version " + "different", r))
          .when(jobDomain)
          .updateJobStatus(any(), any(), anyBoolean());

      JobException exception =
          Assertions.assertThrows(
              JobException.class,
              () -> jobConsumerService.updateJob(recordStatus, jobEntity.getTotalRecords()));

      Assertions.assertEquals(
          "Attempt to update job with wrong version", exception.getMessage(), "Expected Error");
    }

    @Test
    void updateJobException() throws JobDomainException {
      JobEntity jobEntity = testUtil.createJobEntity(JobTypeEnum.UPLOAD_PROCESSING_LEAD_TIMES, 2);
      jobEntity.setJobId(TestUtil.JOB_ID);
      RecordStatusDto recordStatus =
          testUtil.createRecordStatus(
              TestUtil.JOB_ID,
              TestUtil.ORG_ID,
              ApiStatusEnum.SUCCESS,
              HttpStatus.OK,
              null,
              JobTypeEnum.UPLOAD_PROCESSING_LEAD_TIMES,
              2);
      when(jobDomain.save(any())).thenThrow(new RuntimeException());

      JobException exception =
          Assertions.assertThrows(
              JobException.class,
              () -> jobConsumerService.updateJob(recordStatus, jobEntity.getTotalRecords()));

      Assertions.assertEquals(
          "Exception while updating job entity", exception.getMessage(), "Expected Error");
    }
  }

  @Test
  void getJobResults() throws JobException {
    RecordStatusDto recordStatusDto =
        testUtil.createRecordStatus(
            TestUtil.JOB_ID,
            TestUtil.ORG_ID,
            ApiStatusEnum.FAILURE,
            HttpStatus.BAD_REQUEST,
            null,
            JobTypeEnum.UPLOAD_NODE_CARRIER,
            1);
    when(jobRecordDomain.fetchJobRecordsByFiltersPaginatedOutput(
            anyString(), anyString(), any(), anyInt(), anyInt()))
        .thenReturn(testUtil.createPageRecordStatusDtoDto(5, List.of(recordStatusDto), 20));

    Page<RecordStatusDto> page =
        jobConsumerService.getJobResults(
            TestUtil.ORG_ID, TestUtil.JOB_ID, Optional.of(ApiStatusEnum.FAILURE.name()), 1, 15);
    Assertions.assertNotNull(page);
    Assertions.assertFalse(CollectionUtils.isEmpty(page.getContent()));
  }
}
