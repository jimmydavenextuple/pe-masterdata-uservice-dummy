/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.node.carrier.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nextuple.carrier.domain.feign.CarrierFeign;
import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.node.carrier.TestUtil;
import com.nextuple.node.carrier.config.NodeCarrierTenantBasedDBConfig;
import com.nextuple.node.carrier.domain.NodeCarriersDomain;
import com.nextuple.node.carrier.domain.dto.NodeCarrierListCacheKeyDto;
import com.nextuple.node.carrier.domain.entity.NodeCarriersEntity;
import com.nextuple.node.carrier.domain.inbound.NodeCarriersRequest;
import com.nextuple.node.carrier.domain.inbound.NodeCarriersUpdateRequest;
import com.nextuple.node.carrier.domain.outbound.NodeCarriersResponse;
import com.nextuple.node.carrier.exception.InvalidDataException;
import com.nextuple.node.carrier.service.ValidationService;
import com.nextuple.node.domain.feign.NodeFeign;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;

class NodeCarriersServiceImplTest {
  @InjectMocks NodeCarriersServiceImpl nodeCarriersService;
  @Mock NodeFeign nodeFeign;
  @Mock NodeCarriersDomain nodeCarriersDomain;

  @InjectMocks TestUtil testUtil;

  @Mock NodeCarrierTenantBasedDBConfig nodeCarrierTenantBasedDBConfig;

  @Mock ValidationService validationService;

  @Mock CarrierFeign carrierFeign;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  @DisplayName("Successful Execution - Create Node Carrier Happy path")
  void createNodeCarriersHappyPath() throws CommonServiceException, InvalidDataException {
    NodeCarriersRequest nodeCarriersRequest = testUtil.getNodeCarriersRequest();
    when(nodeCarrierTenantBasedDBConfig.getServiceOptions(any()))
        .thenReturn(Set.of("SDND", "EXPRESS", "STANDARD"));
    when(nodeFeign.getNodeDetails(any(), any())).thenReturn(testUtil.getBaseResponseOfNode());
    when(carrierFeign.getCarrierServiceDetailsByCarrierServiceIdAndOrgId(any(), any()))
        .thenReturn(testUtil.getCarrierServiceUpdateResponse());
    when(nodeCarriersDomain.findNodeCarrierDetails(
            nodeCarriersRequest.getOrgId(),
            nodeCarriersRequest.getNodeId(),
            nodeCarriersRequest.getCarrierServiceId(),
            nodeCarriersRequest.getServiceOption()))
        .thenReturn(Optional.empty());
    when(nodeCarriersDomain.saveNodeCarrierEntity(any()))
        .thenReturn(testUtil.getNodeCarriersEntity());
    doNothing().when(validationService).validate(testUtil.getNodeCarriersRequest());
    NodeCarriersResponse nodeCarriersResponse =
        nodeCarriersService.createNodeCarrier(nodeCarriersRequest);
    assertEquals(
        nodeCarriersRequest.getCarrierServiceId(), nodeCarriersResponse.getCarrierServiceId());
    verify(nodeCarriersDomain, times(1)).saveNodeCarrierEntity(any());
  }

  @Test
  @DisplayName("Exception - Create Node Carrier Service Option")
  void createNodeCarrierServiceOptionExceptionTest() throws CommonServiceException {
    NodeCarriersRequest nodeCarriersRequest = testUtil.getNodeCarriersRequest();
    when(nodeCarriersDomain.findNodeCarrierDetails(any(), any(), any(), any()))
        .thenReturn(Optional.empty());
    when(nodeFeign.getNodeDetails(any(), any())).thenReturn(testUtil.getBaseResponseOfNode());
    doThrow(new RuntimeException("Error during node carrier service option creation"))
        .when(nodeCarriersDomain)
        .saveNodeCarrierEntity(any());

    RuntimeException exception =
        assertThrows(
            RuntimeException.class,
            () -> nodeCarriersService.createNodeCarrier(nodeCarriersRequest));

    assertEquals("Error during node carrier service option creation", exception.getMessage());
    verify(nodeCarriersDomain, times(1)).saveNodeCarrierEntity(any());
  }

  @Test
  @DisplayName("Successful Execution - Node Carrier Found")
  void getNodeCarrierDetails_Success() throws CommonServiceException {
    when(nodeCarriersDomain.filterAndGetNodeCarrierDetails(any(), any(), any(), any()))
        .thenReturn(Optional.of(testUtil.getNodeCarriersEntity()));
    NodeCarriersResponse result =
        nodeCarriersService.getNodeCarrierDetails(
            TestUtil.ORG_ID,
            TestUtil.NODE_ID,
            TestUtil.CARRIER_SERVICE_ID,
            TestUtil.SERVICE_OPTION);
    assertNotNull(result);
    assertEquals(TestUtil.CARRIER_SERVICE_ID, result.getCarrierServiceId());
    verify(nodeCarriersDomain, times(1)).filterAndGetNodeCarrierDetails(any(), any(), any(), any());
  }

  @Test
  @DisplayName("Exception - Node Carrier Not Found")
  void getNodeCarrierDetails_NodeCarrierNotFound() throws CommonServiceException {
    when(nodeCarriersDomain.filterAndGetNodeCarrierDetails(any(), any(), any(), any()))
        .thenReturn(Optional.empty());

    CommonServiceException exception =
        assertThrows(
            CommonServiceException.class,
            () ->
                nodeCarriersService.getNodeCarrierDetails(
                    "invalidOrgId",
                    "invalidNodeId",
                    "invalidCarrierServiceId",
                    "invalidServiceOption"));

    assertEquals("Node Carrier not found for given details", exception.getMessage());
  }

  @Test
  @DisplayName("Successful Update - Node Carrier Found")
  void updateNodeCarrier_SuccessfulUpdate() throws CommonServiceException, InvalidDataException {
    NodeCarriersUpdateRequest updateRequest = testUtil.getNodeCarriersUpdateRequest();
    NodeCarriersEntity existingEntity = testUtil.getNodeCarriersEntity();
    when(nodeCarriersDomain.findNodeCarrierDetails(any(), any(), any(), any()))
        .thenReturn(Optional.of(existingEntity));
    when(nodeCarriersDomain.saveNodeCarrierEntity(any())).thenReturn(existingEntity);
    NodeCarriersResponse result =
        nodeCarriersService.updateNodeCarrier(
            TestUtil.ORG_ID,
            TestUtil.NODE_ID,
            TestUtil.CARRIER_SERVICE_ID,
            TestUtil.SERVICE_OPTION,
            updateRequest);

    assertNotNull(result);
    verify(nodeCarriersDomain, times(1)).findNodeCarrierDetails(any(), any(), any(), any());
    verify(nodeCarriersDomain, times(1)).saveNodeCarrierEntity(any());
  }

  @Test
  @DisplayName("Exception - Node Carrier Not Found")
  void updateNodeCarrier_NodeCarrierNotFound() throws CommonServiceException {
    NodeCarriersUpdateRequest updateRequest = testUtil.getNodeCarriersUpdateRequest();
    when(nodeCarriersDomain.findNodeCarrierDetails(any(), any(), any(), any()))
        .thenReturn(Optional.empty());

    CommonServiceException exception =
        assertThrows(
            CommonServiceException.class,
            () ->
                nodeCarriersService.updateNodeCarrier(
                    TestUtil.ORG_ID,
                    TestUtil.NODE_ID,
                    TestUtil.CARRIER_SERVICE_ID,
                    TestUtil.SERVICE_OPTION,
                    updateRequest));

    assertEquals("Node Carrier not found for given details", exception.getMessage());
    verify(nodeCarriersDomain, times(1)).findNodeCarrierDetails(any(), any(), any(), any());
  }

  @Test
  @DisplayName("Successful Execution - When node carrier is deleted successfully")
  void deleteNodeCarriersTest() throws CommonServiceException {
    when(nodeCarrierTenantBasedDBConfig.getServiceOptions(any()))
        .thenReturn(Set.of("SDND", "EXPRESS", "STANDARD"));
    when(nodeFeign.getNodeDetails(any(), any())).thenReturn(testUtil.getBaseResponseOfNode());

    when(nodeCarriersDomain.findNodeCarrierDetails(any(), any(), any(), any()))
        .thenReturn(Optional.ofNullable(testUtil.getNodeCarriersEntity()));
    doNothing()
        .when(nodeCarriersDomain)
        .deleteNodeCarrierEntityByNodeIdAndOrgIdAndCarrierServiceIdAndServiceOption(
            any(), any(), any(), any());

    NodeCarriersResponse nodeCarriersResponse =
        nodeCarriersService.deleteNodeCarrier(
            TestUtil.ORG_ID, TestUtil.NODE_ID, TestUtil.CARRIER_SERVICE_ID, "EXPRESS");

    assertEquals(testUtil.getNodeCarriersResponse(), nodeCarriersResponse);
    verify(nodeCarriersDomain, times(1)).findNodeCarrierDetails(any(), any(), any(), any());
    verify(nodeCarriersDomain, times(1))
        .deleteNodeCarrierEntityByNodeIdAndOrgIdAndCarrierServiceIdAndServiceOption(
            any(), any(), any(), any());
  }

  @Test
  @DisplayName("Exception - Node Carrier Not Found - Deletion")
  void deleteNodeCarrier_NodeCarrierNotFound() throws CommonServiceException {
    when(nodeCarrierTenantBasedDBConfig.getServiceOptions(any()))
        .thenReturn(Set.of("SDND", "EXPRESS", "STANDARD"));
    when(nodeFeign.getNodeDetails(any(), any())).thenReturn(testUtil.getBaseResponseOfNode());
    when(carrierFeign.getCarrierServiceDetailsByCarrierServiceIdAndOrgId(any(), any()))
        .thenReturn(testUtil.getCarrierServiceUpdateResponse());
    when(nodeCarriersDomain.findNodeCarrierDetails(any(), any(), any(), any()))
        .thenReturn(Optional.empty());

    CommonServiceException exception =
        assertThrows(
            CommonServiceException.class,
            () ->
                nodeCarriersService.deleteNodeCarrier(
                    TestUtil.ORG_ID,
                    "nonexistentNodeId",
                    "nonexistentCarrierServiceId",
                    "STANDARD"));

    assertEquals("Node Carrier not found for given details", exception.getMessage());
    assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
    assertNotNull(exception.getFieldInfo());
    assertEquals(4, exception.getFieldInfo().size());
  }

  @Test
  void getAllNodeCarriersCacheKeysTest() throws CommonServiceException {
    List<NodeCarriersEntity> nodeCarrierEntities = testUtil.getNodeCarriersEntityList();

    when(nodeCarriersDomain.getAllNodeCarriers(any())).thenReturn(nodeCarrierEntities);

    List<NodeCarrierListCacheKeyDto> response = nodeCarriersService.getAllNodeCarriersCacheKeys(2);

    assertEquals(2, response.size());
    assertEquals(nodeCarrierEntities.get(0).getNodeId(), response.get(0).getNodeId());
    verify(nodeCarriersDomain, times(1)).getAllNodeCarriers(any());
  }

  @Test
  @DisplayName("When node carriers list to be fetched successful")
  void getNodeCarriersList() throws CommonServiceException {
    when(nodeCarriersDomain.findNodeCarriersListByOrgIdAndNodeIdAndServiceOption(
            anyString(), anyString(), anyString()))
        .thenReturn(testUtil.getNodeCarriersEntity(TestUtil.CARRIER_SERVICE_ID));

    List<NodeCarriersResponse> nodeCarrierResponseList =
        nodeCarriersService.getNodeCarriersList(
            TestUtil.ORG_ID, TestUtil.NODE_ID, TestUtil.SERVICE_OPTION);

    assertEquals(
        testUtil.getNodeCarriersEntity(TestUtil.CARRIER_SERVICE_ID).size(),
        nodeCarrierResponseList.size());
    assertEquals(TestUtil.NODE_ID, nodeCarrierResponseList.get(0).getNodeId());
    assertEquals(TestUtil.ORG_ID, nodeCarrierResponseList.get(0).getOrgId());
    assertEquals(TestUtil.CARRIER_SERVICE_ID, nodeCarrierResponseList.get(0).getCarrierServiceId());

    verify(nodeCarriersDomain, times(1))
        .findNodeCarriersListByOrgIdAndNodeIdAndServiceOption(
            anyString(), anyString(), anyString());
  }

  @Test
  @DisplayName("When node carriers list to be fetched is not found")
  void getNodeCarriersList2() throws CommonServiceException {
    List<NodeCarriersEntity> entityList = Collections.emptyList();
    when(nodeCarriersDomain.findNodeCarriersListByOrgIdAndNodeIdAndServiceOption(
            any(), any(), any()))
        .thenReturn(entityList);

    Exception ex =
        assertThrows(
            CommonServiceException.class,
            () ->
                nodeCarriersService.getNodeCarriersList(
                    TestUtil.NODE_ID, TestUtil.ORG_ID, TestUtil.SERVICE_OPTION));

    assertEquals("Node Carriers not found for given key", ex.getMessage());
    verify(nodeCarriersDomain, times(1))
        .findNodeCarriersListByOrgIdAndNodeIdAndServiceOption(any(), any(), any());
  }

  @Test
  @DisplayName("Successful Execution - Get Node Carriers List for NodeId and OrgId")
  void getNodeCarriersListForNodeIdAndOrgIdTest() throws CommonServiceException {
    NodeCarriersEntity entity1 = testUtil.getNodeCarriersEntity();
    NodeCarriersEntity entity2 = testUtil.getNodeCarriersEntity2();
    List<NodeCarriersEntity> entities = Arrays.asList(entity1, entity2);
    when(nodeCarriersDomain.findByOrgIdAndNodeId(TestUtil.ORG_ID, TestUtil.ORG_ID))
        .thenReturn(entities);

    List<NodeCarriersResponse> responses =
        nodeCarriersService.getNodeCarriersListByOrgIdAndNodeId(TestUtil.ORG_ID, TestUtil.ORG_ID);
    assertNotNull(responses);
    assertEquals(entities.size(), responses.size());
    verify(nodeCarriersDomain, times(1)).findByOrgIdAndNodeId(TestUtil.ORG_ID, TestUtil.ORG_ID);
  }

  @Test
  @DisplayName("Successful Execution - Get List of Carrier Service Names by OrgId and NodeId")
  void getListOfCarrierServiceNameByOrgIdAndNodeIdTest() throws CommonServiceException {
    List<NodeCarriersEntity> entities =
        Arrays.asList(testUtil.getNodeCarriersEntity(), testUtil.getNodeCarriersEntity2());
    when(nodeCarriersDomain.findByOrgIdAndNodeId(TestUtil.ORG_ID, TestUtil.ORG_ID))
        .thenReturn(entities);

    List<String> carrierServiceNames =
        nodeCarriersService.getListOfCarrierServiceNameByOrgIdAndNodeId(
            TestUtil.ORG_ID, TestUtil.ORG_ID);

    assertNotNull(carrierServiceNames);
    assertEquals(
        entities.stream()
            .map(NodeCarriersEntity::getCarrierServiceId)
            .distinct()
            .collect(Collectors.toList()),
        carrierServiceNames);

    verify(nodeCarriersDomain, times(1)).findByOrgIdAndNodeId(TestUtil.ORG_ID, TestUtil.ORG_ID);
  }

  @Test
  void getAllNodeCarriersByOrgIdCarrierServiceIdTest() throws CommonServiceException {
    List<NodeCarriersEntity> nodeCarriersEntities = testUtil.getNodeCarriersEntityList();
    when(nodeCarriersDomain.getAllNodeCarriersByOrgIdCarrierServiceId(any(), any()))
        .thenReturn(nodeCarriersEntities);

    List<NodeCarriersResponse> nodeCarrierResponseList =
        nodeCarriersService.getAllNodeCarriersByOrgIdCarrierServiceId(
            TestUtil.ORG_ID, TestUtil.CARRIER_SERVICE_ID);

    assertEquals(2, nodeCarrierResponseList.size());
    assertEquals(TestUtil.NODE_ID, nodeCarrierResponseList.get(0).getNodeId());
    assertEquals(TestUtil.ORG_ID, nodeCarrierResponseList.get(0).getOrgId());
    assertEquals(TestUtil.CARRIER_SERVICE_ID, nodeCarrierResponseList.get(0).getCarrierServiceId());

    verify(nodeCarriersDomain, times(1)).getAllNodeCarriersByOrgIdCarrierServiceId(any(), any());
  }
}
