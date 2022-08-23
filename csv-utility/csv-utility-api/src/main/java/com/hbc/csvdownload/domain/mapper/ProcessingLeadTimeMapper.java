package com.hbc.csvdownload.domain.mapper;

import com.hbc.csvdownload.domain.pojo.ProcessingLeadTimesRaw;
import com.hbc.node.carrier.domain.inbound.NodeCarrierRequest;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ProcessingLeadTimeMapper {

  NodeCarrierRequest convertToNodeCarrierRequest(ProcessingLeadTimesRaw processingLeadTimesRaw);
}
