package com.nextuple.service.inventory.domain.mapper;

import com.nextuple.service.inventory.domain.entity.ServiceOptionInventoryTypeEntity;
import com.nextuple.service.inventory.domain.inbound.ServiceInventoryRequest;
import com.nextuple.service.inventory.domain.outbound.ServiceInventoryDto;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ServiceInventoryMapper {
  ServiceOptionInventoryTypeEntity serviceInventoryRequestToServiceOptionInventoryTypeEntity(
      ServiceInventoryRequest serviceInventoryRequest);

  ServiceInventoryDto toServiceInventoryDto(
      ServiceOptionInventoryTypeEntity serviceOptionInventoryTypeEntity);
}
