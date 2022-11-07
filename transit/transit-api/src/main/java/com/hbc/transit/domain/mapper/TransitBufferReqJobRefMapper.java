package com.hbc.transit.domain.mapper;

import com.hbc.transit.domain.entity.TransitBufferReqJobRefEntity;
import com.hbc.transit.domain.inbound.TransitBufferReqJobRefRequest;
import com.hbc.transit.domain.outbound.TransitBufferReqJobRefResponse;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface TransitBufferReqJobRefMapper {

  TransitBufferReqJobRefResponse toTransitBufferReqJobRefDto(
      TransitBufferReqJobRefEntity transitBufferReqJobRefEntity);

  TransitBufferReqJobRefEntity transitBufferReqJobRefDtoToEntity(
      TransitBufferReqJobRefRequest transitBufferReqJobRefRequest);
}
