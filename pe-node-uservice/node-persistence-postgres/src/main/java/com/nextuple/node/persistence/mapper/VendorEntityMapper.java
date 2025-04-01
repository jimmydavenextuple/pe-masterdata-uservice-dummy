package com.nextuple.node.persistence.mapper;

import com.nextuple.node.persistence.domain.VendorDomainDto;
import com.nextuple.node.persistence.domain.key.VendorDomainKey;
import com.nextuple.node.persistence.entity.VendorEntity;
import com.nextuple.node.persistence.entity.key.VendorKey;
import com.nextuple.postgres.mapper.DomainToEntityMapper;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    componentModel = "spring")
public interface VendorEntityMapper
    extends DomainToEntityMapper<VendorDomainDto, VendorDomainKey, VendorEntity, VendorKey> {}
