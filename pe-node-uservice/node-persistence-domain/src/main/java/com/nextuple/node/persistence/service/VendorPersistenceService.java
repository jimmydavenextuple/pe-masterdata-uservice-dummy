package com.nextuple.node.persistence.service;

import com.nextuple.common.service.DomainPersistenceService;
import com.nextuple.node.persistence.domain.VendorDomainDto;
import com.nextuple.node.persistence.domain.key.VendorDomainKey;
import com.nextuple.node.persistence.exception.VendorDomainException;
import java.util.Optional;

public interface VendorPersistenceService
    extends DomainPersistenceService<VendorDomainDto, VendorDomainKey> {
  VendorDomainDto saveVendorDetails(VendorDomainDto vendorDomainDto) throws VendorDomainException;

  Optional<VendorDomainDto> findVendorByVendorIdAndOrgId(String vendorId, String orgId)
      throws VendorDomainException;

  void deleteVendor(VendorDomainDto vendorDomainDto) throws VendorDomainException;
}
