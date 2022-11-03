package com.hbc.fsa.node.mapper;

import com.hbc.fsa.node.pojo.Node;
import com.hbc.fsa.node.pojo.NodeDetails;
import com.hbc.node.domain.dto.NodeCacheKeyDto;
import com.hbc.node.domain.outbound.NodeResponse;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface NodeMapper {
  List<Node> convertToNode(List<NodeCacheKeyDto> dtos);

  List<NodeDetails> convertToNodeDetailsList(List<NodeResponse> responses);

  NodeDetails convertToNodeDetails(NodeResponse response);
}
