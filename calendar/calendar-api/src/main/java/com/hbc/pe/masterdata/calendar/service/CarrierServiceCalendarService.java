package com.hbc.pe.masterdata.calendar.service;

import com.hbc.calendar.domain.dto.CarrierCalendarCacheKeyDto;
import com.hbc.calendar.domain.inbound.CarrierServiceCalendarRequest;
import com.hbc.calendar.domain.outbound.CarrierServiceCalendarResponse;
import com.hbc.common.exception.CommonServiceException;
import com.hbc.common.response.error.FieldError;
import com.hbc.pe.masterdata.calendar.domain.CalendarDomain;
import com.hbc.pe.masterdata.calendar.domain.CarrierServiceCalendarDomain;
import com.hbc.pe.masterdata.calendar.domain.entity.CarrierServiceCalendarEntity;
import com.hbc.pe.masterdata.calendar.domain.mapper.CalendarMapper;
import com.hbc.pe.masterdata.calendar.domain.repository.CarrierServiceCalendarRepository;
import com.hbc.pe.masterdata.calendar.exception.CalendarDomainException;
import com.hbc.pe.masterdata.calendar.exception.CalenderServiceException;
import com.hbc.pe.masterdata.calendar.exception.DateException;
import com.hbc.pe.masterdata.calendar.util.DateValidation;
import com.hbc.postgres.config.ReaderDS;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

@Service
@RequiredArgsConstructor
public class CarrierServiceCalendarService {

  private static final Logger logger = LoggerFactory.getLogger(CarrierServiceCalendarService.class);
  private static final CalendarMapper INSTANCE = Mappers.getMapper(CalendarMapper.class);
  private final CarrierServiceCalendarDomain carrierServiceCalendarDomain;
  private final CalendarDomain calendarDomain;
  private final CarrierServiceCalendarRepository carrierServiceCalendarRepository;
  private final DateValidation dateValidation;
  private static final String ORG_ID = "orgId";
  private static final String CALENDAR_ID = "calendarId";
  private static final String CARRIER_SERVICE_ID = "carrierServiceId";
  private static final String SHIPPING_STAGE = "shippingStage";
  private static final String EFFECTIVE_DATE = "effectiveDate";

  /** Creates a new Carrier Service Calendar */
  public CarrierServiceCalendarResponse processCreateCarrierServiceCalendar(
      CarrierServiceCalendarRequest carrierServiceCalendarRequest)
      throws CalendarDomainException, CommonServiceException, DateException {
    if (!dateValidation.validateDate(carrierServiceCalendarRequest.getEffectiveDate())) {
      throw new DateException(
          "Invalid Date",
          carrierServiceCalendarRequest.getCalendarId(),
          carrierServiceCalendarRequest.getOrgId());
    }
    validateCalendarId(
        carrierServiceCalendarRequest.getCalendarId(), carrierServiceCalendarRequest.getOrgId());
    var carrierServiceCalendarEntity =
        INSTANCE.convertToCarrierServiceCalendarEntity(carrierServiceCalendarRequest);
    Optional<CarrierServiceCalendarEntity> existingCarrierServiceCalendarEntity =
        carrierServiceCalendarRepository
            .findByCalendarIdAndOrgIdAndCarrierServiceIdAndShippingStageAndEffectiveDate(
                carrierServiceCalendarRequest.getCalendarId(),
                carrierServiceCalendarRequest.getOrgId(),
                carrierServiceCalendarRequest.getCarrierServiceId(),
                carrierServiceCalendarRequest.getShippingStage(),
                carrierServiceCalendarRequest.getEffectiveDate());
    if (existingCarrierServiceCalendarEntity.isPresent()) {
      logger.error(
          "Node Calendar already exists for calendarId:{} ,orgId:{}, nodeId:{} ,shippingStage:{} , effectiveDate:{}",
          carrierServiceCalendarRequest.getCalendarId(),
          carrierServiceCalendarRequest.getOrgId(),
          carrierServiceCalendarRequest.getCarrierServiceId(),
          carrierServiceCalendarRequest.getShippingStage(),
          carrierServiceCalendarRequest.getEffectiveDate());
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(
          CALENDAR_ID,
          FieldError.builder().rejectedValue(carrierServiceCalendarEntity.getCalendarId()).build());
      errorMap.put(
          ORG_ID,
          FieldError.builder().rejectedValue(carrierServiceCalendarEntity.getOrgId()).build());
      errorMap.put(
          CARRIER_SERVICE_ID,
          FieldError.builder()
              .rejectedValue(carrierServiceCalendarEntity.getCarrierServiceId())
              .build());
      errorMap.put(
          SHIPPING_STAGE,
          FieldError.builder()
              .rejectedValue(carrierServiceCalendarEntity.getShippingStage())
              .build());
      errorMap.put(
          EFFECTIVE_DATE,
          FieldError.builder()
              .rejectedValue(carrierServiceCalendarEntity.getEffectiveDate())
              .build());
      throw new CommonServiceException(
          "Carrier Service Calendar already exists for the given details",
          HttpStatus.BAD_REQUEST,
          0x1772,
          errorMap);
    }
    var savedCarrierServiceCalendarEntity =
        carrierServiceCalendarDomain.saveCarrierServiceCalendarEntity(carrierServiceCalendarEntity);
    return INSTANCE.convertToCarrierServiceCalendarResponse(savedCarrierServiceCalendarEntity);
  }

  /** Get Carrier&Service Calendar details by orgId and carrierServiceId */
  @ReaderDS
  public List<CarrierServiceCalendarResponse> processGetCarrierServiceCalendar(
      String orgId,
      String carrierServiceId,
      Optional<String> serviceOption,
      Optional<String> shippingStage)
      throws CalendarDomainException, CalenderServiceException {
    return INSTANCE.convertToCarrierServiceCalendarResponseList(
        getAndFilterCarrierServiceCalendar(orgId, carrierServiceId, serviceOption, shippingStage));
  }

  public List<CarrierServiceCalendarEntity> getAndFilterCarrierServiceCalendar(
      String orgId,
      String carrierServiceId,
      Optional<String> serviceOption,
      Optional<String> shippingStage)
      throws CalendarDomainException, CalenderServiceException {
    List<CarrierServiceCalendarEntity> entityList;
    List<CarrierServiceCalendarEntity> filteredList;
    try {
      if (serviceOption.isPresent() && !ObjectUtils.isEmpty(serviceOption.get())) {
        String carrierServiceOption = "ALL-" + serviceOption.get();
        entityList =
            carrierServiceCalendarDomain.getCarrierServiceCalendar(
                orgId, carrierServiceId, carrierServiceOption, shippingStage.orElse("ALL"));
        if (CollectionUtils.isEmpty(entityList))
          throw new CalenderServiceException("Calender doesn't exist", orgId, carrierServiceId);
        filteredList =
            "ALL".equals(carrierServiceId)
                ? new ArrayList<>()
                : getFilteredEntityList(carrierServiceId, entityList);
        if (CollectionUtils.isEmpty(filteredList)) {
          filteredList = getFilteredEntityList(carrierServiceOption, entityList);
        }
      } else {
        entityList =
            carrierServiceCalendarDomain.getCarrierServiceCalendar(
                orgId, carrierServiceId, shippingStage.orElse("ALL"));
        if (CollectionUtils.isEmpty(entityList))
          throw new CalenderServiceException("Calender doesn't exist", orgId, carrierServiceId);
        filteredList = getFilteredEntityList(carrierServiceId, entityList);
      }
      return CollectionUtils.isEmpty(filteredList) ? entityList : filteredList;
    } catch (Exception e) {
      logger.error("Error in getAndFilterCarrierServiceCalendar");
      throw e;
    }
  }

  private List<CarrierServiceCalendarEntity> getFilteredEntityList(
      String carrierServiceId, List<CarrierServiceCalendarEntity> entityList) {
    return entityList.stream()
        .filter(x -> carrierServiceId.equals(x.getCarrierServiceId()))
        .collect(Collectors.toList());
  }

  public void validateCalendarId(String calendarId, String orgId)
      throws CalendarDomainException, CommonServiceException {
    var calendarEntity = calendarDomain.getCalendar(orgId, calendarId);

    if (ObjectUtils.isEmpty(calendarEntity)) {
      logger.error("Cannot create a carrier service calendar as calendarId/orgId is invalid");
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
      errorMap.put(CALENDAR_ID, FieldError.builder().rejectedValue(calendarId).build());
      throw new CommonServiceException(
          "Cannot create a carrier service calendar as calendarId/orgId is invalid",
          HttpStatus.NOT_FOUND,
          0x1771,
          errorMap);
    }
  }

  public List<CarrierCalendarCacheKeyDto> getAllCarrierCalendarCacheKeys(Integer limit)
      throws CalendarDomainException {
    var carrierServiceCalendarEntities =
        carrierServiceCalendarDomain.getAllCarrierServiceCalendars(limit);

    return INSTANCE.convertToCarrierCalendarCacheKeyDtoList(carrierServiceCalendarEntities);
  }

  public List<CarrierServiceCalendarResponse> getCarrierServiceAssociationWithCalendar(
      String calendarId, String orgId) throws CalendarDomainException {
    var carrierServiceCalendarEntities =
        carrierServiceCalendarDomain.getCarrierServiceCalendarByOrgIdAndCalendarId(
            calendarId, orgId);

    return INSTANCE.convertToCarrierServiceCalendarResponseList(carrierServiceCalendarEntities);
  }

  /** Upserts a Carrier Service Calendar (creates new or updates existing) */
  public CarrierServiceCalendarResponse processUpsertCarrierServiceCalendar(
      CarrierServiceCalendarRequest carrierServiceCalendarRequest)
      throws CalendarDomainException, CommonServiceException, DateException {
    if (!dateValidation.validateDate(carrierServiceCalendarRequest.getEffectiveDate())) {
      throw new DateException(
          "Invalid Date",
          carrierServiceCalendarRequest.getCalendarId(),
          carrierServiceCalendarRequest.getOrgId());
    }
    validateCalendarId(
        carrierServiceCalendarRequest.getCalendarId(), carrierServiceCalendarRequest.getOrgId());
    
    var carrierServiceCalendarEntity =
        INSTANCE.convertToCarrierServiceCalendarEntity(carrierServiceCalendarRequest);
    
    var savedCarrierServiceCalendarEntity =
        carrierServiceCalendarDomain.saveCarrierServiceCalendarEntity(carrierServiceCalendarEntity);
    return INSTANCE.convertToCarrierServiceCalendarResponse(savedCarrierServiceCalendarEntity);
  }
}
