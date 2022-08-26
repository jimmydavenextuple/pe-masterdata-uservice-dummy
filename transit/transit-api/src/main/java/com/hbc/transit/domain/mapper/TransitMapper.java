package com.hbc.transit.domain.mapper;

import com.hbc.transit.domain.entity.TransitEntity;
import com.hbc.transit.domain.inbound.TransitBufferCreationRequest;
import com.hbc.transit.domain.inbound.TransitDataCreationRequest;
import com.hbc.transit.domain.inbound.TransitDataUpdationRequest;
import com.hbc.transit.domain.outbound.TransitResponse;
import java.util.List;
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

  TransitEntity updateTransitEntity(
      TransitBufferCreationRequest transitBufferCreationRequest,
      @MappingTarget TransitEntity updatedTransitEntity);

  List<TransitResponse> toTransitResponseList(List<TransitEntity> transitEntity);
}
