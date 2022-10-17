package com.hbc.csvdownload.common;

import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.LATITUDE;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.LONGITUDE;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.NODE_TYPE;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.POSTAL_CODE_PREFIX;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.STATE;
import static org.junit.jupiter.api.parallel.Resources.TIME_ZONE;

import com.hbc.common.base.PagePayload;
import com.hbc.common.base.PagePayload.Pagination;
import com.hbc.common.response.BaseResponse;
import com.hbc.csvdownload.domain.pojo.DownloadErrorTransitData;
import com.hbc.csvdownload.domain.pojo.ProcessingLeadTimesRaw;
import com.hbc.dataupload.common.outbound.NodeCarrierServiceAndServiceOptionResponse;
import com.hbc.dataupload.common.outbound.ProcessingTimeBufferResponse;
import com.hbc.dataupload.common.pojo.ActiveCombination;
import com.hbc.dataupload.common.pojo.ProcessingTimeBuffer;
import com.hbc.jobs.framework.common.domain.enums.JobStatusEnum;
import com.hbc.jobs.framework.common.domain.enums.JobTypeEnum;
import com.hbc.jobs.framework.common.domain.outbound.JobResponse;
import com.hbc.jobs.framework.common.domain.pojo.AuditLog;
import com.hbc.jobs.framework.common.domain.pojo.JobDto;
import com.hbc.jobs.framework.common.domain.pojo.RecordStatusDto;
import com.hbc.postal.code.timezone.api.domain.dto.PostalCodeTimezoneDto;
import com.hbc.transit.domain.outbound.TransitResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class TestUtil {

  public static final String ORG_ID = "BAY";
  public static final String CARRIER_SERVICE_ID = "ALL_SDND";
  public static final String SOURCE_REGION = "ON";
  public static final String DESTINATION_REGION = "DEL";
  public static final String SOURCE_FSA = "A0A";
  public static final String DESTINATION_FSA = "M1R";
  public static final String NODE_ID = "nodeId";
  public static final String JOB_ID = "jobId1";
  public static final String SERVICE_OPTION = "serviceOptions";
  public static final String COUNTRY = "CA";
  public static final String STREET = "1963 Boul. Lionel-Bertrand";
  public static final String CITY = "Boisbriand";
  public static final String PROVINCE = "QC";
  public static final String POSTAL_CODE = "J7H 1N8";
  public static final Double PROCESSING_TIME = 20.0;
  private static final String NODE_ID_2 = "nodeId2";
  private static final String SERVICE_OPTION_2 = "EXPRESS";
  public static final String processingLeadTimesCsvData =
      "nodeId,orgId,serviceOptions,processingTime (in hrs),action\n"
          + "1554,BAY,SDND,2,U\n"
          + "1560,BAY,SDND,2,U\n"
          + "1101,BAY,SDND,2,U\n"
          + "1518,BAY,NEXTDAY,6,D\n"
          + "1634,BAY,EXPRESS,30.92,U\n"
          + "1601,BAY,EXPRESS,22.55,U\n"
          + "1125,BAY,EXPRESS,19.90,D\n"
          + "1114,BAY,SDND,24.97,U";

  public static final Optional<String> STATUS = Optional.empty();

  public static final JobTypeEnum jobType = JobTypeEnum.getTypeFromString("any");
  public static final String processingLeadTimesRequestBodyJson =
      "{\"nodeId\":\"1554\",\"orgId\":\"BAY\",\"carrierServiceId\":\"ALL-SDND\",\"serviceOption\":\"SDND\",\"processingTime\":2.0,\"lastPickupTime\":\"00:00\"}";

  public static final String transitTimesRequestBodyJson =
      "{\"orgId\":\"BAY\",\"sourceGeozone\":\"A0A\",\"destinationGeozone\":\"M1R\",\"carrierServiceId\":\"ALL-SDND\",\"transitDays\":\"1.5\"}";

  public JobDto getJobDto() {
    JobDto jobDto = new JobDto();
    jobDto.setJobId(JOB_ID);
    jobDto.setTotalRecords(2);
    jobDto.setJobType(JobTypeEnum.UPLOAD_PROCESSING_LEAD_TIMES);
    jobDto.setProcessedRecords(0);
    jobDto.setRemainingRecords(2);
    jobDto.setFailureCount(0);
    jobDto.setSuccessCount(0);
    jobDto.setStatus(JobStatusEnum.SUBMITTED);
    jobDto.setOrgId(ORG_ID);

    AuditLog auditLog = new AuditLog();
    auditLog.setStatus(JobStatusEnum.SUBMITTED);
    auditLog.setTimeStamp(new Date());
    jobDto.setAuditLog(Collections.singletonList(auditLog));
    return jobDto;
  }

  public JobDto getJobDto2() {
    JobDto jobDto = new JobDto();
    jobDto.setJobId(JOB_ID);
    jobDto.setTotalRecords(2);
    jobDto.setJobType(jobType);
    jobDto.setProcessedRecords(0);
    jobDto.setRemainingRecords(2);
    jobDto.setFailureCount(0);
    jobDto.setSuccessCount(0);
    jobDto.setStatus(JobStatusEnum.SUBMITTED);
    jobDto.setOrgId(ORG_ID);

    AuditLog auditLog = new AuditLog();
    auditLog.setStatus(JobStatusEnum.SUBMITTED);
    auditLog.setTimeStamp(new Date());
    jobDto.setAuditLog(Collections.singletonList(auditLog));
    return jobDto;
  }

  public List<RecordStatusDto> getJobRecordsForProcessingLeadTimes() {
    RecordStatusDto recordDto =
        RecordStatusDto.builder()
            .jobId(JOB_ID)
            .jobType(JobTypeEnum.UPLOAD_PROCESSING_LEAD_TIMES)
            .orgId(ORG_ID)
            .errorMessage("Invalid nodeId")
            .requestBody(processingLeadTimesRequestBodyJson)
            .build();

    return List.of(recordDto);
  }

  public RecordStatusDto getJobRecordsForTransitTimes() {

    return RecordStatusDto.builder()
        .jobId(JOB_ID)
        .jobType(JobTypeEnum.UPLOAD_TRANSIT_TIMES)
        .orgId(ORG_ID)
        .errorMessage("Invalid nodeId")
        .requestBody(transitTimesRequestBodyJson)
        .build();
  }

  public ProcessingLeadTimesRaw getProcessingLeadTimesRaw() {
    ProcessingLeadTimesRaw processingLeadTimesRaw = new ProcessingLeadTimesRaw();
    processingLeadTimesRaw.setNodeId(NODE_ID);
    processingLeadTimesRaw.setOrgId(ORG_ID);
    processingLeadTimesRaw.setServiceOption(SERVICE_OPTION);
    processingLeadTimesRaw.setProcessingTime(String.valueOf(PROCESSING_TIME));

    return processingLeadTimesRaw;
  }

  public JobResponse createJobResponse(JobTypeEnum jobTypeEnum, int totalRecords) {
    JobResponse job = new JobResponse();
    job.setJobId(JOB_ID);
    job.setTotalRecords(totalRecords);
    job.setJobType(jobTypeEnum);
    job.setProcessedRecords(0);
    job.setRemainingRecords(totalRecords);
    job.setFailureCount(0);
    job.setSuccessCount(0);
    job.setStatus(JobStatusEnum.SUBMITTED);
    job.setOrgId(ORG_ID);

    AuditLog auditLog = new AuditLog();
    auditLog.setStatus(JobStatusEnum.SUBMITTED);
    auditLog.setTimeStamp(new Date());
    job.setAuditLog(Collections.singletonList(auditLog));
    return job;
  }

  public TransitResponse getTransitResponse(Float transitDays) {
    Date bufferStartDate = new Date(1000);
    Date bufferEndDate = new Date(1000);
    return TransitResponse.builder()
        .orgId(ORG_ID)
        .sourceGeozone(SOURCE_FSA)
        .destinationGeozone(DESTINATION_FSA)
        .carrierServiceId(CARRIER_SERVICE_ID)
        .transitDays(transitDays)
        .bufferDays(3.0)
        .bufferStartDate(bufferStartDate)
        .bufferEndDate(bufferEndDate)
        .build();
  }

  public DownloadErrorTransitData getAddTransitDataRequest() {
    DownloadErrorTransitData downloadErrorTransitData = new DownloadErrorTransitData();
    downloadErrorTransitData.setOrgId(ORG_ID);
    downloadErrorTransitData.setSourceGeozone("SSFA");
    downloadErrorTransitData.setDestinationGeozone("DSFA");
    downloadErrorTransitData.setCarrierServiceId("ALL-SDND");
    downloadErrorTransitData.setTransitDays("2");
    return downloadErrorTransitData;
  }

  public PostalCodeTimezoneDto getPostalCodeTimezoneDto() {
    return PostalCodeTimezoneDto.builder()
        .orgId(ORG_ID)
        .postalCodePrefix(POSTAL_CODE_PREFIX)
        .country(COUNTRY)
        .state(STATE)
        .city(CITY)
        .latitude(LATITUDE)
        .longitude(LONGITUDE)
        .timeZone(TIME_ZONE)
        .build();
  }

  public ActiveCombination getActiveCombination() {
    return ActiveCombination.builder()
        .nodeId(NODE_ID)
        .carrierServiceId(CARRIER_SERVICE_ID)
        .serviceOption(SERVICE_OPTION)
        .isActive(true)
        .build();
  }

  public NodeCarrierServiceAndServiceOptionResponse
      getNodeCarrierServiceAndServiceOptionResponse() {
    NodeCarrierServiceAndServiceOptionResponse response =
        new NodeCarrierServiceAndServiceOptionResponse();
    response.setNodeId(NODE_ID);
    response.setOrgId(ORG_ID);
    response.setStreet(STREET);
    response.setCity(CITY);
    response.setProvince(PROVINCE);
    response.setPostalCode(POSTAL_CODE);
    response.setCarrierServices(List.of(CARRIER_SERVICE_ID));
    response.setServiceOptions(List.of(SERVICE_OPTION));
    response.setActiveCombination(List.of(getActiveCombination()));

    return response;
  }

  public PagePayload<NodeCarrierServiceAndServiceOptionResponse>
      getNodeCarrierServiceAndServiceOptionResponse(Integer pageNo) {
    PagePayload<NodeCarrierServiceAndServiceOptionResponse> nodeCarrierServicePagePayload =
        new PagePayload<>();

    NodeCarrierServiceAndServiceOptionResponse response =
        getNodeCarrierServiceAndServiceOptionResponse();

    Pagination pagination = new Pagination();
    pagination.setTotalPages(1);
    pagination.setCurrentPage(pageNo);
    pagination.setSortBy("nodeId");
    pagination.setSortOrder("ASC");
    pagination.setTotalRecords(1);
    nodeCarrierServicePagePayload.setPagination(pagination);
    nodeCarrierServicePagePayload.setData(List.of(response));

    return nodeCarrierServicePagePayload;
  }

  public BaseResponse<PagePayload<ProcessingTimeBufferResponse>>
      getBaseResponseOfProcessingTimeBuffers() {
    return BaseResponse.builder()
        .message("Processing time buffers fetched successfully")
        .payload(getProcessingTimeBufferPagePayload(1))
        .build();
  }

  public PagePayload<ProcessingTimeBufferResponse> getProcessingTimeBufferPagePayload(int pageNo) {
    PagePayload<ProcessingTimeBufferResponse> processingTimeBufferDtoPagePayload =
        new PagePayload<>();

    ProcessingTimeBufferResponse processingTimeBufferResponse1 =
        getProcessingTimeBufferResponse(NODE_ID);
    ProcessingTimeBufferResponse processingTimeBufferResponse2 =
        getProcessingTimeBufferResponse(NODE_ID_2);

    Pagination pagination = new Pagination();
    pagination.setTotalPages(2);
    pagination.setCurrentPage(pageNo);
    pagination.setSortBy("DESC");
    pagination.setTotalRecords(2);
    processingTimeBufferDtoPagePayload.setPagination(pagination);
    processingTimeBufferDtoPagePayload.setData(
        Arrays.asList(processingTimeBufferResponse1, processingTimeBufferResponse2));

    return processingTimeBufferDtoPagePayload;
  }

  public ProcessingTimeBufferResponse getProcessingTimeBufferResponse(String nodeId) {
    return ProcessingTimeBufferResponse.builder()
        .nodeId(nodeId)
        .orgId(ORG_ID)
        .nodeType(NODE_TYPE)
        .serviceOptions(List.of(SERVICE_OPTION, SERVICE_OPTION_2))
        .processingTimeBuffers(
            List.of(
                getProcessingTimeBuffer(SERVICE_OPTION), getProcessingTimeBuffer(SERVICE_OPTION_2)))
        .build();
  }

  private ProcessingTimeBuffer getProcessingTimeBuffer(String serviceOption) {
    ProcessingTimeBuffer processingTimeBuffer = new ProcessingTimeBuffer();
    processingTimeBuffer.setServiceOption(serviceOption);
    processingTimeBuffer.setBufferHours(2.5);
    processingTimeBuffer.setBufferStartDate(new Date(1000));
    processingTimeBuffer.setBufferEndDate(new Date(1000));
    processingTimeBuffer.setStatus("Inactive");
    return processingTimeBuffer;
  }

  public ProcessingTimeBufferResponse getProcessingTimeBufferResponseEmptyValues(String nodeId) {
    return ProcessingTimeBufferResponse.builder()
        .nodeId(nodeId)
        .orgId(ORG_ID)
        .nodeType(NODE_TYPE)
        .serviceOptions(new ArrayList<>())
        .processingTimeBuffers(new ArrayList<>())
        .build();
  }
}
