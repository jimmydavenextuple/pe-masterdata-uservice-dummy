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
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.MHF_ELIGIBLE;
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
import com.hbc.common.response.BaseResponse;
import com.hbc.node.carrier.domain.outbound.NodeCarrierResponse;
import com.hbc.node.domain.outbound.NodeResponse;
import com.hbc.postal.code.timezone.api.domain.dto.PostalCodeTimezoneDto;
import com.hbc.promise.sourcing.rule.api.domain.dto.PromiseSourcingRuleDto;
import com.hbc.transit.domain.outbound.TransitResponse;
import com.hbc.weightage.configuration.api.domain.dto.WeightageConfigurationDto;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
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
  public static String SOURCE_GEOZONE = "SGZ";
  public static String DESTINATION_GEOZONE = "DGZ";
  public static Float TRANSIT_DAYS = Float.valueOf(1);

  private NodeCarrierResponse getNodeCarrierResponse() {
    return NodeCarrierResponse.builder()
        .nodeId(NODE_ID)
        .orgId(ORG_ID)
        .carrierServiceId(CARRIER_SERVICE_ID)
        .serviceOption(SERVICE_OPTION)
        .processingTime(PROCESSING_TIME)
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
        NEXTDAY_ELIGIBLE, Boolean.TRUE,
        MHF_ELIGIBLE, Boolean.TRUE);
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
}
