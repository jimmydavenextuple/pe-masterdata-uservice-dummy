package com.nextuple.vendor.persistence.mapper;

import com.nextuple.postgres.mapper.DomainToEntityMapper;
import com.nextuple.vendor.persistence.domain.VendorDomainDto;
import com.nextuple.vendor.persistence.domain.key.VendorDomainKey;
import com.nextuple.vendor.persistence.entity.VendorEntity;
import com.nextuple.vendor.persistence.entity.key.VendorKey;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    componentModel = "spring")
public interface VendorEntityMapper
    extends DomainToEntityMapper<VendorDomainDto, VendorDomainKey, VendorEntity, VendorKey> {}
