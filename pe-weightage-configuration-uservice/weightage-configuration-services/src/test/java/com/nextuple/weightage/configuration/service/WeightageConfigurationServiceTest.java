/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */
package com.nextuple.weightage.configuration.service;

import static com.nextuple.weightage.configuration.utils.WeightageConfigurationConstants.KEY;
import static com.nextuple.weightage.configuration.utils.WeightageConfigurationConstants.KEYS;
import static com.nextuple.weightage.configuration.utils.WeightageConfigurationConstants.ORG_ID;
import static com.nextuple.weightage.configuration.utils.WeightageConfigurationConstants.TYPE;
import static com.nextuple.weightage.configuration.utils.WeightageConfigurationConstants.WEIGHTAGE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.exception.PromiseEngineException;
import com.nextuple.weightage.configuration.TestUtil;
import com.nextuple.weightage.configuration.api.domain.dto.WeightageCacheKeyDto;
import com.nextuple.weightage.configuration.api.domain.dto.WeightageConfigurationDto;
import com.nextuple.weightage.configuration.api.domain.inbound.CreateWeightageConfigurationRequest;
import com.nextuple.weightage.configuration.api.domain.inbound.FetchWeightageRequest;
import com.nextuple.weightage.configuration.api.domain.inbound.UpdateWeightageConfigurationRequest;
import com.nextuple.weightage.configuration.persistence.domain.WeightageConfigurationDomainDto;
import com.nextuple.weightage.configuration.persistence.service.impl.WeightageConfigurationPersistenceServiceImpl;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

class WeightageConfigurationServiceTest {
  @InjectMocks private WeightageConfigurationService weightageConfigurationService;

  @Mock
  private WeightageConfigurationPersistenceServiceImpl weightageConfigurationPersistenceService;

  @InjectMocks private TestUtil testUtil;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void fetchWeightageTest() throws PromiseEngineException, CommonServiceException {
    List<WeightageConfigurationDomainDto> weightageConfigurationDomainDtoList =
        Collections.singletonList(testUtil.getWeightageConfigurationDomainDto());
    FetchWeightageRequest fetchWeightageRequest = testUtil.getFetchWeightageRequest();
    when(weightageConfigurationPersistenceService.fetchWeightage(any(FetchWeightageRequest.class)))
        .thenReturn(weightageConfigurationDomainDtoList);
    Map<String, Float> fetchWeightageResponse =
        weightageConfigurationService.fetchWeightage(fetchWeightageRequest);
    assertTrue(fetchWeightageResponse.containsKey(KEYS.get(0)));
    assertTrue(fetchWeightageResponse.containsValue(WEIGHTAGE));
    verify(weightageConfigurationPersistenceService, times(1))
        .fetchWeightage(any(FetchWeightageRequest.class));
  }

  @Test
  void fetchWeightageNotFoundTest() throws PromiseEngineException {
    FetchWeightageRequest fetchWeightageRequest = testUtil.getFetchWeightageRequest();
    when(weightageConfigurationPersistenceService.fetchWeightage(any(FetchWeightageRequest.class)))
        .thenReturn(new ArrayList<>());
    assertThrows(
        PromiseEngineException.class,
        () -> {
          weightageConfigurationService.fetchWeightage(fetchWeightageRequest);
        });
    verify(weightageConfigurationPersistenceService, times(1))
        .fetchWeightage(any(FetchWeightageRequest.class));
  }

  @Test
  void createWeightageConfigurationTest() throws PromiseEngineException, CommonServiceException {
    WeightageConfigurationDomainDto weightageConfigurationDomainDto =
        testUtil.getWeightageConfigurationDomainDto();
    CreateWeightageConfigurationRequest createWeightageConfigurationRequest =
        testUtil.getCreateWeightageConfigurationRequest();
    when(weightageConfigurationPersistenceService.saveWeightageConfiguration(
            any(WeightageConfigurationDomainDto.class)))
        .thenReturn(weightageConfigurationDomainDto);
    WeightageConfigurationDto received_dto =
        weightageConfigurationService.createWeightageConfiguration(
            createWeightageConfigurationRequest);
    assertEquals(createWeightageConfigurationRequest.getWeightage(), received_dto.getWeightage());
    verify(weightageConfigurationPersistenceService, times(1))
        .saveWeightageConfiguration(any(WeightageConfigurationDomainDto.class));
  }

  @Test
  void getWeightageConfigurationTest() throws PromiseEngineException {
    WeightageConfigurationDomainDto weightageConfigurationDomainDto =
        testUtil.getWeightageConfigurationDomainDto();
    when(weightageConfigurationPersistenceService.getWeightageConfiguration(
            anyString(), anyString(), anyString()))
        .thenReturn(weightageConfigurationDomainDto);
    WeightageConfigurationDto received_dto =
        weightageConfigurationService.getWeightageConfiguration(ORG_ID, TYPE, KEYS.get(0));
    assertEquals(received_dto.getOrgId(), weightageConfigurationDomainDto.getOrgId());
    verify(weightageConfigurationPersistenceService, times(1))
        .getWeightageConfiguration(anyString(), anyString(), anyString());
  }

  @Test
  void getWeightageConfigurationNotFoundTest() throws PromiseEngineException {
    when(weightageConfigurationPersistenceService.getWeightageConfiguration(
            anyString(), anyString(), anyString()))
        .thenReturn(null);
    assertThrows(
        PromiseEngineException.class,
        () -> {
          weightageConfigurationService.getWeightageConfiguration(ORG_ID, TYPE, KEYS.get(0));
        });
    verify(weightageConfigurationPersistenceService, times(1))
        .getWeightageConfiguration(anyString(), anyString(), anyString());
  }

  @Test
  void getWeightageConfigurationsByKey() throws PromiseEngineException {
    WeightageConfigurationDomainDto weightageConfigurationDomainDto =
        testUtil.getWeightageConfigurationDomainDto();
    when(weightageConfigurationPersistenceService.getWeightageConfigurationsByKey(anyString()))
        .thenReturn(Collections.singletonList(weightageConfigurationDomainDto));
    List<WeightageConfigurationDto> received_dto =
        weightageConfigurationService.getWeightageConfigurationsByKey(KEYS.get(0));
    assertEquals(received_dto.get(0).getOrgId(), weightageConfigurationDomainDto.getOrgId());
    verify(weightageConfigurationPersistenceService, times(1))
        .getWeightageConfigurationsByKey(anyString());
  }

  @Test
  void updateWeightageConfigurationTest() throws PromiseEngineException {
    WeightageConfigurationDomainDto weightageConfigurationDomainDto =
        testUtil.getWeightageConfigurationDomainDto();
    UpdateWeightageConfigurationRequest baseRequest =
        testUtil.getUpdateWeightageConfigurationRequest();
    when(weightageConfigurationPersistenceService.getWeightageConfiguration(
            anyString(), anyString(), anyString()))
        .thenReturn(weightageConfigurationDomainDto);
    when(weightageConfigurationPersistenceService.saveWeightageConfiguration(
            any(WeightageConfigurationDomainDto.class)))
        .thenReturn(weightageConfigurationDomainDto);
    WeightageConfigurationDto updated_weightageConfigurationDto =
        weightageConfigurationService.updateWeightageConfiguration(
            ORG_ID, TYPE, KEYS.get(0), baseRequest);
    assertEquals(baseRequest.getWeightage(), updated_weightageConfigurationDto.getWeightage());
    assertEquals(baseRequest.getType(), updated_weightageConfigurationDto.getType());
    verify(weightageConfigurationPersistenceService, times(1))
        .getWeightageConfiguration(anyString(), anyString(), anyString());
    verify(weightageConfigurationPersistenceService, times(1))
        .saveWeightageConfiguration(any(WeightageConfigurationDomainDto.class));
  }

  @Test
  void deleteWeightageConfigurationTest() throws PromiseEngineException {
    WeightageConfigurationDomainDto weightageConfigurationDomainDto =
        testUtil.getWeightageConfigurationDomainDto();
    when(weightageConfigurationPersistenceService.getWeightageConfiguration(
            anyString(), anyString(), anyString()))
        .thenReturn(weightageConfigurationDomainDto);
    when(weightageConfigurationPersistenceService.deleteWeightageConfiguration(
            any(WeightageConfigurationDomainDto.class)))
        .thenReturn(weightageConfigurationDomainDto);
    WeightageConfigurationDto deleted_weightageConfigurationDto =
        weightageConfigurationService.deleteWeightageConfiguration(ORG_ID, TYPE, KEYS.get(0));
    assertEquals(
        weightageConfigurationDomainDto.getOrgId(), deleted_weightageConfigurationDto.getOrgId());
  }

  @Test
  void getAllWeightageCacheKeysTest() throws PromiseEngineException {
    List<WeightageConfigurationDomainDto> weightageConfigurationList =
        testUtil.getWeightageConfigurationList();
    when(weightageConfigurationPersistenceService.getAllWeightageConfiguration(any()))
        .thenReturn(weightageConfigurationList);
    List<WeightageCacheKeyDto> response = weightageConfigurationService.getAllWeightageCacheKeys(2);
    assertEquals(2, response.size());
    assertEquals(weightageConfigurationList.get(0).getType(), response.get(0).getType());
    verify(weightageConfigurationPersistenceService, Mockito.times(1))
        .getAllWeightageConfiguration(any());
  }

  @Test
  void validateKeysExceptionTest() throws CommonServiceException {
    List<String> keys = new ArrayList<>();
    keys.add("");
    Exception exception =
        Assertions.assertThrows(
            CommonServiceException.class, () -> weightageConfigurationService.validateKeys(keys));
    Assertions.assertEquals("Keys cannot contain null or an empty string", exception.getMessage());
  }

  @Test
  void createWeightageConfigurationIfAlreadyExistsTest() throws PromiseEngineException {
    WeightageConfigurationDomainDto weightageConfigurationDomainDto =
        testUtil.getWeightageConfigurationDomainDto();
    CreateWeightageConfigurationRequest createWeightageConfigurationRequest =
        CreateWeightageConfigurationRequest.builder()
            .orgId(ORG_ID)
            .type(TYPE)
            .key(KEY)
            .weightage(WEIGHTAGE)
            .build();
    when(weightageConfigurationPersistenceService.getWeightageConfiguration(any(), any(), any()))
        .thenReturn(weightageConfigurationDomainDto);
    Assertions.assertThrows(
        CommonServiceException.class,
        () ->
            weightageConfigurationService.createWeightageConfiguration(
                testUtil.getCreateWeightageConfigurationRequest()));
    verify(weightageConfigurationPersistenceService, times(0))
        .saveWeightageConfiguration(any(WeightageConfigurationDomainDto.class));
  }
}
