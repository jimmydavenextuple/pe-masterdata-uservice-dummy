package com.hbc.pe.masterdata.calendar.domain;

import com.hbc.pe.masterdata.calendar.domain.entity.CarrierServiceCalendarEntity;
import com.hbc.pe.masterdata.calendar.domain.repository.CarrierServiceCalendarRepository;
import com.hbc.pe.masterdata.calendar.exception.CalendarDomainException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CarrierServiceCalendarDomain {
  private static final Logger logger = LoggerFactory.getLogger(CarrierServiceCalendarDomain.class);
  private final CarrierServiceCalendarRepository carrierServiceCalendarRepository;

  public CarrierServiceCalendarEntity saveCarrierServiceCalendarEntity(
      CarrierServiceCalendarEntity carrierServiceCalendarEntity) throws CalendarDomainException {
    logger.debug("Inside saveCarrierServiceEntity()");
    try {
      return carrierServiceCalendarRepository.save(carrierServiceCalendarEntity);
    } catch (Exception e) {
      throw new CalendarDomainException(
          "Unable to create carrier service calendar",
          e,
          carrierServiceCalendarEntity.getCalendarId(),
          carrierServiceCalendarEntity.getOrgId(),
          null,
          carrierServiceCalendarEntity.getCarrierServiceId());
    }
  }

  public List<CarrierServiceCalendarEntity> getCarrierServiceCalendar(
      String orgId, String carrierServiceId, String shippingStage) throws CalendarDomainException {
    try {
      return carrierServiceCalendarRepository.findAllCarrierServiceCalendar(
          orgId, carrierServiceId, shippingStage);

    } catch (Exception e) {
      throw new CalendarDomainException(
          "Unable to fetch carrier service calendar", e, null, orgId, null, carrierServiceId);
    }
  }

  public List<CarrierServiceCalendarEntity> getCarrierServiceCalendar(
      String orgId, String carrierServiceId, String carrierServiceOption, String shippingStage)
      throws CalendarDomainException {
    try {
      return carrierServiceCalendarRepository.findCarrierServiceCalendar(
          orgId, carrierServiceId, carrierServiceOption, shippingStage);
    } catch (Exception e) {
      throw new CalendarDomainException(
          "Unable to fetch carrier service calendar", e, null, orgId, null, carrierServiceId);
    }
  }
}
