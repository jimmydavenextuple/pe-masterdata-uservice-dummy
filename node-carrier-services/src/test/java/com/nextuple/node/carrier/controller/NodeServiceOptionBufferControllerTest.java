/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.node.carrier.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.node.carrier.TestUtil;
import com.nextuple.node.carrier.domain.outbound.NodeServiceOptionBufferResponse;
import com.nextuple.node.carrier.service.impl.NodeServiceOptionBufferServiceImpl;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class NodeServiceOptionBufferControllerTest {
  @InjectMocks NodeServiceOptionBufferController nodeServiceOptionBufferController;
  @InjectMocks TestUtil testUtil;
  @Mock NodeServiceOptionBufferServiceImpl nodeServiceOptionBufferService;

  @Test
  @DisplayName("Successful Execution - Create NodeServiceOption Buffer")
  void createNodeServiceOptionBufferTest() throws CommonServiceException {
    when(nodeServiceOptionBufferService.createNodeServiceOptionBuffer(any()))
        .thenReturn(testUtil.getNodeServiceOptionBufferResponse());
    ResponseEntity<BaseResponse<NodeServiceOptionBufferResponse>> response =
        nodeServiceOptionBufferController.createNodeServiceOptionBuffer(
            testUtil.getNodeServiceOptionBufferRequest());
    assertNotNull(response);
    assertEquals(
        "Node service option buffer created successfully", response.getBody().getMessage());
    verify(nodeServiceOptionBufferService, times(1)).createNodeServiceOptionBuffer(any());
  }

  @Test
  @DisplayName("Successful Execution - Get NodeServiceOption Buffer")
  void getNodeServiceOptionBufferTest() throws CommonServiceException {
    when(nodeServiceOptionBufferService.fetchNodeServiceOptionBuffer(any(), any()))
        .thenReturn(testUtil.getNodeServiceOptionBufferResponse());
    ResponseEntity<BaseResponse<NodeServiceOptionBufferResponse>> response =
        nodeServiceOptionBufferController.fetchNodeServiceOptionBuffer(TestUtil.ORG_ID, 1L);
    assertNotNull(response);
    assertEquals(
        "Node service option buffer fetched successfully", response.getBody().getMessage());
    verify(nodeServiceOptionBufferService, times(1)).fetchNodeServiceOptionBuffer(any(), any());
  }

  @Test
  @DisplayName("Successful Execution - Fetch Applicable Node ServiceOption Buffers")
  void fetchApplicableNodeServiceOptionBuffersTest() throws CommonServiceException {
    when(nodeServiceOptionBufferService.fetchApplicableNodeServiceOptionBuffers(
            any(), any(), any(), any(), any()))
        .thenReturn(List.of(testUtil.getNodeServiceOptionBufferResponse()));
    ResponseEntity<BaseResponse<List<NodeServiceOptionBufferResponse>>> response =
        nodeServiceOptionBufferController.fetchApplicableNodeServiceOptionBuffers(
            TestUtil.ORG_ID, TestUtil.NODE_ID, TestUtil.SERVICE_OPTION, LocalDate.now(), 7);
    assertNotNull(response);
    assertEquals(
        "Node service option buffer fetched successfully", response.getBody().getMessage());
    verify(nodeServiceOptionBufferService, times(1))
        .fetchApplicableNodeServiceOptionBuffers(any(), any(), any(), any(), any());
  }

  @Test
  @DisplayName("Successful Execution - Update Node ServiceOption Buffer")
  void updateNodeServiceOptionBufferTest() throws CommonServiceException {
    when(nodeServiceOptionBufferService.updateNodeServiceOptionBuffer(any(), any(), any()))
        .thenReturn(testUtil.getNodeServiceOptionBufferResponse());
    ResponseEntity<BaseResponse<NodeServiceOptionBufferResponse>> response =
        nodeServiceOptionBufferController.updateNodeServiceOptionBuffer(
            TestUtil.ORG_ID, 1L, testUtil.getNodeServiceOptionBufferUpdateRequest());
    assertNotNull(response);
    assertEquals(
        "Node service option buffer updated successfully", response.getBody().getMessage());
    verify(nodeServiceOptionBufferService, times(1))
        .updateNodeServiceOptionBuffer(any(), any(), any());
  }

  @Test
  @DisplayName("Successful Execution - Delete Node ServiceOption Buffer")
  void deleteNodeServiceOptionBufferTest() throws CommonServiceException {
    when(nodeServiceOptionBufferService.deleteNodeServiceOptionBuffer(any()))
        .thenReturn(testUtil.getNodeServiceOptionBufferResponse());
    ResponseEntity<BaseResponse<NodeServiceOptionBufferResponse>> response =
        nodeServiceOptionBufferController.deleteNodeServiceOptionBuffer(
            testUtil.getNodeServiceOptionBufferDeleteRequest());
    assertNotNull(response);
    assertEquals(
        "Node service option buffer deleted successfully", response.getBody().getMessage());
    verify(nodeServiceOptionBufferService, times(1)).deleteNodeServiceOptionBuffer(any());
  }

  @Test
  @DisplayName("Successful Execution - Delete Node ServiceOption Buffer by orgId and nodeId")
  void deleteNodeServiceOptionBufferTest2() throws CommonServiceException {
    when(nodeServiceOptionBufferService.deleteNodeServiceOptionBufferByOrgIdAndId(any(), any()))
        .thenReturn(testUtil.getNodeServiceOptionBufferResponse());
    ResponseEntity<BaseResponse<NodeServiceOptionBufferResponse>> response =
        nodeServiceOptionBufferController.deleteNodeServiceOptionBuffer(TestUtil.ORG_ID, 1L);
    assertNotNull(response);
    assertEquals(
        "Node service option buffer deleted successfully", response.getBody().getMessage());
    verify(nodeServiceOptionBufferService, times(1))
        .deleteNodeServiceOptionBufferByOrgIdAndId(any(), any());
  }

  @Test
  @DisplayName("Successful Execution - Get Buffers By OrgId And NodeId And ServiceOption")
  void getBuffersByOrgIdAndNodeIdAndServiceOption_Success() throws CommonServiceException {
    List<NodeServiceOptionBufferResponse> bufferResponses = new ArrayList<>();
    bufferResponses.add(testUtil.getNodeServiceOptionBufferResponse());
    when(nodeServiceOptionBufferService.getBuffersByOrgIdAndNodeIdAndServiceOption(
            any(), any(), any()))
        .thenReturn(bufferResponses);
    ResponseEntity<BaseResponse<List<NodeServiceOptionBufferResponse>>> response =
        nodeServiceOptionBufferController.getBuffersByOrgIdAndNodeIdAndServiceOption(
            TestUtil.ORG_ID, TestUtil.NODE_ID, TestUtil.SERVICE_OPTION);
    assertNotNull(response);
    assertEquals(200, response.getStatusCodeValue());
    assertEquals(
        "Node Service option buffer list fetched successfully", response.getBody().getMessage());
    assertFalse(response.getBody().getPayload().isEmpty());
    assertEquals(1, response.getBody().getPayload().size());
    verify(nodeServiceOptionBufferService, times(1))
        .getBuffersByOrgIdAndNodeIdAndServiceOption(any(), any(), any());
  }
}
