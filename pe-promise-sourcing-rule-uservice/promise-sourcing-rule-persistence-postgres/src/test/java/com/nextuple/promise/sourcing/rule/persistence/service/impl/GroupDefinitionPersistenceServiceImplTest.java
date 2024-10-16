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
import com.nextuple.promise.sourcing.rule.persistence.domain.GroupDefinitionDomainDto;
import com.nextuple.promise.sourcing.rule.persistence.entity.GroupDefinitionEntity;
import com.nextuple.promise.sourcing.rule.persistence.mapper.GroupDefinitionEntityMapper;
import com.nextuple.promise.sourcing.rule.persistence.repository.GroupDefinitionRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

class GroupDefinitionPersistenceServiceImplTest {
  @InjectMocks private GroupDefinitionPersistenceServiceImpl groupDefinitionPersistenceService;

  @Mock private GroupDefinitionRepository groupDefinitionRepository;

  @Mock private GroupDefinitionEntityMapper getGroupDefinitionEntityMapper;

  @InjectMocks private TestUtil testUtil;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    // Added this
    ReflectionTestUtils.setField(
        groupDefinitionPersistenceService, "repository", groupDefinitionRepository);
    ReflectionTestUtils.setField(
        groupDefinitionPersistenceService, "mapper", getGroupDefinitionEntityMapper);
  }

  private static final GroupDefinitionEntityMapper GROUP_DEFINITION_ENTITY_MAPPER =
      Mappers.getMapper(GroupDefinitionEntityMapper.class);

  @Test
  void saveGroupDefinitionEntityTest() throws PromiseEngineException {
    GroupDefinitionEntity groupDefinitionEntity = testUtil.getGroupDefinitionEntity();
    GroupDefinitionDomainDto groupDefinitionDomainDto = testUtil.getGroupDefinitionDomainDto();
    when(groupDefinitionRepository.save(any(GroupDefinitionEntity.class)))
        .thenReturn(groupDefinitionEntity);
    when(getGroupDefinitionEntityMapper.toEntity(any(GroupDefinitionDomainDto.class)))
        .thenReturn(groupDefinitionEntity);
    when(getGroupDefinitionEntityMapper.toDomain(any(GroupDefinitionEntity.class)))
        .thenReturn(groupDefinitionDomainDto);
    GroupDefinitionDomainDto saved_groupDefinitionEntity =
        groupDefinitionPersistenceService.saveGroupDefinition(groupDefinitionDomainDto);
    assertEquals(groupDefinitionDomainDto, saved_groupDefinitionEntity);
    verify(groupDefinitionRepository, times(1)).save(any());
  }

  @Test
  void saveGroupDefinitionEntityExceptionTest() {
    when(getGroupDefinitionEntityMapper.toEntity(any(GroupDefinitionDomainDto.class)))
        .thenReturn(testUtil.getGroupDefinitionEntity());
    when(groupDefinitionRepository.save(any(GroupDefinitionEntity.class)))
        .thenThrow(new RuntimeException("error"));

    Exception exception =
        assertThrows(
            PromiseEngineException.class,
            () -> {
              groupDefinitionPersistenceService.saveGroupDefinition(
                  testUtil.getGroupDefinitionDomainDto());
            });

    assertEquals("Unable to save group definition", exception.getMessage());
  }

  @Test
  void fetchGroupDefinitionByIdTest() throws PromiseEngineException {
    GroupDefinitionDomainDto groupDefinitionDomainDto = testUtil.getGroupDefinitionDomainDto();
    when(groupDefinitionRepository.findById(anyLong()))
        .thenReturn(Optional.of(testUtil.getGroupDefinitionEntity()));
    when(getGroupDefinitionEntityMapper.toDomain(any(GroupDefinitionEntity.class)))
        .thenReturn(groupDefinitionDomainDto);
    Optional<GroupDefinitionDomainDto> received_entity =
        groupDefinitionPersistenceService.fetchGroupDefinitionById(TestUtil.ID);
    assertEquals(groupDefinitionDomainDto, received_entity.get());
    verify(groupDefinitionRepository, times(1)).findById(anyLong());
  }

  @Test
  void fetchGroupDefinitionByIdExceptionTest() {
    when(groupDefinitionRepository.findById(anyLong())).thenThrow(new RuntimeException("error"));

    Exception exception =
        assertThrows(
            PromiseEngineException.class,
            () -> {
              groupDefinitionPersistenceService.fetchGroupDefinitionById(TestUtil.ID);
            });
    assertEquals("Unable to find group definition by id", exception.getMessage());

    verify(groupDefinitionRepository, times(1)).findById(anyLong());
  }

  @Test
  void fetchGroupDefinitionByIdAndOrgIdTest() throws PromiseEngineException {
    GroupDefinitionEntity groupDefinitionEntity = testUtil.getGroupDefinitionEntity();
    GroupDefinitionDomainDto groupDefinitionDomainDto = testUtil.getGroupDefinitionDomainDto();
    when(groupDefinitionRepository.findByIdAndOrgId(anyLong(), anyString()))
        .thenReturn(Optional.of(groupDefinitionEntity));
    when(getGroupDefinitionEntityMapper.toDomain(any(GroupDefinitionEntity.class)))
        .thenReturn(groupDefinitionDomainDto);
    Optional<GroupDefinitionDomainDto> received_entity =
        groupDefinitionPersistenceService.fetchGroupDefinitionByIdAndOrgId(
            TestUtil.ID, TestUtil.ORG_ID);
    assertEquals(groupDefinitionDomainDto, received_entity.get());
    verify(groupDefinitionRepository, times(1)).findByIdAndOrgId(anyLong(), anyString());
  }

  @Test
  void fetchGroupDefinitionByIdandOrgIdExceptionTest() {
    when(groupDefinitionRepository.findByIdAndOrgId(anyLong(), anyString()))
        .thenThrow(new RuntimeException("error"));

    Exception exception =
        assertThrows(
            PromiseEngineException.class,
            () -> {
              groupDefinitionPersistenceService.fetchGroupDefinitionByIdAndOrgId(
                  TestUtil.ID, TestUtil.ORG_ID);
            });
    assertEquals("Unable to find group definition by id and org id", exception.getMessage());

    verify(groupDefinitionRepository, times(1)).findByIdAndOrgId(anyLong(), anyString());
  }

  @Test
  void fetchGroupDefinitionListByOrgIdAndNameTest() throws PromiseEngineException {
    List<GroupDefinitionEntity> groupDefinitionEntityList = testUtil.getGroupDefinitionEntityList();
    List<GroupDefinitionDomainDto> groupDefinitionDomainDtoList =
        testUtil.getGroupDefinitionDomainDtoList();
    when(groupDefinitionRepository.findByOrgIdAndGroupName(anyString(), anyString()))
        .thenReturn(groupDefinitionEntityList);
    when(getGroupDefinitionEntityMapper.toDomain(anyList()))
        .thenReturn(groupDefinitionDomainDtoList);
    List<GroupDefinitionDomainDto> groupDefinitionEntityListResponse =
        groupDefinitionPersistenceService.fetchGroupDefinitionListByOrgIdAndName(
            TestUtil.ORG_ID, TestUtil.GROUP_NAME);
    assertEquals(groupDefinitionDomainDtoList.get(0), groupDefinitionEntityListResponse.get(0));
    verify(groupDefinitionRepository, times(1)).findByOrgIdAndGroupName(anyString(), anyString());
  }

  @Test
  void fetchGroupDefinitionByOrgIdAndNameExceptionTest() {
    when(groupDefinitionRepository.findByOrgIdAndGroupName(anyString(), anyString()))
        .thenThrow(new RuntimeException("error"));

    Exception exception =
        assertThrows(
            PromiseEngineException.class,
            () -> {
              groupDefinitionPersistenceService.fetchGroupDefinitionListByOrgIdAndName(
                  TestUtil.ORG_ID, TestUtil.GROUP_NAME);
            });
    assertEquals("Unable to fetch group definition list by orgId and name", exception.getMessage());

    verify(groupDefinitionRepository, times(1)).findByOrgIdAndGroupName(anyString(), anyString());
  }

  @Test
  void fetchGroupDefinitionListByOrgIdAndSourcingAttributesDefinitionIdAndReqAttributesValueTest()
      throws PromiseEngineException {
    List<GroupDefinitionEntity> groupDefinitionEntityList = testUtil.getGroupDefinitionEntityList();
    List<GroupDefinitionDomainDto> groupDefinitionDomainDtoList =
        testUtil.getGroupDefinitionDomainDtoList();
    when(groupDefinitionRepository
            .findByOrgIdAndSourcingAttributesDefinitionIdAndReqAttributesValue(
                anyString(), anyLong(), anyString()))
        .thenReturn(groupDefinitionEntityList);
    when(getGroupDefinitionEntityMapper.toDomain(anyList()))
        .thenReturn(groupDefinitionDomainDtoList);
    List<GroupDefinitionDomainDto> groupDefinitionEntityListResponse =
        groupDefinitionPersistenceService
            .fetchGroupDefinitionListByOrgIdAndSourcingAttributesDefinitionIdAndReqAttributesValue(
                TestUtil.ORG_ID,
                TestUtil.SOURCING_ATTRIBUTES_DEFINITION_ID,
                TestUtil.REQUIRED_ATTRIBUTES_VALUE);
    assertEquals(groupDefinitionDomainDtoList.get(0), groupDefinitionEntityListResponse.get(0));
    verify(groupDefinitionRepository, times(1))
        .findByOrgIdAndSourcingAttributesDefinitionIdAndReqAttributesValue(
            anyString(), anyLong(), anyString());
  }

  @Test
  void
      fetchGroupDefinitionListByOrgIdAndSourcingAttributesDefinitionIdAndReqAttributesValueExceptionTest() {
    when(groupDefinitionRepository
            .findByOrgIdAndSourcingAttributesDefinitionIdAndReqAttributesValue(
                anyString(), anyLong(), anyString()))
        .thenThrow(new RuntimeException("error"));

    Exception exception =
        assertThrows(
            PromiseEngineException.class,
            () -> {
              groupDefinitionPersistenceService
                  .fetchGroupDefinitionListByOrgIdAndSourcingAttributesDefinitionIdAndReqAttributesValue(
                      TestUtil.ORG_ID,
                      TestUtil.SOURCING_ATTRIBUTES_DEFINITION_ID,
                      TestUtil.REQUIRED_ATTRIBUTES_VALUE);
            });
    assertEquals(
        "Unable to fetch group definition list by orgId , sourcingAttributesDefinitionId and reqAttributesValue",
        exception.getMessage());

    verify(groupDefinitionRepository, times(1))
        .findByOrgIdAndSourcingAttributesDefinitionIdAndReqAttributesValue(
            anyString(), anyLong(), anyString());
  }

  @Test
  void fetchGroupDefinitionListByOrgIdAndSourcingAttributesDefinitionIdTest()
      throws PromiseEngineException {
    List<GroupDefinitionEntity> groupDefinitionEntityList = testUtil.getGroupDefinitionEntityList();
    List<GroupDefinitionDomainDto> groupDefinitionDomainDtoList =
        testUtil.getGroupDefinitionDomainDtoList();
    when(groupDefinitionRepository.findByOrgIdAndSourcingAttributesDefinitionId(
            anyString(), anyLong()))
        .thenReturn(groupDefinitionEntityList);
    when(getGroupDefinitionEntityMapper.toDomain(anyList()))
        .thenReturn(groupDefinitionDomainDtoList);
    List<GroupDefinitionDomainDto> groupDefinitionEntityListResponse =
        groupDefinitionPersistenceService
            .fetchGroupDefinitionListByOrgIdAndSourcingAttributesDefinitionId(
                TestUtil.ORG_ID, TestUtil.SOURCING_ATTRIBUTES_DEFINITION_ID);
    assertEquals(groupDefinitionDomainDtoList.get(0), groupDefinitionEntityListResponse.get(0));
    verify(groupDefinitionRepository, times(1))
        .findByOrgIdAndSourcingAttributesDefinitionId(anyString(), anyLong());
  }

  @Test
  void fetchGroupDefinitionListByOrgIdAndSourcingAttributesDefinitionIdExceptionTest() {
    when(groupDefinitionRepository.findByOrgIdAndSourcingAttributesDefinitionId(
            anyString(), anyLong()))
        .thenThrow(new RuntimeException("error"));

    Exception exception =
        assertThrows(
            PromiseEngineException.class,
            () -> {
              groupDefinitionPersistenceService
                  .fetchGroupDefinitionListByOrgIdAndSourcingAttributesDefinitionId(
                      TestUtil.ORG_ID, TestUtil.SOURCING_ATTRIBUTES_DEFINITION_ID);
            });
    assertEquals(
        "Unable to fetch group definition list by orgId and sourcingAttributesDefinitionId",
        exception.getMessage());

    verify(groupDefinitionRepository, times(1))
        .findByOrgIdAndSourcingAttributesDefinitionId(anyString(), anyLong());
  }

  @Test
  void deleteGroupDefinitionTest() throws PromiseEngineException {
    GroupDefinitionEntity groupDefinitionEntity = testUtil.getGroupDefinitionEntity();
    GroupDefinitionDomainDto groupDefinitionDomainDto = testUtil.getGroupDefinitionDomainDto();
    doNothing().when(groupDefinitionRepository).delete(any(GroupDefinitionEntity.class));
    when(getGroupDefinitionEntityMapper.toEntity(any(GroupDefinitionDomainDto.class)))
        .thenReturn(groupDefinitionEntity);
    groupDefinitionPersistenceService.deleteGroupDefinition(groupDefinitionDomainDto);
    verify(groupDefinitionRepository, times(1)).delete(any());
  }

  @Test
  void deleteGroupDefinitionExceptionTest() {
    when(getGroupDefinitionEntityMapper.toEntity(any(GroupDefinitionDomainDto.class)))
        .thenReturn(testUtil.getGroupDefinitionEntity());
    doThrow(new RuntimeException("error"))
        .when(groupDefinitionRepository)
        .delete(any(GroupDefinitionEntity.class));

    Exception ex =
        assertThrows(
            PromiseEngineException.class,
            () -> {
              groupDefinitionPersistenceService.deleteGroupDefinition(
                  testUtil.getGroupDefinitionDomainDto());
            });
    assertEquals("Unable to delete group definition", ex.getMessage());
  }

  @Test
  void deleteByIdInTest() throws PromiseEngineException {
    doNothing().when(groupDefinitionRepository).deleteByIdIn(anyList());
    groupDefinitionPersistenceService.deleteByIdIn(List.of(1L));
    verify(groupDefinitionRepository, times(1)).deleteByIdIn(anyList());
  }

  @Test
  void deleteByIdInExceptionTest() {
    doThrow(new RuntimeException("error")).when(groupDefinitionRepository).deleteByIdIn(anyList());

    Exception ex =
        assertThrows(
            PromiseEngineException.class,
            () -> {
              groupDefinitionPersistenceService.deleteByIdIn(List.of(1L));
            });
    assertEquals("Unable to delete group definitions", ex.getMessage());
  }
}
