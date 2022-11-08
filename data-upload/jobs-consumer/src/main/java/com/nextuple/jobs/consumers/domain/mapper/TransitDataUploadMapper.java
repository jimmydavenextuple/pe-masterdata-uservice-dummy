package com.nextuple.jobs.consumers.domain.mapper;

import com.nextuple.csvdownload.common.pojo.TransitDataUpload;
import com.nextuple.jobs.framework.common.domain.pojo.TransitBufferUpload;
import com.nextuple.transit.domain.inbound.TransitBufferRequest;
import com.nextuple.transit.domain.inbound.TransitDataCreationRequest;
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
