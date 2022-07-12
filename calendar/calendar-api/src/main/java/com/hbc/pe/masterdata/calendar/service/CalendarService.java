package com.hbc.pe.masterdata.calendar.service;

import com.hbc.calendar.domain.CalendarDaysStatusInfo;
import com.hbc.calendar.domain.inbound.CalendarRequest;
import com.hbc.calendar.domain.outbound.CalendarResponse;
import com.hbc.calendar.domain.pojo.ExceptionDays;
import com.hbc.common.exception.CommonServiceException;
import com.hbc.common.response.error.FieldError;
import com.hbc.pe.masterdata.calendar.domain.CalendarDomain;
import com.hbc.pe.masterdata.calendar.domain.NodeCalendarDomain;
import com.hbc.pe.masterdata.calendar.domain.entity.CalendarEntity;
import com.hbc.pe.masterdata.calendar.domain.entity.CarrierServiceCalendarEntity;
import com.hbc.pe.masterdata.calendar.domain.entity.NodeCalendarEntity;
import com.hbc.pe.masterdata.calendar.domain.entity.NodeCarrierServiceCalendarEntity;
import com.hbc.pe.masterdata.calendar.domain.mapper.CalendarMapper;
import com.hbc.pe.masterdata.calendar.exception.CalendarDomainException;
import com.hbc.pe.masterdata.calendar.util.DateUtil;
import java.util.*;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

@Service
@RequiredArgsConstructor
public class CalendarService {

  private static final Logger logger = LoggerFactory.getLogger(CalendarService.class);
  private static final CalendarMapper INSTANCE = Mappers.getMapper(CalendarMapper.class);
  private final CalendarDomain calendarDomain;
  private final NodeCalendarDomain nodeCalendarDomain;
  private final CarrierServiceCalendarService carrierServiceCalendarService;
  private final NodeCarrierServiceCalendarService nodeCarrierServiceCalendarService;
  // will need to get via node master data feign client
  private static final String NODE_TIMEZONE = "America/Chicago";
  private static final String ORG_ID = "orgId";
  private static final String NODE_ID = "nodeId";
  private static final String CARRIER_SERVICE_ID = "carrierServiceId";

  @Value("${constants.defaultNumberOfDaysInFuture}")
  private Integer defaultNumberOfDaysInFuture;

  /** Creates a new Calendar */
  public CalendarResponse processCreateCalendar(CalendarRequest calendarRequest)
      throws CalendarDomainException {
    var calendarEntity = INSTANCE.convertToCalendarEntity(calendarRequest);
    var savedCalendarEntity = calendarDomain.saveCalendarEntity(calendarEntity);
    return INSTANCE.convertToCalendarResponse(savedCalendarEntity);
  }

  /** Get Calendar details by calendarId and OrgId */
  public CalendarResponse processGetCalendar(String orgId, String calendarId)
      throws CalendarDomainException {
    return INSTANCE.convertToCalendarResponse(calendarDomain.getCalendar(orgId, calendarId));
  }

  public List<CalendarDaysStatusInfo> processGetUpcomingDaysCalendarStatus(
      String orgId,
      Optional<String> nodeId,
      Optional<String> carrierServiceId,
      Optional<String> serviceOption,
      Optional<Integer> numberOfDaysInFuture,
      Optional<String> shippingStage,
      Optional<String> destinationTimezone)
      throws CalendarDomainException, CommonServiceException {
    validateRequestFields(orgId, nodeId, carrierServiceId);

    List<CalendarDaysStatusInfo> calendarDaysStatusInfoList = new ArrayList<>();
    String timezone = nodeId.isPresent() ? NODE_TIMEZONE : destinationTimezone.orElse("UTC");

    int numDays = numberOfDaysInFuture.orElseGet(() -> defaultNumberOfDaysInFuture);
    for (var i = 0; i < numDays; i++) {
      calendarDaysStatusInfoList.add(
          CalendarDaysStatusInfo.builder()
              .date(DateUtil.addDaysToCurrentDate(i, timezone))
              .isActive(Boolean.TRUE)
              .build());
    }
    String startDate = DateUtil.addDaysToCurrentDate(0, timezone);
    String endDate = DateUtil.addDaysToCurrentDate(numDays, timezone);
    var currentCalendarId = "";
    Map<String, String> nextCalendarIds = new HashMap<>();

    if (nodeId.isPresent() && carrierServiceId.isEmpty()) {
      List<NodeCalendarEntity> nodeCalendarEntityList =
          nodeCalendarDomain.getNodeCalendar(orgId, nodeId.get());
      Optional<NodeCalendarEntity> nodeCalendarEntity =
          nodeCalendarEntityList.stream()
              .filter(x -> x.getEffectiveDate().compareTo(startDate) <= 0)
              .max(Comparator.comparing(NodeCalendarEntity::getEffectiveDate));

      currentCalendarId = validateNodeCalendar(orgId, nodeId.get(), nodeCalendarEntity);
      nextCalendarIds =
          nodeCalendarEntityList.stream()
              .filter(
                  x ->
                      x.getEffectiveDate().compareTo(startDate) > 0
                          && x.getEffectiveDate().compareTo(endDate) <= 0)
              .collect(
                  Collectors.toMap(
                      NodeCalendarEntity::getEffectiveDate, NodeCalendarEntity::getCalendarId));
    }

    if (carrierServiceId.isPresent() && nodeId.isEmpty()) {
      List<CarrierServiceCalendarEntity> carrierServiceCalendarEntityList =
          carrierServiceCalendarService.getAndFilterCarrierServiceCalendar(
              orgId, carrierServiceId.get(), serviceOption, shippingStage);
      Optional<CarrierServiceCalendarEntity> carrierServiceCalendarEntity =
          carrierServiceCalendarEntityList.stream()
              .filter(x -> x.getEffectiveDate().compareTo(startDate) <= 0)
              .max(Comparator.comparing(CarrierServiceCalendarEntity::getEffectiveDate));

      currentCalendarId =
          validateCarrierServiceCalendar(
              orgId, carrierServiceId.get(), carrierServiceCalendarEntity);

      nextCalendarIds =
          carrierServiceCalendarEntityList.stream()
              .filter(
                  x ->
                      x.getEffectiveDate().compareTo(startDate) > 0
                          && x.getEffectiveDate().compareTo(endDate) <= 0)
              .collect(
                  Collectors.toMap(
                      CarrierServiceCalendarEntity::getEffectiveDate,
                      CarrierServiceCalendarEntity::getCalendarId));
    }

    if (nodeId.isPresent() && carrierServiceId.isPresent()) {
      List<NodeCarrierServiceCalendarEntity> nodeCarrierServiceCalendarEntityList =
          nodeCarrierServiceCalendarService.getAndFilterNodeCarrierServiceCalendar(
              orgId, nodeId.get(), carrierServiceId.get(), serviceOption);
      Optional<NodeCarrierServiceCalendarEntity> nodeCarrierServiceCalendarEntity =
          nodeCarrierServiceCalendarEntityList.stream()
              .filter(x -> x.getEffectiveDate().compareTo(startDate) <= 0)
              .max(Comparator.comparing(NodeCarrierServiceCalendarEntity::getEffectiveDate));

      currentCalendarId =
          validateNodeCarrierServiceCalendar(
              orgId, nodeId.get(), carrierServiceId.get(), nodeCarrierServiceCalendarEntity);
      nextCalendarIds =
          nodeCarrierServiceCalendarEntityList.stream()
              .filter(
                  x ->
                      x.getEffectiveDate().compareTo(startDate) > 0
                          && x.getEffectiveDate().compareTo(endDate) <= 0)
              .collect(
                  Collectors.toMap(
                      NodeCarrierServiceCalendarEntity::getEffectiveDate,
                      NodeCarrierServiceCalendarEntity::getCalendarId));
    }

    var calendarEntity = calendarDomain.getCalendar(orgId, currentCalendarId);
    if (ObjectUtils.isEmpty(calendarEntity)) {
      throw new CommonServiceException(
          "No Calendar with calendarId = "
              + currentCalendarId
              + " found that exist with the association calendar ",
          HttpStatus.BAD_REQUEST,
          0xfffff5,
          Map.of(ORG_ID, FieldError.builder().rejectedValue(orgId).build()));
    }
    List<String> exceptionDays =
        CollectionUtils.isEmpty(calendarEntity.getExceptionDays())
            ? new ArrayList<>()
            : calendarEntity.getExceptionDays().stream()
                .map(ExceptionDays::getDate)
                .collect(Collectors.toList());
    calendarDaysStatusInfoList.forEach(
        info -> {
          info.setIsActive(findWeekInfo(DateUtil.getDayOfWeek(info.getDate()), calendarEntity));
          if (exceptionDays.stream().anyMatch(e -> e.equals(info.getDate()))) {
            info.setIsActive(Boolean.FALSE);
          }
        });

    if (nextCalendarIds.isEmpty()) return calendarDaysStatusInfoList;

    return calculateOtherActiveCalendarStatuses(orgId, calendarDaysStatusInfoList, nextCalendarIds);
  }

  private void validateRequestFields(
      String orgId, Optional<String> nodeId, Optional<String> carrierServiceId)
      throws CommonServiceException {
    if (nodeId.isEmpty() && carrierServiceId.isEmpty()) {
      throw new CommonServiceException(
          "Either nodeId or carrierServiceId must pe provided",
          HttpStatus.BAD_REQUEST,
          0xfffff4,
          Map.of(ORG_ID, FieldError.builder().rejectedValue(orgId).build()));
    }
  }

  private List<CalendarDaysStatusInfo> calculateOtherActiveCalendarStatuses(
      String orgId,
      List<CalendarDaysStatusInfo> calendarDaysStatusInfoList,
      Map<String, String> calendarIds)
      throws CalendarDomainException {
    for (CalendarDaysStatusInfo x : calendarDaysStatusInfoList) {
      if (calendarIds.containsKey(x.getDate())) {
        var calendarEntity = calendarDomain.getCalendar(orgId, calendarIds.get(x.getDate()));

        List<String> exceptionDays =
            CollectionUtils.isEmpty(calendarEntity.getExceptionDays())
                ? new ArrayList<>()
                : calendarEntity.getExceptionDays().stream()
                    .map(ExceptionDays::getDate)
                    .collect(Collectors.toList());
        calendarDaysStatusInfoList.stream()
            .filter(info -> info.getDate().compareTo(x.getDate()) >= 0)
            .forEach(
                info -> {
                  info.setIsActive(
                      findWeekInfo(DateUtil.getDayOfWeek(info.getDate()), calendarEntity));
                  if (exceptionDays.stream().anyMatch(e -> e.equals(info.getDate()))) {
                    info.setIsActive(Boolean.FALSE);
                  }
                });
      }
    }
    return calendarDaysStatusInfoList;
  }

  private String validateNodeCarrierServiceCalendar(
      String orgId,
      String nodeId,
      String carrierServiceId,
      Optional<NodeCarrierServiceCalendarEntity> nodeCarrierServiceCalendarEntity)
      throws CommonServiceException {
    String calendarId;
    if (nodeCarrierServiceCalendarEntity.isPresent()) {
      calendarId = nodeCarrierServiceCalendarEntity.get().getCalendarId();
    } else {
      throw new CommonServiceException(
          "No active calendar associated to the node, carrier & service",
          HttpStatus.NOT_FOUND,
          0xfffff3,
          Map.of(
              ORG_ID,
              FieldError.builder().rejectedValue(orgId).build(),
              NODE_ID,
              FieldError.builder().rejectedValue(nodeId).build(),
              CARRIER_SERVICE_ID,
              FieldError.builder().rejectedValue(carrierServiceId).build()));
    }
    return calendarId;
  }

  private String validateCarrierServiceCalendar(
      String orgId,
      String carrierServiceId,
      Optional<CarrierServiceCalendarEntity> carrierServiceCalendarEntity)
      throws CommonServiceException {
    String calendarId;
    if (carrierServiceCalendarEntity.isPresent()) {
      calendarId = carrierServiceCalendarEntity.get().getCalendarId();
    } else {
      throw new CommonServiceException(
          "No active calendar associated to the carrier & service",
          HttpStatus.NOT_FOUND,
          0xfffff2,
          Map.of(
              ORG_ID,
              FieldError.builder().rejectedValue(orgId).build(),
              CARRIER_SERVICE_ID,
              FieldError.builder().rejectedValue(carrierServiceId).build()));
    }
    return calendarId;
  }

  private String validateNodeCalendar(
      String orgId, String nodeId, Optional<NodeCalendarEntity> nodeCalendarEntity)
      throws CommonServiceException {
    String calendarId;
    if (nodeCalendarEntity.isPresent()) {
      calendarId = nodeCalendarEntity.get().getCalendarId();
    } else {
      throw new CommonServiceException(
          "No active calendar associated to the node",
          HttpStatus.NOT_FOUND,
          0xfffff1,
          Map.of(
              ORG_ID,
              FieldError.builder().rejectedValue(orgId).build(),
              NODE_ID,
              FieldError.builder().rejectedValue(nodeId).build()));
    }
    return calendarId;
  }

  private boolean findWeekInfo(int x, CalendarEntity calendarEntity) {
    if (x == 1) return calendarEntity.getIsMondayWorking();
    if (x == 2) return calendarEntity.getIsTuesdayWorking();
    if (x == 3) return calendarEntity.getIsWednesdayWorking();
    if (x == 4) return calendarEntity.getIsThursdayWorking();
    if (x == 5) return calendarEntity.getIsFridayWorking();
    if (x == 6) return calendarEntity.getIsSaturdayWorking();
    if (x == 7) return calendarEntity.getIsSundayWorking();
    return false;
  }
}
