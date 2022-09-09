package com.hbc.jobs.dashboard.service;

import static com.hbc.jobs.framework.common.domain.enums.RecordDataTypeEnum.getTypeFromString;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hbc.common.base.PagePayload;
import com.hbc.common.constants.CommonConstants;
import com.hbc.common.context.CurrentThreadContext;
import com.hbc.common.response.BaseResponse;
import com.hbc.common.util.JsonUtil;
import com.hbc.jobs.dashboard.exception.JobException;
import com.hbc.jobs.framework.common.clients.JobsConsumerClient;
import com.hbc.jobs.framework.common.domain.enums.JobStatusEnum;
import com.hbc.jobs.framework.common.domain.enums.JobTypeEnum;
import com.hbc.jobs.framework.common.domain.enums.RecordDataTypeEnum;
import com.hbc.jobs.framework.common.domain.pojo.AuditLog;
import com.hbc.jobs.framework.common.domain.pojo.JobDto;
import com.hbc.jobs.framework.common.domain.pojo.RecordDto;
import com.hbc.jobs.framework.common.domain.pojo.RecordInputDto;
import com.hbc.jobs.framework.common.domain.pojo.RecordInputWrapper;
import com.hbc.jobs.framework.common.domain.pojo.RecordStatusDto;
import com.hbc.jobs.framework.common.utils.ExceptionUtils;
import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReaderBuilder;
import feign.FeignException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class JobService {
  private final KafkaTemplate<String, RecordDto> kafkaTemplate;
  private final JobsConsumerClient jobsConsumerClient;

  @Value("${jobs-framework.kafka-publish.topic-name}")
  String dashboardProducerName;

  /**
   * Process job given in csv/json file
   *
   * @param inputFile
   * @param orgId
   * @param jobType
   * @return
   * @throws JobException
   */
  public JobDto processJobOffline(MultipartFile inputFile, String orgId, JobTypeEnum jobType)
      throws JobException {
    log.debug("Inside processJobOffline service");

    try {
      RecordDataTypeEnum fileType = getFileType(inputFile);
      final List<String> recordsFromFile = getRecordsFromFile(inputFile);
      JobDto job = constructJob(recordsFromFile.size(), orgId, jobType);
      BaseResponse<JobDto> baseResponse = jobsConsumerClient.createJob(job);
      JobDto jobResponse = baseResponse.getPayload();
      IntStream.range(0, recordsFromFile.size())
          .forEach(
              recordNumber ->
                  publishToKafka(
                      recordNumber, recordsFromFile.get(recordNumber), jobResponse, fileType));
      return job;
    } catch (FeignException e) {
      log.error("Error while processing csv/json file", e);
      var errorResponse = ExceptionUtils.parseFeignException(e);
      throw new JobException(errorResponse.getMessage(), e, null, jobType);
    } catch (Exception e) {
      log.error("Unable to process csv/json file", e);
      throw new JobException("Error while processing csv/json file", e, null, jobType);
    }
  }

  /**
   * Construct the list of records from the csv/json file
   *
   * @param inputFile
   * @return
   * @throws JobException
   */
  private List<String> getRecordsFromFile(MultipartFile inputFile) throws JobException {
    RecordDataTypeEnum fileExtension = getFileType(inputFile);
    List<String> records;
    if (fileExtension == RecordDataTypeEnum.JSON) {
      records = parseJSON(inputFile);
    } else {
      records = parseCSV(inputFile);
    }
    return records;
  }

  /**
   * @param inputFile
   * @return
   */
  private RecordDataTypeEnum getFileType(MultipartFile inputFile) {
    String originalFilename = inputFile.getOriginalFilename();
    if (!ObjectUtils.isEmpty(originalFilename)) {
      String[] fileName = originalFilename.split("\\.");
      String extension = fileName[fileName.length - 1].toLowerCase();
      return getTypeFromString(extension.toLowerCase());
    } else {
      return null;
    }
  }

  /**
   * @param inputFile
   * @return
   * @throws JobException
   */
  private List<String> parseCSV(MultipartFile inputFile) throws JobException {
    try {
      Reader reader = new InputStreamReader(inputFile.getInputStream());

      CSVParser parser =
          new CSVParserBuilder().withSeparator(',').withIgnoreQuotations(false).build();

      var csvReader = new CSVReaderBuilder(reader).withSkipLines(0).withCSVParser(parser).build();

      List<String[]> rows = csvReader.readAll();
      final String header = Arrays.stream(rows.get(0)).collect(Collectors.joining(","));
      final var MARKER = "\n";

      return rows.stream()
          .skip(1)
          .filter(
              row -> {
                String line = Arrays.stream(row).collect(Collectors.joining(""));
                return !line.trim().isEmpty();
              })
          .map(row -> header + MARKER + Arrays.stream(row).collect(Collectors.joining(",")))
          .collect(Collectors.toList());
    } catch (Exception e) {
      log.error("Unable to parse the csv file", e);
      throw new JobException("Error while parsing csv file", e, null, null);
    }
  }

  /**
   * @param inputFile
   * @return
   * @throws JobException
   */
  private List<String> parseJSON(MultipartFile inputFile) throws JobException {
    try {
      return parseJSON(new String(inputFile.getBytes()));
    } catch (Exception e) {
      log.error("Unable to parse the json file", e);
      throw new JobException("Error while parsing json file", e, null, null);
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
   * @param job
   * @param fileType
   */
  private void publishToKafka(int recordId, String data, JobDto job, RecordDataTypeEnum fileType) {
    log.debug("Inside publish to kafka method");
    var recordDto = new RecordDto();
    recordDto.setRecordId(recordId);
    recordDto.setRecordData(data);
    recordDto.setInputs(parseRecordInputs(data, job.getTotalRecords()));
    recordDto.setJob(job);
    recordDto.setRecordType(fileType);
    Message<RecordDto> message;
    message =
        MessageBuilder.withPayload(recordDto)
            .setHeader(KafkaHeaders.TOPIC, dashboardProducerName)
            .setHeader(CommonConstants.HEADER_USER, job.getUserId())
            .setHeader("jwtToken", CurrentThreadContext.getLogContext().getAuthorizationHeader())
            .build();

    try {
      log.debug("Publishing to kafka");
      kafkaTemplate
          .send(message)
          .addCallback(
              e -> log.error("JobService::publishToKafka():success to publish record dto", e),
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
   * @param jobTypeEnum consumers.
   * @return
   */
  private JobDto constructJob(int totalRecords, String orgId, JobTypeEnum jobTypeEnum) {
    var job = new JobDto();
    job.setJobId(UUID.randomUUID().toString());

    job.setTotalRecords(totalRecords);
    job.setJobType(jobTypeEnum);

    job.setProcessedRecords(0);
    job.setRemainingRecords(totalRecords - job.getProcessedRecords());
    job.setFailureCount(0);
    job.setSuccessCount(0);
    job.setStatus(JobStatusEnum.SUBMITTED);
    job.setOrgId(orgId);

    var auditLog = new AuditLog();
    auditLog.setStatus(JobStatusEnum.SUBMITTED);
    auditLog.setTimeStamp(new Date());
    job.setAuditLog(Arrays.asList(auditLog));
    return job;
  }

  /**
   * Retrieve the job
   *
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
   * @param jobType
   * @param days
   * @param pageNo
   * @param pageSize
   * @return
   * @throws JobException
   */
  @SuppressWarnings("squid:S107")
  public PagePayload<JobDto> getJobsByJobInfo(
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

  /**
   * @param request
   * @param orgId
   * @param jobType
   * @return
   * @throws JobException
   */
  public JobDto processJobJsonOffline(
      String request, String orgId, JobTypeEnum jobType, Optional<String> jobId)
      throws JobException {
    log.debug("Inside processJobOffline service");

    try {
      List<String> jsonList = parseJSON(request);
      JobDto jobResponse = null;
      if (jobId.isPresent() && !ObjectUtils.isEmpty(jobId.orElse(null))) {
        jobResponse = jobsConsumerClient.getJob(orgId, jobId.get()).getPayload();
      } else {
        JobDto job = constructJob(jsonList.size(), orgId, jobType);
        BaseResponse<JobDto> baseResponse = jobsConsumerClient.createJob(job);
        jobResponse = baseResponse.getPayload();
      }
      JobDto finalJobResponse = jobResponse;
      IntStream.range(0, jsonList.size())
          .forEach(
              recordNumber ->
                  publishToKafka(
                      recordNumber,
                      jsonList.get(recordNumber),
                      finalJobResponse,
                      RecordDataTypeEnum.JSON));
      return jobResponse;
    } catch (FeignException e) {
      log.error("Error while submitting the job", e);
      throw new JobException("Failed to submit the job", e, null, jobType);
    } catch (Exception e) {
      log.error("Unable to process json request", e);
      throw new JobException("Exception while processing job json request", e, null, jobType);
    }
  }

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
}
