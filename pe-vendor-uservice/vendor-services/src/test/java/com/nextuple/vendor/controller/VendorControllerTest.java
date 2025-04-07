/*
 * Copyright (c) 2025., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.vendor.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.exception.PromiseEngineException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.vendor.TestUtil;
import com.nextuple.vendor.domain.inbound.VendorRequest;
import com.nextuple.vendor.domain.inbound.VendorUpdationRequest;
import com.nextuple.vendor.domain.outbound.VendorResponse;
import com.nextuple.vendor.service.VendorService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class VendorControllerTest {
  @InjectMocks private VendorController vendorController;
  @InjectMocks private TestUtil testUtil;
  @Mock private VendorService vendorService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  @DisplayName("Create Vendor Controller Test - happy path")
  void createVendorTest() throws PromiseEngineException {
    VendorRequest vendorRequest = testUtil.getVendorRequest();
    when(vendorService.createVendor(any(VendorRequest.class)))
        .thenReturn(testUtil.getVendorResponse());
    ResponseEntity<BaseResponse<VendorResponse>> responseEntity =
        vendorController.createVendor(vendorRequest);
    Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    Assertions.assertEquals(testUtil.getVendorResponse(), responseEntity.getBody().getPayload());
    verify(vendorService, times(1)).createVendor(any());
  }

  @Test
  @DisplayName("Create Vendor Controller Test - exception case")
  void createVendorExceptionTest() throws PromiseEngineException {
    VendorRequest vendorRequest = testUtil.getVendorRequest();
    when(vendorService.createVendor(any(VendorRequest.class)))
        .thenThrow(new RuntimeException("Failed to create vendor"));
    Exception exception =
        Assertions.assertThrows(
            Exception.class, () -> vendorController.createVendor(vendorRequest));
    Assertions.assertEquals("Failed to create vendor", exception.getMessage());
    verify(vendorService, times(1)).createVendor(any());
  }

  @Test
  @DisplayName("Get Vendor Details by Vendor Id and OrgId Controller Test - happy path")
  void getVendorDetailsByVendorIdAndOrgIdTest()
      throws CommonServiceException, PromiseEngineException {
    VendorResponse vendorResponse = testUtil.getVendorResponse();
    when(vendorService.getVendorDetails(any(), any())).thenReturn(vendorResponse);
    ResponseEntity<BaseResponse<VendorResponse>> responseEntity =
        vendorController.getVendorDetails(TestUtil.VENDOR_ID, TestUtil.ORG_ID);
    Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    Assertions.assertEquals(vendorResponse, responseEntity.getBody().getPayload());
    verify(vendorService, times(1)).getVendorDetails(any(), any());
  }

  @Test
  @DisplayName("Get Vendor Details by Vendor Id and OrgId Controller Test - exception case")
  void getVendorDetailsByVendorIdAndOrgIdExceptionTest()
      throws CommonServiceException, PromiseEngineException {
    when(vendorService.getVendorDetails(any(), any()))
        .thenThrow(new RuntimeException("Unable to fetch vendor details"));
    Exception exception =
        Assertions.assertThrows(
            Exception.class,
            () -> vendorController.getVendorDetails(TestUtil.VENDOR_ID, TestUtil.ORG_ID));
    Assertions.assertEquals("Unable to fetch vendor details", exception.getMessage());
    verify(vendorService, times(1)).getVendorDetails(any(), any());
  }

  @Test
  @DisplayName("Update Vendor Controller Test - happy path")
  void updateVendorTest() throws CommonServiceException, PromiseEngineException {
    VendorUpdationRequest vendorUpdationRequest = testUtil.getVendorUpdationRequest();
    when(vendorService.updateVendorDetails(
            anyString(), anyString(), any(VendorUpdationRequest.class)))
        .thenReturn(testUtil.getUpdatedVendorResponse());
    ResponseEntity<BaseResponse<VendorResponse>> responseEntity =
        vendorController.updateVendorDetails(
            TestUtil.VENDOR_ID, TestUtil.ORG_ID, vendorUpdationRequest);
    Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    Assertions.assertEquals(
        testUtil.getUpdatedVendorResponse(), responseEntity.getBody().getPayload());
    verify(vendorService, times(1)).updateVendorDetails(anyString(), anyString(), any());
  }

  @Test
  @DisplayName("Update Vendor Controller Test - exception case")
  void updateVendorExceptionTest() throws CommonServiceException, PromiseEngineException {
    VendorUpdationRequest vendorUpdationRequest = testUtil.getVendorUpdationRequest();
    when(vendorService.updateVendorDetails(
            anyString(), anyString(), any(VendorUpdationRequest.class)))
        .thenThrow(new RuntimeException("Unable to update the vendor details"));
    Exception exception =
        Assertions.assertThrows(
            Exception.class,
            () ->
                vendorController.updateVendorDetails(
                    TestUtil.VENDOR_ID, TestUtil.ORG_ID, vendorUpdationRequest));
    Assertions.assertEquals("Unable to update the vendor details", exception.getMessage());
    verify(vendorService, times(1)).updateVendorDetails(anyString(), anyString(), any());
  }

  @Test
  @DisplayName("Delete Vendor Controller Test - happy path")
  void deleteVendorTest() throws CommonServiceException, PromiseEngineException {
    when(vendorService.deleteVendor(any(), any())).thenReturn(testUtil.getVendorResponse());
    ResponseEntity<BaseResponse<VendorResponse>> responseEntity =
        vendorController.deleteVendor(TestUtil.VENDOR_ID, TestUtil.ORG_ID);
    Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    Assertions.assertEquals(testUtil.getVendorResponse(), responseEntity.getBody().getPayload());
    verify(vendorService, times(1)).deleteVendor(any(), any());
  }

  @Test
  @DisplayName("Delete Vendor Controller Test - exception case")
  void deleteVendorExceptionTest() throws CommonServiceException, PromiseEngineException {
    when(vendorService.deleteVendor(any(), any()))
        .thenThrow(new RuntimeException("Error while deleting vendor"));
    Exception exception =
        Assertions.assertThrows(
            Exception.class,
            () -> vendorController.deleteVendor(TestUtil.VENDOR_ID, TestUtil.ORG_ID));
    Assertions.assertEquals("Error while deleting vendor", exception.getMessage());
    verify(vendorService, times(1)).deleteVendor(any(), any());
  }
}
