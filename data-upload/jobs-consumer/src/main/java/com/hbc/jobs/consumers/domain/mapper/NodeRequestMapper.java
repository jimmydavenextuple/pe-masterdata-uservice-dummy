package com.hbc.jobs.consumers.domain.mapper;

import com.hbc.jobs.framework.common.domain.pojo.NodeDataUpload;
import com.hbc.node.domain.inbound.NodeRequest;
import com.hbc.node.domain.inbound.NodeUpdationRequest;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface NodeRequestMapper {

  NodeRequest convertToNodeRequest(NodeDataUpload nodeDataUpload);

  NodeUpdationRequest convertToNodeUpdationRequest(NodeDataUpload nodeDataUpload);
}
