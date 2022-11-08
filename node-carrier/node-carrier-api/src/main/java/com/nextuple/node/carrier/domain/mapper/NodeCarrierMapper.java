package com.nextuple.node.carrier.domain.mapper;

import com.nextuple.node.carrier.domain.dto.NodeCarrierListCacheKeyDto;
import com.nextuple.node.carrier.domain.entity.NodeCarrierEntity;
import com.nextuple.node.carrier.domain.entity.NodeCarrierSelectionEntity;
import com.nextuple.node.carrier.domain.inbound.NodeCarrierBufferRequest;
import com.nextuple.node.carrier.domain.inbound.NodeCarrierRequest;
import com.nextuple.node.carrier.domain.inbound.NodeCarrierSelectionRequest;
import com.nextuple.node.carrier.domain.inbound.NodeCarrierUpdateRequest;
import com.nextuple.node.carrier.domain.outbound.NodeCarrierResponse;
import com.nextuple.node.carrier.domain.outbound.NodeCarrierSelectionResponse;
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

  NodeCarrierEntity nodeCarrierBufferRequestToEntity(
      NodeCarrierBufferRequest nodeCarrierBufferRequest);

  NodeCarrierResponse toNodeCarrierDto(NodeCarrierEntity nodeCarrierEntity);

  NodeCarrierEntity updateNodeCarrierEntity(
      NodeCarrierUpdateRequest nodeCarrierUpdateRequest,
      @MappingTarget NodeCarrierEntity nodeCarrierEntity);

  NodeCarrierEntity updateNodeCarrierEntityWithBuffer(
      NodeCarrierBufferRequest nodeCarrierBufferRequest,
      @MappingTarget NodeCarrierEntity nodeCarrierEntity);

  List<NodeCarrierResponse> toNodeCarrierResponseList(
      List<NodeCarrierEntity> nodeCarrierEntityList);

  NodeCarrierSelectionEntity nodeCarrierSelectionRequestToEntity(
      NodeCarrierSelectionRequest nodeCarrierSelectionRequest);

  NodeCarrierSelectionResponse toNodeCarrierSelectionResponse(
      NodeCarrierSelectionEntity saveNodeCarrierSelectionEntity);

  List<NodeCarrierSelectionResponse> toNodeCarrierSelectionResponseList(
      List<NodeCarrierSelectionEntity> nodeCarrierByOrgIdAndServiceOptionAndDestinationGeoZone);

  List<NodeCarrierListCacheKeyDto> toNodeCarrierListCacheKeyDto(
      List<NodeCarrierEntity> nodeCarrierEntityList);
}
