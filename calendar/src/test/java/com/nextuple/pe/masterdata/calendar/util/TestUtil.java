package com.nextuple.pe.masterdata.calendar.util;

import com.nextuple.pe.masterdata.calendar.domain.entity.CalendarEntity;
import com.nextuple.pe.masterdata.calendar.domain.entity.CarrierServiceCalendarEntity;
import com.nextuple.pe.masterdata.calendar.domain.entity.NodeCalendarEntity;
import com.nextuple.pe.masterdata.calendar.domain.entity.NodeCarrierServiceCalendarEntity;
import com.nextuple.pe.masterdata.calendar.domain.inbound.CalendarRequest;
import com.nextuple.pe.masterdata.calendar.domain.inbound.CarrierServiceCalendarRequest;
import com.nextuple.pe.masterdata.calendar.domain.inbound.NodeCalendarRequest;
import com.nextuple.pe.masterdata.calendar.domain.inbound.NodeCarrierServiceCalendarRequest;
import com.nextuple.pe.masterdata.calendar.domain.outbound.CalendarResponse;
import com.nextuple.pe.masterdata.calendar.domain.outbound.CarrierServiceCalendarResponse;
import com.nextuple.pe.masterdata.calendar.domain.outbound.NodeCalendarResponse;
import com.nextuple.pe.masterdata.calendar.domain.outbound.NodeCarrierServiceCalendarResponse;
import com.nextuple.pe.masterdata.calendar.domain.pojo.CalendarDaysStatusInfo;
import com.nextuple.pe.masterdata.calendar.domain.pojo.ExceptionDays;
import java.util.List;

public class TestUtil {

  public static final String CALENDAR_ID = "C001";
  public static final String ORG_ID = "Bay";
  public static final String NODE_ID = "N001";
  public static final String DESCRIPTION = "Yearly";
  public static final String EXCEPTION_DATE = DateUtil.addDaysToCurrentDate(4, "UTC");
  public static final String EFFECTIVE_DATE = "2022-01-01";
  public static final String EXCEPTION_REASON = "Public Holiday";
  public static final String CARRIER_SERVICE_ID = "Puro-Post";
  public static final String SHIPPING_STAGE = "PICKUP";
  public static final String ALL_SHIPPING_STAGE = "ALL";
  public static final String SERVICE_OPTION = "SDND";

  public CalendarResponse getCalendarResponse() {
    return CalendarResponse.builder()
        .calendarId(CALENDAR_ID)
        .orgId(ORG_ID)
        .description(DESCRIPTION)
        .isMondayWorking(Boolean.TRUE)
        .exceptionDays(List.of(getExceptionDays()))
        .build();
  }

  public CalendarRequest getCalendarRequest() {
    return CalendarRequest.builder()
        .calendarId(CALENDAR_ID)
        .orgId(ORG_ID)
        .description(DESCRIPTION)
        .isMondayWorking(Boolean.TRUE)
        .exceptionDays(List.of(getExceptionDays()))
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
        .exceptionDays(List.of(getExceptionDays()))
        .build();
  }

  public ExceptionDays getExceptionDays() {
    ExceptionDays exceptionDays = new ExceptionDays();
    exceptionDays.setDate(EXCEPTION_DATE);
    exceptionDays.setReason(EXCEPTION_REASON);
    return exceptionDays;
  }

  public NodeCalendarResponse getNodeCalendarResponse() {
    return NodeCalendarResponse.builder()
        .calendarId(CALENDAR_ID)
        .orgId(ORG_ID)
        .nodeId(NODE_ID)
        .description(DESCRIPTION)
        .effectiveDate(EFFECTIVE_DATE)
        .build();
  }

  public NodeCalendarRequest getNodeCalendarRequest() {
    return NodeCalendarRequest.builder()
        .calendarId(CALENDAR_ID)
        .orgId(ORG_ID)
        .nodeId(NODE_ID)
        .description(DESCRIPTION)
        .effectiveDate(EFFECTIVE_DATE)
        .build();
  }

  public NodeCalendarEntity getNodeCalendarEntity() {
    return NodeCalendarEntity.builder()
        .calendarId(CALENDAR_ID)
        .orgId(ORG_ID)
        .nodeId(NODE_ID)
        .description(DESCRIPTION)
        .effectiveDate(EFFECTIVE_DATE)
        .build();
  }

  public CarrierServiceCalendarResponse getCarrierServiceCalendarResponse() {
    return CarrierServiceCalendarResponse.builder()
        .calendarId(CALENDAR_ID)
        .orgId(ORG_ID)
        .carrierServiceId(CARRIER_SERVICE_ID)
        .description(DESCRIPTION)
        .effectiveDate(EFFECTIVE_DATE)
        .shippingStage(SHIPPING_STAGE)
        .build();
  }

  public CarrierServiceCalendarRequest getCarrierServiceCalendarRequest() {
    return CarrierServiceCalendarRequest.builder()
        .calendarId(CALENDAR_ID)
        .orgId(ORG_ID)
        .carrierServiceId(CARRIER_SERVICE_ID)
        .description(DESCRIPTION)
        .effectiveDate(EFFECTIVE_DATE)
        .shippingStage(SHIPPING_STAGE)
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
        .build();
  }

  public NodeCarrierServiceCalendarResponse getNodeCarrierServiceCalendarResponse() {
    return NodeCarrierServiceCalendarResponse.builder()
        .calendarId(CALENDAR_ID)
        .orgId(ORG_ID)
        .carrierServiceId(CARRIER_SERVICE_ID)
        .description(DESCRIPTION)
        .effectiveDate(EFFECTIVE_DATE)
        .nodeId(NODE_ID)
        .build();
  }

  public NodeCarrierServiceCalendarRequest getNodeCarrierServiceCalendarRequest() {
    return NodeCarrierServiceCalendarRequest.builder()
        .calendarId(CALENDAR_ID)
        .orgId(ORG_ID)
        .carrierServiceId(CARRIER_SERVICE_ID)
        .description(DESCRIPTION)
        .effectiveDate(EFFECTIVE_DATE)
        .nodeId(NODE_ID)
        .build();
  }

  public NodeCarrierServiceCalendarEntity getNodeCarrierServiceCalendarEntity() {
    return NodeCarrierServiceCalendarEntity.builder()
        .calendarId(CALENDAR_ID)
        .orgId(ORG_ID)
        .carrierServiceId(CARRIER_SERVICE_ID)
        .description(DESCRIPTION)
        .effectiveDate(EFFECTIVE_DATE)
        .nodeId(NODE_ID)
        .build();
  }

  public List<CalendarDaysStatusInfo> getCalendarDaysStatusInfoList() {
    return List.of(CalendarDaysStatusInfo.builder().date("2022-01-01").isActive(true).build());
  }
}
