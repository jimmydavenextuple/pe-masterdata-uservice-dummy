package com.hbc.service.inventory.domain.mapper;

import com.hbc.service.inventory.domain.entity.ServiceOptionInventoryTypeEntity;
import com.hbc.service.inventory.domain.inbound.ServiceInventoryRequest;
import com.hbc.service.inventory.domain.outbound.ServiceInventoryDto;
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
