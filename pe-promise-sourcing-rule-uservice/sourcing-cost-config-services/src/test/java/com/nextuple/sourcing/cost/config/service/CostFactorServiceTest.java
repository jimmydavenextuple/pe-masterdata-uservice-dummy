/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.sourcing.cost.config.service;

import static com.nextuple.sourcing.cost.config.service.CostFactorService.COST_FACTOR_DISPLAY_NAME_NOT_UNIQUE_EXCEPTION_MESSAGE;
import static com.nextuple.sourcing.cost.config.service.CostFactorService.UPDATE_BUCKETED_COST_FACTOR_UOM_EXCEPTION_MESSAGE;
import static com.nextuple.sourcing.cost.config.utils.TestUtil.COST_FACTOR;
import static com.nextuple.sourcing.cost.config.utils.TestUtil.COST_FACTOR_ID;
import static com.nextuple.sourcing.cost.config.utils.TestUtil.ORG_ID;
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
import com.nextuple.sourcing.cost.config.domain.entity.CostFactorAuditLogEntity;
import com.nextuple.sourcing.cost.config.domain.entity.CostFactorEntity;
import com.nextuple.sourcing.cost.config.domain.repository.CostAttributeMappingRepository;
import com.nextuple.sourcing.cost.config.domain.repository.CostAttributeRepository;
import com.nextuple.sourcing.cost.config.domain.repository.CostFactorAuditLogRepository;
import com.nextuple.sourcing.cost.config.domain.repository.CostFactorRepository;
import com.nextuple.sourcing.cost.config.domain.repository.CostItineraryAndFactorsRepository;
import com.nextuple.sourcing.cost.config.dto.CostFactorCacheKeyDto;
import com.nextuple.sourcing.cost.config.dto.CostFactorDto;
import com.nextuple.sourcing.cost.config.enums.CostFactorTypeEnum;
import com.nextuple.sourcing.cost.config.enums.DataTypeEnum;
import com.nextuple.sourcing.cost.config.inbound.CostFactorRequest;
import com.nextuple.sourcing.cost.config.outbound.ExpressionValidationResponse;
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

class CostFactorServiceTest {
  @InjectMocks private CostFactorService costFactorService;
  @Mock private CostFactorRepository costFactorRepository;
  @Mock private CostFactorAuditLogRepository costFactorAuditLogRepository;
  @Mock private CostItineraryAndFactorsRepository costItineraryAndFactorsRepository;
  @Mock private CostAttributeRepository costAttributeRepository;
  @Mock private CostAttributeMappingRepository costAttributeMappingRepository;

  @Mock private ExpressionValidationService expressionValidationService;

  @InjectMocks private TestUtil testUtil;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  @DisplayName("Create cost factor")
  void createCostFactorTest() throws CommonServiceException {
    CostFactorRequest costFactorRequest = testUtil.getCreateCostFactorRequest();
    when(costFactorRepository.findByOrgIdAndCostFactor(anyString(), anyString()))
        .thenReturn(List.of());
    when(costAttributeMappingRepository.findByOrgIdAndCanonicalName(anyString(), anyString()))
        .thenReturn(List.of(testUtil.getCostAttributeMappingEntity()));
    when(costFactorRepository.save(any(CostFactorEntity.class)))
        .thenReturn(testUtil.getCostFactorEntity());
    when(costFactorAuditLogRepository.save(any(CostFactorAuditLogEntity.class)))
        .thenReturn(testUtil.getCostFactorAuditLogEntity());
    when(expressionValidationService.validateExpression(anyString(), anyString(), any()))
        .thenReturn(ExpressionValidationResponse.builder().build());
    CostFactorDto costFactorResponse =
        costFactorService.createCostFactor(ORG_ID, costFactorRequest);
    assertEquals(testUtil.getCostFactorEntity().getId(), costFactorResponse.getId());
    assertEquals(
        testUtil.getCostAttributeMappingRequest().getCustomAttributes(),
        costFactorResponse.getCustomAttributes());
    assertEquals(
        testUtil.getCostFactorEntity().getIsRateCardLookUpRequired(),
        costFactorResponse.getIsRateCardLookUpRequired());
    verify(costFactorRepository, times(1)).save(any(CostFactorEntity.class));
    verify(costFactorAuditLogRepository, times(1)).save(any(CostFactorAuditLogEntity.class));
    verify(expressionValidationService, times(1))
        .validateExpression(anyString(), anyString(), any());
  }

  @Test
  @DisplayName("Create cost factor, when isRateCardLookUpRequired is not passed")
  void createCostFactorTestWhenRateCardLookUpFieldIsPassed() throws CommonServiceException {
    CostFactorRequest costFactorRequest = testUtil.getCreateCostFactorRequest();
    costFactorRequest.setIsRateCardLookUpRequired(null);
    CostFactorEntity costFactorEntity = testUtil.getCostFactorEntity();
    costFactorEntity.setIsRateCardLookUpRequired(true);
    when(costFactorRepository.findByOrgIdAndCostFactor(anyString(), anyString()))
        .thenReturn(List.of());
    when(costAttributeMappingRepository.findByOrgIdAndCanonicalName(anyString(), anyString()))
        .thenReturn(List.of(testUtil.getCostAttributeMappingEntity()));
    when(costFactorRepository.save(any(CostFactorEntity.class))).thenReturn(costFactorEntity);
    when(costFactorAuditLogRepository.save(any(CostFactorAuditLogEntity.class)))
        .thenReturn(testUtil.getCostFactorAuditLogEntity());
    when(expressionValidationService.validateExpression(anyString(), anyString(), any()))
        .thenReturn(ExpressionValidationResponse.builder().build());
    CostFactorDto costFactorResponse =
        costFactorService.createCostFactor(ORG_ID, costFactorRequest);
    assertEquals(testUtil.getCostFactorEntity().getId(), costFactorResponse.getId());
    assertEquals(
        testUtil.getCostAttributeMappingRequest().getCustomAttributes(),
        costFactorResponse.getCustomAttributes());
    assertEquals(
        costFactorEntity.getIsRateCardLookUpRequired(),
        costFactorResponse.getIsRateCardLookUpRequired());
    verify(costFactorRepository, times(1)).save(any(CostFactorEntity.class));
    verify(costFactorAuditLogRepository, times(1)).save(any(CostFactorAuditLogEntity.class));
    verify(expressionValidationService, times(1))
        .validateExpression(anyString(), anyString(), any());
  }

  @Test
  @DisplayName("Create cost factor, when isRateCardLookUpRequired disabled but isBucketed enabled")
  void createCostFactorTestWhenRateCardLookUpFieldFalse() throws CommonServiceException {
    CostFactorRequest costFactorRequest = testUtil.getCreateCostFactorRequest();
    costFactorRequest.setIsRateCardLookUpRequired(false);
    costFactorRequest.setIsBucketed(true);
    CostFactorEntity costFactorEntity = testUtil.getCostFactorEntity();
    costFactorEntity.setIsRateCardLookUpRequired(true);
    when(costFactorRepository.findByOrgIdAndCostFactor(anyString(), anyString()))
        .thenReturn(List.of());
    when(costAttributeMappingRepository.findByOrgIdAndCanonicalName(anyString(), anyString()))
        .thenReturn(List.of(testUtil.getCostAttributeMappingEntity()));
    when(costFactorRepository.save(any(CostFactorEntity.class))).thenReturn(costFactorEntity);
    when(costFactorAuditLogRepository.save(any(CostFactorAuditLogEntity.class)))
        .thenReturn(testUtil.getCostFactorAuditLogEntity());
    when(expressionValidationService.validateExpression(anyString(), anyString(), any()))
        .thenReturn(ExpressionValidationResponse.builder().build());
    CommonServiceException ce =
        assertThrows(
            CommonServiceException.class,
            () -> costFactorService.createCostFactor(ORG_ID, costFactorRequest));
    assertEquals(
        "Cost factor cannot be created if rateCardLookUp is disabled and bucketed is enabled.",
        ce.getMessage());
    assertEquals(HttpStatus.BAD_REQUEST, ce.getHttpStatus());
  }

  @Test
  @DisplayName(
      "Create cost factor, when isRateCardLookUpRequired disabled but date type is not number")
  void createCostFactorTestWhenRateCardLookUpFieldFalseAndDataTypeNotNumber()
      throws CommonServiceException {
    CostFactorRequest costFactorRequest = testUtil.getCreateCostFactorRequest();
    costFactorRequest.setCostFactorType(CostFactorTypeEnum.REGULAR);
    costFactorRequest.setIsRateCardLookUpRequired(false);
    costFactorRequest.setIsBucketed(false);
    costFactorRequest.setDataType(DataTypeEnum.STRING);
    CostFactorEntity costFactorEntity = testUtil.getCostFactorEntity();
    costFactorEntity.setIsRateCardLookUpRequired(true);
    when(costFactorRepository.findByOrgIdAndCostFactor(anyString(), anyString()))
        .thenReturn(List.of());
    when(costAttributeMappingRepository.findByOrgIdAndCanonicalName(anyString(), anyString()))
        .thenReturn(List.of(testUtil.getCostAttributeMappingEntity()));
    when(costFactorRepository.save(any(CostFactorEntity.class))).thenReturn(costFactorEntity);
    when(costFactorAuditLogRepository.save(any(CostFactorAuditLogEntity.class)))
        .thenReturn(testUtil.getCostFactorAuditLogEntity());
    when(expressionValidationService.validateExpression(anyString(), anyString(), any()))
        .thenReturn(ExpressionValidationResponse.builder().build());
    CommonServiceException ce =
        assertThrows(
            CommonServiceException.class,
            () -> costFactorService.createCostFactor(ORG_ID, costFactorRequest));
    assertEquals(
        "Cost factor cannot be created if rateCardLookUp is disabled and data type is not a number.",
        ce.getMessage());
    assertEquals(HttpStatus.BAD_REQUEST, ce.getHttpStatus());
  }

  @Test
  @DisplayName("Create cost factor when values is not provided")
  void createCostFactorTestWithoutValues() throws CommonServiceException {
    CostFactorRequest costFactorRequest = testUtil.getCreateCostFactorRequest();
    costFactorRequest.setValues("");
    when(costFactorRepository.findByOrgIdAndCostFactor(anyString(), anyString()))
        .thenReturn(List.of());
    when(costAttributeMappingRepository.findByOrgIdAndCanonicalName(anyString(), anyString()))
        .thenReturn(List.of(testUtil.getCostAttributeMappingEntity()));
    when(costFactorRepository.save(any(CostFactorEntity.class)))
        .thenReturn(testUtil.getCostFactorEntity());
    when(costFactorAuditLogRepository.save(any(CostFactorAuditLogEntity.class)))
        .thenReturn(testUtil.getCostFactorAuditLogEntity());
    when(expressionValidationService.validateExpression(anyString(), anyString(), any()))
        .thenReturn(ExpressionValidationResponse.builder().build());
    CostFactorDto costFactorResponse =
        costFactorService.createCostFactor(ORG_ID, costFactorRequest);
    assertEquals(testUtil.getCostFactorEntity().getId(), costFactorResponse.getId());
    assertEquals(
        testUtil.getCostAttributeMappingRequest().getCustomAttributes(),
        costFactorResponse.getCustomAttributes());
    verify(costFactorRepository, times(1)).save(any(CostFactorEntity.class));
    verify(costFactorAuditLogRepository, times(1)).save(any(CostFactorAuditLogEntity.class));
    verify(expressionValidationService, times(1))
        .validateExpression(anyString(), anyString(), any());
  }

  @Test
  @DisplayName("Create Cost Factor - When combination of cost factor and org id is not unique")
  void createCostFactorExceptionTest() {
    when(costFactorRepository.findByOrgIdAndCostFactor(anyString(), anyString()))
        .thenReturn(List.of(testUtil.getCostFactorEntity()));

    CommonServiceException ex =
        assertThrows(
            CommonServiceException.class,
            () ->
                costFactorService.createCostFactor(ORG_ID, testUtil.getCreateCostFactorRequest()));

    assertEquals("Combination of orgId and costFactor should be unique", ex.getMessage());

    verify(costFactorRepository, times(1)).findByOrgIdAndCostFactor(anyString(), anyString());
    verify(costFactorRepository, times(0)).save(any(CostFactorEntity.class));
  }

  @Test
  @DisplayName("Create Cost Factor - When Derived cost factor type has isBucketed as false")
  void createCostFactorTestWhenDerivedCostFactorTypeHasIsBucketedAsFalse() {
    CostFactorRequest costFactorRequest = testUtil.getCreateCostFactorRequest();
    costFactorRequest.setIsBucketed(false);

    CommonServiceException ex =
        assertThrows(
            CommonServiceException.class,
            () -> costFactorService.createCostFactor(ORG_ID, costFactorRequest));

    assertEquals("Derived cost factor type cannot have isBucketed as false", ex.getMessage());

    verify(costFactorRepository, times(0)).save(any(CostFactorEntity.class));
  }

  @Test
  @DisplayName(
      "Create cost factor - When cost attribute mapping is not defined but cost attribute is defined")
  void createCostFactorNoAttributeMappingTest() throws CommonServiceException {
    CostFactorRequest costFactorRequest = testUtil.getCreateCostFactorRequest();
    when(costFactorRepository.findByOrgIdAndCostFactor(anyString(), anyString()))
        .thenReturn(List.of());
    when(costAttributeMappingRepository.findByOrgIdAndCanonicalName(anyString(), anyString()))
        .thenReturn(List.of());
    when(costAttributeRepository.findByAttributeName(anyString()))
        .thenReturn(Optional.ofNullable(testUtil.getCostAttributeDetailsEntity()));
    when(costFactorRepository.save(any(CostFactorEntity.class)))
        .thenReturn(testUtil.getCostFactorEntity());
    when(costFactorAuditLogRepository.save(any(CostFactorAuditLogEntity.class)))
        .thenReturn(testUtil.getCostFactorAuditLogEntity());
    when(expressionValidationService.validateExpression(anyString(), anyString(), any()))
        .thenReturn(ExpressionValidationResponse.builder().build());
    CostFactorDto costFactorResponse =
        costFactorService.createCostFactor(ORG_ID, costFactorRequest);
    assertEquals(testUtil.getCostFactorEntity().getId(), costFactorResponse.getId());
    assertEquals(
        testUtil.getCostAttributeMappingRequest().getCustomAttributes(),
        costFactorResponse.getCustomAttributes());
    verify(costFactorRepository, times(1)).save(any(CostFactorEntity.class));
    verify(costFactorAuditLogRepository, times(1)).save(any(CostFactorAuditLogEntity.class));
    verify(expressionValidationService, times(1))
        .validateExpression(anyString(), anyString(), any());
  }

  @Test
  @DisplayName(
      "Create Cost Factor - When Cost attribute and cost attribute mapping are not configured for given formula")
  void createCostFactorTestWhenCostAttributeIsNotConfiguredForGivenFormula() {
    CostFactorRequest costFactorRequest = testUtil.getCreateCostFactorRequest();
    costFactorRequest.setCostFactorType(CostFactorTypeEnum.REGULAR);
    costFactorRequest.setFormula("carrierServiceId");
    when(costFactorRepository.findByOrgIdAndCostFactor(anyString(), anyString()))
        .thenReturn(List.of());
    when(costAttributeMappingRepository.findByOrgIdAndCanonicalName(anyString(), anyString()))
        .thenReturn(List.of());
    when(costAttributeMappingRepository.findByOrgIdAndCanonicalName(anyString(), anyString()))
        .thenReturn(List.of());
    when(costAttributeRepository.findByAttributeName(anyString())).thenReturn(Optional.empty());

    CommonServiceException ex =
        assertThrows(
            CommonServiceException.class,
            () -> costFactorService.createCostFactor(ORG_ID, costFactorRequest));

    assertEquals("Cost attribute not configured for given formula", ex.getMessage());

    verify(costFactorRepository, times(0)).save(any(CostFactorEntity.class));
  }

  @Test
  @DisplayName("Create cost factor - When displayName is not unique")
  void createCostFactorDuplicateDisplayNameTest() {
    CostFactorRequest costFactorRequest = testUtil.getCreateCostFactorRequest();
    when(costFactorRepository.findFirstByOrgIdAndDisplayName(anyString(), anyString()))
        .thenReturn(Optional.of(testUtil.getCostFactorEntity()));

    CommonServiceException ex =
        assertThrows(
            CommonServiceException.class,
            () -> costFactorService.createCostFactor(ORG_ID, costFactorRequest));

    assertEquals(COST_FACTOR_DISPLAY_NAME_NOT_UNIQUE_EXCEPTION_MESSAGE, ex.getMessage());

    verify(costFactorRepository, times(0)).save(any(CostFactorEntity.class));
    verify(costFactorAuditLogRepository, times(0)).save(any(CostFactorAuditLogEntity.class));
  }

  @Test
  @DisplayName("Find cost factor by org Id and cost factor Id - happy path")
  void findCostFactorByOrgIdAndCostFactorIdTest() throws CommonServiceException {
    when(costFactorRepository.findByIdAndOrgId(anyLong(), anyString()))
        .thenReturn(Optional.ofNullable(testUtil.getCostFactorEntity()));

    CostFactorDto preferenceSelectorResponse =
        costFactorService.findCostFactorByOrgIdAndCostFactorId(ORG_ID, COST_FACTOR_ID);
    assertEquals(testUtil.getCostFactorEntity().getId(), preferenceSelectorResponse.getId());
    assertEquals(
        testUtil.getCostAttributeMappingRequest().getCustomAttributes(),
        preferenceSelectorResponse.getCustomAttributes());
    verify(costFactorRepository, times(1)).findByIdAndOrgId(anyLong(), anyString());
  }

  @Test
  @DisplayName(
      "Find cost factor by org Id and cost factor Id - when cost factor is not present in db")
  void findCostFactorByOrgIdAndCostFactorIdWhenEntityIsNotThereInDBTest() {
    when(costFactorRepository.findByIdAndOrgId(anyLong(), anyString()))
        .thenReturn(Optional.empty());

    CommonServiceException ex =
        assertThrows(
            CommonServiceException.class,
            () -> costFactorService.findCostFactorByOrgIdAndCostFactorId(ORG_ID, COST_FACTOR_ID));

    assertEquals("Cost Factor not found", ex.getMessage());
    verify(costFactorRepository, times(1)).findByIdAndOrgId(anyLong(), anyString());
  }

  @Test
  @DisplayName("Find cost factor by org Id and cost factor - happy path")
  void findCostFactorByOrgIdAndCostFactorTest() throws CommonServiceException {
    when(costFactorRepository.findByOrgIdAndCostFactor(anyString(), anyString()))
        .thenReturn(List.of(testUtil.getCostFactorEntity()));

    CostFactorDto preferenceSelectorResponse =
        costFactorService.findCostFactorByOrgIdAndCostFactor(ORG_ID, COST_FACTOR);
    assertEquals(testUtil.getCostFactorEntity().getId(), preferenceSelectorResponse.getId());
    assertEquals(
        testUtil.getCostAttributeMappingRequest().getCustomAttributes(),
        preferenceSelectorResponse.getCustomAttributes());
    verify(costFactorRepository, times(1)).findByOrgIdAndCostFactor(anyString(), anyString());
  }

  @Test
  @DisplayName("Should throw exception when cost factor is not present in db")
  void findCostFactorByOrgIdAndCostFactorWhenEntityIsNotThereInDBTest() {
    when(costFactorRepository.findByOrgIdAndCostFactor(anyString(), anyString()))
        .thenReturn(List.of());

    CommonServiceException ex =
        assertThrows(
            CommonServiceException.class,
            () -> costFactorService.findCostFactorByOrgIdAndCostFactor(ORG_ID, COST_FACTOR));

    assertEquals("Cost Factor not found", ex.getMessage());
    verify(costFactorRepository, times(1)).findByOrgIdAndCostFactor(anyString(), anyString());
  }

  @Test
  @DisplayName("Update cost factor - happy path")
  void updateCostFactorTest() throws CommonServiceException {
    var costFactorEntity = testUtil.getCostFactorEntity();
    costFactorEntity.setIsBucketed(false);

    when(costFactorRepository.findByIdAndOrgId(anyLong(), anyString()))
        .thenReturn(Optional.of(costFactorEntity));
    when(costFactorRepository.save(any(CostFactorEntity.class)))
        .thenReturn(testUtil.getCostFactorEntity());
    when(costFactorAuditLogRepository.save(any(CostFactorAuditLogEntity.class)))
        .thenReturn(testUtil.getCostFactorAuditLogEntity());

    CostFactorDto costFactorResponse =
        costFactorService.updateCostFactor(
            COST_FACTOR_ID, ORG_ID, testUtil.getCostFactorUpdationRequest());
    assertEquals(
        testUtil.getCostFactorEntity().getCostFactor(), costFactorResponse.getCostFactor());
    assertEquals(
        testUtil.getCostAttributeMappingRequest().getCustomAttributes(),
        costFactorResponse.getCustomAttributes());

    verify(costFactorRepository, times(1)).findByIdAndOrgId(anyLong(), anyString());
    verify(costFactorRepository, times(1)).save(any(CostFactorEntity.class));
    verify(costFactorAuditLogRepository, times(1)).save(any(CostFactorAuditLogEntity.class));
  }

  @Test
  @DisplayName("Update cost factor - when cost factor is not present in db")
  void updateCostFactorWhenEntityIsNotThereInDBTest() {
    when(costFactorRepository.findByIdAndOrgId(anyLong(), anyString()))
        .thenReturn(Optional.empty());

    CommonServiceException ex =
        assertThrows(
            CommonServiceException.class,
            () ->
                costFactorService.updateCostFactor(
                    COST_FACTOR_ID, ORG_ID, testUtil.getCostFactorUpdationRequest()));

    assertEquals("Cost Factor not found", ex.getMessage());
    verify(costFactorRepository, times(1)).findByIdAndOrgId(anyLong(), anyString());
    verify(costFactorRepository, times(0)).save(any(CostFactorEntity.class));
  }

  @Test
  @DisplayName("Update cost factor - When displayName is not unique")
  void updateCostFactorDuplicateDisplayNameTest() {
    var costFactorEntity = testUtil.getCostFactorEntity();
    costFactorEntity.setIsBucketed(false);
    when(costFactorRepository.findByIdAndOrgId(anyLong(), anyString()))
        .thenReturn(Optional.of(costFactorEntity));
    when(costFactorRepository.findFirstByOrgIdAndDisplayName(anyString(), anyString()))
        .thenReturn(Optional.of(costFactorEntity));

    CommonServiceException ex =
        assertThrows(
            CommonServiceException.class,
            () ->
                costFactorService.updateCostFactor(
                    COST_FACTOR_ID, ORG_ID, testUtil.getCostFactorUpdationRequest()));

    assertEquals(COST_FACTOR_DISPLAY_NAME_NOT_UNIQUE_EXCEPTION_MESSAGE, ex.getMessage());

    verify(costFactorRepository, times(0)).save(any(CostFactorEntity.class));
    verify(costFactorAuditLogRepository, times(0)).save(any(CostFactorAuditLogEntity.class));
  }

  @Test
  @DisplayName("Update cost factor - updating uom for bucketed cost factors")
  void updateCostFactorBucketedUomTest() {
    when(costFactorRepository.findByIdAndOrgId(anyLong(), anyString()))
        .thenReturn(Optional.of(testUtil.getCostFactorEntity()));
    when(costFactorRepository.findFirstByOrgIdAndDisplayName(anyString(), anyString()))
        .thenReturn(Optional.empty());
    when(costFactorRepository.save(any(CostFactorEntity.class)))
        .thenReturn(testUtil.getCostFactorEntity());
    when(costFactorAuditLogRepository.save(any(CostFactorAuditLogEntity.class)))
        .thenReturn(testUtil.getCostFactorAuditLogEntity());

    CommonServiceException ex =
        assertThrows(
            CommonServiceException.class,
            () ->
                costFactorService.updateCostFactor(
                    COST_FACTOR_ID, ORG_ID, testUtil.getCostFactorUpdationRequest()));

    assertEquals(UPDATE_BUCKETED_COST_FACTOR_UOM_EXCEPTION_MESSAGE, ex.getMessage());

    verify(costFactorRepository, times(1)).findByIdAndOrgId(anyLong(), anyString());
    verify(costFactorRepository, times(0)).save(any(CostFactorEntity.class));
    verify(costFactorAuditLogRepository, times(0)).save(any(CostFactorAuditLogEntity.class));
  }

  @Test
  @DisplayName("Delete cost factor - happy path")
  void deleteCostFactorTest() throws CommonServiceException {
    when(costFactorRepository.findByIdAndOrgId(anyLong(), anyString()))
        .thenReturn(Optional.ofNullable(testUtil.getCostFactorEntity()));
    doNothing().when(costFactorRepository).delete(any(CostFactorEntity.class));
    when(costItineraryAndFactorsRepository.findCostFactorInItineraries(anyString(), anyString()))
        .thenReturn(List.of());

    CostFactorDto costFactorResponse = costFactorService.deleteCostFactor(COST_FACTOR_ID, ORG_ID);
    assertEquals(
        testUtil.getCostFactorEntity().getCostFactor(), costFactorResponse.getCostFactor());
    verify(costFactorRepository, times(1)).findByIdAndOrgId(anyLong(), anyString());
    verify(costItineraryAndFactorsRepository, times(1))
        .findCostFactorInItineraries(anyString(), anyString());
    verify(costFactorRepository, times(1)).delete(any(CostFactorEntity.class));
  }

  @Test
  @DisplayName("Delete cost factor - when cost factor is not present in db")
  void deleteCostFactorWhenEntityIsNotThereInDBTest() {
    when(costFactorRepository.findByIdAndOrgId(anyLong(), anyString()))
        .thenReturn(Optional.empty());

    CommonServiceException ex =
        assertThrows(
            CommonServiceException.class,
            () -> costFactorService.deleteCostFactor(COST_FACTOR_ID, ORG_ID));

    assertEquals("Cost Factor not found", ex.getMessage());
    verify(costFactorRepository, times(1)).findByIdAndOrgId(anyLong(), anyString());
    verify(costItineraryAndFactorsRepository, times(0))
        .findCostFactorInItineraries(anyString(), anyString());
    verify(costFactorRepository, times(0)).delete(any(CostFactorEntity.class));
  }

  @Test
  @DisplayName("Delete cost factor - when cost factor is associate to one or more cost itineraries")
  void deleteCostFactorWhenCostFactorIsAssociatedWithItineraryTest() {
    when(costFactorRepository.findByIdAndOrgId(anyLong(), anyString()))
        .thenReturn(Optional.ofNullable(testUtil.getCostFactorEntity()));
    when(costItineraryAndFactorsRepository.findCostFactorInItineraries(anyString(), anyString()))
        .thenReturn(List.of(testUtil.getCostItineraryAndFactorsEntity()));

    CommonServiceException ex =
        assertThrows(
            CommonServiceException.class,
            () -> costFactorService.deleteCostFactor(COST_FACTOR_ID, ORG_ID));

    assertEquals("Cost factor is associated with one or more cost itinerary", ex.getMessage());
    assertEquals(HttpStatus.BAD_REQUEST, ex.getHttpStatus());
    verify(costFactorRepository, times(1)).findByIdAndOrgId(anyLong(), anyString());
    verify(costItineraryAndFactorsRepository, times(1))
        .findCostFactorInItineraries(anyString(), anyString());
    verify(costFactorRepository, times(0)).delete(any(CostFactorEntity.class));
  }

  @Test
  void getAllCostFactorCacheKeysTest() {
    List<CostFactorEntity> costFactorEntities = testUtil.getCostFactorEntityList();

    when(costFactorRepository.findAllCostFactorEntities(any())).thenReturn(costFactorEntities);

    List<CostFactorCacheKeyDto> response = costFactorService.getCostFactorCacheKeys(2);

    Assertions.assertEquals(2, response.size());
    Assertions.assertEquals(costFactorEntities.get(0).getOrgId(), response.get(0).getOrgId());
    verify(costFactorRepository, VerificationModeFactory.times(1)).findAllCostFactorEntities(any());
  }
}
