/*
 * Copyright (c) 2025., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.vendor.persistence.service.impl;

import com.nextuple.postgres.service.CommonPersistenceService;
import com.nextuple.vendor.persistence.domain.VendorDomainDto;
import com.nextuple.vendor.persistence.domain.key.VendorDomainKey;
import com.nextuple.vendor.persistence.entity.VendorEntity;
import com.nextuple.vendor.persistence.entity.key.VendorKey;
import com.nextuple.vendor.persistence.mapper.VendorEntityMapper;
import com.nextuple.vendor.persistence.repository.VendorRepository;
import com.nextuple.vendor.persistence.service.VendorPersistenceService;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VendorPersistenceServiceImpl
    extends CommonPersistenceService<
        VendorDomainDto,
        VendorDomainKey,
        VendorEntity,
        VendorKey,
        VendorRepository,
        VendorEntityMapper>
    implements VendorPersistenceService {
  @Override
  public VendorDomainDto saveVendorDetails(VendorDomainDto vendorDomainDto) {
    return save(vendorDomainDto);
  }

  @Override
  public Optional<VendorDomainDto> findVendorByVendorIdAndOrgId(String vendorId, String orgId) {
    return findByKey(VendorDomainKey.builder().orgId(orgId).vendorId(vendorId).build());
  }

  @Override
  public void deleteVendor(VendorDomainDto vendorDomainDto) {
    delete(vendorDomainDto);
  }
}
