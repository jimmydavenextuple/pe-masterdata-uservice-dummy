/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.node.carrier.service.impl;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nextuple.carrier.domain.feign.CarrierFeign;
import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.node.carrier.TestUtil;
import com.nextuple.node.carrier.config.NodeCarrierTenantBasedDBConfig;
import com.nextuple.node.carrier.domain.NodeServiceOptionsDomain;
import com.nextuple.node.carrier.domain.entity.NodeServiceOptionEntity;
import com.nextuple.node.carrier.domain.inbound.NodeServiceOptionRequest;
import com.nextuple.node.carrier.domain.inbound.NodeServiceOptionUpdateRequest;
import com.nextuple.node.carrier.domain.outbound.NodeServiceOptionResponse;
import com.nextuple.node.carrier.exception.InvalidDataException;
import com.nextuple.node.carrier.service.ValidationService;
import com.nextuple.node.domain.feign.NodeFeign;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;

class NodeServiceOptionsServiceImplTest {
  @InjectMocks NodeServiceOptionsServiceImpl nodeServiceOptionService;
  @Mock NodeFeign nodeFeign;
  @Mock NodeServiceOptionsDomain nodeServiceOptionsDomain;
  @Mock ValidationService validationService;
  @InjectMocks TestUtil testUtil;

  @Mock NodeCarrierTenantBasedDBConfig nodeCarrierTenantBasedDBConfig;

  @Mock CarrierFeign carrierFeign;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  @DisplayName("Create Node Service Option - Happy path")
  void createNodeServiceOptionHappyPath() throws CommonServiceException, InvalidDataException {
    NodeServiceOptionRequest nodeServiceOptionRequest = testUtil.getNodeServiceOptionRequest();
    when(nodeCarrierTenantBasedDBConfig.getServiceOptions(any()))
        .thenReturn(Set.of("SDND", "EXPRESS", "STANDARD", TestUtil.SERVICE_OPTION));
    when(nodeFeign.getNodeDetails(any(), any())).thenReturn(testUtil.getBaseResponseOfNode());
    when(carrierFeign.getCarrierServiceDetailsByCarrierServiceIdAndOrgId(any(), any()))
        .thenReturn(testUtil.getCarrierServiceUpdateResponse());
    when(nodeServiceOptionsDomain.saveNodeServiceOptionEntity(any()))
        .thenReturn(testUtil.getNodeServiceOptionEntity());
    doNothing().when(validationService).validate(nodeServiceOptionRequest);
    NodeServiceOptionResponse nodeServiceOptionResponse =
        nodeServiceOptionService.createNodeServiceOption(nodeServiceOptionRequest);
    Assertions.assertEquals(
        nodeServiceOptionRequest.getServiceOption(), nodeServiceOptionResponse.getServiceOption());
    verify(nodeServiceOptionsDomain, times(1)).saveNodeServiceOptionEntity(any());
  }

  @Test
  @DisplayName("When service option passed is not valid")
  void createNodeCarriersTestWithInvalidServiceOption()
      throws CommonServiceException, InvalidDataException {
    NodeServiceOptionRequest nodeServiceOptionRequest = testUtil.getNodeServiceOptionRequest();
    when(nodeFeign.getNodeDetails(any(), any())).thenReturn(testUtil.getBaseResponseOfNode());

    when(carrierFeign.getCarrierServiceDetailsByCarrierServiceIdAndOrgId(any(), any()))
        .thenReturn(null);
    doThrow(
            new CommonServiceException(
                "Invalid serviceOption", HttpStatus.BAD_REQUEST, 0x1772, null))
        .when(validationService)
        .validate(nodeServiceOptionRequest);

    Exception exception =
        assertThrows(
            CommonServiceException.class,
            () -> nodeServiceOptionService.createNodeServiceOption(nodeServiceOptionRequest));
    Assertions.assertEquals("Invalid serviceOption", exception.getMessage());
  }

  @Test
  @DisplayName("Create Node Service Option - Invalid Processing Time")
  void createNodeServiceOption_InvalidProcessingTimeExceptionTest()
      throws CommonServiceException, InvalidDataException {
    NodeServiceOptionRequest nodeServiceOptionRequest = testUtil.getNodeServiceOptionRequest();
    nodeServiceOptionRequest.setProcessingTime(-2.0);
    when(nodeCarrierTenantBasedDBConfig.getServiceOptions(any()))
        .thenReturn(Set.of("SDND", "EXPRESS", "STANDARD", TestUtil.SERVICE_OPTION));
    when(nodeFeign.getNodeDetails(any(), any())).thenReturn(testUtil.getBaseResponseOfNode());
    doThrow(
            new InvalidDataException(
                "Processing lead time can not be negative or empty",
                null,
                nodeServiceOptionRequest.getProcessingTime()))
        .when(validationService)
        .validate(nodeServiceOptionRequest);
    InvalidDataException exception =
        assertThrows(
            InvalidDataException.class,
            () -> nodeServiceOptionService.createNodeServiceOption(nodeServiceOptionRequest));
    Assertions.assertEquals(
        "Processing lead time can not be negative or empty", exception.getMessage());
    verify(nodeServiceOptionsDomain, never()).saveNodeServiceOptionEntity(any());
  }

  @Test
  @DisplayName("Create Node Service Option - Invalid nodeId and orgId combination")
  void createNodeServiceOptionExceptionTest() throws CommonServiceException, InvalidDataException {
    NodeServiceOptionRequest nodeServiceOptionRequest = testUtil.getNodeServiceOptionRequest();
    nodeServiceOptionRequest.setOrgId("invalid");
    when(nodeFeign.getNodeDetails(any(), any())).thenThrow(new RuntimeException());
    doThrow(
            new CommonServiceException(
                "Invalid nodeId and orgId combination", HttpStatus.BAD_REQUEST, 0x1772, null))
        .when(validationService)
        .validate(nodeServiceOptionRequest);

    Exception exception =
        assertThrows(
            CommonServiceException.class,
            () -> nodeServiceOptionService.createNodeServiceOption(nodeServiceOptionRequest));
    Assertions.assertEquals("Invalid nodeId and orgId combination", exception.getMessage());
  }

  @Test
  @DisplayName("Successful Execution - Node Service Option Found")
  void getNodeServiceOption_Success() throws CommonServiceException {
    when(nodeServiceOptionsDomain.findNodeServiceOptionEntity(any(), any(), any()))
        .thenReturn(Optional.of(testUtil.getNodeServiceOptionEntity()));
    NodeServiceOptionResponse result =
        nodeServiceOptionService.getNodeServiceOption(
            TestUtil.ORG_ID, TestUtil.NODE_ID, TestUtil.SERVICE_OPTION);
    Assertions.assertNotNull(result);
    Assertions.assertEquals(TestUtil.SERVICE_OPTION, result.getServiceOption());
    verify(nodeServiceOptionsDomain, times(1)).findNodeServiceOptionEntity(any(), any(), any());
  }

  @Test
  @DisplayName("Node Service Option Not Found")
  void getNodeServiceOption_NodeServiceOptionNotFound() throws CommonServiceException {
    when(nodeServiceOptionsDomain.findNodeServiceOptionEntity(any(), any(), any()))
        .thenReturn(Optional.empty());

    CommonServiceException exception =
        assertThrows(
            CommonServiceException.class,
            () ->
                nodeServiceOptionService.getNodeServiceOption(
                    "invalidOrgId", "invalidNodeId", "invalidServiceOption"));

    Assertions.assertEquals(
        "Node Service Option not found for given details", exception.getMessage());
  }

  @Test
  @DisplayName("Successful Update - Node Service Option Found")
  void updateNodeServiceOption_SuccessfulUpdate()
      throws CommonServiceException, InvalidDataException {
    NodeServiceOptionUpdateRequest updateRequest = testUtil.getNodeServiceOptionUpdateRequest();
    NodeServiceOptionEntity existingEntity = testUtil.getNodeServiceOptionEntity();
    when(nodeCarrierTenantBasedDBConfig.getServiceOptions(any()))
        .thenReturn(Set.of("SDND", "EXPRESS", "STANDARD", TestUtil.SERVICE_OPTION));
    when(nodeFeign.getNodeDetails(any(), any())).thenReturn(testUtil.getBaseResponseOfNode());
    when(nodeServiceOptionsDomain.findNodeServiceOptionEntity(any(), any(), any()))
        .thenReturn(Optional.of(existingEntity));
    when(nodeServiceOptionsDomain.saveNodeServiceOptionEntity(any())).thenReturn(existingEntity);
    NodeServiceOptionResponse result =
        nodeServiceOptionService.updateNodeServiceOption(
            TestUtil.ORG_ID, TestUtil.NODE_ID, TestUtil.SERVICE_OPTION, updateRequest);
    Assertions.assertNotNull(result);
    verify(nodeServiceOptionsDomain, times(1)).findNodeServiceOptionEntity(any(), any(), any());
    verify(nodeServiceOptionsDomain, times(1)).saveNodeServiceOptionEntity(any());
  }

  @Test
  @DisplayName("Node Carrier Not Found")
  void updateNodeServiceOption_NodeServiceOptionNotFound() throws CommonServiceException {
    NodeServiceOptionUpdateRequest updateRequest = testUtil.getNodeServiceOptionUpdateRequest();
    when(nodeCarrierTenantBasedDBConfig.getServiceOptions(any()))
        .thenReturn(Set.of("SDND", "EXPRESS", "STANDARD", TestUtil.SERVICE_OPTION));
    when(nodeFeign.getNodeDetails(any(), any())).thenReturn(testUtil.getBaseResponseOfNode());
    when(nodeServiceOptionsDomain.findNodeServiceOptionEntity(any(), any(), any()))
        .thenReturn(Optional.empty());
    CommonServiceException exception =
        assertThrows(
            CommonServiceException.class,
            () ->
                nodeServiceOptionService.updateNodeServiceOption(
                    TestUtil.ORG_ID, TestUtil.NODE_ID, TestUtil.SERVICE_OPTION, updateRequest));

    Assertions.assertEquals(
        "Node Service Option not found for given details", exception.getMessage());
    verify(nodeServiceOptionsDomain, times(1)).findNodeServiceOptionEntity(any(), any(), any());
  }

  @Test
  @DisplayName("Invalid Processing Time")
  void updateNodeServiceOption_InvalidProcessingTime()
      throws CommonServiceException, InvalidDataException {
    when(nodeCarrierTenantBasedDBConfig.getServiceOptions(any()))
        .thenReturn(Set.of("SDND", "EXPRESS", "STANDARD", TestUtil.SERVICE_OPTION));
    when(nodeFeign.getNodeDetails(any(), any())).thenReturn(testUtil.getBaseResponseOfNode());
    when(carrierFeign.getCarrierServiceDetailsByCarrierServiceIdAndOrgId(any(), any()))
        .thenReturn(testUtil.getCarrierServiceUpdateResponse());
    doNothing().when(validationService).validate(testUtil.getNodeServiceOptionUpdateRequest());
    NodeServiceOptionUpdateRequest updateRequest = testUtil.getNodeServiceOptionUpdateRequest();
    updateRequest.setProcessingTime(-2.0);
    doThrow(
            new InvalidDataException(
                "Processing lead time can not be negative or empty",
                null,
                updateRequest.getProcessingTime()))
        .when(validationService)
        .validate(updateRequest);

    InvalidDataException exception =
        assertThrows(
            InvalidDataException.class,
            () ->
                nodeServiceOptionService.updateNodeServiceOption(
                    TestUtil.ORG_ID, TestUtil.NODE_ID, TestUtil.SERVICE_OPTION, updateRequest));

    Assertions.assertEquals(
        "Processing lead time can not be negative or empty", exception.getMessage());
    verify(nodeServiceOptionsDomain, never()).findNodeServiceOptionEntity(any(), any(), any());
  }

  @Test
  @DisplayName("Invalid Processing Time - null")
  void updateNodeServiceOption_InvalidProcessingTime2()
      throws CommonServiceException, InvalidDataException {
    when(nodeCarrierTenantBasedDBConfig.getServiceOptions(any()))
        .thenReturn(Set.of("SDND", "EXPRESS", "STANDARD", TestUtil.SERVICE_OPTION));
    when(nodeFeign.getNodeDetails(any(), any())).thenReturn(testUtil.getBaseResponseOfNode());
    when(carrierFeign.getCarrierServiceDetailsByCarrierServiceIdAndOrgId(any(), any()))
        .thenReturn(testUtil.getCarrierServiceUpdateResponse());
    doNothing().when(validationService).validate(testUtil.getNodeServiceOptionUpdateRequest());
    NodeServiceOptionUpdateRequest updateRequest = testUtil.getNodeServiceOptionUpdateRequest();
    updateRequest.setProcessingTime(null);
    doThrow(
            new InvalidDataException(
                "Processing lead time can not be negative or empty",
                null,
                updateRequest.getProcessingTime()))
        .when(validationService)
        .validate(updateRequest);

    InvalidDataException exception =
        assertThrows(
            InvalidDataException.class,
            () ->
                nodeServiceOptionService.updateNodeServiceOption(
                    TestUtil.ORG_ID, TestUtil.NODE_ID, TestUtil.SERVICE_OPTION, updateRequest));

    Assertions.assertEquals(
        "Processing lead time can not be negative or empty", exception.getMessage());
    verify(nodeServiceOptionsDomain, never()).findNodeServiceOptionEntity(any(), any(), any());
  }

  @Test
  @DisplayName("When node service option is deleted successfully")
  void deleteNodeServiceOptionTest() throws CommonServiceException {
    when(nodeCarrierTenantBasedDBConfig.getServiceOptions(any()))
        .thenReturn(Set.of("SDND", "EXPRESS", "STANDARD"));
    when(nodeFeign.getNodeDetails(any(), any())).thenReturn(testUtil.getBaseResponseOfNode());

    when(nodeServiceOptionsDomain.findNodeServiceOptionEntity(any(), any(), any()))
        .thenReturn(Optional.ofNullable(testUtil.getNodeServiceOptionEntity()));
    doNothing()
        .when(nodeServiceOptionsDomain)
        .deleteNodeServiceOptionEntityByOrgIdAndNodeIdAndServiceOption(any(), any(), any());

    NodeServiceOptionResponse nodeServiceOptionResponse =
        nodeServiceOptionService.deleteNodeServiceOption(
            TestUtil.ORG_ID, TestUtil.NODE_ID, "EXPRESS");

    Assertions.assertEquals(testUtil.getNodeServiceOptionResponse(), nodeServiceOptionResponse);
    verify(nodeServiceOptionsDomain, times(1)).findNodeServiceOptionEntity(any(), any(), any());
    verify(nodeServiceOptionsDomain, times(1))
        .deleteNodeServiceOptionEntityByOrgIdAndNodeIdAndServiceOption(any(), any(), any());
  }

  @Test
  @DisplayName("No Node service option Details found")
  void deleteNodeServiceOptionException1() throws CommonServiceException {
    when(nodeFeign.getNodeDetails(any(), any())).thenThrow(RuntimeException.class);

    CommonServiceException exception =
        assertThrows(
            CommonServiceException.class,
            () ->
                nodeServiceOptionService.deleteNodeServiceOption(
                    TestUtil.ORG_ID, TestUtil.NODE_ID, "EXPRESS"));
    Assertions.assertEquals(
        "Node Service Option not found for given details", exception.getMessage());

    verify(nodeServiceOptionsDomain, times(1)).findNodeServiceOptionEntity(any(), any(), any());
    verify(nodeServiceOptionsDomain, times(0))
        .deleteNodeServiceOptionEntityByOrgIdAndNodeIdAndServiceOption(any(), any(), any());
  }

  @Test
  @DisplayName("Successful Execution - Node Service Option List Found")
  void getNodeServiceOptionList_Success() throws CommonServiceException {
    List<NodeServiceOptionEntity> optionEntityList = new ArrayList<>();
    optionEntityList.add(testUtil.getNodeServiceOptionEntity());
    when(nodeServiceOptionsDomain.findByOrgIdAndNodeId(any(), any())).thenReturn(optionEntityList);

    List<NodeServiceOptionResponse> result =
        nodeServiceOptionService.getNodeServiceOptionList(TestUtil.ORG_ID, TestUtil.NODE_ID);

    Assertions.assertNotNull(result);
    Assertions.assertFalse(result.isEmpty());
    Assertions.assertEquals(1, result.size());

    verify(nodeServiceOptionsDomain, times(1)).findByOrgIdAndNodeId(any(), any());
  }
}
