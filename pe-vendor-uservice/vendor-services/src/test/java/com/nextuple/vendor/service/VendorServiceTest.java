/*
 * Copyright (c) 2025., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.vendor.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.exception.PromiseEngineException;
import com.nextuple.vendor.TestUtil;
import com.nextuple.vendor.domain.inbound.VendorRequest;
import com.nextuple.vendor.domain.inbound.VendorUpdationRequest;
import com.nextuple.vendor.domain.outbound.VendorResponse;
import com.nextuple.vendor.persistence.domain.VendorDomainDto;
import com.nextuple.vendor.persistence.service.VendorPersistenceService;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class VendorServiceTest {
  @InjectMocks private VendorService vendorService;
  @InjectMocks private TestUtil testUtil;
  @Mock private VendorPersistenceService vendorPersistenceService;

  @Test
  @DisplayName("Create Vendor Test - happy path")
  void createVendorTest() throws PromiseEngineException {
    VendorDomainDto vendorDomainDto = testUtil.getVendorDomainDto(TestUtil.VENDOR_ID);
    VendorRequest vendorRequest = testUtil.getVendorRequest();
    when(vendorPersistenceService.saveVendorDetails(any(VendorDomainDto.class)))
        .thenReturn(vendorDomainDto);
    VendorResponse receivedDto = vendorService.createVendor(testUtil.getVendorRequest());
    Assertions.assertEquals(vendorRequest.getVendorId(), receivedDto.getVendorId());
    verify(vendorPersistenceService, times(1)).saveVendorDetails(any(VendorDomainDto.class));
  }

  @Test
  @DisplayName("Update Vendor Test - happy path")
  void updateVendorDetailsSuccess() throws CommonServiceException, PromiseEngineException {
    VendorDomainDto vendorDomainDto = testUtil.getVendorDomainDto(TestUtil.VENDOR_ID);
    VendorUpdationRequest vendorUpdationRequest = testUtil.getVendorUpdationRequest();
    when(vendorPersistenceService.findVendorByVendorIdAndOrgId(TestUtil.VENDOR_ID, TestUtil.ORG_ID))
        .thenReturn(Optional.of(vendorDomainDto));
    when(vendorPersistenceService.saveVendorDetails(any(VendorDomainDto.class)))
        .thenReturn(vendorDomainDto);

    VendorResponse response =
        vendorService.updateVendorDetails(
            TestUtil.VENDOR_ID, TestUtil.ORG_ID, vendorUpdationRequest);

    Assertions.assertNotNull(response);
    Assertions.assertEquals(TestUtil.VENDOR_ID, response.getVendorId());
    verify(vendorPersistenceService, times(1))
        .findVendorByVendorIdAndOrgId(TestUtil.VENDOR_ID, TestUtil.ORG_ID);
    verify(vendorPersistenceService, times(1)).saveVendorDetails(any(VendorDomainDto.class));
  }

  @Test
  @DisplayName("Update Vendor Test - vendor not found")
  void updateVendorDetailsVendorNotFound() throws CommonServiceException, PromiseEngineException {
    VendorUpdationRequest vendorUpdationRequest = testUtil.getVendorUpdationRequest();
    when(vendorPersistenceService.findVendorByVendorIdAndOrgId(TestUtil.VENDOR_ID, TestUtil.ORG_ID))
        .thenReturn(Optional.empty());

    CommonServiceException exception =
        Assertions.assertThrows(
            CommonServiceException.class,
            () -> {
              vendorService.updateVendorDetails(
                  TestUtil.VENDOR_ID, TestUtil.ORG_ID, vendorUpdationRequest);
            });

    Assertions.assertEquals("Vendor not found with given details", exception.getMessage());
    verify(vendorPersistenceService, times(1))
        .findVendorByVendorIdAndOrgId(TestUtil.VENDOR_ID, TestUtil.ORG_ID);
  }

  @Test
  @DisplayName("Get Vendor Details Test - happy path")
  void getVendorDetailsSuccess() throws CommonServiceException, PromiseEngineException {
    VendorDomainDto vendorDomainDto = testUtil.getVendorDomainDto(TestUtil.VENDOR_ID);
    when(vendorPersistenceService.findVendorByVendorIdAndOrgId(anyString(), anyString()))
        .thenReturn(Optional.of(vendorDomainDto));

    VendorResponse response = vendorService.getVendorDetails(TestUtil.VENDOR_ID, TestUtil.ORG_ID);

    Assertions.assertNotNull(response);
    Assertions.assertEquals(TestUtil.VENDOR_ID, response.getVendorId());
    verify(vendorPersistenceService, times(1))
        .findVendorByVendorIdAndOrgId(TestUtil.VENDOR_ID, TestUtil.ORG_ID);
  }

  @Test
  @DisplayName("Get Vendor Details Test - vendor not found")
  void getVendorDetailsVendorNotFound() throws CommonServiceException, PromiseEngineException {
    when(vendorPersistenceService.findVendorByVendorIdAndOrgId(anyString(), anyString()))
        .thenReturn(Optional.empty());

    CommonServiceException exception =
        Assertions.assertThrows(
            CommonServiceException.class,
            () -> {
              vendorService.getVendorDetails(TestUtil.VENDOR_ID, TestUtil.ORG_ID);
            });

    Assertions.assertEquals("Vendor not found with given details", exception.getMessage());
    verify(vendorPersistenceService, times(1))
        .findVendorByVendorIdAndOrgId(TestUtil.VENDOR_ID, TestUtil.ORG_ID);
  }

  @Test
  @DisplayName("Delete Vendor Test - happy path")
  void deleteVendorSuccess() throws CommonServiceException, PromiseEngineException {
    VendorDomainDto vendorDomainDto = testUtil.getVendorDomainDto(TestUtil.VENDOR_ID);
    when(vendorPersistenceService.findVendorByVendorIdAndOrgId(TestUtil.VENDOR_ID, TestUtil.ORG_ID))
        .thenReturn(Optional.of(vendorDomainDto));

    VendorResponse response = vendorService.deleteVendor(TestUtil.VENDOR_ID, TestUtil.ORG_ID);

    Assertions.assertNotNull(response);
    Assertions.assertEquals(TestUtil.VENDOR_ID, response.getVendorId());
    verify(vendorPersistenceService, times(1))
        .findVendorByVendorIdAndOrgId(TestUtil.VENDOR_ID, TestUtil.ORG_ID);
    verify(vendorPersistenceService, times(1)).deleteVendor(vendorDomainDto);
  }

  @Test
  @DisplayName("Delete Vendor Test - vendor not found")
  void deleteVendorNotFound() throws CommonServiceException, PromiseEngineException {
    when(vendorPersistenceService.findVendorByVendorIdAndOrgId(TestUtil.VENDOR_ID, TestUtil.ORG_ID))
        .thenReturn(Optional.empty());

    CommonServiceException exception =
        Assertions.assertThrows(
            CommonServiceException.class,
            () -> {
              vendorService.deleteVendor(TestUtil.VENDOR_ID, TestUtil.ORG_ID);
            });

    Assertions.assertEquals("Vendor not found with given details", exception.getMessage());
    verify(vendorPersistenceService, times(1))
        .findVendorByVendorIdAndOrgId(TestUtil.VENDOR_ID, TestUtil.ORG_ID);
    verify(vendorPersistenceService, times(0)).deleteVendor(any(VendorDomainDto.class));
  }
}
