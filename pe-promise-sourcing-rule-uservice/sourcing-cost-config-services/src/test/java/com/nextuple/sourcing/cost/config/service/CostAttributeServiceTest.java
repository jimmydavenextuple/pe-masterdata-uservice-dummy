/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.sourcing.cost.config.service;

import static com.nextuple.sourcing.cost.config.utils.TestUtil.ATTRIBUTE_NAME;
import static com.nextuple.sourcing.cost.config.utils.TestUtil.ID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.sourcing.cost.config.domain.entity.CostAttributeDetailsEntity;
import com.nextuple.sourcing.cost.config.domain.repository.CostAttributeRepository;
import com.nextuple.sourcing.cost.config.dto.CostAttributeDto;
import com.nextuple.sourcing.cost.config.inbound.CostAttributeRequest;
import com.nextuple.sourcing.cost.config.utils.TestUtil;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class CostAttributeServiceTest {
  @InjectMocks private CostAttributeService costAttributeService;
  @Mock private CostAttributeRepository costAttributeRepository;

  @InjectMocks private TestUtil testUtil;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  @DisplayName("Create cost attribute - Happy Path")
  void createCostAttributeTest() throws CommonServiceException {
    when(costAttributeRepository.save(any(CostAttributeDetailsEntity.class)))
        .thenReturn(testUtil.getCostAttributeDetailsEntity());
    when(costAttributeRepository.findByAttributeName(anyString())).thenReturn(Optional.empty());
    CostAttributeDto costAttributeDto =
        costAttributeService.createCostAttribute(testUtil.getCostAttributeRequest());
    assertEquals(testUtil.getCostAttributeMappingEntity().getId(), costAttributeDto.getId());

    verify(costAttributeRepository, times(1)).save(any(CostAttributeDetailsEntity.class));
  }

  @Test
  @DisplayName(
      "Create cost attribute - When attribute name doesn't contain alphanumeric characters")
  void createCostAttributeTestWhenAttributeNameIsInvalid() {
    CostAttributeRequest costAttributeRequest = testUtil.getCostAttributeRequest();
    costAttributeRequest.setAttributeName("item_length");

    CommonServiceException ex =
        assertThrows(
            CommonServiceException.class,
            () -> costAttributeService.createCostAttribute(costAttributeRequest));

    assertEquals("Invalid format! Only alphanumeric characters allowed.", ex.getMessage());

    verify(costAttributeRepository, times(0)).save(any(CostAttributeDetailsEntity.class));
  }

  @Test
  @DisplayName("Create cost attribute - When attribute already exist with given attribute name ")
  void createCostAttributeTestWithExistingAttributeName() {
    when(costAttributeRepository.findByAttributeName(anyString()))
        .thenReturn(Optional.ofNullable(testUtil.getCostAttributeDetailsEntity()));
    CommonServiceException ex =
        assertThrows(
            CommonServiceException.class,
            () -> costAttributeService.createCostAttribute(testUtil.getCostAttributeRequest()));

    assertEquals("Cost attribute already exist for given attribute name", ex.getMessage());

    verify(costAttributeRepository, times(0)).save(any(CostAttributeDetailsEntity.class));
  }

  @Test
  @DisplayName("Find cost attribute details by costAttributeId - Happy Path")
  void findCostAttributeDetailsByIdTest() throws CommonServiceException {
    when(costAttributeRepository.findById(anyLong()))
        .thenReturn(Optional.ofNullable(testUtil.getCostAttributeDetailsEntity()));

    CostAttributeDto costAttributeDto = costAttributeService.findCostAttributeDetailsById(ID);
    assertEquals(testUtil.getCostAttributeDetailsEntity().getId(), costAttributeDto.getId());

    verify(costAttributeRepository, times(1)).findById(anyLong());
  }

  @Test
  @DisplayName(
      "Find cost attribute details by costAttributeId - When cost attribute record not found")
  void findCostAttributeDetailsByIdWhenEntityIsNotThereInDBTest() {
    when(costAttributeRepository.findById(anyLong())).thenReturn(Optional.empty());

    CommonServiceException ex =
        assertThrows(
            CommonServiceException.class,
            () -> costAttributeService.findCostAttributeDetailsById(ID));

    assertEquals("Cost attribute details not found", ex.getMessage());
    verify(costAttributeRepository, times(1)).findById(anyLong());
  }

  @Test
  @DisplayName("Update cost attribute - Happy Path")
  void updateCostAttributeTest() throws CommonServiceException {
    when(costAttributeRepository.findById(anyLong()))
        .thenReturn(Optional.ofNullable(testUtil.getCostAttributeDetailsEntity()));
    when(costAttributeRepository.save(any(CostAttributeDetailsEntity.class)))
        .thenReturn(testUtil.getCostAttributeDetailsEntity());

    CostAttributeDto costAttributeDto =
        costAttributeService.updateCostAttribute(ID, testUtil.getCostAttributeUpdateRequest());
    assertEquals(testUtil.getCostAttributeDetailsEntity().getId(), costAttributeDto.getId());

    verify(costAttributeRepository, times(1)).findById(anyLong());
    verify(costAttributeRepository, times(1)).save(any(CostAttributeDetailsEntity.class));
  }

  @Test
  @DisplayName("Update cost attribute - When cost attribute record not found")
  void updateCostAttributeWhenEntityIsNotThereInDBTest() {
    when(costAttributeRepository.findById(anyLong())).thenReturn(Optional.empty());

    CommonServiceException ex =
        assertThrows(
            CommonServiceException.class,
            () ->
                costAttributeService.updateCostAttribute(
                    ID, testUtil.getCostAttributeUpdateRequest()));

    assertEquals("Cost attribute details not found", ex.getMessage());
    verify(costAttributeRepository, times(1)).findById(anyLong());
    verify(costAttributeRepository, times(0)).save(any(CostAttributeDetailsEntity.class));
  }

  @Test
  @DisplayName("Find cost attribute details by attributeName - Happy Path")
  void findCostAttributeDetailsByAttributeNameTest() throws CommonServiceException {
    when(costAttributeRepository.findByAttributeName(anyString()))
        .thenReturn(Optional.ofNullable(testUtil.getCostAttributeDetailsEntity()));

    CostAttributeDto costAttributeDto =
        costAttributeService.findCostAttributeDetailsByAttributeName(ATTRIBUTE_NAME);
    assertEquals(testUtil.getCostAttributeDetailsEntity().getId(), costAttributeDto.getId());

    verify(costAttributeRepository, times(1)).findByAttributeName(anyString());
  }

  @Test
  @DisplayName(
      "Find cost attribute details by attributeName - When cost attribute record not found")
  void findCostAttributeDetailsByAttributeNameWhenEntityIsNotThereInDBTest() {
    when(costAttributeRepository.findByAttributeName(anyString())).thenReturn(Optional.empty());

    CommonServiceException ex =
        assertThrows(
            CommonServiceException.class,
            () -> costAttributeService.findCostAttributeDetailsByAttributeName(ATTRIBUTE_NAME));

    assertEquals("Cost attribute details not found for given attributeName", ex.getMessage());
    verify(costAttributeRepository, times(1)).findByAttributeName(anyString());
  }

  @Test
  @DisplayName("Find cost attribute details list - Happy Path")
  void findCostAttributeDetailsListTest() {
    when(costAttributeRepository.findByIsPublished(any()))
        .thenReturn(List.of(testUtil.getCostAttributeDetailsEntity()));

    List<CostAttributeDto> costAttributeDtoList =
        costAttributeService.findCostAttributeDetailsList();
    assertEquals(1, costAttributeDtoList.size());

    verify(costAttributeRepository, times(1)).findByIsPublished(any());
  }

  @Test
  @DisplayName("Delete cost attribute details - Happy Path")
  void deleteCostAttributeDetailsTest() {
    when(costAttributeRepository.findAll())
        .thenReturn(List.of(testUtil.getCostAttributeDetailsEntity()));
    doNothing().when(costAttributeRepository).deleteAll();

    List<CostAttributeDto> costAttributeDtoList = costAttributeService.deleteCostAttributeDetails();
    assertEquals(1, costAttributeDtoList.size());

    verify(costAttributeRepository, times(1)).findAll();
    verify(costAttributeRepository, times(1)).deleteAll();
  }

  @Test
  @DisplayName("Get cache keys for all cost attribute")
  void getAllCostAttributeCacheKeysTest() {
    List<CostAttributeDetailsEntity> result = testUtil.getCostAttributeDetailsEntityList();
    when(costAttributeRepository.findAllCostAttributeEntities(any())).thenReturn(result);
    Assertions.assertEquals(1, result.size());
  }
}
