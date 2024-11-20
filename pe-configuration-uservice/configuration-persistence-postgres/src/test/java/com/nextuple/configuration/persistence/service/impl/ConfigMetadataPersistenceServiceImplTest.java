/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */
package com.nextuple.configuration.persistence.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nextuple.common.exception.PromiseEngineException;
import com.nextuple.configuration.persistence.domain.ConfigMetadataDomainDto;
import com.nextuple.configuration.persistence.entity.ConfigMetadataEntity;
import com.nextuple.configuration.persistence.mapper.ConfigMetadataEntityMapper;
import com.nextuple.configuration.persistence.repository.ConfigMetadataRepository;
import com.nextuple.configuration.persistence.util.TestUtil;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

class ConfigMetadataPersistenceServiceImplTest {

  @Mock private ConfigMetadataRepository configMetadataRepository;

  @Mock private ConfigMetadataEntityMapper configMetadataEntityMapper;

  @InjectMocks private ConfigMetadataPersistenceServiceImpl configMetadataPersistenceService;

  @InjectMocks private TestUtil testUtil;

  @BeforeEach
  public void setUp() {
    AutoCloseable autoCloseable = MockitoAnnotations.openMocks(this);
    ReflectionTestUtils.setField(
        configMetadataPersistenceService, "repository", configMetadataRepository);
    ReflectionTestUtils.setField(
        configMetadataPersistenceService, "mapper", configMetadataEntityMapper);
  }

  @Test
  void saveConfigMetadataTest() throws PromiseEngineException {
    ConfigMetadataEntity configMetadataEntity = testUtil.getConfigMetadataEntity();
    when(configMetadataEntityMapper.toEntity(any(ConfigMetadataDomainDto.class)))
        .thenReturn(configMetadataEntity);
    when(configMetadataEntityMapper.toDomain(any(ConfigMetadataEntity.class)))
        .thenReturn(testUtil.getConfigMetadataPersistenceDomainDto());
    when(configMetadataRepository.save(any(ConfigMetadataEntity.class)))
        .thenReturn(configMetadataEntity);
    ConfigMetadataDomainDto configConfigMetadataDomainDto =
        configMetadataPersistenceService.saveConfigMetadata(
            testUtil.getConfigMetadataPersistenceDomainDto());
    Assertions.assertEquals(configMetadataEntity.getId(), configConfigMetadataDomainDto.getId());
    verify(configMetadataRepository, times(1)).save(any());
  }

  @Test
  void saveConfigMetadataExceptionTest() throws PromiseEngineException {
    ConfigMetadataEntity configMetadataEntity = testUtil.getConfigMetadataEntity();
    when(configMetadataEntityMapper.toEntity(any(ConfigMetadataDomainDto.class)))
        .thenReturn(configMetadataEntity);
    when(configMetadataEntityMapper.toDomain(any(ConfigMetadataEntity.class)))
        .thenReturn(testUtil.getConfigMetadataPersistenceDomainDto());
    when(configMetadataRepository.save(any(ConfigMetadataEntity.class)))
        .thenThrow(new RuntimeException("error"));
    Exception ex =
        assertThrows(
            PromiseEngineException.class,
            () -> {
              configMetadataPersistenceService.saveConfigMetadata(
                  testUtil.getConfigMetadataPersistenceDomainDto());
            });
    assertEquals("Unable to save configuration metadata", ex.getMessage());
    verify(configMetadataRepository, times(1)).save(any(ConfigMetadataEntity.class));
  }

  @Test
  void fetchConfigMetadataByConfigKeyTest() throws PromiseEngineException {
    ConfigMetadataEntity configMetadataEntity = testUtil.getConfigMetadataEntity();
    when(configMetadataEntityMapper.toEntity(any(ConfigMetadataDomainDto.class)))
        .thenReturn(configMetadataEntity);
    when(configMetadataEntityMapper.toDomain(any(ConfigMetadataEntity.class)))
        .thenReturn(testUtil.getConfigMetadataPersistenceDomainDto());
    when(configMetadataRepository.findByConfigKey(anyString()))
        .thenReturn(Optional.of(configMetadataEntity));
    Optional<ConfigMetadataDomainDto> response =
        configMetadataPersistenceService.fetchConfigMetadataByConfigKey(TestUtil.CONFIG_KEY);
    assertEquals(configMetadataEntity, configMetadataEntityMapper.toEntity(response.get()));
    verify(configMetadataRepository, times(1)).findByConfigKey(anyString());
  }

  @Test
  void fetchConfigMetadataByConfigKeyExceptionTest() throws PromiseEngineException {
    when(configMetadataRepository.findByConfigKey(anyString()))
        .thenThrow(new RuntimeException("error"));
    Exception ex =
        assertThrows(
            PromiseEngineException.class,
            () -> {
              configMetadataPersistenceService.fetchConfigMetadataByConfigKey(TestUtil.CONFIG_KEY);
            });

    assertEquals("Unable to fetch configuration metadata", ex.getMessage());
    verify(configMetadataRepository, times(1)).findByConfigKey(anyString());
  }

  @Test
  void deleteConfigMetadataTest() throws PromiseEngineException {
    doNothing().when(configMetadataRepository).delete(any(ConfigMetadataEntity.class));
    configMetadataPersistenceService.deleteConfigMetadata(
        testUtil.getConfigMetadataPersistenceDomainDto());
    verify(configMetadataRepository, times(1)).delete(any());
  }

  @Test
  void deleteConfigMetadataExceptionTest() throws PromiseEngineException {
    ConfigMetadataEntity configMetadataEntity = testUtil.getConfigMetadataEntity();
    when(configMetadataEntityMapper.toEntity(any(ConfigMetadataDomainDto.class)))
        .thenReturn(configMetadataEntity);
    when(configMetadataEntityMapper.toDomain(any(ConfigMetadataEntity.class)))
        .thenReturn(testUtil.getConfigMetadataPersistenceDomainDto());
    doThrow(new RuntimeException("error"))
        .when(configMetadataRepository)
        .delete(any(ConfigMetadataEntity.class));
    Exception ex =
        assertThrows(
            PromiseEngineException.class,
            () -> {
              configMetadataPersistenceService.deleteConfigMetadata(
                  testUtil.getConfigMetadataPersistenceDomainDto());
            });
    assertEquals("Unable to delete configuration metadata", ex.getMessage());
    verify(configMetadataRepository, times(1)).delete(any());
  }
}
