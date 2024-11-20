/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.node.persistence.service.impl;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nextuple.common.pojo.PageParams;
import com.nextuple.common.pojo.PageProperties;
import com.nextuple.node.persistence.domain.NodeDomainDto;
import com.nextuple.node.persistence.entity.NodeEntity;
import com.nextuple.node.persistence.exception.NodeDomainException;
import com.nextuple.node.persistence.mapper.NodeEntityMapper;
import com.nextuple.node.persistence.repository.NodeRepository;
import com.nextuple.node.persistence.util.TestUtil;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.util.ReflectionTestUtils;

class NodePersistenceServiceImplTest {
  @Mock private NodeRepository nodeRepository;
  @Mock private NodeEntityMapper nodeEntityMapper;
  @Mock private PageProperties pageProperties;
  @InjectMocks private NodePersistenceServiceImpl nodePersistenceService;
  @InjectMocks private TestUtil testUtil;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    ReflectionTestUtils.setField(nodePersistenceService, "pageProperties", pageProperties);
    ReflectionTestUtils.setField(nodePersistenceService, "repository", nodeRepository);
    ReflectionTestUtils.setField(nodePersistenceService, "mapper", nodeEntityMapper);
  }

  @Test
  void saveNodeTest() throws NodeDomainException {
    NodeEntity nodeEntity = testUtil.getNodeEntity();
    when(nodeEntityMapper.toEntity(any(NodeDomainDto.class))).thenReturn(nodeEntity);
    when(nodeEntityMapper.toDomain(any(NodeEntity.class))).thenReturn(testUtil.getNodeDomainDto());
    when(nodeRepository.save(any())).thenReturn(nodeEntity);
    NodeDomainDto node = nodePersistenceService.saveNodeDetails(testUtil.getNodeDomainDto());
    Assertions.assertEquals(nodeEntity.getNodeId(), node.getNodeId());
    verify(nodeRepository, times(1)).save(any());
  }

  @Test
  void saveNodeExceptionTest() {
    when(nodeRepository.save(any())).thenThrow(new RuntimeException("Error while saving"));
    Exception exception =
        assertThrows(
            NodeDomainException.class,
            () -> nodePersistenceService.saveNodeDetails(testUtil.getNodeDomainDto()));
    Assertions.assertEquals("Error while saving the node", exception.getMessage());
    verify(nodeRepository, times(1)).save(any());
  }

  @Test
  void getNodeDetailsTest() throws NodeDomainException {
    NodeEntity nodeEntity = testUtil.getNodeEntity();
    when(nodeEntityMapper.toEntity(any(NodeDomainDto.class))).thenReturn(nodeEntity);
    when(nodeEntityMapper.toDomain(any(NodeEntity.class))).thenReturn(testUtil.getNodeDomainDto());
    when(nodeRepository.findById(any())).thenReturn(Optional.of(nodeEntity));
    Optional<NodeDomainDto> optionalNodeDomainDto =
        nodePersistenceService.findNodeByNodeIdAndOrgId(TestUtil.NODE_ID, TestUtil.ORG_ID);
    Assertions.assertEquals(nodeEntity.getNodeId(), optionalNodeDomainDto.get().getNodeId());
    verify(nodeRepository, times(1)).findById(any());
  }

  @Test
  void getNodeDetailsTestException() {
    when(nodeRepository.findById(any()))
        .thenThrow(new RuntimeException("Error while fetching details"));
    Exception exception =
        assertThrows(
            NodeDomainException.class,
            () ->
                nodePersistenceService.findNodeByNodeIdAndOrgId(TestUtil.NODE_ID, TestUtil.ORG_ID));
    Assertions.assertEquals("Error while finding node", exception.getMessage());
    verify(nodeRepository, times(1)).findById(any());
  }

  @Test
  void nodeDeletionTest() throws NodeDomainException {
    when(nodeEntityMapper.toEntity(any(NodeDomainDto.class))).thenReturn(testUtil.getNodeEntity());
    when(nodeEntityMapper.toDomain(any(NodeEntity.class))).thenReturn(testUtil.getNodeDomainDto());
    doNothing().when(nodeRepository).delete(any());
    nodePersistenceService.deleteNode(testUtil.getNodeDomainDto());
    verify(nodeRepository, times(1)).delete(any());
  }

  @Test
  void nodeDeletionTestException() {
    doThrow(new RuntimeException("error while deleting")).when(nodeRepository).delete(any());
    Exception exception =
        assertThrows(
            NodeDomainException.class,
            () -> nodePersistenceService.deleteNode(testUtil.getNodeDomainDto()));
    Assertions.assertEquals("Error while deleting node", exception.getMessage());
    verify(nodeRepository, times(1)).delete(any());
  }

  @Test
  void getNodeByOrgIdDefaultSortOrderTest() throws NodeDomainException {
    List<NodeEntity> nodeEntityList = testUtil.getNodeEntityList();
    Pageable pageable = PageRequest.of(1, 1, Sort.by(TestUtil.SORT_BY).ascending());
    Page<NodeEntity> nodeEntityPage =
        new PageImpl<>(nodeEntityList, pageable, nodeEntityList.size());
    when(nodeRepository.findNodeByOrgId(anyString(), any(Pageable.class)))
        .thenReturn(nodeEntityPage);
    when(nodeEntityMapper.toDomain(any(NodeEntity.class)))
        .thenReturn(testUtil.getNodeDomainDtoList().get(0))
        .thenReturn(testUtil.getNodeDomainDtoList().get(1));
    Page<NodeDomainDto> response =
        nodePersistenceService.getNodeByOrgId(TestUtil.ORG_ID, 1, 1, TestUtil.SORT_BY, "ASC");
    Assertions.assertEquals(nodeEntityList.size(), response.getContent().size());
    Assertions.assertEquals(2, response.getTotalPages());
    Assertions.assertEquals(1, response.getPageable().getPageSize());
    Assertions.assertEquals(2, response.getTotalElements());
    Assertions.assertEquals("nodeId: ASC", response.getSort().toString());
    verify(nodeRepository, times(1)).findNodeByOrgId(anyString(), any(Pageable.class));
  }

  @Test
  void getNodeByOrgIdDESCSortOrderTest() throws NodeDomainException {
    List<NodeEntity> nodeEntityList = testUtil.getNodeEntityList();
    Pageable pageable = PageRequest.of(1, 1, Sort.by(TestUtil.SORT_BY).descending());
    Page<NodeEntity> nodeEntityPage =
        new PageImpl<>(nodeEntityList, pageable, nodeEntityList.size());
    when(nodeRepository.findNodeByOrgId(anyString(), any(Pageable.class)))
        .thenReturn(nodeEntityPage);
    when(nodeEntityMapper.toDomain(any(NodeEntity.class)))
        .thenReturn(testUtil.getNodeDomainDtoList().get(0))
        .thenReturn(testUtil.getNodeDomainDtoList().get(1));
    Page<NodeDomainDto> response =
        nodePersistenceService.getNodeByOrgId(TestUtil.ORG_ID, 1, 1, TestUtil.SORT_BY, "desc");
    Assertions.assertEquals(nodeEntityList.size(), response.getContent().size());
    Assertions.assertEquals(2, response.getTotalPages());
    Assertions.assertEquals(1, response.getPageable().getPageSize());
    Assertions.assertEquals(2, response.getTotalElements());
    Assertions.assertEquals("nodeId: DESC", response.getSort().toString());
    verify(nodeRepository, times(1)).findNodeByOrgId(anyString(), any(Pageable.class));
  }

  @Test
  void getNodeByOrgIdTestException() {
    when(nodeRepository.findNodeByOrgId(anyString(), any(Pageable.class)))
        .thenThrow(new RuntimeException("Error while fetching node list"));
    Exception exception =
        assertThrows(
            NodeDomainException.class,
            () ->
                nodePersistenceService.getNodeByOrgId(
                    TestUtil.ORG_ID, 1, 1, TestUtil.SORT_BY, "ASC"));
    Assertions.assertEquals("Error while finding node list", exception.getMessage());
    verify(nodeRepository, times(1)).findNodeByOrgId(anyString(), any(Pageable.class));
  }

  @Test
  void getAllNodesPaginatedTest() throws NodeDomainException {
    List<NodeEntity> nodeEntityList = testUtil.getNodeEntityList();
    Pageable pageable = PageRequest.of(1, 1, Sort.by(TestUtil.SORT_BY).ascending());
    Page<NodeEntity> nodeEntityPage =
        new PageImpl<>(nodeEntityList, pageable, nodeEntityList.size());
    when(nodeRepository.findAll(any(Pageable.class))).thenReturn(nodeEntityPage);
    when(nodeEntityMapper.toDomain(any(NodeEntity.class)))
        .thenReturn(testUtil.getNodeDomainDtoList().get(0))
        .thenReturn(testUtil.getNodeDomainDtoList().get(1));
    Page<NodeDomainDto> response =
        nodePersistenceService.getAllNodesPaginated(1, 1, TestUtil.SORT_BY, "ASC");
    Assertions.assertEquals(nodeEntityList.size(), response.getContent().size());
    Assertions.assertEquals(2, response.getTotalPages());
    Assertions.assertEquals(1, response.getPageable().getPageSize());
    Assertions.assertEquals(2, response.getTotalElements());
    Assertions.assertEquals("nodeId: ASC", response.getSort().toString());
    verify(nodeRepository, times(1)).findAll(any(Pageable.class));
  }

  @Test
  void getAllNodesPaginatedTestException() {
    when(nodeRepository.findAll(any(Pageable.class)))
        .thenThrow(new RuntimeException("Error while fetching node list"));
    Exception exception =
        assertThrows(
            NodeDomainException.class,
            () -> nodePersistenceService.getAllNodesPaginated(1, 1, TestUtil.SORT_BY, "ASC"));
    Assertions.assertEquals("Error while finding node list", exception.getMessage());
    verify(nodeRepository, times(1)).findAll(any(Pageable.class));
  }

  @Test
  void getAllNodeEntitiesTest() throws NodeDomainException {
    List<NodeEntity> nodeEntities = testUtil.getNodeEntityList();
    when(nodeRepository.findAllNodeEntities(any())).thenReturn(nodeEntities);
    when(nodeEntityMapper.toDomain(any(List.class))).thenReturn(testUtil.getNodeDomainDtoList());
    List<NodeDomainDto> responseList = nodePersistenceService.getAllNodeEntities(2);
    Assertions.assertEquals(2, responseList.size());
    Assertions.assertEquals(nodeEntities.get(0).getNodeId(), responseList.get(0).getNodeId());
    verify(nodeRepository, times(1)).findAllNodeEntities(any());
  }

  @Test
  void getAllNodeEntitiesExceptionTest() {
    when(nodeRepository.findAllNodeEntities(any()))
        .thenThrow(new RuntimeException("Error while fetching all node records"));
    Exception exception =
        assertThrows(NodeDomainException.class, () -> nodePersistenceService.getAllNodeEntities(2));
    Assertions.assertEquals("Error while fetching all node records", exception.getMessage());
    verify(nodeRepository, times(1)).findAllNodeEntities(any());
  }

  @Test
  void getNodeByOrgIdV1WithoutPageSizeSortOrderTest() throws NodeDomainException {
    List<NodeEntity> nodeEntityList = testUtil.getNodeEntityListV1();
    when(pageProperties.getPageNo()).thenReturn(2);
    Pageable pageable = Pageable.unpaged();
    Page<NodeEntity> nodeEntityPage =
        new PageImpl<>(nodeEntityList, pageable, nodeEntityList.size());
    when(nodeRepository.findNodeByOrgId(anyString(), any(Pageable.class)))
        .thenReturn(nodeEntityPage);
    when(nodeEntityMapper.toDomain(any(NodeEntity.class)))
        .thenReturn(testUtil.getNodeDomainDtoList().get(0))
        .thenReturn(testUtil.getNodeDomainDtoList().get(1));
    PageParams pageParams =
        testUtil.getPageParamsWithoutPageSize(
            Optional.of(2),
            Optional.of(TestUtil.SORT_BY),
            Optional.of(TestUtil.DEFAULT_SORT_ORDER));
    Page<NodeDomainDto> response =
        nodePersistenceService.getNodeByOrgIdV1(TestUtil.ORG_ID, pageParams);
    Assertions.assertEquals(nodeEntityList.size(), response.getContent().size());
    Assertions.assertEquals(1, response.getTotalPages());
    Assertions.assertEquals(3, response.getTotalElements());
    Assertions.assertEquals("UNSORTED", response.getSort().toString());
    verify(nodeRepository, times(1)).findNodeByOrgId(anyString(), any(Pageable.class));
  }

  @Test
  void getNodeByOrgIdV1WithPageSizeSortOrderTest() throws NodeDomainException {
    List<NodeEntity> nodeEntityList = testUtil.getNodeEntityListV1();
    when(pageProperties.getPageNo()).thenReturn(1);
    when(pageProperties.getPageSize()).thenReturn(1);
    Pageable pageable = PageRequest.of(1, 1, Sort.by(TestUtil.SORT_BY).ascending());
    Page<NodeEntity> nodeEntityPage =
        new PageImpl<>(nodeEntityList, pageable, nodeEntityList.size());
    when(nodeRepository.findNodeByOrgId(anyString(), any(Pageable.class)))
        .thenReturn(nodeEntityPage);
    PageParams pageParams =
        testUtil.getPageParams(
            Optional.of(2),
            Optional.of(1),
            Optional.of(TestUtil.SORT_BY),
            Optional.of(TestUtil.DEFAULT_SORT_ORDER));
    when(nodeEntityMapper.toDomain(any(NodeEntity.class)))
        .thenReturn(testUtil.getNodeDomainDtoList().get(0))
        .thenReturn(testUtil.getNodeDomainDtoList().get(1));
    Page<NodeDomainDto> response =
        nodePersistenceService.getNodeByOrgIdV1(TestUtil.ORG_ID, pageParams);
    Assertions.assertEquals(nodeEntityList.size(), response.getContent().size());
    Assertions.assertEquals(3, response.getTotalPages());
    Assertions.assertEquals(3, response.getTotalElements());
    Assertions.assertEquals(1, response.getPageable().getPageSize());
    Assertions.assertEquals("nodeId: ASC", response.getSort().toString());
    verify(nodeRepository, times(1)).findNodeByOrgId(anyString(), any(Pageable.class));
  }

  @Test
  void getNodeByOrgIdV1WithPageSizeSortOrderDESCTest() throws NodeDomainException {
    List<NodeEntity> nodeEntityList = testUtil.getNodeEntityListV1();
    when(pageProperties.getPageNo()).thenReturn(1);
    when(pageProperties.getPageSize()).thenReturn(1);
    Pageable pageable = PageRequest.of(1, 1, Sort.by(TestUtil.SORT_BY).descending());
    Page<NodeEntity> nodeEntityPage =
        new PageImpl<>(nodeEntityList, pageable, nodeEntityList.size());
    when(nodeRepository.findNodeByOrgId(anyString(), any(Pageable.class)))
        .thenReturn(nodeEntityPage);
    when(nodeEntityMapper.toDomain(any(NodeEntity.class)))
        .thenReturn(testUtil.getNodeDomainDtoList().get(0))
        .thenReturn(testUtil.getNodeDomainDtoList().get(1));
    PageParams pageParams =
        testUtil.getPageParamsWithoutPageSize(
            Optional.of(2), Optional.of(TestUtil.SORT_BY), Optional.of(TestUtil.SORT_ORDER_DESC));
    Page<NodeDomainDto> response =
        nodePersistenceService.getNodeByOrgIdV1(TestUtil.ORG_ID, pageParams);
    Assertions.assertEquals("nodeId: DESC", response.getSort().toString());
    verify(nodeRepository, times(1)).findNodeByOrgId(anyString(), any(Pageable.class));
  }

  @Test
  void getNodeByOrgIdV1TestException() {
    when(nodeRepository.findNodeByOrgId(anyString(), any(Pageable.class)))
        .thenThrow(new RuntimeException("Error while fetching node list"));
    PageParams pageParams =
        testUtil.getPageParamsWithoutPageSize(
            Optional.of(2),
            Optional.of(TestUtil.SORT_BY),
            Optional.of(TestUtil.DEFAULT_SORT_ORDER));
    Exception exception =
        assertThrows(
            NodeDomainException.class,
            () -> nodePersistenceService.getNodeByOrgIdV1(TestUtil.ORG_ID, pageParams));
    Assertions.assertEquals("Error while finding node list", exception.getMessage());
    verify(nodeRepository, times(1)).findNodeByOrgId(anyString(), any(Pageable.class));
  }

  @Test
  @DisplayName("Get all unique node types associated to orgId")
  void getAllUniqueNodeTypesTest() {
    when(nodeRepository.findDistinctNodeTypesByOrgId(any())).thenReturn(List.of("DC", "MFC"));
    List<String> nodeTypes = nodePersistenceService.getAllUniqueNodeTypesByOrgId(TestUtil.ORG_ID);
    Assertions.assertEquals(List.of("DC", "MFC"), nodeTypes);
    verify(nodeRepository, times(1)).findDistinctNodeTypesByOrgId(any());
  }

  @Test
  @DisplayName("Get Nodes by Node Type and Org ID")
  void getByNodeTypeAndOrgIdTest() throws NodeDomainException {
    List<NodeEntity> nodeEntityList = testUtil.getNodeEntityList();
    Pageable pageable = PageRequest.of(1, 10, Sort.by("nodeName").ascending());
    Page<NodeEntity> nodeEntityPage =
        new PageImpl<>(nodeEntityList, pageable, nodeEntityList.size());

    when(pageProperties.getPageNo()).thenReturn(1);
    when(pageProperties.getPageSize()).thenReturn(10);
    when(nodeRepository.findByNodeTypeAndOrgId(anyString(), anyString(), any(Pageable.class)))
        .thenReturn(nodeEntityPage);
    when(nodeEntityMapper.toDomain(any(NodeEntity.class)))
        .thenReturn(testUtil.getNodeDomainDtoList().get(0))
        .thenReturn(testUtil.getNodeDomainDtoList().get(1));

    PageParams pageParams =
        new PageParams(
            Optional.of(1), Optional.of(10), Optional.of("nodeName"), Optional.of("ASC"));
    Page<NodeDomainDto> response =
        nodePersistenceService.getByNodeTypeAndOrgId(
            TestUtil.ORG_ID, TestUtil.NODE_TYPE, pageParams);

    Assertions.assertEquals(nodeEntityList.size(), response.getContent().size());
    Assertions.assertEquals(2, response.getTotalPages());
    Assertions.assertEquals("nodeName: ASC", response.getSort().toString());

    verify(nodeRepository, times(1))
        .findByNodeTypeAndOrgId(anyString(), anyString(), any(Pageable.class));
  }

  @Test
  @DisplayName("Get Nodes by Node Type and Org ID: Exception")
  void getByNodeTypeAndOrgIdTestException() {
    when(nodeRepository.findByNodeTypeAndOrgId(anyString(), anyString(), any(Pageable.class)))
        .thenThrow(new RuntimeException("Error while fetching node list"));

    PageParams pageParams =
        new PageParams(
            Optional.of(1), Optional.of(10), Optional.of("nodeName"), Optional.of("ASC"));
    Exception exception =
        assertThrows(
            NodeDomainException.class,
            () ->
                nodePersistenceService.getByNodeTypeAndOrgId(
                    TestUtil.ORG_ID, TestUtil.NODE_TYPE, pageParams));

    Assertions.assertEquals("Error while finding node list", exception.getMessage());

    verify(nodeRepository, times(1))
        .findByNodeTypeAndOrgId(anyString(), anyString(), any(Pageable.class));
  }

  @Test
  @DisplayName("Get Nodes by Node IDs and Org ID")
  void getByNodeIdInAndOrgIdTest() throws NodeDomainException {
    List<NodeEntity> nodeEntityList = testUtil.getNodeEntityList();
    Pageable pageable = PageRequest.of(1, 10, Sort.by("nodeName").ascending());
    Page<NodeEntity> nodeEntityPage =
        new PageImpl<>(nodeEntityList, pageable, nodeEntityList.size());

    when(pageProperties.getPageNo()).thenReturn(1);
    when(pageProperties.getPageSize()).thenReturn(10);
    when(nodeRepository.findByNodeIdInAndOrgId(anyList(), anyString(), any(Pageable.class)))
        .thenReturn(nodeEntityPage);
    when(nodeEntityMapper.toDomain(any(NodeEntity.class)))
        .thenReturn(testUtil.getNodeDomainDtoList().get(0))
        .thenReturn(testUtil.getNodeDomainDtoList().get(1));

    List<String> nodeIdsList = List.of("NodeId1", "NodeId2");
    PageParams pageParams =
        new PageParams(
            Optional.of(1), Optional.of(10), Optional.of("nodeName"), Optional.of("ASC"));
    Page<NodeDomainDto> response =
        nodePersistenceService.getByNodeIdInAndOrgId(TestUtil.ORG_ID, nodeIdsList, pageParams);

    Assertions.assertEquals(nodeEntityList.size(), response.getContent().size());
    Assertions.assertEquals(2, response.getTotalPages());
    Assertions.assertEquals("nodeName: ASC", response.getSort().toString());

    verify(nodeRepository, times(1))
        .findByNodeIdInAndOrgId(anyList(), anyString(), any(Pageable.class));
  }

  @Test
  @DisplayName("Get Nodes by Node IDs and Org ID: Exception")
  void getByNodeIdInAndOrgIdTestException() {
    when(nodeRepository.findByNodeIdInAndOrgId(anyList(), anyString(), any(Pageable.class)))
        .thenThrow(new RuntimeException("Error while fetching node list"));

    List<String> nodeIdsList = List.of("NodeId1", "NodeId2");
    PageParams pageParams =
        new PageParams(
            Optional.of(1), Optional.of(10), Optional.of("nodeName"), Optional.of("ASC"));
    Exception exception =
        assertThrows(
            NodeDomainException.class,
            () ->
                nodePersistenceService.getByNodeIdInAndOrgId(
                    TestUtil.ORG_ID, nodeIdsList, pageParams));

    Assertions.assertEquals("Error while finding node list", exception.getMessage());

    verify(nodeRepository, times(1))
        .findByNodeIdInAndOrgId(anyList(), anyString(), any(Pageable.class));
  }
}
