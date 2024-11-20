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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import com.nextuple.common.exception.PromiseEngineException;
import com.nextuple.promise.sourcing.rule.persistence.domain.NodeGroupDomainDto;
import com.nextuple.promise.sourcing.rule.persistence.entity.NodeGroupEntity;
import com.nextuple.promise.sourcing.rule.persistence.entity.key.NodeGroupKey;
import com.nextuple.promise.sourcing.rule.persistence.mapper.NodeGroupEntityMapper;
import com.nextuple.promise.sourcing.rule.persistence.repository.NodeGroupRepository;
import com.nextuple.promise.sourcing.rule.persistence.repository.SourcingRuleDetailsRepository;
import com.nextuple.promise.sourcing.rule.persistence.repository.SourcingRulesConfigurationRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
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

class NodeGroupPersistenceServiceImplTest {
  @InjectMocks private NodeGroupPersistenceServiceImpl nodeGroupPersistenceService;

  @Mock private NodeGroupRepository nodeGroupRepository;
  @Mock private SourcingRuleDetailsRepository sourcingRuleDetailsRepository;

  @Mock private NodeGroupEntityMapper nodeGroupEntityMapper;

  @Mock private SourcingRulesConfigurationRepository rulesConfigurationRepository;

  @InjectMocks private TestUtil testUtil;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    ReflectionTestUtils.setField(
        nodeGroupPersistenceService,
        "sourcingRuleDetailsRepository",
        sourcingRuleDetailsRepository);
    ReflectionTestUtils.setField(
        nodeGroupPersistenceService, "rulesConfigurationRepository", rulesConfigurationRepository);
    ReflectionTestUtils.setField(nodeGroupPersistenceService, "repository", nodeGroupRepository);
    ReflectionTestUtils.setField(nodeGroupPersistenceService, "mapper", nodeGroupEntityMapper);
  }

  @Test
  void saveNodeGroupEntityTest() throws PromiseEngineException {
    NodeGroupEntity nodeGroupEntity = testUtil.getNodeGroupEntity();
    NodeGroupDomainDto nodeGroupDomainDto = testUtil.getNodeGroupDomainDto();
    when(nodeGroupRepository.save(any(NodeGroupEntity.class))).thenReturn(nodeGroupEntity);
    when(nodeGroupEntityMapper.toEntity(any(NodeGroupDomainDto.class))).thenReturn(nodeGroupEntity);
    when(nodeGroupEntityMapper.toDomain(any(NodeGroupEntity.class))).thenReturn(nodeGroupDomainDto);
    NodeGroupDomainDto saved_nodeGroupEntityResponse =
        nodeGroupPersistenceService.saveNodeGroup(nodeGroupDomainDto);
    assertEquals(nodeGroupDomainDto, saved_nodeGroupEntityResponse);
    verify(nodeGroupRepository, times(1)).save(any());
  }

  @Test
  void saveNodeGroupEntityExceptionTest() {
    when(nodeGroupEntityMapper.toEntity(any(NodeGroupDomainDto.class)))
        .thenReturn(testUtil.getNodeGroupEntity());
    when(nodeGroupRepository.save(any(NodeGroupEntity.class)))
        .thenThrow(new RuntimeException("error"));
    Exception exception =
        assertThrows(
            PromiseEngineException.class,
            () -> {
              nodeGroupPersistenceService.saveNodeGroup(testUtil.getNodeGroupDomainDto());
            });

    assertEquals("Unable to save node group", exception.getMessage());
  }

  @Test
  void fetchNodeGroupEntityTest() throws PromiseEngineException {
    NodeGroupDomainDto nodeGroupDomainDto = testUtil.getNodeGroupDomainDto();
    when(nodeGroupRepository.findById(anyLong()))
        .thenReturn(Optional.of(testUtil.getNodeGroupEntity()));
    when(nodeGroupEntityMapper.toDomain(any(NodeGroupEntity.class))).thenReturn(nodeGroupDomainDto);
    when(nodeGroupEntityMapper.toEntityKey(any()))
        .thenReturn(NodeGroupKey.builder().id(TestUtil.ID).build());
    Optional<NodeGroupDomainDto> saved_nodeGroupEntityResponse =
        nodeGroupPersistenceService.fetchNodeGroupById(TestUtil.NODE_GROUP_ID);
    assertEquals(nodeGroupDomainDto, saved_nodeGroupEntityResponse.get());
    verify(nodeGroupRepository, times(1)).findById(anyLong());
  }

  @Test
  void fetchNodeGroupEntityExceptionTest() {
    when(nodeGroupRepository.findById(anyLong())).thenThrow(new RuntimeException("error"));
    Exception exception =
        assertThrows(
            PromiseEngineException.class,
            () -> {
              nodeGroupPersistenceService.fetchNodeGroupById(TestUtil.NODE_GROUP_ID);
            });
    assertEquals("Unable to find node group", exception.getMessage());
    verify(nodeGroupRepository, times(1)).findById(anyLong());
  }

  @Test
  void fetchNodeGroupByIdAndOrgIdTest() throws PromiseEngineException {
    NodeGroupEntity nodeGroupEntity = testUtil.getNodeGroupEntity();
    NodeGroupDomainDto nodeGroupDomainDto = testUtil.getNodeGroupDomainDto();
    when(nodeGroupRepository.findByIdAndOrgId(anyLong(), anyString()))
        .thenReturn(Optional.of(nodeGroupEntity));
    when(nodeGroupEntityMapper.toDomain(any(NodeGroupEntity.class))).thenReturn(nodeGroupDomainDto);
    Optional<NodeGroupDomainDto> saved_nodeGroupEntityResponse =
        nodeGroupPersistenceService.fetchNodeGroupByIdAndOrgId(
            TestUtil.NODE_GROUP_ID, testUtil.ORG_ID);
    assertEquals(nodeGroupDomainDto, saved_nodeGroupEntityResponse.get());
    verify(nodeGroupRepository, times(1)).findByIdAndOrgId(anyLong(), anyString());
  }

  @Test
  void fetchNodeGroupEntityByIdAndOrgIdExceptionTest() {
    when(nodeGroupRepository.findByIdAndOrgId(anyLong(), anyString()))
        .thenThrow(new RuntimeException("error"));
    Exception exception =
        assertThrows(
            PromiseEngineException.class,
            () -> {
              nodeGroupPersistenceService.fetchNodeGroupByIdAndOrgId(
                  TestUtil.NODE_GROUP_ID, TestUtil.ORG_ID);
            });
    assertEquals("Unable to find node group and orgId", exception.getMessage());
    verify(nodeGroupRepository, times(1)).findByIdAndOrgId(anyLong(), anyString());
  }

  @Test
  void fetchNodeGroupEntityListTest() throws PromiseEngineException {
    List<NodeGroupEntity> nodeGroupEntityList = testUtil.getNodeGroupEntityList();
    List<NodeGroupDomainDto> nodeGroupDomainDtoList = testUtil.getNodeGroupEntityDomainDtoList();
    when(nodeGroupRepository.findByOrgIdAndNodeGroupName(anyString(), anyString()))
        .thenReturn(nodeGroupEntityList);
    when(nodeGroupEntityMapper.toDomain(nodeGroupEntityList)).thenReturn(nodeGroupDomainDtoList);
    List<NodeGroupDomainDto> nodeGroupResponseList =
        nodeGroupPersistenceService.fetchNodeGroupByOrgIdAndName(
            TestUtil.ORG_ID, TestUtil.NODE_GROUP_NAME);
    assertEquals(nodeGroupResponseList.get(0), nodeGroupResponseList.get(0));
    verify(nodeGroupRepository, times(1)).findByOrgIdAndNodeGroupName(anyString(), anyString());
  }

  @Test
  void fetchNodeGroupEntityListExceptionTest() {
    when(nodeGroupRepository.findByOrgIdAndNodeGroupName(anyString(), anyString()))
        .thenThrow(new RuntimeException("error"));
    Exception exception =
        assertThrows(
            PromiseEngineException.class,
            () -> {
              nodeGroupPersistenceService.fetchNodeGroupByOrgIdAndName(
                  TestUtil.ORG_ID, TestUtil.NODE_GROUP_NAME);
            });
    assertEquals("Unable to fetch node groups", exception.getMessage());
    verify(nodeGroupRepository, times(1)).findByOrgIdAndNodeGroupName(anyString(), anyString());
  }

  @Test
  void fetchNodeGroupEntityListByOrgIdTest() throws PromiseEngineException {
    List<NodeGroupEntity> nodeGroupEntityList = testUtil.getNodeGroupEntityList();
    List<NodeGroupDomainDto> nodeGroupDomainDtoList = testUtil.getNodeGroupEntityDomainDtoList();
    when(nodeGroupRepository.findByOrgId(anyString())).thenReturn(nodeGroupEntityList);
    when(nodeGroupEntityMapper.toDomain(nodeGroupEntityList)).thenReturn(nodeGroupDomainDtoList);

    List<NodeGroupDomainDto> nodeGroupEntityListResponse =
        nodeGroupPersistenceService.fetchNodeGroupListByOrgId(TestUtil.ORG_ID);
    assertEquals(nodeGroupDomainDtoList.get(0), nodeGroupEntityListResponse.get(0));
    verify(nodeGroupRepository, times(1)).findByOrgId(anyString());
  }

  @Test
  void fetchNodeGroupEntityListByOrgIdExceptionTest() {
    when(nodeGroupRepository.findByOrgId(anyString())).thenThrow(new RuntimeException("error"));
    Exception exception =
        assertThrows(
            PromiseEngineException.class,
            () -> {
              nodeGroupPersistenceService.fetchNodeGroupListByOrgId(TestUtil.ORG_ID);
            });
    assertEquals("Unable to fetch node group list by orgId", exception.getMessage());
    verify(nodeGroupRepository, times(1)).findByOrgId(anyString());
  }

  @Test
  void deleteNodeGroupEntityTest() throws PromiseEngineException {

    when(nodeGroupEntityMapper.toEntity(testUtil.getNodeGroupDomainDto()))
        .thenReturn(testUtil.getNodeGroupEntity());
    doNothing().when(nodeGroupRepository).delete(any());
    NodeGroupDomainDto entity = new NodeGroupDomainDto();
    entity.setId(42L);
    nodeGroupPersistenceService.deleteNodeGroupEntity(entity);
    verify(nodeGroupRepository, times(1)).delete(any());
  }

  @Test
  void deleteNodeGroupEntityExceptionTest() {
    when(nodeGroupEntityMapper.toEntity(testUtil.getNodeGroupDomainDto()))
        .thenReturn(testUtil.getNodeGroupEntity());
    doThrow(new RuntimeException("Error while deleting")).when(nodeGroupRepository).delete(any());
    assertThrows(
        PromiseEngineException.class,
        () -> nodeGroupPersistenceService.deleteNodeGroupEntity(new NodeGroupDomainDto()));
  }

  @Test
  void deleteNodeGroupEntitiesTestWhenNotAllNodeGroupsDeleted() throws PromiseEngineException {
    doNothing().when(nodeGroupRepository).deleteAllByIdIn(anyList());
    when(sourcingRuleDetailsRepository.findByNodeGroupsLike(anyString()))
        .thenReturn(testUtil.getSourcingRuleConfigurations());
    when(rulesConfigurationRepository.findById(TestUtil.SOURCING_RULE_ID))
        .thenReturn(Optional.of(testUtil.getSourcingRulesConfigurationEntity()));
    when(sourcingRuleDetailsRepository.findByOrgIdAndSourcingRuleId(any(), any()))
        .thenReturn(testUtil.getSourcingRuleConfigurations());
    doNothing().when(rulesConfigurationRepository).deleteAll(any());
    doNothing().when(rulesConfigurationRepository).deleteAll((List.of()));
    doNothing().when(sourcingRuleDetailsRepository).deleteAll(any());
    when(sourcingRuleDetailsRepository.saveAll(any())).thenReturn(new ArrayList<>());
    nodeGroupPersistenceService.deleteNodeGroupEntities(List.of(42L, 43L));
    verify(nodeGroupRepository, times(1)).deleteAllByIdIn(anyList());
    verify(sourcingRuleDetailsRepository, times(2)).deleteAll(any());
    verify(sourcingRuleDetailsRepository, times(2)).saveAll(any());
    verify(rulesConfigurationRepository, times(0)).deleteAll((List.of()));
  }

  @Test
  void deleteNodeGroupEntitiesTestWhenAllNodeGroupDeleted() throws PromiseEngineException {
    doNothing().when(nodeGroupRepository).deleteAllByIdIn(any());
    when(sourcingRuleDetailsRepository.findByNodeGroupsLike(anyString()))
        .thenReturn(testUtil.getSourcingRuleConfigurations());
    when(rulesConfigurationRepository.findById(TestUtil.SOURCING_RULE_ID))
        .thenReturn(Optional.of(testUtil.getSourcingRulesConfigurationEntity()));
    when(sourcingRuleDetailsRepository.findByOrgIdAndSourcingRuleId(any(), any()))
        .thenReturn(List.of());
    doNothing().when(rulesConfigurationRepository).deleteAll(any());
    doNothing()
        .when(rulesConfigurationRepository)
        .deleteAll((List.of(testUtil.getSourcingRulesConfigurationEntity())));
    doNothing().when(sourcingRuleDetailsRepository).deleteAll(any());
    when(sourcingRuleDetailsRepository.saveAll(any())).thenReturn(new ArrayList<>());
    nodeGroupPersistenceService.deleteNodeGroupEntities(List.of(42L, 43L));
    verify(nodeGroupRepository, times(1)).deleteAllByIdIn(any());
    verify(sourcingRuleDetailsRepository, times(2)).deleteAll(any());
    verify(sourcingRuleDetailsRepository, times(2)).saveAll(any());
    verify(rulesConfigurationRepository, times(2)).deleteAll((any()));
  }

  @Test
  void deleteNodeGroupEntitiesExceptionTest() {
    doThrow(new RuntimeException("Error while deleting"))
        .when(nodeGroupRepository)
        .deleteAllByIdIn(anyList());
    Assertions.assertThrows(
        PromiseEngineException.class,
        () -> nodeGroupPersistenceService.deleteNodeGroupEntities(List.of(42L)));
  }

  @Test
  void fetchPaginatedNodeGroupEntityListByOrgIdTest() throws PromiseEngineException {
    List<NodeGroupEntity> nodeGroupEntityList = testUtil.getNodeGroupEntityList();
    List<NodeGroupDomainDto> nodeGroupDomainDtoList = testUtil.getNodeGroupEntityDomainDtoList();
    Pageable pageable = PageRequest.of(1, 1, Sort.by(TestUtil.SORT_BY).descending());
    Page<NodeGroupEntity> nodeGroupEntityPage =
        new PageImpl<>(nodeGroupEntityList, pageable, nodeGroupEntityList.size());
    Page<NodeGroupDomainDto> nodeGroupDomainDtosPage =
        new PageImpl<>(nodeGroupDomainDtoList, pageable, nodeGroupDomainDtoList.size());
    when(nodeGroupEntityMapper.toDomain(any(NodeGroupEntity.class)))
        .thenReturn(nodeGroupDomainDtoList.get(0))
        .thenReturn(nodeGroupDomainDtoList.get(1));
    when(nodeGroupRepository.findByOrgId(anyString(), eq(pageable)))
        .thenReturn(nodeGroupEntityPage);
    Page<NodeGroupDomainDto> actualNodeGroupEntityResponse =
        nodeGroupPersistenceService.fetchPaginatedNodeGroupListByOrgId(TestUtil.ORG_ID, pageable);
    assertEquals(nodeGroupDomainDtosPage, actualNodeGroupEntityResponse);
    verify(nodeGroupRepository, times(1)).findByOrgId(anyString(), eq(pageable));
  }

  @Test
  void fetchPaginatedNodeGroupEntityListByOrgIdExceptionTest() {
    Pageable pageable = PageRequest.of(1, 1, Sort.by(TestUtil.SORT_BY).descending());
    when(nodeGroupRepository.findByOrgId(anyString(), any()))
        .thenThrow(new RuntimeException("error"));
    Exception exception =
        assertThrows(
            PromiseEngineException.class,
            () -> {
              nodeGroupPersistenceService.fetchPaginatedNodeGroupListByOrgId(
                  TestUtil.ORG_ID, pageable);
            });
    assertEquals("Unable to fetch node group list by orgId", exception.getMessage());
    verify(nodeGroupRepository, times(1)).findByOrgId(anyString(), any());
  }
}
