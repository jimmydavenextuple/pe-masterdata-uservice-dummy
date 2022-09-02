package com.hbc.csvdownload.common;

import com.hbc.csvdownload.domain.pojo.ProcessingLeadTimesRaw;
import com.hbc.jobs.framework.common.domain.enums.JobStatusEnum;
import com.hbc.jobs.framework.common.domain.enums.JobTypeEnum;
import com.hbc.jobs.framework.common.domain.pojo.AuditLog;
import com.hbc.jobs.framework.common.domain.pojo.JobDto;
import com.hbc.transit.domain.outbound.TransitResponse;
import java.util.Collections;
import java.util.Date;
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
}
