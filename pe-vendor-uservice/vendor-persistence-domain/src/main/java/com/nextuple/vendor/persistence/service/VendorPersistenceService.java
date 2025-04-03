package com.nextuple.vendor.persistence.service;

import com.nextuple.common.service.DomainPersistenceService;
import com.nextuple.vendor.persistence.domain.VendorDomainDto;
import com.nextuple.vendor.persistence.domain.key.VendorDomainKey;
import com.nextuple.vendor.persistence.exception.VendorDomainException;
import java.util.Optional;

public interface VendorPersistenceService
    extends DomainPersistenceService<VendorDomainDto, VendorDomainKey> {
  VendorDomainDto saveVendorDetails(VendorDomainDto vendorDomainDto) throws VendorDomainException;

  Optional<VendorDomainDto> findVendorByVendorIdAndOrgId(String vendorId, String orgId)
      throws VendorDomainException;

  void deleteVendor(VendorDomainDto vendorDomainDto) throws VendorDomainException;
}
