/*
 * Copyright (c) 2025., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.vendor.persistence.service.impl;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.exception.PromiseEngineException;
import com.nextuple.vendor.persistence.domain.VendorDomainDto;
import com.nextuple.vendor.persistence.entity.VendorEntity;
import com.nextuple.vendor.persistence.entity.key.VendorKey;
import com.nextuple.vendor.persistence.mapper.VendorEntityMapper;
import com.nextuple.vendor.persistence.repository.VendorRepository;
import com.nextuple.vendor.persistence.util.TestUtil;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

class VendorPersistenceServiceImplTest {
  @Mock private VendorRepository vendorRepository;
  @Mock private VendorEntityMapper vendorEntityMapper;
  @InjectMocks private VendorPersistenceServiceImpl vendorPersistenceService;
  @InjectMocks private TestUtil testUtil;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    ReflectionTestUtils.setField(vendorPersistenceService, "repository", vendorRepository);
    ReflectionTestUtils.setField(vendorPersistenceService, "mapper", vendorEntityMapper);
  }

  @Test
  void saveVendorTest() throws PromiseEngineException {
    VendorEntity vendorEntity = testUtil.getVendorEntity();
    when(vendorEntityMapper.toEntity(any(VendorDomainDto.class))).thenReturn(vendorEntity);
    when(vendorEntityMapper.toDomain(any(VendorEntity.class)))
        .thenReturn(testUtil.getVendorDomainDto());
    when(vendorRepository.save(any())).thenReturn(vendorEntity);
    VendorDomainDto vendor =
        vendorPersistenceService.saveVendorDetails(testUtil.getVendorDomainDto());
    Assertions.assertEquals(vendorEntity.getVendorId(), vendor.getVendorId());
    verify(vendorRepository, times(1)).save(any());
  }

  @Test
  void saveVendorExceptionTest() {
    when(vendorRepository.save(any())).thenThrow(new RuntimeException("Error while saving"));
    Exception exception =
        assertThrows(
            PromiseEngineException.class,
            () -> vendorPersistenceService.saveVendorDetails(testUtil.getVendorDomainDto()));
    Assertions.assertEquals("Unable to save vendor : Error while saving", exception.getMessage());
    verify(vendorRepository, times(1)).save(any());
  }

  @Test
  void getVendorDetailsTest() throws PromiseEngineException {
    VendorEntity vendorEntity = testUtil.getVendorEntity();
    when(vendorEntityMapper.toEntity(any(VendorDomainDto.class))).thenReturn(vendorEntity);
    when(vendorEntityMapper.toDomain(any(VendorEntity.class)))
        .thenReturn(testUtil.getVendorDomainDto());
    when(vendorRepository.findById(any())).thenReturn(Optional.of(vendorEntity));
    Optional<VendorDomainDto> optionalVendorDomainDto =
        vendorPersistenceService.findVendorByVendorIdAndOrgId(TestUtil.VENDOR_ID, TestUtil.ORG_ID);
    Assertions.assertEquals(
        vendorEntity.getVendorId(), optionalVendorDomainDto.get().getVendorId());
    verify(vendorRepository, times(1)).findById(any());
  }

  @Test
  void getVendorDetailsTestException() {
    when(vendorRepository.findById(any()))
        .thenThrow(new RuntimeException("Error while fetching details"));
    Exception exception =
        assertThrows(
            PromiseEngineException.class,
            () ->
                vendorPersistenceService.findVendorByVendorIdAndOrgId(
                    TestUtil.VENDOR_ID, TestUtil.ORG_ID));
    Assertions.assertEquals(
        "Unable to fetch vendor : Error while fetching details", exception.getMessage());
    verify(vendorRepository, times(1)).findById(any());
  }

  @Test
  void vendorDeletionTest() throws CommonServiceException {
    when(vendorEntityMapper.toEntity(any(VendorDomainDto.class)))
        .thenReturn(testUtil.getVendorEntity());
    when(vendorEntityMapper.toDomain(any(VendorEntity.class)))
        .thenReturn(testUtil.getVendorDomainDto());
    doNothing().when(vendorRepository).delete(any());
    when(vendorRepository.findById(any(VendorKey.class)))
        .thenReturn(Optional.of(testUtil.getVendorEntity()));
    vendorPersistenceService.deleteVendor(testUtil.getVendorDomainDto());
    verify(vendorRepository, times(1)).delete(any());
  }

  @Test
  void vendorDeletionTestException() {
    doThrow(new RuntimeException("error while deleting")).when(vendorRepository).delete(any());
    Exception exception =
        assertThrows(
            CommonServiceException.class,
            () -> vendorPersistenceService.deleteVendor(testUtil.getVendorDomainDto()));
    Assertions.assertEquals("Vendor not found for given orgId, vendorId", exception.getMessage());
    verify(vendorRepository, times(0)).delete(any());
  }
}
