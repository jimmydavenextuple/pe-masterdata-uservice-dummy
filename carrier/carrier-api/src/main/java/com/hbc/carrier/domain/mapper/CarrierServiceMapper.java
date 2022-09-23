package com.hbc.carrier.domain.mapper;

import com.hbc.carrier.domain.dto.CarrierCacheKeyDto;
import com.hbc.carrier.domain.entity.CarrierServiceEntity;
import com.hbc.carrier.domain.inbound.CarrierServiceRequest;
import com.hbc.carrier.domain.inbound.CarrierServiceUpdateRequest;
import com.hbc.carrier.domain.outbound.CarrierServiceResponse;
import java.util.List;
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

  List<CarrierServiceResponse> toCarrierServiceResponseList(List<CarrierServiceEntity> carrierServiceEntity);

  CarrierServiceEntity updateCarrierServiceEntity(
      CarrierServiceUpdateRequest carrierServiceUpdateRequest,
      @MappingTarget CarrierServiceEntity carrierServiceEntity);

  List<CarrierCacheKeyDto> toCarrierCacheKeyList(List<CarrierServiceEntity> carrierServiceEntities);
}
