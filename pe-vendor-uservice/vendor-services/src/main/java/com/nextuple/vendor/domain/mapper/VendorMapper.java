package com.nextuple.vendor.domain.mapper;

import com.nextuple.vendor.domain.inbound.VendorRequest;
import com.nextuple.vendor.domain.inbound.VendorUpdationRequest;
import com.nextuple.vendor.domain.outbound.VendorResponse;
import com.nextuple.vendor.persistence.domain.VendorDomainDto;
import com.nextuple.vendor.persistence.entity.VendorEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface VendorMapper {
  VendorDomainDto vendorRequestToVendorEntity(VendorRequest vendorRequest);

  VendorResponse toVendorResponse(VendorDomainDto vendorDetails);

  VendorDomainDto updateVendorDetails(
      VendorUpdationRequest vendorUpdationRequest,
      @MappingTarget VendorDomainDto existingVendorDetails);

  VendorDomainDto toVendorDomainDto(VendorEntity vendorEntity);
}
