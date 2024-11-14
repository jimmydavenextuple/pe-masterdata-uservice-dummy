package com.nextuple.sourcing.cost.config.domain.mapper;

import com.nextuple.sourcing.cost.config.domain.entity.TenantCostTypeEntity;
import com.nextuple.sourcing.cost.config.dto.TenantCostTypeCacheKeyDto;
import com.nextuple.sourcing.cost.config.inbound.TenantCostTypeRequest;
import com.nextuple.sourcing.cost.config.inbound.TenantCostTypeUpdateRequest;
import com.nextuple.sourcing.cost.config.outbound.TenantCostTypeResponse;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface TenantCostTypeMapper {
  TenantCostTypeEntity toTenantCostTypeEntity(TenantCostTypeRequest tenantCostTypeRequest);

  TenantCostTypeResponse toTenantCostTypeResponse(TenantCostTypeEntity tenantCostTypeEntity);

  List<TenantCostTypeResponse> toTenantCostTypeListResponse(
      List<TenantCostTypeEntity> tenantCostTypeEntityList);

  void updateTenantCostTypeToEntity(
      TenantCostTypeUpdateRequest tenantCostTypeUpdateRequest,
      @MappingTarget TenantCostTypeEntity tenantCostTypeEntity);

  List<TenantCostTypeCacheKeyDto> toTenantCostTypeCacheKeyResponseList(
      List<TenantCostTypeEntity> tenantCostTypeEntities);
}
