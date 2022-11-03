package com.hbc.transit.domain.mapper;

import com.hbc.transit.domain.entity.TransitEntity;
import com.hbc.transit.domain.inbound.TransitDataCreationRequest;
import com.hbc.transit.domain.inbound.TransitDataUpdationRequest;
import com.hbc.transit.domain.outbound.TransitResponse;
import com.hbc.transit.domain.pojo.ProjectedTransitEntity;
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

  List<TransitResponse> toTransitResponseList(List<TransitEntity> transitEntity);

  TransitResponse convertToTransitResponse(ProjectedTransitEntity projectedTransitEntity);

  List<TransitResponse> convertToTransitResponseList(
      List<ProjectedTransitEntity> projectedTransitEntities);
}
