package com.hbc.pe.masterdata.calendar.service;

import com.hbc.calendar.domain.inbound.CarrierServiceCalendarRequest;
import com.hbc.calendar.domain.outbound.CarrierServiceCalendarResponse;
import com.hbc.common.exception.CommonServiceException;
import com.hbc.common.response.error.FieldError;
import com.hbc.pe.masterdata.calendar.domain.CalendarDomain;
import com.hbc.pe.masterdata.calendar.domain.CarrierServiceCalendarDomain;
import com.hbc.pe.masterdata.calendar.domain.entity.CalendarEntity;
import com.hbc.pe.masterdata.calendar.domain.entity.CarrierServiceCalendarEntity;
import com.hbc.pe.masterdata.calendar.domain.mapper.CalendarMapper;
import com.hbc.pe.masterdata.calendar.exception.CalendarDomainException;
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
  private static final String ORG_ID = "orgId";
  private static final String CALENDAR_ID = "calendarId";

  /** Creates a new Carrier Service Calendar */
  public CarrierServiceCalendarResponse processCreateCarrierServiceCalendar(
      CarrierServiceCalendarRequest carrierServiceCalendarRequest)
      throws CalendarDomainException, CommonServiceException {
    validateCalendarId(
        carrierServiceCalendarRequest.getCalendarId(), carrierServiceCalendarRequest.getOrgId());
    var carrierServiceCalendarEntity =
        INSTANCE.convertToCarrierServiceCalendarEntity(carrierServiceCalendarRequest);
    var savedCarrierServiceCalendarEntity =
        carrierServiceCalendarDomain.saveCarrierServiceCalendarEntity(carrierServiceCalendarEntity);
    return INSTANCE.convertToCarrierServiceCalendarResponse(savedCarrierServiceCalendarEntity);
  }

  /** Get Carrier&Service Calendar details by orgId and carrierServiceId */
  public List<CarrierServiceCalendarResponse> processGetCarrierServiceCalendar(
      String orgId,
      String carrierServiceId,
      Optional<String> serviceOption,
      Optional<String> shippingStage)
      throws CalendarDomainException {
    return INSTANCE.convertToCarrierServiceCalendarResponseList(
        getAndFilterCarrierServiceCalendar(orgId, carrierServiceId, serviceOption, shippingStage));
  }

  public List<CarrierServiceCalendarEntity> getAndFilterCarrierServiceCalendar(
      String orgId,
      String carrierServiceId,
      Optional<String> serviceOption,
      Optional<String> shippingStage)
      throws CalendarDomainException {
    List<CarrierServiceCalendarEntity> entityList;
    List<CarrierServiceCalendarEntity> filteredList;
    try {
      if (serviceOption.isPresent() && !ObjectUtils.isEmpty(serviceOption.get())) {
        String carrierServiceOption = "ALL-" + serviceOption.get();
        entityList =
            carrierServiceCalendarDomain.getCarrierServiceCalendar(
                orgId, carrierServiceId, carrierServiceOption, shippingStage.orElse("ALL"));
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
    CalendarEntity calendarEntity = calendarDomain.getCalendar(orgId, calendarId);

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
}
