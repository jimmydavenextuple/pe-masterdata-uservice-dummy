/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.pe.masterdata.calendar.util;

import com.nextuple.calendar.domain.CalendarDaysStatusInfo;
import com.nextuple.calendar.domain.dto.CarrierCalendarCacheKeyDto;
import com.nextuple.calendar.domain.dto.NodeCalendarCacheKeyDto;
import com.nextuple.calendar.domain.dto.NodeCarrierCalendarCacheKeyDto;
import com.nextuple.calendar.domain.inbound.CalendarRequest;
import com.nextuple.calendar.domain.inbound.CarrierServiceCalendarRequest;
import com.nextuple.calendar.domain.inbound.NodeCalendarRequest;
import com.nextuple.calendar.domain.inbound.NodeCarrierServiceCalendarRequest;
import com.nextuple.calendar.domain.outbound.CalendarResponse;
import com.nextuple.calendar.domain.outbound.CarrierServiceCalendarResponse;
import com.nextuple.calendar.domain.outbound.NodeCalendarResponse;
import com.nextuple.calendar.domain.outbound.NodeCarrierServiceCalendarResponse;
import com.nextuple.calendar.domain.pojo.ExceptionDays;
import com.nextuple.calendar.persistence.domain.CalendarDomainDto;
import com.nextuple.calendar.persistence.domain.CarrierServiceCalendarDomainDto;
import com.nextuple.calendar.persistence.domain.NodeCalendarDomainDto;
import com.nextuple.calendar.persistence.domain.NodeCarrierServiceCalendarDomainDto;
import com.nextuple.carrier.domain.outbound.CarrierServiceResponse;
import com.nextuple.common.pojo.PageParams;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.node.domain.outbound.NodeResponse;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class TestUtil {

  public static final String CALENDAR_ID = "C001";
  public static final String CALENDAR_ID_2 = "C002";
  public static final String ORG_ID = "Bay";
  public static final String NODE_ID = "N001";
  public static final String DESCRIPTION = "Yearly";
  public static final String EXCEPTION_DATE = DateUtil.addDaysToCurrentDate(4);
  public static final String FROM_DATE = "2024-01-01";
  public static final String EXCEPTION_DATE_2 =
      DateUtil.addDaysToGivenDate(Optional.of(FROM_DATE), 4);
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

  public CalendarResponse getCalendarResponse() {
    return CalendarResponse.builder()
        .calendarId(CALENDAR_ID)
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
        .build();
  }

  public CalendarResponse getCalendarResponse1() {
    return CalendarResponse.builder()
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
        .build();
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
        .build();
  }

  public CalendarRequest getCalendarRequest() {
    CalendarRequest request = new CalendarRequest();
    request.setCalendarId(CALENDAR_ID);
    request.setOrgId(ORG_ID);
    request.setDescription(DESCRIPTION);
    request.setIsMondayWorking(Boolean.TRUE);
    request.setIsTuesdayWorking(Boolean.TRUE);
    request.setIsWednesdayWorking(Boolean.TRUE);
    request.setIsThursdayWorking(Boolean.TRUE);
    request.setIsFridayWorking(Boolean.TRUE);
    request.setIsSaturdayWorking(Boolean.TRUE);
    request.setIsSundayWorking(Boolean.TRUE);
    request.setExceptionDays(Collections.singletonList(getExceptionDays()));
    return request;
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
        .build();
  }

  public CalendarDomainDto getCalendarDomainDto2() {
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
        .isSundayWorking(Boolean.FALSE)
        .exceptionDays(Collections.singletonList(getExceptionDays()))
        .build();
  }

  public ExceptionDays getExceptionDays() {
    ExceptionDays exceptionDays = new ExceptionDays();
    exceptionDays.setDate(EXCEPTION_DATE);
    exceptionDays.setReason(EXCEPTION_REASON);
    return exceptionDays;
  }

  public CalendarDomainDto getCalendarDomainDtoWithFromDate() {
    return CalendarDomainDto.builder()
        .calendarId(CALENDAR_ID_2)
        .orgId(ORG_ID)
        .description(DESCRIPTION)
        .isMondayWorking(Boolean.TRUE)
        .isTuesdayWorking(Boolean.TRUE)
        .isWednesdayWorking(Boolean.TRUE)
        .isThursdayWorking(Boolean.TRUE)
        .isFridayWorking(Boolean.TRUE)
        .isSaturdayWorking(Boolean.FALSE)
        .isSundayWorking(Boolean.FALSE)
        .exceptionDays(Collections.singletonList(getExceptionDays2()))
        .build();
  }

  public ExceptionDays getExceptionDays2() {
    ExceptionDays exceptionDays = new ExceptionDays();
    exceptionDays.setDate(EXCEPTION_DATE_2);
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

  public NodeCalendarDomainDto getNodeCalendarDomainDto() {
    return NodeCalendarDomainDto.builder()
        .calendarId(CALENDAR_ID)
        .orgId(ORG_ID)
        .nodeId(NODE_ID)
        .description(DESCRIPTION)
        .effectiveDate(EFFECTIVE_DATE)
        .build();
  }

  public NodeCalendarDomainDto getNodeCalendarDomainDto1() {
    return NodeCalendarDomainDto.builder()
        .calendarId(CALENDAR_ID)
        .orgId(ORG_ID)
        .nodeId(NODE_ID)
        .description(DESCRIPTION)
        .effectiveDate(EFFECTIVE_DATE_2)
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

  public CarrierServiceCalendarDomainDto getCarrierServiceCalendarDomainDto() {
    return CarrierServiceCalendarDomainDto.builder()
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

  public NodeCarrierServiceCalendarDomainDto getNodeCarrierServiceCalendarDomainDto() {
    return NodeCarrierServiceCalendarDomainDto.builder()
        .calendarId(CALENDAR_ID)
        .orgId(ORG_ID)
        .carrierServiceId(CARRIER_SERVICE_ID)
        .description(DESCRIPTION)
        .effectiveDate(EFFECTIVE_DATE)
        .nodeId(NODE_ID)
        .build();
  }

  public BaseResponse<NodeResponse> getNodeDetails(Boolean isActive) {
    NodeResponse nodeResponse = new NodeResponse();
    nodeResponse.setNodeId(NODE_ID);
    nodeResponse.setIsActive(isActive);
    return BaseResponse.builder().payload(nodeResponse).build();
  }

  public List<CalendarDaysStatusInfo> getCalendarDaysStatusInfoList() {
    return Collections.singletonList(
        CalendarDaysStatusInfo.builder().date("2022-01-01").isActive(true).build());
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
            .build();

    CarrierServiceCalendarDomainDto carrierServiceCalendarDomainDto2 =
        CarrierServiceCalendarDomainDto.builder()
            .calendarId(CALENDAR_ID_2)
            .orgId(ORG_ID)
            .carrierServiceId(CARRIER_SERVICE_ID)
            .description(DESCRIPTION)
            .effectiveDate(EFFECTIVE_DATE)
            .shippingStage(SHIPPING_STAGE)
            .build();

    return List.of(carrierServiceCalendarDomainDto1, carrierServiceCalendarDomainDto2);
  }

  public List<NodeCalendarDomainDto> getNodeCalendarDomainDtoList() {
    NodeCalendarDomainDto nodeCalendarDomainDto1 =
        NodeCalendarDomainDto.builder()
            .calendarId(CALENDAR_ID)
            .orgId(ORG_ID)
            .nodeId(NODE_ID)
            .description(DESCRIPTION)
            .effectiveDate(EFFECTIVE_DATE)
            .build();

    NodeCalendarDomainDto nodeCalendarDomainDto2 =
        NodeCalendarDomainDto.builder()
            .calendarId(CALENDAR_ID_2)
            .orgId(ORG_ID)
            .nodeId(NODE_ID)
            .description(DESCRIPTION)
            .effectiveDate(EFFECTIVE_DATE)
            .build();

    return List.of(nodeCalendarDomainDto1, nodeCalendarDomainDto2);
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
            .build();

    NodeCarrierServiceCalendarDomainDto nodeCarrierServiceCalendarDomainDto2 =
        NodeCarrierServiceCalendarDomainDto.builder()
            .calendarId(CALENDAR_ID_2)
            .orgId(ORG_ID)
            .carrierServiceId(CARRIER_SERVICE_ID)
            .description(DESCRIPTION)
            .effectiveDate(EFFECTIVE_DATE)
            .nodeId(NODE_ID)
            .build();

    return List.of(nodeCarrierServiceCalendarDomainDto1, nodeCarrierServiceCalendarDomainDto2);
  }

  public List<CarrierCalendarCacheKeyDto> getCarrierCalendarCacheKeyDtoList() {
    CarrierCalendarCacheKeyDto carrierCalendarCacheKeyDto1 =
        CarrierCalendarCacheKeyDto.builder()
            .orgId(ORG_ID)
            .carrierServiceId(CARRIER_SERVICE_ID)
            .build();

    CarrierCalendarCacheKeyDto carrierCalendarCacheKeyDto2 =
        CarrierCalendarCacheKeyDto.builder()
            .orgId(ORG_ID)
            .carrierServiceId(CARRIER_SERVICE_ID_2)
            .build();

    return List.of(carrierCalendarCacheKeyDto1, carrierCalendarCacheKeyDto2);
  }

  public List<NodeCalendarCacheKeyDto> getNodeCalendarCacheKeyDtoList() {
    NodeCalendarCacheKeyDto nodeCalendarCacheKeyDto1 =
        NodeCalendarCacheKeyDto.builder().nodeId(NODE_ID).orgId(ORG_ID).build();

    NodeCalendarCacheKeyDto nodeCalendarCacheKeyDto2 =
        NodeCalendarCacheKeyDto.builder().nodeId(NODE_ID).orgId(ORG_ID).build();

    return List.of(nodeCalendarCacheKeyDto1, nodeCalendarCacheKeyDto2);
  }

  public List<NodeCarrierCalendarCacheKeyDto> getNodeCarrierCalendarCacheKeyDtoList() {
    NodeCarrierCalendarCacheKeyDto nodeCarrierCalendarCacheKeyDto1 =
        NodeCarrierCalendarCacheKeyDto.builder()
            .carrierServiceId(CARRIER_SERVICE_ID)
            .nodeId(NODE_ID)
            .orgId(ORG_ID)
            .build();

    NodeCarrierCalendarCacheKeyDto nodeCarrierCalendarCacheKeyDto2 =
        NodeCarrierCalendarCacheKeyDto.builder()
            .carrierServiceId(CARRIER_SERVICE_ID_2)
            .nodeId(NODE_ID)
            .orgId(ORG_ID)
            .build();

    return List.of(nodeCarrierCalendarCacheKeyDto1, nodeCarrierCalendarCacheKeyDto2);
  }

  public CarrierServiceCalendarDomainDto getCarrierServiceCalendarDomainDto1() {
    return CarrierServiceCalendarDomainDto.builder()
        .calendarId(CALENDAR_ID)
        .orgId(ORG_ID)
        .carrierServiceId(CARRIER_SERVICE_ID)
        .description(DESCRIPTION)
        .effectiveDate(EFFECTIVE_DATE_2)
        .shippingStage(SHIPPING_STAGE)
        .build();
  }

  public Page<CalendarResponse> getCalendarPageResponses(
      int totalPage,
      List<CalendarResponse> calendarResponses,
      int totalElements,
      String sortOrder) {
    return new Page<CalendarResponse>() {
      @Override
      public int getTotalPages() {
        return totalPage;
      }

      @Override
      public long getTotalElements() {
        return totalElements;
      }

      @Override
      public <U> Page<U> map(Function<? super CalendarResponse, ? extends U> converter) {
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
      public List<CalendarResponse> getContent() {
        return calendarResponses;
      }

      @Override
      public boolean hasContent() {
        return false;
      }

      @Override
      public Sort getSort() {
        return SORT_ORDER_ASC.equals(sortOrder)
            ? Sort.by(SORT_BY).ascending()
            : Sort.by(SORT_BY).descending();
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

      @Override
      public Iterator<CalendarResponse> iterator() {
        return null;
      }
    };
  }

  public Page<CalendarDomainDto> getCalendarPageDomainDtos(
      int totalPage,
      List<CalendarDomainDto> calendarDomainDtos,
      int totalElements,
      String sortOrder) {
    return new Page<CalendarDomainDto>() {
      @Override
      public int getTotalPages() {
        return totalPage;
      }

      @Override
      public long getTotalElements() {
        return totalElements;
      }

      @Override
      public <U> Page<U> map(Function<? super CalendarDomainDto, ? extends U> converter) {
        return (Page<U>)
            getCalendarPageResponses(
                totalPage,
                Arrays.asList(getCalendarResponse(), getCalendarResponse1()),
                totalElements,
                sortOrder);
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
      public List<CalendarDomainDto> getContent() {
        return calendarDomainDtos;
      }

      @Override
      public boolean hasContent() {
        return false;
      }

      @Override
      public Sort getSort() {
        return SORT_ORDER_ASC.equals(sortOrder)
            ? Sort.by(SORT_BY).ascending()
            : Sort.by(SORT_BY).descending();
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

      @Override
      public Iterator<CalendarDomainDto> iterator() {
        return null;
      }
    };
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

  public BaseResponse<List<CarrierServiceResponse>> getCarrierServiceResponse() {
    CarrierServiceResponse response = new CarrierServiceResponse();
    response.setOrgId(ORG_ID);
    response.setCarrierServiceId(CARRIER_SERVICE_ID);
    return BaseResponse.builder().payload(List.of(response)).build();
  }
}
