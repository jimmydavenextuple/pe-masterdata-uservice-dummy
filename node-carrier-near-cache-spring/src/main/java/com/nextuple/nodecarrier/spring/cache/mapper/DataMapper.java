package com.nextuple.nodecarrier.spring.cache.mapper;

import com.nextuple.nodecarrier.cache.domain.NodeCarrierDetails;
import com.nextuple.nodecarrier.cache.domain.NodeCarrierResponse;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface DataMapper {

  NodeCarrierDetails convertToNodeCarrierCacheValue(NodeCarrierResponse nodeCarrierResponse);
}
