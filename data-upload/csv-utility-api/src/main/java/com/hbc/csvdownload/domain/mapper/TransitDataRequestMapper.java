package com.hbc.csvdownload.domain.mapper;

import com.hbc.csvdownload.domain.pojo.TransitDataErrorLogsPojo;
import com.hbc.transit.domain.inbound.TransitDataCreationRequest;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface TransitDataRequestMapper {

  TransitDataErrorLogsPojo convertToTransitDataErrorLogsPojo(
      TransitDataCreationRequest transitDataCreationRequest);
}
