package com.hbc.jobs.consumers.domain.mapper;

import com.hbc.csvdownload.common.pojo.ProcessingLeadTime;
import com.hbc.node.carrier.domain.inbound.NodeCarrierRequest;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface NodeCarrierRequestMapper {

  NodeCarrierRequest convertToNodeCarrierRequest(ProcessingLeadTime processingLeadTime);
}
