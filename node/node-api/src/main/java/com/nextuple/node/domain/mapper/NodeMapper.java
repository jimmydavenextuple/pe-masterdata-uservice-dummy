package com.nextuple.node.domain.mapper;

import com.nextuple.node.domain.dto.NodeCacheKeyDto;
import com.nextuple.node.domain.dto.NodeDto;
import com.nextuple.node.domain.entity.NodeEntity;
import com.nextuple.node.domain.inbound.NodeRequest;
import com.nextuple.node.domain.inbound.NodeUpdationRequest;
import com.nextuple.node.domain.outbound.NodeResponse;
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
}
