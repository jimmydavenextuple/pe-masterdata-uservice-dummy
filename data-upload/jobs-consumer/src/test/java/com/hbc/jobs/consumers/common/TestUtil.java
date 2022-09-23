package com.hbc.jobs.consumers.common;

import com.hbc.csvdownload.common.pojo.ProcessingLeadTime;
import com.hbc.csvdownload.common.pojo.TransitDataUpload;
import com.hbc.jobs.consumers.domain.entity.JobEntity;
import com.hbc.jobs.consumers.domain.entity.JobRecordEntity;
import com.hbc.jobs.consumers.domain.mapper.JobMapper;
import com.hbc.jobs.consumers.domain.mapper.JobRecordMapper;
import com.hbc.jobs.framework.common.domain.enums.ApiStatusEnum;
import com.hbc.jobs.framework.common.domain.enums.JobStatusEnum;
import com.hbc.jobs.framework.common.domain.enums.JobTypeEnum;
import com.hbc.jobs.framework.common.domain.enums.RecordDataTypeEnum;
import com.hbc.jobs.framework.common.domain.pojo.AuditLog;
import com.hbc.jobs.framework.common.domain.pojo.JobDto;
import com.hbc.jobs.framework.common.domain.pojo.JobFilters;
import com.hbc.jobs.framework.common.domain.pojo.RecordDto;
import com.hbc.jobs.framework.common.domain.pojo.RecordInputDto;
import com.hbc.jobs.framework.common.domain.pojo.RecordStatusDto;
import com.hbc.node.carrier.domain.inbound.NodeCarrierRequest;
import com.hbc.node.carrier.domain.outbound.NodeCarrierResponse;
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
  public static final String JOB_TYPE = JobTypeEnum.UPLOAD_PROCESSING_LEAD_TIMES.name();
  public static final String JOB_ID = "JobId1";

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

  public List<JobDto> createJobDtoList() {
    JobDto job1 = createJob(JobTypeEnum.UPLOAD_PROCESSING_LEAD_TIMES, 2);
    JobDto job2 = createJob(JobTypeEnum.UPLOAD_TRANSIT_TIMES, 5);
    return Arrays.asList(job1, job2);
  }

  public Page<JobDto> createPageJobDto(int totalPage, List<JobDto> jobDtos, int totalElements) {

    return (Page<JobDto>)
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

    AuditLog auditLog = new AuditLog();
    auditLog.setStatus(JobStatusEnum.SUBMITTED);
    auditLog.setTimeStamp(new Date());
    job.setAuditLog(Collections.singletonList(auditLog));
    return job;
  }

  public JobEntity createJobEntity(JobTypeEnum jobTypeEnum, int totalRecords) {
    return JobMapper.INSTANCE.toJobEntity(createJob(jobTypeEnum, totalRecords));
  }

  public RecordDto createRecordDto(
      JobDto jobDto,
      String record,
      int recordId,
      RecordDataTypeEnum recordDataTypeEnum,
      RecordInputDto inputDto) {
    RecordDto recordDto = new RecordDto();
    recordDto.setJob(jobDto);
    recordDto.setInputs(inputDto);
    recordDto.setRecordData(record);
    recordDto.setRecordId(recordId);
    recordDto.setRecordType(recordDataTypeEnum);
    return recordDto;
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

  public ProcessingLeadTime getProcessingLeadTime(String action) {
    ProcessingLeadTime processingLeadTime = new ProcessingLeadTime();
    processingLeadTime.setNodeId("node-1");
    processingLeadTime.setOrgId(ORG_ID);
    processingLeadTime.setServiceOption("SDND");
    processingLeadTime.setCarrierServiceId("");
    processingLeadTime.setProcessingTime(30.92);
    processingLeadTime.setActionType(action);
    return processingLeadTime;
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

  public TransitDataUpload getTransitDataUpload(String action) {
    TransitDataUpload transitDataUpload = new TransitDataUpload();
    transitDataUpload.setOrgId(ORG_ID);
    transitDataUpload.setSourceGeozone("SSFA");
    transitDataUpload.setDestinationGeozone("DSFA");
    transitDataUpload.setCarrierServiceId("ALL-SDND");
    transitDataUpload.setTransitDays(2F);
    transitDataUpload.setActionType(action);
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
}
