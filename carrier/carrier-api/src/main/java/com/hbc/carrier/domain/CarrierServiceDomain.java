package com.hbc.carrier.domain;

import com.hbc.carrier.domain.entity.CarrierServiceEntity;
import com.hbc.carrier.exception.CarrierServiceDomainException;
import com.hbc.carrier.repository.CarrierServiceRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CarrierServiceDomain {
  private static final Logger logger = LoggerFactory.getLogger(CarrierServiceDomain.class);
  private final CarrierServiceRepository carrierServiceRepository;

  public CarrierServiceEntity saveCarrierServiceEntity(CarrierServiceEntity carrierServiceEntity)
      throws CarrierServiceDomainException {
    try {
      return carrierServiceRepository.save(carrierServiceEntity);
    } catch (Exception e) {
      logger.error(String.valueOf(e), "Unable to create carrier service");
      throw new CarrierServiceDomainException(
          "Error while saving the carrier service",
          carrierServiceEntity.getCarrierId(),
          carrierServiceEntity.getCarrierServiceId(),
          carrierServiceEntity.getOrgId());
    }
  }

  public Optional<CarrierServiceEntity> findCarrierServiceByCarrierIdAndServiceIdAndOrgId(
      String carrierId, String serviceId, String orgId) throws CarrierServiceDomainException {
    try {
      return carrierServiceRepository.findCarrierServiceByCarrierIdAndCarrierServiceIdAndOrgId(
          carrierId, serviceId, orgId);
    } catch (Exception e) {
      logger.error(String.valueOf(e), "Unable to find carrier service");
      throw new CarrierServiceDomainException(
          "Error while finding carrier service", carrierId, serviceId, orgId);
    }
  }

  public void deleteCarrierService(CarrierServiceEntity carrierServiceEntity)
      throws CarrierServiceDomainException {
    try {
      carrierServiceRepository.delete(carrierServiceEntity);
    } catch (Exception e) {
      logger.error(String.valueOf(e), "Unable to delete carrier service");
      throw new CarrierServiceDomainException(
          "Error while deleting carrier service",
          carrierServiceEntity.getCarrierId(),
          carrierServiceEntity.getCarrierServiceId(),
          carrierServiceEntity.getOrgId());
    }
  }
}
