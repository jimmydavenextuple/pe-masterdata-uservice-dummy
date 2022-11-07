package com.hbc.jobs.consumers.domain.mapper;

import com.hbc.carrier.domain.inbound.CarrierServiceRequest;
import com.hbc.carrier.domain.inbound.CarrierServiceUpdateRequest;
import com.hbc.jobs.framework.common.domain.pojo.CarrierServiceUpload;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CarrierServiceRequestMapper {
  CarrierServiceRequest convertToCarrierServiceRequest(CarrierServiceUpload carrierServiceUpload);

  CarrierServiceUpdateRequest convertToCarrierServiceUpdateRequest(
      CarrierServiceUpload carrierServiceUpload);
}
