/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.promise.sourcing.rule.controller;

import static com.nextuple.promise.sourcing.rule.TestUtil.NODE_GROUP_ID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.exception.PromiseEngineException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.promise.sourcing.rule.TestUtil;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.NodePriorityRequest;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.NodePriorityUpdationRequest;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.NodeGroupDetailsResponse;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.NodeGroupWithNodesResponse;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.NodePriorityResponse;
import com.nextuple.promise.sourcing.rule.persistence.domain.NodePriorityDomainDto;
import com.nextuple.promise.sourcing.rule.service.NodePriorityService;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class NodePriorityControllerTest {

  @Mock private NodePriorityService nodePriorityService;
  @InjectMocks private NodePriorityController nodePriorityController;
  @InjectMocks private TestUtil testUtil;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void addNodePriorityToNodeGroupTest() throws PromiseEngineException, CommonServiceException {
    NodePriorityResponse nodePriorityResponse = testUtil.getNodePriorityResponse();
    when(nodePriorityService.processAddNodePriorityToNodeGroup(any(NodePriorityRequest.class)))
        .thenReturn(nodePriorityResponse);

    ResponseEntity<BaseResponse<NodePriorityResponse>> responseEntity =
        nodePriorityController.addNodePriorityToNodeGroup(testUtil.getNodePriorityRequest());

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals(nodePriorityResponse.getId(), responseEntity.getBody().getPayload().getId());

    verify(nodePriorityService, times(1))
        .processAddNodePriorityToNodeGroup(any(NodePriorityRequest.class));
  }

  @Test
  void addNodePriorityToNodeGroupExceptionTest()
      throws PromiseEngineException, CommonServiceException {
    when(nodePriorityService.processAddNodePriorityToNodeGroup(any(NodePriorityRequest.class)))
        .thenThrow(new RuntimeException("Error in adding node to node group"));

    Exception ex =
        assertThrows(
            Exception.class,
            () -> {
              nodePriorityController.addNodePriorityToNodeGroup(testUtil.getNodePriorityRequest());
            });
    assertEquals("Error in adding node to node group", ex.getMessage());
    verify(nodePriorityService, times(1))
        .processAddNodePriorityToNodeGroup(any(NodePriorityRequest.class));
  }

  @Test
  void getNodesAssociatedToANodeGroupTest() throws PromiseEngineException, CommonServiceException {
    NodeGroupDetailsResponse nodeGroupDetailsResponse = testUtil.getNodeGroupDetailsListResponse();
    when(nodePriorityService.processGetNodesAssociatedToANodeGroup(anyString(), anyLong()))
        .thenReturn(nodeGroupDetailsResponse);

    ResponseEntity<BaseResponse<NodeGroupDetailsResponse>> responseEntity =
        nodePriorityController.getNodesAssociatedToANodeGroup(TestUtil.ORG_ID, NODE_GROUP_ID);

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals(
        nodeGroupDetailsResponse.getNodeGroupId(),
        responseEntity.getBody().getPayload().getNodeGroupId());

    verify(nodePriorityService, times(1))
        .processGetNodesAssociatedToANodeGroup(anyString(), anyLong());
  }

  @Test
  void getNodesAssociatedToANodeGroupExceptionTest()
      throws PromiseEngineException, CommonServiceException {
    when(nodePriorityService.processGetNodesAssociatedToANodeGroup(anyString(), anyLong()))
        .thenThrow(new RuntimeException("Error in getting nodes associated to a node group"));

    Exception ex =
        assertThrows(
            Exception.class,
            () -> {
              nodePriorityController.getNodesAssociatedToANodeGroup(TestUtil.ORG_ID, NODE_GROUP_ID);
            });
    assertEquals("Error in getting nodes associated to a node group", ex.getMessage());
    verify(nodePriorityService, times(1))
        .processGetNodesAssociatedToANodeGroup(anyString(), anyLong());
  }

  @Test
  void getNodePriorityDetailsByIdTest() throws PromiseEngineException, CommonServiceException {
    NodePriorityResponse nodePriorityResponse = testUtil.getNodePriorityResponse();
    when(nodePriorityService.processGetNodePriorityDetailsByIdAndOrgId(anyLong(), anyString()))
        .thenReturn(nodePriorityResponse);

    ResponseEntity<BaseResponse<NodePriorityResponse>> responseEntity =
        nodePriorityController.getNodePriorityDetailsByOrgIdAndId(
            TestUtil.ORG_ID, TestUtil.NODE_GROUP_DETAIL_ID);

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals(nodePriorityResponse.getId(), responseEntity.getBody().getPayload().getId());

    verify(nodePriorityService, times(1))
        .processGetNodePriorityDetailsByIdAndOrgId(anyLong(), anyString());
  }

  @Test
  void getNodeGroupExceptionTest() throws PromiseEngineException, CommonServiceException {
    when(nodePriorityService.processGetNodePriorityDetailsByIdAndOrgId(anyLong(), anyString()))
        .thenThrow(new RuntimeException("Error in fetching node priority details by id"));

    Exception ex =
        assertThrows(
            Exception.class,
            () -> {
              nodePriorityController.getNodePriorityDetailsByOrgIdAndId(
                  TestUtil.ORG_ID, TestUtil.NODE_GROUP_DETAIL_ID);
            });
    assertEquals("Error in fetching node priority details by id", ex.getMessage());
    verify(nodePriorityService, times(1))
        .processGetNodePriorityDetailsByIdAndOrgId(anyLong(), anyString());
  }

  @Test
  void updatePriorityWithinNodeGroupTest() throws PromiseEngineException, CommonServiceException {
    NodePriorityResponse nodePriorityResponse = testUtil.getUpdatedNodePriorityResponse(100);
    when(nodePriorityService.processUpdateNodePriorityWithinNodeGroup(
            anyString(), anyLong(), anyString(), any(NodePriorityUpdationRequest.class)))
        .thenReturn(nodePriorityResponse);

    ResponseEntity<BaseResponse<NodePriorityResponse>> responseEntity =
        nodePriorityController.updatePriorityWithinNodeGroup(
            TestUtil.ORG_ID,
            NODE_GROUP_ID,
            TestUtil.NODE_ID,
            testUtil.getNodePriorityUpdationRequest(100));

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals(nodePriorityResponse.getId(), responseEntity.getBody().getPayload().getId());

    verify(nodePriorityService, times(1))
        .processUpdateNodePriorityWithinNodeGroup(
            anyString(), anyLong(), anyString(), any(NodePriorityUpdationRequest.class));
  }

  @Test
  void updatePriorityWithinNodeGroupExceptionTest()
      throws PromiseEngineException, CommonServiceException {
    when(nodePriorityService.processUpdateNodePriorityWithinNodeGroup(
            anyString(), anyLong(), anyString(), any(NodePriorityUpdationRequest.class)))
        .thenThrow(new RuntimeException("Error in updating node priority"));

    Exception ex =
        assertThrows(
            Exception.class,
            () -> {
              nodePriorityController.updatePriorityWithinNodeGroup(
                  TestUtil.ORG_ID,
                  NODE_GROUP_ID,
                  TestUtil.NODE_ID,
                  testUtil.getNodePriorityUpdationRequest(100));
            });
    assertEquals("Error in updating node priority", ex.getMessage());
    verify(nodePriorityService, times(1))
        .processUpdateNodePriorityWithinNodeGroup(
            anyString(), anyLong(), anyString(), any(NodePriorityUpdationRequest.class));
  }

  @Test
  void removeNodeFromNodeGroupTest() throws PromiseEngineException, CommonServiceException {
    NodePriorityResponse nodePriorityResponse = testUtil.getNodePriorityResponse();
    when(nodePriorityService.processRemoveNodeFromNodeGroup(anyString(), anyLong(), anyString()))
        .thenReturn(nodePriorityResponse);

    ResponseEntity<BaseResponse<NodePriorityResponse>> responseEntity =
        nodePriorityController.removeNodeFromNodeGroup(
            TestUtil.ORG_ID, NODE_GROUP_ID, TestUtil.NODE_ID);

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals(nodePriorityResponse.getId(), responseEntity.getBody().getPayload().getId());

    verify(nodePriorityService, times(1))
        .processRemoveNodeFromNodeGroup(anyString(), anyLong(), anyString());
  }

  @Test
  void removeNodeFromNodeGroupExceptionTest()
      throws PromiseEngineException, CommonServiceException {
    when(nodePriorityService.processRemoveNodeFromNodeGroup(anyString(), anyLong(), anyString()))
        .thenThrow(new RuntimeException("Error in removing node from node group"));

    Exception ex =
        assertThrows(
            Exception.class,
            () -> {
              nodePriorityController.removeNodeFromNodeGroup(
                  TestUtil.ORG_ID, NODE_GROUP_ID, TestUtil.NODE_ID);
            });
    assertEquals("Error in removing node from node group", ex.getMessage());
    verify(nodePriorityService, times(1))
        .processRemoveNodeFromNodeGroup(anyString(), anyLong(), anyString());
  }

  @Test
  void updateNodesWithinNodeGroupTest() throws PromiseEngineException, CommonServiceException {
    NodeGroupWithNodesResponse response = testUtil.getNodeGroupWithNodesResponse();
    when(nodePriorityService.editNodesWithinNodeGroup(any(), any(), any())).thenReturn(response);

    ResponseEntity<BaseResponse<NodeGroupWithNodesResponse>> responseEntity =
        nodePriorityController.updateNodesWithinNodeGroup("NEXTUPLE", "ng-1", response);
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals(
        response.getNodeGroupName(), responseEntity.getBody().getPayload().getNodeGroupName());

    verify(nodePriorityService, times(1)).editNodesWithinNodeGroup(any(), any(), any());
  }

  @Test
  void updateNodesWithinNodeGroupExceptionTest()
      throws CommonServiceException, PromiseEngineException {
    NodeGroupWithNodesResponse response = testUtil.getNodeGroupWithNodesResponse();

    when(nodePriorityService.editNodesWithinNodeGroup(any(), any(), any()))
        .thenThrow(new RuntimeException("Error in adding nodes to node group"));

    Exception ex =
        assertThrows(
            Exception.class,
            () -> {
              nodePriorityController.updateNodesWithinNodeGroup("NEXTUPLE", "ng-1", response);
            });
    assertEquals("Error in adding nodes to node group", ex.getMessage());

    verify(nodePriorityService, times(1)).editNodesWithinNodeGroup(any(), any(), any());
  }

  @Test
  @DisplayName("Remove all nodes from the Nodegroup - Happy path")
  void removeAllNodesFromTheNodeGroup() throws PromiseEngineException, CommonServiceException {
    List<NodePriorityDomainDto> nodePriorityEntityList = List.of(testUtil.getNodePriorityEntity());
    when(nodePriorityService.deleteNodesFromTheNodeGroup(any(), any()))
        .thenReturn(nodePriorityEntityList);
    ResponseEntity<BaseResponse<List<NodePriorityDomainDto>>> response =
        nodePriorityController.removeAllNodesFromTheNodeGroup("NEXTUPLE", NODE_GROUP_ID);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(
        nodePriorityEntityList.get(0).getNodeId(),
        response.getBody().getPayload().get(0).getNodeId());
    assertEquals(
        nodePriorityEntityList.get(0).getNodeGroupId(),
        response.getBody().getPayload().get(0).getNodeGroupId());
    verify(nodePriorityService, times(1)).deleteNodesFromTheNodeGroup(any(), any());
  }

  @Test
  @DisplayName("Remove all nodes from the Nodegroup - Exception scenario")
  void removeAllNodesFromTheNodeGroupExceptionTest()
      throws CommonServiceException, PromiseEngineException {
    when(nodePriorityService.deleteNodesFromTheNodeGroup(any(), any()))
        .thenThrow(new RuntimeException("Failed to process remove node from node group request"));
    Exception ex =
        assertThrows(
            Exception.class,
            () -> {
              nodePriorityController.removeAllNodesFromTheNodeGroup("NEXTUPLE", NODE_GROUP_ID);
            });
    assertEquals("Failed to process remove node from node group request", ex.getMessage());
    verify(nodePriorityService, times(1)).deleteNodesFromTheNodeGroup(any(), any());
  }

  @Test
  @DisplayName("Fetch node priority details by nodeId and orgId")
  void fetchNodePriorityByNodeIdAndOrgIdTest()
      throws PromiseEngineException, CommonServiceException {
    List<NodePriorityResponse> expected = List.of(testUtil.getNodePriorityResponse());
    when(nodePriorityService.getNodePriorityListByNodeIdAndOrgId(anyString(), anyString()))
        .thenReturn(expected);
    ResponseEntity<BaseResponse<List<NodePriorityResponse>>> responses =
        nodePriorityController.getNodePriorityDetailsByOrgIdAndNodeId(
            TestUtil.ORG_ID, TestUtil.NODE_ID);
    Assertions.assertEquals(1, responses.getBody().getPayload().size());
  }

  @Test
  @DisplayName("Fetch node priority details by nodeId and orgId - Exception scenario")
  void fetchNodePriorityByNodeIdAndOrgIdExceptionTest()
      throws PromiseEngineException, CommonServiceException {
    String errorMessage = "Error occurred";
    when(nodePriorityService.getNodePriorityListByNodeIdAndOrgId(anyString(), anyString()))
        .thenThrow(new RuntimeException(errorMessage));
    RuntimeException e =
        Assertions.assertThrows(
            RuntimeException.class,
            () ->
                nodePriorityController.getNodePriorityDetailsByOrgIdAndNodeId(
                    TestUtil.ORG_ID, TestUtil.NODE_ID));
    Assertions.assertEquals(errorMessage, e.getMessage());
  }
}
