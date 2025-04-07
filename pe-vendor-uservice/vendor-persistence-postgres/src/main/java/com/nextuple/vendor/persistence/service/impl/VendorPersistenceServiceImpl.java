/*
 * Copyright (c) 2025., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.vendor.persistence.service.impl;

import com.nextuple.common.context.Logger;
import com.nextuple.common.context.LoggerFactory;
import com.nextuple.common.enums.ApplicationLayer;
import com.nextuple.common.enums.ExceptionCodeMapping;
import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.exception.PromiseEngineException;
import com.nextuple.common.response.error.FieldError;
import com.nextuple.postgres.service.CommonPersistenceService;
import com.nextuple.vendor.persistence.domain.VendorDomainDto;
import com.nextuple.vendor.persistence.domain.key.VendorDomainKey;
import com.nextuple.vendor.persistence.entity.VendorEntity;
import com.nextuple.vendor.persistence.entity.key.VendorKey;
import com.nextuple.vendor.persistence.mapper.VendorEntityMapper;
import com.nextuple.vendor.persistence.repository.VendorRepository;
import com.nextuple.vendor.persistence.service.VendorPersistenceService;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
      throws PromiseEngineException {
    try {
      return save(vendorDomainDto);
    } catch (Exception e) {
      logger.error(String.valueOf(e), "Unable to save vendor");
      throw new PromiseEngineException(
          ApplicationLayer.DAO_LAYER,
          ExceptionCodeMapping.DAO_SAVE_FAILED,
          "Unable to save vendor : " + e.getMessage());
    }
  }

  @Override
  public Optional<VendorDomainDto> findVendorByVendorIdAndOrgId(String vendorId, String orgId)
      throws PromiseEngineException {
    try {
      return findByKey(VendorDomainKey.builder().orgId(orgId).vendorId(vendorId).build());
    } catch (Exception e) {
      throw new PromiseEngineException(
          ApplicationLayer.DAO_LAYER,
          ExceptionCodeMapping.DAO_FIND_FAILED,
          "Unable to fetch vendor : " + e.getMessage());
    }
  }

  @Override
  public void deleteVendor(VendorDomainDto vendorDomainDto) throws CommonServiceException {
    Optional<VendorEntity> vendorEntity =
        getRepository()
            .findById(
                VendorKey.builder()
                    .vendorId(vendorDomainDto.getVendorId())
                    .orgId(vendorDomainDto.getOrgId())
                    .build());
    if (vendorEntity.isEmpty()) {
      logger.error("Unable to find vendor");
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put("orgId", FieldError.builder().rejectedValue(vendorDomainDto.getOrgId()).build());
      errorMap.put(
          "vendorId", FieldError.builder().rejectedValue(vendorDomainDto.getVendorId()).build());
      throw new CommonServiceException(
          "Vendor not found for given orgId, vendorId", HttpStatus.NOT_FOUND, 0X2771, errorMap);
    }
    delete(vendorDomainDto);
  }
}
