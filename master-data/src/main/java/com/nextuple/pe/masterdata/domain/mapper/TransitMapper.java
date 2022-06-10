package com.nextuple.pe.masterdata.domain.mapper;

import com.nextuple.pe.masterdata.domain.entity.TransitEntity;
import com.nextuple.pe.masterdata.domain.inbound.TransitDataCreationRequest;
import com.nextuple.pe.masterdata.domain.inbound.TransitDataUpdationRequest;
import com.nextuple.pe.masterdata.domain.outbound.TransitResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface TransitMapper {

  TransitEntity toTransitEntity(TransitDataCreationRequest transitDataCreationRequest);

  TransitResponse toTransitResponse(TransitEntity transitEntity);

  TransitEntity updateTransitEntity(
      TransitDataUpdationRequest transitDataUpdationRequest,
      @MappingTarget TransitEntity updatedTransitEntity);
}
