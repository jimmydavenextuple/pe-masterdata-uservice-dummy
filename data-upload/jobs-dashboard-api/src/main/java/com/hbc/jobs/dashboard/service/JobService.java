package com.hbc.jobs.dashboard.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hbc.common.base.PagePayload;
import com.hbc.common.constants.CommonConstants;
import com.hbc.common.response.BaseResponse;
import com.hbc.common.util.JsonUtil;
import com.hbc.csvdownload.common.pojo.TransitDataUpload;
import com.hbc.csvdownload.domain.pojo.ProcessingLeadTimesRaw;
import com.hbc.jobs.consumers.domain.JobDomain;
import com.hbc.jobs.consumers.domain.mapper.JobMapper;
import com.hbc.jobs.dashboard.exception.JobException;
import com.hbc.jobs.framework.common.clients.JobsConsumerClient;
import com.hbc.jobs.framework.common.domain.enums.JobStatusEnum;
import com.hbc.jobs.framework.common.domain.enums.JobTypeEnum;
import com.hbc.jobs.framework.common.domain.enums.RecordDataTypeEnum;
import com.hbc.jobs.framework.common.domain.outbound.JobResponse;
import com.hbc.jobs.framework.common.domain.pojo.AuditLog;
import com.hbc.jobs.framework.common.domain.pojo.JobDto;
import com.hbc.jobs.framework.common.domain.pojo.RecordDto;
import com.hbc.jobs.framework.common.domain.pojo.RecordInputDto;
import com.hbc.jobs.framework.common.domain.pojo.RecordInputWrapper;
import com.hbc.jobs.framework.common.domain.pojo.RecordStatusDto;
import com.hbc.jobs.framework.common.utils.ExceptionUtils;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import feign.FeignException;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
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

    try {
      JobDto job = constructJob(0, orgId, jobType, inputFile, fileName);
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
   */
  private void publishToKafka(
      int recordId, String data, JobResponse jobResponse, RecordDataTypeEnum fileType) {
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
            .setHeader(CommonConstants.HEADER_USER, jobResponse.getUserId())
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
      String fileName) {
    var job = new JobDto();
    job.setJobId(UUID.randomUUID().toString());

    job.setTotalRecords(totalRecords);
    job.setJobType(jobTypeEnum);
    job.setFileName(fileName);
    job.setFile(inputFile.getByteArray());
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
        InputStream inputStream = new ByteArrayInputStream(jobDto.getFile());

        updateRequestObjectsList(jobType, uploadRequestList, inputStream);

        jobDto =
            INSTANCE.toJob(
                jobDomain.getAndUpdateJobStatusByOrgIdAndStatus(
                    jobDto.getOrgId(), JobStatusEnum.PROCESSING, JobStatusEnum.PROCESSED));

        jobDto.setTotalRecords(jobDto.getTotalRecords() + uploadRequestList.size());
        jobDto.setRemainingRecords(jobDto.getRemainingRecords() + uploadRequestList.size());
        jobResponse = jobsConsumerClient.updateJob(jobDto).getPayload();
      }
      var finalJobResponse = jobResponse;
      IntStream.range(0, uploadRequestList.size())
          .forEach(
              recordNumber ->
                  publishToKafka(
                      recordNumber,
                      JsonUtil.convert(uploadRequestList.get(recordNumber)),
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

  private void updateRequestObjectsList(
      JobTypeEnum jobType, ArrayList<Object> uploadRequestList, InputStream inputStream)
      throws IOException, CsvException {
    if (jobType == JobTypeEnum.UPLOAD_TRANSIT_TIMES) {
      log.debug("Processing transit times upload data");
      uploadRequestList.addAll(createUploadTransitTimesJobRequest(inputStream));
    } else if (jobType == JobTypeEnum.UPLOAD_PROCESSING_LEAD_TIMES) {
      log.debug("Processing processing lead times upload data");
      uploadRequestList.addAll(createUploadProcessingLeadTimesJobRequest(inputStream));
    } else if (jobType == JobTypeEnum.DELETE_TRANSIT_BUFFER) {
      log.debug("Processing delete transit buffer upload data");
      uploadRequestList.addAll(createUploadTransitTimesJobRequest(inputStream));
    }
  }

  private List<ProcessingLeadTimesRaw> createUploadProcessingLeadTimesJobRequest(
      InputStream inputStream) throws IOException {

    var bufferedReader =
        new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
    var csvFormat = CSVFormat.DEFAULT.withHeader(ProcessingLeadTimesRaw.columnHeadersArray());
    var csvParser = new CSVParser(bufferedReader, csvFormat);

    Iterable<CSVRecord> csvRecords = csvParser.getRecords();
    Iterator<CSVRecord> iterator = csvRecords.iterator();
    iterator.next();
    List<ProcessingLeadTimesRaw> processingLeadTimesRawList = new ArrayList<>();

    /** CSV data parsed and map to NodeCarrierRequest object */
    while (iterator.hasNext()) {
      var csvRecord = iterator.next();
      var processingLeadTime =
          ProcessingLeadTimesRaw.builder()
              .orgId(csvRecord.get(CommonConstants.ORG_ID))
              .nodeId(csvRecord.get(CommonConstants.NODE_ID))
              .processingTime(csvRecord.get(CommonConstants.PROCESSING_TIME))
              .serviceOption(csvRecord.get(CommonConstants.SERVICE_OPTION))
              .actionType(csvRecord.get(CommonConstants.ACTION_TYPE))
              .carrierServiceId("")
              .build();
      processingLeadTimesRawList.add(processingLeadTime);
    }

    /** form job request string */
    return processingLeadTimesRawList;
  }

  private List<TransitDataUpload> createUploadTransitTimesJobRequest(InputStream inputStream)
      throws IOException, CsvException {

    var inputStreamReader = new InputStreamReader(inputStream);
    var csvReader = new CSVReader(inputStreamReader);
    List<String[]> csvFileContents = csvReader.readAll();
    csvReader.close();

    // Extract orgId value
    String[] orgIdRow = csvFileContents.remove(0);
    String orgIdValue = orgIdRow[1];
    // Extract carrierServiceId  value
    String[] carrierServiceIdRow = csvFileContents.remove(0);
    String carrierServiceIdValue = carrierServiceIdRow[1];

    // Extract destination/sourceFsa header and sourceFsa values
    String[] sFsaListWithHeader = csvFileContents.remove(0);

    int size = csvFileContents.get(0).length;
    List<String> sFsaListWithOutHeader = Arrays.asList(sFsaListWithHeader).subList(1, size);

    List<TransitDataUpload> transitDataUploadList = new ArrayList<>();
    csvFileContents.stream()
        .filter(row -> row.length != 0)
        .forEach(
            row -> {
              var integer = new AtomicInteger(0);
              String destinationSfa = row[integer.getAndIncrement()];
              transitDataUploadList.addAll(
                  createTransitDataCreationRequestObjects(
                      orgIdValue,
                      sFsaListWithOutHeader,
                      carrierServiceIdValue,
                      row,
                      destinationSfa,
                      integer));
            });

    return transitDataUploadList;
  }

  private List<TransitDataUpload> createTransitDataCreationRequestObjects(
      String orgId,
      List<String> sFsaList,
      String carrierServiceIdValue,
      String[] row,
      String destinationSfa,
      AtomicInteger integer) {
    return sFsaList.stream()
        .map(
            sFsa -> {
              var transitDataUpload = new TransitDataUpload();
              transitDataUpload.setOrgId(orgId);
              transitDataUpload.setCarrierServiceId(carrierServiceIdValue);
              transitDataUpload.setDestinationGeozone(destinationSfa);
              transitDataUpload.setSourceGeozone(sFsa);
              var transitDaysString = row[integer.getAndIncrement()];
              if (!ObjectUtils.isEmpty(transitDaysString)) {
                transitDataUpload.setTransitDays(transitDaysString);
                return transitDataUpload;
              }
              return null;
            })
        .filter(Objects::nonNull)
        .collect(Collectors.toList());
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
}
