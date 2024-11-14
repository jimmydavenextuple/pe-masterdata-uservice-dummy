/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */
package com.nextuple.sourcing.cost.config.service;

import static com.nextuple.sourcing.cost.config.service.CostTypeDashboardService.ONLY_ITINERARY_DEACTIVATE_EXCEPTION_MESSAGE;
import static com.nextuple.sourcing.cost.config.utils.TestUtil.BUYING_COST;
import static com.nextuple.sourcing.cost.config.utils.TestUtil.COST_TYPE;
import static com.nextuple.sourcing.cost.config.utils.TestUtil.DISPLAY_NAME_BUYING_COST;
import static com.nextuple.sourcing.cost.config.utils.TestUtil.ORG_ID;
import static com.nextuple.sourcing.cost.config.utils.TestUtil.RATE_CARD_ACTIVE_TRUE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.configuration.feign.ConfigurationFeign;
import com.nextuple.configuration.outbound.TenantConfigdataResponse;
import com.nextuple.sourcing.cost.config.domain.entity.CostItineraryAndFactorsEntity;
import com.nextuple.sourcing.cost.config.domain.entity.SelectorAndCostItineraryMappingEntity;
import com.nextuple.sourcing.cost.config.domain.entity.TenantCostTypeEntity;
import com.nextuple.sourcing.cost.config.domain.repository.CostFactorRepository;
import com.nextuple.sourcing.cost.config.domain.repository.CostItineraryAndFactorsRepository;
import com.nextuple.sourcing.cost.config.domain.repository.OptimizationAndCostTypesMappingRepository;
import com.nextuple.sourcing.cost.config.domain.repository.PreferenceSelectorRepository;
import com.nextuple.sourcing.cost.config.domain.repository.SelectorAndCostItineraryMappingRepository;
import com.nextuple.sourcing.cost.config.domain.repository.TenantCostTypeRepository;
import com.nextuple.sourcing.cost.config.dto.costtyypesdto.CostTypeDtoInfo;
import com.nextuple.sourcing.cost.config.inbound.UpdateRateCardStatusRequest;
import com.nextuple.sourcing.cost.config.outbound.CostTypeResponse;
import com.nextuple.sourcing.cost.config.outbound.CostTypeValidationResponse;
import com.nextuple.sourcing.cost.config.outbound.UpdateRateCardStatusResponse;
import com.nextuple.sourcing.cost.config.utils.TestUtil;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.http.HttpStatus;

class CostTypeDashboardServiceTest {

  public static final String USD = "USD";
  @Mock PreferenceSelectorRepository preferenceSelectorRepository;
  @Mock TenantCostTypeRepository tenantCostTypeRepository;
  @Mock CostFactorRepository costFactorRepository;
  @Mock SelectorAndCostItineraryMappingRepository selectorAndCostItineraryMappingRepository;
  @Mock CostItineraryAndFactorsRepository costItineraryAndFactorsRepository;
  @Mock ConfigurationFeign configurationFeign;
  @Mock OptimizationAndCostTypesMappingRepository optimizationAndCostTypesMappingRepository;
  @InjectMocks private TestUtil testUtil;
  @InjectMocks @Spy CostTypeDashboardService costTypeDashboardService;

  private static final String COST_ITINERARY_AND_FACTORS_NOT_FOUND_EXCEPTION_MESSAGE =
      "Cost Itinerary And Cost Factors Mapping not found";

  private static final String INACTIVATE_RATE_CARD_STATUS_EXCEPTION_MESSAGE =
      "Couldn't inactivate rate card status for default itinerary";

  private static final String SELECTOR_ITINERARY_NOT_FOUND_EXCEPTION =
      "Selector Itinerary Mapping not found with given details";

  private static final String NO_COST_ITINERARY_ASSOCIATED_WITH_GIVEN_COST_TYPE_AND_SELECTOR =
      "No Cost Itinerary associated with given cost type and selector";

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  @DisplayName("Get Cost Type: Happy Path")
  void getCostTypeHappyPathTest() {
    when(configurationFeign.getTenantConfigdataByOrgIdAndConfigKey(any(), any()))
        .thenReturn(getTenantConfigdataResponseBaseResponse());
    when(tenantCostTypeRepository.findByOrgId(any())).thenReturn(getTenantCostTypeEntities());
    when(preferenceSelectorRepository.findByOrgIdAndCostType(any(), any()))
        .thenReturn(List.of(testUtil.getPreferenceSelectorEntity()));
    when(costFactorRepository.findByOrgIdAndCostFactor(any(), any()))
        .thenReturn(List.of(testUtil.getCostFactorEntityForSelectorCF()))
        .thenReturn(List.of(testUtil.getCostFactorEntity()));
    when(selectorAndCostItineraryMappingRepository
            .findByOrgIdAndCostTypeAndSelectorCfAndSelectorCfValue(any(), any(), any(), any()))
        .thenReturn(List.of(testUtil.getSelectorAndCostItineraryMappingEntity()));
    when(costItineraryAndFactorsRepository.findByOrgIdAndCostItineraryAndItineraryStatus(
            any(), any(), any()))
        .thenReturn(Optional.of(testUtil.getCreatedCostItineraryAndFactorsEntity()));
    when(selectorAndCostItineraryMappingRepository.findByOrgIdAndCostTypeAndSelectorCf(
            anyString(), anyString(), anyString()))
        .thenReturn(List.of(testUtil.getSelectorAndCostItineraryMappingEntity()));
    when(optimizationAndCostTypesMappingRepository.findByOrgIdAndCostTypesContaining(
            anyString(), anyString()))
        .thenReturn(List.of(testUtil.getOptimizationAndCostTypesMappingEntity()));

    CostTypeResponse costTypeResponse = costTypeDashboardService.getCostTypes(TestUtil.ORG_ID);

    assertNotNull(costTypeResponse);
    assertEquals(USD, costTypeResponse.getCurrency());
    assertNotNull(costTypeResponse.getCostTypeList());

    List<CostTypeDtoInfo> costTypeList = costTypeResponse.getCostTypeList();
    assertEquals(2, costTypeList.size());
    assertEquals(TestUtil.COST_TYPE_SHIPPING_COST, costTypeList.get(0).getCostType());
    assertEquals(TestUtil.SELECTOR_CF, costTypeList.get(0).getSelectorCf());
    assertNotNull(costTypeList.get(0).getSelectorCfInfo());
    assertEquals(1, costTypeList.get(0).getSelectorCfInfo().size());

    assertEquals(TestUtil.NODE_PROCESSING_COST, costTypeList.get(1).getCostType());
    assertEquals(TestUtil.SELECTOR_CF, costTypeList.get(1).getSelectorCf());
    assertNotNull(costTypeList.get(1).getSelectorCfInfo());
    assertEquals(1, costTypeList.get(1).getSelectorCfInfo().size());

    verify(configurationFeign, times(1)).getTenantConfigdataByOrgIdAndConfigKey(any(), any());
    verify(tenantCostTypeRepository, times(1)).findByOrgId(any());
    verify(preferenceSelectorRepository, times(2)).findByOrgIdAndCostType(any(), any());
    verify(selectorAndCostItineraryMappingRepository, times(2))
        .findByOrgIdAndCostTypeAndSelectorCfAndSelectorCfValue(any(), any(), any(), any());
    verify(costItineraryAndFactorsRepository, times(2))
        .findByOrgIdAndCostItineraryAndItineraryStatus(any(), any(), any());
  }

  @Test
  @DisplayName("Get Cost Type: Happy Path when no selector CF is associated")
  void getCostTypeHappyPathWhenNoSelectorTest() {
    when(configurationFeign.getTenantConfigdataByOrgIdAndConfigKey(any(), any()))
        .thenReturn(getTenantConfigdataResponseBaseResponse());
    when(tenantCostTypeRepository.findByOrgId(any()))
        .thenReturn(List.of(testUtil.getTenantCostTypeEntity()));
    when(preferenceSelectorRepository.findByOrgIdAndCostType(any(), any())).thenReturn(List.of());

    when(selectorAndCostItineraryMappingRepository
            .findByOrgIdAndCostTypeAndSelectorCfAndSelectorCfValue(any(), any(), any(), any()))
        .thenReturn(List.of(testUtil.getSelectorAndCostItineraryMappingEntity()));
    when(costItineraryAndFactorsRepository.findByOrgIdAndCostItineraryAndItineraryStatus(
            any(), any(), any()))
        .thenReturn(Optional.of(testUtil.getCreatedCostItineraryAndFactorsEntity()));

    when(costFactorRepository.findByOrgIdAndCostFactor(any(), any()))
        .thenReturn(List.of(testUtil.getCostFactorEntity()));

    CostTypeResponse costTypeResponse = costTypeDashboardService.getCostTypes(TestUtil.ORG_ID);

    assertNotNull(costTypeResponse);
    assertEquals(USD, costTypeResponse.getCurrency());
    assertNotNull(costTypeResponse.getCostTypeList());

    List<CostTypeDtoInfo> costTypeList = costTypeResponse.getCostTypeList();
    assertEquals(1, costTypeList.size());
    assertEquals(TestUtil.COST_TYPE_SHIPPING_COST, costTypeList.get(0).getCostType());
    assertEquals("", costTypeList.get(0).getSelectorCf());
    assertEquals(0, costTypeList.get(0).getSelectorCfInfo().size());
    assertEquals(2, costTypeList.get(0).getCostFactors().size());
    assertNotNull(costTypeList.get(0).getRow());
    assertNotNull(costTypeList.get(0).getColumn());

    verify(configurationFeign, times(1)).getTenantConfigdataByOrgIdAndConfigKey(any(), any());
    verify(tenantCostTypeRepository, times(1)).findByOrgId(any());
    verify(preferenceSelectorRepository, times(1)).findByOrgIdAndCostType(any(), any());
    verify(selectorAndCostItineraryMappingRepository, times(1))
        .findByOrgIdAndCostTypeAndSelectorCfAndSelectorCfValue(any(), any(), any(), any());
    verify(costItineraryAndFactorsRepository, times(1))
        .findByOrgIdAndCostItineraryAndItineraryStatus(any(), any(), any());
  }

  @Test
  @DisplayName("Get Cost Types: When not cost type is there for organization")
  void getCostTypeWhenNoCurrencyAndNoCostTypeIsThereTest() {
    when(configurationFeign.getTenantConfigdataByOrgIdAndConfigKey(any(), any()))
        .thenReturn(new BaseResponse<TenantConfigdataResponse>());
    when(tenantCostTypeRepository.findByOrgId(any())).thenReturn(List.of());
    CostTypeResponse costTypeResponse = costTypeDashboardService.getCostTypes(TestUtil.ORG_ID);

    assertNotNull(costTypeResponse);
    assertEquals(USD, costTypeResponse.getCurrency());
    assertEquals(0, costTypeResponse.getCostTypeList().size());
  }

  @Test
  @DisplayName("Get Cost Type: When no cost factors configured for selector Cf")
  void getCostTypeWhenNoCostFactorsForSelectorCfTest() {
    when(configurationFeign.getTenantConfigdataByOrgIdAndConfigKey(any(), any()))
        .thenReturn(getTenantConfigdataResponseBaseResponse());
    when(tenantCostTypeRepository.findByOrgId(any()))
        .thenReturn(List.of(testUtil.getTenantCostTypeEntity()));
    when(preferenceSelectorRepository.findByOrgIdAndCostType(any(), any()))
        .thenReturn(List.of(testUtil.getPreferenceSelectorEntity()));
    when(costFactorRepository.findByOrgIdAndCostFactor(any(), any())).thenReturn(List.of());

    CostTypeResponse costTypeResponse = costTypeDashboardService.getCostTypes(TestUtil.ORG_ID);

    assertNotNull(costTypeResponse);
    assertEquals(USD, costTypeResponse.getCurrency());
    assertNotNull(costTypeResponse.getCostTypeList());

    List<CostTypeDtoInfo> costTypeList = costTypeResponse.getCostTypeList();
    assertEquals(1, costTypeList.size());
    assertEquals(TestUtil.COST_TYPE_SHIPPING_COST, costTypeList.get(0).getCostType());
    assertEquals(TestUtil.SELECTOR_CF, costTypeList.get(0).getSelectorCf());

    assertEquals(0, costTypeList.get(0).getSelectorCfInfo().size());
    assertNull(costTypeList.get(0).getRow());
    assertNull(costTypeList.get(0).getColumn());

    verify(configurationFeign, times(1)).getTenantConfigdataByOrgIdAndConfigKey(any(), any());
    verify(tenantCostTypeRepository, times(1)).findByOrgId(any());
    verify(preferenceSelectorRepository, times(1)).findByOrgIdAndCostType(any(), any());
    verify(selectorAndCostItineraryMappingRepository, times(0))
        .findByOrgIdAndCostTypeAndSelectorCfAndSelectorCfValue(any(), any(), any(), any());
    verify(costItineraryAndFactorsRepository, times(0))
        .findByOrgIdAndCostItineraryAndItineraryStatus(any(), any(), any());
  }

  @Test
  @DisplayName("Get Cost Type: When no itinerary is there for selectorCF")
  void getCostTypeWhenNoCostItineraryForSelectorCfTest() {
    when(configurationFeign.getTenantConfigdataByOrgIdAndConfigKey(any(), any()))
        .thenReturn(getTenantConfigdataResponseBaseResponse());
    when(tenantCostTypeRepository.findByOrgId(any()))
        .thenReturn(List.of(testUtil.getTenantCostTypeEntity()));
    when(preferenceSelectorRepository.findByOrgIdAndCostType(any(), any()))
        .thenReturn(List.of(testUtil.getPreferenceSelectorEntity()));

    when(selectorAndCostItineraryMappingRepository
            .findByOrgIdAndCostTypeAndSelectorCfAndSelectorCfValue(any(), any(), any(), any()))
        .thenReturn(List.of());
    when(costItineraryAndFactorsRepository.findByOrgIdAndCostItineraryAndItineraryStatus(
            any(), any(), any()))
        .thenReturn(Optional.of(testUtil.getCreatedCostItineraryAndFactorsEntity()));

    when(costFactorRepository.findByOrgIdAndCostFactor(any(), any()))
        .thenReturn(List.of(testUtil.getCostFactorEntityForSelectorCF()));

    when(selectorAndCostItineraryMappingRepository.findByOrgIdAndCostTypeAndSelectorCf(
            anyString(), anyString(), anyString()))
        .thenReturn(List.of(testUtil.getSelectorAndCostItineraryMappingEntity()));

    CostTypeResponse costTypeResponse = costTypeDashboardService.getCostTypes(TestUtil.ORG_ID);

    assertNotNull(costTypeResponse);
    assertEquals(USD, costTypeResponse.getCurrency());
    assertNotNull(costTypeResponse.getCostTypeList());

    List<CostTypeDtoInfo> costTypeList = costTypeResponse.getCostTypeList();
    assertEquals(1, costTypeList.size());
    assertEquals(TestUtil.COST_TYPE_SHIPPING_COST, costTypeList.get(0).getCostType());
    assertEquals(TestUtil.SELECTOR_CF, costTypeList.get(0).getSelectorCf());
    assertEquals(1, costTypeList.get(0).getSelectorCfInfo().size());
    assertNull(costTypeList.get(0).getRow());
    assertNull(costTypeList.get(0).getColumn());

    verify(configurationFeign, times(1)).getTenantConfigdataByOrgIdAndConfigKey(any(), any());
    verify(tenantCostTypeRepository, times(1)).findByOrgId(any());
    verify(preferenceSelectorRepository, times(1)).findByOrgIdAndCostType(any(), any());
    verify(selectorAndCostItineraryMappingRepository, times(1))
        .findByOrgIdAndCostTypeAndSelectorCfAndSelectorCfValue(any(), any(), any(), any());
    verify(costItineraryAndFactorsRepository, times(0))
        .findByOrgIdAndCostItineraryAndItineraryStatus(any(), any(), any());
  }

  @Test
  @DisplayName("Get Cost Type: When no CREATED itinerary is there for selectorCF")
  void getCostTypeWhenNoCreatedCostItineraryForSelectorCfTest() {
    when(configurationFeign.getTenantConfigdataByOrgIdAndConfigKey(any(), any()))
        .thenReturn(getTenantConfigdataResponseBaseResponse());
    when(tenantCostTypeRepository.findByOrgId(any()))
        .thenReturn(List.of(testUtil.getTenantCostTypeEntity()));
    when(preferenceSelectorRepository.findByOrgIdAndCostType(any(), any()))
        .thenReturn(List.of(testUtil.getPreferenceSelectorEntity()));

    when(selectorAndCostItineraryMappingRepository
            .findByOrgIdAndCostTypeAndSelectorCfAndSelectorCfValue(any(), any(), any(), any()))
        .thenReturn(List.of(testUtil.getSelectorAndCostItineraryMappingEntity()));
    when(costItineraryAndFactorsRepository.findByOrgIdAndCostItineraryAndItineraryStatus(
            any(), any(), any()))
        .thenReturn(Optional.empty());

    when(costFactorRepository.findByOrgIdAndCostFactor(any(), any()))
        .thenReturn(List.of(testUtil.getCostFactorEntityForSelectorCF()));

    when(selectorAndCostItineraryMappingRepository.findByOrgIdAndCostTypeAndSelectorCf(
            anyString(), anyString(), anyString()))
        .thenReturn(List.of(testUtil.getSelectorAndCostItineraryMappingEntity()));

    CostTypeResponse costTypeResponse = costTypeDashboardService.getCostTypes(TestUtil.ORG_ID);

    assertNotNull(costTypeResponse);
    assertEquals(USD, costTypeResponse.getCurrency());
    assertNotNull(costTypeResponse.getCostTypeList());

    List<CostTypeDtoInfo> costTypeList = costTypeResponse.getCostTypeList();
    assertEquals(1, costTypeList.size());
    assertEquals(TestUtil.COST_TYPE_SHIPPING_COST, costTypeList.get(0).getCostType());
    assertEquals(TestUtil.SELECTOR_CF, costTypeList.get(0).getSelectorCf());
    assertEquals(1, costTypeList.get(0).getSelectorCfInfo().size());
    assertNull(costTypeList.get(0).getRow());
    assertNull(costTypeList.get(0).getColumn());

    verify(configurationFeign, times(1)).getTenantConfigdataByOrgIdAndConfigKey(any(), any());
    verify(tenantCostTypeRepository, times(1)).findByOrgId(any());
    verify(preferenceSelectorRepository, times(1)).findByOrgIdAndCostType(any(), any());
    verify(selectorAndCostItineraryMappingRepository, times(1))
        .findByOrgIdAndCostTypeAndSelectorCfAndSelectorCfValue(any(), any(), any(), any());
    verify(costItineraryAndFactorsRepository, times(1))
        .findByOrgIdAndCostItineraryAndItineraryStatus(any(), any(), any());
  }

  @Test
  @DisplayName("Get Cost Type: Happy Path - Cost Type's Itinerary Without Rate Card Lookup")
  void getCostTypeWithItinerary_costTypeWithoutRateCardLookupTest() {
    var tenantCostTypeEntity = testUtil.getTenantCostTypeEntity();
    tenantCostTypeEntity.setCostType(BUYING_COST);
    tenantCostTypeEntity.setDisplayName(DISPLAY_NAME_BUYING_COST);
    var selectorAndCostItineraryMappingEntity = testUtil.getSelectorAndCostItineraryMappingEntity();
    selectorAndCostItineraryMappingEntity.setSelectorCf(null);
    selectorAndCostItineraryMappingEntity.setSelectorCfValue(null);
    var costFactor1 = testUtil.getCostFactorEntity();
    costFactor1.setCostFactor("zone");
    costFactor1.setIsRateCardLookUpRequired(false);

    when(configurationFeign.getTenantConfigdataByOrgIdAndConfigKey(any(), any()))
        .thenReturn(getTenantConfigdataResponseBaseResponse());
    when(tenantCostTypeRepository.findByOrgId(any())).thenReturn(List.of(tenantCostTypeEntity));
    when(preferenceSelectorRepository.findByOrgIdAndCostType(any(), any())).thenReturn(List.of());
    when(costFactorRepository.findByOrgIdAndCostFactor(any(), any()))
        .thenReturn(List.of(costFactor1));
    when(selectorAndCostItineraryMappingRepository
            .findByOrgIdAndCostTypeAndSelectorCfAndSelectorCfValue(any(), any(), any(), any()))
        .thenReturn(List.of(selectorAndCostItineraryMappingEntity));
    when(costItineraryAndFactorsRepository.findByOrgIdAndCostItineraryAndItineraryStatus(
            any(), any(), any()))
        .thenReturn(Optional.of(testUtil.getCreatedCostItineraryAndFactorsEntity()));
    when(selectorAndCostItineraryMappingRepository.findByOrgIdAndCostTypeAndSelectorCf(
            anyString(), anyString(), anyString()))
        .thenReturn(List.of(selectorAndCostItineraryMappingEntity));
    when(costFactorRepository.findByOrgIdAndCostFactorIn(anyString(), anyList()))
        .thenReturn(List.of(costFactor1));

    CostTypeResponse costTypeResponse = costTypeDashboardService.getCostTypes(TestUtil.ORG_ID);

    assertNotNull(costTypeResponse);
    assertEquals(USD, costTypeResponse.getCurrency());
    assertNotNull(costTypeResponse.getCostTypeList());

    var costTypeList = costTypeResponse.getCostTypeList();
    assertEquals(0, costTypeList.size());

    verify(configurationFeign, times(1)).getTenantConfigdataByOrgIdAndConfigKey(any(), any());
    verify(tenantCostTypeRepository, times(1)).findByOrgId(any());
    verify(preferenceSelectorRepository, times(1)).findByOrgIdAndCostType(any(), any());
    verify(selectorAndCostItineraryMappingRepository, times(1))
        .findByOrgIdAndCostTypeAndSelectorCfAndSelectorCfValue(any(), any(), any(), any());
    verify(costItineraryAndFactorsRepository, times(1))
        .findByOrgIdAndCostItineraryAndItineraryStatus(any(), any(), any());
  }

  private List<TenantCostTypeEntity> getTenantCostTypeEntities() {
    TenantCostTypeEntity entity1 = testUtil.getTenantCostTypeEntity();
    TenantCostTypeEntity entity2 = testUtil.getTenantCostTypeEntity();
    entity2.setCostType(TestUtil.NODE_PROCESSING_COST);
    entity2.setDisplayName(TestUtil.DISPLAY_NAME_NODE_PROCESSING_COST1);
    return List.of(entity1, entity2);
  }

  private BaseResponse<TenantConfigdataResponse> getTenantConfigdataResponseBaseResponse() {
    return BaseResponse.builder()
        .payload(
            TenantConfigdataResponse.builder()
                .orgId(TestUtil.ORG_ID)
                .configKey("currency")
                .configValue("USD"))
        .build();
  }

  @Test
  @DisplayName("Update Rate Card Status - Happy Path")
  void updateRateCardStatusTest() throws CommonServiceException {
    UpdateRateCardStatusRequest updateRateCardStatusRequest =
        testUtil.getUpdateRateCardStatusRequest();

    when(tenantCostTypeRepository.findByOrgIdAndCostType(anyString(), anyString()))
        .thenReturn(Optional.of(testUtil.getTenantCostTypeEntity()));
    when(preferenceSelectorRepository.findByOrgIdAndCostType(anyString(), anyString()))
        .thenReturn(List.of(testUtil.getPreferenceSelectorEntity()));
    when(selectorAndCostItineraryMappingRepository
            .findFirstByOrgIdAndCostTypeAndSelectorCfAndSelectorCfValue(any(), any(), any(), any()))
        .thenReturn(
            Optional.of(testUtil.getSelectorAndCostItineraryEntityToUpdateRateCardStatus()));
    when(costItineraryAndFactorsRepository.findByOrgIdAndCostItinerary(any(), any()))
        .thenReturn(Optional.of(testUtil.getCostItineraryAndFactorsEntity()));

    UpdateRateCardStatusResponse updateRateCardStatusResponse =
        costTypeDashboardService.updateRateCardStatus(ORG_ID, updateRateCardStatusRequest);
    assertEquals(COST_TYPE, updateRateCardStatusResponse.getCostType());
    assertEquals(RATE_CARD_ACTIVE_TRUE, updateRateCardStatusResponse.getIsRateCardActive());

    verify(tenantCostTypeRepository, times(1)).findByOrgIdAndCostType(anyString(), anyString());
    verify(preferenceSelectorRepository, times(1)).findByOrgIdAndCostType(anyString(), anyString());
    verify(selectorAndCostItineraryMappingRepository, times(1))
        .findFirstByOrgIdAndCostTypeAndSelectorCfAndSelectorCfValue(any(), any(), any(), any());
    verify(costItineraryAndFactorsRepository, times(1)).findByOrgIdAndCostItinerary(any(), any());
  }

  @Test
  @DisplayName("Update Rate Card Status - Invalid cost type")
  void updateRateCardStatusInvalidCostTypeTest() {
    UpdateRateCardStatusRequest updateRateCardStatusRequest =
        testUtil.getUpdateRateCardStatusRequest();

    when(tenantCostTypeRepository.findByOrgIdAndCostType(anyString(), anyString()))
        .thenReturn(Optional.empty());

    Exception ex =
        assertThrows(
            CommonServiceException.class,
            () ->
                costTypeDashboardService.updateRateCardStatus(ORG_ID, updateRateCardStatusRequest));
    assertNotNull(ex);
    assertEquals("Invalid cost type for given orgId", ex.getMessage());

    verify(tenantCostTypeRepository, times(1)).findByOrgIdAndCostType(anyString(), anyString());
  }

  @Test
  @DisplayName("Should throw exception when selector is null and is rate card active is false")
  void updateRateCardStatusForEmptySelectorIsNullAndActiveStatusIsFalseTest() {
    UpdateRateCardStatusRequest updateRateCardStatusRequest =
        testUtil.getUpdateRateCardStatusRequestWithNullSelectorFalse();

    when(tenantCostTypeRepository.findByOrgIdAndCostType(anyString(), anyString()))
        .thenReturn(Optional.of(testUtil.getTenantCostTypeEntity()));
    when(preferenceSelectorRepository.findByOrgIdAndCostType(anyString(), anyString()))
        .thenReturn(List.of(testUtil.getPreferenceSelectorEntity()));

    Exception ex =
        assertThrows(
            CommonServiceException.class,
            () ->
                costTypeDashboardService.updateRateCardStatus(ORG_ID, updateRateCardStatusRequest));
    assertNotNull(ex);
    assertEquals("Invalid selector provided in the request", ex.getMessage());

    verify(tenantCostTypeRepository, times(1)).findByOrgIdAndCostType(anyString(), anyString());
    verify(preferenceSelectorRepository, times(1)).findByOrgIdAndCostType(anyString(), anyString());
    verify(selectorAndCostItineraryMappingRepository, times(0))
        .findFirstByOrgIdAndCostType(any(), any());
    verify(costItineraryAndFactorsRepository, times(0)).findByOrgIdAndCostItinerary(any(), any());
  }

  @Test
  @DisplayName("Update rate card - inactivate default itinerary")
  void updateRateCardStatusInactivateDefaultItineraryTest() {
    UpdateRateCardStatusRequest updateRateCardStatusRequest =
        testUtil.getUpdateRateCardStatusRequestForDefaultItinerary();

    when(tenantCostTypeRepository.findByOrgIdAndCostType(anyString(), anyString()))
        .thenReturn(Optional.of(testUtil.getTenantCostTypeEntity()));
    when(preferenceSelectorRepository.findByOrgIdAndCostType(anyString(), anyString()))
        .thenReturn(List.of(testUtil.getPreferenceSelectorEntity()));

    Exception ex =
        assertThrows(
            CommonServiceException.class,
            () ->
                costTypeDashboardService.updateRateCardStatus(ORG_ID, updateRateCardStatusRequest));
    assertNotNull(ex);
    assertEquals("Default itinerary cannot be deactivated", ex.getMessage());

    verify(tenantCostTypeRepository, times(1)).findByOrgIdAndCostType(anyString(), anyString());
    verify(preferenceSelectorRepository, times(1)).findByOrgIdAndCostType(anyString(), anyString());
    verify(selectorAndCostItineraryMappingRepository, times(0))
        .findFirstByOrgIdAndCostType(any(), any());
    verify(costItineraryAndFactorsRepository, times(0)).findByOrgIdAndCostItinerary(any(), any());
  }

  @Test
  @DisplayName("Update rate card - cost itinerary and factors not found")
  void updateRateCardStatusWhenCostItineraryAndFactorsNotFoundTest() {
    UpdateRateCardStatusRequest updateRateCardStatusRequest =
        testUtil.getUpdateRateCardStatusRequest();

    when(tenantCostTypeRepository.findByOrgIdAndCostType(anyString(), anyString()))
        .thenReturn(Optional.of(testUtil.getTenantCostTypeEntity()));
    when(preferenceSelectorRepository.findByOrgIdAndCostType(anyString(), anyString()))
        .thenReturn(List.of(testUtil.getPreferenceSelectorEntity()));
    when(selectorAndCostItineraryMappingRepository
            .findFirstByOrgIdAndCostTypeAndSelectorCfAndSelectorCfValue(any(), any(), any(), any()))
        .thenReturn(
            Optional.of(testUtil.getSelectorAndCostItineraryEntityToUpdateRateCardStatus()));
    when(costFactorRepository.findFirstByOrgIdAndCostFactor(any(), any()))
        .thenReturn(Optional.of(testUtil.getCostFactorEntityForRateCardStatus()));
    when(costItineraryAndFactorsRepository.findByOrgIdAndCostItinerary(any(), any()))
        .thenReturn(Optional.empty());

    Exception ex =
        assertThrows(
            CommonServiceException.class,
            () ->
                costTypeDashboardService.updateRateCardStatus(ORG_ID, updateRateCardStatusRequest));

    assertNotNull(ex);
    assertEquals("Cost Itinerary And Cost Factors Mapping not found", ex.getMessage());

    verify(tenantCostTypeRepository, times(1)).findByOrgIdAndCostType(anyString(), anyString());
    verify(preferenceSelectorRepository, times(1)).findByOrgIdAndCostType(anyString(), anyString());
    verify(selectorAndCostItineraryMappingRepository, times(1))
        .findFirstByOrgIdAndCostTypeAndSelectorCfAndSelectorCfValue(any(), any(), any(), any());
    verify(costItineraryAndFactorsRepository, times(1)).findByOrgIdAndCostItinerary(any(), any());
  }

  @Test
  @DisplayName("When selector for cost type and in request is null and is rate card active is true")
  void updateRateCardStatusForEmptySelectorIsNullTest() throws CommonServiceException {
    UpdateRateCardStatusRequest updateRateCardStatusRequest =
        testUtil.getUpdateRateCardStatusRequestWithNullSelector();

    when(tenantCostTypeRepository.findByOrgIdAndCostType(anyString(), anyString()))
        .thenReturn(Optional.of(testUtil.getTenantCostTypeEntity()));
    when(preferenceSelectorRepository.findByOrgIdAndCostType(anyString(), anyString()))
        .thenReturn(List.of());
    when(selectorAndCostItineraryMappingRepository.findFirstByOrgIdAndCostType(any(), any()))
        .thenReturn(
            Optional.of(testUtil.getSelectorAndCostItineraryEntityToUpdateRateCardStatus()));
    when(costItineraryAndFactorsRepository.findByOrgIdAndCostItinerary(any(), any()))
        .thenReturn(Optional.of(testUtil.getCostItineraryAndFactorsEntity()));

    UpdateRateCardStatusResponse updateRateCardStatusResponse =
        costTypeDashboardService.updateRateCardStatus(ORG_ID, updateRateCardStatusRequest);
    assertEquals(COST_TYPE, updateRateCardStatusResponse.getCostType());
    assertEquals(RATE_CARD_ACTIVE_TRUE, updateRateCardStatusResponse.getIsRateCardActive());

    verify(tenantCostTypeRepository, times(1)).findByOrgIdAndCostType(anyString(), anyString());
    verify(preferenceSelectorRepository, times(1)).findByOrgIdAndCostType(anyString(), anyString());
    verify(selectorAndCostItineraryMappingRepository, times(1))
        .findFirstByOrgIdAndCostType(any(), any());
    verify(costItineraryAndFactorsRepository, times(1)).findByOrgIdAndCostItinerary(any(), any());
  }

  @Test
  @DisplayName("Update rate card - selector and cost itinerary mapping not found")
  void updateRateCardStatusWhenSelectorAndCostItineraryNotFoundTest() {
    UpdateRateCardStatusRequest updateRateCardStatusRequest =
        testUtil.getUpdateRateCardStatusRequest();

    when(tenantCostTypeRepository.findByOrgIdAndCostType(anyString(), anyString()))
        .thenReturn(Optional.of(testUtil.getTenantCostTypeEntity()));
    when(preferenceSelectorRepository.findByOrgIdAndCostType(anyString(), anyString()))
        .thenReturn(List.of(testUtil.getPreferenceSelectorEntity()));

    when(selectorAndCostItineraryMappingRepository
            .findFirstByOrgIdAndCostTypeAndSelectorCfAndSelectorCfValue(any(), any(), any(), any()))
        .thenReturn(Optional.empty());

    Exception ex =
        assertThrows(
            CommonServiceException.class,
            () ->
                costTypeDashboardService.updateRateCardStatus(ORG_ID, updateRateCardStatusRequest));

    assertNotNull(ex);
    assertEquals("Selector Itinerary Mapping not found with given details", ex.getMessage());

    verify(tenantCostTypeRepository, times(1)).findByOrgIdAndCostType(anyString(), anyString());
    verify(preferenceSelectorRepository, times(1)).findByOrgIdAndCostType(anyString(), anyString());
    verify(selectorAndCostItineraryMappingRepository, times(1))
        .findFirstByOrgIdAndCostTypeAndSelectorCfAndSelectorCfValue(any(), any(), any(), any());
  }

  @Test
  @DisplayName("Update rate card - selector and selector cf value is null and isActive is false")
  void updateRateCardStatusWhenSelectorCfAndSelectorCfValueIsNullTest() {
    UpdateRateCardStatusRequest updateRateCardStatusRequest =
        testUtil.getUpdateRateCardStatusSelectorNullValuesRequest();
    updateRateCardStatusRequest.setIsRateCardActive(false);

    when(tenantCostTypeRepository.findByOrgIdAndCostType(anyString(), anyString()))
        .thenReturn(Optional.of(testUtil.getTenantCostTypeEntity()));
    when(preferenceSelectorRepository.findByOrgIdAndCostType(anyString(), anyString()))
        .thenReturn(List.of());

    Exception ex =
        assertThrows(
            CommonServiceException.class,
            () ->
                costTypeDashboardService.updateRateCardStatus(ORG_ID, updateRateCardStatusRequest));

    assertNotNull(ex);
    assertEquals(ONLY_ITINERARY_DEACTIVATE_EXCEPTION_MESSAGE, ex.getMessage());

    verify(tenantCostTypeRepository, times(1)).findByOrgIdAndCostType(anyString(), anyString());
    verify(preferenceSelectorRepository, times(1)).findByOrgIdAndCostType(anyString(), anyString());
    verify(selectorAndCostItineraryMappingRepository, times(0))
        .findFirstByOrgIdAndCostTypeAndSelectorCfAndSelectorCfValue(any(), any(), any(), any());
  }

  @Test
  @DisplayName("Update rate card - selector is null")
  void updateRateCardStatusWhenSelectorCfIsNullTest() {
    UpdateRateCardStatusRequest updateRateCardStatusRequest =
        testUtil.getUpdateRateCardStatusSelectorCfIsnullRequest();

    when(tenantCostTypeRepository.findByOrgIdAndCostType(anyString(), anyString()))
        .thenReturn(Optional.of(testUtil.getTenantCostTypeEntity()));
    when(preferenceSelectorRepository.findByOrgIdAndCostType(anyString(), anyString()))
        .thenReturn(List.of());

    Exception ex =
        assertThrows(
            CommonServiceException.class,
            () ->
                costTypeDashboardService.updateRateCardStatus(ORG_ID, updateRateCardStatusRequest));

    assertNotNull(ex);
    assertEquals("When selectorCf is null, selectorCfValue must also be null", ex.getMessage());

    verify(tenantCostTypeRepository, times(1)).findByOrgIdAndCostType(anyString(), anyString());
    verify(preferenceSelectorRepository, times(1)).findByOrgIdAndCostType(anyString(), anyString());
    verify(costFactorRepository, times(0)).findFirstByOrgIdAndCostFactor(any(), any());
    verify(selectorAndCostItineraryMappingRepository, times(0))
        .findFirstByOrgIdAndCostType(any(), any());
    verify(costItineraryAndFactorsRepository, times(0)).findByOrgIdAndCostItinerary(any(), any());
  }

  @Test
  @DisplayName("Get Cost Type details for validation: Happy Path")
  void getCostTypeDetailsForValidation() throws CommonServiceException {
    CostTypeResponse costTypeResponse = testUtil.getCostTypeResponseWithSelector(COST_TYPE);
    when(costTypeDashboardService.getCostTypes(any())).thenReturn(costTypeResponse);
    when(costFactorRepository.findByOrgIdAndCostFactor(any(), any()))
        .thenReturn(List.of(testUtil.getCostFactorEntity()));
    when(selectorAndCostItineraryMappingRepository
            .findByOrgIdAndCostTypeAndSelectorCfAndSelectorCfValue(any(), any(), any(), any()))
        .thenReturn(List.of(testUtil.getSelectorAndCostItineraryMappingEntity()));
    when(costItineraryAndFactorsRepository.findByOrgIdAndCostItineraryAndItineraryStatus(
            any(), any(), any()))
        .thenReturn(Optional.of(testUtil.getCreatedCostItineraryAndFactorsEntity()));
    CostTypeValidationResponse costTypeValidationResponse =
        costTypeDashboardService.getCostTypeDetailsForValidation(ORG_ID, COST_TYPE);
    assertNotNull(costTypeValidationResponse);
    CostTypeDtoInfo costTypeDtoInfo = costTypeResponse.getCostTypeList().get(0);
    assertEquals(costTypeResponse.getCurrency(), costTypeValidationResponse.getCurrency());
    assertEquals(costTypeDtoInfo.getCostType(), costTypeValidationResponse.getCostType());
    assertEquals(costTypeDtoInfo.getSelectorCf(), costTypeValidationResponse.getSelectorCf());
    assertEquals("", costTypeValidationResponse.getCostItinerary());
    assertEquals(
        costTypeDtoInfo.getSelectorCfInfo().size(),
        costTypeValidationResponse.getSelectorCfInfo().size());
    assertNotNull(costTypeValidationResponse.getSelectorCfInfo().get(0).getRow());
    assertNotNull(costTypeValidationResponse.getSelectorCfInfo().get(0).getColumn());
    assertEquals(
        testUtil.getCostItineraryAndFactorsEntity().getCostItinerary(),
        costTypeValidationResponse.getSelectorCfInfo().get(0).getCostItinerary());
    verify(costTypeDashboardService, times(1)).getCostTypes(any());
    verify(costItineraryAndFactorsRepository, times(1))
        .findByOrgIdAndCostItineraryAndItineraryStatus(any(), any(), any());
  }

  @Test
  @DisplayName(
      "Get Cost Type details for validation: Happy Path with selector but no selectorCf Value")
  void getCostTypeDetailsForValidationWithSelectorButNoSelectorCfValue()
      throws CommonServiceException {
    CostTypeResponse costTypeResponse =
        testUtil.getCostTypeResponseWithSelectorButNoSelectorCfValue(COST_TYPE);
    SelectorAndCostItineraryMappingEntity selectorAndCostItineraryMappingEntity =
        testUtil.getSelectorAndCostItineraryMappingEntity();
    selectorAndCostItineraryMappingEntity.setSelectorCfValue(null);
    selectorAndCostItineraryMappingEntity.setCostItinerary("SHIPPING_COST_DEFAULT");
    CostItineraryAndFactorsEntity costItineraryAndFactorsEntity =
        testUtil.getCreatedCostItineraryAndFactorsEntity();
    costItineraryAndFactorsEntity.setCostItinerary("SHIPPING_COST_DEFAULT");

    when(costTypeDashboardService.getCostTypes(any())).thenReturn(costTypeResponse);
    when(costFactorRepository.findByOrgIdAndCostFactor(any(), any()))
        .thenReturn(List.of(testUtil.getCostFactorEntity()));
    when(selectorAndCostItineraryMappingRepository
            .findByOrgIdAndCostTypeAndSelectorCfAndSelectorCfValue(any(), any(), any(), any()))
        .thenReturn(List.of(selectorAndCostItineraryMappingEntity));
    when(costItineraryAndFactorsRepository.findByOrgIdAndCostItineraryAndItineraryStatus(
            any(), any(), any()))
        .thenReturn(Optional.of(costItineraryAndFactorsEntity));
    CostTypeValidationResponse costTypeValidationResponse =
        costTypeDashboardService.getCostTypeDetailsForValidation(ORG_ID, COST_TYPE);
    assertNotNull(costTypeValidationResponse);
    CostTypeDtoInfo costTypeDtoInfo = costTypeResponse.getCostTypeList().get(0);
    assertEquals(costTypeResponse.getCurrency(), costTypeValidationResponse.getCurrency());
    assertEquals(costTypeDtoInfo.getCostType(), costTypeValidationResponse.getCostType());
    assertEquals(costTypeDtoInfo.getSelectorCf(), costTypeValidationResponse.getSelectorCf());
    assertEquals("", costTypeValidationResponse.getCostItinerary());
    assertEquals(
        costTypeDtoInfo.getSelectorCfInfo().size(),
        costTypeValidationResponse.getSelectorCfInfo().size());
    assertNotNull(costTypeValidationResponse.getSelectorCfInfo().get(0).getRow());
    assertNotNull(costTypeValidationResponse.getSelectorCfInfo().get(0).getColumn());
    assertEquals(
        "SHIPPING_COST_DEFAULT",
        costTypeValidationResponse.getSelectorCfInfo().get(0).getCostItinerary());
    verify(costTypeDashboardService, times(1)).getCostTypes(any());
    verify(costItineraryAndFactorsRepository, times(1))
        .findByOrgIdAndCostItineraryAndItineraryStatus(any(), any(), any());
  }

  @Test
  @DisplayName("Get Cost Type details for validation: Happy Path without selector")
  void getCostTypeDetailsForValidationWithoutSelector() throws CommonServiceException {
    CostTypeResponse costTypeResponse = testUtil.getCostTypeResponseWithoutSelector(COST_TYPE);
    when(costTypeDashboardService.getCostTypes(any())).thenReturn(costTypeResponse);
    when(costFactorRepository.findByOrgIdAndCostFactor(any(), any()))
        .thenReturn(List.of(testUtil.getCostFactorEntity()));
    when(selectorAndCostItineraryMappingRepository
            .findByOrgIdAndCostTypeAndSelectorCfAndSelectorCfValue(any(), any(), any(), any()))
        .thenReturn(List.of(testUtil.getSelectorAndCostItineraryMappingEntity()));
    when(costItineraryAndFactorsRepository.findByOrgIdAndCostItineraryAndItineraryStatus(
            any(), any(), any()))
        .thenReturn(Optional.of(testUtil.getCostItineraryAndFactorsEntity()));
    CostTypeValidationResponse costTypeValidationResponse =
        costTypeDashboardService.getCostTypeDetailsForValidation(ORG_ID, COST_TYPE);
    assertNotNull(costTypeValidationResponse);
    CostTypeDtoInfo costTypeDtoInfo = costTypeResponse.getCostTypeList().get(0);
    assertEquals(costTypeResponse.getCurrency(), costTypeValidationResponse.getCurrency());
    assertEquals(costTypeDtoInfo.getCostType(), costTypeValidationResponse.getCostType());
    assertEquals(costTypeDtoInfo.getSelectorCf(), costTypeValidationResponse.getSelectorCf());
    assertEquals(0, costTypeValidationResponse.getSelectorCfInfo().size());
    assertEquals(
        testUtil.getCostItineraryAndFactorsEntity().getCostItinerary(),
        costTypeValidationResponse.getCostItinerary());
    assertNotNull(costTypeValidationResponse.getRow());
    assertNotNull(costTypeValidationResponse.getColumn());
    assertFalse(costTypeValidationResponse.getCostFactors().isEmpty());
    verify(costTypeDashboardService, times(1)).getCostTypes(any());
    verify(costItineraryAndFactorsRepository, times(1))
        .findByOrgIdAndCostItineraryAndItineraryStatus(any(), any(), any());
  }

  @Test
  @DisplayName("Get Cost Type details for validation: Cost type not found")
  void getCostTypeDetailsWhenCostTypeNotFound() {
    CostTypeResponse costTypeResponse = testUtil.getCostTypeResponseNotFound();
    when(costTypeDashboardService.getCostTypes(any())).thenReturn(costTypeResponse);
    CommonServiceException ex =
        assertThrows(
            CommonServiceException.class,
            () -> {
              costTypeDashboardService.getCostTypeDetailsForValidation(ORG_ID, COST_TYPE);
            });
    assertEquals("Tenant cost type not found for the orgId", ex.getMessage());
    assertEquals(HttpStatus.NOT_FOUND, ex.getHttpStatus());
  }
}
