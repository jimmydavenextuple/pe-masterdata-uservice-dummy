/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.sourcing.cost.config.service;

import static com.nextuple.sourcing.cost.config.utils.TestUtil.CANONICAL_NAME;
import static com.nextuple.sourcing.cost.config.utils.TestUtil.ID;
import static com.nextuple.sourcing.cost.config.utils.TestUtil.ORG_ID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.sourcing.cost.config.domain.entity.CostAttributeMappingEntity;
import com.nextuple.sourcing.cost.config.domain.repository.CostAttributeMappingRepository;
import com.nextuple.sourcing.cost.config.domain.repository.CostAttributeRepository;
import com.nextuple.sourcing.cost.config.inbound.CostAttributeMappingRequest;
import com.nextuple.sourcing.cost.config.outbound.CostAttributeMappingResponse;
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
import org.springframework.http.HttpStatus;

class CostAttributeMappingServiceTest {
  @InjectMocks private CostAttributeMappingService costAttributeMappingService;
  @Mock private CostAttributeMappingRepository costAttributeMappingRepository;
  @Mock private CostAttributeRepository costAttributeRepository;

  @InjectMocks private TestUtil testUtil;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  @DisplayName("Create cost attribute mapping - Happy Path")
  void createCostAttributeMappingTest() throws CommonServiceException {
    CostAttributeMappingRequest costAttributeMappingRequest =
        testUtil.getCostAttributeMappingRequest();
    when(costAttributeRepository.findByAttributeName(anyString()))
        .thenReturn(Optional.ofNullable(testUtil.getCostAttributeDetailsEntity()));
    when(costAttributeMappingRepository.save(any(CostAttributeMappingEntity.class)))
        .thenReturn(testUtil.getCostAttributeMappingEntity());

    CostAttributeMappingResponse costAttributeMappingResponse =
        costAttributeMappingService.createCostAttributeMapping(ORG_ID, costAttributeMappingRequest);
    assertEquals(
        testUtil.getCostAttributeMappingEntity().getId(), costAttributeMappingResponse.getId());
    assertEquals(
        testUtil.getCostAttributeMappingRequest().getCustomAttributes(),
        costAttributeMappingResponse.getCustomAttributes());

    verify(costAttributeMappingRepository, times(1)).save(any(CostAttributeMappingEntity.class));
    verify(costAttributeRepository, times(1)).findByAttributeName(anyString());
  }

  @Test
  @DisplayName(
      "Create cost attribute mapping - When combination of canonicalName and org id is not unique")
  void createCostAttributeMappingTestWhenCanonicalNameIsNotUnique() {
    when(costAttributeRepository.findByAttributeName(anyString()))
        .thenReturn(Optional.ofNullable(testUtil.getCostAttributeDetailsEntity()));
    when(costAttributeMappingRepository.findByOrgIdAndCanonicalName(anyString(), anyString()))
        .thenReturn(List.of(testUtil.getCostAttributeMappingEntity()));

    CommonServiceException ex =
        assertThrows(
            CommonServiceException.class,
            () ->
                costAttributeMappingService.createCostAttributeMapping(
                    ORG_ID, testUtil.getCostAttributeMappingRequest()));

    assertEquals("Combination of orgId and canonicalName should be unique", ex.getMessage());

    verify(costAttributeMappingRepository, times(1))
        .findByOrgIdAndCanonicalName(anyString(), anyString());
    verify(costAttributeMappingRepository, times(0)).save(any(CostAttributeMappingEntity.class));
    verify(costAttributeRepository, times(1)).findByAttributeName(anyString());
  }

  @Test
  @DisplayName(
      "Create cost attribute mapping - When combination of attributeName and org id is not unique")
  void createCostAttributeMappingTestWhenProductAttributeNameIsNotUnique() {
    when(costAttributeRepository.findByAttributeName(anyString()))
        .thenReturn(Optional.ofNullable(testUtil.getCostAttributeDetailsEntity()));
    when(costAttributeMappingRepository.findByOrgIdAndAttributeName(anyString(), anyString()))
        .thenReturn(List.of(testUtil.getCostAttributeMappingEntity()));

    CommonServiceException ex =
        assertThrows(
            CommonServiceException.class,
            () ->
                costAttributeMappingService.createCostAttributeMapping(
                    ORG_ID, testUtil.getCostAttributeMappingRequest()));

    assertEquals("Combination of orgId and attributeName should be unique", ex.getMessage());

    verify(costAttributeMappingRepository, times(1))
        .findByOrgIdAndCanonicalName(anyString(), anyString());
    verify(costAttributeMappingRepository, times(0)).save(any(CostAttributeMappingEntity.class));
    verify(costAttributeRepository, times(1)).findByAttributeName(anyString());
  }

  @Test
  @DisplayName(
      "Create cost attribute mapping - When canonical name doesn't contain alphanumeric characters")
  void createCostAttributeMappingTestWhenCanonicalNameIsInvalid() {
    CostAttributeMappingRequest costAttributeMappingRequest =
        testUtil.getCostAttributeMappingRequest();
    costAttributeMappingRequest.setCanonicalName("item_length");
    when(costAttributeRepository.findByAttributeName(anyString()))
        .thenReturn(Optional.ofNullable(testUtil.getCostAttributeDetailsEntity()));
    when(costAttributeMappingRepository.findByOrgIdAndCanonicalName(anyString(), anyString()))
        .thenReturn(List.of());
    when(costAttributeMappingRepository.findByOrgIdAndAttributeName(anyString(), anyString()))
        .thenReturn(List.of());

    CommonServiceException ex =
        assertThrows(
            CommonServiceException.class,
            () ->
                costAttributeMappingService.createCostAttributeMapping(
                    ORG_ID, costAttributeMappingRequest));

    assertEquals("Invalid format! Only alphanumeric characters allowed.", ex.getMessage());

    verify(costAttributeMappingRepository, times(1))
        .findByOrgIdAndCanonicalName(anyString(), anyString());
    verify(costAttributeMappingRepository, times(0)).save(any(CostAttributeMappingEntity.class));
    verify(costAttributeRepository, times(1)).findByAttributeName(anyString());
  }

  @Test
  @DisplayName("Create cost attribute mapping - When cost attribute doesn't exist")
  void createCostAttributeMappingTestWhenCostAttributeDoesNotExist() {
    CostAttributeMappingRequest costAttributeMappingRequest =
        testUtil.getCostAttributeMappingRequest();
    when(costAttributeRepository.findByAttributeName(anyString())).thenReturn(Optional.empty());

    CommonServiceException ex =
        assertThrows(
            CommonServiceException.class,
            () ->
                costAttributeMappingService.createCostAttributeMapping(
                    ORG_ID, costAttributeMappingRequest));

    assertEquals(
        "Can't add cost attribute mapping as the cost attribute doesn't exist", ex.getMessage());
    assertEquals(HttpStatus.PRECONDITION_FAILED, ex.getHttpStatus());

    verify(costAttributeMappingRepository, times(0)).save(any(CostAttributeMappingEntity.class));
    verify(costAttributeRepository, times(1)).findByAttributeName(anyString());
  }

  @Test
  @DisplayName("Find cost attribute mapping by orgId and costAttributeMappingId - Happy Path")
  void findCostAttributeMappingByOrgIdAndIdTest() throws CommonServiceException {
    when(costAttributeMappingRepository.findByIdAndOrgId(anyLong(), anyString()))
        .thenReturn(Optional.ofNullable(testUtil.getCostAttributeMappingEntity()));

    CostAttributeMappingResponse costAttributeMappingResponse =
        costAttributeMappingService.findCostAttributeMappingByOrgIdAndId(ORG_ID, ID);
    assertEquals(
        testUtil.getCostAttributeMappingEntity().getId(), costAttributeMappingResponse.getId());
    assertEquals(
        testUtil.getCostAttributeMappingRequest().getCustomAttributes(),
        costAttributeMappingResponse.getCustomAttributes());

    verify(costAttributeMappingRepository, times(1)).findByIdAndOrgId(anyLong(), anyString());
  }

  @Test
  @DisplayName(
      "Find cost attribute mapping by orgId and costAttributeMappingId - When cost attribute mapping record not found")
  void findCostAttributeMappingByOrgIdAndIdWhenEntityIsNotThereInDBTest() {
    when(costAttributeMappingRepository.findByIdAndOrgId(anyLong(), anyString()))
        .thenReturn(Optional.empty());

    CommonServiceException ex =
        assertThrows(
            CommonServiceException.class,
            () -> costAttributeMappingService.findCostAttributeMappingByOrgIdAndId(ORG_ID, ID));

    assertEquals("Cost attribute mapping details not found", ex.getMessage());
    verify(costAttributeMappingRepository, times(1)).findByIdAndOrgId(anyLong(), anyString());
  }

  @Test
  @DisplayName("Update cost attribute mapping - Happy Path 1")
  void updateCostAttributeMappingScenario1Test() throws CommonServiceException {
    CostAttributeMappingRequest costAttributeMappingRequest =
        testUtil.getCostAttributeMappingRequest();
    costAttributeMappingRequest.setCanonicalName("itemLength1");

    CostAttributeMappingEntity costAttributeMappingEntity =
        testUtil.getCostAttributeMappingEntity();
    costAttributeMappingEntity.setCanonicalName("itemLength1");

    when(costAttributeMappingRepository.findByIdAndOrgId(anyLong(), anyString()))
        .thenReturn(Optional.ofNullable(testUtil.getCostAttributeMappingEntity()));
    when(costAttributeMappingRepository.save(any(CostAttributeMappingEntity.class)))
        .thenReturn(costAttributeMappingEntity);

    CostAttributeMappingResponse costAttributeMappingResponse =
        costAttributeMappingService.updateCostAttributeMapping(
            ID, ORG_ID, costAttributeMappingRequest);
    assertEquals(
        testUtil.getCostAttributeMappingEntity().getId(), costAttributeMappingResponse.getId());
    assertEquals(
        testUtil.getCostAttributeMappingRequest().getCustomAttributes(),
        costAttributeMappingResponse.getCustomAttributes());

    verify(costAttributeMappingRepository, times(1)).save(any(CostAttributeMappingEntity.class));
  }

  @Test
  @DisplayName("Update cost attribute mapping - Happy Path 2")
  void updateCostAttributeMappingScenario2Test() throws CommonServiceException {
    CostAttributeMappingRequest costAttributeMappingRequest =
        testUtil.getCostAttributeMappingRequest();
    costAttributeMappingRequest.setAttributeName("length1");

    CostAttributeMappingEntity costAttributeMappingEntity =
        testUtil.getCostAttributeMappingEntity();
    costAttributeMappingEntity.setAttributeName("length1");

    when(costAttributeRepository.findByAttributeName(anyString()))
        .thenReturn(Optional.ofNullable(testUtil.getCostAttributeDetailsEntity()));
    when(costAttributeMappingRepository.findByIdAndOrgId(anyLong(), anyString()))
        .thenReturn(Optional.ofNullable(testUtil.getCostAttributeMappingEntity()));
    when(costAttributeMappingRepository.save(any(CostAttributeMappingEntity.class)))
        .thenReturn(costAttributeMappingEntity);

    CostAttributeMappingResponse costAttributeMappingResponse =
        costAttributeMappingService.updateCostAttributeMapping(
            ID, ORG_ID, costAttributeMappingRequest);
    assertEquals(
        testUtil.getCostAttributeMappingEntity().getId(), costAttributeMappingResponse.getId());
    assertEquals(
        testUtil.getCostAttributeMappingRequest().getCustomAttributes(),
        costAttributeMappingResponse.getCustomAttributes());

    verify(costAttributeMappingRepository, times(1)).save(any(CostAttributeMappingEntity.class));
    verify(costAttributeRepository, times(1)).findByAttributeName(anyString());
  }

  @Test
  @DisplayName("Update cost attribute mapping - When cost attribute mapping record not found")
  void updateCostAttributeMappingWhenEntityIsNotThereInDBTest() throws CommonServiceException {

    when(costAttributeMappingRepository.findByIdAndOrgId(anyLong(), anyString()))
        .thenReturn(Optional.empty());

    CommonServiceException ex =
        assertThrows(
            CommonServiceException.class,
            () ->
                costAttributeMappingService.updateCostAttributeMapping(
                    ID, ORG_ID, testUtil.getCostAttributeMappingRequest()));

    assertEquals("Cost attribute mapping details not found", ex.getMessage());

    verify(costAttributeMappingRepository, times(1)).findByIdAndOrgId(anyLong(), anyString());
    verify(costAttributeMappingRepository, times(0)).save(any(CostAttributeMappingEntity.class));
  }

  @Test
  @DisplayName("Find cost attribute mapping by orgId and canonicalName - Happy Path")
  void findCostAttributeMappingByOrgIdAndCanonicalNameTest() throws CommonServiceException {
    when(costAttributeMappingRepository.findByOrgIdAndCanonicalName(anyString(), anyString()))
        .thenReturn(List.of(testUtil.getCostAttributeMappingEntity()));

    CostAttributeMappingResponse costAttributeMappingResponse =
        costAttributeMappingService.findCostAttributeMappingByOrgIdAndCanonicalName(
            ORG_ID, CANONICAL_NAME);
    assertEquals(
        testUtil.getCostAttributeMappingEntity().getId(), costAttributeMappingResponse.getId());
    assertEquals(
        testUtil.getCostAttributeMappingRequest().getCustomAttributes(),
        costAttributeMappingResponse.getCustomAttributes());

    verify(costAttributeMappingRepository, times(1))
        .findByOrgIdAndCanonicalName(anyString(), anyString());
  }

  @Test
  @DisplayName(
      "Find cost attribute mapping by orgId and canonicalName - When cost attribute mapping record not found")
  void findCostAttributeMappingByOrgIdAndCanonicalNameWhenEntityIsNotThereInDBTest() {
    when(costAttributeMappingRepository.findByOrgIdAndCanonicalName(anyString(), anyString()))
        .thenReturn(List.of());

    CommonServiceException ex =
        assertThrows(
            CommonServiceException.class,
            () ->
                costAttributeMappingService.findCostAttributeMappingByOrgIdAndCanonicalName(
                    ORG_ID, CANONICAL_NAME));
    assertEquals(
        "Cost attribute mapping details not found for given orgId and canonicalName",
        ex.getMessage());

    verify(costAttributeMappingRepository, times(1))
        .findByOrgIdAndCanonicalName(anyString(), anyString());
  }

  @Test
  @DisplayName("Get cache keys for all cost attribute mapping")
  void getAllCostAttributeMappingCacheKeysTest() {
    List<CostAttributeMappingEntity> result = testUtil.getCostAttributeMappingEntityList();
    when(costAttributeMappingRepository.findAllCostAttributeMappingEntities(any()))
        .thenReturn(result);
    Assertions.assertEquals(1, result.size());
  }
}
