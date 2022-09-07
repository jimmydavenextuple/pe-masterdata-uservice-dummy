package com.hbc.csvdownload.common;

import com.hbc.csvdownload.domain.pojo.ProcessingLeadTimesRaw;
import com.hbc.jobs.framework.common.domain.enums.JobStatusEnum;
import com.hbc.jobs.framework.common.domain.enums.JobTypeEnum;
import com.hbc.jobs.framework.common.domain.pojo.AuditLog;
import com.hbc.jobs.framework.common.domain.pojo.JobDto;
import com.hbc.jobs.framework.common.domain.pojo.RecordStatusDto;
import com.hbc.transit.domain.inbound.TransitDataCreationRequest;
import com.hbc.transit.domain.outbound.TransitResponse;
import java.util.ArrayList;
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
  public static final Double PROCESSING_TIME = 20.0;
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

  public List<RecordStatusDto> getJobRecordsForTransitTimes() {
    List<RecordStatusDto> recordStatusDtoList = new ArrayList<>();
    RecordStatusDto recordDto =
        RecordStatusDto.builder()
            .jobId(JOB_ID)
            .jobType(JobTypeEnum.UPLOAD_TRANSIT_TIMES)
            .orgId(ORG_ID)
            .errorMessage("Invalid nodeId")
            .requestBody(transitTimesRequestBodyJson)
            .build();

    recordStatusDtoList.add(recordDto);
    return recordStatusDtoList;
  }

  public ProcessingLeadTimesRaw getProcessingLeadTimesRaw() {
    ProcessingLeadTimesRaw processingLeadTimesRaw = new ProcessingLeadTimesRaw();
    processingLeadTimesRaw.setNodeId(NODE_ID);
    processingLeadTimesRaw.setOrgId(ORG_ID);
    processingLeadTimesRaw.setServiceOption(SERVICE_OPTION);
    processingLeadTimesRaw.setProcessingTime(PROCESSING_TIME);

    return processingLeadTimesRaw;
  }

  public JobDto createJob(JobTypeEnum jobTypeEnum, int totalRecords) {
    JobDto job = new JobDto();
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

  public TransitDataCreationRequest getTransitDataCreationRequest() {
    TransitDataCreationRequest transitDataCreationRequest = new TransitDataCreationRequest();
    transitDataCreationRequest.setOrgId(ORG_ID);
    transitDataCreationRequest.setSourceGeozone("SSFA");
    transitDataCreationRequest.setDestinationGeozone("DSFA");
    transitDataCreationRequest.setCarrierServiceId("ALL-SDND");
    transitDataCreationRequest.setTransitDays(2F);
    return transitDataCreationRequest;
  }
}
