/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.csvdownload.util;

import com.nextuple.common.base.PagePayload;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.common.response.PreSignedUrlResponse;
import com.nextuple.csvdownload.common.inbound.GenericUploadRequest;
import com.nextuple.csvdownload.common.outbound.GenericUploadResponse;
import com.nextuple.jobs.framework.common.domain.enums.JobStatusEnum;
import com.nextuple.jobs.framework.common.domain.enums.JobTypeEnum;
import com.nextuple.jobs.framework.common.domain.outbound.FileMetaDataResponse;
import com.nextuple.jobs.framework.common.domain.outbound.FileResponse;
import com.nextuple.jobs.framework.common.domain.outbound.JobResponse;
import com.nextuple.jobs.framework.common.domain.pojo.AuditLog;
import com.nextuple.jobs.framework.common.domain.pojo.JobDto;
import com.nextuple.jobs.framework.common.domain.pojo.RecordStatusDto;
import com.nextuple.sourcing.cost.config.dto.FilterCostFactorInfoDto;
import com.nextuple.sourcing.cost.config.dto.SelectorCostFactorInfoDto;
import com.nextuple.sourcing.cost.config.dto.costtyypesdto.CostFactorDescriptionDto;
import com.nextuple.sourcing.cost.config.dto.costtyypesdto.SelectorCfInfo;
import com.nextuple.sourcing.cost.config.inbound.CostDefinitionRequest;
import com.nextuple.sourcing.cost.config.outbound.CostTypeValidationResponse;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class TestUtil {

  public static final String ORG_ID = "BAY";
  public static final Integer RECORDS_PER_PAGE = 200;
  public static final Integer MAX_NO_OF_PAGES = 5;
  public static final String COST_TYPE = "costType";
  public static final String COST_TYPE1 = "SHIPPING_COST";
  public static final String COST_TYPE1_DISPLAY_NAME = "Shipping Cost";

  public static final String nodeServiceOptionBufferProcessingRequest =
      "{\"nodeId\":\"1554\",\"orgId\":\"BAY\",\n"
          + "    \"serviceOption\": \"STANDARD\",\n"
          + "    \"bufferHours\": \"1\",\n"
          + "    \"startTime\": \"2022-08-23T09:38:57Z\",\n"
          + "    \"endTime\": \"2022-08-24T09:38:57Z\""
          + "}";

  public static final String pickUpCalendar =
      "{\"action\":\"CREATE\",\"nodeId\":\"1554\",\"orgId\":\"BAY\",\n"
          + "    \"calendarId\": \"SDND_2024\",\n"
          + "    \"carrierServiceId\": \"ALL_SDND\",\n"
          + "    \"description\": \"dummy\",\n"
          + "    \"effectiveDate\": \"2022-08-24T09:38:57Z\""
          + "}";

  public static final String postalCodeTimeZone =
      "{\"action\":\"CREATE\",\"zipCodePrefix\":\"DDD\",\"orgId\":\"BAY\",\n"
          + "    \"country\": \"dummy\",\n"
          + "    \"state\": \"dummy\",\n"
          + "    \"city\": \"dummy\",\n"
          + "    \"latitude\": \"dummy\",\n"
          + "    \"longitude\": \"dummy\",\n"
          + "    \"timeZone\": \"America/Toronto\""
          + "}";
  public static final String transitBufferRequestBody =
      "{\"action\":\"CREATE\",\n"
          + "\"sourceGeozone\": \"sFsa\",\n"
          + "\"destinationGeozone\": \"dFsa\"}";

  public static final String FILE_PATH =
      "promise-s3-lambda-dev/ui/node-carrier/2022-10-21/market-region.csv";
  public static final long FILE_METADATA_ID = 1L;
  public static final String BUCKET_NAME = "promise-s3-lambda-dev";
  public static final String FILE_NAME = "postalCodeTimezone.csv";
  public static final String CONTENT_TYPE = "text/csv";
  public static final Long CONTENT_LENGTH = 100L;
  public static final String JOB_ID = "J1";
  public static final String TRANSIT_INVALID_HEADER_ERROR_MESSAGE =
      "Transit Time data uploaded file has invalid headers.";
  public static final String ZONE_INVALID_HEADER_ERROR_MESSAGE =
      "Zones data uploaded file has invalid headers.";

  public static final String ZONES_DATA_UPLOAD_EMPTY_INVALID_HEADERS =
      "Zones data upload has no records or headers are invalid.";
  public static final String NO_RECORD_ERROR_MESSAGE = "No Records found in the csv";
  public static final String nodeCalendarRequestBodyJson =
      "{\n"
          + "    \"calendarId\": \"test_2\",\n"
          + "    \"orgId\": \"BAY\",\n"
          + "    \"nodeId\": \"DC-963-565\",\n"
          + "    \"effectiveDate\": \"2022-07-24\",\n"
          + "    \"description\": \"Monthly\"\n"
          + "}";
  public static final String processingLeadTimesRequestBodyJson =
      "{\"nodeId\":\"1554\",\"orgId\":\"BAY\",\"carrierServiceId\":\"ALL-SDND\",\"serviceOption\":\"SDND\",\"processingTime\":2.0,\"lastPickupTime\":\"00:00\"}";

  public static final String nodeCarrierRequestBodyJson =
      "{\n"
          + "    \"nodeId\": \"NXTHBC01\",\n"
          + "    \"orgId\": \"NXT\",\n"
          + "    \"carrierServiceId\": \"A_STD\",\n"
          + "    \"serviceOption\": \"Standard\",\n"
          + "    \"processingTime\": 20.2,\n"
          + "    \"lastPickupTime\": \"19:00\"\n"
          + "}";
  public static final String nodeRequestBodyJson =
      "{\n"
          + "    \"nodeId\": \"NXTHBC01\",\n"
          + "    \"orgId\": \"NXT\",\n"
          + "    \"street\": \"100 Metropolitan Rd.\",\n"
          + "    \"city\": \"Scarborough\",\n"
          + "    \"state\": \"ON\",\n"
          + "    \"zipCode\": \"M1R 5A2\",\n"
          + "    \"country\": \"Canada\",\n"
          + "    \"latitude\": \"43.769912\",\n"
          + "    \"longitude\": \"-79.296678\",\n"
          + "    \"timezone\": \"America/Toronto\",\n"
          + "    \"shipToHome\": true,\n"
          + "    \"bopisEligible\": true,\n"
          + "    \"serviceOptionEligibilities\": {\n"
          + "        \"expressEligible\": true,\n"
          + "        \"sdndEligible\": true,\n"
          + "        \"bopisEligible\": true,\n"
          + "        \"nextdayEligible\": true\n"
          + "    },\n"
          + "    \"nodeType\": \"MFC\",\n"
          + "    \"nodeLabourTier\": \"tier1\",\n"
          + "    \"isActive\": true,\n"
          + "    \"startWorkingTime\": \"08:00\",\n"
          + "    \"lastWorkingTime\": \"16:00\"\n"
          + "}";

  public static final String nodeRequestBodyJson1 =
      "{\n"
          + "    \"nodeId\": \"NXTHBC01\",\n"
          + "    \"orgId\": \"NXT\",\n"
          + "    \"street\": \"100 Metropolitan Rd.\",\n"
          + "    \"city\": \"Scarborough\",\n"
          + "    \"state\": \"ON\",\n"
          + "    \"zipCode\": \"M1R 5A2\",\n"
          + "    \"country\": \"Canada\",\n"
          + "    \"latitude\": \"43.769912\",\n"
          + "    \"longitude\": \"-79.296678\",\n"
          + "    \"timezone\": \"America/Toronto\",\n"
          + "    \"shipToHome\": true,\n"
          + "    \"bopisEligible\": true,\n"
          + "    \"serviceOptionEligibilities\": {\n"
          + "    },\n"
          + "    \"nodeType\": \"MFC\",\n"
          + "    \"nodeLabourTier\": \"tier1\",\n"
          + "    \"isActive\": true\n"
          + "}";

  public static final String transitTimesRequestBodyJson =
      "{\"orgId\":\"BAY\","
          + "\"sourceGeozone\":\"A0A\","
          + "\"destinationGeozone\":\"M1R\","
          + "\"carrierServiceId\":\"ALL-SDND\","
          + "\"transitDays\":\"1.5\"}";

  public static final String transitTimesRequestBodyJson2 =
      "{\"orgId\":\"BAY\","
          + "\"sourceGeozone\":\"A0B\","
          + "\"destinationGeozone\":\"M1R\","
          + "\"carrierServiceId\":\"ALL-SDND\","
          + "\"transitDays\":\"1.5\"}";

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

  public static final String CSV_CONTENTS_WITH_OUT_SELECTOR_CF_FOR_TABLE =
      "Notes: Filled in values will be in USD,\n"
          + "Org Id,NEXTUPLE_GR\n"
          + "Cost Type,SHIPPING_COST\n"
          + "Surge,Non-holidays\n"
          + "Dynamic - incremental per pound cost bucket,TRUE\n"
          + "Bill Weight,\n"
          + "S,\n"
          + "M,DELETE\n"
          + "L,3\n"
          + "XL,4\n"
          + "\n";

  public static final String CSV_CONTENTS_WITH_SELECTOR_CF_FOR_STATIC_TABLE =
      "Notes: Filled in values will be in USD,\n"
          + "Org Id,NEXTUPLE_GR\n"
          + "Cost Type,Shipping Cost\n"
          + "Carrier Service Id,UPS-AIR\n"
          + "Shipping Cost,\n";

  public static final String CSV_CONTENTS_WITH_OUT_SELECTOR_CF_FOR_STATIC_TABLE =
      "Notes: Filled in values will be in USD,\n"
          + "Org Id,NEXTUPLE_GR\n"
          + "Cost Type,Shipping Cost\n"
          + "Shipping Cost,\n";

  public GenericUploadRequest getGenericUploadRequest() {
    return GenericUploadRequest.builder().orgId(ORG_ID).filePath(FILE_PATH).build();
  }

  public GenericUploadResponse getGenericUploadResponse() {
    return GenericUploadResponse.builder().orgId(ORG_ID).fileMetaDataId(FILE_METADATA_ID).build();
  }

  public FileResponse getFileResponse() {
    return FileResponse.builder()
        .bucketName(BUCKET_NAME)
        .fileName(FILE_NAME)
        .filePath(FILE_PATH)
        .contentLength(CONTENT_LENGTH)
        .contentType(CONTENT_TYPE)
        .inputStream(Mockito.mock(InputStream.class))
        .build();
  }

  public FileResponse getInvalidHeadersFileForCalendarResponse() throws IOException {
    Path path = Paths.get("src", "test", "resources", "carrierService", "carrierService.csv");
    InputStream inputStream = Files.newInputStream(path);
    return FileResponse.builder()
        .bucketName(BUCKET_NAME)
        .fileName(FILE_NAME)
        .filePath(FILE_PATH)
        .contentLength(CONTENT_LENGTH)
        .contentType(CONTENT_TYPE)
        .inputStream(inputStream)
        .build();
  }

  public FileMetaDataResponse getFileMetaDataResponse() {
    return FileMetaDataResponse.builder().id(FILE_METADATA_ID).build();
  }

  public JobResponse getJobResponse() {
    JobResponse response = new JobResponse();
    response.setJobId(JOB_ID);
    return response;
  }

  public JobDto createJobForDownloadLogs(String jobId, JobTypeEnum jobTypeEnum, int totalRecords) {
    JobDto job = new JobDto();
    job.setJobId(jobId);
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

  public RecordStatusDto getRecordStatusDtoForNodeServiceOptionBuffer() {
    return RecordStatusDto.builder()
        .jobId(JOB_ID)
        .jobType(JobTypeEnum.UPLOAD_NODE_SERVICE_OPTION_BUFFER)
        .orgId(ORG_ID)
        .errorMessage("Invalid nodeId")
        .requestBody(nodeServiceOptionBufferProcessingRequest)
        .build();
  }

  public RecordStatusDto getRecordStatusDtoForPickUpCalendar() {
    return RecordStatusDto.builder()
        .jobId(JOB_ID)
        .jobType(JobTypeEnum.UPLOAD_PICKUP_CALENDER)
        .orgId(ORG_ID)
        .errorMessage("Invalid nodeId")
        .requestBody(pickUpCalendar)
        .build();
  }

  public RecordStatusDto getRecordStatusDtoForPostalCodeTimeZone() {
    return RecordStatusDto.builder()
        .jobId(JOB_ID)
        .jobType(JobTypeEnum.UPLOAD_POSTAL_CODE_TIMEZONE)
        .orgId(ORG_ID)
        .errorMessage("Invalid nodeId")
        .requestBody(postalCodeTimeZone)
        .build();
  }

  public RecordStatusDto getRecordStatusDtoForTransitBufferUpload() {
    return RecordStatusDto.builder()
        .jobId(JOB_ID)
        .jobType(JobTypeEnum.TRANSIT_BUFFER_REQUEST)
        .orgId(ORG_ID)
        .errorMessage("Invalid action")
        .requestBody(transitBufferRequestBody)
        .build();
  }

  public PreSignedUrlResponse getPreSignedUrlResponseForNodeServiceOptionBuffer() {
    return PreSignedUrlResponse.builder()
        .signedURL("URL")
        .filePath(
            "ui-logs/calendar/2022-11-02/download-log-calendar-upload16673668548039061987620665277326.csv")
        .storageType("s3")
        .build();
  }

  public PreSignedUrlResponse getPreSignedUrlResponseForPostalCodeTimeZone() {
    return PreSignedUrlResponse.builder()
        .signedURL("URL")
        .filePath(
            "ui-logs/postal-code-timezone/2022-11-02/download-log-postal-code-timezone16673715196797600365693812117503.csv")
        .storageType("s3")
        .build();
  }

  public JobDto createJob(String jobId, JobTypeEnum jobTypeEnum, int totalRecords) {
    JobDto job = new JobDto();
    job.setJobId(jobId);
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

  public RecordStatusDto getJobRecordsForNodeCalendar() {

    return RecordStatusDto.builder()
        .jobId(JOB_ID)
        .jobType(JobTypeEnum.UPLOAD_NODE_CALENDER)
        .orgId(ORG_ID)
        .errorMessage("Invalid nodeId")
        .requestBody(nodeCalendarRequestBodyJson)
        .build();
  }

  public PreSignedUrlResponse getPreSignedUrlResponse() {
    return PreSignedUrlResponse.builder()
        .signedURL("URL")
        .filePath(
            "ui-logs/calendar/2022-11-02/download-log-calendar-upload16673668548039061987620665277326.csv")
        .storageType("s3")
        .build();
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

  public RecordStatusDto getJobRecordsForNodeCarrier() {

    return RecordStatusDto.builder()
        .jobId(JOB_ID)
        .jobType(JobTypeEnum.UPLOAD_NODE_CARRIER)
        .orgId(ORG_ID)
        .errorMessage("Invalid nodeId")
        .requestBody(nodeCarrierRequestBodyJson)
        .build();
  }

  public RecordStatusDto getJobRecordsForNodes() {

    return RecordStatusDto.builder()
        .jobId(JOB_ID)
        .jobType(JobTypeEnum.UPLOAD_NODES)
        .orgId(ORG_ID)
        .errorMessage("Invalid nodeId")
        .requestBody(nodeRequestBodyJson)
        .build();
  }

  public RecordStatusDto getJobRecordsForNodes1() {

    return RecordStatusDto.builder()
        .jobId(JOB_ID)
        .jobType(JobTypeEnum.UPLOAD_NODES)
        .orgId(ORG_ID)
        .errorMessage("Invalid nodeId")
        .requestBody(nodeRequestBodyJson1)
        .build();
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

  public RecordStatusDto getJobRecordsForTransitTimes2() {

    return RecordStatusDto.builder()
        .jobId(JOB_ID)
        .jobType(JobTypeEnum.UPLOAD_TRANSIT_TIMES)
        .orgId(ORG_ID)
        .errorMessage("Invalid nodeId")
        .requestBody(transitTimesRequestBodyJson2)
        .build();
  }

  public RecordStatusDto getJobRecordsForTransitTimes(String requestBody) {

    return RecordStatusDto.builder()
        .jobId(JOB_ID)
        .jobType(JobTypeEnum.UPLOAD_TRANSIT_TIMES)
        .orgId(ORG_ID)
        .errorMessage("Invalid nodeId")
        .requestBody(requestBody)
        .build();
  }

  public String transitRequestBody(String src, String dest) {
    return "{\"orgId\":\"BAY\","
        + "\"sourceGeozone\":\""
        + src
        + "\","
        + "\"destinationGeozone\":\""
        + dest
        + "\","
        + "\"carrierServiceId\":\"ALL-SDND\","
        + "\"transitDays\":\"1.5\"}";
  }

  public String[] generateGeozones(int length) {
    String geozones[] = new String[length];
    while (length-- > 0) geozones[length] = String.valueOf((int) (100 + Math.random() * 500));
    return geozones;
  }

  public List<RecordStatusDto> getRecordStatusDtoForLeadTime() {
    List<RecordStatusDto> recordStatusDtoList = new ArrayList<>();
    RecordStatusDto dto = new RecordStatusDto();
    dto.setRequestBody(getProcessingLeadTimesRequestBodyJson());
    recordStatusDtoList.add(dto);
    return recordStatusDtoList;
  }

  public String getProcessingLeadTimesRequestBodyJson() {
    return "{\"actionType\": \"UPDATE\",\"nodeId\": \"NODE1\",\"orgId\": \"XYZINC\",\"serviceOption\": \"SDND\",\"processingTime\": \"2\",\"carrierServiceId\": \"ALL-SDND\"\n"
        + "}";
  }

  public RecordStatusDto getJobRecordsForCustomRegion() {

    return RecordStatusDto.builder()
        .jobId(JOB_ID)
        .jobType(JobTypeEnum.UPLOAD_NODE_CARRIER)
        .orgId(ORG_ID)
        .errorMessage("Invalid regionId")
        .requestBody(customRegionRequestBodyJson)
        .build();
  }

  public static final String customRegionRequestBodyJson =
      "{\"id\": \"CRID8\",\n"
          + "   \"orgId\": \"NEXTUPLE\",\n"
          + "   \"customRegionName\": \"CT6\",\n"
          + "   \"codes\": [\"T2P103\"],\n"
          + "   \"customRegionDescription\": \"Boston Metro Region\"\n"
          + "}";

  public RecordStatusDto getJobRecordsForZone(String errorMessage, String requestBody) {
    return RecordStatusDto.builder()
        .jobId(JOB_ID)
        .jobType(JobTypeEnum.UPLOAD_ZONES)
        .orgId(ORG_ID)
        .errorMessage(errorMessage)
        .requestBody(requestBody)
        .build();
  }

  public String zoneRequestBody(String src, String dest) {
    return "{\"orgId\":\"NEXTUPLE\","
        + "\"sourceGeozone\":\""
        + src
        + "\","
        + "\"destinationGeozone\":\""
        + dest
        + "\","
        + "\"carrierServiceId\":\"ALL-SDND\","
        + "\"zone\":\"Zone1\"}";
  }

  public CostDefinitionRequest getGridRequest() {
    return CostDefinitionRequest.builder()
        .costType("SHIPPING_COST")
        .selector(
            SelectorCostFactorInfoDto.builder()
                .selectorCf("carrierServiceId")
                .selectorCfValue("UPS-GROUND")
                .build())
        .filters(
            List.of(
                FilterCostFactorInfoDto.builder()
                    .costFactor("surge")
                    .costFactorValue("NON-HOLIDAY")
                    .build()))
        .row("billWeight")
        .column("zone")
        .build();
  }

  public RecordStatusDto getJobRecordsForCostDefinition(String errorMessage, String requestJson) {
    return RecordStatusDto.builder()
        .jobId(JOB_ID)
        .jobType(JobTypeEnum.UPLOAD_COST_DEFINITION)
        .orgId(ORG_ID)
        .errorMessage(errorMessage)
        .requestBody(requestJson)
        .build();
  }

  public static final String costDefinitionRequestBodyJson1 =
      """
          {"orgId": "NEXTUPLE",
             "costItinerary": "UPS_GROUND_LIKE",
             "costFactorCombinationKey": "UPS_GROUND|Zone1|S",
             "costValue": "12.0",
             "prevSlabValue": ""
          }""";

  public static final String costDefinitionRequestBodyJson2 =
      """
      {"orgId": "NEXTUPLE",
         "costItinerary": "UPS_GROUND_LIKE",
         "costFactorCombinationKey": "UPS_GROUND|Zone1|M",
         "costValue": "9.0",
         "prevSlabValue": "UPS_GROUND|Zone1|S"
      }""";

  public static final String costDefinitionRequestBodyJson3 =
      """
          {"orgId": "NEXTUPLE",
             "costItinerary": "UPS_GROUND_LIKE",
             "costFactorCombinationKey": "Non-Holidays|S",
             "costValue": "DELETE",
             "prevSlabValue": ""
          }""";

  public static final String costDefinitionRequestBodyJson4 =
      """
          {"orgId": "NEXTUPLE",
             "costItinerary": "UPS_GROUND_LIKE",
             "costFactorCombinationKey": "Non-Holidays|M",
             "costValue": "",
             "prevSlabValue": ""
          }""";

  public JobDto getJobDto() {
    JobDto jobDto = new JobDto();
    jobDto.setJobId(JOB_ID);
    jobDto.setTotalRecords(2);
    jobDto.setJobType(JobTypeEnum.UPLOAD_COST_DEFINITION);
    jobDto.setProcessedRecords(2);
    jobDto.setRemainingRecords(0);
    jobDto.setFailureCount(0);
    jobDto.setSuccessCount(0);
    jobDto.setStatus(JobStatusEnum.COMPLETED);
    jobDto.setOrgId(ORG_ID);
    return jobDto;
  }

  public BaseResponse<FileMetaDataResponse> getFileMetaDataBaseResponse() {
    FileMetaDataResponse fileMetaDataResponse =
        FileMetaDataResponse.builder()
            .id(1L)
            .storageType("Blob")
            .path("bucket/path")
            .name("ANY")
            .size("56")
            .build();
    BaseResponse<FileMetaDataResponse> fileMetaDataResponseBaseResponse = new BaseResponse<>();
    fileMetaDataResponseBaseResponse.setPayload(fileMetaDataResponse);
    fileMetaDataResponseBaseResponse.setSuccess(Boolean.TRUE);
    return fileMetaDataResponseBaseResponse;
  }

  public FileResponse getCostDefinitionFileResponse(InputStream inputStream) {
    return FileResponse.builder()
        .bucketName(BUCKET_NAME)
        .fileName(FILE_NAME)
        .filePath(FILE_PATH)
        .contentLength(CONTENT_LENGTH)
        .contentType(CONTENT_TYPE)
        .inputStream(inputStream)
        .build();
  }

  public GenericUploadRequest getUploadRequestWithAdditionalReference() {
    return GenericUploadRequest.builder()
        .orgId("NEXTUPLE")
        .filePath(FILE_PATH)
        .additionalReference(Map.of(COST_TYPE, COST_TYPE1))
        .build();
  }

  public CostTypeValidationResponse getCostTypeValidationResponseForTable() {
    return CostTypeValidationResponse.builder()
        .currency("USD")
        .costType(COST_TYPE1)
        .displayName(COST_TYPE1)
        .selectorCf("carrierServiceId")
        .selectorCfDisplayName("Carrier service Id")
        .selectorCfInfo(
            List.of(
                SelectorCfInfo.builder()
                    .selectorCfValue("UPS-GROUND")
                    .displayName("UPS-GROUND")
                    .costItinerary("UPS-ITN")
                    .row(
                        CostFactorDescriptionDto.builder()
                            .costFactor("distances")
                            .displayName("Distances")
                            .values(List.of("10-20", "20-30", "30-40", "40-50"))
                            .build())
                    .costFactors(
                        List.of(
                            CostFactorDescriptionDto.builder()
                                .costFactor("surge")
                                .displayName("Surge")
                                .values(List.of("NON-HOLIDAY", "HOLIDAY"))
                                .build()))
                    .build()))
        .build();
  }

  public CostTypeValidationResponse getCostTypeValidationResponseForGrid() {
    return CostTypeValidationResponse.builder()
        .currency("USD")
        .costType(COST_TYPE1)
        .displayName(COST_TYPE1)
        .selectorCf("carrierServiceId")
        .selectorCfDisplayName("Carrier service Id")
        .selectorCfInfo(
            List.of(
                SelectorCfInfo.builder()
                    .selectorCfValue("UPS-GROUND")
                    .displayName("UPS-GROUND")
                    .costItinerary("UPS-ITN")
                    .row(
                        CostFactorDescriptionDto.builder()
                            .costFactor("distances")
                            .displayName("Distances")
                            .values(List.of("10-20", "20-30", "30-40", "40-50"))
                            .build())
                    .column(
                        CostFactorDescriptionDto.builder()
                            .costFactor("zones")
                            .displayName("Shipping Zones")
                            .values(List.of("Zone1", "Zone2", "Zone3"))
                            .build())
                    .costFactors(
                        List.of(
                            CostFactorDescriptionDto.builder()
                                .costFactor("surge")
                                .displayName("Surge")
                                .values(List.of("NON-HOLIDAY", "HOLIDAY"))
                                .build()))
                    .build(),
                SelectorCfInfo.builder()
                    .selectorCfValue("")
                    .displayName("Default")
                    .costItinerary("UPS-DEFAULT-LIKE")
                    .row(
                        CostFactorDescriptionDto.builder()
                            .costFactor("distances")
                            .displayName("Distances")
                            .values(List.of("10-20", "20-30", "30-40", "40-50"))
                            .build())
                    .column(
                        CostFactorDescriptionDto.builder()
                            .costFactor("zones")
                            .displayName("Shipping Zones")
                            .values(List.of("Zone1", "Zone2", "Zone3"))
                            .build())
                    .costFactors(
                        List.of(
                            CostFactorDescriptionDto.builder()
                                .costFactor("surge")
                                .displayName("Surge")
                                .values(List.of("NON-HOLIDAY", "HOLIDAY"))
                                .build()))
                    .build()))
        .build();
  }

  public CostTypeValidationResponse getCostTypeValidationResponseForSingleCf() {
    return CostTypeValidationResponse.builder()
        .currency("USD")
        .costType(COST_TYPE1)
        .displayName(COST_TYPE1)
        .selectorCf("carrierServiceId")
        .selectorCfDisplayName("Carrier service Id")
        .selectorCfInfo(
            List.of(
                SelectorCfInfo.builder()
                    .selectorCfValue("UPS-GROUND")
                    .displayName("UPS-GROUND")
                    .costItinerary("UPS-ITN")
                    .row(
                        CostFactorDescriptionDto.builder()
                            .costFactor("distances")
                            .displayName("Distances")
                            .values(List.of("10-20"))
                            .build())
                    .costFactors(
                        List.of(
                            CostFactorDescriptionDto.builder()
                                .costFactor("surge")
                                .displayName("Surge")
                                .values(List.of("NON-HOLIDAY", "HOLIDAY"))
                                .build()))
                    .build()))
        .build();
  }

  public CostTypeValidationResponse getCostTypeValidationResponseForStaticTable() {
    return CostTypeValidationResponse.builder()
        .currency("USD")
        .costType(COST_TYPE1)
        .displayName(COST_TYPE1_DISPLAY_NAME)
        .selectorCf("carrierServiceId")
        .selectorCfDisplayName("Carrier service Id")
        .selectorCfInfo(
            List.of(
                SelectorCfInfo.builder()
                    .selectorCfValue("UPS-GROUND")
                    .costItinerary("UPS-ITN")
                    .costFactors(List.of())
                    .displayName("UPS-GROUND")
                    .build()))
        .build();
  }

  public CostTypeValidationResponse getCostTypeValidationResponseForStaticWithOutSCF() {
    return CostTypeValidationResponse.builder()
        .currency("USD")
        .costType(COST_TYPE1)
        .displayName(COST_TYPE1_DISPLAY_NAME)
        .selectorCf("")
        .costFactors(List.of())
        .selectorCfDisplayName("")
        .selectorCfInfo(List.of())
        .build();
  }

  public CostTypeValidationResponse getCostTypeValidationResponseForGridWithOutSCF() {
    return CostTypeValidationResponse.builder()
        .currency("USD")
        .costType(COST_TYPE1)
        .displayName(COST_TYPE1)
        .selectorCf("")
        .row(
            CostFactorDescriptionDto.builder()
                .costFactor("distances")
                .displayName("Distances")
                .values(List.of("10-20", "20-30", "30-40", "40-50"))
                .build())
        .column(
            CostFactorDescriptionDto.builder()
                .costFactor("zones")
                .displayName("Shipping Zones")
                .values(List.of("Zone1", "Zone2", "Zone3"))
                .build())
        .costFactors(
            List.of(
                CostFactorDescriptionDto.builder()
                    .costFactor("surge")
                    .displayName("Surge")
                    .values(List.of("NON-HOLIDAY", "HOLIDAY"))
                    .build()))
        .selectorCfDisplayName("")
        .selectorCfInfo(List.of())
        .build();
  }

  public CostTypeValidationResponse getCostTypeDetailsWithMultipleFilterCf() {
    return CostTypeValidationResponse.builder()
        .currency("USD")
        .costType(COST_TYPE1)
        .displayName(COST_TYPE1)
        .selectorCf("carrierServiceId")
        .selectorCfDisplayName("Carrier service Id")
        .selectorCfInfo(
            List.of(
                SelectorCfInfo.builder()
                    .selectorCfValue("UPS-GROUND")
                    .displayName("UPS-GROUND")
                    .costItinerary("UPS-ITN")
                    .row(
                        CostFactorDescriptionDto.builder()
                            .costFactor("distances")
                            .displayName("Distances")
                            .values(List.of("10-20", "20-30", "30-40", "40-50"))
                            .build())
                    .column(
                        CostFactorDescriptionDto.builder()
                            .costFactor("zones")
                            .displayName("Shipping Zones")
                            .values(List.of("Zone1", "Zone2", "Zone3"))
                            .build())
                    .costFactors(
                        List.of(
                            CostFactorDescriptionDto.builder()
                                .costFactor("surge")
                                .displayName("Surge")
                                .values(List.of("NON-HOLIDAY", "HOLIDAY"))
                                .build()))
                    .build(),
                SelectorCfInfo.builder()
                    .selectorCfValue("")
                    .displayName("Default")
                    .costItinerary("UPS-DEFAULT-LIKE")
                    .row(
                        CostFactorDescriptionDto.builder()
                            .costFactor("distances")
                            .displayName("Distances")
                            .values(List.of("10-20", "20-30", "30-40", "40-50"))
                            .build())
                    .column(
                        CostFactorDescriptionDto.builder()
                            .costFactor("zones")
                            .displayName("Shipping Zones")
                            .values(List.of("Zone1", "Zone2", "Zone3"))
                            .build())
                    .costFactors(
                        List.of(
                            CostFactorDescriptionDto.builder()
                                .costFactor("surge")
                                .displayName("Surge")
                                .values(List.of("NON-HOLIDAY", "HOLIDAY"))
                                .build(),
                            CostFactorDescriptionDto.builder()
                                .costFactor("isHazmat")
                                .displayName("Is Hazardous")
                                .values(List.of("TRUE", "FALSE"))
                                .build()))
                    .build()))
        .build();
  }
}
