package com.hbc.carrier.domain;

import static com.hbc.common.constants.CommonConstants.DEFAULT_SORT_ORDER;

import com.hbc.carrier.domain.entity.CarrierServiceEntity;
import com.hbc.carrier.domain.mapper.CarrierServiceMapper;
import com.hbc.carrier.domain.outbound.CarrierServiceResponse;
import com.hbc.carrier.exception.CarrierServiceDomainException;
import com.hbc.carrier.repository.CarrierServiceRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CarrierServiceDomain {
  private static final Logger logger = LoggerFactory.getLogger(CarrierServiceDomain.class);
  private final CarrierServiceRepository carrierServiceRepository;
  public static final CarrierServiceMapper INSTANCE = Mappers.getMapper(CarrierServiceMapper.class);

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

  public Page<CarrierServiceResponse> findCarrierServiceListByOrgId(
      String orgId, Integer pageNo, Integer pageSize, String sortBy, String sortOrder)
      throws CarrierServiceDomainException {
    try {
      Pageable pageable = null;
      if (sortOrder.equalsIgnoreCase(DEFAULT_SORT_ORDER)) {
        pageable = PageRequest.of(pageNo - 1, pageSize, Sort.by(sortBy).ascending());
      } else {
        pageable = PageRequest.of(pageNo - 1, pageSize, Sort.by(sortBy).descending());
      }
      return carrierServiceRepository
          .findCarrierServicesByOrgId(orgId, pageable)
          .map(INSTANCE::toCarrierServiceResponse);
    } catch (Exception e) {
      logger.error(String.valueOf(e), "Unable to find carrier service list");
      throw new CarrierServiceDomainException(
          "Error while finding carrier service list", null, null, orgId);
    }
  }

  public List<CarrierServiceResponse> findCarrierServiceListByOrgIdWithoutPagination(String orgId)
      throws CarrierServiceDomainException {
    try {
      List<CarrierServiceEntity> responses =
          carrierServiceRepository.findCarrierServicesByOrgId(orgId);
      return INSTANCE.toCarrierServiceResponseList(responses);
    } catch (Exception e) {
      logger.error(String.valueOf(e), "Unable to find carrier service list");
      throw new CarrierServiceDomainException(
          "Error while finding carrier service list", null, null, orgId);
    }
  }

  public Optional<List<CarrierServiceEntity>> findCarrierServiceByServiceIdAndOrgId(
      String serviceId, String orgId) throws CarrierServiceDomainException {
    try {
      return carrierServiceRepository.findCarrierServiceByCarrierServiceIdAndOrgId(
          serviceId, orgId);
    } catch (Exception e) {
      logger.error(String.valueOf(e), "Unable to find carrier service");
      throw new CarrierServiceDomainException(
          "Error while finding carrier service", "", serviceId, orgId);
    }
  }

  public List<CarrierServiceEntity> getAllCarrierServiceEntities(Integer limit)
      throws CarrierServiceDomainException {
    try {
      return carrierServiceRepository.findAllCarriersByLimit(limit);
    } catch (Exception e) {
      logger.error(String.valueOf(e), "Unable to fetch the carrier entities");
      throw new CarrierServiceDomainException(
          "Error while fetching all carrier services", null, null, null);
    }
  }
}
