package com.hbc.jobs.consumers.common;

import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.CALENDAR_ID;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.CARRIER_SERVICE_ID;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.DESCRIPTION;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.LATITUDE;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.LONGITUDE;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.NODE_ID;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.POSTAL_CODE_PREFIX;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.SERVICE_OPTION;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.STATE;
import static org.junit.jupiter.api.parallel.Resources.TIME_ZONE;

import com.hbc.common.util.DateUtil;
import com.hbc.csvdownload.common.pojo.TransitDataUpload;
import com.hbc.csvdownload.domain.pojo.ProcessingLeadTimesRaw;
import com.hbc.jobs.consumers.domain.entity.JobEntity;
import com.hbc.jobs.consumers.domain.entity.JobRecordEntity;
import com.hbc.jobs.consumers.domain.mapper.JobMapper;
import com.hbc.jobs.consumers.domain.mapper.JobRecordMapper;
import com.hbc.jobs.consumers.feign.AuthTokenResponse;
import com.hbc.jobs.framework.common.domain.enums.ApiStatusEnum;
import com.hbc.jobs.framework.common.domain.enums.JobStatusEnum;
import com.hbc.jobs.framework.common.domain.enums.JobTypeEnum;
import com.hbc.jobs.framework.common.domain.outbound.JobResponse;
import com.hbc.jobs.framework.common.domain.pojo.AuditLog;
import com.hbc.jobs.framework.common.domain.pojo.JobDetailsDto;
import com.hbc.jobs.framework.common.domain.pojo.JobDto;
import com.hbc.jobs.framework.common.domain.pojo.JobFilters;
import com.hbc.jobs.framework.common.domain.pojo.NodeCalendarUpload;
import com.hbc.jobs.framework.common.domain.pojo.NodeCarrierUpload;
import com.hbc.jobs.framework.common.domain.pojo.NodeDataUpload;
import com.hbc.jobs.framework.common.domain.pojo.PostalCodeTimezoneUpload;
import com.hbc.jobs.framework.common.domain.pojo.RecordDto;
import com.hbc.jobs.framework.common.domain.pojo.RecordStatusDto;
import com.hbc.jobs.framework.common.domain.pojo.TransitBufferUpload;
import com.hbc.node.carrier.domain.inbound.NodeCarrierRequest;
import com.hbc.node.carrier.domain.outbound.NodeCarrierResponse;
import com.hbc.node.domain.outbound.NodeResponse;
import com.hbc.postal.code.timezone.api.domain.dto.PostalCodeTimezoneDto;
import com.hbc.transit.domain.outbound.TransitBufferResponse;
import com.hbc.transit.domain.outbound.TransitResponse;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class TestUtil {

  public static final String ORG_ID = "BAY";
  public static final String JOB_ID = "JobId1";
  public static final String STREET = "street-1";
  public static final String CITY = "city-1";
  public static final String PROVINCE = "province-1";
  public static final String POSTAL_CODE = "33666";
  public static final String COUNTRY = "country-1";
  public static final String LATITUDE = "43.769912";
  public static final String LONGITUDE = "-79.296678";
  public static final String TIME_ZONE = "America/Toronto";
  public static Boolean SHIP_TO_TIME = Boolean.TRUE;
  public static Boolean BOPIS_ELIGIBLE = Boolean.TRUE;
  public static Boolean SDND_ELIGIBLE = Boolean.TRUE;
  public static Boolean EXPRESS_ELIGIBLE = Boolean.TRUE;
  public static String NODE_TYPE = "MFC";
  public static Boolean IS_ACTIVE = Boolean.TRUE;

  public static final String CSV_CONTENTS_PROCESSING_LEAD_TIMES =
      "nodeId,orgId,serviceOptions,processingTime (in hrs),action\n"
          + "1554,BAY,SDND,2,U\n"
          + "1560,BAY,SDND,2,D\n"
          + "1101,BAY,SDND,2,U";

  public static final String CSV_CONTENTS_TRANSIT_TIMES =
      "orgId,BAY,,,,,,,,,\n"
          + "Carrier Service:,ALL-Standard,,,,,,,,,\n"
          + "Destination FSA / Source FSA ->,SFSA1,SFSA2,SFSA3\n"
          + "DFSA1,,9.96,9.96\n"
          + "DFSA2,D,9,9.9\n"
          + "DFSA3,10,9,9\n";

  public static final String JOB_STRING =
      "[{\"nodeId\":\"1554\",\"orgId\":\"BAY\",\"serviceOption\":\"SDND\",\"carrierServiceId\":\"\",\"processingTime\":2.0,\"actionType\":\"U\"},{\"nodeId\":\"1560\",\"orgId\":\"BAY\",\"serviceOption\":\"SDND\",\"carrierServiceId\":\"\",\"processingTime\":2.0,\"actionType\":\"D\"},{\"nodeId\":\"1101\",\"orgId\":\"BAY\",\"serviceOption\":\"SDND\",\"carrierServiceId\":\"\",\"processingTime\":2.0,\"actionType\":\"U\"}]";

  public static final Optional<String> DEFAULT_SORT_FIELD = Optional.of("created_date");

  public static final Optional<String> DEFAULT_SORT_ORDER = Optional.of("ASC");

  public static final Optional<Integer> DEFAULT_PAGE_NO = Optional.of(1);

  public static final Optional<Integer> DEFAULT_PAGE_SIZE = Optional.of(15);

  public JobRecordEntity getJobRecordEntity() {
    return JobRecordMapper.INSTANCE.toJobRecordEntity(
        createRecordStatus(
            JOB_ID,
            ORG_ID,
            ApiStatusEnum.SUCCESS,
            HttpStatus.OK,
            "",
            JobTypeEnum.UPLOAD_PROCESSING_LEAD_TIMES,
            3));
  }

  public RecordStatusDto createRecordStatus(
      String jobId,
      String orgId,
      ApiStatusEnum status,
      HttpStatus httpStatus,
      String userId,
      JobTypeEnum jobType,
      int recordNo) {
    RecordStatusDto recordStatusDto = new RecordStatusDto();
    recordStatusDto.setJobId(jobId);
    recordStatusDto.setOrgId(orgId);
    recordStatusDto.setStatus(status);
    recordStatusDto.setStatusCode(httpStatus.value());
    recordStatusDto.setUserId(userId);
    recordStatusDto.setRequestBody("");
    recordStatusDto.setResponseBody("");
    recordStatusDto.setResponseTime(30L);
    recordStatusDto.setJobType(jobType);
    recordStatusDto.setRecordNo(recordNo);
    return recordStatusDto;
  }

  public List<RecordStatusDto> createRecordStatusDtoList(String tenantId) {
    RecordStatusDto record1 =
        createRecordStatus(
            "JobId1",
            tenantId,
            ApiStatusEnum.SUCCESS,
            HttpStatus.OK,
            "user1",
            JobTypeEnum.UPLOAD_PROCESSING_LEAD_TIMES,
            0);
    RecordStatusDto record2 =
        createRecordStatus(
            "JobId1",
            tenantId,
            ApiStatusEnum.SUCCESS,
            HttpStatus.OK,
            "user1",
            JobTypeEnum.UPLOAD_PROCESSING_LEAD_TIMES,
            1);
    return Arrays.asList(record1, record2);
  }

  public List<JobResponse> createJobResponseList() {
    JobResponse job1 = createJobResponse(JobTypeEnum.UPLOAD_PROCESSING_LEAD_TIMES, 2);
    JobResponse job2 = createJobResponse(JobTypeEnum.UPLOAD_TRANSIT_TIMES, 5);
    return Arrays.asList(job1, job2);
  }

  public List<JobDto> createJobDtoList() {
    JobDto job1 = createJob(JobTypeEnum.UPLOAD_PROCESSING_LEAD_TIMES, 2);
    JobDto job2 = createJob(JobTypeEnum.UPLOAD_TRANSIT_TIMES, 5);
    return Arrays.asList(job1, job2);
  }

  public Page<JobResponse> createPageJobDto(
      int totalPage, List<JobResponse> jobDtos, int totalElements) {

    return (Page<JobResponse>)
        new Page() {
          @Override
          public int getTotalPages() {
            return totalPage;
          }

          @Override
          public long getTotalElements() {
            return totalElements;
          }

          @Override
          public Page map(Function converter) {
            return null;
          }

          @Override
          public int getNumber() {
            return 0;
          }

          @Override
          public int getSize() {
            return 0;
          }

          @Override
          public int getNumberOfElements() {
            return 0;
          }

          @Override
          public List getContent() {
            return jobDtos;
          }

          @Override
          public boolean hasContent() {
            return false;
          }

          @Override
          public Sort getSort() {
            return null;
          }

          @Override
          public boolean isFirst() {
            return false;
          }

          @Override
          public boolean isLast() {
            return false;
          }

          @Override
          public boolean hasNext() {
            return false;
          }

          @Override
          public boolean hasPrevious() {
            return false;
          }

          @Override
          public Pageable nextPageable() {
            return null;
          }

          @Override
          public Pageable previousPageable() {
            return null;
          }

          @Override
          public Iterator iterator() {
            return null;
          }
        };
  }

  public Page<RecordStatusDto> createPageRecordStatusDto(
      int totalPage, List<RecordStatusDto> recordStatusDtos, int totalElements) {
    Page<RecordStatusDto> pageResponse =
        new Page() {
          @Override
          public int getTotalPages() {
            return totalPage;
          }

          @Override
          public long getTotalElements() {
            return totalElements;
          }

          @Override
          public Page map(Function converter) {
            return null;
          }

          @Override
          public int getNumber() {
            return 0;
          }

          @Override
          public int getSize() {
            return 0;
          }

          @Override
          public int getNumberOfElements() {
            return 0;
          }

          @Override
          public List getContent() {
            return recordStatusDtos;
          }

          @Override
          public boolean hasContent() {
            return false;
          }

          @Override
          public Sort getSort() {
            return null;
          }

          @Override
          public boolean isFirst() {
            return false;
          }

          @Override
          public boolean isLast() {
            return false;
          }

          @Override
          public boolean hasNext() {
            return false;
          }

          @Override
          public boolean hasPrevious() {
            return false;
          }

          @Override
          public Pageable nextPageable() {
            return null;
          }

          @Override
          public Pageable previousPageable() {
            return null;
          }

          @Override
          public Iterator iterator() {
            return null;
          }
        };

    return pageResponse;
  }

  public JobDto createJob(JobTypeEnum jobTypeEnum, int totalRecords) {
    JobDto job = new JobDto();
    job.setJobId(UUID.randomUUID().toString());
    job.setTotalRecords(totalRecords);
    job.setJobType(jobTypeEnum);
    job.setProcessedRecords(0);
    job.setFailureCount(0);
    job.setSuccessCount(0);
    job.setStatus(JobStatusEnum.SUBMITTED);
    job.setOrgId(ORG_ID);
    job.setFile(new byte[] {1, 2, 3});
    AuditLog auditLog = new AuditLog();
    auditLog.setStatus(JobStatusEnum.SUBMITTED);
    auditLog.setTimeStamp(new Date());
    job.setAuditLog(Collections.singletonList(auditLog));
    return job;
  }

  public JobResponse createJobResponse(JobTypeEnum jobTypeEnum, int totalRecords) {
    JobResponse job = new JobResponse();
    job.setJobId(UUID.randomUUID().toString());
    job.setTotalRecords(totalRecords);
    job.setJobType(jobTypeEnum);
    job.setProcessedRecords(0);
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

  public JobEntity createJobEntity(JobTypeEnum jobTypeEnum, int totalRecords) {
    return JobMapper.INSTANCE.toJobEntity(createJob(jobTypeEnum, totalRecords));
  }

  public NodeCarrierRequest getNodeCarrierRequest() {
    NodeCarrierRequest nodeCarrierRequest = new NodeCarrierRequest();
    nodeCarrierRequest.setNodeId("node-1");
    nodeCarrierRequest.setOrgId(ORG_ID);
    nodeCarrierRequest.setServiceOption("SDND");
    nodeCarrierRequest.setCarrierServiceId("");
    nodeCarrierRequest.setProcessingTime(30.92);
    return nodeCarrierRequest;
  }

  public ProcessingLeadTimesRaw getProcessingLeadTime(String action) {
    ProcessingLeadTimesRaw processingLeadTime = new ProcessingLeadTimesRaw();
    processingLeadTime.setNodeId("node-1");
    processingLeadTime.setOrgId(ORG_ID);
    processingLeadTime.setServiceOption("SDND");
    processingLeadTime.setCarrierServiceId("");
    processingLeadTime.setProcessingTime("30.92");
    processingLeadTime.setActionType(action);
    return processingLeadTime;
  }

  public NodeCarrierUpload getNodeCarrierUpload(String action) {
    return NodeCarrierUpload.builder()
        .action(action)
        .nodeId(NODE_ID)
        .orgId(ORG_ID)
        .carrierServiceId(CARRIER_SERVICE_ID)
        .serviceOption(SERVICE_OPTION)
        .lastPickupTime("12:00")
        .build();
  }

  public NodeCarrierResponse getNodeCarrierResponse() {
    NodeCarrierResponse nodeCarrierResponse = new NodeCarrierResponse();
    nodeCarrierResponse.setNodeId("node-1");
    nodeCarrierResponse.setOrgId(ORG_ID);
    nodeCarrierResponse.setServiceOption("SDND");
    nodeCarrierResponse.setCarrierServiceId("ALL-SDND");
    nodeCarrierResponse.setLastPickupTime("0");
    nodeCarrierResponse.setProcessingTime(30.92);
    return nodeCarrierResponse;
  }

  public TransitDataUpload getTransitDataUpload() {
    TransitDataUpload transitDataUpload = new TransitDataUpload();
    transitDataUpload.setOrgId(ORG_ID);
    transitDataUpload.setSourceGeozone("SSFA");
    transitDataUpload.setDestinationGeozone("DSFA");
    transitDataUpload.setCarrierServiceId("ALL-SDND");
    transitDataUpload.setTransitDays("2F");
    return transitDataUpload;
  }

  public TransitResponse getTransitResponse() {
    TransitResponse transitResponse = new TransitResponse();
    transitResponse.setOrgId(ORG_ID);
    transitResponse.setSourceGeozone("SSFA");
    transitResponse.setDestinationGeozone("DSFA");
    transitResponse.setCarrierServiceId("ALL-SDND");
    transitResponse.setTransitDays(2F);
    return transitResponse;
  }

  public JobFilters getJobFilters() {
    JobFilters jobFilters = new JobFilters();
    jobFilters.setDays(Optional.empty());
    jobFilters.setJobType(Optional.empty());
    jobFilters.setSortBy(DEFAULT_SORT_FIELD);
    jobFilters.setSortOrder(DEFAULT_SORT_ORDER);
    jobFilters.setPageNo(DEFAULT_PAGE_NO);
    jobFilters.setPageSize(DEFAULT_PAGE_SIZE);

    return jobFilters;
  }

  public AuthTokenResponse getAuthTokenResponse() {
    AuthTokenResponse authTokenResponse = new AuthTokenResponse();
    authTokenResponse.setAccessToken("token");
    authTokenResponse.setTokenType("tokenType");
    authTokenResponse.setExpiresIn(20);

    return authTokenResponse;
  }

  public JobDetailsDto getJobDetailsDto() {
    JobDetailsDto jobDetailsDto = new JobDetailsDto();
    jobDetailsDto.setJobId(JOB_ID);
    jobDetailsDto.setJobType(JobTypeEnum.TRANSIT_BUFFER_REQUEST);
    jobDetailsDto.setStatus(JobStatusEnum.COMPLETED);
    jobDetailsDto.setOrgId(ORG_ID);
    jobDetailsDto.setTotalRecords(2);

    return jobDetailsDto;
  }

  public TransitBufferUpload getTransitBufferUpload(String bufferDays, String action) {
    TransitBufferUpload transitBufferUpload = new TransitBufferUpload();
    transitBufferUpload.setOrgId(ORG_ID);
    transitBufferUpload.setCarrierServiceId("ALL-EXPRESS");
    transitBufferUpload.setBufferDays(bufferDays);
    transitBufferUpload.setSourceGeozone("T0A");
    transitBufferUpload.setDestinationGeozone("M1R");
    transitBufferUpload.setBufferStartDate("2022-10-19T15:51:17Z");
    transitBufferUpload.setBufferEndDate("2022-10-19T15:51:17Z");
    transitBufferUpload.setAction(action);

    return transitBufferUpload;
  }

  public TransitBufferResponse getTransitBufferResponse() {
    return TransitBufferResponse.builder()
        .orgId(ORG_ID)
        .carrierServiceId("ALL-EXPRESS")
        .sourceGeozone("T0A")
        .destinationGeozone("M1R")
        .bufferDays(2D)
        .bufferEndDate(
            DateUtil.getDate(DateUtil.convertDateLongFormUTC(DateUtil.addDaysToCurrentDate(1))))
        .bufferStartDate(
            DateUtil.getDate(DateUtil.convertDateLongFormUTC(DateUtil.getCurrentDate())))
        .build();
  }

  public PostalCodeTimezoneDto getPostalCodeTimezoneDto() {
    return PostalCodeTimezoneDto.builder()
        .orgId(ORG_ID)
        .postalCodePrefix(POSTAL_CODE_PREFIX)
        .country("COUNTRY")
        .state(STATE)
        .city("CITY")
        .latitude(LATITUDE)
        .longitude(LONGITUDE)
        .timeZone(TIME_ZONE)
        .build();
  }

  public PostalCodeTimezoneUpload getPostalCodeTimezoneUpload(String action) {
    return PostalCodeTimezoneUpload.builder()
        .action(action)
        .orgId(ORG_ID)
        .postalCodePrefix(POSTAL_CODE_PREFIX)
        .country("COUNTRY")
        .state(STATE)
        .city("CITY")
        .latitude(LATITUDE)
        .longitude(LONGITUDE)
        .timeZone(TIME_ZONE)
        .build();
  }

  public RecordDto getRecordDto(JobTypeEnum jobTypeEnum) {
    RecordDto record = new RecordDto();
    record.setOrgId(ORG_ID);
    record.setJobId(TestUtil.JOB_ID);
    record.setTotalRecords(2);
    record.setJobType(jobTypeEnum);

    return record;
  }

  public NodeResponse getNodeResponse() {
    return NodeResponse.builder()
        .nodeId(NODE_ID)
        .orgId(ORG_ID)
        .street(STREET)
        .bopisEligible(BOPIS_ELIGIBLE)
        .city(CITY)
        .country(COUNTRY)
        .nodeType(NODE_TYPE)
        .isActive(IS_ACTIVE)
        .latitude(LATITUDE)
        .longitude(LONGITUDE)
        .postalCode(POSTAL_CODE)
        .province(PROVINCE)
        .shipToHome(SHIP_TO_TIME)
        .timezone(TIME_ZONE)
        .build();
  }

  public NodeDataUpload getNodeDataUpload(String action) {
    return NodeDataUpload.builder()
        .action(action)
        .nodeId(NODE_ID)
        .orgId(ORG_ID)
        .street(STREET)
        .bopisEligible(String.valueOf(BOPIS_ELIGIBLE))
        .city(CITY)
        .country(COUNTRY)
        .nodeType(NODE_TYPE)
        .isActive(String.valueOf(IS_ACTIVE))
        .latitude(LATITUDE)
        .longitude(LONGITUDE)
        .postalCode(POSTAL_CODE)
        .province(PROVINCE)
        .shipToHome(String.valueOf(SHIP_TO_TIME))
        .timezone(TIME_ZONE)
        .build();
  }

  public NodeCalendarUpload getNodeCalendarUpload(String action) {
    return NodeCalendarUpload.builder()
        .calendarId(CALENDAR_ID)
        .description(DESCRIPTION)
        .effectiveDate("2022-10-26")
        .nodeId(NODE_ID)
        .orgId(ORG_ID)
        .action(action)
        .build();
  }
}
