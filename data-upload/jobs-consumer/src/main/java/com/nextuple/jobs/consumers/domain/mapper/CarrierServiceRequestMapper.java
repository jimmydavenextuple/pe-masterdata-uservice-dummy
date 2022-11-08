package com.nextuple.jobs.consumers.domain.mapper;

import com.nextuple.carrier.domain.inbound.CarrierServiceRequest;
import com.nextuple.carrier.domain.inbound.CarrierServiceUpdateRequest;
import com.nextuple.jobs.framework.common.domain.pojo.CarrierServiceUpload;
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
