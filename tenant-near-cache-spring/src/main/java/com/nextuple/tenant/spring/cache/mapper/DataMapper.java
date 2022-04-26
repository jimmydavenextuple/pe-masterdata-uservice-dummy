package com.nextuple.tenant.spring.cache.mapper;

import com.nextuple.core.tenant.domain.TenantDto;
import com.nextuple.tenant.cache.domain.TenantDetails;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface DataMapper {

  TenantDetails toTenantCacheValue(TenantDto tenantDto);
}
