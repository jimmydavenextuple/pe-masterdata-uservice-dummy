/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.node.carrier.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nextuple.carrier.domain.feign.CarrierFeign;
import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.node.carrier.TestUtil;
import com.nextuple.node.carrier.config.NodeCarrierTenantBasedDBConfig;
import com.nextuple.node.carrier.domain.inbound.NodeCarriersRequest;
import com.nextuple.node.carrier.domain.inbound.NodeCarriersUpdateRequest;
import com.nextuple.node.carrier.domain.inbound.NodeServiceOptionRequest;
import com.nextuple.node.carrier.domain.inbound.NodeServiceOptionUpdateRequest;
import com.nextuple.node.carrier.exception.InvalidDataException;
import com.nextuple.node.domain.feign.NodeFeign;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ValidationServiceTest {

  @InjectMocks ValidationService validationService;

  @Mock NodeCarrierTenantBasedDBConfig nodeCarrierTenantBasedDBConfig;
  @Mock NodeFeign nodeFeign;
  @Mock CarrierFeign carrierFeign;

  @InjectMocks private TestUtil testUtil;

  @Test
  void nodeCarriersRequest_ValidServiceOption()
      throws CommonServiceException, InvalidDataException {
    NodeCarriersRequest request = testUtil.getNodeCarriersRequest();
    when(nodeCarrierTenantBasedDBConfig.getServiceOptions(any()))
        .thenReturn(Set.of("EXPRESS", "SDND", "serviceOption-1"));
    when(nodeFeign.getNodeDetails(any(), any())).thenReturn(testUtil.getBaseResponseOfNode());
    when(carrierFeign.getCarrierServiceDetailsByCarrierServiceIdAndOrgId(any(), any()))
        .thenReturn(testUtil.getCarrierServiceUpdateResponse());

    validationService.validate(request);

    assertDoesNotThrow(() -> validationService.validate(request));
  }

  @Test
  void nodeCarriersRequest_Exception2() {
    NodeCarriersRequest request = testUtil.getNodeCarriersRequest();
    when(nodeFeign.getNodeDetails(any(), any())).thenReturn(testUtil.getBaseResponseOfNode());
    Exception e =
        assertThrows(CommonServiceException.class, () -> validationService.validate(request));
    assertEquals(
        "Node carrier data cannot be created with given carrierServiceId and orgId",
        e.getMessage());
  }

  @Test
  void nodeCarriersRequest_InvalidServiceOption() throws CommonServiceException {
    when(nodeCarrierTenantBasedDBConfig.getServiceOptions(any()))
        .thenReturn(Set.of("EXPRESS", "SDND"));
    NodeCarriersRequest request = testUtil.getNodeCarriersRequest();
    when(nodeFeign.getNodeDetails(any(), any())).thenReturn(testUtil.getBaseResponseOfNode());
    when(carrierFeign.getCarrierServiceDetailsByCarrierServiceIdAndOrgId(any(), any()))
        .thenReturn(testUtil.getCarrierServiceUpdateResponse());
    request.setServiceOption("invalid");
    Exception e =
        assertThrows(CommonServiceException.class, () -> validationService.validate(request));
    assertEquals("Invalid serviceOption", e.getMessage());
  }

  @Test
  void validateCarrierServiceId_Exception2NullPayload() {
    NodeCarriersRequest request = testUtil.getNodeCarriersRequest();
    when(nodeFeign.getNodeDetails(any(), any())).thenReturn(testUtil.getBaseResponseOfNode());
    var response = testUtil.getCarrierServiceUpdateResponse();
    response.setPayload(null);
    when(carrierFeign.getCarrierServiceDetailsByCarrierServiceIdAndOrgId(any(), any()))
        .thenReturn(response);
    Exception e =
        assertThrows(CommonServiceException.class, () -> validationService.validate(request));
    assertEquals(
        "Node carrier data cannot be created with given carrierServiceId and orgId",
        e.getMessage());
  }

  @Test
  void validateCarrierServiceId_Exception3EmptylPayload() {
    NodeCarriersRequest request = testUtil.getNodeCarriersRequest();
    var response = testUtil.getCarrierServiceUpdateResponse();
    response.setPayload(List.of());
    when(nodeFeign.getNodeDetails(any(), any())).thenReturn(testUtil.getBaseResponseOfNode());
    when(carrierFeign.getCarrierServiceDetailsByCarrierServiceIdAndOrgId(any(), any()))
        .thenReturn(response);
    Exception e =
        assertThrows(CommonServiceException.class, () -> validationService.validate(request));
    assertEquals(
        "Node carrier data cannot be created with given carrierServiceId and orgId",
        e.getMessage());
  }

  @Test
  void validateCarrierServiceId_Exception4EmptyCarrierServiceId() {
    NodeCarriersRequest request = testUtil.getNodeCarriersRequest();
    when(nodeFeign.getNodeDetails(any(), any())).thenReturn(testUtil.getBaseResponseOfNode());
    var response = testUtil.getCarrierServiceUpdateResponse();
    when(carrierFeign.getCarrierServiceDetailsByCarrierServiceIdAndOrgId("", TestUtil.ORG_ID))
        .thenReturn(response);
    Exception e =
        assertThrows(CommonServiceException.class, () -> validationService.validate(request));
    assertEquals(
        "Node carrier data cannot be created with given carrierServiceId and orgId",
        e.getMessage());
  }

  @Test
  void validateNodeDetails_InvalidDetailsNullResponse() {
    when(nodeFeign.getNodeDetails(any(), any())).thenReturn(null);
    NodeCarriersRequest request = testUtil.getNodeCarriersRequest();
    request.setNodeId("invalid");
    Exception e =
        assertThrows(CommonServiceException.class, () -> validationService.validate(request));
    assertEquals("Invalid nodeId and orgId combination", e.getMessage());
    verify(nodeFeign, times(1)).getNodeDetails(any(), any());
  }

  @Test
  void validateNodeDetails_InvalidDetailsNullPayload() {
    var response = testUtil.getBaseResponseOfNode();
    response.setPayload(null);
    when(nodeFeign.getNodeDetails(any(), any())).thenReturn(response);
    NodeCarriersRequest request = testUtil.getNodeCarriersRequest();
    request.setNodeId("invalid");
    Exception e =
        assertThrows(CommonServiceException.class, () -> validationService.validate(request));
    assertEquals("Invalid nodeId and orgId combination", e.getMessage());
    verify(nodeFeign, times(1)).getNodeDetails(any(), any());
  }

  @Test
  void nodeServiceOptionRequest_ValidServiceOption()
      throws CommonServiceException, InvalidDataException {
    NodeServiceOptionRequest request = testUtil.getNodeServiceOptionRequest();
    when(nodeCarrierTenantBasedDBConfig.getServiceOptions(any()))
        .thenReturn(Set.of("EXPRESS", "SDND", "serviceOption-1"));
    when(nodeFeign.getNodeDetails(any(), any())).thenReturn(testUtil.getBaseResponseOfNode());
    validationService.validate(request);

    assertDoesNotThrow(() -> validationService.validate(request));
  }

  @Test
  void nodeServiceOptionRequest_InvalidServiceOption() throws CommonServiceException {
    when(nodeCarrierTenantBasedDBConfig.getServiceOptions(any()))
        .thenReturn(Set.of("EXPRESS", "SDND"));
    NodeServiceOptionRequest request = testUtil.getNodeServiceOptionRequest();
    when(nodeFeign.getNodeDetails(any(), any())).thenReturn(testUtil.getBaseResponseOfNode());
    request.setServiceOption("invalid");
    Exception e =
        assertThrows(CommonServiceException.class, () -> validationService.validate(request));
    assertEquals("Invalid serviceOption", e.getMessage());
  }

  @Test
  void validateLastPickupTimeTest_valid() {
    NodeCarriersUpdateRequest request = testUtil.getNodeCarriersUpdateRequest();
    request.setLastPickupTime("20:00");
    assertDoesNotThrow(() -> validationService.validate(request));
  }

  @Test
  void validateLastPickupTimeTest_Invalid() {
    NodeCarriersUpdateRequest request = testUtil.getNodeCarriersUpdateRequest();
    request.setLastPickupTime("200:00");

    Exception ex =
        assertThrows(InvalidDataException.class, () -> validationService.validate(request));
    assertEquals("LastPickupTime is invalid", ex.getMessage());
  }

  @Test
  void validateProcessingLeadTime_ZeroLeadTime() {
    NodeServiceOptionUpdateRequest request = testUtil.getNodeServiceOptionUpdateRequest();
    request.setProcessingTime(0.0);

    assertDoesNotThrow(() -> validationService.validate(request));
  }

  @Test
  void validateProcessingLeadTime_ValidLeadTime() {
    NodeServiceOptionUpdateRequest request = testUtil.getNodeServiceOptionUpdateRequest();
    request.setProcessingTime(10.0);

    assertDoesNotThrow(() -> validationService.validate(request));
  }

  @Test
  void validateProcessingLeadTime_InvalidLeadTime() {
    NodeServiceOptionUpdateRequest request = testUtil.getNodeServiceOptionUpdateRequest();
    request.setProcessingTime(-10.0);
    Exception exception =
        assertThrows(InvalidDataException.class, () -> validationService.validate(request));
    assertEquals("Processing lead time can not be negative or empty", exception.getMessage());
  }

  @Test
  void validateProcessingLeadTime_nullLeadTime() {
    NodeServiceOptionUpdateRequest request = testUtil.getNodeServiceOptionUpdateRequest();
    request.setProcessingTime(null);
    Exception exception =
        assertThrows(InvalidDataException.class, () -> validationService.validate(request));
    assertEquals("Processing lead time can not be negative or empty", exception.getMessage());
  }

  @Test
  void validateCarrierDetails_ExceptionDuringFetching() {
    NodeCarriersRequest request = testUtil.getNodeCarriersRequest();
    when(nodeFeign.getNodeDetails(any(), any())).thenReturn(testUtil.getBaseResponseOfNode());
    when(carrierFeign.getCarrierServiceDetailsByCarrierServiceIdAndOrgId(any(), any()))
        .thenThrow(new RuntimeException("Failed to fetch carrier details"));
    Exception e =
        assertThrows(CommonServiceException.class, () -> validationService.validate(request));
    assertEquals(
        "Node carrier data cannot be created with given carrierServiceId and orgId",
        e.getMessage());
  }

  @Test
  void validateCarrierServiceId_ValidCarrierDetails() throws CommonServiceException {
    NodeCarriersRequest request = testUtil.getNodeCarriersRequest();
    when(nodeCarrierTenantBasedDBConfig.getServiceOptions(any()))
        .thenReturn(Set.of("EXPRESS", "SDND", "serviceOption-1"));
    when(nodeFeign.getNodeDetails(any(), any())).thenReturn(testUtil.getBaseResponseOfNode());
    when(carrierFeign.getCarrierServiceDetailsByCarrierServiceIdAndOrgId(any(), any()))
        .thenReturn(testUtil.getCarrierServiceUpdateResponse());
    assertDoesNotThrow(() -> validationService.validate(request));
  }

  @Test
  void validateCarrierServiceId_EmptyCarrierServiceId() throws CommonServiceException {
    NodeCarriersRequest request = testUtil.getNodeCarriersRequest();
    request.setCarrierServiceId("");
    when(nodeCarrierTenantBasedDBConfig.getServiceOptions(any()))
        .thenReturn(Set.of("EXPRESS", "SDND", "serviceOption-1"));
    when(nodeFeign.getNodeDetails(any(), any())).thenReturn(testUtil.getBaseResponseOfNode());
    assertDoesNotThrow(() -> validationService.validate(request));
  }
}
