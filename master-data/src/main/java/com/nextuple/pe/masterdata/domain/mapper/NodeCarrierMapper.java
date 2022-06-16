package com.nextuple.pe.masterdata.domain.mapper;

import com.nextuple.node.carrier.domain.outbound.NodeCarrierResponse;
import com.nextuple.pe.masterdata.domain.entity.NodeCarrierEntity;
import com.nextuple.pe.masterdata.domain.inbound.NodeCarrierRequest;
import com.nextuple.pe.masterdata.domain.inbound.NodeCarrierUpdateRequest;
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
}
