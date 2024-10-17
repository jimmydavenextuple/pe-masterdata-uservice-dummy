/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.jobs.consumers.common;

import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.CALENDAR_ID;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.CARRIER_ID;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.CARRIER_NAME;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.CARRIER_SERVICE_ID;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.CREATE;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.CUSTOM_REGION_NAME;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.DESCRIPTION;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.DESTINATION_GEO_ZONE;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.EFFECTIVE_DATE;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.NODE_ID;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.SERVICE_NAME;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.SERVICE_OPTION;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.SERVICE_OPTIONS;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.SHIPPING_STAGE;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.SOURCE_GEO_ZONE;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.ZIP_CODE_PREFIX;

import com.nextuple.calendar.domain.outbound.CalendarResponse;
import com.nextuple.calendar.domain.outbound.CarrierServiceCalendarResponse;
import com.nextuple.calendar.domain.outbound.NodeCarrierServiceCalendarResponse;
import com.nextuple.calendar.domain.pojo.ExceptionDays;
import com.nextuple.carrier.domain.outbound.CarrierServiceResponse;
import com.nextuple.common.util.DateUtil;
import com.nextuple.csvdownload.common.pojo.TransitDataUpload;
import com.nextuple.csvdownload.domain.pojo.ProcessingLeadTimesRaw;
import com.nextuple.dataupload.common.constants.DataUploadUtilityConstants;
import com.nextuple.jobs.consumers.domain.entity.JobEntity;
import com.nextuple.jobs.consumers.domain.entity.JobRecordEntity;
import com.nextuple.jobs.consumers.domain.mapper.JobMapper;
import com.nextuple.jobs.consumers.domain.mapper.JobRecordMapper;
import com.nextuple.jobs.consumers.feign.AuthTokenResponse;
import com.nextuple.jobs.framework.common.domain.enums.ApiStatusEnum;
import com.nextuple.jobs.framework.common.domain.enums.JobStatusEnum;
import com.nextuple.jobs.framework.common.domain.enums.JobTypeEnum;
import com.nextuple.jobs.framework.common.domain.outbound.JobResponse;
import com.nextuple.jobs.framework.common.domain.pojo.AuditLog;
import com.nextuple.jobs.framework.common.domain.pojo.CalendarDataUpload;
import com.nextuple.jobs.framework.common.domain.pojo.CarrierCalendarUpload;
import com.nextuple.jobs.framework.common.domain.pojo.CarrierServiceUpload;
import com.nextuple.jobs.framework.common.domain.pojo.CostValueDataUpload;
import com.nextuple.jobs.framework.common.domain.pojo.CustomRegionUpload;
import com.nextuple.jobs.framework.common.domain.pojo.JobDetailsDto;
import com.nextuple.jobs.framework.common.domain.pojo.JobDto;
import com.nextuple.jobs.framework.common.domain.pojo.JobFilters;
import com.nextuple.jobs.framework.common.domain.pojo.NodeCalendarUpload;
import com.nextuple.jobs.framework.common.domain.pojo.NodeCarrierUpload;
import com.nextuple.jobs.framework.common.domain.pojo.NodeDataUpload;
import com.nextuple.jobs.framework.common.domain.pojo.PickUpCalendarUpload;
import com.nextuple.jobs.framework.common.domain.pojo.PostalCodeTimezoneUpload;
import com.nextuple.jobs.framework.common.domain.pojo.ProcessingTimeBufferUpload;
import com.nextuple.jobs.framework.common.domain.pojo.RecordDto;
import com.nextuple.jobs.framework.common.domain.pojo.RecordStatusDto;
import com.nextuple.jobs.framework.common.domain.pojo.TransitBufferUpload;
import com.nextuple.jobs.framework.common.domain.pojo.ZoneDataUpload;
import com.nextuple.node.carrier.domain.inbound.NodeCarrierRequest;
import com.nextuple.node.carrier.domain.outbound.NodeCarrierResponse;
import com.nextuple.node.carrier.domain.outbound.NodeServiceOptionBufferResponse;
import com.nextuple.node.domain.outbound.NodeResponse;
import com.nextuple.postal.code.timezone.api.domain.dto.PostalCodeTimezoneDto;
import com.nextuple.postal.code.timezone.api.domain.outbound.CustomRegionResponse;
import com.nextuple.postal.code.timezone.api.domain.outbound.PostalCodeResponse;
import com.nextuple.sourcing.cost.config.outbound.CostValueResponse;
import com.nextuple.transit.domain.outbound.TransitBufferResponse;
import com.nextuple.transit.domain.outbound.TransitResponse;
import com.nextuple.transit.domain.outbound.ZoneResponse;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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
  public static final String TRUE = "true";
  public static final String ORG_ID = "BAY";
  public static final String JOB_ID = "JobId1";
  public static final String STREET = "street-1";
  public static final String CITY = "city-1";
  public static final String STATE = "state-1";
  public static final String ZIP_CODE = "33666";
  public static final String COUNTRY = "country-1";
  public static final String LATITUDE = "43.769912";
  public static final String LONGITUDE = "-79.296678";
  public static final String TIME_ZONE = "America/Toronto";
  public static final String EXCEPTION_DATE = String.valueOf(DateUtil.addDaysToCurrentDate(4));
  public static final String EXCEPTION_REASON = "Public Holiday";
  public static final String REGION_ID = "ID1011";
  public static final String REGION_NAME = "Bay Area";
  public static final List<String> CODES = Arrays.asList("T2P", "T3P");
  public static final String ZONE = "Zone1";
  public static final String COST_FACTOR_COMBINATION_KEY = "UPS_GROUND|NON_HOLIDAYS|Z1|XXL";
  public static final String PREV_SLAB_VALUE = "UPS_GROUND|NON_HOLIDAYS|Z1|XL";
  public static final String COST_ITINERARY = "SHIPPING_COST";

  public static final List<String> tenantServiceOptionExpected =
      List.of("sdndEligible", "expressEligible");

  public static final String EXCEPTION_DAYS = "[]";
  public static final String CSV_CONTENTS_PROCESSING_LEAD_TIMES =
      "action,nodeId,orgId,serviceOptions,processingTime (in hrs)\n"
          + "UPDATE,1554,BAY,SDND,2\n"
          + "UPDATE,1101,BAY,SDND,2,\n"
          + "DELETE,1560,BAY,SDND,2";
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
  public static Boolean SHIP_TO_TIME = Boolean.TRUE;
  public static Boolean BOPIS_ELIGIBLE = Boolean.TRUE;
  public static Boolean SDND_ELIGIBLE = Boolean.TRUE;
  public static Boolean EXPRESS_ELIGIBLE = Boolean.TRUE;
  public static String NODE_TYPE = "MFC";
  public static Boolean IS_ACTIVE = Boolean.TRUE;

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
    job.setJobType(String.valueOf(jobTypeEnum));
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
        .zipCodePrefix(ZIP_CODE_PREFIX)
        .country("COUNTRY")
        .state(DataUploadUtilityConstants.STATE)
        .city("CITY")
        .latitude(LATITUDE)
        .longitude(LONGITUDE)
        .timeZone(TIME_ZONE)
        .build();
  }

  public PostalCodeResponse getPostalCodeTimezoneResponse() {
    String customRegions = "CR1";
    return PostalCodeResponse.builder()
        .orgId(ORG_ID)
        .zipCode(ZIP_CODE)
        .zipCodePrefix(ZIP_CODE_PREFIX)
        .country("COUNTRY")
        .state(DataUploadUtilityConstants.STATE)
        .city("CITY")
        .latitude(LATITUDE)
        .longitude(LONGITUDE)
        .timeZone(TIME_ZONE)
        .customRegion(customRegions)
        .build();
  }

  public PostalCodeTimezoneUpload getPostalCodeTimezoneUpload(String action) {
    return PostalCodeTimezoneUpload.builder()
        .action(action)
        .orgId(ORG_ID)
        .zipCodePrefix(ZIP_CODE_PREFIX)
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
    record.setJobType(String.valueOf(jobTypeEnum));
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
        .zipCode(ZIP_CODE)
        .state(STATE)
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
        .zipCode(ZIP_CODE)
        .state(STATE)
        .shipToHome(String.valueOf(SHIP_TO_TIME))
        .timezone(TIME_ZONE)
        .serviceOptionEligibilities(
            Map.of(
                "sdndEligible", String.valueOf(SDND_ELIGIBLE),
                "nextdayEligible", String.valueOf(Boolean.TRUE),
                "expressEligible", String.valueOf(EXPRESS_ELIGIBLE)))
        .build();
  }

  public NodeDataUpload getNodeDataUploadWithInvalidBoolean(String action) {
    return NodeDataUpload.builder()
        .action(action)
        .nodeId(NODE_ID)
        .orgId(ORG_ID)
        .street(STREET)
        .bopisEligible("INVALID")
        .city(CITY)
        .country(COUNTRY)
        .nodeType(NODE_TYPE)
        .isActive(String.valueOf(IS_ACTIVE))
        .latitude(LATITUDE)
        .longitude(LONGITUDE)
        .zipCode(ZIP_CODE)
        .state(STATE)
        .shipToHome(String.valueOf(SHIP_TO_TIME))
        .timezone(TIME_ZONE)
        .serviceOptionEligibilities(
            Map.of(
                "sdndEligible", String.valueOf(SDND_ELIGIBLE),
                "nextdayEligible", String.valueOf(Boolean.TRUE),
                "expressEligible", String.valueOf(EXPRESS_ELIGIBLE)))
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

  public CostValueDataUpload getCostValueUploadRequest(String costValue) {
    return CostValueDataUpload.builder()
        .orgId(ORG_ID)
        .costItinerary(COST_ITINERARY)
        .costFactorCombinationKey(COST_FACTOR_COMBINATION_KEY)
        .prevSlabValue(PREV_SLAB_VALUE)
        .costValue(costValue)
        .build();
  }

  public CostValueResponse getCostValueUploadResponse() {
    return CostValueResponse.builder()
        .orgId(ORG_ID)
        .costItinerary(COST_ITINERARY)
        .costFactorCombinationKey(COST_FACTOR_COMBINATION_KEY)
        .prevSlabValue(PREV_SLAB_VALUE)
        .costValue(20.0)
        .build();
  }

  public CarrierCalendarUpload getCarrierCalendarUpload(String action) {
    return CarrierCalendarUpload.builder()
        .calendarId(CALENDAR_ID)
        .carrierServiceId(CARRIER_SERVICE_ID)
        .description(DESCRIPTION)
        .effectiveDate("2022-10-26")
        .shippingStage(SHIPPING_STAGE)
        .orgId(ORG_ID)
        .action(action)
        .build();
  }

  public CarrierServiceCalendarResponse getCarrierServiceCalendarResponse() {
    return CarrierServiceCalendarResponse.builder()
        .calendarId(CALENDAR_ID)
        .orgId(ORG_ID)
        .carrierServiceId(CARRIER_SERVICE_ID)
        .shippingStage(SHIPPING_STAGE)
        .effectiveDate(EFFECTIVE_DATE)
        .description(DESCRIPTION)
        .build();
  }

  public CarrierServiceResponse getCarrierServiceResponse() {
    return CarrierServiceResponse.builder()
        .orgId(ORG_ID)
        .carrierId(CARRIER_ID)
        .carrierServiceId(CARRIER_SERVICE_ID)
        .carrierName(CARRIER_NAME)
        .serviceName(SERVICE_NAME)
        .serviceOptions(SERVICE_OPTIONS)
        .build();
  }

  public CarrierServiceUpload getCarrierServiceUpload(String action) {
    return CarrierServiceUpload.builder()
        .action(action)
        .orgId(ORG_ID)
        .carrierId(CARRIER_ID)
        .carrierServiceId(CARRIER_SERVICE_ID)
        .carrierName(CARRIER_NAME)
        .serviceName(SERVICE_NAME)
        .serviceOptions(SERVICE_OPTIONS)
        .build();
  }

  public NodeCarrierResponse getNodeCarrierBufferResponse() {
    return NodeCarrierResponse.builder()
        .nodeId(NODE_ID)
        .orgId(ORG_ID)
        .serviceOption(SERVICE_OPTION)
        .bufferHours(2D)
        .bufferStartDate(new Date())
        .bufferEndDate(new Date())
        .build();
  }

  public ProcessingTimeBufferUpload getProcessingTimeBufferUpload(String action) {
    return ProcessingTimeBufferUpload.builder()
        .action(action)
        .nodeId(NODE_ID)
        .orgId(ORG_ID)
        .serviceOption(SERVICE_OPTION)
        .bufferHours("2")
        .bufferStartDate(DateUtil.getCurrentTimeStampInString())
        .bufferEndDate(DateUtil.getCurrentTimeStampInString())
        .build();
  }

  public ProcessingTimeBufferUpload getInvalidProcessingTimeBufferUpload() {
    return ProcessingTimeBufferUpload.builder()
        .action(CREATE)
        .nodeId(NODE_ID)
        .orgId(ORG_ID)
        .serviceOption(SERVICE_OPTION)
        .bufferHours("2")
        .bufferStartDate(DateUtil.getCurrentUTCTimeStampInString())
        .bufferEndDate(DateUtil.getCurrentUTCTimeStampInString())
        .build();
  }

  public ProcessingTimeBufferUpload getBlankDateProcessingTimeBufferUpload() {
    return ProcessingTimeBufferUpload.builder()
        .action(CREATE)
        .nodeId(NODE_ID)
        .orgId(ORG_ID)
        .serviceOption(SERVICE_OPTION)
        .bufferHours("2")
        .bufferStartDate("")
        .bufferEndDate("")
        .build();
  }

  public ProcessingTimeBufferUpload getNullDateProcessingTimeBufferUpload() {
    return ProcessingTimeBufferUpload.builder()
        .action(CREATE)
        .nodeId(NODE_ID)
        .orgId(ORG_ID)
        .serviceOption(SERVICE_OPTION)
        .bufferHours("2")
        .bufferStartDate(null)
        .bufferEndDate(null)
        .build();
  }

  public NodeServiceOptionBufferResponse getNodeServiceOptionBufferResponse() {
    return NodeServiceOptionBufferResponse.builder()
        .nodeId(NODE_ID)
        .orgId(ORG_ID)
        .serviceOption(SERVICE_OPTION)
        .bufferHours(2.0)
        .bufferStartDate(new Date())
        .bufferEndDate(new Date())
        .build();
  }

  public ExceptionDays getExceptionDays() {
    ExceptionDays exceptionDays = new ExceptionDays();
    exceptionDays.setDate(EXCEPTION_DATE);
    exceptionDays.setReason(EXCEPTION_REASON);
    return exceptionDays;
  }

  public CalendarDataUpload getCalendarDataUpload(String action, String exceptionDays) {
    return CalendarDataUpload.builder()
        .action(action)
        .calendarId(CALENDAR_ID)
        .orgId(ORG_ID)
        .description(DESCRIPTION)
        .isMondayWorking(TRUE)
        .isTuesdayWorking(TRUE)
        .isWednesdayWorking(TRUE)
        .isThursdayWorking(TRUE)
        .isFridayWorking(TRUE)
        .isSaturdayWorking(TRUE)
        .isSundayWorking(TRUE)
        .exceptionDays(exceptionDays)
        .build();
  }

  public CalendarDataUpload getInvalidCalendarDataUpload(String action, String exceptionDays) {
    return CalendarDataUpload.builder()
        .action(action)
        .calendarId(CALENDAR_ID)
        .orgId(ORG_ID)
        .description(DESCRIPTION)
        .isMondayWorking(null)
        .isTuesdayWorking("")
        .isWednesdayWorking("invalid")
        .isThursdayWorking(TRUE)
        .isFridayWorking(TRUE)
        .isSaturdayWorking(TRUE)
        .isSundayWorking(TRUE)
        .exceptionDays(exceptionDays)
        .build();
  }

  public CalendarResponse getCalendarResponse() {
    return CalendarResponse.builder()
        .calendarId(CALENDAR_ID)
        .orgId(ORG_ID)
        .description(DESCRIPTION)
        .isMondayWorking(Boolean.TRUE)
        .isTuesdayWorking(Boolean.TRUE)
        .isWednesdayWorking(Boolean.TRUE)
        .isThursdayWorking(Boolean.TRUE)
        .isFridayWorking(Boolean.TRUE)
        .isSaturdayWorking(Boolean.TRUE)
        .isSundayWorking(Boolean.TRUE)
        .exceptionDays(Collections.singletonList(getExceptionDays()))
        .build();
  }

  public PickUpCalendarUpload getPickUpCalendarUpload(String action) {
    return PickUpCalendarUpload.builder()
        .action(action)
        .calendarId(CALENDAR_ID)
        .orgId(ORG_ID)
        .carrierServiceId(CARRIER_SERVICE_ID)
        .description(DESCRIPTION)
        .effectiveDate(EFFECTIVE_DATE)
        .nodeId(NODE_ID)
        .build();
  }

  public NodeCarrierServiceCalendarResponse getNodeCarrierServiceCalendarResponse() {
    return NodeCarrierServiceCalendarResponse.builder()
        .calendarId(CALENDAR_ID)
        .orgId(ORG_ID)
        .carrierServiceId(CARRIER_SERVICE_ID)
        .description(DESCRIPTION)
        .effectiveDate(EFFECTIVE_DATE)
        .nodeId(NODE_ID)
        .build();
  }

  public Page<RecordStatusDto> createPageRecordStatusDtoDto(
      int totalPage, List<RecordStatusDto> jobDtos, int totalElements) {

    return (Page<RecordStatusDto>)
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

  public Page<JobRecordEntity> createPageRecordEntity(
      int totalPage, List<JobRecordEntity> jobDtos, int totalElements) {

    return (Page<JobRecordEntity>)
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

  public CustomRegionResponse getCustomRegionResponse() {
    return CustomRegionResponse.builder()
        .orgId(ORG_ID)
        .id(REGION_ID)
        .customRegionName(CUSTOM_REGION_NAME)
        .codes(CODES)
        .build();
  }

  public CustomRegionUpload getCustomRegionUpload(String action) {
    return CustomRegionUpload.builder()
        .action(action)
        .orgId(ORG_ID)
        .id(REGION_ID)
        .customRegionName(CUSTOM_REGION_NAME)
        .codes(CODES)
        .build();
  }

  public ZoneDataUpload getZoneDataUpload() {
    return ZoneDataUpload.builder()
        .orgId(ORG_ID)
        .carrierServiceId(CARRIER_SERVICE_ID)
        .sourceGeozone(SOURCE_GEO_ZONE)
        .destinationGeozone(DESTINATION_GEO_ZONE)
        .zone(ZONE)
        .build();
  }

  public ZoneResponse getZoneResponse() {
    return ZoneResponse.builder()
        .orgId(ORG_ID)
        .carrierServiceId(CARRIER_SERVICE_ID)
        .sourceGeozone(SOURCE_GEO_ZONE)
        .destinationGeozone(DESTINATION_GEO_ZONE)
        .zone(ZONE)
        .build();
  }
}
