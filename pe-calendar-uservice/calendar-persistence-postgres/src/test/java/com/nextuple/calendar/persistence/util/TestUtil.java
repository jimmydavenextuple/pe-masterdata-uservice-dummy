/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.calendar.persistence.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.nextuple.calendar.domain.pojo.ExceptionDays;
import com.nextuple.calendar.persistence.domain.CalendarDomainDto;
import com.nextuple.calendar.persistence.domain.CarrierServiceCalendarDomainDto;
import com.nextuple.calendar.persistence.domain.NodeCalendarDomainDto;
import com.nextuple.calendar.persistence.domain.NodeCarrierServiceCalendarDomainDto;
import com.nextuple.calendar.persistence.entity.CalendarEntity;
import com.nextuple.calendar.persistence.entity.CarrierServiceCalendarEntity;
import com.nextuple.calendar.persistence.entity.NodeCalendarEntity;
import com.nextuple.calendar.persistence.entity.NodeCarrierServiceCalendarEntity;
import java.util.Collections;
import java.util.List;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

public class TestUtil {

  public static final String CALENDAR_ID = "C001";
  public static final String CALENDAR_ID_2 = "C002";
  public static final String ORG_ID = "Bay";
  public static final String NODE_ID = "N001";
  public static final String DESCRIPTION = "Yearly";
  public static final String EXCEPTION_DATE = addDaysToCurrentDate(4);
  public static final String EFFECTIVE_DATE = "2022-01-01";
  public static final String EXCEPTION_REASON = "Public Holiday";
  public static final String CARRIER_SERVICE_ID = "Puro-Post";
  public static final String SHIPPING_STAGE = "PICKUP";
  public static final String ALL_SHIPPING_STAGE = "ALL";
  public static final String SERVICE_OPTION = "SDND";
  public static final String EFFECTIVE_DATE_2 = "2022-09-09";
  public static final String SORT_BY = "calendarId";
  public static final String SORT_ORDER_DESC = "desc";
  public static final String SORT_ORDER_ASC = "ASC";
  private static final String CARRIER_SERVICE_ID_2 = "Puro-Express";
  private static final JsonNode CUSTOM_ATTRIBUTES =
      JsonNodeFactory.instance.objectNode().put("key1", "value1").put("key2", "value2");

  public static String addDaysToCurrentDate(int toAddDays) {
    var dt = new DateTime();
    var added = dt.withZone(DateTimeZone.forID("UTC")).plusDays(toAddDays);

    return added.toString("yyyy-MM-dd");
  }

  public CalendarDomainDto getCalendarDomainDto1() {
    return CalendarDomainDto.builder()
        .calendarId(CALENDAR_ID_2)
        .orgId(ORG_ID)
        .description(DESCRIPTION)
        .isMondayWorking(Boolean.TRUE)
        .isTuesdayWorking(Boolean.TRUE)
        .isWednesdayWorking(Boolean.TRUE)
        .isThursdayWorking(Boolean.TRUE)
        .isFridayWorking(Boolean.TRUE)
        .isSaturdayWorking(Boolean.TRUE)
        .isSundayWorking(Boolean.TRUE)
        .exceptionDays(Collections.singletonList(getExceptionDays()))
        .customAttributes(CUSTOM_ATTRIBUTES)
        .build();
  }

  public CalendarDomainDto getCalendarDomainDto() {
    return CalendarDomainDto.builder()
        .calendarId(CALENDAR_ID)
        .orgId(ORG_ID)
        .description(DESCRIPTION)
        .isMondayWorking(Boolean.TRUE)
        .isTuesdayWorking(Boolean.TRUE)
        .isWednesdayWorking(Boolean.TRUE)
        .isThursdayWorking(Boolean.TRUE)
        .isFridayWorking(Boolean.TRUE)
        .isSaturdayWorking(Boolean.TRUE)
        .isSundayWorking(Boolean.FALSE)
        .exceptionDays(Collections.singletonList(getExceptionDays()))
        .customAttributes(CUSTOM_ATTRIBUTES)
        .build();
  }

  public CalendarEntity getCalendarEntity() {
    return CalendarEntity.builder()
        .calendarId(CALENDAR_ID)
        .orgId(ORG_ID)
        .description(DESCRIPTION)
        .isMondayWorking(Boolean.TRUE)
        .isTuesdayWorking(Boolean.TRUE)
        .isWednesdayWorking(Boolean.TRUE)
        .isThursdayWorking(Boolean.TRUE)
        .isFridayWorking(Boolean.TRUE)
        .isSaturdayWorking(Boolean.TRUE)
        .isSundayWorking(Boolean.FALSE)
        .exceptionDays(Collections.singletonList(getExceptionDays()))
        .customAttributes(CUSTOM_ATTRIBUTES)
        .build();
  }

  public CalendarEntity getCalendarEntity1() {
    return CalendarEntity.builder()
        .calendarId(CALENDAR_ID_2)
        .orgId(ORG_ID)
        .description(DESCRIPTION)
        .isMondayWorking(Boolean.TRUE)
        .isTuesdayWorking(Boolean.TRUE)
        .isWednesdayWorking(Boolean.TRUE)
        .isThursdayWorking(Boolean.TRUE)
        .isFridayWorking(Boolean.TRUE)
        .isSaturdayWorking(Boolean.TRUE)
        .isSundayWorking(Boolean.FALSE)
        .exceptionDays(Collections.singletonList(getExceptionDays()))
        .customAttributes(CUSTOM_ATTRIBUTES)
        .build();
  }

  public ExceptionDays getExceptionDays() {
    ExceptionDays exceptionDays = new ExceptionDays();
    exceptionDays.setDate(EXCEPTION_DATE);
    exceptionDays.setReason(EXCEPTION_REASON);
    return exceptionDays;
  }

  public NodeCalendarEntity getNodeCalendarEntity() {
    return NodeCalendarEntity.builder()
        .calendarId(CALENDAR_ID)
        .orgId(ORG_ID)
        .nodeId(NODE_ID)
        .description(DESCRIPTION)
        .effectiveDate(EFFECTIVE_DATE)
        .customAttributes(CUSTOM_ATTRIBUTES)
        .build();
  }

  public NodeCalendarDomainDto getNodeCalendarDomainDto() {
    return NodeCalendarDomainDto.builder()
        .calendarId(CALENDAR_ID)
        .orgId(ORG_ID)
        .nodeId(NODE_ID)
        .description(DESCRIPTION)
        .effectiveDate(EFFECTIVE_DATE)
        .customAttributes(CUSTOM_ATTRIBUTES)
        .build();
  }

  public NodeCalendarEntity getNodeCalendarEntity1() {
    return NodeCalendarEntity.builder()
        .calendarId(CALENDAR_ID)
        .orgId(ORG_ID)
        .nodeId(NODE_ID)
        .description(DESCRIPTION)
        .effectiveDate(EFFECTIVE_DATE_2)
        .customAttributes(CUSTOM_ATTRIBUTES)
        .build();
  }

  public NodeCalendarDomainDto getNodeCalendarDomainDto1() {
    return NodeCalendarDomainDto.builder()
        .calendarId(CALENDAR_ID)
        .orgId(ORG_ID)
        .nodeId(NODE_ID)
        .description(DESCRIPTION)
        .effectiveDate(EFFECTIVE_DATE_2)
        .customAttributes(CUSTOM_ATTRIBUTES)
        .build();
  }

  public CarrierServiceCalendarEntity getCarrierServiceCalendarEntity() {
    return CarrierServiceCalendarEntity.builder()
        .calendarId(CALENDAR_ID)
        .orgId(ORG_ID)
        .carrierServiceId(CARRIER_SERVICE_ID)
        .description(DESCRIPTION)
        .effectiveDate(EFFECTIVE_DATE)
        .shippingStage(SHIPPING_STAGE)
        .customAttributes(CUSTOM_ATTRIBUTES)
        .build();
  }

  public CarrierServiceCalendarDomainDto getCarrierServiceCalendarDomainDto() {
    return CarrierServiceCalendarDomainDto.builder()
        .calendarId(CALENDAR_ID)
        .orgId(ORG_ID)
        .carrierServiceId(CARRIER_SERVICE_ID)
        .description(DESCRIPTION)
        .effectiveDate(EFFECTIVE_DATE)
        .shippingStage(SHIPPING_STAGE)
        .customAttributes(CUSTOM_ATTRIBUTES)
        .build();
  }

  public NodeCarrierServiceCalendarEntity getNodeCarrierServiceCalendarEntity() {
    return NodeCarrierServiceCalendarEntity.builder()
        .calendarId(CALENDAR_ID)
        .orgId(ORG_ID)
        .carrierServiceId(CARRIER_SERVICE_ID)
        .description(DESCRIPTION)
        .effectiveDate(EFFECTIVE_DATE)
        .customAttributes(CUSTOM_ATTRIBUTES)
        .nodeId(NODE_ID)
        .build();
  }

  public NodeCarrierServiceCalendarDomainDto getNodeCarrierServiceCalendarDomainDto() {
    return NodeCarrierServiceCalendarDomainDto.builder()
        .calendarId(CALENDAR_ID)
        .orgId(ORG_ID)
        .carrierServiceId(CARRIER_SERVICE_ID)
        .description(DESCRIPTION)
        .effectiveDate(EFFECTIVE_DATE)
        .nodeId(NODE_ID)
        .customAttributes(CUSTOM_ATTRIBUTES)
        .build();
  }

  public List<CarrierServiceCalendarEntity> getCarrierServiceCalendarEntityList() {
    CarrierServiceCalendarEntity carrierServiceCalendarEntity1 =
        CarrierServiceCalendarEntity.builder()
            .calendarId(CALENDAR_ID)
            .orgId(ORG_ID)
            .carrierServiceId(CARRIER_SERVICE_ID)
            .description(DESCRIPTION)
            .effectiveDate(EFFECTIVE_DATE)
            .shippingStage(SHIPPING_STAGE)
            .customAttributes(CUSTOM_ATTRIBUTES)
            .build();

    CarrierServiceCalendarEntity carrierServiceCalendarEntity2 =
        CarrierServiceCalendarEntity.builder()
            .calendarId(CALENDAR_ID_2)
            .orgId(ORG_ID)
            .carrierServiceId(CARRIER_SERVICE_ID)
            .description(DESCRIPTION)
            .effectiveDate(EFFECTIVE_DATE)
            .shippingStage(SHIPPING_STAGE)
            .customAttributes(CUSTOM_ATTRIBUTES)
            .build();

    return List.of(carrierServiceCalendarEntity1, carrierServiceCalendarEntity2);
  }

  public List<CarrierServiceCalendarDomainDto> getCarrierServiceCalendarDomainDtoList() {
    CarrierServiceCalendarDomainDto carrierServiceCalendarDomainDto1 =
        CarrierServiceCalendarDomainDto.builder()
            .calendarId(CALENDAR_ID)
            .orgId(ORG_ID)
            .carrierServiceId(CARRIER_SERVICE_ID)
            .description(DESCRIPTION)
            .effectiveDate(EFFECTIVE_DATE)
            .shippingStage(SHIPPING_STAGE)
            .customAttributes(CUSTOM_ATTRIBUTES)
            .build();

    CarrierServiceCalendarDomainDto carrierServiceCalendarDomainDto2 =
        CarrierServiceCalendarDomainDto.builder()
            .calendarId(CALENDAR_ID_2)
            .orgId(ORG_ID)
            .carrierServiceId(CARRIER_SERVICE_ID)
            .description(DESCRIPTION)
            .effectiveDate(EFFECTIVE_DATE)
            .shippingStage(SHIPPING_STAGE)
            .customAttributes(CUSTOM_ATTRIBUTES)
            .build();

    return List.of(carrierServiceCalendarDomainDto1, carrierServiceCalendarDomainDto2);
  }

  public List<NodeCalendarEntity> getNodeCalendarEntityList() {
    NodeCalendarEntity nodeCalendarEntity1 =
        NodeCalendarEntity.builder()
            .calendarId(CALENDAR_ID)
            .orgId(ORG_ID)
            .nodeId(NODE_ID)
            .description(DESCRIPTION)
            .effectiveDate(EFFECTIVE_DATE)
            .customAttributes(CUSTOM_ATTRIBUTES)
            .build();

    NodeCalendarEntity nodeCalendarEntity2 =
        NodeCalendarEntity.builder()
            .calendarId(CALENDAR_ID_2)
            .orgId(ORG_ID)
            .nodeId(NODE_ID)
            .description(DESCRIPTION)
            .effectiveDate(EFFECTIVE_DATE)
            .customAttributes(CUSTOM_ATTRIBUTES)
            .build();

    return List.of(nodeCalendarEntity1, nodeCalendarEntity2);
  }

  public List<NodeCalendarDomainDto> getNodeCalendarDomainDtoList() {
    NodeCalendarDomainDto nodeCalendarDomainDto1 =
        NodeCalendarDomainDto.builder()
            .calendarId(CALENDAR_ID)
            .orgId(ORG_ID)
            .nodeId(NODE_ID)
            .description(DESCRIPTION)
            .effectiveDate(EFFECTIVE_DATE)
            .customAttributes(CUSTOM_ATTRIBUTES)
            .build();

    NodeCalendarDomainDto nodeCalendarDomainDto2 =
        NodeCalendarDomainDto.builder()
            .calendarId(CALENDAR_ID_2)
            .orgId(ORG_ID)
            .nodeId(NODE_ID)
            .description(DESCRIPTION)
            .effectiveDate(EFFECTIVE_DATE)
            .customAttributes(CUSTOM_ATTRIBUTES)
            .build();

    return List.of(nodeCalendarDomainDto1, nodeCalendarDomainDto2);
  }

  public List<NodeCarrierServiceCalendarEntity> getNodeCarrierServiceCalendarEntityList() {
    NodeCarrierServiceCalendarEntity nodeCarrierServiceCalendarEntity1 =
        NodeCarrierServiceCalendarEntity.builder()
            .calendarId(CALENDAR_ID)
            .orgId(ORG_ID)
            .carrierServiceId(CARRIER_SERVICE_ID)
            .description(DESCRIPTION)
            .effectiveDate(EFFECTIVE_DATE)
            .customAttributes(CUSTOM_ATTRIBUTES)
            .nodeId(NODE_ID)
            .build();

    NodeCarrierServiceCalendarEntity nodeCarrierServiceCalendarEntity2 =
        NodeCarrierServiceCalendarEntity.builder()
            .calendarId(CALENDAR_ID_2)
            .orgId(ORG_ID)
            .carrierServiceId(CARRIER_SERVICE_ID)
            .description(DESCRIPTION)
            .effectiveDate(EFFECTIVE_DATE)
            .nodeId(NODE_ID)
            .customAttributes(CUSTOM_ATTRIBUTES)
            .build();

    return List.of(nodeCarrierServiceCalendarEntity1, nodeCarrierServiceCalendarEntity2);
  }

  public List<NodeCarrierServiceCalendarDomainDto> getNodeCarrierServiceCalendarDomainDtoList() {
    NodeCarrierServiceCalendarDomainDto nodeCarrierServiceCalendarDomainDto1 =
        NodeCarrierServiceCalendarDomainDto.builder()
            .calendarId(CALENDAR_ID)
            .orgId(ORG_ID)
            .carrierServiceId(CARRIER_SERVICE_ID)
            .description(DESCRIPTION)
            .effectiveDate(EFFECTIVE_DATE)
            .nodeId(NODE_ID)
            .customAttributes(CUSTOM_ATTRIBUTES)
            .build();

    NodeCarrierServiceCalendarDomainDto nodeCarrierServiceCalendarDomainDto2 =
        NodeCarrierServiceCalendarDomainDto.builder()
            .calendarId(CALENDAR_ID_2)
            .orgId(ORG_ID)
            .carrierServiceId(CARRIER_SERVICE_ID)
            .description(DESCRIPTION)
            .effectiveDate(EFFECTIVE_DATE)
            .nodeId(NODE_ID)
            .customAttributes(CUSTOM_ATTRIBUTES)
            .build();

    return List.of(nodeCarrierServiceCalendarDomainDto1, nodeCarrierServiceCalendarDomainDto2);
  }

  public CarrierServiceCalendarEntity getCarrierServiceCalendarEntity1() {
    return CarrierServiceCalendarEntity.builder()
        .calendarId(CALENDAR_ID)
        .orgId(ORG_ID)
        .carrierServiceId(CARRIER_SERVICE_ID)
        .description(DESCRIPTION)
        .effectiveDate(EFFECTIVE_DATE_2)
        .shippingStage(SHIPPING_STAGE)
        .customAttributes(CUSTOM_ATTRIBUTES)
        .build();
  }

  public CarrierServiceCalendarDomainDto getCarrierServiceCalendarDomainDto1() {
    return CarrierServiceCalendarDomainDto.builder()
        .calendarId(CALENDAR_ID)
        .orgId(ORG_ID)
        .carrierServiceId(CARRIER_SERVICE_ID)
        .description(DESCRIPTION)
        .effectiveDate(EFFECTIVE_DATE_2)
        .shippingStage(SHIPPING_STAGE)
        .customAttributes(CUSTOM_ATTRIBUTES)
        .build();
  }

  public NodeCarrierServiceCalendarEntity getNodeCarrierServiceCalendarEntity1() {
    return NodeCarrierServiceCalendarEntity.builder()
        .calendarId(CALENDAR_ID)
        .orgId(ORG_ID)
        .carrierServiceId(CARRIER_SERVICE_ID)
        .description(DESCRIPTION)
        .effectiveDate(EFFECTIVE_DATE_2)
        .nodeId(NODE_ID)
        .customAttributes(CUSTOM_ATTRIBUTES)
        .build();
  }

  public NodeCarrierServiceCalendarDomainDto getNodeCarrierServiceCalendarDomainDto1() {
    return NodeCarrierServiceCalendarDomainDto.builder()
        .calendarId(CALENDAR_ID)
        .orgId(ORG_ID)
        .carrierServiceId(CARRIER_SERVICE_ID)
        .description(DESCRIPTION)
        .effectiveDate(EFFECTIVE_DATE_2)
        .nodeId(NODE_ID)
        .customAttributes(CUSTOM_ATTRIBUTES)
        .build();
  }

  public List<CalendarEntity> getCalendarEntityList() {
    return List.of(getCalendarEntity1(), getCalendarEntity());
  }
}
