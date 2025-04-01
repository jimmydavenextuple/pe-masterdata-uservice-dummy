package com.nextuple.node.domain.mapper;

import com.nextuple.node.domain.inbound.VendorRequest;
import com.nextuple.node.domain.inbound.VendorUpdationRequest;
import com.nextuple.node.domain.outbound.VendorResponse;
import com.nextuple.node.persistence.domain.VendorDomainDto;
import com.nextuple.node.persistence.entity.VendorEntity;
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
