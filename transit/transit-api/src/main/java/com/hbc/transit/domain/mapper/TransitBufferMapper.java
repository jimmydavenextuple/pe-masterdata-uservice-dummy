package com.hbc.transit.domain.mapper;

import com.hbc.transit.domain.entity.TransitBufferEntity;
import com.hbc.transit.domain.inbound.TransitBufferRequest;
import com.hbc.transit.domain.outbound.TransitBufferResponse;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface TransitBufferMapper {
  TransitBufferEntity toTransitBufferEntity(TransitBufferRequest transitBufferRequest);

  TransitBufferResponse toTransitBufferResponse(TransitBufferEntity transitBufferEntity);

  List<TransitBufferResponse> toTransitBufferResponseList(
      List<TransitBufferEntity> transitBufferEntities);
}
