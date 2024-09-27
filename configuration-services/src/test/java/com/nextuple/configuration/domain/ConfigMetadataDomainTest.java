/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */
package com.nextuple.configuration.domain;

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
import com.nextuple.configuration.TestUtil;
import com.nextuple.configuration.domain.entity.ConfigMetadataEntity;
import com.nextuple.configuration.repository.ConfigMetadataRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class ConfigMetadataDomainTest {

  @Mock private ConfigMetadataRepository configMetadataRepository;
  @InjectMocks private ConfigMetadataDomain configMetadataDomain;

  @InjectMocks private TestUtil testUtil;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void saveConfigMetadataTest() throws PromiseEngineException {
    ConfigMetadataEntity configMetadataEntity = testUtil.getConfigMetadataEntity();
    when(configMetadataRepository.save(any(ConfigMetadataEntity.class)))
        .thenReturn(configMetadataEntity);

    ConfigMetadataEntity savedConfigMetadataEntity =
        configMetadataDomain.saveConfigMetadata(configMetadataEntity);

    assertEquals(configMetadataEntity, savedConfigMetadataEntity);
    verify(configMetadataRepository, times(1)).save(any(ConfigMetadataEntity.class));
  }

  @Test
  void saveConfigMetadataExceptionTest() throws PromiseEngineException {
    ConfigMetadataEntity configMetadataEntity = testUtil.getConfigMetadataEntity();

    when(configMetadataRepository.save(any(ConfigMetadataEntity.class)))
        .thenThrow(new RuntimeException("error"));

    Exception ex =
        assertThrows(
            PromiseEngineException.class,
            () -> {
              configMetadataDomain.saveConfigMetadata(configMetadataEntity);
            });
    assertEquals("Unable to save configuration metadata", ex.getMessage());
    verify(configMetadataRepository, times(1)).save(any(ConfigMetadataEntity.class));
  }

  @Test
  void fetchConfigMetadataByConfigKeyTest() throws PromiseEngineException {
    ConfigMetadataEntity configMetadataEntity = testUtil.getConfigMetadataEntity();
    when(configMetadataRepository.findByConfigKey(anyString()))
        .thenReturn(Optional.of(configMetadataEntity));
    Optional<ConfigMetadataEntity> response =
        configMetadataDomain.fetchConfigMetadataByConfigKey(TestUtil.CONFIG_KEY);

    assertEquals(configMetadataEntity, response.get());
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
              configMetadataDomain.fetchConfigMetadataByConfigKey(TestUtil.CONFIG_KEY);
            });

    assertEquals("Unable to fetch configuration metadata", ex.getMessage());
    verify(configMetadataRepository, times(1)).findByConfigKey(anyString());
  }

  @Test
  void deleteConfigMetadataTest() throws PromiseEngineException {
    ConfigMetadataEntity configMetadataEntity = testUtil.getConfigMetadataEntity();
    doNothing().when(configMetadataRepository).delete(any(ConfigMetadataEntity.class));

    configMetadataDomain.deleteConfigMetadata(configMetadataEntity);

    verify(configMetadataRepository, times(1)).delete(any());
  }

  @Test
  void deleteConfigMetadataExceptionTest() throws PromiseEngineException {
    ConfigMetadataEntity configMetadataEntity = testUtil.getConfigMetadataEntity();
    doThrow(new RuntimeException("error"))
        .when(configMetadataRepository)
        .delete(any(ConfigMetadataEntity.class));
    Exception ex =
        assertThrows(
            PromiseEngineException.class,
            () -> {
              configMetadataDomain.deleteConfigMetadata(configMetadataEntity);
            });
    assertEquals("Unable to delete configuration metadata", ex.getMessage());
    verify(configMetadataRepository, times(1)).delete(any());
  }
}
