package com.nextuple.pe.masterdata.domain;

import com.nextuple.pe.masterdata.domain.entity.ServiceOptionInventoryTypeEntity;
import com.nextuple.pe.masterdata.domain.repository.ServiceInventoryRepository;
import com.nextuple.pe.masterdata.exception.ServiceInventoryDomainException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ServiceOptionInventoryTypeDomain {
  private static final Logger logger =
      LoggerFactory.getLogger(ServiceOptionInventoryTypeDomain.class);
  private final ServiceInventoryRepository serviceInventoryRepository;

  public ServiceOptionInventoryTypeEntity saveServiceOptionInventoryTypeEntity(
      ServiceOptionInventoryTypeEntity serviceOptionInventoryTypeEntity)
      throws ServiceInventoryDomainException {
    try {
      return serviceInventoryRepository.save(serviceOptionInventoryTypeEntity);
    } catch (Exception e) {
      logger.error(String.valueOf(e), "Unable to create ServiceOptionInventoryTypeEntity");
      throw new ServiceInventoryDomainException(
          "Error while saving the ServiceOptionInventoryTypeEntity",
          serviceOptionInventoryTypeEntity.getOrgId(),
          serviceOptionInventoryTypeEntity.getServiceOption());
    }
  }

  public Optional<ServiceOptionInventoryTypeEntity>
      findServiceOptionInventoryTypeEntityByOrgIdAndServiceOption(
          String orgId, String serviceOption) throws ServiceInventoryDomainException {
    try {
      return serviceInventoryRepository.findServiceOptionInventoryTypeEntityByOrgIdAndServiceOption(
          orgId, serviceOption);
    } catch (Exception e) {
      logger.error(String.valueOf(e), "Unable to find ServiceOptionInventoryTypeEntity");
      throw new ServiceInventoryDomainException(
          "Error while finding ServiceOptionInventoryTypeEntity", orgId, serviceOption);
    }
  }
}
