/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.calendar.consumer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.nextuple.calendar.consumer.dto.CalendarFeedDto;
import com.nextuple.calendar.consumer.dto.CarrierServiceCalendarFeedDto;
import com.nextuple.calendar.consumer.dto.NodeCalendarFeedDto;
import com.nextuple.calendar.consumer.dto.PickupCalendarFeedDto;
import com.nextuple.calendar.domain.outbound.CalendarResponse;
import com.nextuple.calendar.domain.outbound.CarrierServiceCalendarResponse;
import com.nextuple.calendar.domain.outbound.NodeCalendarResponse;
import com.nextuple.calendar.domain.outbound.NodeCarrierServiceCalendarResponse;
import com.nextuple.calendar.domain.pojo.ExceptionDays;
import com.nextuple.calendar.persistence.domain.CalendarDomainDto;
import com.nextuple.calendar.persistence.domain.CarrierServiceCalendarDomainDto;
import com.nextuple.calendar.persistence.domain.NodeCalendarDomainDto;
import com.nextuple.calendar.persistence.domain.NodeCarrierServiceCalendarDomainDto;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.master.data.integration.dto.ResponseDto;
import com.nextuple.master.data.integration.enums.ActionEnum;
import com.nextuple.master.data.integration.inbound.BatchRequest;
import com.nextuple.master.data.integration.outbound.BatchResponse;
import java.util.ArrayList;
import java.util.List;

public class TestUtil {

  public static final String ORG_ID = "org-1";
  public static final String CALENDAR_ID = "calendar-1";
  public static final String DESCRIPTION = "Calendar-description";
  public static final Boolean IS_MONDAY_WORKING = true;
  public static final Boolean IS_TUESDAY_WORKING = true;
  public static final Boolean IS_WEDNESDAY_WORKING = true;
  public static final Boolean IS_THURSDAY_WORKING = true;
  public static final Boolean IS_FRIDAY_WORKING = true;
  public static final Boolean IS_SATURDAY_WORKING = false;
  public static final Boolean IS_SUNDAY_WORKING = false;
  public static final List<ExceptionDays> EXCEPTION_DAYS = new ArrayList<>();
  public static final String NODE_ID = "node-1";
  public static final String CARRIER_SERVICE_ID = "carrier-service";
  public static final String EFFECTIVE_DATE = "effective-date";
  public static final String SHIPPING_STAGE = "Shipping-stage";
  private static final JsonNode CUSTOM_ATTRIBUTES =
      JsonNodeFactory.instance.objectNode().put("key1", "value1").put("key2", "value2");

  public CalendarFeedDto calendarFeedDto() {
    return CalendarFeedDto.builder()
        .orgId(ORG_ID)
        .calendarId(CALENDAR_ID)
        .description(DESCRIPTION)
        .isMondayWorking(IS_MONDAY_WORKING)
        .isTuesdayWorking(IS_TUESDAY_WORKING)
        .isWednesdayWorking(IS_WEDNESDAY_WORKING)
        .isThursdayWorking(IS_THURSDAY_WORKING)
        .isFridayWorking(IS_FRIDAY_WORKING)
        .isSaturdayWorking(IS_SATURDAY_WORKING)
        .isSundayWorking(IS_SUNDAY_WORKING)
        .exceptionDays(EXCEPTION_DAYS)
        .customAttributes(CUSTOM_ATTRIBUTES)
        .build();
  }

  public NodeCalendarFeedDto nodeCalendarFeedDto() {
    return NodeCalendarFeedDto.builder()
        .nodeId(NODE_ID)
        .orgId(ORG_ID)
        .calendarId(CALENDAR_ID)
        .description(DESCRIPTION)
        .effectiveDate(EFFECTIVE_DATE)
        .customAttributes(CUSTOM_ATTRIBUTES)
        .build();
  }

  public CarrierServiceCalendarFeedDto carrierServiceCalendarFeedDto() {
    return CarrierServiceCalendarFeedDto.builder()
        .orgId(ORG_ID)
        .calendarId(CALENDAR_ID)
        .description(DESCRIPTION)
        .carrierServiceId(CARRIER_SERVICE_ID)
        .shippingStage(SHIPPING_STAGE)
        .effectiveDate(EFFECTIVE_DATE)
        .customAttributes(CUSTOM_ATTRIBUTES)
        .build();
  }

  public PickupCalendarFeedDto pickupCalendarFeedDto() {
    return PickupCalendarFeedDto.builder()
        .nodeId(NODE_ID)
        .orgId(ORG_ID)
        .calendarId(CALENDAR_ID)
        .description(DESCRIPTION)
        .effectiveDate(EFFECTIVE_DATE)
        .carrierServiceId(CARRIER_SERVICE_ID)
        .customAttributes(CUSTOM_ATTRIBUTES)
        .build();
  }

  public BatchRequest<CalendarFeedDto> getCalendarFeedRequest(ActionEnum action) {
    BatchRequest<CalendarFeedDto> batchRequest = new BatchRequest<>();
    batchRequest.setAction(action);
    batchRequest.setRecordNo(1);
    batchRequest.setPayload(calendarFeedDto());
    return batchRequest;
  }

  public BatchRequest<NodeCalendarFeedDto> getNodeCalendarFeedRequest(ActionEnum action) {
    BatchRequest<NodeCalendarFeedDto> batchRequest = new BatchRequest<>();
    batchRequest.setAction(action);
    batchRequest.setRecordNo(1);
    batchRequest.setPayload(nodeCalendarFeedDto());
    return batchRequest;
  }

  public BatchRequest<CarrierServiceCalendarFeedDto> getCarrierServiceCalendarFeedRequest(
      ActionEnum action) {
    BatchRequest<CarrierServiceCalendarFeedDto> batchRequest = new BatchRequest<>();
    batchRequest.setAction(action);
    batchRequest.setRecordNo(1);
    batchRequest.setPayload(carrierServiceCalendarFeedDto());
    return batchRequest;
  }

  public BatchRequest<PickupCalendarFeedDto> getPickupCalendarFeedRequest(ActionEnum action) {
    BatchRequest<PickupCalendarFeedDto> batchRequest = new BatchRequest<>();
    batchRequest.setAction(action);
    batchRequest.setRecordNo(1);
    batchRequest.setPayload(pickupCalendarFeedDto());
    return batchRequest;
  }

  public ResponseDto createResponseDto(int recordNo, int statusCode, String message) {
    return ResponseDto.builder().recordNo(recordNo).statusCode(statusCode).message(message).build();
  }

  public BatchResponse getCalendarBatchResponse(
      int totalRecords, int successfulRecords, int failedRecords) {
    return BatchResponse.builder()
        .totalRecords(totalRecords)
        .successfulRecords(successfulRecords)
        .failedRecords(failedRecords)
        .build();
  }

  public BaseResponse<CalendarResponse> getBaseResponseOfCalendarFeed(String message) {
    return BaseResponse.builder()
        .message(message)
        .success(true)
        .payload(getCalendarResponse())
        .build();
  }

  public BaseResponse<NodeCalendarResponse> getBaseResponseOfNodeCalendarFeed(String message) {
    return BaseResponse.builder()
        .message(message)
        .success(true)
        .payload(getNodeCalendarResponse())
        .build();
  }

  public BaseResponse<CarrierServiceCalendarResponse> getBaseResponseOfCarrierServiceCalendarFeed(
      String message) {
    return BaseResponse.builder()
        .message(message)
        .success(true)
        .payload(getCarrierServiceCalendarResponse())
        .build();
  }

  public BaseResponse<NodeCarrierServiceCalendarResponse> getBaseResponseOfPickupCalendarFeed(
      String message) {
    return BaseResponse.builder()
        .message(message)
        .success(true)
        .payload(getPickupCalendarResponse())
        .build();
  }

  public CalendarResponse getCalendarResponse() {
    return CalendarResponse.builder()
        .orgId(ORG_ID)
        .calendarId(CALENDAR_ID)
        .description(DESCRIPTION)
        .exceptionDays(EXCEPTION_DAYS)
        .isMondayWorking(IS_MONDAY_WORKING)
        .isTuesdayWorking(IS_TUESDAY_WORKING)
        .isWednesdayWorking(IS_WEDNESDAY_WORKING)
        .isThursdayWorking(IS_THURSDAY_WORKING)
        .isFridayWorking(IS_FRIDAY_WORKING)
        .isSaturdayWorking(IS_SATURDAY_WORKING)
        .isSundayWorking(IS_SUNDAY_WORKING)
        .customAttributes(CUSTOM_ATTRIBUTES)
        .build();
  }

  public NodeCalendarResponse getNodeCalendarResponse() {
    return NodeCalendarResponse.builder()
        .nodeId(NODE_ID)
        .orgId(ORG_ID)
        .calendarId(CALENDAR_ID)
        .description(DESCRIPTION)
        .effectiveDate(EFFECTIVE_DATE)
        .customAttributes(CUSTOM_ATTRIBUTES)
        .build();
  }

  public CarrierServiceCalendarResponse getCarrierServiceCalendarResponse() {
    return CarrierServiceCalendarResponse.builder()
        .orgId(ORG_ID)
        .calendarId(CALENDAR_ID)
        .carrierServiceId(CARRIER_SERVICE_ID)
        .description(DESCRIPTION)
        .effectiveDate(EFFECTIVE_DATE)
        .customAttributes(CUSTOM_ATTRIBUTES)
        .build();
  }

  public NodeCarrierServiceCalendarResponse getPickupCalendarResponse() {
    return NodeCarrierServiceCalendarResponse.builder()
        .nodeId(NODE_ID)
        .orgId(ORG_ID)
        .calendarId(CALENDAR_ID)
        .carrierServiceId(CARRIER_SERVICE_ID)
        .description(DESCRIPTION)
        .effectiveDate(EFFECTIVE_DATE)
        .customAttributes(CUSTOM_ATTRIBUTES)
        .build();
  }

  public CalendarDomainDto getCalendarDomainDto() {
    return CalendarDomainDto.builder()
        .orgId(ORG_ID)
        .calendarId(CALENDAR_ID)
        .description(DESCRIPTION)
        .exceptionDays(EXCEPTION_DAYS)
        .isMondayWorking(IS_MONDAY_WORKING)
        .isTuesdayWorking(IS_TUESDAY_WORKING)
        .isWednesdayWorking(IS_WEDNESDAY_WORKING)
        .isThursdayWorking(IS_THURSDAY_WORKING)
        .isFridayWorking(IS_FRIDAY_WORKING)
        .isSaturdayWorking(IS_SATURDAY_WORKING)
        .isSundayWorking(IS_SUNDAY_WORKING)
        .customAttributes(CUSTOM_ATTRIBUTES)
        .build();
  }

  public NodeCalendarDomainDto getNodeCalendarDomainDto() {
    return NodeCalendarDomainDto.builder()
        .nodeId(NODE_ID)
        .orgId(ORG_ID)
        .calendarId(CALENDAR_ID)
        .description(DESCRIPTION)
        .effectiveDate(EFFECTIVE_DATE)
        .customAttributes(CUSTOM_ATTRIBUTES)
        .build();
  }

  public CarrierServiceCalendarDomainDto getCarrierServiceCalendarDomainDto() {
    return CarrierServiceCalendarDomainDto.builder()
        .orgId(ORG_ID)
        .calendarId(CALENDAR_ID)
        .carrierServiceId(CARRIER_SERVICE_ID)
        .description(DESCRIPTION)
        .effectiveDate(EFFECTIVE_DATE)
        .customAttributes(CUSTOM_ATTRIBUTES)
        .build();
  }

  public NodeCarrierServiceCalendarDomainDto getPickupCalendarDomainDto() {
    return NodeCarrierServiceCalendarDomainDto.builder()
        .nodeId(NODE_ID)
        .orgId(ORG_ID)
        .calendarId(CALENDAR_ID)
        .carrierServiceId(CARRIER_SERVICE_ID)
        .description(DESCRIPTION)
        .effectiveDate(EFFECTIVE_DATE)
        .customAttributes(CUSTOM_ATTRIBUTES)
        .build();
  }
}
