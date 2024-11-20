/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.jobs.dashboard.common;

import com.nextuple.common.base.PagePayload;
import com.nextuple.csvdownload.common.pojo.TransitDataUpload;
import com.nextuple.csvdownload.domain.pojo.ProcessingLeadTimesRaw;
import com.nextuple.jobs.consumers.domain.entity.JobEntity;
import com.nextuple.jobs.consumers.domain.mapper.JobMapper;
import com.nextuple.jobs.consumers.feign.AuthTokenResponse;
import com.nextuple.jobs.framework.common.domain.enums.ApiStatusEnum;
import com.nextuple.jobs.framework.common.domain.enums.JobStatusEnum;
import com.nextuple.jobs.framework.common.domain.enums.JobTypeEnum;
import com.nextuple.jobs.framework.common.domain.enums.RecordDataTypeEnum;
import com.nextuple.jobs.framework.common.domain.outbound.FileMetaDataResponse;
import com.nextuple.jobs.framework.common.domain.outbound.FileResponse;
import com.nextuple.jobs.framework.common.domain.outbound.JobResponse;
import com.nextuple.jobs.framework.common.domain.pojo.AuditLog;
import com.nextuple.jobs.framework.common.domain.pojo.JobDto;
import com.nextuple.jobs.framework.common.domain.pojo.JobFilters;
import com.nextuple.jobs.framework.common.domain.pojo.Metadata;
import com.nextuple.jobs.framework.common.domain.pojo.RecordDto;
import com.nextuple.jobs.framework.common.domain.pojo.RecordStatusDto;
import com.nextuple.jobs.framework.common.domain.pojo.TransitBufferUpload;
import com.nextuple.sourcing.cost.config.dto.costtyypesdto.CostFactorDescriptionDto;
import com.nextuple.sourcing.cost.config.dto.costtyypesdto.SelectorCfInfo;
import com.nextuple.sourcing.cost.config.outbound.CostTypeValidationResponse;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.IntStream;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class TestUtil {

  public static final String ORG_ID = "BAY";
  public static final JobTypeEnum JOB_TYPE_UPLOAD_PROCESSING_LEAD_TIMES =
      JobTypeEnum.UPLOAD_PROCESSING_LEAD_TIMES;
  public static final JobTypeEnum JOB_TYPE_UPLOAD_TRANSIT_TIMES = JobTypeEnum.UPLOAD_TRANSIT_TIMES;
  public static final String JOB_ID = "JobId1";

  public static final String fileName = "Bulk Data Upload.csv";

  public static final String BUCKET_NAME = "bucketName";
  public static final String FILE_PATH = "filePath";

  public static final List<String> tenantServiceOptionExpected =
      List.of("sdndEligible", "expressEligible");

  public static final String CSV_CONTENTS_PROCESSING_LEAD_TIMES =
      "action,nodeId,orgId,serviceOptions,processingTime (in hrs)\n"
          + "UPDATE,1554,BAY,SDND,2\n"
          + "UPDATE,1101,BAY,SDND,2,\n"
          + "DELETE,1560,BAY,SDND,2";

  public static final String CSV_CONTENTS_TRANSIT_BUFFER_REQUEST =
      "orgId,carrierServiceId,sourceGeozone,destinationGeozone,bufferDays,bufferStartDate,bufferEndDate,action,createdBy,transitBufferConfigRequestId\n"
          + "BAY,ALL-SDND,B1P,T0A,1,2022-10-01T17:30:00Z,2022-11-10T17:30:00Z,U,abc,1\n"
          + "BAY,ALL-EXPRESS,B3A,T0A,2,2022-08-01T17:30:00Z,2022-08-11T01:30:00Z,D,def,1\n"
          + "BAY,ALL-EXPRESS,B3B,T0C,1,2022-10-01T17:30:00Z,2022-11-10T17:30:00Z,C,ghi,1\n"
          + "BAY,ALL-EXPRESS,B4A,T0A,1,2022-08-01T17:30:00Z,2022-08-11T01:30:00Z,U,abc,1\n"
          + "BAY,ALL-STANDARD,B4A,T0C,1,2022-10-01T17:30:00Z,2022-11-10T17:30:00Z,D,def,1\n"
          + "BAY,ALL-STANDARD,B1P,T0A,1,2022-08-01T17:30:00Z,2022-08-11T01:30:00Z,C,klm,1";

  public static final String CSV_CONTENTS_POSTAL_CODE_TIMEZONE =
      "action,orgId,postalCode,postalCodePrefix,country,state,city,latitude,longitude,timeZone,customRegions\n"
          + "CREATE,BAY,A0A123,A0A,CA,NL,Witless Bay,47.545965,-53.138234, America/St_Johns,CR1:CR2\n"
          + "UPDATE,BAY,A0B123,A0B,CA,NL,Witless Bay1,47.545975,-53.138244, America/St_Johns,CR1:CR2\n"
          + "CREATE,BAY,A0C123,A0C,CA,NL,Witless Bay3,47.545985,-53.138254, America/St_Johns,CR1:CR2\n"
          + "DELETE,BAY,A0D123,A0D,CA,NL,Witless Bay4,47.545995,-53.138264, America/St_Johns,CR1:CR2";

  public static final String CSV_CONTENTS_TRANSIT_TIMES =
      "orgId,BAY,,,,,,,,,\n"
          + "Carrier Service:,ALL-Standard,,,,,,,,,\n"
          + "Destination FSA / Source FSA ->,SFSA1,SFSA2,SFSA3\n"
          + "DFSA1,,9.96,9.96\n"
          + "DFSA2,D,9,9.9\n"
          + "DFSA3,10,9,9\n"
          + "\n";

  public static final String CSV_CONTENTS_ZONES =
      "Carrier Service:,ALL-Standard,,,,,,,,,\n"
          + "Destination FSA / Source FSA ->,SFSA1,SFSA2,SFSA3\n"
          + "DFSA1,Zone3,Zone1,Zone2\n"
          + "DFSA2,DELETE,NA,Zone2\n"
          + "DFSA3,NA,NA,DELETE\n"
          + "\n";

  public static final String PARTIAL_EMPTY_CSV_CONTENTS_ZONES =
      "Carrier Service:,ALL-Standard,,,,,,,,,\n"
          + "Destination FSA / Source FSA ->,SFSA1,SFSA2,SFSA3\n"
          + "DFSA1,Zone3,Zone1,Zone2\n"
          + "DFSA2,,NA,Zone2\n"
          + "DFSA3,NA,,DELETE\n"
          + "DFSA4,,,\n"
          + "\n";

  public static final String EMPTY_CSV_CONTENTS_ZONES =
      "Carrier Service:,ALL-Standard,,,,,,,,,\n"
          + "Destination FSA / Source FSA ->,SFSA1,SFSA2,SFSA3\n"
          + "DFSA1,,,\n"
          + "\n";

  public static final String CSV_CONTENTS_COST_DEFINITION_FOR_GRID =
      "Notes: Filled in values will be in USD,,,\n"
          + "orgId,NEXTUPLE_GR,,\n"
          + "costType,SHIPPING_COST,,\n"
          + "Carrier Service Id,UPS_GROUND,,\n"
          + "Surge,Non-holidays,,\n"
          + "Dynamic - incremental per pound cost bucket,TRUE,,\n"
          + "bill weight/zones,Zone1,Zone2,Zone3\n"
          + "S,1,11,15\n"
          + "M,2,22,25\n"
          + "L,3,33,35\n"
          + "XL,4,44,45\n"
          + "\n";

  public static final String CSV_CONTENTS_COST_DEFINITION_FOR_TABLE =
      "Notes: Filled in values will be in USD,\n"
          + "orgId,NEXTUPLE_GR\n"
          + "costType,SHIPPING_COST\n"
          + "Carrier Service Id,UPS_AIR\n"
          + "Surge,Non-holidays\n"
          + "Dynamic - incremental per pound cost bucket,FALSE\n"
          + "bill weight,\n"
          + "S,1.23\n"
          + "M,2.32\n"
          + "L,3.01\n"
          + "XL,4.5\n"
          + "\n";

  public static final String CSV_CONTENTS_COST_DEFINITION_FOR_STATIC_TABLE =
      "Notes: Filled in values will be in USD,\n"
          + "orgId,NEXTUPLE_GR\n"
          + "costType,Shipping Cost\n"
          + "Carrier Service Id,ROADIE\n"
          + "Shipping Cost,5\n"
          + "\n";

  public static final String CSV_CONTENTS_WITHOUT_SELECTOR_CF_FOR_GRID =
      "Notes: Filled in values will be in USD,,\n"
          + "orgId,NEXTUPLE_GR,\n"
          + "costType,NODE_PROCESSING_COST,\n"
          + "Surge,Non-holidays,\n"
          + "Dynamic - incremental per pound cost bucket,TRUE,\n"
          + "shift/labour type,Manager,Worker\n"
          + "Morning,1,0\n"
          + "Night,0,1\n"
          + "\n";
  public static final String CSV_CONTENTS_WITHOUT_SELECTOR_CF_VALUE_FOR_GRID =
      "Notes: Filled in values will be in USD,,,\n"
          + "orgId,NEXTUPLE_GR,,\n"
          + "costType,SHIPPING_COST,,\n"
          + "Carrier Service Id,,,\n"
          + "Surge,Non-holidays,,\n"
          + "Dynamic - incremental per pound cost bucket,TRUE,,\n"
          + "bill weight/zones,Zone1,Zone2,Zone3\n"
          + "S,1,11,15\n"
          + "M,2,22,25\n"
          + "L,3,33,35\n"
          + "XL,4,44,45\n"
          + "\n";

  public static final String CSV_CONTENTS_WITHOUT_SELECTOR_CF_FOR_TABLE =
      "Notes: Filled in values will be in USD,\n"
          + "orgId,NEXTUPLE_GR\n"
          + "costType,NODE_PROCESSING_COST\n"
          + "Surge,Non-holidays\n"
          + "Dynamic - incremental per pound cost bucket,FALSE\n"
          + "Node,\n"
          + "DC,100\n"
          + "MFC,200\n"
          + "\n";
  public static final String CSV_CONTENTS_WITHOUT_SELECTOR_CF_VALUE_FOR_TABLE =
      "Notes: Filled in values will be in USD,\n"
          + "orgId,NEXTUPLE_GR\n"
          + "costType,SHIPPING_COST\n"
          + "Carrier Service Id,\n"
          + "Surge,Non-holidays\n"
          + "Dynamic - incremental per pound cost bucket,FALSE\n"
          + "bill weight,\n"
          + "S,1.23\n"
          + "M,2.32\n"
          + "L,3.01\n"
          + "XL,4.5\n"
          + "\n";

  public static final String CSV_CONTENTS_WITHOUT_SELECTOR_CF_FOR_STATIC_TABLE =
      "Notes: Filled in values will be in USD,\n"
          + "orgId,NEXTUPLE_GR\n"
          + "costType,NODE_PROCESSING_COST\n"
          + "NODE_PROCESSING_COST,5\n"
          + "\n";

  public static final String CSV_CONTENTS_WITHOUT_SELECTOR_CF_VALUE_FOR_STATIC_TABLE =
      "Notes: Filled in values will be in USD,\n"
          + "orgId,NEXTUPLE_GR\n"
          + "costType,Shipping Cost\n"
          + "Carrier Service Id,\n"
          + "Shipping Cost,5\n"
          + "\n";

  public static final String CSV_CONTENTS_DELETE_TRANSIT_BUFFER =
      "orgId,BAY,,,,,,,,,\n"
          + "Carrier Service:,ALL-Standard,,,,,,,,,\n"
          + "Destination FSA / Source FSA ->,SFSA1,SFSA2,SFSA3\n"
          + "DFSA1,D,D.D,D\n"
          + "DFSA2,D,D,D\n"
          + "DFSA3,D,D,D\n"
          + "\n";

  public static final String CSV_CONTENTS_UPLOAD_CARRIER_SERVICE =
      "action,orgId,carrierId,carrierName,carrierServiceId,serviceName,serviceOptions\n"
          + "CREATE,org,cId998,cn998,cSId998,sN998,sOpt998\n"
          + "UPDATE,org,cId999,cn999,cSId999,sN999,sOpt999\n"
          + "DELETE,org,cId1000,cn1000,cSId1000,sN1000,sOpt1000\n"
          + "CREATE,org,cId1001,cn1001,cSId1001,sN1001,sOpt1001";

  public static final String CSV_CONTENTS_UPLOAD_CARRIER_CALENDAR =
      "action,calendarId,orgId,carrierServiceId,shippingStage,description,effectiveDate\n"
          + "CREATE,SDND_2022,BAY,ALL_SDND,ALL,Yearly Calendar,2022-02-03\n"
          + "CREATE,SDND_2022,BAY,ALL_SDND,ALL,Yearly Calendar,2022-02-04";

  public static final String CSV_CONTENTS_UPLOAD_NODE_CALENDAR =
      "action,calendarId,nodeId,orgId,description,effectiveDate\n"
          + "CREATE,CGY_2022,1957,BAY,Calgary MFC - non peak,2022-02-02\n"
          + "CREATE,SLC_2022,DC-963-565,BAY,SLC - non peak,2022-01-01\n"
          + "CREATE,VLC_2022,DC-963-577,BAY,VLC - non peak, 2022-01-03";

  public static final String CSV_CONTENTS_UPLOAD_NODE =
      "action,nodeId,orgId,street,city,province,postalCode,country,latitude,longitude,timezone,shipToHome,bopisEligible,nodeType,nodeLabourTier,isActive,startWorkingTime,lastWorkingTime,sdndEligible,expressEligible\n"
          + "CREATE,1957,BAY,Calgary Downtown,Calgary,AB,T2P 1B5,CA,52.1977,-113.8721,America/Edmonton,TRUE,FALSE,MFC,tier1,TRUE,,,FALSE,FALSE\n"
          + "UPDATE,1957,BAY,Calgary Downtown,Calgary,AB,T2P 1B5,CA,52.1977,-113.8721,America/Edmonton,TRUE,FALSE,MFC,tier1,TRUE,,,FALSE,FALSE\n"
          + "DELETE,1957,BAY,Calgary Downtown,Calgary,AB,T2P 1B5,CA,52.1977,-113.8721,America/Edmonton,TRUE,FALSE,MFC,tier1,TRUE,,,FALSE,FALSE";

  public static final String CSV_CONTENTS_UPLOAD_NODE_CARRIER =
      "action,nodeId,orgId,carrierServiceId,serviceOption,lastPickupTime\n"
          + "  CREATE,1,BAY,ALL-EXPRESS,EXPRESS,20:00\n"
          + "  UPDATE,1,BAY,ALL-EXPRESS,EXPRESS,10:00\n"
          + "  DELETE,12,BAY,ALL-EXPRESS,EXPRESS,20:00";

  public static final String CSV_CONTENTS_UPLOAD_CALENDAR_DATA =
      "action,calendarId,orgId,description,isMondayWorking,isTuesdayWorking,isWednesdayWorking,isThursdayWorking,isFridayWorking,isSaturdayWorking,isSundayWorking,exceptionDays\n"
          + "CREATE,Store_2022,BAY,Store calendar - non peak,true,true,true,true,true,true,true,\"[{\\\"date\\\": \\\"2022-01-01\\\",\\\"reason\\\": \\\"New Year's Day\\\"}]\"";
  //          + "CREATE,SLC_2022,BAY,SLC Operations
  // Calendar,true,true,true,true,true,true,true,\"[{\\\"date\\\":
  // \\\"2022-01-01\\\",\\\"reason\\\": \\\"New Year's Day\\\"},{\\\"date\\\":
  // \\\"2022-02-21\\\",\\\"reason\\\": \\\"Family Day\\\"},{\\\"date\\\":
  // \\\"2022-04-15\\\",\\\"reason\\\": \\\"Good Friday\\\"},{\\\"date\\\":
  // \\\"2022-04-18\\\",\\\"reason\\\": \\\"Easter Monday\\\"},{\\\"date\\\":
  // \\\"2022-05-23\\\",\\\"reason\\\": \\\"Victoria Day\\\"},{\\\"date\\\":
  // \\\"2022-07-01\\\",\\\"reason\\\": \\\"Canada Day\\\"},{\\\"date\\\":
  // \\\"2022-08-01\\\",\\\"reason\\\": \\\"Civid Holiday\\\"},{\\\"date\\\":
  // \\\"2022-09-05\\\",\\\"reason\\\": \\\"Labour Day\\\"},{\\\"date\\\":
  // \\\"2022-10-10\\\",\\\"reason\\\": \\\"Thanksgiving\\\"},{\\\"date\\\":
  // \\\"2022-11-11\\\",\\\"reason\\\": \\\"Remembrance Day\\\"},{\\\"date\\\":
  // \\\"2022-12-25\\\",\\\"reason\\\": \\\"Christmas Day\\\"}]\"";

  public static final Optional<String> DEFAULT_SORT_FIELD = Optional.of("created_date");

  public static final Optional<String> DEFAULT_SORT_ORDER = Optional.of("ASC");

  public static final Optional<Integer> DEFAULT_PAGE_NO = Optional.of(1);

  public static final Optional<Integer> DEFAULT_PAGE_SIZE = Optional.of(15);

  public JobDto createJob(
      String jobId,
      String orgId,
      JobStatusEnum status,
      List<AuditLog> auditLogs,
      JobTypeEnum jobTypeEnum,
      Long fileMetaDataId) {
    JobDto job = new JobDto();
    job.setOrgId(orgId);
    job.setStatus(status);
    job.setTotalRecords(10);
    job.setProcessedRecords(5);
    job.setSuccessCount(5);
    job.setFailureCount(0);
    job.setJobType(jobTypeEnum);
    job.setMetadata(new Metadata());
    job.setUserId("User1");
    job.setAuditLog(auditLogs);
    job.setJobId(jobId);
    job.setFileMetaDataId(fileMetaDataId);
    return job;
  }

  public JobEntity createJobEntity(
      String jobId,
      String orgId,
      JobStatusEnum jobStatus,
      List<AuditLog> auditLog,
      JobTypeEnum jobType,
      Long fileMetaDataId) {
    return JobMapper.INSTANCE.toJobEntity(
        createJob(jobId, orgId, jobStatus, auditLog, jobType, fileMetaDataId));
  }

  public JobEntity createJobEntity(
      String jobId,
      String orgId,
      JobStatusEnum jobStatus,
      List<AuditLog> auditLog,
      JobTypeEnum jobType) {
    return JobMapper.INSTANCE.toJobEntity(
        createJob(jobId, orgId, jobStatus, auditLog, jobType, null));
  }

  public JobResponse createJobResponse(
      String jobId,
      String orgId,
      JobStatusEnum status,
      List<AuditLog> auditLogs,
      JobTypeEnum jobTypeEnum) {
    JobResponse job = new JobResponse();
    job.setOrgId(orgId);
    job.setStatus(status);
    job.setTotalRecords(10);
    job.setProcessedRecords(5);
    job.setSuccessCount(5);
    job.setFailureCount(0);
    job.setJobType(String.valueOf(jobTypeEnum));
    job.setMetadata(new Metadata());
    job.setUserId("User1");
    job.setAuditLog(auditLogs);
    job.setJobId(jobId);
    return job;
  }

  public RecordStatusDto createRecordStatusDto(
      String jobId, JobTypeEnum jobType, int recordNo, int statusCode) {
    RecordStatusDto recordStatusDto = new RecordStatusDto();

    recordStatusDto.setJobId(jobId);
    recordStatusDto.setOrgId(ORG_ID);
    recordStatusDto.setRecordNo(recordNo);
    recordStatusDto.setRequestBody("Request body");
    recordStatusDto.setResponseBody("Response Body");
    recordStatusDto.setJobType(jobType);
    recordStatusDto.setStatusCode(statusCode);
    if (statusCode == 200) {
      recordStatusDto.setStatus(ApiStatusEnum.valueOf("SUCCESS"));
    } else {
      recordStatusDto.setStatus(ApiStatusEnum.valueOf("FAILURE"));
    }
    return recordStatusDto;
  }

  public AuditLog createAuditLog(JobStatusEnum jobStatusEnum) {
    AuditLog auditLog = new AuditLog();
    auditLog.setTimeStamp(new Date());
    auditLog.setStatus(jobStatusEnum);
    return auditLog;
  }

  public List<JobDto> createJobList(String jobType, String orgId) {
    AuditLog submit = createAuditLog(JobStatusEnum.SUBMITTED);
    AuditLog running = createAuditLog(JobStatusEnum.RUNNING);

    List<AuditLog> auditLogList1 = new ArrayList<>(Collections.singletonList(submit));
    List<AuditLog> auditLogList2 = new ArrayList<>(Arrays.asList(submit, running));

    JobDto jobId1 =
        createJob(
            "JobId1",
            orgId,
            JobStatusEnum.SUBMITTED,
            auditLogList1,
            JobTypeEnum.valueOf(jobType),
            null);
    JobDto jobId2 =
        createJob(
            "JobId2",
            orgId,
            JobStatusEnum.RUNNING,
            auditLogList2,
            JobTypeEnum.valueOf(jobType),
            null);
    return Arrays.asList(jobId1, jobId2);
  }

  public List<RecordStatusDto> createRecordStatusDtoList(
      String jobId, JobTypeEnum jobType, int numRecords, int successRecords) {
    ArrayList<RecordStatusDto> recordStatusDtos = new ArrayList<>();
    IntStream.range(0, numRecords)
        .forEach(
            i -> {
              if (i < successRecords) {
                recordStatusDtos.add(createRecordStatusDto(jobId, jobType, i, 200));
              } else {
                recordStatusDtos.add(createRecordStatusDto(jobId, jobType, i, 500));
              }
            });
    return recordStatusDtos;
  }

  public PagePayload<JobResponse> createPagePayloadJobDto(
      List<JobDto> jobDtoList, int totalPage, int totalElements, int pageNo) {
    PagePayload<JobResponse> pagePayload = new PagePayload<>();
    Page<JobResponse> pageResp = createPageJobDtos(totalPage, jobDtoList, totalElements);
    PagePayload.Pagination pagination = new PagePayload.Pagination();
    pagination.setTotalRecords((int) pageResp.getTotalElements());
    pagination.setTotalPages(pageResp.getTotalPages());
    pagination.setCurrentPage(pageNo);
    pagePayload.setData(pageResp.getContent());
    pagePayload.setPagination(pagination);
    return pagePayload;
  }

  public RecordDto getRecordDto() {
    var recordDto = new RecordDto();
    recordDto.setRecordId(1);
    recordDto.setRecordData("dummy");
    recordDto.setOrgId("BAY");
    recordDto.setJobId("dummy");
    recordDto.setJobType(String.valueOf(JobTypeEnum.TRANSIT_BUFFER_REQUEST));
    recordDto.setTotalRecords(1);
    recordDto.setRecordType(RecordDataTypeEnum.CSV);
    return recordDto;
  }

  public Page<JobResponse> createPageJobDtos(
      int totalPage, List<JobDto> jobList, int totalElements) {
    Page<JobResponse> pageResponse =
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
            return jobList;
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

  public FileMetaDataResponse getFileMetaDataResponse(Long fileMetaDataId) {
    return FileMetaDataResponse.builder()
        .id(fileMetaDataId)
        .name("transitBufferRequest")
        .path(String.join("/", BUCKET_NAME, FILE_PATH))
        .build();
  }

  public FileResponse getFileResponse() {
    return FileResponse.builder()
        .bucketName(BUCKET_NAME)
        .filePath(FILE_PATH)
        .inputStream(new ByteArrayInputStream(CSV_CONTENTS_TRANSIT_BUFFER_REQUEST.getBytes()))
        .build();
  }

  public AuthTokenResponse getAuthTokenResponse() {
    com.nextuple.jobs.consumers.feign.AuthTokenResponse authTokenResponse = new AuthTokenResponse();
    authTokenResponse.setAccessToken("token");
    authTokenResponse.setTokenType("tokenType");
    authTokenResponse.setExpiresIn(20);

    return authTokenResponse;
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

  public ProcessingLeadTimesRaw getProcessingLeadTimesRaw() {
    ProcessingLeadTimesRaw processingLeadTimesRaw = new ProcessingLeadTimesRaw();
    processingLeadTimesRaw.setNodeId("NODE_ID");
    processingLeadTimesRaw.setOrgId(ORG_ID);
    processingLeadTimesRaw.setServiceOption("SERVICE_OPTION");
    processingLeadTimesRaw.setProcessingTime(String.valueOf("PROCESSING_TIME"));

    return processingLeadTimesRaw;
  }

  public JobEntity createJobEntity(JobTypeEnum jobTypeEnum, int totalRecords) {
    return JobMapper.INSTANCE.toJobEntity(createJob(jobTypeEnum, totalRecords));
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

  public PagePayload<RecordStatusDto> createPagePayloadRecordStatusDto(
      List<RecordStatusDto> recordStatusDtos, int totalPage, int totalElements, int pageNo) {
    PagePayload<RecordStatusDto> pagePayload = new PagePayload<>();
    Page<RecordStatusDto> pageResp =
        createPagerecordStatusDtos(totalPage, recordStatusDtos, totalElements);
    PagePayload.Pagination pagination = new PagePayload.Pagination();
    pagination.setTotalRecords((int) pageResp.getTotalElements());
    pagination.setTotalPages(pageResp.getTotalPages());
    pagination.setCurrentPage(pageNo);
    pagePayload.setData(pageResp.getContent());
    pagePayload.setPagination(pagination);
    return pagePayload;
  }

  public Page<RecordStatusDto> createPagerecordStatusDtos(
      int totalPage, List<RecordStatusDto> jobList, int totalElements) {
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
            return jobList;
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

  public JobDto getJobDto(JobTypeEnum jobTypeEnum) {
    JobDto jobDto = new JobDto();
    jobDto.setJobId(JOB_ID);
    jobDto.setTotalRecords(2);
    jobDto.setJobType(jobTypeEnum);
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

  public CostTypeValidationResponse getCostTypeValidationResponse() {
    return CostTypeValidationResponse.builder()
        .costItinerary("")
        .costFactors(List.of())
        .currency("USD")
        .costType("SHIPPING_COST")
        .selectorCf("carrierServiceId")
        .selectorCfInfo(getSelectorCfInfoList())
        .build();
  }

  private List<SelectorCfInfo> getSelectorCfInfoList() {
    SelectorCfInfo gridData =
        getSelectorCfInfo(
            "SHIPPING_COST_UPS_GROUND_LIKE",
            getCostFactorList("Surge", List.of("Non-Holidays")),
            getCostFactorDescriptionDto("Zone", List.of("Zone1", "Zone2", "Zone3")),
            getCostFactorDescriptionDto("Bill Weight", List.of("S", "M", "L", "XL")),
            "UPS_GROUND");

    SelectorCfInfo tableData =
        getSelectorCfInfo(
            "SHIPPING_COST_UPS_AIR_LIKE",
            getCostFactorList("Surge", List.of("Non-Holidays")),
            getCostFactorDescriptionDto("Bill Weight", List.of("S", "M", "L", "XL")),
            null,
            "UPS_AIR");

    SelectorCfInfo staticData =
        getSelectorCfInfo("SHIPPING_COST_ROADIE_LIKE", List.of(), null, null, "ROADIE");

    return List.of(gridData, tableData, staticData);
  }

  private CostFactorDescriptionDto getCostFactorDescriptionDto(
      String costFactor, List<String> values) {
    return CostFactorDescriptionDto.builder().costFactor(costFactor).values(values).build();
  }

  private List<CostFactorDescriptionDto> getCostFactorList(String costFactor, List<String> values) {
    return List.of(getCostFactorDescriptionDto(costFactor, values));
  }

  public SelectorCfInfo getSelectorCfInfo(
      String costItinerary,
      List<CostFactorDescriptionDto> costFactors,
      CostFactorDescriptionDto row,
      CostFactorDescriptionDto column,
      String selectorCfValue) {
    return SelectorCfInfo.builder()
        .selectorCfValue("UPS_GROUND")
        .costItinerary(costItinerary)
        .costFactors(costFactors)
        .row(row)
        .column(column)
        .selectorCfValue(selectorCfValue)
        .build();
  }

  public CostTypeValidationResponse getCostTypeDetailsWithEmptySelectorCfForGrid() {
    return CostTypeValidationResponse.builder()
        .costType("NODE_PROCESSING_COST")
        .costItinerary("NODE_LIKE")
        .costFactors(getCostFactorList("Surge", List.of("Non-Holidays")))
        .row(getCostFactorDescriptionDto("shift", List.of("Morning", "Night")))
        .column(getCostFactorDescriptionDto("labour type", List.of("Manager", "Worker")))
        .selectorCf("")
        .selectorCfInfo(List.of())
        .build();
  }

  public CostTypeValidationResponse getCostTypeDetailsWithEmptySelectorCfForTable() {
    return CostTypeValidationResponse.builder()
        .costType("NODE_PROCESSING_COST")
        .costItinerary("NODE_LIKE")
        .costFactors(getCostFactorList("Surge", List.of("Non-Holidays")))
        .row(getCostFactorDescriptionDto("Node", List.of("DC", "MFC")))
        .selectorCf("")
        .selectorCfInfo(List.of())
        .build();
  }

  public CostTypeValidationResponse getCostTypeDetailsWithEmptySelectorCfForStaticTable() {
    return CostTypeValidationResponse.builder()
        .costType("NODE_PROCESSING_COST")
        .costItinerary("NODE_LIKE")
        .costFactors(List.of())
        .selectorCf("")
        .selectorCfInfo(List.of())
        .build();
  }
}
