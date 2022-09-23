package com.hbc.jobs.consumers.domain.mapper;

import com.hbc.csvdownload.common.pojo.TransitDataUpload;
import com.hbc.transit.domain.inbound.TransitDataCreationRequest;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface TransitDataUploadMapper {
  TransitDataCreationRequest convertToTransitDataRequest(TransitDataUpload transitDataUpload);
}
