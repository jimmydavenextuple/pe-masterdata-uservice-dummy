package com.hbc.carrier.spring.cache.mapper;

import com.hbc.carrier.cache.domain.CarrierDetails;
import com.hbc.carrier.domain.outbound.CarrierServiceResponse;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface DataMapper {

  CarrierDetails convertToCarrierCacheValue(CarrierServiceResponse carrierServiceResponse);
}
