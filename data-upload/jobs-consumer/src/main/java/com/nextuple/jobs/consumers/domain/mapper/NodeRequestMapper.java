package com.nextuple.jobs.consumers.domain.mapper;

import com.nextuple.jobs.framework.common.domain.pojo.NodeDataUpload;
import com.nextuple.node.domain.inbound.NodeRequest;
import com.nextuple.node.domain.inbound.NodeUpdationRequest;
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
