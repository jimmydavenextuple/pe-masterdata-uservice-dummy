package com.hbc.csvdownload.util;

import com.hbc.common.base.PagePayload;
import com.hbc.csvdownload.common.inbound.GenericUploadRequest;
import com.hbc.csvdownload.common.outbound.GenericUploadResponse;
import com.hbc.jobs.framework.common.domain.enums.JobStatusEnum;
import com.hbc.jobs.framework.common.domain.enums.JobTypeEnum;
import com.hbc.jobs.framework.common.domain.outbound.FileMetaDataResponse;
import com.hbc.jobs.framework.common.domain.outbound.FileResponse;
import com.hbc.jobs.framework.common.domain.outbound.JobResponse;
import com.hbc.jobs.framework.common.domain.outbound.PreSignedUrlResponse;
import com.hbc.jobs.framework.common.domain.pojo.AuditLog;
import com.hbc.jobs.framework.common.domain.pojo.JobDto;
import com.hbc.jobs.framework.common.domain.pojo.RecordStatusDto;
import java.io.InputStream;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class TestUtil {

  public static final String ORG_ID = "BAY";

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
      "{\"action\":\"CREATE\",\"postalCodePrefix\":\"DDD\",\"orgId\":\"BAY\",\n"
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
          + "    \"province\": \"ON\",\n"
          + "    \"postalCode\": \"M1R 5A2\",\n"
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
          + "    \"isActive\": true\n"
          + "}";

  public static final String nodeRequestBodyJson1 =
      "{\n"
          + "    \"nodeId\": \"NXTHBC01\",\n"
          + "    \"orgId\": \"NXT\",\n"
          + "    \"street\": \"100 Metropolitan Rd.\",\n"
          + "    \"city\": \"Scarborough\",\n"
          + "    \"province\": \"ON\",\n"
          + "    \"postalCode\": \"M1R 5A2\",\n"
          + "    \"country\": \"Canada\",\n"
          + "    \"latitude\": \"43.769912\",\n"
          + "    \"longitude\": \"-79.296678\",\n"
          + "    \"timezone\": \"America/Toronto\",\n"
          + "    \"shipToHome\": true,\n"
          + "    \"bopisEligible\": true,\n"
          + "    \"serviceOptionEligibilities\": {\n"
          + "    },\n"
          + "    \"nodeType\": \"MFC\",\n"
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
}
