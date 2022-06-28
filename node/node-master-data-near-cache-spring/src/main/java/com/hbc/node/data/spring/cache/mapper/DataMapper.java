package com.hbc.node.data.spring.cache.mapper;

import com.hbc.node.data.cache.domain.NodeDataCacheValue;
import com.hbc.node.domain.outbound.NodeResponse;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface DataMapper {
  NodeDataCacheValue toNodeCacheValue(NodeResponse nodeResponse);
}
