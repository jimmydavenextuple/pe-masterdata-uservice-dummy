/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.weightage.configuration.persistence.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nextuple.common.exception.PromiseEngineException;
import com.nextuple.weightage.configuration.api.domain.inbound.FetchWeightageRequest;
import com.nextuple.weightage.configuration.persistence.domain.WeightageConfigurationDomainDto;
import com.nextuple.weightage.configuration.persistence.entity.WeightageConfigurationEntity;
import com.nextuple.weightage.configuration.persistence.mapper.WeightageConfigurationEntityMapper;
import com.nextuple.weightage.configuration.persistence.repository.WeightageConfigurationRepository;
import com.nextuple.weightage.configuration.persistence.service.impl.WeightageConfigurationPersistenceServiceImpl;
import com.nextuple.weightage.configuration.persistence.utils.TestUtil;
import com.nextuple.weightage.configuration.persistence.utils.WeightageConfigurationPersistenceConstants;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

class WeightageConfigurationPersistenceServiceTest {
  @InjectMocks
  private WeightageConfigurationPersistenceServiceImpl weightageConfigurationPersistenceService;

  @Mock private WeightageConfigurationRepository weightageConfigurationRepository;
  @Mock private WeightageConfigurationEntityMapper weightageConfigurationEntityMapper;
  @InjectMocks private TestUtil testUtil;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    // Added this
    ReflectionTestUtils.setField(
        weightageConfigurationPersistenceService, "repository", weightageConfigurationRepository);
    ReflectionTestUtils.setField(
        weightageConfigurationPersistenceService, "mapper", weightageConfigurationEntityMapper);
  }

  @Test
  @DisplayName("fetchWeightage: Happy Path")
  void fetchWeightageTest() throws PromiseEngineException {
    WeightageConfigurationDomainDto weightageConfigurationDomainDto =
        testUtil.getWeightageConfigurationDomainDto();
    FetchWeightageRequest fetchWeightageRequest = testUtil.getFetchWeightageRequest();

    when(weightageConfigurationEntityMapper.toDomain(anyList()))
        .thenReturn(List.of(weightageConfigurationDomainDto));
    when(weightageConfigurationRepository.findByKeyInAndOrgIdAndType(
            anyList(), anyString(), anyString()))
        .thenReturn(List.of(testUtil.getWeightageConfigurationEntity()));

    List<WeightageConfigurationDomainDto> received_entity =
        weightageConfigurationPersistenceService.fetchWeightage(fetchWeightageRequest);
    assertEquals(
        WeightageConfigurationPersistenceConstants.KEYS.get(0), received_entity.get(0).getKey());
    verify(weightageConfigurationRepository, times(1))
        .findByKeyInAndOrgIdAndType(anyList(), anyString(), anyString());
    verify(weightageConfigurationEntityMapper, times(1)).toDomain(anyList());
  }

  @Test
  @DisplayName("fetchWeightage: Exception scenario")
  void fetchWeightageExceptionTest() {
    FetchWeightageRequest fetchWeightageRequest = testUtil.getFetchWeightageRequest();
    when(weightageConfigurationEntityMapper.toDomain(anyList())).thenReturn(List.of());

    when(weightageConfigurationRepository.findByKeyInAndOrgIdAndType(
            anyList(), anyString(), anyString()))
        .thenThrow(new RuntimeException("error"));

    Exception exception =
        assertThrows(
            PromiseEngineException.class,
            () -> {
              weightageConfigurationPersistenceService.fetchWeightage(fetchWeightageRequest);
            });
    assertEquals("Unable to find weightage configuration", exception.getMessage());
    verify(weightageConfigurationRepository, times(1))
        .findByKeyInAndOrgIdAndType(anyList(), anyString(), anyString());
  }

  @Test
  @DisplayName("createWeightageConfiguration: Happy path")
  void createWeightageConfigurationTest() throws PromiseEngineException {
    WeightageConfigurationDomainDto weightageConfigurationDomainDto =
        testUtil.getWeightageConfigurationDomainDto();
    WeightageConfigurationEntity weightageConfigurationEntity =
        testUtil.getWeightageConfigurationEntity();
    when(weightageConfigurationRepository.save(any())).thenReturn(weightageConfigurationEntity);
    when(weightageConfigurationEntityMapper.toEntity(any(WeightageConfigurationDomainDto.class)))
        .thenReturn(weightageConfigurationEntity);
    when(weightageConfigurationEntityMapper.toDomain(any(WeightageConfigurationEntity.class)))
        .thenReturn(weightageConfigurationDomainDto);

    WeightageConfigurationDomainDto received_entity =
        weightageConfigurationPersistenceService.saveWeightageConfiguration(
            weightageConfigurationDomainDto);
    assertEquals(weightageConfigurationDomainDto, received_entity);
    verify(weightageConfigurationRepository, times(1)).save(any());
  }

  @Test
  @DisplayName("createWeightageConfiguration: Exception")
  void createWeightageConfigurationExceptionTest() {
    WeightageConfigurationDomainDto weightageConfigurationDomainDto =
        testUtil.getWeightageConfigurationDomainDto();

    when(weightageConfigurationRepository.save(any())).thenThrow(new RuntimeException("error"));

    Exception exception =
        assertThrows(
            PromiseEngineException.class,
            () -> {
              weightageConfigurationPersistenceService.saveWeightageConfiguration(
                  weightageConfigurationDomainDto);
            });
    assertEquals("Unable to save Weightage Configuration", exception.getMessage());
    verify(weightageConfigurationRepository, times(1)).save(any());
  }

  @Test
  @DisplayName("getWeightageConfiguration: Happy path")
  void getWeightageConfigurationTest() throws PromiseEngineException {
    WeightageConfigurationDomainDto weightageConfigurationDomainDto =
        testUtil.getWeightageConfigurationDomainDto();
    WeightageConfigurationEntity weightageConfigurationEntity =
        testUtil.getWeightageConfigurationEntity();

    when(weightageConfigurationRepository.findByOrgIdAndTypeAndKey(
            anyString(), anyString(), anyString()))
        .thenReturn(weightageConfigurationEntity);
    when(weightageConfigurationEntityMapper.toDomain(any(WeightageConfigurationEntity.class)))
        .thenReturn(weightageConfigurationDomainDto);

    WeightageConfigurationDomainDto received_entity =
        weightageConfigurationPersistenceService.getWeightageConfiguration(
            WeightageConfigurationPersistenceConstants.ORG_ID,
            WeightageConfigurationPersistenceConstants.TYPE,
            WeightageConfigurationPersistenceConstants.KEYS.get(0));
    assertEquals(weightageConfigurationDomainDto, received_entity);
    verify(weightageConfigurationRepository, times(1))
        .findByOrgIdAndTypeAndKey(anyString(), anyString(), anyString());
  }

  @Test
  @DisplayName("getWeightageConfiguration: Exception")
  void getWeightageConfigurationExceptionTest() {
    when(weightageConfigurationRepository.findByOrgIdAndTypeAndKey(
            anyString(), anyString(), anyString()))
        .thenThrow(new RuntimeException("error"));

    Exception exception =
        assertThrows(
            PromiseEngineException.class,
            () -> {
              weightageConfigurationPersistenceService.getWeightageConfiguration(
                  WeightageConfigurationPersistenceConstants.ORG_ID,
                  WeightageConfigurationPersistenceConstants.TYPE,
                  WeightageConfigurationPersistenceConstants.KEYS.get(0));
            });
    assertEquals("Unable to find Weightage Configuration", exception.getMessage());
    verify(weightageConfigurationRepository, times(1))
        .findByOrgIdAndTypeAndKey(anyString(), anyString(), anyString());
  }

  @Test
  @DisplayName("getWeightageConfigurationsByKey: Happy Path")
  void getWeightageConfigurationsByKeyTest() throws PromiseEngineException {
    WeightageConfigurationDomainDto weightageConfigurationDomainDto =
        testUtil.getWeightageConfigurationDomainDto();
    WeightageConfigurationEntity weightageConfigurationEntity =
        weightageConfigurationEntityMapper.toEntity(weightageConfigurationDomainDto);

    when(weightageConfigurationRepository.findByKey(anyString()))
        .thenReturn(Collections.singletonList(weightageConfigurationEntity));
    when(weightageConfigurationEntityMapper.toDomain(anyList()))
        .thenReturn(List.of(weightageConfigurationDomainDto));
    List<WeightageConfigurationDomainDto> received_entity =
        weightageConfigurationPersistenceService.getWeightageConfigurationsByKey(
            WeightageConfigurationPersistenceConstants.KEYS.get(0));
    assertEquals(weightageConfigurationDomainDto, received_entity.get(0));
    verify(weightageConfigurationRepository, times(1)).findByKey(any());
    verify(weightageConfigurationEntityMapper, times(1)).toDomain(anyList());
  }

  @Test
  @DisplayName("getWeightageConfigurationsByKeyException: Exception")
  void getWeightageConfigurationsByKeyExceptionTest() {
    when(weightageConfigurationRepository.findByKey(anyString()))
        .thenThrow(new RuntimeException("error"));

    Exception exception =
        assertThrows(
            PromiseEngineException.class,
            () -> {
              weightageConfigurationPersistenceService.getWeightageConfigurationsByKey(
                  WeightageConfigurationPersistenceConstants.KEYS.get(0));
            });
    assertEquals("Unable to find Weightage Configuration", exception.getMessage());
    verify(weightageConfigurationRepository, times(1)).findByKey(any());
  }

  @Test
  @DisplayName("updateWeightageConfiguration: Happy Path")
  void updateWeightageConfigurationTest() throws PromiseEngineException {
    WeightageConfigurationDomainDto weightageConfigurationDomainDto =
        testUtil.getWeightageConfigurationDomainDto();
    WeightageConfigurationEntity weightageConfigurationEntity =
        weightageConfigurationEntityMapper.toEntity(weightageConfigurationDomainDto);

    when(weightageConfigurationEntityMapper.toEntity(any(WeightageConfigurationDomainDto.class)))
        .thenReturn(weightageConfigurationEntity);
    when(weightageConfigurationEntityMapper.toDomain(any(WeightageConfigurationEntity.class)))
        .thenReturn(weightageConfigurationDomainDto);

    when(weightageConfigurationRepository.save(any()))
        .thenReturn(testUtil.getWeightageConfigurationEntity());

    WeightageConfigurationDomainDto received_entity =
        weightageConfigurationPersistenceService.saveWeightageConfiguration(
            weightageConfigurationDomainDto);
    assertEquals(weightageConfigurationDomainDto, received_entity);
    verify(weightageConfigurationRepository, times(1)).save(any());
  }

  @Test
  @DisplayName("updateWeightageConfiguration: Exception")
  void updateWeightageConfigurationExceptionTest() {
    WeightageConfigurationDomainDto weightageConfiguration =
        testUtil.getWeightageConfigurationDomainDto();

    when(weightageConfigurationRepository.save(any())).thenThrow(new RuntimeException("error"));

    Exception exception =
        assertThrows(
            PromiseEngineException.class,
            () -> {
              weightageConfigurationPersistenceService.saveWeightageConfiguration(
                  weightageConfiguration);
            });
    assertEquals("Unable to save Weightage Configuration", exception.getMessage());
    verify(weightageConfigurationRepository, times(1)).save(any());
  }

  @Test
  @DisplayName("deleteWeightageConfiguration: Happy Path")
  void deleteWeightageConfigurationTest() throws PromiseEngineException {
    WeightageConfigurationDomainDto weightageConfigurationDomainDto =
        testUtil.getWeightageConfigurationDomainDto();

    WeightageConfigurationEntity weightageConfigurationEntity =
        weightageConfigurationEntityMapper.toEntity(weightageConfigurationDomainDto);
    when(weightageConfigurationRepository.findByOrgIdAndTypeAndKey(
            anyString(), anyString(), anyString()))
        .thenReturn(weightageConfigurationEntity);

    WeightageConfigurationDomainDto received_entity =
        weightageConfigurationPersistenceService.deleteWeightageConfiguration(
            weightageConfigurationDomainDto);
    assertEquals(weightageConfigurationDomainDto, received_entity);
    verify(weightageConfigurationRepository, times(1)).delete(any());
  }

  @Test
  @DisplayName("getAllWeightageConfiguration: Happy path")
  void getAllWeightageConfigurationTest() throws PromiseEngineException {
    List<WeightageConfigurationDomainDto> weightageConfigurationDomainDtoList =
        testUtil.getWeightageConfigurationList();
    List<WeightageConfigurationEntity> weightageConfigurationEntityList =
        weightageConfigurationEntityMapper.toEntity(weightageConfigurationDomainDtoList);

    when(weightageConfigurationRepository.findAllRecords(any()))
        .thenReturn(weightageConfigurationEntityList);
    when(weightageConfigurationEntityMapper.toDomain(anyList()))
        .thenReturn(List.of(testUtil.getWeightageConfigurationDomainDto()));

    List<WeightageConfigurationDomainDto> response =
        weightageConfigurationPersistenceService.getAllWeightageConfiguration(2);
    assertEquals(1, response.size());
    assertEquals(weightageConfigurationDomainDtoList.get(0).getType(), response.get(0).getType());
    verify(weightageConfigurationRepository, times(1)).findAllRecords(any());
  }

  @Test
  @DisplayName("getAllWeightageConfigurationException: Exception")
  void getAllWeightageConfigurationExceptionTest() {
    when(weightageConfigurationRepository.findAllRecords(any()))
        .thenThrow(new RuntimeException("Unable to fetch all Weightage Configuration records"));

    Exception exception =
        assertThrows(
            PromiseEngineException.class,
            () -> weightageConfigurationPersistenceService.getAllWeightageConfiguration(2));

    Assertions.assertEquals(
        "Unable to fetch all Weightage Configuration records", exception.getMessage());
    verify(weightageConfigurationRepository, times(1)).findAllRecords(any());
  }
}
