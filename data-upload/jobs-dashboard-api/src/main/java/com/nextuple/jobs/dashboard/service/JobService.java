package com.nextuple.jobs.dashboard.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nextuple.common.base.PagePayload;
import com.nextuple.common.constants.CommonConstants;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.common.util.JsonUtil;
import com.nextuple.jobs.consumers.domain.JobDomain;
import com.nextuple.jobs.consumers.domain.mapper.JobMapper;
import com.nextuple.jobs.consumers.exception.JobDomainException;
import com.nextuple.jobs.consumers.feign.AuthTokenResponse;
import com.nextuple.jobs.consumers.service.AuthTokenService;
import com.nextuple.jobs.dashboard.exception.JobException;
import com.nextuple.jobs.framework.common.clients.FileMetaDataClient;
import com.nextuple.jobs.framework.common.clients.JobsConsumerClient;
import com.nextuple.jobs.framework.common.domain.enums.JobStatusEnum;
import com.nextuple.jobs.framework.common.domain.enums.JobTypeEnum;
import com.nextuple.jobs.framework.common.domain.enums.RecordDataTypeEnum;
import com.nextuple.jobs.framework.common.domain.outbound.FileMetaDataResponse;
import com.nextuple.jobs.framework.common.domain.outbound.JobResponse;
import com.nextuple.jobs.framework.common.domain.pojo.AuditLog;
import com.nextuple.jobs.framework.common.domain.pojo.JobDto;
import com.nextuple.jobs.framework.common.domain.pojo.RecordDto;
import com.nextuple.jobs.framework.common.domain.pojo.RecordInputDto;
import com.nextuple.jobs.framework.common.domain.pojo.RecordInputWrapper;
import com.nextuple.jobs.framework.common.domain.pojo.RecordStatusDto;
import com.nextuple.jobs.framework.common.service.FileService;
import com.nextuple.jobs.framework.common.utils.ExceptionUtils;
import com.opencsv.exceptions.CsvException;
import feign.FeignException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@Service
@RequiredArgsConstructor
@Slf4j
public class JobService {

  private final KafkaTemplate<String, RecordDto> kafkaTemplate;
  private final JobsConsumerClient jobsConsumerClient;
  private final JobDomain jobDomain;
  private final FileMetaDataClient fileMetaDataClient;
  private final FileService fileService;
  private final AuthTokenService authTokenService;
  private final ProcessFileContentsMapperFactory processFileContentsMapperFactory;
  private static final JobMapper INSTANCE = Mappers.getMapper(JobMapper.class);

  @Value("${jobs-framework.kafka-publish.topic-name}")
  String dashboardProducerName;

  /**
   * Process job given in csv/json file
   *
   * @param inputFile
   * @param orgId
   * @param jobType
   * @param fileName
   * @return
   * @throws JobException
   */
  public JobResponse processJobOffline(
      ByteArrayResource inputFile, String orgId, JobTypeEnum jobType, String fileName)
      throws JobException {
    log.debug("Inside processJobOffline service");

    JobDto job = constructJob(0, orgId, jobType, inputFile, fileName, null);
    return createJob(jobType, job);
  }

  private JobResponse createJob(JobTypeEnum jobType, JobDto job) throws JobException {
    try {
      BaseResponse<JobResponse> baseResponse = jobsConsumerClient.createJob(job);
      return baseResponse.getPayload();
    } catch (FeignException e) {
      log.error("Error while creating job", e);
      var errorResponse = ExceptionUtils.parseFeignException(e);
      throw new JobException(errorResponse.getMessage(), e, null, jobType);
    } catch (Exception e) {
      log.error("Error while process job offline", e);
      throw new JobException("Error while process job offline", e, null, jobType);
    }
  }

  /**
   * Get the object list from JSON file
   *
   * @param input
   * @return
   * @throws JobException
   */
  public List<String> parseJSON(String input) throws JobException {
    try {
      var objectMapper = new ObjectMapper();
      List<Object> mappedJson = objectMapper.readValue(input, List.class);
      return mappedJson.stream().map(JsonUtil::convert).collect(Collectors.toList());
    } catch (Exception e) {
      log.error("Unable to parse the json file", e);
      throw new JobException("Error while parsing json file", e, null, null);
    }
  }

  /**
   * @param recordId
   * @param data
   * @param jobResponse
   * @param fileType
   * @param authToken
   */
  private void publishToKafka(
      int recordId,
      String data,
      JobResponse jobResponse,
      RecordDataTypeEnum fileType,
      AuthTokenResponse authToken,
      LocalDateTime expiryTs) {
    log.debug("Inside publish to kafka method");
    var recordDto = new RecordDto();
    recordDto.setRecordId(recordId);
    recordDto.setRecordData(data);
    recordDto.setInputs(parseRecordInputs(data, jobResponse.getTotalRecords()));
    recordDto.setOrgId(jobResponse.getOrgId());
    recordDto.setJobId(jobResponse.getJobId());
    recordDto.setJobType(jobResponse.getJobType());
    recordDto.setTotalRecords(jobResponse.getTotalRecords());
    recordDto.setRecordType(fileType);
    Message<RecordDto> message;
    message =
        MessageBuilder.withPayload(recordDto)
            .setHeader(KafkaHeaders.TOPIC, dashboardProducerName)
            .setHeader(CommonConstants.AUTHORIZATION_HEADER, authToken.getAccessToken())
            .setHeader(
                CommonConstants.AUTH_EXPIRY_TIMESTAMP_HEADER,
                expiryTs.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
            .setHeader(CommonConstants.HEADER_USER, jobResponse.getUserId())
            .build();

    try {
      log.debug("Publishing to kafka");
      kafkaTemplate
          .send(message)
          .addCallback(
              e -> log.debug("JobService::publishToKafka():success to publish record dto", e),
              e -> log.error("JobService::publishToKafka():failed to publish record dto", e));
      log.debug("Publish to kafka method ends");
    } catch (Exception e) {
      log.error("JobService::publishToKafka():failed to publish record dto", e);
      throw e;
    }
  }

  private RecordInputDto parseRecordInputs(String data, int totalRecords) {
    var recordInputWrapper = JsonUtil.convertToObject(data, RecordInputWrapper.class);
    var inputs = new RecordInputDto();
    if (recordInputWrapper != null && recordInputWrapper.getInputs() != null) {
      inputs = recordInputWrapper.getInputs();
    }

    if (!inputs.containsKey("retryCount")) {
      inputs.put("retryCount", Integer.valueOf(totalRecords));
    }

    return inputs;
  }

  /**
   * @param totalRecords
   * @param orgId
   * @param jobTypeEnum
   * @param inputFile
   * @param fileName
   * @return
   */
  private JobDto constructJob(
      int totalRecords,
      String orgId,
      JobTypeEnum jobTypeEnum,
      ByteArrayResource inputFile,
      String fileName,
      Long fileMetaDataId) {
    var job = new JobDto();
    job.setJobId(UUID.randomUUID().toString());

    job.setTotalRecords(totalRecords);
    job.setJobType(jobTypeEnum);
    job.setFileName(fileName);
    if (inputFile != null) {
      job.setFile(inputFile.getByteArray());
    }
    job.setProcessedRecords(0);
    job.setRemainingRecords(totalRecords - job.getProcessedRecords());
    job.setFailureCount(0);
    job.setSuccessCount(0);
    job.setStatus(JobStatusEnum.SUBMITTED);
    job.setOrgId(orgId);
    job.setFileMetaDataId(fileMetaDataId);

    var auditLog = new AuditLog();
    auditLog.setStatus(JobStatusEnum.SUBMITTED);
    auditLog.setTimeStamp(new Date());
    job.setAuditLog(Arrays.asList(auditLog));
    return job;
  }

  private JobDto constructJob(
      int totalRecords, String orgId, JobTypeEnum jobTypeEnum, long fileMetaDataId) {
    return constructJob(totalRecords, orgId, jobTypeEnum, null, null, fileMetaDataId);
  }

  /**
   * Retrieve the job
   *
   * @param orgId
   * @param jobId
   * @return
   * @throws JobException
   */
  public JobDto getJob(String orgId, String jobId) throws JobException {
    log.debug("Inside getJob service");

    try {
      BaseResponse<JobDto> baseResponse = jobsConsumerClient.getJob(orgId, jobId);
      return baseResponse.getPayload();
    } catch (FeignException e) {
      log.error("Error while retrieving the job by jobId.", e);
      var errorResponse = ExceptionUtils.parseFeignException(e);
      throw new JobException(errorResponse.getMessage(), e, jobId, null);
    } catch (Exception e) {
      log.error("Unable to create job", e);
      throw new JobException("Error while retrieving the job", e, jobId, null);
    }
  }

  /**
   * @param orgId
   * @param jobType
   * @param days
   * @param sortField
   * @param sortOrder
   * @param pageNo
   * @param pageSize
   * @return
   * @throws JobException
   */
  @SuppressWarnings("squid:S107")
  public PagePayload<JobResponse> getJobsByJobInfo(
      String orgId,
      Optional<String> jobType,
      Optional<Integer> days,
      Optional<String> sortField,
      Optional<String> sortOrder,
      int pageNo,
      int pageSize)
      throws JobException {
    log.debug("--Inside getJobsByJobInfo()--");
    try {
      return jobsConsumerClient
          .getJobsByFilter(
              orgId,
              jobType.orElse(null),
              days.orElse(null),
              sortField.orElse(null),
              sortOrder.orElse(null),
              pageNo,
              pageSize)
          .getPayload();

    } catch (FeignException e) {
      log.error("Error while retrieving the list of jobs", e);
      var errorResponse = ExceptionUtils.parseFeignException(e);
      throw new JobException(errorResponse.getMessage(), e, null, null);
    } catch (Exception e) {
      log.error("Unable to retrieve the job information", e);
      throw new JobException("Exception while retrieving the jobs", e, null, null);
    }
  }

  public JobResponse processJobJsonOffline(
      String orgId, JobTypeEnum jobType, Optional<String> jobId) throws JobException {
    return processJobJsonOffline(null, orgId, jobType, jobId);
  }

  /**
   * @param request
   * @param orgId
   * @param jobType
   * @param jobId
   * @return
   * @throws JobException
   */
  public JobResponse processJobJsonOffline(
      String request, String orgId, JobTypeEnum jobType, Optional<String> jobId) // NOSONAR
      throws JobException {
    log.debug("Inside processJobOffline service");

    try {
      var jobResponse = new JobResponse();
      var uploadRequestList = new ArrayList<>();
      if (jobId.isPresent() && !ObjectUtils.isEmpty(jobId.orElse(null))) {
        var jobDto = jobsConsumerClient.getJob(orgId, jobId.get()).getPayload();
        InputStream inputStream = null;
        if (!ObjectUtils.isEmpty(jobDto.getFileMetaDataId())) {
          BaseResponse<FileMetaDataResponse> fileMetaDataResponse =
              fileMetaDataClient.findFileMetadataById(jobDto.getFileMetaDataId());
          if (!ObjectUtils.isEmpty(fileMetaDataResponse.getPayload())) {
            String[] fileUrlList = fileMetaDataResponse.getPayload().getPath().split("/", 2);
            String bucketName = fileUrlList[0];
            var filePath = fileUrlList[1];
            long fileDownloadTime = System.currentTimeMillis();
            var fileResponse = fileService.getFile(bucketName, filePath);
            log.debug(
                "File download task took {} milliseconds for jobId: {}",
                System.currentTimeMillis() - fileDownloadTime,
                jobId);
            inputStream = fileResponse.getInputStream();
          }
        } else if (jobDto.getFile().length != 0) {
          inputStream = new ByteArrayInputStream(jobDto.getFile());
        }
        jobResponse = processFileContents(jobDto, uploadRequestList, inputStream);
      }
      return jobResponse;
    } catch (FeignException e) {
      log.error("Error while submitting the job", e);
      throw new JobException("Failed to submit the job", e, null, jobType);
    } catch (Exception e) {
      log.error("Unable to process json request", e);
      throw new JobException("Exception while processing job json request", e, null, jobType);
    }
  }

  private JobResponse processFileContents(
      JobDto jobDto, ArrayList<Object> uploadRequestList, InputStream inputStream)
      throws IOException, CsvException, JobDomainException {
    JobResponse jobResponse;

    var processFileContents =
        processFileContentsMapperFactory.getProcessFileContentsMapper(jobDto.getJobType());
    long fileContentsProcessingTime = System.currentTimeMillis();
    uploadRequestList.addAll(
        processFileContents.updateRequestObjectsList(jobDto.getJobType(), inputStream));
    log.debug(
        "File contents processing task took {} milliseconds for jobId: {}",
        System.currentTimeMillis() - fileContentsProcessingTime,
        jobDto.getJobId());

    jobDto =
        INSTANCE.toJob(
            jobDomain.getAndUpdateJobStatusByOrgIdAndStatus(
                jobDto.getOrgId(), JobStatusEnum.PROCESSING, JobStatusEnum.PROCESSED));

    jobDto.setTotalRecords(jobDto.getTotalRecords() + uploadRequestList.size());
    jobDto.setRemainingRecords(jobDto.getRemainingRecords() + uploadRequestList.size());
    jobResponse = jobsConsumerClient.updateJob(jobDto).getPayload();

    var finalJobResponse = jobResponse;
    AuthTokenResponse authToken = authTokenService.generateAuthToken();
    LocalDateTime expiryTs = getAuthExpirationTime(authToken);
    long totalKafkaPublishTime = System.currentTimeMillis();
    IntStream.range(0, uploadRequestList.size())
        .forEach(
            recordNumber -> {
              long kafkaPublishTimePerRecord = System.currentTimeMillis();
              publishToKafka(
                  recordNumber,
                  JsonUtil.convert(uploadRequestList.get(recordNumber)),
                  finalJobResponse,
                  RecordDataTypeEnum.JSON,
                  authToken,
                  expiryTs);
              log.debug(
                  "Kafka publish per record task took {} milliseconds for recordNumber: {}",
                  System.currentTimeMillis() - kafkaPublishTimePerRecord,
                  recordNumber);
            });
    log.debug(
        "Total kafka publish for all records task took {} milliseconds",
        System.currentTimeMillis() - totalKafkaPublishTime);

    return jobResponse;
  }

  private LocalDateTime getAuthExpirationTime(AuthTokenResponse authToken) {
    var dateTime = LocalDateTime.now();
    dateTime = dateTime.plusSeconds(authToken.getExpiresIn());
    return dateTime;
  }

  /**
   * @param orgId
   * @param jobId
   * @param status
   * @return
   * @throws JobException
   */
  public List<RecordStatusDto> getJobResults(String orgId, String jobId, Optional<String> status)
      throws JobException {
    log.debug("--Inside getJobResults()--");
    try {
      return jobsConsumerClient
          .getJobRecordsByFilter(orgId, jobId, status.orElse(null))
          .getPayload();

    } catch (FeignException e) {
      log.error("Error while retrieving the job results", e);
      var errorResponse = ExceptionUtils.parseFeignException(e);
      throw new JobException(errorResponse.getMessage(), e, null, null);
    } catch (Exception e) {
      log.error("Unable to retrieve the job records information", e);
      throw new JobException("Exception while retrieving the job results", e, null, null);
    }
  }

  public JobResponse processJobOffline(String orgId, JobTypeEnum jobType, long fileMetadataId)
      throws JobException {
    log.debug("Inside process job offline service for: {}", jobType.name());

    JobDto job = constructJob(0, orgId, jobType, fileMetadataId);
    return createJob(jobType, job);
  }

  public PagePayload<RecordStatusDto> getJobRecordsByFilters(
      String orgId, String jobId, Optional<String> status, Integer pageNo, Integer pageSize)
      throws JobException {
    log.debug("--Inside getJobResults()--");
    try {
      return jobsConsumerClient
          .getJobRecordsByFilters(orgId, jobId, status.orElse(null), pageNo, pageSize)
          .getPayload();

    } catch (FeignException e) {
      log.error("Error while retrieving the job results", e);
      var errorResponse = ExceptionUtils.parseFeignException(e);
      throw new JobException(errorResponse.getMessage(), e, null, null);
    } catch (Exception e) {
      log.error("Unable to retrieve the job records information", e);
      throw new JobException("Exception while retrieving the job results", e, null, null);
    }
  }
}
