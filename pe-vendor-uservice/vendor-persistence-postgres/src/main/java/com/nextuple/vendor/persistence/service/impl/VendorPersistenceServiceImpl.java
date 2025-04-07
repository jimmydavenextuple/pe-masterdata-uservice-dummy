/*
 * Copyright (c) 2025., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.vendor.persistence.service.impl;

import static com.nextuple.common.constants.CommonConstants.DEFAULT_SORT_ORDER;

import com.nextuple.common.context.Logger;
import com.nextuple.common.context.LoggerFactory;
import com.nextuple.postgres.service.CommonPersistenceService;
import com.nextuple.vendor.persistence.domain.VendorDomainDto;
import com.nextuple.vendor.persistence.domain.key.VendorDomainKey;
import com.nextuple.vendor.persistence.entity.VendorEntity;
import com.nextuple.vendor.persistence.entity.key.VendorKey;
import com.nextuple.vendor.persistence.exception.VendorDomainException;
import com.nextuple.vendor.persistence.mapper.VendorEntityMapper;
import com.nextuple.vendor.persistence.repository.VendorRepository;
import com.nextuple.vendor.persistence.service.VendorPersistenceService;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
  private static final Logger logger = LoggerFactory.getLogger(VendorPersistenceServiceImpl.class);

  @Override
  public VendorDomainDto saveVendorDetails(VendorDomainDto vendorDomainDto)
      throws VendorDomainException {
    try {
      return save(vendorDomainDto);
    } catch (Exception e) {
      logger.error(String.valueOf(e), "Unable to save vendor");
      throw new VendorDomainException(
          "Error while saving the vendor",
          vendorDomainDto.getVendorId(),
          vendorDomainDto.getOrgId());
    }
  }

  @Override
  public Optional<VendorDomainDto> findVendorByVendorIdAndOrgId(String vendorId, String orgId)
      throws VendorDomainException {
    try {
      return findByKey(VendorDomainKey.builder().orgId(orgId).vendorId(vendorId).build());
    } catch (Exception e) {
      logger.error(String.valueOf(e), "Unable to find vendor");
      throw new VendorDomainException("Error while finding vendor", vendorId, orgId);
    }
  }

  @Override
  public void deleteVendor(VendorDomainDto vendorDomainDto) throws VendorDomainException {
    try {
      delete(vendorDomainDto);
    } catch (Exception e) {
      logger.error(String.valueOf(e), "Unable to delete vendor");
      throw new VendorDomainException(
          "Error while deleting vendor", vendorDomainDto.getVendorId(), vendorDomainDto.getOrgId());
    }
  }

  @Override
  public Page<VendorDomainDto> getVendorByOrgId(
      String orgId, Integer pageNo, Integer pageSize, String sortBy, String sortOrder)
      throws VendorDomainException {
    try {
      Pageable pageable;
      if (sortOrder.equalsIgnoreCase(DEFAULT_SORT_ORDER)) {
        pageable = PageRequest.of(pageNo - 1, pageSize, Sort.by(sortBy).ascending());
      } else {
        pageable = PageRequest.of(pageNo - 1, pageSize, Sort.by(sortBy).descending());
      }
      return getRepository().findVendorByOrgId(orgId, pageable).map(getMapper()::toDomain);
    } catch (Exception e) {
      logger.error(String.valueOf(e), "Unable to find vendor list");
      throw new VendorDomainException("Error while finding vendor list", null, orgId);
    }
  }
}
