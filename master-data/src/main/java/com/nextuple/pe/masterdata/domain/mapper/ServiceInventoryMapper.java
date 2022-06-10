package com.nextuple.pe.masterdata.domain.mapper;

import com.nextuple.pe.masterdata.domain.entity.ServiceOptionInventoryTypeEntity;
import com.nextuple.pe.masterdata.domain.inbound.ServiceInventoryRequest;
import com.nextuple.pe.masterdata.domain.outbound.ServiceInventoryDto;
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
