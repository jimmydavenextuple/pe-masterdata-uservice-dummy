/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.node.carrier.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nextuple.carrier.domain.feign.CarrierFeign;
import com.nextuple.carrier.domain.outbound.CarrierServiceResponse;
import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.common.util.DateValidationUtil;
import com.nextuple.node.carrier.TestUtil;
import com.nextuple.node.carrier.config.NodeCarrierTenantBasedDBConfig;
import com.nextuple.node.carrier.domain.NodeCarrierDomain;
import com.nextuple.node.carrier.domain.dto.NodeCarrierListCacheKeyDto;
import com.nextuple.node.carrier.domain.entity.NodeCarrierEntity;
import com.nextuple.node.carrier.domain.inbound.NodeCarrierBufferRequest;
import com.nextuple.node.carrier.domain.inbound.NodeCarrierRequest;
import com.nextuple.node.carrier.domain.inbound.NodeCarrierSelectionRequest;
import com.nextuple.node.carrier.domain.outbound.NodeCarrierResponse;
import com.nextuple.node.carrier.domain.outbound.NodeCarrierSelectionResponse;
import com.nextuple.node.carrier.exception.InvalidDataException;
import com.nextuple.node.carrier.exception.NodeCarrierDomainException;
import com.nextuple.node.carrier.exception.NodeCarrierSelectionDomainException;
import com.nextuple.node.domain.feign.NodeFeign;
import com.nextuple.node.domain.outbound.NodeResponse;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.CollectionUtils;

@ExtendWith(MockitoExtension.class)
class NodeCarrierServiceTest {

  @InjectMocks NodeCarrierService nodeCarrierService;
  @Mock NodeFeign nodeFeign;
  @Mock NodeCarrierDomain nodeCarrierDomain;

  @InjectMocks TestUtil testUtil;

  @Mock DateValidationUtil dateValidationUtil;

  @Mock CarrierFeign carrierFeign;

  @Mock NodeCarrierTenantBasedDBConfig nodeCarrierTenantBasedDBConfig;

  @Test
  @DisplayName("When node carrier is created successfully")
  void createNodeCarrierTest()
      throws NodeCarrierDomainException, CommonServiceException, InvalidDataException {
    NodeCarrierRequest nodeCarrierRequest = testUtil.getNodeCarrierRequest();
    when(nodeCarrierTenantBasedDBConfig.getServiceOptions(any()))
        .thenReturn(Set.of("SDND", "EXPRESS", "STANDARD"));
    when(nodeFeign.getNodeDetails(any(), any())).thenReturn(testUtil.getBaseResponseOfNode());
    when(carrierFeign.getCarrierServiceDetailsByCarrierServiceIdAndOrgId(any(), any()))
        .thenReturn(testUtil.getCarrierServiceUpdateResponse());
    when(nodeCarrierDomain.saveNodeCarrierEntity(any()))
        .thenReturn(testUtil.getNodeCarrierEntity());

    NodeCarrierResponse nodeCarrierResponse =
        nodeCarrierService.createNodeCarrier(nodeCarrierRequest);

    Assertions.assertEquals(
        nodeCarrierRequest.getCarrierServiceId(), nodeCarrierResponse.getCarrierServiceId());
    verify(nodeCarrierDomain, times(1)).saveNodeCarrierEntity(any());
  }

  @Test
  @DisplayName("When node carrier is created successfully")
  void createNodeCarrierTestNullCarrierServiceId()
      throws NodeCarrierDomainException, CommonServiceException, InvalidDataException {
    NodeCarrierRequest nodeCarrierRequest = testUtil.getNodeCarrierRequest();
    when(nodeCarrierTenantBasedDBConfig.getServiceOptions(any()))
        .thenReturn(Set.of("SDND", "EXPRESS", "STANDARD"));
    when(nodeFeign.getNodeDetails(any(), any())).thenReturn(testUtil.getBaseResponseOfNode());
    nodeCarrierRequest.setCarrierServiceId(null);
    NodeCarrierEntity nodeCarrierEntity = testUtil.getNodeCarrierEntity();
    nodeCarrierEntity.setCarrierServiceId(null);
    when(nodeCarrierDomain.saveNodeCarrierEntity(any())).thenReturn(nodeCarrierEntity);

    NodeCarrierResponse nodeCarrierResponse =
        nodeCarrierService.createNodeCarrier(nodeCarrierRequest);

    Assertions.assertNull(nodeCarrierResponse.getCarrierServiceId());
    verify(nodeCarrierDomain, times(1)).saveNodeCarrierEntity(any());
  }

  @Test
  @DisplayName("When carrierServiceId passed is not valid")
  void createNodeCarrierTestWithInvalidCarrierServiceId_Test1() {
    NodeCarrierRequest nodeCarrierRequest = testUtil.getNodeCarrierRequest();
    when(nodeFeign.getNodeDetails(any(), any())).thenReturn(testUtil.getBaseResponseOfNode());

    when(carrierFeign.getCarrierServiceDetailsByCarrierServiceIdAndOrgId(any(), any()))
        .thenReturn(null);

    Exception exception =
        Assertions.assertThrows(
            CommonServiceException.class,
            () -> nodeCarrierService.createNodeCarrier(nodeCarrierRequest));
    Assertions.assertEquals(
        "Node carrier data cannot be created with given carrierServiceId and orgId",
        exception.getMessage());
  }

  @Test
  void createNodeCarrierTestWithInvalidCarrierServiceId_Test2() {
    NodeCarrierRequest nodeCarrierRequest = testUtil.getNodeCarrierRequest();
    when(nodeFeign.getNodeDetails(any(), any())).thenReturn(testUtil.getBaseResponseOfNode());
    BaseResponse<List<CarrierServiceResponse>> carrierServiceList =
        testUtil.getCarrierServiceUpdateResponse();
    carrierServiceList.setPayload(null);
    when(carrierFeign.getCarrierServiceDetailsByCarrierServiceIdAndOrgId(any(), any()))
        .thenReturn(carrierServiceList);

    Exception exception =
        Assertions.assertThrows(
            CommonServiceException.class,
            () -> nodeCarrierService.createNodeCarrier(nodeCarrierRequest));
    Assertions.assertEquals(
        "Node carrier data cannot be created with given carrierServiceId and orgId",
        exception.getMessage());
  }

  @Test
  void createNodeCarrierTestWithInvalidCarrierServiceId_Test3() {
    NodeCarrierRequest nodeCarrierRequest = testUtil.getNodeCarrierRequest();
    when(nodeFeign.getNodeDetails(any(), any())).thenReturn(testUtil.getBaseResponseOfNode());
    BaseResponse<List<CarrierServiceResponse>> carrierServiceList =
        testUtil.getCarrierServiceUpdateResponse();
    List<CarrierServiceResponse> emptyList = Collections.<CarrierServiceResponse>emptyList();
    carrierServiceList.setPayload(emptyList);
    when(carrierFeign.getCarrierServiceDetailsByCarrierServiceIdAndOrgId(any(), any()))
        .thenReturn(carrierServiceList);

    Exception exception =
        Assertions.assertThrows(
            CommonServiceException.class,
            () -> nodeCarrierService.createNodeCarrier(nodeCarrierRequest));
    Assertions.assertEquals(
        "Node carrier data cannot be created with given carrierServiceId and orgId",
        exception.getMessage());
  }

  @Test
  void createNodeCarrierServiceOptionExceptionTest() throws CommonServiceException {
    when(nodeCarrierTenantBasedDBConfig.getServiceOptions(any()))
        .thenReturn(Set.of("SDND", "EXPRESS", "STANDARD"));
    when(nodeFeign.getNodeDetails(any(), any())).thenReturn(testUtil.getBaseResponseOfNode());
    when(carrierFeign.getCarrierServiceDetailsByCarrierServiceIdAndOrgId(any(), any()))
        .thenReturn(testUtil.getCarrierServiceUpdateResponse());
    NodeCarrierRequest nodeCarrierRequest1 = testUtil.getNodeCarrierRequest5();

    Exception exception =
        Assertions.assertThrows(
            CommonServiceException.class,
            () -> nodeCarrierService.createNodeCarrier(nodeCarrierRequest1));
    Assertions.assertEquals("Invalid serviceOption", exception.getMessage());
  }

  @Test
  void createNodeCarrierExceptionTest() {
    when(nodeFeign.getNodeDetails(any(), any())).thenThrow(RuntimeException.class);
    NodeCarrierRequest nodeCarrierRequest1 = testUtil.getNodeCarrierRequest5();
    Exception exception =
        Assertions.assertThrows(
            CommonServiceException.class,
            () -> nodeCarrierService.createNodeCarrier(nodeCarrierRequest1));
    Assertions.assertEquals("Invalid nodeId and orgId combination", exception.getMessage());
  }

  @Test
  @DisplayName("When processing lead time is invalid")
  void createNodeCarrierTestInvalidProcessingLeadTime() throws CommonServiceException {
    when(nodeCarrierTenantBasedDBConfig.getServiceOptions(any()))
        .thenReturn(Set.of("SDND", "EXPRESS", "STANDARD"));
    when(nodeFeign.getNodeDetails(any(), any())).thenReturn(testUtil.getBaseResponseOfNode());
    NodeCarrierRequest nodeCarrierRequest1 = testUtil.getNodeCarrierRequest();
    nodeCarrierRequest1.setProcessingTime(-2.0);
    nodeCarrierRequest1.setCarrierServiceId(null);
    NodeCarrierRequest nodeCarrierRequest2 = testUtil.getNodeCarrierRequest();
    nodeCarrierRequest2.setProcessingTime(null);
    nodeCarrierRequest2.setCarrierServiceId(null);

    Exception exception1 =
        Assertions.assertThrows(
            InvalidDataException.class,
            () -> nodeCarrierService.createNodeCarrier(nodeCarrierRequest1));

    Exception exception2 =
        Assertions.assertThrows(
            InvalidDataException.class,
            () -> nodeCarrierService.createNodeCarrier(nodeCarrierRequest2));

    Assertions.assertNotNull(exception1);
    Assertions.assertNotNull(exception2);
  }

  @Test
  @DisplayName("When node carrier is created successfully")
  void createNodeCarrierWithValidBufferEndDateTest()
      throws NodeCarrierDomainException, CommonServiceException, InvalidDataException {
    NodeCarrierRequest nodeCarrierRequest = testUtil.getNodeCarrierRequest3();
    when(nodeCarrierTenantBasedDBConfig.getServiceOptions(any()))
        .thenReturn(Set.of("SDND", "EXPRESS", "STANDARD"));
    when(nodeFeign.getNodeDetails(any(), any())).thenReturn(testUtil.getBaseResponseOfNode());
    when(carrierFeign.getCarrierServiceDetailsByCarrierServiceIdAndOrgId(any(), any()))
        .thenReturn(testUtil.getCarrierServiceUpdateResponse());
    when(nodeCarrierDomain.saveNodeCarrierEntity(any()))
        .thenReturn(testUtil.getNodeCarrierEntity());

    NodeCarrierResponse nodeCarrierResponse =
        nodeCarrierService.createNodeCarrier(nodeCarrierRequest);

    Assertions.assertEquals(
        nodeCarrierRequest.getCarrierServiceId(), nodeCarrierResponse.getCarrierServiceId());
    verify(nodeCarrierDomain, times(1)).saveNodeCarrierEntity(any());
  }

  @Test
  @DisplayName("When bufferHours is negative")
  void createNodeCarrierWithNegativeBufferHoursExceptionTest() {
    NodeCarrierRequest nodeCarrierRequest = testUtil.getNodeCarrierRequest4();

    Exception ex =
        Assertions.assertThrows(
            CommonServiceException.class,
            () -> nodeCarrierService.createNodeCarrier(nodeCarrierRequest));

    Assertions.assertEquals("bufferHours cannot be negative", ex.getMessage());
  }

  @Test
  @DisplayName("When node carrier buffer data is updated successfully")
  void updateNodeCarrierBufferDataTest() throws NodeCarrierDomainException, CommonServiceException {
    when(nodeCarrierTenantBasedDBConfig.getServiceOptions(any()))
        .thenReturn(Set.of("serviceOption-1", "serviceOption-2", "serviceOption-3"));
    doNothing().when(dateValidationUtil).validateBufferStartAndEndDate(any(), any());
    when(nodeCarrierDomain.findNodeCarrierDetails(any(), any(), any(), any()))
        .thenReturn(Optional.ofNullable(testUtil.getNodeCarrierEntity()));
    when(nodeCarrierDomain.saveNodeCarrierEntity(any()))
        .thenReturn(testUtil.getNodeCarrierEntity());

    NodeCarrierResponse nodeCarrierResponse =
        nodeCarrierService.updateBufferData(testUtil.getNodeCarrierBufferRequest2());

    Assertions.assertEquals(testUtil.getNodeCarrierResponse(), nodeCarrierResponse);

    verify(nodeCarrierDomain, times(1)).findNodeCarrierDetails(any(), any(), any(), any());
    verify(nodeCarrierDomain, times(1)).saveNodeCarrierEntity(any());
  }

  @Test
  @DisplayName("When bufferHours is negative while updating buffer details")
  void updateNodeCarrierWithNegativeBufferHoursExceptionTest() throws CommonServiceException {
    NodeCarrierBufferRequest nodeCarrierBufferRequest = testUtil.getNodeCarrierBufferRequest3();

    doNothing().when(dateValidationUtil).validateBufferStartAndEndDate(any(), any());

    Exception ex =
        Assertions.assertThrows(
            CommonServiceException.class,
            () -> nodeCarrierService.updateBufferData(nodeCarrierBufferRequest));

    Assertions.assertEquals("bufferHours cannot be negative", ex.getMessage());
  }

  @Test
  @DisplayName("When node carrier to be updated is not found")
  void updateNodeCarrierNotFoundToUpdateBufferDataTest()
      throws NodeCarrierDomainException, CommonServiceException {
    when(nodeCarrierTenantBasedDBConfig.getServiceOptions(any()))
        .thenReturn(Set.of("serviceOption-1", "serviceOption-2", "serviceOption-3"));
    doNothing().when(dateValidationUtil).validateBufferStartAndEndDate(any(), any());
    when(nodeCarrierDomain.findNodeCarrierDetails(any(), any(), any(), any()))
        .thenReturn(Optional.empty());

    Exception ex =
        Assertions.assertThrows(
            CommonServiceException.class,
            () -> nodeCarrierService.updateBufferData(testUtil.getNodeCarrierBufferRequest2()));

    Assertions.assertEquals(TestUtil.NODE_CARRIER_NOT_FOUND_ERROR_MSG, ex.getMessage());
    verify(nodeCarrierDomain, times(1)).findNodeCarrierDetails(any(), any(), any(), any());
    verify(nodeCarrierDomain, times(0)).saveNodeCarrierEntity(any());
  }

  @Test
  @DisplayName("When node carrier to be updated is have invalid service option found")
  void updateNodeCarrierWithInvalidServieOptionToUpdateBufferDataTest()
      throws NodeCarrierDomainException, CommonServiceException {
    when(nodeCarrierTenantBasedDBConfig.getServiceOptions(any()))
        .thenReturn(Set.of("SDND", "EXPRESS", "STANDARD"));
    doNothing().when(dateValidationUtil).validateBufferStartAndEndDate(any(), any());
    Exception ex =
        Assertions.assertThrows(
            CommonServiceException.class,
            () -> nodeCarrierService.updateBufferData(testUtil.getNodeCarrierBufferRequest2()));

    Assertions.assertEquals(TestUtil.INVALID_SERVICE_OPTION, ex.getMessage());
  }

  @Test
  @DisplayName("When node carrier details is fetched successfully")
  void getNodeCarrierDetailsTest() throws NodeCarrierDomainException, CommonServiceException {
    when(nodeCarrierDomain.filterAndGetNodeCarrierDetails(any(), any(), any(), any()))
        .thenReturn(List.of(testUtil.getNodeCarrierEntity()));

    NodeCarrierResponse nodeCarrierResponse =
        nodeCarrierService.getNodeCarrierDetails(
            TestUtil.NODE_ID,
            TestUtil.ORG_ID,
            TestUtil.CARRIER_SERVICE_ID,
            TestUtil.SERVICE_OPTION);

    Assertions.assertEquals(testUtil.getNodeCarrierResponse(), nodeCarrierResponse);
    verify(nodeCarrierDomain, times(1)).filterAndGetNodeCarrierDetails(any(), any(), any(), any());
  }

  @Test
  @DisplayName("When node carrier to be fetched is not found")
  void getNodeCarrierDetailsNotFoundTest() throws NodeCarrierDomainException {
    List<NodeCarrierEntity> nodeCarrierEntityList = Collections.emptyList();
    when(nodeCarrierDomain.filterAndGetNodeCarrierDetails(any(), any(), any(), any()))
        .thenReturn(nodeCarrierEntityList);

    Exception ex =
        Assertions.assertThrows(
            CommonServiceException.class,
            () ->
                nodeCarrierService.getNodeCarrierDetails(
                    TestUtil.NODE_ID,
                    TestUtil.ORG_ID,
                    TestUtil.CARRIER_SERVICE_ID,
                    TestUtil.SERVICE_OPTION));

    Assertions.assertEquals(TestUtil.NODE_CARRIER_NOT_FOUND_ERROR_MSG, ex.getMessage());
    verify(nodeCarrierDomain, times(1)).filterAndGetNodeCarrierDetails(any(), any(), any(), any());
  }

  @Test
  void getNodeCarrierDetailsInvalidServiceOptionTestException() throws NodeCarrierDomainException {
    when(nodeCarrierDomain.filterAndGetNodeCarrierDetails(any(), any(), any(), any()))
        .thenReturn(List.of(testUtil.getNodeCarrierEntity()));

    Exception ex =
        Assertions.assertThrows(
            CommonServiceException.class,
            () ->
                nodeCarrierService.getNodeCarrierDetails(
                    TestUtil.NODE_ID, TestUtil.ORG_ID, TestUtil.CARRIER_SERVICE_ID, "Standard"));

    Assertions.assertEquals(TestUtil.NODE_CARRIER_NOT_FOUND_ERROR_MSG, ex.getMessage());
    verify(nodeCarrierDomain, times(1)).filterAndGetNodeCarrierDetails(any(), any(), any(), any());
  }

  @Test
  @DisplayName("When node carrier is updated successfully")
  void updateNodeCarrierTest()
      throws NodeCarrierDomainException, CommonServiceException, InvalidDataException {
    when(nodeCarrierDomain.findNodeCarrierDetails(any(), any(), any(), any()))
        .thenReturn(Optional.ofNullable(testUtil.getNodeCarrierEntity()));
    when(nodeCarrierDomain.saveNodeCarrierEntity(any()))
        .thenReturn(testUtil.getNodeCarrierEntity());

    NodeCarrierResponse nodeCarrierResponse =
        nodeCarrierService.updateNodeCarrier(
            TestUtil.NODE_ID,
            TestUtil.ORG_ID,
            TestUtil.CARRIER_SERVICE_ID,
            TestUtil.SERVICE_OPTION,
            testUtil.getNodeCarrierUpdateRequest());

    Assertions.assertEquals(testUtil.getNodeCarrierResponse(), nodeCarrierResponse);

    verify(nodeCarrierDomain, times(1)).findNodeCarrierDetails(any(), any(), any(), any());
    verify(nodeCarrierDomain, times(1)).saveNodeCarrierEntity(any());
  }

  @Test
  @DisplayName("When node carrier to be updated is not found")
  void updateNodeCarrierNotFoundTest() throws NodeCarrierDomainException {
    when(nodeCarrierDomain.findNodeCarrierDetails(any(), any(), any(), any()))
        .thenReturn(Optional.empty());

    Exception ex =
        Assertions.assertThrows(
            CommonServiceException.class,
            () ->
                nodeCarrierService.updateNodeCarrier(
                    TestUtil.NODE_ID,
                    TestUtil.ORG_ID,
                    TestUtil.CARRIER_SERVICE_ID,
                    TestUtil.SERVICE_OPTION,
                    testUtil.getNodeCarrierUpdateRequest()));

    Assertions.assertEquals(TestUtil.NODE_CARRIER_NOT_FOUND_ERROR_MSG, ex.getMessage());
    verify(nodeCarrierDomain, times(1)).findNodeCarrierDetails(any(), any(), any(), any());
    verify(nodeCarrierDomain, times(0)).saveNodeCarrierEntity(any());
  }

  @Test
  @DisplayName("When node carrier is deleted successfully")
  void deleteNodeCarrierTest() throws NodeCarrierDomainException, CommonServiceException {
    when(nodeCarrierTenantBasedDBConfig.getServiceOptions(any()))
        .thenReturn(Set.of("SDND", "EXPRESS", "STANDARD"));
    when(nodeFeign.getNodeDetails(any(), any())).thenReturn(testUtil.getBaseResponseOfNode());

    when(nodeCarrierDomain.findNodeCarrierDetails(any(), any(), any(), any()))
        .thenReturn(Optional.ofNullable(testUtil.getNodeCarrierEntity()));
    doNothing().when(nodeCarrierDomain).deleteNodeCarrierEntity(any());

    NodeCarrierResponse nodeCarrierResponse =
        nodeCarrierService.deleteNodeCarrier(
            TestUtil.NODE_ID, TestUtil.ORG_ID, TestUtil.CARRIER_SERVICE_ID, "EXPRESS");

    Assertions.assertEquals(testUtil.getNodeCarrierResponse(), nodeCarrierResponse);
    verify(nodeCarrierDomain, times(1)).findNodeCarrierDetails(any(), any(), any(), any());
    verify(nodeCarrierDomain, times(1)).deleteNodeCarrierEntity(any());
  }

  @Test
  @DisplayName("No Node Details found")
  void deleteNodeCarrierTestException1() throws NodeCarrierDomainException {
    when(nodeFeign.getNodeDetails(any(), any())).thenThrow(RuntimeException.class);

    Assertions.assertThrows(
        CommonServiceException.class,
        () ->
            nodeCarrierService.deleteNodeCarrier(
                TestUtil.NODE_ID, TestUtil.ORG_ID, TestUtil.CARRIER_SERVICE_ID, "EXPRESS"));

    verify(nodeCarrierDomain, times(0)).findNodeCarrierDetails(any(), any(), any(), any());
    verify(nodeCarrierDomain, times(0)).deleteNodeCarrierEntity(any());
  }

  @Test
  @DisplayName("No Node Details found")
  void deleteNodeCarrierTestExceptionNullNodeInfo() throws NodeCarrierDomainException {
    when(nodeFeign.getNodeDetails(any(), any())).thenReturn(null);

    Assertions.assertThrows(
        CommonServiceException.class,
        () ->
            nodeCarrierService.deleteNodeCarrier(
                TestUtil.NODE_ID, TestUtil.ORG_ID, TestUtil.CARRIER_SERVICE_ID, "EXPRESS"));

    verify(nodeCarrierDomain, times(0)).findNodeCarrierDetails(any(), any(), any(), any());
    verify(nodeCarrierDomain, times(0)).deleteNodeCarrierEntity(any());
  }

  @Test
  @DisplayName("No Node Details found")
  void deleteNodeCarrierTestExceptionEmptyNodeInfo() throws NodeCarrierDomainException {
    BaseResponse<NodeResponse> response = testUtil.getBaseResponseOfNode1();
    response.setPayload(null);
    when(nodeFeign.getNodeDetails(any(), any())).thenReturn(response);

    Assertions.assertThrows(
        CommonServiceException.class,
        () ->
            nodeCarrierService.deleteNodeCarrier(
                TestUtil.NODE_ID, TestUtil.ORG_ID, TestUtil.CARRIER_SERVICE_ID, "EXPRESS"));

    verify(nodeCarrierDomain, times(0)).findNodeCarrierDetails(any(), any(), any(), any());
    verify(nodeCarrierDomain, times(0)).deleteNodeCarrierEntity(any());
  }

  @Test
  void getNodeCarrierForNodeIdAOrgIdAndServiceOptionTest()
      throws NodeCarrierDomainException, CommonServiceException {
    List<NodeCarrierEntity> nodeCarrierEntityList = testUtil.getNodeCarrierEntityList();
    when(nodeCarrierDomain.findNodeCarrierByNodeIdAOrgIdAndServiceOption(any(), any(), any()))
        .thenReturn(nodeCarrierEntityList);

    List<NodeCarrierResponse> nodeCarrierResponseList =
        nodeCarrierService.getNodeCarrierForNodeIdAOrgIdAndServiceOption(
            TestUtil.NODE_ID, TestUtil.ORG_ID, TestUtil.SERVICE_OPTION);

    Assertions.assertEquals(2, nodeCarrierResponseList.size());
    Assertions.assertEquals(testUtil.getNodeCarrierDtoList(), nodeCarrierResponseList);
    verify(nodeCarrierDomain, times(1))
        .findNodeCarrierByNodeIdAOrgIdAndServiceOption(any(), any(), any());
  }

  @Test
  @DisplayName("When node carrier list to be fetched is not found")
  void getNodeCarrierForNodeIdAOrgIdAndServiceOptionNotFoundTest()
      throws NodeCarrierDomainException {
    List<NodeCarrierEntity> entityList = Collections.emptyList();
    when(nodeCarrierDomain.findNodeCarrierByNodeIdAOrgIdAndServiceOption(any(), any(), any()))
        .thenReturn(entityList);

    Exception ex =
        Assertions.assertThrows(
            CommonServiceException.class,
            () ->
                nodeCarrierService.getNodeCarrierForNodeIdAOrgIdAndServiceOption(
                    TestUtil.NODE_ID, TestUtil.ORG_ID, TestUtil.SERVICE_OPTION));

    Assertions.assertEquals(TestUtil.NODE_CARRIER_NOT_FOUND_ERROR_MSG, ex.getMessage());
    verify(nodeCarrierDomain, times(1))
        .findNodeCarrierByNodeIdAOrgIdAndServiceOption(any(), any(), any());
  }

  @Test
  void validateLastPickupTimeTest() {
    String lastPickUpTime = "14:89";
    Exception ex =
        Assertions.assertThrows(
            InvalidDataException.class,
            () -> nodeCarrierService.validateLastPickupTime(lastPickUpTime));
    Assertions.assertEquals("LastPickupTime is invalid", ex.getMessage());
  }

  @Test
  void getNodeCarrierForNodeIdAndOrgIdTest() throws NodeCarrierDomainException {
    List<NodeCarrierEntity> nodeCarrierEntityList = testUtil.getNodeCarrierEntityList2();
    when(nodeCarrierDomain.findNodeCarrierByNodeIdAndOrgId(anyString(), anyString()))
        .thenReturn(nodeCarrierEntityList);

    List<NodeCarrierResponse> nodeCarrierResponseList =
        nodeCarrierService.getNodeCarrierForNodeIdAndOrgId(TestUtil.NODE_ID, TestUtil.ORG_ID);

    assertEquals(nodeCarrierEntityList.size(), nodeCarrierResponseList.size());
    assertEquals(TestUtil.NODE_ID, nodeCarrierResponseList.get(0).getNodeId());
    assertEquals(TestUtil.ORG_ID, nodeCarrierResponseList.get(0).getOrgId());
    assertEquals("", nodeCarrierResponseList.get(0).getCarrierServiceId());

    verify(nodeCarrierDomain, times(1)).findNodeCarrierByNodeIdAndOrgId(anyString(), anyString());
  }

  @Test
  void updateProcessingLeadTime() throws NodeCarrierDomainException {
    when(nodeCarrierDomain.saveNodeCarrierEntity(any()))
        .thenReturn(testUtil.getNodeCarrierEntity());
    NodeCarrierResponse nodeCarrierResponse =
        nodeCarrierService.updateProcessingLeadTime(testUtil.getNodeCarrierRequest());
    Assertions.assertNotNull(nodeCarrierResponse);
    verify(nodeCarrierDomain, times(1)).saveNodeCarrierEntity(any());
  }

  @Test
  @DisplayName("When node carrier is created successfully")
  void createNodeCarrierSelectionTest() {
    NodeCarrierSelectionRequest nodeCarrierSelectionRequest =
        testUtil.getNodeCarrierSelectionRequest();
    when(nodeCarrierDomain.saveNodeCarrierSelectionEntity(any()))
        .thenReturn(testUtil.getNodeCarrierSelectionEntity());

    NodeCarrierSelectionResponse nodeCarrierSelectionResponse =
        nodeCarrierService.addNodeCarrierSelectionPriority(nodeCarrierSelectionRequest);

    Assertions.assertEquals(
        nodeCarrierSelectionRequest.getSourceGeozone(),
        nodeCarrierSelectionResponse.getSourceGeozone());
    verify(nodeCarrierDomain, times(1)).saveNodeCarrierSelectionEntity(any());
  }

  @Test
  void getNodeCarrierSelection() {
    when(nodeCarrierDomain.findNodeCarrierByOrgIdAndServiceOptionAndDestinationGeoZone(
            anyString(), anyString(), anyString()))
        .thenReturn(List.of(testUtil.getNodeCarrierSelectionEntity()));

    List<NodeCarrierSelectionResponse> nodeCarrierResponseList =
        nodeCarrierService.getNodeCarrierSelectionDetails(
            TestUtil.ORG_ID, TestUtil.SERVICE_OPTION, TestUtil.DESTINATION_GEOZONE);

    assertEquals(TestUtil.SERVICE_OPTION, nodeCarrierResponseList.get(0).getServiceOption());
    assertEquals(TestUtil.ORG_ID, nodeCarrierResponseList.get(0).getOrgId());

    verify(nodeCarrierDomain, times(1))
        .findNodeCarrierByOrgIdAndServiceOptionAndDestinationGeoZone(
            anyString(), anyString(), anyString());
  }

  @Test
  void getAllNodeCarrierCacheKeysTest() throws NodeCarrierDomainException {
    List<NodeCarrierEntity> nodeCarrierEntities = testUtil.getNodeCarrierEntityList();

    when(nodeCarrierDomain.getAllNodeCarriers(any())).thenReturn(nodeCarrierEntities);

    List<NodeCarrierListCacheKeyDto> response = nodeCarrierService.getAllNodeCarrierCacheKeys(2);

    assertEquals(2, response.size());
    assertEquals(nodeCarrierEntities.get(0).getNodeId(), response.get(0).getNodeId());
    verify(nodeCarrierDomain, times(1)).getAllNodeCarriers(any());
  }

  @Test
  void deleteNodeCarrierSelectionTest()
      throws NodeCarrierSelectionDomainException, CommonServiceException {
    when(nodeCarrierDomain.findNodeCarrierSelectionDetails(any(), any(), any(), any()))
        .thenReturn(Optional.of(testUtil.getNodeCarrierSelectionEntity()));
    doNothing().when(nodeCarrierDomain).deleteNodeCarrierSelectionEntity(any());

    nodeCarrierService.deleteNodeCarrierSelection(testUtil.getNodeCarrierSelectionRequest());

    verify(nodeCarrierDomain, times(1)).findNodeCarrierSelectionDetails(any(), any(), any(), any());
    verify(nodeCarrierDomain, times(1)).deleteNodeCarrierSelectionEntity(any());
  }

  @Test
  void deleteNodeCarrierSelectionNotFoundTestException()
      throws NodeCarrierSelectionDomainException {
    when(nodeCarrierDomain.findNodeCarrierSelectionDetails(any(), any(), any(), any()))
        .thenReturn(Optional.empty());

    Exception ex =
        Assertions.assertThrows(
            CommonServiceException.class,
            () ->
                nodeCarrierService.deleteNodeCarrierSelection(
                    testUtil.getNodeCarrierSelectionRequest()));

    Assertions.assertEquals("Node Carrier Selection not found for given details", ex.getMessage());
    verify(nodeCarrierDomain, times(1)).findNodeCarrierSelectionDetails(any(), any(), any(), any());
    verify(nodeCarrierDomain, times(0)).deleteNodeCarrierSelectionEntity(any());
  }

  @Test
  @DisplayName("Wrong Service Option")
  void deleteNodeCarrierTestException2() throws NodeCarrierDomainException, CommonServiceException {
    when(nodeCarrierTenantBasedDBConfig.getServiceOptions(any()))
        .thenReturn(Set.of("SDND", "EXPRESS", "STANDARD"));
    when(nodeFeign.getNodeDetails(any(), any())).thenReturn(testUtil.getBaseResponseOfNode());

    Assertions.assertThrows(
        CommonServiceException.class,
        () ->
            nodeCarrierService.deleteNodeCarrier(
                TestUtil.NODE_ID,
                TestUtil.ORG_ID,
                TestUtil.CARRIER_SERVICE_ID,
                TestUtil.SERVICE_OPTION));

    verify(nodeCarrierDomain, times(0)).findNodeCarrierDetails(any(), any(), any(), any());
    verify(nodeCarrierDomain, times(0)).deleteNodeCarrierEntity(any());
  }

  @Test
  @DisplayName("Wrong OrgId Option")
  void deleteNodeCarrierTestException3() throws NodeCarrierDomainException, CommonServiceException {
    when(nodeCarrierTenantBasedDBConfig.getServiceOptions(any()))
        .thenReturn(Set.of("SDND", "EXPRESS", "STANDARD"));
    when(nodeFeign.getNodeDetails(any(), any())).thenReturn(testUtil.getBaseResponseOfNode());

    Assertions.assertThrows(
        CommonServiceException.class,
        () ->
            nodeCarrierService.deleteNodeCarrier(
                TestUtil.NODE_ID, "", TestUtil.CARRIER_SERVICE_ID, "EXPRESS"));

    verify(nodeCarrierDomain, times(0)).findNodeCarrierDetails(any(), any(), any(), any());
    verify(nodeCarrierDomain, times(0)).deleteNodeCarrierEntity(any());
  }

  @Test
  @DisplayName("When node carrier to be deleted is not found")
  void deleteNodeCarrierNotFoundTestException()
      throws NodeCarrierDomainException, CommonServiceException {
    when(nodeCarrierTenantBasedDBConfig.getServiceOptions(any()))
        .thenReturn(Set.of("SDND", "EXPRESS", "STANDARD"));
    when(nodeFeign.getNodeDetails(any(), any())).thenReturn(testUtil.getBaseResponseOfNode());

    when(nodeCarrierDomain.findNodeCarrierDetails(any(), any(), any(), any()))
        .thenReturn(Optional.empty());

    Exception ex =
        Assertions.assertThrows(
            CommonServiceException.class,
            () ->
                nodeCarrierService.deleteNodeCarrier(
                    TestUtil.NODE_ID, TestUtil.ORG_ID, TestUtil.CARRIER_SERVICE_ID, "SDND"));

    Assertions.assertEquals(TestUtil.NODE_CARRIER_NOT_FOUND_ERROR_MSG, ex.getMessage());
    verify(nodeCarrierDomain, times(1)).findNodeCarrierDetails(any(), any(), any(), any());
    verify(nodeCarrierDomain, times(0)).deleteNodeCarrierEntity(any());
  }

  @Test
  @DisplayName("No Node Details found")
  void deleteNodeCarrierTestException4() throws NodeCarrierDomainException {

    when(nodeFeign.getNodeDetails(any(), any())).thenThrow(RuntimeException.class);

    Assertions.assertThrows(
        CommonServiceException.class,
        () ->
            nodeCarrierService.deleteNodeCarrier(
                TestUtil.NODE_ID, TestUtil.ORG_ID, TestUtil.CARRIER_SERVICE_ID, "EXPRESS"));

    verify(nodeCarrierDomain, times(0)).findNodeCarrierDetails(any(), any(), any(), any());
    verify(nodeCarrierDomain, times(0)).deleteNodeCarrierEntity(any());
  }

  @Test
  void getUniqueCarrierServiceIdList() throws NodeCarrierDomainException {
    when(nodeCarrierDomain.fetchUniqueNodeCarrierServiceListByOrgIdAndNodeId(
            anyString(), anyString()))
        .thenReturn(List.of(TestUtil.CARRIER_SERVICE_ID));

    List<String> uniqueCarrierServiceIdList =
        nodeCarrierService.getUniqueNodeCarrierServiceList(TestUtil.NODE_ID, TestUtil.ORG_ID);
    Assertions.assertFalse(CollectionUtils.isEmpty(uniqueCarrierServiceIdList));
    verify(nodeCarrierDomain, times(1))
        .fetchUniqueNodeCarrierServiceListByOrgIdAndNodeId(anyString(), anyString());
  }

  @Test
  void getNodeCarrierListForNodeIdAndOrgIdTest1() throws NodeCarrierDomainException {
    when(nodeCarrierDomain.findNodeCarrierDetailsByNodeIdAndOrgId(anyString(), anyString()))
        .thenReturn(
            testUtil.getNodeCarrierEntityListWithPickupDetails(TestUtil.CARRIER_SERVICE_ID));

    List<NodeCarrierResponse> nodeCarrierResponseList =
        nodeCarrierService.getNodeCarrierListForNodeIdAndOrgId(TestUtil.NODE_ID, TestUtil.ORG_ID);

    assertEquals(
        testUtil.getNodeCarrierEntityListWithPickupDetails(TestUtil.CARRIER_SERVICE_ID).size(),
        nodeCarrierResponseList.size());
    assertEquals(TestUtil.NODE_ID, nodeCarrierResponseList.get(0).getNodeId());
    assertEquals(TestUtil.ORG_ID, nodeCarrierResponseList.get(0).getOrgId());
    assertEquals(TestUtil.CARRIER_SERVICE_ID, nodeCarrierResponseList.get(0).getCarrierServiceId());

    verify(nodeCarrierDomain, times(1))
        .findNodeCarrierDetailsByNodeIdAndOrgId(anyString(), anyString());
  }

  @Test
  void getNodeCarrierListForNodeIdAndOrgIdTest2() throws NodeCarrierDomainException {
    when(nodeCarrierDomain.findNodeCarrierDetailsByNodeIdAndOrgId(anyString(), anyString()))
        .thenReturn(testUtil.getNodeCarrierEntityListWithPickupDetails(""));

    List<NodeCarrierResponse> nodeCarrierResponseList =
        nodeCarrierService.getNodeCarrierListForNodeIdAndOrgId(TestUtil.NODE_ID, TestUtil.ORG_ID);

    assertEquals(1, nodeCarrierResponseList.size());
    assertEquals(TestUtil.NODE_ID, nodeCarrierResponseList.get(0).getNodeId());
    assertEquals(TestUtil.ORG_ID, nodeCarrierResponseList.get(0).getOrgId());
    assertEquals(
        TestUtil.CARRIER_SERVICE_ID_2, nodeCarrierResponseList.get(0).getCarrierServiceId());

    verify(nodeCarrierDomain, times(1))
        .findNodeCarrierDetailsByNodeIdAndOrgId(anyString(), anyString());
  }

  @Test
  void getAllNodeCarrierByOrgIdTest() throws NodeCarrierDomainException {
    List<NodeCarrierEntity> nodeCarrierEntities = testUtil.getNodeCarrierEntityList2();
    when(nodeCarrierDomain.getAllNodeCarriersByOrgId(any())).thenReturn(nodeCarrierEntities);

    List<NodeCarrierResponse> nodeCarrierResponseList =
        nodeCarrierService.getAllNodeCarrierByOrgId(TestUtil.ORG_ID);

    assertEquals(2, nodeCarrierResponseList.size());
    assertEquals(TestUtil.NODE_ID, nodeCarrierResponseList.get(0).getNodeId());
    assertEquals(TestUtil.ORG_ID, nodeCarrierResponseList.get(0).getOrgId());
    assertEquals("", nodeCarrierResponseList.get(0).getCarrierServiceId());

    verify(nodeCarrierDomain, times(1)).getAllNodeCarriersByOrgId(any());
  }

  @Test
  void getAllNodeCarriersByOrgIdCarrierServiceIdTest() throws NodeCarrierDomainException {
    List<NodeCarrierEntity> nodeCarrierEntities = testUtil.getNodeCarrierEntityList();
    when(nodeCarrierDomain.getAllNodeCarriersByOrgIdCarrierServiceId(any(), any()))
        .thenReturn(nodeCarrierEntities);

    List<NodeCarrierResponse> nodeCarrierResponseList =
        nodeCarrierService.getAllNodeCarriersByOrgIdCarrierServiceId(
            TestUtil.ORG_ID, TestUtil.CARRIER_SERVICE_ID);

    assertEquals(2, nodeCarrierResponseList.size());
    assertEquals(TestUtil.NODE_ID, nodeCarrierResponseList.get(0).getNodeId());
    assertEquals(TestUtil.ORG_ID, nodeCarrierResponseList.get(0).getOrgId());
    assertEquals(TestUtil.CARRIER_SERVICE_ID, nodeCarrierResponseList.get(0).getCarrierServiceId());

    verify(nodeCarrierDomain, times(1)).getAllNodeCarriersByOrgIdCarrierServiceId(any(), any());
  }
}
