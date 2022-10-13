package com.hbc.node.domain.mapper;

import com.hbc.node.domain.dto.NodeCacheKeyDto;
import com.hbc.node.domain.dto.NodeDto;
import com.hbc.node.domain.entity.NodeEntity;
import com.hbc.node.domain.inbound.NodeRequest;
import com.hbc.node.domain.inbound.NodeUpdationRequest;
import com.hbc.node.domain.outbound.NodeResponse;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface NodeMapper {

  NodeEntity nodeRequestToNodeEntity(NodeRequest nodeRequest);

  NodeResponse toNodeResponse(NodeEntity nodeEntity);

  NodeEntity updateNodeEntity(
      NodeUpdationRequest nodeUpdationRequest, @MappingTarget NodeEntity existingNodeEntity);

  NodeDto toNodeDto(NodeEntity nodeEntity);

  List<NodeCacheKeyDto> toNodeCacheKeyResponseList(List<NodeEntity> nodeEntities);

  List<NodeResponse> toNodeResponseList(List<NodeEntity> nodeEntities);
}
