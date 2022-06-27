package com.hbc.service.inventory.service;

import com.hbc.common.exception.CommonServiceException;
import com.hbc.common.response.error.FieldError;
import com.hbc.service.inventory.domain.ServiceOptionInventoryTypeDomain;
import com.hbc.service.inventory.domain.entity.ServiceOptionInventoryTypeEntity;
import com.hbc.service.inventory.domain.inbound.ServiceInventoryRequest;
import com.hbc.service.inventory.domain.mapper.ServiceInventoryMapper;
import com.hbc.service.inventory.domain.outbound.ServiceInventoryDto;
import com.hbc.service.inventory.exception.ServiceInventoryDomainException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ServiceOptionInventoryTypeService {
  private static final Logger logger =
      LoggerFactory.getLogger(ServiceOptionInventoryTypeService.class);
  private static final String SERVICE_OPTION = "serviceOption";

  private static final String ORG_ID = "orgId";

  private final ServiceOptionInventoryTypeDomain serviceOptionInventoryTypeDomain;

  public static final ServiceInventoryMapper INSTANCE =
      Mappers.getMapper(ServiceInventoryMapper.class);

  public ServiceInventoryDto createServiceOptionInventoryType(
      ServiceInventoryRequest serviceToInventoryRequest) throws ServiceInventoryDomainException {

    ServiceOptionInventoryTypeEntity serviceOptionInventoryTypeEntity =
        INSTANCE.serviceInventoryRequestToServiceOptionInventoryTypeEntity(
            serviceToInventoryRequest);

    return INSTANCE.toServiceInventoryDto(
        serviceOptionInventoryTypeDomain.saveServiceOptionInventoryTypeEntity(
            serviceOptionInventoryTypeEntity));
  }

  public ServiceInventoryDto getServiceOptionInventoryTypeMapping(
      String orgId, String serviceOption)
      throws ServiceInventoryDomainException, CommonServiceException {

    Optional<ServiceOptionInventoryTypeEntity> serviceOptionInventoryTypeEntity =
        serviceOptionInventoryTypeDomain
            .findServiceOptionInventoryTypeEntityByOrgIdAndServiceOption(orgId, serviceOption);

    if (serviceOptionInventoryTypeEntity.isEmpty()) {
      logger.info(
          "ServiceOptionInventoryType not found with given orgId:{} , serviceOption:{}",
          orgId,
          serviceOption);
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
      errorMap.put(SERVICE_OPTION, FieldError.builder().rejectedValue(serviceOption).build());
      throw new CommonServiceException(
          "ServiceOptionInventoryType not found with given details",
          HttpStatus.NOT_FOUND,
          0x1771,
          errorMap);
    }
    return INSTANCE.toServiceInventoryDto(serviceOptionInventoryTypeEntity.get());
  }
}
