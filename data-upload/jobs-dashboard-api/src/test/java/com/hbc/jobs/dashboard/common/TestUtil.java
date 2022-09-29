package com.hbc.jobs.dashboard.common;

import com.hbc.common.base.PagePayload;
import com.hbc.jobs.consumers.domain.entity.JobEntity;
import com.hbc.jobs.consumers.domain.mapper.JobMapper;
import com.hbc.jobs.framework.common.domain.enums.ApiStatusEnum;
import com.hbc.jobs.framework.common.domain.enums.JobStatusEnum;
import com.hbc.jobs.framework.common.domain.enums.JobTypeEnum;
import com.hbc.jobs.framework.common.domain.outbound.JobResponse;
import com.hbc.jobs.framework.common.domain.pojo.AuditLog;
import com.hbc.jobs.framework.common.domain.pojo.JobDto;
import com.hbc.jobs.framework.common.domain.pojo.JobFilters;
import com.hbc.jobs.framework.common.domain.pojo.Metadata;
import com.hbc.jobs.framework.common.domain.pojo.RecordStatusDto;
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

  public static final String CSV_CONTENTS_DELETE_TRANSIT_BUFFER =
      "orgId,BAY,,,,,,,,,\n"
          + "Carrier Service:,ALL-Standard,,,,,,,,,\n"
          + "Destination FSA / Source FSA ->,SFSA1,SFSA2,SFSA3\n"
          + "DFSA1,D,D.D,D\n"
          + "DFSA2,D,D,D\n"
          + "DFSA3,D,D,D\n";

  public static final Optional<String> DEFAULT_SORT_FIELD = Optional.of("created_date");

  public static final Optional<String> DEFAULT_SORT_ORDER = Optional.of("ASC");

  public static final Optional<Integer> DEFAULT_PAGE_NO = Optional.of(1);

  public static final Optional<Integer> DEFAULT_PAGE_SIZE = Optional.of(15);

  public JobDto createJob(
      String jobId,
      String orgId,
      JobStatusEnum status,
      List<AuditLog> auditLogs,
      JobTypeEnum jobTypeEnum) {
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
    return job;
  }

  public JobEntity createJobEntity(
      String jobId,
      String orgId,
      JobStatusEnum jobStatus,
      List<AuditLog> auditLog,
      JobTypeEnum jobType) {
    return JobMapper.INSTANCE.toJobEntity(createJob(jobId, orgId, jobStatus, auditLog, jobType));
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
            "JobId1", orgId, JobStatusEnum.SUBMITTED, auditLogList1, JobTypeEnum.valueOf(jobType));
    JobDto jobId2 =
        createJob(
            "JobId2", orgId, JobStatusEnum.RUNNING, auditLogList2, JobTypeEnum.valueOf(jobType));
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
}
