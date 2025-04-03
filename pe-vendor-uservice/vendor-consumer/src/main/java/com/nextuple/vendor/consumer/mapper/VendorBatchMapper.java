package com.nextuple.vendor.consumer.mapper;

import com.nextuple.vendor.consumer.dto.VendorFeedDto;
import com.nextuple.vendor.domain.inbound.VendorRequest;
import com.nextuple.vendor.domain.inbound.VendorUpdationRequest;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface VendorBatchMapper {
  VendorRequest toVendorRequest(VendorFeedDto vendorDto);

  VendorUpdationRequest toVendorUpdateRequest(VendorFeedDto vendorDto);
}
