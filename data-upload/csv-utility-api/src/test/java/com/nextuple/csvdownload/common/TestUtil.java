package com.nextuple.csvdownload.common;

import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.CALENDAR_ID;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.EFFECTIVE_DATE;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.LAST_PICKUP_TIME;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.LATITUDE;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.LONGITUDE;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.NODE_TYPE;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.POSTAL_CODE_PREFIX;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.SERVICE_NAME;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.STATE;
import static org.junit.jupiter.api.parallel.Resources.TIME_ZONE;

import com.nextuple.calendar.domain.outbound.CarrierServiceCalendarResponse;
import com.nextuple.calendar.domain.outbound.NodeCalendarResponse;
import com.nextuple.carrier.domain.outbound.CarrierServiceResponse;
import com.nextuple.common.base.PagePayload;
import com.nextuple.common.base.PagePayload.Pagination;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.csvdownload.domain.pojo.DownloadErrorTransitData;
import com.nextuple.csvdownload.domain.pojo.ProcessingLeadTimesRaw;
import com.nextuple.dataupload.common.outbound.NodeCarrierServiceAndServiceOptionResponse;
import com.nextuple.dataupload.common.outbound.ProcessingTimeBufferResponse;
import com.nextuple.dataupload.common.pojo.ActiveCombination;
import com.nextuple.dataupload.common.pojo.ProcessingTimeBuffer;
import com.nextuple.jobs.framework.common.domain.enums.JobStatusEnum;
import com.nextuple.jobs.framework.common.domain.enums.JobTypeEnum;
import com.nextuple.jobs.framework.common.domain.outbound.FileResponse;
import com.nextuple.jobs.framework.common.domain.outbound.JobResponse;
import com.nextuple.jobs.framework.common.domain.outbound.PreSignedUrlResponse;
import com.nextuple.jobs.framework.common.domain.pojo.AuditLog;
import com.nextuple.jobs.framework.common.domain.pojo.JobDto;
import com.nextuple.jobs.framework.common.domain.pojo.RecordStatusDto;
import com.nextuple.node.carrier.domain.outbound.NodeCarrierResponse;
import com.nextuple.node.domain.dto.NodeDto;
import com.nextuple.postal.code.timezone.api.domain.dto.PostalCodeTimezoneDto;
import com.nextuple.promise.common.domain.Item;
import com.nextuple.promise.common.domain.SfccErrorResponse;
import com.nextuple.promise.common.domain.SfccErrorResponseLine;
import com.nextuple.promise.common.domain.SfccOrder;
import com.nextuple.promise.common.domain.SfccOrderLine;
import com.nextuple.promise.common.domain.SfccPromiseDetails;
import com.nextuple.promise.common.domain.SfccResponse;
import com.nextuple.promise.common.domain.SfccResponseLine;
import com.nextuple.promise.common.domain.SfccSuggestedPromiseOption;
import com.nextuple.promise.common.domain.SfccSuggestedPromiseOptionError;
import com.nextuple.promise.common.domain.ShipToAddress;
import com.nextuple.transit.domain.dto.TransitTimeEntriesDto;
import com.nextuple.transit.domain.enums.TransitBufferConfigRequestStatusEnum;
import com.nextuple.transit.domain.inbound.TransitBufferConfigRequest;
import com.nextuple.transit.domain.outbound.TransitBufferConfigResponse;
import com.nextuple.transit.domain.outbound.TransitResponse;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class TestUtil {

  public static final String ORG_ID = "BAY";
  public static final String CARRIER_SERVICE_ID = "ALL_SDND";
  public static final String CARRIER_ID = "01";

  public static final String SOURCE_REGION = "ON";
  public static final String DESTINATION_REGION = "DEL";
  public static final String SOURCE_FSA = "A0A";
  public static final String DESTINATION_FSA = "M1R";
  public static final String NODE_ID = "nodeId";
  public static final String JOB_ID = "jobId1";
  public static final String SERVICE_OPTION = "serviceOptions";
  public static final String COUNTRY = "CA";
  public static final String STREET = "1963 Boul. Lionel-Bertrand";
  public static final String CITY = "Boisbriand";
  public static final String PROVINCE = "QC";
  public static final String POSTAL_CODE = "J7H 1N8";
  public static final Double PROCESSING_TIME = 20.0;
  public static final String NODE_ID_2 = "nodeId2";
  private static final String SERVICE_OPTION_2 = "EXPRESS";
  public static final String templateType = "nodeCarrier";

  public static final String templateTypeInvalid = "invalid";

  public static final String invalidTemplateTypeErrMsg = "Invalid template type";

  public static final String FILE_PATH =
      "promise-s3-lambda-dev/ui/node-carrier/2022-10-21/market-region.csv";
  public static final long FILE_METADATA_ID = 1L;
  public static final String BUCKET_NAME = "promise-s3-lambda-dev";
  public static final String FILE_NAME = "postalCodeTimezone.csv";
  public static final String CONTENT_TYPE = "text/csv";
  public static final Long CONTENT_LENGTH = 100L;
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

  public static final String nodeCarrierCsvData =
      "orgId,carrierId,carrierName,carrierServiceId,serviceName,serviceOptions"
          + "\n"
          + "BAY,GoFor,GoFor,GoFor-SDND,GoFor,SDND\n"
          + "BAY,TFORCE,TForce,TFORCE-NEXTDAY,TForce NextDay Guaranteed,NEXTDAY\n"
          + "BAY,CanadaPost,Canada Post,CanadaPost-STANDARD,Canada Post Expedited Parcel,STANDARD\n"
          + "BAY,CanadaPost,Canada Post,CanadaPost-EXPRESS,Canada Post Xpresspost,EXPRESS\n"
          + "BAY,UPSN,UPS,UPSN-STANDARD,UPS Standard,STANDARD";

  public static final String eddComputationData =
      "organizationCode,sessionId,basketId,pageName,shipToAddress_zipCode,shipToAddress_province,lines_item_itemId,lines_item_itemType,lines_item_unitOfMeasure,lines_item_seller,lines_lineId,lines_requiredQty,lines_shipToAddress_zipCode,lines_shipToAddress_province"
          + "\n"
          + "BAY,jkfdsj5748fdgf58gfh,156465897,Basket/Checkout,V0A 1B5,BC,12340008,EVERYDAY,EACH,HBC,1,5,V0A 1B5,BC,\n"
          + ",,,,,,123400081,EVERYDAY,EACH,HBC,2,5,T2P 1B5,BC,\n"
          + "BAY,dsffsdsfdsgffdvddsf,156465823,Basket/Checkout,V0B 1B5,BC,1234000888,EVERYDAY,EACH,HBC,1,5,V0A 1B5,BC,\n"
          + ",,,,,,1234000819,EVERYDAY,EACH,HBC,2,5,T2P 1B5,BC,";

  public static final String eddComputationDataWith4Orders =
      "organizationCode,sessionId,basketId,pageName,shipToAddress_zipCode,shipToAddress_province,lines_item_itemId,lines_item_itemType,lines_item_unitOfMeasure,lines_item_seller,lines_lineId,lines_requiredQty,lines_shipToAddress_zipCode,lines_shipToAddress_province"
          + "\n"
          + "BAY,jkfdsj5748fdgf58gfh,156465897,Basket/Checkout,V0A 1B5,BC,12340008,EVERYDAY,EACH,HBC,1,5,V0A 1B5,BC,\n"
          + ",,,,,,123400081,EVERYDAY,EACH,HBC,2,5,T2P 1B5,BC,\n"
          + "BAY,dsffsdsfdsgffdvddsf,156465823,Basket/Checkout,V0B 1B5,BC,1234000888,EVERYDAY,EACH,HBC,1,5,V0A 1B5,BC,\n"
          + ",,,,,,1234000819,EVERYDAY,EACH,HBC,2,5,T2P 1B5,BC,\n"
          + "BAY,jkfdsj5748fdgf58gfh,156465897,Basket/Checkout,V0A 1B5,BC,12340008,EVERYDAY,EACH,HBC,1,5,V0A 1B5,BC,\n"
          + ",,,,,,123400081,EVERYDAY,EACH,HBC,2,5,T2P 1B5,BC,\n"
          + "BAY,dsffsdsfdsgffdvddsf,156465823,Basket/Checkout,V0B 1B5,BC,1234000888,EVERYDAY,EACH,HBC,1,5,V0A 1B5,BC,\n"
          + ",,,,,,1234000819,EVERYDAY,EACH,HBC,2,5,T2P 1B5,BC,";

  public static final String eddComputationDataWith6Orders =
      "organizationCode,sessionId,basketId,pageName,shipToAddress_zipCode,shipToAddress_province,lines_item_itemId,lines_item_itemType,lines_item_unitOfMeasure,lines_item_seller,lines_lineId,lines_requiredQty,lines_shipToAddress_zipCode,lines_shipToAddress_province"
          + "\n"
          + "BAY,jkfdsj5748fdgf58gfh,156465897,Basket/Checkout,V0A 1B5,BC,12340008,EVERYDAY,EACH,HBC,1,5,V0A 1B5,BC,\n"
          + ",,,,,,123400081,EVERYDAY,EACH,HBC,2,5,T2P 1B5,BC,\n"
          + "BAY,jkfdsj5748fdgf58gfh,156465897,Basket/Checkout,V0A 1B5,BC,12340008,EVERYDAY,EACH,HBC,1,5,V0A 1B5,BC,\n"
          + ",,,,,,123400081,EVERYDAY,EACH,HBC,2,5,T2P 1B5,BC,\n"
          + "BAY,jkfdsj5748fdgf58gfh,156465897,Basket/Checkout,V0A 1B5,BC,12340008,EVERYDAY,EACH,HBC,1,5,V0A 1B5,BC,\n"
          + ",,,,,,123400081,EVERYDAY,EACH,HBC,2,5,T2P 1B5,BC,\n"
          + "BAY,dsffsdsfdsgffdvddsf,156465823,Basket/Checkout,V0B 1B5,BC,1234000888,EVERYDAY,EACH,HBC,1,5,V0A 1B5,BC,\n"
          + ",,,,,,1234000819,EVERYDAY,EACH,HBC,2,5,T2P 1B5,BC,\n"
          + "BAY,jkfdsj5748fdgf58gfh,156465897,Basket/Checkout,V0A 1B5,BC,12340008,EVERYDAY,EACH,HBC,1,5,V0A 1B5,BC,\n"
          + ",,,,,,123400081,EVERYDAY,EACH,HBC,2,5,T2P 1B5,BC,\n"
          + "BAY,dsffsdsfdsgffdvddsf,156465823,Basket/Checkout,V0B 1B5,BC,1234000888,EVERYDAY,EACH,HBC,1,5,V0A 1B5,BC,\n"
          + ",,,,,,1234000819,EVERYDAY,EACH,HBC,2,5,T2P 1B5,BC,";

  public static final String eddComputationDataWith15Lines =
      "organizationCode,sessionId,basketId,pageName,shipToAddress_zipCode,shipToAddress_province,lines_item_itemId,lines_item_itemType,lines_item_unitOfMeasure,lines_item_seller,lines_lineId,lines_requiredQty,lines_shipToAddress_zipCode,lines_shipToAddress_province"
          + "\n"
          + "BAY,jkfdsj5748fdgf58gfh,156465897,Basket/Checkout,V0A 1B5,BC,12340008,EVERYDAY,EACH,HBC,1,5,V0A 1B5,BC,\n"
          + ",,,,,,123400081,EVERYDAY,EACH,HBC,2,5,T2P 1B5,BC,\n"
          + ",,,,,,123400081,EVERYDAY,EACH,HBC,2,5,T2P 1B5,BC,\n"
          + ",,,,,,123400081,EVERYDAY,EACH,HBC,2,5,T2P 1B5,BC,\n"
          + ",,,,,,1234000819,EVERYDAY,EACH,HBC,2,5,T2P 1B5,BC,\n"
          + ",,,,,,123400081,EVERYDAY,EACH,HBC,2,5,T2P 1B5,BC,\n"
          + ",,,,,,123400081,EVERYDAY,EACH,HBC,2,5,T2P 1B5,BC,\n"
          + ",,,,,,123400081,EVERYDAY,EACH,HBC,2,5,T2P 1B5,BC,\n"
          + ",,,,,,123400081,EVERYDAY,EACH,HBC,2,5,T2P 1B5,BC,\n"
          + ",,,,,,123400081,EVERYDAY,EACH,HBC,2,5,T2P 1B5,BC,\n"
          + ",,,,,,123400081,EVERYDAY,EACH,HBC,2,5,T2P 1B5,BC,\n"
          + ",,,,,,123400081,EVERYDAY,EACH,HBC,2,5,T2P 1B5,BC,\n"
          + ",,,,,,123400081,EVERYDAY,EACH,HBC,2,5,T2P 1B5,BC,\n"
          + ",,,,,,123400081,EVERYDAY,EACH,HBC,2,5,T2P 1B5,BC,\n"
          + ",,,,,,1234000819,EVERYDAY,EACH,HBC,2,5,T2P 1B5,BC,";

  public static final Optional<String> STATUS = Optional.empty();

  public static final JobTypeEnum jobType = JobTypeEnum.getTypeFromString("any");
  public static final String processingLeadTimesRequestBodyJson =
      "{\"nodeId\":\"1554\",\"orgId\":\"BAY\",\"carrierServiceId\":\"ALL-SDND\",\"serviceOption\":\"SDND\",\"processingTime\":2.0,\"lastPickupTime\":\"00:00\"}";
  public static final String transitTimesRequestBodyJson =
      "{\"orgId\":\"BAY\",\"sourceGeozone\":\"A0A\",\"destinationGeozone\":\"M1R\",\"carrierServiceId\":\"ALL-SDND\",\"transitDays\":\"1.5\"}";

  public static final String nodeRequestBodyJson =
      "{\"nodeId\":\"1554\",\"orgId\":\"BAY\""
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
          + "        \"nextdayEligible\": true\n"
          + "    },\n"
          + "    \"nodeType\": \"MFC\",\n"
          + "    \"isActive\": true\n"
          + "}";

  public static final String nodeCarrierRequestBodyJson =
      "{\n"
          + "    \"nodeId\": \"NXTHBC01\",\n"
          + "    \"orgId\": \"NXT\",\n"
          + "    \"carrierServiceId\": \"A_STD\",\n"
          + "    \"serviceOption\": \"Standard\",\n"
          + "    \"processingTime\": 20.2,\n"
          + "    \"lastPickupTime\": \"19:00\"\n"
          + "}";
  public static final String nodeCalendarRequestBodyJson =
      "{\n"
          + "    \"calendarId\": \"test_2\",\n"
          + "    \"orgId\": \"BAY\",\n"
          + "    \"nodeId\": \"DC-963-565\",\n"
          + "    \"effectiveDate\": \"2022-07-24\",\n"
          + "    \"description\": \"Monthly\"\n"
          + "}";

  public static final String calendarRequestBodyJson =
      "{\"action\":\"CREATE\","
          + "\"orgId\":\"BAY\","
          + "\"calendarId\":\"DSV_2022\","
          + "\"description\":\"DSVs Operations Calendar\","
          + "\"isFridayWorking\":true,"
          + "\"isMondayWorking\":true,"
          + "\"isTuesdayWorking\":true,"
          + "\"isSundayWorking\":false,"
          + "\"isWednesdayWorking\":true,"
          + "\"isSaturdayWorking\":false,"
          + "\"isThursdayWorking\":true,"
          + "\"exceptionDays\":[{\"date\":\"2022-01-01\",\"reason\":\"New Year's Day\"},{\"date\":\"2022-02-21\",\"reason\":\"Family Day\"},{\"date\":\"2022-04-15\",\"reason\":\"Good Friday\"},{\"date\":\"2022-04-18\",\"reason\":\"Easter Monday\"},{\"date\":\"2022-05-23\",\"reason\":\"Victoria Day\"},{\"date\":\"2022-07-01\",\"reason\":\"Canada Day\"},{\"date\":\"2022-08-01\",\"reason\":\"Civid Holiday\"},{\"date\":\"2022-09-05\",\"reason\":\"Labour Day\"},{\"date\":\"2022-10-10\",\"reason\":\"Thanksgiving\"},{\"date\":\"2022-11-11\",\"reason\":\"Remembrance Day\"},{\"date\":\"2022-12-25\",\"reason\":\"Christmas Day\"}]"
          + "}";

  public static final String calendarEmptyExceptionDaysRequestBodyJson =
      "{\"action\":\"CREATE\","
          + "\"orgId\":\"BAY\","
          + "\"calendarId\":\"DSV_2022\","
          + "\"description\":\"DSVs Operations Calendar\","
          + "\"isFridayWorking\":true,"
          + "\"isMondayWorking\":true,"
          + "\"isTuesdayWorking\":true,"
          + "\"isSundayWorking\":false,"
          + "\"isWednesdayWorking\":true,"
          + "\"isSaturdayWorking\":false,"
          + "\"isThursdayWorking\":true,"
          + "\"exceptionDays\":[]}";

  public static final String carrierServiceRequestBodyJson =
      "{\n"
          + "    \"orgId\":\"BAY\",\n"
          + "    \"carrierId\":\"A_STD\",\n"
          + "    \"carrierServiceId\":\"ALL-STANDARD\",\n"
          + "    \"carrierName\":\"ALL\",\n"
          + "    \"serviceName\":\"service-1-name\",\n"
          + "    \"serviceOptions\":\"Standard\"\n"
          + "}";
  public static final String carrierServiceCalendarRequestBodyJson =
      "{\n"
          + "    \"calendarId\": \"test_2\",\n"
          + "    \"orgId\": \"BAY\",\n"
          + "    \"carrierServiceId\": \"ALL-SDND\",\n"
          + "    \"shippingStage\": \"ALL\",\n"
          + "    \"effectiveDate\": \"2022-08-04\",\n"
          + "    \"description\": \"Monthly\"\n"
          + "}";

  public static final String postalCodeTimezoneRequestBodyJson =
      "{\n"
          + "    \"calendarId\": \"test_2\",\n"
          + "    \"orgId\": \"BAY\",\n"
          + "    \"carrierServiceId\": \"ALL-SDND\",\n"
          + "    \"shippingStage\": \"ALL\",\n"
          + "    \"effectiveDate\": \"2022-08-04\",\n"
          + "    \"description\": \"Monthly\"\n"
          + "}";

  public static Double BUFFER_DAYS = 3.0;

  public static final String STORAGE_TYPE = "S3";
  public static final String FILE_PATH_WITH_BUCKET_NAME =
      "promise-s3-lambda-dev/ui/transit-buffer/2022-10-18/fsa_upload..csv";

  public static final String CREATED_BY = "createdBy";

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

  public RecordStatusDto getJobRecordsForTransitTimes() {

    return RecordStatusDto.builder()
        .jobId(JOB_ID)
        .jobType(JobTypeEnum.UPLOAD_TRANSIT_TIMES)
        .orgId(ORG_ID)
        .errorMessage("Invalid nodeId")
        .requestBody(transitTimesRequestBodyJson)
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

  public RecordStatusDto getJobRecordsForNodeCarrier() {

    return RecordStatusDto.builder()
        .jobId(JOB_ID)
        .jobType(JobTypeEnum.UPLOAD_NODE_CARRIER)
        .orgId(ORG_ID)
        .errorMessage("Invalid nodeId")
        .requestBody(nodeCarrierRequestBodyJson)
        .build();
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

  public RecordStatusDto getJobRecordsForCalendar() {

    return RecordStatusDto.builder()
        .jobId(JOB_ID)
        .jobType(JobTypeEnum.UPLOAD_CALENDER)
        .orgId(ORG_ID)
        .errorMessage("Invalid nodeId")
        .requestBody(calendarRequestBodyJson)
        .build();
  }

  public RecordStatusDto getJobRecordsForCalendarWithEmptyExceptionDays() {

    return RecordStatusDto.builder()
        .jobId(JOB_ID)
        .jobType(JobTypeEnum.UPLOAD_CALENDER)
        .orgId(ORG_ID)
        .errorMessage("Invalid nodeId")
        .requestBody(calendarEmptyExceptionDaysRequestBodyJson)
        .build();
  }

  public RecordStatusDto getJobRecordsForCarrierService() {

    return RecordStatusDto.builder()
        .jobId(JOB_ID)
        .jobType(JobTypeEnum.UPLOAD_CARRIER_SERVICE)
        .orgId(ORG_ID)
        .errorMessage("Invalid nodeId")
        .requestBody(carrierServiceRequestBodyJson)
        .build();
  }

  public RecordStatusDto getJobRecordsForCarrierServiceCalendar() {

    return RecordStatusDto.builder()
        .jobId(JOB_ID)
        .jobType(JobTypeEnum.UPLOAD_CARRIER_SERVICE_CALENDER)
        .orgId(ORG_ID)
        .errorMessage("Invalid nodeId")
        .requestBody(carrierServiceCalendarRequestBodyJson)
        .build();
  }

  public RecordStatusDto getJobRecordsForPostalCodeTimezone() {

    return RecordStatusDto.builder()
        .jobId(JOB_ID)
        .jobType(JobTypeEnum.UPLOAD_POSTAL_CODE_TIMEZONE)
        .orgId(ORG_ID)
        .errorMessage("Invalid nodeId")
        .requestBody(postalCodeTimezoneRequestBodyJson)
        .build();
  }

  public ProcessingLeadTimesRaw getProcessingLeadTimesRaw() {
    ProcessingLeadTimesRaw processingLeadTimesRaw = new ProcessingLeadTimesRaw();
    processingLeadTimesRaw.setNodeId(NODE_ID);
    processingLeadTimesRaw.setOrgId(ORG_ID);
    processingLeadTimesRaw.setServiceOption(SERVICE_OPTION);
    processingLeadTimesRaw.setProcessingTime(String.valueOf(PROCESSING_TIME));

    return processingLeadTimesRaw;
  }

  public JobResponse createJobResponse(JobTypeEnum jobTypeEnum, int totalRecords) {
    JobResponse job = new JobResponse();
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

  public TransitTimeEntriesDto getTransitTimeEntriesDto() {
    return TransitTimeEntriesDto.builder()
        .orgId(ORG_ID)
        .carrierServiceId(CARRIER_SERVICE_ID)
        .totalRecords(2)
        .build();
  }

  public DownloadErrorTransitData getAddTransitDataRequest() {
    DownloadErrorTransitData downloadErrorTransitData = new DownloadErrorTransitData();
    downloadErrorTransitData.setOrgId(ORG_ID);
    downloadErrorTransitData.setSourceGeozone("SSFA");
    downloadErrorTransitData.setDestinationGeozone("DSFA");
    downloadErrorTransitData.setCarrierServiceId("ALL-SDND");
    downloadErrorTransitData.setTransitDays("2");
    return downloadErrorTransitData;
  }

  public PostalCodeTimezoneDto getPostalCodeTimezoneDto() {
    return PostalCodeTimezoneDto.builder()
        .orgId(ORG_ID)
        .postalCodePrefix(POSTAL_CODE_PREFIX)
        .country(COUNTRY)
        .state(STATE)
        .city(CITY)
        .latitude(LATITUDE)
        .longitude(LONGITUDE)
        .timeZone(TIME_ZONE)
        .build();
  }

  public CarrierServiceCalendarResponse getCarrierServiceCalendarResponse() {
    return CarrierServiceCalendarResponse.builder()
        .carrierServiceId(CARRIER_SERVICE_ID)
        .calendarId(CALENDAR_ID)
        .orgId(ORG_ID)
        .build();
  }

  public CarrierServiceResponse getCarrierServiceResponse() {
    return CarrierServiceResponse.builder()
        .carrierServiceId(CARRIER_SERVICE_ID)
        .carrierId(CARRIER_ID)
        .serviceName(SERVICE_NAME)
        .serviceOptions(SERVICE_OPTION)
        .build();
  }

  public ActiveCombination getActiveCombination() {
    return ActiveCombination.builder()
        .nodeId(NODE_ID)
        .carrierServiceId(CARRIER_SERVICE_ID)
        .serviceOption(SERVICE_OPTION)
        .isActive(true)
        .build();
  }

  public NodeCarrierServiceAndServiceOptionResponse
      getNodeCarrierServiceAndServiceOptionResponse() {
    NodeCarrierServiceAndServiceOptionResponse response =
        new NodeCarrierServiceAndServiceOptionResponse();
    response.setNodeId(NODE_ID);
    response.setOrgId(ORG_ID);
    response.setStreet(STREET);
    response.setCity(CITY);
    response.setProvince(PROVINCE);
    response.setPostalCode(POSTAL_CODE);
    response.setCarrierServices(List.of(CARRIER_SERVICE_ID));
    response.setServiceOptions(List.of(SERVICE_OPTION));
    response.setActiveCombination(List.of(getActiveCombination()));

    return response;
  }

  public PagePayload<NodeCarrierServiceAndServiceOptionResponse>
      getNodeCarrierServiceAndServiceOptionResponse(Integer pageNo) {
    PagePayload<NodeCarrierServiceAndServiceOptionResponse> nodeCarrierServicePagePayload =
        new PagePayload<>();

    NodeCarrierServiceAndServiceOptionResponse response =
        getNodeCarrierServiceAndServiceOptionResponse();

    Pagination pagination = new Pagination();
    pagination.setTotalPages(1);
    pagination.setCurrentPage(pageNo);
    pagination.setSortBy("nodeId");
    pagination.setSortOrder("ASC");
    pagination.setTotalRecords(1);
    nodeCarrierServicePagePayload.setPagination(pagination);
    nodeCarrierServicePagePayload.setData(List.of(response));

    return nodeCarrierServicePagePayload;
  }

  public BaseResponse<PagePayload<ProcessingTimeBufferResponse>>
      getBaseResponseOfProcessingTimeBuffers() {
    return BaseResponse.builder()
        .message("Processing time buffers fetched successfully")
        .payload(getProcessingTimeBufferPagePayload(1))
        .build();
  }

  public PagePayload<ProcessingTimeBufferResponse> getProcessingTimeBufferPagePayload(int pageNo) {
    PagePayload<ProcessingTimeBufferResponse> processingTimeBufferDtoPagePayload =
        new PagePayload<>();

    ProcessingTimeBufferResponse processingTimeBufferResponse1 =
        getProcessingTimeBufferResponse(NODE_ID);
    ProcessingTimeBufferResponse processingTimeBufferResponse2 =
        getProcessingTimeBufferResponse(NODE_ID_2);

    Pagination pagination = new Pagination();
    pagination.setTotalPages(2);
    pagination.setCurrentPage(pageNo);
    pagination.setSortBy("DESC");
    pagination.setTotalRecords(2);
    processingTimeBufferDtoPagePayload.setPagination(pagination);
    processingTimeBufferDtoPagePayload.setData(
        Arrays.asList(processingTimeBufferResponse1, processingTimeBufferResponse2));

    return processingTimeBufferDtoPagePayload;
  }

  public ProcessingTimeBufferResponse getProcessingTimeBufferResponse(String nodeId) {
    return ProcessingTimeBufferResponse.builder()
        .nodeId(nodeId)
        .orgId(ORG_ID)
        .nodeType(NODE_TYPE)
        .serviceOptions(List.of(SERVICE_OPTION, SERVICE_OPTION_2))
        .processingTimeBuffers(
            List.of(
                getProcessingTimeBuffer(SERVICE_OPTION), getProcessingTimeBuffer(SERVICE_OPTION_2)))
        .build();
  }

  private ProcessingTimeBuffer getProcessingTimeBuffer(String serviceOption) {
    ProcessingTimeBuffer processingTimeBuffer = new ProcessingTimeBuffer();
    processingTimeBuffer.setServiceOption(serviceOption);
    processingTimeBuffer.setBufferHours(2.5);
    processingTimeBuffer.setBufferStartDate(new Date(1000));
    processingTimeBuffer.setBufferEndDate(new Date(1000));
    processingTimeBuffer.setStatus("Inactive");
    return processingTimeBuffer;
  }

  public ProcessingTimeBufferResponse getProcessingTimeBufferResponseEmptyValues(String nodeId) {
    return ProcessingTimeBufferResponse.builder()
        .nodeId(nodeId)
        .orgId(ORG_ID)
        .nodeType(NODE_TYPE)
        .serviceOptions(new ArrayList<>())
        .processingTimeBuffers(new ArrayList<>())
        .build();
  }

  public ProcessingTimeBufferResponse getProcessingTimeBufferResponsePartialEmptyValues(
      String nodeId) {
    return ProcessingTimeBufferResponse.builder()
        .nodeId(nodeId)
        .orgId(ORG_ID)
        .nodeType(NODE_TYPE)
        .serviceOptions(new ArrayList<>())
        .processingTimeBuffers(List.of(getProcessingTimeBufferWithNullValues(SERVICE_OPTION)))
        .build();
  }

  private ProcessingTimeBuffer getProcessingTimeBufferWithNullValues(String serviceOption) {
    ProcessingTimeBuffer processingTimeBuffer = new ProcessingTimeBuffer();
    processingTimeBuffer.setServiceOption(serviceOption);
    processingTimeBuffer.setBufferHours(2.5);
    processingTimeBuffer.setBufferStartDate(null);
    processingTimeBuffer.setBufferEndDate(null);
    processingTimeBuffer.setStatus(null);
    return processingTimeBuffer;
  }

  public SfccOrder getSfccOrder() {
    ShipToAddress shipToAddress = ShipToAddress.builder().zipCode("V3G 1S3").province("ON").build();
    Item item = Item.builder().itemId("item1").itemType("REGULAR").seller("HBC").build();
    Double requiredQty = Double.valueOf(5);
    SfccOrderLine line =
        SfccOrderLine.builder()
            .lineId("1")
            .requiredQty(requiredQty)
            .shipToAddress(shipToAddress)
            .item(item)
            .build();

    List<SfccOrderLine> sfccOrderLineList = new ArrayList<>();
    sfccOrderLineList.add(line);

    return SfccOrder.builder()
        .basketId("156465897")
        .organizationCode("BAY")
        .pageName("Basket/Checkout")
        .sessionId("jkfdsj5748fdgf58gfh")
        .lines(sfccOrderLineList)
        .build();
  }

  public SfccResponse getSfccResponse() {
    Double requiredQty = Double.valueOf(5);
    Double maxAvailableQuantity = Double.valueOf(5);
    Long qty = Long.valueOf(5);
    SfccPromiseDetails sfccPromiseDetails =
        SfccPromiseDetails.builder()
            .fillQuantity(qty)
            .sourceNodeId("node2")
            .sourceNodeType("FC")
            .build();
    List<SfccPromiseDetails> sfccPromiseDetailsList = new ArrayList<>();
    sfccPromiseDetailsList.add(sfccPromiseDetails);
    SfccResponseLine sfccResponseLine =
        SfccResponseLine.builder()
            .maxAvailableQuantity(maxAvailableQuantity)
            .requestQuantity(requiredQty)
            .itemId("item1")
            .itemType("BAY")
            .promiseDetails(sfccPromiseDetailsList)
            .build();
    List<SfccResponseLine> sfccResponseLines = new ArrayList<>();
    sfccResponseLines.add(sfccResponseLine);
    String sDate1 = "2022-06-17T05:05:00";
    SfccSuggestedPromiseOption sfccSuggestedPromiseOption =
        SfccSuggestedPromiseOption.builder().edd(sDate1).lines(sfccResponseLines).build();
    List<SfccSuggestedPromiseOption> sfccSuggestedPromiseOptionList = new ArrayList<>();
    sfccSuggestedPromiseOptionList.add(sfccSuggestedPromiseOption);
    return SfccResponse.builder()
        .cartId("156465897")
        .orgId("BAY")
        .pageName("Basket/Checkout")
        .sessionId("jkfdsj5748fdgf58gfh")
        .sdnd(sfccSuggestedPromiseOptionList)
        .express(sfccSuggestedPromiseOptionList)
        .standard(sfccSuggestedPromiseOptionList)
        .nextday(sfccSuggestedPromiseOptionList)
        .hasExceptions(false)
        .build();
  }

  public SfccResponse getSfccResponse2() {
    Double requiredQty = Double.valueOf(5);
    Double maxAvailableQuantity = Double.valueOf(5);
    Long qty = Long.valueOf(3);
    Long unavailableQty = Long.valueOf(2);
    SfccPromiseDetails sfccPromiseDetails =
        SfccPromiseDetails.builder()
            .fillQuantity(qty)
            .sourceNodeId("node2")
            .sourceNodeType("FC")
            .build();
    List<SfccPromiseDetails> sfccPromiseDetailsList = new ArrayList<>();
    sfccPromiseDetailsList.add(sfccPromiseDetails);
    SfccResponseLine sfccResponseLine =
        SfccResponseLine.builder()
            .maxAvailableQuantity(maxAvailableQuantity)
            .requestQuantity(requiredQty)
            .itemId("item1")
            .promiseDetails(sfccPromiseDetailsList)
            .build();
    List<SfccResponseLine> sfccResponseLines = new ArrayList<>();
    sfccResponseLines.add(sfccResponseLine);
    String sDate1 = "2022-06-16T05:05:00";
    SfccSuggestedPromiseOption sfccSuggestedPromiseOption =
        SfccSuggestedPromiseOption.builder().edd(sDate1).lines(sfccResponseLines).build();
    String sDate2 = "2022-06-17T05:05:00";
    SfccSuggestedPromiseOption sfccSuggestedPromiseOption2 =
        SfccSuggestedPromiseOption.builder().edd(sDate2).lines(sfccResponseLines).build();
    List<SfccSuggestedPromiseOption> sfccSuggestedPromiseOptionList = new ArrayList<>();
    sfccSuggestedPromiseOptionList.add(sfccSuggestedPromiseOption);
    sfccSuggestedPromiseOptionList.add(sfccSuggestedPromiseOption2);
    SfccErrorResponseLine sfccErrorResponseLine =
        SfccErrorResponseLine.builder()
            .errorCode(1011)
            .errorMessage("Unavailable")
            .requestQuantity(5.0)
            .itemId("item1")
            .build();
    List<SfccErrorResponseLine> sfccErrorResponseLineList = new ArrayList<>();
    sfccErrorResponseLineList.add(sfccErrorResponseLine);
    sfccErrorResponseLineList.add(sfccErrorResponseLine);
    SfccSuggestedPromiseOptionError sfccSuggestedPromiseOptionError =
        SfccSuggestedPromiseOptionError.builder().lines(sfccErrorResponseLineList).build();
    SfccErrorResponse sfccErrorResponse =
        SfccErrorResponse.builder()
            .sdnd(sfccSuggestedPromiseOptionError)
            .express(sfccSuggestedPromiseOptionError)
            .standard(sfccSuggestedPromiseOptionError)
            .nextday(sfccSuggestedPromiseOptionError)
            .build();
    return SfccResponse.builder()
        .cartId("156465897")
        .orgId("BAY")
        .pageName("Basket/Checkout")
        .sessionId("jkfdsj5748fdgf58gfh")
        .sdnd(sfccSuggestedPromiseOptionList)
        .express(sfccSuggestedPromiseOptionList)
        .standard(sfccSuggestedPromiseOptionList)
        .nextday(sfccSuggestedPromiseOptionList)
        .hasExceptions(true)
        .exceptions(sfccErrorResponse)
        .build();
  }

  public SfccResponse getSfccResponse3() {
    SfccErrorResponse sfccErrorResponse = SfccErrorResponse.builder().build();
    return SfccResponse.builder()
        .cartId("156465897")
        .orgId("BAY")
        .pageName("Basket/Checkout")
        .sessionId("jkfdsj5748fdgf58gfh")
        .exceptions(sfccErrorResponse)
        .build();
  }

  public SfccResponse getSfccResponse4() {
    SfccPromiseDetails sfccPromiseDetails =
        SfccPromiseDetails.builder().sourceNodeId("node2").sourceNodeType("FC").build();
    List<SfccPromiseDetails> sfccPromiseDetailsList = new ArrayList<>();
    sfccPromiseDetailsList.add(sfccPromiseDetails);
    SfccResponseLine sfccResponseLine =
        SfccResponseLine.builder().itemId("item1").promiseDetails(sfccPromiseDetailsList).build();
    List<SfccResponseLine> sfccResponseLines = new ArrayList<>();
    sfccResponseLines.add(sfccResponseLine);
    String sDate1 = "2022-06-16T05:05:00";
    SfccSuggestedPromiseOption sfccSuggestedPromiseOption =
        SfccSuggestedPromiseOption.builder().edd(sDate1).lines(sfccResponseLines).build();
    String sDate2 = "2022-06-17T05:05:00";
    SfccSuggestedPromiseOption sfccSuggestedPromiseOption2 =
        SfccSuggestedPromiseOption.builder().edd(sDate2).lines(sfccResponseLines).build();
    List<SfccSuggestedPromiseOption> sfccSuggestedPromiseOptionList = new ArrayList<>();
    sfccSuggestedPromiseOptionList.add(sfccSuggestedPromiseOption);
    sfccSuggestedPromiseOptionList.add(sfccSuggestedPromiseOption2);
    SfccErrorResponseLine sfccErrorResponseLine =
        SfccErrorResponseLine.builder()
            .errorMessage("Unavailable")
            .itemId("item1")
            .unavailableQuantity(2.0)
            .build();
    List<SfccErrorResponseLine> sfccErrorResponseLineList = new ArrayList<>();
    sfccErrorResponseLineList.add(sfccErrorResponseLine);
    sfccErrorResponseLineList.add(sfccErrorResponseLine);
    SfccSuggestedPromiseOptionError sfccSuggestedPromiseOptionError =
        SfccSuggestedPromiseOptionError.builder().lines(sfccErrorResponseLineList).build();
    SfccErrorResponse sfccErrorResponse =
        SfccErrorResponse.builder()
            .sdnd(sfccSuggestedPromiseOptionError)
            .express(sfccSuggestedPromiseOptionError)
            .standard(sfccSuggestedPromiseOptionError)
            .nextday(sfccSuggestedPromiseOptionError)
            .build();
    return SfccResponse.builder()
        .cartId("156465897")
        .orgId("BAY")
        .pageName("Basket/Checkout")
        .sessionId("jkfdsj5748fdgf58gfh")
        .sdnd(sfccSuggestedPromiseOptionList)
        .express(sfccSuggestedPromiseOptionList)
        .standard(sfccSuggestedPromiseOptionList)
        .nextday(sfccSuggestedPromiseOptionList)
        .hasExceptions(true)
        .exceptions(sfccErrorResponse)
        .build();
  }

  public SfccResponse getSfccResponse5() {
    SfccPromiseDetails sfccPromiseDetails =
        SfccPromiseDetails.builder().sourceNodeId("node2").sourceNodeType("FC").build();
    List<SfccPromiseDetails> sfccPromiseDetailsList = new ArrayList<>();
    sfccPromiseDetailsList.add(sfccPromiseDetails);
    SfccResponseLine sfccResponseLine =
        SfccResponseLine.builder().itemId("item1").promiseDetails(sfccPromiseDetailsList).build();
    List<SfccResponseLine> sfccResponseLines = new ArrayList<>();
    sfccResponseLines.add(sfccResponseLine);
    String sDate1 = "2022-06-16T05:05:00";
    SfccSuggestedPromiseOption sfccSuggestedPromiseOption =
        SfccSuggestedPromiseOption.builder().edd(sDate1).lines(sfccResponseLines).build();
    String sDate2 = "2022-06-17T05:05:00";
    SfccSuggestedPromiseOption sfccSuggestedPromiseOption2 =
        SfccSuggestedPromiseOption.builder().edd(sDate2).lines(sfccResponseLines).build();
    List<SfccSuggestedPromiseOption> sfccSuggestedPromiseOptionList = new ArrayList<>();
    sfccSuggestedPromiseOptionList.add(sfccSuggestedPromiseOption);
    sfccSuggestedPromiseOptionList.add(sfccSuggestedPromiseOption2);
    SfccErrorResponseLine sfccErrorResponseLine =
        SfccErrorResponseLine.builder().errorMessage("Unavailable").itemId("item1").build();
    List<SfccErrorResponseLine> sfccErrorResponseLineList = new ArrayList<>();
    sfccErrorResponseLineList.add(sfccErrorResponseLine);
    sfccErrorResponseLineList.add(sfccErrorResponseLine);
    SfccSuggestedPromiseOptionError sfccSuggestedPromiseOptionError =
        SfccSuggestedPromiseOptionError.builder().lines(sfccErrorResponseLineList).build();
    SfccErrorResponse sfccErrorResponse =
        SfccErrorResponse.builder()
            .sdnd(sfccSuggestedPromiseOptionError)
            .express(sfccSuggestedPromiseOptionError)
            .standard(sfccSuggestedPromiseOptionError)
            .nextday(sfccSuggestedPromiseOptionError)
            .build();
    return SfccResponse.builder()
        .cartId("156465897")
        .orgId("BAY")
        .pageName("Basket/Checkout")
        .sessionId("jkfdsj5748fdgf58gfh")
        .sdnd(sfccSuggestedPromiseOptionList)
        .express(sfccSuggestedPromiseOptionList)
        .standard(sfccSuggestedPromiseOptionList)
        .nextday(sfccSuggestedPromiseOptionList)
        .exceptions(sfccErrorResponse)
        .build();
  }

  public SfccResponse getSfccResponse6() {
    Double requiredQty = Double.valueOf(5);
    Double maxAvailableQuantity = Double.valueOf(5);
    Long qty = Long.valueOf(3);
    SfccPromiseDetails sfccPromiseDetails =
        SfccPromiseDetails.builder()
            .fillQuantity(qty)
            .sourceNodeId("node2")
            .sourceNodeType("FC")
            .build();
    List<SfccPromiseDetails> sfccPromiseDetailsList = new ArrayList<>();
    sfccPromiseDetailsList.add(sfccPromiseDetails);
    SfccResponseLine sfccResponseLine =
        SfccResponseLine.builder()
            .maxAvailableQuantity(maxAvailableQuantity)
            .requestQuantity(requiredQty)
            .itemId("item1")
            .promiseDetails(sfccPromiseDetailsList)
            .build();
    List<SfccResponseLine> sfccResponseLines = new ArrayList<>();
    sfccResponseLines.add(sfccResponseLine);
    String sDate1 = "2022-06-16T05:05:00";
    SfccSuggestedPromiseOption sfccSuggestedPromiseOption =
        SfccSuggestedPromiseOption.builder().edd(sDate1).lines(sfccResponseLines).build();
    String sDate2 = "2022-06-17T05:05:00";
    SfccSuggestedPromiseOption sfccSuggestedPromiseOption2 =
        SfccSuggestedPromiseOption.builder().edd(sDate2).lines(sfccResponseLines).build();
    List<SfccSuggestedPromiseOption> sfccSuggestedPromiseOptionList = new ArrayList<>();
    sfccSuggestedPromiseOptionList.add(sfccSuggestedPromiseOption);
    sfccSuggestedPromiseOptionList.add(sfccSuggestedPromiseOption2);

    SfccSuggestedPromiseOptionError sfccSuggestedPromiseOptionError =
        SfccSuggestedPromiseOptionError.builder().build();
    SfccErrorResponse sfccErrorResponse =
        SfccErrorResponse.builder()
            .sdnd(sfccSuggestedPromiseOptionError)
            .express(sfccSuggestedPromiseOptionError)
            .standard(sfccSuggestedPromiseOptionError)
            .nextday(sfccSuggestedPromiseOptionError)
            .build();
    return SfccResponse.builder()
        .cartId("156465897")
        .orgId("BAY")
        .pageName("Basket/Checkout")
        .sessionId("jkfdsj5748fdgf58gfh")
        .sdnd(sfccSuggestedPromiseOptionList)
        .express(sfccSuggestedPromiseOptionList)
        .standard(sfccSuggestedPromiseOptionList)
        .nextday(sfccSuggestedPromiseOptionList)
        .hasExceptions(true)
        .exceptions(sfccErrorResponse)
        .build();
  }

  public TransitBufferConfigRequest getTransitBufferConfigRequest() {
    Date bufferStartDate = new Date(1000);
    Date bufferEndDate = new Date(1000);
    return TransitBufferConfigRequest.builder()
        .orgId(ORG_ID)
        .carrierServiceId(CARRIER_SERVICE_ID)
        .bufferDays(BUFFER_DAYS)
        .startDate(bufferStartDate)
        .endDate(bufferEndDate)
        .filePath(FILE_PATH_WITH_BUCKET_NAME)
        .storageType(STORAGE_TYPE)
        .build();
  }

  public TransitBufferConfigResponse getTransitBufferConfigResponse() {
    Date bufferStartDate = new Date(1000);
    Date bufferEndDate = new Date(1000);
    return TransitBufferConfigResponse.builder()
        .id(1L)
        .orgId(ORG_ID)
        .carrierServiceId(CARRIER_SERVICE_ID)
        .bufferDays(BUFFER_DAYS)
        .startDate(bufferStartDate)
        .endDate(bufferEndDate)
        .status(TransitBufferConfigRequestStatusEnum.CREATED)
        .build();
  }

  public BaseResponse<TransitBufferConfigResponse> getTransitBufferConfigResponseBaseResponse() {
    BaseResponse<TransitBufferConfigResponse> transitBufferConfigResponseBaseResponse =
        new BaseResponse<>();
    transitBufferConfigResponseBaseResponse.setSuccess(Boolean.TRUE);
    transitBufferConfigResponseBaseResponse.setPayload(getTransitBufferConfigResponse());
    return transitBufferConfigResponseBaseResponse;
  }

  public NodeDto getNodeDto(String nodeId) {
    return NodeDto.builder()
        .nodeId(nodeId)
        .orgId(ORG_ID)
        .nodeType(NODE_TYPE)
        .city(CITY)
        .latitude(LATITUDE)
        .longitude(LONGITUDE)
        .isActive(true)
        .build();
  }

  public PagePayload<NodeDto> getNodePagePayload() {
    PagePayload<NodeDto> nodeDtoPagePayload = new PagePayload<>();

    NodeDto nodeDto1 = getNodeDto(NODE_ID);
    NodeDto nodeDto2 = getNodeDto(NODE_ID_2);

    Pagination pagination = new Pagination();
    pagination.setTotalPages(2);
    pagination.setCurrentPage(1);
    pagination.setSortBy("DESC");
    pagination.setTotalRecords(2);
    nodeDtoPagePayload.setPagination(pagination);
    nodeDtoPagePayload.setData(Arrays.asList(nodeDto1, nodeDto2));

    return nodeDtoPagePayload;
  }

  public BaseResponse<PagePayload<NodeDto>> getBaseResponseOfNodePage() {
    return BaseResponse.builder()
        .message("Nodes fetched successfully")
        .payload(getNodePagePayload())
        .build();
  }

  public BaseResponse<List<NodeCarrierResponse>> getBaseResponseOfNodeCarrier() {
    return BaseResponse.builder()
        .message("Node Carriers fetched successfully")
        .payload(getNodeCarrierResponseList())
        .build();
  }

  public List<NodeCarrierResponse> getNodeCarrierResponseList() {
    return List.of(getNodeCarrierResponse(NODE_ID), getNodeCarrierResponse(NODE_ID_2));
  }

  private NodeCarrierResponse getNodeCarrierResponse(String nodeId) {
    return NodeCarrierResponse.builder()
        .nodeId(nodeId)
        .orgId(ORG_ID)
        .carrierServiceId(CARRIER_SERVICE_ID)
        .serviceOption(SERVICE_OPTION)
        .lastPickupTime(LAST_PICKUP_TIME)
        .build();
  }

  public BaseResponse<List<NodeCalendarResponse>> getNodeCalendarBaseResponse() {
    return BaseResponse.builder()
        .message("Node Calendars fetched successfully")
        .payload(getNodeCalendarResponseList())
        .build();
  }

  public List<NodeCalendarResponse> getNodeCalendarResponseList() {
    NodeCalendarResponse nodeCalendarResponse =
        NodeCalendarResponse.builder()
            .nodeId(NODE_ID)
            .orgId(ORG_ID)
            .calendarId(CALENDAR_ID)
            .effectiveDate(EFFECTIVE_DATE)
            .build();
    return List.of(nodeCalendarResponse);
  }

  public FileResponse getFileResponse() {
    return FileResponse.builder()
        .filePath(FILE_PATH)
        .fileName(FILE_NAME)
        .bucketName(BUCKET_NAME)
        .contentLength(58L)
        .contentType("text/csv")
        .inputStream(new ByteArrayInputStream(eddComputationData.getBytes()))
        .build();
  }

  public FileResponse getFileResponse2() {
    return FileResponse.builder()
        .filePath(FILE_PATH)
        .fileName(FILE_NAME)
        .bucketName(BUCKET_NAME)
        .contentLength(58L)
        .contentType("text/csv")
        .inputStream(new ByteArrayInputStream(eddComputationDataWith4Orders.getBytes()))
        .build();
  }

  public FileResponse getFileResponse3() {
    return FileResponse.builder()
        .filePath(FILE_PATH)
        .fileName(FILE_NAME)
        .bucketName(BUCKET_NAME)
        .contentLength(58L)
        .contentType("text/csv")
        .inputStream(new ByteArrayInputStream(eddComputationDataWith6Orders.getBytes()))
        .build();
  }

  public FileResponse getFileResponse4() {
    return FileResponse.builder()
        .filePath(FILE_PATH)
        .fileName(FILE_NAME)
        .bucketName(BUCKET_NAME)
        .contentLength(58L)
        .contentType("text/csv")
        .inputStream(new ByteArrayInputStream(eddComputationDataWith15Lines.getBytes()))
        .build();
  }

  public PreSignedUrlResponse getPreSignedUrlResponse() {
    return PreSignedUrlResponse.builder()
        .signedURL("URL")
        .storageType("s3")
        .filePath("path")
        .build();
  }

  public String outPutDataForMultipleLineOrder() {
    return "\"orgId\",\"cartId\",\"sessionId\",\"pageName\",\"serviceOption\",\"edd\",\"cutOffTime\",\"lines_itemId\",\"lines_itemType\",\"lines_requestQuantity\",\"lines_maxAvailableQuantity\",\"lines_promiseDetails_sourceNodeId\",\"lines_promiseDetails_sourceNodeType\",\"lines_promiseDetails_sourceNodeType_fillQuantity\",\"hasExceptions\",\"exception_lines_itemId\",\"exception_lines_itemType\",\"exception_lines_errorCode\",\"exception_lines_errorMessage\",\"exception_lines_requestQuantity\",\"exception_lines_unavailableQuantity\"\n\"BAY\",\"156465897\",\"jkfdsj5748fdgf58gfh\",\"Basket/Checkout\",\"SDND\",\"2022-06-17T05:05:00\",,\"item1\",\"BAY\",\"5.0\",\"5.0\",\"node2\",\"FC\",\"5\",\"false\",\"\",\"\",\"\",\"\",\"\",\"\"\n\"BAY\",\"156465897\",\"jkfdsj5748fdgf58gfh\",\"Basket/Checkout\",\"STANDARD\",\"2022-06-17T05:05:00\",,\"item1\",\"BAY\",\"5.0\",\"5.0\",\"node2\",\"FC\",\"5\",\"false\",\"\",\"\",\"\",\"\",\"\",\"\"\n\"BAY\",\"156465897\",\"jkfdsj5748fdgf58gfh\",\"Basket/Checkout\",\"EXPRESS\",\"2022-06-17T05:05:00\",,\"item1\",\"BAY\",\"5.0\",\"5.0\",\"node2\",\"FC\",\"5\",\"false\",\"\",\"\",\"\",\"\",\"\",\"\"\n\"BAY\",\"156465897\",\"jkfdsj5748fdgf58gfh\",\"Basket/Checkout\",\"NEXTDAY\",\"2022-06-17T05:05:00\",,\"item1\",\"BAY\",\"5.0\",\"5.0\",\"node2\",\"FC\",\"5\",\"false\",\"\",\"\",\"\",\"\",\"\",\"\"\n\"BAY\",\"156465897\",\"jkfdsj5748fdgf58gfh\",\"Basket/Checkout\",\"SDND\",\"2022-06-17T05:05:00\",,\"item1\",\"BAY\",\"5.0\",\"5.0\",\"node2\",\"FC\",\"5\",\"false\",\"\",\"\",\"\",\"\",\"\",\"\"\n\"BAY\",\"156465897\",\"jkfdsj5748fdgf58gfh\",\"Basket/Checkout\",\"STANDARD\",\"2022-06-17T05:05:00\",,\"item1\",\"BAY\",\"5.0\",\"5.0\",\"node2\",\"FC\",\"5\",\"false\",\"\",\"\",\"\",\"\",\"\",\"\"\n\"BAY\",\"156465897\",\"jkfdsj5748fdgf58gfh\",\"Basket/Checkout\",\"EXPRESS\",\"2022-06-17T05:05:00\",,\"item1\",\"BAY\",\"5.0\",\"5.0\",\"node2\",\"FC\",\"5\",\"false\",\"\",\"\",\"\",\"\",\"\",\"\"\n\"BAY\",\"156465897\",\"jkfdsj5748fdgf58gfh\",\"Basket/Checkout\",\"NEXTDAY\",\"2022-06-17T05:05:00\",,\"item1\",\"BAY\",\"5.0\",\"5.0\",\"node2\",\"FC\",\"5\",\"false\",\"\",\"\",\"\",\"\",\"\",\"\"\n";
  }
}
