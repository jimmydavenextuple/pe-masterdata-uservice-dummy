package com.hbc.nodecarrier.spring.cache.mapper;

import com.hbc.node.carrier.domain.outbound.NodeCarrierResponse;
import com.hbc.nodecarrier.cache.domain.NodeCarrierDetails;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface DataMapper {

  NodeCarrierDetails convertToNodeCarrierCacheValue(NodeCarrierResponse nodeCarrierResponse);

  List<NodeCarrierDetails> convertToNodeCarrierCacheValue(
      List<NodeCarrierResponse> nodeCarrierResponse);
}
