/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.sourcing.cost.config.service;

import static com.nextuple.sourcing.cost.config.service.CostValueService.PREV_SLAB_VALUE_COST_VALUE_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.sourcing.cost.config.domain.entity.CostFactorEntity;
import com.nextuple.sourcing.cost.config.domain.entity.CostItineraryAndFactorsEntity;
import com.nextuple.sourcing.cost.config.domain.entity.CostValueEntity;
import com.nextuple.sourcing.cost.config.domain.repository.CostFactorRepository;
import com.nextuple.sourcing.cost.config.domain.repository.CostItineraryAndFactorsRepository;
import com.nextuple.sourcing.cost.config.domain.repository.CostValueRepository;
import com.nextuple.sourcing.cost.config.dto.CostValueCacheKeyDto;
import com.nextuple.sourcing.cost.config.outbound.CostValueResponse;
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
import org.mockito.internal.verification.VerificationModeFactory;
import org.springframework.http.HttpStatus;

@DisplayName("CostValueService Test Cases")
class CostValueServiceTest {

  @Mock private CostValueRepository costValueRepository;

  @Mock private CostItineraryAndFactorsRepository costItineraryAndFactorsRepository;
  @Mock private CostFactorRepository costFactorRepository;
  @InjectMocks private CostValueService costValueService;

  @InjectMocks private TestUtil testUtil;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  @DisplayName("Test creating a Cost Value")
  void createCostValue() throws CommonServiceException {
    CostFactorEntity costFactorEntity = testUtil.getCostFactorEntity();
    costFactorEntity.setIsRateCardLookUpRequired(true);
    when(costValueRepository.findByOrgIdAndCostItineraryAndCostFactorCombinationKey(
            anyString(), anyString(), anyString()))
        .thenReturn(Optional.empty());
    when(costItineraryAndFactorsRepository.findByOrgIdAndCostItinerary(anyString(), anyString()))
        .thenReturn(Optional.ofNullable(testUtil.getCostItineraryAndFactorsEntity()));
    when(costFactorRepository.findByOrgIdAndCostFactor(anyString(), anyString()))
        .thenReturn(List.of(costFactorEntity));
    when(costValueRepository.save(any())).thenReturn(testUtil.getCostValueEntity(Boolean.TRUE));
    CostValueResponse response =
        costValueService.createCostValue(
            TestUtil.ORG_ID, testUtil.getCreateCostValueRequest(Boolean.FALSE));
    assertNotNull(response);
    assertEquals(TestUtil.ID, response.getId());
    assertEquals(TestUtil.ORG_ID, response.getOrgId());
    assertEquals(TestUtil.COST_ITINERARY, response.getCostItinerary());
    assertEquals(TestUtil.COST_VALUE_WITH_PREV_SLB, response.getCostValue());
    assertEquals(TestUtil.COST_FACTOR_COMBINATION_KEY, response.getCostFactorCombinationKey());
  }

  @Test
  @DisplayName("Test creating a Cost Value - with no cost value config for previous slab")
  void createCostValueNoCostConfigPrevSlab() {
    CostFactorEntity costFactorEntity = testUtil.getCostFactorEntity();
    costFactorEntity.setIsRateCardLookUpRequired(true);
    when(costValueRepository.findByOrgIdAndCostItineraryAndCostFactorCombinationKey(
            anyString(), anyString(), anyString()))
        .thenReturn(Optional.empty());
    when(costItineraryAndFactorsRepository.findByOrgIdAndCostItinerary(anyString(), anyString()))
        .thenReturn(Optional.ofNullable(testUtil.getCostItineraryAndFactorsEntity()));
    when(costFactorRepository.findByOrgIdAndCostFactor(anyString(), anyString()))
        .thenReturn(List.of(costFactorEntity));
    when(costValueRepository.findByOrgIdAndCostItineraryAndCostFactorCombinationKey(
            anyString(), anyString(), anyString()))
        .thenReturn(Optional.empty());

    CommonServiceException exception =
        assertThrows(
            CommonServiceException.class,
            () ->
                costValueService.createCostValue(
                    TestUtil.ORG_ID, testUtil.getCreateCostValueRequest(Boolean.TRUE)));
    assertEquals(PREV_SLAB_VALUE_COST_VALUE_NOT_FOUND, exception.getMessage());
    assertEquals(HttpStatus.PRECONDITION_FAILED, exception.getHttpStatus());
    assertEquals(0x1771, exception.getErrorCode());
    assertTrue(exception.getFieldInfo().containsKey(TestUtil.ORG_ID_KEY));
  }

  @Test
  @DisplayName("Test updating the existing Cost Value")
  void createCostValue_update_existing_cost_value() throws CommonServiceException {
    CostFactorEntity costFactorEntity = testUtil.getCostFactorEntity();
    costFactorEntity.setIsRateCardLookUpRequired(true);
    when(costValueRepository.findByOrgIdAndCostItineraryAndCostFactorCombinationKey(
            anyString(), anyString(), anyString()))
        .thenReturn(Optional.of(testUtil.getCostValueEntity(Boolean.TRUE)));

    when(costItineraryAndFactorsRepository.findByOrgIdAndCostItinerary(anyString(), anyString()))
        .thenReturn(Optional.ofNullable(testUtil.getCostItineraryAndFactorsEntity()));
    when(costFactorRepository.findByOrgIdAndCostFactor(anyString(), anyString()))
        .thenReturn(List.of(costFactorEntity));
    when(costValueRepository.save(any())).thenReturn(testUtil.getCostValueEntity(Boolean.TRUE));

    CostValueResponse response =
        costValueService.createCostValue(
            TestUtil.ORG_ID, testUtil.getCreateCostValueRequest(Boolean.FALSE));
    assertNotNull(response);
    assertEquals(TestUtil.ORG_ID, response.getOrgId());
    assertEquals(TestUtil.COST_ITINERARY, response.getCostItinerary());
    assertEquals(TestUtil.COST_FACTOR_COMBINATION_KEY, response.getCostFactorCombinationKey());
    assertEquals(TestUtil.COST_VALUE_WITH_PREV_SLB, response.getCostValue());
    assertEquals(TestUtil.PREV_SLAB_VALUE, response.getPrevSlabValue());
  }

  @Test
  @DisplayName(
      "Test updating the existing Cost Value - with no cost value config for previous slab")
  void createCostValueUpdateExistingCostValueNoCostConfigPrevSlab() {
    CostFactorEntity costFactorEntity = testUtil.getCostFactorEntity();
    costFactorEntity.setIsRateCardLookUpRequired(true);
    when(costValueRepository.findByOrgIdAndCostItineraryAndCostFactorCombinationKey(
            anyString(), anyString(), anyString()))
        .thenReturn(Optional.of(testUtil.getCostValueEntity(Boolean.TRUE)));

    when(costItineraryAndFactorsRepository.findByOrgIdAndCostItinerary(anyString(), anyString()))
        .thenReturn(Optional.ofNullable(testUtil.getCostItineraryAndFactorsEntity()));
    when(costFactorRepository.findByOrgIdAndCostFactor(anyString(), anyString()))
        .thenReturn(List.of(costFactorEntity));
    when(costValueRepository.findByOrgIdAndCostItineraryAndCostFactorCombinationKey(
            anyString(), anyString(), anyString()))
        .thenReturn(Optional.empty());

    CommonServiceException exception =
        assertThrows(
            CommonServiceException.class,
            () ->
                costValueService.createCostValue(
                    TestUtil.ORG_ID, testUtil.getCreateCostValueRequest(Boolean.TRUE)));
    assertEquals(PREV_SLAB_VALUE_COST_VALUE_NOT_FOUND, exception.getMessage());
    assertEquals(HttpStatus.PRECONDITION_FAILED, exception.getHttpStatus());
    assertEquals(0x1771, exception.getErrorCode());
    assertTrue(exception.getFieldInfo().containsKey(TestUtil.ORG_ID_KEY));
  }

  @Test
  @DisplayName("Test creating a Cost Value - Cost itinerary not found")
  void createCostValue_costItinerary_not_found() {
    when(costValueRepository.findByOrgIdAndCostItineraryAndCostFactorCombinationKey(
            anyString(), anyString(), anyString()))
        .thenReturn(Optional.empty());
    when(costItineraryAndFactorsRepository.findByOrgIdAndCostItinerary(anyString(), anyString()))
        .thenReturn(Optional.empty());

    CommonServiceException exception =
        assertThrows(
            CommonServiceException.class,
            () ->
                costValueService.createCostValue(
                    TestUtil.ORG_ID, testUtil.getCreateCostValueRequest(Boolean.TRUE)));
    assertEquals(TestUtil.COST_ITINERARY_NOT_FOUND, exception.getMessage());
    assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
    assertEquals(0x1771, exception.getErrorCode());
    assertTrue(exception.getFieldInfo().containsKey(TestUtil.ORG_ID_KEY));
  }

  @Test
  @DisplayName("Test retrieving a Cost Value")
  void getCostValue() throws CommonServiceException {
    when(costValueRepository.findByIdAndOrgId(TestUtil.ID, TestUtil.ORG_ID))
        .thenReturn(Optional.ofNullable(testUtil.getCostValueEntity(Boolean.TRUE)));
    CostValueResponse response = costValueService.getCostValue(TestUtil.ORG_ID, TestUtil.ID);
    assertNotNull(response);
    assertEquals(TestUtil.ID, response.getId());
    assertEquals(TestUtil.ORG_ID, response.getOrgId());
    assertEquals(TestUtil.COST_ITINERARY, response.getCostItinerary());
    assertEquals(TestUtil.COST_VALUE_WITH_PREV_SLB, response.getCostValue());
    assertEquals(TestUtil.COST_FACTOR_COMBINATION_KEY, response.getCostFactorCombinationKey());
  }

  @Test
  @DisplayName("Test retrieving a Cost Value by orgId,costItinerary and costFactorCombinationKey")
  void getCostValueForCostFactorCombinationKey() throws CommonServiceException {
    when(costValueRepository.findByOrgIdAndCostItineraryAndCostFactorCombinationKey(
            anyString(), anyString(), anyString()))
        .thenReturn(Optional.of(testUtil.getCostValueEntity(Boolean.TRUE)));
    CostValueResponse response =
        costValueService.getCostValueForCostFactorCombinationKey(
            TestUtil.ORG_ID, TestUtil.COST_ITINERARY, TestUtil.COST_FACTOR_COMBINATION_KEY);
    assertNotNull(response);
    assertEquals(TestUtil.ID, response.getId());
    assertEquals(TestUtil.ORG_ID, response.getOrgId());
    assertEquals(TestUtil.COST_ITINERARY, response.getCostItinerary());
    assertEquals(TestUtil.COST_VALUE_WITH_PREV_SLB, response.getCostValue());
    assertEquals(TestUtil.COST_FACTOR_COMBINATION_KEY, response.getCostFactorCombinationKey());
  }

  @Test
  @DisplayName("Test updating a Cost Value")
  void updateCostValue() throws CommonServiceException {

    CostValueEntity entity = testUtil.getCostValueEntity(Boolean.TRUE);
    when(costValueRepository.findByIdAndOrgId(TestUtil.ID, TestUtil.ORG_ID))
        .thenReturn(java.util.Optional.of(entity));
    when(costValueRepository.findByOrgIdAndCostItineraryAndCostFactorCombinationKey(
            anyString(), anyString(), anyString()))
        .thenReturn(Optional.ofNullable(testUtil.getCostValueEntity(true)));
    when(costValueRepository.save(any())).thenReturn(entity);
    CostValueResponse response =
        costValueService.updateCostValue(
            TestUtil.ID, TestUtil.ORG_ID, testUtil.getUpdateCostValueRequest(Boolean.TRUE));
    assertNotNull(response);
    assertEquals(TestUtil.ID, response.getId());
    assertEquals(TestUtil.ORG_ID, response.getOrgId());

    assertEquals(TestUtil.COST_ITINERARY, response.getCostItinerary());
    assertEquals(TestUtil.COST_VALUE_WITH_PREV_SLB, response.getCostValue());
    assertEquals(TestUtil.COST_FACTOR_COMBINATION_KEY, response.getCostFactorCombinationKey());
  }

  @Test
  @DisplayName("Test updating a Cost Value - with no cost value config for previous slab")
  void updateCostValueNoCostConfigPrevSlab() {

    CostValueEntity entity = testUtil.getCostValueEntity(Boolean.TRUE);
    when(costValueRepository.findByIdAndOrgId(TestUtil.ID, TestUtil.ORG_ID))
        .thenReturn(Optional.of(entity));
    when(costValueRepository.findByOrgIdAndCostItineraryAndCostFactorCombinationKey(
            anyString(), anyString(), anyString()))
        .thenReturn(Optional.empty());

    CommonServiceException exception =
        assertThrows(
            CommonServiceException.class,
            () ->
                costValueService.updateCostValue(
                    TestUtil.ID,
                    TestUtil.ORG_ID,
                    testUtil.getUpdateCostValueRequest(Boolean.TRUE)));
    assertEquals(PREV_SLAB_VALUE_COST_VALUE_NOT_FOUND, exception.getMessage());
    assertEquals(HttpStatus.PRECONDITION_FAILED, exception.getHttpStatus());
    assertEquals(0x1771, exception.getErrorCode());
    assertTrue(exception.getFieldInfo().containsKey(TestUtil.ORG_ID_KEY));
  }

  @Test
  @DisplayName("Test retrieving a Cost Value - Not Found")
  void getCostValue_NotFound() {
    when(costValueRepository.findByIdAndOrgId(TestUtil.ID, TestUtil.ORG_ID))
        .thenReturn(Optional.empty());
    CommonServiceException exception =
        assertThrows(
            CommonServiceException.class,
            () -> costValueService.getCostValue(TestUtil.ORG_ID, TestUtil.ID));
    assertEquals(TestUtil.COST_TABLE_NOT_FOUND_EXCEPTION, exception.getMessage());
    assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
    assertEquals(0x1771, exception.getErrorCode());
    assertTrue(exception.getFieldInfo().containsKey(TestUtil.COST_TABLE_ID));
    assertTrue(exception.getFieldInfo().containsKey(TestUtil.ORG_ID_KEY));
  }

  @Test
  @DisplayName("Test updating a Cost Value - Not Found")
  void updateCostValue_NotFound() {
    when(costValueRepository.findByIdAndOrgId(TestUtil.ID, TestUtil.ORG_ID))
        .thenReturn(Optional.empty());
    CommonServiceException exception =
        assertThrows(
            CommonServiceException.class,
            () ->
                costValueService.updateCostValue(
                    TestUtil.ID,
                    TestUtil.ORG_ID,
                    testUtil.getUpdateCostValueRequest(Boolean.TRUE)));
    assertEquals(TestUtil.COST_TABLE_NOT_FOUND_EXCEPTION, exception.getMessage());
    assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
    assertEquals(0x1771, exception.getErrorCode());
    assertTrue(exception.getFieldInfo().containsKey(TestUtil.COST_TABLE_ID));
    assertTrue(exception.getFieldInfo().containsKey(TestUtil.ORG_ID_KEY));
  }

  @Test
  @DisplayName("Delete cost value - happy path")
  void deleteCostValueTest() throws CommonServiceException {

    CostValueEntity entity = testUtil.getCostValueEntity(Boolean.TRUE);
    when(costValueRepository.findByIdAndOrgId(TestUtil.ID, TestUtil.ORG_ID))
        .thenReturn(java.util.Optional.of(entity));

    doNothing().when(costValueRepository).delete(any());
    CostValueResponse response = costValueService.deleteCostValue(TestUtil.ORG_ID, TestUtil.ID);
    assertNotNull(response);
    assertEquals(TestUtil.ID, response.getId());
    assertEquals(TestUtil.ORG_ID, response.getOrgId());

    assertEquals(TestUtil.COST_ITINERARY, response.getCostItinerary());
    assertEquals(TestUtil.COST_VALUE_WITH_PREV_SLB, response.getCostValue());
    assertEquals(TestUtil.COST_FACTOR_COMBINATION_KEY, response.getCostFactorCombinationKey());
  }

  @Test
  @DisplayName("Delete cost value - when cost value is not present in db")
  void deleteCostValueWhenEntityIsNotThereInDBTest() {

    when(costValueRepository.findByIdAndOrgId(TestUtil.ID, TestUtil.ORG_ID))
        .thenReturn(Optional.empty());

    CommonServiceException ex =
        assertThrows(
            CommonServiceException.class,
            () -> costValueService.deleteCostValue(TestUtil.ORG_ID, TestUtil.ID));

    assertEquals(TestUtil.COST_TABLE_NOT_FOUND_EXCEPTION, ex.getMessage());
    verify(costValueRepository, times(1)).findByIdAndOrgId(anyLong(), anyString());
    verify(costValueRepository, times(0)).delete(any(CostValueEntity.class));
  }

  @Test
  void testDeleteCostValueForCostFactorCombinationKey_WhenValidEntityExists_ShouldReturnResponse() {
    // Mock the repository method to return a non-empty optional
    CostValueEntity entity = testUtil.getCostValueEntity(Boolean.TRUE);
    when(costValueRepository.findByOrgIdAndCostItineraryAndCostFactorCombinationKey(
            anyString(), anyString(), anyString()))
        .thenReturn(Optional.of(entity));

    // Call the method and assert that the response is returned
    CostValueResponse response =
        assertDoesNotThrow(
            () ->
                costValueService.deleteCostValueForCostFactorCombinationKey(
                    TestUtil.ORG_ID,
                    TestUtil.COST_ITINERARY,
                    TestUtil.COST_FACTOR_COMBINATION_KEY));
    assertNotNull(response);
    verify(costValueRepository, times(1))
        .findByOrgIdAndCostItineraryAndCostFactorCombinationKey(any(), any(), any());
  }

  @Test
  void
      testDeleteCostValueForCostFactorCombinationKey_WhenEntityDoesNotExist_ShouldThrowException() {
    // Mock the repository method to return empty optional
    when(costValueRepository.findByOrgIdAndCostItineraryAndCostFactorCombinationKey(
            anyString(), anyString(), anyString()))
        .thenReturn(Optional.empty());

    // Assert that a CommonServiceException is thrown
    CommonServiceException exception =
        assertThrows(
            CommonServiceException.class,
            () ->
                costValueService.deleteCostValueForCostFactorCombinationKey(
                    TestUtil.ORG_ID,
                    TestUtil.COST_ITINERARY,
                    TestUtil.COST_FACTOR_COMBINATION_KEY));

    // Assert the exception details
    assertEquals(TestUtil.COST_TABLE_NOT_FOUND_EXCEPTION, exception.getMessage());
    assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
    assertEquals(0x1771, exception.getErrorCode());

    verify(costValueRepository, times(1))
        .findByOrgIdAndCostItineraryAndCostFactorCombinationKey(any(), any(), any());
  }

  @Test
  void getAllCostFactorCacheKeysTest() {
    List<CostValueEntity> costValueEntities = testUtil.getCostValueEntityList();

    when(costValueRepository.findAllCostValueEntities(any())).thenReturn(costValueEntities);

    List<CostValueCacheKeyDto> response = costValueService.getCostValueCacheKeys(2);

    Assertions.assertEquals(2, response.size());
    Assertions.assertEquals(costValueEntities.get(0).getOrgId(), response.get(0).getOrgId());
    verify(costValueRepository, VerificationModeFactory.times(1)).findAllCostValueEntities(any());
  }

  @Test
  @DisplayName(
      "Create cost value exception scenario - When isRateCardLookUp value for the cost factor is false")
  void createCostValueExceptionTest() {
    var costFactor = testUtil.getCostFactorEntity();
    costFactor.setIsRateCardLookUpRequired(false);
    when(costItineraryAndFactorsRepository.findByOrgIdAndCostItinerary(anyString(), anyString()))
        .thenReturn(Optional.ofNullable(testUtil.getCostItineraryAndFactorsEntity()));
    when(costFactorRepository.findByOrgIdAndCostFactor(anyString(), anyString()))
        .thenReturn(List.of(costFactor));

    CommonServiceException exception =
        assertThrows(
            CommonServiceException.class,
            () ->
                costValueService.createCostValue(
                    TestUtil.ORG_ID, testUtil.getCreateCostValueRequest(Boolean.TRUE)));
    assertEquals(
        "Cost value cannot be added as the rate card look up is not required for the given cost itinerary",
        exception.getMessage());
    assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
    assertEquals(0x1784, exception.getErrorCode());
    assertTrue(exception.getFieldInfo().containsKey(TestUtil.ORG_ID_KEY));

    verify(costItineraryAndFactorsRepository, times(1))
        .findByOrgIdAndCostItinerary(anyString(), anyString());
    verify(costFactorRepository, times(1)).findByOrgIdAndCostFactor(anyString(), anyString());
  }

  @Test
  @DisplayName("Create cost value when cost factor value is null")
  void createCostValueWithNullCostCostFactorTest() {
    CostItineraryAndFactorsEntity costItineraryAndFactorsEntity =
        testUtil.getCostItineraryAndFactorsEntity();
    costItineraryAndFactorsEntity.setCostFactors(null);
    when(costValueRepository.findByOrgIdAndCostItineraryAndCostFactorCombinationKey(
            anyString(), anyString(), anyString()))
        .thenReturn(Optional.empty());
    when(costItineraryAndFactorsRepository.findByOrgIdAndCostItinerary(anyString(), anyString()))
        .thenReturn(Optional.ofNullable(costItineraryAndFactorsEntity));
    when(costFactorRepository.findByOrgIdAndCostFactor(anyString(), anyString()))
        .thenReturn(List.of(testUtil.getCostFactorEntity()));
    when(costValueRepository.save(any())).thenReturn(testUtil.getCostValueEntity(Boolean.TRUE));

    var response =
        assertDoesNotThrow(
            () ->
                costValueService.createCostValue(
                    TestUtil.ORG_ID, testUtil.getCreateCostValueRequest(Boolean.FALSE)));

    assertNotNull(response);
    verify(costItineraryAndFactorsRepository, times(1))
        .findByOrgIdAndCostItinerary(anyString(), anyString());
    verify(costFactorRepository, times(0)).findByOrgIdAndCostFactor(anyString(), anyString());
  }
}
