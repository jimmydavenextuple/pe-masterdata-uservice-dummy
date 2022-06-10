package com.nextuple.pe.masterdata.domain.mapper;

import com.nextuple.pe.masterdata.domain.entity.NodeEntity;
import com.nextuple.pe.masterdata.domain.inbound.NodeRequest;
import com.nextuple.pe.masterdata.domain.inbound.NodeUpdationRequest;
import com.nextuple.pe.masterdata.domain.outbound.NodeResponse;
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
}
