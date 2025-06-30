/*
 * Copyright (c) 2025., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.item.persistence.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nextuple.item.persistence.domain.ItemSubstitutionDomainDto;
import com.nextuple.item.persistence.entity.ItemSubstitutionEntity;
import com.nextuple.item.persistence.mapper.ItemSubstitutionEntityMapper;
import com.nextuple.item.persistence.repository.ItemSubstitutionRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.util.Pair;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class ItemSubstitutionPersistenceServiceImplTest {

  @InjectMocks private ItemSubstitutionPersistenceServiceImpl itemSubstitutionPersistenceService;

  @Mock private ItemSubstitutionRepository itemSubstitutionRepository;

  @Mock private ItemSubstitutionEntityMapper itemSubstitutionEntityMapper;

  private ItemSubstitutionEntity mockEntity;
  private ItemSubstitutionDomainDto mockDomainDto;
  private List<ItemSubstitutionEntity> mockEntityList;
  private List<ItemSubstitutionDomainDto> mockDomainDtoList;

  @BeforeEach
  void setUp() {
    // Set up the repository and mapper in the service using reflection
    ReflectionTestUtils.setField(
        itemSubstitutionPersistenceService, "repository", itemSubstitutionRepository);
    ReflectionTestUtils.setField(
        itemSubstitutionPersistenceService, "mapper", itemSubstitutionEntityMapper);

    // Initialize test data
    mockEntity = new ItemSubstitutionEntity();
    mockEntity.setOrgId("test-org");
    mockEntity.setPrimaryItemId("item1");
    mockEntity.setPrimaryUom("EACH");
    mockEntity.setAlternateItemId("item2");
    mockEntity.setAlternateUom("EACH");
    mockEntity.setPriority(1);

    mockDomainDto = new ItemSubstitutionDomainDto();
    mockDomainDto.setOrgId("test-org");
    mockDomainDto.setPrimaryItemId("item1");
    mockDomainDto.setPrimaryUom("EACH");
    mockDomainDto.setAlternateItemId("item2");
    mockDomainDto.setAlternateUom("EACH");
    mockDomainDto.setPriority(1);

    mockEntityList = new ArrayList<>();
    mockEntityList.add(mockEntity);

    mockDomainDtoList = new ArrayList<>();
    mockDomainDtoList.add(mockDomainDto);
  }

  @Test
  @DisplayName(
      "Test findByOrgIdAndPrimaryItemIdAndPrimaryUom - Should return list of domain DTOs when entities exist")
  void testFindByOrgIdAndPrimaryItemIdAndPrimaryUom() {
    // Arrange
    String orgId = "test-org";
    String primaryItemId = "item1";
    String primaryUom = "EACH";

    when(itemSubstitutionRepository.findByOrgIdAndPrimaryItemIdAndPrimaryUom(
            orgId, primaryItemId, primaryUom))
        .thenReturn(mockEntityList);
    when(itemSubstitutionEntityMapper.toDomain(mockEntityList)).thenReturn(mockDomainDtoList);

    // Act
    List<ItemSubstitutionDomainDto> result =
        itemSubstitutionPersistenceService.findByOrgIdAndPrimaryItemIdAndPrimaryUom(
            orgId, primaryItemId, primaryUom);

    // Assert
    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals(mockDomainDtoList, result);

    // Verify interactions
    verify(itemSubstitutionRepository, times(1))
        .findByOrgIdAndPrimaryItemIdAndPrimaryUom(orgId, primaryItemId, primaryUom);
    verify(itemSubstitutionEntityMapper, times(1)).toDomain(mockEntityList);
  }

  @Test
  @DisplayName(
      "Test findByOrgIdAndPrimaryItemIdAndPrimaryUom - Should return empty list when no entities exist")
  void testFindByOrgIdAndPrimaryItemIdAndPrimaryUomEmptyResult() {
    // Arrange
    String orgId = "test-org";
    String primaryItemId = "non-existent";
    String primaryUom = "EACH";

    List<ItemSubstitutionEntity> emptyList = new ArrayList<>();
    List<ItemSubstitutionDomainDto> emptyDomainList = new ArrayList<>();

    when(itemSubstitutionRepository.findByOrgIdAndPrimaryItemIdAndPrimaryUom(
            orgId, primaryItemId, primaryUom))
        .thenReturn(emptyList);
    when(itemSubstitutionEntityMapper.toDomain(emptyList)).thenReturn(emptyDomainList);

    // Act
    List<ItemSubstitutionDomainDto> result =
        itemSubstitutionPersistenceService.findByOrgIdAndPrimaryItemIdAndPrimaryUom(
            orgId, primaryItemId, primaryUom);

    // Assert
    assertNotNull(result);
    assertTrue(result.isEmpty());

    // Verify interactions
    verify(itemSubstitutionRepository, times(1))
        .findByOrgIdAndPrimaryItemIdAndPrimaryUom(orgId, primaryItemId, primaryUom);
    verify(itemSubstitutionEntityMapper, times(1)).toDomain(emptyList);
  }

  @Test
  @DisplayName(
      "Test findByOrgIdAndPrimaryItemIdAndPrimaryUomAndAlternateItemIdAndAlternateUom - Should return domain DTO when entity exists")
  void testFindByOrgIdAndPrimaryItemIdAndPrimaryUomAndAlternateItemIdAndAlternateUom() {
    // Arrange
    String orgId = "test-org";
    String primaryItemId = "item1";
    String primaryUom = "EACH";
    String alternateItemId = "item2";
    String alternateUom = "EACH";

    when(itemSubstitutionRepository
            .findByOrgIdAndPrimaryItemIdAndPrimaryUomAndAlternateItemIdAndAlternateUom(
                orgId, primaryItemId, primaryUom, alternateItemId, alternateUom))
        .thenReturn(mockEntity);
    when(itemSubstitutionEntityMapper.toDomain(mockEntity)).thenReturn(mockDomainDto);

    // Act
    Optional<ItemSubstitutionDomainDto> result =
        itemSubstitutionPersistenceService
            .findByOrgIdAndPrimaryItemIdAndPrimaryUomAndAlternateItemIdAndAlternateUom(
                orgId, primaryItemId, primaryUom, alternateItemId, alternateUom);

    // Assert
    assertTrue(result.isPresent());
    assertEquals(mockDomainDto, result.get());

    // Verify interactions
    verify(itemSubstitutionRepository, times(1))
        .findByOrgIdAndPrimaryItemIdAndPrimaryUomAndAlternateItemIdAndAlternateUom(
            orgId, primaryItemId, primaryUom, alternateItemId, alternateUom);
    verify(itemSubstitutionEntityMapper, times(1)).toDomain(mockEntity);
  }

  @Test
  @DisplayName(
      "Test findByOrgIdAndPrimaryItemIdAndPrimaryUomAndAlternateItemIdAndAlternateUom - Should return empty optional when entity does not exist")
  void testFindByOrgIdAndPrimaryItemIdAndPrimaryUomAndAlternateItemIdAndAlternateUomNotFound() {
    // Arrange
    String orgId = "test-org";
    String primaryItemId = "item1";
    String primaryUom = "EACH";
    String alternateItemId = "non-existent";
    String alternateUom = "EACH";

    when(itemSubstitutionRepository
            .findByOrgIdAndPrimaryItemIdAndPrimaryUomAndAlternateItemIdAndAlternateUom(
                orgId, primaryItemId, primaryUom, alternateItemId, alternateUom))
        .thenReturn(null);

    // Act
    Optional<ItemSubstitutionDomainDto> result =
        itemSubstitutionPersistenceService
            .findByOrgIdAndPrimaryItemIdAndPrimaryUomAndAlternateItemIdAndAlternateUom(
                orgId, primaryItemId, primaryUom, alternateItemId, alternateUom);

    // Assert
    assertFalse(result.isPresent());

    // Verify interactions
    verify(itemSubstitutionRepository, times(1))
        .findByOrgIdAndPrimaryItemIdAndPrimaryUomAndAlternateItemIdAndAlternateUom(
            orgId, primaryItemId, primaryUom, alternateItemId, alternateUom);
    verify(itemSubstitutionEntityMapper, never()).toDomain(any(ItemSubstitutionEntity.class));
  }

  @Test
  @DisplayName("Test inherited save method from CommonPersistenceService")
  void testSave() {
    // Arrange
    when(itemSubstitutionEntityMapper.toEntity(mockDomainDto)).thenReturn(mockEntity);
    when(itemSubstitutionRepository.save(mockEntity)).thenReturn(mockEntity);
    when(itemSubstitutionEntityMapper.toDomain(mockEntity)).thenReturn(mockDomainDto);

    // Act
    ItemSubstitutionDomainDto result = itemSubstitutionPersistenceService.save(mockDomainDto);

    // Assert
    assertNotNull(result);
    assertEquals(mockDomainDto, result);

    // Verify interactions
    verify(itemSubstitutionEntityMapper, times(1)).toEntity(mockDomainDto);
    verify(itemSubstitutionRepository, times(1)).save(mockEntity);
    verify(itemSubstitutionEntityMapper, times(1)).toDomain(mockEntity);
  }

  @Test
  @DisplayName("Test inherited delete method from CommonPersistenceService")
  void testDelete() {
    // Arrange
    when(itemSubstitutionEntityMapper.toEntity(mockDomainDto)).thenReturn(mockEntity);
    doNothing().when(itemSubstitutionRepository).delete(mockEntity);

    // Act
    itemSubstitutionPersistenceService.delete(mockDomainDto);

    // Assert & Verify interactions
    verify(itemSubstitutionEntityMapper, times(1)).toEntity(mockDomainDto);
    verify(itemSubstitutionRepository, times(1)).delete(mockEntity);
  }

  @Test
  void testFindByOrgIdAndPrimaryItemIdAndPrimaryUomListEmptyResult() {
    // Arrange
    String orgId = "test-org";
    List<Pair<String, String>> primaryItemIdAndUomList =
        List.of(Pair.of("item1", "EACH"), Pair.of("item2", "EACH"));

    when(itemSubstitutionRepository.findByOrgIdAndPrimaryItemIdAndPrimaryUomList(
            orgId, primaryItemIdAndUomList))
        .thenReturn(new ArrayList<>());
    when(itemSubstitutionEntityMapper.toDomain(new ArrayList<>())).thenReturn(new ArrayList<>());

    // Act
    List<ItemSubstitutionDomainDto> result =
        itemSubstitutionPersistenceService.findByOrgIdAndPrimaryItemIdAndPrimaryUomList(
            orgId, primaryItemIdAndUomList);

    // Assert
    assertEquals(0, result.size());

    // Verify interactions
    verify(itemSubstitutionRepository, times(1))
        .findByOrgIdAndPrimaryItemIdAndPrimaryUomList(orgId, primaryItemIdAndUomList);
    verify(itemSubstitutionEntityMapper, times(1)).toDomain(new ArrayList<>());
  }
}
