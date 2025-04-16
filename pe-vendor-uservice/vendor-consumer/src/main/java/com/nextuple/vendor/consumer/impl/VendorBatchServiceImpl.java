/*
 * Copyright (c) 2025., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.vendor.consumer.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.error.FieldError;
import com.nextuple.master.data.integration.enums.TaskInformation;
import com.nextuple.master.data.integration.inbound.BatchRequest;
import com.nextuple.master.data.integration.service.BatchService;
import com.nextuple.vendor.consumer.dto.VendorFeedDto;
import com.nextuple.vendor.consumer.mapper.VendorBatchMapper;
import com.nextuple.vendor.domain.feign.VendorFeign;
import com.nextuple.vendor.persistence.domain.VendorDomainDto;
import com.nextuple.vendor.persistence.service.VendorPersistenceService;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class VendorBatchServiceImpl extends BatchService<VendorFeedDto> {

  private final VendorFeign vendorFeign;
  private final VendorPersistenceService vendorPersistenceService;
  public static final VendorBatchMapper INSTANCE = Mappers.getMapper(VendorBatchMapper.class);
  private final TypeReference<BatchRequest<VendorFeedDto>> vendorTypeReference =
      new TypeReference<>() {};

  @Override
  public TaskInformation getTaskInformation() {
    return TaskInformation.VENDOR_FEED;
  }

  @Override
  public TypeReference<BatchRequest<VendorFeedDto>> getTypeReference() {
    return vendorTypeReference;
  }

  @Override
  public String createRecordImpl(VendorFeedDto payload) {
    return vendorFeign.createVendor(INSTANCE.toVendorRequest(payload)).getMessage();
  }

  @Override
  public String updateRecordImpl(VendorFeedDto payload) {
    return vendorFeign
        .updateVendorDetails(
            payload.getVendorId(), payload.getOrgId(), INSTANCE.toVendorUpdateRequest(payload))
        .getMessage();
  }

  @Override
  public String deleteRecordImpl(VendorFeedDto payload) {
    return vendorFeign.deleteVendor(payload.getVendorId(), payload.getOrgId()).getMessage();
  }

  @Override
  public void checkForOutdatedRecord(BatchRequest<VendorFeedDto> vendorBatchRequest)
      throws CommonServiceException {
    VendorFeedDto vendorDto = vendorBatchRequest.getPayload();
    String vendorId = vendorDto.getVendorId();
    String orgId = vendorDto.getOrgId();
    if (Objects.nonNull(vendorId) && Objects.nonNull(orgId)) {
      Optional<VendorDomainDto> vendorDomainDto =
          vendorPersistenceService.findVendorByVendorIdAndOrgId(vendorId, orgId);
      if (checkForBatchRequestExpired(vendorBatchRequest, vendorDomainDto)) {
        throwExceptionForOutdatedRecords(vendorBatchRequest, vendorDomainDto);
      }
    }
  }

  private static void throwExceptionForOutdatedRecords(
      BatchRequest<VendorFeedDto> vendorBatchRequest, Optional<VendorDomainDto> vendorDomainDto)
      throws CommonServiceException {
    if (vendorDomainDto.isPresent()) {
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(
          "receivedTimestamp",
          FieldError.builder().rejectedValue(vendorBatchRequest.getReceivedTimestamp()).build());
      errorMap.put(
          "lastUpdatedTimestamp",
          FieldError.builder().rejectedValue(vendorDomainDto.get().getLastModifiedDate()).build());
      throw new CommonServiceException(
          "Can't process the record as it's outdated", HttpStatus.BAD_REQUEST, 0x1771, errorMap);
    }
  }

  private static boolean checkForBatchRequestExpired(
      BatchRequest<VendorFeedDto> vendorBatchRequest, Optional<VendorDomainDto> vendorDomainDto) {
    return vendorDomainDto.isPresent()
        && (vendorDomainDto
            .get()
            .getLastModifiedDate()
            .after(vendorBatchRequest.getReceivedTimestamp()));
  }
}
