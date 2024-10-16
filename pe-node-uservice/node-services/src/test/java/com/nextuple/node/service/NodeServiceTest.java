/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.node.service;

import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.pojo.PageParams;
import com.nextuple.common.pojo.PageProperties;
import com.nextuple.node.TestUtil;
import com.nextuple.node.config.NodeTenantBasedDBConfig;
import com.nextuple.node.domain.dto.NodeCacheKeyDto;
import com.nextuple.node.domain.dto.NodeDto;
import com.nextuple.node.domain.inbound.NodeRequest;
import com.nextuple.node.domain.inbound.NodeUpdationRequest;
import com.nextuple.node.domain.outbound.NodeResponse;
import com.nextuple.node.domain.outbound.NodeTypesResponse;
import com.nextuple.node.persistence.domain.NodeDomainDto;
import com.nextuple.node.persistence.exception.NodeDomainException;
import com.nextuple.node.persistence.service.NodePersistenceService;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@ExtendWith(MockitoExtension.class)
class NodeServiceTest {

  @InjectMocks private NodeService nodeService;

  @InjectMocks private TestUtil testUtil;

  @Mock private NodePersistenceService nodePersistenceService;

  @Mock NodeTenantBasedDBConfig nodeTenantBasedDBConfig;

  @Mock private PageProperties pageProperties;

  @Test
  void createNodeTest() throws NodeDomainException, CommonServiceException {
    NodeDomainDto nodeDomainDto = testUtil.getNodeDomainDto(TestUtil.NODE_ID);

    NodeRequest nodeRequest = testUtil.getNodeRequest();
    when(nodePersistenceService.saveNodeDetails(any(NodeDomainDto.class)))
        .thenReturn(nodeDomainDto);
    Set<String> nodeTypes = Set.of("STORE", "FC", "MFC", "DROPSHIP VENDOR");
    when(nodeTenantBasedDBConfig.getNodeTypes(anyString())).thenReturn(nodeTypes);

    NodeResponse received_dto = nodeService.createNode(testUtil.getNodeRequest());
    Assertions.assertEquals(nodeRequest.getNodeId(), received_dto.getNodeId());
    verify(nodePersistenceService, times(1)).saveNodeDetails(any(NodeDomainDto.class));
  }

  @Test
  @DisplayName(
      "Happy Path - Creating node when startWorkingTime and lastWorkingTime fields are provided.")
  void createNodeStartAndLastWorkingTimeProvidedTest()
      throws NodeDomainException, CommonServiceException {
    NodeDomainDto nodeDomainDto = testUtil.getNodeDomainDto(TestUtil.NODE_ID);

    NodeRequest nodeRequest = testUtil.getNodeRequest();
    nodeRequest.setStartWorkingTime("08:00");
    nodeRequest.setLastWorkingTime("16:00");
    when(nodePersistenceService.saveNodeDetails(any(NodeDomainDto.class)))
        .thenReturn(nodeDomainDto);
    Set<String> nodeTypes = Set.of("STORE", "FC", "MFC", "DROPSHIP VENDOR");
    when(nodeTenantBasedDBConfig.getNodeTypes(anyString())).thenReturn(nodeTypes);

    NodeResponse received_dto = nodeService.createNode(nodeRequest);
    Assertions.assertEquals(nodeRequest.getNodeId(), received_dto.getNodeId());
    verify(nodePersistenceService, times(1)).saveNodeDetails(any(NodeDomainDto.class));
  }

  @Test
  @DisplayName(
      "Test creating a node - when lastWorkingTime is not provided and startWorkingTime is provided.")
  void createNodeLastWorkingTimeNotProvidedTest()
      throws NodeDomainException, CommonServiceException {
    NodeRequest nodeRequest = testUtil.getNodeRequest();
    nodeRequest.setStartWorkingTime("08:00");
    Set<String> nodeTypes = Set.of("STORE", "FC", "MFC", "DROPSHIP VENDOR");
    when(nodeTenantBasedDBConfig.getNodeTypes(anyString())).thenReturn(nodeTypes);

    Exception ex =
        assertThrows(CommonServiceException.class, () -> nodeService.createNode(nodeRequest));

    Assertions.assertEquals(
        "Either both startWorkingTime and lastWorkingTime should be present or neither should be present.",
        ex.getMessage());
    verify(nodePersistenceService, times(0)).saveNodeDetails(any());
  }

  @Test
  @DisplayName(
      "Test creating a node - when startWorkingTime or lastWorkingTime is not in HH:MM format.")
  void createNodeTimeFormatIncorrectTest() throws NodeDomainException, CommonServiceException {
    NodeRequest nodeRequest = testUtil.getNodeRequest();
    nodeRequest.setStartWorkingTime("0800");
    nodeRequest.setLastWorkingTime("16:00");

    Set<String> nodeTypes = Set.of("STORE", "FC", "MFC", "DROPSHIP VENDOR");
    when(nodeTenantBasedDBConfig.getNodeTypes(anyString())).thenReturn(nodeTypes);
    Exception ex =
        assertThrows(CommonServiceException.class, () -> nodeService.createNode(nodeRequest));
    Assertions.assertEquals("startWorkingTime is invalid.", ex.getMessage());

    nodeRequest.setStartWorkingTime("08:00");
    nodeRequest.setLastWorkingTime("2459");
    ex = assertThrows(CommonServiceException.class, () -> nodeService.createNode(nodeRequest));
    Assertions.assertEquals("lastWorkingTime is invalid.", ex.getMessage());

    verify(nodePersistenceService, times(0)).saveNodeDetails(any());
  }

  @Test
  void updateNodeDetailsTest() throws NodeDomainException, CommonServiceException {
    NodeDomainDto nodeDomainDto = testUtil.getNodeDomainDto(TestUtil.NODE_ID);
    NodeUpdationRequest nodeUpdationRequest = testUtil.getNodeUpdationRequest();
    NodeDomainDto updatedNodeDomainDto = testUtil.getUpdatedNodeDomainDto();
    Set<String> nodeTypes = Set.of("STORE", "FC", "MFC", "DROPSHIP VENDOR");
    when(nodeTenantBasedDBConfig.getNodeTypes(anyString())).thenReturn(nodeTypes);
    when(nodePersistenceService.findNodeByNodeIdAndOrgId(any(), any()))
        .thenReturn(Optional.of(nodeDomainDto));
    when(nodePersistenceService.saveNodeDetails(any())).thenReturn(updatedNodeDomainDto);

    NodeResponse nodeResponse =
        nodeService.updateNodeDetails(TestUtil.NODE_ID, TestUtil.ORG_ID, nodeUpdationRequest);
    Assertions.assertEquals(testUtil.getUpdatedNodeResponse(), nodeResponse);

    verify(nodePersistenceService, times(1)).saveNodeDetails(any(NodeDomainDto.class));
    verify(nodePersistenceService, times(1)).findNodeByNodeIdAndOrgId(any(), any());
  }

  @Test
  @DisplayName(
      "Happy Path - Updating node when startWorkingTime and lastWorkingTime fields are provided.")
  void updateNodeDetailsTestWithStartAndLastWorkingTime()
      throws NodeDomainException, CommonServiceException {
    NodeDomainDto nodeDomainDto = testUtil.getNodeDomainDto(TestUtil.NODE_ID);
    NodeUpdationRequest nodeUpdationRequest = testUtil.getNodeUpdationRequest();
    nodeUpdationRequest.setStartWorkingTime("08:00");
    nodeUpdationRequest.setLastWorkingTime("16:00");
    NodeDomainDto updatedNodeDomainDto = testUtil.getUpdatedNodeDomainDto();
    updatedNodeDomainDto.setStartWorkingTime("08:00");
    updatedNodeDomainDto.setLastWorkingTime("16:00");
    Set<String> nodeTypes = Set.of("STORE", "FC", "MFC", "DROPSHIP VENDOR");
    when(nodeTenantBasedDBConfig.getNodeTypes(anyString())).thenReturn(nodeTypes);
    when(nodePersistenceService.findNodeByNodeIdAndOrgId(any(), any()))
        .thenReturn(Optional.of(nodeDomainDto));
    when(nodePersistenceService.saveNodeDetails(any())).thenReturn(updatedNodeDomainDto);

    NodeResponse nodeResponse =
        nodeService.updateNodeDetails(TestUtil.NODE_ID, TestUtil.ORG_ID, nodeUpdationRequest);
    Assertions.assertEquals(
        nodeUpdationRequest.getStartWorkingTime(), nodeResponse.getStartWorkingTime());

    verify(nodePersistenceService, times(1)).saveNodeDetails(any(NodeDomainDto.class));
    verify(nodePersistenceService, times(1)).findNodeByNodeIdAndOrgId(any(), any());
  }

  @Test
  void updateNodeDetailsTestNodeNotFoundException()
      throws NodeDomainException, CommonServiceException {
    NodeUpdationRequest nodeUpdationRequest = testUtil.getNodeUpdationRequest();
    when(nodePersistenceService.findNodeByNodeIdAndOrgId(any(), any()))
        .thenReturn(Optional.empty());
    Set<String> nodeTypes = Set.of("STORE", "FC", "MFC", "DROPSHIP VENDOR");
    when(nodeTenantBasedDBConfig.getNodeTypes(anyString())).thenReturn(nodeTypes);

    Exception exception =
        Assertions.assertThrows(
            CommonServiceException.class,
            () ->
                nodeService.updateNodeDetails(
                    TestUtil.NODE_ID, TestUtil.ORG_ID, nodeUpdationRequest));
    Assertions.assertEquals("Node not found with given details", exception.getMessage());

    verify(nodePersistenceService, times(0)).saveNodeDetails(any(NodeDomainDto.class));
    verify(nodePersistenceService, times(1)).findNodeByNodeIdAndOrgId(any(), any());
  }

  @Test
  void getNodeDetailsByNodeIdAndOrgIdTest() throws NodeDomainException, CommonServiceException {
    NodeDomainDto nodeDomainDto = testUtil.getNodeDomainDto(TestUtil.NODE_ID);
    NodeResponse nodeResponseExpected = testUtil.getNodeResponse();
    nodeResponseExpected.setStartWorkingTime("08:00");
    nodeResponseExpected.setLastWorkingTime("16:00");
    when(nodePersistenceService.findNodeByNodeIdAndOrgId(any(), any()))
        .thenReturn(Optional.of(nodeDomainDto));

    NodeResponse nodeResponse = nodeService.getNodeDetails(TestUtil.NODE_ID, TestUtil.ORG_ID);
    Assertions.assertEquals(nodeResponseExpected, nodeResponse);
    verify(nodePersistenceService, times(1)).findNodeByNodeIdAndOrgId(any(), any());
  }

  @Test
  void getNodeDetailsByNodeIdAndOrgIdTestException() throws NodeDomainException {
    when(nodePersistenceService.findNodeByNodeIdAndOrgId(any(), any()))
        .thenReturn(Optional.empty());

    Exception exception =
        Assertions.assertThrows(
            CommonServiceException.class,
            () -> nodeService.getNodeDetails(TestUtil.NODE_ID, TestUtil.ORG_ID));
    Assertions.assertEquals("Node not found with given details", exception.getMessage());

    verify(nodePersistenceService, times(1)).findNodeByNodeIdAndOrgId(any(), any());
  }

  @Test
  void deleteNodeTest() throws NodeDomainException, CommonServiceException {
    NodeDomainDto nodeDomainDto = testUtil.getNodeDomainDto(TestUtil.NODE_ID);
    NodeResponse nodeResponseExpected = testUtil.getNodeResponse();
    nodeResponseExpected.setStartWorkingTime("08:00");
    nodeResponseExpected.setLastWorkingTime("16:00");
    when(nodePersistenceService.findNodeByNodeIdAndOrgId(any(), any()))
        .thenReturn(Optional.of(nodeDomainDto));

    NodeResponse nodeResponse = nodeService.deleteNode(TestUtil.NODE_ID, TestUtil.ORG_ID);
    Assertions.assertEquals(nodeResponseExpected, nodeResponse);
    verify(nodePersistenceService, times(1)).findNodeByNodeIdAndOrgId(any(), any());
  }

  @Test
  void deleteNodeTestException() throws NodeDomainException {
    when(nodePersistenceService.findNodeByNodeIdAndOrgId(any(), any()))
        .thenReturn(Optional.empty());

    Exception exception =
        Assertions.assertThrows(
            CommonServiceException.class,
            () -> nodeService.deleteNode(TestUtil.NODE_ID, TestUtil.ORG_ID));
    Assertions.assertEquals("Node not found with given details", exception.getMessage());

    verify(nodePersistenceService, times(1)).findNodeByNodeIdAndOrgId(any(), any());
  }

  @Test
  void getNodeListByOrgIdTest() throws NodeDomainException, CommonServiceException {
    List<NodeDomainDto> nodeDomainDtoList = testUtil.getNodeDomainDtoList();

    Pageable pageable = PageRequest.of(1, 1, Sort.by(TestUtil.SORT_BY).ascending());
    Page<NodeDomainDto> nodeDomainDtoPage =
        new PageImpl<>(nodeDomainDtoList, pageable, nodeDomainDtoList.size());

    when(nodePersistenceService.getNodeByOrgId(any(), any(), any(), any(), any()))
        .thenReturn(nodeDomainDtoPage);

    PageParams pageParams =
        new PageParams(
            Optional.of(1),
            Optional.of(1),
            Optional.of(TestUtil.SORT_BY),
            Optional.of(TestUtil.SORT_ORDER_DESC));

    Page<NodeDto> response =
        nodeService.getNodeListByOrgId(TestUtil.ORG_ID, null, null, pageParams);

    Assertions.assertEquals(nodeDomainDtoList.size(), response.getContent().size());
    Assertions.assertEquals(2, response.getTotalPages());
    Assertions.assertEquals(1, response.getPageable().getPageSize());
    Assertions.assertEquals(2, response.getTotalElements());
    Assertions.assertEquals("nodeId: ASC", response.getSort().toString());
    Assertions.assertEquals("08:00", response.getContent().get(0).getStartWorkingTime());
    Assertions.assertEquals("16:00", response.getContent().get(1).getLastWorkingTime());

    verify(nodePersistenceService, Mockito.times(1))
        .getNodeByOrgId(any(), any(), any(), any(), any());
  }

  @Test
  void getNodeListByOrgIdDefaultSortOrderTest() throws NodeDomainException, CommonServiceException {
    List<NodeDomainDto> nodeDomainDtoList = testUtil.getNodeDomainDtoList();

    Pageable pageable = PageRequest.of(1, 1, Sort.by(TestUtil.SORT_BY).ascending());
    Page<NodeDomainDto> nodeDomainDtoPage =
        new PageImpl<>(nodeDomainDtoList, pageable, nodeDomainDtoList.size());

    when(nodePersistenceService.getNodeByOrgId(any(), any(), any(), any(), any()))
        .thenReturn(nodeDomainDtoPage);
    PageParams pageParams =
        new PageParams(
            Optional.of(1),
            Optional.of(1),
            Optional.of(TestUtil.SORT_BY),
            Optional.of(TestUtil.SORT_ORDER_DESC));

    Page<NodeDto> response =
        nodeService.getNodeListByOrgId(TestUtil.ORG_ID, null, null, pageParams);

    Assertions.assertEquals(nodeDomainDtoList.size(), response.getContent().size());
    Assertions.assertEquals(2, response.getTotalPages());
    Assertions.assertEquals(1, response.getPageable().getPageSize());
    Assertions.assertEquals(2, response.getTotalElements());
    Assertions.assertEquals("nodeId: ASC", response.getSort().toString());

    verify(nodePersistenceService, Mockito.times(1))
        .getNodeByOrgId(any(), any(), any(), any(), any());
  }

  @Test
  void getNodeListByOrgIdExceptionTest() throws NodeDomainException {
    PageParams pageParams =
        new PageParams(
            Optional.of(1),
            Optional.of(1),
            Optional.of(TestUtil.SORT_BY),
            Optional.of("invalid sort order"));

    Exception exception =
        Assertions.assertThrows(
            CommonServiceException.class,
            () -> nodeService.getNodeListByOrgId(TestUtil.ORG_ID, null, null, pageParams));

    Assertions.assertEquals(
        "Invalid sort order, consider giving either ASC or DESC", exception.getMessage());
    verify(nodePersistenceService, Mockito.times(0))
        .getNodeByOrgId(any(), any(), any(), any(), any());
  }

  @Test
  void getAllNodesTest() throws NodeDomainException {
    List<NodeDomainDto> nodeDomainDtoList = testUtil.getNodeDomainDtoList();
    Pageable pageable = PageRequest.of(1, 1, Sort.by(TestUtil.SORT_BY).ascending());
    Page<NodeDomainDto> nodeDomainDtoPage =
        new PageImpl<>(nodeDomainDtoList, pageable, nodeDomainDtoList.size());

    when(nodePersistenceService.getAllNodesPaginated(any(), any(), any(), any()))
        .thenReturn(nodeDomainDtoPage);

    Page<NodeDto> nodeDtoPage =
        nodeService.getAllNodes(1, 1, TestUtil.SORT_BY, TestUtil.SORT_ORDER_DESC);

    Assertions.assertEquals(nodeDomainDtoPage.getTotalElements(), nodeDtoPage.getContent().size());
    Assertions.assertEquals(2, nodeDtoPage.getTotalPages());
    Assertions.assertEquals(1, nodeDtoPage.getPageable().getPageSize());
    Assertions.assertEquals(2, nodeDtoPage.getTotalElements());
    Assertions.assertEquals("nodeId: ASC", nodeDomainDtoPage.getSort().toString());
    Assertions.assertEquals("08:00", nodeDtoPage.getContent().get(0).getStartWorkingTime());
    Assertions.assertEquals("16:00", nodeDtoPage.getContent().get(1).getLastWorkingTime());

    verify(nodePersistenceService, Mockito.times(1))
        .getAllNodesPaginated(any(), any(), any(), any());
  }

  @Test
  void getAllNodesExceptionTest() throws NodeDomainException {
    when(nodePersistenceService.getAllNodesPaginated(any(), any(), any(), any()))
        .thenThrow(new NodeDomainException("Failed to fetch nodes", null, null));
    Exception exception =
        Assertions.assertThrows(
            NodeDomainException.class,
            () -> nodeService.getAllNodes(1, 1, TestUtil.SORT_BY, "DESC"));

    Assertions.assertEquals("Failed to fetch nodes", exception.getMessage());
    verify(nodePersistenceService, Mockito.times(1))
        .getAllNodesPaginated(any(), any(), any(), any());
  }

  @Test
  void getAllNodeCacheKeysTest() throws NodeDomainException {
    List<NodeDomainDto> nodeDomainDtoList = testUtil.getNodeDomainDtoList();

    when(nodePersistenceService.getAllNodeEntities(any())).thenReturn(nodeDomainDtoList);

    List<NodeCacheKeyDto> response = nodeService.getAllNodeCacheKeys(2);

    Assertions.assertEquals(2, response.size());
    Assertions.assertEquals(nodeDomainDtoList.get(0).getNodeId(), response.get(0).getNodeId());
    verify(nodePersistenceService, times(1)).getAllNodeEntities(any());
  }

  @Test
  void createNodeTest_validateFields() throws NodeDomainException, CommonServiceException {
    Set<String> nodeTypes = Set.of("STORE", "FC", "MFC", "DROPSHIP VENDOR");
    when(nodeTenantBasedDBConfig.getNodeTypes(anyString())).thenReturn(nodeTypes);
    NodeRequest nodeRequest1 = testUtil.getNodeRequest();
    nodeRequest1.setCountry("IND");
    NodeRequest nodeRequest2 = testUtil.getNodeRequest();
    nodeRequest2.setTimezone("IST");
    NodeRequest nodeRequest3 = testUtil.getNodeRequest();
    nodeRequest3.setNodeType(" ");
    Assertions.assertThrows(
        CommonServiceException.class, () -> nodeService.createNode(nodeRequest1));
    Assertions.assertThrows(
        CommonServiceException.class, () -> nodeService.createNode(nodeRequest2));
    Assertions.assertThrows(
        CommonServiceException.class, () -> nodeService.createNode(nodeRequest3));
    verify(nodePersistenceService, times(0)).saveNodeDetails(any(NodeDomainDto.class));
  }

  @Test
  void getNodeListByOrgIdTestV1WithoutPageSize()
      throws NodeDomainException, CommonServiceException {
    List<NodeDomainDto> nodeDomainDtoList = testUtil.getNodeDomainDtoListV1();

    Pageable pageable = Pageable.unpaged();
    Page<NodeDomainDto> nodeDomainDtoPage =
        new PageImpl<>(nodeDomainDtoList, pageable, nodeDomainDtoList.size());

    when(nodePersistenceService.getNodeByOrgIdV1(any(), any())).thenReturn(nodeDomainDtoPage);

    Page<NodeDto> response =
        nodeService.getNodeListByOrgIdV1(
            TestUtil.ORG_ID,
            testUtil.getPageParamsWithoutPageSize(
                Optional.of(2),
                Optional.of(TestUtil.SORT_BY),
                Optional.of(TestUtil.DEFAULT_SORT_ORDER)));

    Assertions.assertEquals(nodeDomainDtoList.size(), response.getContent().size());
    Assertions.assertEquals(1, response.getTotalPages());
    Assertions.assertEquals(4, response.getTotalElements());
    Assertions.assertEquals("UNSORTED", response.getSort().toString());
    Assertions.assertEquals("08:00", response.getContent().get(0).getStartWorkingTime());
    Assertions.assertEquals("16:00", response.getContent().get(1).getLastWorkingTime());

    verify(nodePersistenceService, Mockito.times(1)).getNodeByOrgIdV1(any(), any());
  }

  @Test
  void getNodeListByOrgIdTestV1WithPageSize() throws NodeDomainException, CommonServiceException {
    List<NodeDomainDto> nodeDomainDtoList = testUtil.getNodeDomainDtoListV1();

    Pageable pageable = PageRequest.of(1, 1, Sort.by(TestUtil.SORT_BY).ascending());
    Page<NodeDomainDto> nodeDomainDtoPage =
        new PageImpl<>(nodeDomainDtoList, pageable, nodeDomainDtoList.size());

    when(nodePersistenceService.getNodeByOrgIdV1(any(), any())).thenReturn(nodeDomainDtoPage);

    Page<NodeDto> response =
        nodeService.getNodeListByOrgIdV1(
            TestUtil.ORG_ID,
            testUtil.getPageParamsWithoutPageSize(
                Optional.of(2),
                Optional.of(TestUtil.SORT_BY),
                Optional.of(TestUtil.DEFAULT_SORT_ORDER)));

    Assertions.assertEquals(nodeDomainDtoList.size(), response.getContent().size());
    Assertions.assertEquals(4, response.getTotalPages());
    Assertions.assertEquals(4, response.getTotalElements());
    Assertions.assertEquals("nodeId: ASC", response.getSort().toString());

    verify(nodePersistenceService, Mockito.times(1)).getNodeByOrgIdV1(any(), any());
  }

  @Test
  void getNodeListByOrgIdV1ExceptionTest() throws NodeDomainException {

    PageParams pageParams =
        testUtil.getPageParamsWithoutPageSize(
            Optional.of(2), Optional.of(TestUtil.SORT_BY), Optional.of("Invalid sort order"));

    Exception exception =
        Assertions.assertThrows(
            CommonServiceException.class,
            () -> nodeService.getNodeListByOrgIdV1(TestUtil.ORG_ID, pageParams));

    Assertions.assertEquals(
        "Invalid sort order, consider giving either ASC or DESC", exception.getMessage());
    verify(nodePersistenceService, Mockito.times(0)).getNodeByOrgIdV1(any(), any());
  }

  @Test
  @DisplayName("Get all unique node types.")
  void getAllUniqueNodeTypesTest() {
    when(nodePersistenceService.getAllUniqueNodeTypesByOrgId(any()))
        .thenReturn(List.of("DC", "MFC"));
    NodeTypesResponse nodeTypes = nodeService.getAllNodeTypesByOrgId(TestUtil.ORG_ID);
    Assertions.assertEquals(List.of("DC", "MFC"), nodeTypes.getNodeTypes());
    verify(nodePersistenceService, times(1)).getAllUniqueNodeTypesByOrgId(any());
  }

  @Test
  @DisplayName("Get Nodes with empty nodeIds")
  void getNodeListByOrgId_withEmptyNodeIdsTest()
      throws NodeDomainException, CommonServiceException {
    List<NodeDomainDto> nodeDomainDtoList = testUtil.getNodeDomainDtoList();

    Pageable pageable = PageRequest.of(1, 1, Sort.by(TestUtil.SORT_BY).ascending());
    Page<NodeDomainDto> nodeDomainDtoPage =
        new PageImpl<>(nodeDomainDtoList, pageable, nodeDomainDtoList.size());

    when(nodePersistenceService.getByNodeTypeAndOrgId(
            anyString(), anyString(), any(PageParams.class)))
        .thenReturn(nodeDomainDtoPage);

    PageParams pageParams =
        new PageParams(
            Optional.of(1),
            Optional.of(1),
            Optional.of(TestUtil.SORT_BY),
            Optional.of(TestUtil.SORT_ORDER_DESC));

    Page<NodeDto> response =
        nodeService.getNodeListByOrgId(TestUtil.ORG_ID, null, "STORE", pageParams);

    Assertions.assertEquals(nodeDomainDtoList.size(), response.getContent().size());
    Assertions.assertEquals(2, response.getTotalPages());
    Assertions.assertEquals(1, response.getPageable().getPageSize());
    Assertions.assertEquals(2, response.getTotalElements());
    Assertions.assertEquals("nodeId: ASC", response.getSort().toString());
    Assertions.assertEquals("08:00", response.getContent().get(0).getStartWorkingTime());
    Assertions.assertEquals("16:00", response.getContent().get(1).getLastWorkingTime());

    verify(nodePersistenceService, Mockito.times(1))
        .getByNodeTypeAndOrgId(anyString(), anyString(), any(PageParams.class));
  }

  @Test
  @DisplayName("Get Nodes with empty nodeType")
  void getNodeListByOrgId_withEmptyNodeTypeTest()
      throws NodeDomainException, CommonServiceException {
    List<NodeDomainDto> nodeDomainDtoList = testUtil.getNodeDomainDtoList();

    Pageable pageable = PageRequest.of(1, 1, Sort.by(TestUtil.SORT_BY).ascending());
    Page<NodeDomainDto> nodeDomainDtoPage =
        new PageImpl<>(nodeDomainDtoList, pageable, nodeDomainDtoList.size());

    when(nodePersistenceService.getByNodeIdInAndOrgId(
            anyString(), anyList(), any(PageParams.class)))
        .thenReturn(nodeDomainDtoPage);

    PageParams pageParams =
        new PageParams(
            Optional.of(1),
            Optional.of(1),
            Optional.of(TestUtil.SORT_BY),
            Optional.of(TestUtil.SORT_ORDER_DESC));

    Page<NodeDto> response =
        nodeService.getNodeListByOrgId(TestUtil.ORG_ID, "node1", null, pageParams);

    Assertions.assertEquals(nodeDomainDtoList.size(), response.getContent().size());
    Assertions.assertEquals(2, response.getTotalPages());
    Assertions.assertEquals(1, response.getPageable().getPageSize());
    Assertions.assertEquals(2, response.getTotalElements());
    Assertions.assertEquals("nodeId: ASC", response.getSort().toString());
    Assertions.assertEquals("08:00", response.getContent().get(0).getStartWorkingTime());
    Assertions.assertEquals("16:00", response.getContent().get(1).getLastWorkingTime());

    verify(nodePersistenceService, Mockito.times(1))
        .getByNodeIdInAndOrgId(anyString(), anyList(), any(PageParams.class));
  }
}
