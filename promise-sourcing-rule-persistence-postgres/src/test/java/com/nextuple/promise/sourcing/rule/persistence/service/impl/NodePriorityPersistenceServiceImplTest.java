/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.promise.sourcing.rule.persistence.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nextuple.common.exception.PromiseEngineException;
import com.nextuple.promise.sourcing.rule.persistence.domain.NodePriorityDomainDto;
import com.nextuple.promise.sourcing.rule.persistence.entity.NodePriorityEntity;
import com.nextuple.promise.sourcing.rule.persistence.mapper.NodePriorityEntityMapper;
import com.nextuple.promise.sourcing.rule.persistence.repository.NodePriorityRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

class NodePriorityPersistenceServiceImplTest {
  @InjectMocks private NodePriorityPersistenceServiceImpl nodePriorityPersistenceService;

  @Mock private NodePriorityRepository nodePriorityRepository;

  @Mock private NodePriorityEntityMapper nodePriorityEntityMapper;

  @InjectMocks private TestUtil testUtil;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    // Added this
    ReflectionTestUtils.setField(
        nodePriorityPersistenceService, "repository", nodePriorityRepository);
    ReflectionTestUtils.setField(
        nodePriorityPersistenceService, "mapper", nodePriorityEntityMapper);
  }

  @Test
  void saveNodePriorityEntityTest() throws PromiseEngineException {
    NodePriorityEntity nodePriorityEntity = testUtil.getNodePriorityEntity();
    NodePriorityDomainDto nodePriorityDomainDto = testUtil.getNodePriorityDomainDto();
    when(nodePriorityRepository.save(any(NodePriorityEntity.class))).thenReturn(nodePriorityEntity);
    when(nodePriorityEntityMapper.toEntity(any(NodePriorityDomainDto.class)))
        .thenReturn(nodePriorityEntity);
    when(nodePriorityEntityMapper.toDomain(any(NodePriorityEntity.class)))
        .thenReturn(nodePriorityDomainDto);
    NodePriorityDomainDto saved_nodePriorityEntityResponse =
        nodePriorityPersistenceService.saveNodePriorityEntity(nodePriorityDomainDto);
    assertEquals(nodePriorityDomainDto, saved_nodePriorityEntityResponse);
    verify(nodePriorityRepository, times(1)).save(any());
  }

  @Test
  void saveNodePriorityEntityExceptionTest() {
    NodePriorityEntity nodePriorityEntity = testUtil.getNodePriorityEntity();
    when(nodePriorityEntityMapper.toEntity(any(NodePriorityDomainDto.class)))
        .thenReturn(nodePriorityEntity);
    when(nodePriorityRepository.save(any(NodePriorityEntity.class)))
        .thenThrow(new RuntimeException("error"));
    Exception exception =
        assertThrows(
            PromiseEngineException.class,
            () -> {
              nodePriorityPersistenceService.saveNodePriorityEntity(
                  testUtil.getNodePriorityDomainDto());
            });

    assertEquals("Unable to save node group details entity", exception.getMessage());
  }

  @Test
  void fetchNodePriorityListByOrgIdAndNodeGroupIdAndNodeIdTest() throws PromiseEngineException {
    NodePriorityEntity nodePriorityEntity = testUtil.getNodePriorityEntity();
    NodePriorityDomainDto nodePriorityDomainDto = testUtil.getNodePriorityDomainDto();
    when(nodePriorityEntityMapper.toDomain(anyList())).thenReturn(List.of(nodePriorityDomainDto));
    when(nodePriorityRepository.findByOrgIdAndNodeGroupIdAndNodeId(
            anyString(), anyLong(), anyString()))
        .thenReturn(List.of(nodePriorityEntity));

    List<NodePriorityDomainDto> saved_nodePriorityEntityResponse =
        nodePriorityPersistenceService.fetchNodePriorityListByOrgIdAndNodeGroupIdAndNodeId(
            TestUtil.ORG_ID, TestUtil.NODE_GROUP_ID, TestUtil.NODE_ID);
    assertEquals(nodePriorityDomainDto, saved_nodePriorityEntityResponse.get(0));
    verify(nodePriorityRepository, times(1))
        .findByOrgIdAndNodeGroupIdAndNodeId(anyString(), anyLong(), anyString());
  }

  @Test
  void fetchNodePriorityListByOrgIdAndNodeGroupIdAndNodeIdExceptionTest() {
    when(nodePriorityRepository.findByOrgIdAndNodeGroupIdAndNodeId(
            anyString(), anyLong(), anyString()))
        .thenThrow(new RuntimeException("error"));
    Exception exception =
        assertThrows(
            PromiseEngineException.class,
            () -> {
              nodePriorityPersistenceService.fetchNodePriorityListByOrgIdAndNodeGroupIdAndNodeId(
                  TestUtil.ORG_ID, TestUtil.NODE_GROUP_ID, TestUtil.NODE_ID);
            });

    assertEquals(
        "Unable to fetch node group details entity list by orgId , nodeGroupId and nodeId",
        exception.getMessage());
  }

  @Test
  void fetchNodePriorityEntityByIdTest() throws PromiseEngineException {
    NodePriorityEntity nodePriorityEntity = testUtil.getNodePriorityEntity();
    NodePriorityDomainDto nodePriorityDomainDto = testUtil.getNodePriorityDomainDto();
    when(nodePriorityEntityMapper.toDomain(any(NodePriorityEntity.class)))
        .thenReturn(nodePriorityDomainDto);
    when(nodePriorityRepository.findByIdAndOrgId(anyLong(), anyString()))
        .thenReturn(Optional.of(nodePriorityEntity));
    Optional<NodePriorityDomainDto> saved_nodePriorityEntityResponse =
        nodePriorityPersistenceService.fetchNodePriorityEntityByIdAndOrgId(
            TestUtil.NODE_GROUP_DETAIL_ID, TestUtil.ORG_ID);
    assertEquals(nodePriorityDomainDto, saved_nodePriorityEntityResponse.get());
    verify(nodePriorityRepository, times(1)).findByIdAndOrgId(anyLong(), anyString());
  }

  @Test
  void fetchNodePriorityEntityByIdExceptionTest() {
    when(nodePriorityRepository.findByIdAndOrgId(anyLong(), anyString()))
        .thenThrow(new RuntimeException("error"));
    Exception exception =
        assertThrows(
            PromiseEngineException.class,
            () -> {
              nodePriorityPersistenceService.fetchNodePriorityEntityByIdAndOrgId(
                  TestUtil.NODE_GROUP_DETAIL_ID, TestUtil.ORG_ID);
            });

    assertEquals(
        "Unable to fetch node group details entity by id and orgId", exception.getMessage());
  }

  @Test
  void fetchNodePriorityListByOrgIdAndNodeGroupIdTest() throws PromiseEngineException {
    NodePriorityEntity nodePriorityEntity = testUtil.getNodePriorityEntity();
    NodePriorityDomainDto nodePriorityDomainDto = testUtil.getNodePriorityDomainDto();
    when(nodePriorityEntityMapper.toDomain(anyList())).thenReturn(List.of(nodePriorityDomainDto));
    when(nodePriorityRepository.findByOrgIdAndNodeGroupId(anyString(), anyLong()))
        .thenReturn(List.of(nodePriorityEntity));

    List<NodePriorityDomainDto> saved_nodePriorityEntityResponse =
        nodePriorityPersistenceService.fetchNodePriorityListByOrgIdAndNodeGroupId(
            TestUtil.ORG_ID, TestUtil.NODE_GROUP_ID);
    assertEquals(nodePriorityDomainDto, saved_nodePriorityEntityResponse.get(0));
    verify(nodePriorityRepository, times(1)).findByOrgIdAndNodeGroupId(anyString(), anyLong());
  }

  @Test
  void fetchNodePriorityListByOrgIdAndNodeGroupIdExceptionTest() {
    when(nodePriorityRepository.findByOrgIdAndNodeGroupId(anyString(), anyLong()))
        .thenThrow(new RuntimeException("error"));
    Exception exception =
        assertThrows(
            PromiseEngineException.class,
            () -> {
              nodePriorityPersistenceService.fetchNodePriorityListByOrgIdAndNodeGroupId(
                  TestUtil.ORG_ID, TestUtil.NODE_GROUP_ID);
            });

    assertEquals(
        "Unable to fetch node group details entity list by orgId , nodeGroupId",
        exception.getMessage());
  }

  @Test
  void deleteNodePriorityEntityTest() throws PromiseEngineException {
    NodePriorityEntity nodePriorityEntity = testUtil.getNodePriorityEntity();
    NodePriorityDomainDto nodePriorityDomainDto = testUtil.getNodePriorityDomainDto();
    doNothing().when(nodePriorityRepository).delete(any(NodePriorityEntity.class));
    when(nodePriorityEntityMapper.toEntity(any(NodePriorityDomainDto.class)))
        .thenReturn(nodePriorityEntity);
    nodePriorityPersistenceService.deleteNodePriorityEntity(nodePriorityDomainDto);
    verify(nodePriorityRepository, times(1)).delete(any(NodePriorityEntity.class));
  }

  @Test
  void deleteNodePriorityEntityExceptionTest() {
    NodePriorityEntity nodePriorityEntity = testUtil.getNodePriorityEntity();
    NodePriorityDomainDto nodePriorityDomainDto = testUtil.getNodePriorityDomainDto();
    when(nodePriorityEntityMapper.toEntity(any(NodePriorityDomainDto.class)))
        .thenReturn(nodePriorityEntity);
    doThrow(new RuntimeException("error"))
        .when(nodePriorityRepository)
        .delete(any(NodePriorityEntity.class));
    Exception exception =
        assertThrows(
            PromiseEngineException.class,
            () -> {
              nodePriorityPersistenceService.deleteNodePriorityEntity(nodePriorityDomainDto);
            });

    assertEquals("Unable to delete node group details entity", exception.getMessage());
  }

  @Test
  void findByNodeGroupIdsInOrderByPriorityTest() throws PromiseEngineException {
    List<Long> nodeGroupEntityIds = testUtil.getNodeGroupEntityIds(5);
    List<NodePriorityEntity> expectedNodePriorityEntityList = testUtil.getNodePriorityEntityList();
    when(nodePriorityEntityMapper.toDomain(anyList()))
        .thenReturn(List.of(testUtil.getNodePriorityDomainDto()));
    when(nodePriorityRepository.findByNodeGroupIdInOrderByPriority(nodeGroupEntityIds))
        .thenReturn(expectedNodePriorityEntityList);
    List<NodePriorityDomainDto> nodePriorityEntityList =
        nodePriorityPersistenceService.findByNodeGroupIdsInOrderByPriority(nodeGroupEntityIds);
    assertEquals(testUtil.getNodePriorityDomainDto(), nodePriorityEntityList.get(0));
    verify(nodePriorityRepository, times(1)).findByNodeGroupIdInOrderByPriority(nodeGroupEntityIds);
  }

  @Test
  @DisplayName("Fetch node priority list by node id and org id")
  void fetchNodePriorityListByNodeIdAndOrgIdTest() throws PromiseEngineException {
    when(nodePriorityRepository.findByNodeIdAndOrgId(anyString(), anyString()))
        .thenReturn(testUtil.getNodePriorityEntityList());
    when(nodePriorityEntityMapper.toDomain(anyList()))
        .thenReturn(List.of(testUtil.getNodePriorityDomainDto()));
    List<NodePriorityDomainDto> dtos =
        nodePriorityPersistenceService.fetchNodePriorityListByNodeIdAndOrgId(
            TestUtil.ORG_ID, TestUtil.NODE_ID);
    Assertions.assertEquals(1, dtos.size());
    Assertions.assertEquals(TestUtil.ORG_ID, dtos.get(0).getOrgId());
    Assertions.assertEquals(TestUtil.NODE_ID, dtos.get(0).getNodeId());
  }

  @Test
  @DisplayName("Fetch node priority list by node id and org id - Exception test")
  void fetchNodePriorityListByNodeIdAndOrgIdExceptionTest() {
    when(nodePriorityRepository.findByNodeIdAndOrgId(anyString(), anyString()))
        .thenThrow(new RuntimeException("Error occurred"));
    when(nodePriorityEntityMapper.toDomain(anyList()))
        .thenReturn(List.of(testUtil.getNodePriorityDomainDto()));
    Assertions.assertThrows(
        PromiseEngineException.class,
        () ->
            nodePriorityPersistenceService.fetchNodePriorityListByNodeIdAndOrgId(
                TestUtil.ORG_ID, TestUtil.NODE_ID));
  }
}
