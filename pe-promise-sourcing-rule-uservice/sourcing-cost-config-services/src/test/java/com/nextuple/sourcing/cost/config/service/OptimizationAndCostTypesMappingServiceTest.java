/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.sourcing.cost.config.service;

import static com.nextuple.sourcing.cost.config.service.CostTypeDashboardService.COST_TYPES;
import static com.nextuple.sourcing.cost.config.service.OptimizationAndCostTypesMappingService.BOTH_COST_AND_REVENUE_FOR_PROFIT_OS;
import static com.nextuple.sourcing.cost.config.service.OptimizationAndCostTypesMappingService.COST_TYPES_DOES_NOT_EXISTS;
import static com.nextuple.sourcing.cost.config.service.OptimizationAndCostTypesMappingService.ONLY_COST_TYPES_FOR_CBO;
import static com.nextuple.sourcing.cost.config.service.OptimizationAndCostTypesMappingService.OPTIMIZATION_STRATEGY_DOES_NOT_EXISTS;
import static com.nextuple.sourcing.cost.config.service.OptimizationAndCostTypesMappingService.OPT_COST_TYPE_MAPPING_EXISTS_WITH_ORG_ID_AND_OPT_STRATEGY;
import static com.nextuple.sourcing.cost.config.service.OptimizationAndCostTypesMappingService.OPT_COST_TYPE_MAPPING_NOT_FOUND_WITH_ID_AND_ORG_ID;
import static com.nextuple.sourcing.cost.config.service.OptimizationAndCostTypesMappingService.OPT_COST_TYPE_MAPPING_NOT_FOUND_WITH_ORG_ID_AND_OPT_STRATEGY;
import static com.nextuple.sourcing.cost.config.utils.TestUtil.ID;
import static com.nextuple.sourcing.cost.config.utils.TestUtil.OPT_STRATEGY;
import static com.nextuple.sourcing.cost.config.utils.TestUtil.ORG_ID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.exception.PromiseEngineException;
import com.nextuple.sourcing.cost.config.domain.entity.OptimizationAndCostTypesMappingEntity;
import com.nextuple.sourcing.cost.config.domain.repository.OptimizationAndCostTypesMappingRepository;
import com.nextuple.sourcing.cost.config.domain.repository.TenantCostTypeRepository;
import com.nextuple.sourcing.cost.config.utils.TestUtil;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@DisplayName("OptimizationAndCostTypesMappingService Test Cases")
class OptimizationAndCostTypesMappingServiceTest {
  @Mock private OptimizationAndCostTypesMappingRepository optimizationAndCostTypesMappingRepository;

  @Mock private TenantCostTypeRepository tenantCostTypeRepository;

  @InjectMocks OptimizationAndCostTypesMappingService optimizationAndCostTypesMappingService;
  @InjectMocks private TestUtil testUtil;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @DisplayName("Create Optimization And Cost Types Mapping Service: Happy Path")
  @Test
  void createOptimizationAndCostTypesMappingTest()
      throws CommonServiceException, PromiseEngineException {
    var optimizationAndCostTypesMappingEntity = testUtil.getOptimizationAndCostTypesMappingEntity();
    var request = testUtil.getCreateOptimizationAndCostTypesMappingRequest();

    when(optimizationAndCostTypesMappingRepository.findByOrgIdAndOptimizationStrategy(
            anyString(), anyString()))
        .thenReturn(Optional.empty());
    when(tenantCostTypeRepository.findByOrgIdAndCostTypeIn(anyString(), anySet()))
        .thenReturn(List.of(testUtil.getTenantCostTypeEntity()));
    when(optimizationAndCostTypesMappingRepository.save(
            any(OptimizationAndCostTypesMappingEntity.class)))
        .thenReturn(optimizationAndCostTypesMappingEntity);

    var response =
        optimizationAndCostTypesMappingService.createOptimizationAndCostTypesMapping(request);
    assertNotNull(response);
    assertEquals(request.getOrgId(), response.getOrgId());
    assertEquals(request.getCostTypes(), response.getCostTypes());
    verify(optimizationAndCostTypesMappingRepository, times(1))
        .findByOrgIdAndOptimizationStrategy(anyString(), anyString());
    verify(tenantCostTypeRepository, times(1)).findByOrgIdAndCostTypeIn(anyString(), anySet());
    verify(optimizationAndCostTypesMappingRepository, times(1))
        .save(any(OptimizationAndCostTypesMappingEntity.class));
  }

  @DisplayName("Create Optimization And Cost Types Mapping Service: Happy Path with Profit")
  @Test
  void createOptimizationAndCostTypesMappingTestHappyPathWithProfit()
      throws CommonServiceException, PromiseEngineException {
    var optimizationAndCostTypesMappingEntity =
        testUtil.getOptimizationAndCostTypesMappingEntityWithProfit();
    var request = testUtil.getCreateOptimizationAndCostTypesMappingRequestWithProfit();

    when(optimizationAndCostTypesMappingRepository.findByOrgIdAndOptimizationStrategy(
            anyString(), anyString()))
        .thenReturn(Optional.empty());
    when(tenantCostTypeRepository.findByOrgIdAndCostTypeIn(anyString(), anySet()))
        .thenReturn(testUtil.getTenantCostTypeEntityListWithCostAndRevenue());
    when(optimizationAndCostTypesMappingRepository.save(
            any(OptimizationAndCostTypesMappingEntity.class)))
        .thenReturn(optimizationAndCostTypesMappingEntity);

    var response =
        optimizationAndCostTypesMappingService.createOptimizationAndCostTypesMapping(request);
    assertNotNull(response);
    assertEquals(request.getOrgId(), response.getOrgId());
    assertEquals(request.getCostTypes(), response.getCostTypes());
    verify(optimizationAndCostTypesMappingRepository, times(1))
        .findByOrgIdAndOptimizationStrategy(anyString(), anyString());
    verify(tenantCostTypeRepository, times(1)).findByOrgIdAndCostTypeIn(anyString(), anySet());
    verify(optimizationAndCostTypesMappingRepository, times(1))
        .save(any(OptimizationAndCostTypesMappingEntity.class));
  }

  @DisplayName(
      "Create Optimization And Cost Types Mapping Service: Exception Path - Already Exists")
  @Test
  void createOptimizationAndCostTypesMappingTest_alreadyExists() throws PromiseEngineException {
    var optimizationAndCostTypesMappingEntity = testUtil.getOptimizationAndCostTypesMappingEntity();
    var request = testUtil.getCreateOptimizationAndCostTypesMappingRequest();

    when(optimizationAndCostTypesMappingRepository.findByOrgIdAndOptimizationStrategy(
            anyString(), anyString()))
        .thenReturn(Optional.of(optimizationAndCostTypesMappingEntity));

    CommonServiceException ex =
        assertThrows(
            CommonServiceException.class,
            () ->
                optimizationAndCostTypesMappingService.createOptimizationAndCostTypesMapping(
                    request));

    assertEquals(OPT_COST_TYPE_MAPPING_EXISTS_WITH_ORG_ID_AND_OPT_STRATEGY, ex.getMessage());
    verify(optimizationAndCostTypesMappingRepository, times(1))
        .findByOrgIdAndOptimizationStrategy(anyString(), anyString());
    verify(tenantCostTypeRepository, times(0)).findByOrgIdAndCostTypeIn(anyString(), anySet());
    verify(optimizationAndCostTypesMappingRepository, times(0))
        .save(any(OptimizationAndCostTypesMappingEntity.class));
  }

  @DisplayName(
      "Create Optimization And Cost Types Mapping Service: Exception Path - Strategy Does Not Exists")
  @Test
  void createOptimizationAndCostTypesMappingTest_strategyDoesNotExists() {
    var request = testUtil.getCreateOptimizationAndCostTypesMappingRequest();
    request.setOptimizationStrategy("INVALID_STRATEGY");

    when(optimizationAndCostTypesMappingRepository.findByOrgIdAndOptimizationStrategy(
            anyString(), anyString()))
        .thenReturn(Optional.empty());

    CommonServiceException ex =
        assertThrows(
            CommonServiceException.class,
            () ->
                optimizationAndCostTypesMappingService.createOptimizationAndCostTypesMapping(
                    request));

    assertEquals(OPTIMIZATION_STRATEGY_DOES_NOT_EXISTS, ex.getMessage());
    verify(optimizationAndCostTypesMappingRepository, times(1))
        .findByOrgIdAndOptimizationStrategy(anyString(), anyString());
    verify(tenantCostTypeRepository, times(0)).findByOrgIdAndCostTypeIn(anyString(), anySet());
    verify(optimizationAndCostTypesMappingRepository, times(0))
        .save(any(OptimizationAndCostTypesMappingEntity.class));
  }

  @DisplayName(
      "Create Optimization And Cost Types Mapping Service: Excpetion Path - Cost Type Doesn't Exist")
  @Test
  void createOptimizationAndCostTypesMappingTest_costTypeInvalid() throws PromiseEngineException {
    var optimizationAndCostTypesMappingEntity = testUtil.getOptimizationAndCostTypesMappingEntity();
    var request = testUtil.getCreateOptimizationAndCostTypesMappingRequest();

    when(optimizationAndCostTypesMappingRepository.findByOrgIdAndOptimizationStrategy(
            anyString(), anyString()))
        .thenReturn(Optional.empty());
    when(tenantCostTypeRepository.findByOrgIdAndCostTypeIn(anyString(), anySet()))
        .thenReturn(List.of());
    when(optimizationAndCostTypesMappingRepository.save(
            any(OptimizationAndCostTypesMappingEntity.class)))
        .thenReturn(optimizationAndCostTypesMappingEntity);

    CommonServiceException ex =
        assertThrows(
            CommonServiceException.class,
            () ->
                optimizationAndCostTypesMappingService.createOptimizationAndCostTypesMapping(
                    request));

    assertEquals(COST_TYPES_DOES_NOT_EXISTS, ex.getMessage());
    verify(optimizationAndCostTypesMappingRepository, times(1))
        .findByOrgIdAndOptimizationStrategy(anyString(), anyString());
    verify(tenantCostTypeRepository, times(1)).findByOrgIdAndCostTypeIn(anyString(), anySet());
    verify(optimizationAndCostTypesMappingRepository, times(0))
        .save(any(OptimizationAndCostTypesMappingEntity.class));
  }

  @DisplayName("Create Optimization And Cost Types Mapping Service: Failure Path with CBO")
  @Test
  void createOptimizationAndCostTypesMappingTestFailurePathOfCBO()
      throws CommonServiceException, PromiseEngineException {
    var request = testUtil.getCreateOptimizationAndCostTypesMappingRequest();
    request.setCostTypes(
        testUtil.getCreateOptimizationAndCostTypesMappingRequestWithProfit().getCostTypes());

    when(optimizationAndCostTypesMappingRepository.findByOrgIdAndOptimizationStrategy(
            anyString(), anyString()))
        .thenReturn(Optional.empty());
    when(tenantCostTypeRepository.findByOrgIdAndCostTypeIn(anyString(), anySet()))
        .thenReturn(testUtil.getTenantCostTypeEntityListWithCostAndRevenue());

    CommonServiceException ex =
        assertThrows(
            CommonServiceException.class,
            () ->
                optimizationAndCostTypesMappingService.createOptimizationAndCostTypesMapping(
                    request));

    assertEquals(ONLY_COST_TYPES_FOR_CBO, ex.getMessage());
    assertEquals(0x1876, ex.getErrorCode());
    assertEquals(request.getCostTypes(), ex.getFieldInfo().get(COST_TYPES).getRejectedValue());
    verify(optimizationAndCostTypesMappingRepository, times(1))
        .findByOrgIdAndOptimizationStrategy(anyString(), anyString());
    verify(tenantCostTypeRepository, times(1)).findByOrgIdAndCostTypeIn(anyString(), anySet());
    verify(optimizationAndCostTypesMappingRepository, times(0))
        .save(any(OptimizationAndCostTypesMappingEntity.class));
  }

  @DisplayName("Create Optimization And Cost Types Mapping Service: Failure Path with Profit")
  @Test
  void createOptimizationAndCostTypesMappingTestFailurePathOfProfit()
      throws CommonServiceException, PromiseEngineException {
    var request = testUtil.getCreateOptimizationAndCostTypesMappingRequestWithProfit();
    request.setCostTypes(testUtil.getCreateOptimizationAndCostTypesMappingRequest().getCostTypes());

    when(optimizationAndCostTypesMappingRepository.findByOrgIdAndOptimizationStrategy(
            anyString(), anyString()))
        .thenReturn(Optional.empty());
    when(tenantCostTypeRepository.findByOrgIdAndCostTypeIn(anyString(), anySet()))
        .thenReturn(testUtil.getTenantCostTypeEntityList());

    CommonServiceException ex =
        assertThrows(
            CommonServiceException.class,
            () ->
                optimizationAndCostTypesMappingService.createOptimizationAndCostTypesMapping(
                    request));

    assertEquals(BOTH_COST_AND_REVENUE_FOR_PROFIT_OS, ex.getMessage());
    assertEquals(0x1877, ex.getErrorCode());
    assertEquals(request.getCostTypes(), ex.getFieldInfo().get(COST_TYPES).getRejectedValue());
    verify(optimizationAndCostTypesMappingRepository, times(1))
        .findByOrgIdAndOptimizationStrategy(anyString(), anyString());
    verify(tenantCostTypeRepository, times(1)).findByOrgIdAndCostTypeIn(anyString(), anySet());
    verify(optimizationAndCostTypesMappingRepository, times(0))
        .save(any(OptimizationAndCostTypesMappingEntity.class));
  }

  @DisplayName("Update Optimization And Cost Types Mapping Service: Happy Path")
  @Test
  void updateOptimizationAndCostTypesMappingByIdAndOrgIdTest() throws CommonServiceException {
    var optimizationAndCostTypesMappingEntity = testUtil.getOptimizationAndCostTypesMappingEntity();
    var request = testUtil.getUpdateOptimizationAndCostTypesMappingRequest();

    when(optimizationAndCostTypesMappingRepository.findByIdAndOrgId(anyLong(), anyString()))
        .thenReturn(Optional.of(optimizationAndCostTypesMappingEntity));
    when(optimizationAndCostTypesMappingRepository.save(
            any(OptimizationAndCostTypesMappingEntity.class)))
        .thenReturn(optimizationAndCostTypesMappingEntity);
    when(tenantCostTypeRepository.findByOrgIdAndCostTypeIn(anyString(), anySet()))
        .thenReturn(List.of(testUtil.getTenantCostTypeEntity()));

    var response =
        optimizationAndCostTypesMappingService.updateOptimizationAndCostTypesMappingByIdAndOrgId(
            ID, ORG_ID, request);
    assertNotNull(response);
    assertEquals(request.getCostTypes(), response.getCostTypes());
    assertEquals(request.getDescription(), response.getDescription());
    verify(optimizationAndCostTypesMappingRepository, times(1))
        .findByIdAndOrgId(anyLong(), anyString());
    verify(tenantCostTypeRepository, times(1)).findByOrgIdAndCostTypeIn(anyString(), anySet());
    verify(optimizationAndCostTypesMappingRepository, times(1))
        .save(any(OptimizationAndCostTypesMappingEntity.class));
  }

  @DisplayName(
      "Update Optimization And Cost Types Mapping Service: Exception Path - Rule Not Found")
  @Test
  void updateOptimizationAndCostTypesMappingByIdAndOrgIdTest_RuleNotFound() {
    var request = testUtil.getUpdateOptimizationAndCostTypesMappingRequest();

    when(optimizationAndCostTypesMappingRepository.findByIdAndOrgId(anyLong(), anyString()))
        .thenReturn(Optional.empty());

    CommonServiceException ex =
        assertThrows(
            CommonServiceException.class,
            () ->
                optimizationAndCostTypesMappingService
                    .updateOptimizationAndCostTypesMappingByIdAndOrgId(ID, ORG_ID, request));

    assertEquals(OPT_COST_TYPE_MAPPING_NOT_FOUND_WITH_ID_AND_ORG_ID, ex.getMessage());
    verify(optimizationAndCostTypesMappingRepository, times(1))
        .findByIdAndOrgId(anyLong(), anyString());
    verify(tenantCostTypeRepository, times(0)).findByOrgIdAndCostTypeIn(anyString(), anySet());
    verify(optimizationAndCostTypesMappingRepository, times(0))
        .save(any(OptimizationAndCostTypesMappingEntity.class));
  }

  @DisplayName(
      "Update Optimization And Cost Types Mapping Service: Exception Path - Cost Types Doesn't Exist")
  @Test
  void updateOptimizationAndCostTypesMappingByIdAndOrgIdTest_costTypesInvalid() {
    var optimizationAndCostTypesMappingEntity = testUtil.getOptimizationAndCostTypesMappingEntity();
    var request = testUtil.getUpdateOptimizationAndCostTypesMappingRequest();

    when(optimizationAndCostTypesMappingRepository.findByIdAndOrgId(anyLong(), anyString()))
        .thenReturn(Optional.of(optimizationAndCostTypesMappingEntity));
    when(optimizationAndCostTypesMappingRepository.save(
            any(OptimizationAndCostTypesMappingEntity.class)))
        .thenReturn(optimizationAndCostTypesMappingEntity);
    when(tenantCostTypeRepository.findByOrgIdAndCostTypeIn(anyString(), anySet()))
        .thenReturn(List.of());

    CommonServiceException ex =
        assertThrows(
            CommonServiceException.class,
            () ->
                optimizationAndCostTypesMappingService
                    .updateOptimizationAndCostTypesMappingByIdAndOrgId(ID, ORG_ID, request));

    assertEquals(COST_TYPES_DOES_NOT_EXISTS, ex.getMessage());
    verify(optimizationAndCostTypesMappingRepository, times(1))
        .findByIdAndOrgId(anyLong(), anyString());
    verify(tenantCostTypeRepository, times(1)).findByOrgIdAndCostTypeIn(anyString(), anySet());
    verify(optimizationAndCostTypesMappingRepository, times(0))
        .save(any(OptimizationAndCostTypesMappingEntity.class));
  }

  @DisplayName(
      "Get Optimization And Cost Types Mapping Service By Org Id and Optimization Strategy: Happy Path")
  @Test
  void getOptimizationAndCostTypesMappingByOrgIdAndOptStrategyTest() throws CommonServiceException {
    var optimizationAndCostTypesMappingEntity = testUtil.getOptimizationAndCostTypesMappingEntity();

    when(optimizationAndCostTypesMappingRepository.findByOrgIdAndOptimizationStrategy(
            anyString(), anyString()))
        .thenReturn(Optional.of(optimizationAndCostTypesMappingEntity));

    var response =
        optimizationAndCostTypesMappingService
            .fetchOptimizationAndCostTypesMappingByOrgIdAndStrategy(ORG_ID, OPT_STRATEGY);
    assertNotNull(response);
    assertEquals(ORG_ID, response.getOrgId());
    assertEquals(OPT_STRATEGY, response.getOptimizationStrategy());
    verify(optimizationAndCostTypesMappingRepository, times(1))
        .findByOrgIdAndOptimizationStrategy(anyString(), anyString());
  }

  @DisplayName(
      "Get Optimization And Cost Types Mapping Service By Org Id and Optimization Strategy: Exception Path - Rule Not Found")
  @Test
  void getOptimizationAndCostTypesMappingByOrgIdAndOptStrategyTest_RuleNotFound() {

    when(optimizationAndCostTypesMappingRepository.findByOrgIdAndOptimizationStrategy(
            anyString(), anyString()))
        .thenReturn(Optional.empty());

    CommonServiceException ex =
        assertThrows(
            CommonServiceException.class,
            () ->
                optimizationAndCostTypesMappingService
                    .fetchOptimizationAndCostTypesMappingByOrgIdAndStrategy(ORG_ID, OPT_STRATEGY));

    assertEquals(OPT_COST_TYPE_MAPPING_NOT_FOUND_WITH_ORG_ID_AND_OPT_STRATEGY, ex.getMessage());
    verify(optimizationAndCostTypesMappingRepository, times(1))
        .findByOrgIdAndOptimizationStrategy(anyString(), anyString());
  }

  @DisplayName("Get Optimization And Cost Types Mapping Service By Org Id and Id: Happy Path")
  @Test
  void getOptimizationAndCostTypesMappingByOrgIdAndIdTest() throws CommonServiceException {
    var optimizationAndCostTypesMappingEntity = testUtil.getOptimizationAndCostTypesMappingEntity();

    when(optimizationAndCostTypesMappingRepository.findByIdAndOrgId(anyLong(), anyString()))
        .thenReturn(Optional.of(optimizationAndCostTypesMappingEntity));

    var response =
        optimizationAndCostTypesMappingService.fetchOptimizationAndCostTypesMappingByOrgIdAndId(
            ORG_ID, ID);
    assertNotNull(response);
    assertEquals(ORG_ID, response.getOrgId());
    assertEquals(ID, response.getId());
    verify(optimizationAndCostTypesMappingRepository, times(1))
        .findByIdAndOrgId(anyLong(), anyString());
  }

  @DisplayName(
      "Get Optimization And Cost Types Mapping Service By Org Id and Id: Exception Path - Rule Not Found")
  @Test
  void getOptimizationAndCostTypesMappingByOrgIdAndIdTest_RuleNotFound() {

    when(optimizationAndCostTypesMappingRepository.findByIdAndOrgId(anyLong(), anyString()))
        .thenReturn(Optional.empty());

    CommonServiceException ex =
        assertThrows(
            CommonServiceException.class,
            () ->
                optimizationAndCostTypesMappingService
                    .fetchOptimizationAndCostTypesMappingByOrgIdAndId(ORG_ID, ID));

    assertEquals(OPT_COST_TYPE_MAPPING_NOT_FOUND_WITH_ID_AND_ORG_ID, ex.getMessage());
    verify(optimizationAndCostTypesMappingRepository, times(1))
        .findByIdAndOrgId(anyLong(), anyString());
  }

  @DisplayName("Delete Optimization And Cost Types Mapping Service By Org Id and Id: Happy Path")
  @Test
  void deleteOptimizationAndCostTypesMappingByOrgIdAndIdTest() throws CommonServiceException {
    var optimizationAndCostTypesMappingEntity = testUtil.getOptimizationAndCostTypesMappingEntity();

    when(optimizationAndCostTypesMappingRepository.findByIdAndOrgId(anyLong(), anyString()))
        .thenReturn(Optional.of(optimizationAndCostTypesMappingEntity));

    var response =
        optimizationAndCostTypesMappingService.deleteOptimizationAndCostTypesMappingByOrgIdAndId(
            ORG_ID, ID);
    assertNotNull(response);
    assertEquals(ORG_ID, response.getOrgId());
    assertEquals(ID, response.getId());
    verify(optimizationAndCostTypesMappingRepository, times(1))
        .findByIdAndOrgId(anyLong(), anyString());
  }

  @DisplayName(
      "Delete Optimization And Cost Types Mapping Service By Org Id and Id: Exception Path - Rule Not Found")
  @Test
  void deleteOptimizationAndCostTypesMappingByOrgIdAndIdTest_RuleNotFound() {

    when(optimizationAndCostTypesMappingRepository.findByIdAndOrgId(anyLong(), anyString()))
        .thenReturn(Optional.empty());

    CommonServiceException ex =
        assertThrows(
            CommonServiceException.class,
            () ->
                optimizationAndCostTypesMappingService
                    .deleteOptimizationAndCostTypesMappingByOrgIdAndId(ORG_ID, ID));

    assertEquals(OPT_COST_TYPE_MAPPING_NOT_FOUND_WITH_ID_AND_ORG_ID, ex.getMessage());
    verify(optimizationAndCostTypesMappingRepository, times(1))
        .findByIdAndOrgId(anyLong(), anyString());
  }
}
