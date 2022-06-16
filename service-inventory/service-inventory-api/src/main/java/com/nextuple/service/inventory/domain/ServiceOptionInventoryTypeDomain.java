package com.nextuple.service.inventory.domain;

import com.nextuple.service.inventory.domain.entity.ServiceOptionInventoryTypeEntity;
import com.nextuple.service.inventory.exception.ServiceInventoryDomainException;
import com.nextuple.service.inventory.repository.ServiceInventoryRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

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
