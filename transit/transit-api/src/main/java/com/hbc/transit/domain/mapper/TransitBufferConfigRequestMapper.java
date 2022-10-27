package com.hbc.transit.domain.mapper;

import com.hbc.transit.domain.entity.TransitBufferConfigRequestEntity;
import com.hbc.transit.domain.inbound.TransitBufferConfigRequest;
import com.hbc.transit.domain.outbound.TransitBufferConfigResponse;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface TransitBufferConfigRequestMapper {

  TransitBufferConfigRequestEntity toTransitBufferConfigEntity(
      TransitBufferConfigRequest transitBufferConfigRequest);

  TransitBufferConfigResponse toTransitBufferConfigResponse(
      TransitBufferConfigRequestEntity transitBufferConfigRequestEntity);

  List<TransitBufferConfigResponse> toTransitBufferConfigResponseList(
      List<TransitBufferConfigRequestEntity> transitBufferConfigRequestEntities);

  TransitBufferConfigResponse toTransitBufferConfigResponse(
      TransitBufferConfigRequestEntity transitBufferConfigRequestEntity, String fileName);
}
