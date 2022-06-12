package com.nextuple.carrier.spring.cache.mapper;

import com.nextuple.carrier.cache.domain.CarrierDetails;
import com.nextuple.domain.carrier.CarrierServiceResponse;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface DataMapper {

  CarrierDetails convertToCarrierCacheValue(CarrierServiceResponse carrierServiceResponse);
}
