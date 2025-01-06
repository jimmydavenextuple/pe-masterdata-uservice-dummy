/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.node.carrier.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.node.carrier.TestUtil;
import com.nextuple.node.carrier.domain.dto.NodeCarrierListCacheKeyDto;
import com.nextuple.node.carrier.domain.outbound.NodeCarriersResponse;
import com.nextuple.node.carrier.exception.InvalidDataException;
import com.nextuple.node.carrier.service.NodeCarriersService;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Description;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class NodeCarriersControllerTest {
  @InjectMocks NodeCarriersController nodeCarriersController;

  @Mock NodeCarriersService nodeCarriersService;

  @InjectMocks TestUtil testUtil;

  @Test
  @DisplayName("When node carrier is created successfully and response is 200 OK")
  void createNodeCarriersTest() throws CommonServiceException, InvalidDataException {
    when(nodeCarriersService.createNodeCarrier(any()))
        .thenReturn(testUtil.getNodeCarriersResponse());

    ResponseEntity<BaseResponse<NodeCarriersResponse>> response =
        nodeCarriersController.createNodeCarrier(testUtil.getNodeCarriersRequest());

    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    Assertions.assertEquals(
        TestUtil.NODE_ID, Objects.requireNonNull(response.getBody()).getPayload().getNodeId());
    Assertions.assertEquals(
        TestUtil.ORG_ID, Objects.requireNonNull(response.getBody()).getPayload().getOrgId());
    verify(nodeCarriersService, times(1)).createNodeCarrier(any());
  }

  @Test
  @DisplayName("When there is somme error in creating node carrier")
  void createNodeCarriersExceptionTest() throws CommonServiceException, InvalidDataException {
    when(nodeCarriersService.createNodeCarrier(any()))
        .thenThrow(new RuntimeException("Failed to create node carrier details"));

    Exception ex =
        Assertions.assertThrows(
            Exception.class,
            () -> nodeCarriersController.createNodeCarrier(testUtil.getNodeCarriersRequest()));

    Assertions.assertEquals("Failed to create node carrier details", ex.getMessage());
    verify(nodeCarriersService, times(1)).createNodeCarrier(any());
  }

  @Test
  @DisplayName("When node carrier is fetched successfully and response is 200 OK")
  void getNodeCarriersTest() throws CommonServiceException {
    when(nodeCarriersService.getNodeCarrierDetails(any(), any(), any(), any()))
        .thenReturn(testUtil.getNodeCarriersResponse());

    ResponseEntity<BaseResponse<NodeCarriersResponse>> response =
        nodeCarriersController.getNodeCarrier(
            TestUtil.NODE_ID,
            TestUtil.ORG_ID,
            TestUtil.CARRIER_SERVICE_ID,
            TestUtil.SERVICE_OPTION);

    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    Assertions.assertEquals(
        TestUtil.NODE_ID, Objects.requireNonNull(response.getBody()).getPayload().getNodeId());
    Assertions.assertEquals(
        TestUtil.ORG_ID, Objects.requireNonNull(response.getBody()).getPayload().getOrgId());
    verify(nodeCarriersService, times(1)).getNodeCarrierDetails(any(), any(), any(), any());
  }

  @Test
  @DisplayName("When there is somme error in fetching node carrier")
  void getNodeCarriersExceptionTest() throws CommonServiceException {
    when(nodeCarriersService.getNodeCarrierDetails(any(), any(), any(), any()))
        .thenThrow(new RuntimeException("Failed to get node carrier details"));

    Exception ex =
        Assertions.assertThrows(
            Exception.class,
            () ->
                nodeCarriersController.getNodeCarrier(
                    TestUtil.NODE_ID,
                    TestUtil.ORG_ID,
                    TestUtil.CARRIER_SERVICE_ID,
                    TestUtil.SERVICE_OPTION));

    Assertions.assertEquals("Failed to get node carrier details", ex.getMessage());
    verify(nodeCarriersService, times(1)).getNodeCarrierDetails(any(), any(), any(), any());
  }

  @Test
  @DisplayName("When node carrier is updated successfully and response is 200 OK")
  void updateNodeCarriersTest() throws CommonServiceException, InvalidDataException {
    when(nodeCarriersService.updateNodeCarrier(any(), any(), any(), any(), any()))
        .thenReturn(testUtil.getNodeCarriersResponse());

    ResponseEntity<BaseResponse<NodeCarriersResponse>> response =
        nodeCarriersController.updateNodeCarrier(
            TestUtil.NODE_ID,
            TestUtil.ORG_ID,
            TestUtil.CARRIER_SERVICE_ID,
            TestUtil.SERVICE_OPTION,
            testUtil.getNodeCarriersUpdateRequest());

    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    Assertions.assertEquals(
        TestUtil.NODE_ID, Objects.requireNonNull(response.getBody()).getPayload().getNodeId());
    Assertions.assertEquals(
        TestUtil.ORG_ID, Objects.requireNonNull(response.getBody()).getPayload().getOrgId());
    verify(nodeCarriersService, times(1)).updateNodeCarrier(any(), any(), any(), any(), any());
  }

  @Test
  @DisplayName("When there is somme error in updating node carrier")
  void updateNodeCarriersExceptionTest() throws CommonServiceException, InvalidDataException {
    when(nodeCarriersService.updateNodeCarrier(any(), any(), any(), any(), any()))
        .thenThrow(new RuntimeException("Failed to update node carrier details"));

    Exception ex =
        Assertions.assertThrows(
            Exception.class,
            () ->
                nodeCarriersController.updateNodeCarrier(
                    TestUtil.NODE_ID,
                    TestUtil.ORG_ID,
                    TestUtil.CARRIER_SERVICE_ID,
                    TestUtil.SERVICE_OPTION,
                    testUtil.getNodeCarriersUpdateRequest()));

    Assertions.assertEquals("Failed to update node carrier details", ex.getMessage());
    verify(nodeCarriersService, times(1)).updateNodeCarrier(any(), any(), any(), any(), any());
  }

  @Test
  @DisplayName("When node carrier is deleted successfully and response is 200 OK")
  void deleteNodeCarriersTest() throws CommonServiceException {
    when(nodeCarriersService.deleteNodeCarrier(any(), any(), any(), any()))
        .thenReturn(testUtil.getNodeCarriersResponse());

    ResponseEntity<BaseResponse<NodeCarriersResponse>> response =
        nodeCarriersController.deleteNodeCarrier(
            TestUtil.NODE_ID,
            TestUtil.ORG_ID,
            TestUtil.CARRIER_SERVICE_ID,
            TestUtil.SERVICE_OPTION);

    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    Assertions.assertEquals(
        TestUtil.NODE_ID, Objects.requireNonNull(response.getBody()).getPayload().getNodeId());
    Assertions.assertEquals(
        TestUtil.ORG_ID, Objects.requireNonNull(response.getBody()).getPayload().getOrgId());
    verify(nodeCarriersService, times(1)).deleteNodeCarrier(any(), any(), any(), any());
  }

  @Test
  @DisplayName("When there is somme error in deleting node carrier")
  void deleteNodeCarriersExceptionTest() throws CommonServiceException {
    when(nodeCarriersService.deleteNodeCarrier(any(), any(), any(), any()))
        .thenThrow(new RuntimeException("Failed to delete node carrier details"));

    Exception ex =
        Assertions.assertThrows(
            Exception.class,
            () ->
                nodeCarriersController.deleteNodeCarrier(
                    TestUtil.NODE_ID,
                    TestUtil.ORG_ID,
                    TestUtil.CARRIER_SERVICE_ID,
                    TestUtil.SERVICE_OPTION));

    assertEquals("Failed to delete node carrier details", ex.getMessage());
    verify(nodeCarriersService, times(1)).deleteNodeCarrier(any(), any(), any(), any());
  }

  @Test
  @DisplayName("Get Node Carriers List for NodeId and OrgId")
  void getNodeCarriersListTest() throws CommonServiceException {
    List<NodeCarriersResponse> nodeCarriersResponseList =
        Arrays.asList(testUtil.getNodeCarriersResponse(), testUtil.getNodeCarriersResponse2());
    when(nodeCarriersService.getNodeCarriersListByOrgIdAndNodeId(TestUtil.ORG_ID, TestUtil.NODE_ID))
        .thenReturn(nodeCarriersResponseList);

    ResponseEntity<BaseResponse<List<NodeCarriersResponse>>> response =
        nodeCarriersController.getNodeCarriersList(TestUtil.ORG_ID, TestUtil.NODE_ID);

    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals("Node Carriers list fetched successfully", response.getBody().getMessage());
    assertEquals(nodeCarriersResponseList, response.getBody().getPayload());
    verify(nodeCarriersService, times(1))
        .getNodeCarriersListByOrgIdAndNodeId(TestUtil.ORG_ID, TestUtil.NODE_ID);
  }

  @Test
  @DisplayName("Successful Retrieval of Unique Carrier Service List")
  void getUniqueNodeCarrierServiceList_Success() throws CommonServiceException {
    List<String> uniqueCarrierServiceList = Arrays.asList("Service1", "Service2", "Service3");
    when(nodeCarriersService.getListOfCarrierServiceNameByOrgIdAndNodeId(
            TestUtil.ORG_ID, TestUtil.NODE_ID))
        .thenReturn(uniqueCarrierServiceList);

    ResponseEntity<BaseResponse<List<String>>> response =
        nodeCarriersController.getUniqueNodeCarrierServiceList(TestUtil.ORG_ID, TestUtil.NODE_ID);

    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(uniqueCarrierServiceList, response.getBody().getPayload());
    verify(nodeCarriersService, times(1))
        .getListOfCarrierServiceNameByOrgIdAndNodeId(TestUtil.ORG_ID, TestUtil.NODE_ID);
  }

  @Test
  @DisplayName("Get Node Carriers List for NodeId and OrgId and serviceOption")
  void getNodeCarriersListByOrgIdAndNodeIdAndServiceOptionTest() throws CommonServiceException {
    List<NodeCarriersResponse> nodeCarriersResponseList =
        Arrays.asList(testUtil.getNodeCarriersResponse(), testUtil.getNodeCarriersResponse2());
    when(nodeCarriersService.getNodeCarriersList(
            TestUtil.ORG_ID, TestUtil.NODE_ID, TestUtil.SERVICE_OPTION))
        .thenReturn(nodeCarriersResponseList);

    ResponseEntity<BaseResponse<List<NodeCarriersResponse>>> response =
        nodeCarriersController.getNodeCarriersListByOrgIdAndNodeIdAndServiceOption(
            TestUtil.ORG_ID, TestUtil.NODE_ID, TestUtil.SERVICE_OPTION);

    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals("Node Carriers list fetched successfully", response.getBody().getMessage());
    verify(nodeCarriersService, times(1))
        .getNodeCarriersList(TestUtil.ORG_ID, TestUtil.NODE_ID, TestUtil.SERVICE_OPTION);
  }

  @Test
  @DisplayName("Get Node Carriers List cache keys test")
  void getNodeCarriersListCacheKeysTest() throws CommonServiceException {
    List<NodeCarrierListCacheKeyDto> nodeCarrierListCacheKeyDtoList =
        testUtil.getNodeCarrierListCacheKeyDtoList();

    when(nodeCarriersService.getAllNodeCarriersCacheKeys(any()))
        .thenReturn(nodeCarrierListCacheKeyDtoList);

    ResponseEntity<BaseResponse<List<NodeCarrierListCacheKeyDto>>> responseEntity =
        nodeCarriersController.getNodeCarriersCacheKeys(2);

    Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    Assertions.assertEquals(2, responseEntity.getBody().getPayload().size());

    verify(nodeCarriersService, times(1)).getAllNodeCarriersCacheKeys(any());
  }

  @Test
  void getAllNodeCarriersByOrgIdCarrierServiceIdTest() throws CommonServiceException {
    List<NodeCarriersResponse> nodeCarriersResponseList = testUtil.getNodeCarriersList();
    when(nodeCarriersService.getAllNodeCarriersByOrgIdCarrierServiceId(any(), any()))
        .thenReturn(nodeCarriersResponseList);

    ResponseEntity<BaseResponse<List<NodeCarriersResponse>>> response =
        nodeCarriersController.getAllNodeCarriersByOrgIdCarrierServiceId(
            TestUtil.ORG_ID, TestUtil.CARRIER_SERVICE_ID);

    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    Assertions.assertEquals(
        TestUtil.NODE_ID,
        Objects.requireNonNull(response.getBody()).getPayload().get(0).getNodeId());
    Assertions.assertEquals(
        TestUtil.ORG_ID, Objects.requireNonNull(response.getBody()).getPayload().get(0).getOrgId());
    Assertions.assertEquals(
        TestUtil.CARRIER_SERVICE_ID,
        Objects.requireNonNull(response.getBody()).getPayload().get(0).getCarrierServiceId());
    verify(nodeCarriersService, times(1)).getAllNodeCarriersByOrgIdCarrierServiceId(any(), any());
  }

  @Test
  @Description("Get Node Carriers by orgId, NodeId and carrierServiceId Test")
  void getAllNodeCarriersByOrgIdNodeIdAndCarrierServiceId() throws CommonServiceException {
    List<NodeCarriersResponse> nodeCarriersResponseList = testUtil.getNodeCarriersList();
    when(nodeCarriersService.getAllNodeCarriersByOrgIdNodeIdAndCarrierServiceId(
            any(), any(), any()))
        .thenReturn(nodeCarriersResponseList);
    ResponseEntity<BaseResponse<List<NodeCarriersResponse>>> response =
        nodeCarriersController.getAllNodeCarriersByOrgIdNodeIdAndCarrierServiceId(
            TestUtil.ORG_ID, TestUtil.NODE_ID, TestUtil.CARRIER_SERVICE_ID);
    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    Assertions.assertEquals(
        TestUtil.NODE_ID,
        Objects.requireNonNull(response.getBody()).getPayload().get(0).getNodeId());
    Assertions.assertEquals(
        TestUtil.ORG_ID, Objects.requireNonNull(response.getBody()).getPayload().get(0).getOrgId());
    Assertions.assertEquals(
        TestUtil.CARRIER_SERVICE_ID,
        Objects.requireNonNull(response.getBody()).getPayload().get(0).getCarrierServiceId());
    verify(nodeCarriersService, times(1))
        .getAllNodeCarriersByOrgIdNodeIdAndCarrierServiceId(any(), any(), any());
  }

  @Test
  @Description("Delete Node Carrier by NodeId - Happy Path")
  void deleteNodeCarrierByNodeIdTest() throws CommonServiceException {
    List<NodeCarriersResponse> nodeCarriersResponseList = testUtil.getNodeCarriersList();
    when(nodeCarriersService.deleteNodeCarrierByNodeId(any(), any()))
        .thenReturn(nodeCarriersResponseList);
    ResponseEntity<BaseResponse<List<NodeCarriersResponse>>> response =
        nodeCarriersController.deleteNodeCarrierByNodeId(TestUtil.ORG_ID, TestUtil.NODE_ID);
    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    Assertions.assertEquals(
        TestUtil.NODE_ID,
        Objects.requireNonNull(response.getBody()).getPayload().get(0).getNodeId());
    Assertions.assertEquals(
        TestUtil.ORG_ID, Objects.requireNonNull(response.getBody()).getPayload().get(0).getOrgId());
    Assertions.assertEquals(
        TestUtil.CARRIER_SERVICE_ID,
        Objects.requireNonNull(response.getBody()).getPayload().get(0).getCarrierServiceId());
    verify(nodeCarriersService, times(1)).deleteNodeCarrierByNodeId(any(), any());
  }
}
