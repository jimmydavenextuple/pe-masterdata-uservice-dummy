/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.node.carrier.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.node.carrier.TestUtil;
import com.nextuple.node.carrier.domain.outbound.NodeServiceOptionResponse;
import com.nextuple.node.carrier.exception.InvalidDataException;
import com.nextuple.node.carrier.exception.NodeCarrierDomainException;
import com.nextuple.node.carrier.service.NodeServiceOptionsService;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
public class NodeServiceOptionsControllerTest {

  @InjectMocks NodeServiceOptionsController nodeServiceOptionsController;

  @Mock NodeServiceOptionsService nodeServiceOptionsService;

  @InjectMocks TestUtil testUtil;

  @Test
  @DisplayName("When node service option is created successfully and response is 200 OK")
  void createNodeCarriersTest() throws CommonServiceException, InvalidDataException {
    when(nodeServiceOptionsService.createNodeServiceOption(any()))
        .thenReturn(testUtil.getNodeServiceOptionResponse());

    ResponseEntity<BaseResponse<NodeServiceOptionResponse>> response =
        nodeServiceOptionsController.createNodeServiceOption(
            testUtil.getNodeServiceOptionRequest());

    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    Assertions.assertEquals(
        TestUtil.NODE_ID, Objects.requireNonNull(response.getBody()).getPayload().getNodeId());
    Assertions.assertEquals(
        TestUtil.ORG_ID, Objects.requireNonNull(response.getBody()).getPayload().getOrgId());
    verify(nodeServiceOptionsService, times(1)).createNodeServiceOption(any());
  }

  @Test
  @DisplayName("When there is some error in creating node service option")
  void createNodeCarriersExceptionTest() throws CommonServiceException, InvalidDataException {
    when(nodeServiceOptionsService.createNodeServiceOption(any()))
        .thenThrow(new RuntimeException("Failed to create node service option details"));

    Exception ex =
        Assertions.assertThrows(
            Exception.class,
            () ->
                nodeServiceOptionsController.createNodeServiceOption(
                    testUtil.getNodeServiceOptionRequest()));

    Assertions.assertEquals("Failed to create node service option details", ex.getMessage());
    verify(nodeServiceOptionsService, times(1)).createNodeServiceOption(any());
  }

  @Test
  @DisplayName("When node service option is fetched successfully and response is 200 OK")
  void getNodeCarriersTest() throws CommonServiceException {
    when(nodeServiceOptionsService.getNodeServiceOption(any(), any(), any()))
        .thenReturn(testUtil.getNodeServiceOptionResponse());

    ResponseEntity<BaseResponse<NodeServiceOptionResponse>> response =
        nodeServiceOptionsController.getNodeServiceOption(
            TestUtil.NODE_ID, TestUtil.ORG_ID, TestUtil.SERVICE_OPTION);

    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    Assertions.assertEquals(
        TestUtil.NODE_ID, Objects.requireNonNull(response.getBody()).getPayload().getNodeId());
    Assertions.assertEquals(
        TestUtil.ORG_ID, Objects.requireNonNull(response.getBody()).getPayload().getOrgId());
    verify(nodeServiceOptionsService, times(1)).getNodeServiceOption(any(), any(), any());
  }

  @Test
  @DisplayName("When there is some error in fetching node service option")
  void getNodeCarriersExceptionTest() throws CommonServiceException {
    when(nodeServiceOptionsService.getNodeServiceOption(any(), any(), any()))
        .thenThrow(new RuntimeException("Failed to get node carrier details"));

    Exception ex =
        Assertions.assertThrows(
            Exception.class,
            () ->
                nodeServiceOptionsController.getNodeServiceOption(
                    TestUtil.NODE_ID, TestUtil.ORG_ID, TestUtil.SERVICE_OPTION));

    Assertions.assertEquals("Failed to get node carrier details", ex.getMessage());
    verify(nodeServiceOptionsService, times(1)).getNodeServiceOption(any(), any(), any());
  }

  @Test
  @DisplayName("When node service option is updated successfully and response is 200 OK")
  void updateNodeCarriersTest() throws CommonServiceException, InvalidDataException {
    when(nodeServiceOptionsService.updateNodeServiceOption(any(), any(), any(), any()))
        .thenReturn(testUtil.getNodeServiceOptionResponse());

    ResponseEntity<BaseResponse<NodeServiceOptionResponse>> response =
        nodeServiceOptionsController.updateNodeServiceOption(
            TestUtil.NODE_ID,
            TestUtil.ORG_ID,
            TestUtil.CARRIER_SERVICE_ID,
            testUtil.getNodeServiceOptionUpdateRequest());

    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    Assertions.assertEquals(
        TestUtil.NODE_ID, Objects.requireNonNull(response.getBody()).getPayload().getNodeId());
    Assertions.assertEquals(
        TestUtil.ORG_ID, Objects.requireNonNull(response.getBody()).getPayload().getOrgId());
    verify(nodeServiceOptionsService, times(1)).updateNodeServiceOption(any(), any(), any(), any());
  }

  @Test
  @DisplayName("When there is some error in updating node carrier")
  void updateNodeCarriersExceptionTest() throws CommonServiceException, InvalidDataException {
    when(nodeServiceOptionsService.updateNodeServiceOption(any(), any(), any(), any()))
        .thenThrow(new RuntimeException("Failed to update node carrier details"));

    Exception ex =
        Assertions.assertThrows(
            Exception.class,
            () ->
                nodeServiceOptionsController.updateNodeServiceOption(
                    TestUtil.ORG_ID,
                    TestUtil.NODE_ID,
                    TestUtil.CARRIER_SERVICE_ID,
                    testUtil.getNodeServiceOptionUpdateRequest()));

    Assertions.assertEquals("Failed to update node carrier details", ex.getMessage());
    verify(nodeServiceOptionsService, times(1)).updateNodeServiceOption(any(), any(), any(), any());
  }

  @Test
  @DisplayName("When node carrier is deleted successfully and response is 200 OK")
  void deleteNodeCarriersTest()
      throws NodeCarrierDomainException, CommonServiceException, InvalidDataException {
    when(nodeServiceOptionsService.deleteNodeServiceOption(any(), any(), any()))
        .thenReturn(testUtil.getNodeServiceOptionResponse());

    ResponseEntity<BaseResponse<NodeServiceOptionResponse>> response =
        nodeServiceOptionsController.deleteNodeServiceOption(
            TestUtil.NODE_ID, TestUtil.ORG_ID, TestUtil.SERVICE_OPTION);

    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    Assertions.assertEquals(
        TestUtil.NODE_ID, Objects.requireNonNull(response.getBody()).getPayload().getNodeId());
    Assertions.assertEquals(
        TestUtil.ORG_ID, Objects.requireNonNull(response.getBody()).getPayload().getOrgId());
    verify(nodeServiceOptionsService, times(1)).deleteNodeServiceOption(any(), any(), any());
  }

  @Test
  @DisplayName("When there is some error in deleting node carrier")
  void deleteNodeCarriersExceptionTest() throws CommonServiceException, InvalidDataException {
    when(nodeServiceOptionsService.deleteNodeServiceOption(any(), any(), any()))
        .thenThrow(new RuntimeException("Failed to delete node carrier details"));

    Exception ex =
        Assertions.assertThrows(
            Exception.class,
            () ->
                nodeServiceOptionsController.deleteNodeServiceOption(
                    TestUtil.NODE_ID, TestUtil.ORG_ID, TestUtil.SERVICE_OPTION));

    assertEquals("Failed to delete node carrier details", ex.getMessage());
    verify(nodeServiceOptionsService, times(1)).deleteNodeServiceOption(any(), any(), any());
  }

  @Test
  @DisplayName("When node service option list is fetched successfully and response is 200 OK")
  void getNodeServiceOptionsList_Success() throws CommonServiceException {

    List<NodeServiceOptionResponse> nodeServiceOptionResponseList =
        Arrays.asList(
            testUtil.getNodeServiceOptionResponse(), testUtil.getNodeServiceOptionResponse());
    when(nodeServiceOptionsService.getNodeServiceOptionList(TestUtil.ORG_ID, TestUtil.NODE_ID))
        .thenReturn(nodeServiceOptionResponseList);

    ResponseEntity<BaseResponse<List<NodeServiceOptionResponse>>> response =
        nodeServiceOptionsController.getNodeServiceOptionsList(TestUtil.ORG_ID, TestUtil.NODE_ID);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals("Node Service Option list fetched successfully", response.getBody().getMessage());
    assertEquals(nodeServiceOptionResponseList.size(), response.getBody().getPayload().size());
    verify(nodeServiceOptionsService, times(1))
        .getNodeServiceOptionList(TestUtil.ORG_ID, TestUtil.NODE_ID);
  }

  @Test
  @DisplayName("When there is some error in fetching node service option list")
  void getNodeServiceOptionsList_Exception() throws CommonServiceException {
    when(nodeServiceOptionsService.getNodeServiceOptionList(TestUtil.ORG_ID, TestUtil.NODE_ID))
        .thenThrow(new RuntimeException("Failed to get node service option list"));

    Exception ex =
        assertThrows(
            Exception.class,
            () -> {
              nodeServiceOptionsController.getNodeServiceOptionsList(
                  TestUtil.ORG_ID, TestUtil.NODE_ID);
            });
    assertEquals("Failed to get node service option list", ex.getMessage());
    verify(nodeServiceOptionsService, times(1))
        .getNodeServiceOptionList(TestUtil.ORG_ID, TestUtil.NODE_ID);
  }
}
