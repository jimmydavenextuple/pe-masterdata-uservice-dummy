package com.nextuple.jobs.consumers.domain.mapper;

import com.nextuple.csvdownload.domain.pojo.ProcessingLeadTimesRaw;
import com.nextuple.jobs.framework.common.domain.pojo.NodeCarrierUpload;
import com.nextuple.jobs.framework.common.domain.pojo.ProcessingTimeBufferUpload;
import com.nextuple.node.carrier.domain.inbound.NodeCarrierBufferRequest;
import com.nextuple.node.carrier.domain.inbound.NodeCarrierRequest;
import com.nextuple.node.carrier.domain.inbound.NodeCarrierUpdateRequest;
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
