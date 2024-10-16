/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.promise.sourcing.rule.controller;

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
import com.nextuple.promise.sourcing.rule.api.domain.inbound.CreateNodeGroupRequest;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.UpdateNodeGroupRequest;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.NodeGroupResponse;
import com.nextuple.promise.sourcing.rule.persistence.domain.NodeGroupDomainDto;
import com.nextuple.promise.sourcing.rule.service.NodeGroupService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class NodeGroupControllerTest {

  @Mock private NodeGroupService nodeGroupService;
  @InjectMocks private NodeGroupController nodeGroupController;
  @InjectMocks private TestUtil testUtil;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void createNodeGroupTest() throws PromiseEngineException, CommonServiceException {
    NodeGroupResponse nodeGroupResponse = testUtil.getNodeGroupResponse();
    when(nodeGroupService.createNodeGroup(any(CreateNodeGroupRequest.class)))
        .thenReturn(nodeGroupResponse);

    ResponseEntity<BaseResponse<NodeGroupResponse>> responseEntity =
        nodeGroupController.createNodeGroup(testUtil.getCreateNodeGroupRequest());

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals(nodeGroupResponse.getId(), responseEntity.getBody().getPayload().getId());

    verify(nodeGroupService, times(1)).createNodeGroup(any(CreateNodeGroupRequest.class));
  }

  @Test
  void createNodeGroupExceptionTest() throws PromiseEngineException, CommonServiceException {
    when(nodeGroupService.createNodeGroup(any(CreateNodeGroupRequest.class)))
        .thenThrow(new RuntimeException("Error in creating node group"));

    Exception ex =
        assertThrows(
            Exception.class,
            () -> {
              nodeGroupController.createNodeGroup(testUtil.getCreateNodeGroupRequest());
            });
    assertEquals("Error in creating node group", ex.getMessage());
    verify(nodeGroupService, times(1)).createNodeGroup(any(CreateNodeGroupRequest.class));
  }

  @Test
  void getNodeGroupByIdandOrgIdTest() throws PromiseEngineException, CommonServiceException {
    NodeGroupResponse nodeGroupResponse = testUtil.getNodeGroupResponse();
    when(nodeGroupService.fetchNodeGroupByIdandOrgId(anyLong(), anyString()))
        .thenReturn(nodeGroupResponse);

    ResponseEntity<BaseResponse<NodeGroupResponse>> responseEntity =
        nodeGroupController.getNodeGroupByIdandOrgId(TestUtil.NODE_GROUP_ID, TestUtil.ORG_ID);

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals(nodeGroupResponse.getId(), responseEntity.getBody().getPayload().getId());

    verify(nodeGroupService, times(1)).fetchNodeGroupByIdandOrgId(anyLong(), anyString());
  }

  @Test
  void getNodeGroupByIdandOrgIdExceptionTest()
      throws PromiseEngineException, CommonServiceException {
    when(nodeGroupService.fetchNodeGroupByIdandOrgId(anyLong(), anyString()))
        .thenThrow(new RuntimeException("Error in fetching node group"));

    Exception ex =
        assertThrows(
            Exception.class,
            () -> {
              nodeGroupController.getNodeGroupByIdandOrgId(TestUtil.NODE_GROUP_ID, TestUtil.ORG_ID);
            });
    assertEquals("Error in fetching node group", ex.getMessage());
    verify(nodeGroupService, times(1)).fetchNodeGroupByIdandOrgId(anyLong(), anyString());
  }

  @Test
  void updateNodeGroupTest() throws PromiseEngineException, CommonServiceException {
    NodeGroupResponse nodeGroupResponse =
        testUtil.getUpdatedNodeGroupResponse("NG2", "Node Group 2");
    when(nodeGroupService.updateNodeGroup(
            anyLong(), anyString(), any(UpdateNodeGroupRequest.class)))
        .thenReturn(nodeGroupResponse);

    ResponseEntity<BaseResponse<NodeGroupResponse>> responseEntity =
        nodeGroupController.updateNodeGroup(
            TestUtil.NODE_GROUP_ID,
            TestUtil.ORG_ID,
            testUtil.getUpdateNodeGroupRequest("NG2", "Node Group 2"));

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals(nodeGroupResponse.getId(), responseEntity.getBody().getPayload().getId());

    verify(nodeGroupService, times(1))
        .updateNodeGroup(anyLong(), anyString(), any(UpdateNodeGroupRequest.class));
  }

  @Test
  void updateNodeGroupExceptionTest() throws PromiseEngineException, CommonServiceException {
    when(nodeGroupService.updateNodeGroup(
            anyLong(), anyString(), any(UpdateNodeGroupRequest.class)))
        .thenThrow(new RuntimeException("Error in updating node group"));

    Exception ex =
        assertThrows(
            Exception.class,
            () -> {
              nodeGroupController.updateNodeGroup(
                  TestUtil.NODE_GROUP_ID,
                  TestUtil.ORG_ID,
                  testUtil.getUpdateNodeGroupRequest("NG2", "Node Group 2"));
            });
    assertEquals("Error in updating node group", ex.getMessage());
    verify(nodeGroupService, times(1))
        .updateNodeGroup(anyLong(), anyString(), any(UpdateNodeGroupRequest.class));
  }

  @Test
  @DisplayName("Get NodeGroup List By OrgID - Happy Path")
  void getNodeGroupListByOrgIdTest() throws PromiseEngineException, CommonServiceException {
    List<NodeGroupDomainDto> nodeGroupResponseList = List.of(testUtil.getNodeGroupEntity());
    when(nodeGroupService.getNodeGroupListForOrgId(anyString())).thenReturn(nodeGroupResponseList);
    ResponseEntity<BaseResponse<List<NodeGroupDomainDto>>> response =
        nodeGroupController.getNodeGroupListByOrgId(TestUtil.ORG_ID);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(
        nodeGroupResponseList.get(0).getId(), response.getBody().getPayload().get(0).getId());
    verify(nodeGroupService, times(1)).getNodeGroupListForOrgId(anyString());
  }

  @Test
  @DisplayName("Get NodeGroup List By orgId - Exception scenario")
  void getNodeGroupListByOrgIdTestExceptionTest()
      throws PromiseEngineException, CommonServiceException {
    when(nodeGroupService.getNodeGroupListForOrgId(anyString()))
        .thenThrow(new RuntimeException("Failed to process get node group list request"));
    Exception ex =
        assertThrows(
            Exception.class,
            () -> {
              nodeGroupController.getNodeGroupListByOrgId(TestUtil.ORG_ID);
            });
    assertEquals("Failed to process get node group list request", ex.getMessage());
    verify(nodeGroupService, times(1)).getNodeGroupListForOrgId(anyString());
  }
}
