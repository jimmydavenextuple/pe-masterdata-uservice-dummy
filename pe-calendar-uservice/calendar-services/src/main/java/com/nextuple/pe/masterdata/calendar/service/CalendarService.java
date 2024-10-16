/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.pe.masterdata.calendar.service;

import static com.nextuple.common.constants.CommonConstants.DEFAULT_SORT_ORDER;
import static com.nextuple.common.constants.CommonConstants.DESC_SORT_ORDER;

import com.nextuple.calendar.domain.CalendarDaysStatusInfo;
import com.nextuple.calendar.domain.inbound.CalendarRequest;
import com.nextuple.calendar.domain.outbound.CalendarResponse;
import com.nextuple.calendar.persistence.domain.CalendarDomainDto;
import com.nextuple.calendar.persistence.domain.CarrierServiceCalendarDomainDto;
import com.nextuple.calendar.persistence.domain.NodeCalendarDomainDto;
import com.nextuple.calendar.persistence.domain.NodeCarrierServiceCalendarDomainDto;
import com.nextuple.calendar.persistence.exception.CalendarDomainException;
import com.nextuple.calendar.persistence.exception.CalenderServiceException;
import com.nextuple.calendar.persistence.exception.DateException;
import com.nextuple.calendar.persistence.service.CalendarPersistenceService;
import com.nextuple.calendar.persistence.service.NodeCalendarPersistenceService;
import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.error.FieldError;
import com.nextuple.pe.masterdata.calendar.domain.NodeCarrierServiceCalendarDto;
import com.nextuple.pe.masterdata.calendar.domain.mapper.CalendarMapper;
import com.nextuple.pe.masterdata.calendar.util.CalendarUtil;
import com.nextuple.pe.masterdata.calendar.util.DateUtil;
import com.nextuple.pe.masterdata.calendar.util.DateValidation;
import com.nextuple.postgres.config.ReaderDS;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

@Service
@RequiredArgsConstructor
public class CalendarService {

  private static final Logger logger = LoggerFactory.getLogger(CalendarService.class);
  private static final CalendarMapper INSTANCE = Mappers.getMapper(CalendarMapper.class);
  private final CalendarPersistenceService calendarPersistenceService;
  private final NodeCalendarPersistenceService nodeCalendarPersistenceService;
  private final CarrierServiceCalendarService carrierServiceCalendarService;
  private final DateValidation dateValidation;
  private final NodeCarrierServiceCalendarService nodeCarrierServiceCalendarService;
  private static final String ORG_ID = "orgId";
  private static final String NODE_ID = "nodeId";
  private static final String CARRIER_SERVICE_ID = "carrierServiceId";
  private static final String CALENDAR_ID = "calendarId";
  private static final String SORT_ORDER = "sortOrder";

  @Value("${constants.default-number-of-days-in-future}")
  private Integer defaultNumberOfDaysInFuture;

  /** Creates a new Calendar */
  public CalendarResponse processCreateCalendar(CalendarRequest calendarRequest)
      throws CalendarDomainException, DateException, CommonServiceException {
    if (Objects.nonNull(calendarRequest.getExceptionDays())
        && !dateValidation.validateExceptionDays(calendarRequest.getExceptionDays())) {
      throw new DateException(
          "Date is invalid / missing", calendarRequest.getCalendarId(), calendarRequest.getOrgId());
    }
    var calendarDomainDto = INSTANCE.convertToCalendarDomainDto(calendarRequest);
    Optional<CalendarDomainDto> existingCalendarDto =
        calendarPersistenceService.findCalendarDetailsByCalendarIdAndOrgId(
            calendarRequest.getCalendarId(), calendarRequest.getOrgId());

    if (existingCalendarDto.isPresent()) {
      logger.error(
          "Calendar already exists for calendarId:{} , orgId:{}",
          calendarDomainDto.getCalendarId(),
          calendarDomainDto.getOrgId());
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(
          CALENDAR_ID,
          FieldError.builder().rejectedValue(calendarDomainDto.getCalendarId()).build());
      errorMap.put(
          ORG_ID, FieldError.builder().rejectedValue(calendarDomainDto.getOrgId()).build());
      throw new CommonServiceException(
          "Calendar already exists for the given details",
          HttpStatus.BAD_REQUEST,
          0x1772,
          errorMap);
    }
    var savedCalendarDomainDto = calendarPersistenceService.saveCalendar(calendarDomainDto);
    return INSTANCE.convertToCalendarResponse(savedCalendarDomainDto);
  }

  /** Get Calendar details by calendarId and OrgId */
  @ReaderDS
  public CalendarResponse processGetCalendar(String orgId, String calendarId)
      throws CalendarDomainException, CommonServiceException {
    validateCalendarId(calendarId, orgId);
    return INSTANCE.convertToCalendarResponse(
        calendarPersistenceService.getCalendar(orgId, calendarId));
  }

  @ReaderDS
  public List<CalendarDaysStatusInfo> processGetUpcomingDaysCalendarStatus(
      NodeCarrierServiceCalendarDto nodeCarrierServiceCalendarDto)
      throws CalendarDomainException, CommonServiceException, CalenderServiceException {
    validateRequestFields(
        nodeCarrierServiceCalendarDto.getOrgId(),
        nodeCarrierServiceCalendarDto.getNodeId(),
        nodeCarrierServiceCalendarDto.getCarrierServiceId());
    validateFromDate(nodeCarrierServiceCalendarDto.getFromDate());
    int numDays =
        nodeCarrierServiceCalendarDto
            .getNumberOfDaysInFuture()
            .orElseGet(() -> defaultNumberOfDaysInFuture);
    List<CalendarDaysStatusInfo> calendarDaysStatusInfoList =
        CalendarUtil.getListOfCalendarDaysStatusInfos(
            nodeCarrierServiceCalendarDto.getFromDate(), numDays);
    String currentDate =
        DateUtil.addDaysToGivenDate(nodeCarrierServiceCalendarDto.getFromDate(), 0);
    String startDate = DateUtil.addDaysToGivenDate(nodeCarrierServiceCalendarDto.getFromDate(), -1);
    String endDate =
        DateUtil.addDaysToGivenDate(nodeCarrierServiceCalendarDto.getFromDate(), numDays);
    var currentCalendarId = "";
    var isCurrentEffective = false;
    Map<String, String> nextCalendarIds = new HashMap<>();

    if (nodeCarrierServiceCalendarDto.getNodeId().isPresent()
        && nodeCarrierServiceCalendarDto.getCarrierServiceId().isEmpty()) {
      List<NodeCalendarDomainDto> nodeCalendarDomainDtoList =
          nodeCalendarPersistenceService.getNodeCalendar(
              nodeCarrierServiceCalendarDto.getOrgId(),
              nodeCarrierServiceCalendarDto.getNodeId().get());
      isCurrentEffective = computeIsCurrentEffectiveForNode(nodeCalendarDomainDtoList, currentDate);
      currentCalendarId =
          getCurrentNodeCalendarId(
              nodeCalendarDomainDtoList,
              startDate,
              currentDate,
              nodeCarrierServiceCalendarDto.getOrgId(),
              nodeCarrierServiceCalendarDto.getNodeId().get());
      nextCalendarIds =
          CalendarUtil.getNextNodeCalendarIds(startDate, endDate, nodeCalendarDomainDtoList);
    }
    if (nodeCarrierServiceCalendarDto.getCarrierServiceId().isPresent()
        && nodeCarrierServiceCalendarDto.getNodeId().isEmpty()) {
      List<CarrierServiceCalendarDomainDto> carrierServiceCalendarDomainDtoList =
          carrierServiceCalendarService.getAndFilterCarrierServiceCalendar(
              nodeCarrierServiceCalendarDto.getOrgId(),
              nodeCarrierServiceCalendarDto.getCarrierServiceId().get(),
              nodeCarrierServiceCalendarDto.getServiceOption(),
              nodeCarrierServiceCalendarDto.getShippingStage());
      isCurrentEffective =
          computeIsCurrentEffectiveForCarrier(carrierServiceCalendarDomainDtoList, currentDate);
      currentCalendarId =
          getCurrentCarrierCalendarId(
              carrierServiceCalendarDomainDtoList,
              startDate,
              currentDate,
              nodeCarrierServiceCalendarDto.getOrgId(),
              nodeCarrierServiceCalendarDto.getCarrierServiceId().get());
      nextCalendarIds =
          CalendarUtil.getNextCarrierCalendarIds(
              startDate, endDate, carrierServiceCalendarDomainDtoList);
    }
    if (nodeCarrierServiceCalendarDto.getNodeId().isPresent()
        && nodeCarrierServiceCalendarDto.getCarrierServiceId().isPresent()) {
      List<NodeCarrierServiceCalendarDomainDto> nodeCarrierServiceCalendarDomainDtoList =
          nodeCarrierServiceCalendarService.getAndFilterNodeCarrierServiceCalendar(
              nodeCarrierServiceCalendarDto.getOrgId(),
              nodeCarrierServiceCalendarDto.getNodeId().get(),
              nodeCarrierServiceCalendarDto.getCarrierServiceId().get(),
              nodeCarrierServiceCalendarDto.getServiceOption());
      isCurrentEffective =
          computeIsCurrentEffectiveForNodeCarrier(
              nodeCarrierServiceCalendarDomainDtoList, currentDate);
      currentCalendarId =
          getCurrentNodeCarrierCalendarId(
              nodeCarrierServiceCalendarDomainDtoList,
              startDate,
              currentDate,
              nodeCarrierServiceCalendarDto);
      nextCalendarIds =
          CalendarUtil.getNextNodeCarrierCalendarIds(
              startDate, endDate, nodeCarrierServiceCalendarDomainDtoList);
    }
    var calendarDomainDto =
        calendarPersistenceService.getCalendar(
            nodeCarrierServiceCalendarDto.getOrgId(), currentCalendarId);
    CalendarUtil.checkCalendarDomainDtoIsEmpty(
        nodeCarrierServiceCalendarDto.getOrgId(), currentCalendarId, calendarDomainDto);
    List<String> exceptionDays =
        CalendarUtil.getExceptionDaysForCalendar(calendarDomainDto.getExceptionDays());
    setCalendarDaysStatusInfoList(
        calendarDaysStatusInfoList, exceptionDays, calendarDomainDto, isCurrentEffective);
    if (nextCalendarIds.isEmpty()) return calendarDaysStatusInfoList;
    return calculateOtherActiveCalendarStatuses(
        nodeCarrierServiceCalendarDto.getOrgId(), calendarDaysStatusInfoList, nextCalendarIds);
  }

  private boolean computeIsCurrentEffectiveForNode(
      List<NodeCalendarDomainDto> nodeCalendarDomainDtoList, String currentDate) {
    /**
     * Comparing the current date with effective date of last record in the node calendar list since
     * the list will be sorted in descending order on effective dates
     */
    return !CollectionUtils.isEmpty(nodeCalendarDomainDtoList)
        && currentDate.equals(
            nodeCalendarDomainDtoList.get(nodeCalendarDomainDtoList.size() - 1).getEffectiveDate());
  }

  private boolean computeIsCurrentEffectiveForCarrier(
      List<CarrierServiceCalendarDomainDto> carrierServiceCalendarDomainDtoList,
      String currentDate) {
    /**
     * Comparing the current date with effective date of last record in the carrier calendar list
     * since the list will be sorted in descending order on effective dates
     */
    return !CollectionUtils.isEmpty(carrierServiceCalendarDomainDtoList)
        && currentDate.equals(
            carrierServiceCalendarDomainDtoList
                .get(carrierServiceCalendarDomainDtoList.size() - 1)
                .getEffectiveDate());
  }

  private boolean computeIsCurrentEffectiveForNodeCarrier(
      List<NodeCarrierServiceCalendarDomainDto> nodeCarrierServiceCalendarDomainDtoList,
      String currentDate) {
    /**
     * Comparing the current date with effective date of last record in the node carrier calendar
     * list since the list will be sorted in descending order on effective dates
     */
    return !CollectionUtils.isEmpty(nodeCarrierServiceCalendarDomainDtoList)
        && currentDate.equals(
            nodeCarrierServiceCalendarDomainDtoList
                .get(nodeCarrierServiceCalendarDomainDtoList.size() - 1)
                .getEffectiveDate());
  }

  private String getCurrentNodeCalendarId(
      List<NodeCalendarDomainDto> nodeCalendarDomainDtoList,
      String startDate,
      String currentDate,
      String orgId,
      String nodeId)
      throws CommonServiceException {
    if (computeIsCurrentEffectiveForNode(nodeCalendarDomainDtoList, currentDate)) {
      /**
       * Return last record as the current calendarId from the node calendar list, if the effective
       * date is the current date
       */
      return nodeCalendarDomainDtoList.get(nodeCalendarDomainDtoList.size() - 1).getCalendarId();
    }

    Optional<NodeCalendarDomainDto> nodeCalendarDomainDto =
        nodeCalendarDomainDtoList.stream()
            .filter(x -> x.getEffectiveDate().compareTo(startDate) <= 0)
            .max(Comparator.comparing(NodeCalendarDomainDto::getEffectiveDate));

    return validateNodeCalendar(orgId, nodeId, nodeCalendarDomainDto);
  }

  private String getCurrentCarrierCalendarId(
      List<CarrierServiceCalendarDomainDto> carrierServiceCalendarDomainDtoList,
      String startDate,
      String currentDate,
      String orgId,
      String carrierServiceId)
      throws CommonServiceException {
    if (computeIsCurrentEffectiveForCarrier(carrierServiceCalendarDomainDtoList, currentDate)) {
      /**
       * Return last record as the current calendarId from the carrier calendar list, if the
       * effective date is the current date
       */
      return carrierServiceCalendarDomainDtoList
          .get(carrierServiceCalendarDomainDtoList.size() - 1)
          .getCalendarId();
    }
    Optional<CarrierServiceCalendarDomainDto> carrierServiceCalendarDomainDto =
        carrierServiceCalendarDomainDtoList.stream()
            .filter(x -> x.getEffectiveDate().compareTo(startDate) <= 0)
            .max(Comparator.comparing(CarrierServiceCalendarDomainDto::getEffectiveDate));

    return validateCarrierServiceCalendar(orgId, carrierServiceId, carrierServiceCalendarDomainDto);
  }

  private String getCurrentNodeCarrierCalendarId(
      List<NodeCarrierServiceCalendarDomainDto> nodeCarrierServiceCalendarDomainDtoList,
      String startDate,
      String currentDate,
      NodeCarrierServiceCalendarDto nodeCarrierServiceCalendarDto)
      throws CommonServiceException {
    if (computeIsCurrentEffectiveForNodeCarrier(
        nodeCarrierServiceCalendarDomainDtoList, currentDate)) {
      /**
       * Return last record as the current calendarId from the node carrier calendar list, if the
       * effective date is the current date
       */
      return nodeCarrierServiceCalendarDomainDtoList
          .get(nodeCarrierServiceCalendarDomainDtoList.size() - 1)
          .getCalendarId();
    }

    Optional<NodeCarrierServiceCalendarDomainDto> nodeCarrierServiceCalendarDomainDto =
        nodeCarrierServiceCalendarDomainDtoList.stream()
            .filter(x -> x.getEffectiveDate().compareTo(startDate) <= 0)
            .max(Comparator.comparing(NodeCarrierServiceCalendarDomainDto::getEffectiveDate));

    return validateNodeCarrierServiceCalendar(
        nodeCarrierServiceCalendarDto.getOrgId(),
        nodeCarrierServiceCalendarDto.getNodeId().get(),
        nodeCarrierServiceCalendarDto.getCarrierServiceId().get(),
        nodeCarrierServiceCalendarDomainDto);
  }

  private void setCalendarDaysStatusInfoList(
      List<CalendarDaysStatusInfo> calendarDaysStatusInfoList,
      List<String> exceptionDays,
      CalendarDomainDto calendarDomainDto,
      boolean isCurrentEffective) {
    for (CalendarDaysStatusInfo info : calendarDaysStatusInfoList) {
      if (isCurrentEffective) {
        isCurrentEffective = false;
        continue;
      }
      info.setIsActive(findWeekInfo(DateUtil.getDayOfWeek(info.getDate()), calendarDomainDto));
      if (exceptionDays.stream().anyMatch(e -> e.equals(info.getDate()))) {
        info.setIsActive(Boolean.FALSE);
      }
    }
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
        var calendarDomainDto =
            calendarPersistenceService.getCalendar(orgId, calendarIds.get(x.getDate()));

        List<String> exceptionDays =
            CalendarUtil.getExceptionDaysForCalendar(calendarDomainDto.getExceptionDays());
        calendarDaysStatusInfoList.stream()
            .filter(info -> info.getDate().compareTo(x.getDate()) >= 0)
            .forEach(
                info -> {
                  info.setIsActive(
                      findWeekInfo(DateUtil.getDayOfWeek(info.getDate()), calendarDomainDto));
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
      Optional<NodeCarrierServiceCalendarDomainDto> nodeCarrierServiceCalendarDomainDto)
      throws CommonServiceException {
    String calendarId;
    if (nodeCarrierServiceCalendarDomainDto.isPresent()) {
      calendarId = nodeCarrierServiceCalendarDomainDto.get().getCalendarId();
    } else {
      throw new CommonServiceException(
          "No active calendar associated to the node, carrier & service",
          HttpStatus.BAD_REQUEST,
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
      Optional<CarrierServiceCalendarDomainDto> carrierServiceCalendarDomainDto)
      throws CommonServiceException {
    String calendarId;
    if (carrierServiceCalendarDomainDto.isPresent()) {
      calendarId = carrierServiceCalendarDomainDto.get().getCalendarId();
    } else {
      throw new CommonServiceException(
          "No active calendar associated to the carrier & service",
          HttpStatus.BAD_REQUEST,
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
      String orgId, String nodeId, Optional<NodeCalendarDomainDto> nodeCalendarDomainDto)
      throws CommonServiceException {
    String calendarId;
    if (nodeCalendarDomainDto.isPresent()) {
      calendarId = nodeCalendarDomainDto.get().getCalendarId();
    } else {
      throw new CommonServiceException(
          "No active calendar associated to the node",
          HttpStatus.BAD_REQUEST,
          0xfffff1,
          Map.of(
              ORG_ID,
              FieldError.builder().rejectedValue(orgId).build(),
              NODE_ID,
              FieldError.builder().rejectedValue(nodeId).build()));
    }
    return calendarId;
  }

  public void validateCalendarId(String calendarId, String orgId)
      throws CalendarDomainException, CommonServiceException {
    var calendarDomainDto = calendarPersistenceService.getCalendar(orgId, calendarId);

    if (ObjectUtils.isEmpty(calendarDomainDto)) {
      logger.error("Calendar does not exists");
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
      errorMap.put(CALENDAR_ID, FieldError.builder().rejectedValue(calendarId).build());
      throw new CommonServiceException(
          "Calendar does not exists", HttpStatus.NOT_FOUND, 0x1771, errorMap);
    }
  }

  private boolean findWeekInfo(int x, CalendarDomainDto calendarDomainDto) {
    if (x == 1) return calendarDomainDto.getIsMondayWorking();
    if (x == 2) return calendarDomainDto.getIsTuesdayWorking();
    if (x == 3) return calendarDomainDto.getIsWednesdayWorking();
    if (x == 4) return calendarDomainDto.getIsThursdayWorking();
    if (x == 5) return calendarDomainDto.getIsFridayWorking();
    if (x == 6) return calendarDomainDto.getIsSaturdayWorking();
    if (x == 7) return calendarDomainDto.getIsSundayWorking();
    return false;
  }

  public Page<CalendarResponse> getCalendarList(
      String orgId, Integer pageNo, Integer pageSize, String sortBy, String sortOrder)
      throws CommonServiceException, CalendarDomainException {
    if (sortOrder.equalsIgnoreCase(DEFAULT_SORT_ORDER)
        || sortOrder.equalsIgnoreCase(DESC_SORT_ORDER)) {
      return calendarPersistenceService
          .findCalendarListByOrgId(orgId, pageNo, pageSize, sortBy, sortOrder)
          .map(INSTANCE::convertToCalendarResponse);
    } else {
      logger.error("Invalid sort order");
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(SORT_ORDER, FieldError.builder().rejectedValue(sortOrder).build());
      throw new CommonServiceException(
          "Invalid sort order, consider giving either ASC or DESC",
          HttpStatus.BAD_REQUEST,
          0x1771,
          errorMap);
    }
  }

  private void validateFromDate(Optional<String> fromDate) throws CommonServiceException {
    SimpleDateFormat format = new SimpleDateFormat(DateUtil.SIMPLE_DATE_FORMAT);
    format.setLenient(false);
    try {
      if (Objects.nonNull(fromDate) && fromDate.isPresent()) format.parse(fromDate.get());
    } catch (ParseException e) {
      throw new CommonServiceException(
          "Invalid fromDate or fromDate format, expected yyyy-MM-dd format",
          HttpStatus.BAD_REQUEST,
          0xfffff3,
          Map.of("fromDate", FieldError.builder().rejectedValue(fromDate).build()));
    }
  }
}
