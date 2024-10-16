/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.sourcing.cost.config.service;

import static com.nextuple.sourcing.cost.config.utils.TestUtil.COST_FACTOR_BILL_WEIGHT_UPS;
import static com.nextuple.sourcing.cost.config.utils.TestUtil.COST_FACTOR_ZONE;
import static com.nextuple.sourcing.cost.config.utils.TestUtil.ORG_ID;
import static com.nextuple.sourcing.cost.config.utils.TestUtil.PREV_SLAB_VALUE;
import static com.nextuple.sourcing.cost.config.utils.TestUtil.SELECTOR_CF;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.sourcing.cost.config.domain.entity.CostValueEntity;
import com.nextuple.sourcing.cost.config.domain.entity.SelectorAndCostItineraryMappingEntity;
import com.nextuple.sourcing.cost.config.domain.repository.CostFactorRepository;
import com.nextuple.sourcing.cost.config.domain.repository.CostItineraryAndFactorsRepository;
import com.nextuple.sourcing.cost.config.domain.repository.CostValueRepository;
import com.nextuple.sourcing.cost.config.domain.repository.PreferenceSelectorRepository;
import com.nextuple.sourcing.cost.config.domain.repository.SelectorAndCostItineraryMappingRepository;
import com.nextuple.sourcing.cost.config.domain.repository.TenantCostTypeRepository;
import com.nextuple.sourcing.cost.config.dto.SelectorCostFactorInfoDto;
import com.nextuple.sourcing.cost.config.inbound.CostDefinitionRequest;
import com.nextuple.sourcing.cost.config.outbound.CostDefinitionResponse;
import com.nextuple.sourcing.cost.config.service.validation.RateCardRequestValidationFactory;
import com.nextuple.sourcing.cost.config.service.validation.impl.RateCardRequestSelectorValidationImpl;
import com.nextuple.sourcing.cost.config.service.validation.impl.RateCardRequestValidationImpl;
import com.nextuple.sourcing.cost.config.utils.TestUtil;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;

class RateCardTableDashboardServiceTest {

  @InjectMocks RateCardTableDashboardService rateCardTableDashboardService;

  @Mock SelectorAndCostItineraryMappingRepository selectorAndCostItineraryMappingRepository;
  @Mock CostFactorRepository costFactorRepository;
  @Mock CostValueRepository costValueRepository;
  @Mock CostItineraryAndFactorsRepository costItineraryAndFactorsRepository;
  @Mock TenantCostTypeRepository tenantCostTypeRepository;
  @Mock CostTypeDashboardService costTypeDashboardService;
  @Mock RateCardRequestValidationFactory rateCardRequestValidationFactory;
  @Mock RateCardRequestValidationImpl rateCardRequestValidation;
  @Mock RateCardRequestSelectorValidationImpl rateCardRequestWithSelectorValidation;
  @Mock PreferenceSelectorRepository preferenceSelectorRepository;

  @InjectMocks TestUtil testUtil;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  @DisplayName("Get Rate Card Grid Data: Happy Path")
  void getRateCardGridDataHappyPathTest() throws CommonServiceException {
    CostDefinitionRequest costDefinitionRequest =
        testUtil.getCostDefinitionRequest(SELECTOR_CF, "billWeightUps", "zone");
    List<String> costFactorKeys =
        testUtil.getCostFactorCombinationKeyForGrid(List.of("S", "M", "L"));
    List<CostValueEntity> costValueEntities = testUtil.getCostValueList(costFactorKeys);
    costValueEntities.get(costValueEntities.size() - 1).setPrevSlabValue(PREV_SLAB_VALUE);

    when(tenantCostTypeRepository.findByOrgIdAndCostType(any(), any()))
        .thenReturn(Optional.ofNullable(testUtil.getTenantCostTypeEntity()));
    when(selectorAndCostItineraryMappingRepository
            .findByOrgIdAndCostTypeAndSelectorCfAndSelectorCfValue(any(), any(), any(), any()))
        .thenReturn(List.of(testUtil.getSelectorAndCostItineraryMappingEntity()));
    when(costFactorRepository.findByOrgIdAndCostFactor(ORG_ID, COST_FACTOR_BILL_WEIGHT_UPS))
        .thenReturn(List.of(testUtil.getCostFactorEntity()));
    when(costFactorRepository.findByOrgIdAndCostFactor(ORG_ID, COST_FACTOR_ZONE))
        .thenReturn(List.of(testUtil.getCostFactorEntity()));
    when(costValueRepository.findByOrgIdAndCostItineraryAndCostFactorCombinationKeyIn(
            any(), any(), any()))
        .thenReturn(costValueEntities);
    when(costItineraryAndFactorsRepository.findByOrgIdAndCostItinerary(any(), any()))
        .thenReturn(Optional.ofNullable(testUtil.getCostItineraryAndFactorsEntity()));
    when(preferenceSelectorRepository.findByOrgIdAndCostType(anyString(), anyString()))
        .thenReturn(List.of(testUtil.getPreferenceSelectorEntity()));
    when(costTypeDashboardService.getCostTypeDetailsForValidation(anyString(), anyString()))
        .thenReturn(testUtil.getCostTypeValidationResponse());
    when(rateCardRequestValidationFactory.getRateCardRequestValidationFactory(
            any(SelectorCostFactorInfoDto.class)))
        .thenReturn(rateCardRequestWithSelectorValidation);
    doNothing()
        .when(rateCardRequestWithSelectorValidation)
        .validateCostDefinitionRequest(any(), any());

    CostDefinitionResponse response =
        rateCardTableDashboardService.getRateCardTableData(ORG_ID, costDefinitionRequest);

    assertNotNull(response);
    assertEquals("false", response.getRows().getData().get(0).get("isDynamicBucket"));
    assertEquals(
        "true",
        response
            .getRows()
            .getData()
            .get(response.getRows().getData().size() - 1)
            .get("isDynamicBucket"));
    assertTrue(response.isRateCardActive());
    assertEquals(TestUtil.COST_TYPE_DISPLAY_NAME, response.getColumns().getTitle());
    assertEquals(4, response.getColumns().getHeaders().size());
    assertEquals(TestUtil.COLUMN_META, response.getColumns().getHeaders().get(0).getColumnMeta());
    assertEquals(TestUtil.COLUMN_NAME, response.getColumns().getHeaders().get(0).getColumnName());

    verify(tenantCostTypeRepository, times(2)).findByOrgIdAndCostType(any(), any());
    verify(selectorAndCostItineraryMappingRepository, times(1))
        .findByOrgIdAndCostTypeAndSelectorCfAndSelectorCfValue(any(), any(), any(), any());
    verify(costFactorRepository, times(4)).findByOrgIdAndCostFactor(any(), any());
    verify(costValueRepository, times(1))
        .findByOrgIdAndCostItineraryAndCostFactorCombinationKeyIn(any(), any(), any());
    verify(costItineraryAndFactorsRepository, times(1)).findByOrgIdAndCostItinerary(any(), any());
  }

  @Test
  @DisplayName("Get Rate Card Grid Data: Happy Path:without selector and filter")
  void getRateCardGridDataHappyPathWithOutSelectorAndFilterTest() throws CommonServiceException {
    CostDefinitionRequest costDefinitionRequest =
        testUtil.getCostDefinitionRequest(
            SELECTOR_CF, COST_FACTOR_BILL_WEIGHT_UPS, COST_FACTOR_ZONE);
    costDefinitionRequest.setSelector(null);
    costDefinitionRequest.setCostType("PROCESSING_COST");

    List<String> costFactorKeys =
        testUtil.getCostFactorCombinationKeyForGrid(List.of("Zone1|S", "Zone1|M", "Zone1|L"));
    List<CostValueEntity> costValueEntities = testUtil.getCostValueList(costFactorKeys);
    costValueEntities.get(costValueEntities.size() - 1).setPrevSlabValue(PREV_SLAB_VALUE);
    when(tenantCostTypeRepository.findByOrgIdAndCostType(any(), any()))
        .thenReturn(Optional.ofNullable(testUtil.getTenantCostTypeEntity()));
    when(selectorAndCostItineraryMappingRepository
            .findByOrgIdAndCostTypeAndSelectorCfAndSelectorCfValue(any(), any(), any(), any()))
        .thenReturn(List.of(testUtil.getSelectorAndCostItineraryMappingEntity()));
    when(costFactorRepository.findByOrgIdAndCostFactor(any(), any()))
        .thenReturn(List.of(testUtil.getCostFactorEntity()));
    when(costValueRepository.findByOrgIdAndCostItineraryAndCostFactorCombinationKeyIn(
            any(), any(), any()))
        .thenReturn(costValueEntities);
    when(costItineraryAndFactorsRepository.findByOrgIdAndCostItinerary(any(), any()))
        .thenReturn(Optional.ofNullable(testUtil.getCostItineraryAndFactorsEntity()));
    when(preferenceSelectorRepository.findByOrgIdAndCostType(anyString(), anyString()))
        .thenReturn(List.of());
    when(costTypeDashboardService.getCostTypeDetailsForValidation(anyString(), anyString()))
        .thenReturn(testUtil.getCostTypeValidationResponse());
    when(rateCardRequestValidationFactory.getRateCardRequestValidationFactory(any()))
        .thenReturn(rateCardRequestValidation);
    doNothing().when(rateCardRequestValidation).validateCostDefinitionRequest(any(), any());

    CostDefinitionResponse response =
        rateCardTableDashboardService.getRateCardTableData(ORG_ID, costDefinitionRequest);

    assertNotNull(response);
    assertTrue(response.isRateCardActive());
    assertEquals("false", response.getRows().getData().get(0).get("isDynamicBucket"));
    assertEquals(
        "false",
        response
            .getRows()
            .getData()
            .get(response.getRows().getData().size() - 1)
            .get("isDynamicBucket"));
    assertEquals(TestUtil.COST_TYPE_DISPLAY_NAME, response.getColumns().getTitle());
    assertEquals(4, response.getColumns().getHeaders().size());
    assertEquals(TestUtil.COLUMN_META, response.getColumns().getHeaders().get(0).getColumnMeta());
    assertEquals(TestUtil.COLUMN_NAME, response.getColumns().getHeaders().get(0).getColumnName());

    verify(tenantCostTypeRepository, times(2)).findByOrgIdAndCostType(any(), any());
    verify(selectorAndCostItineraryMappingRepository, times(1))
        .findByOrgIdAndCostTypeAndSelectorCfAndSelectorCfValue(any(), any(), any(), any());
    verify(costFactorRepository, times(4)).findByOrgIdAndCostFactor(any(), any());
    verify(costValueRepository, times(1))
        .findByOrgIdAndCostItineraryAndCostFactorCombinationKeyIn(any(), any(), any());
    verify(costItineraryAndFactorsRepository, times(1)).findByOrgIdAndCostItinerary(any(), any());
  }

  @Test
  @DisplayName("Get Rate Card Table Data: Happy Path")
  void getRateCardTableDataHappyPathTest() throws CommonServiceException {
    CostDefinitionRequest costDefinitionRequest =
        testUtil.getCostDefinitionRequest(SELECTOR_CF, COST_FACTOR_BILL_WEIGHT_UPS, null);

    when(tenantCostTypeRepository.findByOrgIdAndCostType(any(), any()))
        .thenReturn(Optional.ofNullable(testUtil.getTenantCostTypeEntity()));
    when(selectorAndCostItineraryMappingRepository
            .findByOrgIdAndCostTypeAndSelectorCfAndSelectorCfValue(any(), any(), any(), any()))
        .thenReturn(List.of(testUtil.getSelectorAndCostItineraryMappingEntity()));
    when(costFactorRepository.findByOrgIdAndCostFactor(ORG_ID, COST_FACTOR_BILL_WEIGHT_UPS))
        .thenReturn(List.of(testUtil.getCostFactorEntity()));
    when(costFactorRepository.findByOrgIdAndCostFactor(ORG_ID, COST_FACTOR_ZONE))
        .thenReturn(List.of(testUtil.getCostFactorEntity()));
    when(costItineraryAndFactorsRepository.findByOrgIdAndCostItinerary(any(), any()))
        .thenReturn(Optional.ofNullable(testUtil.getCostItineraryAndFactorsEntity()));
    when(preferenceSelectorRepository.findByOrgIdAndCostType(anyString(), anyString()))
        .thenReturn(List.of(testUtil.getPreferenceSelectorEntity()));
    when(costTypeDashboardService.getCostTypeDetailsForValidation(anyString(), anyString()))
        .thenReturn(testUtil.getCostTypeValidationResponse());
    when(rateCardRequestValidationFactory.getRateCardRequestValidationFactory(
            any(SelectorCostFactorInfoDto.class)))
        .thenReturn(rateCardRequestWithSelectorValidation);
    doNothing()
        .when(rateCardRequestWithSelectorValidation)
        .validateCostDefinitionRequest(any(), any());

    CostDefinitionResponse response =
        rateCardTableDashboardService.getRateCardTableData(ORG_ID, costDefinitionRequest);

    assertNotNull(response);
    assertTrue(response.isRateCardActive());
    assertEquals("", response.getColumns().getTitle());
    assertEquals(2, response.getColumns().getHeaders().size());
    assertEquals(TestUtil.COLUMN_META, response.getColumns().getHeaders().get(0).getColumnMeta());
    assertEquals(TestUtil.COLUMN_NAME, response.getColumns().getHeaders().get(0).getColumnName());

    verify(tenantCostTypeRepository, times(2)).findByOrgIdAndCostType(any(), any());
    verify(selectorAndCostItineraryMappingRepository, times(1))
        .findByOrgIdAndCostTypeAndSelectorCfAndSelectorCfValue(any(), any(), any(), any());
    verify(costFactorRepository, times(2)).findByOrgIdAndCostFactor(any(), any());
    verify(costValueRepository, times(1))
        .findByOrgIdAndCostItineraryAndCostFactorCombinationKeyIn(any(), any(), any());
    verify(costItineraryAndFactorsRepository, times(1)).findByOrgIdAndCostItinerary(any(), any());
  }

  @Test
  @DisplayName("Get Rate Card Grid Data: Testing for NA response when cost is not configured")
  void getRateCardGridNAResponseValueTest() throws CommonServiceException {
    CostDefinitionRequest costDefinitionRequest =
        testUtil.getCostDefinitionRequest(
            SELECTOR_CF, COST_FACTOR_BILL_WEIGHT_UPS, COST_FACTOR_ZONE);
    List<CostValueEntity> costValueEntities = List.of(testUtil.getCostValueEntity("UPS"));
    costValueEntities.get(costValueEntities.size() - 1).setPrevSlabValue(PREV_SLAB_VALUE);

    when(tenantCostTypeRepository.findByOrgIdAndCostType(any(), any()))
        .thenReturn(Optional.ofNullable(testUtil.getTenantCostTypeEntity()));
    when(selectorAndCostItineraryMappingRepository
            .findByOrgIdAndCostTypeAndSelectorCfAndSelectorCfValue(any(), any(), any(), any()))
        .thenReturn(List.of(testUtil.getSelectorAndCostItineraryMappingEntity()));
    when(costFactorRepository.findByOrgIdAndCostFactor(ORG_ID, COST_FACTOR_BILL_WEIGHT_UPS))
        .thenReturn(List.of(testUtil.getCostFactorEntity()));
    when(costFactorRepository.findByOrgIdAndCostFactor(ORG_ID, COST_FACTOR_ZONE))
        .thenReturn(List.of(testUtil.getCostFactorEntity()));
    when(costValueRepository.findByOrgIdAndCostItineraryAndCostFactorCombinationKeyIn(
            any(), any(), any()))
        .thenReturn(costValueEntities);
    when(costItineraryAndFactorsRepository.findByOrgIdAndCostItinerary(any(), any()))
        .thenReturn(Optional.ofNullable(testUtil.getCostItineraryAndFactorsEntity()));
    when(preferenceSelectorRepository.findByOrgIdAndCostType(anyString(), anyString()))
        .thenReturn(List.of(testUtil.getPreferenceSelectorEntity()));
    when(costTypeDashboardService.getCostTypeDetailsForValidation(anyString(), anyString()))
        .thenReturn(testUtil.getCostTypeValidationResponse());
    when(rateCardRequestValidationFactory.getRateCardRequestValidationFactory(
            any(SelectorCostFactorInfoDto.class)))
        .thenReturn(rateCardRequestWithSelectorValidation);
    doNothing()
        .when(rateCardRequestWithSelectorValidation)
        .validateCostDefinitionRequest(any(), any());

    CostDefinitionResponse response =
        rateCardTableDashboardService.getRateCardTableData(ORG_ID, costDefinitionRequest);

    assertNotNull(response);
    assertEquals("false", response.getRows().getData().get(0).get("isDynamicBucket"));
    assertEquals("NA", response.getRows().getData().get(0).get("s"));
    assertTrue(response.isRateCardActive());
    assertEquals(TestUtil.COST_TYPE_DISPLAY_NAME, response.getColumns().getTitle());
    assertEquals(4, response.getColumns().getHeaders().size());
    assertEquals(TestUtil.COLUMN_META, response.getColumns().getHeaders().get(0).getColumnMeta());
    assertEquals(TestUtil.COLUMN_NAME, response.getColumns().getHeaders().get(0).getColumnName());

    verify(tenantCostTypeRepository, times(2)).findByOrgIdAndCostType(any(), any());
    verify(selectorAndCostItineraryMappingRepository, times(1))
        .findByOrgIdAndCostTypeAndSelectorCfAndSelectorCfValue(any(), any(), any(), any());
    verify(costFactorRepository, times(4)).findByOrgIdAndCostFactor(any(), any());
    verify(costValueRepository, times(1))
        .findByOrgIdAndCostItineraryAndCostFactorCombinationKeyIn(any(), any(), any());
    verify(costItineraryAndFactorsRepository, times(1)).findByOrgIdAndCostItinerary(any(), any());
  }

  @Test
  @DisplayName("Get Rate Card Table Data: Happy Path: without selector and filter")
  void getRateCardTableDataHappyPathWithOutSelectorAndFilterTest() throws CommonServiceException {
    CostDefinitionRequest costDefinitionRequest =
        testUtil.getCostDefinitionRequest(SELECTOR_CF, COST_FACTOR_BILL_WEIGHT_UPS, null);
    costDefinitionRequest.setSelector(null);
    costDefinitionRequest.setCostType("PROCESSING_COST");

    List<String> costFactorKeys =
        testUtil.getCostFactorCombinationKeyForTable(List.of("S", "M", "L"));
    when(tenantCostTypeRepository.findByOrgIdAndCostType(any(), any()))
        .thenReturn(Optional.ofNullable(testUtil.getTenantCostTypeEntity()));
    when(costFactorRepository.findByOrgIdAndCostFactor(any(), any()))
        .thenReturn(List.of(testUtil.getCostFactorEntity()));
    when(selectorAndCostItineraryMappingRepository
            .findByOrgIdAndCostTypeAndSelectorCfAndSelectorCfValue(any(), any(), any(), any()))
        .thenReturn(List.of(testUtil.getSelectorAndCostItineraryMappingEntity()));
    when(costItineraryAndFactorsRepository.findByOrgIdAndCostItinerary(any(), any()))
        .thenReturn(Optional.ofNullable(testUtil.getCostItineraryAndFactorsEntity()));
    when(costValueRepository.findByOrgIdAndCostItineraryAndCostFactorCombinationKeyIn(
            any(), any(), any()))
        .thenReturn(testUtil.getCostValueList(costFactorKeys));
    when(preferenceSelectorRepository.findByOrgIdAndCostType(anyString(), anyString()))
        .thenReturn(List.of());
    when(costTypeDashboardService.getCostTypeDetailsForValidation(anyString(), anyString()))
        .thenReturn(testUtil.getCostTypeValidationResponse());
    when(rateCardRequestValidationFactory.getRateCardRequestValidationFactory(any()))
        .thenReturn(rateCardRequestValidation);
    doNothing().when(rateCardRequestValidation).validateCostDefinitionRequest(any(), any());

    CostDefinitionResponse response =
        rateCardTableDashboardService.getRateCardTableData(ORG_ID, costDefinitionRequest);
    assertNotNull(response);
    assertEquals(2, response.getColumns().getHeaders().size());
    assertEquals(TestUtil.COLUMN_META, response.getColumns().getHeaders().get(0).getColumnMeta());
    assertEquals(TestUtil.COLUMN_NAME, response.getColumns().getHeaders().get(0).getColumnName());
    assertTrue(response.isRateCardActive());
    assertEquals("", response.getColumns().getTitle());
    verify(tenantCostTypeRepository, times(2)).findByOrgIdAndCostType(any(), any());
    verify(selectorAndCostItineraryMappingRepository, times(1))
        .findByOrgIdAndCostTypeAndSelectorCfAndSelectorCfValue(any(), any(), any(), any());
    verify(costFactorRepository, times(2)).findByOrgIdAndCostFactor(any(), any());
    verify(costValueRepository, times(1))
        .findByOrgIdAndCostItineraryAndCostFactorCombinationKeyIn(any(), any(), any());
    verify(costItineraryAndFactorsRepository, times(1)).findByOrgIdAndCostItinerary(any(), any());
  }

  @Test
  @DisplayName("Get Rate Card Static Table Data: Happy Path")
  void getRateCardStaticTableDataHappyPathTest() throws CommonServiceException {
    CostDefinitionRequest costDefinitionRequest =
        testUtil.getCostDefinitionRequest(SELECTOR_CF, null, null);
    List<String> costFactorKeys =
        testUtil.getCostFactorCombinationKeyForTable(List.of(TestUtil.COST_TYPE));

    when(tenantCostTypeRepository.findByOrgIdAndCostType(any(), any()))
        .thenReturn(Optional.ofNullable(testUtil.getTenantCostTypeEntity()));
    when(selectorAndCostItineraryMappingRepository
            .findByOrgIdAndCostTypeAndSelectorCfAndSelectorCfValue(any(), any(), any(), any()))
        .thenReturn(List.of(testUtil.getSelectorAndCostItineraryMappingEntity()));
    when(costValueRepository.findByOrgIdAndCostItineraryAndCostFactorCombinationKeyIn(
            any(), any(), any()))
        .thenReturn(testUtil.getCostValueList(costFactorKeys));
    when(costItineraryAndFactorsRepository.findByOrgIdAndCostItinerary(any(), any()))
        .thenReturn(Optional.ofNullable(testUtil.getCostItineraryAndFactorsEntity()));
    when(preferenceSelectorRepository.findByOrgIdAndCostType(anyString(), anyString()))
        .thenReturn(List.of(testUtil.getPreferenceSelectorEntity()));
    when(costTypeDashboardService.getCostTypeDetailsForValidation(anyString(), anyString()))
        .thenReturn(testUtil.getCostTypeValidationResponse());
    when(rateCardRequestValidationFactory.getRateCardRequestValidationFactory(
            any(SelectorCostFactorInfoDto.class)))
        .thenReturn(rateCardRequestWithSelectorValidation);
    doNothing()
        .when(rateCardRequestWithSelectorValidation)
        .validateCostDefinitionRequest(any(), any());

    CostDefinitionResponse response =
        rateCardTableDashboardService.getRateCardTableData(ORG_ID, costDefinitionRequest);

    assertNotNull(response);
    assertTrue(response.isRateCardActive());
    assertEquals("", response.getColumns().getTitle());
    assertEquals(1, response.getColumns().getHeaders().size());
    assertFalse(response.getColumns().getHeaders().get(0).isCostFactor());
    assertNotNull(response.getRows().getData().get(0));
    assertEquals(
        TestUtil.COST_TYPE_CAMEL_CASE, response.getColumns().getHeaders().get(0).getColumnMeta());
    assertEquals(TestUtil.COST_TYPE_DISPLAY_NAME, testUtil.getCostFactorEntity().getDisplayName());

    verify(tenantCostTypeRepository, times(2)).findByOrgIdAndCostType(any(), any());
    verify(selectorAndCostItineraryMappingRepository, times(1))
        .findByOrgIdAndCostTypeAndSelectorCfAndSelectorCfValue(any(), any(), any(), any());
    verify(costValueRepository, times(1))
        .findByOrgIdAndCostItineraryAndCostFactorCombinationKeyIn(any(), any(), any());
    verify(costItineraryAndFactorsRepository, times(1)).findByOrgIdAndCostItinerary(any(), any());
  }

  @Test
  @DisplayName("Get Rate Card Static Table Data: With selector but no selector value")
  void getRateCardStaticTableDataHappyPathTestWithSelectorButNoSelectorValue()
      throws CommonServiceException {
    CostDefinitionRequest costDefinitionRequest =
        testUtil.getCostDefinitionRequest(SELECTOR_CF, null, null);
    costDefinitionRequest.getSelector().setSelectorCfValue("");
    List<String> costFactorKeys =
        testUtil.getCostFactorCombinationKeyForTable(List.of(TestUtil.COST_TYPE));

    SelectorAndCostItineraryMappingEntity selectorAndCostItineraryMappingEntity =
        testUtil.getSelectorAndCostItineraryMappingEntity();
    selectorAndCostItineraryMappingEntity.setSelectorCfValue(null);
    when(tenantCostTypeRepository.findByOrgIdAndCostType(any(), any()))
        .thenReturn(Optional.ofNullable(testUtil.getTenantCostTypeEntity()));
    when(selectorAndCostItineraryMappingRepository
            .findByOrgIdAndCostTypeAndSelectorCfAndSelectorCfValue(any(), any(), any(), eq(null)))
        .thenReturn(List.of(selectorAndCostItineraryMappingEntity));
    when(costValueRepository.findByOrgIdAndCostItineraryAndCostFactorCombinationKeyIn(
            any(), any(), any()))
        .thenReturn(testUtil.getCostValueList(costFactorKeys));
    when(costItineraryAndFactorsRepository.findByOrgIdAndCostItinerary(any(), any()))
        .thenReturn(Optional.ofNullable(testUtil.getCostItineraryAndFactorsEntity()));
    when(preferenceSelectorRepository.findByOrgIdAndCostType(anyString(), anyString()))
        .thenReturn(List.of(testUtil.getPreferenceSelectorEntity()));
    when(costTypeDashboardService.getCostTypeDetailsForValidation(anyString(), anyString()))
        .thenReturn(testUtil.getCostTypeValidationResponse());
    when(rateCardRequestValidationFactory.getRateCardRequestValidationFactory(
            any(SelectorCostFactorInfoDto.class)))
        .thenReturn(rateCardRequestWithSelectorValidation);
    doNothing()
        .when(rateCardRequestWithSelectorValidation)
        .validateCostDefinitionRequest(any(), any());

    CostDefinitionResponse response =
        rateCardTableDashboardService.getRateCardTableData(ORG_ID, costDefinitionRequest);

    assertNotNull(response);
    assertTrue(response.isRateCardActive());
    assertEquals("", response.getColumns().getTitle());
    assertEquals(1, response.getColumns().getHeaders().size());
    assertFalse(response.getColumns().getHeaders().get(0).isCostFactor());
    assertNotNull(response.getRows().getData().get(0));
    assertEquals(
        TestUtil.COST_TYPE_CAMEL_CASE, response.getColumns().getHeaders().get(0).getColumnMeta());
    assertEquals(TestUtil.COST_TYPE_DISPLAY_NAME, testUtil.getCostFactorEntity().getDisplayName());

    verify(tenantCostTypeRepository, times(2)).findByOrgIdAndCostType(any(), any());
    verify(selectorAndCostItineraryMappingRepository, times(1))
        .findByOrgIdAndCostTypeAndSelectorCfAndSelectorCfValue(any(), any(), any(), any());
    verify(costValueRepository, times(1))
        .findByOrgIdAndCostItineraryAndCostFactorCombinationKeyIn(any(), any(), any());
    verify(costItineraryAndFactorsRepository, times(1)).findByOrgIdAndCostItinerary(any(), any());
  }

  @Test
  @DisplayName("Get Rate Card Static Table Data: Happy Path: without selector and filter")
  void getRateCardStaticTableDataHappyPathWithOutSelectorAndFilterTest()
      throws CommonServiceException {
    CostDefinitionRequest costDefinitionRequest =
        testUtil.getCostDefinitionRequest(SELECTOR_CF, null, null);
    costDefinitionRequest.setFilters(List.of());
    costDefinitionRequest.setSelector(null);
    List<String> costFactorKeys = testUtil.getCostFactorCombinationKeyForTable(List.of(""));

    when(tenantCostTypeRepository.findByOrgIdAndCostType(any(), any()))
        .thenReturn(Optional.ofNullable(testUtil.getTenantCostTypeEntity()));

    when(costValueRepository.findByOrgIdAndCostItineraryAndCostFactorCombinationKeyIn(
            any(), any(), any()))
        .thenReturn(testUtil.getCostValueList(costFactorKeys));
    when(costItineraryAndFactorsRepository.findByOrgIdAndCostItinerary(any(), any()))
        .thenReturn(Optional.ofNullable(testUtil.getCostItineraryAndFactorsEntity()));
    when(selectorAndCostItineraryMappingRepository
            .findByOrgIdAndCostTypeAndSelectorCfAndSelectorCfValue(any(), any(), any(), any()))
        .thenReturn(List.of(testUtil.getSelectorAndCostItineraryMappingEntity()));
    when(costFactorRepository.findByOrgIdAndCostFactor(any(), any()))
        .thenReturn(List.of(testUtil.getCostFactorEntity()));
    when(preferenceSelectorRepository.findByOrgIdAndCostType(anyString(), anyString()))
        .thenReturn(List.of());
    when(costTypeDashboardService.getCostTypeDetailsForValidation(anyString(), anyString()))
        .thenReturn(testUtil.getCostTypeValidationResponse());
    when(rateCardRequestValidationFactory.getRateCardRequestValidationFactory(any()))
        .thenReturn(rateCardRequestWithSelectorValidation);
    doNothing()
        .when(rateCardRequestWithSelectorValidation)
        .validateCostDefinitionRequest(any(), any());

    CostDefinitionResponse response =
        rateCardTableDashboardService.getRateCardTableData(ORG_ID, costDefinitionRequest);

    assertNotNull(response);
    assertTrue(response.isRateCardActive());
    assertEquals("", response.getColumns().getTitle());
    assertEquals(1, response.getColumns().getHeaders().size());
    assertFalse(response.getColumns().getHeaders().get(0).isCostFactor());
    assertNotNull(response.getRows().getData().get(0));
    assertEquals(
        TestUtil.COST_TYPE_CAMEL_CASE, response.getColumns().getHeaders().get(0).getColumnMeta());
    assertEquals(TestUtil.COST_TYPE_DISPLAY_NAME, testUtil.getCostFactorEntity().getDisplayName());

    verify(tenantCostTypeRepository, times(2)).findByOrgIdAndCostType(any(), any());
    verify(selectorAndCostItineraryMappingRepository, times(1))
        .findByOrgIdAndCostTypeAndSelectorCfAndSelectorCfValue(any(), any(), any(), any());
    verify(costValueRepository, times(1))
        .findByOrgIdAndCostItineraryAndCostFactorCombinationKeyIn(any(), any(), any());
    verify(costItineraryAndFactorsRepository, times(1)).findByOrgIdAndCostItinerary(any(), any());
  }

  @Test
  @DisplayName("Get Rate Card Data: When cost type is invalid")
  void getRateCardTableDataCostTypeExceptionTest() {
    CostDefinitionRequest costDefinitionRequest =
        testUtil.getCostDefinitionRequest("INVALID", "billWeightUps", "zone");

    when(tenantCostTypeRepository.findByOrgIdAndCostType(any(), any()))
        .thenReturn(Optional.empty());

    CommonServiceException ex =
        assertThrows(
            CommonServiceException.class,
            () ->
                rateCardTableDashboardService.getRateCardTableData(ORG_ID, costDefinitionRequest));

    assertEquals("Invalid cost type for given orgId.", ex.getMessage());
    verify(tenantCostTypeRepository, times(1)).findByOrgIdAndCostType(any(), any());
  }

  @Test
  @DisplayName("Get Rate Card Data: When cost itinerary is not found")
  void getRateCardTableDataCostItineraryExceptionTest() throws CommonServiceException {
    CostDefinitionRequest costDefinitionRequest =
        testUtil.getCostDefinitionRequest(
            SELECTOR_CF, COST_FACTOR_BILL_WEIGHT_UPS, COST_FACTOR_ZONE);
    when(tenantCostTypeRepository.findByOrgIdAndCostType(any(), any()))
        .thenReturn(Optional.ofNullable(testUtil.getTenantCostTypeEntity()));
    when(selectorAndCostItineraryMappingRepository
            .findByOrgIdAndCostTypeAndSelectorCfAndSelectorCfValue(any(), any(), any(), any()))
        .thenReturn(List.of());
    when(preferenceSelectorRepository.findByOrgIdAndCostType(anyString(), anyString()))
        .thenReturn(List.of(testUtil.getPreferenceSelectorEntity()));
    when(costTypeDashboardService.getCostTypeDetailsForValidation(anyString(), anyString()))
        .thenReturn(testUtil.getCostTypeValidationResponse());
    when(rateCardRequestValidationFactory.getRateCardRequestValidationFactory(
            any(SelectorCostFactorInfoDto.class)))
        .thenReturn(rateCardRequestWithSelectorValidation);
    doNothing()
        .when(rateCardRequestWithSelectorValidation)
        .validateCostDefinitionRequest(any(), any());

    CommonServiceException ex =
        assertThrows(
            CommonServiceException.class,
            () ->
                rateCardTableDashboardService.getRateCardTableData(ORG_ID, costDefinitionRequest));

    assertEquals("Cost itinerary not found for selector cost factor.", ex.getMessage());
    verify(selectorAndCostItineraryMappingRepository, times(1))
        .findByOrgIdAndCostTypeAndSelectorCfAndSelectorCfValue(any(), any(), any(), any());
  }

  @Test
  @DisplayName("Get Rate Card Data: When filters is Invalid")
  void getRateCardTableDataCostItineraryExceptionTestFilterInvalid() throws CommonServiceException {
    CostDefinitionRequest costDefinitionRequest =
        testUtil.getCostDefinitionRequest(SELECTOR_CF, "billWeightUps", "zone");
    costDefinitionRequest.setFilters(null);
    List<String> costFactorKeys =
        testUtil.getCostFactorCombinationKeyForGrid(List.of("S", "M", "L"));
    List<CostValueEntity> costValueEntities = testUtil.getCostValueList(costFactorKeys);
    costValueEntities.get(costValueEntities.size() - 1).setPrevSlabValue(PREV_SLAB_VALUE);

    when(tenantCostTypeRepository.findByOrgIdAndCostType(any(), any()))
        .thenReturn(Optional.ofNullable(testUtil.getTenantCostTypeEntity()));
    when(selectorAndCostItineraryMappingRepository
            .findByOrgIdAndCostTypeAndSelectorCfAndSelectorCfValue(any(), any(), any(), any()))
        .thenReturn(List.of(testUtil.getSelectorAndCostItineraryMappingEntity()));
    when(costFactorRepository.findByOrgIdAndCostFactor(ORG_ID, COST_FACTOR_BILL_WEIGHT_UPS))
        .thenReturn(List.of(testUtil.getCostFactorEntity()));
    when(costFactorRepository.findByOrgIdAndCostFactor(ORG_ID, COST_FACTOR_ZONE))
        .thenReturn(List.of(testUtil.getCostFactorEntity()));
    when(costValueRepository.findByOrgIdAndCostItineraryAndCostFactorCombinationKeyIn(
            any(), any(), any()))
        .thenReturn(costValueEntities);
    when(costItineraryAndFactorsRepository.findByOrgIdAndCostItinerary(any(), any()))
        .thenReturn(Optional.ofNullable(testUtil.getCostItineraryAndFactorsEntity()));
    when(preferenceSelectorRepository.findByOrgIdAndCostType(anyString(), anyString()))
        .thenReturn(List.of(testUtil.getPreferenceSelectorEntity()));
    when(costTypeDashboardService.getCostTypeDetailsForValidation(anyString(), anyString()))
        .thenReturn(testUtil.getCostTypeValidationResponse());
    when(rateCardRequestValidationFactory.getRateCardRequestValidationFactory(
            any(SelectorCostFactorInfoDto.class)))
        .thenReturn(rateCardRequestWithSelectorValidation);
    doThrow(
            new CommonServiceException(
                "Invalid filter or filter values", HttpStatus.BAD_REQUEST, 0x1771, null))
        .when(rateCardRequestWithSelectorValidation)
        .validateCostDefinitionRequest(any(), any());

    CommonServiceException ex =
        assertThrows(
            CommonServiceException.class,
            () ->
                rateCardTableDashboardService.getRateCardTableData(ORG_ID, costDefinitionRequest));

    assertEquals("Invalid filter or filter values", ex.getMessage());

    verify(tenantCostTypeRepository, times(1)).findByOrgIdAndCostType(any(), any());
  }

  @Test
  @DisplayName("Get Rate Card Data: When row is not valid")
  void getRateCardTableDataEmptyRowExceptionTest() throws CommonServiceException {
    CostDefinitionRequest costDefinitionRequest =
        testUtil.getCostDefinitionRequest(SELECTOR_CF, "", "zone");
    List<String> costFactorKeys =
        testUtil.getCostFactorCombinationKeyForGrid(List.of("S", "M", "L"));
    List<CostValueEntity> costValueEntities = testUtil.getCostValueList(costFactorKeys);
    costValueEntities.get(costValueEntities.size() - 1).setPrevSlabValue(PREV_SLAB_VALUE);

    when(tenantCostTypeRepository.findByOrgIdAndCostType(any(), any()))
        .thenReturn(Optional.ofNullable(testUtil.getTenantCostTypeEntity()));
    when(selectorAndCostItineraryMappingRepository
            .findByOrgIdAndCostTypeAndSelectorCfAndSelectorCfValue(any(), any(), any(), any()))
        .thenReturn(List.of(testUtil.getSelectorAndCostItineraryMappingEntity()));
    when(costFactorRepository.findByOrgIdAndCostFactor(ORG_ID, COST_FACTOR_BILL_WEIGHT_UPS))
        .thenReturn(List.of(testUtil.getCostFactorEntity()));
    when(costFactorRepository.findByOrgIdAndCostFactor(ORG_ID, COST_FACTOR_ZONE))
        .thenReturn(List.of(testUtil.getCostFactorEntity()));
    when(costValueRepository.findByOrgIdAndCostItineraryAndCostFactorCombinationKeyIn(
            any(), any(), any()))
        .thenReturn(costValueEntities);
    when(costItineraryAndFactorsRepository.findByOrgIdAndCostItinerary(any(), any()))
        .thenReturn(Optional.ofNullable(testUtil.getCostItineraryAndFactorsEntity()));
    when(preferenceSelectorRepository.findByOrgIdAndCostType(anyString(), anyString()))
        .thenReturn(List.of(testUtil.getPreferenceSelectorEntity()));
    when(costTypeDashboardService.getCostTypeDetailsForValidation(anyString(), anyString()))
        .thenReturn(testUtil.getCostTypeValidationResponse());
    when(rateCardRequestValidationFactory.getRateCardRequestValidationFactory(
            any(SelectorCostFactorInfoDto.class)))
        .thenReturn(rateCardRequestWithSelectorValidation);
    doThrow(
            new CommonServiceException(
                "Invalid cost factor in row", HttpStatus.BAD_REQUEST, 0x1771, null))
        .when(rateCardRequestWithSelectorValidation)
        .validateCostDefinitionRequest(any(), any());

    CommonServiceException ex =
        assertThrows(
            CommonServiceException.class,
            () ->
                rateCardTableDashboardService.getRateCardTableData(ORG_ID, costDefinitionRequest));

    assertEquals("Invalid cost factor in row", ex.getMessage());

    verify(tenantCostTypeRepository, times(1)).findByOrgIdAndCostType(any(), any());
  }

  @Test
  @DisplayName("Get Rate Card Data: When column is not valid")
  void getRateCardTableDataEmptyColumnExceptionTest() throws CommonServiceException {
    CostDefinitionRequest costDefinitionRequest =
        testUtil.getCostDefinitionRequest(SELECTOR_CF, "billWeightUps", "");
    List<String> costFactorKeys =
        testUtil.getCostFactorCombinationKeyForGrid(List.of("S", "M", "L"));
    List<CostValueEntity> costValueEntities = testUtil.getCostValueList(costFactorKeys);
    costValueEntities.get(costValueEntities.size() - 1).setPrevSlabValue(PREV_SLAB_VALUE);

    when(tenantCostTypeRepository.findByOrgIdAndCostType(any(), any()))
        .thenReturn(Optional.ofNullable(testUtil.getTenantCostTypeEntity()));
    when(selectorAndCostItineraryMappingRepository
            .findByOrgIdAndCostTypeAndSelectorCfAndSelectorCfValue(any(), any(), any(), any()))
        .thenReturn(List.of(testUtil.getSelectorAndCostItineraryMappingEntity()));
    when(costFactorRepository.findByOrgIdAndCostFactor(ORG_ID, COST_FACTOR_BILL_WEIGHT_UPS))
        .thenReturn(List.of(testUtil.getCostFactorEntity()));
    when(costFactorRepository.findByOrgIdAndCostFactor(ORG_ID, COST_FACTOR_ZONE))
        .thenReturn(List.of(testUtil.getCostFactorEntity()));
    when(costValueRepository.findByOrgIdAndCostItineraryAndCostFactorCombinationKeyIn(
            any(), any(), any()))
        .thenReturn(costValueEntities);
    when(costItineraryAndFactorsRepository.findByOrgIdAndCostItinerary(any(), any()))
        .thenReturn(Optional.ofNullable(testUtil.getCostItineraryAndFactorsEntity()));
    when(preferenceSelectorRepository.findByOrgIdAndCostType(anyString(), anyString()))
        .thenReturn(List.of(testUtil.getPreferenceSelectorEntity()));
    when(costTypeDashboardService.getCostTypeDetailsForValidation(anyString(), anyString()))
        .thenReturn(testUtil.getCostTypeValidationResponse());
    when(rateCardRequestValidationFactory.getRateCardRequestValidationFactory(
            any(SelectorCostFactorInfoDto.class)))
        .thenReturn(rateCardRequestWithSelectorValidation);
    doThrow(
            new CommonServiceException(
                "Invalid cost factor in column", HttpStatus.BAD_REQUEST, 0x1771, null))
        .when(rateCardRequestWithSelectorValidation)
        .validateCostDefinitionRequest(any(), any());

    CommonServiceException ex =
        assertThrows(
            CommonServiceException.class,
            () ->
                rateCardTableDashboardService.getRateCardTableData(ORG_ID, costDefinitionRequest));

    assertEquals("Invalid cost factor in column", ex.getMessage());

    verify(tenantCostTypeRepository, times(1)).findByOrgIdAndCostType(any(), any());
  }
}
