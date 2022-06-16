package com.nextuple.transit.domain.mapper;

import com.nextuple.transit.domain.entity.TransitEntity;
import com.nextuple.transit.domain.inbound.TransitDataCreationRequest;
import com.nextuple.transit.domain.inbound.TransitDataUpdationRequest;
import com.nextuple.transit.domain.outbound.TransitResponse;
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
