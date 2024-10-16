/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.sourcing.cost.config.service;

import static com.nextuple.sourcing.cost.config.utils.TestUtil.COST_TYPE;
import static com.nextuple.sourcing.cost.config.utils.TestUtil.ORG_ID;
import static com.nextuple.sourcing.cost.config.utils.TestUtil.SELECTOR_ID;
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
import com.nextuple.sourcing.cost.config.domain.entity.PreferenceSelectorEntity;
import com.nextuple.sourcing.cost.config.domain.repository.CostFactorRepository;
import com.nextuple.sourcing.cost.config.domain.repository.PreferenceSelectorRepository;
import com.nextuple.sourcing.cost.config.domain.repository.SelectorAndCostItineraryMappingRepository;
import com.nextuple.sourcing.cost.config.domain.repository.TenantCostTypeRepository;
import com.nextuple.sourcing.cost.config.dto.PreferenceSelectorDto;
import com.nextuple.sourcing.cost.config.inbound.CreatePreferenceSelectorRequest;
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

class PreferenceSelectorServiceTest {
  @InjectMocks private PreferenceSelectorService preferenceSelectorService;
  @Mock private PreferenceSelectorRepository preferenceSelectorRepository;
  @Mock private SelectorAndCostItineraryMappingRepository selectorAndCostItineraryMappingRepository;

  @Mock private TenantCostTypeRepository tenantCostTypeRepository;
  @Mock private CostFactorRepository costFactorRepository;

  @InjectMocks private TestUtil testUtil;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test()
  void createPreferenceSelectorTest() throws CommonServiceException {
    CreatePreferenceSelectorRequest createPreferenceSelectorRequest =
        testUtil.getUpsertPreferenceSelectorRequest();
    when(preferenceSelectorRepository.save(any(PreferenceSelectorEntity.class)))
        .thenReturn(testUtil.getPreferenceSelectorEntity());
    when(tenantCostTypeRepository.findByOrgIdAndCostType(anyString(), anyString()))
        .thenReturn(Optional.of(testUtil.getTenantCostTypeEntity()));
    when(preferenceSelectorRepository.findFirstByOrgIdAndCostType(anyString(), anyString()))
        .thenReturn(Optional.empty());
    when(costFactorRepository.findFirstByOrgIdAndCostFactor(anyString(), anyString()))
        .thenReturn(Optional.of(testUtil.getCostFactorEntity()));

    PreferenceSelectorDto preferenceSelectorResponse =
        preferenceSelectorService.createPreferenceSelector(ORG_ID, createPreferenceSelectorRequest);
    assertEquals(
        testUtil.getPreferenceSelectorEntity().getId(), preferenceSelectorResponse.getId());
    verify(preferenceSelectorRepository, times(1)).save(any(PreferenceSelectorEntity.class));
    verify(preferenceSelectorRepository, times(1))
        .findFirstByOrgIdAndCostType(anyString(), anyString());
    verify(costFactorRepository, times(1)).findFirstByOrgIdAndCostFactor(anyString(), anyString());
  }

  @Test()
  @DisplayName("Should throw exception when selector already exist for given orgid and costType")
  void createPreferenceSelectorWhenSelectorAlreadyExistForOrgIdAndCostTypeTest()
      throws CommonServiceException {
    CreatePreferenceSelectorRequest createPreferenceSelectorRequest =
        testUtil.getUpsertPreferenceSelectorRequest();

    when(preferenceSelectorRepository.findFirstByOrgIdAndCostType(anyString(), anyString()))
        .thenReturn(Optional.empty());
    when(costFactorRepository.findFirstByOrgIdAndCostFactor(anyString(), anyString()))
        .thenReturn(Optional.empty());

    Exception ex =
        assertThrows(
            CommonServiceException.class,
            () ->
                preferenceSelectorService.createPreferenceSelector(
                    ORG_ID, createPreferenceSelectorRequest));
    assertEquals("selectorCf is not a valid cost factor", ex.getMessage());
    verify(preferenceSelectorRepository, times(1))
        .findFirstByOrgIdAndCostType(anyString(), anyString());
    verify(costFactorRepository, times(1)).findFirstByOrgIdAndCostFactor(anyString(), anyString());
  }

  @Test()
  @DisplayName("Should throw exception when cost factor not found for org id and selector cf")
  void createPreferenceSelectorInvalidCostFactorValidationTest() throws CommonServiceException {
    CreatePreferenceSelectorRequest createPreferenceSelectorRequest =
        testUtil.getUpsertPreferenceSelectorRequest();

    when(preferenceSelectorRepository.findFirstByOrgIdAndCostType(anyString(), anyString()))
        .thenReturn(Optional.of(testUtil.getPreferenceSelectorEntity()));

    Exception ex =
        assertThrows(
            CommonServiceException.class,
            () ->
                preferenceSelectorService.createPreferenceSelector(
                    ORG_ID, createPreferenceSelectorRequest));
    assertEquals("Preference selector already exist for orgId and costType", ex.getMessage());
    verify(preferenceSelectorRepository, times(1))
        .findFirstByOrgIdAndCostType(anyString(), anyString());
  }

  @Test()
  void createPreferenceSelectorTest_InvalidTenantCostTypeValidation()
      throws CommonServiceException {
    CreatePreferenceSelectorRequest createPreferenceSelectorRequest =
        testUtil.getUpsertPreferenceSelectorRequest();
    when(preferenceSelectorRepository.save(any(PreferenceSelectorEntity.class)))
        .thenReturn(testUtil.getPreferenceSelectorEntity());
    when(tenantCostTypeRepository.findByOrgIdAndCostType(anyString(), anyString()))
        .thenReturn(Optional.empty());
    when(preferenceSelectorRepository.findFirstByOrgIdAndCostType(anyString(), anyString()))
        .thenReturn(Optional.empty());
    when(costFactorRepository.findFirstByOrgIdAndCostFactor(anyString(), anyString()))
        .thenReturn(Optional.of(testUtil.getCostFactorEntity()));

    CommonServiceException ex =
        assertThrows(
            CommonServiceException.class,
            () ->
                preferenceSelectorService.createPreferenceSelector(
                    ORG_ID, createPreferenceSelectorRequest));

    assertEquals("Invalid cost type for given orgId.", ex.getMessage());
    verify(preferenceSelectorRepository, times(0)).save(any(PreferenceSelectorEntity.class));
    verify(preferenceSelectorRepository, times(1))
        .findFirstByOrgIdAndCostType(anyString(), anyString());
    verify(costFactorRepository, times(1)).findFirstByOrgIdAndCostFactor(anyString(), anyString());
  }

  @Test()
  void findByOrgIdAndPreferenceSelectorIdTest() throws CommonServiceException {
    when(preferenceSelectorRepository.findByIdAndOrgId(anyLong(), anyString()))
        .thenReturn(Optional.ofNullable(testUtil.getPreferenceSelectorEntity()));

    PreferenceSelectorDto preferenceSelectorResponse =
        preferenceSelectorService.findByOrgIdAndPreferenceSelectorId(ORG_ID, SELECTOR_ID);
    assertEquals(
        testUtil.getPreferenceSelectorEntity().getId(), preferenceSelectorResponse.getId());
    verify(preferenceSelectorRepository, times(1)).findByIdAndOrgId(anyLong(), anyString());
  }

  @Test()
  void findByOrgIdAndPreferenceSelectorIdWhenEntityIsNotThereInDBTest() {
    when(preferenceSelectorRepository.findByIdAndOrgId(anyLong(), anyString()))
        .thenReturn(Optional.empty());

    CommonServiceException ex =
        assertThrows(
            CommonServiceException.class,
            () -> {
              preferenceSelectorService.findByOrgIdAndPreferenceSelectorId(ORG_ID, SELECTOR_ID);
            });

    assertEquals("Preference Selector not found", ex.getMessage());
    verify(preferenceSelectorRepository, times(1)).findByIdAndOrgId(anyLong(), anyString());
  }

  @Test
  @DisplayName("Should find preference selector by orgId and preference cost type")
  void findByOrgIdAndPreferenceCostTypeTest() throws CommonServiceException {
    when(preferenceSelectorRepository.findByOrgIdAndCostType(anyString(), anyString()))
        .thenReturn(List.of(testUtil.getPreferenceSelectorEntity()));

    PreferenceSelectorDto preferenceSelectorResponse =
        preferenceSelectorService.findByOrgIdAndPreferenceCostType(ORG_ID, COST_TYPE);
    assertEquals(
        testUtil.getPreferenceSelectorEntity().getId(), preferenceSelectorResponse.getId());
    verify(preferenceSelectorRepository, times(1)).findByOrgIdAndCostType(anyString(), anyString());
  }

  @Test
  @DisplayName(
      "Should throw exception when preference selector not found by orgId and preference cost type")
  void findByOrgIdAndPreferenceCostTypeWhenEntityIsNotThereInDBTest() {
    when(preferenceSelectorRepository.findByOrgIdAndCostType(anyString(), anyString()))
        .thenReturn(List.of());

    CommonServiceException ex =
        assertThrows(
            CommonServiceException.class,
            () -> {
              preferenceSelectorService.findByOrgIdAndPreferenceCostType(ORG_ID, COST_TYPE);
            });

    assertEquals("Preference Selector not found", ex.getMessage());
    verify(preferenceSelectorRepository, times(1)).findByOrgIdAndCostType(anyString(), anyString());
  }

  @Test()
  void updatePreferenceSelectorTest() throws CommonServiceException {
    when(preferenceSelectorRepository.findByIdAndOrgId(anyLong(), anyString()))
        .thenReturn(Optional.ofNullable(testUtil.getPreferenceSelectorEntity()));
    when(preferenceSelectorRepository.save(any(PreferenceSelectorEntity.class)))
        .thenReturn(testUtil.getPreferenceSelectorEntity());
    when(costFactorRepository.findFirstByOrgIdAndCostFactor(anyString(), anyString()))
        .thenReturn(Optional.of(testUtil.getCostFactorEntity()));

    PreferenceSelectorDto preferenceSelectorResponse =
        preferenceSelectorService.updatePreferenceSelector(
            SELECTOR_ID, ORG_ID, testUtil.getUpdatePreferenceSelectorRequest());
    assertEquals(
        testUtil.getPreferenceSelectorEntity().getCostType(),
        preferenceSelectorResponse.getCostType());
    verify(preferenceSelectorRepository, times(1)).findByIdAndOrgId(anyLong(), anyString());
    verify(preferenceSelectorRepository, times(1)).save(any(PreferenceSelectorEntity.class));
    verify(costFactorRepository, times(1)).findFirstByOrgIdAndCostFactor(anyString(), anyString());
  }

  @Test()
  void updatePreferenceSelectorWhenEntityIsNotThereInDBTest() {
    when(preferenceSelectorRepository.findByIdAndOrgId(anyLong(), anyString()))
        .thenReturn(Optional.empty());

    CommonServiceException ex =
        assertThrows(
            CommonServiceException.class,
            () ->
                preferenceSelectorService.updatePreferenceSelector(
                    SELECTOR_ID, ORG_ID, testUtil.getUpdatePreferenceSelectorRequest()));

    assertEquals("Preference Selector not found", ex.getMessage());
    verify(preferenceSelectorRepository, times(1)).findByIdAndOrgId(anyLong(), anyString());
    verify(preferenceSelectorRepository, times(0)).save(any(PreferenceSelectorEntity.class));
  }

  @Test
  void updatePreferenceSelectorWhenPreferenceSelectorIsAssociatedWithItineraryTest() {
    when(preferenceSelectorRepository.findByIdAndOrgId(anyLong(), anyString()))
        .thenReturn(Optional.ofNullable(testUtil.getPreferenceSelectorEntity()));
    when(selectorAndCostItineraryMappingRepository.findByOrgIdAndSelectorCf(
            anyString(), anyString()))
        .thenReturn(List.of(testUtil.getSelectorAndCostItineraryMappingEntity()));

    CommonServiceException ex =
        assertThrows(
            CommonServiceException.class,
            () ->
                preferenceSelectorService.updatePreferenceSelector(
                    SELECTOR_ID, ORG_ID, testUtil.getUpdatePreferenceSelectorRequest()));

    assertEquals("Preference Selector is associated with one or more itinerary", ex.getMessage());
    verify(preferenceSelectorRepository, times(1)).findByIdAndOrgId(anyLong(), anyString());
    verify(selectorAndCostItineraryMappingRepository, times(1))
        .findByOrgIdAndSelectorCf(anyString(), anyString());
    verify(preferenceSelectorRepository, times(0)).save(any(PreferenceSelectorEntity.class));
  }

  @Test
  @DisplayName("Should throw exception when cost factor not found for org id and selector cf")
  void updatePreferenceSelectorWhenCostFactorNotFoundTest() {
    when(preferenceSelectorRepository.findByIdAndOrgId(anyLong(), anyString()))
        .thenReturn(Optional.ofNullable(testUtil.getPreferenceSelectorEntity()));
    when(selectorAndCostItineraryMappingRepository.findByOrgIdAndSelectorCf(
            anyString(), anyString()))
        .thenReturn(List.of());
    when(costFactorRepository.findFirstByOrgIdAndCostFactor(anyString(), anyString()))
        .thenReturn(Optional.empty());

    CommonServiceException ex =
        assertThrows(
            CommonServiceException.class,
            () ->
                preferenceSelectorService.updatePreferenceSelector(
                    SELECTOR_ID, ORG_ID, testUtil.getUpdatePreferenceSelectorRequest()));

    assertEquals("selectorCf is not a valid cost factor", ex.getMessage());
    verify(preferenceSelectorRepository, times(1)).findByIdAndOrgId(anyLong(), anyString());
    verify(selectorAndCostItineraryMappingRepository, times(1))
        .findByOrgIdAndSelectorCf(anyString(), anyString());
    verify(preferenceSelectorRepository, times(0)).save(any(PreferenceSelectorEntity.class));
  }

  @Test
  void deletePreferenceSelectorTest() throws CommonServiceException {
    when(preferenceSelectorRepository.findByIdAndOrgId(anyLong(), anyString()))
        .thenReturn(Optional.ofNullable(testUtil.getPreferenceSelectorEntity()));
    doNothing().when(preferenceSelectorRepository).delete(any(PreferenceSelectorEntity.class));
    when(selectorAndCostItineraryMappingRepository.findByOrgIdAndSelectorCf(
            anyString(), anyString()))
        .thenReturn(List.of());

    PreferenceSelectorDto preferenceSelectorResponse =
        preferenceSelectorService.deletePreferenceSelector(SELECTOR_ID, ORG_ID);
    assertEquals(
        testUtil.getPreferenceSelectorEntity().getId(), preferenceSelectorResponse.getId());
    verify(preferenceSelectorRepository, times(1)).findByIdAndOrgId(anyLong(), anyString());
    verify(selectorAndCostItineraryMappingRepository, times(1))
        .findByOrgIdAndSelectorCf(anyString(), anyString());
    verify(preferenceSelectorRepository, times(1)).delete(any(PreferenceSelectorEntity.class));
  }

  @Test
  void deletePreferenceSelectorWhenEntityIsNotThereInDBTest() {
    when(preferenceSelectorRepository.findByIdAndOrgId(anyLong(), anyString()))
        .thenReturn(Optional.empty());

    CommonServiceException ex =
        assertThrows(
            CommonServiceException.class,
            () -> preferenceSelectorService.deletePreferenceSelector(SELECTOR_ID, ORG_ID));

    assertEquals("Preference Selector not found", ex.getMessage());
    verify(preferenceSelectorRepository, times(1)).findByIdAndOrgId(anyLong(), anyString());
    verify(selectorAndCostItineraryMappingRepository, times(0))
        .findByOrgIdAndSelectorCf(anyString(), anyString());
    verify(preferenceSelectorRepository, times(0)).delete(any(PreferenceSelectorEntity.class));
  }

  @Test
  void deletePreferenceSelectorWhenPreferenceSelectorIsAssociatedWithItineraryTest() {
    when(preferenceSelectorRepository.findByIdAndOrgId(anyLong(), anyString()))
        .thenReturn(Optional.ofNullable(testUtil.getPreferenceSelectorEntity()));
    when(selectorAndCostItineraryMappingRepository.findByOrgIdAndSelectorCf(
            anyString(), anyString()))
        .thenReturn(List.of(testUtil.getSelectorAndCostItineraryMappingEntity()));

    CommonServiceException ex =
        assertThrows(
            CommonServiceException.class,
            () -> preferenceSelectorService.deletePreferenceSelector(SELECTOR_ID, ORG_ID));

    assertEquals("Preference Selector is associated with one or more itinerary", ex.getMessage());
    verify(preferenceSelectorRepository, times(1)).findByIdAndOrgId(anyLong(), anyString());
    verify(selectorAndCostItineraryMappingRepository, times(1))
        .findByOrgIdAndSelectorCf(anyString(), anyString());
    verify(preferenceSelectorRepository, times(0)).delete(any(PreferenceSelectorEntity.class));
  }

  @Test
  @DisplayName("Get cache keys for all preference selectors")
  void getAllPreferenceSelectorCacheKeysTest() {
    List<PreferenceSelectorEntity> result = testUtil.getPreferenceSelectorEntityList();
    when(preferenceSelectorRepository.findAllPreferenceSelectorEntities(any())).thenReturn(result);
    Assertions.assertEquals(2, result.size());
  }
}
