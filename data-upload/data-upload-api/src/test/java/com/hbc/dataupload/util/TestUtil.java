package com.hbc.dataupload.util;

import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.ALLOCATION_RULE_ID;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.CARRIER_ID;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.CARRIER_NAME;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.CITY;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.COUNTRY;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.DESTINATION_GEO_ZONE;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.EXPRESS_ELIGIBLE;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.LATITUDE;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.LONGITUDE;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.NEXTDAY_ELIGIBLE;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.NODE_TYPE;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.POSTAL_CODE;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.POSTAL_CODE_PREFIX;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.PROVINCE;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.SDND_ELIGIBLE;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.SERVICE_NAME;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.SERVICE_OPTIONS;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.SOURCE_NODES;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.STATE;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.STREET;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.TIMEZONE;
import static com.hbc.dataupload.helper.CarrierDataUploadConstants.CARRIER_DATA_UPLOAD_FAILED;
import static com.hbc.dataupload.helper.CarrierDataUploadConstants.CARRIER_DATA_UPLOAD_PARTIAL_SUCCESS;
import static com.hbc.dataupload.helper.CarrierDataUploadConstants.CARRIER_DATA_UPLOAD_SUCCESS;
import static com.hbc.dataupload.helper.NodeCarrierSelectionUploadConstants.NODE_CARRIER_SELECTION_DATA_UPLOAD_FAILED;
import static com.hbc.dataupload.helper.NodeCarrierSelectionUploadConstants.NODE_CARRIER_SELECTION_DATA_UPLOAD_PARTIAL_SUCCESS;
import static com.hbc.dataupload.helper.NodeCarrierSelectionUploadConstants.NODE_CARRIER_SELECTION_DATA_UPLOAD_SUCCESS;
import static com.hbc.dataupload.helper.NodeDataUploadConstants.NODE_DATA_UPLOAD_FAILED;
import static com.hbc.dataupload.helper.NodeDataUploadConstants.NODE_DATA_UPLOAD_PARTIAL_SUCCESS;
import static com.hbc.dataupload.helper.NodeDataUploadConstants.NODE_DATA_UPLOAD_SUCCESS;
import static com.hbc.dataupload.helper.PostalCodeTimezoneDataUploadConstants.POSTAL_CODE_TIMEZONE_DATA_UPLOAD_FAILED;
import static com.hbc.dataupload.helper.PostalCodeTimezoneDataUploadConstants.POSTAL_CODE_TIMEZONE_DATA_UPLOAD_PARTIAL_SUCCESS;
import static com.hbc.dataupload.helper.PostalCodeTimezoneDataUploadConstants.POSTAL_CODE_TIMEZONE_DATA_UPLOAD_SUCCESS;
import static com.hbc.dataupload.helper.PromiseSourcingRuleDataUploadConstants.PROMISE_SOURCING_RULE_DATA_UPLOAD_FAILED;
import static com.hbc.dataupload.helper.PromiseSourcingRuleDataUploadConstants.PROMISE_SOURCING_RULE_DATA_UPLOAD_PARTIAL_SUCCESS;
import static com.hbc.dataupload.helper.PromiseSourcingRuleDataUploadConstants.PROMISE_SOURCING_RULE_DATA_UPLOAD_SUCCESS;
import static com.hbc.dataupload.helper.TransitDataUploadConstants.TRANSIT_DATA_UPLOAD_FAILED;
import static com.hbc.dataupload.helper.TransitDataUploadConstants.TRANSIT_DATA_UPLOAD_PARTIAL_SUCCESS;
import static com.hbc.dataupload.helper.TransitDataUploadConstants.TRANSIT_DATA_UPLOAD_SUCCESS;
import static com.hbc.dataupload.helper.WeightageConfigurationDataUploadConstants.WEIGHTAGE_CONFIGURATION_DATA_UPLOAD_FAILED;
import static com.hbc.dataupload.helper.WeightageConfigurationDataUploadConstants.WEIGHTAGE_CONFIGURATION_DATA_UPLOAD_PARTIAL_SUCCESS;
import static com.hbc.dataupload.helper.WeightageConfigurationDataUploadConstants.WEIGHTAGE_CONFIGURATION_DATA_UPLOAD_SUCCESS;
import static org.junit.jupiter.api.parallel.Resources.TIME_ZONE;

import com.hbc.calendar.domain.outbound.CalendarResponse;
import com.hbc.calendar.domain.outbound.CarrierServiceCalendarResponse;
import com.hbc.calendar.domain.outbound.NodeCalendarResponse;
import com.hbc.calendar.domain.outbound.NodeCarrierServiceCalendarResponse;
import com.hbc.calendar.domain.pojo.ExceptionDays;
import com.hbc.carrier.domain.outbound.CarrierServiceResponse;
import com.hbc.common.base.PagePayload;
import com.hbc.common.base.PagePayload.Pagination;
import com.hbc.common.pojo.PageParams;
import com.hbc.common.response.BaseResponse;
import com.hbc.dataupload.domain.dto.CalendarDto;
import com.hbc.dataupload.domain.dto.CarrierTransitDto;
import com.hbc.dataupload.domain.dto.NodeServiceOptionDto;
import com.hbc.dataupload.domain.dto.ProcessingTimeBufferDto;
import com.hbc.dataupload.domain.pojo.CarrierServiceCalendars;
import com.hbc.dataupload.domain.pojo.ProcessingTimeBuffer;
import com.hbc.node.carrier.domain.outbound.NodeCarrierResponse;
import com.hbc.node.carrier.domain.outbound.NodeCarrierSelectionResponse;
import com.hbc.node.domain.dto.NodeDto;
import com.hbc.node.domain.outbound.NodeResponse;
import com.hbc.postal.code.timezone.api.domain.dto.MarketRegionDto;
import com.hbc.postal.code.timezone.api.domain.dto.PostalCodeTimezoneDto;
import com.hbc.promise.sourcing.rule.api.domain.dto.PromiseSourcingRuleDto;
import com.hbc.transit.domain.dto.TransitTimeEntriesDto;
import com.hbc.transit.domain.outbound.TransitResponse;
import com.hbc.weightage.configuration.api.domain.dto.WeightageConfigurationDto;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class TestUtil {
  public static final String NODE_ID = "Node_Id_01";
  public static final String ORG_ID = "Org_Id_01";
  public static final String CARRIER_SERVICE_ID = "Carrier_Service_Id_01";
  public static final String SERVICE_OPTION = "Standard";
  public static final Double PROCESSING_TIME = 10.0;
  public static final String LAST_PICK_UP_TIME = "5:00 PM";
  public static final String CALENDAR_ID = "Calendar_Id_01";
  public static final String SHIPPING_STAGE = "ALL";
  public static final String EFFECTIVE_DATE = "2022-01-01";
  public static final String DESCRIPTION = "Yearly Calendar";
  private static final String NODE_ID_2 = "Node_Id_02";
  private static final String CARRIER_SERVICE_ID_2 = "";
  private static final String SERVICE_OPTION_2 = "SDND";
  public static String SOURCE_GEOZONE = "SGZ";
  public static String DESTINATION_GEOZONE = "DGZ";
  public static Float TRANSIT_DAYS = Float.valueOf(1);
  private static final String CARRIER_ID_2 = "Carrier_Id_2";

  private static final String BUFFER_START_DATE = "startTime";
  public static final String KEY = "key";

  public static final String VALUE = "value";

  public static final String TYPE = "type";
  public static final String CALENDAR_ID_2 = "Calendar_Id_02";

  private NodeCarrierResponse getNodeCarrierResponse() {
    return NodeCarrierResponse.builder()
        .nodeId(NODE_ID)
        .orgId(ORG_ID)
        .carrierServiceId(CARRIER_SERVICE_ID)
        .serviceOption(SERVICE_OPTION)
        .lastPickupTime(LAST_PICK_UP_TIME)
        .build();
  }

  public BaseResponse<NodeCarrierResponse> getBaseResponseOfNodeCarrierResponse() {
    return BaseResponse.builder()
        .message("Node carrier details fetched successfully")
        .success(true)
        .payload(getNodeCarrierResponse())
        .build();
  }

  public BaseResponse<NodeCarrierResponse> getBaseResponseOfNodeCarrierResponse2() {
    return BaseResponse.builder()
        .message("Node carrier details fetched successfully")
        .success(false)
        .payload(getNodeCarrierResponse())
        .build();
  }

  public ResponseEntity<BaseResponse<String>> getUploadNodeDataSuccessfulResponse() {
    return ResponseEntity.ok(BaseResponse.builder().message(NODE_DATA_UPLOAD_SUCCESS).build());
  }

  public ResponseEntity<BaseResponse<String>> getUploadNodeDataPartiallySuccessfulResponse() {
    return ResponseEntity.status(HttpStatus.MULTI_STATUS)
        .body(BaseResponse.builder().message(NODE_DATA_UPLOAD_PARTIAL_SUCCESS).build());
  }

  public ResponseEntity<BaseResponse<String>> getUploadNodeDataFailureResponse() {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(BaseResponse.builder().message(NODE_DATA_UPLOAD_FAILED).build());
  }

  public NodeResponse getNodeResponse() {
    return NodeResponse.builder()
        .nodeId(NODE_ID)
        .orgId(ORG_ID)
        .street(STREET)
        .bopisEligible(true)
        .city(CITY)
        .country(COUNTRY)
        .nodeType(NODE_TYPE)
        .isActive(true)
        .latitude(LATITUDE)
        .longitude(LONGITUDE)
        .postalCode(POSTAL_CODE)
        .serviceOptionEligibilities(getServiceOptionEligibilities())
        .province(PROVINCE)
        .shipToHome(true)
        .timezone(TIME_ZONE)
        .build();
  }

  public Map<String, Boolean> getServiceOptionEligibilities() {
    return Map.of(
        SDND_ELIGIBLE, Boolean.TRUE,
        EXPRESS_ELIGIBLE, Boolean.TRUE,
        NEXTDAY_ELIGIBLE, Boolean.TRUE);
  }

  public BaseResponse<NodeResponse> getSuccessfulBaseResponseForNode() {
    return BaseResponse.builder().message("").success(true).payload(getNodeResponse()).build();
  }

  public BaseResponse<NodeResponse> getFailedBaseResponseForNode() {
    return BaseResponse.builder().message("").success(false).payload(getNodeResponse()).build();
  }

  public ResponseEntity<BaseResponse<String>> getUploadPromiseSourcingRuleDataSuccessfulResponse() {
    return ResponseEntity.ok(
        BaseResponse.builder().message(PROMISE_SOURCING_RULE_DATA_UPLOAD_SUCCESS).build());
  }

  public ResponseEntity<BaseResponse<String>>
      getUploadPromiseSourcingRuleDataPartiallySuccessfulResponse() {
    return ResponseEntity.status(HttpStatus.MULTI_STATUS)
        .body(
            BaseResponse.builder()
                .message(PROMISE_SOURCING_RULE_DATA_UPLOAD_PARTIAL_SUCCESS)
                .build());
  }

  public ResponseEntity<BaseResponse<String>> getUploadPromiseSourcingRuleDataFailureResponse() {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(BaseResponse.builder().message(PROMISE_SOURCING_RULE_DATA_UPLOAD_FAILED).build());
  }

  public PromiseSourcingRuleDto getPromiseSourcingRuleDto() {
    return PromiseSourcingRuleDto.builder()
        .orgId(ORG_ID)
        .serviceOption(SERVICE_OPTION)
        .destinationGeoZone(DESTINATION_GEO_ZONE)
        .sourceNodes(Collections.singleton(SOURCE_NODES))
        .priority(1)
        .allocationRuleId(ALLOCATION_RULE_ID)
        .build();
  }

  public BaseResponse<PromiseSourcingRuleDto> getSuccessfulBaseResponseForPromiseSourcingRule() {
    return BaseResponse.builder()
        .message("")
        .success(true)
        .payload(getPromiseSourcingRuleDto())
        .build();
  }

  public BaseResponse<PromiseSourcingRuleDto> getFailedBaseResponseForPromiseSourcingRule() {
    return BaseResponse.builder()
        .message("")
        .success(false)
        .payload(getPromiseSourcingRuleDto())
        .build();
  }

  public ResponseEntity<BaseResponse<String>> getUploadCarrierDataSuccessfulResponse() {
    return ResponseEntity.ok(BaseResponse.builder().message(CARRIER_DATA_UPLOAD_SUCCESS).build());
  }

  public ResponseEntity<BaseResponse<String>> getUploadCarrierDataPartiallySuccessfulResponse() {
    return ResponseEntity.status(HttpStatus.MULTI_STATUS)
        .body(BaseResponse.builder().message(CARRIER_DATA_UPLOAD_PARTIAL_SUCCESS).build());
  }

  public ResponseEntity<BaseResponse<String>> getUploadCarrierDataFailureResponse() {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(BaseResponse.builder().message(CARRIER_DATA_UPLOAD_FAILED).build());
  }

  public CarrierServiceResponse getCarrierResponse() {
    return CarrierServiceResponse.builder()
        .orgId(ORG_ID)
        .carrierId(CARRIER_ID)
        .carrierServiceId(CARRIER_SERVICE_ID)
        .carrierName(CARRIER_NAME)
        .serviceName(SERVICE_NAME)
        .serviceOptions(SERVICE_OPTIONS)
        .build();
  }

  public CarrierServiceResponse getCarrierResponse2() {
    return CarrierServiceResponse.builder()
        .orgId(ORG_ID)
        .carrierId(CARRIER_ID_2)
        .carrierServiceId(CARRIER_SERVICE_ID)
        .carrierName(CARRIER_NAME)
        .serviceName(SERVICE_NAME)
        .serviceOptions(SERVICE_OPTIONS)
        .build();
  }

  public BaseResponse<CarrierServiceResponse> getSuccessfulBaseResponseForCarrier() {
    return BaseResponse.builder().message("").success(true).payload(getCarrierResponse()).build();
  }

  public BaseResponse<CarrierServiceResponse> getFailedBaseResponseForCarrier() {
    return BaseResponse.builder().message("").success(false).payload(getCarrierResponse()).build();
  }

  public ResponseEntity<BaseResponse<String>> getUploadPostalCodeTimezoneDataSuccessfulResponse() {
    return ResponseEntity.ok(
        BaseResponse.builder().message(POSTAL_CODE_TIMEZONE_DATA_UPLOAD_SUCCESS).build());
  }

  public ResponseEntity<BaseResponse<String>>
      getUploadPostalCodeTimezoneDataPartiallySuccessfulResponse() {
    return ResponseEntity.status(HttpStatus.MULTI_STATUS)
        .body(
            BaseResponse.builder()
                .message(POSTAL_CODE_TIMEZONE_DATA_UPLOAD_PARTIAL_SUCCESS)
                .build());
  }

  public ResponseEntity<BaseResponse<String>> getUploadPostalCodeTimezoneDataFailureResponse() {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(BaseResponse.builder().message(POSTAL_CODE_TIMEZONE_DATA_UPLOAD_FAILED).build());
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
        .timeZone(TIMEZONE)
        .build();
  }

  public BaseResponse<PostalCodeTimezoneDto> getSuccessfulBaseResponseForPostalCodeTimezone() {
    return BaseResponse.builder()
        .message("")
        .success(true)
        .payload(getPostalCodeTimezoneDto())
        .build();
  }

  public BaseResponse<PostalCodeTimezoneDto> getFailedBaseResponseForPostalCodeTimezone() {
    return BaseResponse.builder()
        .message("")
        .success(false)
        .payload(getPostalCodeTimezoneDto())
        .build();
  }

  public ResponseEntity<BaseResponse<String>>
      getUploadWeightageConfigurationDataSuccessfulResponse() {
    return ResponseEntity.ok(
        BaseResponse.builder().message(WEIGHTAGE_CONFIGURATION_DATA_UPLOAD_SUCCESS).build());
  }

  public ResponseEntity<BaseResponse<String>>
      getUploadWeightageConfigurationDataPartiallySuccessfulResponse() {
    return ResponseEntity.status(HttpStatus.MULTI_STATUS)
        .body(
            BaseResponse.builder()
                .message(WEIGHTAGE_CONFIGURATION_DATA_UPLOAD_PARTIAL_SUCCESS)
                .build());
  }

  public ResponseEntity<BaseResponse<String>> getUploadWeightageConfigurationDataFailureResponse() {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(BaseResponse.builder().message(WEIGHTAGE_CONFIGURATION_DATA_UPLOAD_FAILED).build());
  }

  public WeightageConfigurationDto getWeightageConfigurationDto() {
    return WeightageConfigurationDto.builder()
        .orgId(ORG_ID)
        .type("PRIORITY")
        .key("P1")
        .weightage(100F)
        .build();
  }

  public BaseResponse<WeightageConfigurationDto>
      getSuccessfulBaseResponseForWeightageConfiguration() {
    return BaseResponse.builder()
        .message("")
        .success(true)
        .payload(getWeightageConfigurationDto())
        .build();
  }

  public BaseResponse<WeightageConfigurationDto> getFailedBaseResponseForWeightageConfiguration() {
    return BaseResponse.builder()
        .message("")
        .success(false)
        .payload(getWeightageConfigurationDto())
        .build();
  }

  public BaseResponse<CarrierServiceCalendarResponse> getBaseResponseOfCarrierCalendar() {
    return BaseResponse.builder()
        .message("Carrier Calendar added fetched successfully")
        .success(true)
        .payload(getCarrierCalendarResponse())
        .build();
  }

  private CarrierServiceCalendarResponse getCarrierCalendarResponse() {
    return CarrierServiceCalendarResponse.builder()
        .calendarId(CALENDAR_ID)
        .orgId(ORG_ID)
        .carrierServiceId(CARRIER_SERVICE_ID)
        .shippingStage(SHIPPING_STAGE)
        .description(DESCRIPTION)
        .effectiveDate(EFFECTIVE_DATE)
        .build();
  }

  public BaseResponse<NodeCalendarResponse> getBaseResponseOfNodeCalendar() {
    return BaseResponse.builder()
        .message("Node Calendar details added successfully")
        .success(true)
        .payload(getNodeCalendarResponse())
        .build();
  }

  private NodeCalendarResponse getNodeCalendarResponse() {
    return NodeCalendarResponse.builder()
        .calendarId(CALENDAR_ID)
        .orgId(ORG_ID)
        .nodeId(NODE_ID)
        .description(DESCRIPTION)
        .effectiveDate(EFFECTIVE_DATE)
        .build();
  }

  public BaseResponse<NodeCarrierServiceCalendarResponse> getBaseResponseOfNodeCarrierCalendar() {
    return BaseResponse.builder()
        .message("Node Carrier Calendar details added successfully")
        .success(true)
        .payload(getNodeCarrierCalendarResponse())
        .build();
  }

  private NodeCarrierServiceCalendarResponse getNodeCarrierCalendarResponse() {
    return NodeCarrierServiceCalendarResponse.builder()
        .calendarId(CALENDAR_ID)
        .orgId(ORG_ID)
        .nodeId(NODE_ID)
        .carrierServiceId(CARRIER_SERVICE_ID)
        .description(DESCRIPTION)
        .effectiveDate(EFFECTIVE_DATE)
        .build();
  }

  public BaseResponse<CalendarResponse> getBaseResponseOfCalendar() {
    return BaseResponse.builder()
        .message("Calendar details added successfully")
        .success(true)
        .payload(getCalendarResponse())
        .build();
  }

  private CalendarResponse getCalendarResponse() {
    ExceptionDays exceptionDays1 = new ExceptionDays();
    exceptionDays1.setDate("2022-01-01");
    exceptionDays1.setReason("New Year's Day");

    ExceptionDays exceptionDays2 = new ExceptionDays();
    exceptionDays2.setDate("2022-02-21");
    exceptionDays2.setReason("Family Day");
    List<ExceptionDays> exceptionDaysList = Arrays.asList(exceptionDays1, exceptionDays2);

    return CalendarResponse.builder()
        .calendarId(CALENDAR_ID)
        .orgId(ORG_ID)
        .description(DESCRIPTION)
        .isMondayWorking(true)
        .isTuesdayWorking(true)
        .isWednesdayWorking(true)
        .isThursdayWorking(true)
        .isFridayWorking(true)
        .isSaturdayWorking(true)
        .isSundayWorking(true)
        .exceptionDays(exceptionDaysList)
        .build();
  }

  public ResponseEntity<BaseResponse<String>> getBaseResponse(
      HttpStatus httpStatus, String message) {
    return ResponseEntity.status(httpStatus).body(BaseResponse.builder().message(message).build());
  }

  public ResponseEntity<BaseResponse<String>> getUploadTransitDataSuccessfulResponse() {
    return ResponseEntity.ok(BaseResponse.builder().message(TRANSIT_DATA_UPLOAD_SUCCESS).build());
  }

  public ResponseEntity<BaseResponse<String>> getUploadTransitDataPartiallySuccessfulResponse() {
    return ResponseEntity.status(HttpStatus.MULTI_STATUS)
        .body(BaseResponse.builder().message(TRANSIT_DATA_UPLOAD_PARTIAL_SUCCESS).build());
  }

  public ResponseEntity<BaseResponse<String>> getUploadTransitDataFailureResponse() {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(BaseResponse.builder().message(TRANSIT_DATA_UPLOAD_FAILED).build());
  }

  public TransitResponse getTransitResponse(
      String orgId,
      String sourceGeozone,
      String destinationGeozone,
      String carrierServiceId,
      Float transitDays) {
    return TransitResponse.builder()
        .orgId(orgId)
        .sourceGeozone(sourceGeozone)
        .destinationGeozone(destinationGeozone)
        .carrierServiceId(carrierServiceId)
        .transitDays(transitDays)
        .build();
  }

  public BaseResponse<TransitResponse> getSuccessfulBaseResponseForTransit() {
    return BaseResponse.builder()
        .message("")
        .success(true)
        .payload(
            getTransitResponse(
                ORG_ID, SOURCE_GEOZONE, DESTINATION_GEOZONE, CARRIER_SERVICE_ID, TRANSIT_DAYS))
        .build();
  }

  public BaseResponse<TransitResponse> getFailedBaseResponseForTransit() {
    return BaseResponse.builder()
        .message("")
        .success(false)
        .payload(
            getTransitResponse(
                ORG_ID, SOURCE_GEOZONE, DESTINATION_GEOZONE, CARRIER_SERVICE_ID, TRANSIT_DAYS))
        .build();
  }

  public BaseResponse<PagePayload<NodeDto>> getNodeListPaginationBaseResponse() {
    return BaseResponse.builder()
        .message("Node Service List fetched successfully")
        .payload(getNodeListPaginationResponse())
        .build();
  }

  private PagePayload<NodeDto> getNodeListPaginationResponse() {
    PagePayload<NodeDto> pagePayload = new PagePayload<>();

    PagePayload.Pagination pagination = new PagePayload.Pagination();
    pagination.setTotalRecords(2);
    pagination.setTotalPages(2);
    pagination.setCurrentPage(1);
    pagination.setSortOrder("ASC");
    pagination.setSortBy("nodeId");
    pagination.setPrevious(null);
    pagination.setNext("/test/{orgId}?pageNo=2,pageSize=1");
    pagePayload.setPagination(pagination);
    pagePayload.setData(Arrays.asList(getNodeDto(NODE_ID), getNodeDto(NODE_ID_2)));

    return pagePayload;
  }

  private NodeDto getNodeDto(String nodeId) {
    return NodeDto.builder()
        .nodeId(nodeId)
        .orgId(ORG_ID)
        .street(STREET)
        .city(CITY)
        .nodeType(NODE_TYPE)
        .province(PROVINCE)
        .build();
  }

  public BaseResponse<List<NodeCarrierResponse>> getBaseResponseOfNodeCarrierListResponse() {
    return BaseResponse.builder()
        .message("Node Carrier List fetched successfully")
        .success(true)
        .payload(
            Arrays.asList(
                getNodeCarrierResponse2(
                    SERVICE_OPTION, 2.5, getBufferDate(2022, 10, 23), getBufferDate(2023, 11, 20)),
                getNodeCarrierResponse2(
                    SERVICE_OPTION_2,
                    4.5,
                    getBufferDate(2021, 10, 23),
                    getBufferDate(2021, 11, 20))))
        .build();
  }

  public Date getBufferDate(int year, int month, int date) {
    Calendar bufferDate = Calendar.getInstance();
    bufferDate.set(year, month, date);
    return bufferDate.getTime();
  }

  private NodeCarrierResponse getNodeCarrierResponse2(
      String serviceOption, Double bufferHours, Date bufferStartDate, Date bufferEndDate) {
    return NodeCarrierResponse.builder()
        .nodeId(NODE_ID)
        .orgId(ORG_ID)
        .carrierServiceId("")
        .serviceOption(serviceOption)
        .processingTime(PROCESSING_TIME)
        .lastPickupTime(LAST_PICK_UP_TIME)
        .bufferHours(bufferHours)
        .bufferStartDate(bufferStartDate)
        .bufferEndDate(bufferEndDate)
        .build();
  }

  public PagePayload<NodeServiceOptionDto> getNodeServiceOptionPagePayload(Integer pageNo) {
    PagePayload<NodeServiceOptionDto> nodeServiceOptionDtoPagePayload = new PagePayload<>();

    NodeServiceOptionDto nodeServiceOptionDto1 = getNodeServiceOptionDto(NODE_ID);
    NodeServiceOptionDto nodeServiceOptionDto2 = getNodeServiceOptionDto(NODE_ID_2);

    Pagination pagination = new Pagination();
    pagination.setTotalPages(2);
    pagination.setCurrentPage(pageNo);
    pagination.setSortBy("DESC");
    pagination.setTotalRecords(4);
    nodeServiceOptionDtoPagePayload.setPagination(pagination);
    nodeServiceOptionDtoPagePayload.setData(
        Arrays.asList(nodeServiceOptionDto1, nodeServiceOptionDto2));

    return nodeServiceOptionDtoPagePayload;
  }

  private NodeServiceOptionDto getNodeServiceOptionDto(String nodeId) {
    Map<String, Double> processingTime = new HashMap<>();
    processingTime.put("SDND", 12.0);
    processingTime.put("Standard", 4.0);

    return NodeServiceOptionDto.builder()
        .nodeId(nodeId)
        .orgId(ORG_ID)
        .serviceOptions(Arrays.asList("SDND", "Standard"))
        .street(STREET)
        .nodeType(NODE_TYPE)
        .processingTime(processingTime)
        .isActive(true)
        .build();
  }

  public PageParams getPageParams(
      Optional<Integer> pageNo,
      Optional<Integer> pageSize,
      Optional<String> sortBy,
      Optional<String> sortOrder) {
    PageParams pageParams = new PageParams();
    pageParams.setPageNo(pageNo);
    pageParams.setPageSize(pageSize);
    pageParams.setSortBy(sortBy);
    pageParams.setSortOrder(sortOrder);
    return pageParams;
  }

  public BaseResponse<PagePayload<CarrierServiceResponse>>
      getCarrierServiceListWithPaginationBaseResponse() {
    return BaseResponse.builder()
        .message("Carrier Service List fetched successfully")
        .payload(getCarrierServiceListWithPaginationResponse())
        .build();
  }

  private PagePayload<CarrierServiceResponse> getCarrierServiceListWithPaginationResponse() {
    PagePayload<CarrierServiceResponse> pagePayload = new PagePayload<>();

    PagePayload.Pagination pagination = new PagePayload.Pagination();
    pagination.setTotalRecords(2);
    pagination.setTotalPages(2);
    pagination.setCurrentPage(1);
    pagination.setSortOrder("ASC");
    pagination.setSortBy("carrierId");
    pagination.setPrevious(null);
    pagination.setNext("/test/{orgId}?pageNo=2,pageSize=1");
    pagePayload.setPagination(pagination);
    pagePayload.setData(Arrays.asList(getCarrierResponse(), getCarrierResponse2()));

    return pagePayload;
  }

  public BaseResponse<List<CarrierServiceCalendarResponse>>
      getCarrierServiceCalendarBaseResponse() {
    return BaseResponse.builder()
        .message("Carrier Calendar fetched successfully")
        .payload(Arrays.asList(getCarrierCalendarResponse()))
        .build();
  }

  public BaseResponse<TransitTimeEntriesDto> getTransitTimeEntriesDtoBaseResponse(
      Integer transitRecords) {
    return BaseResponse.builder()
        .message("Transit Entries fetched successfully")
        .payload(getTransitTimeEntriesDto(transitRecords))
        .build();
  }

  private TransitTimeEntriesDto getTransitTimeEntriesDto(Integer transitRecords) {
    return TransitTimeEntriesDto.builder()
        .orgId(ORG_ID)
        .carrierServiceId(CARRIER_SERVICE_ID)
        .totalRecords(transitRecords)
        .build();
  }

  public PagePayload<CarrierTransitDto> getCarrierTransitPagePayload(Integer pageNo) {
    PagePayload<CarrierTransitDto> carrierTransitDtoPagePayload = new PagePayload<>();

    CarrierTransitDto carrierTransitDto1 = getCarrierTransitDto(CARRIER_ID);
    CarrierTransitDto carrierTransitDto2 = getCarrierTransitDto(CARRIER_ID_2);
    Pagination pagination = new Pagination();
    pagination.setTotalPages(2);
    pagination.setCurrentPage(pageNo);
    pagination.setSortBy("DESC");
    pagination.setTotalRecords(4);
    carrierTransitDtoPagePayload.setPagination(pagination);
    carrierTransitDtoPagePayload.setData(Arrays.asList(carrierTransitDto1, carrierTransitDto2));

    return carrierTransitDtoPagePayload;
  }

  private CarrierTransitDto getCarrierTransitDto(String carrierId) {
    return CarrierTransitDto.builder()
        .orgId(ORG_ID)
        .carrierServiceId(CARRIER_SERVICE_ID)
        .carrierId(carrierId)
        .isCarrierActive(true)
        .isCalendarAssigned(true)
        .carrierName(CARRIER_NAME)
        .serviceName(SERVICE_NAME)
        .carrierServiceCalendars(
            Arrays.asList(
                getCarrierServiceCalendars(CALENDAR_ID, EFFECTIVE_DATE),
                getCarrierServiceCalendars(CARRIER_ID_2, EFFECTIVE_DATE)))
        .build();
  }

  private CarrierServiceCalendars getCarrierServiceCalendars(
      String calendarId, String effectiveDate) {
    CarrierServiceCalendars carrierServiceCalendars = new CarrierServiceCalendars();
    carrierServiceCalendars.setCalendarId(calendarId);
    carrierServiceCalendars.setEffectiveDate(effectiveDate);
    return carrierServiceCalendars;
  }

  public BaseResponse<TransitResponse> getBaseResponseOfTransitResponse() {
    return BaseResponse.builder()
        .message("Node carrier details fetched successfully")
        .success(true)
        .payload(getTransitResponse())
        .build();
  }

  private TransitResponse getTransitResponse() {
    return TransitResponse.builder()
        .orgId(ORG_ID)
        .carrierServiceId(CARRIER_SERVICE_ID)
        .sourceGeozone(SOURCE_GEOZONE)
        .destinationGeozone(DESTINATION_GEOZONE)
        .bufferDays(0.1)
        .build();
  }

  public ResponseEntity<BaseResponse<String>> getNodeCarrierSelectionDataSuccessfulResponse() {
    return ResponseEntity.ok(
        BaseResponse.builder().message(NODE_CARRIER_SELECTION_DATA_UPLOAD_SUCCESS).build());
  }

  public ResponseEntity<BaseResponse<String>> getNodeCarrierSelectionPartiallySuccessfulResponse() {
    return ResponseEntity.status(HttpStatus.MULTI_STATUS)
        .body(
            BaseResponse.builder()
                .message(NODE_CARRIER_SELECTION_DATA_UPLOAD_PARTIAL_SUCCESS)
                .build());
  }

  public ResponseEntity<BaseResponse<String>> getNodeCarrierSelectionFailureResponse() {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(BaseResponse.builder().message(NODE_CARRIER_SELECTION_DATA_UPLOAD_FAILED).build());
  }

  public BaseResponse<NodeCarrierSelectionResponse> getSuccessfulBaseResponse() {
    return BaseResponse.builder().success(true).payload(getNodeCarrierSelectionResponse()).build();
  }

  public NodeCarrierSelectionResponse getNodeCarrierSelectionResponse() {
    return NodeCarrierSelectionResponse.builder()
        .orgId(ORG_ID)
        .serviceOption(SERVICE_OPTION)
        .priority("1")
        .destinationGeozone(DESTINATION_GEOZONE)
        .sourceGeozone(SOURCE_GEOZONE)
        .build();
  }

  public BaseResponse<NodeCarrierSelectionResponse> getFailedBaseResponseForNodeCarrierSelection() {
    return BaseResponse.builder().success(false).payload(getNodeCarrierSelectionResponse()).build();
  }

  private CalendarResponse getCalendarResponse2() {
    ExceptionDays exceptionDays1 = new ExceptionDays();
    exceptionDays1.setDate("2022-01-01");
    exceptionDays1.setReason("New Year's Day");

    List<ExceptionDays> exceptionDaysList = List.of(exceptionDays1);

    return CalendarResponse.builder()
        .calendarId(CALENDAR_ID_2)
        .orgId(ORG_ID)
        .description(DESCRIPTION)
        .isMondayWorking(true)
        .isTuesdayWorking(true)
        .isWednesdayWorking(true)
        .isThursdayWorking(true)
        .isFridayWorking(true)
        .isSaturdayWorking(false)
        .isSundayWorking(false)
        .exceptionDays(exceptionDaysList)
        .build();
  }

  public BaseResponse<PagePayload<CalendarResponse>> getCalendarWithPaginationBaseResponse() {
    return BaseResponse.builder()
        .message("Carrier Service Calendars fetched successfully")
        .payload(getCarrierServiceCalendarWithPaginationResponse())
        .build();
  }

  private PagePayload<CalendarResponse> getCarrierServiceCalendarWithPaginationResponse() {
    PagePayload<CalendarResponse> payload = new PagePayload<>();

    PagePayload.Pagination pagination = new PagePayload.Pagination();
    pagination.setTotalRecords(2);
    pagination.setTotalPages(2);
    pagination.setCurrentPage(1);
    pagination.setSortOrder("ASC");
    pagination.setSortBy("carrierId");
    pagination.setPrevious(null);
    pagination.setNext("/test/{orgId}?pageNo=2,pageSize=1");
    payload.setPagination(pagination);
    payload.setData(List.of(getCalendarResponse(), getCalendarResponse2()));

    return payload;
  }

  public BaseResponse<List<NodeCalendarResponse>> getNodeCalendarBaseResponse() {
    return BaseResponse.builder()
        .message("Node Service Calendars fetched successfully")
        .payload(List.of(getNodeCalendarResponse()))
        .build();
  }

  public BaseResponse<List<CarrierServiceCalendarResponse>> getCarrierCalendarBaseResponse() {
    return BaseResponse.builder()
        .message("Carrier Service Calendars fetched successfully")
        .payload(List.of(getCarrierResponse()))
        .build();
  }

  public BaseResponse<List<NodeCalendarResponse>> getEmptyNodeCalendarBaseResponse() {
    return BaseResponse.builder()
        .message("Node Service Calendars fetched successfully")
        .payload(new ArrayList<>())
        .build();
  }

  public BaseResponse<List<CarrierServiceCalendarResponse>> getEmptyCarrierCalendarBaseResponse() {
    return BaseResponse.builder()
        .message("Carrier Service Calendars fetched successfully")
        .payload(new ArrayList<>())
        .build();
  }

  public PagePayload<CalendarDto> getCalendarPagePayload(int pageNo) {
    PagePayload<CalendarDto> calendarDtoPagePayload = new PagePayload<>();

    CalendarDto calendarDto1 = getCalendarDto(CALENDAR_ID);
    CalendarDto calendarDto2 = getCalendarDto(CALENDAR_ID_2);

    Pagination pagination = new Pagination();
    pagination.setTotalPages(2);
    pagination.setCurrentPage(pageNo);
    pagination.setSortBy("DESC");
    pagination.setTotalRecords(2);
    calendarDtoPagePayload.setPagination(pagination);
    calendarDtoPagePayload.setData(Arrays.asList(calendarDto1, calendarDto2));

    return calendarDtoPagePayload;
  }

  private CalendarDto getCalendarDto(String calendarId) {
    ExceptionDays exceptionDays1 = new ExceptionDays();
    exceptionDays1.setDate("2022-01-01");
    exceptionDays1.setReason("New Year's Day");

    List<ExceptionDays> exceptionDaysList = List.of(exceptionDays1);
    CalendarDto calendarDto = new CalendarDto();

    calendarDto.setCalendarId(calendarId);
    calendarDto.setOrgId(ORG_ID);
    calendarDto.setDescription(DESCRIPTION);
    calendarDto.setExceptionDays(exceptionDaysList);
    calendarDto.setIsActive(true);
    calendarDto.setIsMondayWorking(true);
    calendarDto.setIsSundayWorking(false);
    calendarDto.setIsFridayWorking(true);

    return calendarDto;
  }

  public BaseResponse<List<MarketRegionDto>> getMarketRegionDto() {
    MarketRegionDto marketRegionDto =
        new MarketRegionDto() {
          @Override
          public String getCountry() {
            return COUNTRY;
          }

          @Override
          public long getNoOfStates() {
            return 0;
          }

          @Override
          public long getNoOfCities() {
            return 0;
          }

          @Override
          public long getNoOfPostalCodePrefixes() {
            return 0;
          }

          @Override
          public Date getUploadDate() {
            return null;
          }

          @Override
          public void setUploadDate(String v) {}
        };
    return BaseResponse.builder()
        .message("Market Region fetched successfully")
        .payload(List.of(marketRegionDto))
        .build();
  }

  public BaseResponse<List<NodeCarrierResponse>>
      getBaseResponseOfNodeCarrierListResponseWithNullValues() {
    return BaseResponse.builder()
        .message("Node Carrier List fetched successfully")
        .success(true)
        .payload(Arrays.asList(getNodeCarrierResponse2(SERVICE_OPTION, null, null, null)))
        .build();
  }

  public PagePayload<ProcessingTimeBufferDto> getProcessingTimeBufferPagePayload(int pageNo) {
    PagePayload<ProcessingTimeBufferDto> processingTimeBufferDtoPagePayload = new PagePayload<>();

    ProcessingTimeBufferDto processingTimeBufferDto1 = getProcessingTimeBufferDto(NODE_ID);
    ProcessingTimeBufferDto processingTimeBufferDto2 = getProcessingTimeBufferDto(NODE_ID_2);

    Pagination pagination = new Pagination();
    pagination.setTotalPages(2);
    pagination.setCurrentPage(pageNo);
    pagination.setSortBy("DESC");
    pagination.setTotalRecords(2);
    processingTimeBufferDtoPagePayload.setPagination(pagination);
    processingTimeBufferDtoPagePayload.setData(
        Arrays.asList(processingTimeBufferDto1, processingTimeBufferDto2));

    return processingTimeBufferDtoPagePayload;
  }

  private ProcessingTimeBufferDto getProcessingTimeBufferDto(String nodeId) {
    return ProcessingTimeBufferDto.builder()
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
    processingTimeBuffer.setBufferStartDate(getBufferDate(2022, 10, 10));
    processingTimeBuffer.setBufferEndDate(getBufferDate(2022, 11, 10));
    processingTimeBuffer.setStatus("Active");
    return processingTimeBuffer;
  }
}
