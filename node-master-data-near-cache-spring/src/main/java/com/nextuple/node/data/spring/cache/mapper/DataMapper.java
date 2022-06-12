package com.nextuple.node.data.spring.cache.mapper;

import com.nextuple.domain.node.NodeResponse;
import com.nextuple.node.data.cache.domain.NodeDataCacheValue;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface DataMapper {
  NodeDataCacheValue toNodeCacheValue(NodeResponse nodeResponse);
}
