package com.hbc.csvdownload.common;

import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.CALENDAR_ID;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.CITY;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.COUNTRY;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.LATITUDE;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.LONGITUDE;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.NODE_TYPE;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.POSTAL_CODE_PREFIX;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.SERVICE_NAME;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.STATE;
import static org.junit.jupiter.api.parallel.Resources.TIME_ZONE;

import com.hbc.calendar.domain.outbound.CarrierServiceCalendarResponse;
import com.hbc.carrier.domain.outbound.CarrierServiceResponse;
import com.hbc.common.base.PagePayload;
import com.hbc.common.base.PagePayload.Pagination;
import com.hbc.common.response.BaseResponse;
import com.hbc.csvdownload.domain.pojo.DownloadErrorTransitData;
import com.hbc.csvdownload.domain.pojo.ProcessingLeadTimesRaw;
import com.hbc.dataupload.common.outbound.NodeCarrierServiceAndServiceOptionResponse;
import com.hbc.dataupload.common.outbound.ProcessingTimeBufferResponse;
import com.hbc.dataupload.common.pojo.ActiveCombination;
import com.hbc.dataupload.common.pojo.ProcessingTimeBuffer;
import com.hbc.intermediary.domain.Item;
import com.hbc.intermediary.domain.SfccErrorResponse;
import com.hbc.intermediary.domain.SfccErrorResponseLine;
import com.hbc.intermediary.domain.SfccOrder;
import com.hbc.intermediary.domain.SfccOrderLine;
import com.hbc.intermediary.domain.SfccPromiseDetails;
import com.hbc.intermediary.domain.SfccResponse;
import com.hbc.intermediary.domain.SfccResponseLine;
import com.hbc.intermediary.domain.SfccSuggestedPromiseOption;
import com.hbc.intermediary.domain.SfccSuggestedPromiseOptionError;
import com.hbc.intermediary.domain.ShipToAddress;
import com.hbc.jobs.framework.common.domain.enums.JobStatusEnum;
import com.hbc.jobs.framework.common.domain.enums.JobTypeEnum;
import com.hbc.jobs.framework.common.domain.outbound.JobResponse;
import com.hbc.jobs.framework.common.domain.pojo.AuditLog;
import com.hbc.jobs.framework.common.domain.pojo.JobDto;
import com.hbc.jobs.framework.common.domain.pojo.RecordStatusDto;
import com.hbc.postal.code.timezone.api.domain.dto.PostalCodeTimezoneDto;
import com.hbc.transit.domain.dto.TransitTimeEntriesDto;
import com.hbc.transit.domain.outbound.TransitResponse;
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
  private static final String NODE_ID_2 = "nodeId2";
  private static final String SERVICE_OPTION_2 = "EXPRESS";
  public static final String templateType = "nodeCarrier";

  public static final String templateTypeInvalid = "invalid";

  public static final String invalidTemplateTypeErrMsg = "Invalid template type";
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

  public RecordStatusDto getJobRecordsForTransitTimes() {

    return RecordStatusDto.builder()
        .jobId(JOB_ID)
        .jobType(JobTypeEnum.UPLOAD_TRANSIT_TIMES)
        .orgId(ORG_ID)
        .errorMessage("Invalid nodeId")
        .requestBody(transitTimesRequestBodyJson)
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
}
