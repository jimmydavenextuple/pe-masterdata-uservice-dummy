package com.nextuple.nodde.spring.cache.mapper;

import com.nextuple.core.node.outbound.NodeValidationResponse;
import com.nextuple.node.cache.domain.NodeDetails;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface DataMapper {
  NodeDetails toNodeCacheValue(NodeValidationResponse nodeValidationResponse);
}
