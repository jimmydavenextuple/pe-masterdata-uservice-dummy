package com.hbc.pe.masterdata.calendar.util;

import com.hbc.calendar.domain.CalendarDaysStatusInfo;
import com.hbc.calendar.domain.dto.CarrierCalendarCacheKeyDto;
import com.hbc.calendar.domain.dto.NodeCalendarCacheKeyDto;
import com.hbc.calendar.domain.dto.NodeCarrierCalendarCacheKeyDto;
import com.hbc.calendar.domain.inbound.CalendarRequest;
import com.hbc.calendar.domain.inbound.CarrierServiceCalendarRequest;
import com.hbc.calendar.domain.inbound.NodeCalendarRequest;
import com.hbc.calendar.domain.inbound.NodeCarrierServiceCalendarRequest;
import com.hbc.calendar.domain.outbound.CalendarResponse;
import com.hbc.calendar.domain.outbound.CarrierServiceCalendarResponse;
import com.hbc.calendar.domain.outbound.NodeCalendarResponse;
import com.hbc.calendar.domain.outbound.NodeCarrierServiceCalendarResponse;
import com.hbc.calendar.domain.pojo.ExceptionDays;
import com.hbc.carrier.domain.outbound.CarrierServiceResponse;
import com.hbc.common.pojo.PageParams;
import com.hbc.common.response.BaseResponse;
import com.hbc.node.domain.outbound.NodeResponse;
import com.hbc.pe.masterdata.calendar.domain.entity.CalendarEntity;
import com.hbc.pe.masterdata.calendar.domain.entity.CarrierServiceCalendarEntity;
import com.hbc.pe.masterdata.calendar.domain.entity.NodeCalendarEntity;
import com.hbc.pe.masterdata.calendar.domain.entity.NodeCarrierServiceCalendarEntity;
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
  public static final String EFFECTIVE_DATE = "2022-01-01";
  public static final String EXCEPTION_REASON = "Public Holiday";
  public static final String CARRIER_SERVICE_ID = "Puro-Post";
  public static final String SHIPPING_STAGE = "PICKUP";
  public static final String ALL_SHIPPING_STAGE = "ALL";
  public static final String SERVICE_OPTION = "SDND";
  private static final String CARRIER_SERVICE_ID_2 = "Puro-Express";
  public static final String EFFECTIVE_DATE_2 = "2022-09-09";
  public static final String SORT_BY = "calendarId";
  public static final String SORT_ORDER_DESC = "desc";
  public static final String SORT_ORDER_ASC = "ASC";

  public CalendarResponse getCalendarResponse() {
    return CalendarResponse.builder()
        .calendarId(CALENDAR_ID)
        .orgId(ORG_ID)
        .description(DESCRIPTION)
        .isMondayWorking(Boolean.TRUE)
        .exceptionDays(Collections.singletonList(getExceptionDays()))
        .build();
  }

  public CalendarResponse getCalendarResponse1() {
    return CalendarResponse.builder()
        .calendarId(CALENDAR_ID_2)
        .orgId(ORG_ID)
        .description(DESCRIPTION)
        .isMondayWorking(Boolean.TRUE)
        .exceptionDays(Collections.singletonList(getExceptionDays()))
        .build();
  }

  public CalendarRequest getCalendarRequest() {
    return CalendarRequest.builder()
        .calendarId(CALENDAR_ID)
        .orgId(ORG_ID)
        .description(DESCRIPTION)
        .isMondayWorking(Boolean.TRUE)
        .exceptionDays(Collections.singletonList(getExceptionDays()))
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

  public NodeCalendarEntity getNodeCalendarEntity1() {
    return NodeCalendarEntity.builder()
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

  public List<CarrierServiceCalendarEntity> getCarrierServiceCalendarEntityList() {
    CarrierServiceCalendarEntity carrierServiceCalendarEntity1 =
        CarrierServiceCalendarEntity.builder()
            .calendarId(CALENDAR_ID)
            .orgId(ORG_ID)
            .carrierServiceId(CARRIER_SERVICE_ID)
            .description(DESCRIPTION)
            .effectiveDate(EFFECTIVE_DATE)
            .shippingStage(SHIPPING_STAGE)
            .build();

    CarrierServiceCalendarEntity carrierServiceCalendarEntity2 =
        CarrierServiceCalendarEntity.builder()
            .calendarId(CALENDAR_ID_2)
            .orgId(ORG_ID)
            .carrierServiceId(CARRIER_SERVICE_ID)
            .description(DESCRIPTION)
            .effectiveDate(EFFECTIVE_DATE)
            .shippingStage(SHIPPING_STAGE)
            .build();

    return List.of(carrierServiceCalendarEntity1, carrierServiceCalendarEntity2);
  }

  public List<NodeCalendarEntity> getNodeCalendarEntityList() {
    NodeCalendarEntity nodeCalendarEntity1 =
        NodeCalendarEntity.builder()
            .calendarId(CALENDAR_ID)
            .orgId(ORG_ID)
            .nodeId(NODE_ID)
            .description(DESCRIPTION)
            .effectiveDate(EFFECTIVE_DATE)
            .build();

    NodeCalendarEntity nodeCalendarEntity2 =
        NodeCalendarEntity.builder()
            .calendarId(CALENDAR_ID_2)
            .orgId(ORG_ID)
            .nodeId(NODE_ID)
            .description(DESCRIPTION)
            .effectiveDate(EFFECTIVE_DATE)
            .build();

    return List.of(nodeCalendarEntity1, nodeCalendarEntity2);
  }

  public List<NodeCarrierServiceCalendarEntity> getNodeCarrierServiceCalendarEntityList() {
    NodeCarrierServiceCalendarEntity nodeCarrierServiceCalendarEntity1 =
        NodeCarrierServiceCalendarEntity.builder()
            .calendarId(CALENDAR_ID)
            .orgId(ORG_ID)
            .carrierServiceId(CARRIER_SERVICE_ID)
            .description(DESCRIPTION)
            .effectiveDate(EFFECTIVE_DATE)
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
            .build();

    return List.of(nodeCarrierServiceCalendarEntity1, nodeCarrierServiceCalendarEntity2);
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

  public CarrierServiceCalendarEntity getCarrierServiceCalendarEntity1() {
    return CarrierServiceCalendarEntity.builder()
        .calendarId(CALENDAR_ID)
        .orgId(ORG_ID)
        .carrierServiceId(CARRIER_SERVICE_ID)
        .description(DESCRIPTION)
        .effectiveDate(EFFECTIVE_DATE_2)
        .shippingStage(SHIPPING_STAGE)
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
        .build();
  }

  public List<CalendarEntity> getCalendarEntityList() {
    return List.of(getCalendarEntity1(), getCalendarEntity());
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
