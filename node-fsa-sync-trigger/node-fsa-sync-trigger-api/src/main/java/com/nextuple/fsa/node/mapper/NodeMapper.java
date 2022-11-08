package com.nextuple.fsa.node.mapper;

import com.nextuple.fsa.node.pojo.Node;
import com.nextuple.fsa.node.pojo.NodeDetails;
import com.nextuple.node.domain.dto.NodeCacheKeyDto;
import com.nextuple.node.domain.outbound.NodeResponse;
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
