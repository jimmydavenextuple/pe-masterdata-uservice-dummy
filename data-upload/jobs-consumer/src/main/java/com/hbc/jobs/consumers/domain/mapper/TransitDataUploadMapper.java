package com.hbc.jobs.consumers.domain.mapper;

import com.hbc.csvdownload.common.pojo.TransitDataUpload;
import com.hbc.jobs.framework.common.domain.pojo.TransitBufferUpload;
import com.hbc.transit.domain.inbound.TransitBufferRequest;
import com.hbc.transit.domain.inbound.TransitDataCreationRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface TransitDataUploadMapper {
  TransitDataCreationRequest convertToTransitDataRequest(TransitDataUpload transitDataUpload);

  @Mapping(target = "bufferStartDate", ignore = true)
  @Mapping(target = "bufferEndDate", ignore = true)
  TransitBufferRequest convertToTransitBufferRequest(TransitBufferUpload transitBufferUpload);
}
