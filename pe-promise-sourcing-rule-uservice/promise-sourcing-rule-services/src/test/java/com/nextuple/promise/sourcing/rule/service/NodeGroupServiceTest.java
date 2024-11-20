/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.promise.sourcing.rule.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.exception.PromiseEngineException;
import com.nextuple.common.pojo.PageParams;
import com.nextuple.promise.sourcing.rule.TestUtil;
import com.nextuple.promise.sourcing.rule.api.domain.enums.SourcingAttributesDefinitionStatus;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.CreateNodeGroupRequest;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.DeleteNodeGroupsRequest;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.UpdateNodeGroupRequest;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.*;
import com.nextuple.promise.sourcing.rule.api.domain.pojo.NodePriorityInfo;
import com.nextuple.promise.sourcing.rule.api.domain.projection.SourcingRuleByNodeGroupCountProjection;
import com.nextuple.promise.sourcing.rule.persistence.domain.NodeGroupDomainDto;
import com.nextuple.promise.sourcing.rule.persistence.domain.NodePriorityDomainDto;
import com.nextuple.promise.sourcing.rule.persistence.service.NodeGroupPersistenceService;
import com.nextuple.promise.sourcing.rule.utils.PromiseSourcingRuleUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.projection.SpelAwareProxyProjectionFactory;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;

class NodeGroupServiceTest {
  @InjectMocks private NodeGroupService nodeGroupService;
  @Mock private NodeGroupPersistenceService nodeGroupPersistenceService;
  @Mock private NodePriorityService nodePriorityService;

  @Mock private SourcingRulesConfigurationService sourcingRulesConfigurationService;
  @Mock private SourcingAttributesDefinitionService sourcingAttributesDefinitionService;

  @InjectMocks private TestUtil testUtil;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    ReflectionTestUtils.setField(nodeGroupService, "baseApiUrl", "baseUrl");
  }

  @Test
  void createNodeGroupTest() throws PromiseEngineException, CommonServiceException {
    CreateNodeGroupRequest createNodeGroupRequest = testUtil.getCreateNodeGroupRequest();
    when(nodeGroupPersistenceService.saveNodeGroup(any(NodeGroupDomainDto.class)))
        .thenReturn(testUtil.getNodeGroupEntity());

    NodeGroupResponse nodeGroupResponse = nodeGroupService.createNodeGroup(createNodeGroupRequest);
    assertEquals(testUtil.getNodeGroupEntity().getId(), nodeGroupResponse.getId());
    verify(nodeGroupPersistenceService, times(1)).saveNodeGroup(any(NodeGroupDomainDto.class));
  }

  @Test
  void validateNodeGroupTest1() throws PromiseEngineException {
    CreateNodeGroupRequest createNodeGroupRequest = testUtil.getCreateNodeGroupRequest();
    when(nodeGroupPersistenceService.fetchNodeGroupByOrgIdAndName(anyString(), anyString()))
        .thenReturn(List.of(testUtil.getNodeGroupEntity()));

    Exception ex =
        assertThrows(
            Exception.class,
            () -> {
              nodeGroupService.createNodeGroup(createNodeGroupRequest);
            });

    assertEquals("Combination of orgId and nodeGroupName should be unique", ex.getMessage());
    verify(nodeGroupPersistenceService, times(1))
        .fetchNodeGroupByOrgIdAndName(anyString(), anyString());
  }

  @Test
  void validateNodeGroupTest2() {
    CreateNodeGroupRequest createNodeGroupRequest = testUtil.getCreateNodeGroupRequest();
    createNodeGroupRequest.setNodeGroupName("NG 3");
    assertDoesNotThrow(
        () -> {
          nodeGroupService.createNodeGroup(createNodeGroupRequest);
        });
  }

  @Test
  void validateNodeGroupTest3() {
    CreateNodeGroupRequest createNodeGroupRequest = testUtil.getCreateNodeGroupRequest();
    createNodeGroupRequest.setNodeGroupName("NG #3");

    Exception ex =
        assertThrows(
            Exception.class,
            () -> {
              nodeGroupService.createNodeGroup(createNodeGroupRequest);
            });

    assertEquals(
        "Invalid format! Only alphanumeric characters, underscore and whitespace allowed.",
        ex.getMessage());
  }

  @Test
  void getNodeGroupByIdandOrgIdTest() throws PromiseEngineException, CommonServiceException {
    when(nodeGroupPersistenceService.fetchNodeGroupByIdAndOrgId(anyLong(), anyString()))
        .thenReturn(Optional.ofNullable(testUtil.getNodeGroupEntity()));

    NodeGroupResponse nodeGroupResponse =
        nodeGroupService.fetchNodeGroupByIdandOrgId(TestUtil.NODE_GROUP_ID, TestUtil.ORG_ID);
    assertEquals(testUtil.getNodeGroupEntity().getId(), nodeGroupResponse.getId());
    verify(nodeGroupPersistenceService, times(1))
        .fetchNodeGroupByIdAndOrgId(anyLong(), anyString());
  }

  @Test
  void getNodeGroupByIdandOrgIdExceptionTest() throws PromiseEngineException {
    when(nodeGroupPersistenceService.fetchNodeGroupByIdAndOrgId(anyLong(), anyString()))
        .thenReturn(Optional.empty());

    Exception ex =
        assertThrows(
            Exception.class,
            () -> {
              nodeGroupService.fetchNodeGroupByIdandOrgId(TestUtil.NODE_GROUP_ID, TestUtil.ORG_ID);
            });

    assertEquals("Node Group not found", ex.getMessage());
    verify(nodeGroupPersistenceService, times(1))
        .fetchNodeGroupByIdAndOrgId(anyLong(), anyString());
  }

  @Test
  void updateNodeGroupDetailsTest1() throws PromiseEngineException, CommonServiceException {
    String name = "NG2";
    String description = "Node Group 2";
    NodeGroupResponse nodeGroupResponse = testUtil.getUpdatedNodeGroupResponse(name, description);
    NodeGroupDomainDto updatedNodeGroupEntity = testUtil.getNodeGroupEntity();
    updatedNodeGroupEntity.setNodeGroupName(name);
    updatedNodeGroupEntity.setNodeGroupDescription(description);

    when(nodeGroupPersistenceService.fetchNodeGroupByIdAndOrgId(anyLong(), anyString()))
        .thenReturn(Optional.ofNullable(testUtil.getNodeGroupEntity()));
    when(nodeGroupPersistenceService.saveNodeGroup(any(NodeGroupDomainDto.class)))
        .thenReturn(updatedNodeGroupEntity);

    NodeGroupResponse response =
        nodeGroupService.updateNodeGroup(
            TestUtil.NODE_GROUP_ID,
            TestUtil.ORG_ID,
            testUtil.getUpdateNodeGroupRequest(name, description));
    assertEquals(nodeGroupResponse.getId(), response.getId());
    verify(nodeGroupPersistenceService, times(1))
        .fetchNodeGroupByIdAndOrgId(anyLong(), anyString());
    verify(nodeGroupPersistenceService, times(1)).saveNodeGroup(any(NodeGroupDomainDto.class));
  }

  @Test
  void updateNodeGroupDetailsTest2() throws PromiseEngineException, CommonServiceException {
    String description = "Node Group 2";
    UpdateNodeGroupRequest updateNodeGroupRequest =
        UpdateNodeGroupRequest.builder().nodeGroupDescription(description).build();
    NodeGroupResponse nodeGroupResponse =
        testUtil.getUpdatedNodeGroupResponse(TestUtil.NODE_GROUP_NAME, description);
    NodeGroupDomainDto updatedNodeGroupEntity = testUtil.getNodeGroupEntity();
    updatedNodeGroupEntity.setNodeGroupDescription(description);
    when(nodeGroupPersistenceService.fetchNodeGroupByIdAndOrgId(anyLong(), anyString()))
        .thenReturn(Optional.ofNullable(testUtil.getNodeGroupEntity()));
    when(nodeGroupPersistenceService.saveNodeGroup(any(NodeGroupDomainDto.class)))
        .thenReturn(updatedNodeGroupEntity);

    NodeGroupResponse response =
        nodeGroupService.updateNodeGroup(
            TestUtil.NODE_GROUP_ID, TestUtil.ORG_ID, updateNodeGroupRequest);
    assertEquals(nodeGroupResponse.getId(), response.getId());
    verify(nodeGroupPersistenceService, times(1))
        .fetchNodeGroupByIdAndOrgId(anyLong(), anyString());
    verify(nodeGroupPersistenceService, times(1)).saveNodeGroup(any(NodeGroupDomainDto.class));
  }

  @Test
  void updateNodeGroupDetailsTestException() throws PromiseEngineException {
    String name = "NG2";
    String description = "Node Group 2";

    when(nodeGroupPersistenceService.fetchNodeGroupById(anyLong())).thenReturn(Optional.empty());

    Exception ex =
        assertThrows(
            Exception.class,
            () -> {
              nodeGroupService.updateNodeGroup(
                  TestUtil.NODE_GROUP_ID,
                  TestUtil.ORG_ID,
                  testUtil.getUpdateNodeGroupRequest(name, description));
            });

    assertEquals("Node Group not found", ex.getMessage());
    verify(nodeGroupPersistenceService, times(1))
        .fetchNodeGroupByIdAndOrgId(anyLong(), anyString());
  }

  @Test
  void updateNodeGroupNameTest() throws PromiseEngineException, CommonServiceException {
    String name = "";
    String description = "Node GroupName Test";
    NodeGroupDomainDto updatedNodeGroupEntity = testUtil.getNodeGroupEntity();
    updatedNodeGroupEntity.setNodeGroupName(name);
    updatedNodeGroupEntity.setNodeGroupDescription(description);

    when(nodeGroupPersistenceService.fetchNodeGroupByIdAndOrgId(anyLong(), anyString()))
        .thenReturn(Optional.ofNullable(testUtil.getNodeGroupEntity()));
    when(nodeGroupPersistenceService.saveNodeGroup(any(NodeGroupDomainDto.class)))
        .thenReturn(updatedNodeGroupEntity);

    Exception ex =
        assertThrows(
            Exception.class,
            () -> {
              nodeGroupService.updateNodeGroup(
                  TestUtil.NODE_GROUP_ID,
                  TestUtil.ORG_ID,
                  testUtil.getUpdateNodeGroupRequest(name, description));
            });

    assertEquals("nodeGroupName cannot be blank", ex.getMessage());
  }

  @Test
  @DisplayName("Nodegroup edit with existing name should be reject")
  void updateNodeGroupNameSameTest() throws PromiseEngineException {
    String name = "NG1";
    String description = "Node GroupName Test";
    NodeGroupDomainDto updatedNodeGroupEntity = testUtil.getNodeGroupEntity();
    updatedNodeGroupEntity.setNodeGroupName(name);
    updatedNodeGroupEntity.setNodeGroupDescription(description);

    when(nodeGroupPersistenceService.fetchNodeGroupByIdAndOrgId(anyLong(), anyString()))
        .thenReturn(Optional.ofNullable(testUtil.getNodeGroupEntity()));
    when(nodeGroupPersistenceService.saveNodeGroup(any(NodeGroupDomainDto.class)))
        .thenReturn(updatedNodeGroupEntity);
    when(nodeGroupPersistenceService.fetchNodeGroupByOrgIdAndName(any(), any()))
        .thenReturn(List.of(testUtil.getNodeGroupEntity()));

    Exception ex =
        assertThrows(
            Exception.class,
            () -> {
              nodeGroupService.updateNodeGroup(
                  TestUtil.NODE_GROUP_ID,
                  TestUtil.ORG_ID,
                  testUtil.getUpdateNodeGroupRequest(name, description));
            });

    assertEquals("Combination of orgId and nodeGroupName should be unique", ex.getMessage());
  }

  @Test
  void createNodeGroupFromDashboardTest() throws PromiseEngineException, CommonServiceException {
    NodeGroupWithNodesResponse request = testUtil.getNodeGroupWithNodesResponse();
    when(nodeGroupPersistenceService.fetchNodeGroupByOrgIdAndName(any(), any()))
        .thenReturn(Collections.emptyList());
    when(nodeGroupPersistenceService.saveNodeGroup(any()))
        .thenReturn(testUtil.getNodeGroupEntity());
    when(nodePriorityService.processAddNodePriorityToNodeGroup(any()))
        .thenReturn(testUtil.getNodePriorityResponse());

    NodeGroupWithNodesResponse response =
        nodeGroupService.createNodeGroupFromDashboard(TestUtil.ORG_ID, request);
    Assertions.assertNotNull(response);
    Assertions.assertEquals(2, response.getNodes().size());
    verify(nodeGroupPersistenceService, times(1)).fetchNodeGroupByOrgIdAndName(any(), any());
    verify(nodeGroupPersistenceService, times(1)).saveNodeGroup(any());
    verify(nodePriorityService, times(2)).processAddNodePriorityToNodeGroup(any());
  }

  @Test
  void deleteNodeGroupAndAssociatedNodePriorityTest()
      throws PromiseEngineException, CommonServiceException {
    when(nodeGroupPersistenceService.fetchNodeGroupById(any()))
        .thenReturn(Optional.of(testUtil.getNodeGroupEntity()));
    when(nodePriorityService.processRemoveAllNodesFromNodeGroup(any(), any()))
        .thenReturn(List.of(testUtil.getNodePriorityEntity()));
    doNothing().when(nodeGroupPersistenceService).deleteNodeGroupEntity(any());
    NodeGroupWithNodesResponse response =
        nodeGroupService.deleteNodeGroupAndAssociatedNodePriority(TestUtil.ORG_ID, 42L);
    Assertions.assertNotNull(response);
    Assertions.assertEquals(TestUtil.NODE_GROUP_NAME, response.getNodeGroupName());
    Assertions.assertEquals(1, response.getNodes().size());
    verify(nodeGroupPersistenceService, times(1)).fetchNodeGroupById(anyLong());
    verify(nodeGroupPersistenceService, times(1)).deleteNodeGroupEntity(any());
    verify(nodePriorityService, times(1)).processRemoveAllNodesFromNodeGroup(any(), any());
  }

  @Test
  @DisplayName("Delete Node Group and Associated Node Priority - Exception scenario")
  void deleteNodeGroupAndAssociatedNodePriorityExceptionTest()
      throws PromiseEngineException, CommonServiceException {
    when(nodeGroupPersistenceService.fetchNodeGroupById(any())).thenReturn(Optional.empty());
    CommonServiceException e =
        Assertions.assertThrows(
            CommonServiceException.class,
            () -> {
              nodeGroupService.deleteNodeGroupAndAssociatedNodePriority(TestUtil.ORG_ID, 42L);
            });
    assertEquals("Node Group not found", e.getMessage());
  }

  @Test
  void editNodeGroupFromDashboard() throws PromiseEngineException, CommonServiceException {
    when(nodeGroupPersistenceService.fetchNodeGroupById(any()))
        .thenReturn(Optional.of(testUtil.getNodeGroupEntity()));
    NodeGroupDetailsResponse nodeGroupDetailsResponse = testUtil.getNodeGroupDetailsListResponse();
    List<NodePriorityInfo> nodePriorityInfoList = new ArrayList<>();
    nodePriorityInfoList.addAll(nodeGroupDetailsResponse.getNodeInfo());
    nodePriorityInfoList.add(new NodePriorityInfo("node0", 12));
    nodeGroupDetailsResponse.setNodeInfo(nodePriorityInfoList);
    when(nodePriorityService.processAddNodePriorityToNodeGroup(any()))
        .thenReturn(testUtil.getNodePriorityResponse());
    when(nodePriorityService.deleteNodePriorityAssociatedToNodeGroup(any(), any()))
        .thenReturn(testUtil.getNodePriorityEntityList());

    NodeGroupWithNodesResponse response =
        nodeGroupService.editNodeGroupFromDashboard(
            TestUtil.ORG_ID, TestUtil.NODE_GROUP_ID, testUtil.getNodeGroupWithNodesResponse());
    Assertions.assertNotNull(response);
    Assertions.assertEquals(TestUtil.NODE_GROUP_NAME, response.getNodeGroupName());
    Assertions.assertEquals(2, response.getNodes().size());
    verify(nodeGroupPersistenceService, times(1)).fetchNodeGroupById(any());
    verify(nodePriorityService, times(2)).processAddNodePriorityToNodeGroup(any());
    verify(nodePriorityService, times(1)).deleteNodePriorityAssociatedToNodeGroup(any(), any());
  }

  @Test
  @DisplayName("Update node group with deleted or inactive nodes")
  void editNodeGroupFromDashboardIgnoresDeletedOrInactiveNodes()
      throws PromiseEngineException, CommonServiceException {
    when(nodeGroupPersistenceService.fetchNodeGroupById(any()))
        .thenReturn(Optional.of(testUtil.getNodeGroupEntity()));
    NodeGroupDetailsResponse nodeGroupDetailsResponse = testUtil.getNodeGroupDetailsListResponse();
    List<NodePriorityInfo> nodePriorityInfoList = new ArrayList<>();
    nodePriorityInfoList.addAll(nodeGroupDetailsResponse.getNodeInfo());
    nodePriorityInfoList.add(new NodePriorityInfo("node0", 12));
    nodeGroupDetailsResponse.setNodeInfo(nodePriorityInfoList);
    when(nodePriorityService.processAddNodePriorityToNodeGroup(any()))
        .thenThrow(testUtil.getNodePriorityInvalidNodeException());
    when(nodePriorityService.deleteNodePriorityAssociatedToNodeGroup(any(), any()))
        .thenReturn(testUtil.getNodePriorityEntityList());

    NodeGroupWithNodesResponse response =
        nodeGroupService.editNodeGroupFromDashboard(
            TestUtil.ORG_ID, TestUtil.NODE_GROUP_ID, testUtil.getNodeGroupWithNodesResponse());
    Assertions.assertNotNull(response);
    Assertions.assertEquals(TestUtil.NODE_GROUP_NAME, response.getNodeGroupName());
    Assertions.assertEquals(0, response.getNodes().size());
    verify(nodeGroupPersistenceService, times(1)).fetchNodeGroupById(any());
    verify(nodePriorityService, times(2)).processAddNodePriorityToNodeGroup(any());
    verify(nodePriorityService, times(1)).deleteNodePriorityAssociatedToNodeGroup(any(), any());
  }

  @Test
  @DisplayName("Update node group with same name but different nodes")
  void updateNodeGroupFromDashboardWithSameNameButDifferentNodesTest()
      throws PromiseEngineException {
    when(nodeGroupPersistenceService.fetchNodeGroupById(any()))
        .thenReturn(Optional.of(testUtil.getNodeGroupEntity()));
    when(nodeGroupPersistenceService.fetchNodeGroupByOrgIdAndName(any(), any()))
        .thenReturn(List.of(testUtil.getNodeGroupEntity()));

    Exception ex =
        assertThrows(
            Exception.class,
            () -> {
              nodeGroupService.editNodeGroupFromDashboard(
                  TestUtil.ORG_ID, 2L, testUtil.getNodeGroupWithNodesResponse());
            });

    assertEquals("Combination of orgId and nodeGroupName should be unique", ex.getMessage());
  }

  @Test
  void editNodeGroupFromDashboardNodeGroupNotFound()
      throws PromiseEngineException, CommonServiceException {
    when(nodeGroupPersistenceService.fetchNodeGroupById(any())).thenReturn(Optional.empty());
    CommonServiceException e =
        Assertions.assertThrows(
            CommonServiceException.class,
            () -> {
              nodeGroupService.editNodeGroupFromDashboard(
                  TestUtil.ORG_ID,
                  TestUtil.NODE_GROUP_ID,
                  testUtil.getNodeGroupWithNodesResponse());
            });
    Assertions.assertEquals(HttpStatus.NOT_FOUND, e.getHttpStatus());
    verify(nodeGroupPersistenceService, times(1)).fetchNodeGroupById(any());
    verify(nodePriorityService, times(0)).processAddNodePriorityToNodeGroup(any());
    verify(nodePriorityService, times(0)).deleteNodePriorityAssociatedToNodeGroup(any(), any());
  }

  @Test
  void editNodeGroupWhenTheRequestHasZeroNode()
      throws PromiseEngineException, CommonServiceException {
    var editNodeGroupRequest = testUtil.getNodeGroupWithNodesResponse();
    editNodeGroupRequest.setNodes(List.of());

    CommonServiceException e =
        Assertions.assertThrows(
            CommonServiceException.class,
            () -> {
              nodeGroupService.editNodeGroupFromDashboard(
                  TestUtil.ORG_ID, TestUtil.NODE_GROUP_ID, editNodeGroupRequest);
            });
    Assertions.assertEquals(HttpStatus.BAD_REQUEST, e.getHttpStatus());
    Assertions.assertEquals(
        "Node group must have at least one node attached to it", e.getMessage());
    verify(nodePriorityService, times(0)).processAddNodePriorityToNodeGroup(any());
    verify(nodePriorityService, times(0)).deleteNodePriorityAssociatedToNodeGroup(any(), any());
  }

  @Test
  void deleteNodeGroupsAndAssociatedNodePriorityByOrgId() throws PromiseEngineException {
    doNothing().when(nodeGroupPersistenceService).deleteNodeGroupEntities(any());
    when(nodePriorityService.processRemoveAllNodesFromNodeGroup(any(), any()))
        .thenReturn(List.of(testUtil.getNodePriorityEntity()));

    nodeGroupService.deleteNodeGroupsAndAssociatedNodePriorityByOrgId(
        TestUtil.ORG_ID, new DeleteNodeGroupsRequest(List.of(42L)));
    verify(nodeGroupPersistenceService, times(1)).deleteNodeGroupEntities(any());
    verify(nodePriorityService, times(1)).processRemoveAllNodesFromNodeGroup(any(), any());
  }

  @Test
  void fetchNodeGroupsByOrgIdWithNodePriorityTest()
      throws PromiseEngineException, CommonServiceException {
    List<Long> nodeGroupEntityIdsListLong = testUtil.getNodeGroupEntityIds(Long.class, 5);
    List<NodePriorityDomainDto> nodePriorityEntityList = testUtil.getNodePriorityEntityList();
    List<NodeGroupDomainDto> nodeGroupEntityList = testUtil.getNodeGroupEntityList();
    PageParams pageParams =
        testUtil.getPageParams(
            Optional.of(TestUtil.PAGE_NO),
            Optional.of(1),
            Optional.of(TestUtil.SORT_BY),
            Optional.of(TestUtil.SORT_ORDER_DESC));

    Pageable pageable = PromiseSourcingRuleUtil.getPageableForEmptyPageSize(pageParams);

    Page<NodeGroupDomainDto> nodeGroupEntityPage =
        new PageImpl<>(nodeGroupEntityList, pageable, nodeGroupEntityList.size());

    ProjectionFactory factory = new SpelAwareProxyProjectionFactory();
    SourcingRuleByNodeGroupCountProjection projection =
        factory.createProjection(SourcingRuleByNodeGroupCountProjection.class);
    projection.setNodeGroup(TestUtil.NODE_GROUP_NAME);
    projection.setCount(5L);

    List<SourcingRuleByNodeGroupCountProjection> sourcingRuleCount = new ArrayList<>();
    sourcingRuleCount.add(projection);

    when(nodeGroupPersistenceService.fetchPaginatedNodeGroupListByOrgId(anyString(), eq(pageable)))
        .thenReturn(nodeGroupEntityPage);
    when(nodePriorityService.getNodePriorityListByNodeGroupIds(nodeGroupEntityIdsListLong))
        .thenReturn(nodePriorityEntityList);
    when(sourcingRulesConfigurationService.getActiveSourcingRuleCountByNodeGroupIds(
            any(), anyLong()))
        .thenReturn(sourcingRuleCount);
    when(sourcingAttributesDefinitionService.processGetSourcingAttributesDefinitionInActiveStatus(
            anyString(), any()))
        .thenReturn(
            testUtil.getSourcingRuleAttributesDefinitionResponse(
                SourcingAttributesDefinitionStatus.ACTIVE));

    PageResponse<NodeGroupDetailWithPriorityResponse> expectedResponse =
        testUtil.getNodeGroupDetailWithPriorityResponse();

    PageResponse<NodeGroupDetailWithPriorityResponse> savedResponse =
        nodeGroupService.fetchNodeGroupsByOrgIdWithNodePriority(
            TestUtil.ORG_ID,
            testUtil.getPageParams(
                Optional.of(TestUtil.PAGE_NO),
                Optional.of(1),
                Optional.of(TestUtil.SORT_BY),
                Optional.of(TestUtil.SORT_ORDER_DESC)));

    Assertions.assertNotNull(savedResponse);
    Assertions.assertEquals(
        savedResponse.getPagination().getCurrentPage(),
        expectedResponse.getPagination().getCurrentPage());
    Assertions.assertEquals(
        savedResponse.getPagination().getNext(), expectedResponse.getPagination().getNext());
    Assertions.assertEquals(
        savedResponse.getPagination().getPrevious(),
        expectedResponse.getPagination().getPrevious());
    Assertions.assertEquals(2, savedResponse.getData().size());

    verify(nodeGroupPersistenceService, times(1))
        .fetchPaginatedNodeGroupListByOrgId(anyString(), any());
    verify(nodePriorityService, times(1)).getNodePriorityListByNodeGroupIds(any());
    verify(sourcingRulesConfigurationService, times(1))
        .getActiveSourcingRuleCountByNodeGroupIds(any(), anyLong());
  }

  @Test
  @DisplayName("Get Node Group List")
  void getNodeGroupListForOrgId() throws PromiseEngineException {
    when(nodeGroupPersistenceService.fetchNodeGroupListByOrgId(any()))
        .thenReturn(List.of(testUtil.getNodeGroupEntity()));
    List<NodeGroupDomainDto> nodeGroupEntities =
        nodeGroupService.getNodeGroupListForOrgId(TestUtil.ORG_ID);
    verify(nodeGroupPersistenceService, times(1)).fetchNodeGroupListByOrgId(any());
    assertEquals(testUtil.getNodeGroupEntity().getId(), nodeGroupEntities.get(0).getId());
  }
}
