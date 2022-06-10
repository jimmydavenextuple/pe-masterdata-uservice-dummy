package com.nextuple.pe.masterdata.domain.mapper;

import com.nextuple.pe.masterdata.domain.entity.CarrierServiceEntity;
import com.nextuple.pe.masterdata.domain.inbound.CarrierServiceRequest;
import com.nextuple.pe.masterdata.domain.inbound.CarrierServiceUpdateRequest;
import com.nextuple.pe.masterdata.domain.outbound.CarrierServiceResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CarrierServiceMapper {
  CarrierServiceEntity carrierServiceRequestToCarrierServiceEntity(
      CarrierServiceRequest carrierServiceRequest);

  CarrierServiceResponse toCarrierServiceResponse(CarrierServiceEntity carrierServiceEntity);

  CarrierServiceEntity updateCarrierServiceEntity(
      CarrierServiceUpdateRequest carrierServiceUpdateRequest,
      @MappingTarget CarrierServiceEntity carrierServiceEntity);
}
