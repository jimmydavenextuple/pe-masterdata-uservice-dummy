package com.hbc.jobs.consumers.domain.mapper;

import com.hbc.csvdownload.domain.pojo.ProcessingLeadTimesRaw;
import com.hbc.jobs.framework.common.domain.pojo.NodeCarrierUpload;
import com.hbc.jobs.framework.common.domain.pojo.ProcessingTimeBufferUpload;
import com.hbc.node.carrier.domain.inbound.NodeCarrierBufferRequest;
import com.hbc.node.carrier.domain.inbound.NodeCarrierRequest;
import com.hbc.node.carrier.domain.inbound.NodeCarrierUpdateRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface NodeCarrierRequestMapper {

  NodeCarrierRequest convertToNodeCarrierRequest(ProcessingLeadTimesRaw processingLeadTimesRaw);

  NodeCarrierRequest convertToNodeCarrierRequest(NodeCarrierUpload nodeCarrierUpload);

  NodeCarrierUpdateRequest convertToNodeCarrierUpdateRequest(NodeCarrierUpload nodeCarrierUpload);

  @Mapping(target = "bufferStartDate", ignore = true)
  @Mapping(target = "bufferEndDate", ignore = true)
  NodeCarrierBufferRequest convertToNodeCarrierBufferRequest(
      ProcessingTimeBufferUpload processingTimeBufferUpload);
}
