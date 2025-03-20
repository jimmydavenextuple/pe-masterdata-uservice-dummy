/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.promise.sourcing.rule.service;

import static com.nextuple.promise.sourcing.rule.TestUtil.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.exception.PromiseEngineException;
import com.nextuple.node.domain.feign.NodeFeign;
import com.nextuple.promise.sourcing.rule.TestUtil;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.NodePriorityRequest;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.NodeGroupDetailsResponse;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.NodeGroupWithNodesResponse;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.NodePriorityResponse;
import com.nextuple.promise.sourcing.rule.api.domain.pojo.NodePriorityInfo;
import com.nextuple.promise.sourcing.rule.persistence.domain.NodeGroupDomainDto;
import com.nextuple.promise.sourcing.rule.persistence.domain.NodePriorityDomainDto;
import com.nextuple.promise.sourcing.rule.persistence.service.NodeGroupPersistenceService;
import com.nextuple.promise.sourcing.rule.persistence.service.NodePriorityPersistenceService;
import feign.FeignException.BadRequest;
import feign.Request;
import feign.Request.HttpMethod;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;

class NodePriorityServiceTest {
  @InjectMocks private NodePriorityService nodePriorityService;
  @Mock private NodePriorityPersistenceService nodePriorityPersistenceService;
  @Mock private NodeGroupPersistenceService nodeGroupPersistenceService;
  @Mock private NodeFeign nodeFeign;

  @InjectMocks private TestUtil testUtil;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void processAddNodePriorityToNodeGroupTest()
      throws PromiseEngineException, CommonServiceException {
    NodePriorityRequest nodePriorityRequest = testUtil.getNodePriorityRequest();
    when(nodeGroupPersistenceService.fetchNodeGroupByIdAndOrgId(anyLong(), anyString()))
        .thenReturn(Optional.ofNullable(testUtil.getNodeGroupEntity()));
    when(nodeFeign.getNodeDetails(anyString(), anyString()))
        .thenReturn(testUtil.getBaseResponseOfNode());
    when(nodePriorityPersistenceService.fetchNodePriorityListByOrgIdAndNodeGroupIdAndNodeId(
            anyString(), anyLong(), anyString()))
        .thenReturn(List.of());
    when(nodePriorityPersistenceService.saveNodePriorityEntity(any(NodePriorityDomainDto.class)))
        .thenReturn(testUtil.getNodePriorityEntity());

    NodePriorityResponse nodePriorityResponse =
        nodePriorityService.processAddNodePriorityToNodeGroup(nodePriorityRequest);
    assertEquals(testUtil.getNodeGroupEntity().getId(), nodePriorityResponse.getId());
    assertEquals(CUSTOM_ATTRIBUTES, nodePriorityResponse.getCustomAttributes());

    verify(nodeGroupPersistenceService, times(1))
        .fetchNodeGroupByIdAndOrgId(anyLong(), anyString());
    verify(nodeFeign, times(1)).getNodeDetails(anyString(), anyString());
    verify(nodePriorityPersistenceService, times(1))
        .saveNodePriorityEntity(any(NodePriorityDomainDto.class));
    verify(nodePriorityPersistenceService, times(1)).saveNodePriorityEntity(any());
  }

  @Test
  void processAddNodePriorityToNodeGroupExceptionTest1() throws PromiseEngineException {
    NodePriorityRequest nodePriorityRequest = testUtil.getNodePriorityRequest();
    when(nodeGroupPersistenceService.fetchNodeGroupByIdAndOrgId(anyLong(), anyString()))
        .thenReturn(Optional.empty());

    Exception ex =
        assertThrows(
            Exception.class,
            () -> {
              nodePriorityService.processAddNodePriorityToNodeGroup(nodePriorityRequest);
            });

    assertEquals("Node Group and OrgId does not exist", ex.getMessage());
    verify(nodeGroupPersistenceService, times(1))
        .fetchNodeGroupByIdAndOrgId(anyLong(), anyString());
  }

  @Test
  void processAddNodePriorityToNodeGroupExceptionTest2() throws PromiseEngineException {
    NodePriorityRequest nodePriorityRequest = testUtil.getNodePriorityRequest();
    when(nodeGroupPersistenceService.fetchNodeGroupByIdAndOrgId(anyLong(), anyString()))
        .thenReturn(Optional.ofNullable(testUtil.getNodeGroupEntity()));
    when(nodeFeign.getNodeDetails(anyString(), anyString()))
        .thenReturn(testUtil.getBaseResponseOfNode2());

    Exception ex =
        assertThrows(
            Exception.class,
            () -> {
              nodePriorityService.processAddNodePriorityToNodeGroup(nodePriorityRequest);
            });

    assertEquals("Cannot add node to node group as node is inactive", ex.getMessage());
    verify(nodeGroupPersistenceService, times(1))
        .fetchNodeGroupByIdAndOrgId(anyLong(), anyString());
    verify(nodeFeign, times(1)).getNodeDetails(anyString(), anyString());
  }

  @Test
  void processAddNodePriorityToNodeGroupExceptionTest3() throws PromiseEngineException {
    NodePriorityRequest nodePriorityRequest = testUtil.getNodePriorityRequest();
    Map<String, Collection<String>> headers = new HashMap<>();
    when(nodeGroupPersistenceService.fetchNodeGroupByIdAndOrgId(anyLong(), anyString()))
        .thenReturn(Optional.ofNullable(testUtil.getNodeGroupEntity()));
    when(nodeFeign.getNodeDetails(anyString(), anyString()))
        .thenThrow(
            new BadRequest(
                "error in updating order and bag",
                Request.create(HttpMethod.GET, "", new HashMap<>(), null, null, null),
                TestUtil.getNodeNotFoundError.getBytes(StandardCharsets.UTF_8),
                headers));

    Exception ex =
        assertThrows(
            Exception.class,
            () -> {
              nodePriorityService.processAddNodePriorityToNodeGroup(nodePriorityRequest);
            });

    assertEquals("Invalid node id", ex.getMessage());
    verify(nodeGroupPersistenceService, times(1))
        .fetchNodeGroupByIdAndOrgId(anyLong(), anyString());
    verify(nodeFeign, times(1)).getNodeDetails(anyString(), anyString());
  }

  @Test
  void processAddNodePriorityToNodeGroupExceptionTest4() throws PromiseEngineException {
    NodePriorityRequest nodePriorityRequest = testUtil.getNodePriorityRequest();
    when(nodeGroupPersistenceService.fetchNodeGroupByIdAndOrgId(anyLong(), anyString()))
        .thenReturn(Optional.ofNullable(testUtil.getNodeGroupEntity()));
    when(nodeFeign.getNodeDetails(anyString(), anyString()))
        .thenReturn(testUtil.getBaseResponseOfNode());
    when(nodePriorityPersistenceService.fetchNodePriorityListByOrgIdAndNodeGroupIdAndNodeId(
            anyString(), anyLong(), anyString()))
        .thenReturn(List.of(testUtil.getNodePriorityEntity()));

    assertDoesNotThrow(
        () -> {
          nodePriorityService.processAddNodePriorityToNodeGroup(nodePriorityRequest);
        });
    verify(nodeGroupPersistenceService, times(1))
        .fetchNodeGroupByIdAndOrgId(anyLong(), anyString());
    verify(nodeFeign, times(1)).getNodeDetails(anyString(), anyString());
    verify(nodePriorityPersistenceService, times(1)).saveNodePriorityEntity(any());
  }

  @Test
  void processAddNodePriorityToNodeGroupExceptionTest5() throws PromiseEngineException {
    NodePriorityRequest nodePriorityRequest = testUtil.getNodePriorityRequest();
    nodePriorityRequest.setOrgId("BCA");
    when(nodeGroupPersistenceService.fetchNodeGroupByIdAndOrgId(anyLong(), anyString()))
        .thenReturn(Optional.empty());
    Exception ex =
        assertThrows(
            Exception.class,
            () -> {
              nodePriorityService.processAddNodePriorityToNodeGroup(nodePriorityRequest);
            });
    assertEquals("Node Group and OrgId does not exist", ex.getMessage());
    verify(nodeGroupPersistenceService, times(1))
        .fetchNodeGroupByIdAndOrgId(anyLong(), anyString());
  }

  @Test
  void processGetNodesAssociatedToANodeGroupTest1()
      throws PromiseEngineException, CommonServiceException {
    when(nodeGroupPersistenceService.fetchNodeGroupByIdAndOrgId(anyLong(), anyString()))
        .thenReturn(Optional.ofNullable(testUtil.getNodeGroupEntity()));
    when(nodePriorityPersistenceService.fetchNodePriorityListByOrgIdAndNodeGroupId(
            anyString(), anyLong()))
        .thenReturn(List.of(testUtil.getNodePriorityEntity()));

    NodeGroupDetailsResponse nodeGroupDetailsResponse =
        nodePriorityService.processGetNodesAssociatedToANodeGroup(TestUtil.ORG_ID, NODE_GROUP_ID);
    assertEquals(
        testUtil.getNodePriorityEntity().getNodeGroupId(),
        nodeGroupDetailsResponse.getNodeGroupId());

    verify(nodeGroupPersistenceService, times(1))
        .fetchNodeGroupByIdAndOrgId(anyLong(), anyString());
    verify(nodePriorityPersistenceService, times(1))
        .fetchNodePriorityListByOrgIdAndNodeGroupId(anyString(), anyLong());
  }

  @Test
  void processGetNodesAssociatedToANodeGroupTest2()
      throws PromiseEngineException, CommonServiceException {
    when(nodeGroupPersistenceService.fetchNodeGroupByIdAndOrgId(anyLong(), anyString()))
        .thenReturn(Optional.ofNullable(testUtil.getNodeGroupEntity()));
    when(nodePriorityPersistenceService.fetchNodePriorityListByOrgIdAndNodeGroupId(
            anyString(), anyLong()))
        .thenReturn(List.of());

    Exception ex =
        assertThrows(
            Exception.class,
            () -> {
              nodePriorityService.processGetNodesAssociatedToANodeGroup(
                  TestUtil.ORG_ID, NODE_GROUP_ID);
            });
    assertEquals("No nodes associated to the given node group", ex.getMessage());

    verify(nodeGroupPersistenceService, times(1))
        .fetchNodeGroupByIdAndOrgId(anyLong(), anyString());
    verify(nodePriorityPersistenceService, times(1))
        .fetchNodePriorityListByOrgIdAndNodeGroupId(anyString(), anyLong());
  }

  @Test
  void processGetNodePriorityDetailsByIdTest()
      throws PromiseEngineException, CommonServiceException {
    when(nodePriorityPersistenceService.fetchNodePriorityEntityByIdAndOrgId(anyLong(), anyString()))
        .thenReturn(Optional.ofNullable(testUtil.getNodePriorityEntity()));

    NodePriorityResponse nodePriorityResponse =
        nodePriorityService.processGetNodePriorityDetailsByIdAndOrgId(
            TestUtil.NODE_GROUP_DETAIL_ID, TestUtil.ORG_ID);
    assertEquals(testUtil.getNodePriorityEntity().getId(), nodePriorityResponse.getId());

    verify(nodePriorityPersistenceService, times(1))
        .fetchNodePriorityEntityByIdAndOrgId(anyLong(), anyString());
  }

  @Test
  void processGetNodePriorityDetailsByIdExceptionTest() throws PromiseEngineException {
    when(nodePriorityPersistenceService.fetchNodePriorityEntityByIdAndOrgId(anyLong(), anyString()))
        .thenReturn(Optional.empty());

    Exception ex =
        assertThrows(
            Exception.class,
            () -> {
              nodePriorityService.processGetNodePriorityDetailsByIdAndOrgId(
                  TestUtil.NODE_GROUP_DETAIL_ID, TestUtil.ORG_ID);
            });

    assertEquals("Node group details not found for given id and orgId", ex.getMessage());

    verify(nodePriorityPersistenceService, times(1))
        .fetchNodePriorityEntityByIdAndOrgId(anyLong(), anyString());
  }

  @Test
  void processUpdateNodePriorityWithinNodeGroupTest()
      throws PromiseEngineException, CommonServiceException {
    NodePriorityDomainDto updatedNodePriorityEntity = testUtil.getNodePriorityEntity();
    updatedNodePriorityEntity.setPriority(100);
    when(nodePriorityPersistenceService.fetchNodePriorityListByOrgIdAndNodeGroupIdAndNodeId(
            anyString(), anyLong(), anyString()))
        .thenReturn(List.of(testUtil.getNodePriorityEntity()));
    when(nodePriorityPersistenceService.saveNodePriorityEntity(any(NodePriorityDomainDto.class)))
        .thenReturn(updatedNodePriorityEntity);

    NodePriorityResponse nodePriorityResponse =
        nodePriorityService.processUpdateNodePriorityWithinNodeGroup(
            TestUtil.ORG_ID,
            NODE_GROUP_ID,
            TestUtil.NODE_ID,
            testUtil.getNodePriorityUpdationRequest(100));
    assertEquals(
        testUtil.getUpdatedNodePriorityResponse(100).getPriority(),
        nodePriorityResponse.getPriority());
    assertEquals(
        updatedNodePriorityEntity.getCustomAttributes(),
        nodePriorityResponse.getCustomAttributes());

    verify(nodePriorityPersistenceService, times(1))
        .saveNodePriorityEntity(any(NodePriorityDomainDto.class));
    verify(nodePriorityPersistenceService, times(1))
        .fetchNodePriorityListByOrgIdAndNodeGroupIdAndNodeId(anyString(), anyLong(), anyString());
  }

  @Test
  void processUpdateNodePriorityWithinNodeGroupExceptionTest() throws PromiseEngineException {
    when(nodePriorityPersistenceService.fetchNodePriorityListByOrgIdAndNodeGroupIdAndNodeId(
            anyString(), anyLong(), anyString()))
        .thenReturn(List.of());

    Exception ex =
        assertThrows(
            Exception.class,
            () -> {
              nodePriorityService.processUpdateNodePriorityWithinNodeGroup(
                  TestUtil.ORG_ID,
                  NODE_GROUP_ID,
                  TestUtil.NODE_ID,
                  testUtil.getNodePriorityUpdationRequest(100));
            });

    assertEquals(
        "Node group details not found for given orgId , nodeGroupId and nodeId", ex.getMessage());

    verify(nodePriorityPersistenceService, times(1))
        .fetchNodePriorityListByOrgIdAndNodeGroupIdAndNodeId(anyString(), anyLong(), anyString());
  }

  @Test
  void processRemoveNodeFromNodeGroupTest() throws PromiseEngineException, CommonServiceException {
    NodePriorityDomainDto nodePriorityEntity = testUtil.getNodePriorityEntity();
    when(nodePriorityPersistenceService.fetchNodePriorityListByOrgIdAndNodeGroupIdAndNodeId(
            anyString(), anyLong(), anyString()))
        .thenReturn(List.of(nodePriorityEntity));
    doNothing().when(nodePriorityPersistenceService).deleteNodePriorityEntity(nodePriorityEntity);

    NodePriorityResponse nodePriorityResponse =
        nodePriorityService.processRemoveNodeFromNodeGroup(
            TestUtil.ORG_ID, NODE_GROUP_ID, TestUtil.NODE_ID);
    assertEquals(nodePriorityEntity.getNodeGroupId(), nodePriorityResponse.getNodeGroupId());

    verify(nodePriorityPersistenceService, times(1))
        .fetchNodePriorityListByOrgIdAndNodeGroupIdAndNodeId(anyString(), anyLong(), anyString());
    verify(nodePriorityPersistenceService, times(1))
        .deleteNodePriorityEntity(any(NodePriorityDomainDto.class));
  }

  @Test
  void processRemoveNodeFromNodeGroupExceptionTest() throws PromiseEngineException {
    when(nodePriorityPersistenceService.fetchNodePriorityListByOrgIdAndNodeGroupIdAndNodeId(
            anyString(), anyLong(), anyString()))
        .thenReturn(List.of());

    Exception ex =
        assertThrows(
            Exception.class,
            () -> {
              nodePriorityService.processRemoveNodeFromNodeGroup(
                  TestUtil.ORG_ID, NODE_GROUP_ID, TestUtil.NODE_ID);
            });

    assertEquals(
        "Node group details not found for given orgId , nodeGroupId and nodeId", ex.getMessage());

    verify(nodePriorityPersistenceService, times(1))
        .fetchNodePriorityListByOrgIdAndNodeGroupIdAndNodeId(anyString(), anyLong(), anyString());
  }

  @Test
  void processRemoveAllNodesFromNodeGroup() throws PromiseEngineException {
    when(nodePriorityPersistenceService.deleteNodePriorityByOrgIdAndNodeGroupId(
            anyString(), anyLong()))
        .thenReturn(testUtil.getNodePriorityEntityList());
    List<NodePriorityDomainDto> response =
        nodePriorityService.processRemoveAllNodesFromNodeGroup(TestUtil.ORG_ID, NODE_GROUP_ID);
    Assertions.assertNotNull(response);
    Assertions.assertEquals(1, response.size());
    verify(nodePriorityPersistenceService, times(1))
        .deleteNodePriorityByOrgIdAndNodeGroupId(any(), any());
  }

  @Test
  void deleteNodePriorityAssociatedToNodeGroupTest() throws PromiseEngineException {
    when(nodePriorityPersistenceService.deleteNodePriorityByOrgIdAndNodeGroupId(any(), any()))
        .thenReturn(testUtil.getNodePriorityEntityList());
    when(nodePriorityPersistenceService.fetchNodePriorityListByOrgIdAndNodeGroupId(
            anyString(), anyLong()))
        .thenReturn(List.of(testUtil.getNodePriorityEntity()));
    List<NodePriorityDomainDto> nodePriorityEntities =
        nodePriorityService.deleteNodePriorityAssociatedToNodeGroup(TestUtil.ORG_ID, NODE_GROUP_ID);
    Assertions.assertNotNull(nodePriorityEntities);
    Assertions.assertEquals(1, nodePriorityEntities.size());
    verify(nodePriorityPersistenceService, times(1))
        .fetchNodePriorityListByOrgIdAndNodeGroupId(anyString(), anyLong());
  }

  @Test
  @DisplayName("Delete nodes from Nodegroup when no node priority is found")
  void deleteNodePriorityAssociatedToNodeGroupTestWhenNodePriorityNotFound()
      throws PromiseEngineException {
    when(nodePriorityPersistenceService.fetchNodePriorityListByOrgIdAndNodeGroupId(
            anyString(), anyLong()))
        .thenReturn(List.of());
    when(nodePriorityPersistenceService.deleteNodePriorityByOrgIdAndNodeGroupId(any(), any()))
        .thenReturn(testUtil.getNodePriorityEntityList());
    List<NodePriorityDomainDto> nodePriorityEntities =
        nodePriorityService.deleteNodePriorityAssociatedToNodeGroup(TestUtil.ORG_ID, NODE_GROUP_ID);
    Assertions.assertNotNull(nodePriorityEntities);
    Assertions.assertEquals(0, nodePriorityEntities.size());
    verify(nodePriorityPersistenceService, times(1))
        .fetchNodePriorityListByOrgIdAndNodeGroupId(any(), any());
    verify(nodePriorityPersistenceService, times(0))
        .deleteNodePriorityByOrgIdAndNodeGroupId(any(), any());
  }

  @Test
  void processEditNodesWithinNodeGroupThatDoesNotExistTest()
      throws PromiseEngineException, CommonServiceException {
    when(nodeGroupPersistenceService.fetchNodeGroupByOrgIdAndName(anyString(), anyString()))
        .thenReturn(List.of());

    Exception ex =
        assertThrows(
            Exception.class,
            () -> {
              nodePriorityService.editNodesWithinNodeGroup(
                  TestUtil.ORG_ID,
                  TestUtil.NODE_GROUP_NAME,
                  testUtil.getNodeGroupWithNodesResponse());
            });

    assertEquals("Node group with given nodeGroupName and orgId does not exist", ex.getMessage());

    verify(nodeGroupPersistenceService, times(1))
        .fetchNodeGroupByOrgIdAndName(anyString(), anyString());
  }

  @Test
  void processEditNodesWithinNodeGroupTest() throws PromiseEngineException, CommonServiceException {
    NodeGroupDomainDto nodeGroupEntity = testUtil.getNodeGroupEntity();
    NodeGroupDetailsResponse nodeGroupDetailsResponse = testUtil.getNodeGroupDetailsListResponse();

    when(nodeGroupPersistenceService.fetchNodeGroupByOrgIdAndName(anyString(), anyString()))
        .thenReturn(List.of(nodeGroupEntity));
    List<NodePriorityInfo> nodePriorityInfoList = new ArrayList<>();
    nodePriorityInfoList.addAll(nodeGroupDetailsResponse.getNodeInfo());
    nodePriorityInfoList.add(new NodePriorityInfo("node0", 12));
    nodeGroupDetailsResponse.setNodeInfo(nodePriorityInfoList);
    when(nodeFeign.getNodeDetails(anyString(), anyString()))
        .thenReturn(testUtil.getBaseResponseOfNode());

    NodeGroupWithNodesResponse nodeGroupWithNodesResponse =
        nodePriorityService.editNodesWithinNodeGroup(
            TestUtil.ORG_ID, TestUtil.NODE_GROUP_NAME, testUtil.getNodeGroupWithNodesResponse());
    assertEquals(TestUtil.NODE_GROUP_NAME, nodeGroupWithNodesResponse.getNodeGroupName());
    assertEquals(2, nodeGroupWithNodesResponse.getNodes().size());

    verify(nodeGroupPersistenceService, times(1))
        .fetchNodeGroupByOrgIdAndName(anyString(), anyString());
    verify(nodeGroupPersistenceService, times(0)).saveNodeGroup(any(NodeGroupDomainDto.class));
  }

  @Test
  void processEditNodesWithinNodeGroupTestWhenInvalidNodePresent()
      throws PromiseEngineException, CommonServiceException {
    NodeGroupDomainDto nodeGroupEntity = testUtil.getNodeGroupEntity();
    NodeGroupDetailsResponse nodeGroupDetailsResponse = testUtil.getNodeGroupDetailsListResponse();
    Map<String, Collection<String>> headers = new HashMap<>();
    when(nodeGroupPersistenceService.fetchNodeGroupByOrgIdAndName(anyString(), anyString()))
        .thenReturn(List.of(nodeGroupEntity));
    List<NodePriorityInfo> nodePriorityInfoList = new ArrayList<>();
    nodePriorityInfoList.addAll(nodeGroupDetailsResponse.getNodeInfo());
    nodePriorityInfoList.add(new NodePriorityInfo("node0", 12));
    nodeGroupDetailsResponse.setNodeInfo(nodePriorityInfoList);
    when(nodeFeign.getNodeDetails(anyString(), anyString()))
        .thenThrow(
            new BadRequest(
                "error in fetching node details",
                Request.create(HttpMethod.GET, "", new HashMap<>(), null, null, null),
                TestUtil.getNodeNotFoundError.getBytes(StandardCharsets.UTF_8),
                headers));

    NodeGroupWithNodesResponse nodeGroupWithNodesResponse =
        nodePriorityService.editNodesWithinNodeGroup(
            TestUtil.ORG_ID, TestUtil.NODE_GROUP_NAME, testUtil.getNodeGroupWithNodesResponse());
    assertEquals(TestUtil.NODE_GROUP_NAME, nodeGroupWithNodesResponse.getNodeGroupName());
    assertEquals(0, nodeGroupWithNodesResponse.getNodes().size());

    verify(nodeGroupPersistenceService, times(1))
        .fetchNodeGroupByOrgIdAndName(anyString(), anyString());
    verify(nodeGroupPersistenceService, times(0)).saveNodeGroup(any(NodeGroupDomainDto.class));
  }

  @Test
  void processEditNodesWithinNodeGroupTestWhenNodeIsAlreadyPartOfNodeGroup()
      throws PromiseEngineException, CommonServiceException {
    NodeGroupDomainDto nodeGroupEntity = testUtil.getNodeGroupEntity();
    NodeGroupDetailsResponse nodeGroupDetailsResponse = testUtil.getNodeGroupDetailsListResponse();

    when(nodeGroupPersistenceService.fetchNodeGroupByOrgIdAndName(anyString(), anyString()))
        .thenReturn(List.of(nodeGroupEntity));
    List<NodePriorityInfo> nodePriorityInfoList = new ArrayList<>();
    nodePriorityInfoList.addAll(nodeGroupDetailsResponse.getNodeInfo());
    nodePriorityInfoList.add(new NodePriorityInfo("node0", 12));
    nodeGroupDetailsResponse.setNodeInfo(nodePriorityInfoList);
    when(nodeFeign.getNodeDetails(anyString(), anyString()))
        .thenReturn(testUtil.getBaseResponseOfNode());
    when(nodePriorityPersistenceService.fetchNodePriorityListByOrgIdAndNodeGroupIdAndNodeId(
            anyString(), anyLong(), anyString()))
        .thenReturn(List.of(testUtil.getNodePriorityEntity()));

    NodeGroupWithNodesResponse nodeGroupWithNodesResponse =
        nodePriorityService.editNodesWithinNodeGroup(
            TestUtil.ORG_ID, TestUtil.NODE_GROUP_NAME, testUtil.getNodeGroupWithNodesResponse());
    assertEquals(TestUtil.NODE_GROUP_NAME, nodeGroupWithNodesResponse.getNodeGroupName());
    assertEquals(2, nodeGroupWithNodesResponse.getNodes().size());

    verify(nodeGroupPersistenceService, times(1))
        .fetchNodeGroupByOrgIdAndName(anyString(), anyString());
    verify(nodeGroupPersistenceService, times(0)).saveNodeGroup(any(NodeGroupDomainDto.class));
  }

  @Test
  @DisplayName("Delete nodes from node group - Happy Path")
  void deleteNodesFromNodeGroup() throws PromiseEngineException, CommonServiceException {
    Optional<NodeGroupDomainDto> nodeGroupEntity =
        Optional.ofNullable(testUtil.getNodeGroupEntity());
    when(nodeGroupPersistenceService.fetchNodeGroupByIdAndOrgId(any(), any()))
        .thenReturn(nodeGroupEntity);
    when(nodePriorityPersistenceService.fetchNodePriorityListByOrgIdAndNodeGroupId(
            anyString(), anyLong()))
        .thenReturn(testUtil.getNodePriorityEntityList());
    when(nodePriorityPersistenceService.deleteNodePriorityByOrgIdAndNodeGroupId(
            anyString(), anyLong()))
        .thenReturn(testUtil.getNodePriorityEntityList());
    List<NodePriorityDomainDto> response =
        nodePriorityService.deleteNodesFromTheNodeGroup(TestUtil.ORG_ID, NODE_GROUP_ID);
    assertNotNull(response);
    assertEquals(NODE_ID, response.get(0).getNodeId());
    verify(nodeGroupPersistenceService, times(1)).fetchNodeGroupByIdAndOrgId(anyLong(), any());
    verify(nodePriorityPersistenceService, times(1))
        .fetchNodePriorityListByOrgIdAndNodeGroupId(anyString(), any());
    verify(nodePriorityPersistenceService, times(1))
        .deleteNodePriorityByOrgIdAndNodeGroupId(anyString(), any());
  }

  @Test
  @DisplayName("Delete nodes from node group - Exception scenario")
  void deleteNodesFromNodeGroupExceptionTest()
      throws PromiseEngineException, CommonServiceException {
    when(nodeGroupPersistenceService.fetchNodeGroupByIdAndOrgId(any(), any()))
        .thenReturn(Optional.empty());
    CommonServiceException thrownException =
        assertThrows(
            CommonServiceException.class,
            () -> {
              nodePriorityService.deleteNodesFromTheNodeGroup(TestUtil.ORG_ID, NODE_GROUP_ID);
            });
    assertEquals("Node Group not found", thrownException.getMessage());
    verify(nodeGroupPersistenceService, times(1)).fetchNodeGroupByIdAndOrgId(anyLong(), any());
    verify(nodePriorityPersistenceService, times(0))
        .fetchNodePriorityListByOrgIdAndNodeGroupId(anyString(), any());
    verify(nodePriorityPersistenceService, times(0))
        .deleteNodePriorityByOrgIdAndNodeGroupId(anyString(), any());
  }

  @Test
  @DisplayName("Get Node Priority List By NodeId And OrgId")
  void getNodePriorityListByNodeIdAndOrgIdTest()
      throws PromiseEngineException, CommonServiceException {
    when(nodePriorityPersistenceService.fetchNodePriorityListByNodeIdAndOrgId(
            anyString(), anyString()))
        .thenReturn(testUtil.getNodePriorityEntityList());
    List<NodePriorityResponse> dtos =
        nodePriorityService.getNodePriorityListByNodeIdAndOrgId(NODE_ID, TestUtil.ORG_ID);
    Assertions.assertEquals(1, dtos.size());
    Assertions.assertEquals(TestUtil.ORG_ID, dtos.getFirst().getOrgId());
    Assertions.assertEquals(NODE_ID, dtos.getFirst().getNodeId());
  }

  @Test
  @DisplayName("Get Node Priority List By NodeId And OrgId - Exception scenario")
  void getNodePriorityListByNodeIdAndOrgIdExceptionTest() throws PromiseEngineException {
    when(nodePriorityPersistenceService.fetchNodePriorityListByNodeIdAndOrgId(
            anyString(), anyString()))
        .thenReturn(Collections.emptyList());
    CommonServiceException e =
        Assertions.assertThrows(
            CommonServiceException.class,
            () ->
                nodePriorityService.getNodePriorityListByNodeIdAndOrgId(NODE_ID, TestUtil.ORG_ID));
    Assertions.assertEquals("Node group details not found for given id and orgId", e.getMessage());
    Assertions.assertEquals(HttpStatus.NOT_FOUND, e.getHttpStatus());
  }
}
