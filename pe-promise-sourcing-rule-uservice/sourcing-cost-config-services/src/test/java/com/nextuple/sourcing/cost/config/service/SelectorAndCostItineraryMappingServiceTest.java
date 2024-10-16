/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */
package com.nextuple.sourcing.cost.config.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.sourcing.cost.config.domain.entity.SelectorAndCostItineraryMappingEntity;
import com.nextuple.sourcing.cost.config.domain.repository.CostFactorRepository;
import com.nextuple.sourcing.cost.config.domain.repository.CostItineraryAndFactorsRepository;
import com.nextuple.sourcing.cost.config.domain.repository.PreferenceSelectorRepository;
import com.nextuple.sourcing.cost.config.domain.repository.SelectorAndCostItineraryMappingRepository;
import com.nextuple.sourcing.cost.config.domain.repository.TenantCostTypeRepository;
import com.nextuple.sourcing.cost.config.enums.ItineraryStatusEnum;
import com.nextuple.sourcing.cost.config.outbound.SelectorAndCostItineraryMappingResponse;
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

@DisplayName("SelectorAndCostItineraryMappingService Test Cases")
class SelectorAndCostItineraryMappingServiceTest {

  @Mock private SelectorAndCostItineraryMappingRepository selectorAndCostItineraryMappingRepository;

  @Mock private CostItineraryAndFactorsRepository costItineraryAndFactorsRepository;

  @Mock private TenantCostTypeRepository tenantCostTypeRepository;

  @Mock private PreferenceSelectorRepository preferenceSelectorRepository;

  @Mock private CostFactorRepository costFactorRepository;

  @InjectMocks
  private SelectorAndCostItineraryMappingService selectorAndCostItineraryMappingService;

  @InjectMocks private TestUtil testUtil;

  private static final String ORG_ID = "orgId";
  private static final String SELECTOR_CF = "selectorCf";
  private static final String COST_TYPE = "costType";
  private static final String ID = "id";

  private static final String SELECTOR_ITINERARY_NOT_FOUND_EXCEPTION =
      "Selector Itinerary Mapping not found with given details";

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  @DisplayName("Test creating a Selector And Cost Itinerary Mapping for valid scenario")
  void createSelectorAndCostItineraryMappingValidScenario() throws CommonServiceException {
    var costItineraryAndFactorsEntity = testUtil.getCostItineraryAndFactorsEntity();
    costItineraryAndFactorsEntity.setCostFactors(TestUtil.SELECTOR_CF);
    costItineraryAndFactorsEntity.setItineraryStatus(ItineraryStatusEnum.CREATED);
    var costFactorEntity = testUtil.getCostFactorEntity();
    costFactorEntity.setCostFactor(TestUtil.SELECTOR_CF);
    costFactorEntity.setValues(TestUtil.SELECTOR_CF_VALUE);

    when(selectorAndCostItineraryMappingRepository.save(any()))
        .thenReturn(testUtil.getSelectorAndCostItineraryMappingEntity());
    when(preferenceSelectorRepository.findByOrgIdAndSelectorCf(anyString(), anyString()))
        .thenReturn(List.of(testUtil.getPreferenceSelectorEntity()));
    when(costItineraryAndFactorsRepository.findByOrgIdAndCostItinerary(anyString(), anyString()))
        .thenReturn(Optional.of(costItineraryAndFactorsEntity));
    when(tenantCostTypeRepository.findByOrgIdAndCostType(anyString(), anyString()))
        .thenReturn(Optional.of(testUtil.getTenantCostTypeEntity()));
    when(costFactorRepository.findByOrgIdAndCostFactor(anyString(), anyString()))
        .thenReturn(List.of(costFactorEntity));
    when(preferenceSelectorRepository.findFirstByOrgIdAndCostType(anyString(), anyString()))
        .thenReturn(Optional.of(testUtil.getPreferenceSelectorEntity()));
    var response =
        selectorAndCostItineraryMappingService.createSelectorAndCostItineraryMapping(
            TestUtil.ORG_ID, testUtil.getSelectorAndCostItineraryMappingRequest());
    assertNotNull(response);
    assertEquals(TestUtil.ID, response.getId());
    assertEquals(TestUtil.ORG_ID, response.getOrgId());
  }

  @Test
  @DisplayName("Creating Selector And Cost Itinerary Mapping - with null selector cf value")
  void createSelectorAndCostItineraryMappingWhenSelectorCfValueIsNull()
      throws CommonServiceException {
    var costItineraryAndFactorsEntity = testUtil.getCostItineraryAndFactorsEntity();
    costItineraryAndFactorsEntity.setCostFactors(TestUtil.SELECTOR_CF);
    costItineraryAndFactorsEntity.setItineraryStatus(ItineraryStatusEnum.CREATED);

    when(selectorAndCostItineraryMappingRepository.save(any()))
        .thenReturn(testUtil.getSelectorAndCostItineraryMappingEntity());
    when(preferenceSelectorRepository.findByOrgIdAndSelectorCf(anyString(), anyString()))
        .thenReturn(List.of(testUtil.getPreferenceSelectorEntity()));
    when(costItineraryAndFactorsRepository.findByOrgIdAndCostItinerary(anyString(), anyString()))
        .thenReturn(Optional.of(costItineraryAndFactorsEntity));
    when(tenantCostTypeRepository.findByOrgIdAndCostType(anyString(), anyString()))
        .thenReturn(Optional.of(testUtil.getTenantCostTypeEntity()));
    when(preferenceSelectorRepository.findFirstByOrgIdAndCostType(anyString(), anyString()))
        .thenReturn(Optional.of(testUtil.getPreferenceSelectorEntity()));
    var response =
        selectorAndCostItineraryMappingService.createSelectorAndCostItineraryMapping(
            TestUtil.ORG_ID,
            testUtil.getSelectorAndCostItineraryMappingRequestWithNullSelectorCfValue());
    assertNotNull(response);
    assertEquals(TestUtil.ID, response.getId());
    assertEquals(TestUtil.ORG_ID, response.getOrgId());
  }

  @Test
  @DisplayName("Creating a Selector And Cost Itinerary Mapping - Itinerary without selectorCf")
  void createSelectorAndCostItineraryMappingInvalidScenarioItineraryWithoutSelectorCf() {
    when(preferenceSelectorRepository.findByOrgIdAndSelectorCf(anyString(), anyString()))
        .thenReturn(List.of(testUtil.getPreferenceSelectorEntity()));
    when(costItineraryAndFactorsRepository.findByOrgIdAndCostItinerary(anyString(), anyString()))
        .thenReturn(Optional.ofNullable(testUtil.getCostItineraryAndFactorsEntity()));
    when(tenantCostTypeRepository.findByOrgIdAndCostType(anyString(), anyString()))
        .thenReturn(Optional.of(testUtil.getTenantCostTypeEntity()));
    when(preferenceSelectorRepository.findFirstByOrgIdAndCostType(anyString(), anyString()))
        .thenReturn(Optional.of(testUtil.getPreferenceSelectorEntity()));

    CommonServiceException ex =
        assertThrows(
            CommonServiceException.class,
            () ->
                selectorAndCostItineraryMappingService.createSelectorAndCostItineraryMapping(
                    TestUtil.ORG_ID, testUtil.getSelectorAndCostItineraryMappingRequest()));

    assertEquals(
        "Selector is not part of cost factors associated with cost itinerary", ex.getMessage());
    verify(preferenceSelectorRepository, times(1))
        .findByOrgIdAndSelectorCf(anyString(), anyString());
    verify(costItineraryAndFactorsRepository, times(1))
        .findByOrgIdAndCostItinerary(anyString(), anyString());
  }

  @Test
  @DisplayName("Creating a Selector And Cost Itinerary Mapping - Invalid selectorCfValue")
  void createSelectorAndCostItineraryMappingInvalidScenarioInvalidSelectorCfValue() {
    var costItineraryAndFactorsEntity = testUtil.getCostItineraryAndFactorsEntity();
    costItineraryAndFactorsEntity.setCostFactors(TestUtil.SELECTOR_CF);

    when(preferenceSelectorRepository.findByOrgIdAndSelectorCf(anyString(), anyString()))
        .thenReturn(List.of(testUtil.getPreferenceSelectorEntity()));
    when(costItineraryAndFactorsRepository.findByOrgIdAndCostItinerary(anyString(), anyString()))
        .thenReturn(Optional.of(costItineraryAndFactorsEntity));
    when(tenantCostTypeRepository.findByOrgIdAndCostType(anyString(), anyString()))
        .thenReturn(Optional.of(testUtil.getTenantCostTypeEntity()));
    when(costFactorRepository.findByOrgIdAndCostFactor(anyString(), anyString()))
        .thenReturn(List.of(testUtil.getCostFactorEntity()));
    when(preferenceSelectorRepository.findFirstByOrgIdAndCostType(anyString(), anyString()))
        .thenReturn(Optional.of(testUtil.getPreferenceSelectorEntity()));

    CommonServiceException ex =
        assertThrows(
            CommonServiceException.class,
            () ->
                selectorAndCostItineraryMappingService.createSelectorAndCostItineraryMapping(
                    TestUtil.ORG_ID, testUtil.getSelectorAndCostItineraryMappingRequest()));

    assertEquals(
        "Selector value in the request is not part of selector's possible values", ex.getMessage());
    verify(preferenceSelectorRepository, times(1))
        .findByOrgIdAndSelectorCf(anyString(), anyString());
    verify(costItineraryAndFactorsRepository, times(1))
        .findByOrgIdAndCostItinerary(anyString(), anyString());
  }

  @Test
  @DisplayName("Creating Selector And Cost Itinerary Mapping - Invalid cost itinerary")
  void createSelectorAndCostItineraryMappingInvalidCostItinerary() {
    when(costItineraryAndFactorsRepository.findByOrgIdAndCostItinerary(anyString(), anyString()))
        .thenReturn(Optional.empty());
    when(preferenceSelectorRepository.findFirstByOrgIdAndCostType(anyString(), anyString()))
        .thenReturn(Optional.of(testUtil.getPreferenceSelectorEntity()));

    CommonServiceException ex =
        assertThrows(
            CommonServiceException.class,
            () ->
                selectorAndCostItineraryMappingService.createSelectorAndCostItineraryMapping(
                    TestUtil.ORG_ID, testUtil.getSelectorAndCostItineraryMappingRequest()));

    assertEquals("Cost Itinerary provided in the request is not valid", ex.getMessage());
    verify(costItineraryAndFactorsRepository, times(1))
        .findByOrgIdAndCostItinerary(anyString(), anyString());
  }

  @Test
  @DisplayName(
      "Creating Selector And Cost Itinerary Mapping - Cost itinerary with DRAFT itineraryStatus")
  void createSelectorAndCostItineraryMappingDraftItinerary() {
    var costItineraryAndFactorsEntity = testUtil.getCostItineraryAndFactorsEntity();
    costItineraryAndFactorsEntity.setCostFactors(TestUtil.SELECTOR_CF);
    costItineraryAndFactorsEntity.setItineraryStatus(ItineraryStatusEnum.DRAFT);
    var costFactorEntity = testUtil.getCostFactorEntity();
    costFactorEntity.setCostFactor(TestUtil.SELECTOR_CF);
    costFactorEntity.setValues(TestUtil.SELECTOR_CF_VALUE);

    when(selectorAndCostItineraryMappingRepository.save(any()))
        .thenReturn(testUtil.getSelectorAndCostItineraryMappingEntity());
    when(preferenceSelectorRepository.findByOrgIdAndSelectorCf(anyString(), anyString()))
        .thenReturn(List.of(testUtil.getPreferenceSelectorEntity()));
    when(costItineraryAndFactorsRepository.findByOrgIdAndCostItinerary(anyString(), anyString()))
        .thenReturn(Optional.of(costItineraryAndFactorsEntity));
    when(tenantCostTypeRepository.findByOrgIdAndCostType(anyString(), anyString()))
        .thenReturn(Optional.of(testUtil.getTenantCostTypeEntity()));
    when(costFactorRepository.findByOrgIdAndCostFactor(anyString(), anyString()))
        .thenReturn(List.of(costFactorEntity));
    when(preferenceSelectorRepository.findFirstByOrgIdAndCostType(anyString(), anyString()))
        .thenReturn(Optional.of(testUtil.getPreferenceSelectorEntity()));

    CommonServiceException ex =
        assertThrows(
            CommonServiceException.class,
            () ->
                selectorAndCostItineraryMappingService.createSelectorAndCostItineraryMapping(
                    TestUtil.ORG_ID, testUtil.getSelectorAndCostItineraryMappingRequest()));

    assertEquals("Can't associate itinerary with DRAFT status", ex.getMessage());
    verify(costItineraryAndFactorsRepository, times(1))
        .findByOrgIdAndCostItinerary(anyString(), anyString());
  }

  @Test
  @DisplayName("Test creating a Selector And Cost Itinerary Mapping for for invalid scenario")
  void createSelectorAndCostItineraryMappingInValidScenario() {
    var costItineraryAndFactorsEntity = testUtil.getCostItineraryAndFactorsEntity();
    costItineraryAndFactorsEntity.setCostFactors(TestUtil.SELECTOR_CF);

    when(preferenceSelectorRepository.findByOrgIdAndSelectorCf(anyString(), anyString()))
        .thenReturn(List.of());
    when(costItineraryAndFactorsRepository.findByOrgIdAndCostItinerary(anyString(), anyString()))
        .thenReturn(Optional.of(costItineraryAndFactorsEntity));
    when(tenantCostTypeRepository.findByOrgIdAndCostType(anyString(), anyString()))
        .thenReturn(Optional.empty());
    when(preferenceSelectorRepository.findFirstByOrgIdAndCostType(anyString(), anyString()))
        .thenReturn(Optional.of(testUtil.getPreferenceSelectorEntity()));

    CommonServiceException ex =
        assertThrows(
            CommonServiceException.class,
            () ->
                selectorAndCostItineraryMappingService.createSelectorAndCostItineraryMapping(
                    TestUtil.ORG_ID, testUtil.getSelectorAndCostItineraryMappingRequest()));

    assertEquals("Selector And CostItinerary Mapping Request is not valid.", ex.getMessage());
    verify(preferenceSelectorRepository, times(1))
        .findByOrgIdAndSelectorCf(anyString(), anyString());
    verify(costItineraryAndFactorsRepository, times(1))
        .findByOrgIdAndCostItinerary(anyString(), anyString());
    verify(tenantCostTypeRepository, times(1)).findByOrgIdAndCostType(anyString(), anyString());
    ex.getFieldInfo().containsKey(TestUtil.COST_ITINERARY_KEY);
    ex.getFieldInfo().containsKey(TestUtil.SELECTOR_CF_KEY);
    ex.getFieldInfo().containsKey(TestUtil.COST_TYPE_KEY);
  }

  @Test
  @DisplayName(
      "Create Selector And Cost Itinerary Mapping - When selectorCfValue is already associated with an itinerary")
  void createSelectorAndCostItineraryMappingInvalidScenario2() {
    var costItineraryAndFactorsEntity = testUtil.getCostItineraryAndFactorsEntity();
    costItineraryAndFactorsEntity.setCostFactors(TestUtil.SELECTOR_CF);
    costItineraryAndFactorsEntity.setItineraryStatus(ItineraryStatusEnum.CREATED);
    var costFactorEntity = testUtil.getCostFactorEntity();
    costFactorEntity.setCostFactor(TestUtil.SELECTOR_CF);
    costFactorEntity.setValues(TestUtil.SELECTOR_CF_VALUE);

    when(preferenceSelectorRepository.findByOrgIdAndSelectorCf(anyString(), anyString()))
        .thenReturn(List.of(testUtil.getPreferenceSelectorEntity()));
    when(costItineraryAndFactorsRepository.findByOrgIdAndCostItinerary(anyString(), anyString()))
        .thenReturn(Optional.of(costItineraryAndFactorsEntity));
    when(tenantCostTypeRepository.findByOrgIdAndCostType(anyString(), anyString()))
        .thenReturn(Optional.of(testUtil.getTenantCostTypeEntity()));
    when(costFactorRepository.findByOrgIdAndCostFactor(anyString(), anyString()))
        .thenReturn(List.of(costFactorEntity));
    when(selectorAndCostItineraryMappingRepository.findByOrgIdAndSelectorCfValueAndCostType(
            anyString(), anyString(), anyString()))
        .thenReturn(List.of(testUtil.getSelectorAndCostItineraryMappingEntity()));
    when(preferenceSelectorRepository.findFirstByOrgIdAndCostType(anyString(), anyString()))
        .thenReturn(Optional.of(testUtil.getPreferenceSelectorEntity()));

    CommonServiceException ex =
        assertThrows(
            CommonServiceException.class,
            () ->
                selectorAndCostItineraryMappingService.createSelectorAndCostItineraryMapping(
                    TestUtil.ORG_ID, testUtil.getSelectorAndCostItineraryMappingRequest()));

    assertEquals("Selector value is associated with an itinerary", ex.getMessage());
    verify(preferenceSelectorRepository, times(1))
        .findByOrgIdAndSelectorCf(anyString(), anyString());
    verify(costItineraryAndFactorsRepository, times(1))
        .findByOrgIdAndCostItinerary(anyString(), anyString());
    verify(tenantCostTypeRepository, times(1)).findByOrgIdAndCostType(anyString(), anyString());
  }

  @Test
  @DisplayName(
      "Create Selector And Cost Itinerary Mapping - When cost type without selector is already associated with an itinerary")
  void createSelectorAndCostItineraryMappingInvalidScenario3() {
    var costItineraryAndFactorsEntity = testUtil.getCostItineraryAndFactorsEntity();
    costItineraryAndFactorsEntity.setCostFactors(TestUtil.SELECTOR_CF);
    costItineraryAndFactorsEntity.setItineraryStatus(ItineraryStatusEnum.CREATED);
    var costFactorEntity = testUtil.getCostFactorEntity();
    costFactorEntity.setCostFactor(TestUtil.SELECTOR_CF);
    costFactorEntity.setValues(TestUtil.SELECTOR_CF_VALUE);
    var request = testUtil.getSelectorAndCostItineraryMappingRequest();
    request.setSelectorCf(null);
    request.setSelectorCfValue(null);

    when(preferenceSelectorRepository.findByOrgIdAndSelectorCf(anyString(), anyString()))
        .thenReturn(List.of(testUtil.getPreferenceSelectorEntity()));
    when(costItineraryAndFactorsRepository.findByOrgIdAndCostItinerary(anyString(), anyString()))
        .thenReturn(Optional.of(costItineraryAndFactorsEntity));
    when(tenantCostTypeRepository.findByOrgIdAndCostType(anyString(), anyString()))
        .thenReturn(Optional.of(testUtil.getTenantCostTypeEntity()));
    when(costFactorRepository.findByOrgIdAndCostFactor(anyString(), anyString()))
        .thenReturn(List.of(costFactorEntity));
    when(selectorAndCostItineraryMappingRepository.findByOrgIdAndCostType(anyString(), anyString()))
        .thenReturn(List.of(testUtil.getSelectorAndCostItineraryMappingEntity()));

    CommonServiceException ex =
        assertThrows(
            CommonServiceException.class,
            () ->
                selectorAndCostItineraryMappingService.createSelectorAndCostItineraryMapping(
                    TestUtil.ORG_ID, request));

    assertEquals("Cost type is already associated with an itinerary", ex.getMessage());
    verify(costItineraryAndFactorsRepository, times(1))
        .findByOrgIdAndCostItinerary(anyString(), anyString());
    verify(tenantCostTypeRepository, times(1)).findByOrgIdAndCostType(anyString(), anyString());
  }

  @Test
  @DisplayName(
      "Create Selector And Cost Itinerary Mapping - When selector is EMPTY_STRING with a valid selectorCfValue")
  void createSelectorAndCostItineraryMappingInvalidScenario4() {
    var costItineraryAndFactorsEntity = testUtil.getCostItineraryAndFactorsEntity();
    costItineraryAndFactorsEntity.setCostFactors(TestUtil.SELECTOR_CF);
    costItineraryAndFactorsEntity.setItineraryStatus(ItineraryStatusEnum.CREATED);
    var costFactorEntity = testUtil.getCostFactorEntity();
    costFactorEntity.setCostFactor(TestUtil.SELECTOR_CF);
    costFactorEntity.setValues(TestUtil.SELECTOR_CF_VALUE);
    var request = testUtil.getSelectorAndCostItineraryMappingRequest();
    request.setSelectorCf("");
    request.setSelectorCfValue(null);

    when(preferenceSelectorRepository.findByOrgIdAndSelectorCf(anyString(), anyString()))
        .thenReturn(List.of(testUtil.getPreferenceSelectorEntity()));
    when(costItineraryAndFactorsRepository.findByOrgIdAndCostItinerary(anyString(), anyString()))
        .thenReturn(Optional.of(costItineraryAndFactorsEntity));
    when(tenantCostTypeRepository.findByOrgIdAndCostType(anyString(), anyString()))
        .thenReturn(Optional.of(testUtil.getTenantCostTypeEntity()));
    when(costFactorRepository.findByOrgIdAndCostFactor(anyString(), anyString()))
        .thenReturn(List.of(costFactorEntity));
    when(selectorAndCostItineraryMappingRepository.findByOrgIdAndCostType(anyString(), anyString()))
        .thenReturn(List.of(testUtil.getSelectorAndCostItineraryMappingEntity()));

    CommonServiceException ex =
        assertThrows(
            CommonServiceException.class,
            () ->
                selectorAndCostItineraryMappingService.createSelectorAndCostItineraryMapping(
                    TestUtil.ORG_ID, request));

    assertEquals("Selector should be null or a valid string", ex.getMessage());
  }

  @Test
  @DisplayName(
      "Create Selector And Cost Itinerary Mapping - When selector is null with a valid selectorCfValue")
  void createSelectorAndCostItineraryMappingInvalidScenario5() {
    var costItineraryAndFactorsEntity = testUtil.getCostItineraryAndFactorsEntity();
    costItineraryAndFactorsEntity.setCostFactors(TestUtil.SELECTOR_CF);
    costItineraryAndFactorsEntity.setItineraryStatus(ItineraryStatusEnum.CREATED);
    var costFactorEntity = testUtil.getCostFactorEntity();
    costFactorEntity.setCostFactor(TestUtil.SELECTOR_CF);
    costFactorEntity.setValues(TestUtil.SELECTOR_CF_VALUE);
    var request = testUtil.getSelectorAndCostItineraryMappingRequest();
    request.setSelectorCf(null);

    when(preferenceSelectorRepository.findByOrgIdAndSelectorCf(anyString(), anyString()))
        .thenReturn(List.of(testUtil.getPreferenceSelectorEntity()));
    when(costItineraryAndFactorsRepository.findByOrgIdAndCostItinerary(anyString(), anyString()))
        .thenReturn(Optional.of(costItineraryAndFactorsEntity));
    when(tenantCostTypeRepository.findByOrgIdAndCostType(anyString(), anyString()))
        .thenReturn(Optional.of(testUtil.getTenantCostTypeEntity()));
    when(costFactorRepository.findByOrgIdAndCostFactor(anyString(), anyString()))
        .thenReturn(List.of(costFactorEntity));
    when(selectorAndCostItineraryMappingRepository.findByOrgIdAndCostType(anyString(), anyString()))
        .thenReturn(List.of(testUtil.getSelectorAndCostItineraryMappingEntity()));

    CommonServiceException ex =
        assertThrows(
            CommonServiceException.class,
            () ->
                selectorAndCostItineraryMappingService.createSelectorAndCostItineraryMapping(
                    TestUtil.ORG_ID, request));

    assertEquals(
        "Allowed value for selector should be null when selector is null", ex.getMessage());
  }

  @Test
  @DisplayName(
      "Create Selector And Cost Itinerary Mapping - When selector is valid selector and selectorCfValue is empty string")
  void createSelectorAndCostItineraryMappingInvalidScenario6() {
    var costItineraryAndFactorsEntity = testUtil.getCostItineraryAndFactorsEntity();
    costItineraryAndFactorsEntity.setCostFactors(TestUtil.SELECTOR_CF);
    costItineraryAndFactorsEntity.setItineraryStatus(ItineraryStatusEnum.CREATED);
    var costFactorEntity = testUtil.getCostFactorEntity();
    costFactorEntity.setCostFactor(TestUtil.SELECTOR_CF);
    costFactorEntity.setValues(TestUtil.SELECTOR_CF_VALUE);
    var request = testUtil.getSelectorAndCostItineraryMappingRequest();
    request.setSelectorCfValue("");

    when(preferenceSelectorRepository.findByOrgIdAndSelectorCf(anyString(), anyString()))
        .thenReturn(List.of(testUtil.getPreferenceSelectorEntity()));
    when(costItineraryAndFactorsRepository.findByOrgIdAndCostItinerary(anyString(), anyString()))
        .thenReturn(Optional.of(costItineraryAndFactorsEntity));
    when(tenantCostTypeRepository.findByOrgIdAndCostType(anyString(), anyString()))
        .thenReturn(Optional.of(testUtil.getTenantCostTypeEntity()));
    when(costFactorRepository.findByOrgIdAndCostFactor(anyString(), anyString()))
        .thenReturn(List.of(costFactorEntity));
    when(selectorAndCostItineraryMappingRepository.findByOrgIdAndCostType(anyString(), anyString()))
        .thenReturn(List.of(testUtil.getSelectorAndCostItineraryMappingEntity()));

    CommonServiceException ex =
        assertThrows(
            CommonServiceException.class,
            () ->
                selectorAndCostItineraryMappingService.createSelectorAndCostItineraryMapping(
                    TestUtil.ORG_ID, request));

    assertEquals(
        "Selector value should be null or one of the values defined for selector", ex.getMessage());
  }

  @Test
  @DisplayName(
      "Creating Selector And Cost Itinerary Mapping - with default itinerary already configured for a cost type and selector")
  void createSelectorAndCostItineraryMappingInvalidScenario7() {
    var costItineraryAndFactorsEntity = testUtil.getCostItineraryAndFactorsEntity();
    costItineraryAndFactorsEntity.setCostFactors(TestUtil.SELECTOR_CF);
    costItineraryAndFactorsEntity.setItineraryStatus(ItineraryStatusEnum.CREATED);
    var costFactorEntity = testUtil.getCostFactorEntity();
    costFactorEntity.setCostFactor(TestUtil.SELECTOR_CF);
    costFactorEntity.setValues(TestUtil.SELECTOR_CF_VALUE);
    var request = testUtil.getSelectorAndCostItineraryMappingRequest();
    request.setSelectorCfValue(null);

    when(preferenceSelectorRepository.findByOrgIdAndSelectorCf(anyString(), anyString()))
        .thenReturn(List.of(testUtil.getPreferenceSelectorEntity()));
    when(costItineraryAndFactorsRepository.findByOrgIdAndCostItinerary(anyString(), anyString()))
        .thenReturn(Optional.of(costItineraryAndFactorsEntity));
    when(tenantCostTypeRepository.findByOrgIdAndCostType(anyString(), anyString()))
        .thenReturn(Optional.of(testUtil.getTenantCostTypeEntity()));
    when(costFactorRepository.findByOrgIdAndCostFactor(anyString(), anyString()))
        .thenReturn(List.of(costFactorEntity));
    when(selectorAndCostItineraryMappingRepository
            .findByOrgIdAndCostTypeAndSelectorCfAndSelectorCfValue(
                anyString(), anyString(), anyString(), eq(null)))
        .thenReturn(List.of(testUtil.getSelectorAndCostItineraryMappingEntity()));
    when(preferenceSelectorRepository.findFirstByOrgIdAndCostType(anyString(), anyString()))
        .thenReturn(Optional.of(testUtil.getPreferenceSelectorEntity()));

    CommonServiceException ex =
        assertThrows(
            CommonServiceException.class,
            () ->
                selectorAndCostItineraryMappingService.createSelectorAndCostItineraryMapping(
                    TestUtil.ORG_ID, request));

    assertEquals(
        "An itinerary is already associated with given cost type with valid selector and null selector value",
        ex.getMessage());
    verify(costItineraryAndFactorsRepository, times(1))
        .findByOrgIdAndCostItinerary(anyString(), anyString());
    verify(tenantCostTypeRepository, times(1)).findByOrgIdAndCostType(anyString(), anyString());
  }

  @Test
  @DisplayName("Creating Selector And Cost Itinerary Mapping - with invalid selector")
  void createSelectorAndCostItineraryMappingInvalidScenario8() {

    var request = testUtil.getSelectorAndCostItineraryMappingRequest();

    when(preferenceSelectorRepository.findFirstByOrgIdAndCostType(anyString(), anyString()))
        .thenReturn(Optional.empty());

    CommonServiceException ex =
        assertThrows(
            CommonServiceException.class,
            () ->
                selectorAndCostItineraryMappingService.createSelectorAndCostItineraryMapping(
                    TestUtil.ORG_ID, request));

    assertEquals("Selector is invalid for given orgId and cost type.", ex.getMessage());

    verify(preferenceSelectorRepository, times(1))
        .findFirstByOrgIdAndCostType(anyString(), anyString());
  }

  @Test
  @DisplayName(
      "Creating Selector And Cost Itinerary Mapping - with invalid selector which does not match with the selector in request")
  void createSelectorAndCostItineraryMappingInvalidScenario9() {

    var request = testUtil.getSelectorAndCostItineraryMappingRequest();

    when(preferenceSelectorRepository.findFirstByOrgIdAndCostType(anyString(), anyString()))
        .thenReturn(Optional.of(testUtil.getPreferenceSelectorEntity3()));

    CommonServiceException ex =
        assertThrows(
            CommonServiceException.class,
            () ->
                selectorAndCostItineraryMappingService.createSelectorAndCostItineraryMapping(
                    TestUtil.ORG_ID, request));

    assertEquals("Selector is invalid for given orgId and cost type.", ex.getMessage());

    verify(preferenceSelectorRepository, times(1))
        .findFirstByOrgIdAndCostType(anyString(), anyString());
  }

  @Test
  @DisplayName("Test retrieving a Selector And Cost Itinerary Mapping")
  void getSelectorAndCostItineraryMapping() throws CommonServiceException {
    when(selectorAndCostItineraryMappingRepository.findByIdAndOrgId(TestUtil.ID, TestUtil.ORG_ID))
        .thenReturn(Optional.ofNullable(testUtil.getSelectorAndCostItineraryMappingEntity()));
    var response =
        selectorAndCostItineraryMappingService.getSelectorAndCostItineraryMapping(
            TestUtil.ORG_ID, TestUtil.ID);
    assertNotNull(response);
    assertEquals(TestUtil.ID, response.getId());
    assertEquals(TestUtil.ORG_ID, response.getOrgId());
  }

  @Test
  @DisplayName("Test retrieving a Selector And Cost Itinerary Mapping - Record Not Found")
  void getSelectorAndCostItineraryMapping_NotFound() {
    when(selectorAndCostItineraryMappingRepository.findByIdAndOrgId(TestUtil.ID, TestUtil.ORG_ID))
        .thenReturn(Optional.empty());
    CommonServiceException exception =
        assertThrows(
            CommonServiceException.class,
            () ->
                selectorAndCostItineraryMappingService.getSelectorAndCostItineraryMapping(
                    TestUtil.ORG_ID, TestUtil.ID));
    assertEquals(SELECTOR_ITINERARY_NOT_FOUND_EXCEPTION, exception.getMessage());
    assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
    assertEquals(0x1771, exception.getErrorCode());
    assertTrue(exception.getFieldInfo().containsKey(ID));
    assertTrue(exception.getFieldInfo().containsKey(ORG_ID));
  }

  @Test
  @DisplayName("Should get selector and cost itinerary mapping by orgId , selectorCf and costType")
  void getSelectorAndCostItineraryMappingByOrgIdAndSelectorCfAndCostType()
      throws CommonServiceException {
    when(selectorAndCostItineraryMappingRepository.findByOrgIdAndSelectorCfAndCostType(
            TestUtil.ORG_ID, TestUtil.SELECTOR_CF, TestUtil.COST_TYPE))
        .thenReturn(List.of(testUtil.getSelectorAndCostItineraryMappingEntity()));
    var response =
        selectorAndCostItineraryMappingService
            .getSelectorAndCostItineraryMappingByOrgIdSelectorCfAndCostType(
                TestUtil.ORG_ID, TestUtil.SELECTOR_CF, TestUtil.COST_TYPE);
    assertNotNull(response);
    assertEquals(TestUtil.ID, response.get(0).getId());
    assertEquals(TestUtil.ORG_ID, response.get(0).getOrgId());

    verify(selectorAndCostItineraryMappingRepository, times(1))
        .findByOrgIdAndSelectorCfAndCostType(
            TestUtil.ORG_ID, TestUtil.SELECTOR_CF, TestUtil.COST_TYPE);
  }

  @Test
  @DisplayName("Should get selector and cost itinerary mapping by orgId  and costType")
  void getSelectorAndCostItineraryMappingByOrgIdAndSelectorCfAndCostTypeWithout()
      throws CommonServiceException {
    when(selectorAndCostItineraryMappingRepository.findByOrgIdAndCostType(
            TestUtil.ORG_ID, TestUtil.COST_TYPE))
        .thenReturn(List.of(testUtil.getSelectorAndCostItineraryMappingEntity()));
    var response =
        selectorAndCostItineraryMappingService
            .getSelectorAndCostItineraryMappingByOrgIdSelectorCfAndCostType(
                TestUtil.ORG_ID, null, TestUtil.COST_TYPE);
    assertNotNull(response);
    assertEquals(TestUtil.ID, response.get(0).getId());
    assertEquals(TestUtil.ORG_ID, response.get(0).getOrgId());

    verify(selectorAndCostItineraryMappingRepository, times(1))
        .findByOrgIdAndCostType(TestUtil.ORG_ID, TestUtil.COST_TYPE);
  }

  @Test
  @DisplayName(
      "Should throw exception when selector and cost itinerary mapping not found by orgId , selectorCf and costType")
  void getSelectorAndCostItineraryMappingByOrgIdAndSelectorCfAndCostType_NotFound() {
    when(selectorAndCostItineraryMappingRepository.findByOrgIdAndSelectorCfAndCostType(
            TestUtil.ORG_ID, TestUtil.SELECTOR_CF, TestUtil.COST_TYPE))
        .thenReturn(List.of());
    CommonServiceException exception =
        assertThrows(
            CommonServiceException.class,
            () ->
                selectorAndCostItineraryMappingService
                    .getSelectorAndCostItineraryMappingByOrgIdSelectorCfAndCostType(
                        TestUtil.ORG_ID, TestUtil.SELECTOR_CF, TestUtil.COST_TYPE));
    assertEquals(SELECTOR_ITINERARY_NOT_FOUND_EXCEPTION, exception.getMessage());
    assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
    assertEquals(0x1771, exception.getErrorCode());
    assertTrue(exception.getFieldInfo().containsKey(ORG_ID));
    assertTrue(exception.getFieldInfo().containsKey(SELECTOR_CF));
    assertTrue(exception.getFieldInfo().containsKey(COST_TYPE));
    verify(selectorAndCostItineraryMappingRepository, times(1))
        .findByOrgIdAndSelectorCfAndCostType(
            TestUtil.ORG_ID, TestUtil.SELECTOR_CF, TestUtil.COST_TYPE);
  }

  @Test
  @DisplayName("Delete selector and cost itinerary mapping: Happy Path")
  void deleteSelectorAndCostItineraryMappingHappyPathTest() throws CommonServiceException {
    when(selectorAndCostItineraryMappingRepository.findByIdAndOrgId(TestUtil.ID, TestUtil.ORG_ID))
        .thenReturn(Optional.of(testUtil.getSelectorAndCostItineraryMappingEntity()));
    doNothing()
        .when(selectorAndCostItineraryMappingRepository)
        .delete(any(SelectorAndCostItineraryMappingEntity.class));
    SelectorAndCostItineraryMappingResponse response =
        selectorAndCostItineraryMappingService.deleteSelectorAndCostItineraryMapping(
            TestUtil.ORG_ID, TestUtil.ID);
    assertNotNull(response);
    assertEquals(TestUtil.ORG_ID, response.getOrgId());
    assertEquals(TestUtil.COST_ITINERARY_UPSLIKE, response.getCostItinerary());
    assertEquals(TestUtil.COST_TYPE_SHIPPING_COST, response.getCostType());
  }

  @Test
  @DisplayName("Delete selector and cost itinerary mapping: Record not found")
  void deleteSelectorAndCostItineraryMappingNotFoundTest() {
    when(selectorAndCostItineraryMappingRepository.findByIdAndOrgId(TestUtil.ID, TestUtil.ORG_ID))
        .thenReturn(Optional.empty());
    CommonServiceException exception =
        assertThrows(
            CommonServiceException.class,
            () ->
                selectorAndCostItineraryMappingService.deleteSelectorAndCostItineraryMapping(
                    TestUtil.ORG_ID, TestUtil.ID));
    assertEquals(SELECTOR_ITINERARY_NOT_FOUND_EXCEPTION, exception.getMessage());
    assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
    assertEquals(0x1771, exception.getErrorCode());
    assertTrue(exception.getFieldInfo().containsKey(ID));
    assertTrue(exception.getFieldInfo().containsKey(ORG_ID));
  }

  @Test
  @DisplayName("Get cache keys for all selector and cost itinerary")
  void getAllSelectorAndCostItineraryCacheKeysTest() {
    List<SelectorAndCostItineraryMappingEntity> result =
        testUtil.getSelectorAndCostItineraryEntityList();
    when(selectorAndCostItineraryMappingRepository.findAllSelectorAndCostItineraryMappingEntities(
            any()))
        .thenReturn(result);
    assertEquals(1, result.size());
  }

  @Test
  @DisplayName("Update selector and cost itinerary mapping: Happy Path")
  void updateSelectorAndCostItineraryMappingHappyPathTest() throws CommonServiceException {
    when(selectorAndCostItineraryMappingRepository.findByIdAndOrgId(TestUtil.ID, TestUtil.ORG_ID))
        .thenReturn(Optional.of(testUtil.getSelectorAndCostItineraryMappingEntity()));
    when(costItineraryAndFactorsRepository.findByOrgIdAndCostItinerary(anyString(), anyString()))
        .thenReturn(Optional.of(testUtil.getCreatedCostItineraryAndFactorsEntity()));
    when(selectorAndCostItineraryMappingRepository.save(any()))
        .thenReturn(testUtil.getSelectorAndCostItineraryMappingEntity());
    SelectorAndCostItineraryMappingResponse response =
        selectorAndCostItineraryMappingService.updateSelectorAndCostItineraryMappingByIdAndOrgId(
            TestUtil.ID,
            TestUtil.ORG_ID,
            testUtil.getUpdateSelectorAndCostItineraryMappingRequest());

    assertNotNull(response);
    assertEquals(TestUtil.ORG_ID, response.getOrgId());
    assertEquals(TestUtil.COST_ITINERARY_UPSLIKE, response.getCostItinerary());
    assertEquals(TestUtil.COST_TYPE_SHIPPING_COST, response.getCostType());
  }

  @Test
  @DisplayName("Update selector and cost itinerary mapping when status is DRAFT - Invalid")
  void updateSelectorAndCostItineraryMappingDraftStatusTest() throws CommonServiceException {
    when(selectorAndCostItineraryMappingRepository.findByIdAndOrgId(TestUtil.ID, TestUtil.ORG_ID))
        .thenReturn(Optional.of(testUtil.getSelectorAndCostItineraryMappingEntity()));
    when(costItineraryAndFactorsRepository.findByOrgIdAndCostItinerary(anyString(), anyString()))
        .thenReturn(Optional.of(testUtil.getCostItineraryAndFactorsEntity()));

    Exception ex =
        assertThrows(
            CommonServiceException.class,
            () ->
                selectorAndCostItineraryMappingService
                    .updateSelectorAndCostItineraryMappingByIdAndOrgId(
                        TestUtil.ID,
                        TestUtil.ORG_ID,
                        testUtil.getUpdateSelectorAndCostItineraryMappingRequest()));

    assertNotNull(ex);
    assertEquals("Can't associate itinerary with DRAFT status", ex.getMessage());
  }

  @Test
  @DisplayName("Update selector and cost itinerary mapping when cost itinerary not found")
  void updateSelectorAndCostItineraryMappingWhenCostItineraryFoundTest()
      throws CommonServiceException {
    when(selectorAndCostItineraryMappingRepository.findByIdAndOrgId(TestUtil.ID, TestUtil.ORG_ID))
        .thenReturn(Optional.of(testUtil.getSelectorAndCostItineraryMappingEntity()));
    when(costItineraryAndFactorsRepository.findByOrgIdAndCostItinerary(anyString(), anyString()))
        .thenReturn(Optional.empty());

    Exception ex =
        assertThrows(
            CommonServiceException.class,
            () ->
                selectorAndCostItineraryMappingService
                    .updateSelectorAndCostItineraryMappingByIdAndOrgId(
                        TestUtil.ID,
                        TestUtil.ORG_ID,
                        testUtil.getUpdateSelectorAndCostItineraryMappingRequest()));

    assertNotNull(ex);
    assertEquals("Cost Itinerary and factors not found with given details", ex.getMessage());
  }
}
