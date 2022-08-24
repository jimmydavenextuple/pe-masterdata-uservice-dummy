package com.hbc.node.carrier.domain.mapper;

import com.hbc.node.carrier.domain.entity.NodeCarrierEntity;
import com.hbc.node.carrier.domain.inbound.NodeCarrierRequest;
import com.hbc.node.carrier.domain.inbound.NodeCarrierUpdateRequest;
import com.hbc.node.carrier.domain.outbound.NodeCarrierResponse;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface NodeCarrierMapper {

  NodeCarrierEntity nodeCarrierRequestToEntity(NodeCarrierRequest nodeCarrierRequest);

  NodeCarrierResponse toNodeCarrierDto(NodeCarrierEntity nodeCarrierEntity);

  NodeCarrierEntity updateNodeCarrierEntity(
      NodeCarrierUpdateRequest nodeCarrierUpdateRequest,
      @MappingTarget NodeCarrierEntity nodeCarrierEntity);

  List<NodeCarrierResponse> toNodeCarrierResponseList(
      List<NodeCarrierEntity> nodeCarrierEntityList);
}
