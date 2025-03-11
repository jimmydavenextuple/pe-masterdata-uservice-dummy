/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.dataupload.util;

import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.ALLOCATION_RULE_ID;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.CARRIER_ID;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.CARRIER_NAME;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.CITY;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.COUNTRY;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.DESTINATION_GEO_ZONE;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.EXPRESS_ELIGIBLE;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.LATITUDE;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.LONGITUDE;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.NEXTDAY_ELIGIBLE;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.SDND_ELIGIBLE;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.SERVICE_NAME;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.SERVICE_OPTIONS;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.SOURCE_NODES;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.STATE;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.STREET;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.TIMEZONE;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.ZIP_CODE;
import static com.nextuple.dataupload.helper.CarrierDataUploadConstants.CARRIER_DATA_UPLOAD_FAILED;
import static com.nextuple.dataupload.helper.CarrierDataUploadConstants.CARRIER_DATA_UPLOAD_PARTIAL_SUCCESS;
import static com.nextuple.dataupload.helper.CarrierDataUploadConstants.CARRIER_DATA_UPLOAD_SUCCESS;
import static com.nextuple.dataupload.helper.NodeCarrierSelectionUploadConstants.NODE_CARRIER_SELECTION_DATA_UPLOAD_FAILED;
import static com.nextuple.dataupload.helper.NodeCarrierSelectionUploadConstants.NODE_CARRIER_SELECTION_DATA_UPLOAD_PARTIAL_SUCCESS;
import static com.nextuple.dataupload.helper.NodeCarrierSelectionUploadConstants.NODE_CARRIER_SELECTION_DATA_UPLOAD_SUCCESS;
import static com.nextuple.dataupload.helper.NodeDataUploadConstants.NODE_DATA_UPLOAD_FAILED;
import static com.nextuple.dataupload.helper.NodeDataUploadConstants.NODE_DATA_UPLOAD_PARTIAL_SUCCESS;
import static com.nextuple.dataupload.helper.NodeDataUploadConstants.NODE_DATA_UPLOAD_SUCCESS;
import static com.nextuple.dataupload.helper.PostalCodeTimezoneDataUploadConstants.POSTAL_CODE_TIMEZONE_DATA_UPLOAD_FAILED;
import static com.nextuple.dataupload.helper.PostalCodeTimezoneDataUploadConstants.POSTAL_CODE_TIMEZONE_DATA_UPLOAD_PARTIAL_SUCCESS;
import static com.nextuple.dataupload.helper.PostalCodeTimezoneDataUploadConstants.POSTAL_CODE_TIMEZONE_DATA_UPLOAD_SUCCESS;
import static com.nextuple.dataupload.helper.PromiseSourcingRuleDataUploadConstants.PROMISE_SOURCING_RULE_DATA_UPLOAD_FAILED;
import static com.nextuple.dataupload.helper.PromiseSourcingRuleDataUploadConstants.PROMISE_SOURCING_RULE_DATA_UPLOAD_PARTIAL_SUCCESS;
import static com.nextuple.dataupload.helper.PromiseSourcingRuleDataUploadConstants.PROMISE_SOURCING_RULE_DATA_UPLOAD_SUCCESS;
import static com.nextuple.dataupload.helper.TransitDataUploadConstants.TRANSIT_DATA_UPLOAD_FAILED;
import static com.nextuple.dataupload.helper.TransitDataUploadConstants.TRANSIT_DATA_UPLOAD_PARTIAL_SUCCESS;
import static com.nextuple.dataupload.helper.TransitDataUploadConstants.TRANSIT_DATA_UPLOAD_SUCCESS;
import static com.nextuple.dataupload.helper.WeightageConfigurationDataUploadConstants.WEIGHTAGE_CONFIGURATION_DATA_UPLOAD_FAILED;
import static com.nextuple.dataupload.helper.WeightageConfigurationDataUploadConstants.WEIGHTAGE_CONFIGURATION_DATA_UPLOAD_PARTIAL_SUCCESS;
import static com.nextuple.dataupload.helper.WeightageConfigurationDataUploadConstants.WEIGHTAGE_CONFIGURATION_DATA_UPLOAD_SUCCESS;
import static org.junit.jupiter.api.parallel.Resources.TIME_ZONE;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.nextuple.calendar.domain.outbound.CalendarResponse;
import com.nextuple.calendar.domain.outbound.CarrierServiceCalendarResponse;
import com.nextuple.calendar.domain.outbound.NodeCalendarResponse;
import com.nextuple.calendar.domain.outbound.NodeCarrierServiceCalendarResponse;
import com.nextuple.calendar.domain.pojo.ExceptionDays;
import com.nextuple.carrier.domain.outbound.CarrierServiceResponse;
import com.nextuple.common.base.PagePayload;
import com.nextuple.common.base.PagePayload.Pagination;
import com.nextuple.common.pojo.PageParams;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.configuration.outbound.TenantConfigdataResponse;
import com.nextuple.dataupload.common.inbound.ConfigureShipChargeCappingRequest;
import com.nextuple.dataupload.common.inbound.DeleteTargetProfitMarginRequest;
import com.nextuple.dataupload.common.inbound.TargetProfitMarginRequest;
import com.nextuple.dataupload.common.outbound.AttributeAndValuesTGMResponse;
import com.nextuple.dataupload.common.outbound.ConfigureShipChargeCappingResponse;
import com.nextuple.dataupload.common.outbound.NodeCarrierServiceAndServiceOptionResponse;
import com.nextuple.dataupload.common.outbound.ProcessingTimeBufferResponse;
import com.nextuple.dataupload.common.outbound.ShipChargeDetailsTGMResponse;
import com.nextuple.dataupload.common.outbound.TargetProfitMarginResponse;
import com.nextuple.dataupload.common.outbound.TransitBufferDetailsResponse;
import com.nextuple.dataupload.common.pojo.ActiveCombination;
import com.nextuple.dataupload.common.pojo.ProcessingTimeBuffer;
import com.nextuple.dataupload.domain.dto.CalendarDto;
import com.nextuple.dataupload.domain.dto.CarrierTransitDto;
import com.nextuple.dataupload.domain.dto.NodeCarrierServiceResponse;
import com.nextuple.dataupload.domain.dto.NodeListDto;
import com.nextuple.dataupload.domain.dto.NodeServiceOptionDto;
import com.nextuple.dataupload.domain.dto.NodeWorkingCalendarDto;
import com.nextuple.dataupload.domain.dto.PickupTimeDto;
import com.nextuple.dataupload.domain.pojo.CarrierServiceCalendar;
import com.nextuple.dataupload.domain.pojo.PickUpCalendar;
import com.nextuple.dataupload.domain.pojo.ProcessingTimeDetails;
import com.nextuple.item.domain.outbound.ItemListResponse;
import com.nextuple.node.carrier.domain.outbound.NodeCarrierResponse;
import com.nextuple.node.carrier.domain.outbound.NodeCarrierSelectionResponse;
import com.nextuple.node.domain.dto.NodeDto;
import com.nextuple.node.domain.outbound.NodeResponse;
import com.nextuple.postal.code.timezone.api.domain.dto.CustomRegionInfo;
import com.nextuple.postal.code.timezone.api.domain.dto.MarketRegionInfo;
import com.nextuple.postal.code.timezone.api.domain.dto.PostalCodeTimezoneDto;
import com.nextuple.promise.sourcing.rule.api.domain.dto.PromiseSourcingRuleDto;
import com.nextuple.sourcing.cost.config.dto.*;
import com.nextuple.sourcing.cost.config.inbound.CostDefinitionRequest;
import com.nextuple.sourcing.cost.config.outbound.CostDefinitionResponse;
import com.nextuple.transit.domain.dto.TransitTimeEntriesDto;
import com.nextuple.transit.domain.inbound.DistinctGeozonesResponse;
import com.nextuple.transit.domain.outbound.TransferScheduleResponse;
import com.nextuple.transit.domain.outbound.TransitBufferConfigResponse;
import com.nextuple.transit.domain.outbound.TransitResponse;
import com.nextuple.weightage.configuration.api.domain.dto.WeightageConfigurationDto;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import org.jetbrains.annotations.NotNull;
import org.joda.time.DateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
  public static final String NODE_TYPE = "MFC";
  public static final String SORT_BY = "customRegionId";
  public static final String SORT_ORDER_DESC = "DESC";
  public static final String NODE_ID_2 = "Node_Id_02";
  public static final String CARRIER_SERVICE_ID_2 = "Carrier_Service_Id_02";
  public static final String SERVICE_OPTION_2 = "SDND";
  private static final String SERVICE_OPTION_STD = "STANDARD";
  private static final String SERVICE_OPTION_EXP = "EXPRESS";
  public static String SOURCE_GEOZONE = "SGZ";
  public static String DESTINATION_GEOZONE = "DGZ";
  public static Float TRANSIT_DAYS = 1F;
  public static final String CARRIER_ID_2 = "Carrier_Id_2";
  public static final String ITEM_ID = "item1";
  public static final String ITEM_IDS = "item1,item2";
  public static final String UOM = "each";

  private static final String BUFFER_START_DATE = "startTime";
  public static final String KEY = "key";

  public static final String VALUE = "value";

  public static final String TYPE = "type";
  public static final String CALENDAR_ID_2 = "Calendar_Id_02";
  public static final String EFFECTIVE_DATE_2 = "2024-01-01";
  public static final String ZIP_CODE_PREFIX = "ABC";
  public static final String COST_TYPE = "SHIPPING_COST";
  public static final String SELECTOR_CF_VALUE = "UPS_GROUND";
  public static final String SELECTOR_CF = "carrierServiceId";
  public static final String FILTER_COST_FACTOR = "Surge";
  public static final String FILTER_COST_FACTOR_VALUE = "NON_HOLIDAYS";
  public static final String ITEM_ID_1 = "item-01";
  public static final String ITEM_ID_2 = "item-02";
  public static final String ITEM_HANDLING_TYPE = "Conveyable";
  public static final String SHORT_DESC = "BoldFit Rope";
  private static final String ITEMSOURCE = "ORG";
  public static final Long LEAD_TIME = 1L;
  public static final String ATTRIBUTE_VALUE_KITCHEN = "KITCHEN";
  public static final String ATTRIBUTE_NAME_ITEM_CATEGORY = "itemCategory";
  public static final Integer TARGET_GROSS_PROFIT_MARGIN = 10;
  public static final String ATTRIBUTE_VALUE_ELECTRONICS = "ELECTRONICS";
  public static final String CONFIG_KEY_ITEM_CATEGORY =
      "target-gross-profit-margins-" + ATTRIBUTE_NAME_ITEM_CATEGORY;
  public static final String CONFIG_VALUE_ITEM_CATEGORY =
      ATTRIBUTE_VALUE_ELECTRONICS + ":" + TARGET_GROSS_PROFIT_MARGIN;
  public static final String CONFIG_VALUE_ITEM_KITCHEN =
      ATTRIBUTE_VALUE_KITCHEN + ":" + TARGET_GROSS_PROFIT_MARGIN;
  public static final String CONFIG_VALUE_ITEM_CATEGORY_UPDATED =
      ATTRIBUTE_VALUE_ELECTRONICS
          + ":"
          + TARGET_GROSS_PROFIT_MARGIN
          + ","
          + ATTRIBUTE_VALUE_KITCHEN
          + ":"
          + TARGET_GROSS_PROFIT_MARGIN;
  public static final String CONFIG_VALUE_ITEM_CATEGORY_UPDATED_TARGET_MARGIN =
      ATTRIBUTE_VALUE_KITCHEN + ":" + TARGET_GROSS_PROFIT_MARGIN;
  public static final String SHIP_CHARGE_CAPPING_CONSTANT = "10,20";
  public static final String SHIP_CHARGE_CAPPING_STATUS = "true";
  public static final String ID = "CRID1";
  public static final String ID_2 = "CRID2";
  public static final String CUSTOM_REGION_DESC = "ABC";
  public static final String CUSTOM_REGION_NAME = "CR1";
  public static final List<String> PARTIAL_CODES = Arrays.asList("T2P", "T3P");
  private static final JsonNode customAttributes =
      JsonNodeFactory.instance.objectNode().put("key1", "value1").put("key2", "value2");

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
        .zipCode(ZIP_CODE)
        .serviceOptionEligibilities(getServiceOptionEligibilities())
        .state(STATE)
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

  public BaseResponse<NodeResponse> getSuccessfulBaseResponseForNodeWithStartAndLastWorkingTime() {
    NodeResponse nodeResponse = getNodeResponse();
    nodeResponse.setStartWorkingTime("08:00");
    nodeResponse.setLastWorkingTime("18:00");
    return BaseResponse.builder().message("").success(true).payload(nodeResponse).build();
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
        .zipCodePrefix(ZIP_CODE_PREFIX)
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
        .customAttributes(customAttributes)
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
        .payload(getCarrierCalendarResponse(EFFECTIVE_DATE))
        .build();
  }

  private CarrierServiceCalendarResponse getCarrierCalendarResponse(String effectiveDate) {
    return CarrierServiceCalendarResponse.builder()
        .calendarId(CALENDAR_ID)
        .orgId(ORG_ID)
        .carrierServiceId(CARRIER_SERVICE_ID)
        .shippingStage(SHIPPING_STAGE)
        .description(DESCRIPTION)
        .effectiveDate(effectiveDate)
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
    Map<String, Boolean> serviceOptionEligibilities = new HashMap<>();
    serviceOptionEligibilities.put("sdndEligible", Boolean.TRUE);
    serviceOptionEligibilities.put("expressEligible", Boolean.TRUE);
    serviceOptionEligibilities.put("standardEligible", Boolean.FALSE);
    return NodeDto.builder()
        .nodeId(nodeId)
        .orgId(ORG_ID)
        .street(STREET)
        .city(CITY)
        .nodeType(NODE_TYPE)
        .state(STATE)
        .serviceOptionEligibilities(serviceOptionEligibilities)
        .build();
  }

  public BaseResponse<List<NodeCarrierResponse>> getBaseResponseOfNodeCarrierListResponse() {
    DateTime today = new DateTime();
    return BaseResponse.builder()
        .message("Node Carrier List fetched successfully")
        .success(true)
        .payload(
            Arrays.asList(
                getNodeCarrierResponse2(
                    SERVICE_OPTION,
                    2.5,
                    getBufferDate(2022, 10, 23),
                    getBufferDate(today.getYear(), today.getMonthOfYear(), today.getDayOfMonth())),
                getNodeCarrierResponse2(
                    SERVICE_OPTION_2,
                    4.5,
                    getBufferDate(2021, 10, 23),
                    getBufferDate(2021, 11, 20))))
        .build();
  }

  public BaseResponse<List<NodeCarrierResponse>> getBaseResponseOfNodeCarrierListEmptyResponse() {
    return BaseResponse.builder()
        .message("Node Carrier List fetched successfully")
        .success(true)
        .payload(List.of())
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
        .carrierServiceId(CARRIER_SERVICE_ID)
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
        .customAttributes(customAttributes)
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
        .payload(Arrays.asList(getCarrierCalendarResponse(EFFECTIVE_DATE)))
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
        .carrierServiceCalendar(getCarrierServiceCalendars(CALENDAR_ID, EFFECTIVE_DATE))
        .build();
  }

  private CarrierServiceCalendar getCarrierServiceCalendars(
      String calendarId, String effectiveDate) {
    CarrierServiceCalendar carrierServiceCalendar = new CarrierServiceCalendar();
    carrierServiceCalendar.setCalendarId(calendarId);
    carrierServiceCalendar.setEffectiveDate(effectiveDate);
    return carrierServiceCalendar;
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

  public DistinctGeozonesResponse geozonesResponse() {
    DistinctGeozonesResponse response = new DistinctGeozonesResponse();
    response.setDestinationGeozones(Arrays.asList(DESTINATION_GEOZONE, DESTINATION_GEOZONE + "1"));
    response.setSourceGeozones(Arrays.asList(SOURCE_GEOZONE, SOURCE_GEOZONE + "1"));

    return response;
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

  public BaseResponse<List<MarketRegionInfo>> getMarketRegionInfo() {
    MarketRegionInfo marketRegionInfo = new MarketRegionInfo();
    marketRegionInfo.setCountry("CA");
    marketRegionInfo.setUploadDate(new Date().toString());
    return BaseResponse.builder()
        .message("Market Region fetched successfully")
        .payload(List.of(marketRegionInfo))
        .build();
  }

  public PickUpCalendar getPickUpCalendar() {
    PickUpCalendar pickUpCalendar = new PickUpCalendar();
    pickUpCalendar.setCalendarId(CALENDAR_ID);
    pickUpCalendar.setCarrierServiceId(CARRIER_SERVICE_ID);
    pickUpCalendar.setNodeId(NODE_ID);

    return pickUpCalendar;
  }

  public NodeCarrierServiceResponse getNodeCarrierServiceResponse() {
    NodeCarrierServiceResponse nodeCarrierServiceResponse = new NodeCarrierServiceResponse();
    nodeCarrierServiceResponse.setNodeId(NODE_ID);
    nodeCarrierServiceResponse.setOrgId(ORG_ID);
    nodeCarrierServiceResponse.setStreet(STREET);
    nodeCarrierServiceResponse.setCity(CITY);
    nodeCarrierServiceResponse.setState(STATE);
    nodeCarrierServiceResponse.setZipCode(ZIP_CODE);
    nodeCarrierServiceResponse.setCarrierServices(List.of(CARRIER_SERVICE_ID));
    nodeCarrierServiceResponse.setPickupCalendar(List.of(getPickUpCalendar()));
    nodeCarrierServiceResponse.setCustomAttributes(customAttributes);

    return nodeCarrierServiceResponse;
  }

  public PagePayload<NodeCarrierServiceResponse> getNodeCarrierServicePagePayload(Integer pageNo) {
    PagePayload<NodeCarrierServiceResponse> nodeCarrierServicePagePayload = new PagePayload<>();

    NodeCarrierServiceResponse nodeCarrierServiceResponse = getNodeCarrierServiceResponse();

    Pagination pagination = new Pagination();
    pagination.setTotalPages(2);
    pagination.setCurrentPage(pageNo);
    pagination.setSortBy("DESC");
    pagination.setTotalRecords(4);
    nodeCarrierServicePagePayload.setPagination(pagination);
    nodeCarrierServicePagePayload.setData(List.of(nodeCarrierServiceResponse));

    return nodeCarrierServicePagePayload;
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
    response.setState(STATE);
    response.setZipCode(ZIP_CODE);
    response.setCarrierServices(List.of(CARRIER_SERVICE_ID));
    response.setServiceOptions(List.of(SERVICE_OPTION));
    response.setActiveCombination(List.of(getActiveCombination()));
    response.setCustomAttributes(customAttributes);

    return response;
  }

  public PagePayload<NodeCarrierServiceAndServiceOptionResponse>
      getNodeCarrierServiceAndServiceOptionResponse(Integer pageNo) {
    PagePayload<NodeCarrierServiceAndServiceOptionResponse> nodeCarrierServicePagePayload =
        new PagePayload<>();

    NodeCarrierServiceAndServiceOptionResponse response =
        getNodeCarrierServiceAndServiceOptionResponse();

    Pagination pagination = new Pagination();
    pagination.setTotalPages(2);
    pagination.setCurrentPage(pageNo);
    pagination.setSortBy("DESC");
    pagination.setTotalRecords(4);
    nodeCarrierServicePagePayload.setPagination(pagination);
    nodeCarrierServicePagePayload.setData(List.of(response));

    return nodeCarrierServicePagePayload;
  }

  public NodeCarrierServiceCalendarResponse getNodeCarrierServiceCalendarResponse() {
    NodeCarrierServiceCalendarResponse nodeCarrierServiceCalendarResponse =
        new NodeCarrierServiceCalendarResponse();
    nodeCarrierServiceCalendarResponse.setNodeId(NODE_ID);
    nodeCarrierServiceCalendarResponse.setCarrierServiceId(CARRIER_SERVICE_ID);
    nodeCarrierServiceCalendarResponse.setCalendarId(CALENDAR_ID);
    nodeCarrierServiceCalendarResponse.setOrgId(ORG_ID);
    nodeCarrierServiceCalendarResponse.setEffectiveDate(EFFECTIVE_DATE);
    nodeCarrierServiceCalendarResponse.setDescription(DESCRIPTION);

    return nodeCarrierServiceCalendarResponse;
  }

  public PagePayload<NodeListDto> getNodeListPagePayload(Integer pageNo) {
    PagePayload<NodeListDto> nodeListDtoPagePayload = new PagePayload<>();

    NodeListDto nodeListDto = getNodeListDto(NODE_ID);

    Pagination pagination = new Pagination();
    pagination.setTotalPages(2);
    pagination.setCurrentPage(pageNo);
    pagination.setSortBy("DESC");
    pagination.setTotalRecords(4);
    nodeListDtoPagePayload.setPagination(pagination);
    nodeListDtoPagePayload.setData(Arrays.asList(nodeListDto));

    return nodeListDtoPagePayload;
  }

  private NodeListDto getNodeListDto(String nodeId) {
    NodeListDto nodeListDto = new NodeListDto();
    nodeListDto.setNodeId(nodeId);
    nodeListDto.setOrgId(ORG_ID);
    nodeListDto.setIsActive(Boolean.TRUE);
    nodeListDto.setCarrierServices(List.of(CARRIER_SERVICE_ID));
    nodeListDto.setServiceOptions(List.of(SERVICE_OPTION));
    PickupTimeDto pickupTimeDto = new PickupTimeDto();
    pickupTimeDto.setNodeId(nodeId);
    pickupTimeDto.setCarrierServiceId(CARRIER_SERVICE_ID);
    pickupTimeDto.setPickupTime(LAST_PICK_UP_TIME);
    nodeListDto.setPickupTime(List.of(pickupTimeDto));
    NodeWorkingCalendarDto nodeWorkingCalendarDto = new NodeWorkingCalendarDto();
    nodeWorkingCalendarDto.setCalendarId(CALENDAR_ID);
    nodeWorkingCalendarDto.setEffectiveDate(EFFECTIVE_DATE);
    nodeListDto.setNodeWorkingCalendar(nodeWorkingCalendarDto);
    nodeListDto.setCustomAttributes(customAttributes);
    return nodeListDto;
  }

  public BaseResponse<List<NodeCalendarResponse>> getBaseResponseOfNodeCalendarList() {
    return BaseResponse.builder()
        .message("Node Calendar details added successfully")
        .success(true)
        .payload(List.of(getNodeCalendarResponse()))
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

  public PagePayload<ProcessingTimeBufferResponse> getProcessingTimeBufferPagePayload(int pageNo) {
    PagePayload<ProcessingTimeBufferResponse> processingTimeBufferDtoPagePayload =
        new PagePayload<>();

    ProcessingTimeBufferResponse processingTimeBufferResponse1 =
        getProcessingTimeBufferDto(NODE_ID);
    ProcessingTimeBufferResponse processingTimeBufferResponse2 =
        getProcessingTimeBufferDto(NODE_ID_2);

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

  private ProcessingTimeBufferResponse getProcessingTimeBufferDto(String nodeId) {
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
    processingTimeBuffer.setBufferStartDate(getBufferDate(2022, 10, 10));
    processingTimeBuffer.setBufferEndDate(getBufferDate(2022, 11, 10));
    processingTimeBuffer.setStatus("Active");
    return processingTimeBuffer;
  }

  public BaseResponse<List<NodeCarrierResponse>>
      getBaseResponseOfNodeCarrierListResponseWithPartialNullValues() {
    return BaseResponse.builder()
        .message("Node Carrier List fetched successfully")
        .success(true)
        .payload(
            Arrays.asList(
                getNodeCarrierResponse2(SERVICE_OPTION, 5.5, null, getBufferDate(2023, 11, 20))))
        .build();
  }

  public TransitBufferDetailsResponse getTransitBufferDetailsResponse() {
    return TransitBufferDetailsResponse.builder()
        .carrierServiceId(CARRIER_SERVICE_ID)
        .orgId(ORG_ID)
        .hasTransitBuffer(true)
        .build();
  }

  public PagePayload<TransitBufferDetailsResponse> getTransitBufferDetailsResponsePagePayload(
      int pageNo) {
    Pagination pagination = new Pagination();
    pagination.setTotalPages(2);
    pagination.setCurrentPage(pageNo);
    pagination.setSortBy("DESC");
    pagination.setTotalRecords(2);

    PagePayload<TransitBufferDetailsResponse> payload = new PagePayload<>();
    payload.setData(List.of(getTransitBufferDetailsResponse()));
    payload.setPagination(pagination);

    return payload;
  }

  public PagePayload<CarrierServiceResponse> getCarrierServiceResponsePagePayload(int pageNo) {
    Pagination pagination = new Pagination();
    pagination.setTotalPages(2);
    pagination.setCurrentPage(pageNo);
    pagination.setSortBy("DESC");
    pagination.setTotalRecords(2);

    PagePayload<CarrierServiceResponse> payload = new PagePayload<>();
    payload.setData(List.of(getCarrierResponse(), getCarrierResponse2()));
    payload.setPagination(pagination);

    return payload;
  }

  public TransitBufferConfigResponse getTransitBufferConfigResponse(String carrierServiceId) {
    return TransitBufferConfigResponse.builder().carrierServiceId(carrierServiceId).build();
  }

  public BaseResponse<List<TransitBufferConfigResponse>> getBaseResponseTransitBufferConfigResponse(
      String carrierServiceId) {
    return BaseResponse.builder()
        .message("Transit Buffer are fetched successfully")
        .success(true)
        .payload(Arrays.asList(getTransitBufferConfigResponse(carrierServiceId)))
        .build();
  }

  public BaseResponse<List<CarrierServiceCalendarResponse>>
      getCarrierServiceCalendarWithFutureEffectiveDates() {
    return BaseResponse.builder()
        .message("Carrier Calendar fetched successfully")
        .payload(Arrays.asList(getCarrierCalendarResponse(EFFECTIVE_DATE_2)))
        .build();
  }

  public BaseResponse<List<NodeCarrierResponse>> getBaseResponseOfNodeCarrierResponseList() {
    return BaseResponse.builder()
        .message("Node carrier details fetched successfully")
        .success(true)
        .payload(List.of(getNodeCarrierResponse()))
        .build();
  }

  public CostDefinitionRequest getCostDefinitionRequest(
      String selectorCf, String rowCf, String columnCf) {
    return CostDefinitionRequest.builder()
        .costType(COST_TYPE)
        .selector(
            SelectorCostFactorInfoDto.builder()
                .selectorCf(selectorCf)
                .selectorCfValue(SELECTOR_CF_VALUE)
                .build())
        .filters(getFilterCostFactorInfoDto())
        .row(rowCf)
        .column(columnCf)
        .build();
  }

  public CostDefinitionResponse getCostDefinitionResponse() {
    return CostDefinitionResponse.builder()
        .isRateCardActive(true)
        .columns(getColumnData())
        .rows(getRowData())
        .build();
  }

  private List<FilterCostFactorInfoDto> getFilterCostFactorInfoDto() {
    FilterCostFactorInfoDto filterCostFactorInfoDto =
        FilterCostFactorInfoDto.builder()
            .costFactor(FILTER_COST_FACTOR)
            .costFactorValue(FILTER_COST_FACTOR_VALUE)
            .build();
    return List.of(filterCostFactorInfoDto);
  }

  private RateCardColumnsDto getColumnData() {
    CostFactorHeadersInfoDto costFactorHeadersInfoDto1 =
        CostFactorHeadersInfoDto.builder()
            .columnName("BillWeight UPS")
            .columnMeta("billWeightUPS")
            .isCostFactor(true)
            .build();

    CostFactorHeadersInfoDto costFactorHeadersInfoDto2 =
        CostFactorHeadersInfoDto.builder()
            .columnName("Zone1")
            .columnMeta("zone1")
            .isCostFactor(false)
            .build();
    return RateCardColumnsDto.builder()
        .title("Shipping Zones")
        .headers(List.of(costFactorHeadersInfoDto1, costFactorHeadersInfoDto2))
        .build();
  }

  private RateCardRowsDto getRowData() {
    Map<String, String> rowDataMap1 = new HashMap<>();
    rowDataMap1.put("billWeightUPS", "S");
    rowDataMap1.put("zone1", "1.0");
    rowDataMap1.put("isDynamicBucket", "false");

    Map<String, String> rowDataMap2 = new HashMap<>();
    rowDataMap2.put("M", "4.0");
    rowDataMap2.put("zone2", "2.0");
    rowDataMap2.put("isDynamicBucket", "true");

    return RateCardRowsDto.builder().data(List.of(rowDataMap1, rowDataMap2)).build();
  }

  public BaseResponse getFeignResponse() {

    return BaseResponse.builder()
        .message("Transit Entries fetched successfully")
        .payload(getCostDefinitionResponse())
        .build();
  }

  public ItemListResponse getItemListResponse(String itemId) {
    return ItemListResponse.builder()
        .itemId(itemId)
        .itemSource(ITEMSOURCE)
        .leadTime(LEAD_TIME)
        .handlingType(ITEM_HANDLING_TYPE)
        .processingTime(PROCESSING_TIME)
        .shortDescription(SHORT_DESC)
        .serviceOptions(SERVICE_OPTIONS)
        .uom(UOM)
        .customAttributes(customAttributes)
        .build();
  }

  public List<ItemListResponse> getItemList() {
    return List.of(getItemListResponse(ITEM_ID_1), getItemListResponse(ITEM_ID_2));
  }

  public Page<ItemListResponse> getItemListPage(
      int totalPages, List<ItemListResponse> itemListResponses, int totalElements) {
    Page<ItemListResponse> itemListResponsePage =
        new Page<ItemListResponse>() {
          @Override
          public int getTotalPages() {
            return totalPages;
          }

          @Override
          public long getTotalElements() {
            return totalElements;
          }

          @Override
          public <U> Page<U> map(Function<? super ItemListResponse, ? extends U> converter) {
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
          public List<ItemListResponse> getContent() {
            return itemListResponses;
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

          @NotNull
          @Override
          public Iterator<ItemListResponse> iterator() {
            return null;
          }
        };
    return itemListResponsePage;
  }

  public PagePayload<ItemListResponse> setPagePayload(Page<ItemListResponse> itemListResponsePage) {
    PagePayload<ItemListResponse> pagePayload = new PagePayload<>();
    var pagination = new PagePayload.Pagination();
    pagination.setTotalRecords((int) itemListResponsePage.getTotalElements());
    pagination.setTotalPages(itemListResponsePage.getTotalPages());
    pagePayload.setPagination(pagination);
    pagePayload.setData(itemListResponsePage.getContent());
    return pagePayload;
  }

  public BaseResponse<List<NodeCarrierResponse>> getBaseResponseNodeServiceOptions() {
    BaseResponse build =
        BaseResponse.builder()
            .payload(
                List.of(
                    NodeCarrierResponse.builder()
                        .nodeId(NODE_ID)
                        .orgId(ORG_ID)
                        .serviceOption(SERVICE_OPTION_2)
                        .processingTime(PROCESSING_TIME)
                        .build(),
                    NodeCarrierResponse.builder()
                        .nodeId(NODE_ID_2)
                        .orgId(ORG_ID)
                        .serviceOption(SERVICE_OPTION_STD)
                        .processingTime(PROCESSING_TIME)
                        .build()))
            .build();
    return build;
  }

  public BaseResponse<List<NodeCarrierServiceCalendarResponse>>
      getNodeCarrierServiceOptionCalendarResponse() {
    BaseResponse build =
        BaseResponse.builder()
            .payload(
                List.of(
                    NodeCarrierServiceCalendarResponse.builder()
                        .nodeId(NODE_ID)
                        .orgId(ORG_ID)
                        .carrierServiceId(CARRIER_SERVICE_ID)
                        .calendarId(CALENDAR_ID)
                        .build(),
                    NodeCarrierServiceCalendarResponse.builder()
                        .nodeId(NODE_ID_2)
                        .orgId(ORG_ID)
                        .carrierServiceId(CARRIER_SERVICE_ID_2)
                        .calendarId(CALENDAR_ID_2)
                        .build()))
            .build();
    return build;
  }

  public List<ProcessingTimeDetails> getProcessingTimeDetail() {
    return List.of(
        ProcessingTimeDetails.builder()
            .processingTime(PROCESSING_TIME)
            .serviceOption(SERVICE_OPTION_2)
            .build(),
        ProcessingTimeDetails.builder()
            .processingTime(0D)
            .serviceOption(SERVICE_OPTION_EXP)
            .build());
  }

  public PagePayload<TransferScheduleResponse> getTransferSchedulePagePayloadResponse() {
    TransferScheduleResponse transferScheduleResponse =
        TransferScheduleResponse.builder()
            .dropoffNodeId("Node-1")
            .sourceNodeId("Node-2")
            .startTime(new Date())
            .endTime(new Date())
            .build();
    List<TransferScheduleResponse> transferScheduleResponseList = List.of(transferScheduleResponse);
    Pagination pagination = new Pagination();
    pagination.setNext("");
    pagination.setPrevious("");
    pagination.setCurrentPage(1);
    pagination.setTotalPages(1);

    PagePayload pagePayload = new PagePayload<TransferScheduleResponse>();
    pagePayload.setData(transferScheduleResponseList);
    pagePayload.setPagination(pagination);

    return pagePayload;
  }

  public DeleteTargetProfitMarginRequest getDeleteTargetProfitMarginRequest() {
    return DeleteTargetProfitMarginRequest.builder()
        .attributeName(ATTRIBUTE_NAME_ITEM_CATEGORY)
        .attributeValues(List.of(ATTRIBUTE_VALUE_KITCHEN, ATTRIBUTE_VALUE_ELECTRONICS))
        .build();
  }

  public DeleteTargetProfitMarginRequest getDeleteTargetProfitMarginRequestSingle() {
    return DeleteTargetProfitMarginRequest.builder()
        .attributeName(ATTRIBUTE_NAME_ITEM_CATEGORY)
        .attributeValues(List.of(ATTRIBUTE_VALUE_KITCHEN))
        .build();
  }

  public TargetProfitMarginRequest getTargetProfitMarginRequest() {
    return TargetProfitMarginRequest.builder()
        .attributeName(ATTRIBUTE_NAME_ITEM_CATEGORY)
        .attributeValue(ATTRIBUTE_VALUE_KITCHEN)
        .targetGrossProfitMargin(TARGET_GROSS_PROFIT_MARGIN)
        .build();
  }

  public TargetProfitMarginResponse getTargetProfitMarginResponse() {
    return TargetProfitMarginResponse.builder()
        .orgId(ORG_ID)
        .attributeName(ATTRIBUTE_NAME_ITEM_CATEGORY)
        .attributeValue(ATTRIBUTE_VALUE_KITCHEN)
        .targetGrossProfitMargin(TARGET_GROSS_PROFIT_MARGIN)
        .build();
  }

  public List<TargetProfitMarginResponse> getFetchTargetProfitMarginResponse() {
    return List.of(getTargetProfitMarginResponse());
  }

  public AttributeAndValuesTGMResponse getAttributeAndValuesTGMResponse() {
    return AttributeAndValuesTGMResponse.builder()
        .orgId(ORG_ID)
        .attributeName(ATTRIBUTE_NAME_ITEM_CATEGORY)
        .attributeValues(List.of(ATTRIBUTE_VALUE_KITCHEN))
        .build();
  }

  public ShipChargeDetailsTGMResponse getShipChargeDetailsTGMResponse() {
    return ShipChargeDetailsTGMResponse.builder()
        .orgId(ORG_ID)
        .isShipChargeCappingLogicEnabled(true)
        .shipChargeCappingConstantOne(10)
        .shipChargeCappingConstantTwo(20)
        .build();
  }

  public ConfigureShipChargeCappingRequest getConfigureShipChargeCappingRequest() {
    return ConfigureShipChargeCappingRequest.builder()
        .shipChargeCappingConstantOne(10)
        .shipChargeCappingConstantTwo(20)
        .build();
  }

  public ConfigureShipChargeCappingResponse getConfigureShipChargeCappingResponse() {
    return ConfigureShipChargeCappingResponse.builder()
        .orgId(ORG_ID)
        .shipChargeCappingConstantOne(10)
        .shipChargeCappingConstantTwo(20)
        .build();
  }

  public BaseResponse<TenantConfigdataResponse> getDummyTenantConfigData(
      String orgId, String configKey, String configValue) {
    return BaseResponse.builder()
        .payload(
            TenantConfigdataResponse.builder()
                .orgId(orgId)
                .configKey(configKey)
                .configValue(configValue)
                .build())
        .build();
  }

  public TargetProfitMarginRequest getTargetProfitMarginRequestElectronics() {
    return TargetProfitMarginRequest.builder()
        .attributeName(ATTRIBUTE_NAME_ITEM_CATEGORY)
        .attributeValue(ATTRIBUTE_VALUE_ELECTRONICS)
        .targetGrossProfitMargin(TARGET_GROSS_PROFIT_MARGIN)
        .build();
  }

  public List<CustomRegionInfo> getTwoCustomRegionInfoList() {
    CustomRegionInfo customRegionInfo1 =
        CustomRegionInfo.builder()
            .customRegionId(ID)
            .customRegionDescription(CUSTOM_REGION_DESC)
            .customRegionName(CUSTOM_REGION_NAME)
            .zipCodes(PARTIAL_CODES)
            .citiesCount(1)
            .statesCount(1)
            .zipCodePrefixesCount(2)
            .orgId(ORG_ID)
            .uploadDate("2024-10-10")
            .build();

    CustomRegionInfo customRegionInfo2 =
        CustomRegionInfo.builder()
            .customRegionId(ID_2)
            .customRegionDescription(CUSTOM_REGION_DESC)
            .customRegionName(CUSTOM_REGION_NAME)
            .zipCodes(List.of("S1P"))
            .citiesCount(1)
            .statesCount(1)
            .zipCodePrefixesCount(1)
            .orgId(ORG_ID)
            .uploadDate("2024-10-10")
            .build();
    return List.of(customRegionInfo1, customRegionInfo2);
  }

  public BaseResponse<PagePayload<CustomRegionInfo>> createPagePayloadCustomRegionInfo(
      List<CustomRegionInfo> customRegionInfos) {
    Pageable pageable = PageRequest.of(1, 10, Sort.by("customRegionId").ascending());
    Page<CustomRegionInfo> customRegionDtoPage =
        new PageImpl<>(customRegionInfos, pageable, customRegionInfos.size());
    PagePayload<CustomRegionInfo> pagePayload = new PagePayload<>();
    PagePayload.Pagination pagination = new PagePayload.Pagination();
    pagination.setTotalRecords((int) customRegionDtoPage.getTotalElements());
    pagination.setTotalPages(customRegionDtoPage.getTotalPages());
    pagination.setCurrentPage(1);
    pagePayload.setData(customRegionDtoPage.getContent());
    pagePayload.setPagination(pagination);
    return BaseResponse.builder().payload(pagePayload).build();
  }

  public static final List<String> extendedTenantServiceOptionExpected =
      List.of(
          "SDND",
          "sdndEligible",
          "express",
          "canada-post-regular-parcel-peoples",
          "FREE-EXPRESS-DELIVERY-LOYALTY",
          "ups-expedited-peoples",
          "ups-express-saver-peoples",
          "ups-ground-pagoda",
          "ups-next-day-air-jared",
          "upsNextDayAirJared",
          "STANDARD");
}
