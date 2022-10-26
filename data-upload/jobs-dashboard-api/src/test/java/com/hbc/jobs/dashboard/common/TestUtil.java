package com.hbc.jobs.dashboard.common;

import com.hbc.common.base.PagePayload;
import com.hbc.csvdownload.common.pojo.TransitDataUpload;
import com.hbc.csvdownload.domain.pojo.ProcessingLeadTimesRaw;
import com.hbc.jobs.consumers.domain.entity.JobEntity;
import com.hbc.jobs.consumers.domain.mapper.JobMapper;
import com.hbc.jobs.framework.common.domain.enums.ApiStatusEnum;
import com.hbc.jobs.framework.common.domain.enums.JobStatusEnum;
import com.hbc.jobs.framework.common.domain.enums.JobTypeEnum;
import com.hbc.jobs.framework.common.domain.outbound.FileMetaDataResponse;
import com.hbc.jobs.framework.common.domain.outbound.FileResponse;
import com.hbc.jobs.framework.common.domain.outbound.JobResponse;
import com.hbc.jobs.framework.common.domain.pojo.AuditLog;
import com.hbc.jobs.framework.common.domain.pojo.JobDto;
import com.hbc.jobs.framework.common.domain.pojo.JobFilters;
import com.hbc.jobs.framework.common.domain.pojo.Metadata;
import com.hbc.jobs.framework.common.domain.pojo.RecordStatusDto;
import com.hbc.jobs.framework.common.domain.pojo.TransitBufferUpload;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
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

  public static final String CSV_CONTENTS_PROCESSING_LEAD_TIMES =
      "nodeId,orgId,serviceOptions,processingTime (in hrs),action\n"
          + "1554,BAY,SDND,2,U\n"
          + "1560,BAY,SDND,2,D\n"
          + "1101,BAY,SDND,2,U";

  public static final String CSV_CONTENTS_TRANSIT_BUFFER_REQUEST =
      "orgId,carrierServiceId,sourceGeozone,destinationGeozone,bufferDays,bufferStartDate,bufferEndDate,action,createdBy\n"
          + "BAY,ALL-SDND,B1P,T0A,1,2022-10-01T17:30:00Z,2022-11-10T17:30:00Z,U,abc\n"
          + "BAY,ALL-EXPRESS,B3A,T0A,2,2022-08-01T17:30:00Z,2022-08-11T01:30:00Z,D,def\n"
          + "BAY,ALL-EXPRESS,B3B,T0C,1,2022-10-01T17:30:00Z,2022-11-10T17:30:00Z,C,ghi\n"
          + "BAY,ALL-EXPRESS,B4A,T0A,1,2022-08-01T17:30:00Z,2022-08-11T01:30:00Z,U,abc\n"
          + "BAY,ALL-STANDARD,B4A,T0C,1,2022-10-01T17:30:00Z,2022-11-10T17:30:00Z,D,def\n"
          + "BAY,ALL-STANDARD,B1P,T0A,1,2022-08-01T17:30:00Z,2022-08-11T01:30:00Z,C,klm";

  public static final String CSV_CONTENTS_POSTAL_CODE_TIMEZONE =
      "action,orgId,postalCodePrefix,country,state,city,latitude,longitude,timeZone\n"
          + "CREATE,BAY,A0A,CA,NL,Witless Bay,47.545965,-53.138234, America/St_Johns\n"
          + "UPDATE,BAY,A0B,CA,NL,Witless Bay1,47.545975,-53.138244, America/St_Johns\n"
          + "CREATE,BAY,A0C,CA,NL,Witless Bay3,47.545985,-53.138254, America/St_Johns\n"
          + "DELETE,BAY,A0D,CA,NL,Witless Bay4,47.545995,-53.138264, America/St_Johns";

  public static final String CSV_CONTENTS_TRANSIT_TIMES =
      "orgId,BAY,,,,,,,,,\n"
          + "Carrier Service:,ALL-Standard,,,,,,,,,\n"
          + "Destination FSA / Source FSA ->,SFSA1,SFSA2,SFSA3\n"
          + "DFSA1,,9.96,9.96\n"
          + "DFSA2,D,9,9.9\n"
          + "DFSA3,10,9,9\n"
          + "\n";

  public static final String CSV_CONTENTS_DELETE_TRANSIT_BUFFER =
      "orgId,BAY,,,,,,,,,\n"
          + "Carrier Service:,ALL-Standard,,,,,,,,,\n"
          + "Destination FSA / Source FSA ->,SFSA1,SFSA2,SFSA3\n"
          + "DFSA1,D,D.D,D\n"
          + "DFSA2,D,D,D\n"
          + "DFSA3,D,D,D\n"
          + "\n";

  public static final String CSV_CONTENTS_UPLOAD_NODE_CALENDAR =
      "action,calendarId,nodeId,orgId,description,effectiveDate\n"
          + "CREATE,CGY_2022,1957,BAY,Calgary MFC - non peak,2022-02-02\n"
          + "CREATE,SLC_2022,DC-963-565,BAY,SLC - non peak,2022-01-01\n"
          + "CREATE,VLC_2022,DC-963-577,BAY,VLC - non peak, 2022-01-03";

  public static final String CSV_CONTENTS_UPLOAD_NODE =
      "action,nodeId,orgId,street,city,province,postalCode,country,latitude,longitude,timezone,shipToHome,sdndEligible,bopisEligible,expressEligible,nodeType,isActive,nextdayEligible\n"
          + "CREATE,node,org,street0,city0,province0,postalCode0,country0,latitude0,longitude0,timezone0,true,true,false,true,nodeType0,true,true\n"
          + "UPDATE,node,org,street1,city1,province1,postalCode1,country1,latitude1,longitude1,timezone1,true,true,false,true,nodeType1,true,true\n"
          + "DELETE,node,org,street2,city2,province2,postalCode2,country2,latitude2,longitude2,timezone2,true,true,false,true,nodeType2,true,true";

  public static final String CSV_CONTENTS_UPLOAD_NODE_CARRIER =
      "action,nodeId,orgId,carrierServiceId,serviceOption,lastPickupTime\n"
          + "  CREATE,1,BAY,ALL-EXPRESS,EXPRESS,20:00\n"
          + "  UPDATE,1,BAY,ALL-EXPRESS,EXPRESS,10:00\n"
          + "  DELETE,12,BAY,ALL-EXPRESS,EXPRESS,20:00";

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
    job.setJobType(jobTypeEnum);
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
}
